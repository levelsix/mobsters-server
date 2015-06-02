package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RemoveMonsterFromBattleTeamResponseEvent extends	NormalResponseEvent<RemoveMonsterFromBattleTeamResponseProto> {

	

	public RemoveMonsterFromBattleTeamResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRemoveMonsterFromBattleTeamResponseProto(
			RemoveMonsterFromBattleTeamResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public RemoveMonsterFromBattleTeamResponseProto getRemoveMonsterFromBattleTeamResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
