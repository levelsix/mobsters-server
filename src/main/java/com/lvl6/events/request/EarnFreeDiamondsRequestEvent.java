package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventInAppPurchaseProto.EarnFreeDiamondsRequestProto;

public class EarnFreeDiamondsRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EarnFreeDiamondsRequestProto earnFreeDiamondsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      earnFreeDiamondsRequestProto = EarnFreeDiamondsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = earnFreeDiamondsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("earn free diamonds request exception", e);
    }
  }

  public EarnFreeDiamondsRequestProto getEarnFreeDiamondsRequestProto() {
    return earnFreeDiamondsRequestProto;
  }
}
