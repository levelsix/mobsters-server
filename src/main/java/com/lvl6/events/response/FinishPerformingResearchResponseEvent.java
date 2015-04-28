package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class FinishPerformingResearchResponseEvent extends NormalResponseEvent {

	private FinishPerformingResearchResponseProto finishPerformingResearchResponseProto;

	public FinishPerformingResearchResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_FINISH_PERFORMING_RESEARCH_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = finishPerformingResearchResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setFinishPerformingResearchResponseProto(
			FinishPerformingResearchResponseProto finishPerformingResearchResponseProto) {
		this.finishPerformingResearchResponseProto = finishPerformingResearchResponseProto;
	}
	
	public int eventSize() {
		return finishPerformingResearchResponseProto.getSerializedSize();
	}

}
