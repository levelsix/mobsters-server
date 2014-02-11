package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonRequestProto;

public class ReviveInDungeonRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private ReviveInDungeonRequestProto reviveInDungeonRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      reviveInDungeonRequestProto = ReviveInDungeonRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = reviveInDungeonRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("revive in dungeon request exception", e);
    }
  }

  public ReviveInDungeonRequestProto getReviveInDungeonRequestProto() {
    return reviveInDungeonRequestProto;
  }
}
