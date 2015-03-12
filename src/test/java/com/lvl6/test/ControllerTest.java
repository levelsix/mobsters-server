package com.lvl6.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.lvl6.events.request.CollectMonsterEnhancementRequestEvent;
import com.lvl6.events.request.CompleteBattleItemRequestEvent;
import com.lvl6.events.request.CreateBattleItemRequestEvent;
import com.lvl6.events.request.DestroyMoneyTreeStructureRequestEvent;
import com.lvl6.events.request.FinishPerformingResearchRequestEvent;
import com.lvl6.events.request.InAppPurchaseRequestEvent;
import com.lvl6.events.request.PerformResearchRequestEvent;
import com.lvl6.events.request.SellUserMonsterRequestEvent;
import com.lvl6.events.request.SubmitMonsterEnhancementRequestEvent;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.ResearchForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.StaticDataContainer;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemRequestProto;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemRequestProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseRequestProto;
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchRequestProto;
import com.lvl6.proto.EventResearchProto.PerformResearchRequestProto;
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureRequestProto;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterSellProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.BattleItemQueueForUserRetrieveUtil;
import com.lvl6.retrieveutils.ClanInviteRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterEnhancingForUserRetrieveUtils2;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.server.controller.CollectMonsterEnhancementController;
import com.lvl6.server.controller.CompleteBattleItemController;
import com.lvl6.server.controller.CreateBattleItemController;
import com.lvl6.server.controller.DestroyMoneyTreeStructureController;
import com.lvl6.server.controller.DevController;
import com.lvl6.server.controller.EnhancementWaitTimeCompleteController;
import com.lvl6.server.controller.EvolutionFinishedController;
import com.lvl6.server.controller.EvolveMonsterController;
import com.lvl6.server.controller.FinishPerformingResearchController;
import com.lvl6.server.controller.InAppPurchaseController;
import com.lvl6.server.controller.PerformResearchController;
import com.lvl6.server.controller.RetrieveClanInfoController;
import com.lvl6.server.controller.SellUserMonsterController;
import com.lvl6.server.controller.StartupController;
import com.lvl6.server.controller.SubmitMonsterEnhancementController;
import com.lvl6.server.controller.TransferClanOwnershipController;
import com.lvl6.server.controller.UserCreateController;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ControllerTest extends TestCase {
	private static final int DEFAULT_TTL = 9;
	
	private JdbcTemplate jdbcTemplate;

	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
//	@Autowired
//	PurchaseCityExpansionController purchaseCityExpansionController;
	
	@Autowired
	StartupController startupController;
	
	@Autowired
	UserCreateController userCreateController;
	
	@Autowired
	TransferClanOwnershipController transferClanOwnershipController;
	
//	@Autowired
//	QuestProgressController questProgressController;
//	
//	@Autowired
//	EnhanceMonsterController enhanceMonsterController;
	
	@Autowired
	DevController devController;
	
	@Autowired
	RetrieveClanInfoController retrieveClanInfoController;
	
	@Autowired
	ResearchForUserRetrieveUtils researchForUserRetrieveUtil;
	
	@Autowired
	InsertUtil insertUtil;
	
	@Autowired
	UpdateUtil updateUtil;
	
//	@Autowired
//	InviteToClanController inviteToClanController;
	
	@Autowired
	EvolveMonsterController evolveMonsterController;
	
	@Autowired
	EvolutionFinishedController evolutionFinishedController;
	
	@Autowired
	SubmitMonsterEnhancementController submitMonsterEnhancementController;
	
	@Autowired
	EnhancementWaitTimeCompleteController enhancementWaitTimeCompleteController;
	
	@Autowired
	CollectMonsterEnhancementController collectMonsterEnhancementController;
	
	@Autowired
	SellUserMonsterController sellUserMonsterController;
	
	@Autowired
	PerformResearchController performResearchController;
	
	@Autowired
	InAppPurchaseController inAppPurchaseController;
	
	@Autowired
	DestroyMoneyTreeStructureController destroyMoneyTreeStructureController;
	
	@Autowired 
	FinishPerformingResearchController finishPerformingResearchController;
	
	@Autowired
	CreateBattleItemController createBattleItemController;
	
	@Autowired
	CompleteBattleItemController completeBattleItemController;
	
	@Autowired
	BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	
	@Autowired
	BattleItemQueueForUserRetrieveUtil battleItemQueueForUserRetrieveUtil;
	
	@Autowired
	StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2;
	
	@Autowired
	MonsterEnhancingForUserRetrieveUtils2 monsterEnhancingForUserRetrieveUtil;
	
	@Autowired
	TimeUtils timeUtils;
	
	@Autowired
	UserRetrieveUtils2 userRetrieveUtil;
	
	@Autowired
	MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
	
	@Autowired
	ClanRetrieveUtils2 clanRetrieveUtil;
	
	@Autowired
	ClanInviteRetrieveUtil clanInviteRetrieveUtil;
	
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
//		User aUser = userRetrieveUtil.getUserByUDID(udid);
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
		
		//just to see if optimizations from commit  worked
		/*String udid = "e6e393e9dda799377ca53ca0d3de739b73b137ba";
		float versionNum = 1.0F;
		String macAddress = "";
		String advertiserId = "76864DFE-1BF1-47B5-ADE7-6530F5E0B9A3";
		boolean isTutorial = false;
		String fbId = "";//"2500169137658";
		boolean isFreshRestart = true;
		
		StartupRequestProto.Builder srpb = StartupRequestProto.newBuilder();
		srpb.setUdid(udid);
		srpb.setVersionNum(versionNum);
		srpb.setMacAddress(macAddress);
		srpb.setAdvertiserId(advertiserId);
		srpb.setIsForceTutorial(isTutorial);
		srpb.setFbId(fbId);
		srpb.setIsFreshRestart(isFreshRestart);
		
		StartupRequestEvent sre = new StartupRequestEvent();
		sre.setStartupRequestProto(srpb.build());
		
		startupController.handleEvent(sre);
		log.info("done");*/
	}
	
	
	@Test
	public void testPurchaseCityExpansion() {
//		String udid = "udid";
//		createUser(udid);
//		User aUser = userRetrieveUtil.getUserByUDID(udid);
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
		User aUser = userRetrieveUtil.getUserById(379);
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
		User aUser = userRetrieveUtil.getUserById(379);
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
	
	/*
	@Test
	public void testEnhanceMonster() {
		int unitTesterId = getUnitTesterId();
		User unitTester = userRetrieveUtil.getUserById(unitTesterId);
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
	*/
	
	private Map<String, MonsterForUser> createCompleteMonsters(String userId,
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
		List<String> monsterForUserIds = InsertUtils.get()
			.insertIntoMonsterForUserReturnIds(userId, completeUserMonsters, mfusop, now);
	
		Map<String, MonsterForUser> returnMap = new HashMap<String, MonsterForUser>();
		for (int index = 0; index < twoMonsters.size(); index++) {
			MonsterForUser mfu = completeUserMonsters.get(index);
			String mfuId = monsterForUserIds.get(index);
			
			mfu.setId(mfuId);
			returnMap.put(mfuId, mfu);
		}
		return returnMap;
	}
	
	private static int expectedEnhancedMonsterExpLvlHp = 1;
//	private String submitEnhanceMonsterRequest(User aUser, Map<String, MonsterForUser> mfuMap) {
//		//create the arguments for the event
//		MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(aUser, null);
//		List<UserEnhancementItemProto> feeders = new ArrayList<UserEnhancementItemProto>();
//		
//		for (MonsterForUser mfu: mfuMap.values()) {
//
//		    UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto.newBuilder();
//		    ueipb.setUserMonsterUuid(mfu.getId());
//		    ueipb.setEnhancingCost(0);
//		    feeders.add(ueipb.build());
//		}
//		
//		UserEnhancementItemProto baseMonster = feeders.remove(0);
//		
//		UserEnhancementProto uep = CreateInfoProtoUtils.createUserEnhancementProtoFromObj(
//			aUser.getId(), baseMonster, feeders);
//		
//		UserMonsterCurrentExpProto.Builder result = UserMonsterCurrentExpProto.newBuilder();
//		result.setUserMonsterUuid(baseMonster.getUserMonsterUuid());
//		result.setExpectedExperience(expectedEnhancedMonsterExpLvlHp);
//		result.setExpectedLevel(expectedEnhancedMonsterExpLvlHp);
//		result.setExpectedHp(expectedEnhancedMonsterExpLvlHp);
//		
//		int gemsSpent = 1;
//		int oilChange = 1;
//		
//		EnhanceMonsterRequestProto.Builder eventBuilder =
//			EnhanceMonsterRequestProto.newBuilder();
//		eventBuilder.setSender(mup);
//		eventBuilder.setUep(uep);
//		eventBuilder.setEnhancingResult(result.build());
//		eventBuilder.setGemsSpent(gemsSpent);
//		eventBuilder.setOilChange(oilChange);
//
//		//give the user money
//		aUser.updateRelativeCashAndOilAndGems(0, 1, 1);
//		
//		//generate the event
//		EnhanceMonsterRequestEvent event = new EnhanceMonsterRequestEvent();
//		event.setTag(0);
//		event.setEnhanceMonsterRequestProto(eventBuilder.build());
//		enhanceMonsterController.handleEvent(event);
//		
//		return baseMonster.getUserMonsterUuid();
//	}
	
	@Test
	public void testDevControllerAwardMonster() {
		/*String unitTesterId = getUnitTesterId();
		User unitTester = userRetrieveUtil.getUserById(unitTesterId);
		
		//get num monsters
		List<MonsterForUser> mfuList = monsterForUserRetrieveUtils.getMonstersForUser(unitTesterId);
		assertNotNull(mfuList);
		assertTrue(String.format(
			"Monsters should exist, but don't. %s",
			mfuList),
			!mfuList.isEmpty());

		//build arguments
		DevRequestProto.Builder drpb = DevRequestProto.newBuilder();
		drpb.setSender(
			CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(unitTester, null));
		drpb.setDevRequest(DevRequest.GET_MONZTER);
		drpb.setStaticDataId(ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID);
		drpb.setQuantity(1);
		
		DevRequestEvent event = new DevRequestEvent();
		event.setTag(0);
		event.setDevRequestProto(drpb.build());
		
		//call controller
		devController.handleEvent(event);
		
		List<MonsterForUser> mfuListTwo = monsterForUserRetrieveUtils.getMonstersForUser(unitTesterId);
		assertTrue(String.format(
				"one more monster should have been added to %s, but is %s",
				mfuList, mfuListTwo),
			mfuListTwo.size() == (mfuList.size() + 1));
		
		mfuListTwo.removeAll(mfuList);
		
		DeleteUtils.get().deleteMonsterForUser(mfuListTwo.get(0).getId());*/
	}
	
//	@Test
//	public void testRetrieveClanInfo() {
//		int errorId = 1098;
//		User error = userRetrieveUtil.getUserById(errorId);
//		int clanId = 66;
//		Clan c = ClanRetrieveUtils.getClanWithId(clanId);
//		boolean isForBrowsingList = false;
//		ClanInfoGrabType cigt = ClanInfoGrabType.ALL;
//		
//		RetrieveClanInfoRequestProto.Builder rcirpb = RetrieveClanInfoRequestProto.newBuilder();
//		rcirpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(error, c));
//		rcirpb.setClanId(clanId);
//		rcirpb.setGrabType(cigt);
//		rcirpb.setIsForBrowsingList(isForBrowsingList);
//		
//		RetrieveClanInfoRequestEvent rcire = new RetrieveClanInfoRequestEvent();
//		rcire.setTag(1);
//		rcire.setRetrieveClanInfoRequestProto(rcirpb.build());
//		
//		retrieveClanInfoController.handleEvent(rcire);
//		log.info("Done");
//	}
	
//	@Test
//	public void testDialogueCreation() {
//		TaskRetrieveUtils.getTaskIdsToTasks();
//		
//	}
	
	@Test
	public void testClanInvite() {
		/*String jackMayHoff = "1110";
		User jmh = userRetrieveUtil.getUserById(jackMayHoff);
		UpdateUtils.get().updateUserClanStatus(jackMayHoff, jmh.getClanId(), UserClanStatus.JUNIOR_LEADER);
		Clan c = clanRetrieveUtil.getClanWithId(jmh.getClanId());
		String prospectiveMemberId = getUnitTesterId();
		ClanInvite ci = clanInviteRetrieveUtil.getClanInvite(prospectiveMemberId, jackMayHoff);
		assertNull(ci);
		
		InviteToClanRequestProto.Builder itcrpb = InviteToClanRequestProto.newBuilder();
		itcrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(jmh, c));
		itcrpb.setProspectiveMember(prospectiveMemberId);
		itcrpb.setClientTime((new Date()).getTime());
		
		InviteToClanRequestEvent icre = new InviteToClanRequestEvent();
		icre.setTag(1);
		icre.setInviteToClanRequestProto(itcrpb.build());
		
//		inviteToClanController.handleEvent(icre);
//		ci = clanInviteRetrieveUtil.getClanInvite(prospectiveMemberId, jackMayHoff);
//		assertNotNull(ci);
		
		DeleteUtils.get().deleteClanInvite(prospectiveMemberId, null);
		ci = clanInviteRetrieveUtil.getClanInvite(prospectiveMemberId, jackMayHoff);
		assertNull(ci);*/
	}
	
//	@Test
//	public void testUserResearchInsert() {
//		String userId = "abcd";
//		Research research = ResearchRetrieveUtils.getResearchForId(10000);
//		Date date = new Date();
//		Timestamp timestamp = new Timestamp(date.getTime());
//		String userResearchId = null; 
//		userResearchId = insertUtil.insertUserResearch(userId, research, timestamp, false);
//		
//		assertNotNull(userResearchId);
//
//		
//	}
	
//	@Test
//	public void testUpdateUserResearchIsComplete() {
//		//User user = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
//		updateUtil.updateUserResearchCompleteStatus("326acfa8-0b37-478d-84d6-5f99d481fdef");
//		assertTrue(true);
//	}
	

	@Test
	public void testResearch() {
		
		//create test user
//		Map<String, Object> userInsertParams = new HashMap<String, Object>();
//		userInsertParams.put("id", "abcd");
//		userInsertParams.put("name", "tester");
//		userInsertParams.put("level", 1);
//		userInsertParams.put("gems", 100);
//		userInsertParams.put("cash", 100);
//		userInsertParams.put("oil", 100);
//		userInsertParams.put("experience", 1);
//		userInsertParams.put("udid_for_history", "abcd");
//		userInsertParams.put("last_login", "2014-12-02 03:59:27");
//		userInsertParams.put("is_fake", 0);
//		userInsertParams.put("is_admin", 0);
//		userInsertParams.put("num_coins_retrieved_from_structs", 0);
//		userInsertParams.put("num_oil_retrieved_from_structs", 0);
//		userInsertParams.put("last_obstacle_spawned_time", "2014-12-02 03:59:27");
//		
//		DBConnection.get().insertIntoTableBasic(DBConstants.TABLE_USER, userInsertParams);
		
		//create test spell
		Map<String, Object> researchInsertParams = new HashMap<String, Object>();
		researchInsertParams.put("id", 10002);
		researchInsertParams.put("name", "spell3");
		researchInsertParams.put("cost_amt", 100);
		researchInsertParams.put("cost_type", "OIL");
		
		DBConnection.get().insertIntoTableBasic(DBConstants.TABLE_RESEARCH_CONFIG, researchInsertParams);
		
		
		User user = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		int userGems = user.getGems();
		PerformResearchRequestProto.Builder prrpb = PerformResearchRequestProto.newBuilder();
		
		prrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		prrpb.setResearchId(10002);
		Date date = new Date();
		prrpb.setClientTime(date.getTime());
		prrpb.setGemsSpent(25);
		prrpb.setResourceChange(100);
		ResourceType rt = ResourceType.OIL;
		prrpb.setResourceType(rt);
		
		
		PerformResearchRequestEvent prre = new PerformResearchRequestEvent();
		prre.setTag(1);
		prre.setPerformResearchRequestProto(prrpb.build());
		performResearchController.handleEvent(prre);
		
		int userGems3 = user.getGems();
		int userOil = user.getOil();
		List<ResearchForUser> rfuList = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
		//assertNotNull(rfuList);
		String userResearchUuid = null;
		for(ResearchForUser rfu : rfuList) {
			if(rfu.getResearchId() == 10002) {
				assertEquals(rfu.getUserId(), user.getId());
				User rfuUser = userRetrieveUtil.getUserById(rfu.getUserId());
				assertEquals(rfu.getResearchId(), 10002);
				userResearchUuid = rfu.getId();
				assertEquals(userOil-100, rfuUser.getOil());
				assertEquals(userGems3-25, rfuUser.getGems());
			}
			
		}
//		assertTrue(!rfuList.isEmpty());
//		User user2 = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
//		
//		
//		
//		FinishPerformingResearchRequestProto.Builder fprrpb = FinishPerformingResearchRequestProto.newBuilder();
//		fprrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
//		fprrpb.setUserResearchUuid(userResearchUuid);
//		
//		FinishPerformingResearchRequestEvent fprre = new FinishPerformingResearchRequestEvent();
//		fprre.setTag(1);
//		fprre.setFinishPerformingResearchRequestProto(fprrpb.build());
//		finishPerformingResearchController.handleEvent(fprre);
//		List<ResearchForUser> rfuList2 = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
//		boolean isComplete2 = false;
//		for(ResearchForUser rfu2 : rfuList2) {
//			isComplete2 = rfu2.isComplete();
//		}
//		assertTrue(isComplete2);
//		
//		
//		//create test spell
//		Map<String, Object> researchInsertParams2 = new HashMap<String, Object>();
//		researchInsertParams.put("id", 10001);
//		researchInsertParams.put("name", "spell2");
//		researchInsertParams.put("cost_amt", 100);
//		researchInsertParams.put("cost_type", "CASH");
//		
//		DBConnection.get().insertIntoTableBasic(DBConstants.TABLE_RESEARCH_CONFIG, researchInsertParams);
//
//		int userCash = user.getCash();
//		int userGems2 = user.getGems();
//		prrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
//		prrpb.setResearchId(10001);
//		prrpb.setClientTime(date.getTime());
//		prrpb.setGemsSpent(25);
//		prrpb.setUserResearchUuid(userResearchUuid);
//		prrpb.setResourceChange(50);
//		ResourceType rt = ResourceType.CASH;
//		prrpb.setResourceType(rt);
//		
//		PerformResearchRequestEvent prre2 = new PerformResearchRequestEvent();
//		prre2.setTag(1);
//		prre2.setPerformResearchRequestProto(prrpb.build());
//		performResearchController.handleEvent(prre2);
//		
//		List<ResearchForUser> rfuList3 = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
//		for(ResearchForUser rfu: rfuList3) {
//			assertEquals(rfu.getUserId(), user.getId());
//			User currUser = userRetrieveUtil.getUserById(rfu.getUserId());
//			assertFalse(rfu.isComplete());
//			assertTrue(rfu.getResearchId() == 10001);
//			assertEquals(userCash-50, currUser.getCash());
//			assertEquals(userGems2-25, currUser.getGems());
//		}
//
//		
	}

	@Test
	public void testUpgradeResearch() {
		User user = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		Date date = new Date();
		String userResearchUuid = "239c8623-c346-4472-87fa-6faecdcb2039";
		//create test spell
		Map<String, Object> researchInsertParams2 = new HashMap<String, Object>();
		researchInsertParams2.put("id", 10001);
		researchInsertParams2.put("name", "spell2");
		researchInsertParams2.put("cost_amt", 100);
		researchInsertParams2.put("cost_type", "CASH");

		DBConnection.get().insertIntoTableBasic(DBConstants.TABLE_RESEARCH_CONFIG, researchInsertParams2);

		PerformResearchRequestProto.Builder prrpb = PerformResearchRequestProto.newBuilder();

		int userCash = user.getCash();
		int userGems2 = user.getGems();
		prrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		prrpb.setResearchId(10001);
		prrpb.setClientTime(date.getTime());
		prrpb.setGemsSpent(25);
		prrpb.setUserResearchUuid(userResearchUuid);
		prrpb.setResourceChange(50);
		ResourceType rt = ResourceType.CASH;
		prrpb.setResourceType(rt);

		PerformResearchRequestEvent prre2 = new PerformResearchRequestEvent();
		prre2.setTag(1);
		prre2.setPerformResearchRequestProto(prrpb.build());
		performResearchController.handleEvent(prre2);

		List<ResearchForUser> rfuList3 = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
		for(ResearchForUser rfu: rfuList3) {
			assertEquals(rfu.getUserId(), user.getId());
			User currUser = userRetrieveUtil.getUserById(rfu.getUserId());
			assertFalse(rfu.isComplete());
			assertTrue(rfu.getResearchId() == 10001);
			assertEquals(userCash-50, currUser.getCash());
			assertEquals(userGems2-25, currUser.getGems());
		}

	}
	
	@Test
	public void testFinishResearch() {
		User user = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		Date date = new Date();
		String userResearchUuid = "0185e5f9-622a-415b-8444-d3743cbf8442";
		
		FinishPerformingResearchRequestProto.Builder fprrpb = FinishPerformingResearchRequestProto.newBuilder();
		fprrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		fprrpb.setUserResearchUuid(userResearchUuid);
		fprrpb.setGemsSpent(50);
		
		FinishPerformingResearchRequestEvent fprre = new FinishPerformingResearchRequestEvent();
		fprre.setTag(1);
		fprre.setFinishPerformingResearchRequestProto(fprrpb.build());
		finishPerformingResearchController.handleEvent(fprre);
		List<ResearchForUser> rfuList2 = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
		boolean isComplete2 = false;
		for(ResearchForUser rfu2 : rfuList2) {
			assertFalse(rfu2.isComplete());
			
		}
	}
	
	//buy money tree twice, then destroy it
	@Test
	public void testBuyingMoneyTree() {
		User user = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		InAppPurchaseRequestProto.Builder iaprpb = InAppPurchaseRequestProto.newBuilder();
		iaprpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		String receipt1 = "ewoJInNpZ25hdHVyZSIgPSAiQXJCRkpXdkttVytTMHlRNlU5cmh5azVnWE93ajgwZCtnNm"
				+ "NDRjdxOFNnbjU4OEhZY2VVR3h1aE9QKzV3NERKVDAvdjBwQ3VTbFArV3JvOWV0YmRRZFZOcE1YdWVDc"
				+ "GNvdEVpc2FRbzVuUE1rTFdkQUhHSVFtbFBVbnVzVEhZUmh2djZhTC9ITVc3ZXNDL2d4Ti92dkRCWWxu"
				+ "bXYvNWdPb3lLbjJzekFXVG5keUFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BME"
				+ "dDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pT"
				+ "QkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVU"
				+ "RXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1F"
				+ "YVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1"
				+ "DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTU"
				+ "VrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR"
				+ "0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamlt"
				+ "THdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg5RDU"
				+ "1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STS"
				+ "tRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQ"
				+ "TFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhH"
				+ "QTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0"
				+ "lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVT"
				+ "UxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllIS"
				+ "ytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU"
				+ "1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQ"
				+ "cFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRF"
				+ "Y1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzRHhKemViU3Bi"
				+ "YUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9"
				+ "ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUxTFRBeUx"
				+ "USXpJREUyT2pBek9qRXdJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMxcFp"
				+ "HVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1"
				+ "qWmpNR0U0TXpoa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01"
				+ "EQXdNREUwTkRZd01UUTVNaUk3Q2draVluWnljeUlnUFNBaU1TNHhMakV3TGpFeU5TSTdDZ2tpZEhKaGJ"
				+ "uTmhZM1JwYjI0dGFXUWlJRDBnSWpFd01EQXdNREF4TkRRMk1ERTBPVElpT3dvSkluRjFZVzUwYVhSNUl"
				+ "pQTlJQ0l4SWpzS0NTSnZjbWxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRJME5"
				+ "6TTJNVGt3T1RJeUlqc0tDU0oxYm1seGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWtJM1J"
				+ "VWkdNamcwTFVJeE5Ea3ROREJGUmkxQ056STBMVEV5UXpRek1qVTJRMEkyTVNJN0Nna2ljSEp2WkhWamR"
				+ "DMXBaQ0lnUFNBaVkyOXRMbk5qYjNCbGJIa3ViVzlpYzNGMVlXUXVjM1JoY25SbGNuQmhZMnNpT3dvSkl"
				+ "tbDBaVzB0YVdRaUlEMGdJamsyTlRrNU16a3hNaUk3Q2draVltbGtJaUE5SUNKamIyMHViSFpzTmk1dGI"
				+ "ySnpkR1Z5Y3lJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxdGN5SWdQU0FpTVRReU5EY3pOakU1TURreU1"
				+ "pSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNaTB5TkNBd01Eb3dNem94TUNCRm"
				+ "RHTXZSMDFVSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE5TMHdNaTB5TXlBeE"
				+ "5qb3dNem94TUNCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p2Y21sbmFXNWhiQzF3ZFhKam"
				+ "FHRnpaUzFrWVhSbElpQTlJQ0l5TURFMUxUQXlMVEkwSURBd09qQXpPakV3SUVWMFl5OUhUVlFpT3dwOS"
				+ "I7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWduaW5nLXN0YX"
				+ "R1cyIgPSAiMCI7Cn0=";
		
		iaprpb.setReceipt(receipt1);
		
		InAppPurchaseRequestEvent iapre = new InAppPurchaseRequestEvent();
		iapre.setTag(1);
		iapre.setInAppPurchaseRequestProto(iaprpb.build());
		inAppPurchaseController.handleEvent(iapre);
		
		List<StructureForUser> sfuList = structureForUserRetrieveUtils2.getUserStructsForUser(user.getId());
		boolean hasMoneyTree = false;
		for(StructureForUser sfu : sfuList) {
			if(sfu.getStructId() >= 10000) {
				hasMoneyTree = true;
			}
		}
		assertTrue(hasMoneyTree);
		
		String receipt2 = "ewoJInNpZ25hdHVyZSIgPSAiQW5JVHhmUkJFaEc5dldtQmZzTU1QTHpPc0xKSFgvZGpGMURBTFhSWTB"
				+ "MMTZ5QWZpb3NLM2VCbGQ1dmhsTjBxNHNjdGNmdEdmdUllSTFSaVpaRjVTSUp2cGUrN3g2N3dNUFNKWjhIczlwSU"
				+ "5hTGVUdjZCVVFrdDMrK2lhYVVLWVU0QVd1QW1mMjB6cWY3dElJVnFaK0h4SHFZdDdPRGlDd0JqMCtRbE93Vj"
				+ "lFYkFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN"
				+ "6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNa"
				+ "U0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6S"
				+ "UZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWER"
				+ "URTJNRFV4T0RFNE16RXpNRm93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV"
				+ "05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3Ykd"
				+ "VZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNb"
				+ "VRFdUxnamltTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg"
				+ "5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STStRT"
				+ "FJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQTFVZERnUVd"
				+ "CQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBR"
				+ "kRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0J"
				+ "nVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDRpT"
				+ "zRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J"
				+ "1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS"
				+ "01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGR"
				+ "xRnd4VmdtNTJoM29lSk9PdC92WTRFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU"
				+ "0dZejErVTNzRHhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJ"
				+ "jaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNR"
				+ "EUxTFRBeUxUSTBJREUwT2pBeU9qRTJJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMx"
				+ "cFpHVnVkR2xtYVdWeUlpQTlJQ0k0TlRGaFlUQmpNR0kwTWpjMVpESTFaakl4TVRBeU4yVTRNbUV3WVdSaU1qWmp"
				+ "NR0U0TXpoa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01EQXdNREUwT"
				+ "kRjM01URTBNaUk3Q2draVluWnljeUlnUFNBaU1TNHhMakV3TGpFeU5TSTdDZ2tpZEhKaGJuTmhZM1JwYjI0dGF"
				+ "XUWlJRDBnSWpFd01EQXdNREF4TkRRM056RXhORElpT3dvSkluRjFZVzUwYVhSNUlpQTlJQ0l4SWpzS0NTSnZjb"
				+ "WxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRJME9ERTFNek0yTkRNeElqc0tDU0oxYm1s"
				+ "eGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWtJM1JVWkdNamcwTFVJeE5Ea3ROREJGUmkxQ056ST"
				+ "BMVEV5UXpRek1qVTJRMEkyTVNJN0Nna2ljSEp2WkhWamRDMXBaQ0lnUFNBaVkyOXRMbk5qYjNCbGJIa3ViVzlp"
				+ "YzNGMVlXUXVjM1JoY25SbGNuQmhZMnNpT3dvSkltbDBaVzB0YVdRaUlEMGdJamsyTlRrNU16a3hNaUk3Q2draV"
				+ "ltbGtJaUE5SUNKamIyMHViSFpzTmk1dGIySnpkR1Z5Y3lJN0Nna2ljSFZ5WTJoaGMyVXRaR0YwWlMxdGN5SWdQ"
				+ "U0FpTVRReU5EZ3hOVE16TmpRek1TSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTSWdQU0FpTWpBeE5TMHdNaTB5Tk"
				+ "NBeU1qb3dNam94TmlCRmRHTXZSMDFVSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsTFhCemRDSWdQU0FpTWpBeE5T"
				+ "MHdNaTB5TkNBeE5Eb3dNam94TmlCQmJXVnlhV05oTDB4dmMxOUJibWRsYkdWeklqc0tDU0p2Y21sbmFXNWhiQz"
				+ "F3ZFhKamFHRnpaUzFrWVhSbElpQTlJQ0l5TURFMUxUQXlMVEkwSURJeU9qQXlPakUySUVWMFl5OUhUVlFpT3dw"
				+ "OSI7CgkiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOwoJInBvZCIgPSAiMTAwIjsKCSJzaWduaW5nLXN0YXR1cy"
				+ "IgPSAiMCI7Cn0";
	
		User user2 = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		InAppPurchaseRequestProto.Builder iaprpb2 = InAppPurchaseRequestProto.newBuilder();
		iaprpb2.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user2, null));	
		
		iaprpb2.setReceipt(receipt2);
		
		InAppPurchaseRequestEvent iapre2 = new InAppPurchaseRequestEvent();
		iapre2.setTag(1);
		iapre2.setInAppPurchaseRequestProto(iaprpb2.build());
		inAppPurchaseController.handleEvent(iapre2);
		String userStructId = "";
		
		List<StructureForUser> sfuList2 = structureForUserRetrieveUtils2.getUserStructsForUser(user2.getId());
		int moneyTreeCounter = 0;
		for(StructureForUser sfu : sfuList2) {
			if(sfu.getStructId() >= 10000) {
				moneyTreeCounter++;
				userStructId = sfu.getId();
			}
		}
		assertTrue(moneyTreeCounter == 1);
		
		//destroy money tree
		User user3 = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		DestroyMoneyTreeStructureRequestProto.Builder dmtsrpb = DestroyMoneyTreeStructureRequestProto.newBuilder();
		dmtsrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user3, null));	
		
		dmtsrpb.addUserStructUuid(userStructId);
		
		DestroyMoneyTreeStructureRequestEvent dmtsre = new DestroyMoneyTreeStructureRequestEvent();
		dmtsre.setTag(1);
		dmtsre.setDestroyMoneyTreeStructureRequestProto(dmtsrpb.build());
		destroyMoneyTreeStructureController.handleEvent(dmtsre);
		
		User user4 = userRetrieveUtil.getUserById("0185e5f9-622a-415b-8444-d3743cbf8442");
		List<StructureForUser> sfuList3 = structureForUserRetrieveUtils2.getUserStructsForUser(user4.getId());
		int moneyTreeCounter2 = 0;
		for(StructureForUser sfu : sfuList3) {
			if(sfu.getStructId() >= 10000) {
				moneyTreeCounter2++;
				
			}
		}
		assertTrue(moneyTreeCounter2 == 0);	
	}
	
	@Test
	public void testBattleItems() {
		User user1 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int userCash1 = user1.getCash();
		int userOil1 = user1.getOil();
		int userGems1 = user1.getGems();
		CreateBattleItemRequestProto.Builder cbirpb = CreateBattleItemRequestProto.newBuilder();
		cbirpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user1, null));	

		//create list of battle items add to queue
		List<BattleItemQueueForUserProto> newList = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu = new BattleItemQueueForUser();
		biqfu.setBattleItemId(1);
		biqfu.setPriority(1);
		Date now = new Date();
		biqfu.setExpectedStartTime(new Timestamp(now.getTime()));
		biqfu.setUserId(user1.getId());
		BattleItemQueueForUserProto biqfup = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu);
		newList.add(biqfup);

		cbirpb.addAllBiqfuNew(newList);
		cbirpb.setCashChange(50);
		cbirpb.setOilChange(0);
		cbirpb.setGemCostForCreating(0);
		
		CreateBattleItemRequestEvent cbire = new CreateBattleItemRequestEvent();
		cbire.setTag(1);
		cbire.setCreateBattleItemRequestProto(cbirpb.build());
		createBattleItemController.handleEvent(cbire);
		
		User user2 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");

		List<BattleItemQueueForUser> bifuList = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user1.getId());
		assertTrue(bifuList.size() == 1);
		assertEquals(user2.getCash() + 50, userCash1);
		
		//////////////////second request event//////////////////////////////////////////////////////
		int userCash2 = user2.getCash();
		int userOil2 = user2.getOil();
		int userGems2 = user2.getGems();
		CreateBattleItemRequestProto.Builder cbirpb2 = CreateBattleItemRequestProto.newBuilder();
		cbirpb2.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user2, null));	

		//finished list
		List<BattleItemQueueForUserProto> deletedList2 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu2 = new BattleItemQueueForUser();
		biqfu2.setBattleItemId(1);
		biqfu2.setPriority(1);
		Date now2 = new Date();
		biqfu2.setExpectedStartTime(new Timestamp(now2.getTime()));
		biqfu2.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup2 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu2);
		deletedList2.add(biqfup2);
		
		//create list of battle items add to queue
		List<BattleItemQueueForUserProto> newList2 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu3 = new BattleItemQueueForUser();
		biqfu3.setBattleItemId(1);
		biqfu3.setPriority(1);
		Date now3 = new Date();
		biqfu3.setExpectedStartTime(new Timestamp(now3.getTime()+1000));
		biqfu3.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup3 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu3);

		BattleItemQueueForUser biqfu4 = new BattleItemQueueForUser();
		biqfu4.setBattleItemId(2);
		biqfu4.setPriority(2);
		Date now4 = new Date();
		biqfu4.setExpectedStartTime(new Timestamp(now4.getTime()+1000));
		biqfu4.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup4 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu4);
		
		BattleItemQueueForUser biqfu5 = new BattleItemQueueForUser();
		biqfu5.setBattleItemId(3);
		biqfu5.setPriority(3);
		Date now5 = new Date();
		biqfu5.setExpectedStartTime(new Timestamp(now5.getTime()+1000));
		biqfu5.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup5 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu5);
		
		newList2.add(biqfup3);
		newList2.add(biqfup4);
		newList2.add(biqfup5);

		cbirpb2.addAllBiqfuNew(newList2);
		cbirpb2.addAllBiqfuDelete(deletedList2);
		cbirpb2.setCashChange(75);
		cbirpb2.setOilChange(100);
		cbirpb2.setGemCostForCreating(100);
		
		CreateBattleItemRequestEvent cbire2 = new CreateBattleItemRequestEvent();
		cbire2.setTag(1);
		cbire2.setCreateBattleItemRequestProto(cbirpb2.build());
		createBattleItemController.handleEvent(cbire2);
		
		User user3 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");

		List<BattleItemQueueForUser> bifuList2 = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user1.getId());
		assertTrue(bifuList2.size() == 3);
		assertEquals(user3.getCash() + 75, userCash2);
		assertEquals(user3.getOil() + 100, userOil2);
		assertEquals(user3.getGems() + 100, userGems2);

		
		//////////////////third request event//////////////////////////////////////////////////////
		int userCash3 = user3.getCash();
		int userOil3 = user3.getOil();
		int userGems3 = user3.getGems();
		CreateBattleItemRequestProto.Builder cbirpb3 = CreateBattleItemRequestProto.newBuilder();
		cbirpb3.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user3, null));	

		//removed list
		List<BattleItemQueueForUserProto> removedList3 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu6 = new BattleItemQueueForUser();
		biqfu6.setBattleItemId(1);
		biqfu6.setPriority(1);
		Date now6 = new Date();
		biqfu6.setExpectedStartTime(new Timestamp(now6.getTime()));
		biqfu6.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup6 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu6);
		removedList3.add(biqfup6);
		
		//updated list
		List<BattleItemQueueForUserProto> updatedList3 = new ArrayList<BattleItemQueueForUserProto>();
		BattleItemQueueForUser biqfu7 = new BattleItemQueueForUser();
		biqfu7.setBattleItemId(2);
		biqfu7.setPriority(2);
		Date now7 = new Date();
		biqfu7.setExpectedStartTime(new Timestamp(now7.getTime()+2000));
		biqfu7.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup7 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu7);

		BattleItemQueueForUser biqfu8 = new BattleItemQueueForUser();
		biqfu8.setBattleItemId(2);
		biqfu8.setPriority(2);
		Date now8 = new Date();
		biqfu8.setExpectedStartTime(new Timestamp(now8.getTime()+2000));
		biqfu8.setUserId(user2.getId());
		BattleItemQueueForUserProto biqfup8 = CreateInfoProtoUtils.createBattleItemQueueForUserProto(biqfu8);
		updatedList3.add(biqfup7);
		updatedList3.add(biqfup8);

		cbirpb3.addAllBiqfuDelete(removedList3);
		cbirpb3.addAllBiqfuUpdate(updatedList3);
		cbirpb3.setCashChange(-50);
		cbirpb3.setOilChange(0);
		cbirpb3.setGemCostForCreating(0);
		
		CreateBattleItemRequestEvent cbire3 = new CreateBattleItemRequestEvent();
		cbire3.setTag(1);
		cbire3.setCreateBattleItemRequestProto(cbirpb3.build());
		createBattleItemController.handleEvent(cbire3);
		
		User user4 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");

		List<BattleItemQueueForUser> bifuList3 = battleItemQueueForUserRetrieveUtil.getUserBattleItemQueuesForUser(user4.getId());
		assertTrue(bifuList3.size() == 2);
		assertEquals(user4.getCash() - 50, userCash3);
		assertEquals(user4.getOil(), userOil3);
		assertEquals(user4.getGems(), userGems3);

		/////////////////////////COMPLETE RESEARCH/////////////////////////////////////////////
		Date date = new Date();
		
		CompleteBattleItemRequestProto.Builder cbirpb = CompleteBattleItemRequestProto.newBuilder();
		cbirpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user4, null));
		cbirpb.setGemsSpent(50);
		
		FinishPerformingResearchRequestEvent fprre = new FinishPerformingResearchRequestEvent();
		fprre.setTag(1);
		fprre.setFinishPerformingResearchRequestProto(fprrpb.build());
		finishPerformingResearchController.handleEvent(fprre);
		List<ResearchForUser> rfuList2 = researchForUserRetrieveUtil.getAllResearchForUser(user.getId());
		boolean isComplete2 = false;
		for(ResearchForUser rfu2 : rfuList2) {
			assertFalse(rfu2.isComplete());
			
		}
		
		
	}
	



	private String randomUUID() {
		return UUID.randomUUID().toString();
	}


