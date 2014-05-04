package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SpawnMiniJobResponseEvent extends NormalResponseEvent {

  private SpawnMiniJobResponseProto spawnMiniJobResponseProto;
  
  public SpawnMiniJobResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SPAWN_MINI_JOB_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = spawnMiniJobResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSpawnMiniJobResponseProto(SpawnMiniJobResponseProto spawnMiniJobResponseProto) {
    this.spawnMiniJobResponseProto = spawnMiniJobResponseProto;
  }

}
