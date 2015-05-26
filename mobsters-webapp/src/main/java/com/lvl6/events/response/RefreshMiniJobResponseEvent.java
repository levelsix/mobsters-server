package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RefreshMiniJobResponseEvent extends NormalResponseEvent {

	private RefreshMiniJobResponseProto refreshMiniJobResponseProto;

	public RefreshMiniJobResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REFRESH_MINI_JOB_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = refreshMiniJobResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRefreshMiniJobResponseProto(
			RefreshMiniJobResponseProto refreshMiniJobResponseProto) {
		this.refreshMiniJobResponseProto = refreshMiniJobResponseProto;
	}

	@Override
	public int eventSize() {
		return refreshMiniJobResponseProto.getSerializedSize();
	}

}
