package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventRewardProto.CollectGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CollectGiftResponseEvent extends NormalResponseEvent<CollectGiftResponseProto> {

	private CollectGiftResponseProto collectGiftResponseProto;

	public CollectGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COLLECT_GIFT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = collectGiftResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}
}
