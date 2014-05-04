package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobRequestProto;

public class SpawnMiniJobRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private SpawnMiniJobRequestProto spawnMiniJobRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      spawnMiniJobRequestProto = SpawnMiniJobRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = spawnMiniJobRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("spawn obstacle request exception", e);
    }
  }

  public SpawnMiniJobRequestProto getSpawnMiniJobRequestProto() {
    return spawnMiniJobRequestProto;
  }
  //added for testing purposes
  public void setSpawnMiniJobRequestProto(SpawnMiniJobRequestProto sorp) {
	  this.spawnMiniJobRequestProto = sorp;
  }
  
}
