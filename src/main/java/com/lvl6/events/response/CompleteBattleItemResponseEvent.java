package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CompleteBattleItemResponseEvent extends NormalResponseEvent<CompleteBattleItemResponseProto> {

	private CompleteBattleItemResponseProto completeBattleItemResponseProto;

	public CompleteBattleItemResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COMPLETE_BATTLE_ITEM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = completeBattleItemResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCompleteBattleItemResponseProto(
			CompleteBattleItemResponseProto completeBattleItemResponseProto) {
		this.completeBattleItemResponseProto = completeBattleItemResponseProto;
	}

}
