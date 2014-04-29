package com.lvl6.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.BeginObstacleRemovalRequestEvent;
import com.lvl6.events.request.ObstacleRemovalCompleteRequestEvent;
import com.lvl6.events.request.SpawnObstacleRequestEvent;
import com.lvl6.info.Obstacle;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalRequestProto;
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteRequestProto;
import com.lvl6.proto.EventStructureProto.SpawnObstacleRequestProto;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ObstacleForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ObstacleRetrieveUtils;
import com.lvl6.server.controller.BeginObstacleRemovalController;
import com.lvl6.server.controller.ObstacleRemovalCompleteController;
import com.lvl6.server.controller.SpawnObstacleController;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.CreateInfoProtoUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class ObstacleTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Autowired
	protected SpawnObstacleController spawnObstacleController;
	
	@Autowired
	protected BeginObstacleRemovalController beginObstacleRemovalController;
	
	@Autowired
	protected ObstacleRemovalCompleteController obstacleRemovalCompleteController;
	
	@Autowired
	protected UserRetrieveUtils userRetrieveUtils;
	
	@Autowired
	protected TimeUtils timeUtils;
	
	@Autowired
	protected ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil;
	

	@Test
//	@Rollback(true)
	public void testObstacleEvents() {
		spawnObstacle();
		beginRemoveObstacle();
		beginCompletingObstacleRemovalNotMaxObstacles();
	}
	
	@Test
