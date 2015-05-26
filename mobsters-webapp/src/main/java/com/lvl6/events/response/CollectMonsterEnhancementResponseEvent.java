package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CollectMonsterEnhancementResponseEvent extends NormalResponseEvent {

	private CollectMonsterEnhancementResponseProto collectMonsterEnhancementResponseProto;

	public CollectMonsterEnhancementResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COLLECT_MONSTER_ENHANCEMENT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = collectMonsterEnhancementResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCollectMonsterEnhancementResponseProto(
			CollectMonsterEnhancementResponseProto collectMonsterEnhancementResponseProto) {
		this.collectMonsterEnhancementResponseProto = collectMonsterEnhancementResponseProto;
	}
	
	public int eventSize() {
		return collectMonsterEnhancementResponseProto.getSerializedSize();
	}

}
