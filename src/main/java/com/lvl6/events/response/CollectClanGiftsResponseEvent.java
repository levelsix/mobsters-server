package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CollectClanGiftsResponseEvent extends NormalResponseEvent<CollectClanGiftsResponseProto> {

	private CollectClanGiftsResponseProto responseProto;

	public CollectClanGiftsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COLLECT_CLAN_GIFTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCollectClanGiftsResponseProto(
			CollectClanGiftsResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public CollectClanGiftsResponseProto getCollectClanGiftsResponseProto() {   //because APNS required
		return responseProto;
	}

}
