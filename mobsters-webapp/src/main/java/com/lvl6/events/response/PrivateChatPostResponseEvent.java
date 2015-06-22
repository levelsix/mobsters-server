package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PrivateChatPostResponseEvent extends NormalResponseEvent<PrivateChatPostResponseProto> {



	public PrivateChatPostResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PRIVATE_CHAT_POST_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public PrivateChatPostResponseProto getPrivateChatPostResponseProto() { //required for APNS
		return responseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
