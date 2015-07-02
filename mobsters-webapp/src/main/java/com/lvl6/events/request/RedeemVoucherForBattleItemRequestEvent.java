package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventItemProto.RedeemVoucherForBattleItemRequestProto;

public class RedeemVoucherForBattleItemRequestEvent extends RequestEvent {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemVoucherForBattleItemRequestEvent.class);

	private RedeemVoucherForBattleItemRequestProto redeemVoucherForBattleItemRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			redeemVoucherForBattleItemRequestProto = RedeemVoucherForBattleItemRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = redeemVoucherForBattleItemRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("RedeemVoucherForBattleItem request exception", e);
		}
	}

	public RedeemVoucherForBattleItemRequestProto getRedeemVoucherForBattleItemRequestProto() {
		return redeemVoucherForBattleItemRequestProto;
	}

	public void setRedeemVoucherForBattleItemRequestProto(
			RedeemVoucherForBattleItemRequestProto redeemVoucherForBattleItemRequestProto) {
		this.redeemVoucherForBattleItemRequestProto = redeemVoucherForBattleItemRequestProto;
	}

}