//	private String insertIntoUserMonsterTable(String userId, int monsterId, int currExp, int currLvl,
//			int currHealth, int numPieces, int isComplete, Timestamp combineStartTime, 
//			int teamSlotNum, String sourceOfPieces, int hasAllPieces, int restricted) {
//		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
//		String id = randomUUID();
//
//		Map<String, Object> insertParams = new HashMap<String, Object>();
//		insertParams.put(DBConstants.MONSTER_FOR_USER__ID, id);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__USER_ID,	userId);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, monsterId);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, currExp);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, currLvl);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, currHealth);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__NUM_PIECES, numPieces);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__IS_COMPLETE, isComplete);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME, combineStartTime);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, teamSlotNum);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES, sourceOfPieces);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES, hasAllPieces);
//		insertParams.put(DBConstants.MONSTER_FOR_USER__RESTRICTED, restricted);
//
//
//		int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
//		
//		return id;
//	}
	
	@Test
	public void testMonsterEnhancingAndHistoryTables() {
		////////////////////////ENHANCE REQUEST//////////////////////////////
		User user = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int userOil = user.getOil();
		int monsterEnhanceRows1 = retrieveNumberOfMonsterEnhanceHistoryRows(user.getId());
		List<MonsterForUser> userMonsterList1 = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		
		//give user the three necessary monsters used in evolving
		Date now = new Date();
		Timestamp nowTimestamp = new Timestamp(now.getTime());
		String userFeederMonsterId1 = insertIntoUserMonsterTable(user.getId(), 1, 1000, 10, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		String userFeederMonsterId2 = insertIntoUserMonsterTable(user.getId(), 1, 0, 1, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		String userBeingEnhancedMonsterId = insertIntoUserMonsterTable(user.getId(), 1750, 0, 1, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		
		MinimumUserProtoWithMaxResources.Builder mupWithMaxResources = MinimumUserProtoWithMaxResources.newBuilder();
		mupWithMaxResources.setMinUserProto(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		mupWithMaxResources.setMaxCash(1000000);
		mupWithMaxResources.setMaxOil(1000000);
		
		SubmitMonsterEnhancementRequestProto.Builder smerpb = SubmitMonsterEnhancementRequestProto.newBuilder();
		smerpb.setSender(mupWithMaxResources);
		
		//creating the 3 protos for enhanced monster and feeders
		MonsterEnhancingForUser mefu = new MonsterEnhancingForUser();
		mefu.setUserId(user.getId());
		mefu.setEnhancingCost(0);
		mefu.setEnhancingComplete(false);
		mefu.setMonsterForUserId(userBeingEnhancedMonsterId);
		UserEnhancementItemProto ueipEnhancedMonster = CreateInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu);
		
		MonsterEnhancingForUser mefu2 = new MonsterEnhancingForUser();
		mefu2.setUserId(user.getId());
		mefu2.setEnhancingCost(100);
		mefu2.setEnhancingComplete(false);
		mefu2.setMonsterForUserId(userFeederMonsterId1);
		mefu2.setExpectedStartTime(nowTimestamp);
		UserEnhancementItemProto ueipFeeder1 = CreateInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu2);
		
		MonsterEnhancingForUser mefu3 = new MonsterEnhancingForUser();
		mefu3.setUserId(user.getId());
		mefu3.setEnhancingCost(100);
		mefu3.setEnhancingComplete(false);
		mefu3.setMonsterForUserId(userFeederMonsterId2);
		mefu3.setExpectedStartTime(nowTimestamp);
		UserEnhancementItemProto ueipFeeder2 = CreateInfoProtoUtils.createUserEnhancementItemProtoFromObj(mefu3);
		
		List<UserEnhancementItemProto> listOfMonstersForEnhance = new ArrayList<UserEnhancementItemProto>();	
		listOfMonstersForEnhance.add(ueipFeeder2);
		listOfMonstersForEnhance.add(ueipFeeder1);
		listOfMonstersForEnhance.add(ueipEnhancedMonster);

		smerpb.addAllUeipNew(listOfMonstersForEnhance);
		smerpb.setGemsSpent(0);
		smerpb.setOilChange(-200);
				
		SubmitMonsterEnhancementRequestEvent smere = new SubmitMonsterEnhancementRequestEvent();
		smere.setTag(1);
		smere.setSubmitMonsterEnhancementRequestProto(smerpb.build());
		submitMonsterEnhancementController.handleEvent(smere);
		
		User user2 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		List<MonsterForUser> userMonsterList2 = monsterForUserRetrieveUtils.getMonstersForUser(user2.getId());
		int monsterDeleteHistoryRows2 = retrieveNumberOfMonsterDeleteHistoryRows(user2.getId());
		int monsterEnhanceRows2 = retrieveNumberOfMonsterEnhanceHistoryRows(user2.getId());

		assertEquals(user2.getOil(), userOil-200);
//		assertEquals(monsterEnhanceRows1 + 3, monsterEnhanceRows2);
		
		////////////////////////ENHANCE WAIT TIME COMPLETE REQUEST//////////////////////////////
		int userGems = user2.getGems();
		EnhancementWaitTimeCompleteRequestProto.Builder ewtcrpb = EnhancementWaitTimeCompleteRequestProto.newBuilder();
		ewtcrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user2, null));
		ewtcrpb.setIsSpeedup(true);
		ewtcrpb.setGemsForSpeedup(100);
		ewtcrpb.setUserMonsterUuid(userBeingEnhancedMonsterId);
		
		EnhancementWaitTimeCompleteRequestEvent ewtcre = new EnhancementWaitTimeCompleteRequestEvent();
		ewtcre.setTag(1);
		ewtcre.setEnhancementWaitTimeCompleteRequestProto(ewtcrpb.build());
		enhancementWaitTimeCompleteController.handleEvent(ewtcre);
		
		User user3 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int userGems2 = user3.getGems();
		Map<String, MonsterEnhancingForUser> enhancingMonstersMap = monsterEnhancingForUserRetrieveUtil.getMonstersForUser(user3.getId());
		MonsterEnhancingForUser mefuNonFeeder = enhancingMonstersMap.get(userBeingEnhancedMonsterId);
		
		assertEquals(userGems - 100, userGems2);
//		assertEquals(monsterEnhanceRows1 + 3, monsterEnhanceRows2);
		assertTrue(mefuNonFeeder.isEnhancingComplete());
		
		////////////////////////ENHANCE COLLECT REQUEST//////////////////////////////
		CollectMonsterEnhancementRequestProto.Builder cmerpb = CollectMonsterEnhancementRequestProto.newBuilder();
		cmerpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user3, null));
		
		UserMonsterCurrentExpProto.Builder umcepb = UserMonsterCurrentExpProto.newBuilder();
		umcepb.setUserMonsterUuid(userBeingEnhancedMonsterId);
		umcepb.setExpectedExperience(100);
		umcepb.setExpectedLevel(2);
		umcepb.setExpectedHp(100);
		
		cmerpb.setUmcep(umcepb);
		List<String> deletedMonsters = new ArrayList<String>();
		deletedMonsters.add(userFeederMonsterId1);
		deletedMonsters.add(userFeederMonsterId2);
		cmerpb.addAllUserMonsterUuids(deletedMonsters);
		
		CollectMonsterEnhancementRequestEvent cmere = new CollectMonsterEnhancementRequestEvent();
		cmere.setTag(1);
		cmere.setCollectMonsterEnhancementRequestProto(cmerpb.build());
		collectMonsterEnhancementController.handleEvent(cmere);
		
		
		User user4 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int monsterDeleteHistoryRows3 = retrieveNumberOfMonsterDeleteHistoryRows(user4.getId());
		List<MonsterForUser> userMonsterList3 = monsterForUserRetrieveUtils.getMonstersForUser(user4.getId());
		int monsterEnhanceRows3 = retrieveNumberOfMonsterEnhanceHistoryRows(user4.getId());


		assertEquals(userMonsterList2.size() - 2, userMonsterList3.size());
		assertEquals(monsterDeleteHistoryRows2 + 2, monsterDeleteHistoryRows3);
		assertEquals(monsterEnhanceRows2 + 2, monsterEnhanceRows3);

	}
	

	@Test
	public void testMonsterEvolutionFinishedAndHistoryTables() {
		////////////////////////EVOLVE REQUEST//////////////////////////////
		User user = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int userOil = user.getOil();
		int monsterEvolutionHistoryRows1 = retrieveNumberOfMonsterEvolutionHistoryRows(user.getId());
		List<MonsterForUser> userMonsterList1 = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		int monsterDeleteHistoryRows1 = retrieveNumberOfMonsterDeleteHistoryRows(user.getId());

		
		//give user the three necessary monsters used in evolving
		Date now = new Date();
		Timestamp nowTimestamp = new Timestamp(now.getTime());
		String userMonsterId1 = insertIntoUserMonsterTable(user.getId(), 1, 1000, 10, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		String userMonsterId2 = insertIntoUserMonsterTable(user.getId(), 1, 0, 1, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		String userCatalystMonsterId = insertIntoUserMonsterTable(user.getId(), 1750, 0, 1, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);
		
		EvolveMonsterRequestProto.Builder emrpb = EvolveMonsterRequestProto.newBuilder();
		emrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		
		MonsterEvolvingForUser mefu = new MonsterEvolvingForUser(userCatalystMonsterId, userMonsterId1, userMonsterId2, user.getId(), nowTimestamp);
		UserMonsterEvolutionProto umep = CreateInfoProtoUtils.createUserEvolutionProtoFromEvolution(mefu);
		
		emrpb.setEvolution(umep);
		emrpb.setGemsSpent(0);
		emrpb.setOilChange(-100);
		
		EvolveMonsterRequestEvent emre = new EvolveMonsterRequestEvent();
		emre.setTag(1);
		emre.setEvolveMonsterRequestProto(emrpb.build());
		evolveMonsterController.handleEvent(emre);
		
		User user2 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		List<MonsterForUser> userMonsterList2 = monsterForUserRetrieveUtils.getMonstersForUser(user2.getId());

		assertEquals(user2.getOil(), userOil-100);
		assertEquals(userMonsterList1.size() + 3, userMonsterList2.size());
		
		////////////////////////EVOLVE FINISH REQUEST//////////////////////////////
		EvolutionFinishedRequestProto.Builder efrpb = EvolutionFinishedRequestProto.newBuilder();
		efrpb.setSender(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		efrpb.setGemsSpent(0);
		
		EvolutionFinishedRequestEvent efre = new EvolutionFinishedRequestEvent();
		efre.setTag(1);
		efre.setEvolutionFinishedRequestProto(efrpb.build());
		evolutionFinishedController.handleEvent(efre);
		
		User user3 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		int monsterDeleteHistoryRows2 = retrieveNumberOfMonsterDeleteHistoryRows(user3.getId());
		List<MonsterForUser> userMonsterList3 = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		int monsterEvolutionHistoryRows2 = retrieveNumberOfMonsterEvolutionHistoryRows(user2.getId());

		assertEquals(userMonsterList2.size() - 2, userMonsterList3.size());
		assertEquals(monsterDeleteHistoryRows1 + 3, monsterDeleteHistoryRows2);
		assertEquals(monsterEvolutionHistoryRows1 + 1, monsterEvolutionHistoryRows2);

	}
	
	private int retrieveNumberOfMonsterEvolutionHistoryRows(String userId) {
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		int size = 0;
		try {
			if (conn != null) {
				Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
				absoluteConditionParams.put(DBConstants.MONSTER_EVOLVING_HISTORY__USER_ID, userId);
				rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, DBConstants.TABLE_MONSTER_EVOLVING_HISTORY);
				rs.last();
				size = rs.getRow();
			}
		}
		catch(Exception e) {
			log.info("exception: " + e);
			return -1;
		}
		return size;
	}
	
	private int retrieveNumberOfMonsterEnhanceHistoryRows(String userId) {
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		int size = 0;
		try {
			if (conn != null) {
				Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
				absoluteConditionParams.put(DBConstants.MONSTER_ENHANCING_HISTORY__USER_ID, userId);
				rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, DBConstants.TABLE_MONSTER_ENHANCING_HISTORY);
				rs.last();
				size = rs.getRow();
			}
		}
		catch(Exception e) {
			log.info("exception: " + e);
			return -1;
		}
		return size;
	}

	private int retrieveNumberOfMonsterDeleteHistoryRows(String userId) {
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		int size = 0;
		try {
			if (conn != null) {
				Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
				absoluteConditionParams.put(DBConstants.MONSTER_FOR_USER_DELETED__USER_ID, userId);
				rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, DBConstants.TABLE_MONSTER_FOR_USER_DELETED);
				rs.last();
				size = rs.getRow();
			}
		}
		catch(Exception e) {
			log.info("exception: " + e);
			return -1;
		}
		return size;
	}

	
	@Test
	public void testSellingMonsterAndHistory() {
		User user = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		List<MonsterForUser> userMonsterList = monsterForUserRetrieveUtils.getMonstersForUser(user.getId());
		int monsterDeleteHistoryRows = retrieveNumberOfMonsterDeleteHistoryRows(user.getId());
		int userCash = user.getCash();

		Date now = new Date();
		Timestamp nowTimestamp = new Timestamp(now.getTime());
		String userMonsterId = insertIntoUserMonsterTable(user.getId(), 1, 1000, 10, 10, 1, 1, nowTimestamp, 0, " cheater, cheater, pumpkin eater ", 1, 0);

		MinimumUserProtoWithMaxResources.Builder mupWithMaxResources = MinimumUserProtoWithMaxResources.newBuilder();
		mupWithMaxResources.setMinUserProto(CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(user, null));
		mupWithMaxResources.setMaxCash(1000000);
		mupWithMaxResources.setMaxOil(1000000);
		
		MinimumUserMonsterSellProto.Builder mumsp = MinimumUserMonsterSellProto.newBuilder();
		mumsp.setUserMonsterUuid(userMonsterId);
		mumsp.setCashAmount(100);
		List<MinimumUserMonsterSellProto> sellList = new ArrayList<MinimumUserMonsterSellProto>();
		sellList.add(mumsp.build());
		
		SellUserMonsterRequestProto.Builder sumrpb = SellUserMonsterRequestProto.newBuilder();
		sumrpb.setSender(mupWithMaxResources);
		sumrpb.addAllSales(sellList);
		
		SellUserMonsterRequestEvent sumre = new SellUserMonsterRequestEvent();
		sumre.setTag(1);
		sumre.setSellUserMonsterRequestProto(sumrpb.build());
		sellUserMonsterController.handleEvent(sumre);
		
		User user2 = userRetrieveUtil.getUserById("02ae9fb2-5117-4f18-b05c-de4b19a6aaad");
		List<MonsterForUser> userMonsterList2 = monsterForUserRetrieveUtils.getMonstersForUser(user2.getId());
		int monsterDeleteHistoryRows2 = retrieveNumberOfMonsterDeleteHistoryRows(user2.getId());
		
		
		assertEquals(userCash + 100, user2.getCash());
		assertEquals(userMonsterList.size(), userMonsterList2.size());
		assertEquals(monsterDeleteHistoryRows + 1, monsterDeleteHistoryRows2);

	}
	
	
	@Test
	public void testStaticDataContainer() {
		StaticDataProto staticData = StaticDataContainer.getStaticData();
		assertNotNull("StaticDataContainer returns null StaticDataProto", staticData);
		List<MonsterProto> allMonsters = staticData.getAllMonstersList(); 
		assertNotNull("Expected: monsters. Actual: no monsters exist",
			allMonsters);
		assertTrue("2 Expected: monsters. Actual: no monsters exist",
			!(allMonsters.isEmpty()) );
		
		
		StaticDataProto staticData2 = staticData.toBuilder().build();
		allMonsters = staticData2.getAllMonstersList();
		assertNotNull("Expected: monsters. Actual: no monsters exist",
			allMonsters);
		assertTrue("2 Expected: monsters. Actual: no monsters exist",
			!(allMonsters.isEmpty()) );
		
		log.info("staticData2={}", staticData2);
	}
	
	private String getUnitTesterId() {
		return "11";
	}
	
	
