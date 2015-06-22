package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateUserStrengthResponseEvent extends NormalResponseEvent<UpdateUserStrengthResponseProto> {



	public UpdateUserStrengthResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_USER_STRENGTH_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public UpdateUserStrengthResponseProto getUpdateUserStrengthResponseProto() {   //because APNS required
		return responseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
