package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;

public class RetrieveCurrencyFromNormStructureRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RetrieveCurrencyFromNormStructureRequestEvent.class);

	private RetrieveCurrencyFromNormStructureRequestProto retrieveCurrencyFromNormStructureRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			retrieveCurrencyFromNormStructureRequestProto = RetrieveCurrencyFromNormStructureRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = retrieveCurrencyFromNormStructureRequestProto
					.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RetrieveCurrencyFromNormStructureRequest exception", e);
		}
	}

	public RetrieveCurrencyFromNormStructureRequestProto getRetrieveCurrencyFromNormStructureRequestProto() {
		return retrieveCurrencyFromNormStructureRequestProto;
	}

	@Override
	public String toString() {
		return "RetrieveCurrencyFromNormStructureRequestEvent [retrieveCurrencyFromNormStructureRequestProto="
				+ retrieveCurrencyFromNormStructureRequestProto + "]";
	}

}//RetrieveCurrencyFromNormStructureRequestProto
