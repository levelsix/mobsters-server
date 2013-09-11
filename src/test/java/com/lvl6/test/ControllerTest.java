package com.lvl6.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.IMap;
import com.lvl6.events.request.PurchaseCityExpansionRequestEvent;
import com.lvl6.events.request.StartupRequestEvent;
import com.lvl6.events.request.UserCreateRequestEvent;
import com.lvl6.info.User;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventProto.PurchaseCityExpansionRequestProto;
import com.lvl6.proto.EventProto.StartupRequestProto;
import com.lvl6.proto.EventProto.UserCreateRequestProto;
import com.lvl6.proto.InfoProto.CoordinateProto;
import com.lvl6.proto.InfoProto.LocationProto;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.InfoProto.UserType;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.server.controller.PurchaseCityExpansionController;
import com.lvl6.server.controller.StartupController;
import com.lvl6.server.controller.UserCreateController;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ControllerTest extends TestCase {
	private static final int DEFAULT_TTL = 9;
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Autowired
	PurchaseCityExpansionController purchaseCityExpansionController;
	
	@Autowired
	StartupController startupController;
	
	@Autowired
	UserCreateController userCreateController;
	
	
	
	@Autowired
	UserRetrieveUtils userRetrieveUtils;
	
	
	
	@Autowired
	GameServer server;
	
	@Resource(name = "playersByPlayerId")
	IMap<Integer, ConnectedPlayer> playersByPlayerId;

	@Resource(name = "playersPreDatabaseByUDID")
	IMap<String, ConnectedPlayer> playersPreDatabaseByUDID;

	public IMap<String, ConnectedPlayer> getPlayersPreDatabaseByUDID() {
		return playersPreDatabaseByUDID;
	}

	public void setPlayersPreDatabaseByUDID(
			IMap<String, ConnectedPlayer> playersPreDatabaseByUDID) {
		this.playersPreDatabaseByUDID = playersPreDatabaseByUDID;
	}

	public IMap<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(
			IMap<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}
	
	
	

	public void createUser(String udid) {
		//need to add a new ConnectedPlayer
		ConnectedPlayer newp = new ConnectedPlayer();
		newp.setIp_connection_id("173.228.88.96");
		newp.setServerHostName(server.serverId());
		newp.setUdid(udid);
		getPlayersPreDatabaseByUDID().put(newp.getUdid(), newp, DEFAULT_TTL, TimeUnit.MINUTES);
		
		String name = "bob";
		UserType ut = UserType.BAD_ARCHER;
		
		LocationProto.Builder lpb = LocationProto.newBuilder();
		lpb.setLatitude(1.1D);
		lpb.setLongitude(1.1D);
		LocationProto lp = lpb.build();
		
		String deviceToken = "deviceToken";
		int attack = 12;
		int defense = 12;
		int energy = 50;
		int stamina = 3;
		
		long timeOfStructPurchase = (new Date()).getTime();
		long timeOfStructBuild = timeOfStructPurchase;
		
		CoordinateProto.Builder cpb = CoordinateProto.newBuilder();
		cpb.setX(1.1F);
		cpb.setY(1.1F);
		CoordinateProto cp = cpb.build();
		boolean usedDiamondsToBuilt = true;
		
		UserCreateRequestProto.Builder ucrpb = UserCreateRequestProto.newBuilder();
		ucrpb.setUdid(udid);
		ucrpb.setName(name);
		ucrpb.setType(ut);
		ucrpb.setUserLocation(lp);
		ucrpb.setDeviceToken(deviceToken);
		ucrpb.setAttack(attack);
		ucrpb.setDefense(defense);
		ucrpb.setEnergy(energy);
		ucrpb.setStamina(stamina);
		ucrpb.setTimeOfStructPurchase(timeOfStructPurchase);
		ucrpb.setTimeOfStructBuild(timeOfStructBuild);
		ucrpb.setStructCoords(cp);
		ucrpb.setUsedDiamondsToBuilt(usedDiamondsToBuilt);
		
		UserCreateRequestEvent ucre = new UserCreateRequestEvent();
		ucre.setTag(1);
		ucre.setUdid(udid);
		ucre.setUserCreateRequestProto(ucrpb.build());
		
		getUserCreateController().handleEvent(ucre);
	}
	
	public void loginUser(String udid) {
		StartupRequestProto.Builder srpb = StartupRequestProto.newBuilder();
		srpb.setUdid(udid);
		srpb.setVersionNum(3.6f);
		srpb.setIsForceTutorial(false);
		
		StartupRequestEvent sre = new StartupRequestEvent();
		sre.setStartupRequestProto(srpb.build());
		
		//have the controller process this event
		getStartupController().handleEvent(sre);
	}
	
	public void purchaseCityExpansion(User aUser) {
		int diamondChange = 0;
		int coinChange = 50000;
		int experienceChange = 0;
		boolean successful = aUser.updateRelativeDiamondsCoinsExperienceNaive(
				diamondChange, coinChange, experienceChange);
		assertTrue(successful);
		
		PurchaseCityExpansionRequestProto.Builder pcerpb =
				PurchaseCityExpansionRequestProto.newBuilder();
		
		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(aUser);
		pcerpb.setSender(mup);
		pcerpb.setXPosition(1);
		pcerpb.setYPosition(1);
		pcerpb.setTimeOfPurchase((new Date()).getTime());
		
		PurchaseCityExpansionRequestEvent pcere = new PurchaseCityExpansionRequestEvent();
		pcere.setPurchaseCityExpansionRequestProto(pcerpb.build());
		
		getPurchaseCityExpansionController().handleEvent(pcere);
	}

	@Test
	public void testStartup() {
//		String udid = "blah";
//		//log.info("\n\n\n\n\n\n\n\n\n\n\n CREATING USER");
//		createUser(udid);
//		//log.info("CREATED USER\n\n\n\n\n\n\n\n\n\n\n");
//		
//		User aUser = getUserRetrieveUtils().getUserByUDID(udid);
//		assertTrue("Expected: not null. Actual=" + aUser, null != aUser);
//		//log.info("user=" + aUser);
//		//log.info("\n\n\n\n\n\n");
//		
//		loginUser(udid);
//		
//		//TODO: MAKE CLEAN UP FUNCTIONS TO DELETE EVERYTHING THIS METHOD DID
//		int userId = aUser.getId();
//		deleteUserById(userId);
//		deleteUserLoginHistory(userId, udid);
//		deleteFirstTimeUsers(udid);
	}
	
	
	@Test
	public void testPurchaseCityExpansion() {
//		String udid = "udid";
//		createUser(udid);
//		User aUser = getUserRetrieveUtils().getUserByUDID(udid);
//		//increase the user's coins 
//		
//		log.info("\n\n\n\n\n\n\n\n\n\n\n PURCHASING EXPANSION");
//		purchaseCityExpansion(aUser);
//		log.info("\n\n\n\n\n\n\n\n\n\n\n PURCHASED EXPANSION");
//		//TODO: MAKE CLEAN UP FUNCTIONS TO DELETE EVERYTHING THIS METHOD DID
//		int userId = aUser.getId();
//		deleteUserById(userId);
//		deleteUserLoginHistory(userId, udid);
//		deleteFirstTimeUsers(udid);
//		deleteUserCityExpansionData(userId);
	}
	
	
	
	public void deleteUserById(int userId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER__ID, userId);

		int numDeleted = DBConnection.get().deleteRows(
				DBConstants.TABLE_USER, conditionParams, "and");
		log.info("num users deleted=" + numDeleted);
	}
	
	public void deleteUserLoginHistory(int userId, String udid) {
		String tableName = DBConstants.TABLE_LOGIN_HISTORY;
		
		String query = " DELETE FROM " + tableName + " WHERE " +
				DBConstants.LOGIN_HISTORY__ID + " > ? AND " + 
				DBConstants.LOGIN_HISTORY__USER_ID + " = ? OR " + 
				DBConstants.LOGIN_HISTORY__UDID + " = ?;";
		
		List<Object> values = new ArrayList<Object>();
		values.add(0); //for id 
		values.add(userId);
		values.add(udid);
		    
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
				query, values);
		log.info("num login history rows deleted=" + numDeleted);
	}
	
	public void deleteFirstTimeUsers(String openUdid) {
		String tableName = DBConstants.TABLE_FIRST_TIME_USERS;
		
		String query = " DELETE FROM " + tableName + " WHERE " +
				DBConstants.FIRST_TIME_USERS__ID + " > ? and " + 
				DBConstants.FIRST_TIME_USERS__OPEN_UDID + " = ?;";
		
		List<Object> values = new ArrayList<Object>();
		values.add(0); //for 
		values.add(openUdid);
		    
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
				query, values);
		log.info("num first time users deleted=" + numDeleted);
	}
	
	public void deleteUserCityExpansionData(int userId) {
		String tableName = DBConstants.TABLE_USER_CITY_EXPANSION_DATA;

		String query = " DELETE FROM " + tableName + " WHERE " +
				DBConstants.USER_CITY_EXPANSION_DATA__USER_ID + " = ?;";
		
		List<Object> values = new ArrayList<Object>();
		values.add(userId); //for 
		    
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
				query, values);
		log.info("num user city expansion data deleted=" + numDeleted);
	}
	

	public PurchaseCityExpansionController getPurchaseCityExpansionController() {
		return purchaseCityExpansionController;
	}

	public void setPurchaseCityExpansionController(
			PurchaseCityExpansionController purchaseCityExpansionController) {
		this.purchaseCityExpansionController = purchaseCityExpansionController;
	}

	public StartupController getStartupController() {
		return startupController;
	}

	public void setStartupController(StartupController startupController) {
		this.startupController = startupController;
	}

	public UserCreateController getUserCreateController() {
		return userCreateController;
	}

	public void setUserCreateController(UserCreateController userCreateController) {
		this.userCreateController = userCreateController;
	}

	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}


	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}
	
}
