package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AcceptOrRejectClanInviteResponseEvent extends NormalResponseEvent<AcceptOrRejectClanInviteResponseProto> {

	//

	public AcceptOrRejectClanInviteResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ACCEPT_OR_REJECT_CLAN_INVITE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
