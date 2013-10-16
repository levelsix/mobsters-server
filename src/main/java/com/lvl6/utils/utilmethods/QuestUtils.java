package com.lvl6.utils.utilmethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.events.response.QuestCompleteResponseEvent;
import com.lvl6.info.CityElement;
import com.lvl6.info.Quest;
import com.lvl6.info.UserQuest;
import com.lvl6.info.UserStruct;
import com.lvl6.info.jobs.BuildStructJob;
import com.lvl6.info.jobs.UpgradeStructJob;
import com.lvl6.proto.EventQuestProto.QuestCompleteResponseProto;
import com.lvl6.proto.QuestProto.SpecialQuestAction;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestJobBuildStructRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.UpgradeStructJobRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.QuestGraph;
import com.lvl6.utils.RetrieveUtils;

public class QuestUtils {


	private static final Logger log = LoggerFactory.getLogger(QuestUtils.class);
	
  public static void checkAndSendQuestsCompleteBasic(GameServer server, int userId, MinimumUserProto senderProto, 
      SpecialQuestAction justCompletedSpecialQuestAction, boolean checkOnlySpecialQuests) {
  	
  	//list of user quest will never be null
    List<UserQuest> inProgressUserQuests = RetrieveUtils
    		.userQuestRetrieveUtils().getIncompleteUserQuestsForUser(userId);
    
    for (UserQuest userQuest : inProgressUserQuests) {
    	//user quest will never be null
    	Quest quest = QuestRetrieveUtils.getQuestForQuestId(userQuest.getQuestId());
    	if (null == quest) {
    		log.error("db error: quest nonexistent. (DELETE!) userQuest=" + userQuest);
    	}
    	
    	//if it's a special quest but didnt just do one, skip
    	//if you just did a special quest only and this quest doesnt have a special quest
    	boolean isSpecialQuest = quest.isSpecialQuest();
    	if ( (isSpecialQuest && justCompletedSpecialQuestAction == null)
    			|| (checkOnlySpecialQuests && !isSpecialQuest) ) {
    		continue;
    	}
    	QuestUtils.checkQuestCompleteAndMaybeSendIfJustCompleted(server, quest,
    			userQuest, senderProto, true, justCompletedSpecialQuestAction);

    }
  }

  public static boolean checkQuestCompleteAndMaybeSendIfJustCompleted(
  		GameServer server, Quest quest, UserQuest userQuest,
      MinimumUserProto senderProto, boolean sendCompleteMessageIfJustCompleted,
      SpecialQuestAction justCompletedSpecialQuestAction) {
  	if (null == userQuest) {
  		return false;
  	}
    if (userQuest.isComplete()) {
    	return true;                        //already completed
    }
    // criteria for marking incomplete user quest as complete:
    // 1) completed all tasks and 2) defeated all things and
    // 3) retrieved all the necessary coins
    if (!userQuest.isTasksComplete() || !userQuest.isDefeatTypeJobsComplete() ||
    		userQuest.getCoinsRetrievedForReq() < quest.getCoinRetrievalAmountRequired()) {
    	return false;
    }


    if (quest.isSpecialQuest()) {
    	// check if this incomplete special quest should
    	// be recorded as completed
    	return recordSpecialQuestAsCompleted(server, quest, userQuest,
    			senderProto, justCompletedSpecialQuestAction,
    			sendCompleteMessageIfJustCompleted);
    }

    List<Integer> buildStructJobsRequired = quest.getBuildStructJobsRequired();
    List<Integer> upgradeStructJobsRequired = quest.getUpgradeStructJobsRequired();
    boolean buildStructJobsExist = quest.hasBuildStructJobs();
    boolean upgradeStructJobsExist = quest.hasUpgradeStructJobs();
    
    int userId = userQuest.getUserId();
    Map<Integer, List<UserStruct>> structIdsToUserStructs =
        new HashMap<Integer, List<UserStruct>>();
    //completedBuildStructJobs() might populate structIdsToUserStructs
    //if build struct jobs exist and user has not finished it all, exit
    if (!completedBuildStructJobs(userId, buildStructJobsExist,
        buildStructJobsRequired, structIdsToUserStructs)) {
      return false;
    }
    //if build struct jobs exist and user has not finished it all, exit
    if (!completedUpgradeStructJobs(userId, upgradeStructJobsExist,
        upgradeStructJobsRequired, structIdsToUserStructs)) {
      return false;
    }

    sendQuestCompleteResponseIfRequestedAndUpdateUserQuest(server, quest, userQuest, senderProto,
        sendCompleteMessageIfJustCompleted);
    return true;
//    if (possessEquipJobsRequired != null && possessEquipJobsRequired.size() > 0) {
//    	Map<Integer, List<UserEquip>> equipIdsToUserEquips = RetrieveUtils.userEquipRetrieveUtils().getEquipIdsToUserEquipsForUser(userQuest.getUserId());
//    	if (equipIdsToUserEquips == null || equipIdsToUserEquips.size() <= 0) {
//    		return false;
//    	}
//    	Map<Integer, PossessEquipJob> pejs = PossessEquipJobRetrieveUtils.getPossessEquipJobsForPossessEquipJobIds(possessEquipJobsRequired);
//    	for (PossessEquipJob pej : pejs.values()) {
//    		if (equipIdsToUserEquips.get(pej.getEquipId()) == null || equipIdsToUserEquips.get(pej.getEquipId()).size() < pej.getQuantity()) {
//    			return false;
//    		} 
//    	}
//    }
  }
  
