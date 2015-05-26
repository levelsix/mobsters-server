package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class InviteFbFriendsForSlotsResponseEvent extends NormalResponseEvent<InviteFbFriendsForSlotsResponseProto> {

	private InviteFbFriendsForSlotsResponseProto responseProto;

	public InviteFbFriendsForSlotsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setInviteFbFriendsForSlotsResponseProto(
			InviteFbFriendsForSlotsResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public InviteFbFriendsForSlotsResponseProto getInviteFbFriendsForSlotsResponseProto() {   //because APNS required
		return responseProto;
	}
	
	public int eventSize() {
		return inviteFbFriendsForSlotsResponseProto.getSerializedSize();
	}

}
