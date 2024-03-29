package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedRequestProto;

public class EvolutionFinishedRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(EvolutionFinishedRequestEvent.class);

	private EvolutionFinishedRequestProto evolutionFinishedRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			evolutionFinishedRequestProto = EvolutionFinishedRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = evolutionFinishedRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("EvolutionFinishedRequest exception", e);
		}
	}

	public EvolutionFinishedRequestProto getEvolutionFinishedRequestProto() {
		return evolutionFinishedRequestProto;
	}

	public void setEvolutionFinishedRequestProto(
			EvolutionFinishedRequestProto efrp) {
		this.evolutionFinishedRequestProto = efrp;
	}

	@Override
	public String toString() {
		return "EvolutionFinishedRequestEvent [evolutionFinishedRequestProto="
				+ evolutionFinishedRequestProto + "]";
	}

}
