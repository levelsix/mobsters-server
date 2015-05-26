package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.ReceivedGroupChatResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReceivedGroupChatResponseEvent extends NormalResponseEvent<ReceivedGroupChatResponseProto> {

	private ReceivedGroupChatResponseProto responseProto;

	public ReceivedGroupChatResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RECEIVED_GROUP_CHAT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setReceivedGroupChatResponseProto(
			ReceivedGroupChatResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
