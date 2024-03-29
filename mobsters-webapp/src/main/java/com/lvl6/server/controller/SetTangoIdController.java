package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetTangoIdRequestEvent;
import com.lvl6.events.response.SetTangoIdResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetTangoIdRequestProto;
import com.lvl6.proto.EventUserProto.SetTangoIdResponseProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.actionobjects.SetTangoIdAction;
import com.lvl6.server.eventsender.ToClientEvents;

@Component
@DependsOn("gameServer")
public class SetTangoIdController extends EventController {


	private static final Logger log = LoggerFactory.getLogger(SetTangoIdController.class);

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtil;

	@Autowired
	protected MiscMethods miscMethods;

	public SetTangoIdController() {
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SetTangoIdRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SET_TANGO_ID_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses){
		SetTangoIdRequestProto reqProto = ((SetTangoIdRequestEvent) event)
				.getSetTangoIdRequestProto();
		log.info("reqProto={}", reqProto);

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String tangoId = reqProto.getTangoId();
		if (tangoId != null && tangoId.isEmpty())
			tangoId = null;

		SetTangoIdResponseProto.Builder resBuilder = SetTangoIdResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
		if (null != tangoId) {
			resBuilder.setTangoId(tangoId);
		}

		boolean invalidUuids = true;
		try {
			UUID.fromString(userId);

			invalidUuids = false;
		} catch (Exception e) {
			log.error(String.format("UUID error. incorrect userId=%s", userId),
					e);
			invalidUuids = true;
		}

		//UUID checks
		if (invalidUuids) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			SetTangoIdResponseEvent resEvent = new SetTangoIdResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		//    server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		try {
			SetTangoIdAction stia = new SetTangoIdAction(userId, tangoId, userRetrieveUtil);
			stia.execute(resBuilder);

			SetTangoIdResponseProto resProto = resBuilder.build();
			SetTangoIdResponseEvent resEvent = new SetTangoIdResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			if ( ResponseStatus.SUCCESS.equals(resBuilder.getStatus()) ) {
				//game center id might have changed
				//null PvpLeagueFromUser means will pull from hazelcast instead
				UpdateClientUserResponseEvent resEventUpdate = miscMethods
						.createUpdateClientUserResponseEventAndUpdateLeaderboard(
								stia.getUser(), null, null);
				resEventUpdate.setTag(event.getTag());
				responses.normalResponseEvents().add(resEventUpdate);
			}

		} catch (Exception e) {
			log.error("exception in SetTangoIdController processEvent", e);
			//don't let the client hang
			try {
				resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
				SetTangoIdResponseEvent resEvent = new SetTangoIdResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SetTangoIdController processEvent",
						e);
			}
		} finally {
			//      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}
}
