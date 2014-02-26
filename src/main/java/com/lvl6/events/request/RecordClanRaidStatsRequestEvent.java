package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsRequestProto;

public class RecordClanRaidStatsRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RecordClanRaidStatsRequestProto recordClanRaidStatsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      recordClanRaidStatsRequestProto = RecordClanRaidStatsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = recordClanRaidStatsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("boot player from clan request exception", e);
    }
  }

  public RecordClanRaidStatsRequestProto getRecordClanRaidStatsRequestProto() {
    return recordClanRaidStatsRequestProto;
  }
}
