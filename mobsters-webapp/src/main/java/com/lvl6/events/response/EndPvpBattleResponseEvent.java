package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndPvpBattleResponseEvent extends NormalResponseEvent<EndPvpBattleResponseProto> {



	public EndPvpBattleResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_END_PVP_BATTLE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public EndPvpBattleResponseProto getEndPvpBattleResponseProto() {   //because APNS required
		return responseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}
}
