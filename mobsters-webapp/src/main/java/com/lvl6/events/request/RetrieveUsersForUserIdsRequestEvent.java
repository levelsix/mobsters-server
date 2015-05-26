package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsRequestProto;

public class RetrieveUsersForUserIdsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RetrieveUsersForUserIdsRequestProto retrieveUsersForUserIdsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveUsersForUserIdsRequestProto = RetrieveUsersForUserIdsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveUsersForUserIdsRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveUsersForUserIdsRequest exception", e);
		}
	}

	public RetrieveUsersForUserIdsRequestProto getRetrieveUsersForUserIdsRequestProto() {
		return retrieveUsersForUserIdsRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveUsersForUserIdsRequestEvent [retrieveUsersForUserIdsRequestProto="
				+ retrieveUsersForUserIdsRequestProto + "]";
	}

}
