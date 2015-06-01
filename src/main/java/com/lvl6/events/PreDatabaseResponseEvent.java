package com.lvl6.events;

import com.google.protobuf.GeneratedMessage;

public abstract class PreDatabaseResponseEvent<T extends GeneratedMessage> extends ResponseEvent<T> {
	protected String udid;

	public PreDatabaseResponseEvent(String udid) {
		this.udid = udid;
	}

	public String getUdid() {
		return udid;
	}
}
