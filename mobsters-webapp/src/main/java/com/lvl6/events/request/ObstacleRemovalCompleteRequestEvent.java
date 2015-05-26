package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteRequestProto;

public class ObstacleRemovalCompleteRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private ObstacleRemovalCompleteRequestProto obstacleRemovalCompleteRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			obstacleRemovalCompleteRequestProto = ObstacleRemovalCompleteRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = obstacleRemovalCompleteRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("ObstacleRemovalCompleteRequest exception", e);
		}
	}

	public ObstacleRemovalCompleteRequestProto getObstacleRemovalCompleteRequestProto() {
		return obstacleRemovalCompleteRequestProto;
	}

	//added for testing purposes
	public void setObstacleRemovalCompleteRequestProto(
			ObstacleRemovalCompleteRequestProto obstacleRemovalCompleteRequestProto) {
		this.obstacleRemovalCompleteRequestProto = obstacleRemovalCompleteRequestProto;
	}

	@Override
	public String toString() {
		return "ObstacleRemovalCompleteRequestEvent [obstacleRemovalCompleteRequestProto="
				+ obstacleRemovalCompleteRequestProto + "]";
	}

}
