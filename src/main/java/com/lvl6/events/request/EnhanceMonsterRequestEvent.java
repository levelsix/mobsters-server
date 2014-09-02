package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterRequestProto;

public class EnhanceMonsterRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EnhanceMonsterRequestProto enhanceMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
    	enhanceMonsterRequestProto = EnhanceMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = enhanceMonsterRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("enhancement wait time complete request exception", e);
    }
  }

  public EnhanceMonsterRequestProto getEnhanceMonsterRequestProto() {
    return enhanceMonsterRequestProto;
  }

  public void setEnhanceMonsterRequestProto( EnhanceMonsterRequestProto enhanceMonsterRequestProto )
  {
	  this.enhanceMonsterRequestProto = enhanceMonsterRequestProto;
  }

}
