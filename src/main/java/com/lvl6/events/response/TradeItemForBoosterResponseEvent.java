package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class TradeItemForBoosterResponseEvent extends NormalResponseEvent<TradeItemForBoosterResponseProto> {

	private TradeItemForBoosterResponseProto responseProto;

	public TradeItemForBoosterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_TRADE_ITEM_FOR_BOOSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setTradeItemForBoosterResponseProto(
			TradeItemForBoosterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public int eventSize() {
		return tradeItemForBoosterResponseProto.getSerializedSize();
	}
}
