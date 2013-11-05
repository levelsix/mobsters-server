package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class InviteFbFriendsForSlotsResponseEvent extends NormalResponseEvent {

  private InviteFbFriendsForSlotsResponseProto inviteFbFriendsForSlotsResponseProto;
  
  public InviteFbFriendsForSlotsResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = inviteFbFriendsForSlotsResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setInviteFbFriendsForSlotsResponseProto(InviteFbFriendsForSlotsResponseProto inviteFbFriendsForSlotsResponseProto) {
    this.inviteFbFriendsForSlotsResponseProto = inviteFbFriendsForSlotsResponseProto;
  }

  public InviteFbFriendsForSlotsResponseProto getInviteFbFriendsForSlotsResponseProto() {   //because APNS required
    return inviteFbFriendsForSlotsResponseProto;
  }
  
}
