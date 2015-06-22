package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.TransferClanOwnershipRequestProto;

public class TransferClanOwnershipRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(TransferClanOwnershipRequestEvent.class);

	private TransferClanOwnershipRequestProto transferClanOwnershipRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			transferClanOwnershipRequestProto = TransferClanOwnershipRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = transferClanOwnershipRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("TransferClanOwnershipRequest exception", e);
		}
	}

	public TransferClanOwnershipRequestProto getTransferClanOwnershipRequestProto() {
		return transferClanOwnershipRequestProto;
	}

	public void setTransferClanOwnershipRequestProto(
			TransferClanOwnershipRequestProto transferClanOwnershipRequestProto) {
		this.transferClanOwnershipRequestProto = transferClanOwnershipRequestProto;
	}

	@Override
	public String toString() {
		return "TransferClanOwnershipRequestEvent [transferClanOwnershipRequestProto="
				+ transferClanOwnershipRequestProto + "]";
	}

}
