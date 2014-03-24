package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ObstacleRemovalCompleteResponseEvent extends NormalResponseEvent {

  private ObstacleRemovalCompleteResponseProto obstacleRemovalCompleteResponseProto;
  
  public ObstacleRemovalCompleteResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_OBSTACLE_REMOVAL_COMPLETE_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = obstacleRemovalCompleteResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setObstacleRemovalCompleteResponseProto(ObstacleRemovalCompleteResponseProto obstacleRemovalCompleteResponseProto) {
    this.obstacleRemovalCompleteResponseProto = obstacleRemovalCompleteResponseProto;
  }

}
