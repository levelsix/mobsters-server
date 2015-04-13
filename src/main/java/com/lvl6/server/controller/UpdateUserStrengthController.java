
package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpdateUserStrengthRequestEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.events.response.UpdateUserStrengthResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthRequestProto;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto.UpdateUserStrengthStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.UpdateUserStrengthAction;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component
@DependsOn("gameServer")
public class UpdateUserStrengthController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	@Autowired
	protected Locker locker;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Autowired
	protected UpdateUtil updateUtil;

	public UpdateUserStrengthController() {
		numAllocatedThreads = 4;
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
	protected void processRequestEvent(RequestEvent event) throws Exception {
		UpdateUserStrengthRequestProto reqProto = ((UpdateUserStrengthRequestEvent) event)
				.getUpdateUserStrengthRequestProto();

		//get values sent from the client (the request proto)
		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();

		//all positive numbers, server will change to negative
		long updatedStrength = reqProto.getUpdatedStrength();

		//set some values to send to the client (the response proto)
		UpdateUserStrengthResponseProto.Builder resBuilder = UpdateUserStrengthResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(UpdateUserStrengthStatus.FAIL_OTHER); //default

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
			resBuilder.setStatus(UpdateUserStrengthStatus.FAIL_OTHER);
			UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setUpdateUserStrengthResponseProto(resBuilder.build());
			server.writeEvent(resEvent);
			return;
		}

		getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
		try {
			UpdateUserStrengthAction uusa = new UpdateUserStrengthAction(userId, updatedStrength, userRetrieveUtils, updateUtil);

			uusa.execute(resBuilder);

			UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setUpdateUserStrengthResponseProto(resBuilder.build());
			server.writeEvent(resEvent);

			if (UpdateUserStrengthStatus.SUCCESS.equals(resBuilder.getStatus())) {

				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								uusa.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

			}

		} catch (Exception e) {
			log.error("exception in UpdateUserStrengthController processEvent",
					e);
			//don't let the client hang
			try {
				resBuilder.setStatus(UpdateUserStrengthStatus.FAIL_OTHER);
				UpdateUserStrengthResponseEvent resEvent = new UpdateUserStrengthResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setUpdateUserStrengthResponseProto(resBuilder.build());
				server.writeEvent(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in UpdateUserStrengthController processEvent",
						e);
			}
		} finally {
			getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());
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
