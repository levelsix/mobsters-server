package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RedeemSecretGiftResponseEvent extends NormalResponseEvent {

	private RedeemSecretGiftResponseProto redeemSecretGiftResponseProto;

	public RedeemSecretGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REDEEM_SECRET_GIFT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = redeemSecretGiftResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRedeemSecretGiftResponseProto(
			RedeemSecretGiftResponseProto redeemSecretGiftResponseProto) {
		this.redeemSecretGiftResponseProto = redeemSecretGiftResponseProto;
	}
	
	public int eventSize() {
		return redeemSecretGiftResponseProto.getSerializedSize();
	}

}
