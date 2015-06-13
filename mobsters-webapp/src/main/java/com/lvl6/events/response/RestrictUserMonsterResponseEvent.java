package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.RestrictUserMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RestrictUserMonsterResponseEvent extends NormalResponseEvent<RestrictUserMonsterResponseProto> {

	

	public RestrictUserMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RESTRICT_USER_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRestrictUserMonsterResponseProto(
			RestrictUserMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public RestrictUserMonsterResponseProto getRestrictUserMonsterResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
