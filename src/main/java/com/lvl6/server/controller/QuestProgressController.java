package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.lvl6.info.QuestForUser;
import com.lvl6.info.QuestJob;
import com.lvl6.info.QuestJobForUser;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.Builder;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto.QuestProgressStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.QuestProto.QuestJobProto.QuestJobType;
import com.lvl6.proto.QuestProto.UserQuestJobProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.QuestJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.QuestUtils;
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
    
    List<UserQuestJobProto> userQuestJobProtoList = reqProto
    		.getUserQuestJobsList();
    userQuestJobProtoList = new ArrayList<UserQuestJobProto>(userQuestJobProtoList);
    
    Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto =
    		QuestUtils.mapifyByQuestJobId(userQuestJobProtoList);
    
    //use this value when updating user quest, don't check this
    //at the moment used for donate monster quests
    List<Long> deleteUserMonsterIds = reqProto.getDeleteUserMonsterIdsList();
    deleteUserMonsterIds = new ArrayList<Long>(deleteUserMonsterIds);
    Date deleteDate = new Date();

    //set stuff to send to the client
    QuestProgressResponseProto.Builder resBuilder = QuestProgressResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(QuestProgressStatus.FAIL_OTHER);


    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	//retrieve whatever is necessary from the db
    	QuestForUser qfu = RetrieveUtils.questForUserRetrieveUtils()
    			.getSpecificUnredeemedUserQuest(userId, questId);
    	
    	Map<Integer, QuestJobForUser> questJobIdToUserQuestJob =
    			getQuestJobForUserRetrieveUtil().getQuestJobIdsToJobs(
    					userId, questId);
    	
    	//only retrieve user monsters if client sent ids
    	Map<Long, MonsterForUser> userMonstersInDb = null;
    	if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
    		userMonstersInDb = RetrieveUtils.monsterForUserRetrieveUtils()
    				.getSpecificOrAllUserMonstersForUser(
    						userId, deleteUserMonsterIds);
    	}

    	boolean legitProgress = checkLegitProgress(resBuilder, userId, questId,
    			isQuestComplete, qfu, questJobIdToUserQuestJobProto,
    			questJobIdToUserQuestJob, deleteUserMonsterIds,
    			userMonstersInDb);

    	boolean success = false;
    	if (legitProgress) {
    		success = writeChangesToDB(userId, questId, isQuestComplete,
    				questJobIdToUserQuestJobProto, questJobIdToUserQuestJob,
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
    		writeChangesToHistory(userId, questId, userMonstersInDb, deleteDate);
    	}

    } catch (Exception e) {
    	log.error("exception in QuestProgress processEvent", e);
    } finally {
    	getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }


  private boolean checkLegitProgress(Builder resBuilder, int userId,
  		int questId, boolean isQuestComplete, QuestForUser qfu,
  		Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto,
  		Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob,
  		List<Long> deleteUserMonsterIds,
  		Map<Long, MonsterForUser> userMonstersInDb) {
	  
	  Quest quest = QuestRetrieveUtils.getQuestForQuestId(questId);
	  
	  //make sure the quest, relating to the user_quest being updated,
	  //exists
	  if (null == quest) {
		  log.error("no quest exists with id=" + questId);
		  resBuilder.setStatus(QuestProgressStatus.FAIL_NO_QUEST_EXISTS);
		  return false;
	  }
	  
	  validateDeletingMonsterQuestJob(questJobIdToUserQuestJobProto,
			  deleteUserMonsterIds, userMonstersInDb);
	  
	  //Loop through each client UserQuestJobProto,
	  //if it isn't valid, remove it from being persisted to db
	  Map<Integer, UserQuestJobProto> copy =
			  new HashMap<Integer, UserQuestJobProto>(questJobIdToUserQuestJobProto);
	  Set<Integer> completedQuestJobIds = new HashSet<Integer>();
	  
	  for (Integer questJobId : copy.keySet()) {
		  UserQuestJobProto uqjp = copy.get(questJobId);
		  
		  //check if this is valid quest job
		  boolean isValidQuestJobProgress = checkIfQuestJobCanBeUpdated(quest,
				  questJobId, questJobIdsToUserQuestJob);
		  if (!isValidQuestJobProgress) {
			  log.warn("client sent invalid questJob: " + uqjp +
					  " removing it from being persisted to db");
			  questJobIdToUserQuestJobProto.remove(questJobId);
			  continue;
		  }
		  
		  //check if this quest job can be declared as completed
		  boolean isQuestJobComplete = uqjp.getIsComplete();
		  if (!isQuestJobComplete) {
			  continue;
		  }
		  
		  QuestJob qj = QuestJobRetrieveUtils.getQuestJobForQuestJobId(questJobId);
		  boolean isQuestJobReallyComplete = checkIfQuestJobIsComplete(uqjp, qj);
		  if (!isQuestJobReallyComplete) {
			  log.warn("client incorreclty sent completed questJob: " +
					  uqjp + " removing it from being persisted to db");
			  questJobIdToUserQuestJobProto.remove(questJobId);
			  continue;
		  }
		  
		  //quest job is really complete
		  completedQuestJobIds.add(questJobId);
	  }
	  
	  //quest job is indeed complete
	  //since quest job complete, check if quest isComplete is set,
	  //if not then return true. otherwise, check to see that the other
	  //quest jobs for this quest are complete
	  if (!isQuestComplete) {
		  return true;
	  }
	  
	  if (!checkEntireQuestComplete(questId, completedQuestJobIds,
			  questJobIdsToUserQuestJob)) {
		  log.error("client says user's quest is complete, but it isn't. " +
		  		"userQuestJobs: " + questJobIdsToUserQuestJob);
		  return false;
	  }

	  return true;
  }
  
  //go through each UserQuestJobProto, looking for the donate monster quest
  private void validateDeletingMonsterQuestJob(
		  Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto,
		  List<Long> deleteUserMonsterIds,
		  Map<Long, MonsterForUser> userMonstersInDb) {
	  
	  Map<Integer, UserQuestJobProto> copy =
			  new HashMap<Integer, UserQuestJobProto>(questJobIdToUserQuestJobProto);
	 
	  //verify there's only one donate monster job, if deleting monsters
	  Set<Integer> donateMonsterQuestJobIds = new HashSet<Integer>();
	  for (Integer questJobId : copy.keySet()) {
		  
		  QuestJob qj = QuestJobRetrieveUtils.getQuestJobForQuestJobId(questJobId);
		  
		  String questJobType = qj.getQuestJobType();
		  if (!questJobType.equals(QuestJobType.DONATE_MONSTER.name())) {
			  continue;
		  }
		  donateMonsterQuestJobIds.add(questJobId);
	  }
	  
	  //if there are no donate monster jobs,
	  //deleteUserMonsterIds should be empty or null
	  if (donateMonsterQuestJobIds.isEmpty()) {
		  if (null != deleteUserMonsterIds && !deleteUserMonsterIds.isEmpty()) {
			  log.warn("client wants to delete monsters but there are no" +
			  		" DONATE_MONSTER quest jobs.");
			  deleteUserMonsterIds.clear();
		  }
		  return;
	  }
	  
	  //if there is more than one DONATE_MONSTER quest job, delete them 
	  //and empty out deleteUserMonsterIds
	  if (donateMonsterQuestJobIds.size() > 1) {
		  log.warn("client wants to satisfy more than one DONATE_MONSTER " +
		  		"questJob. removing these questJobIds:" +
				  donateMonsterQuestJobIds);
		  for (Integer questJobId : donateMonsterQuestJobIds) {
			  questJobIdToUserQuestJobProto.remove(questJobId);
		  }
		  deleteUserMonsterIds.clear();
		  return;
	  }
	  
	  log.info("exactly one DONATE_MONSTER quest job, checking if enough" +
	  		" monster ids are sent");
	  
	  int donateQuestJobId =
			  new ArrayList<Integer>(donateMonsterQuestJobIds).get(0); 
	  UserQuestJobProto uqjp = questJobIdToUserQuestJobProto
			  .get(donateQuestJobId);
	  if (!uqjp.getIsComplete()) {
		  log.error("client did not set DONATE_MONSTER quest job to complete");
		  questJobIdToUserQuestJobProto.remove(donateQuestJobId);
		  deleteUserMonsterIds.clear();
		  return;
	  }
	  
	  validateMonstersBeingDeleted(donateQuestJobId,
			  questJobIdToUserQuestJobProto, deleteUserMonsterIds,
			  userMonstersInDb);
  }
  
  private void validateMonstersBeingDeleted(int donateQuestJobId,
		  Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto,
		  List<Long> deleteUserMonsterIds,
		  Map<Long, MonsterForUser> userMonstersInDb) {
	  int deleteSize = deleteUserMonsterIds.size();
	  //make sure the deleted user monster ids exist
	  int existingSize = userMonstersInDb.size();
	  if (deleteSize != existingSize) {
		  log.error("user trying to delete some nonexisting user_monsters." +
				  " deleteIds=" + deleteUserMonsterIds + "\t existing" +
				  " user_monsters=" + userMonstersInDb);
		  questJobIdToUserQuestJobProto.remove(donateQuestJobId);
		  deleteUserMonsterIds.clear();
		  return;
	  }

	  //user wants to delete some monsters, make sure it's the right amount
	  QuestJob qj = QuestJobRetrieveUtils
			  .getQuestJobForQuestJobId(donateQuestJobId);
	  
	  int requiredProgress = qj.getQuantity();
	  UserQuestJobProto uqjp = questJobIdToUserQuestJobProto
			  .get(donateQuestJobId);
	  int questProgress = uqjp.getProgress();
	  
	  if (deleteSize != requiredProgress || requiredProgress != questProgress) {
		  log.error("insufficient vespene gas, jk, userMonsterIds to delete" +
		  		" need: " + requiredProgress + ", sent ids: " +
				  deleteUserMonsterIds);
		  questJobIdToUserQuestJobProto.remove(donateQuestJobId);
		  deleteUserMonsterIds.clear();
		  return;
	  }
	  
	  if (!MonsterStuffUtils.checkAllUserMonstersAreComplete(
			  deleteUserMonsterIds, userMonstersInDb)) {
		  //user trying to delete incomplete user monster
		  log.error("user trying to delete an incomplete user monster." +
				  " deletedUserMonsters=" + userMonstersInDb +
				  "\t QuestJob=" + qj);
		  questJobIdToUserQuestJobProto.remove(donateQuestJobId);
		  deleteUserMonsterIds.clear();
	  }
  }
  
  
  private boolean checkIfQuestJobCanBeUpdated(Quest quest, int questJobId,
		  Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob) {
	  
	  QuestJob qj = QuestJobRetrieveUtils.getQuestJobForQuestJobId(questJobId);
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
	  
	  return true;
  }
  
  private boolean checkIfQuestJobIsComplete(UserQuestJobProto uqjp,
		  QuestJob qj) {
	  int newProgress = uqjp.getProgress();

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
	  
	  return true;
  }
  
  //go through each questJob for quest and see if user finished questJob or
  //has now just finished it
  private boolean checkEntireQuestComplete(int questId,
		  Set<Integer> questJobIdsJustCompleted,
		  Map<Integer, QuestJobForUser> questJobIdsToUserQuestJob) {
	  
	  //get all the quest's quest job ids
	  Map<Integer, QuestJob> questJobIdToQuestJob = QuestJobRetrieveUtils
			  .getQuestJobsForQuestId(questId);
	  
	  //go through all the quest's job ids and see if user completed it
	  for (Integer questJobId : questJobIdToQuestJob.keySet()) {
		  if (questJobIdsJustCompleted.contains(questJobId)) {
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
		  boolean questComplete, 
		  Map<Integer, UserQuestJobProto> questJobIdToUserQuestJobProto,
		  Map<Integer, QuestJobForUser> questJobIdToUserQuestJob,
		  List<Long> deleteUserMonsterIds) {
	  
	  if (questJobIdToUserQuestJobProto.isEmpty()) {
		  return true;
	  }
	  
	  Map<Integer, QuestJobForUser> questJobIdToNewUserQuestJob = QuestUtils.
			  deserializeUserQuestJobProto(questJobIdToUserQuestJobProto);
  	
	  //update user quest jobs
	  int num = getUpdateUtil().updateUserQuestJobs(userId,
			  questJobIdToNewUserQuestJob);
	  log.error("num updated for unredeemd user quest job:" + num);

	  //update user quest
	  if (questComplete && !getUpdateUtil()
			  .updateUserQuestIscomplete(userId, questId)) {
		  log.error("could not update user quest to complete. questId=" +
				  questId);
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
