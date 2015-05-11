package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.ReceivedClanGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReceivedClanGiftResponseEvent extends
		NormalResponseEvent {

	private ReceivedClanGiftResponseProto receivedClanGiftResponseProto;

	public ReceivedClanGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RECEIVED_CLAN_GIFTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = receivedClanGiftResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setReceivedClanGiftResponseProto(
			ReceivedClanGiftResponseProto receivedClanGiftResponseProto) {
		this.receivedClanGiftResponseProto = receivedClanGiftResponseProto;
	}

}