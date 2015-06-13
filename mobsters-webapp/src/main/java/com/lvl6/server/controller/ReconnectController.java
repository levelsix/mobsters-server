package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ReconnectRequestEvent;
import com.lvl6.events.response.ForceLogoutResponseEvent;
import com.lvl6.events.response.ReconnectResponseEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventStartupProto.ForceLogoutResponseProto;
import com.lvl6.proto.EventStartupProto.ReconnectRequestProto;
import com.lvl6.proto.EventStartupProto.ReconnectResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.eventsender.PreDBFacebookEvent;
import com.lvl6.server.eventsender.PreDBResponseEvent;
import com.lvl6.server.eventsender.ToClientEvents;

@Component

public class ReconnectController extends EventController {

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

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
		String udid = reqProto.getUdid();

		User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());
		String fbId = user.getFacebookId();
		
		if (user != null) {
			forceLogoutOthers(udid, senderProto.getUserUuid(), user, fbId, responses);

			responses.setUserId(senderProto.getUserUuid());

			if (senderProto.hasClan() && senderProto.getClan().getClanUuid() != null && senderProto.getClan().getClanUuid().length() > 0) {
				responses.setNewClanId(senderProto.getClan().getClanUuid());
				responses.setClanChanged(true);
			}
		} else {
			log.error("reconnect called with no user.");
		}

		ReconnectResponseProto.Builder resBuilder = ReconnectResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);

		ReconnectResponseProto resProto = resBuilder.build();
		ReconnectResponseEvent resEvent = new ReconnectResponseEvent(
				senderProto.getUserUuid());
		resEvent.setResponseProto(resProto);
		responses.normalResponseEvents().add(resEvent);
	}

	public void forceLogoutOthers(String udid, String playerId, User u, String fbId, ToClientEvents responses) {
		ForceLogoutResponseProto logoutResponse = ForceLogoutResponseProto.newBuilder().setUdid(udid).setPreviousLoginTime(u.getLastLogin().getTime()).build();
		ForceLogoutResponseEvent event = new ForceLogoutResponseEvent(playerId);
		event.setResponseProto(logoutResponse);
		
		responses.preDBResponseEvents().add(new PreDBResponseEvent(event, udid));
		responses.normalResponseEvents().add(event);
		
		if (fbId != null && !fbId.isEmpty()) {
			responses.preDBFacebookEvents().add(new PreDBFacebookEvent(fbId, event));
		}
	}
	


	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
}
