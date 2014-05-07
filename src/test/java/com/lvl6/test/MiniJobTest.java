package com.lvl6.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.elasticsearch.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.BeginMiniJobRequestEvent;
import com.lvl6.events.request.CompleteMiniJobRequestEvent;
import com.lvl6.events.request.SpawnMiniJobRequestEvent;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobRequestProto;
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobRequestProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.MiniJobForUserRetrieveUtil;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.server.controller.BeginMiniJobController;
import com.lvl6.server.controller.CompleteMiniJobController;
import com.lvl6.server.controller.SpawnMiniJobController;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class MiniJobTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserRetrieveUtils userRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected MiniJobForUserRetrieveUtil miniJobForUserRetieveUtil;
	
	@Autowired
	protected MonsterForUserRetrieveUtils monsterForUserRetrieveUtils;
	
	@Autowired
	protected SpawnMiniJobController spawnMiniJobController;
	
	@Autowired
	protected BeginMiniJobController beginMiniJobController;
	
	@Autowired
	protected CompleteMiniJobController completeMiniJobController;
	
	@Test
//	@Rollback(true) //doesn't roll back transaction >:C
//	@Transactional //just manually undo...
	public void testSendSpawnMiniJob() {
		log.info("spawn mini job");
		int userId = getTestUserId();
		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
		Date lastMiniJobSpawnTimeThen = unitTesterThen.getLastMiniJobGeneratedTime();

		Date clientTime = new Date();
		int numToSpawn = 2;
		int structId = getMiniJobTestStructId();

		//get count of user's current MiniJobs
		Map<Long, MiniJobForUser> userMiniJobIdsToUserMiniJobsThen =
				getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, null);
		int miniJobCountThen = userMiniJobIdsToUserMiniJobsThen.size();
		log.info("sendSpawnMiniJob() userMiniJobsThen=" +
				userMiniJobIdsToUserMiniJobsThen);
		
		sendSpawnMiniJobRequestEvent(unitTesterThen, clientTime, numToSpawn, structId);
		
		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
		//should have new entries for this user
		Map<Long, MiniJobForUser> userMiniJobIdsToUserMiniJobsNow =
				getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, null);
		int miniJobCountNow = userMiniJobIdsToUserMiniJobsNow.size();
		log.info("sendSpawnMiniJob() userMiniJobsNow=" +
				userMiniJobIdsToUserMiniJobsNow);
		
		int newMiniJobAmount = miniJobCountNow - miniJobCountThen; 
		assertTrue("Expected new MiniJobs: " + numToSpawn + ". Actual: " +
				newMiniJobAmount, numToSpawn == newMiniJobAmount);

		
		//check to see that the user's last mini job spawn time changed
		User unitTesterNow = getUserRetrieveUtils().getUserById(userId);
		Date lastMiniJobSpawnTimeNow = unitTesterNow.getLastMiniJobGeneratedTime();
		log.info("!!!!!!!!!!!!!!!!!!!!! ");
		log.info("lastMiniJobSpawnTimeThen=" + lastMiniJobSpawnTimeThen);
		log.info("lastMiniJobSpawnTimeNow=" + lastMiniJobSpawnTimeNow);
		boolean spawnTimeChanged = getTimeUtils().isFirstEarlierThanSecond(
				lastMiniJobSpawnTimeThen, lastMiniJobSpawnTimeNow);
		log.info("spawnTimeChanged=" + spawnTimeChanged);
		assertTrue("Expected miniJobSpawnTime: in the future. Actual: not." +
				" then=" + lastMiniJobSpawnTimeThen + ", now=" +
				lastMiniJobSpawnTimeNow, spawnTimeChanged);
		
		
		//manually delete the created MiniJobForUser
		Set<Long> set1 = userMiniJobIdsToUserMiniJobsNow.keySet();
		Set<Long> set2 = userMiniJobIdsToUserMiniJobsThen.keySet();
		Set<Long> newMiniJobForUserIds = Sets.difference(set1, set2);
		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen,
				newMiniJobForUserIds);
	}
	
	@Test
	public void testSendBeginMiniJob() {
		log.info("begin mini job");
		int userId = getTestUserId();
		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
		Date clientTime = new Date();

		//spawn the minijob
		MiniJobForUser mjfuThen = createNewMiniJobForUser(userId,
				unitTesterThen, clientTime); 
		long newMiniJobForUserIdThen = mjfuThen.getId();
		Collection<Long> userMiniJobIds =
				Collections.singleton(newMiniJobForUserIdThen);
		assertTrue("Expected startTime: null. Actual: " + mjfuThen,
				null == mjfuThen.getTimeStarted());
		assertTrue("Expected userMonsterIds: null or empty. Actual: " +
				mjfuThen,
				null == mjfuThen.getUserMonsterIdStr() ||
				mjfuThen.getUserMonsterIdStr().isEmpty());
		
		//send the begin minijob request
		Map<Long, MonsterForUser> mfuIdToMfu = getMonsterForUserRetrieveUtils()
				.getSpecificOrAllUserMonstersForUser(userId, null);
		assertTrue("Expected userMonsters: not null, or empty. Actual: " +
				mfuIdToMfu,
				null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
		Collection<Long> userMonsterIds = mfuIdToMfu.keySet();
		log.info("testSendBeginMiniJob() begfore" +
				" sendBeginMiniJobRequestEvent");  
		sendBeginMiniJobRequestEvent(unitTesterThen, clientTime,
				userMonsterIds, newMiniJobForUserIdThen);
		log.info("testSendBeginMiniJob() after sendBeginMiniJobRequestEvent");  
		
		
		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
		Map<Long, MiniJobForUser> mjfuIdToMjfu = getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds) ;
		
		MiniJobForUser mjfuNow  = mjfuIdToMjfu.get(newMiniJobForUserIdThen);
		
		Date startTime = mjfuNow.getTimeStarted();
		assertTrue("Expected startTime: not null. Actual: " + mjfuNow,
				null != startTime);
		
		long startTimeMillis = startTime.getTime();
		long afterStartTimeMillis = startTimeMillis + 10000;
		long beforeStartTimeMillis = startTimeMillis - 10000;
		long clientTimeMillis = clientTime.getTime();
		
		assertTrue("Expected startTime: " + clientTime + ". Actual: " +
				mjfuNow,
				afterStartTimeMillis > clientTimeMillis &&
				beforeStartTimeMillis < clientTimeMillis);
		assertTrue("Expected userMonsterIds: nonNull or empty. Actual: " +
				mjfuNow,
				null != mjfuNow.getUserMonsterIds() &&
				userMonsterIds.containsAll(mjfuNow.getUserMonsterIds()));
		
		
		
		//manually delete the created MiniJobForUser
		Date lastMiniJobSpawnTimeThen = unitTesterThen
				.getLastMiniJobGeneratedTime();
		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen,
				userMiniJobIds);
	}
	protected MiniJobForUser createNewMiniJobForUser(
			int userId, User unitTester, Date clientTime) {
		int numToSpawn = 1;
		int structId = getMiniJobTestStructId();

		Map<Long, MiniJobForUser> miniJobIdToUserMiniJobsThen =
				getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, null);
		
		sendSpawnMiniJobRequestEvent(unitTester, clientTime, numToSpawn, structId);
		
		Map<Long, MiniJobForUser> miniJobIdToUserMiniJobsNow =
				getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, null);
		
		//get the newly created MiniJob and begin it
		Set<Long> set1 = miniJobIdToUserMiniJobsNow.keySet();
		Set<Long> set2 = miniJobIdToUserMiniJobsThen.keySet();
		Set<Long> newMiniJobForUserIds = Sets.difference(set1, set2);
		
		long newMiniJobForUserId = (newMiniJobForUserIds.iterator()).next();
		
		Collection<Long> userMiniJobIds =
				Collections.singleton(newMiniJobForUserId);
		Map<Long, MiniJobForUser> miniJobForUserIdToMjfu =
				getMiniJobForUserRetieveUtil()
				.getSpecificOrAllIdToMiniJobForUser(userId, userMiniJobIds);
		
		MiniJobForUser mjfuThen =
				miniJobForUserIdToMjfu.get(newMiniJobForUserId);
		return mjfuThen;
	}
	
	@Test
	public void testSendCompleteMiniJob() {
		log.info("begin mini job");
		int userId = getTestUserId();
		User unitTesterThen = getUserRetrieveUtils().getUserById(userId);
		Date clientTime = new Date();
		Map<Long, MonsterForUser> mfuIdToMfu = getMonsterForUserRetrieveUtils()
				.getSpecificOrAllUserMonstersForUser(userId, null);

		//spawn the minijob
		MiniJobForUser mjfuThen = createNewMiniJobForUser(userId,
				unitTesterThen, clientTime); 
		
		//begin the minijob
		long newMiniJobForUserIdThen = mjfuThen.getId();
		beginMiniJobForUser(userId, unitTesterThen, clientTime,
				newMiniJobForUserIdThen, mjfuThen, mfuIdToMfu);
		
		List<UserMonsterCurrentHealthProto> umchp = createUmchp(mfuIdToMfu);
		
		//send the complete mini job request
		sendCompleteMiniJobRequestEvent(unitTesterThen, clientTime,
				newMiniJobForUserIdThen, false, 0, umchp);
		
		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
		
		//manually delete the created MiniJobForUser
		Collection<Long> userMiniJobIds =
				Collections.singleton(newMiniJobForUserIdThen);
		Date lastMiniJobSpawnTimeThen = unitTesterThen
				.getLastMiniJobGeneratedTime();
		undoMiniJobTest(userId, unitTesterThen, lastMiniJobSpawnTimeThen, userMiniJobIds);
	}
	protected void beginMiniJobForUser(int userId, User user,
			Date clientTime, long newMiniJobForUserId, MiniJobForUser mjfu,
			Map<Long, MonsterForUser> mfuIdToMfu) {
		assertTrue("Expected startTime: null. Actual: " + mjfu,
				null == mjfu.getTimeStarted());
		assertTrue("Expected userMonsterIds: null or empty. Actual: " +
				mjfu,
				null == mjfu.getUserMonsterIdStr() ||
				mjfu.getUserMonsterIdStr().isEmpty());
		
		//begin the minijob
		assertTrue("Expected userMonsters: not null, or empty. Actual: " +
				mfuIdToMfu,
				null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
		Collection<Long> userMonsterIds = mfuIdToMfu.keySet();
		sendBeginMiniJobRequestEvent(user, clientTime,
				userMonsterIds, newMiniJobForUserId);
	}
	protected List<UserMonsterCurrentHealthProto> createUmchp(
			Map<Long, MonsterForUser> mfuIdToMfu) {
		assertTrue("Expected userMonsters: not null, or empty. Actual: " +
				mfuIdToMfu,
				null != mfuIdToMfu && !mfuIdToMfu.isEmpty());
		
		List<UserMonsterCurrentHealthProto> umchpList =
				new ArrayList<UserMonsterCurrentHealthProto>();
		
		for (MonsterForUser mfu : mfuIdToMfu.values()) {
			UserMonsterCurrentHealthProto umchp = CreateInfoProtoUtils.
					createUserMonsterCurrentHealthProto(mfu);
			umchpList.add(umchp);
		}
		
		return umchpList;
	}
	
	
	protected int getTestUserId() {
		return 11; //Unit testing account
	}
	
	protected int getMiniJobTestStructId() {
		return 130; //TODO: INCORRECT. Get the real one, this is just a dummy value;
	}
	
	protected void sendSpawnMiniJobRequestEvent(User user,
			Date clientTime, int numToSpawn, int structId) {
		SpawnMiniJobRequestProto smjrp = createSpawnMiniJobRequestProto(
				user, clientTime, numToSpawn, structId);
		SpawnMiniJobRequestEvent smjre = new SpawnMiniJobRequestEvent();
		smjre.setTag(0);
		smjre.setSpawnMiniJobRequestProto(smjrp);
		
		//SENDING THE REQUEST
		getSpawnMiniJobController().handleEvent(smjre);
	}
	protected SpawnMiniJobRequestProto createSpawnMiniJobRequestProto(
			User user, Date clientTime, int numToSpawn, int structId) {
		assertTrue("Expected user: not null. Actual: " + user,
				null != user);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(user);
		
		SpawnMiniJobRequestProto.Builder smjrpb =
				SpawnMiniJobRequestProto.newBuilder();
		
		smjrpb.setSender(mup);
		smjrpb.setClientTime(clientTime.getTime());
		smjrpb.setNumToSpawn(numToSpawn);
		smjrpb.setStructId(structId);
		
		return smjrpb.build();
	}
	
	
	protected void sendBeginMiniJobRequestEvent(User user, Date clientTime,
			Collection<Long> userMonsterIds, long userMiniJobId) {
		BeginMiniJobRequestProto bmjrp = createBeginMinijobRequestProto(
				user, clientTime, userMonsterIds, userMiniJobId);
		BeginMiniJobRequestEvent bmjre = new BeginMiniJobRequestEvent();
		bmjre.setTag(0);
		bmjre.setBeginMiniJobRequestProto(bmjrp);
		
		//SENDING THE REQUEST
		getBeginMiniJobController().handleEvent(bmjre);
	}
	protected BeginMiniJobRequestProto createBeginMinijobRequestProto(
			User user, Date clientTime, Collection<Long> userMonsterIds,
			long userMiniJobId) {
		assertTrue("Expected user: not null. Actual: " + user,
				null != user);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(user);
		
		BeginMiniJobRequestProto.Builder bmjrpb =
				BeginMiniJobRequestProto.newBuilder();
		bmjrpb.setSender(mup);
		bmjrpb.setClientTime(clientTime.getTime());
		bmjrpb.addAllUserMonsterIds(userMonsterIds);
		bmjrpb.setUserMiniJobId(userMiniJobId);
		
		return bmjrpb.build();
	}
	
	
	protected void sendCompleteMiniJobRequestEvent(User user, Date clientTime,
			long userMiniJobId, boolean isSpeedUp, int gemCost,
			List<UserMonsterCurrentHealthProto> umchp) {
		CompleteMiniJobRequestProto cmjrp = createCompleteMiniJobRequestProto(
				user, clientTime, userMiniJobId, isSpeedUp, gemCost, umchp);
		
		CompleteMiniJobRequestEvent cmjre = new CompleteMiniJobRequestEvent();
		cmjre.setTag(0);
		cmjre.setCompleteMiniJobRequestProto(cmjrp);
		
		//SENDING THE REQUEST
		getCompleteMiniJobController().handleEvent(cmjre);
	}
	protected CompleteMiniJobRequestProto createCompleteMiniJobRequestProto(
			User user, Date clientTime, long userMiniJobId, boolean isSpeedUp,
			int gemCost, List<UserMonsterCurrentHealthProto> umchp) {
		assertTrue("Expected user: not null. Actual: " + user,
				null != user);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(user);
		
		CompleteMiniJobRequestProto.Builder cmjrpb =
				CompleteMiniJobRequestProto.newBuilder();
		cmjrpb.setSender(mup);
		cmjrpb.setClientTime(clientTime.getTime());
		cmjrpb.setUserMiniJobId(userMiniJobId);
		cmjrpb.setIsSpeedUp(isSpeedUp);
		cmjrpb.setGemCost(gemCost);
		cmjrpb.addAllUmchp(umchp);
		
		return cmjrpb.build();
	}
	
	
	protected void undoMiniJobTest(int userId, User user,
			Date lastMiniJobSpawnTime,
			Collection<Long> miniJobForUserIds) {
		deleteMiniJobForUser(userId, miniJobForUserIds);

		//undo the lastMiniJobSpawnTime
		Timestamp nowTime = null;
		if (null != lastMiniJobSpawnTime) {
			nowTime = new Timestamp(lastMiniJobSpawnTime.getTime());
		}
		user.updateLastMiniJobGeneratedTime(lastMiniJobSpawnTime, nowTime);
	}
	protected void deleteMiniJobForUser(int userId,
			Collection<Long> miniJobForUserIds) {
		String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;
		int size = miniJobForUserIds.size();
		List<String> questions = Collections.nCopies(size, "?");
	    String questionMarks = StringUtils.csvList(questions);
	    
	    StringBuilder querySb = new StringBuilder();
	    querySb.append("DELETE FROM ");
	    querySb.append(tableName);
	    querySb.append(" WHERE ");
	    querySb.append(DBConstants.MINI_JOB_FOR_USER__ID);
	    querySb.append(" IN (");
	    querySb.append(questionMarks);
	    querySb.append(")");
	    querySb.append(" AND ");
	    querySb.append(DBConstants.MINI_JOB_FOR_USER__USER_ID);
	    querySb.append("=");
	    querySb.append(userId);
	    String query = querySb.toString();
	    
	    log.info("deleteMiniJobForUser() query=" + query);
	    List<Long> miniJobForUserIdList =
	    		new ArrayList<Long>(miniJobForUserIds);
	    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
	    		miniJobForUserIdList);
	    log.info("num mini_job_for_user deleted=" + numDeleted);
	    
	    assertTrue("Expected rows deleted: " + size + ". Actual: " +
	    		numDeleted, size == numDeleted);
	}

	
	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public MiniJobForUserRetrieveUtil getMiniJobForUserRetieveUtil() {
		return miniJobForUserRetieveUtil;
	}

	public void setMiniJobForUserRetieveUtil(
			MiniJobForUserRetrieveUtil miniJobForUserRetieveUtil) {
		this.miniJobForUserRetieveUtil = miniJobForUserRetieveUtil;
	}

	public MonsterForUserRetrieveUtils getMonsterForUserRetrieveUtils() {
		return monsterForUserRetrieveUtils;
	}

	public void setMonsterForUserRetrieveUtils(
			MonsterForUserRetrieveUtils monsterForUserRetrieveUtils) {
		this.monsterForUserRetrieveUtils = monsterForUserRetrieveUtils;
	}

	public SpawnMiniJobController getSpawnMiniJobController() {
		return spawnMiniJobController;
	}

	public void setSpawnMiniJobController(
			SpawnMiniJobController spawnMiniJobController) {
		this.spawnMiniJobController = spawnMiniJobController;
	}

	public BeginMiniJobController getBeginMiniJobController() {
		return beginMiniJobController;
	}

	public void setBeginMiniJobController(
			BeginMiniJobController beginMiniJobController) {
		this.beginMiniJobController = beginMiniJobController;
	}

	public CompleteMiniJobController getCompleteMiniJobController() {
		return completeMiniJobController;
	}

	public void setCompleteMiniJobController(
			CompleteMiniJobController completeMiniJobController) {
		this.completeMiniJobController = completeMiniJobController;
	}

}
