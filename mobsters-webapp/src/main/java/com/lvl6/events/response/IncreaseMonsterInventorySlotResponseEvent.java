package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class IncreaseMonsterInventorySlotResponseEvent extends	NormalResponseEvent<IncreaseMonsterInventorySlotResponseProto> {

	

	public IncreaseMonsterInventorySlotResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setIncreaseMonsterInventorySlotResponseProto(
			IncreaseMonsterInventorySlotResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public IncreaseMonsterInventorySlotResponseProto getIncreaseMonsterInventorySlotResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
