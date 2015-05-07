package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanRequestProto;

public class RetractRequestJoinClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RetractRequestJoinClanRequestProto retractRequestJoinClanRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retractRequestJoinClanRequestProto = RetractRequestJoinClanRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retractRequestJoinClanRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetractRequestJoinClanRequest exception", e);
		}
	}

	public RetractRequestJoinClanRequestProto getRetractRequestJoinClanRequestProto() {
		return retractRequestJoinClanRequestProto;
	}
	
	public void setRetractRequestJoinClanRequestProto(RetractRequestJoinClanRequestProto rrjcrp) {
		this.retractRequestJoinClanRequestProto = rrjcrp;
	}

	@Override
	public String toString() {
		return "RetractRequestJoinClanRequestEvent [retractRequestJoinClanRequestProto="
				+ retractRequestJoinClanRequestProto + "]";
	}

}
