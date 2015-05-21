package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CollectClanGiftsResponseEvent extends NormalResponseEvent {

	private CollectClanGiftsResponseProto collectClanGiftsResponseProto;

	public CollectClanGiftsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COLLECT_CLAN_GIFTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = collectClanGiftsResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCollectClanGiftsResponseProto(
			CollectClanGiftsResponseProto collectClanGiftsResponseProto) {
		this.collectClanGiftsResponseProto = collectClanGiftsResponseProto;
	}

	public CollectClanGiftsResponseProto getCollectClanGiftsResponseProto() {   //because APNS required
		return collectClanGiftsResponseProto;
	}

	@Override
	public int eventSize() {
		return collectClanGiftsResponseProto.getSerializedSize();
	}
}
