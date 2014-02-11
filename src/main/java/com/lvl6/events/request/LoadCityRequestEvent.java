package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventCityProto.LoadCityRequestProto;

public class LoadCityRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private LoadCityRequestProto loadCityRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      loadCityRequestProto = LoadCityRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = loadCityRequestProto.getSender().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("load city request exception", e);
    }
  }

  public LoadCityRequestProto getLoadCityRequestProto() {
    return loadCityRequestProto;
  }
}
