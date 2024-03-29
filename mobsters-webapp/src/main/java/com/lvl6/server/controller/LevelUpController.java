package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LevelUpRequestEvent;
import com.lvl6.events.response.LevelUpResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.LevelUpRequestProto;
import com.lvl6.proto.EventUserProto.LevelUpResponseProto;
import com.lvl6.proto.EventUserProto.LevelUpResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;

@Component
public class LevelUpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(LevelUpController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected Locker locker;

	@Autowired
	protected MiscMethods miscMethods;

	public LevelUpController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new LevelUpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_LEVEL_UP_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		LevelUpRequestProto reqProto = ((LevelUpRequestEvent) event)
				.getLevelUpRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int newLevel = reqProto.getNextLevel();

		LevelUpResponseProto.Builder resBuilder = LevelUpResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		UUID userUuid = null;
		boolean invalidUuids = true;
		try {
			userUuid = UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			LevelUpResponseEvent resEvent = new LevelUpResponseEvent(userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			User user = getUserRetrieveUtils().getUserById(userId);
			boolean legitLevelUp = checkLegitLevelUp(resBuilder, user);

			boolean success = false;
			if (legitLevelUp) {
				success = writeChangesToDB(user, newLevel);
			}

			if (success) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			}

			LevelUpResponseEvent resEvent = new LevelUpResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			LevelUpResponseProto resProto = resBuilder.build();
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (success) {
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in LevelUpController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				LevelUpResponseEvent resEvent = new LevelUpResponseEvent(userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error("exception2 in LevelUpController processEvent", e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
	}

	private boolean writeChangesToDB(User user, int newLevel) {
		if (!user.updateLevel(newLevel, true)) {
			log.error("problem in changing the user's level");
			return false;
		}
		return true;
	}

	private boolean checkLegitLevelUp(Builder resBuilder, User user) {
		if (null == user) {
			log.error("user is null");
			return false;
		}
		return true;
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
