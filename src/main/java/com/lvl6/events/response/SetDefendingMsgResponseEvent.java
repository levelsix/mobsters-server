package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SetDefendingMsgResponseEvent extends NormalResponseEvent<SetDefendingMsgResponseProto> {

	private SetDefendingMsgResponseProto responseProto;

	public SetDefendingMsgResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SET_DEFENDING_MSG_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSetDefendingMsgResponseProto(
			SetDefendingMsgResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
