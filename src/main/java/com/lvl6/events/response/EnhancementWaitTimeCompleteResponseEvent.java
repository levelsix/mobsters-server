package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EnhancementWaitTimeCompleteResponseEvent extends NormalResponseEvent<EnhancementWaitTimeCompleteResponseProto> {

	private EnhancementWaitTimeCompleteResponseProto responseProto;

	public EnhancementWaitTimeCompleteResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setEnhancementWaitTimeCompleteResponseProto(
			EnhancementWaitTimeCompleteResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return enhancementWaitTimeCompleteResponseProto.getSerializedSize();
	}

}
