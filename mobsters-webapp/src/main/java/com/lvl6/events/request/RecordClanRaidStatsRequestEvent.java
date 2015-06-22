package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsRequestProto;

public class RecordClanRaidStatsRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RecordClanRaidStatsRequestEvent.class);

	private RecordClanRaidStatsRequestProto recordClanRaidStatsRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			recordClanRaidStatsRequestProto = RecordClanRaidStatsRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = recordClanRaidStatsRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RecordClanRaidStatsRequest exception", e);
		}
	}

	public RecordClanRaidStatsRequestProto getRecordClanRaidStatsRequestProto() {
		return recordClanRaidStatsRequestProto;
	}

	@Override
	public String toString() {
		return "RecordClanRaidStatsRequestEvent [recordClanRaidStatsRequestProto="
				+ recordClanRaidStatsRequestProto + "]";
	}

}
