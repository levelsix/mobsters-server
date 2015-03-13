package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.InviteToClanResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class InviteToClanResponseEvent extends NormalResponseEvent {

	private InviteToClanResponseProto inviteToClanResponseProto;

	public InviteToClanResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_INVITE_TO_CLAN_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = inviteToClanResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setInviteToClanResponseProto(
			InviteToClanResponseProto inviteToClanResponseProto) {
		this.inviteToClanResponseProto = inviteToClanResponseProto;
	}

}
