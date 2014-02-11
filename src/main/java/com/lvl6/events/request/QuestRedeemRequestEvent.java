package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventQuestProto.QuestRedeemRequestProto;

public class QuestRedeemRequestEvent extends RequestEvent {
	
  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private QuestRedeemRequestProto questRedeemRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      questRedeemRequestProto = QuestRedeemRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = questRedeemRequestProto.getSender().getMinUserProto().getUserId();
    } catch (InvalidProtocolBufferException e) {
      log.error("quest redeem request exception", e);
    }
  }

  public QuestRedeemRequestProto getQuestRedeemRequestProto() {
    return questRedeemRequestProto;
  }
}
