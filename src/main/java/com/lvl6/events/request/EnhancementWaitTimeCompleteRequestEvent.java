package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteRequestProto;

public class EnhancementWaitTimeCompleteRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EnhancementWaitTimeCompleteRequestProto enhancementWaitTimeCompleteRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
    	enhancementWaitTimeCompleteRequestProto = EnhancementWaitTimeCompleteRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = enhancementWaitTimeCompleteRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("enhancement wait time complete request exception", e);
    }
  }

  public EnhancementWaitTimeCompleteRequestProto getEnhancementWaitTimeCompleteRequestProto() {
    return enhancementWaitTimeCompleteRequestProto;
  }
}
