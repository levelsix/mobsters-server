package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EvolutionFinishedResponseEvent extends NormalResponseEvent<EvolutionFinishedResponseProto> {

	

	public EvolutionFinishedResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_EVOLUTION_FINISHED_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setEvolutionFinishedResponseProto(
			EvolutionFinishedResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
