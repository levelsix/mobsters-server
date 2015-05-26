package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ObstacleRemovalCompleteResponseEvent extends NormalResponseEvent<ObstacleRemovalCompleteResponseProto> {

	private ObstacleRemovalCompleteResponseProto responseProto;

	public ObstacleRemovalCompleteResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_OBSTACLE_REMOVAL_COMPLETE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setObstacleRemovalCompleteResponseProto(
			ObstacleRemovalCompleteResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}


}
