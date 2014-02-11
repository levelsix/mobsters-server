package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;

public class InAppPurchaseRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private InAppPurchaseRequestProto inAppPurchaseRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      inAppPurchaseRequestProto = InAppPurchaseRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = inAppPurchaseRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("in app purchase request exception", e);
    }
  }

  public InAppPurchaseRequestProto getInAppPurchaseRequestProto() {
    return inAppPurchaseRequestProto;
  }
}
