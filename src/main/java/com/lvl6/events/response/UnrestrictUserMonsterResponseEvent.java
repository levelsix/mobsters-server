package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UnrestrictUserMonsterResponseEvent extends NormalResponseEvent<UnrestrictUserMonsterResponseProto> {

	

	public UnrestrictUserMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UNRESTRICT_USER_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUnrestrictUserMonsterResponseProto(
			UnrestrictUserMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public UnrestrictUserMonsterResponseProto getUnrestrictUserMonsterResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
