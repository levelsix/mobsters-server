package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventRewardProto.SendTangoGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SendTangoGiftResponseEvent extends NormalResponseEvent<SendTangoGiftResponseProto> {

	private SendTangoGiftResponseProto sendTangoGiftResponseProto;

	public SendTangoGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SEND_TANGO_GIFT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = sendTangoGiftResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
