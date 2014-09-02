package com.lvl6.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hazelcast.query.Predicate;
import com.lvl6.events.request.EnhanceMonsterRequestEvent;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventMonsterProto.EnhanceMonsterRequestProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentExpProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.server.controller.EnhanceMonsterController;
import com.lvl6.server.controller.PurchaseCityExpansionController;
import com.lvl6.server.controller.QuestProgressController;
import com.lvl6.server.controller.StartupController;
import com.lvl6.server.controller.TransferClanOwnershipController;
import com.lvl6.server.controller.UserCreateController;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;


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
	TransferClanOwnershipController transferClanOwnershipController;
	
	@Autowired
	QuestProgressController questProgressController;
	
	@Autowired
	EnhanceMonsterController enhanceMonsterController;
	
	@Autowired
	TimeUtils timeUtils;
	
	@Autowired
	UserRetrieveUtils userRetrieveUtils;
	
	@Autowired
	MonsterForUserRetrieveUtils monsterForUserRetrieveUtils;
	
	@Autowired
	GameServer server;
	
	@Autowired
	HazelcastPvpUtil hazelcastPvpUtil;
	
	
	@Resource(name = "playersByPlayerId")
	IMap<Integer, ConnectedPlayer> playersByPlayerId;

	@Resource(name = "playersPreDatabaseByUDID")
	IMap<String, ConnectedPlayer> playersPreDatabaseByUDID;


//	public void createUser(String udid) {
//		//need to add a new ConnectedPlayer
//		ConnectedPlayer newp = new ConnectedPlayer();
//		newp.setIp_connection_id("173.228.88.96");
//		newp.setServerHostName(server.serverId());
//		newp.setUdid(udid);
//		getPlayersPreDatabaseByUDID().put(newp.getUdid(), newp, DEFAULT_TTL, TimeUnit.MINUTES);
//		
//		String name = "bob";
//		
//		String deviceToken = "deviceToken";
//		long timeOfStructPurchase = (new Date()).getTime();
//		long timeOfStructBuild = timeOfStructPurchase;
//		
//		CoordinateProto.Builder cpb = CoordinateProto.newBuilder();
//		cpb.setX(1.1F);
//		cpb.setY(1.1F);
//		CoordinateProto cp = cpb.build();
//		boolean usedDiamondsToBuilt = true;
//		
//		UserCreateRequestProto.Builder ucrpb = UserCreateRequestProto.newBuilder();
//		ucrpb.setUdid(udid);
//		ucrpb.setName(name);
//		ucrpb.setDeviceToken(deviceToken);
//		ucrpb.setTimeOfStructPurchase(timeOfStructPurchase);
//		ucrpb.setTimeOfStructBuild(timeOfStructBuild);
//		ucrpb.setStructCoords(cp);
//		ucrpb.setUsedDiamondsToBuilt(usedDiamondsToBuilt);
//		
//		UserCreateRequestEvent ucre = new UserCreateRequestEvent();
//		ucre.setTag(1);
//		ucre.setUdid(udid);
//		ucre.setUserCreateRequestProto(ucrpb.build());
//		
//		getUserCreateController().handleEvent(ucre);
//	}
	
//	public void loginUser(String udid) {
//		StartupRequestProto.Builder srpb = StartupRequestProto.newBuilder();
//		srpb.setUdid(udid);
//		srpb.setVersionNum(3.6f);
//		srpb.setIsForceTutorial(false);
//		
//		StartupRequestEvent sre = new StartupRequestEvent();
//		sre.setStartupRequestProto(srpb.build());
//		
//		//have the controller process this event
//		getStartupController().handleEvent(sre);
//	}
	
//	public void purchaseCityExpansion(User aUser) {
//		int diamondChange = 0;
//		int coinChange = 50000;
//		int experienceChange = 0;
//		boolean successful = aUser.updateRelativeGemsCashExperienceNaive(
//				diamondChange, coinChange, experienceChange);
//		assertTrue(successful);
//		
//		PurchaseCityExpansionRequestProto.Builder pcerpb =
//				PurchaseCityExpansionRequestProto.newBuilder();
//		
//		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(aUser);
//		pcerpb.setSender(mup);
//		pcerpb.setXPosition(1);
//		pcerpb.setYPosition(1);
//		pcerpb.setTimeOfPurchase((new Date()).getTime());
//		
//		PurchaseCityExpansionRequestEvent pcere = new PurchaseCityExpansionRequestEvent();
//		pcere.setPurchaseCityExpansionRequestProto(pcerpb.build());
//		
//		getPurchaseCityExpansionController().handleEvent(pcere);
//	}

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
	
	
	
