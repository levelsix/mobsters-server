package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureRequestProto;

public class DestroyMoneyTreeStructureRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(DestroyMoneyTreeStructureRequestEvent.class);

	private DestroyMoneyTreeStructureRequestProto destroyMoneyTreeStructureRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			destroyMoneyTreeStructureRequestProto = DestroyMoneyTreeStructureRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = destroyMoneyTreeStructureRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("DestroyMoneyTree exception", e);
		}
	}

	public DestroyMoneyTreeStructureRequestProto getDestroyMoneyTreeStructureRequestProto() {
		return destroyMoneyTreeStructureRequestProto;
	}

	public void setDestroyMoneyTreeStructureRequestProto(
			DestroyMoneyTreeStructureRequestProto dmtsrp) {
		this.destroyMoneyTreeStructureRequestProto = dmtsrp;
	}

	@Override
	public String toString() {
		return "DestroyUserStructRequestEvent [destroyUserStructRequestProto="
				+ destroyMoneyTreeStructureRequestProto + "]";
	}

}
