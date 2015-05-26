package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class QueueUpResponseEvent extends NormalResponseEvent<QueueUpResponseProto> {

	private QueueUpResponseProto responseProto;

	public QueueUpResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_QUEUE_UP_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setQueueUpResponseProto(
			QueueUpResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public QueueUpResponseProto getQueueUpResponseProto() {   //because APNS required
		return responseProto;
	}
	

	public int eventSize() {
		return QueueUpResponseProto.getSerializedSize();
	}

}
