package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PromoteDemoteClanMemberResponseEvent extends NormalResponseEvent<PromoteDemoteClanMemberResponseProto> {

	private PromoteDemoteClanMemberResponseProto responseProto;

	public PromoteDemoteClanMemberResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PROMOTE_DEMOTE_CLAN_MEMBER_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setPromoteDemoteClanMemberResponseProto(
			PromoteDemoteClanMemberResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
