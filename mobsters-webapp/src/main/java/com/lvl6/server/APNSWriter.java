package com.lvl6.server;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.lvl6.events.GameEvent;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.events.ResponseEvent;
import com.lvl6.events.response.EndPvpBattleResponseEvent;
import com.lvl6.events.response.GeneralNotificationResponseEvent;
import com.lvl6.events.response.PrivateChatPostResponseEvent;
import com.lvl6.info.User;
import com.lvl6.properties.APNSProperties;
import com.lvl6.properties.Globals;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto;
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto;
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.Wrap;
import com.lvl6.utils.utilmethods.UpdateUtils;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.PayloadBuilder;

public class APNSWriter extends Wrap implements ApplicationContextAware {
	// reference to game server

	@Autowired
	UserClanRetrieveUtils2	userClanRetrieveUtil;

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	public Map<String, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(Map<String, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Resource(name = "playersByPlayerId")
	protected Map<String, ConnectedPlayer>	playersByPlayerId;

	private static Logger					log											= LoggerFactory.getLogger(APNSWriter.class);

	private static final int				SOFT_MAX_NOTIFICATION_BADGES				= 20;

	// private static final int MIN_MINUTES_BETWEEN_BATTLE_NOTIFICATIONS = 180;
	// // 3
	// hours
	// private static final int MIN_MINUTES_BETWEEN_WALL_POST_NOTIFICATIONS =
	// 0;//15;

	private static final int				MAX_NUM_CHARACTERS_TO_SEND_FOR_WALL_POST	= 120;

	// 3 days
	private static final long				MINUTES_BETWEEN_INACTIVE_DEVICE_TOKEN_FLUSH	= 60 * 24 * 3;
	private static Date						LAST_NULLIFY_INACTIVE_DEVICE_TOKEN_TIME		= new Date();

	@Autowired
	protected APNSProperties				apnsProperties;

	public void setApnsProperties(APNSProperties apnsProperties) {
		this.apnsProperties = apnsProperties;
	}

	/**
	 * constructor.
	 */
	public APNSWriter() {

	}

	/** unused */
	@Override
	protected void processEvent(GameEvent event) {
		if (event instanceof NormalResponseEvent)
			processResponseEvent((NormalResponseEvent) event);

	}

	/**
	 * our own version of processEvent that takes the additional parameter of
	 * the writeBuffer
	 */
	protected void processResponseEvent(NormalResponseEvent event) {
		String playerId = event.getPlayerId();
		log.info("received APNS notification to send to player with id " + playerId);
		User user = RetrieveUtils.userRetrieveUtils().getUserById(playerId);
		if (user != null && user.getDeviceToken() != null && user.getDeviceToken().length() > 0) {
			try {
				ApnsService service = getApnsService();
				if (service != null) {
					Date now = new Date();
					if (LAST_NULLIFY_INACTIVE_DEVICE_TOKEN_TIME.getTime() + 60000 * MINUTES_BETWEEN_INACTIVE_DEVICE_TOKEN_FLUSH < now.getTime()) {
						LAST_NULLIFY_INACTIVE_DEVICE_TOKEN_TIME = now;
						Map<String, Date> inactiveDevices = service.getInactiveDevices();
						UpdateUtils.get().updateNullifyDeviceTokens(inactiveDevices.keySet());
					}

					if (PrivateChatPostResponseEvent.class.isInstance(event)) {
						handlePrivateChatPostNotification(service, (PrivateChatPostResponseEvent) event, user, user.getDeviceToken());
					}
					
					if(EndPvpBattleResponseEvent.class.isInstance(event)) {
						handleEndPvpNotification(service, (EndPvpBattleResponseEvent) event, user, user.getDeviceToken());
					}

					if (GeneralNotificationResponseEvent.class.isInstance(event)) {
						handleGeneralNotification(service, (GeneralNotificationResponseEvent) event, user, user.getDeviceToken());
					}
				} else {
					log.warn("Apns service is null");
				}

			} catch (FileNotFoundException e) {
				log.error("File not found", e);
			}
		} else {
			log.warn("could not send push notification because user " + user + " has no device token");
		}
	}

	protected ApnsService	service;

	@PostConstruct
	public ApnsService getApnsService() throws FileNotFoundException {
		if (service == null) {
			log.info("Apns Service null... building new");
			buildService();
		}
		try {
			log.info("Testing APNS connection");
			service.testConnection();
		} catch (Throwable e) {
			log.info("ApnsService connection test failed... building again");
		}
		return service;
	}

	/*
	 * @Scheduled(fixedRate=1000*60*60) public void resetApnsService() {
	 * log.info("Rebuilding APNSService"); service.stop(); service = null; try {
	 * getApnsService(); } catch (FileNotFoundException e) {
	 * log.error("Error rebuilding APNSService", e); } }
	 */

	protected void buildService() {
		log.info("Building ApnsService");
		try {
			// InputStream inputStream =
			// getClass().getClassLoader().getResourceAsStream(apnsProperties.pathToCert);
			org.springframework.core.io.Resource resource = context.getResource(apnsProperties.pathToCert);
			Object[] args = { apnsProperties.pathToCert, resource.exists(), resource.contentLength() };
			log.info("Loading cert: {}, exists: {}, length: {}", args);
			ApnsServiceBuilder builder = APNS.newService().withCert(resource.getInputStream(), apnsProperties.certPassword);
			if (Globals.IS_SANDBOX()) {
				log.info("Building apns with sandbox=true");
				builder.withSandboxDestination();
			} else {
				builder.withProductionDestination();
			}
			builder.withDelegate(new Lvl6ApnsDelegate());
			service = builder.build();
			service.start();
		} catch (Exception e) {
			log.error("Error getting apns cert.. Invalid SSL Config Exception", e);
		}
	}

	private void handleGeneralNotification(ApnsService service, GeneralNotificationResponseEvent event, User user, String token) {
		if (user.getNumBadges() < SOFT_MAX_NOTIFICATION_BADGES) {
			PayloadBuilder pb = APNS.newPayload().actionKey("View Now").badge(1);

			log.info("GeneralNotification for user: " + user.getId());
			GeneralNotificationResponseProto resProto = event.getGeneralNotificationResponseProto();
			String title = resProto.getTitle();
			String subtitle = resProto.getSubtitle();

			String alertBody = title + " " + subtitle;
			pb.alertBody(alertBody);
			if (!pb.isTooLong()) {
				log.info("sending apns for a general notification");
				service.push(token, pb.build());
			} else {
				log.error("PlayloadBuilder isTooLong to send apns message for general notification");
			}
		}
	}

	private void determineEvent(ResponseEvent event, User user, String deviceToken) {
		try {
			ApnsService service = getApnsService();
			if (service != null) {

				if (GeneralNotificationResponseEvent.class.isInstance(event)) {
					handleGeneralNotification(service, (GeneralNotificationResponseEvent) event, user, deviceToken);
				}
			} else {
				log.warn("Apns service is null");
			}

		} catch (FileNotFoundException e) {
			log.error("File not found", e);
		}
	}

	/**
	 * sends to offline people
	 *
	 * @param event
	 * @param playerId
	 *            - person to send event to
	 */
	protected void sendApnsNotificationToPlayer(ResponseEvent event, String playerId) {
		ConnectedPlayer player = playersByPlayerId.get(playerId);
		if (player == null) {
			log.info("sending apns with type=" + event.getEventType() + " to player with id " + playerId + ", event=" + event);

			User user = RetrieveUtils.userRetrieveUtils().getUserById(playerId);
			String deviceToken = user.getDeviceToken();
			if (user != null && deviceToken != null && deviceToken.length() > 0) {
				determineEvent(event, user, deviceToken);
			} else {
				log.warn("could not send push notification because user " + user + " has no device token");
			}
		}
	}

	// copied from EventWriter.processClanResponseEvent
	@Override
	public void processClanResponseEvent(ResponseEvent event, String clanId) {
		log.debug("apnsWriter received clan event=" + event);
		ResponseEvent e = event;
		List<String> statuses = new ArrayList<String>();
		statuses.add(UserClanStatus.LEADER.name());
		statuses.add(UserClanStatus.JUNIOR_LEADER.name());
		statuses.add(UserClanStatus.CAPTAIN.name());
		statuses.add(UserClanStatus.MEMBER.name());
		List<String> userIds = RetrieveUtils.userClanRetrieveUtils().getUserIdsWithStatuses(clanId, statuses);

		for (String userId : userIds) {
			log.info("Sending apns to clan: {} member: {}", clanId, userId);
			sendApnsNotificationToPlayer(e, userId);
		}
	}

	private void handlePrivateChatPostNotification(ApnsService service, PrivateChatPostResponseEvent event, User user, String token) {
		if (user.getNumBadges() < SOFT_MAX_NOTIFICATION_BADGES) {
			PayloadBuilder pb = APNS.newPayload().actionKey("View").badge(1);
			log.info("PrivateChatPostNotification for user: " + user.getId());
			PrivateChatPostResponseProto resProto = event.getPrivateChatPostResponseProto();
			PrivateChatPostProto post = resProto.getPost();

			String content = post.getContent();
			if (content.length() > MAX_NUM_CHARACTERS_TO_SEND_FOR_WALL_POST) {
				content = content.substring(0, MAX_NUM_CHARACTERS_TO_SEND_FOR_WALL_POST);
				int index = content.lastIndexOf(" ");
				if (index > 0) {
					content = content.substring(0, index);
					content += "...";
				}
			}
			MinimumUserProto mup = post.getPoster();
			String clanId = "";
			if (null != mup.getClan()) {
				clanId = mup.getClan().getClanUuid();
			}
			String clan = !clanId.isEmpty() ? "[" + mup.getClan().getTag() + "] " : "";
			pb.alertBody(clan + mup.getName() + " sent a private message: " + content);

			if (!pb.isTooLong()) {
				log.info("Pushing apns message");
				service.push(token, pb.build());
			} else {
				log.error("PlayloadBuilder isTooLong to send apns message");
			}
		}
	}
	
	private void handleEndPvpNotification(ApnsService service, EndPvpBattleResponseEvent event, User user, String token) {
		PayloadBuilder pb = APNS.newPayload().actionKey("View").badge(1);
		log.info("EndPvpNotification for user {}", user);
		EndPvpBattleResponseProto resProto = event.getEndPvpBattleResponseProto();
		boolean attackerWon = resProto.getAttackerWon();
		MinimumUserProtoWithMaxResources mup = resProto.getSender();
		String clanId = mup.getMinUserProto().getClan().getClanUuid();
		String tag = mup.getMinUserProto().getClan().getTag();
		String name = mup.getMinUserProto().getName();
		String clan = !clanId.isEmpty() ? "[" + tag + "] " : "";
		String message = clan + name;
		
		int randomNum = (int)(Math.random()*5);
		if(attackerWon) {
			switch(randomNum) {
				case 1: message = "You've just been humiliated by " + message + ". Don't let em get away with it!"; 
						break;
				case 2: message = "You just got spanked by " + message + " . Get back on and tell em you liked it!";
						break;
				case 3: message = message + " totally embarassed you in front of all your friends. Win back your honor!";
						break;
				case 4: message = "UTTER DEVASTATION was just unleashed by " + message + " on you. You ok bro?";
						break;
				default: message = message + " just defeated you. Go get your revenge";
						break;
			}
		}
		else {
			switch(randomNum) {
				case 1: message = message + " totally just tried to beat you and failed miserably. Get back on and laugh at em";
						break;
				case 2: message = message + " just tried to beat you...it was honestly pretty sad. Get back on and end their misery";
						break;
				case 3: message = "WOW you strong like a rock! " + message + " weak unlike a rock. Get on and show him how strong "
						+ "rock is!";
						break;
				case 4: message = "Party foul!! " + message + " just tried to get you with your back turned. Go mess em up!";
						break;
				default: message = message + " has failed to defeated you. Get on and teach em who's boss";
						break;
			}
		}
		
		pb.alertBody(message);
		if (!pb.isTooLong()) {
			log.info("Pushing apns message");
			service.push(token, pb.build());
		} else {
			log.error("PlayloadBuilder isTooLong to send apns message");
		}
	}

	private ApplicationContext	context;

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		context = arg0;

	}

}// APNSWriter