package com.lvl6.server.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.lvl6.info.QuestJob;
import com.lvl6.info.QuestJobForUser;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.QuestProto.QuestJobProto.QuestJobType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.QuestJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

  @Component @DependsOn("gameServer") public class QuestProgressController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  @Autowired
  protected QuestJobForUserRetrieveUtil questJobForUserRetrieveUtil;
  
  @Autowired
  protected UpdateUtil updateUtil;
  
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
    
    log.info("reqProto=" + reqProto);

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int questId = reqProto.getQuestId();
    boolean isQuestComplete = reqProto.getIsComplete();
    
    int questJobId = reqProto.getQuestJobId();
    int newProgress = reqProto.getCurrentProgress();
    boolean isQuestJobComplete = reqProto.getIsQuestJobComplete(); 
    
    //use this value when updating user quest, don't check this
    //at the moment used for donate monster quests
    List<Long> deleteUserMonsterIds = reqProto.getDeleteUserMonsterIdsList();
    Date deleteDate = new Date();

    //set stuff to send to the client
    QuestProgressResponseProto.Builder resBuilder = QuestProgressResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);


    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	//retrieve whatever is necessary from the db
//    	QuestForUser qfu = RetrieveUtils.questForUserRetrieveUtils()
//    			.getSpecificUnredeemedUserQuest(userId, questId);
    	
    	Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob =
    			getQuestJobForUserRetrieveUtil().getQuestJobIdsToJobs(
    					userId, questId);
    	
    	//only retrieve user monsters if client sent ids
    	Map<Long, MonsterForUser> deleteUserMonsters = null;
    	if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
    		deleteUserMonsters = RetrieveUtils.monsterForUserRetrieveUtils()
    				.getSpecificOrAllUserMonstersForUser(
    						userId, deleteUserMonsterIds);
    	}

    	boolean legitProgress = checkLegitProgress(resBuilder, userId, questId,
    			isQuestComplete, questJobId, newProgress, isQuestJobComplete,
    			questJobIdsToUserQuestJob, deleteUserMonsterIds,
    			deleteUserMonsters);

    	boolean success = false;
    	if (legitProgress) {
    		QuestJobForUser existingJob = questJobIdsToUserQuestJob
    				.get(questJobId);
    		success = writeChangesToDB(userId, questId, isQuestComplete,
    				questJobId, newProgress, isQuestJobComplete, existingJob,
    				deleteUserMonsterIds);
    	}
    	
    	if (success) {
    		  resBuilder.setStatus(QuestProgressStatus.SUCCESS);
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
    	getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitProgress(Builder resBuilder, int userId,
  		int questId, boolean isQuestComplete, int questJobId, int newProgress,
  		boolean isQuestJobComplete,
  		Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob,
  		List<Long> deleteUserMonsterIds,
  		Map<Long, MonsterForUser> deletedUserMonsters) {
	  
	  Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
	  QuestJob qj = QuestJobRetrieveUtils.getQuestJobForQuestJobId(questJobId);
	  
	  //make sure the quest, relating to the user_quest being updated,
	  //exists
	  if (null == quest || null == qj) {
		  log.error("parameter passed in is null. quest=" + quest +
				  " or questJob=" + qj);
		  resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
		  return false;
	  }
	  
	  
	  //check to make sure that the user has this quest job
	  if (!questJobIdsToUserQuestJob.containsKey(questJobId)) {
		  //expected to never go in here
		  log.error("user trying to update progress for nonexisting" +
				  " QuestJobForUser with questJobId=" + questJobId +
				  "\t quest=" + quest + "\t questJob=" + qj +
				  "\t userQuestJobs=" + questJobIdsToUserQuestJob);
		  return false;
	  }
	  
	  //check if already complete
	  QuestJobForUser qjfu = questJobIdsToUserQuestJob.get(questJobId);
	  if (qjfu.isComplete()) {
		  log.error("quest job for user already complete. qjfu=" + qjfu);
		  return false;
	  }

	  
	  //if client says quest job is incomplete, then return true and exit
	  if (!isQuestJobComplete) {
		  //since only updating quest progress, if user is deleting
		  //monsters then validate the monsters being deleted
		  //otherwise, would just return true
		  return checkDeletingMonsters(resBuilder, quest, qj,
				  deleteUserMonsterIds, deletedUserMonsters);
	  }
	  
	  //client is saying quest job is complete
	  //now check if the quest job is actually complete
	  int questJobMaxProgress = qj.getQuantity();
	  if (newProgress > questJobMaxProgress) {
		  log.warn("client is trying to set user_quest_job past the max" +
				  " progress. questJob=" + qj + "\t newProgress=" +
				  newProgress);
	  }
	  
	  if (newProgress < questJobMaxProgress) {
		  log.error("client says quest job is complete but it isn't. sent:" +
				  newProgress + ". progress should be questJob:" + qj);
		  return false;
	  }
	  
	  //quest job is indeed complete
	  //since quest job complete, check if quest isComplete is set,
	  //if not then return true. otherwise, check to see that the other
	  //quest jobs for this quest are complete
	  if (!isQuestComplete) {
		  return true;
	  }
	  
	  if (!checkEntireQuestComplete(questId, Collections.singleton(questJobId),
			  questJobIdsToUserQuestJob)) {
		  log.error("client says user's quest is complete, but it isn't. " +
		  		"userQuestJobs: " + questJobIdsToUserQuestJob);
		  return false;
	  }

	  return true;
  }
  
  //returns true if ids of monsters provided are null or the monsters
  //match quest job criteria
  private boolean checkDeletingMonsters(Builder resBuilder, Quest quest,
		  QuestJob qj, List<Long> deleteUserMonsterIds,
		  Map<Long, MonsterForUser> deletedUserMonsters) {
	  
	  if (null == deleteUserMonsterIds || deleteUserMonsterIds.isEmpty()) {
		  return true;
	  }
	  
	  int questMaxProgress = qj.getQuantity();
	  
	  //if user wants to delete some monsters, make sure it's the right amount
	  //user shouldn't delete user monsters when quest isn't a donate quest
	  String donateMonster = QuestJobType.DONATE_MONSTER.name();
	  if (!donateMonster.equals(qj.getQuestJobType())) {
		  log.error("user trying to delete user monsters for a non" +
				  " donate monster quest. quest=" + quest +
				  "\t deleteUserMonsterIds=" + deleteUserMonsterIds);
		  resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);
		  return false;
	  }

	  int deleteSize = deleteUserMonsterIds.size();
	  //make sure length of ids to delete = amount required by quest
	  if (questMaxProgress != deleteSize) {
		  log.error("amount of user monster ids being deleted does not" +
				  " match quest. questAmount=" + questMaxProgress +
				  "\t deleteAmount=" + deleteSize + "\t quest=" + quest +
				  "\t questJob=" + qj);
		  resBuilder.setStatus(QuestProgressStatus
				  .FAIL_DELETE_AMOUNT_DOES_NOT_MATCH_QUEST);
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

	  if (!MonsterStuffUtils.checkAllMonstersForUserComplete(quest,
			  deleteUserMonsterIds, deletedUserMonsters)) {
		  //user trying to delete incomplete user monster
		  log.error("user trying to delete an incomplete user monster." +
				  " deletedUserMonsters=" + deletedUserMonsters +
				  "\t quest=" + quest);
		  return false;
	  }
	  return true;
  }

  //go through 
  private boolean checkEntireQuestComplete(int questId, Set<Integer> blackList,
		  Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob) {
	  
	  //get all the quest's quest job ids
	  Map<Integer, QuestJob> questJobIdToQuestJob = QuestJobRetrieveUtils
			  .getQuestJobsForQuestId(questId);
	  
	  //go through all the quest's job ids and see if user completed it
	  for (Integer questJobId : questJobIdToQuestJob.keySet()) {
		  if (blackList.contains(questJobId)) {
			  continue;
		  }
		  
		  if (!questJobIdsToUserQuestJob.containsKey(questJobId)) {
			  log.info("questJobForUser does not exist for quest job id:" +
					  questJobId);
			  return false;
		  }
		  
		  QuestJobForUser qjfu = questJobIdsToUserQuestJob.get(questJobId);
		  if (!qjfu.isComplete()) {
			  log.info("questJobForUser is not complete: " + qjfu);
			  return false;
		  }
	  }
	  
	  return true;
  }


  private boolean writeChangesToDB(int userId, int questId,
		  boolean questComplete, int questJobId, int currentProgress,
		  boolean questJobComplete, QuestJobForUser existingJob,
		  List<Long> deleteUserMonsterIds) {
  	
	  //update user quest job
	  int num = getUpdateUtil().updateUserQuestJob(userId, questJobId,
			  currentProgress, questJobComplete);
	  if (1 != num) {
		  log.error("num updated for unredeemd user quest job:" + num +
				  "\t userId=" + userId + "\t questJobId=" + questJobId +
				  "\t currentProgress=" + currentProgress);
		  return false;
	  }
	  
	  //update user quest
	  if (!getUpdateUtil().updateUserQuestIscomplete(userId, questId)) {
		  log.error("could not update user quest to complete. questId=" +
				  questId + ". attempting to revert user quest job:" +
				  existingJob);
		  int oldProgress = existingJob.getProgress();
		  boolean isComplete = existingJob.isComplete();
		  num = getUpdateUtil().updateUserQuestJob(userId, questJobId,
				  oldProgress, isComplete);
		  log.info("numUpdated when reverting UserQuestJob: " + num);
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
  
  //TODO: FIX THIS
  private void writeChangesToHistory(int userId, int questId,
  		Map<Long, MonsterForUser> deleteUserMonsters, Date deleteDate) {
//  	
//  	if (null == deleteUserMonsters || deleteUserMonsters.isEmpty()) {
//  		return;
//  	}
//  	String deleteReason = ControllerConstants.MFUDR__QUEST + questId;
//  	
//  	int size = deleteUserMonsters.size();
//  	List<String> deleteReasons = Collections.nCopies(size, deleteReason);
//  	Collection<MonsterForUser> userMonsters = deleteUserMonsters.values();
//  	List<MonsterForUser> userMonstersList = new ArrayList<MonsterForUser>(userMonsters);
//  	int num = InsertUtils.get().insertIntoMonsterForUserDeleted(userId,
//  			deleteReasons, userMonstersList, deleteDate);
//  	
//  	log.info("user monsters deleted for questId=" + questId + ". num=" + num);
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public QuestJobForUserRetrieveUtil getQuestJobForUserRetrieveUtil() {
	  return questJobForUserRetrieveUtil;
  }

  public void setQuestJobForUserRetrieveUtil(
		  QuestJobForUserRetrieveUtil questJobForUserRetrieveUtil) {
	  this.questJobForUserRetrieveUtil = questJobForUserRetrieveUtil;
  }

  public UpdateUtil getUpdateUtil() {
	  return updateUtil;
  }

  public void setUpdateUtil(UpdateUtil updateUtil) {
	  this.updateUtil = updateUtil;
  }

  
}
