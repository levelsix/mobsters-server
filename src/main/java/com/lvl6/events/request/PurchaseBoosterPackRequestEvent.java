package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackRequestProto;

public class PurchaseBoosterPackRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private PurchaseBoosterPackRequestProto purchaseBoosterPackRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      purchaseBoosterPackRequestProto = PurchaseBoosterPackRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = purchaseBoosterPackRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("purchase booster pack request exception", e);
    }
  }

  public PurchaseBoosterPackRequestProto getPurchaseBoosterPackRequestProto() {
    return purchaseBoosterPackRequestProto;
  }
}
