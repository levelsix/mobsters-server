package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventRewardProto.SendTangoGiftRequestProto;

public class SendTangoGiftRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(SendTangoGiftRequestEvent.class);

	private SendTangoGiftRequestProto sendTangoGiftRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			sendTangoGiftRequestProto = SendTangoGiftRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = sendTangoGiftRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("SendTangoGiftRequest exception", e);
		}
	}

	public SendTangoGiftRequestProto getSendTangoGiftRequestProto() {
		return sendTangoGiftRequestProto;
	}

	@Override
	public String toString() {
		return "SendTangoGiftRequestEvent [sendTangoGiftRequestProto="
				+ sendTangoGiftRequestProto + "]";
	}

}
