package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EnhancementWaitTimeCompleteRequestEvent;
import com.lvl6.events.response.EnhancementWaitTimeCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteRequestProto;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class EnhancementWaitTimeCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(EnhancementWaitTimeCompleteController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected TimeUtils timeUtils;

	public EnhancementWaitTimeCompleteController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EnhancementWaitTimeCompleteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EnhancementWaitTimeCompleteRequestProto reqProto = ((EnhancementWaitTimeCompleteRequestEvent) event)
				.getEnhancementWaitTimeCompleteRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		boolean isSpeedUp = reqProto.getIsSpeedup();
		int gemsForSpeedUp = reqProto.getGemsForSpeedup();
		//		UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
		//user monster ids that will be deleted from monster enhancing for user table
		//		List<Long> userMonsterIdsThatFinished = reqProto.getUserMonsterIdsList();
		//		userMonsterIdsThatFinished = new ArrayList<Long>(userMonsterIdsThatFinished);
		Timestamp curTime = new Timestamp((new Date()).getTime());
		Date clientTime = new Date(reqProto.getClientTime());
		String curEnhancingMfuId = reqProto.getUserMonsterUuid(); //monster being enhanced

		//set some values to send to the client (the response proto)
		EnhancementWaitTimeCompleteResponseProto.Builder resBuilder = EnhancementWaitTimeCompleteResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		
		if(timeUtils.numMinutesDifference(clientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			UUID.fromString(curEnhancingMfuId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s or curEnhancingMfuId=%s",
					userId, curEnhancingMfuId), e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			int previousGems = 0;
			//			List<Long> userMonsterIds = new ArrayList<Long>();
			//			userMonsterIds.add(umcep.getUserMonsterId()); //monster being enhanced
			//			userMonsterIds.addAll(userMonsterIdsThatFinished);

			//get whatever we need from the database
			User aUser = userRetrieveUtil.getUserById(userId);
			//			Map<Long, MonsterEnhancingForUser> inEnhancing =
			//				MonsterEnhancingForUserRetrieveUtils.getMonstersForUser(userId);
			//			Map<Long, MonsterForUser> idsToUserMonsters = RetrieveUtils
			//				.monsterForUserRetrieveUtils()
			//				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

			//DEPRECATED --> //do check to make sure one monster has a null start time
			boolean legit = checkLegit(resBuilder, aUser, userId,
			//				idsToUserMonsters, inEnhancing, umcep, userMonsterIdsThatFinished,
					isSpeedUp, gemsForSpeedUp);

			Map<String, Integer> money = new HashMap<String, Integer>();
			boolean successful = false;
			if (legit) {
				previousGems = aUser.getGems();
				successful = writeChangesToDb(aUser, userId, curTime,
				//					inEnhancing, umcep, userMonsterIdsThatFinished,
						isSpeedUp, gemsForSpeedUp, curEnhancingMfuId, money);
			}
			if (successful) {
				setResponseBuilder(resBuilder);
			}

			EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//tell the client to update user because user's funds most likely changed
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				//				writeChangesToHistory(userId, inEnhancing, userMonsterIdsThatFinished);
				//				writeToUserCurrencyHistory(aUser, curTime, umcep.getUserMonsterId(), money, previousGems);
				writeToUserCurrencyHistory(aUser, curTime, curEnhancingMfuId,
						money, previousGems);
			}
		} catch (Exception e) {
			log.error(
					"exception in EnhancementWaitTimeCompleteController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
				EnhancementWaitTimeCompleteResponseEvent resEvent = new EnhancementWaitTimeCompleteResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in EnhancementWaitTimeCompleteController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	/**
	 * Return true if user request is valid; false otherwise and set the builder
	 * status to the appropriate value.
	 * 
	 * If user wants to speed up and if user does not have enough funds, will
	 * return fail.
	 * 
	 * @param resBuilder
	 * @param u
	 * @param userId
	 * @param idsToUserMonsters (DEPRECATED) - the monsters the user has
	 * @param inEnhancing (DEPRECATED) - the monsters that are in the enhancing
	 *            queue
	 * @param umcep (DEPRECATED) - the base monster that is updated from using
	 *            up some of the feeders
	 * @param usedUpUserMonsterIds (DEPRECATED) - userMonsterIds the user thinks
	 *            has finished being enhanced
	 * @param speedUp
	 * @param gemsForSpeedUp
	 * @return
	 */
	private boolean checkLegit(Builder resBuilder, User u, String userId,
	//		Map<Long, MonsterForUser> idsToUserMonsters,
	//		Map<Long, MonsterEnhancingForUser> inEnhancing, UserMonsterCurrentExpProto umcep,
	//		List<Long> usedUpMonsterIds,
			boolean speedUp, int gemsForSpeedUp) {

		//		if (null == u || null == umcep || usedUpMonsterIds.isEmpty()) {
		if (null == u) {
			log.error(String
					.format(
					//				"user or idList is null. userId=%s, user=%s, umcep=%s, usedUpMonsterIds=%s, speedup=%s, gemsForSpeedup=%s",
					//				userId, u, umcep, usedUpMonsterIds, speedup, gemsForSpeedup));
					"user is null. userId=%s, user=%s, speedUp=%s, gemsForSpeedUp=%s",
							userId, u, speedUp, gemsForSpeedUp));
			return false;
		}
		//		log.info("inEnhancing=" + inEnhancing);
		//		long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();

		//make sure that the user monster ids that will be deleted will only be
		//the ids that exist in enhancing table
		//		Set<Long> inEnhancingIds = inEnhancing.keySet();
		//		MonsterStuffUtils.retainValidMonsterIds(inEnhancingIds, usedUpMonsterIds);

		//check to make sure the base monsterId is in enhancing
		//		if (!inEnhancingIds.contains(userMonsterIdBeingEnhanced)) {
		//			log.error("client did not send updated base monster specifying what new exp and lvl are");
		//			return false;
		//		}

		/* NOT SURE IF THESE ARE  NECESSARY, SO DOING IT ANYWAY*/
		//check to make sure the monster being enhanced is part of the
		//user's monsters
		//		if (!idsToUserMonsters.containsKey(userMonsterIdBeingEnhanced)) {
		//			log.error(String.format(
		//				"monster being enhanced doesn't exist!. userMonsterIdBeingEnhanced=%s, deleteIds=%s, inEnhancing=%s, gemsForSpeedup=%s, speedup=%s", 
		//				userMonsterIdBeingEnhanced, usedUpMonsterIds, inEnhancing, gemsForSpeedup, speedup));
		//			return false;
		//		}

		//retain only the valid monster for user ids that will be deleted
		//		Set<Long> existingIds = idsToUserMonsters.keySet();
		//		MonsterStuffUtils.retainValidMonsterIds(existingIds, usedUpMonsterIds);

		//CHECK MONEY and CHECK SPEEDUP
		if (speedUp) {
			int userGems = u.getGems();

			if (userGems < gemsForSpeedUp) {
				log.error(String
						.format(
						//					"insufficient gems to speed up enhancing. userGems=%s, cost=%s, umcep=%s, inEnhancing=%s, deleteIds=%s",
						//					userGems, gemsForSpeedup, umcep, inEnhancing, usedUpMonsterIds));
						"insufficient gems to speed up enhancing. userGems=%s, cost=%s",
								userGems, gemsForSpeedUp));
				resBuilder
						.setStatus(ResponseStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User u, String uId,
			Timestamp clientTime,
			//		Map<Long, MonsterEnhancingForUser> inEnhancing, UserMonsterCurrentExpProto umcep,
			//		List<Long> userMonsterIds,
			boolean isSpeedup, int gemsForSpeedup, String curEnhancingMfuId,
			Map<String, Integer> money) {

		if (isSpeedup) {
			//CHARGE THE USER
			int gemCost = -1 * gemsForSpeedup;
			if (!u.updateRelativeGemsNaive(gemCost, 0)) {
				log.error(String
						.format(
						//					"problem updating user gems. gemsForSpeedup=%s, clientTime=%s, baseMonster=%s, userMonsterIdsToDelete=%s, user=%s",
						//					gemsForSpeedup, clientTime, umcep, userMonsterIds, u));
						"problem updating user gems. gemsForSpeedup=%s, clientTime=%s, user=%s, curEnhancingMfuId=%s",
								gemsForSpeedup, clientTime, u,
								curEnhancingMfuId));
				return false;
			} else {
				if (0 != gemCost) {
					money.put(miscMethods.gems, gemCost);
				}
			}
		}

		//		long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();
		//		int newExp = umcep.getExpectedExperience();
		//		int newLvl = umcep.getExpectedLevel();
		//		int newHp = umcep.getExpectedHp();
		//
		//		//GIVE THE MONSTER EXP
		//		int num = UpdateUtils.get().updateUserMonsterExpAndLvl(userMonsterIdBeingEnhanced,
		//			newExp, newLvl, newHp);

		//update monster being enhanced to say it is finished
		int num = UpdateUtils.get().updateCompleteEnhancing(uId,
				curEnhancingMfuId);
		log.info(String.format(
				"numUpdated (enhancements completed, expected 1)=%s", num));

		return true;
	}

	private void setResponseBuilder(Builder resBuilder) {
	}

	private void writeChangesToHistory(String uId,
			Map<String, MonsterEnhancingForUser> inEnhancing,
			List<String> userMonsterIds) {

		//TODO: keep track of the userMonsters that are deleted

		//TODO: keep track of the monsters that were enhancing

		//delete the selected monsters from  the enhancing table
		int num = DeleteUtils.get().deleteMonsterEnhancingForUser(uId,
				userMonsterIds);
		log.info(String
				.format("deleted monster healing rows. numDeleted=%s, userMonsterIds=%s, inEnhancing=%s",
						num, userMonsterIds, inEnhancing));

		//delete the userMonsterIds from the monster_for_user table, but don't delete
		//the monster user is enhancing
		num = DeleteUtils.get().deleteMonstersForUser(userMonsterIds);
		log.info(String
				.format("defeated monster_for_user rows. numDeleted=%s, inEnhancing=%s",
						num, inEnhancing));

	}

	private void writeToUserCurrencyHistory(User aUser, Timestamp curTime,
			String userMonsterId, Map<String, Integer> money, int previousGems) {
		if (money.isEmpty()) {
			return;
		}
		String gems = miscMethods.gems;
		String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_ENHANCING;

		String userId = aUser.getId();
		Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();

		previousCurrencies.put(gems, previousGems);
		currentCurrencies.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reasonForChange);
		detailsMap.put(gems, " userMonsterId=" + userMonsterId);
		miscMethods.writeToUserCurrencyOneUser(userId, curTime, money,
				previousCurrencies, currentCurrencies, reasonsForChanges,
				detailsMap);

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
