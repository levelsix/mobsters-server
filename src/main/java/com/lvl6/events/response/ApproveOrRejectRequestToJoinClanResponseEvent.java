package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class ApproveOrRejectRequestToJoinClanResponseEvent extends NormalResponseEvent<ApproveOrRejectRequestToJoinClanResponseProto> {

	private ApproveOrRejectRequestToJoinClanResponseProto responseProto;

	public ApproveOrRejectRequestToJoinClanResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto
				.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setApproveOrRejectRequestToJoinClanResponseProto(
			ApproveOrRejectRequestToJoinClanResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
