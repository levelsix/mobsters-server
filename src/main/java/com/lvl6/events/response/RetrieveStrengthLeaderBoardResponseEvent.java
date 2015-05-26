package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RetrieveStrengthLeaderBoardResponseEvent extends NormalResponseEvent {

	private RetrieveStrengthLeaderBoardResponseProto retrieveStrengthLeaderBoardResponseProto;

	public RetrieveStrengthLeaderBoardResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RETRIEVE_STRENGTH_LEADER_BOARD_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = retrieveStrengthLeaderBoardResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRetrieveStrengthLeaderBoardResponseProto(
			RetrieveStrengthLeaderBoardResponseProto retrieveStrengthLeaderBoardResponseProto) {
		this.retrieveStrengthLeaderBoardResponseProto = retrieveStrengthLeaderBoardResponseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
