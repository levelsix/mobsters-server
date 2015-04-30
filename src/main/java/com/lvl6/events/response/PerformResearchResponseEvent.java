package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PerformResearchResponseEvent extends NormalResponseEvent<PerformResearchResponseProto> {

	private PerformResearchResponseProto performResearchResponseProto;

	public PerformResearchResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PERFORM_RESEARCH_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = performResearchResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setPerformResearchResponseProto(
			PerformResearchResponseProto performResearchResponseProto) {
		this.performResearchResponseProto = performResearchResponseProto;
	}

}
