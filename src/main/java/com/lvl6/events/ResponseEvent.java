package com.lvl6.events;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

/**
 * GameEventDefault.java
 *
 * A basic GameEvent class, this can be extended for other Events or a
 * completely different class may be used as required by a specific game.
 */

public abstract class ResponseEvent<T extends GeneratedMessage> extends GameEvent {
	
	protected T responseProto;
	
	public T getResponseProto() {
		return responseProto;
	}

	public void setResponseProto(T responseProto) {
		this.responseProto = responseProto;
	}

	/** event type */
	protected EventProtocolResponse eventType;
	protected int tag;

	public EventProtocolResponse getEventType() {
		return eventType;
	}
	
	public abstract int eventSize();

	public abstract int write(ByteBuffer bb);

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	

	
	public byte[] getBytes(){
		return getResponseProto().toByteArray();
	}
	
	public ByteString getByteString(){
		return getResponseProto().toByteString();
	}


}
