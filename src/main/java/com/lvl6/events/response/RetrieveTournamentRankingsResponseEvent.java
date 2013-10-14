package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventTournamentProto.RetrieveTournamentRankingsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveTournamentRankingsResponseEvent extends NormalResponseEvent {

  private RetrieveTournamentRankingsResponseProto retrieveTournamentRankingsResponseProto;
  
  public RetrieveTournamentRankingsResponseEvent(int playerId) {
    super(playerId);
    eventType = EventProtocolResponse.S_RETRIEVE_TOURNAMENT_RANKINGS_EVENT;
  }

  /** 
   * write the event to the given ByteBuffer
   * 
   * note we are using 1.4 ByteBuffers for both client and server
   * depending on the deployment you may need to support older java
   * versions on the client and use old-style socket input/output streams
   */
  public int write(ByteBuffer buff) {
    ByteString b = retrieveTournamentRankingsResponseProto.toByteString();
    b.copyTo(buff);
    return b.size();
  }

  public void setRetrieveTournamentRankingsResponseProto(
      RetrieveTournamentRankingsResponseProto retrieveTournamentRankingsResponseProto) {
    this.retrieveTournamentRankingsResponseProto = retrieveTournamentRankingsResponseProto;
  }
  
}
