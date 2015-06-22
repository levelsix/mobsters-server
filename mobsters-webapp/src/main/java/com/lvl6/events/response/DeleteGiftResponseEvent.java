package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventRewardProto.DeleteGiftResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class DeleteGiftResponseEvent extends NormalResponseEvent<DeleteGiftResponseProto> {

	private DeleteGiftResponseProto deleteGiftResponseProto;

	public DeleteGiftResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_DELETE_GIFT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = deleteGiftResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
