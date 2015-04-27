package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.DeleteClanGiftsRequestProto;

public class DeleteClanGiftsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private DeleteClanGiftsRequestProto deleteClanGiftsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			deleteClanGiftsRequestProto = DeleteClanGiftsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = deleteClanGiftsRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("DeleteClanGiftsRequest exception");
		}
	}

	public DeleteClanGiftsRequestProto getDeleteClanGiftsRequestProto() {
		return deleteClanGiftsRequestProto;
	}

	public void setDeleteClanGiftsRequestProto(DeleteClanGiftsRequestProto ccsrp) {
		this.deleteClanGiftsRequestProto = ccsrp;
	}

	@Override
	public String toString() {
		return "DeleteClanGiftsRequestEvent [deleteClanGiftsRequestProto="
				+ deleteClanGiftsRequestProto + "]";
	}
}
