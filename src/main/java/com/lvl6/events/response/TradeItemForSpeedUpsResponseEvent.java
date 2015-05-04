package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class TradeItemForSpeedUpsResponseEvent extends NormalResponseEvent<TradeItemForSpeedUpsResponseProto> {

	private TradeItemForSpeedUpsResponseProto responseProto;

	public TradeItemForSpeedUpsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_TRADE_ITEM_FOR_SPEED_UPS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setTradeItemForSpeedUpsResponseProto(
			TradeItemForSpeedUpsResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
