package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.PreDatabaseResponseEvent;
import com.lvl6.proto.EventUserProto.UserCreateResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class UserCreateResponseEvent extends PreDatabaseResponseEvent<UserCreateResponseProto> {

	private UserCreateResponseProto responseProto;

	public UserCreateResponseEvent(String udid) {
		super(udid);
		eventType = EventProtocolResponse.S_USER_CREATE_EVENT;
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

	public void setUserCreateResponseProto(
			UserCreateResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
