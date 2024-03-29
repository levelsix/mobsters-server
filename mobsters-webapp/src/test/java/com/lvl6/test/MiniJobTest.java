//package com.lvl6.test;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import junit.framework.TestCase;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.google.common.collect.Sets;
//import com.lvl6.events.request.BeginMiniJobRequestEvent;
//import com.lvl6.events.request.CompleteMiniJobRequestEvent;
//import com.lvl6.events.request.RedeemMiniJobRequestEvent;
//import com.lvl6.events.request.SpawnMiniJobRequestEvent;
//import com.lvl6.info.MiniJob;
//import com.lvl6.info.MiniJobForUser;
//import com.lvl6.info.MonsterForUser;
//import com.lvl6.info.User;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.proto.EventMiniJobProto.BeginMiniJobRequestProto;
//import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;
//import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobRequestProto;
//import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobRequestProto;
//import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
//import com.lvl6.proto.UserProto.MinimumUserProto;
//import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
//import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
//import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
//import com.lvl6.retrieveutils.UserRetrieveUtils2;
//import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
//import com.lvl6.server.controller.BeginMiniJobController;
//import com.lvl6.server.controller.CompleteMiniJobController;
//import com.lvl6.server.controller.RedeemMiniJobController;
//import com.lvl6.server.controller.SpawnMiniJobController;
//import com.lvl6.server.controller.utils.TimeUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.DBConnection;
//import com.lvl6.utils.utilmethods.StringUtils;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/test-spring-application-context.xml")
//public class MiniJobTest extends TestCase {
//
//	private static Logger log = LoggerFactory.getLogger(//	}.getClass().getEnclosingClass());
//
//	@Autowired
//	protected UserRetrieveUtils2 userRetrieveUtils;
//
//	@Autowired
//	protected TimeUtils timeUtils;
//
//	@Autowired
//	protected MiniJobForUserRetrieveUtil miniJobForUserRetieveUtil;
//
//	@Autowired
//	protected MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils;
//
//	@Autowired
//	protected SpawnMiniJobController spawnMiniJobController;
//
//	@Autowired
//	protected BeginMiniJobController beginMiniJobController;
//
//	@Autowired
//	protected CompleteMiniJobController completeMiniJobController;
//
//	@Autowired
//	protected RedeemMiniJobController redeemMiniJobController;
//
//	@Test
//	//	@Rollback(true) //doesn't roll back transaction >:C
//	//	@Transactional //just manually undo...
//	public void testSendSpawnMiniJob() {
//		log.info("spawn mini job");
//		String userId = getTestUserId();
//		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
//		Date lastMiniJobSpawnTimeThen = unitTesterThen
//				.getLastMiniJobGeneratedTime();
//
//		//just so can set this in the future
//		Date clientTime = new Date(lastMiniJobSpawnTimeThen.getTime() + 10000);
//		int numToSpawn = 2;
//		int structId = getMiniJobTestStructId();
//
//		//get count of user's current MiniJobs
//		Map<String, MiniJobForUser> userMiniJobIdsToUserMiniJobsThen = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, null);
//		int miniJobCountThen = userMiniJobIdsToUserMiniJobsThen.size();
//		log.info("sendSpawnMiniJob() userMiniJobsThen="
//				+ userMiniJobIdsToUserMiniJobsThen);
//
//		sendSpawnMiniJobRequestEvent(unitTesterThen, clientTime, numToSpawn,
//				structId);
//
//		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
//		//should have new entries for this user
//		Map<String, MiniJobForUser> userMiniJobIdsToUserMiniJobsNow = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, null);
//		int miniJobCountNow = userMiniJobIdsToUserMiniJobsNow.size();
//		log.info("sendSpawnMiniJob() userMiniJobsNow="
//				+ userMiniJobIdsToUserMiniJobsNow);
//
//		int newMiniJobAmount = miniJobCountNow - miniJobCountThen;
//		assertTrue("Expected new MiniJobs: " + numToSpawn + ". Actual: "
//				+ newMiniJobAmount, numToSpawn == newMiniJobAmount);
//
//		//check to see that the user's last mini job spawn time changed
//		User unitTesterNow = getUserRetrieveUtils().getUserById(userId);
//		Date lastMiniJobSpawnTimeNow = unitTesterNow
//				.getLastMiniJobGeneratedTime();
//		log.info("!!!!!!!!!!!!!!!!!!!!! ");
//		log.info("lastMiniJobSpawnTimeThen=" + lastMiniJobSpawnTimeThen);
//		log.info("lastMiniJobSpawnTimeNow=" + lastMiniJobSpawnTimeNow);
//		boolean spawnTimeChanged = getTimeUtils().isFirstEarlierThanSecond(
//				lastMiniJobSpawnTimeThen, lastMiniJobSpawnTimeNow);
//		log.info("spawnTimeChanged=" + spawnTimeChanged);
//		assertTrue("Expected miniJobSpawnTime: in the future. Actual: not."
//				+ " then=" + lastMiniJobSpawnTimeThen + ", now="
//				+ lastMiniJobSpawnTimeNow, spawnTimeChanged);
//
//		//manually delete the created MiniJobForUser
//		Set<String> set1 = userMiniJobIdsToUserMiniJobsNow.keySet();
//		Set<String> set2 = userMiniJobIdsToUserMiniJobsThen.keySet();
//		Set<String> newMiniJobForUserIds = Sets.difference(set1, set2);
//		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen,
//				newMiniJobForUserIds);
//	}
//
//	@Test
//	public void testSendBeginMiniJob() {
//		log.info("begin mini job");
//		String userId = getTestUserId();
//		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
//		Date clientTime = new Date();
//
//		//spawn the minijob
//		MiniJobForUser mjfuThen = createNewMiniJobForUser(userId,
//				unitTesterThen, clientTime);
//		String newMiniJobForUserIdThen = mjfuThen.getId();
//		Collection<String> userMiniJobIds = Collections
//				.singleton(newMiniJobForUserIdThen);
//		assertTrue("Expected startTime: null. Actual: " + mjfuThen,
//				null == mjfuThen.getTimeStarted());
//		assertTrue("Expected userMonsterIds: null or empty. Actual: "
//				+ mjfuThen, null == mjfuThen.getUserMonsterIdStr()
//				|| mjfuThen.getUserMonsterIdStr().isEmpty());
//
//		//send the begin minijob request
//		Map<String, MonsterForUser> mfuIdToMfu = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, null);
//		assertTrue("Expected userMonsters: not null, or empty. Actual: "
//				+ mfuIdToMfu, null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
//		Collection<String> userMonsterIds = mfuIdToMfu.keySet();
//		log.info("testSendBeginMiniJob() begfore"
//				+ " sendBeginMiniJobRequestEvent");
//		sendBeginMiniJobRequestEvent(unitTesterThen, clientTime,
//				userMonsterIds, newMiniJobForUserIdThen);
//		log.info("testSendBeginMiniJob() after sendBeginMiniJobRequestEvent");
//
//		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
//		Map<String, MiniJobForUser> mjfuIdToMjfu = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//
//		MiniJobForUser mjfuNow = mjfuIdToMjfu.get(newMiniJobForUserIdThen);
//
//		Date startTime = mjfuNow.getTimeStarted();
//		assertTrue("Expected startTime: not null. Actual: " + mjfuNow,
//				null != startTime);
//
//		long startTimeMillis = startTime.getTime();
//		long afterStartTimeMillis = startTimeMillis + 10000;
//		long beforeStartTimeMillis = startTimeMillis - 10000;
//		long clientTimeMillis = clientTime.getTime();
//
//		assertTrue(
//				"Expected startTime: " + clientTime + ". Actual: " + mjfuNow,
//				afterStartTimeMillis > clientTimeMillis
//						&& beforeStartTimeMillis < clientTimeMillis);
//		assertTrue("Expected userMonsterIds: nonNull or empty. Actual: "
//				+ mjfuNow, null != mjfuNow.getUserMonsterIds()
//				&& userMonsterIds.containsAll(mjfuNow.getUserMonsterIds()));
//
//		//manually delete the created MiniJobForUser
//		Date lastMiniJobSpawnTimeThen = unitTesterThen
//				.getLastMiniJobGeneratedTime();
//		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen,
//				userMiniJobIds);
//	}
//
//	protected MiniJobForUser createNewMiniJobForUser(String userId,
//			User unitTester, Date clientTime) {
//		int numToSpawn = 1;
//		int structId = getMiniJobTestStructId();
//
//		Map<String, MiniJobForUser> miniJobIdToUserMiniJobsThen = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, null);
//
//		sendSpawnMiniJobRequestEvent(unitTester, clientTime, numToSpawn,
//				structId);
//
//		Map<String, MiniJobForUser> miniJobIdToUserMiniJobsNow = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, null);
//
//		//get the newly created MiniJob and begin it
//		Set<String> set1 = miniJobIdToUserMiniJobsNow.keySet();
//		Set<String> set2 = miniJobIdToUserMiniJobsThen.keySet();
//		Set<String> newMiniJobForUserIds = Sets.difference(set1, set2);
//
//		String newMiniJobForUserId = (newMiniJobForUserIds.iterator()).next();
//
//		Collection<String> userMiniJobIds = Collections
//				.singleton(newMiniJobForUserId);
//		Map<String, MiniJobForUser> miniJobForUserIdToMjfu = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//
//		MiniJobForUser mjfuThen = miniJobForUserIdToMjfu
//				.get(newMiniJobForUserId);
//		return mjfuThen;
//	}
//
//	@Test
//	public void testSendCompleteMiniJob() {
//		log.info("complete mini job");
//		String userId = getTestUserId();
//		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
//		Date clientTime = new Date();
//		Map<String, MonsterForUser> mfuIdToMfu = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, null);
//
//		//spawn the minijob
//		MiniJobForUser mjfuThen = createNewMiniJobForUser(userId,
//				unitTesterThen, clientTime);
//
//		//begin the minijob
//		String newMiniJobForUserIdThen = mjfuThen.getId();
//		beginMiniJobForUser(userId, unitTesterThen, clientTime,
//				newMiniJobForUserIdThen, mjfuThen, mfuIdToMfu);
//
//		//List<UserMonsterCurrentHealthProto> umchp = createUmchp(mfuIdToMfu);
//
//		//send the complete mini job request
//		sendCompleteMiniJobRequestEvent(unitTesterThen, clientTime,
//				newMiniJobForUserIdThen, false, 0);//, umchp);
//
//		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
//		Collection<String> userMiniJobIds = Collections
//				.singleton(newMiniJobForUserIdThen);
//		Map<String, MiniJobForUser> mjfuIdToMjfu = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//		MiniJobForUser mjfuNow = mjfuIdToMjfu.get(newMiniJobForUserIdThen);
//
//		assertTrue("Expected completeTime: different. Actual: then=" + mjfuThen
//				+ ", now=" + mjfuNow,
//				mjfuThen.getTimeCompleted() != mjfuNow.getTimeCompleted());
//
//		//manually delete the created MiniJobForUser
//		Date lastMiniJobSpawnTimeThen = unitTesterThen
//				.getLastMiniJobGeneratedTime();
//		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen,
//				userMiniJobIds);
//	}
//
//	protected void beginMiniJobForUser(String userId, User user,
//			Date clientTime, String newMiniJobForUserId, MiniJobForUser mjfu,
//			Map<String, MonsterForUser> mfuIdToMfu) {
//		assertTrue("Expected startTime: null. Actual: " + mjfu,
//				null == mjfu.getTimeStarted());
//		assertTrue("Expected userMonsterIds: null or empty. Actual: " + mjfu,
//				null == mjfu.getUserMonsterIdStr()
//						|| mjfu.getUserMonsterIdStr().isEmpty());
//
//		//begin the minijob
//		assertTrue("Expected userMonsters: not null, or empty. Actual: "
//				+ mfuIdToMfu, null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
//		Collection<String> userMonsterIds = mfuIdToMfu.keySet();
//		sendBeginMiniJobRequestEvent(user, clientTime, userMonsterIds,
//				newMiniJobForUserId);
//	}
//
//	protected List<UserMonsterCurrentHealthProto> createUmchp(
//			Map<String, MonsterForUser> mfuIdToMfu) {
//		assertTrue("Expected userMonsters: not null, or empty. Actual: "
//				+ mfuIdToMfu, null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
//
//		List<UserMonsterCurrentHealthProto> umchpList = new ArrayList<UserMonsterCurrentHealthProto>();
//
//		for (MonsterForUser mfu : mfuIdToMfu.values()) {
//			UserMonsterCurrentHealthProto umchp = CreateInfoProtoUtils
//					.createUserMonsterCurrentHealthProto(mfu);
//			umchpList.add(umchp);
//		}
//
//		return umchpList;
//	}
//
//	@Test
//	public void testSendRedeemMiniJob() {
//		log.info("complete mini job");
//		String userId = getTestUserId();
//		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
//		Date clientTime = new Date();
//		Map<String, MonsterForUser> monstersThen = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, null);
//
//		MiniJobForUser mjfuThen = completeMiniJob(userId, unitTesterThen,
//				clientTime);
//		int miniJobId = mjfuThen.getMiniJobId();
//		String miniJobForUserId = mjfuThen.getId();
//
//		List<UserMonsterCurrentHealthProto> umchp = createUmchp(monstersThen);
//
//		//send the redeem mini job request
//		sendRedeemMiniJobRequestEvent(unitTesterThen, mjfuThen, clientTime,
//				umchp);
//
//		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
//
//		//no more MiniJobForUser
//		Collection<String> userMiniJobIds = Collections
//				.singleton(miniJobForUserId);
//		Map<String, MiniJobForUser> idToMjfu = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//		MiniJobForUser mjfuNow = null;
//		if (idToMjfu.containsKey(miniJobForUserId)) {
//			mjfuNow = idToMjfu.get(miniJobForUserId);
//		}
//		assertTrue("Expected MiniJobForUser: null. Actual: " + mjfuNow,
//				null == mjfuNow);
//
//		User unitTesterNow = getUserRetrieveUtils().getUserById(userId);
//		Map<String, MonsterForUser> monstersNow = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, null);
//
//		//user currency should change if MiniJob gave any reward
//		checkIfUserRewarded(unitTesterThen, unitTesterNow, miniJobId,
//				monstersThen, monstersNow);
//
//		//user monsters should have changed if MiniJob gave monster reward
//	}
//
//	protected MiniJobForUser completeMiniJob(String userId, User user,
//			Date clientTime) {
//		Map<String, MonsterForUser> mfuIdToMfu = getMonsterForUserRetrieveUtils()
//				.getSpecificOrAllUserMonstersForUser(userId, null);
//
//		//spawn the minijob
//		MiniJobForUser mjfuThen = createNewMiniJobForUser(userId, user,
//				clientTime);
//
//		//begin the minijob
//		String newMiniJobForUserIdThen = mjfuThen.getId();
//		beginMiniJobForUser(userId, user, clientTime, newMiniJobForUserIdThen,
//				mjfuThen, mfuIdToMfu);
//
//		//List<UserMonsterCurrentHealthProto> umchp = createUmchp(mfuIdToMfu);
//
//		//send the complete mini job request
//		sendCompleteMiniJobRequestEvent(user, clientTime,
//				newMiniJobForUserIdThen, false, 0);//, umchp);
//
//		Collection<String> userMiniJobIds = Collections
//				.singleton(newMiniJobForUserIdThen);
//		Map<String, MiniJobForUser> mjfuIdToMjfu = getMiniJobForUserRetieveUtil()
//				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
//		MiniJobForUser mjfuNow = mjfuIdToMjfu.get(newMiniJobForUserIdThen);
//
//		return mjfuNow;
//	}
//
//	protected void checkIfUserRewarded(User userThen, User userNow,
//			int miniJobId, Map<String, MonsterForUser> monstersThen,
//			Map<String, MonsterForUser> monstersNow) {
//		MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
//
//		int cashReward = mj.getCashReward();
//		if (cashReward > 0) {
//			int cashThen = userThen.getCash();
//			int cashNow = userNow.getCash();
//
//			int expectedCash = cashThen + cashReward;
//			assertTrue("Expected cash: " + expectedCash + ". Actual: "
//					+ cashNow, expectedCash == cashNow);
//		}
//
//		int oilReward = mj.getOilReward();
//		if (oilReward > 0) {
//			int oilThen = userThen.getOil();
//			int oilNow = userNow.getOil();
//
//			int expectedOil = oilThen + oilReward;
//			assertTrue("Expected oil: " + expectedOil + ". Actual: " + oilNow,
//					expectedOil == oilNow);
//		}
//
//		int gemReward = mj.getGemReward();
//		if (gemReward > 0) {
//			int gemThen = userThen.getGems();
//			int gemNow = userNow.getGems();
//
//			int expectedGem = gemThen + gemReward;
//			assertTrue("Expected gem: " + expectedGem + ". Actual: " + gemNow,
//					expectedGem == gemNow);
//		}
//
//		int monsterIdReward = mj.getMonsterIdReward();
//		if (monsterIdReward > 0) {
//			//check that monstersThen is different than monstersNow
//			boolean monstersDifferent = checkMonstersDifferent(monstersThen,
//					monstersNow);
//			assertTrue("Expected MonsterForUser: different. Actual: not.",
//					monstersDifferent);
//		}
//	}
//
//	protected boolean checkMonstersDifferent(
//			Map<String, MonsterForUser> monstersOne,
//			Map<String, MonsterForUser> monstersTwo) {
//		boolean monstersDifferent = false;
//
//		if (monstersOne.size() != monstersTwo.size()) {
//			monstersDifferent = true;
//			return monstersDifferent;
//		}
//		//since same size, then one of the monsters might be different
//		for (String mfuId : monstersOne.keySet()) {
//
//			boolean existsTwo = monstersTwo.containsKey(mfuId);
//
//			assertTrue("Expected monsterForUser: exists. Actual: doesn't.",
//					existsTwo);
//
//			MonsterForUser one = monstersOne.get(mfuId);
//			MonsterForUser two = monstersTwo.get(mfuId);
//
//			int numPiecesOne = one.getNumPieces();
//			int numPiecesTwo = two.getNumPieces();
//
//			if (numPiecesOne == numPiecesTwo) {
//				continue;
//			}
//
//			monstersDifferent = true;
//			break;
//		}
//		return monstersDifferent;
//	}
//
//	protected String getTestUserId() {
//		return "11"; //Unit testing account
//	}
//
//	protected int getMiniJobTestStructId() {
//		return 171;
//	}
//
//	protected void sendSpawnMiniJobRequestEvent(User user, Date clientTime,
//			int numToSpawn, int structId) {
//		SpawnMiniJobRequestProto smjrp = createSpawnMiniJobRequestProto(user,
//				clientTime, numToSpawn, structId);
//		SpawnMiniJobRequestEvent smjre = new SpawnMiniJobRequestEvent();
//		smjre.setTag(0);
//		smjre.setSpawnMiniJobRequestProto(smjrp);
//
//		//SENDING THE REQUEST
//		getSpawnMiniJobController().handleEvent(smjre);
//	}
//
//	protected SpawnMiniJobRequestProto createSpawnMiniJobRequestProto(
//			User user, Date clientTime, int numToSpawn, int structId) {
//		assertTrue("Expected user: not null. Actual: " + user, null != user);
//		MinimumUserProto mup = CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user, null);
//
//		SpawnMiniJobRequestProto.Builder smjrpb = SpawnMiniJobRequestProto
//				.newBuilder();
//
//		smjrpb.setSender(mup);
//		smjrpb.setClientTime(clientTime.getTime());
//		smjrpb.setNumToSpawn(numToSpawn);
//		smjrpb.setStructId(structId);
//
//		return smjrpb.build();
//	}
//
//	protected void sendBeginMiniJobRequestEvent(User user, Date clientTime,
//			Collection<String> userMonsterIds, String userMiniJobId) {
//		BeginMiniJobRequestProto bmjrp = createBeginMinijobRequestProto(user,
//				clientTime, userMonsterIds, userMiniJobId);
//		BeginMiniJobRequestEvent bmjre = new BeginMiniJobRequestEvent();
//		bmjre.setTag(0);
//		bmjre.setBeginMiniJobRequestProto(bmjrp);
//
//		//SENDING THE REQUEST
//		getBeginMiniJobController().handleEvent(bmjre);
//	}
//
//	protected BeginMiniJobRequestProto createBeginMinijobRequestProto(
//			User user, Date clientTime, Collection<String> userMonsterIds,
//			String userMiniJobId) {
//		assertTrue("Expected user: not null. Actual: " + user, null != user);
//		MinimumUserProto mup = CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user, null);
//
//		BeginMiniJobRequestProto.Builder bmjrpb = BeginMiniJobRequestProto
//				.newBuilder();
//		bmjrpb.setSender(mup);
//		bmjrpb.setClientTime(clientTime.getTime());
//		bmjrpb.addAllUserMonsterUuids(userMonsterIds);
//		bmjrpb.setUserMiniJobUuid(userMiniJobId);
//
//		return bmjrpb.build();
//	}
//
//	protected void sendCompleteMiniJobRequestEvent(User user, Date clientTime,
//			String userMiniJobId, boolean isSpeedUp, int gemCost) {//,
//		//List<UserMonsterCurrentHealthProto> umchp) {
//		CompleteMiniJobRequestProto cmjrp = createCompleteMiniJobRequestProto(
//				user, clientTime, userMiniJobId, isSpeedUp, gemCost);//, umchp);
//
//		CompleteMiniJobRequestEvent cmjre = new CompleteMiniJobRequestEvent();
//		cmjre.setTag(0);
//		cmjre.setCompleteMiniJobRequestProto(cmjrp);
//
//		//SENDING THE REQUEST
//		getCompleteMiniJobController().handleEvent(cmjre);
//	}
//
//	protected CompleteMiniJobRequestProto createCompleteMiniJobRequestProto(
//			User user, Date clientTime, String userMiniJobId,
//			boolean isSpeedUp, int gemCost) {
//		assertTrue("Expected user: not null. Actual: " + user, null != user);
//		MinimumUserProto mup = CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user, null);
//
//		CompleteMiniJobRequestProto.Builder cmjrpb = CompleteMiniJobRequestProto
//				.newBuilder();
//		cmjrpb.setSender(mup);
//		cmjrpb.setClientTime(clientTime.getTime());
//		cmjrpb.setUserMiniJobUuid(userMiniJobId);
//		cmjrpb.setIsSpeedUp(isSpeedUp);
//		cmjrpb.setGemCost(gemCost);
//		//cmjrpb.addAllUmchp(umchp);
//
//		return cmjrpb.build();
//	}
//
//	protected void sendRedeemMiniJobRequestEvent(User user,
//			MiniJobForUser mjfu, Date clientTime,
//			List<UserMonsterCurrentHealthProto> umchp) {
//		RedeemMiniJobRequestProto rmjrp = createRedeemMiniJobRequestProto(user,
//				mjfu, clientTime, umchp);
//
//		RedeemMiniJobRequestEvent rmjre = new RedeemMiniJobRequestEvent();
//		rmjre.setTag(0);
//		rmjre.setRedeemMiniJobRequestProto(rmjrp);
//
//		//SENDING THE REQUEST
//		getRedeemMiniJobController().handleEvent(rmjre);
//	}
//
//	protected RedeemMiniJobRequestProto createRedeemMiniJobRequestProto(
//			User user, MiniJobForUser mjfu, Date clientTime,
//			List<UserMonsterCurrentHealthProto> umchp) {
//		assertTrue("Expected user: not null. Actual: " + user, null != user);
//		int miniJobId = mjfu.getMiniJobId();
//		MiniJob mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
//		int cashReward = mj.getCashReward();
//		int oilReward = mj.getOilReward();
//
//		MinimumUserProto mup = CreateInfoProtoUtils
//				.createMinimumUserProtoFromUserAndClan(user, null);
//
//		MinimumUserProtoWithMaxResources.Builder mupwmrb = MinimumUserProtoWithMaxResources
//				.newBuilder();
//		int maxCash = cashReward + user.getCash();
//		int maxOil = oilReward + user.getOil();
//		mupwmrb.setMaxCash(maxCash);
//		mupwmrb.setMaxOil(maxOil);
//		mupwmrb.setMinUserProto(mup);
//
//		RedeemMiniJobRequestProto.Builder rmjrpb = RedeemMiniJobRequestProto
//				.newBuilder();
//		rmjrpb.setSender(mupwmrb.build());
//		rmjrpb.setClientTime(clientTime.getTime());
//		rmjrpb.setUserMiniJobUuid(mjfu.getId());
//		rmjrpb.addAllUmchp(umchp);
//
//		return rmjrpb.build();
//	}
//
//	protected void undoMiniJobTest(String userId, User user,
//			Date lastMiniJobSpawnTime, Collection<String> miniJobForUserIds) {
//		deleteMiniJobForUser(userId, miniJobForUserIds);
//
//		//undo the lastMiniJobSpawnTime
//		Timestamp nowTime = null;
//		if (null != lastMiniJobSpawnTime) {
//			nowTime = new Timestamp(lastMiniJobSpawnTime.getTime());
//		}
//		user.updateLastMiniJobGeneratedTime(lastMiniJobSpawnTime, nowTime);
//	}
//
//	protected void deleteMiniJobForUser(String userId,
//			Collection<String> miniJobForUserIds) {
//		String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;
//		int size = miniJobForUserIds.size();
//		List<String> questions = Collections.nCopies(size, "?");
//		String questionMarks = StringUtils.csvList(questions);
//
//		StringBuilder querySb = new StringBuilder();
//		querySb.append("DELETE FROM ");
//		querySb.append(tableName);
//		querySb.append(" WHERE ");
//		querySb.append(DBConstants.MINI_JOB_FOR_USER__ID);
//		querySb.append(" IN (");
//		querySb.append(questionMarks);
//		querySb.append(")");
//		querySb.append(" AND ");
//		querySb.append(DBConstants.MINI_JOB_FOR_USER__USER_ID);
//		querySb.append("=");
//		querySb.append(userId);
//		String query = querySb.toString();
//
//		log.info("deleteMiniJobForUser() query=" + query);
//		List<String> miniJobForUserIdList = new ArrayList<String>(
//				miniJobForUserIds);
//		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
//				miniJobForUserIdList);
//		log.info("num mini_job_for_user deleted=" + numDeleted);
//
//		assertTrue(
//				"Expected rows deleted: " + size + ". Actual: " + numDeleted,
//				size == numDeleted);
//	}
//
//	public UserRetrieveUtils2 getUserRetrieveUtils() {
//		return userRetrieveUtils;
//	}
//
//	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
//		this.userRetrieveUtils = userRetrieveUtils;
//	}
//
//	public TimeUtils getTimeUtils() {
//		return timeUtils;
//	}
//
//	public void setTimeUtils(TimeUtils timeUtils) {
//		this.timeUtils = timeUtils;
//	}
//
//	public MiniJobForUserRetrieveUtil getMiniJobForUserRetieveUtil() {
//		return miniJobForUserRetieveUtil;
//	}
//
//	public void setMiniJobForUserRetieveUtil(
//			MiniJobForUserRetrieveUtil miniJobForUserRetieveUtil) {
//		this.miniJobForUserRetieveUtil = miniJobForUserRetieveUtil;
//	}
//
//	public MonsterForUserRetrieveUtils2 getMonsterForUserRetrieveUtils() {
//		return monsterForUserRetrieveUtils;
//	}
//
//	public void setMonsterForUserRetrieveUtils(
//			MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
//		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
//	}
//
//	public SpawnMiniJobController getSpawnMiniJobController() {
//		return spawnMiniJobController;
//	}
//
//	public void setSpawnMiniJobController(
//			SpawnMiniJobController spawnMiniJobController) {
//		this.spawnMiniJobController = spawnMiniJobController;
//	}
//
//	public BeginMiniJobController getBeginMiniJobController() {
//		return beginMiniJobController;
//	}
//
//	public void setBeginMiniJobController(
//			BeginMiniJobController beginMiniJobController) {
//		this.beginMiniJobController = beginMiniJobController;
//	}
//
//	public CompleteMiniJobController getCompleteMiniJobController() {
//		return completeMiniJobController;
//	}
//
//	public void setCompleteMiniJobController(
//			CompleteMiniJobController completeMiniJobController) {
//		this.completeMiniJobController = completeMiniJobController;
//	}
//
//	public RedeemMiniJobController getRedeemMiniJobController() {
//		return redeemMiniJobController;
//	}
//
//	public void setRedeemMiniJobController(
//			RedeemMiniJobController redeemMiniJobController) {
//		this.redeemMiniJobController = redeemMiniJobController;
//	}
//
//}
