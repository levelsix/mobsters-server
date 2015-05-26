package com.lvl6.utils;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.events.GameEvent;
import com.lvl6.events.ResponseEvent;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public abstract class Wrap {
	// log4j logger

	private static final Logger log = LoggerFactory.getLogger(Wrap.class);

	public final void initWrap(int numWorkers) {

	}

	public void shutdown() {

	}

	@Trace(dispatcher = true)
	public void handleEvent(GameEvent event) {
		try {
			NewRelic.setTransactionName("EventController", getClass()
					.getSimpleName());
			StopWatch timer = new StopWatch();
			timer.start();
			processEvent(event);
			NewRelic.recordResponseTimeMetric(getClass().getSimpleName(),
					timer.getNanoTime());
		} catch (Exception e) {
			log.error("Error handling event: {}", event, e);
		}
	}

	public void handleClanEvent(ResponseEvent event, String clanId) {
		try {
			processClanResponseEvent(event, clanId);
		} catch (Exception e) {
			log.error("Error handling clan event: {}", event, e);
		}
	}

	protected abstract void processEvent(GameEvent event) throws Exception;

	public void processClanResponseEvent(ResponseEvent event, String clanId) {

	}
}
