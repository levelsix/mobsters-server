package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventProto.UpdateEquipDurabilityResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateEquipDurabilityResponseEvent extends NormalResponseEvent {

  private UpdateEquipDurabilityResponseProto updateEquipDurabilityResponseProto;
  
  public UpdateEquipDurabilityResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_UPDATE_EQUIP_DURABILITY_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = updateEquipDurabilityResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setUpdateEquipDurabilityResponseProto(UpdateEquipDurabilityResponseProto updateEquipDurabilityResponseProto) {
    this.updateEquipDurabilityResponseProto = updateEquipDurabilityResponseProto;
  }

  public UpdateEquipDurabilityResponseProto getUpdateEquipDurabilityResponseProto() {   //because APNS required
    return updateEquipDurabilityResponseProto;
  }
  
}
