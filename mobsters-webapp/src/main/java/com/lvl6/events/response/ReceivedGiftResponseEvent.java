package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReceivedGiftResponseEvent extends
		NormalResponseEvent {

	public ReceivedGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RECEIVED_GIFT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setReceivedGiftResponseProto(
			ReceivedGiftResponseProto receivedGiftResponseProto) {
		this.responseProto = receivedGiftResponseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
