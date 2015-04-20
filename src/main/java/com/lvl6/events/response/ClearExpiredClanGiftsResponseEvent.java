package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.ClearExpiredClanGiftsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ClearExpiredClanGiftsResponseEvent extends NormalResponseEvent {

	private ClearExpiredClanGiftsResponseProto clearExpiredClanGiftsResponseProto;

	public ClearExpiredClanGiftsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_CLEAR_EXPIRED_CLAN_GIFTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = clearExpiredClanGiftsResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setClearExpiredClanGiftsResponseProto(
			ClearExpiredClanGiftsResponseProto clearExpiredClanGiftsResponseProto) {
		this.clearExpiredClanGiftsResponseProto = clearExpiredClanGiftsResponseProto;
	}

}
