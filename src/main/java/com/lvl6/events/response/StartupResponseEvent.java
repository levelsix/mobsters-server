package com.lvl6.events.response;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.lvl6.events.PreDatabaseResponseEvent;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class StartupResponseEvent extends PreDatabaseResponseEvent<StartupResponseProto> {

	
	private static final Logger log = LoggerFactory.getLogger(StartupResponseEvent.class);
	
	private StartupResponseProto	responseProto;

	public StartupResponseEvent(String udid) {
		super(udid);
		eventType = EventProtocolResponse.S_STARTUP_EVENT;
	}


	@Override
	public int write(ByteBuffer buff) {
		ByteString b = responseProto.toByteString();
		b.copyTo(buff);
		return b.size();
	}

	public void setStartupResponseProto(StartupResponseProto rProto) {
		//log.info("Setting response proto: {} this: {}", rProto, this);
		this.responseProto = rProto;
	}

	public int eventSize() {
		//log.info("Getting size of proto: {} this: {}", this);
		return responseProto.getSerializedSize();
	}

}
