package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CompleteMiniJobResponseEvent extends NormalResponseEvent<CompleteMiniJobResponseProto> {

	private CompleteMiniJobResponseProto responseProto;

	public CompleteMiniJobResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COMPLETE_MINI_JOB_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCompleteMiniJobResponseProto(
			CompleteMiniJobResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
