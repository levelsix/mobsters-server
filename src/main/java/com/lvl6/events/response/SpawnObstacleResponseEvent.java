package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.SpawnObstacleResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SpawnObstacleResponseEvent extends NormalResponseEvent {

	private SpawnObstacleResponseProto spawnObstacleResponseProto;

	public SpawnObstacleResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SPAWN_OBSTACLE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = spawnObstacleResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSpawnObstacleResponseProto(
			SpawnObstacleResponseProto spawnObstacleResponseProto) {
		this.spawnObstacleResponseProto = spawnObstacleResponseProto;
	}
	
	public int eventSize() {
		return spawnObstacleResponseProto.getSerializedSize();
	}

}
