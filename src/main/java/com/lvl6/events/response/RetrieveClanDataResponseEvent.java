package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveClanDataResponseEvent extends NormalResponseEvent {

	private RetrieveClanDataResponseProto retrieveClanDataResponseProto;

	public RetrieveClanDataResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_CLAN_DATA_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = retrieveClanDataResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveClanDataResponseProto(
			RetrieveClanDataResponseProto retrieveClanDataResponseProto) {
		this.retrieveClanDataResponseProto = retrieveClanDataResponseProto;
	}
	
	public int eventSize() {
		return retrieveClanDataResponseProto.getSerializedSize();
	}

}
