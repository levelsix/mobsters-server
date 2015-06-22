package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;

public class CreateBattleItemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(CreateBattleItemRequestEvent.class);

	private CreateBattleItemRequestProto createBattleItemRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			createBattleItemRequestProto = CreateBattleItemRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = createBattleItemRequestProto.getSender()
					.getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("CreateBattleItemRequest exception", e);
		}
	}

	public CreateBattleItemRequestProto getCreateBattleItemRequestProto() {
		return createBattleItemRequestProto;
	}

	//added for testing purposes
	public void setCreateBattleItemRequestProto(
			CreateBattleItemRequestProto sorp) {
		this.createBattleItemRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "CreateBattleItemRequestEvent [createBattleItemRequestProto="
				+ createBattleItemRequestProto + "]";
	}

}
