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
import com.lvl6.events.request.ReviveInDungeonRequestEvent;
import com.lvl6.events.response.AchievementProgressResponseEvent;
import com.lvl6.events.response.ReviveInDungeonResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonRequestProto;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto.Builder;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class ReviveInDungeonController extends EventController {

	private static Logger log = LoggerFactory.getLogger(ReviveInDungeonController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;
	
	@Autowired
	protected TimeUtils timeUtils;

	public ReviveInDungeonController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ReviveInDungeonRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REVIVE_IN_DUNGEON_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		ReviveInDungeonRequestProto reqProto = ((ReviveInDungeonRequestEvent) event)
				.getReviveInDungeonRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String userTaskId = reqProto.getUserTaskUuid();
		Timestamp curTime = new Timestamp(reqProto.getClientTime());
		List<UserMonsterCurrentHealthProto> reviveMeProtoList = reqProto
				.getReviveMeList();
		//positive value, need to convert to negative when updating user
		int gemsSpent = reqProto.getGemsSpent();

		//set some values to send to the client (the response proto)
		ReviveInDungeonResponseProto.Builder resBuilder = ReviveInDungeonResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		UUID userTaskUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			userTaskUuid = UUID.fromString(userTaskId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userTaskId=%s", userId,
					userTaskId), e);
			invalidUuids = true;
		}
		
		if(reqProto.getClientTime() == 0) {
			resBuilder.setStatus(ResponseStatus.FAIL_CLIENT_TIME_NOT_SENT);
			log.error("clientTime not sent");
			ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		if(timeUtils.numMinutesDifference(new Date(reqProto.getClientTime()), new Date()) > 
		ControllerConstants.CLIENT_TIME_MINUTES_CONSTANT_CHECK) {
			resBuilder.setStatus(ResponseStatus.FAIL_TIME_OUT_OF_SYNC);
			log.error("time is out of sync > 2 hrs for userId {}", senderProto.getUserUuid());
			ReviveInDungeonResponseEvent resEvent = 
					new ReviveInDungeonResponseEvent(senderProto.getUserUuid());
			resEvent.setResponseProto(resBuilder.build());
			resEvent.setTag(event.getTag());
			responses.normalResponseEvents().add(resEvent);
			return;
		}
		
		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());
			User aUser = getUserRetrieveUtils().getUserById(userId);
			int previousGems = 0;

			//will be populated by checkLegit(...);
			Map<String, Integer> userMonsterIdToExpectedHealth = new HashMap<String, Integer>();

			//      List<TaskForUserOngoing> userTaskList = new ArrayList<TaskForUserOngoing>();
			boolean legit = checkLegit(resBuilder, aUser, userId, gemsSpent,
					userTaskId, reviveMeProtoList,
					userMonsterIdToExpectedHealth);//, userTaskList);

			Map<String, Integer> currencyChange = new HashMap<String, Integer>();
			boolean successful = false;
			if (legit) {
				//    	  TaskForUserOngoing ut = userTaskList.get(0);
				previousGems = aUser.getGems();
				successful = writeChangesToDb(aUser, userId, userTaskId,
						gemsSpent, curTime, userMonsterIdToExpectedHealth,
						currencyChange);
			}
			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								aUser, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
				writeToUserCurrencyHistory(userId, aUser, userTaskId, curTime,
						previousGems, currencyChange);
			}
		} catch (Exception e) {
			log.error("exception in ReviveInDungeonController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				ReviveInDungeonResponseEvent resEvent = new ReviveInDungeonResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in ReviveInDungeonController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 */
	private boolean checkLegit(Builder resBuilder, User u, String userId,
			int gemsSpent, String userTaskId,
			List<UserMonsterCurrentHealthProto> reviveMeProtoList,
			Map<String, Integer> userMonsterIdToExpectedHealth) {//, List<TaskForUserOngoing> userTaskList) {
		if (null == u) {
			log.error("unexpected error: user is null. user=" + u);
			return false;
		}
		//
		//    //make sure user task exists
		//    TaskForUserOngoing ut = TaskForUserOngoingRetrieveUtils.getUserTaskForId(userTaskId);
		//    if (null == ut) {
		//    	log.error("unexpected error: no user task for id userTaskId=" + userTaskId);
		//    	return false;
		//    }

		//extract the ids so it's easier to get userMonsters from db
		List<String> userMonsterIds = monsterStuffUtils.getUserMonsterIds(
				reviveMeProtoList, userMonsterIdToExpectedHealth);
		Map<String, MonsterForUser> userMonsters = getMonsterForUserRetrieveUtils()
				.getSpecificOrAllUserMonstersForUser(userId, userMonsterIds);

		if (null == userMonsters || userMonsters.isEmpty()) {
			log.error("unexpected error: userMonsterIds don't exist. ids="
					+ userMonsterIds);
			return false;
		}

		//see if the user has the equips
		if (userMonsters.size() != reviveMeProtoList.size()) {
			log.error("unexpected error: mismatch between user equips client sent and "
					+ "what is in the db. clientUserMonsterIds="
					+ userMonsterIds
					+ "\t inDb="
					+ userMonsters
					+ "\t continuing the processing");
		}

		//make sure user has enough diamonds/gold?
		int userDiamonds = u.getGems();
		int cost = gemsSpent;
		if (cost > userDiamonds) {
			log.error("user error: user does not have enough diamonds to revive. "
					+ "cost=" + cost + "\t userDiamonds=" + userDiamonds);
			resBuilder.setStatus(ResponseStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}

		//    userTaskList.add(ut);
		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(User u, String uId, String userTaskId,
			int gemsSpent, Timestamp clientTime,
			Map<String, Integer> userMonsterIdToExpectedHealth,
			Map<String, Integer> currencyChange) {

		//update user diamonds
		int gemsChange = -1 * gemsSpent;
		if (!updateUser(u, gemsChange)) {
			log.error("unexpected error: could not decrement user's gold by "
					+ gemsChange);
			return false;
		} else {
			if (0 != gemsChange) {
				currencyChange.put(miscMethods.gems, gemsSpent);
			}
		}

		//update num revives for user task
		int numRevivesDelta = 1;
		int numUpdated = UpdateUtils.get().incrementUserTaskNumRevives(
				userTaskId, numRevivesDelta);
		if (1 != numUpdated) {
			log.error("unexpected error: user_task not updated correctly. Attempting "
					+ "to give back diamonds. userTaskId="
					+ userTaskId
					+ "\t numUpdated=" + numUpdated + "\t user=" + u);
			//undo gold charge
			if (!updateUser(u, -1 * gemsChange)) {
				log.error("unexpected error: could not change back user's gems by "
						+ -1 * gemsChange);
			}
			currencyChange.clear();
			return false;
		}

		//HEAL THE USER MONSTERS
		//replace existing health for these user monsters with new values
		numUpdated = UpdateUtils.get().updateUserMonstersHealth(
				userMonsterIdToExpectedHealth);

		if (numUpdated >= userMonsterIdToExpectedHealth.size()) {
			return true;
		}
		log.warn("unexpected error: not all user equips were updated. "
				+ "actual numUpdated=" + numUpdated + "expected: "
				+ "userMonsterIdToExpectedHealth="
				+ userMonsterIdToExpectedHealth);

		return true;
	}

	private boolean updateUser(User u, int diamondChange) {
		if (!u.updateRelativeGemsNaive(diamondChange, 0)) {
			log.error("unexpected error: problem with updating user diamonds for reviving. diamondChange="
					+ diamondChange + "user=" + u);
			return false;
		}
		return true;
	}

	private void writeToUserCurrencyHistory(String userId, User aUser,
			String userTaskId, Timestamp curTime, int previousGems,
			Map<String, Integer> currencyChange) {

		if (currencyChange.isEmpty()) {
			return;
		}

		StringBuilder detailsSb = new StringBuilder();
		detailsSb.append("userTaskId=");
		detailsSb.append(userTaskId);

		Map<String, Integer> previousCurrency = new HashMap<String, Integer>();
		Map<String, Integer> currentCurrency = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		Map<String, String> detailsMap = new HashMap<String, String>();
		String reason = ControllerConstants.UCHRFC__REVIVE_IN_DUNGEON;
		String gems = miscMethods.gems;

		previousCurrency.put(gems, previousGems);
		currentCurrency.put(gems, aUser.getGems());
		reasonsForChanges.put(gems, reason);
		detailsMap.put(gems, detailsSb.toString());

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

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

}
