package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetGameCenterIdRequestEvent;
import com.lvl6.events.response.SetGameCenterIdResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetGameCenterIdRequestProto;
import com.lvl6.proto.EventUserProto.SetGameCenterIdResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.eventsender.ToClientEvents;

@Component

public class SetGameCenterIdController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SetGameCenterIdController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected MiscMethods miscMethods;

	public SetGameCenterIdController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SetGameCenterIdRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SET_GAME_CENTER_ID_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SetGameCenterIdRequestProto reqProto = ((SetGameCenterIdRequestEvent) event)
				.getSetGameCenterIdRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String gameCenterId = reqProto.getGameCenterId();
		if (gameCenterId != null && gameCenterId.isEmpty())
			gameCenterId = null;

		SetGameCenterIdResponseProto.Builder resBuilder = SetGameCenterIdResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		if (null != gameCenterId) {
			resBuilder.setGameCenterId(gameCenterId);
		}

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
			SetGameCenterIdResponseEvent resEvent = new SetGameCenterIdResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//    locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
		try {
			User user = getUserRetrieveUtils().getUserById(
					senderProto.getUserUuid());

			//      boolean isDifferent = checkIfNewTokenDifferent(user.getGameCenterId(), gameCenterId);
			boolean legit = writeChangesToDb(user, gameCenterId);

			if (legit) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			} else {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			}

			SetGameCenterIdResponseProto resProto = resBuilder.build();
			SetGameCenterIdResponseEvent resEvent = new SetGameCenterIdResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (legit) {
				//game center id might have changed
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in SetGameCenterIdController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SetGameCenterIdResponseEvent resEvent = new SetGameCenterIdResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SetGameCenterIdController processEvent",
						e);
			}
		} finally {
			//      locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName()); 
		}
	}

	private boolean writeChangesToDb(User user, String gameCenterId) {
		try {
			if (!user.updateGameCenterId(gameCenterId)) {
				log.error("problem with setting user's facebook id to "
						+ gameCenterId);
			}
			return true;
		} catch (Exception e) {
			log.error("problem with updating user game center id. user=" + user
					+ "\t gameCenterId=" + gameCenterId);
		}

		return false;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
}
