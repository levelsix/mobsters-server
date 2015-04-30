package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class NormStructWaitCompleteResponseEvent extends NormalResponseEvent<NormStructWaitCompleteResponseProto> {

	private NormStructWaitCompleteResponseProto normStructWaitCompleteResponseProto;

	public NormStructWaitCompleteResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_NORM_STRUCT_WAIT_COMPLETE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = normStructWaitCompleteResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setNormStructWaitCompleteResponseProto(
			NormStructWaitCompleteResponseProto normStructWaitCompleteResponseProto) {
		this.normStructWaitCompleteResponseProto = normStructWaitCompleteResponseProto;
	}

}
