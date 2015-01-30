package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.SolicitTeamDonationResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SolicitTeamDonationResponseEvent extends NormalResponseEvent {

  private SolicitTeamDonationResponseProto solicitTeamDonationResponseProto;
  
  public SolicitTeamDonationResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_SOLICIT_TEAM_DONATION_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = solicitTeamDonationResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setSolicitTeamDonationResponseProto(SolicitTeamDonationResponseProto solicitTeamDonationResponseProto) {
    this.solicitTeamDonationResponseProto = solicitTeamDonationResponseProto;
  }

}
