package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.DeleteClanGiftsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class DeleteClanGiftsResponseEvent extends NormalResponseEvent<DeleteClanGiftsResponseProto> {

	

	public DeleteClanGiftsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_DELETE_CLAN_GIFTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setDeleteClanGiftsResponseProto(
			DeleteClanGiftsResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}