package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AcceptAndRejectFbInviteForSlotsResponseEvent extends NormalResponseEvent<AcceptAndRejectFbInviteForSlotsResponseProto> {



	public AcceptAndRejectFbInviteForSlotsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto
				.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public AcceptAndRejectFbInviteForSlotsResponseProto getAcceptAndRejectFbInviteForSlotsResponseProto() {   //because APNS required
		return responseProto;
	}

}
