package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveMiniEventResponseEvent extends NormalResponseEvent<RetrieveMiniEventResponseProto> {

	private RetrieveMiniEventResponseProto responseProto;

	public RetrieveMiniEventResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_MINI_EVENT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveMiniEventResponseProto(
			RetrieveMiniEventResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return retrieveMiniEventResponseProto.getSerializedSize();
	}

}
