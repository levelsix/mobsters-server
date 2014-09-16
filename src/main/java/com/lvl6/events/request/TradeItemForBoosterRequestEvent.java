package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.TradeItemForBoosterRequestProto;

public class TradeItemForBoosterRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private TradeItemForBoosterRequestProto tradeItemForBoosterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      tradeItemForBoosterRequestProto = TradeItemForBoosterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = tradeItemForBoosterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("enable apns request exception", e);
    }
  }

  public TradeItemForBoosterRequestProto getTradeItemForBoosterRequestProto() {
    return tradeItemForBoosterRequestProto;
  }

  public void setTradeItemForBoosterRequestProto( TradeItemForBoosterRequestProto tradeItemForBoosterRequestProto )
  {
	  this.tradeItemForBoosterRequestProto = tradeItemForBoosterRequestProto;
  }

}
