package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventQuestProto.QuestProgressRequestProto;

public class QuestProgressRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(QuestProgressRequestEvent.class);

	private QuestProgressRequestProto questProgressRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			questProgressRequestProto = QuestProgressRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = questProgressRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("QuestProgressRequest exception", e);
		}
	}

	public QuestProgressRequestProto getQuestProgressRequestProto() {
		return questProgressRequestProto;
	}

	public void setQuestProgressRequestProto(
			QuestProgressRequestProto questProgressRequestProto) {
		this.questProgressRequestProto = questProgressRequestProto;
	}

	@Override
	public String toString() {
		return "QuestProgressRequestEvent [questProgressRequestProto="
				+ questProgressRequestProto + "]";
	}

}
