package com.lvl6.eventhandlers;


public class AmqpGameEventHandlerOld {//extends AbstractGameEventHandler implements	MessageListener {

/*	static Logger log = LoggerFactory.getLogger(GameEventHandlerOld.class);

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
		log.debug("Received event from client: " + event.getPlayerId());
		if (getApplicationMode().isMaintenanceMode()) {
			String playerId = event.getPlayerId();
			User user = userRetrieveUtils.getUserById(playerId);
			if(user.isAdmin()) {
				
			}else {
				//not an admin so send maintenance message and return
				if (event instanceof PreDatabaseRequestEvent) {
					String udid = ((PreDatabaseRequestEvent) event).getUdid();
					messagingUtil.sendMaintanenceModeMessageUdid(
							getApplicationMode().getMessageForUsers(), udid);
				} else {
					messagingUtil.sendMaintanenceModeMessageUdid(
							getApplicationMode().getMessageForUsers(), playerId);
				}
				return;
			}
		}
		
		updatePlayerToServerMaps(event);
		ec.handleEvent(event);
		
	}

	@Override
	protected void delegateEvent(RequestEvent event,EventProtocolRequest eventType) {
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
	}*/

}
