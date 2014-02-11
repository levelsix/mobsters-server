package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamRequestProto;

public class AddMonsterToBattleTeamRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private AddMonsterToBattleTeamRequestProto addMonsterToBattleTeamRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      addMonsterToBattleTeamRequestProto = AddMonsterToBattleTeamRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = addMonsterToBattleTeamRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("add monster to battle team request exception", e);
    }
  }

  public AddMonsterToBattleTeamRequestProto getAddMonsterToBattleTeamRequestProto() {
    return addMonsterToBattleTeamRequestProto;
  }
}
