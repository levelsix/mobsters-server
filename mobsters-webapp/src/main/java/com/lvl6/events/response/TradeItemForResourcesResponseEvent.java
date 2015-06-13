package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class TradeItemForResourcesResponseEvent extends NormalResponseEvent<TradeItemForResourcesResponseProto> {

	

	public TradeItemForResourcesResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_TRADE_ITEM_FOR_RESOURCES_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setTradeItemForResourcesResponseProto(
			TradeItemForResourcesResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
