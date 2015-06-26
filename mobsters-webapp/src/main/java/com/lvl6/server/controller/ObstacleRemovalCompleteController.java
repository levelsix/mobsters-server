package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ObstacleRemovalCompleteRequestEvent;
import com.lvl6.events.response.ObstacleRemovalCompleteResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.DeleteUtils;

@Component

public class ObstacleRemovalCompleteController extends EventController {

	private static Logger log = LoggerFactory.getLogger(ObstacleRemovalCompleteController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;
	
	@Autowired
	protected ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	public ObstacleRemovalCompleteController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ObstacleRemovalCompleteRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_OBSTACLE_REMOVAL_COMPLETE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		ObstacleRemovalCompleteRequestProto reqProto = ((ObstacleRemovalCompleteRequestEvent) event)
				.getObstacleRemovalCompleteRequestProto();

		log.info("reqProto=" + reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		boolean speedUp = reqProto.getSpeedUp();
		int gemCostToSpeedUp = reqProto.getGemsSpent();
		String userObstacleId = reqProto.getUserObstacleUuid();
		boolean atMaxObstacles = reqProto.getAtMaxObstacles();

		ObstacleRemovalCompleteResponseProto.Builder resBuilder = ObstacleRemovalCompleteResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		UUID userUuid = null;
		UUID userObstacleUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			userObstacleUuid = UUID.fromString(userObstacleId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect posterId=%s, recipientId=%s",
					userId, userObstacleId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			ObstacleRemovalCompleteResponseEvent resEvent = new ObstacleRemovalCompleteResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			User user = getUserRetrieveUtils().getUserById(
					senderProto.getUserUuid());
			ObstacleForUser ofu = getObstacleForUserRetrieveUtil()
					.getUserObstacleForId(userObstacleId);

			boolean legitExpansionComplete = checkLegit(resBuilder, userId,
					user, userObstacleId, ofu, speedUp, gemCostToSpeedUp);
			int previousGems = 0;

			boolean success = false;
			Map<String, Integer> money = new HashMap<String, Integer>();
			if (legitExpansionComplete) {
				previousGems = user.getGems();
				success = writeChangesToDB(user, userObstacleId, speedUp,
						gemCostToSpeedUp, clientTime, atMaxObstacles, money);
			}

			ObstacleRemovalCompleteResponseEvent resEvent = new ObstacleRemovalCompleteResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (success && (speedUp || atMaxObstacles)) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(userId, user, clientTime, money,
						previousGems, ofu);
			}

		} catch (Exception e) {
			log.error(
					"exception in ObstacleRemovalCompleteController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				ObstacleRemovalCompleteResponseEvent resEvent = new ObstacleRemovalCompleteResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in ObstacleRemovalCompleteController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, String userId, User user,
			String ofuId, ObstacleForUser ofu, boolean speedUp,
			int gemCostToSpeedup) {

		if (null == user || null == ofu) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error("unexpected error: user or obstacle for user is null. user="
					+ user
					+ "\t userId="
					+ userId
					+ "\t obstacleForUser="
					+ ofu + "\t ofuId=" + ofuId);
			return false;
		}

		if (speedUp && user.getGems() < gemCostToSpeedup) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			log.error("user error: user does not have enough gems to speed up removal."
					+ "\t obstacleForUser="
					+ ofu
					+ "\t cost="
					+ gemCostToSpeedup);
			return false;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDB(User user, String ofuId, boolean speedUp,
			int gemCost, Timestamp clientTime, boolean atMaxObstacles,
			Map<String, Integer> money) {
		int gemChange = -1 * gemCost;
		int obstaclesRemovedDelta = 1;

		if (speedUp && atMaxObstacles) {
			log.info("isSpeedup and maxObstacles");
		} else if (speedUp) {
			log.info("isSpeedup");
			clientTime = null;
		} else if (atMaxObstacles) {
			log.info("maxObstacles");
			gemChange = 0;
			//if user at max obstacles and removes one, a new obstacle
			//should spawn in the amount of time it takes to spawn one, not
			//right when user clears obstacle
		} else {
			gemChange = 0;
			clientTime = null;
			log.info("not isSpeedup and not maxObstacles");
		}

		if (!user.updateRelativeGemsObstacleTimeNumRemoved(gemChange,
				clientTime, obstaclesRemovedDelta)) {
			log.error("problem updating user gems. gemChange=" + gemChange);
			return false;
		} else {
			//everything went ok
			if (0 != gemChange) {
				money.put(miscMethods.gems, gemChange);
			}
		}

		int numDeleted = DeleteUtils.get().deleteObstacleForUser(ofuId);
		log.info("(obstacles) numDeleted=" + numDeleted);
		return true;
	}

	private void writeToUserCurrencyHistory(String userId, User user,
			Timestamp curTime, Map<String, Integer> currencyChange,
			int previousGems, ObstacleForUser ofu) {
		String reason = ControllerConstants.UCHRFC__SPED_UP_REMOVE_OBSTACLE;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("obstacleId=");
		detailsSb.append(ofu.getObstacleId());
		detailsSb.append(" x=");
		detailsSb.append(ofu.getXcoord());
		detailsSb.append(" y=");
		detailsSb.append(ofu.getYcoord());
		String details = detailsSb.toString();

		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;

		previousCurrency.put(gems, previousGems);
		currentCurrency.put(gems, user.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, details);

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);

	}

	public ObstacleForUserRetrieveUtil2 getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}

	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

}
