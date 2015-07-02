
package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateUserStrengthRequestEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.events.response.UpdateUserStrengthResponseEvent;
import com.lvl6.leaderboards.LeaderBoardImpl;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthRequestProto;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.UpdateUserStrengthAction;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component

public class UpdateUserStrengthController extends EventController {

	private static Logger log = LoggerFactory.getLogger(UpdateUserStrengthController.class);

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	@Autowired
	protected LeaderBoardImpl leaderBoardImpl;

//	@Autowired
//	protected LeaderBoardImpl leaderBoardImpl;

	public UpdateUserStrengthController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new UpdateUserStrengthRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_UPDATE_USER_STRENGTH_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		UpdateUserStrengthRequestProto reqProto = ((UpdateUserStrengthRequestEvent) event)
				.getUpdateUserStrengthRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();

		//all positive numbers, server will change to negative
		long updatedStrength = reqProto.getUpdatedStrength();
		int highestToonAtk = reqProto.getHighestToonAtk();
		int highestToonHp = reqProto.getHighestToonHp();

		//set some values to send to the client (the response proto)
		UpdateUserStrengthResponseProto.Builder resBuilder = UpdateUserStrengthResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER); //default

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
			UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		boolean gotLock = false;
		try {
			gotLock = locker.lockPlayer(userUuid, this.getClass().getSimpleName());

			UpdateUserStrengthAction uusa = new UpdateUserStrengthAction(userId, updatedStrength,
					highestToonAtk, highestToonHp, userRetrieveUtils, updateUtil);

			uusa.execute(resBuilder);

			UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);

			if (ResponseStatus.SUCCESS.equals(resBuilder.getStatus())) {

				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods()
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								uusa.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in UpdateUserStrengthController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateUserStrengthController processEvent",
						e);
			}
		} finally {
			if (gotLock) {
				locker.unlockPlayer(userUuid, this.getClass().getSimpleName());
			}
		}
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
