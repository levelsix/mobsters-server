package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsRequestProto;

public class FinishNormStructWaittimeWithDiamondsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private FinishNormStructWaittimeWithDiamondsRequestProto finishNormStructWaittimeWithDiamondsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      finishNormStructWaittimeWithDiamondsRequestProto = FinishNormStructWaittimeWithDiamondsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = finishNormStructWaittimeWithDiamondsRequestProto.getSender().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("FinishNormStructWaittimeWithDiamondsRequest exception", e);
    }
  }

  public FinishNormStructWaittimeWithDiamondsRequestProto getFinishNormStructWaittimeWithDiamondsRequestProto() {
    return finishNormStructWaittimeWithDiamondsRequestProto;
  }

  @Override
  public String toString()
  {
	  return "FinishNormStructWaittimeWithDiamondsRequestEvent [finishNormStructWaittimeWithDiamondsRequestProto="
		  + finishNormStructWaittimeWithDiamondsRequestProto
		  + "]";
  }
  
}
