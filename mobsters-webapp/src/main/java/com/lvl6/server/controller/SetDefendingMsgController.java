package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetDefendingMsgRequestEvent;
import com.lvl6.events.response.SetDefendingMsgResponseEvent;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgRequestProto;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto.SetDefendingMsgStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.SetDefendingMsgAction;
import com.lvl6.server.eventsender.ToClientEvents;

@Component

public class SetDefendingMsgController extends EventController {

	private static final Logger log = LoggerFactory
			.getLogger(SetDefendingMsgController.class);

	public SetDefendingMsgController() {

	}

	@Autowired
	protected MiscMethods miscMethods;

	@Autowired
	protected Locker locker;

	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;

	@Override
	public RequestEvent createRequestEvent() {
		return new SetDefendingMsgRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SET_DEFENDING_MSG_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		SetDefendingMsgRequestProto reqProto = ((SetDefendingMsgRequestEvent) event)
				.getSetDefendingMsgRequestProto();

		log.info(String.format("reqProto=%s", reqProto));

		MinimumUserProto senderProto = reqProto.getSender();
		String userId = senderProto.getUserUuid();
		String msg = reqProto.getMsg();

		SetDefendingMsgResponseProto.Builder resBuilder = SetDefendingMsgResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		resBuilder.setStatus(SetDefendingMsgStatus.FAIL_OTHER);

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
			log.info("invalid UUIDS.");
			resBuilder.setStatus(SetDefendingMsgStatus.FAIL_OTHER);
			SetDefendingMsgResponseEvent resEvent = new SetDefendingMsgResponseEvent(
					userId);
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resBuilder.build());
			responses.normalResponseEvents().add(resEvent);
			return;
		}

		locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass()
				.getSimpleName());
		try {
			//
			SetDefendingMsgAction rsga = new SetDefendingMsgAction(userId, msg,
					userRetrieveUtil, miscMethods);

			rsga.execute(resBuilder);

			SetDefendingMsgResponseProto resProto = resBuilder.build();
			SetDefendingMsgResponseEvent resEvent = new SetDefendingMsgResponseEvent(
					senderProto.getUserUuid());
			resEvent.setTag(event.getTag());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

		} catch (Exception e) {
			log.error("exception in SetDefendingMsgController processEvent", e);
			try {
				resBuilder.setStatus(SetDefendingMsgStatus.FAIL_OTHER);
				SetDefendingMsgResponseEvent resEvent = new SetDefendingMsgResponseEvent(
						userId);
				resEvent.setTag(event.getTag());
				resEvent.setResponseProto(resBuilder.build());
				responses.normalResponseEvents().add(resEvent);
			} catch (Exception e2) {
				log.error(
						"exception2 in SetDefendingMsgController processEvent",
						e);
			}

		} finally {
			locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass()
					.getSimpleName());
		}
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}

}
