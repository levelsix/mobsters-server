package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventApnsProto.EnableAPNSResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EnableAPNSResponseEvent extends NormalResponseEvent<EnableAPNSResponseProto> {

	private EnableAPNSResponseProto responseProto;

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

	public void setEnableAPNSResponseProto(
			EnableAPNSResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}
}