//	public void deleteUserById(int userId) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//    conditionParams.put(DBConstants.USER__ID, userId);
//
//		int numDeleted = DBConnection.get().deleteRows(
//				DBConstants.TABLE_USER, conditionParams, "and");
//		log.info("num users deleted=" + numDeleted);
//	}
	
//	public void deleteUserLoginHistory(int userId, String udid) {
//		String tableName = DBConstants.TABLE_LOGIN_HISTORY;
//		
//		String query = " DELETE FROM " + tableName + " WHERE " +
//				DBConstants.LOGIN_HISTORY__ID + " > ? AND " + 
//				DBConstants.LOGIN_HISTORY__USER_ID + " = ? OR " + 
//				DBConstants.LOGIN_HISTORY__UDID + " = ?;";
//		
//		List<Object> values = new ArrayList<Object>();
//		values.add(0); //for id 
//		values.add(userId);
//		values.add(udid);
//		    
//		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
//				query, values);
//		log.info("num login history rows deleted=" + numDeleted);
//	}
	
//	public void deleteFirstTimeUsers(String openUdid) {
//		String tableName = DBConstants.TABLE_USER_BEFORE_TUTORIAL_COMPLETION;
//		
//		String query = " DELETE FROM " + tableName + " WHERE " +
//				DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__ID + " > ? and " + 
//				DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID + " = ?;";
//		
//		List<Object> values = new ArrayList<Object>();
//		values.add(0); //for 
//		values.add(openUdid);
//		    
//		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
//				query, values);
//		log.info("num first time users deleted=" + numDeleted);
//	}
	
