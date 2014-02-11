package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureRequestProto;

public class UpgradeNormStructureRequestEvent extends RequestEvent {
	
	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private UpgradeNormStructureRequestProto upgradeNormStructureRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      upgradeNormStructureRequestProto = UpgradeNormStructureRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = upgradeNormStructureRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("upgrade norm structure request exception", e);
    }
  }

  public UpgradeNormStructureRequestProto getUpgradeNormStructureRequestProto() {
    return upgradeNormStructureRequestProto;
  }
}
