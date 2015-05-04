package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AttackClanRaidMonsterResponseEvent extends NormalResponseEvent<AttackClanRaidMonsterResponseProto> {

	private AttackClanRaidMonsterResponseProto responseProto;

	public AttackClanRaidMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ATTACK_CLAN_RAID_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setAttackClanRaidMonsterResponseProto(
			AttackClanRaidMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
