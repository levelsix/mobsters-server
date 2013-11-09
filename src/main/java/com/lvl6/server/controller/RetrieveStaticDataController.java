package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveStaticDataRequestEvent;
import com.lvl6.events.response.RetrieveStaticDataResponseEvent;
import com.lvl6.info.City;
import com.lvl6.info.ExpansionCost;
import com.lvl6.info.Monster;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.StaticUserLevelInfo;
import com.lvl6.info.Structure;
import com.lvl6.info.Task;
import com.lvl6.proto.CityProto.CityExpansionCostProto;
import com.lvl6.proto.EventStaticDataProto.RetrieveStaticDataRequestProto;
import com.lvl6.proto.EventStaticDataProto.RetrieveStaticDataResponseProto;
import com.lvl6.proto.EventStaticDataProto.RetrieveStaticDataResponseProto.Builder;
import com.lvl6.proto.EventStaticDataProto.RetrieveStaticDataResponseProto.RetrieveStaticDataStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.StaticUserLevelInfoProto;
import com.lvl6.retrieveutils.rarechange.CityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StaticUserLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.QuestUtils;

  @Component @DependsOn("gameServer") public class RetrieveStaticDataController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public RetrieveStaticDataController() {
    numAllocatedThreads = 15;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new RetrieveStaticDataRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_STATIC_DATA_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrieveStaticDataRequestProto reqProto = ((RetrieveStaticDataRequestEvent)event).getRetrieveStaticDataRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();

    RetrieveStaticDataResponseProto.Builder resBuilder = RetrieveStaticDataResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(RetrieveStaticDataStatus.SUCCESS);

    populateResBuilder(resBuilder, senderProto.getUserId());
    RetrieveStaticDataResponseProto resProto = resBuilder.build();

    RetrieveStaticDataResponseEvent resEvent = new RetrieveStaticDataResponseEvent(senderProto.getUserId());
    resEvent.setTag(event.getTag());
    resEvent.setRetrieveStaticDataResponseProto(resProto);

    server.writeEvent(resEvent);
  }

  private void populateResBuilder(Builder resBuilder, int userId) {
  	//Player city expansions
  	Map<Integer, ExpansionCost> expansionCosts =
  			ExpansionCostRetrieveUtils.getAllExpansionNumsToCosts();
  	for (ExpansionCost cec : expansionCosts.values()) {
  		CityExpansionCostProto cecp = CreateInfoProtoUtils
  				.createCityExpansionCostProtoFromCityExpansionCost(cec);
  		resBuilder.addExpansionCosts(cecp);
  	}
  	//Cities
  	Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
  	for (Integer cityId : cities.keySet()) {
  		City city = cities.get(cityId);
  		resBuilder.addAllCities(CreateInfoProtoUtils.createFullCityProtoFromCity(city));
  	}
  	//Structures
  	Map<Integer, Structure> structs = StructureRetrieveUtils.getStructIdsToStructs();
  	for (Structure struct : structs.values()) {
  		resBuilder.addAllStructs(CreateInfoProtoUtils.createFullStructureProtoFromStructure(struct));
  	}
  	//Tasks
  	Map<Integer, Task> taskIdsToTasks = TaskRetrieveUtils.getTaskIdsToTasks();
  	for (Task aTask : taskIdsToTasks.values()) {
  		FullTaskProto ftp = CreateInfoProtoUtils.createFullTaskProtoFromTask(aTask);
  		resBuilder.addAllTasks(ftp);
  	}
  	//Monsters
  	Map<Integer, Monster> monsters = MonsterRetrieveUtils.getMonsterIdsToMonsters();
  	for (Monster monster : monsters.values()) {
  		resBuilder.addAllMonsters(CreateInfoProtoUtils.createMonsterProto(monster));
  	}
  	//User level stuff
  	Map<Integer, StaticUserLevelInfo> levelToStaticUserLevelInfo = 
  			StaticUserLevelInfoRetrieveUtils.getAllStaticUserLevelInfo();
  	for (int lvl : levelToStaticUserLevelInfo.keySet())  {
  		StaticUserLevelInfo sli = levelToStaticUserLevelInfo.get(lvl);
  		int exp = sli.getLvl();
  		int maxCash = sli.getMaxCash();

  		StaticUserLevelInfoProto.Builder slipb = StaticUserLevelInfoProto.newBuilder();
  		slipb.setLevel(lvl);
  		slipb.setRequiredExperience(exp);
  		slipb.setMaxCash(maxCash);
  		resBuilder.addSlip(slipb.build());
  	}

  	setInProgressAndAvailableQuests(resBuilder, userId);

//    if (reqProto.getCurrentLockBoxEvents()) {
//      resBuilder.addAllLockBoxEvents(MiscMethods.currentLockBoxEvents());
//    }
    
//    //sets the leaderboard stuff: events and rewards
//    if(reqProto.getCurrentLeaderboardEvents()) {
//      resBuilder.addAllLeaderboardEvents(MiscMethods.currentLeaderboardEventProtos());
//      //TODO: SET THE REWARD STUFF
//    }
  }
  
  private void setInProgressAndAvailableQuests(Builder resBuilder, int userId) {
	  List<QuestForUser> inProgressAndRedeemedUserQuests = RetrieveUtils.questForUserRetrieveUtils()
	      .getUserQuestsForUser(userId);
	  
	  
	  List<Integer> inProgressQuestIds = new ArrayList<Integer>();
	  List<Integer> redeemedQuestIds = new ArrayList<Integer>();
	  
	  Map<Integer, Quest> questIdToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
	  for (QuestForUser uq : inProgressAndRedeemedUserQuests) {
	  	
	    if (uq.isRedeemed()) {
	      redeemedQuestIds.add(uq.getQuestId());
	      
	    } else {
	    	//unredeemed quest section
	      Quest quest = QuestRetrieveUtils.getQuestForQuestId(uq.getQuestId());
	      FullQuestProto questProto = CreateInfoProtoUtils.createFullQuestProtoFromQuest(quest);
	      
	      inProgressQuestIds.add(uq.getQuestId());
	      if (uq.isComplete()) { 
	      	//complete and unredeemed userQuest, so quest goes in unredeemedQuest
	        resBuilder.addUnredeemedQuests(questProto);
	      } else {
	      	//incomplete and unredeemed userQuest, so quest goes in inProgressQuest
	        resBuilder.addInProgressQuests(questProto);
	      }
	    }
	  }
	  
	  List<Integer> availableQuestIds = QuestUtils.getAvailableQuestsForUser(redeemedQuestIds,
	      inProgressQuestIds);
	  if (availableQuestIds == null) {
	  	return;
	  }
	  
	  //from the available quest ids generate the available quest protos
	  for (Integer questId : availableQuestIds) {
	  	FullQuestProto fqp = CreateInfoProtoUtils.createFullQuestProtoFromQuest(
	  			questIdToQuests.get(questId));
	  	resBuilder.addAvailableQuests(fqp);
	  }
}

}
