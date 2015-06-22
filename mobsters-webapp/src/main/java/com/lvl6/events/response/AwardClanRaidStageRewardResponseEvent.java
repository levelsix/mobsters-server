package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.AwardClanRaidStageRewardResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AwardClanRaidStageRewardResponseEvent extends NormalResponseEvent<AwardClanRaidStageRewardResponseProto> {



	public AwardClanRaidStageRewardResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_AWARD_CLAN_RAID_STAGE_REWARD_EVENT;
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
