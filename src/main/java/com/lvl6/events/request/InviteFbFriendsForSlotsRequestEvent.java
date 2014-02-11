package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;

public class InviteFbFriendsForSlotsRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private InviteFbFriendsForSlotsRequestProto inviteFbFriendsForSlotsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      inviteFbFriendsForSlotsRequestProto = InviteFbFriendsForSlotsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = inviteFbFriendsForSlotsRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("invite fb friends for slots request exception", e);
    }
  }

  public InviteFbFriendsForSlotsRequestProto getInviteFbFriendsForSlotsRequestProto() {
    return inviteFbFriendsForSlotsRequestProto;
  }
}
