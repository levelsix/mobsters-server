package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SpawnMiniJobResponseEvent extends NormalResponseEvent<SpawnMiniJobResponseProto> {

	private SpawnMiniJobResponseProto responseProto;

	public SpawnMiniJobResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SPAWN_MINI_JOB_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSpawnMiniJobResponseProto(
			SpawnMiniJobResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
