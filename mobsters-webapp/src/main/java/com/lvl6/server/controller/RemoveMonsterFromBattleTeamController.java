package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RemoveMonsterFromBattleTeamRequestEvent;
import com.lvl6.events.response.RemoveMonsterFromBattleTeamResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamRequestProto;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class RemoveMonsterFromBattleTeamController extends EventController {

	private static Logger log = LoggerFactory.getLogger(RemoveMonsterFromBattleTeamController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	public RemoveMonsterFromBattleTeamController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new RemoveMonsterFromBattleTeamRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		RemoveMonsterFromBattleTeamRequestProto reqProto = ((RemoveMonsterFromBattleTeamRequestEvent) event)
				.getRemoveMonsterFromBattleTeamRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String userMonsterId = reqProto.getUserMonsterUuid();

		//set some values to send to the client (the response proto)
		RemoveMonsterFromBattleTeamResponseProto.Builder resBuilder = RemoveMonsterFromBattleTeamResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		UUID userMonsterUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);
			userMonsterUuid = UUID.fromString(userMonsterId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMonsterId=%s",
					userId, userMonsterId), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			RemoveMonsterFromBattleTeamResponseEvent resEvent = new RemoveMonsterFromBattleTeamResponseEvent(
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
			//User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);

			//make sure it exists
			MonsterForUser mfu = getMonsterForUserRetrieveUtils()
					.getSpecificUserMonster(userMonsterId);

			boolean legit = checkLegit(resBuilder, userId, userMonsterId, mfu);

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(userId, userMonsterId, mfu);
			}

			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			RemoveMonsterFromBattleTeamResponseEvent resEvent = new RemoveMonsterFromBattleTeamResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder
					.build());
			responses.normalResponseEvents().add(resEvent);
			//
			//      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
			//          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
			//      resEventUpdate.setTag(event.getTag());
			//      responses.normalResponseEvents().add(resEventUpdate);
		} catch (Exception e) {
			log.error(
					"exception in RemoveMonsterFromBattleTeamController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder
						.setStatus(ResponseStatus.FAIL_OTHER);
				RemoveMonsterFromBattleTeamResponseEvent resEvent = new RemoveMonsterFromBattleTeamResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in RemoveMonsterFromBattleTeamController processEvent",
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
	private boolean checkLegit(Builder resBuilder, String userId,
			String userMonsterId, MonsterForUser mfu) {

		if (null == mfu) {
			log.error("no monster_for_user exists with id=" + userMonsterId);
			return false;
		}

		//check to make sure this is indeed the user's monster
		String mfuUserId = mfu.getUserId();
		if (!mfuUserId.equals(userId)) {
			log.error("what is this I don't even...client trying to \"unequip\" "
					+ "another user's monster. userId="
					+ userId
					+ "\t monsterForUser=" + mfu);
			return false;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(String uId, String userMonsterId,
			MonsterForUser mfu) {
		//"unequip" monster
		int teamSlotNum = 0;

		int numUpdated = UpdateUtils.get().updateUserMonsterTeamSlotNum(
				userMonsterId, teamSlotNum);

		if (numUpdated == 1) {
			return true;
		}
		log.warn("unexpected error: user monster not updated. "
				+ "actual numUpdated=" + numUpdated + "expected: 1 "
				+ "monsterForUser=" + mfu);
		return true;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

}
