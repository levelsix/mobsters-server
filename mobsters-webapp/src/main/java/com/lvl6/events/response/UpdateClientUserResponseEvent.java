package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UpdateClientUserResponseEvent extends NormalResponseEvent<UpdateClientUserResponseProto> {



	public UpdateClientUserResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_UPDATE_CLIENT_USER_EVENT;
	}

	/**
	 * write the event to the given ByteBuffer
	 *
	 * note we are using 1.4 ByteBuffers for both client and server depending on
	 * the deployment you may need to support older java versions on the client
	 * and use old-style socket input/output streams
	 */
	@Override
	public int write(ByteBuffer buff) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(buff);
		return b.size();
	}

	@Override
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