  //records to db if special quest is completed;
  // compare random quest with quest user completed
  // return true if random quest matches quest user completed
  private static boolean recordSpecialQuestAsCompleted(GameServer server,
  		Quest quest, UserQuest userQuest, MinimumUserProto senderProto,
  		SpecialQuestAction justCompletedSpecialQuestAction,
  		boolean sendCompleteMessageIfJustCompleted) {

  	if (justCompletedSpecialQuestAction != null &&
  			justCompletedSpecialQuestAction == quest.getSpecialQuestActionRequired()) {
  		sendQuestCompleteResponseIfRequestedAndUpdateUserQuest(server, quest,
  				userQuest, senderProto, sendCompleteMessageIfJustCompleted);
  		return true;
  	} else if (quest.getSpecialQuestActionRequired() == SpecialQuestAction.REQUEST_JOIN_CLAN &&
  			senderProto.hasClan() && senderProto.getClan().getClanId() > 0) {
  		//user's action just now didn't complete clan quest,
  		//but clan quest needs to be recorded as completed
  		sendQuestCompleteResponseIfRequestedAndUpdateUserQuest(server, quest, userQuest, senderProto,
  				sendCompleteMessageIfJustCompleted);
  	}
  	return false;
  }

  //return true if no build job structs exist or if user completed them all
  //return false otherwise
  private static boolean completedBuildStructJobs(int userId,
      boolean buildStructJobsExist, List<Integer> buildStructJobsRequired,
      Map<Integer, List<UserStruct>> structIdsToUserStructs) {
    if (!buildStructJobsExist) {
      return true;
    }

    Map<Integer, List<UserStruct>> structIdsToUserStructsTemp =
        RetrieveUtils.userStructRetrieveUtils().getStructIdsToUserStructsForUser(userId);
    if (structIdsToUserStructsTemp == null || structIdsToUserStructsTemp.size() <= 0) {
      //quest has build struct jobs but user has not done any, exit
      return false;
    }

    Map<Integer, BuildStructJob> bsjs = QuestJobBuildStructRetrieveUtils
        .getBuildStructJobsForBuildStructJobIds(buildStructJobsRequired);

    //see if the user completed each build struct job, if not then exit
    for (BuildStructJob bsj : bsjs.values()) {
      if (!completedBuildStructJob(bsj, structIdsToUserStructsTemp)) {
        return false;
      }
    }
    structIdsToUserStructs.putAll(structIdsToUserStructsTemp);
    return true;
  }
  //helper function for completedBuildStructJobs
  private static boolean completedBuildStructJob(BuildStructJob bsj,
      Map<Integer, List<UserStruct>> structIdsToUserStructs) {
    int bsjStructId = bsj.getStructId();
    int quantityBuilt = 0;

    if (structIdsToUserStructs.get(bsjStructId) != null) {
      for (UserStruct us : structIdsToUserStructs.get(bsjStructId)) {
        if (us.getLastRetrieved() != null) {
        	//not being null indicates the user FINISHED BUILDING the building
          quantityBuilt++;
        }
      }
    }

    if (quantityBuilt <  bsj.getQuantity()) {
      return false;
    } else {
      return true;
    }
  }

