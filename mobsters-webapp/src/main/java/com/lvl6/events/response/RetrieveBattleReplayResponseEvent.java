package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveBattleReplayResponseEvent extends NormalResponseEvent<RetrieveBattleReplayResponseProto> {

	private RetrieveBattleReplayResponseProto retrieveBattleReplayResponseProto;

	public RetrieveBattleReplayResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_BATTLE_REPLAY_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = retrieveBattleReplayResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveBattleReplayResponseProto(
			RetrieveBattleReplayResponseProto retrieveBattleReplayResponseProto) {
		this.retrieveBattleReplayResponseProto = retrieveBattleReplayResponseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
