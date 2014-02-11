package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.PreDatabaseRequestEvent;
import com.lvl6.proto.EventStartupProto.StartupRequestProto;

public class StartupRequestEvent extends PreDatabaseRequestEvent{

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private StartupRequestProto startupRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      startupRequestProto = StartupRequestProto.parseFrom(ByteString.copyFrom(buff));
      
      // Player id is -1 since it won't be initialized yet. 
      playerId = -1;
      
      udid = startupRequestProto.getUdid();
    } catch (InvalidProtocolBufferException e) {
      log.error("startup request exception", e);
    }
  }

  public StartupRequestProto getStartupRequestProto() {
    return startupRequestProto;
  }

  public void setStartupRequestProto(StartupRequestProto startupRequestProto) {
    this.startupRequestProto = startupRequestProto;
  }
  
  
}