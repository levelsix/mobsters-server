package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginObstacleRemovalResponseEvent extends NormalResponseEvent<BeginObstacleRemovalResponseProto> {

	

	public BeginObstacleRemovalResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_BEGIN_OBSTACLE_REMOVAL_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setBeginObstacleRemovalResponseProto(
			BeginObstacleRemovalResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
