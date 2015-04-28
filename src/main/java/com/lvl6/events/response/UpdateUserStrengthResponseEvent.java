package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateUserStrengthResponseEvent extends NormalResponseEvent {

	private UpdateUserStrengthResponseProto updateUserStrengthResponseProto;

	public UpdateUserStrengthResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_USER_STRENGTH_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = updateUserStrengthResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUpdateUserStrengthResponseProto(
			UpdateUserStrengthResponseProto updateUserStrengthResponseProto) {
		this.updateUserStrengthResponseProto = updateUserStrengthResponseProto;
	}

	public UpdateUserStrengthResponseProto getUpdateUserStrengthResponseProto() {   //because APNS required
		return updateUserStrengthResponseProto;
	}
	
	public int eventSize() {
		return updateUserStrengthResponseProto.getSerializedSize();
	}

}
