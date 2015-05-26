package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class BeginDungeonResponseEvent extends NormalResponseEvent<BeginDungeonResponseProto> {

	private BeginDungeonResponseProto responseProto;

	public BeginDungeonResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_BEGIN_DUNGEON_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setBeginDungeonResponseProto(
			BeginDungeonResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public BeginDungeonResponseProto getBeginDungeonResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return beginDungeonResponseProto.getSerializedSize();
	}

}
