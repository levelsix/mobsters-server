package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterRequestProto;

public class UnrestrictUserMonsterRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(UnrestrictUserMonsterRequestEvent.class);

	private UnrestrictUserMonsterRequestProto unrestrictUserMonsterRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			unrestrictUserMonsterRequestProto = UnrestrictUserMonsterRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = unrestrictUserMonsterRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UnrestrictUserMonsterRequest exception", e);
		}
	}

	public UnrestrictUserMonsterRequestProto getUnrestrictUserMonsterRequestProto() {
		return unrestrictUserMonsterRequestProto;
	}

	@Override
	public String toString() {
		return "UnrestrictUserMonsterRequestEvent [unrestrictUserMonsterRequestProto="
				+ unrestrictUserMonsterRequestProto + "]";
	}

}
