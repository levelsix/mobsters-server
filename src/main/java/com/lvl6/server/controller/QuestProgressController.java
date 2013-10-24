package com.lvl6.server.controller;

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
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

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
    List<Integer> deleteUserMonsterIds = reqProto.getDeleteUserMonsterIdsList();

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

      boolean legitProgress = checkLegitProgress(resBuilder, userId, 
      		currentProgress, questId, quest, questIdsToUnredeemedUserQuests);

      if (legitProgress) {
        writeChangesToDB(userId, quest, questId, currentProgress, isComplete,
        		deleteUserMonsterIds);
      }
      
      QuestProgressResponseEvent resEvent = new QuestProgressResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setQuestProgressResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in QuestProgress processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitProgress(Builder resBuilder, int userId,
  		int newProgress, int questId, Quest quest,
  		Map<Integer, QuestForUser> questIdsToUnredeemedUserQuests) {
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
    
    
    resBuilder.setStatus(QuestProgressStatus.SUCCESS);
    return true;
  }

  private void writeChangesToDB(int userId, Quest quest, int questId,
  		int currentProgress, boolean isComplete, List<Integer> deleteUserMonsterIds) {
  	//if userQuest's progress reached the progress specified in quest then
  	//also set userQuest.isComplete = true;
  	
  	int questMaxProgress = quest.getQuantity();
    if (currentProgress >= questMaxProgress) {
//    	isComplete = true;
    	log.warn("client is trying to set user_quest past the max progress. quest=" +
    			quest + "\t ");
    }
  	int num = insertUtils.insertUpdateUnredeemedUserQuest(userId,
  			questId, currentProgress, isComplete);
  	if (num != 1) {
  		log.error("num inserted/updated for unredeemd user quest:" +
  				num + "\t userId=" + userId + "\t questId=" + questId +
  				"\t currentProgress=" + currentProgress);
  	}
  }
}
