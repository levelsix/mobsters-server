package com.lvl6.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.events.GameEvent;
import com.lvl6.events.ResponseEvent;
import com.lvl6.utils.Wrap;

public abstract class EventWriterOld extends Wrap {

	private static Logger log = LoggerFactory.getLogger(EventWriterOld.class);

	public EventWriterOld() {
		super();
	}

	public abstract void processGlobalChatResponseEvent(ResponseEvent event);

	@Override
	public void handleClanEvent(ResponseEvent event, String clanId) {
		try {
			processClanResponseEvent(event, clanId);
		} catch (Exception e) {
			log.error("Error handling clan event: " + event, e);
		}
	}

	@Override
	public abstract void processClanResponseEvent(ResponseEvent event,
			String clanId);

	@Override
	public void handleEvent(GameEvent event) {
		try {
			processEvent(event);
		} catch (Exception e) {
			log.error("Error handling event: {}", event, e);
		}
	}

	@Override
	protected abstract void processEvent(GameEvent event) throws Exception;

	public abstract void processPreDBResponseEvent(ResponseEvent event,
			String udid);

	public abstract void processPreDBFacebookEvent(ResponseEvent event,
			String fbId);
	//public abstract void sendAdminMessage(String message);

}