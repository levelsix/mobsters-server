package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class TradeItemForResourcesResponseEvent extends NormalResponseEvent {

  private TradeItemForResourcesResponseProto tradeItemForResourcesResponseProto;
  
  public TradeItemForResourcesResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_TRADE_ITEM_FOR_RESOURCES_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = tradeItemForResourcesResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setTradeItemForResourcesResponseProto(TradeItemForResourcesResponseProto tradeItemForResourcesResponseProto) {
    this.tradeItemForResourcesResponseProto = tradeItemForResourcesResponseProto;
  }

}
