package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesRequestProto;

public class ExchangeGemsForResourcesRequestEvent extends RequestEvent {

  private ExchangeGemsForResourcesRequestProto exchangeGemsForResourcesRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      exchangeGemsForResourcesRequestProto = ExchangeGemsForResourcesRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = exchangeGemsForResourcesRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public ExchangeGemsForResourcesRequestProto getExchangeGemsForResourcesRequestProto() {
    return exchangeGemsForResourcesRequestProto;
  }
}
