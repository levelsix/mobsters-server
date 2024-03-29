package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventPvpProto.QueueUpRequestProto;

public class QueueUpRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(QueueUpRequestEvent.class);

	private QueueUpRequestProto queueUpRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			queueUpRequestProto = QueueUpRequestProto.parseFrom(ByteString
					.copyFrom(buff));
			playerId = queueUpRequestProto.getAttacker().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("QueueUpRequest exception", e);
		}
	}

	public QueueUpRequestProto getQueueUpRequestProto() {
		return queueUpRequestProto;
	}

	public void setQueueUpRequestProto(QueueUpRequestProto queueUpRequestProto) {
		this.queueUpRequestProto = queueUpRequestProto;
	}

	@Override
	public String toString() {
		return "QueueUpRequestEvent [queueUpRequestProto="
				+ queueUpRequestProto + "]";
	}

}//QueueUpRequestProto
