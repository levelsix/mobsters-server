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
import com.lvl6.events.request.UpgradeNormStructureRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.events.response.UpgradeNormStructureResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResourceType;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class UpgradeNormStructureController extends EventController {

	private static Logger log = LoggerFactory.getLogger(UpgradeNormStructureController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected StructureForUserRetrieveUtils2 userStructRetrieveUtils;

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected TimeUtils timeUtils;

	public UpgradeNormStructureController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new UpgradeNormStructureRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPGRADE_NORM_STRUCTURE_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UpgradeNormStructureRequestProto reqProto = ((UpgradeNormStructureRequestEvent) event)
				.getUpgradeNormStructureRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String userStructId = reqProto.getUserStructUuid();
		Timestamp timeOfUpgrade = new Timestamp(reqProto.getTimeOfUpgrade());
		int gemsSpent = reqProto.getGemsSpent();
		//positive means refund, negative means charge user
		int resourceChange = reqProto.getResourceChange();
		ResourceType rt = reqProto.getResourceType();

		UpgradeNormStructureResponseProto.Builder resBuilder = UpgradeNormStructureResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		if(reqProto.getTimeOfUpgrade() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getTimeOfUpgrade()), new Date()) >
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			UpgradeNormStructureResponseEvent resEvent =
					new UpgradeNormStructureResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			UUID.fromString(userStructId);
			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s or userStructId=%s",
					userId, userStructId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			User user = getUserRetrieveUtils().getUserById(userId);
			Structure currentStruct = null;
			Structure nextLevelStruct = null;
			StructureForUser userStruct = getUserStructRetrieveUtils()
					.getSpecificUserStruct(userStructId);

			if (userStruct != null) {
				currentStruct = structureRetrieveUtils
						.getStructForStructId(userStruct.getStructId());
				nextLevelStruct = structureRetrieveUtils
						.getUpgradedStructForStructId(userStruct.getStructId());
			}
			int previousCash = 0;
			int previousOil = 0;
			int previousGems = 0;

			boolean legitUpgrade = checkLegitUpgrade(resBuilder, user,
					userStruct, currentStruct, nextLevelStruct, gemsSpent,
					resourceChange, rt, timeOfUpgrade);
			UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (legitUpgrade) {
				previousCash = user.getCash();
				previousOil = user.getOil();
				previousGems = user.getGems();

				Map<String, Integer> money = new HashMap<String, Integer>();
				writeChangesToDB(user, userStruct, nextLevelStruct, gemsSpent,
						resourceChange, rt, timeOfUpgrade, money);
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

				writeToUserCurrencyHistory(user, userStruct, currentStruct,
						nextLevelStruct, timeOfUpgrade, money, previousCash,
						previousOil, previousGems);
			}
		} catch (Exception e) {
			log.error("exception in UpgradeNormStructure processEvent", e);
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in UpgradeNormStructure processEvent", e2);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private boolean checkLegitUpgrade(Builder resBuilder, User user,
			StructureForUser userStruct, Structure currentStruct,
			Structure nextLevelStruct, int gemsSpent, int resourceChange,
			ResourceType rt, Timestamp timeOfUpgrade) {
		if (user == null || userStruct == null
				|| userStruct.getLastRetrieved() == null) {
			log.error(
					"parameter passed in is null. user={}, userStruct={}, userStruct's lastRetrieveTime={}",
					new Object[] { user, userStruct,
							userStruct.getLastRetrieved() });
			return false;
		}
		if (!userStruct.isComplete()) {
			resBuilder.setStatus(ResponseStatus.FAIL_NOT_BUILT_YET);
			log.error("user struct is not complete yet");
			return false;
		}
		if (null == nextLevelStruct) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_AT_MAX_LEVEL_ALREADY);
			log.error("user struct at max level already. struct is {}",
					currentStruct);
			return false;
		}
		if (timeOfUpgrade.getTime() < userStruct.getLastRetrieved().getTime()) {
			resBuilder.setStatus(ResponseStatus.FAIL_NOT_BUILT_YET);
			log.error(
					"the upgrade time {} is before the last time the building was retrieved:{}",
					timeOfUpgrade, userStruct.getLastRetrieved());
			return false;
		}
		//see if the user can upgrade it
		if (!user.getId().equals(userStruct.getUserId())) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_NOT_USERS_STRUCT);
			log.error("user struct belongs to someone else with id={}",
					userStruct.getUserId());
			return false;
		}

		if (gemsSpent < 0) {
			log.warn("gemsSpent is negative! gemsSpent={}", gemsSpent);
			gemsSpent = Math.abs(gemsSpent);
		}
		int userGems = user.getGems();
		if (gemsSpent > 0 && userGems < gemsSpent) {
			log.error(
					"user has {} gems; trying to spend {} and {} {} to upgrade to structure={}",
					new Object[] { userGems, gemsSpent, resourceChange, rt,
							nextLevelStruct });
			resBuilder
					.setStatus(ResponseStatus.FAIL_INSUFFICIENT_GEMS);
			return false;
		}

		//since negative resourceChange means charge, then negative of that is
		//the cost. If resourceChange is positive, meaning refund, user will always
		//have more than a negative amount
		int resourceRequired = -1 * resourceChange;
		if (ResourceType.CASH.equals(rt)) {
			if (user.getCash() < resourceRequired) {
				resBuilder
						.setStatus(ResponseStatus.FAIL_INSUFFICIENT_CASH);
				log.error("user doesn't have enough cash, has {}, needs {}",
						user.getCash(), resourceChange);
				return false;
			}
		} else if (ResourceType.OIL.equals(rt)) {
			if (user.getOil() < resourceRequired) {
				resBuilder
						.setStatus(ResponseStatus.FAIL_INSUFFICIENT_OIL);
				log.error("user doesn't have enough gems, has , needs ",
						user.getGems(), resourceChange);
				return false;
			}
		}

		/*//TODO: only make one user struct retrieve call
		List<StructureForUser> userStructs = RetrieveUtils.userStructRetrieveUtils().getUserStructsForUser(user.getId());
		if (userStructs != null) {
		  for (StructureForUser us : userStructs) {
		    if (!us.isComplete() && us.getUpgradeStartTime() != null) {
		      resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_ANOTHER_STRUCT_STILL_UPGRADING);
		      log.error("another struct is still upgrading: user struct=" + us);
		      return false;
		    }
		  }
		}*/

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private void writeChangesToDB(User user, StructureForUser userStruct,
			Structure upgradedStruct, int gemsSpent, int resourceChange,
			ResourceType rt, Timestamp timeOfUpgrade, Map<String, Integer> money) {

		int newStructId = upgradedStruct.getId();
		//upgrade the user's struct
		if (!UpdateUtils.get().updateBeginUpgradingUserStruct(
				userStruct.getId(), newStructId, timeOfUpgrade)) {
			log.error(String
					.format("problem with changing time of upgrade to %s and marking as incomplete, userStruct=%",
							timeOfUpgrade, userStruct));
		}

		//charge the user
		int cashChange = 0;
		int oilChange = 0;
		int gemChange = -1 * Math.abs(gemsSpent);

		if (ResourceType.CASH.equals(rt)) {
			cashChange = resourceChange;
		} else if (ResourceType.OIL.equals(rt)) {
			oilChange = resourceChange;
		}

		int num = user.updateRelativeCashAndOilAndGems(cashChange, oilChange,
				gemChange, 0);
		if (1 != num) {
			if(userStruct.getStructId() == ControllerConstants.STRUCTURE__LAB_ID) {
				log.info("userid {} is upgrading lab", user.getId());
			}
			else {
				log.error(String
						.format("problem updating user currency. gemChange=%s, cashChange=%s, oilChange=%s, numRowsUpdated=%s",
								gemChange, cashChange, oilChange, num));
			}
		} else {//things went ok
			if (0 != gemChange) {
				money.put(miscMethods.gems, gemChange);
			}
			if (0 != cashChange) {
				money.put(miscMethods.cash, cashChange);
			}
			if (0 != oilChange) {
				money.put(miscMethods.oil, oilChange);
			}
		}

	}

	private void writeToUserCurrencyHistory(User aUser,
			StructureForUser userStruct, Structure curStruct,
			Structure upgradedStruct, Timestamp timeOfUpgrade,
			Map<String, Integer> money, int previousCash, int previousOil,
			int previousGems) {

		String userId = aUser.getId();
		String userStructId = userStruct.getId();
		int prevStructId = curStruct.getId();
		int prevLevel = curStruct.getLevel();
		StringBuilder structDetailsSb = new StringBuilder();
		structDetailsSb.append("uStructId:");
		structDetailsSb.append(userStructId);
		structDetailsSb.append(" preStructId:");
		structDetailsSb.append(prevStructId);
		structDetailsSb.append(" prevLevel:");
		structDetailsSb.append(prevLevel);
		String structDetails = structDetailsSb.toString();

		Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
		String reasonForChange = ControllerConstants.UCHRFC__UPGRADE_NORM_STRUCT;
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> details = new HashMap<String, String>();
		String gems = miscMethods.gems;
		String oil = miscMethods.oil;
		String cash = miscMethods.cash;

		previousCurrencies.put(cash, previousCash);
		previousCurrencies.put(oil, previousOil);
		previousCurrencies.put(gems, previousGems);

		currentCurrencies.put(cash, aUser.getCash());
		currentCurrencies.put(oil, aUser.getOil());
		currentCurrencies.put(gems, aUser.getGems());

		reasonsForChanges.put(cash, reasonForChange);
		reasonsForChanges.put(oil, reasonForChange);
		reasonsForChanges.put(gems, reasonForChange);

		details.put(cash, structDetails);
		details.put(oil, structDetails);
		details.put(gems, structDetails);

		miscMethods.writeToUserCurrencyOneUser(userId, timeOfUpgrade, money,
				previousCurrencies, currentCurrencies, reasonsForChanges,
				details);
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

	public StructureForUserRetrieveUtils2 getUserStructRetrieveUtils() {
		return userStructRetrieveUtils;
	}

	public void setUserStructRetrieveUtils(
			StructureForUserRetrieveUtils2 userStructRetrieveUtils) {
		this.userStructRetrieveUtils = userStructRetrieveUtils;
	}

}
