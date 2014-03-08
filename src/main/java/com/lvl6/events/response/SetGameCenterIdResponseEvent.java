package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.SetGameCenterIdResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetGameCenterIdResponseEvent extends NormalResponseEvent {

  private SetGameCenterIdResponseProto setGameCenterIdResponseProto;
  
  public SetGameCenterIdResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SET_GAME_CENTER_ID_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = setGameCenterIdResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSetGameCenterIdResponseProto(SetGameCenterIdResponseProto setGameCenterIdResponseProto) {
    this.setGameCenterIdResponseProto = setGameCenterIdResponseProto;
  }

}
