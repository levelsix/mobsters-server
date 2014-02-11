package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsRequestProto;

public class RetrieveTournamentRankingsRequestEvent extends RequestEvent{
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RetrieveTournamentRankingsRequestProto retrieveTournamentRankingsRequestProto;
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  @Override
  public void read(ByteBuffer buff) {
    try {
      retrieveTournamentRankingsRequestProto = RetrieveTournamentRankingsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = retrieveTournamentRankingsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("retrieve tournament rankings request exception", e);
    }
  }

  public RetrieveTournamentRankingsRequestProto getRetrieveTournamentRankingsRequestProto() {
    return retrieveTournamentRankingsRequestProto;
  }
  
}//RetrieveTournamentRankingsRequestProto