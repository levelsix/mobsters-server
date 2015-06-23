package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CustomizePvpBoardObstacleResponseEvent extends NormalResponseEvent<CustomizePvpBoardObstacleResponseProto> {



	public CustomizePvpBoardObstacleResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_CUSTOMIZE_PVP_BOARD_OBSTACLE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
