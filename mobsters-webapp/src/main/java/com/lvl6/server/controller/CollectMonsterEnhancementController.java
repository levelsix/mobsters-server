package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.CollectMonsterEnhancementRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.CollectMonsterEnhancementResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterDeleteHistory;
import com.lvl6.info.MonsterEnhanceHistory;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementRequestProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class CollectMonsterEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(CollectMonsterEnhancementController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	protected TimeUtils timeUtils;

	public CollectMonsterEnhancementController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new CollectMonsterEnhancementRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_COLLECT_MONSTER_ENHANCEMENT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		CollectMonsterEnhancementRequestProto reqProto = ((CollectMonsterEnhancementRequestEvent) event)
				.getCollectMonsterEnhancementRequestProto();

		log.info("reqProto={}", reqProto);

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Date clientTime = new Date(reqProto.getClientTime());
		UserMonsterCurrentExpProto umcep = reqProto.getUmcep();
		//user monster ids that will be deleted from monster enhancing for user table
		List<String> userMonsterIdsThatFinished = reqProto
				.getUserMonsterUuidsList();
		userMonsterIdsThatFinished = new ArrayList<String>(
				userMonsterIdsThatFinished);

		//set some values to send to the client (the response proto)
		CollectMonsterEnhancementResponseProto.Builder resBuilder = CollectMonsterEnhancementResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(clientTime, new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			StringUtils.convertToUUID(userMonsterIdsThatFinished);
			UUID.fromString(umcep.getUserMonsterUuid());
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s or userMonsterIdsThatFinished=%s "
							+ "or usermonstercurrexpproto's usermonsterid",
					userId, userMonsterIdsThatFinished,
					umcep.getUserMonsterUuid()), e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			List<String> userMonsterIds = new ArrayList<String>();
			if (null != umcep && !userMonsterIdsThatFinished.isEmpty()) {
				userMonsterIds.add(umcep.getUserMonsterUuid()); //monster being enhanced
				userMonsterIds.addAll(userMonsterIdsThatFinished);
			}

			Map<String, MonsterForUser> enhancedAndDeletedMonsterForUsers = monsterForUserRetrieveUtil
					.getSpecificUserMonsters(userMonsterIds);

			MonsterForUser mfu = enhancedAndDeletedMonsterForUsers.get(umcep
					.getUserMonsterUuid());
			enhancedAndDeletedMonsterForUsers
					.remove(umcep.getUserMonsterUuid());

			//get whatever we need from the database
			Map<String, MonsterEnhancingForUser> inEnhancing = monsterEnhancingForUserRetrieveUtil
					.getMonstersForUser(userId);

			User aUser = userRetrieveUtil.getUserById(userId);

			MonsterEnhancingForUser mefu = inEnhancing.get(umcep
					.getUserMonsterUuid());

			boolean legit = checkLegit(resBuilder, userId, userMonsterIds,
					inEnhancing, umcep, userMonsterIdsThatFinished, mefu);

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(aUser, umcep, mfu);
			}
			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				writeChangesToHistory(userId, umcep, mfu, inEnhancing, mefu,
						userMonsterIdsThatFinished, userMonsterIds);

				writeToMonsterDeleteHistory(enhancedAndDeletedMonsterForUsers);
				log.info("added deleted monsters to monster delete table");
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}
		} catch (Exception e) {
			log.error(
					"exception in CollectMonsterEnhancementController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
				CollectMonsterEnhancementResponseEvent resEvent = new CollectMonsterEnhancementResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in CollectMonsterEnhancementController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Return true if user request is valid; false otherwise and set the builder
	 * status to the appropriate value.
	 *
	 * True if monster being enhanced has completed enhancing and all the
	 * monsters involved in enhancing is accounted for by what client sent
	 *
	 * @param resBuilder
	 * @param u
	 * @param userId
	 * @param userMonsterIds - monster ids client sent that are in enhancing
	 * @param inEnhancing - the monsters that are in the enhancing queue
	 * @param umcep - the base monster that is updated from using up some of the
	 *            feeders
	 * @param usedUpUserMonsterIds - userMonsterIds the user thinks has finished
	 *            being enhanced
	 * @return
	 */
	private boolean checkLegit(Builder resBuilder, String userId,
			List<String> userMonsterIds,
			Map<String, MonsterEnhancingForUser> inEnhancing,
			UserMonsterCurrentExpProto umcep, List<String> usedUpMonsterIds,
			MonsterEnhancingForUser mefu) {

		if (null == umcep || usedUpMonsterIds.isEmpty()) {
			log.error(String
					.format("deficient enhancing data. umcep=%s, usedUpMonsterIds=%s, inEnhancing=%s",
							umcep, usedUpMonsterIds, inEnhancing));
			return false;
		}
		log.info(String.format("inEnhancing=%s", inEnhancing));
		//		long userMonsterIdBeingEnhanced = umcep.getUserMonsterId();

		//make sure that all the monsters involved in enhancing is accounted for
		Set<String> inEnhancingIds = inEnhancing.keySet();

		if (inEnhancingIds.size() != userMonsterIds.size()
				|| !inEnhancingIds.containsAll(userMonsterIds)) {
			log.error(String
					.format("inconsistent enhancing data. umcep=%s, usedUpMonsterIds=%s, inEnhancing=%s",
							umcep, usedUpMonsterIds, inEnhancing));
			return false;
		}

		//check to make sure the base is complete
		if (!mefu.isEnhancingComplete()) {
			log.error(String.format(
					"base monster being enhanced is incomplete: %s", mefu));
			return false;
		}

		return true;
	}

	private boolean writeChangesToDb(User u, UserMonsterCurrentExpProto umcep,
			MonsterForUser mfu) {

		String userMonsterIdBeingEnhanced = umcep.getUserMonsterUuid();
		int newExp = umcep.getExpectedExperience();
		int newLvl = umcep.getExpectedLevel();
		int newHp = umcep.getExpectedHp();

		//reward the user with exp

		awardUserExp(u, userMonsterIdBeingEnhanced, newExp, mfu);

		//GIVE THE MONSTER EXP
		int num = UpdateUtils.get().updateUserMonsterExpAndLvl(
				userMonsterIdBeingEnhanced, newExp, newLvl, newHp);
		log.info(String.format(
				"numUpdated (monster being enhanced, expected 1)=%s", num));

		return true;
	}

	private void awardUserExp(User u, String userMonsterIdBeingEnhanced,
			int newExp, MonsterForUser mfu) {
		try {
			float expReward = (float) newExp - (float) mfu.getCurrentExp();
			expReward *= ControllerConstants.MONSTER_ENHANCING__PLAYER_EXP_CONVERTER;
			log.info(String.format("expReward for enhancing=%s, userBefore=%s",
					expReward, u));

			if (expReward > 0) {
				u.updateRelativeGemsNaive(0, (int) expReward);
				log.info(String.format("expReward for userAfter=%s", u));
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"can't reward user exp for enhancing. mfu=%s, u=%s",
							mfu, u), e);
		}
	}

	private void writeChangesToHistory(String userId,
			UserMonsterCurrentExpProto umcep, MonsterForUser mfu,
			Map<String, MonsterEnhancingForUser> inEnhancing,
			MonsterEnhancingForUser mefu, List<String> usedUpMfuIds,
			List<String> allEnhancingMfuIds) {
		int expectedExp = umcep.getExpectedExperience();
		int prevExp = mfu.getCurrentExp();
		List<Timestamp> expectedStartTimes = new ArrayList<Timestamp>();
		for (String id : inEnhancing.keySet()) {
			if (inEnhancing.get(id).getExpectedStartTime() != null) {
				expectedStartTimes.add(new Timestamp(inEnhancing.get(id)
						.getExpectedStartTime().getTime()));
			}
		}
		Date now = new Date();
		Timestamp timeOfEntry = new Timestamp(now.getTime());

		MonsterEnhanceHistory meh = new MonsterEnhanceHistory(userId,
				mefu.getMonsterForUserId(), usedUpMfuIds, expectedExp, prevExp,
				expectedStartTimes, timeOfEntry, mefu.getEnhancingCost());

		InsertUtils.get().insertMonsterEnhanceHistory(meh);

		//delete the selected monsters from  the enhancing table
		int num = DeleteUtils.get().deleteMonsterEnhancingForUser(userId,
				allEnhancingMfuIds);
		log.info(String
				.format("deleted monster enhancing rows. numDeleted=%s, userMonsterIds=%s, inEnhancing=%s",
						num, allEnhancingMfuIds, inEnhancing));

		//delete the userMonsterIds from the monster_for_user table, but don't delete
		//the monster user is enhancing
		num = DeleteUtils.get().deleteMonstersForUser(usedUpMfuIds);
		log.info(String
				.format("deleted monster_for_user rows. numDeleted=%s, inEnhancing=%s, deletedIds=%s",
						num, inEnhancing, usedUpMfuIds));

	}

	private void writeToMonsterDeleteHistory(
			Map<String, MonsterForUser> deletedMonsterForUsers) {
		String deletedReason = "enhancing";
		Date now = new Date();
		Timestamp deletedTime = new Timestamp(now.getTime());
		List<MonsterDeleteHistory> deletedHistoryList = new ArrayList<MonsterDeleteHistory>();

		for (String id : deletedMonsterForUsers.keySet()) {
			MonsterForUser mfu = deletedMonsterForUsers.get(id);
			String details = "feeder monsters";
			MonsterDeleteHistory mdh = new MonsterDeleteHistory(mfu,
					deletedReason, details, deletedTime);
			deletedHistoryList.add(mdh);

		}

		InsertUtils.get().insertMonsterDeleteHistory(deletedHistoryList);
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterEnhancingForUserRetrieveUtils2 getMonsterEnhancingForUserRetrieveUtil() {
		return monsterEnhancingForUserRetrieveUtil;
	}

	public void setMonsterEnhancingForUserRetrieveUtil(
			MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil) {
		this.monsterEnhancingForUserRetrieveUtil = monsterEnhancingForUserRetrieveUtil;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtil() {
		return monsterForUserRetrieveUtil;
	}

	public void setMonsterForUserRetrieveUtil(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtil) {
		this.monsterForUserRetrieveUtil = monsterForUserRetrieveUtil;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

}
