package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RemoveUserItemUsedResponseEvent extends NormalResponseEvent<RemoveUserItemUsedResponseProto> {

	private RemoveUserItemUsedResponseProto removeUserItemUsedResponseProto;

	public RemoveUserItemUsedResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_REMOVE_USER_ITEM_USED_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b = removeUserItemUsedResponseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRemoveUserItemUsedResponseProto(
			RemoveUserItemUsedResponseProto removeUserItemUsedResponseProto) {
		this.removeUserItemUsedResponseProto = removeUserItemUsedResponseProto;
	}

}
