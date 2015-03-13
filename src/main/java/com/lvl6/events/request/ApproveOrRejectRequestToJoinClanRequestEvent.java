package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;

public class ApproveOrRejectRequestToJoinClanRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private ApproveOrRejectRequestToJoinClanRequestProto approveOrRejectRequestToJoinClanRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			approveOrRejectRequestToJoinClanRequestProto = ApproveOrRejectRequestToJoinClanRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = approveOrRejectRequestToJoinClanRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ApproveOrRejectRequestToJoinClanRequest exception", e);
		}
	}

	public ApproveOrRejectRequestToJoinClanRequestProto getApproveOrRejectRequestToJoinClanRequestProto() {
		return approveOrRejectRequestToJoinClanRequestProto;
	}

	@Override
	public String toString() {
		return "ApproveOrRejectRequestToJoinClanRequestEvent [approveOrRejectRequestToJoinClanRequestProto="
				+ approveOrRejectRequestToJoinClanRequestProto + "]";
	}

}
