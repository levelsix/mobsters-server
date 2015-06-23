package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.SetTangoIdResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetTangoIdResponseEvent extends NormalResponseEvent<SetTangoIdResponseProto> {

	private SetTangoIdResponseProto setTangoIdResponseProto;

	public SetTangoIdResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SET_TANGO_ID_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = setTangoIdResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
