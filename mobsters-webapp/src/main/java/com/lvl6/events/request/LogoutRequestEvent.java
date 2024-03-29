package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.LogoutRequestProto;

public class LogoutRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(LogoutRequestEvent.class);

	private LogoutRequestProto logoutRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			logoutRequestProto = LogoutRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = logoutRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("LogoutRequest exception", e);
		}
	}

	public LogoutRequestProto getLogoutRequestProto() {
		return logoutRequestProto;
	}

	@Override
	public String toString() {
		return "LogoutRequestEvent [logoutRequestProto=" + logoutRequestProto
				+ "]";
	}

}
