package com.lvl6.loadtesting;

import org.springframework.messaging.Message;

import com.lvl6.proto.UserProto.MinimumUserProto;

public interface LoadTestEventGenerator {
	public abstract Message<byte[]> userCreate(String udid);

	public abstract Message<byte[]> startup(String udid);

	//	public abstract Message<byte[]> userQuestDetails(MinimumUserProto.Builder user);
	public MinimumUserProto.Builder minimumUserProto(String userId);
}
