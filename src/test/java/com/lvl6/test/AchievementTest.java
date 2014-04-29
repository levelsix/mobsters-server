package com.lvl6.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.events.request.AchievementProgressRequestEvent;
import com.lvl6.info.Achievement;
import com.lvl6.info.AchievementForUser;
import com.lvl6.info.User;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;
import com.lvl6.proto.EventAchievementProto.AchievementProgressRequestProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.server.controller.AchievementProgressController;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-application-context.xml")
public class AchievementTest extends TestCase {
	
	private static Logger log = LoggerFactory.getLogger(new Object() {
  }.getClass().getEnclosingClass());
	
	@Autowired
	protected UserRetrieveUtils userRetrieveUtils;
	
	@Autowired
	protected AchievementForUserRetrieveUtil achievementForUserRetieveUtil;
	
	@Autowired AchievementProgressController achievementProgressController;
	
	protected Random rand = new Random();

	@Test
//	@Rollback(true) //doesn't roll back transaction >:C
//	@Transactional //just manually undo...
	public void sendIncompleteProgressedAchievement() {
		log.info("incomplete achievement progress");
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);

		//get an achievement that has a quantity more than one
		Achievement a = getTestAchievement(true);
		int achievementId = a.getId();
		//achievement's progress is > 1, so it's still incomplete
		int progress = 1;
		boolean isComplete = false;
		boolean isRedeemed = false;
		
		sendAchievementProgressRequestEvent(unitTester, achievementId,
				progress, isComplete, isRedeemed);
		
		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
		//should have an entry for this user and achievement
		Collection<Integer> achievementIds = Collections
				.singleton(achievementId);
		Map<Integer, AchievementForUser> afuMap =
				getAchievementForUserRetieveUtil()
				.getSpecificOrAllAchievementIdToAchievementForUserId(
						userId, achievementIds);
		
		assertTrue("Expected AchievementForUser: one. Actual:" + afuMap,
				null != afuMap && !afuMap.isEmpty() && 1 == afuMap.size());
		
		AchievementForUser afu = afuMap.get(achievementId);
		int actualProgress = afu.getProgress();
		assertTrue("Expected progress: " + progress + ". Actual: " +
				actualProgress, progress == actualProgress);
		
		boolean actualIsComplete = afu.isComplete();
		assertTrue("Expected isComplete: " + isComplete + ". Actual: " +
				actualIsComplete, isComplete == actualIsComplete);
		
		boolean actualIsRedeemed = afu.isRedeemed();
		assertTrue("Expected isRedeemed: " + isRedeemed + ". Actual: " +
				actualIsRedeemed, isRedeemed == actualIsRedeemed);
		//maybe manually delete the created AchievementForUser
		
