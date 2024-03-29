package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class GeneralNotificationResponseEvent extends NormalResponseEvent<GeneralNotificationResponseProto> {



	//The input argument is not used.
	public GeneralNotificationResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_GENERAL_NOTIFICATION_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public GeneralNotificationResponseProto getGeneralNotificationResponseProto() {
		return responseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}
}
