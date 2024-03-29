package com.lvl6.events.request;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lvl6.events.PreDatabaseRequestEvent;
import com.lvl6.proto.EventUserProto.UserCreateRequestProto;

public class UserCreateRequestEvent extends PreDatabaseRequestEvent {

	private static Logger log = LoggerFactory.getLogger(UserCreateRequestEvent.class);

	private UserCreateRequestProto userCreateRequestProto;

	/**
	 * read the event from the given ByteBuffer to populate this event
	 */
	@Override
	public void read(ByteBuffer buff) {
		try {
			userCreateRequestProto = UserCreateRequestProto
					.parseFrom(ByteString.copyFrom(buff));

			// Player id is -1 since it won't be initialized yet. 
			playerId = "";

			udid = userCreateRequestProto.getUdid();
		} catch (InvalidProtocolBufferException e) {
			log.error("UserCreateRequest exception", e);
		}
	}

	public UserCreateRequestProto getUserCreateRequestProto() {
		return userCreateRequestProto;
	}

	public void setUserCreateRequestProto(UserCreateRequestProto ucrp) {
		this.userCreateRequestProto = ucrp;
	}

	@Override
	public String toString() {
		return "UserCreateRequestEvent [userCreateRequestProto="
				+ userCreateRequestProto + "]";
	}

}
