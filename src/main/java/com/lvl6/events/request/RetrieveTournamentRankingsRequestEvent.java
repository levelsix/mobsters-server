package com.lvl6.events.request;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsRequestProto;

public class RetrieveTournamentRankingsRequestEvent extends RequestEvent{
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
      e.printStackTrace();
    }
  }

  public RetrieveTournamentRankingsRequestProto getRetrieveTournamentRankingsRequestProto() {
    return retrieveTournamentRankingsRequestProto;
  }
  
}//RetrieveTournamentRankingsRequestProto