//	public PurchaseCityExpansionController getPurchaseCityExpansionController() {
//		return purchaseCityExpansionController;
//	}
//	public void setPurchaseCityExpansionController(
//			PurchaseCityExpansionController purchaseCityExpansionController) {
//		this.purchaseCityExpansionController = purchaseCityExpansionController;
//	}

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

//	public QuestProgressController getQuestProgressController()
//	{
//		return questProgressController;
//	}
//	public void setQuestProgressController( QuestProgressController questProgressController )
//	{
//		this.questProgressController = questProgressController;
//	}
//
//	public EnhanceMonsterController getEnhanceMonsterController()
//	{
//		return enhanceMonsterController;
//	}
//	public void setEnhanceMonsterController( EnhanceMonsterController enhanceMonsterController )
//	{
//		this.enhanceMonsterController = enhanceMonsterController;
//	}

	public DevController getDevController()
	{
		return devController;
	}
	public void setDevController( DevController devController )
	{
		this.devController = devController;
	}

	public RetrieveClanInfoController getRetrieveClanInfoController()
	{
		return retrieveClanInfoController;
	}
	public void setRetrieveClanInfoController( RetrieveClanInfoController retrieveClanInfoController )
	{
		this.retrieveClanInfoController = retrieveClanInfoController;
	}

//	public InviteToClanController getInviteToClanController()
//	{
//		return inviteToClanController;
//	}
//	public void setInviteToClanController( InviteToClanController inviteToClanController )
//	{
//		this.inviteToClanController = inviteToClanController;
//	}


	
	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}
	
	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils()
	{
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
		MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils )
	{
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public ClanInviteRetrieveUtil getClanInviteRetrieveUtil()
	{
		return clanInviteRetrieveUtil;
	}


	public void setClanInviteRetrieveUtil( ClanInviteRetrieveUtil clanInviteRetrieveUtil )
	{
		this.clanInviteRetrieveUtil = clanInviteRetrieveUtil;
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
