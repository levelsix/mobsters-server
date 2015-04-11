package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.BeginClanAvengingResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginClanAvengingResponseEvent extends NormalResponseEvent {

	private BeginClanAvengingResponseProto beginClanAvengingResponseProto;

	public BeginClanAvengingResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_BEGIN_CLAN_AVENGING_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = beginClanAvengingResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setBeginClanAvengingResponseProto(
			BeginClanAvengingResponseProto beginClanAvengingResponseProto) {
		this.beginClanAvengingResponseProto = beginClanAvengingResponseProto;
	}

}