//	public void deleteUserCityExpansionData(int userId) {
//		String tableName = DBConstants.TABLE_EXPANSION_PURCHASE_FOR_USER;
//
//		String query = " DELETE FROM " + tableName + " WHERE " +
//				DBConstants.EXPANSION_PURCHASE_FOR_USER__USER_ID + " = ?;";
//		
//		List<Object> values = new ArrayList<Object>();
//		values.add(userId); //for 
//		    
//		int numDeleted = DBConnection.get().deleteDirectQueryNaive(
//				query, values);
//		log.info("num user city expansion data deleted=" + numDeleted);
//	}
	
	public void testPopulateMonsterLevelInfo() {
		/*Map<Integer, Monster> idsToMonsters = MonsterRetrieveUtils.getMonsterIdsToMonsters();
		
		if (null == idsToMonsters || idsToMonsters.isEmpty()) {
			return;
		}
		
		Set<Integer> monsterIds = idsToMonsters.keySet();
		List<Integer> ascendingIds = new ArrayList<Integer>(monsterIds);
		Collections.sort(ascendingIds);
		
//		List<String> columns = new ArrayList<String>();
//		
//		StringBuilder insertSb = new StringBuilder();
//		insertSb.append("INSERT INTO ");
//		insertSb.append(DBConstants.TABLE_MONSTER_LEVEL_INFO);
//		insertSb.append(" (monster_id, level) VALUES ("); //NOTE: THESE COLUMNS ARE DEPENDING ON THE TABLE!!!!!
//		
//		List<String> idAndLvl = new ArrayList<String>();
//		for (Integer monsterId : ascendingIds) {
//			Monster mon = idToMonsters.get(monsterId);
//			
//			int maxLvl = mon.getMaxLevel();
//			
//			for (int lvl = 1; lvl <= maxLvl; lvl++) {
//				StringBuilder idAndLvlSb = new StringBuilder();
//				idAndLvlSb.append("(");
//				idAndLvlSb.append(monsterId);
//				idAndLvlSb.append(", ");
//				idAndLvlSb.append(lvl);
//				idAndLvlSb.append(")");
//				
//				String idAndLvlStr = idAndLvlSb.toString();
//				idAndLvl.add(idAndLvlStr);
//			}
//		}
//		String idLvlStr = com.lvl6.utils.utilmethods.StringUtils.csvList(idAndLvl);
//		insertSb.append(idLvlStr);
//		insertSb.append(")");

		List<Integer> allMonsterIds = new ArrayList<Integer>();
		List<Integer> allLvls = new ArrayList<Integer>();
		for (Integer monsterId : ascendingIds) {
			Monster mon = idsToMonsters.get(monsterId);
			int maxLvl = mon.getMaxLevel();

			for (int lvl = 1; lvl <= maxLvl; lvl++) {
				allMonsterIds.add(monsterId);
				allLvls.add(lvl);
			}
		}
		
		Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
    int numRows = allMonsterIds.size();

    insertParams.put("monster_id", allMonsterIds);
    insertParams.put("level", allLvls);
    
    int numInserted = DBConnection.get().insertIntoTableMultipleRows(
    		DBConstants.TABLE_MONSTER_LEVEL_INFO, insertParams, numRows);
    
		log.info("expected: " + numRows + ", but numInserted=" + numInserted);*/
	}
	
	@Test
	public void testDateTime() {
//		log.info("testing date time");
//		
//		DateTime dt = new DateTime(new Date(), DateTimeZone.UTC);
//		Date now = dt.toDate();
//		log.info("now UTC= " + now);
//		
//		int minutesAddend = 90;
//		Date startDate = getTimeUtils().createPstDate(now, -6, 0, minutesAddend);
//		
//		log.info("startDate= " + startDate);
//		
//		minutesAddend = 1440;
//		Date endDate = getTimeUtils().createPstDateAddMinutes(startDate, minutesAddend);
//		log.info("endDate= " + endDate);
	}
	
	@Test
	public void testActiveEvents() {
		Map<Integer, ClanEventPersistent> activeEvents = ClanEventPersistentRetrieveUtils
				.getActiveClanEventIdsToEvents(new Date(), getTimeUtils());
		
		log.info("activeEvents=");
		log.info(activeEvents + "");
	}

	@Test
	public void testHazelcastPvpUtilQuery() {
		log.info("CREATING HAZELCAST PVP QUERY");
		
		int minElo = 5;
		int maxElo = 7;
		Date now = new Date();
		int limit = 5;
		Collection<String> excludeIds = new ArrayList<String>();
		excludeIds.add("1");
		excludeIds.add("a");
		
		Predicate<?, ?> predicate = null;
		try {
			predicate = getHazelcastPvpUtil().generatePredicate(minElo,
					maxElo, now, limit, excludeIds);
		} catch(Exception e) {
			log.error("exception creating hazelcast pvp query.", e);
		}
		
		assertNotNull(predicate);
	}
	/*
	@Test
	public void testTransferClanOwnership() {
		User aUser = getUserRetrieveUtils().getUserById(379);
		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(aUser);
		int clanOwnerIdNew = 574;
		
		TransferClanOwnershipRequestProto.Builder reqProto = TransferClanOwnershipRequestProto.newBuilder(); 
		reqProto.setSender(mup);
		reqProto.setClanOwnerIdNew(clanOwnerIdNew);
		
		TransferClanOwnershipRequestEvent reqEvent = new TransferClanOwnershipRequestEvent();
		reqEvent.setTag(1);
		reqEvent.setTransferClanOwnershipRequestProto(reqProto.build());
		
		getTransferClanOwnershipController().handleEvent(reqEvent);
		
	}
	
	@Test
	public void testQuestProgress() {
		User aUser = getUserRetrieveUtils().getUserById(379);
		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(aUser);
		
		QuestProgressRequestProto.Builder reqProto = QuestProgressRequestProto.newBuilder();
		reqProto.setSender(mup);
		reqProto.setQuestId(20);
		reqProto.setIsComplete(true);
		
		UserQuestJobProto.Builder uqjpBuilder = UserQuestJobProto.newBuilder();
		uqjpBuilder.setQuestId(20);
		uqjpBuilder.setQuestJobId(20);
		uqjpBuilder.setIsComplete(true);
		uqjpBuilder.setProgress(4);
		
		List<UserQuestJobProto> lizt = new ArrayList<UserQuestJobProto>();
		lizt.add(uqjpBuilder.build());
		
		reqProto.addAllUserQuestJobs(lizt);
		reqProto.addAllDeleteUserMonsterIds(Arrays.asList(9195L, 9196L, 9197L, 9198L));
		
		QuestProgressRequestEvent reqEvent = new QuestProgressRequestEvent();
		reqEvent.setTag(1);
		reqEvent.setQuestProgressRequestProto(reqProto.build());
		
		getQuestProgressController().handleEvent(reqEvent);
	}
	*/
	
	@Test
	public void testEnhanceMonster() {
		int unitTesterId = getUnitTesterId();
		User unitTester = getUserRetrieveUtils().getUserById(unitTesterId);
		Date now = new Date();
		//get num monsters
		List<MonsterForUser> mfuList = monsterForUserRetrieveUtils.getMonstersForUser(unitTesterId);
		assertNotNull(mfuList);
		assertTrue(String.format(
			"Monsters should exist, but don't. %s",
				mfuList),
			!mfuList.isEmpty());
		
		//add 'n' monsters
		Map<Long, MonsterForUser> newMonsters = createCompleteMonsters(unitTesterId, now);
		log.info(String.format("newMonsters=%s", newMonsters));
		int newMonsterCount = newMonsters.size();
		assertNotNull(newMonsters);
		
		List<MonsterForUser> mfuListTwo = monsterForUserRetrieveUtils.getMonstersForUser(unitTesterId);
				
		assertEquals(String.format(
			"supposed to be n=%s more monsters. old=%s, new=%s, expectedDelta=%s",
			newMonsterCount, mfuList, mfuListTwo, newMonsters ),
			mfuList.size() + newMonsterCount,
			mfuListTwo.size());
		
		//submit EnhanceMonsterRequest with these 'n' newly added monsters
		Long enhancedMonsterId = submitEnhanceMonsterRequest(unitTester, newMonsters);
		
		//check to make sure num monsters is down by 1
		Map<Long, MonsterForUser> mfuMapThree = monsterForUserRetrieveUtils.getSpecificOrAllUserMonstersForUser(unitTesterId, null);
		assertEquals(String.format(
			"supposed to be n=%s more monsters. old=%s, new=%s",
			newMonsterCount - 1, mfuList, mfuMapThree),
			mfuList.size() + newMonsterCount - 1,
			mfuMapThree.size());
		
		//check to make sure the newly enhanced monster changed
		MonsterForUser enhancedMonster = mfuMapThree.get(enhancedMonsterId);
		assertEquals(
			String.format( "exp should have been: %s", expectedEnhancedMonsterExpLvlHp),
			expectedEnhancedMonsterExpLvlHp, enhancedMonster.getCurrentExp());
		assertEquals(
			String.format( "lvl should have been: %s", expectedEnhancedMonsterExpLvlHp),
			expectedEnhancedMonsterExpLvlHp, enhancedMonster.getCurrentLvl());
		assertEquals(
			String.format( "health should have been: %s", expectedEnhancedMonsterExpLvlHp),
			expectedEnhancedMonsterExpLvlHp, enhancedMonster.getCurrentHealth());
		
		//delete it
		DeleteUtils.get().deleteMonsterForUser(enhancedMonster.getId());
	}
	
	private Map<Long, MonsterForUser> createCompleteMonsters(int userId,
		Date now) {
		
		List<Monster> twoMonsters = new ArrayList<Monster>(
			MonsterRetrieveUtils.getMonsterIdsToMonsters().values());
		twoMonsters = twoMonsters.subList(0, 2);
		
		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
		for (Monster monzter : twoMonsters) {
			//create a "complete" user monster
			boolean hasAllPieces = true;
			boolean isComplete = true;
			MonsterForUser newUserMonster = MonsterStuffUtils.createNewUserMonster(
				userId, monzter.getNumPuzzlePieces(), monzter, now, hasAllPieces, isComplete);

			//return this monster in the argument list completeUserMonsters, so caller
			//can use it
			completeUserMonsters.add(newUserMonster);
		}
		
		String mfusop = "ControllerTest.createCompleteMonsters";
		List<Long> monsterForUserIds = InsertUtils.get()
			.insertIntoMonsterForUserReturnIds(userId, completeUserMonsters, mfusop, now);
	
		Map<Long, MonsterForUser> returnMap = new HashMap<Long, MonsterForUser>();
		for (int index = 0; index < twoMonsters.size(); index++) {
			MonsterForUser mfu = completeUserMonsters.get(index);
			Long mfuId = monsterForUserIds.get(index);
			
			mfu.setId(mfuId);
			returnMap.put(mfuId, mfu);
		}
		return returnMap;
	}
	
	private static int expectedEnhancedMonsterExpLvlHp = 1;
	private Long submitEnhanceMonsterRequest(User aUser, Map<Long, MonsterForUser> mfuMap) {
		//create the arguments for the event
		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUser(aUser);
		List<UserEnhancementItemProto> feeders = new ArrayList<UserEnhancementItemProto>();
		
		for (MonsterForUser mfu: mfuMap.values()) {

		    UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto.newBuilder();
		    ueipb.setUserMonsterId(mfu.getId());
		    ueipb.setEnhancingCost(0);
		    feeders.add(ueipb.build());
		}
		
		UserEnhancementItemProto baseMonster = feeders.remove(0);
		
		UserEnhancementProto uep = CreateInfoProtoUtils.createUserEnhancementProtoFromObj(
			aUser.getId(), baseMonster, feeders);
		
		UserMonsterCurrentExpProto.Builder result = UserMonsterCurrentExpProto.newBuilder();
		result.setUserMonsterId(baseMonster.getUserMonsterId());
		result.setExpectedExperience(expectedEnhancedMonsterExpLvlHp);
		result.setExpectedLevel(expectedEnhancedMonsterExpLvlHp);
		result.setExpectedHp(expectedEnhancedMonsterExpLvlHp);
		
		int gemsSpent = 1;
		int oilChange = 1;
		
		EnhanceMonsterRequestProto.Builder eventBuilder =
			EnhanceMonsterRequestProto.newBuilder();
		eventBuilder.setSender(mup);
		eventBuilder.setUep(uep);
		eventBuilder.setEnhancingResult(result.build());
		eventBuilder.setGemsSpent(gemsSpent);
		eventBuilder.setOilChange(oilChange);

		//give the user money
		aUser.updateRelativeCashAndOilAndGems(0, 1, 1);
		
		//generate the event
		EnhanceMonsterRequestEvent event = new EnhanceMonsterRequestEvent();
		event.setTag(0);
		event.setEnhanceMonsterRequestProto(eventBuilder.build());
		enhanceMonsterController.handleEvent(event);
		
		return baseMonster.getUserMonsterId();
	}
	
	private int getUnitTesterId() {
		return 11;
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

	public TransferClanOwnershipController getTransferClanOwnershipController()
	{
		return transferClanOwnershipController;
	}

	public void setTransferClanOwnershipController(
		TransferClanOwnershipController transferClanOwnershipController )
	{
		this.transferClanOwnershipController = transferClanOwnershipController;
	}

	public QuestProgressController getQuestProgressController()
	{
		return questProgressController;
	}
	public void setQuestProgressController( QuestProgressController questProgressController )
	{
		this.questProgressController = questProgressController;
	}

	public EnhanceMonsterController getEnhanceMonsterController()
	{
		return enhanceMonsterController;
	}

	public void setEnhanceMonsterController( EnhanceMonsterController enhanceMonsterController )
	{
		this.enhanceMonsterController = enhanceMonsterController;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
	
	public MonsterForUserRetrieveUtils getMonsterForUserRetrieveUtils()
	{
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
		MonsterForUserRetrieveUtils monsterForUserRetrieveUtils )
	{
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}


	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}
	
	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}


	public IMap<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(
			IMap<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}
	
	public IMap<String, ConnectedPlayer> getPlayersPreDatabaseByUDID() {
		return playersPreDatabaseByUDID;
	}
	
	public void setPlayersPreDatabaseByUDID(
			IMap<String, ConnectedPlayer> playersPreDatabaseByUDID) {
		this.playersPreDatabaseByUDID = playersPreDatabaseByUDID;
	}
	
}
