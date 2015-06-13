package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BootPlayerFromClanResponseEvent extends NormalResponseEvent<BootPlayerFromClanResponseProto> {

	

	public BootPlayerFromClanResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_BOOT_PLAYER_FROM_CLAN_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setBootPlayerFromClanResponseProto(
			BootPlayerFromClanResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
