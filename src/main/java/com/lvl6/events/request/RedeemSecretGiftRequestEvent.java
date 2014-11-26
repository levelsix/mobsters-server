package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.RedeemSecretGiftRequestProto;

public class RedeemSecretGiftRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
  private RedeemSecretGiftRequestProto redeemSecretGiftRequestProto;
  
  /**
   * read the event from the given ByteBuffer to populate this event
   */
  public void read(ByteBuffer buff) {
    try {
      redeemSecretGiftRequestProto = RedeemSecretGiftRequestProto.parseFrom(ByteString.copyFrom(buff));
      playerId = redeemSecretGiftRequestProto.getMup().getUserUuid();
    } catch (InvalidProtocolBufferException e) {
      log.error("RedeemSecretGift request exception", e);
    }
  }

  public RedeemSecretGiftRequestProto getRedeemSecretGiftRequestProto() {
    return redeemSecretGiftRequestProto;
  }

  public void setRedeemSecretGiftRequestProto( RedeemSecretGiftRequestProto redeemSecretGiftRequestProto )
  {
	  this.redeemSecretGiftRequestProto = redeemSecretGiftRequestProto;
  }

}
