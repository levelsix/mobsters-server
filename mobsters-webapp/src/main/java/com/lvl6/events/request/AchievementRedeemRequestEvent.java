package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.RequestEvent;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemRequestProto;

public class AchievementRedeemRequestEvent extends RequestEvent {

	private static Logger log = LoggerFactory.getLogger(AchievementRedeemRequestEvent.class);

	private AchievementRedeemRequestProto achievementRedeemRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			achievementRedeemRequestProto = AchievementRedeemRequestProto
					.parseFrom(ByteString.copyFrom(buff));
			playerId = achievementRedeemRequestProto.getSender().getUserUuid();
		} catch (InvalidProtocolBufferException e) {
			log.error("AchievementRedeemRequest exception", e);
		}
	}

	public AchievementRedeemRequestProto getAchievementRedeemRequestProto() {
		return achievementRedeemRequestProto;
	}

	@Override
	public String toString() {
		return "AchievementRedeemRequestEvent [achievementRedeemRequestProto="
				+ achievementRedeemRequestProto + "]";
	}

}
