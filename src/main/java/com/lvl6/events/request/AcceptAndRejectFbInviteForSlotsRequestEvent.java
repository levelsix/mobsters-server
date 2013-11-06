package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;

public class AcceptAndRejectFbInviteForSlotsRequestEvent extends RequestEvent {

  private AcceptAndRejectFbInviteForSlotsRequestProto acceptAndRejectFbInviteForSlotsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      acceptAndRejectFbInviteForSlotsRequestProto = AcceptAndRejectFbInviteForSlotsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = acceptAndRejectFbInviteForSlotsRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
  }

  public AcceptAndRejectFbInviteForSlotsRequestProto getAcceptAndRejectFbInviteForSlotsRequestProto() {
    return acceptAndRejectFbInviteForSlotsRequestProto;
  }
}
