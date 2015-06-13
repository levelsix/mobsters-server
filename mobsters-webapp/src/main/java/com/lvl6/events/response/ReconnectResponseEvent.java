package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStartupProto.ReconnectResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReconnectResponseEvent extends NormalResponseEvent<ReconnectResponseProto> {

	public ReconnectResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RECONNECT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setReconnectResponseProto(
			ReconnectResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
