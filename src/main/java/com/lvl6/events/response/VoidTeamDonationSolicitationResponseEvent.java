package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class VoidTeamDonationSolicitationResponseEvent extends NormalResponseEvent {

  private VoidTeamDonationSolicitationResponseProto voidTeamDonationSolicitationResponseProto;
  
  public VoidTeamDonationSolicitationResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_VOID_TEAM_DONATION_SOLICITATION_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = voidTeamDonationSolicitationResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setVoidTeamDonationSolicitationResponseProto(VoidTeamDonationSolicitationResponseProto voidTeamDonationSolicitationResponseProto) {
    this.voidTeamDonationSolicitationResponseProto = voidTeamDonationSolicitationResponseProto;
  }

}