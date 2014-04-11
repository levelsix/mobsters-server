package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.SpawnObstacleRequestProto;

public class SpawnObstacleRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private SpawnObstacleRequestProto spawnObstacleRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      spawnObstacleRequestProto = SpawnObstacleRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = spawnObstacleRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("spawn obstacle request exception", e);
    }
  }

  public SpawnObstacleRequestProto getSpawnObstacleRequestProto() {
    return spawnObstacleRequestProto;
  }
  //added for testing purposes
  public void setSpawnObstacleRequestProto(SpawnObstacleRequestProto sorp) {
	  this.spawnObstacleRequestProto = sorp;
  }
  
}
