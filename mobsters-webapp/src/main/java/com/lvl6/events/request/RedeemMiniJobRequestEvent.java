package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobRequestProto;

public class RedeemMiniJobRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(RedeemMiniJobRequestEvent.class);

	private RedeemMiniJobRequestProto redeemMiniJobRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			redeemMiniJobRequestProto = RedeemMiniJobRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = redeemMiniJobRequestProto.getSender().getMinUserProto()
					.getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RedeemMiniJobRequest exception", e);
		}
	}

	public RedeemMiniJobRequestProto getRedeemMiniJobRequestProto() {
		return redeemMiniJobRequestProto;
	}

	//added for testing purposes
	public void setRedeemMiniJobRequestProto(RedeemMiniJobRequestProto sorp) {
		this.redeemMiniJobRequestProto = sorp;
	}

	@Override
	public String toString() {
		return "RedeemMiniJobRequestEvent [redeemMiniJobRequestProto="
				+ redeemMiniJobRequestProto + "]";
	}

}
