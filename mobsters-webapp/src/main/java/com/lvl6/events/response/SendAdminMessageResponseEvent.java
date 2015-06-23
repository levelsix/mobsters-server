package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.SendAdminMessageResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SendAdminMessageResponseEvent extends NormalResponseEvent<SendAdminMessageResponseProto> {



	public SendAdminMessageResponseProto getSendAdminMessageResponseProto() {
		return responseProto;
	}

	public SendAdminMessageResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SEND_ADMIN_MESSAGE_EVENT;
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
