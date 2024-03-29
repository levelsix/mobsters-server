package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureRequestProto;

public class UpgradeNormStructureRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(UpgradeNormStructureRequestEvent.class);

	private UpgradeNormStructureRequestProto upgradeNormStructureRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			upgradeNormStructureRequestProto = UpgradeNormStructureRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = upgradeNormStructureRequestProto.getSender()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UpgradeNormStructureRequest exception", e);
		}
	}

	public UpgradeNormStructureRequestProto getUpgradeNormStructureRequestProto() {
		return upgradeNormStructureRequestProto;
	}

	@Override
	public String toString() {
		return "UpgradeNormStructureRequestEvent [upgradeNormStructureRequestProto="
				+ upgradeNormStructureRequestProto + "]";
	}

}
