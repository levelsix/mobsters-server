package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ReviveInDungeonResponseEvent extends NormalResponseEvent<ReviveInDungeonResponseProto> {

	private ReviveInDungeonResponseProto responseProto;

	public ReviveInDungeonResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REVIVE_IN_DUNGEON_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setReviveInDungeonResponseProto(
			ReviveInDungeonResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public ReviveInDungeonResponseProto getReviveInDungeonResponseProto() {   //because APNS required
		return responseProto;
	}

}
