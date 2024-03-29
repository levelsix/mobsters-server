package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetAvatarMonsterRequestEvent;
import com.lvl6.events.response.SetAvatarMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterRequestProto;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.eventsender.ToClientEvents;

@Component

public class SetAvatarMonsterController extends EventController {

	private static Logger log = LoggerFactory.getLogger(SetAvatarMonsterController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;
	
	@Autowired
	protected MiscMethods miscMethods;

	public SetAvatarMonsterController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SetAvatarMonsterRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SET_AVATAR_MONSTER_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SetAvatarMonsterRequestProto reqProto = ((SetAvatarMonsterRequestEvent) event)
				.getSetAvatarMonsterRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		int monsterId = reqProto.getMonsterId();

		SetAvatarMonsterResponseProto.Builder resBuilder = SetAvatarMonsterResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

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
			SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(
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

			//      boolean isDifferent = checkIfNewTokenDifferent(user.getAvatarMonster(), gameCenterId);

			boolean legit = monsterId > 0;

			boolean successful = false;
			if (legit) {
				successful = writeChangesToDb(user, monsterId);
			} else {
				log.error("can't unset avatarMonsterId");
			}

			if (successful) {
				resBuilder.setStatus(ResponseStatus.SUCCESS);
			} else {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			}

			SetAvatarMonsterResponseProto resProto = resBuilder.build();
			SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if (successful) {
				//game center id might have changed
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								user, null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in SetAvatarMonsterController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SetAvatarMonsterController processEvent",
						e);
			}
		} finally {
			//      locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName()); 
		}
	}

	private boolean writeChangesToDb(User user, int avatarMonsterId) {
		try {
			if (!user.updateAvatarMonsterId(avatarMonsterId)) {
				log.error("problem with setting user's avatarMonsterId to "
						+ avatarMonsterId);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("problem with updating user avatar monster id. user="
					+ user + "\t avatarMonsterId=" + avatarMonsterId);
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
