package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamRequestProto;

public class RetrieveUserMonsterTeamRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RetrieveUserMonsterTeamRequestProto retrieveUserMonsterTeamRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      retrieveUserMonsterTeamRequestProto = RetrieveUserMonsterTeamRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retrieveUserMonsterTeamRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("RetrieveUserMonsterTeam request exception", e);
    }
  }

  public RetrieveUserMonsterTeamRequestProto getRetrieveUserMonsterTeamRequestProto() {
    return retrieveUserMonsterTeamRequestProto;
  }

  public void setRetrieveUserMonsterTeamRequestProto( RetrieveUserMonsterTeamRequestProto retrieveUserMonsterTeamRequestProto )
  {
	  this.retrieveUserMonsterTeamRequestProto = retrieveUserMonsterTeamRequestProto;
  }

}
