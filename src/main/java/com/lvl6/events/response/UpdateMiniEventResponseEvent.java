package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateMiniEventResponseEvent extends NormalResponseEvent {

	private UpdateMiniEventResponseProto updateMiniEventResponseProto;

	public UpdateMiniEventResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_MINI_EVENT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = updateMiniEventResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setUpdateMiniEventResponseProto(
			UpdateMiniEventResponseProto updateMiniEventResponseProto) {
		this.updateMiniEventResponseProto = updateMiniEventResponseProto;
	}

	public int eventSize() {
		return updateMiniEventResponseProto.getSerializedSize();
	}
}
