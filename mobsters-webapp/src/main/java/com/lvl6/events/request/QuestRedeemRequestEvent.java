package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventQuestProto.QuestRedeemRequestProto;

public class QuestRedeemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(QuestRedeemRequestEvent.class);

	private QuestRedeemRequestProto questRedeemRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			questRedeemRequestProto = QuestRedeemRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = questRedeemRequestProto.getSender().getMinUserProto()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("QuestRedeemRequest exception", e);
		}
	}

	public QuestRedeemRequestProto getQuestRedeemRequestProto() {
		return questRedeemRequestProto;
	}

	@Override
	public String toString() {
		return "QuestRedeemRequestEvent [questRedeemRequestProto="
				+ questRedeemRequestProto + "]";
	}

}
