package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveClanInfoResponseEvent extends NormalResponseEvent<RetrieveClanInfoResponseProto> {

	private RetrieveClanInfoResponseProto responseProto;

	public RetrieveClanInfoResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_CLAN_INFO_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveClanInfoResponseProto(
			RetrieveClanInfoResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return retrieveClanInfoResponseProto.getSerializedSize();
	}

}
