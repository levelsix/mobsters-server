package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ReconnectRequestEvent;
import com.lvl6.events.response.ReconnectResponseEvent;

import com.lvl6.proto.EventStartupProto.ReconnectRequestProto;
import com.lvl6.proto.EventStartupProto.ReconnectResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.eventsender.ToClientEvents;

@Component

public class ReconnectController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public ReconnectController() {

	}

	@Override
	public RequestEvent createRequestEvent() {
		return new ReconnectRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_RECONNECT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		ReconnectRequestProto reqProto = ((ReconnectRequestEvent) event)
				.getReconnectRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();

		ReconnectResponseProto.Builder resBuilder = ReconnectResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		ReconnectResponseProto resProto = resBuilder.build();
		ReconnectResponseEvent resEvent = new ReconnectResponseEvent(
				senderProto.getUserUuid());
		resEvent.setResponseProto(resProto);
		responses.normalResponseEvents().add(resEvent);
		
		responses.setUserId(senderProto.getUserUuid());
		
		if (senderProto.hasClan() && senderProto.getClan().getClanUuid() != null && senderProto.getClan().getClanUuid().length() > 0) {
			responses.setNewClanId(senderProto.getClan().getClanUuid());
			responses.setClanChanged(true);
		}
	}
}
