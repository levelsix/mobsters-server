package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsRequestProto;

public class RetrieveUsersForUserIdsRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private RetrieveUsersForUserIdsRequestProto retrieveUsersForUserIdsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      retrieveUsersForUserIdsRequestProto = RetrieveUsersForUserIdsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retrieveUsersForUserIdsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("retrieve users for user ids request exception", e);
    }
  }

  public RetrieveUsersForUserIdsRequestProto getRetrieveUsersForUserIdsRequestProto() {
    return retrieveUsersForUserIdsRequestProto;
  }
}
