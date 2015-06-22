package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpgradeNormStructureResponseEvent extends NormalResponseEvent<UpgradeNormStructureResponseProto> {



	public UpgradeNormStructureResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPGRADE_NORM_STRUCTURE_EVENT;
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
