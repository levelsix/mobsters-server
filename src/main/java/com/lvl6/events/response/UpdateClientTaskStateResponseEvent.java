package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateClientTaskStateResponseEvent extends NormalResponseEvent {

  private UpdateClientTaskStateResponseProto updateClientTaskStateResponseProto;
  
  public UpdateClientTaskStateResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_UPDATE_CLIENT_TASK_STATE_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = updateClientTaskStateResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setUpdateClientTaskStateResponseProto(UpdateClientTaskStateResponseProto updateClientTaskStateResponseProto) {
    this.updateClientTaskStateResponseProto = updateClientTaskStateResponseProto;
  }

  public UpdateClientTaskStateResponseProto getUpdateClientTaskStateResponseProto() {   //because APNS required
    return updateClientTaskStateResponseProto;
  }
  
}
