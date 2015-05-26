package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStaticDataProto.PurgeClientStaticDataResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PurgeClientStaticDataResponseEvent extends NormalResponseEvent<PurgeClientStaticDataResponseProto>{

	private PurgeClientStaticDataResponseProto responseProto;

	public PurgeClientStaticDataResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PURGE_STATIC_DATA_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setPurgeClientStaticDataResponseProto(
			PurgeClientStaticDataResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
