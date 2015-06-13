package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStartupProto.ReconnectRequestProto;

public class ReconnectRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private ReconnectRequestProto reconnectRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			reconnectRequestProto = ReconnectRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = reconnectRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ReconnectRequest exception", e);
		}
	}

	public ReconnectRequestProto getReconnectRequestProto() {
		return reconnectRequestProto;
	}

	public void setReconnectRequestProto(ReconnectRequestProto reconnectRequestProto) {
		this.reconnectRequestProto = reconnectRequestProto;
	}

	@Override
	public String toString() {
		return "ReconnectRequestEvent [reconnectRequestProto="
				+ reconnectRequestProto + "]";
	}

}//ReconnectRequestProto
