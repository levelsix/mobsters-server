package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EvolutionFinishedResponseEvent extends NormalResponseEvent {

  private EvolutionFinishedResponseProto evolutionFinishedResponseProto;
  
  public EvolutionFinishedResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_EVOLUTION_FINISHED_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = evolutionFinishedResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setEvolutionFinishedResponseProto(EvolutionFinishedResponseProto evolutionFinishedResponseProto) {
    this.evolutionFinishedResponseProto = evolutionFinishedResponseProto;
  }

}
