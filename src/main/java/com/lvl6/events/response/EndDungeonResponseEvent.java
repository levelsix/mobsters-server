package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndDungeonResponseEvent extends NormalResponseEvent<EndDungeonResponseProto> {

	private EndDungeonResponseProto responseProto;

	public EndDungeonResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_END_DUNGEON_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setEndDungeonResponseProto(
			EndDungeonResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public EndDungeonResponseProto getEndDungeonResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return endDungeonResponseProto.getSerializedSize();
	}

}
