package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginMiniJobResponseEvent extends NormalResponseEvent {

  private BeginMiniJobResponseProto beginMiniJobResponseProto;
  
  public BeginMiniJobResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_BEGIN_MINI_JOB_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = beginMiniJobResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setBeginMiniJobResponseProto(BeginMiniJobResponseProto beginMiniJobResponseProto) {
    this.beginMiniJobResponseProto = beginMiniJobResponseProto;
  }

}
