package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventRewardProto.DeleteGiftRequestProto;

public class DeleteGiftRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(DeleteGiftRequestEvent.class);

	private DeleteGiftRequestProto deleteGiftRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			deleteGiftRequestProto = DeleteGiftRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = deleteGiftRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("DeleteGiftRequest exception");
		}
	}

	public DeleteGiftRequestProto getDeleteGiftRequestProto() {
		return deleteGiftRequestProto;
	}

	public void setDeleteGiftRequestProto(DeleteGiftRequestProto ccsrp) {
		this.deleteGiftRequestProto = ccsrp;
	}

	@Override
	public String toString() {
		return "DeleteGiftRequestEvent [deleteGiftRequestProto="
				+ deleteGiftRequestProto + "]";
	}
}
