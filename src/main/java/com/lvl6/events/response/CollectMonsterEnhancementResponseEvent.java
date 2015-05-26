package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CollectMonsterEnhancementResponseEvent extends NormalResponseEvent<CollectMonsterEnhancementResponseProto> {

	private CollectMonsterEnhancementResponseProto responseProto;

	public CollectMonsterEnhancementResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COLLECT_MONSTER_ENHANCEMENT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCollectMonsterEnhancementResponseProto(
			CollectMonsterEnhancementResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return collectMonsterEnhancementResponseProto.getSerializedSize();
	}

}
