package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetAvatarMonsterResponseEvent extends NormalResponseEvent<SetAvatarMonsterResponseProto> {

	private SetAvatarMonsterResponseProto responseProto;

	public SetAvatarMonsterResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SET_AVATAR_MONSTER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSetAvatarMonsterResponseProto(
			SetAvatarMonsterResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
