package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ExchangeGemsForResourcesResponseEvent extends NormalResponseEvent {

  private ExchangeGemsForResourcesResponseProto exchangeGemsForResourcesResponseProto;
  
  public ExchangeGemsForResourcesResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_EXCHANGE_GEMS_FOR_RESOURCES_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = exchangeGemsForResourcesResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setExchangeGemsForResourcesResponseProto(ExchangeGemsForResourcesResponseProto exchangeGemsForResourcesResponseProto) {
    this.exchangeGemsForResourcesResponseProto = exchangeGemsForResourcesResponseProto;
  }

}
