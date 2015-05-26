package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveUserMonsterTeamResponseEvent extends NormalResponseEvent<RetrieveUserMonsterTeamResponseProto> {

	private RetrieveUserMonsterTeamResponseProto responseProto;

	public RetrieveUserMonsterTeamResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_USER_MONSTER_TEAM_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveUserMonsterTeamResponseProto(
			RetrieveUserMonsterTeamResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