		deleteAchievements(userId, achievementIds);
	}
	
	@Test
	public void sendMultipleIncompleteProgressedAchievement() {
		log.info("incomplete achievement progress");
		int userId = getTestUserId();
		User unitTester = getUserRetrieveUtils().getUserById(userId);

		int achievementIdOne = 13;
		int progress = 1;
		boolean isComplete = false;
		boolean isRedeemed = false;
		AchievementForUser afuOne = new AchievementForUser();
		afuOne.setAchievementId(achievementIdOne);
		afuOne.setProgress(progress);
		afuOne.setComplete(isComplete);
		afuOne.setRedeemed(isRedeemed);
		
		int achievementIdTwo = 14;
		AchievementForUser afuTwo = new AchievementForUser();
		afuTwo.setAchievementId(achievementIdTwo);
		afuTwo.setProgress(progress);
		afuTwo.setComplete(isComplete);
		afuTwo.setRedeemed(isRedeemed);
		
		int achievementIdThree = 15;
		AchievementForUser afuThree = new AchievementForUser();
		afuThree.setAchievementId(achievementIdThree);
		afuThree.setProgress(progress);
		afuThree.setComplete(isComplete);
		afuThree.setRedeemed(isRedeemed);
		
		List<AchievementForUser> afuList = new ArrayList<AchievementForUser>();
		afuList.add(afuOne);
		afuList.add(afuTwo);
		afuList.add(afuThree);
		
		sendAchievementProgressRequestEvent(unitTester, afuList);
		
		//CHECK DATABASE TO VALIDATE CONTROLLER LOGIC
		//should have an entry for this user and achievement
		Collection<Integer> achievementIds = new ArrayList<Integer>();
		achievementIds.add(achievementIdOne);
		achievementIds.add(achievementIdTwo);
		achievementIds.add(achievementIdThree);
		
		Map<Integer, AchievementForUser> afuMap =
				getAchievementForUserRetieveUtil()
				.getSpecificOrAllAchievementIdToAchievementForUserId(
						userId, achievementIds);
		
		assertTrue("Expected AchievementForUser: three. Actual:" + afuMap,
				null != afuMap && !afuMap.isEmpty() && 3 == afuMap.size());
		
		for (AchievementForUser afuTemp : afuMap.values()) {
			int actualProgress = afuTemp.getProgress();
			assertTrue("Expected progress: " + progress + ". Actual: " +
					actualProgress, progress == actualProgress);

			boolean actualIsComplete = afuTemp.isComplete();
			assertTrue("Expected isComplete: " + isComplete + ". Actual: " +
					actualIsComplete, isComplete == actualIsComplete);

			boolean actualIsRedeemed = afuTemp.isRedeemed();
			assertTrue("Expected isRedeemed: " + isRedeemed + ". Actual: " +
					actualIsRedeemed, isRedeemed == actualIsRedeemed);
		}
		//maybe manually delete the created AchievementForUser
		deleteAchievements(userId, achievementIds);
	}
	
	
	
	protected int getTestUserId() {
		return 11; //Unit testing account
	}
	
	protected Achievement getTestAchievement(boolean multiQuantityAchievement) {
		Achievement a = null;
		
		Map<Integer, Achievement> achievementIdsToAchievements =
				AchievementRetrieveUtils.getAchievementIdsToAchievements();
		for (Achievement aTemp : achievementIdsToAchievements.values()) {
			if (aTemp.getQuantity() > 1 && multiQuantityAchievement) {
				a = aTemp;
				break;
			}
			
			if (aTemp.getQuantity() == 1 && !multiQuantityAchievement) {
				a = aTemp;
				break;
			}
		}
		
		assertTrue("Expected achievement: not null. Actual:" + a,
				null != a);
		return a;
	}
	
	protected void sendAchievementProgressRequestEvent(User user,
			int achievementId, int progress, boolean isComplete,
			boolean isRedeemed) {
		AchievementProgressRequestProto aprp =
				createAchievementProgressRequestProto(user,
						achievementId, progress, isComplete, isRedeemed);
		AchievementProgressRequestEvent apre =
				new AchievementProgressRequestEvent();
		apre.setTag(0);
		apre.setAchievementProgressRequestProto(aprp);
		
		//SENDING THE REQUEST
		getAchievementProgressController().handleEvent(apre);
	}

	protected void sendAchievementProgressRequestEvent(User user,
			List<AchievementForUser> afuList) {
		
		
		AchievementProgressRequestProto aprp =
				createAchievementProgressRequestProto(user, afuList);
		AchievementProgressRequestEvent apre =
				new AchievementProgressRequestEvent();
		apre.setTag(0);
		apre.setAchievementProgressRequestProto(aprp);
		
		//SENDING THE REQUEST
		getAchievementProgressController().handleEvent(apre);
	}
	
	protected AchievementProgressRequestProto createAchievementProgressRequestProto(
			User user, int achievementId, int progress, boolean isComplete,
			boolean isRedeemed) {
		assertTrue("Expected user: not null. Actual: " + user,
				null != user);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(user);
		
		//create UserAchievementProto
		AchievementForUser afu = new AchievementForUser();
		afu.setAchievementId(achievementId);
		afu.setProgress(progress);
		afu.setComplete(isComplete);
		afu.setRedeemed(isRedeemed);
		UserAchievementProto uap = CreateInfoProtoUtils
    			.createUserAchievementProto(afu);
		
		Timestamp clientTime = new Timestamp((new Date()).getTime());
		
		AchievementProgressRequestProto.Builder aprpb =
				AchievementProgressRequestProto.newBuilder();
		aprpb.setSender(mup);
		aprpb.addUapList(uap);
		aprpb.setClientTime(clientTime.getTime());
		
		return aprpb.build();
	}
	
	protected AchievementProgressRequestProto createAchievementProgressRequestProto(
			User user, List<AchievementForUser> afuList) {
		assertTrue("Expected user: not null. Actual: " + user,
				null != user);
		MinimumUserProto mup = CreateInfoProtoUtils
				.createMinimumUserProtoFromUser(user);
		Timestamp clientTime = new Timestamp((new Date()).getTime());
		
		AchievementProgressRequestProto.Builder aprpb =
				AchievementProgressRequestProto.newBuilder();
		aprpb.setSender(mup);
		aprpb.setClientTime(clientTime.getTime());
		
		
		//create UserAchievementProto
		for (AchievementForUser afu : afuList) {
			UserAchievementProto uap = CreateInfoProtoUtils
					.createUserAchievementProto(afu);
			aprpb.addUapList(uap);
		}
		
		return aprpb.build();
	}
	
	protected void deleteAchievements(int userId,
			Collection<Integer> achievementIds) {
		String tableName = DBConstants.TABLE_ACHIEVEMENT_FOR_USER;
		int size = achievementIds.size();
		List<String> questions = Collections.nCopies(size, "?");
	    String questionMarks = StringUtils.csvList(questions);
	    
	    StringBuilder querySb = new StringBuilder();
	    querySb.append("DELETE FROM ");
	    querySb.append(tableName);
	    querySb.append(" WHERE ");
	    querySb.append(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID);
	    querySb.append(" IN (");
	    querySb.append(questionMarks);
	    querySb.append(")");
	    querySb.append(" AND ");
	    querySb.append(DBConstants.ACHIEVEMENT_FOR_USER__USER_ID);
	    querySb.append("=");
	    querySb.append(userId);
	    String query = querySb.toString();
	    
	    log.info("deleteAchievements() query=" + query);
	    List<Integer> achievementIdList = new ArrayList<Integer>(achievementIds);
	    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query,
	    		achievementIdList);
	    log.info("num achievement_for_user deleted=" + numDeleted);
	    
	    assertTrue("Expected rows deleted: " + size + ". Actual: " +
	    		numDeleted, size == numDeleted);
	}

	
	public UserRetrieveUtils getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}
	
	public AchievementForUserRetrieveUtil getAchievementForUserRetieveUtil() {
		return achievementForUserRetieveUtil;
	}

	public void setAchievementForUserRetieveUtil(
			AchievementForUserRetrieveUtil achievementForUserRetieveUtil) {
		this.achievementForUserRetieveUtil = achievementForUserRetieveUtil;
	}

	public AchievementProgressController getAchievementProgressController() {
		return achievementProgressController;
	}

	public void setAchievementProgressController(
			AchievementProgressController achievementProgressController) {
		this.achievementProgressController = achievementProgressController;
	}

}
