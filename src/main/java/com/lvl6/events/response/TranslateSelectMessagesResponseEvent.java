package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class TranslateSelectMessagesResponseEvent extends NormalResponseEvent<TranslateSelectMessagesResponseProto> {

	private TranslateSelectMessagesResponseProto responseProto;

	public TranslateSelectMessagesResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_TRANSLATE_SELECT_MESSAGES_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setTranslateSelectMessagesResponseProto(
			TranslateSelectMessagesResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return translateSelectMessagesResponseProto.getSerializedSize();
	}

}
