package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateMonsterHealthResponseEvent extends NormalResponseEvent<UpdateMonsterHealthResponseProto> {

	

	public UpdateMonsterHealthResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_MONSTER_HEALTH_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUpdateMonsterHealthResponseProto(
			UpdateMonsterHealthResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public UpdateMonsterHealthResponseProto getUpdateMonsterHealthResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
