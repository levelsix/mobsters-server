package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateClientTaskStateResponseEvent extends NormalResponseEvent<UpdateClientTaskStateResponseProto> {

	private UpdateClientTaskStateResponseProto responseProto;

	public UpdateClientTaskStateResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_CLIENT_TASK_STATE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUpdateClientTaskStateResponseProto(
			UpdateClientTaskStateResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public UpdateClientTaskStateResponseProto getUpdateClientTaskStateResponseProto() {   //because APNS required
		return responseProto;
	}
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
