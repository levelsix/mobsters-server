package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EvolveMonsterRequestProto;

public class EvolveMonsterRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private EvolveMonsterRequestProto evolveMonsterRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      evolveMonsterRequestProto = EvolveMonsterRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = evolveMonsterRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("EvolveMonsterRequest exception", e);
    }
  }

  public EvolveMonsterRequestProto getEvolveMonsterRequestProto() {
    return evolveMonsterRequestProto;
  }
  
  public void setEvolveMonsterRequestProto(EvolveMonsterRequestProto emrp) {
	  this.evolveMonsterRequestProto = emrp;
  }

  @Override
  public String toString()
  {
	  return "EvolveMonsterRequestEvent [evolveMonsterRequestProto="
		  + evolveMonsterRequestProto
		  + "]";
  }

}
