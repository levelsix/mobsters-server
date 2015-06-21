package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.SpawnObstacleRequestProto;

public class SpawnObstacleRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SpawnObstacleRequestEvent.class);

	private SpawnObstacleRequestProto spawnObstacleRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			spawnObstacleRequestProto = SpawnObstacleRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = spawnObstacleRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SpawnObstacleRequest exception", e);
		}
	}

	public SpawnObstacleRequestProto getSpawnObstacleRequestProto() {
		return spawnObstacleRequestProto;
	}

	//added for testing purposes
	public void setSpawnObstacleRequestProto(SpawnObstacleRequestProto sorp) {
		this.spawnObstacleRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "SpawnObstacleRequestEvent [spawnObstacleRequestProto="
				+ spawnObstacleRequestProto + "]";
	}

}
