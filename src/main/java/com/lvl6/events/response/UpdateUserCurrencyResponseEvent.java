package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateUserCurrencyResponseEvent extends NormalResponseEvent {

  private UpdateUserCurrencyResponseProto updateUserCurrencyResponseProto;
  
  public UpdateUserCurrencyResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_UPDATE_USER_CURRENCY_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = updateUserCurrencyResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setUpdateUserCurrencyResponseProto(UpdateUserCurrencyResponseProto updateUserCurrencyResponseProto) {
    this.updateUserCurrencyResponseProto = updateUserCurrencyResponseProto;
  }

  public UpdateUserCurrencyResponseProto getUpdateUserCurrencyResponseProto() {   //because APNS required
    return updateUserCurrencyResponseProto;
  }
  
}
