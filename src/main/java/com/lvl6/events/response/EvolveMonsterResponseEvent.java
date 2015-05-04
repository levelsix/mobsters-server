package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EvolveMonsterResponseEvent extends NormalResponseEvent<EvolveMonsterResponseProto> {

	private EvolveMonsterResponseProto responseProto;

	public EvolveMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_EVOLVE_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setEvolveMonsterResponseProto(
			EvolveMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
