package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventApnsProto.EnableAPNSResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EnableAPNSResponseEvent extends NormalResponseEvent<EnableAPNSResponseProto> {



	public EnableAPNSResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ENABLE_APNS_EVENT;
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
