package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QuestProgressRequestEvent;
import com.lvl6.events.response.QuestProgressResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.QuestProto.FullQuestProto.QuestType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;

  @Component @DependsOn("gameServer") public class QuestProgressController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  
  
  @Autowired
  protected InsertUtil insertUtils;

  public void setInsertUtils(InsertUtil insertUtils) {
	this.insertUtils = insertUtils;
  }

  
  public QuestProgressController() {
    numAllocatedThreads = 5;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new QuestProgressRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_QUEST_PROGRESS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    QuestProgressRequestProto reqProto = ((QuestProgressRequestEvent)event).getQuestProgressRequestProto();

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int questId = reqProto.getQuestId();
    int currentProgress = reqProto.getCurrentProgress();
    //use this value when updating user quest, don't check this
    boolean isComplete = reqProto.getIsComplete();
    //at the moment used for donate monster quests
    List<Long> deleteUserMonsterIds = reqProto.getDeleteUserMonsterIdsList();
    Date deleteDate = new Date();

    //set stuff to send to the client
    QuestProgressResponseProto.Builder resBuilder = QuestProgressResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
    	//retrieve whatever is necessary from the db
      Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
      Map<Integer, QuestForUser> questIdsToUnredeemedUserQuests = RetrieveUtils
      		.questForUserRetrieveUtils().getQuestIdToUnredeemedUserQuests(userId);
      Map<Long, MonsterForUser> deleteUserMonsters = RetrieveUtils
      		.monsterForUserRetrieveUtils().getSpecificOrAllUserMonstersForUser(userId, deleteUserMonsterIds);
      		

      boolean legitProgress = checkLegitProgress(resBuilder, userId, 
      		currentProgress, questId, quest, questIdsToUnredeemedUserQuests,
      		deleteUserMonsterIds, deleteUserMonsters);

      boolean success = false;
      if (legitProgress) {
        success = writeChangesToDB(userId, quest, questId, currentProgress,
        		isComplete, deleteUserMonsterIds);
      }
      
      QuestProgressResponseEvent resEvent = new QuestProgressResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setQuestProgressResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (success) {
      	//TODO: RECORD THAT THE USER DELETED THESE MONSERS AND THE REASON
      	writeChangesToHistory(userId, questId, deleteUserMonsters, deleteDate);
      }

    } catch (Exception e) {
      log.error("exception in QuestProgress processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitProgress(Builder resBuilder, int userId,
  		int newProgress, int questId, Quest quest,
  		Map<Integer, QuestForUser> questIdsToUnredeemedUserQuests,
  		List<Long> deleteUserMonsterIds, Map<Long, MonsterForUser> deletedUserMonsters) {
  	//make sure the quest, relating to the user_quest updated, exists
    if (quest == null) {
      log.error("parameter passed in is null.  quest=" + quest);
      resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
      return false;
    }
    
    int questMaxProgress = quest.getQuantity();
    if (newProgress > questMaxProgress) {
    	log.warn("client is trying to set user_quest past the max progress. quest=" +
    			quest + "\t ");
    }
    
    //CHECK TO MAKE SURE THAT THE USER HAS THIS QUEST
    if (!questIdsToUnredeemedUserQuests.containsKey(questId)) {
    	log.error("user trying to update progress for nonexisting user_quest. " +
    			"progress=" + newProgress + "\t quest=" + quest + "\t userQuests=" +
    			questIdsToUnredeemedUserQuests);
    	return false;
    }
    
    //if user wants to delete some monsters, make sure it's the right amount
    if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
    	//user shouldn't delete user monsters when the quest isn't a donate quest
    	if (quest.getQuestType() != QuestType.DONATE_MONSTER_VALUE) {
    		log.error("user trying to delete user monsters for a non donate monster quest." +
    				" quest=" + quest + "\t deleteUserMonsterIds=" + deleteUserMonsterIds);
    		resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);
    		return false;
    	}
    	
    	int deleteSize = deleteUserMonsterIds.size();
    	//make sure that length of ids to delete = the amount required by the quest
    	if (questMaxProgress != deleteSize) {
    		log.error("amount of user monster ids being deleted does not match quest." +
    				" questAmount=" + questMaxProgress + "\t deleteAmount=" + deleteSize +
    				"\t quest=" + quest + "\t");
    		resBuilder.setStatus(QuestProgressStatus.FAIL_DELETE_AMOUNT_DOES_NOT_MATCH_QUEST);
    		return false;
    	}

    	//make sure the deleted user monster ids exist
    	int existingSize = deletedUserMonsters.size();
    	if (deleteSize != existingSize) {
    		log.error("user trying to delete some nonexisting user_monsters. deleteIds=" +
    				deleteUserMonsterIds + "\t existing user_monsters=" + deletedUserMonsters);
    		resBuilder.setStatus(QuestProgressStatus.FAIL_NONEXISTENT_USER_MONSTERS);
    		return false;
    	}

    	//make sure the monsters are all complete
    	for (long deleteId : deleteUserMonsterIds) {
    		//this assumes all the deleted user monster ids are retrieved from db
    		MonsterForUser mfu = deletedUserMonsters.get(deleteId);
    		if (mfu.isComplete()) {
    			continue;
    		}
    		//user trying to delete incomplete user monster
    		log.error("user trying to delete incomplete user monster. userMonster=" +
    				mfu + "\t quest=" + quest);
    		return false;
    	}
    }
    
    resBuilder.setStatus(QuestProgressStatus.SUCCESS);
    return true;
  }

  private boolean writeChangesToDB(int userId, Quest quest, int questId,
  		int currentProgress, boolean isComplete, List<Long> deleteUserMonsterIds) {
  	//if userQuest's progress reached the progress specified in quest then
  	//also set userQuest.isComplete = true;
  	
  	int num = insertUtils.insertUpdateUnredeemedUserQuest(userId,
  			questId, currentProgress, isComplete);
  	if (num != 1) {
  		log.error("num inserted/updated for unredeemd user quest:" +
  				num + "\t userId=" + userId + "\t questId=" + questId +
  				"\t currentProgress=" + currentProgress);
  		return false;
  	}
  	
  	//delete the user monster ids
  	if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
  		num = DeleteUtils.get().deleteMonstersForUser(deleteUserMonsterIds); 
  		log.info("num user monsters deleted: " + num + "\t ids deleted: "+
  				deleteUserMonsterIds);
  	}
  	return true;
  }
  
  private void writeChangesToHistory(int userId, int questId,
  		Map<Long, MonsterForUser> deleteUserMonsters, Date deleteDate) {
  	
  	if (null == deleteUserMonsters || deleteUserMonsters.isEmpty()) {
  		return;
  	}
  	String deleteReason = ControllerConstants.MFUDR__QUEST + questId;
  	
  	int size = deleteUserMonsters.size();
  	List<String> deleteReasons = Collections.nCopies(size, deleteReason);
  	Collection<MonsterForUser> userMonsters = deleteUserMonsters.values();
  	List<MonsterForUser> userMonstersList = new ArrayList<MonsterForUser>(userMonsters);
  	int num = InsertUtils.get().insertIntoMonsterForUserDeleted(userId,
  			deleteReasons, userMonstersList, deleteDate);
  	
  	log.info("user monsters deleted for questId=" + questId + ". num=" + num);
  }
}
