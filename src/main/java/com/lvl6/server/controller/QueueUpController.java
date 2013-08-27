package com.lvl6.server.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Random;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.QueueUpRequestEvent;
import com.lvl6.events.response.QueueUpResponseEvent;

import com.lvl6.info.Location;

import com.lvl6.info.User;

import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventProto.QueueUpRequestProto;
import com.lvl6.proto.EventProto.QueueUpResponseProto;
import com.lvl6.proto.EventProto.QueueUpResponseProto.QueueUpStatus;
import com.lvl6.proto.EventProto.QueueUpResponseProto.Builder;

import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.InfoProto.UserType;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;

import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;


@Component @DependsOn("gameServer") public class QueueUpController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_USER;

	@Autowired
	protected InsertUtil insertUtils;

	public void setInsertUtils(InsertUtil insertUtils) {
		this.insertUtils = insertUtils;
	}

	@Autowired
	protected LeaderBoardUtil leaderUtil;
	public void setLeaderUtil(LeaderBoardUtil leaderUtil) {
		this.leaderUtil = leaderUtil;
	}

	public QueueUpController() {
		numAllocatedThreads = 10;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new QueueUpRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_QUEUE_UP_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		QueueUpRequestProto reqProto = ((QueueUpRequestEvent) event)
				.getQueueUpRequestProto();

		MinimumUserProto attackerProto = reqProto.getAttacker();
		int attackerElo = reqProto.getElo();
		//client keeps adding to this list, prevents same users coming up in queue
		List<Integer> seenUserIds = reqProto.getSeenUserIdsList();
		Date clientDate = new Date();
	    QueueUpResponseProto.Builder resBuilder = QueueUpResponseProto.newBuilder();
		resBuilder.setAttacker(attackerProto);
		resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);

		try {
			User attacker = RetrieveUtils.userRetrieveUtils().getUserById(attackerProto.getUserId());

			boolean legitQueueUp = checkLegitQueueUp(resBuilder, attacker, attackerElo, seenUserIds, clientDate);

			QueueUpResponseEvent resEvent = new QueueUpResponseEvent(attackerProto.getUserId());

			resEvent.setQueueUpResponseProto(resBuilder.build());  

			if (legitQueueUp) {
				writeChangesToDB(attacker, attackerElo);
				resEvent.setTag(event.getTag());
				User defender = queuedOpponent(attacker, attackerElo, seenUserIds, clientDate);
				resBuilder.setDefender(CreateInfoProtoUtils.createMinimumUserProtoFromUser(defender));
				server.writeEvent(resEvent);
			}
		} catch (Exception e) {
			log.error("exception in QueueUp processEvent", e);
		} 
	}


	private int calculateQueueCost(User attacker, int elo) {
		//TODO: make formula for queue search cost factoring in user lvl and elo
		return 1000;
	}

	//gets users -100 to +100 elo from user
	private List<User> firstPossibleOpponents(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		int eloLowerRange1 = elo - 100;
		int eloUpperRange1 = elo + 100;

		String query1 = "SELECT * FROM " + TABLE_NAME + "WHERE" + DBConstants.USER__ELO + ">" 
				+ eloLowerRange1 + "and" + DBConstants.USER__ELO + "<" + eloUpperRange1;

		ResultSet rs1 = null;
		Connection conn = DBConnection.get().getConnection();

		try{
			PreparedStatement stmt1 = conn.prepareStatement(query1);
			rs1 = stmt1.executeQuery();
		}
		catch (SQLException e) {
			log.error("problem with " + query1);
		} catch (NullPointerException e) {
			log.error("problem with " + query1);
		}

		//range of users -100 and +100 elo
		List<User> usersList1 = convertRSToUsers(rs1);
		DBConnection.get().close(rs1, null, conn);  

		for(int i=0;i<usersList1.size();i++) {
			if(usersList1.get(i).isHasBeginnerShield() || 
					usersList1.get(i).getShieldEndTime().getTime() > clientDate.getTime()) {
				usersList1.remove(i);
			}
			for(int j=0;j<seenUserIds.size();j++) {
				if(usersList1.get(i).getId() == seenUserIds.get(j))
					usersList1.remove(i);
			}
		}

		return usersList1;
	}

	//gets users -200 to -100 and +100 to +200
	private List<User> secondPossibleOpponents(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		int eloLowerRange1 = elo - 100;
		int eloUpperRange1 = elo + 100;

		int eloLowerRange2 = eloLowerRange1 - 100;
		int eloUpperRange2 = eloUpperRange1 + 100;

		String query2 = "SELECT * FROM " + TABLE_NAME + "WHERE" + DBConstants.USER__ELO + ">" 
				+ eloLowerRange2 + "and" + DBConstants.USER__ELO + "<" + eloLowerRange1;

		String query3 = "SELECT * FROM " + TABLE_NAME + "WHERE" + DBConstants.USER__ELO + ">" 
				+ eloUpperRange1 + "and" + DBConstants.USER__ELO + "<" + eloUpperRange2;

		ResultSet rs2 = null;
		ResultSet rs3 = null;

		Connection conn = DBConnection.get().getConnection();

		try{
			PreparedStatement stmt2 = conn.prepareStatement(query2);
			rs2 = stmt2.executeQuery();

			PreparedStatement stmt3 = conn.prepareStatement(query3);
			rs3 = stmt3.executeQuery();
		}
		catch (SQLException e) {
			log.error("problem with " + query2 + "or" + query3);
		} catch (NullPointerException e) {
			log.error("problem with " + query2 + "or" + query3);
		}

		//range of users -200 and -100 and +100 to +200 elo
		List<User> usersList2 = new ArrayList<User>(convertRSToUsers(rs2));
		usersList2.addAll(convertRSToUsers(rs3));

		DBConnection.get().close(rs2, null, conn);
		DBConnection.get().close(rs3, null, conn);

		for(int i=0;i<usersList2.size();i++) {
			if(usersList2.get(i).isHasBeginnerShield() || 
					  usersList2.get(i).getShieldEndTime().getTime() > clientDate.getTime()) {
				usersList2.remove(i);
			}
			for(int j=0;j<seenUserIds.size();j++) {
				if(usersList2.get(i).getId() == seenUserIds.get(j))
					usersList2.remove(i);
			}
		}

		return usersList2;
	}

	//gets users -300 to -200 and +200 to +300
	private List<User> thirdPossibleOpponents(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		int eloLowerRange1 = elo - 100;
		int eloUpperRange1 = elo + 100;

		int eloLowerRange2 = eloLowerRange1 - 100;
		int eloUpperRange2 = eloUpperRange1 + 100;

		int eloLowerRange3 = eloLowerRange2 - 100;
		int eloUpperRange3 = eloUpperRange2 + 100;

		String query4 = "SELECT * FROM " + TABLE_NAME + "WHERE" + DBConstants.USER__ELO + ">" 
				+ eloLowerRange3 + "and" + DBConstants.USER__ELO + "<" + eloLowerRange2;

		String query5 = "SELECT * FROM " + TABLE_NAME + "WHERE" + DBConstants.USER__ELO + ">" 
				+ eloUpperRange2 + "and" + DBConstants.USER__ELO + "<" + eloUpperRange3;

		ResultSet rs4 = null;
		ResultSet rs5 = null;
		Connection conn = DBConnection.get().getConnection();

		try{
			PreparedStatement stmt4 = conn.prepareStatement(query4);
			rs4 = stmt4.executeQuery();

			PreparedStatement stmt5 = conn.prepareStatement(query5);
			rs5 = stmt5.executeQuery();
		}
		catch (SQLException e) {
			log.error("problem with " + query4 + "or" + query5);
		} catch (NullPointerException e) {
			log.error("problem with " + query4 + "or" + query5);
		}

		//range of users -200 and -100 and +100 to +200 elo
		List<User> usersList3 = new ArrayList<User>(convertRSToUsers(rs4));
		usersList3.addAll(convertRSToUsers(rs5));

		DBConnection.get().close(rs4, null, conn);
		DBConnection.get().close(rs5, null, conn);

		for(int i=0;i<usersList3.size();i++) {
			if(usersList3.get(i).isHasBeginnerShield() || 
					usersList3.get(i).getShieldEndTime().getTime() > clientDate.getTime()) {
				usersList3.remove(i);
			}
			for(int j=0;j<seenUserIds.size();j++) {
				if(usersList3.get(i).getId() == seenUserIds.get(j))
					usersList3.remove(i);
			}
		}

		return usersList3;
	}

	private User queuedOpponent(User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		List<User> userList1 = firstPossibleOpponents(attacker, elo, seenUserIds, clientDate);
		List<User> userList2 = secondPossibleOpponents(attacker, elo, seenUserIds, clientDate);
		List<User> userList3 = thirdPossibleOpponents(attacker, elo, seenUserIds, clientDate);

		List<User> userList = new ArrayList<User>();

		if(userList1.size() < 68) {
			userList.addAll(userList1);
		}
		else {
			for(int i=0;i<68;i++) {
				Random rand = new Random();
				User u = userList1.get(rand.nextInt(userList1.size()));
				userList.add(u);
			}
		}

		if(userList2.size() < 27) {
			userList.addAll(userList2);
		} else {
			for(int i=0;i<27;i++) {
				Random rand = new Random();
				User u = userList2.get(rand.nextInt(userList2.size()));
				userList.add(u);
			}
		}

		if(userList3.size() < 5) {
			userList.addAll(userList3);
		} else {
			for(int i=0;i<5;i++) {
				Random rand = new Random();
				User u = userList3.get(rand.nextInt(userList3.size()));
				userList.add(u);
			}
		}

		if(userList.size()==0) {
			return null;
		}
		else {
			Random rand = new Random();
			User queuedOpponent = userList.get(rand.nextInt(userList.size()));
			return queuedOpponent;
		}
	}


	private boolean checkLegitQueueUp(Builder resBuilder, User attacker, int elo, List<Integer> seenUserIds, Date clientDate) {
		if (attacker == null) {
			resBuilder.setStatus(QueueUpStatus.OTHER_FAIL);
			log.error("problem with QueueUp- attacker is null. attacker is " + attacker);
			return false;
		}
		if (attacker.getCoins() < calculateQueueCost(attacker, attacker.getElo())) {
			resBuilder.setStatus(QueueUpStatus.NOT_ENOUGH_SILVER);
			log.error("problem with QueueUp- attacker doesn't have enough silver to search queue");
			return false;
		}

		User queuedOpponent = queuedOpponent(attacker, elo, seenUserIds, clientDate);
		if(queuedOpponent==null) {
			resBuilder.setStatus(QueueUpStatus.CANT_FIND_ANYONE);
			log.error("no users to match up with");
			return false;
		}
		if(queuedOpponent.getShieldEndTime() != null) {
			if(queuedOpponent.isHasBeginnerShield() || 
					queuedOpponent.getShieldEndTime().getTime() > clientDate.getTime()) {
				resBuilder.setStatus(QueueUpStatus.HAS_SHIELD);
				log.error("trying to queue with opponent with shield on");
				return false;
			}
		}

		resBuilder.setStatus(QueueUpStatus.SUCCESS);
		return true;
	}

	// change user silver value and remove his shield if he has one
	private boolean writeChangesToDB(User attacker, int elo) {
		int queueCost = calculateQueueCost(attacker, elo);
		return attacker.updateRelativeStaminaExperienceCoinsBattleswonBattleslostFleesSimulatestaminarefill (
				0, 0, queueCost, 0, 0, 0,  false, false, null, true, false, false);  
	}


	private List<User> convertRSToUsers(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				List<User> users = new ArrayList<User>();
				while(rs.next()) {  //should only be one
					users.add(convertRSRowToUser(rs));
				}
				return users;
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}



	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private User convertRSRowToUser(ResultSet rs) throws SQLException {
		int i = 1;
		int userId = rs.getInt(i++);
		String name = rs.getString(i++);
		int level = rs.getInt(i++);
		UserType type = UserType.valueOf(rs.getInt(i++));
		int attack = rs.getInt(i++);
		int defense = rs.getInt(i++);
		int stamina = rs.getInt(i++);

		Date lastStaminaRefillTime = null;
		Timestamp ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastStaminaRefillTime = new Date(ts.getTime());
		}

		int energy = rs.getInt(i++);

		Date lastEnergyRefillTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastEnergyRefillTime = new Date(ts.getTime());
		}

		int skillPoints = rs.getInt(i++);
		int energyMax = rs.getInt(i++);
		int staminaMax = rs.getInt(i++);
		int diamonds = rs.getInt(i++);
		int coins = rs.getInt(i++);
		int marketplaceDiamondsEarnings = rs.getInt(i++);
		int marketplaceCoinsEarnings = rs.getInt(i++);
		int vaultBalance = rs.getInt(i++);
		int experience = rs.getInt(i++);
		int tasksCompleted = rs.getInt(i++);
		int battlesWon = rs.getInt(i++);
		int battlesLost = rs.getInt(i++);
		int flees = rs.getInt(i++);
		String referralCode = rs.getString(i++);
		int numReferrals = rs.getInt(i++);
		String udid = rs.getString(i++);
		Location userLocation = new Location(rs.getDouble(i++), rs.getDouble(i++));
		int numPostsInMarketplace = rs.getInt(i++);
		int numMarketplaceSalesUnredeemed = rs.getInt(i++);

		int weaponEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			weaponEquippedUserEquipId = ControllerConstants.NOT_SET;
		}
		int armorEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			armorEquippedUserEquipId = ControllerConstants.NOT_SET;
		}
		int amuletEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			amuletEquippedUserEquipId = ControllerConstants.NOT_SET;
		}

		Date lastLoginTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastLoginTime = new Date(ts.getTime());
		}

		Date lastLogoutTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastLogoutTime = new Date(ts.getTime());
		}

		String deviceToken = rs.getString(i++);

		Date lastBattleNotificationTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastBattleNotificationTime = new Date(ts.getTime());
		}

		Date lastTimeAttacked = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastTimeAttacked = new Date(ts.getTime());
		}

		int numBadges = rs.getInt(i++);

		Date lastShortLicensePurchaseTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastShortLicensePurchaseTime = new Date(ts.getTime());
		}

		Date lastLongLicensePurchaseTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastLongLicensePurchaseTime = new Date(ts.getTime());
		}

		boolean isFake = rs.getBoolean(i++);

		Date userCreateTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			userCreateTime = new Date(ts.getTime());
		}

		boolean isAdmin = rs.getBoolean(i++);

		String apsalarId = rs.getString(i++);
		int numCoinsRetrievedFromStructs = rs.getInt(i++);
		int numAdcolonyVideosWatched = rs.getInt(i++);
		int numTimesKiipRewarded = rs.getInt(i++);
		int numConsecutiveDaysPlayed = rs.getInt(i++);
		int numGroupChatsRemaining = rs.getInt(i++);

		int clanId = rs.getInt(i++);
		if (rs.wasNull()) {
			clanId = ControllerConstants.NOT_SET;
		}

		Date lastGoldmineRetrieval = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastGoldmineRetrieval = new Date(ts.getTime());
		}

		Date lastMktNotificationTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastMktNotificationTime = new Date(ts.getTime());
		}

		Date lastWallNotificationTime = null;
		ts = rs.getTimestamp(i++);
		if (!rs.wasNull()) {
			lastWallNotificationTime = new Date(ts.getTime());
		}

		int kabamNaid = rs.getInt(i++);

		boolean hasReceivedfbReward = rs.getBoolean(i++);

		int weaponTwoEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			weaponTwoEquippedUserEquipId = ControllerConstants.NOT_SET;
		}
		int armorTwoEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			armorTwoEquippedUserEquipId = ControllerConstants.NOT_SET;
		}
		int amuletTwoEquippedUserEquipId = rs.getInt(i++);
		if (rs.wasNull()) {
			amuletTwoEquippedUserEquipId = ControllerConstants.NOT_SET;
		} 

		int prestigeLevel = rs.getInt(i++);

		int numAdditionalForgeSlots = rs.getInt(i++);
		int numBeginnerSalesPurchased = rs.getInt(i++);
		boolean isMentor = rs.getBoolean(i++);
		boolean hasBeginnerShield = rs.getBoolean(i++);
		Date shieldStartTime = rs.getDate(i++);
		int elo = rs.getInt(i++);
		String rank = rs.getString(i++);

		User user = new User(userId, name, level, type, attack, defense, stamina,
				lastStaminaRefillTime, energy, lastEnergyRefillTime, skillPoints,
				energyMax, staminaMax, diamonds, coins, marketplaceDiamondsEarnings, 
				marketplaceCoinsEarnings, vaultBalance, experience, tasksCompleted, 
				battlesWon, battlesLost, flees, referralCode, numReferrals, udid,
				userLocation, numPostsInMarketplace, numMarketplaceSalesUnredeemed,
				weaponEquippedUserEquipId, armorEquippedUserEquipId, amuletEquippedUserEquipId,
				lastLoginTime, lastLogoutTime, deviceToken, lastBattleNotificationTime, lastTimeAttacked,
				numBadges, lastShortLicensePurchaseTime, lastLongLicensePurchaseTime, isFake, 
				userCreateTime, isAdmin, apsalarId, numCoinsRetrievedFromStructs, numAdcolonyVideosWatched, 
				numTimesKiipRewarded, numConsecutiveDaysPlayed, numGroupChatsRemaining, clanId, 
				lastGoldmineRetrieval, lastMktNotificationTime, lastWallNotificationTime, 
				kabamNaid, hasReceivedfbReward, weaponTwoEquippedUserEquipId, armorTwoEquippedUserEquipId, 
				amuletTwoEquippedUserEquipId, prestigeLevel, numAdditionalForgeSlots, numBeginnerSalesPurchased, 
				isMentor, hasBeginnerShield, shieldStartTime, elo, rank);
		return user;
	}
}
