package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RedeemSecretGiftResponseEvent extends NormalResponseEvent<RedeemSecretGiftResponseProto> {



	public RedeemSecretGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REDEEM_SECRET_GIFT_EVENT;
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
