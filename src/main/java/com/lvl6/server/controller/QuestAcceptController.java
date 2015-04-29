//package com.lvl6.server.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.events.RequestEvent;
//import com.lvl6.events.request.QuestAcceptRequestEvent;
//import com.lvl6.events.response.QuestAcceptResponseEvent;
//import com.lvl6.info.Quest;
//import com.lvl6.info.QuestForUser;
//import com.lvl6.info.QuestJob;
//import com.lvl6.info.User;
//import com.lvl6.proto.EventQuestProto.QuestAcceptRequestProto;
//import com.lvl6.proto.EventQuestProto.QuestAcceptResponseProto;
//import com.lvl6.proto.EventQuestProto.QuestAcceptResponseProto.Builder;
//import com.lvl6.proto.EventQuestProto.QuestAcceptResponseProto.QuestAcceptStatus;
//import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
//import com.lvl6.server.Locker;
//import com.lvl6.utils.RetrieveUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.InsertUtils;
//import com.lvl6.utils.utilmethods.QuestUtils;
//
//  @Component @DependsOn("gameServer") public class QuestAcceptController extends EventController {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  @Autowired
//  protected Locker locker;
//  
//  @Autowired
//  protected InsertUtil insertUtils;
//
//  public void setInsertUtils(InsertUtil insertUtils) {
//	this.insertUtils = insertUtils;
//  }
//
//  
//  public QuestAcceptController() {
//    
//  }
//  
//  @Override
//  public RequestEvent createRequestEvent() {
//    return new QuestAcceptRequestEvent();
//  }
//
//  @Override
//  public EventProtocolRequest getEventType() {
//    return EventProtocolRequest.C_QUEST_ACCEPT_EVENT;
//  }
//
//  @Override
//  public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
//    QuestAcceptRequestProto reqProto = ((QuestAcceptRequestEvent)event).getQuestAcceptRequestProto();
//
//    MinimumUserProto senderProto = reqProto.getSender();
//    int userId = senderProto.getUserUuid();
//    int questId = reqProto.getQuestId();
//
//    QuestAcceptResponseProto.Builder resBuilder = QuestAcceptResponseProto.newBuilder();
//    resBuilder.setSender(senderProto);
//    resBuilder.setStatus(QuestAcceptStatus.FAIL_OTHER);
//
//
//    getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
//    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserUuid());
//      Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
//
//      boolean legitAccept = checkLegitAccept(resBuilder, user, userId,
//    		  quest, questId);
//
//      boolean success = false;
//      if (legitAccept) {
//    	  success = writeChangesToDB(userId, questId, quest);
//      }
//      
//      if (success) {
//    	  resBuilder.setStatus(QuestAcceptStatus.SUCCESS);
//      }
//      
//      QuestAcceptResponseEvent resEvent = new QuestAcceptResponseEvent(senderProto.getUserUuid());
//      resEvent.setTag(event.getTag());
//      resEvent.setQuestAcceptResponseProto(resBuilder.build());  
//      responses.normalResponseEvents().add(resEvent);
//
//
//    } catch (Exception e) {
//      log.error("exception in QuestAccept processEvent", e);
//    } finally {
//      getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());      
//    }
//  }
//
//  private boolean checkLegitAccept(Builder resBuilder, User user,
//  		int userId, Quest quest, int questId) {
//    if (user == null || quest == null) {
//      log.error("parameter passed in is null. user=" + user + ", quest=" + quest);
//      return false;
//    }
//    List<QuestForUser> inProgressAndRedeemedUserQuests = RetrieveUtils.questForUserRetrieveUtils().getUserQuestsForUser(user.getId());
//    List<Integer> inProgressQuestIds = new ArrayList<Integer>();
//    List<Integer> redeemedQuestIds = new ArrayList<Integer>();
//
//    if (inProgressAndRedeemedUserQuests != null) {
//      for (QuestForUser uq : inProgressAndRedeemedUserQuests) {
//        if (uq.isRedeemed()) {
//          redeemedQuestIds.add(uq.getQuestId());
//        } else {
//          inProgressQuestIds.add(uq.getQuestId());  
//        }
//      }
//      List<Integer> availableQuestIds = QuestUtils.getAvailableQuestsForUser(redeemedQuestIds, inProgressQuestIds);
//      if (availableQuestIds != null && availableQuestIds.contains(quest.getId())) {
//        resBuilder.setStatus(QuestAcceptStatus.SUCCESS);
//        return true;
//      } else {
//        resBuilder.setStatus(QuestAcceptStatus.FAIL_NOT_AVAIL_TO_USER);
//        log.error("quest with id " + quest.getId() + " is not available to user");
//        return false;
//      }
//    }
//    
//    if (inProgressQuestIds.contains(questId)) {
//    	log.error("db says user already accepted this quest. quest=" + quest);
//    	resBuilder.setStatus(QuestAcceptStatus.FAIL_ALREADY_ACCEPTED);
//    	return false;
//    }
//    
//    return true;
//  }
//
//  private boolean writeChangesToDB(int userId, int questId, Quest quest) {
//	  //insert the quest for the user
//	  int num = InsertUtils.get().insertUserQuest(userId, questId);
//
//	  log.info("num quests inserted into quest_for_user: " + num + 
//			  "\t quest inserted: " + quest);
//
//	  //insert the quest job for user
//	  Map<Integer, QuestJob> questJobIdsToJobs = QuestJobRetrieveUtils
//			  .getQuestJobsForQuestId(questId);
//	  List<Integer> questJobIds = new ArrayList<Integer>(
//			  questJobIdsToJobs.keySet());
//
//	  num = InsertUtils.get().insertUserQuestJobs(userId, questId,
//			  questJobIds);
//	  log.info("num quest jobs inserted into quest_job_for_user: " + num);
//
//	  return true;
//  }
//
//  public Locker getLocker() {
//	  return locker;
//  }
//
//  public void setLocker(Locker locker) {
//	  this.locker = locker;
//  }
//  
//}
