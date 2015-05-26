package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ExchangeGemsForResourcesResponseEvent extends NormalResponseEvent<ExchangeGemsForResourcesResponseProto> {

	private ExchangeGemsForResourcesResponseProto responseProto;

	public ExchangeGemsForResourcesResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_EXCHANGE_GEMS_FOR_RESOURCES_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setExchangeGemsForResourcesResponseProto(
			ExchangeGemsForResourcesResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return exchangeGemsForResourcesResponseProto.getSerializedSize();
	}

}
