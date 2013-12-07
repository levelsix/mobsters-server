package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetFacebookIdResponseEvent extends NormalResponseEvent {

  private SetFacebookIdResponseProto setFacebookIdResponseProto;
  
  public SetFacebookIdResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SET_FACEBOOK_ID_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = setFacebookIdResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSetFacebookIdResponseProto(SetFacebookIdResponseProto setFacebookIdResponseProto) {
    this.setFacebookIdResponseProto = setFacebookIdResponseProto;
  }

}
