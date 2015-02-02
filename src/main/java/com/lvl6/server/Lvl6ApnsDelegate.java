package com.lvl6.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.DeliveryError;

public class Lvl6ApnsDelegate implements ApnsDelegate {

	
	private static final Logger log = LoggerFactory.getLogger(Lvl6ApnsDelegate.class);
	
	@Override
	public void cacheLengthExceeded(int arg0) {
		log.error("Notnoop cache length exceeded: {}", arg0);
	}

	@Override
	public void connectionClosed(DeliveryError arg0, int arg1) {
		log.error("Apns delivery error: {}, {}", arg1, arg0);
	}

	@Override
	public void messageSendFailed(ApnsNotification arg0, Throwable arg1) {
		log.error("Apns Message send failed: {}", arg0, arg1);
	}

	@Override
	public void messageSent(ApnsNotification arg0, boolean arg1) {
		log.info("Message sent: {} , message: {}", arg1, arg0);
	}

	@Override
	public void notificationsResent(int arg0) {
		log.info("Notifications resent: {}", arg0);
	}

}
