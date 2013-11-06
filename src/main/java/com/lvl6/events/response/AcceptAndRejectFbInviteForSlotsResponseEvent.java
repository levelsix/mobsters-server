package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AcceptAndRejectFbInviteForSlotsResponseEvent extends NormalResponseEvent {

  private AcceptAndRejectFbInviteForSlotsResponseProto acceptAndRejectFbInviteForSlotsResponseProto;
  
  public AcceptAndRejectFbInviteForSlotsResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = acceptAndRejectFbInviteForSlotsResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setAcceptAndRejectFbInviteForSlotsResponseProto(AcceptAndRejectFbInviteForSlotsResponseProto acceptAndRejectFbInviteForSlotsResponseProto) {
    this.acceptAndRejectFbInviteForSlotsResponseProto = acceptAndRejectFbInviteForSlotsResponseProto;
  }

  public AcceptAndRejectFbInviteForSlotsResponseProto getAcceptAndRejectFbInviteForSlotsResponseProto() {   //because APNS required
    return acceptAndRejectFbInviteForSlotsResponseProto;
  }
  
}
