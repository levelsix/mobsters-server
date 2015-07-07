package com.lvl6.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.server.events.ApplicationMode;

public class MaintenanceModeEventHandler implements MessageListener {

	private static final Logger	log	= LoggerFactory.getLogger(MaintenanceModeEventHandler.class);

	@Autowired
	MessageConverter			converter;

	@Autowired
	ApplicationMode				appMode;

	public ApplicationMode getAppMode() {
		return appMode;
	}

	public void setAppMode(ApplicationMode appMode) {
		this.appMode = appMode;
	}

	@Override
	public void onMessage(Message message) {
		ApplicationMode mode = (ApplicationMode) converter.fromMessage(message);
		appMode.setMaintenanceMode(mode.isMaintenanceMode());
		appMode.setMessageForUsers(mode.getMessageForUsers());
		log.warn("Set Application maintainence mode: {} with message: {}", mode.isMaintenanceMode(), mode.getMessageForUsers());
	}

}
