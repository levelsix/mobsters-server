package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.SendAdminMessageResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SendAdminMessageResponseEvent extends NormalResponseEvent<SendAdminMessageResponseProto> {

	private SendAdminMessageResponseProto responseProto;

	public SendAdminMessageResponseProto getSendAdminMessageResponseProto() {
		return responseProto;
	}

	public void setSendAdminMessageResponseProto(
			SendAdminMessageResponseProto responseProto) {
		this.responseProto = responseProto;
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

	public int eventSize() {
		return sendAdminMessageResponseProto.getSerializedSize();
	}
}
