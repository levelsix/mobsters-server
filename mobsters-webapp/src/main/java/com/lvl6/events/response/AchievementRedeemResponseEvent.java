package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventAchievementProto.AchievementRedeemResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AchievementRedeemResponseEvent extends NormalResponseEvent<AchievementRedeemResponseProto> {



	public AchievementRedeemResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ACHIEVEMENT_REDEEM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}