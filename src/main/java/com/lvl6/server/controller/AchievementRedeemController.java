package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AchievementRedeemRequestEvent;
import com.lvl6.events.response.AchievementRedeemResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Achievement;
import com.lvl6.info.AchievementForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemRequestProto;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemResponseProto;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemResponseProto.AchievementRedeemStatus;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class AchievementRedeemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	AchievementRetrieveUtils achievementRetrieveUtils;

	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	public AchievementRedeemController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new AchievementRedeemRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ACHIEVEMENT_REDEEM_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		AchievementRedeemRequestProto reqProto = ((AchievementRedeemRequestEvent) event)
				.getAchievementRedeemRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int achievementId = reqProto.getAchievementId();
		Date currentDate = new Date();
		Timestamp now = new Timestamp(currentDate.getTime());

		AchievementRedeemResponseProto.Builder resBuilder = AchievementRedeemResponseProto
				.newBuilder();
		resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = false;
		try {
			userUuid = UUID.fromString(userId);
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);
			AchievementRedeemResponseEvent resEvent = new AchievementRedeemResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setAchievementRedeemResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			//TODO: consider only retrieving user if the request is valid
			User user = userRetrieveUtil.getUserById(senderProto.getUserUuid());
			int previousGems = 0;

			Map<Integer, AchievementForUser> achievementIdToUserAchievement = getAchievementForUserRetrieveUtil()
					.getSpecificOrAllAchievementIdToAchievementForUserId(
							userId, Collections.singleton(achievementId));

			boolean legitRedeem = checkLegitRedeem(resBuilder, userId,
					achievementId, achievementIdToUserAchievement);

			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			boolean success = false;
			if (legitRedeem) {
				previousGems = user.getGems();

				success = writeChangesToDB(userId, user, achievementId, now,
						currencyChange);
			}

			if (success) {
				resBuilder.setStatus(AchievementRedeemStatus.SUCCESS);
			}

			AchievementRedeemResponseEvent resEvent = new AchievementRedeemResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setAchievementRedeemResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				Map<String, Integer> previousCurrency = Collections
						.singletonMap(miscMethods.gems, previousGems);
				writeToUserCurrencyHistory(user, userId, achievementId,
						currencyChange, previousCurrency, now);
			}

		} catch (Exception e) {
			log.error("exception in AchievementRedeem processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);
				AchievementRedeemResponseEvent resEvent = new AchievementRedeemResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setAchievementRedeemResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in AchievementRedeem processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegitRedeem(Builder resBuilder, String userId,
			int achievementId,
			Map<Integer, AchievementForUser> achievementIdToUserAchievement) {
		if (null == achievementIdToUserAchievement
				|| achievementIdToUserAchievement.isEmpty()
				|| !achievementIdToUserAchievement.containsKey(achievementId)) {
			resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);
			log.error(String
					.format("userAchievement does not exist. id=%s, userAchievement=%s",
							achievementId, achievementIdToUserAchievement));
			return false;
		}

		AchievementForUser userAchievement = achievementIdToUserAchievement
				.get(achievementId);
		if (!userAchievement.isComplete()) {
			resBuilder.setStatus(AchievementRedeemStatus.FAIL_NOT_COMPLETE);
			log.error("userAchievement is not complete");
			return false;
		}

		if (userAchievement.isRedeemed()) {
			resBuilder.setStatus(AchievementRedeemStatus.FAIL_ALREADY_REDEEMED);
			log.error(String.format("userAchievement is already redeemed: %s",
					userAchievement));
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(String userId, User user,
			int achievementId, Timestamp redeemTime,
			Map<String, Integer> currencyChange) {
		int numUpdated = UpdateUtils.get().updateRedeemAchievementForUser(
				userId, Collections.singletonList(achievementId), redeemTime);
		log.info(String.format(
				"user achievements redeemed. numUpdated=%s, achievementId=%s",
				numUpdated, achievementId));

		Achievement achievement = achievementRetrieveUtils
				.getAchievementForAchievementId(achievementId);
		int gemReward = achievement.getGemReward();
		int gemsGained = Math.max(0, gemReward);
		int expGained = achievement.getExpReward();

		if (0 == gemsGained) {
			log.info(String.format(
					"redeeming achievement does not give gem reward: %s",
					achievement));
		}

		if (!user.updateRelativeGemsCashOilExperienceNaive(gemsGained, 0, 0,
				expGained)) {
			log.error(String.format("problem with giving user %s gems",
					gemsGained));
		} else {
			//things worked
			if (0 != gemsGained) {
				currencyChange.put(miscMethods.gems, gemsGained);
			}
		}
		return true;
	}

	public void writeToUserCurrencyHistory(User aUser, String userId,
			int achievementId, Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency, Timestamp curTime) {

		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;

		String reason = ControllerConstants.UCHRFC__QUEST_REDEEM;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("achievement redeemed=");
		detailsSb.append(achievementId);
		String details = detailsSb.toString();

		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, details);

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);
	}

	public AchievementForUserRetrieveUtil getAchievementForUserRetrieveUtil() {
		return achievementForUserRetrieveUtil;
	}

	public void setAchievementForUserRetrieveUtil(
			AchievementForUserRetrieveUtil achievementForUserRetrieveUtil) {
		this.achievementForUserRetrieveUtil = achievementForUserRetrieveUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
