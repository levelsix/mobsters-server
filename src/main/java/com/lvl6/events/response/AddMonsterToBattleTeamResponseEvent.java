package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AddMonsterToBattleTeamResponseEvent extends NormalResponseEvent<AddMonsterToBattleTeamResponseProto> {

	

	public AddMonsterToBattleTeamResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setAddMonsterToBattleTeamResponseProto(
			AddMonsterToBattleTeamResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public AddMonsterToBattleTeamResponseProto getAddMonsterToBattleTeamResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
