package com.lvl6.events;

import com.google.protobuf.GeneratedMessage;

public abstract class NormalResponseEvent<T extends GeneratedMessage> extends ResponseEvent<T> {
	protected String playerId;   //refers to whoever sent the event/triggered it

	public String getPlayerId() {
		return playerId;
	}

	public NormalResponseEvent(String playerId) {
		this.playerId = playerId;
	}
	

}
