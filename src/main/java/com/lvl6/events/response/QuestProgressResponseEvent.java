package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class QuestProgressResponseEvent extends NormalResponseEvent<QuestProgressResponseProto> {

	private QuestProgressResponseProto responseProto;

	public QuestProgressResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_QUEST_PROGRESS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setQuestProgressResponseProto(
			QuestProgressResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
