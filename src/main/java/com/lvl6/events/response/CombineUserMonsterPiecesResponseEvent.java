package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CombineUserMonsterPiecesResponseEvent extends NormalResponseEvent<CombineUserMonsterPiecesResponseProto> {

	private CombineUserMonsterPiecesResponseProto responseProto;

	public CombineUserMonsterPiecesResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_COMBINE_USER_MONSTER_PIECES_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setCombineUserMonsterPiecesResponseProto(
			CombineUserMonsterPiecesResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public CombineUserMonsterPiecesResponseProto getCombineUserMonsterPiecesResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return combineMonsterPiecesResponseProto.getSerializedSize();
	}

}
