package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class EndClanHelpResponseEvent extends NormalResponseEvent<EndClanHelpResponseProto> {



	public EndClanHelpResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_END_CLAN_HELP_EVENT;
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
