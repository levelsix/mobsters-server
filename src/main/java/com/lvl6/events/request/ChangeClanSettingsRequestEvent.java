package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.ChangeClanSettingsRequestProto;

public class ChangeClanSettingsRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private ChangeClanSettingsRequestProto changeClanSettingsRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      changeClanSettingsRequestProto = ChangeClanSettingsRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = changeClanSettingsRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("change clan settings request exception");
    }
  }

  public ChangeClanSettingsRequestProto getChangeClanSettingsRequestProto() {
    return changeClanSettingsRequestProto;
  }
}
