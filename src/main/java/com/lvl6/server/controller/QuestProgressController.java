package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QuestProgressRequestEvent;
import com.lvl6.events.response.QuestProgressResponseEvent;
import com.lvl6.info.Quest;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
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

    //set stuff to send to the client
    QuestProgressResponseProto.Builder resBuilder = QuestProgressResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
      Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);

      boolean legitProgress = checkLegitProgress(resBuilder, userId, 
      		currentProgress, questId, quest);

      if (legitProgress) {
        writeChangesToDB(userId, questId, currentProgress, quest);
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
  		int progress, int questId, Quest quest) {
  	//make sure the quest, relating to the user_quest updated, exists
    if (quest == null) {
      log.error("parameter passed in is null.  quest=" + quest);
      resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
      return false;
    }
    
    int questMaxProgress = quest.getQuantity();
    if (progress > questMaxProgress) {
    	log.warn("user went beyond the max progress for a quest. progress=" +
    			progress + "\t quest=" + quest);
    }
    
    
    resBuilder.setStatus(QuestProgressStatus.SUCCESS);
    return true;
  }
  
  /*
    private boolean checkLegitProgress(Builder resBuilder, int userId,
  		int progress, int questId, Quest quest, Map<Integer, QuestForUser> userQuests) {
    if (quest == null) {
      log.error("parameter passed in is null.  quest=" + quest);
      resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
      return false;
    }
    
    //CHECK TO MAKE SURE THAT THE USER HAS THIS QUEST
    if (!userQuests.containsKey(questId)) {
    	log.error("user trying to update progress for nonexisting user_quest. " +
    			"progress=" + progress + "\t quest=" + quest + "\t userQuests=" + userQuests);
    	return false;
    }
    
    resBuilder.setStatus(QuestProgressStatus.SUCCESS);
    return true;
  }
   */

  private void writeChangesToDB(int userId, int questId,
  		int currentProgress, Quest quest) {
  	//if userQuest's progress reached the progress specified in quest then
  	//also set userQuest.isComplete = true;
  	boolean isComplete = false;
  	
  	int questMaxProgress = quest.getQuantity();
    if (currentProgress >= questMaxProgress) {
    	isComplete = true;
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
