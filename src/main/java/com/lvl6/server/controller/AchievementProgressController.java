package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AchievementProgressRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.info.AchievementForUser;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;
import com.lvl6.proto.EventAchievementProto.AchievementProgressRequestProto;
import com.lvl6.proto.EventAchievementProto.AchievementProgressResponseProto;
import com.lvl6.proto.EventAchievementProto.AchievementProgressResponseProto.AchievementProgressStatus;
import com.lvl6.proto.EventAchievementProto.AchievementProgressResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.AchievementStuffUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component @DependsOn("gameServer") public class AchievementProgressController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected AchievementStuffUtil achievementStuffUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;

	@Autowired
	protected UpdateUtil updateUtil;

	public AchievementProgressController() {
		numAllocatedThreads = 3;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new AchievementProgressRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ACHIEVEMENT_PROGRESS_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		AchievementProgressRequestProto reqProto = ((AchievementProgressRequestEvent)event).getAchievementProgressRequestProto();
		
		log.info(String.format("reqProto=%s", reqProto));

		//get stuff client sent
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<UserAchievementProto> uapList = reqProto.getUapListList();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());

		Map<Integer, UserAchievementProto> achievementIdsToUap =
				getAchievementStuffUtil().achievementIdsToUap(uapList);
		
		//set stuff to send to the client
		AchievementProgressResponseProto.Builder resBuilder = AchievementProgressResponseProto.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(AchievementProgressStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		
		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
				"UUID error. incorrect userId=%s",
				userId), e);
		}
		
		//UUID checks
	    if (invalidUuids) {
	    	resBuilder.setStatus(AchievementProgressStatus.FAIL_OTHER);
			AchievementProgressResponseEvent resEvent = new AchievementProgressResponseEvent(userId);
	      	resEvent.setTag(event.getTag());
	      	resEvent.setAchievementProgressResponseProto(resBuilder.build());
	      	server.writeEvent(resEvent);
	    	return;
	    }
		
		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			
			//achievementIdsToUap might be modified
			boolean legitProgress = checkLegitProgress(resBuilder, userId,
					uapList, achievementIdsToUap);

			boolean success = false;
			if (legitProgress) {
				success = writeChangesToDB(userId, clientTime, achievementIdsToUap);
			}

			if (success) {
				resBuilder.setStatus(AchievementProgressStatus.SUCCESS);
			}

			AchievementProgressResponseEvent resEvent = new AchievementProgressResponseEvent(senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setAchievementProgressResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

		} catch (Exception e) {
			log.error("exception in AchievementProgress processEvent", e);
			resBuilder.setStatus(AchievementProgressStatus.FAIL_OTHER);
			AchievementProgressResponseEvent resEvent = new AchievementProgressResponseEvent(userId);
	      	resEvent.setTag(event.getTag());
	      	resEvent.setAchievementProgressResponseProto(resBuilder.build());
	      	server.writeEvent(resEvent);
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
		}
	}


	//achievementIdsToUap might be modified
	private boolean checkLegitProgress(Builder resBuilder, String userId,
			List<UserAchievementProto> uapList,
			Map<Integer, UserAchievementProto> achievementIdsToUap) {

		//make sure the quest, relating to the user_quest updated, exists
		if (null == uapList || uapList.isEmpty()) {
			log.error(String.format(
				"parameter passed in is null. uapList=%s", uapList));
			return false;
		}
		
		filterOutInvalidAchievements(achievementIdsToUap);
		filterOutCompletedAchievements(userId, achievementIdsToUap);

		return true;
	}

	private void filterOutInvalidAchievements(
			Map<Integer, UserAchievementProto> achievementIdsToUap) {

		Set<Integer> achievementIds = new HashSet<Integer>(
				achievementIdsToUap.keySet());
		
		//filter out the invalid achievements
		Set<Integer> validAchievementIds = AchievementRetrieveUtils
				.getAchievementIds();

		for (Integer achievementId : achievementIds) {

			if (validAchievementIds.contains(achievementId)) {
				//valid achievement
				continue;
			}
			//filtering out invalid achievements
			UserAchievementProto uap = achievementIdsToUap.get(achievementId);
			log.error(String.format(
				"removing invalid achievement client sent: %s, valid ids are: %s",
				uap, validAchievementIds));
			
			achievementIdsToUap.remove(achievementId);
		}
	}
	
	private void filterOutCompletedAchievements(String userId,
			Map<Integer, UserAchievementProto> achievementIdsToUap) {
		

		Set<Integer> achievementIds = new HashSet<Integer>(
				achievementIdsToUap.keySet());
		
		//get all the achievements, for this user, that the client sent
		Map<Integer, AchievementForUser> achievementIdToUserAchievements =
				getAchievementForUserRetrieveUtil()
				.getSpecificOrAllAchievementIdToAchievementForUserId(
						userId, achievementIds);


		for (Integer achievementId : achievementIds) {
			if (!achievementIdToUserAchievements.containsKey(achievementId)) {
				//user is just beginning this achievement
				continue;
			}
			//note: user is updating existing achievement's progress

			//filtering out completed achievements
			AchievementForUser afu = achievementIdToUserAchievements
					.get(achievementId);
			if (afu.isComplete()) {
				UserAchievementProto uap = achievementIdsToUap
						.get(achievementId);
				log.warn(String.format(
					"client updating completed achievement: %s, with new values: %s, %s",
					afu, uap, " removing this requested update"));
				achievementIdsToUap.remove(achievementId);
				continue;
			}

		}
	}

	private boolean writeChangesToDB(String userId, Timestamp clientTime,
			Map<Integer, UserAchievementProto> achievementIdsToUap) {

		if (achievementIdsToUap.isEmpty()) {
			log.warn("writeChangesToDB() no achievements to update");
			return true;
		}
		
		//transform UserAchievementProto into AchievementForUser objects
		Map<Integer, AchievementForUser> achievementIdToAfu =
				getAchievementStuffUtil()
				.javafyUserAchievementProto(achievementIdsToUap);
		
//		log.info("writeChangesToDB() achievementIdToAfu" +
//				achievementIdToAfu);

		int numUpdated = getUpdateUtil().updateUserAchievement(userId,
				clientTime, achievementIdToAfu);
		
		log.info("writeChangesToDB() numUpdated=" + numUpdated);
		
		return true;
	}


	public AchievementStuffUtil getAchievementStuffUtil() {
		return achievementStuffUtil;
	}

	public void setAchievementStuffUtil(AchievementStuffUtil achievementStuffUtil) {
		this.achievementStuffUtil = achievementStuffUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public AchievementForUserRetrieveUtil getAchievementForUserRetrieveUtil() {
		return achievementForUserRetrieveUtil;
	}

	public void setAchievementForUserRetrieveUtil(
			AchievementForUserRetrieveUtil questForUserRetrieveUtil) {
		this.achievementForUserRetrieveUtil = questForUserRetrieveUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}


}
