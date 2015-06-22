package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PurchaseBoosterPackResponseEvent extends NormalResponseEvent<PurchaseBoosterPackResponseProto> {



	public PurchaseBoosterPackResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PURCHASE_BOOSTER_PACK_EVENT;
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
