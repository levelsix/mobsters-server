package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.SendGroupChatResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SendGroupChatResponseEvent extends NormalResponseEvent<SendGroupChatResponseProto> {

	private SendGroupChatResponseProto responseProto;

	public SendGroupChatResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SEND_GROUP_CHAT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSendGroupChatResponseProto(
			SendGroupChatResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public SendGroupChatResponseProto getSendGroupChatResponseProto() { //required for APNS
		return responseProto;
	}
	
	public int eventSize() {
		return sendGroupChatResponseProto.getSerializedSize();
	}

}
