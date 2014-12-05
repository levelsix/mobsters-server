package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventDungeonProto.BeginDungeonRequestProto;

public class BeginDungeonRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BeginDungeonRequestProto beginDungeonRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      beginDungeonRequestProto = BeginDungeonRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = beginDungeonRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("BeginDungeonRequest exception", e);
    }
  }

  public BeginDungeonRequestProto getBeginDungeonRequestProto() {
    return beginDungeonRequestProto;
  }

  @Override
  public String toString()
  {
	  return "BeginDungeonRequestEvent [beginDungeonRequestProto="
		  + beginDungeonRequestProto
		  + "]";
  }
  
}
