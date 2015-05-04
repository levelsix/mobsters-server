package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.EnableAPNSRequestEvent;
import com.lvl6.events.response.EnableAPNSResponseEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventApnsProto.EnableAPNSRequestProto;
import com.lvl6.proto.EventApnsProto.EnableAPNSResponseProto;
import com.lvl6.proto.EventApnsProto.EnableAPNSResponseProto.EnableAPNSStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.RetrieveUtils;

@Component

public class EnableAPNSController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public EnableAPNSController() {
		
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new EnableAPNSRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_ENABLE_APNS_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		EnableAPNSRequestProto reqProto = ((EnableAPNSRequestEvent) event)
				.getEnableAPNSRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		String deviceToken = reqProto.getDeviceToken();
		if (deviceToken != null && deviceToken.length() == 0)
			deviceToken = null;

		EnableAPNSResponseProto.Builder resBuilder = EnableAPNSResponseProto
				.newBuilder();
		resBuilder.setSender(senderProto);
		//    locker.lockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName());
		try {
			User user = RetrieveUtils.userRetrieveUtils().getUserById(
					senderProto.getUserUuid());

			if (deviceToken != null && user != null) {
				resBuilder.setStatus(EnableAPNSStatus.SUCCESS);
			} else {
				resBuilder.setStatus(EnableAPNSStatus.NOT_ENABLED);
			}

			EnableAPNSResponseProto resProto = resBuilder.build();
			EnableAPNSResponseEvent resEvent = new EnableAPNSResponseEvent(
					senderProto.getUserUuid());
			resEvent.setResponseProto(resProto);
			responses.normalResponseEvents().add(resEvent);

			boolean isDifferent = checkIfNewTokenDifferent(
					user.getDeviceToken(), deviceToken);

			if (isDifferent) {
				if (!user.updateSetdevicetoken(deviceToken)) {
					log.error("problem with setting user's device token to "
							+ deviceToken);
				}
			}
		} catch (Exception e) {
			log.error("exception in EnableAPNSController processEvent", e);
		} finally {
			//      locker.unlockPlayer(UUID.fromString(senderProto.getUserUuid()), this.getClass().getSimpleName()); 
		}
	}

	private boolean checkIfNewTokenDifferent(String oldToken, String newToken) {
		boolean oldTokenIsNothing = oldToken == null || oldToken.length() == 0;
		boolean newTokenIsNothing = newToken == null || newToken.length() == 0;

		if (oldTokenIsNothing && newTokenIsNothing) {
			return false;
		}

		if (!oldTokenIsNothing && !newTokenIsNothing) {
			return !oldToken.equals(newToken);
		}

		return true;
	}

}
