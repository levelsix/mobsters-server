package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto;

public class RedeemMiniEventRewardRequestEvent extends RequestEvent {

	private Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private RedeemMiniEventRewardRequestProto redeemMiniEventRewardRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			redeemMiniEventRewardRequestProto = RedeemMiniEventRewardRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = redeemMiniEventRewardRequestProto.getSender().getMinUserProto().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RedeemMiniEventReward request exception", e);
		}
	}

	public RedeemMiniEventRewardRequestProto getRedeemMiniEventRewardRequestProto() {
		return redeemMiniEventRewardRequestProto;
	}

	public void setRedeemMiniEventRewardRequestProto(
			RedeemMiniEventRewardRequestProto redeemMiniEventRewardRequestProto) {
		this.redeemMiniEventRewardRequestProto = redeemMiniEventRewardRequestProto;
	}

}
