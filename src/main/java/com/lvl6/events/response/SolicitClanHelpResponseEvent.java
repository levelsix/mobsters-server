package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class SolicitClanHelpResponseEvent extends NormalResponseEvent<SolicitClanHelpResponseProto> {

	private SolicitClanHelpResponseProto responseProto;

	public SolicitClanHelpResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_SOLICIT_CLAN_HELP_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setSolicitClanHelpResponseProto(
			SolicitClanHelpResponseProto responseProto) {
		this.responseProto = responseProto;
	}

}
