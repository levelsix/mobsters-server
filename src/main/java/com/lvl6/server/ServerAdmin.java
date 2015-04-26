package com.lvl6.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.lvl6.events.response.PurgeClientStaticDataResponseEvent;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventStaticDataProto.PurgeClientStaticDataResponseProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils2;
import com.lvl6.utils.ConnectedPlayer;

public class ServerAdmin implements MessageListener<ServerMessage> {

	Logger log = LoggerFactory.getLogger(getClass());

	protected JdbcTemplate jdbc;

	@Resource(name = "serverMessagesTemplate")
	protected RabbitTemplate serverMessagesTemplate;

	public RabbitTemplate getServerMessagesTemplate() {
		return serverMessagesTemplate;
	}

	public void setServerMessagesTemplate(RabbitTemplate serverMessagesTemplate) {
		this.serverMessagesTemplate = serverMessagesTemplate;
	}
	@Autowired
	protected MiscMethods miscMethods;

	@Resource
	protected ApplicationMode appMode;

	public ApplicationMode getAppMode() {
		return appMode;
	}

	public void setAppMode(ApplicationMode appMode) {
		this.appMode = appMode;
	}

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbc = new JdbcTemplate(dataSource);
	}

	@Resource(name = "serverTasksExecutor")
	protected TaskExecutor executor;

	public TaskExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(TaskExecutor executor) {
		this.executor = executor;
	}

	@Resource(name = "playersByPlayerId")
	Map<String, ConnectedPlayer> players;

	@Resource(name = "serverEvents")
	protected ITopic<ServerMessage> serverEvents;

	@Resource(name = "staticDataReloadDone")
	protected ITopic<ServerMessage> staticDataReloadDone;

	@Resource(name = "eventWriter")
	protected EventWriterOld writer;

	@Autowired
	protected HazelcastInstance hazel;

	public HazelcastInstance getHazel() {
		return hazel;
	}

	public void setHazel(HazelcastInstance hazel) {
		this.hazel = hazel;
	}

	public ITopic<ServerMessage> getStaticDataReloadDone() {
		return staticDataReloadDone;
	}

	public void setStaticDataReloadDone(
			ITopic<ServerMessage> staticDataReloadDone) {
		this.staticDataReloadDone = staticDataReloadDone;
	}

	public Map<String, ConnectedPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, ConnectedPlayer> players) {
		this.players = players;
	}

	public ITopic<ServerMessage> getServerEvents() {
		return serverEvents;
	}

	public void setServerEvents(ITopic<ServerMessage> serverEvents) {
		this.serverEvents = serverEvents;
	}

	public EventWriterOld getWriter() {
		return writer;
	}

	public void setWriter(EventWriterOld writer) {
		this.writer = writer;
	}

	@Autowired
	protected QuestForUserRetrieveUtils2 qfuRetrieveUtils;

	protected Integer instanceCountForDataReload = 0;
	protected Integer instancesDoneReloadingCount = 0;
	protected ILock instancesReloadingLock;
	protected String registrationId;

	public void reloadAllStaticData() {
		instancesDoneReloadingCount = 0;
		instanceCountForDataReload = getHazel().getCluster().getMembers()
				.size();
		log.info("Reloading all static data for cluster instances: "
				+ instanceCountForDataReload);
		//providing data type Object worked in hazelcast 2.6
		//		instancesReloadingLock = hazel.getLock(ServerMessage.RELOAD_STATIC_DATA);
		//need to provide string now since using hazelcast 3.2
		instancesReloadingLock = hazel.getLock(ServerMessage.RELOAD_STATIC_DATA
				.name());
		try {
			instancesReloadingLock.tryLock(20, TimeUnit.SECONDS);
			//adding message listener creates an id to reger to the listener just added
			//so saving this so as to refer to it later
			this.registrationId = getStaticDataReloadDone().addMessageListener(
					this);
			serverEvents.publish(ServerMessage.RELOAD_STATIC_DATA);
		} catch (InterruptedException e) {
			log.error("Could not obtain lock for reloading instances", e);
		}
	}

	public void reloadLeaderboard() {
		/*executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					UserRetrieveUtils2 uru = RetrieveUtils.userRetrieveUtils();
					List<String> ids = jdbc.query("select " + DBConstants.USER__ID + " from " + DBConstants.TABLE_USER
							+ " where " + DBConstants.USER__IS_FAKE + "=0;", new RowMapper<String>() {
						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							return rs.getString(DBConstants.USER__ID);
						}
					});
					log.info("Reloading leaderboard stats for {} users", ids.size());
					//Map<Integer, User> users = uru.getUsersByIds(ids);
					for (final String id : ids) {
						try {
							User usr = uru.getUserById(id);
							log.info("Batch reloading leaderboard for user {}", usr.getId());
							//null for pvpLeagueForUser, will pull it from hazelcast
							//leaderboard.updateLeaderboardForUser(usr, null);
						} catch (Exception e) {
							log.error("Error updating leaderboard for user: {}", id, e);
						}
					}
				}catch(Exception e) {
					log.error("Error reloading leaderboard",e);
				}
			}
		});*/
	}

	public void setApplicationMode(Boolean maintenanceMode,
			String messageForUsers) {
		log.info("Setting application maintenance mode: {} message: {}",
				maintenanceMode, messageForUsers);
		appMode.setMaintenanceMode(maintenanceMode);
		appMode.setMessageForUsers(messageForUsers);
		serverMessagesTemplate.convertAndSend(appMode);
	}

	protected void sendPurgeStaticDataNotificationToAllClients() {
		Set<String> keySet = players.keySet();
		if (keySet != null) {
			Iterator<String> playas = keySet.iterator();
			log.info("Sending purge static data notification to clients: "
					+ keySet.size());
			while (playas.hasNext()) {
				String playa = playas.next();

				StaticDataProto sdp = miscMethods.getAllStaticData(playa, true,
						qfuRetrieveUtils);
				PurgeClientStaticDataResponseEvent pcsd = new PurgeClientStaticDataResponseEvent(
						playa);
				PurgeClientStaticDataResponseProto.Builder purgeProto = PurgeClientStaticDataResponseProto
						.newBuilder();
				purgeProto.setSenderUuid(playa);
				purgeProto.setStaticDataStuff(sdp);

				pcsd.setPurgeClientStaticDataResponseProto(purgeProto.build());
				writer.handleEvent(pcsd);
			}
		}
	}

	@Override
	public void onMessage(Message<ServerMessage> msg) {
		if (msg.getMessageObject().equals(
				ServerMessage.DONE_RELOADING_STATIC_DATA)) {
			instancesDoneReloadingCount++;
			log.info("Instance done reloading static data: {}/{}",
					instancesDoneReloadingCount, instanceCountForDataReload);
			if (instancesDoneReloadingCount >= instanceCountForDataReload
					|| instancesDoneReloadingCount >= getHazel().getCluster()
							.getMembers().size()) {
				log.info("All instances done reloading static data");
				//providing data type Object worked in hazelcast 2.6
				//				getStaticDataReloadDone().removeMessageListener(this);
				getStaticDataReloadDone().removeMessageListener(registrationId);
				sendPurgeStaticDataNotificationToAllClients();
				instancesReloadingLock.forceUnlock();
				instancesReloadingLock = null;
			}
		}
	}

}