  private static boolean completedUpgradeStructJobs(int userId,
      boolean upgradeStructJobsExist, List<Integer> upgradeStructJobsRequired,
      Map<Integer, List<UserStruct>> structIdsToUserStructs) {
    if (!upgradeStructJobsExist) {
      return true;
    }

    Map<Integer, List<UserStruct>> structIdsToUserStructsTemp =
        structIdsToUserStructs;
    if (null == structIdsToUserStructsTemp || structIdsToUserStructsTemp.isEmpty()) {
      //set it now because probably parent calling function didn't set it
      structIdsToUserStructsTemp =  RetrieveUtils.userStructRetrieveUtils()
          .getStructIdsToUserStructsForUser(userId);
    }
    if (structIdsToUserStructsTemp == null || structIdsToUserStructsTemp.isEmpty()) {
      //quest has upgrade struct jobs but user has not done any, exit
      return false;
    }

    Map<Integer, UpgradeStructJob> usjs = UpgradeStructJobRetrieveUtils
        .getUpgradeStructJobsForUpgradeStructJobIds(upgradeStructJobsRequired);

    for (UpgradeStructJob usj : usjs.values()) {
      if (!completedUpgradeStructJob(usj, structIdsToUserStructsTemp)) {
        return false;
      }
    }

    return true;
  }
  //helper method for completedUpgradeStructJobs
  private static boolean completedUpgradeStructJob(UpgradeStructJob usj,
      Map<Integer, List<UserStruct>> structIdsToUserStructs) {
    int usjStructId = usj.getStructId();
    if (structIdsToUserStructs.get(usjStructId) == null) {
      return false;
    }
    boolean usjComplete = false;

    //see if user upgraded any buildings to required level
    //user can build multiple instances of a building
    for (UserStruct us : structIdsToUserStructs.get(usj.getStructId())) {
      if (us.getLevel() >= usj.getLevelReq()) {
        usjComplete = true;
      }
    }

    return usjComplete;
  }

  

  private static void sendQuestCompleteResponseIfRequestedAndUpdateUserQuest(GameServer server, Quest quest,
      UserQuest userQuest, MinimumUserProto senderProto,
      boolean sendCompleteMessageIfJustCompleted) {
    if (server != null && senderProto != null && sendCompleteMessageIfJustCompleted) {
      sendQuestCompleteResponse(server, senderProto, quest);
    }
    if (!userQuest.isComplete() && !UpdateUtils.get().updateUserQuestIscomplete(userQuest.getUserId(), userQuest.getQuestId())) {
      log.error("problem with marking user quest as complete. userquest=" + userQuest);
    }
  }

  private static void sendQuestCompleteResponse (GameServer server, MinimumUserProto senderProto, Quest quest){
    QuestCompleteResponseProto.Builder builder = QuestCompleteResponseProto.newBuilder().setSender(senderProto)
        .setQuestId(quest.getId());
    CityElement cityElement = CityElementsRetrieveUtils.getCityElement(quest.getCityId(), quest.getAssetNumWithinCity());
    if (cityElement != null) {
      builder.setCityElement(CreateInfoProtoUtils.createCityElementProtoFromCityElement(cityElement));
    }
    QuestCompleteResponseEvent event = new QuestCompleteResponseEvent(senderProto.getUserId());
    event.setQuestCompleteResponseProto(builder.build());
    server.writeEvent(event);
  }

  public static List<Integer> getAvailableQuestsForUser(List<Integer> redeemed, List<Integer> inProgress) {
    QuestGraph graph = QuestRetrieveUtils.getQuestGraph();
    return graph.getQuestsAvailable(redeemed, inProgress);
  }


}
