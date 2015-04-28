package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RequestJoinClanResponseEvent extends NormalResponseEvent {

	private RequestJoinClanResponseProto requestJoinClanResponseProto;

	public RequestJoinClanResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REQUEST_JOIN_CLAN_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = requestJoinClanResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRequestJoinClanResponseProto(
			RequestJoinClanResponseProto requestJoinClanResponseProto) {
		this.requestJoinClanResponseProto = requestJoinClanResponseProto;
	}
	
	public int eventSize() {
		return requestJoinClanResponseProto.getSerializedSize();
	}

}
