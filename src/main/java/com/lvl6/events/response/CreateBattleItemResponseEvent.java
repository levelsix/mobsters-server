package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CreateBattleItemResponseEvent extends NormalResponseEvent {

	private CreateBattleItemResponseProto createBattleItemResponseProto;

	public CreateBattleItemResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_CREATE_BATTLE_ITEM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = createBattleItemResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCreateBattleItemResponseProto(
			CreateBattleItemResponseProto createBattleItemResponseProto) {
		this.createBattleItemResponseProto = createBattleItemResponseProto;
	}

	public int eventSize() {
		return createBattleItemResponseProto.getSerializedSize();
	}
}
