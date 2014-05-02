package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class AchievementRedeemController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;

	@Autowired
	protected Locker locker;

	public AchievementRedeemController() {
		numAllocatedThreads = 4;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		AchievementRedeemRequestProto reqProto = ((AchievementRedeemRequestEvent)event)
				.getAchievementRedeemRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		int userId = senderProto.getUserId();
		int achievementId = reqProto.getAchievementId();
		Date currentDate = new Date();
		Timestamp now = new Timestamp(currentDate.getTime());

		AchievementRedeemResponseProto.Builder resBuilder =
				AchievementRedeemResponseProto.newBuilder();
		resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);

		getLocker().lockPlayer(userId, this.getClass().getSimpleName());
		try {
			//retrieve whatever is necessary from the db
			//TODO: consider only retrieving user if the request is valid
			User user = RetrieveUtils.userRetrieveUtils()
					.getUserById(senderProto.getUserId());
			int previousGems = 0;

			Map<Integer, AchievementForUser> achievementIdToUserAchievement = 
					getAchievementForUserRetrieveUtil()
					.getSpecificOrAllAchievementIdToAchievementForUserId(
							userId, Collections.singleton(achievementId));
			
			boolean legitRedeem = checkLegitRedeem(resBuilder, userId,
					achievementId, achievementIdToUserAchievement);

			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			boolean success = false;
			if (legitRedeem) {
				previousGems = user.getGems();
				
				success = writeChangesToDB(userId, user, achievementId,
						now, currencyChange);
			}
			
			if (success) {
				resBuilder.setStatus(AchievementRedeemStatus.SUCCESS);
			}
			
			AchievementRedeemResponseEvent resEvent = new AchievementRedeemResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setAchievementRedeemResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);
			
			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				Map<String, Integer> previousCurrency = Collections
						.singletonMap(MiscMethods.gems, previousGems);
				writeToUserCurrencyHistory(user, userId, achievementId,
						currencyChange, previousCurrency, now);
			}
			
			
		} catch (Exception e) {
			log.error("exception in AchievementRedeem processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);
				AchievementRedeemResponseEvent resEvent = new AchievementRedeemResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setAchievementRedeemResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in AchievementRedeem processEvent", e);
			}
		} finally {
			getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
		}
	}


	private boolean checkLegitRedeem(Builder resBuilder, int userId,
			int achievementId,
			Map<Integer, AchievementForUser> achievementIdToUserAchievement) {
		if (null == achievementIdToUserAchievement ||
				achievementIdToUserAchievement.isEmpty() ||
				!achievementIdToUserAchievement.containsKey(achievementId)) {
			resBuilder.setStatus(AchievementRedeemStatus.FAIL_OTHER);
			log.error("userAchievement does not exist. id=" + achievementId +
					"userAchievement=" + achievementIdToUserAchievement);
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
			log.error("userAchievement is already redeemed: " +
					userAchievement);
			return false;
		}
		
		return true;  
	}

	private boolean writeChangesToDB(int userId, User user,
			int achievementId, Timestamp redeemTime,
			Map<String, Integer> currencyChange) {
		int numUpdated = UpdateUtils.get().updateRedeemAchievementForUser(
				userId, Collections.singletonList(achievementId), redeemTime); 
		log.info("user achievements redeemed. numUpdated=" + numUpdated +
				"\t achievementId=" + achievementId);

		Achievement achievement = AchievementRetrieveUtils 
				.getAchievementForAchievementId(achievementId);
		int gemReward = achievement.getGemReward();
		int gemsGained = Math.max(0, gemReward);

		if (0 == gemsGained) {
			log.warn("redeeming achievement does not give gem reward: " +
					achievement);
		}

		if (!user.updateRelativeGemsCashOilExperienceNaive(gemsGained,
				0, 0, 0)) {
			log.error("problem with giving user " + gemsGained + " gems");
		} else {
			//things worked
			if (0 != gemsGained) {
				currencyChange.put(MiscMethods.gems, gemsGained);
			}
		}
		return true;
	}

	public void writeToUserCurrencyHistory(User aUser, int userId,
			int achievementId, Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency, Timestamp curTime) {

		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = MiscMethods.gems;

		String reason = ControllerConstants.UCHRFC__QUEST_REDEEM;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("achievement redeemed=");
		detailsSb.append(achievementId);
		String details = detailsSb.toString();

		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, details);

		MiscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange, 
				previousCurrency, currentCurrency, reasonsForChanges, detailsMap);
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