//	@Rollback(true)
	public void testObstacleEventsAtMaxObstacles() {
		spawnObstacle();
		beginRemoveObstacle();
		beginCompletingObstacleRemovalAtMaxObstacles();
	}
	
	public void spawnObstacle() {
		log.info("spawning obstacle");
		//create user proto
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);
		assertTrue("Expected user: not null. Actual: " + unitTester,
				null != unitTester);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(unitTester);
		
		//create obstacle proto
    	MinimumObstacleProto mop = createTestObstacleProto();
    	int obstacleId = mop.getObstacleId();
    	//make sure it exists
    	Obstacle obstacle = ObstacleRetrieveUtils.getObstacleForId(obstacleId);
    	assertTrue("Expected obstacle: not null. Actual:" + obstacle,
    			null != obstacle);
    	
    	//keep track of how many the user has so far
    	List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		assertTrue("Expected obstacles: not null. Actual: " + ofuList,
				null != ofuList);
		int oldSize = ofuList.size();

    	//calculate when user last spawned obstacle (10 min ago) 
    	Date nowDate = getTimeUtils().createPstDateAddMinutes(new Date(), 0);
    	log.info("nowDate=" + nowDate);
    	Date tenMinAgo = getTimeUtils().createPstDateAddMinutes(nowDate, -10);
    	log.info("tenMinAgo=" + tenMinAgo);
    	Timestamp tenMinAgoTimestamp = new Timestamp(tenMinAgo.getTime());
    	boolean updated = unitTester.updateRelativeGemsObstacleTimeNumRemoved(
    			0, tenMinAgoTimestamp, 0);
    	
    	assertTrue("Expected update status: true. Actual: " + updated, updated);
    	
    	long nowMillis = nowDate.getTime();
    	
    	//Create SpawnObstacleRequestProto, with spawn time being now
    	SpawnObstacleRequestProto.Builder sorpb =
    			SpawnObstacleRequestProto.newBuilder();
    	sorpb.setSender(mup);
    	sorpb.addProspectiveObstacles(mop);
    	sorpb.setCurTime(nowMillis);
    	
    	//have controller process it
    	SpawnObstacleRequestEvent sore = new SpawnObstacleRequestEvent();
    	sore.setTag(1);
    	sore.setSpawnObstacleRequestProto(sorpb.build());
    	getSpawnObstacleController().handleEvent(sore);
    	
    	
    	//make sure the user's obstacle spawned times are different after
    	//spawning obstacle
    	User unitTesterSpawnedObstacle = getUserRetrieveUtils()
    			.getUserById(userId);

    	//make sure user exists
    	int otherUserId = unitTesterSpawnedObstacle.getId();
    	assertTrue("Expected userId: " + userId + ". Actual: " +
    			unitTesterSpawnedObstacle.getId(), userId == otherUserId);
    	
    	//check to make sure user's last obstacle spawn time is ten minutes ago
    	Date beforeTime = unitTester.getLastObstacleSpawnedTime();
    	Date nowTime = unitTesterSpawnedObstacle.getLastObstacleSpawnedTime();
    	
    	log.info("unitTester=" + unitTester);
    	log.info("unitTesterSpawnedObstacle=" + unitTesterSpawnedObstacle);
    	int numMinDifference = getTimeUtils()
    			.numMinutesDifference(beforeTime, nowTime);
    	assertTrue("Expected time diff: 10. Actual time diff: " +
    			numMinDifference,
    			11 >= numMinDifference && numMinDifference >= 9);
    	
    	//make sure the num obstacles user has changed
    	List<ObstacleForUser> newOfuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		assertTrue("Expected obstacles: not null. Actual:" + newOfuList,
				null != ofuList);
		int newSize = newOfuList.size();
		int expectedSize = oldSize + 1;
		
		assertTrue("Expected num obstacles: " + expectedSize + ". Actual: " +
				newSize, expectedSize == newSize);
    	
    	log.info("ending spawning obstacle");
	}

	//call spawnObstacle() first
	public void beginRemoveObstacle() {
		log.info("begin obstacle removal");
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(unitTester);
		
		//check the user obstacle exists
		List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		assertTrue("Expected obstacles: not null. Actual: " + ofuList,
				null != ofuList);
		
		//calculate when obstacle will be removed (now) 
		Date nowDate = new Date();

		//make sure object's start removal time is null
		//this is assuming spawnObstacle works...
		ObstacleForUser ofu = getLastCreatedObstacle(ofuList);
		Date removalTime = ofu.getRemovalTime();
		assertTrue("Expected time: null. Actual: " + removalTime,
				null == removalTime);
		int ofuId = ofu.getId();
		
		//so as to leave db the way it was before doing this test
		User richerUnitTester = getUserRetrieveUtils().getUserById(userId);
		richerUnitTester.updateRelativeCashAndOilAndGems(1, 0, 0);
		
		//send event to remove it now
		BeginObstacleRemovalRequestProto.Builder borrpb =
				BeginObstacleRemovalRequestProto.newBuilder();
		borrpb.setSender(mup);
		borrpb.setCurTime(nowDate.getTime());
		borrpb.setResourceChange(-1);
		borrpb.setResourceType(ResourceType.CASH);
		borrpb.setUserObstacleId(ofuId);
		
		BeginObstacleRemovalRequestEvent borre =
				new BeginObstacleRemovalRequestEvent();
		borre.setTag(1);
		borre.setBeginObstacleRemovalRequestProto(borrpb.build());
		getBeginObstacleRemovalController().handleEvent(borre);
		
		//check to make sure user's currency is back where it was
		User poorerUnitTester = getUserRetrieveUtils().getUserById(userId);
		int expectedCash = unitTester.getCash();
		int actualCash = poorerUnitTester.getCash();
		assertTrue("Expected cash: " + expectedCash + ". Actual: " + actualCash,
				expectedCash == actualCash);

		//check to make sure the removal time is non null and is set
		ObstacleForUser doomedOfu = getObstacleForUserRetrieveUtil()
				.getUserObstacleForId(ofuId);
		Date newRemovalTime = doomedOfu.getRemovalTime();
		assertTrue("Expected time: not null. Actual=" + removalTime,
				null != newRemovalTime);
		
		assertTrue("Expected time: " + nowDate + ". Actual: " + newRemovalTime,
				getTimeUtils()
				.numMinutesDifference(nowDate, newRemovalTime) == 0);
		log.info("ending obstacle removal");
	}
	
	//If ever switching from int id to string, this method needs to change
	private ObstacleForUser getLastCreatedObstacle(
			List<ObstacleForUser> ofuList) {
		Comparator<ObstacleForUser> c = new Comparator<ObstacleForUser>() {
			  @Override
			  public int compare(ObstacleForUser o1, ObstacleForUser o2) {
				  if (o1.getId() < o2.getId()) {
					  return -1;
				  } else if (o1.getId() > o2.getId()) {
					  return 1;
				  } else {
					  return 0;
				  }
			  }
		  };
		  
		  List<ObstacleForUser> tempOfuList =
				  new ArrayList<ObstacleForUser>(ofuList);
		  Collections.sort(tempOfuList, c);
		  int lastIndex = tempOfuList.size() - 1;
		  
		  return tempOfuList.get(lastIndex);
	}
	
	public void beginCompletingObstacleRemovalNotMaxObstacles() {
		log.info("begin completing removing obstacle");
		
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);
		int numObstaclesRemoved = unitTester.getNumObstaclesRemoved();
		Date oldLastObstacleSpawnedTime = unitTester
				.getLastObstacleSpawnedTime();
		
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(unitTester);
		
		//calculate when obstacle is done being removed ten minutes from now
    	Date nowDate = new Date();
    	log.info("nowDate=" + nowDate);
    	Date tenMinFromNow = getTimeUtils().createPstDateAddMinutes(nowDate, 10);
    	log.info("tenMinFromNow=" + tenMinFromNow);
    	Timestamp tenMinFromNowTimestamp = new Timestamp(tenMinFromNow.getTime());
    	
		//check the user obstacle exists
		List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		assertTrue("Expected obstacles: not null. Actual: " + ofuList,
				null != ofuList);
		int oldSize = ofuList.size();

		ObstacleForUser ofu = getLastCreatedObstacle(ofuList);
		int ofuId = ofu.getId();
		
		//create the event
		ObstacleRemovalCompleteRequestProto.Builder orcrpb =
				ObstacleRemovalCompleteRequestProto.newBuilder();
		orcrpb.setSender(mup);
		orcrpb.setSpeedUp(false);
		orcrpb.setCurTime(tenMinFromNowTimestamp.getTime());
		orcrpb.setUserObstacleId(ofuId);
		orcrpb.setAtMaxObstacles(false);
		
		//send the event for processing
		ObstacleRemovalCompleteRequestEvent orcre =
				new ObstacleRemovalCompleteRequestEvent();
		orcre.setTag(1);
		orcre.setObstacleRemovalCompleteRequestProto(orcrpb.build());
		getObstacleRemovalCompleteController().handleEvent(orcre);
		
		
		//check to make sure the user has one less obstacle 
		List<ObstacleForUser> newOfuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		int newSize = newOfuList.size();
		int expectedSize = oldSize - 1;
		assertTrue("Expected num obstacles: " + expectedSize + ". Actual: " +
				newSize, expectedSize == newSize);
		
		//check to make sure the user's num obstacles removed is one more
		User newUnitTester = getUserRetrieveUtils().getUserById(userId);
		int newNumObstaclesRemoved = newUnitTester.getNumObstaclesRemoved();
		int expectedRemoved = numObstaclesRemoved + 1;
		assertTrue("Expected obstacles removed: " + expectedRemoved +
				". Actual: " + newNumObstaclesRemoved,
				expectedRemoved == newNumObstaclesRemoved);
		
		//check to make sure the last obstacle spawn time remains same
		Date newLastObstacleSpawnedTime = newUnitTester
				.getLastObstacleSpawnedTime();
		assertTrue("Expected same obstacle spawn time. Actual times, old: " +
				oldLastObstacleSpawnedTime + ", new: " +
				newLastObstacleSpawnedTime,
				oldLastObstacleSpawnedTime.equals(newLastObstacleSpawnedTime));
		
		log.info("ending completing removing obstacle");
	}
	
	public void beginCompletingObstacleRemovalAtMaxObstacles() {
		log.info("begin completing removing obstacle at max");
		
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);
		int numObstaclesRemoved = unitTester.getNumObstaclesRemoved();
		Date oldLastObstacleSpawnedTime = unitTester
				.getLastObstacleSpawnedTime();
		
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(unitTester);
		
		//calculate when obstacle is done being removed ten minutes from now
    	Date nowDate = new Date();
    	log.info("nowDate=" + nowDate);
    	Date tenMinFromNow = getTimeUtils().createPstDateAddMinutes(nowDate, 10);
    	log.info("tenMinFromNow=" + tenMinFromNow);
    	Timestamp tenMinFromNowTimestamp = new Timestamp(tenMinFromNow.getTime());
    	
		//check the user obstacle exists
		List<ObstacleForUser> ofuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		assertTrue("Expected obstacles: not null. Actual: " + ofuList,
				null != ofuList);
		int oldSize = ofuList.size();

		ObstacleForUser ofu = getLastCreatedObstacle(ofuList);
		int ofuId = ofu.getId();
		
		//create the event
		ObstacleRemovalCompleteRequestProto.Builder orcrpb =
				ObstacleRemovalCompleteRequestProto.newBuilder();
		orcrpb.setSender(mup);
		orcrpb.setSpeedUp(false);
		orcrpb.setCurTime(tenMinFromNowTimestamp.getTime());
		orcrpb.setUserObstacleId(ofuId);
		orcrpb.setAtMaxObstacles(true);
		
		//send the event for processing
		ObstacleRemovalCompleteRequestEvent orcre =
				new ObstacleRemovalCompleteRequestEvent();
		orcre.setTag(1);
		orcre.setObstacleRemovalCompleteRequestProto(orcrpb.build());
		getObstacleRemovalCompleteController().handleEvent(orcre);
		
		
		//check to make sure the user has one less obstacle 
		List<ObstacleForUser> newOfuList = getObstacleForUserRetrieveUtil()
				.getUserObstacleForUser(userId);
		int newSize = newOfuList.size();
		int expectedSize = oldSize - 1;
		assertTrue("Expected num obstacles: " + expectedSize + ". Actual: " +
				newSize, expectedSize == newSize);
		
		//check to make sure the user's num obstacles removed is one more
		User newUnitTester = getUserRetrieveUtils().getUserById(userId);
		int newNumObstaclesRemoved = newUnitTester.getNumObstaclesRemoved();
		int expectedRemoved = numObstaclesRemoved + 1;
		assertTrue("Expected obstacles removed: " + expectedRemoved +
				". Actual: " + newNumObstaclesRemoved,
				expectedRemoved == newNumObstaclesRemoved);
		
		//check to make sure the last obstacle spawn time is diff
		Date newLastObstacleSpawnedTime = newUnitTester
				.getLastObstacleSpawnedTime();
		int numMinDiff = getTimeUtils().numMinutesDifference(
				oldLastObstacleSpawnedTime, newLastObstacleSpawnedTime);
		assertTrue("Expected diff obstacle spawn time. Actual times, old: " +
				oldLastObstacleSpawnedTime + ", new: " +
				newLastObstacleSpawnedTime, numMinDiff >= 9 && numMinDiff <= 11);
		
		log.info("ending completing removing obstacle at max");
	}
	
	
	protected int getTestUserId() {
		return 11; //Unit testing account
	}
	
	protected MinimumObstacleProto createTestObstacleProto() {
		int obstacleId = ControllerConstants.TUTORIAL__INIT_OBSTACLE_ID[0];
    	float posX = ControllerConstants.TUTORIAL__INIT_OBSTACLE_X[0];
    	float posY = ControllerConstants.TUTORIAL__INIT_OBSTACLE_Y[0];
    	int orientation = 1;
    	MinimumObstacleProto mop = CreateInfoProtoUtils
    			.createMinimumObstacleProto(obstacleId, posX, posY,
    					orientation);
    	return mop;
	}

	
	
	public SpawnObstacleController getSpawnObstacleController() {
		return spawnObstacleController;
	}

	public void setSpawnObstacleController(
			SpawnObstacleController spawnObstacleController) {
		this.spawnObstacleController = spawnObstacleController;
	}

	public BeginObstacleRemovalController getBeginObstacleRemovalController() {
		return beginObstacleRemovalController;
	}

	public void setBeginObstacleRemovalController(
			BeginObstacleRemovalController beginObstacleRemovalController) {
		this.beginObstacleRemovalController = beginObstacleRemovalController;
	}

	public ObstacleRemovalCompleteController getObstacleRemovalCompleteController() {
		return obstacleRemovalCompleteController;
	}

	public void setObstacleRemovalCompleteController(
			ObstacleRemovalCompleteController obstacleRemovalCompleteController) {
		this.obstacleRemovalCompleteController = obstacleRemovalCompleteController;
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

	public ObstacleForUserRetrieveUtil getObstacleForUserRetrieveUtil() {
		return obstacleForUserRetrieveUtil;
	}

	public void setObstacleForUserRetrieveUtil(
			ObstacleForUserRetrieveUtil obstacleForUserRetrieveUtil) {
		this.obstacleForUserRetrieveUtil = obstacleForUserRetrieveUtil;
	}
	
}
