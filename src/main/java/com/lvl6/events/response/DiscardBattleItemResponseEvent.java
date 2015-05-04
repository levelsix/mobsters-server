package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class DiscardBattleItemResponseEvent extends NormalResponseEvent<DiscardBattleItemResponseProto> {

	private DiscardBattleItemResponseProto responseProto;

	public DiscardBattleItemResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_DISCARD_BATTLE_ITEM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setDiscardBattleItemResponseProto(
			DiscardBattleItemResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
