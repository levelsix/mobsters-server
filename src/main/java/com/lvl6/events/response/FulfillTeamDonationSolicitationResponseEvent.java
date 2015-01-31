package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class FulfillTeamDonationSolicitationResponseEvent extends NormalResponseEvent {

  private FulfillTeamDonationSolicitationResponseProto fulfillTeamDonationSolicitationResponseProto;
  
  public FulfillTeamDonationSolicitationResponseEvent(String playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_FULFILL_TEAM_DONATION_SOLICITATION_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = fulfillTeamDonationSolicitationResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setFulfillTeamDonationSolicitationResponseProto(FulfillTeamDonationSolicitationResponseProto fulfillTeamDonationSolicitationResponseProto) {
    this.fulfillTeamDonationSolicitationResponseProto = fulfillTeamDonationSolicitationResponseProto;
  }

}
