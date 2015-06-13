package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class HealMonsterResponseEvent extends NormalResponseEvent<HealMonsterResponseProto> {

	

	public HealMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_HEAL_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setHealMonsterResponseProto(
			HealMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
