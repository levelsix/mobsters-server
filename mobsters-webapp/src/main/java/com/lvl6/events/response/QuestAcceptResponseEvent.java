package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventQuestProto.QuestAcceptResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class QuestAcceptResponseEvent extends NormalResponseEvent<QuestAcceptResponseProto> {

	

	public QuestAcceptResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_QUEST_ACCEPT_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setQuestAcceptResponseProto(
			QuestAcceptResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}