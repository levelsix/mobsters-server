package com.lvl6.eventhandlers;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.IMap;
import com.lvl6.events.PreDatabaseRequestEvent;
import com.lvl6.events.RequestEvent;
import com.lvl6.info.User;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.EventController;
import com.lvl6.utils.Attachment;
import com.lvl6.utils.ConnectedPlayer;

public class AmqpGameEventHandler extends AbstractGameEventHandler implements
		MessageListener {

	static Logger log = LoggerFactory.getLogger(GameEventHandler.class);

	private static final int DEFAULT_TTL = 9;

	@Resource(name = "playersByPlayerId")
	IMap<String, ConnectedPlayer> playersByPlayerId;
	
	@Autowired
	UserRetrieveUtils2 userRetrieveUtils;

	public IMap<String, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(
			IMap<String, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Resource(name = "playersPreDatabaseByUDID")
	IMap<String, ConnectedPlayer> playersPreDatabaseByUDID;

	public IMap<String, ConnectedPlayer> getPlayersPreDatabaseByUDID() {
		return playersPreDatabaseByUDID;
	}

	public void setPlayersPreDatabaseByUDID(
			IMap<String, ConnectedPlayer> playersPreDatabaseByUDID) {
		this.playersPreDatabaseByUDID = playersPreDatabaseByUDID;
	}

	@Override
	public void onMessage(Message msg) {
		try {
			if (msg != null) {
				log.debug("Received message", msg.getMessageProperties()
						.getMessageId());
				Attachment attachment = new Attachment();
				byte[] payload = msg.getBody();
				attachment.readBuff = ByteBuffer.wrap(payload);
				while (attachment.eventReady()) {
					processAttachment(attachment);
					attachment.reset();
				}
			} else {
				throw new RuntimeException(
						"Message was null or missing headers");
			}
		} catch (Exception e) {
			log.error("Error processing amqp message", e);
		}
	}

	protected void processAttachment(Attachment attachment) {
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOf(attachment.payload,
				attachment.payloadSize));
		EventController ec = getServer().getEventControllerByEventType(
				attachment.eventType);
		if (ec == null) {
			log.error("No event controller found in controllerMap for {}",
					attachment.eventType);
			return;
		}
		RequestEvent event = ec.createRequestEvent();
		event.setTag(attachment.tag);
		event.read(bb);

		User user = null;
		String playerId = event.getPlayerId();
		
		log.debug("Received event from client: " + event.getPlayerId());
		if (getApplicationMode().isMaintenanceMode()) {
			if(playerId != null && !playerId.isEmpty()) {
				user = userRetrieveUtils.getUserById(playerId);
			}
			else if (event instanceof PreDatabaseRequestEvent) {
				String udid = ((PreDatabaseRequestEvent) event).getUdid();
				if(udid != null) {
					user = userRetrieveUtils.getUserByUDIDorFbId(udid, "").get(0);
				}
			}
			if(user != null && user.isAdmin()) {
				
			}else {
				//not an admin so send maintenance message and return
				if (event instanceof PreDatabaseRequestEvent) {
					String udid = ((PreDatabaseRequestEvent) event).getUdid();
					messagingUtil.sendMaintanenceModeMessageUdid(
							getApplicationMode().getMessageForUsers(), udid);
				} else {
					messagingUtil.sendMaintanenceModeMessage(
							getApplicationMode().getMessageForUsers(), playerId);
				}
				return;
			}
		}
		updatePlayerToServerMaps(event);
		ec.handleEvent(event);	
	}

	@Override
	protected void delegateEvent(byte[] bytes, RequestEvent event,
			String ip_connection_id, EventProtocolRequest eventType) {
		if (event != null && eventType.getNumber() < 0) {
			log.error("the event type is < 0");
			return;
		}
		EventController ec = server.getEventControllerByEventType(eventType);
		if (ec == null) {
			log.error("No EventController for eventType: " + eventType);
			return;
		}
		ec.handleEvent(event);
	}

	protected void updatePlayerToServerMaps(RequestEvent event) {
		log.debug("Updating player to server maps for player: "
				+ event.getPlayerId());
		if (playersByPlayerId.containsKey(event.getPlayerId())) {
			ConnectedPlayer p = playersByPlayerId.get(event.getPlayerId());
			if (p != null) {
				p.setLastMessageSentToServer(new Date());
				// if (!p.getIp_connection_id().equals(ip_connection_id) ||
				// !p.getServerHostName().equals(server.serverId())) {
				// log.debug("Player is connected to a new socket or server");
				p.setIp_connection_id("amqp");
				p.setServerHostName(server.serverId());
				// }
				playersByPlayerId.put(event.getPlayerId(), p, DEFAULT_TTL,
						TimeUnit.MINUTES);
			} else {
				addNewConnection(event);
			}
		} else {
			addNewConnection(event);
		}

	}

	protected void addNewConnection(RequestEvent event) {
		ConnectedPlayer newp = new ConnectedPlayer();
		newp.setIp_connection_id("amqp");
		newp.setServerHostName(server.serverId());
		if (event instanceof PreDatabaseRequestEvent) {
			newp.setUdid(((PreDatabaseRequestEvent) event).getUdid());
			getPlayersPreDatabaseByUDID().put(newp.getUdid(), newp,
					DEFAULT_TTL, TimeUnit.MINUTES);
			log.info("New player with UdId: " + newp.getUdid());
		} else if (event.getPlayerId() != null
				&& !event.getPlayerId().equals("")) {
			log.info("Player logged on: " + event.getPlayerId());
			newp.setPlayerId(event.getPlayerId());
			playersByPlayerId.put(event.getPlayerId(), newp, DEFAULT_TTL,
					TimeUnit.MINUTES);
		} else {
			log.error(String.format("playerId not set for RequestEvent: %s",
					event));
		}
	}

}
