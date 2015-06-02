package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RedeemMiniEventRewardResponseEvent extends NormalResponseEvent<RedeemMiniEventRewardResponseProto> {

	

	public RedeemMiniEventRewardResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REDEEM_MINI_EVENT_REWARD_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRedeemMiniEventRewardResponseProto(
			RedeemMiniEventRewardResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
