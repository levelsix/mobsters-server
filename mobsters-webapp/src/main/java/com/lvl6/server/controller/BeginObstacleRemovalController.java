package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.BeginObstacleRemovalRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.BeginObstacleRemovalResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalRequestProto;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResourceType;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component
public class BeginObstacleRemovalController extends EventController {

	private static Logger log = LoggerFactory.getLogger(BeginObstacleRemovalController.class);

	@Autowired
	protected Locker locker;
	
	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil;
	
	@Autowired
	protected TimeUtils timeUtils;

	public BeginObstacleRemovalController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new BeginObstacleRemovalRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_BEGIN_OBSTACLE_REMOVAL_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		BeginObstacleRemovalRequestProto reqProto = ((BeginObstacleRemovalRequestEvent) event)
				.getBeginObstacleRemovalRequestProto();
		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		Timestamp clientTime = new Timestamp(reqProto.getCurTime());
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int resourceChange = reqProto.getResourceChange();
		ResourceType rt = reqProto.getResourceType();
		String userObstacleId = reqProto.getUserObstacleUuid();

		BeginObstacleRemovalResponseProto.Builder resBuilder = BeginObstacleRemovalResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		
		if(reqProto.getCurTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		if(timeUtils.numMinutesDifference(new Date(reqProto.getCurTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			BeginObstacleRemovalResponseEvent resEvent = 
					new BeginObstacleRemovalResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;

		try {
			userUuid = UUID.fromString(userId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());

		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(
					senderProto.getUserUuid());
			ObstacleForUser ofu = obstacleForUserRetrieveUtil
					.getUserObstacleForId(userObstacleId);

			boolean legitComplete = checkLegit(resBuilder, userId, user,
					userObstacleId, ofu, gemsSpent, resourceChange, rt);

			boolean success = false;
			//make it easier to record currency history
			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
			if (legitComplete) {
				success = writeChangesToDB(user, userObstacleId, gemsSpent,
						resourceChange, rt, clientTime, currencyChange,
						previousCurrency);
			}

			BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(userId, user, clientTime,
						currencyChange, previousCurrency, ofu, rt);
			}

		} catch (Exception e) {
			log.error(
					"exception in BeginObstacleRemovalController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				BeginObstacleRemovalResponseEvent resEvent = new BeginObstacleRemovalResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in BeginObstacleRemovalController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
		}
	}

	private boolean checkLegit(Builder resBuilder, String userId, User user,
			String ofuId, ObstacleForUser ofu, int gemsSpent,
			int resourceChange, ResourceType rt) {

		if (null == user || null == ofu) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error(String
					.format("user or ofu is null. user=%s, userId=%s, ofu=%s, ofuId=%s",
							user, userId, ofu, ofuId));
			return false;
		}

		if (!hasEnoughGems(resBuilder, user, gemsSpent)) {
			return false;
		}

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int resourceRequired = -1 * resourceChange;
		if (ResourceType.CASH.equals(rt)) {
			if (!hasEnoughCash(resBuilder, user, resourceRequired)) {
				return false;
			}
		}

		if (ResourceType.OIL.equals(rt)) {
			if (!hasEnoughOil(resBuilder, user, resourceRequired)) {
				return false;
			}
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder, User u, int gemsSpent) {
		int userGems = u.getGems();
		//if user's aggregate gems is < cost, don't allow transaction
		if (userGems < gemsSpent) {
			log.error(String.format(
					"not enough gems. userGems=%s, gemsSpent=%s", userGems,
					gemsSpent));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder, User u, int cashSpent) {
		int userCash = u.getCash();
		//if user's aggregate cash is < cost, don't allow transaction
		if (userCash < cashSpent) {
			log.error(String.format(
					"not enough cash. userCash=%s, cashSpent=%s", userCash,
					cashSpent));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder, User u, int oilSpent) {
		int userOil = u.getOil();
		//if user's aggregate oil is < cost, don't allow transaction
		if (userOil < oilSpent) {
			log.error(String.format("not enough oil. userOil=%s, oilSpent=%s",
					userOil, oilSpent));
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(User user, String ofuId, int gemsSpent,
			int resourceChange, ResourceType rt, Timestamp clientTime,
			Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency) {

		//update user currency
		int gemsChange = -1 * Math.abs(gemsSpent);
		int cashChange = 0;
		int oilChange = 0;

		if (0 != gemsChange) {
			previousCurrency.put(miscMethods.gems, user.getGems());
		}
		if (ResourceType.CASH.equals(rt)) {
			log.info("user spent cash.");
			cashChange = resourceChange;
			previousCurrency.put(miscMethods.cash, user.getCash());
		}
		if (ResourceType.OIL.equals(rt)) {
			log.info("user spent cash.");
			oilChange = resourceChange;
			previousCurrency.put(miscMethods.oil, user.getOil());
		}

		if (!updateUser(user, gemsChange, cashChange, oilChange)) {
			log.error(String
					.format("could not decrement user's gems by %s, cash by %s,  or oil by %s",
							gemsChange, cashChange, oilChange));
			return false;
		} else {
			if (0 != gemsChange) {
				currencyChange.put(miscMethods.gems, gemsChange);
			}
			if (0 != cashChange) {
				currencyChange.put(miscMethods.cash, cashChange);
			}
			if (0 != oilChange) {
				currencyChange.put(miscMethods.oil, oilChange);
			}
		}

		int numUpdated = UpdateUtils.get().updateObstacleForUserRemovalTime(
				ofuId, clientTime);
		log.info(String.format("(obstacles, should be 1) numUpdated=%s",
				numUpdated));
		return true;
	}

	private boolean updateUser(User u, int gemsChange, int cashChange,
			int oilChange) {
		int numChange = u.updateRelativeCashAndOilAndGems(cashChange,
				oilChange, gemsChange, 0);

		if (numChange <= 0) {
			log.error(String
					.format("problem updating user gems, cash, oil. gemChange=%s, cash=%s, oil=%s, user=%s",
							gemsChange, cashChange, oilChange, u));
			return false;
		}
		return true;
	}

	private void writeToUserCurrencyHistory(String userId, User user,
			Timestamp curTime, Map<String, Integer> currencyChange,
			Map<String, Integer> previousCurrency, ObstacleForUser ofu,
			ResourceType rt) {
		if (currencyChange.isEmpty()) {
			return;
		}

		String reason = ControllerConstants.UCHRFC__REMOVE_OBSTACLE;
		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("obstacleId=");
		detailsSb.append(ofu.getObstacleId());
		detailsSb.append(" x=");
		detailsSb.append(ofu.getXcoord());
		detailsSb.append(" y=");
		detailsSb.append(ofu.getYcoord());
		detailsSb.append(" resourceType=");
		detailsSb.append(rt.name());
		String details = detailsSb.toString();

		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String gems = miscMethods.gems;
		String cash = miscMethods.cash;
		String oil = miscMethods.oil;

		if (currencyChange.containsKey(gems)) {
			currentCurrency.put(gems, user.getGems());
			reasonsForChanges.put(gems, reason);
			detailsMap.put(gems, details);
		}
		if (currencyChange.containsKey(cash)) {
			currentCurrency.put(cash, user.getCash());
			reasonsForChanges.put(cash, reason);
			detailsMap.put(cash, details);
		}
		if (currencyChange.containsKey(oil)) {
			currentCurrency.put(oil, user.getOil());
			reasonsForChanges.put(oil, reason);
			detailsMap.put(oil, details);
		}

		miscMethods.writeToUserCurrencyOneUser(userId, curTime, currencyChange,
				previousCurrency, currentCurrency, reasonsForChanges,
				detailsMap);

	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public ObstacleForUserRetrieveUtil2 getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}

	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil2 obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}

}
