package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;

public class InviteFbFriendsForSlotsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private InviteFbFriendsForSlotsRequestProto inviteFbFriendsForSlotsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			inviteFbFriendsForSlotsRequestProto = InviteFbFriendsForSlotsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = inviteFbFriendsForSlotsRequestProto.getSender()
					.getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("InviteFbFriendsForSlotsRequest exception", e);
		}
	}

	public InviteFbFriendsForSlotsRequestProto getInviteFbFriendsForSlotsRequestProto() {
		return inviteFbFriendsForSlotsRequestProto;
	}

	@Override
	public String toString() {
		return "InviteFbFriendsForSlotsRequestEvent [inviteFbFriendsForSlotsRequestProto="
				+ inviteFbFriendsForSlotsRequestProto + "]";
	}

}
