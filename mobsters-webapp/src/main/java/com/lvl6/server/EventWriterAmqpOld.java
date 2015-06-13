package com.lvl6.server;

import java.nio.ByteBuffer;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.BroadcastResponseEvent;
import com.lvl6.events.GameEvent;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.events.ResponseEvent;
import com.lvl6.properties.Globals;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.utils.NIOUtils;

//@Component
public class EventWriterAmqpOld extends EventWriterOld {

	@Resource(name = "chatMessagesTemplate")
	RabbitTemplate chatTemplate;

	public RabbitTemplate getChatTemplate() {
		return chatTemplate;
	}

	public void setChatTemplate(RabbitTemplate chatTemplate) {
		this.chatTemplate = chatTemplate;
	}

	@Resource(name = "clientMessagesTemplate")
	RabbitTemplate clientsTemplate;

	public RabbitTemplate getClientsTemplate() {
		return clientsTemplate;
	}

	public void setClientsTemplate(RabbitTemplate clientsTemplate) {
		this.clientsTemplate = clientsTemplate;
	}

	@Autowired
	UserClanRetrieveUtils2 userClanRetrieveUtil;

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(
			UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(EventWriterAmqpOld.class);

	@Override
	protected void processEvent(GameEvent event) {
		if (event instanceof ResponseEvent)
			processResponseEvent((ResponseEvent) event);

	}

	@Override
	public void processPreDBFacebookEvent(ResponseEvent event, String facebookId) {
		//log.info("writer received predb fb event=\n"+event.toString());
		byte[] buff = getByteArray(event);
		MessageProperties msgProps = getProps();
		String routingKey = "client_facebookid_" + facebookId;
		log.debug("writing predb event with type=" + event.getEventType()
				+ " to player with routingKey " + routingKey + ", event="
				+ event);
		sendMessageToPlayer(buff, msgProps, routingKey);
	}

	@Override
	public void processPreDBResponseEvent(ResponseEvent event, String udid) {
		//log.info("writer received predb event=\n"+event.toString());
		byte[] buff = getByteArray(event);
		MessageProperties msgProps = getProps();
		String routingKey = "client_udid_" + udid;
		log.debug("writing predb event with type=" + event.getEventType()
				+ " to player with routingKey " + routingKey + ", event="
				+ event);
		sendMessageToPlayer(buff, msgProps, routingKey);
	}

	/**
	 * our own version of processEvent that takes the additional parameter of
	 * the writeBuffer
	 */
	public void processResponseEvent(ResponseEvent event) {
		//log.debug("writer received event=\n" + event.toString());
		byte[] buff = getByteArray(event);
		MessageProperties msgProps = getProps();
		if (BroadcastResponseEvent.class.isInstance(event)) {
			int[] recipients = ((BroadcastResponseEvent) event).getRecipients();
			for (int i = 0; i < recipients.length; i++) {
				if (recipients[i] > 0) {
					String routingKey = "client_userid_" + recipients[i];
					log.debug("writing broadcast event with type="
							+ event.getEventType()
							+ " to players with routingKey " + routingKey);
					sendMessageToPlayer(buff, msgProps, routingKey);
				}
			}
		}
		// Otherwise this is just a normal message, send response to sender.
		else {
			String playerId = ((NormalResponseEvent) event).getPlayerId();
			String routingKey = getRoutingKeyForSingleUser(playerId);
			log.debug("writing normal event with type=" + event.getEventType()
					+ " to player with routingKey " + routingKey + " event="
					+ event.getClass().getSimpleName());
			sendMessageToPlayer(buff, msgProps, routingKey);
		}
	}

	public String getRoutingKeyForSingleUser(String playerId) {
		return "client_userid_" + playerId;
	}

	protected MessageProperties getProps() {
		MessageProperties msgProps = new MessageProperties();
		//msgProps.setHeader("x-message-ttl", 360000);
		msgProps.setExpiration("120000");
		return msgProps;
	}

	protected void sendMessageToPlayer(byte[] buff, MessageProperties msgProps,
			String routingKey) {
		clientsTemplate.send(routingKey, new Message(buff, msgProps));
	}

	@Override
	public void processClanResponseEvent(ResponseEvent event, String clanId) {
		MessageProperties msgProps = getProps();
		String clanIdString = "clan_" + clanId;
		log.info("Sending clan response event with routing key:" + clanIdString);
		chatTemplate.send(clanIdString, new Message(getByteArray(event),
				msgProps));
	}

	@Override
	public void processGlobalChatResponseEvent(ResponseEvent event) {
		MessageProperties msgProps = getProps();
		String chatGlobalRoutingKey = "chat_global";
		log.info("Sending global event with type=" + event.getEventType()
				+ " with routing key:" + chatGlobalRoutingKey);
		chatTemplate.send(chatGlobalRoutingKey, new Message(
				getByteArray(event), msgProps));
	}

	protected ByteBuffer getBytes(ResponseEvent event) {
		ByteBuffer writeBuffer = NIOUtils.prepBuffer(event);
		return writeBuffer;
	}

	protected byte[] getByteArray(ResponseEvent event) {
		ByteBuffer writeBuffer = NIOUtils.prepBuffer(event);
		int remaining = writeBuffer.remaining();
		//log.info("Got byte[] of size: {}", remaining);
		byte[] b = new byte[remaining];
		writeBuffer.get(b);
		return b;
	}

	protected void traceByteArray(byte[] bytes) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 50; i++) {
			buf.append(i);
			buf.append(": ");
			buf.append(Integer.toHexString(bytes[i]));
			buf.append("\n");
		}
		log.info("First 50 bytes:\n{}", buf.toString());
		buf = new StringBuffer();
		for (int i = bytes.length - 51; i < bytes.length; i++) {
			buf.append(i);
			buf.append(": ");
			buf.append(Integer.toHexString(bytes[i]));
			buf.append("\n");
		}
		log.info("Last 50 bytes:\n{}", buf.toString());
	}

}// EventWriter