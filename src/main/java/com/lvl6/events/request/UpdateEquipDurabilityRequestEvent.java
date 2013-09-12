package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventProto.UpdateEquipDurabilityRequestProto;

public class UpdateEquipDurabilityRequestEvent extends RequestEvent {

  private UpdateEquipDurabilityRequestProto updateEquipDurabilityRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      updateEquipDurabilityRequestProto = UpdateEquipDurabilityRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = updateEquipDurabilityRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public UpdateEquipDurabilityRequestProto getUpdateEquipDurabilityRequestProto() {
    return updateEquipDurabilityRequestProto;
  }
}
