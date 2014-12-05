package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalRequestProto;

public class BeginObstacleRemovalRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private BeginObstacleRemovalRequestProto beginObstacleRemovalRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      beginObstacleRemovalRequestProto = BeginObstacleRemovalRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = beginObstacleRemovalRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("BeginObstacleRemovalRequest exception", e);
    }
  }

  public BeginObstacleRemovalRequestProto getBeginObstacleRemovalRequestProto() {
    return beginObstacleRemovalRequestProto;
  }
  //added for testing purposes
  public void setBeginObstacleRemovalRequestProto(
		  BeginObstacleRemovalRequestProto beginObstacleRemovalRequestProto) {
	  this.beginObstacleRemovalRequestProto = beginObstacleRemovalRequestProto;
  }

  @Override
  public String toString()
  {
	  return "BeginObstacleRemovalRequestEvent [beginObstacleRemovalRequestProto="
		  + beginObstacleRemovalRequestProto
		  + "]";
  }

}
