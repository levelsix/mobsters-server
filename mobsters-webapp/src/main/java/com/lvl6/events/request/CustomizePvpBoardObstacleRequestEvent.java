package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleRequestProto;

public class CustomizePvpBoardObstacleRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(CustomizePvpBoardObstacleRequestEvent.class);

	private CustomizePvpBoardObstacleRequestProto customizePvpBoardObstacleRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			customizePvpBoardObstacleRequestProto = CustomizePvpBoardObstacleRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = customizePvpBoardObstacleRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("CustomizePvpBoardObstacle request exception", e);
		}
	}

	public CustomizePvpBoardObstacleRequestProto getCustomizePvpBoardObstacleRequestProto() {
		return customizePvpBoardObstacleRequestProto;
	}

	public void setCustomizePvpBoardObstacleRequestProto(
			CustomizePvpBoardObstacleRequestProto customizePvpBoardObstacleRequestProto) {
		this.customizePvpBoardObstacleRequestProto = customizePvpBoardObstacleRequestProto;
	}

}
