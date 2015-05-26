package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveClanDataResponseEvent extends NormalResponseEvent<RetrieveClanDataResponseProto> {

	private RetrieveClanDataResponseProto responseProto;

	public RetrieveClanDataResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_CLAN_DATA_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveClanDataResponseProto(
			RetrieveClanDataResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return retrieveClanDataResponseProto.getSerializedSize();
	}

}
