package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginObstacleRemovalResponseEvent extends NormalResponseEvent {

  private BeginObstacleRemovalResponseProto beginObstacleRemovalResponseProto;
  
  public BeginObstacleRemovalResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_BEGIN_OBSTACLE_REMOVAL_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = beginObstacleRemovalResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setBeginObstacleRemovalResponseProto(BeginObstacleRemovalResponseProto beginObstacleRemovalResponseProto) {
    this.beginObstacleRemovalResponseProto = beginObstacleRemovalResponseProto;
  }

}
