package com.lvl6.server.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UnrestrictUserMonsterRequestEvent;
import com.lvl6.events.response.UnrestrictUserMonsterResponseEvent;
import com.lvl6.info.MonsterForUser;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterRequestProto;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto.UnrestrictUserMonsterStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component

public class UnrestrictUserMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(UnrestrictUserMonsterController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;

	public UnrestrictUserMonsterController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new UnrestrictUserMonsterRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UNRESTRICT_USER_MONSTER_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UnrestrictUserMonsterRequestProto reqProto = ((UnrestrictUserMonsterRequestEvent) event)
				.getUnrestrictUserMonsterRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		List<String> userMonsterIdList = reqProto.getUserMonsterUuidsList();

		//set some values to send to the client (the response proto)
		UnrestrictUserMonsterResponseProto.Builder resBuilder = UnrestrictUserMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(UnrestrictUserMonsterStatus.FAIL_OTHER); //default

		UUID userUuid = null;
		UUID userMonsterUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			if (userMonsterIdList != null) {
				for (String userMonsterId : userMonsterIdList) {
					userMonsterUuid = UUID.fromString(userMonsterId);
				}
			}

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format(
					"UUID error. incorrect userId=%s, userMonsterIdList=%s",
					userId, userMonsterIdList), e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(UnrestrictUserMonsterStatus.FAIL_OTHER);
			UnrestrictUserMonsterResponseEvent resEvent = new UnrestrictUserMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//    getLocker().lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			//User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);

			//make sure it exists
			Map<String, MonsterForUser> mfuMap = getMonsterForUserRetrieveUtils()
					.getSpecificOrAllRestrictedUserMonstersForUser(userId,
							userMonsterIdList);

			boolean legit = checkLegit(resBuilder, userId, userMonsterIdList,
					mfuMap);

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(userId, userMonsterIdList);
			}

			if (successful) {
				resBuilder.setStatus(UnrestrictUserMonsterStatus.SUCCESS);
			}

			UnrestrictUserMonsterResponseEvent resEvent = new UnrestrictUserMonsterResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			//
			//      UpdateClientUserResponseEvent resEventUpdate = MiscMethods
			//          .createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
			//      resEventUpdate.setTag(event.getTag());
			//      responses.normalResponseEvents().add(resEventUpdate);
		} catch (Exception e) {
			log.error(
					"exception in UnrestrictUserMonsterController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(UnrestrictUserMonsterStatus.FAIL_OTHER);
				UnrestrictUserMonsterResponseEvent resEvent = new UnrestrictUserMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder
						.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UnrestrictUserMonsterController processEvent",
						e);
			}
		} finally {
			//      getLocker().unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	/*
	 * Return true if user request is valid; false otherwise and set the
	 * builder status to the appropriate value.
	 */
	private boolean checkLegit(Builder resBuilder, String userId,
			List<String> userMonsterIdList, Map<String, MonsterForUser> mfuMap) {

		if (null == mfuMap || mfuMap.isEmpty()) {
			log.error(String.format(
					"no restricted monsters_for_user exist with ids=%s",
					userMonsterIdList));
			return false;
		}

		resBuilder.setStatus(UnrestrictUserMonsterStatus.SUCCESS);
		return true;
	}

	private boolean writeChangesToDb(String uId, List<String> userMonsterIdList) {

		int numUpdated = UpdateUtils.get().updateUnrestrictUserMonsters(uId,
				userMonsterIdList);

		if (numUpdated == 0) {
			log.warn(String
					.format("user monsters not updated. actual numUpdated=%d, expected: >0, userMonsterIdList=%s",
							numUpdated, userMonsterIdList));
		}
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
