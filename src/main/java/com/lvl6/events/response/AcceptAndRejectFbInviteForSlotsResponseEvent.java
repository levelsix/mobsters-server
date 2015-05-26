package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AcceptAndRejectFbInviteForSlotsResponseEvent extends NormalResponseEvent<AcceptAndRejectFbInviteForSlotsResponseProto> {

	private AcceptAndRejectFbInviteForSlotsResponseProto responseProto;

	public AcceptAndRejectFbInviteForSlotsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
	}
	
	public int eventSize() {
		return acceptAndRejectFbInviteForSlotsResponseProto.getSerializedSize();
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto
				.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setAcceptAndRejectFbInviteForSlotsResponseProto(
			AcceptAndRejectFbInviteForSlotsResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public AcceptAndRejectFbInviteForSlotsResponseProto getAcceptAndRejectFbInviteForSlotsResponseProto() {   //because APNS required
		return responseProto;
	}

}
