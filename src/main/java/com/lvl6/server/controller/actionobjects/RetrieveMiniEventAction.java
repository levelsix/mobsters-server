package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.AchievementForUser;
import com.lvl6.info.MiniEvent;
import com.lvl6.info.MiniEventForPlayerLvl;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.info.MiniEventGoal;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.info.MiniEventLeaderboardReward;
import com.lvl6.info.MiniEventTierReward;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.RetrieveMiniEventStatus;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventGoalForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

public class RetrieveMiniEventAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private Date now;
	private boolean completedClanAchievements;
	protected UserRetrieveUtils2 userRetrieveUtil;
	private AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;
	private MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;
	private MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private DeleteUtil deleteUtil;
	private MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;
	private MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;
	private MiniEventRetrieveUtils miniEventRetrieveUtils;
	private MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;
	private MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils;
	private TimeUtils timeUtil;

	public RetrieveMiniEventAction(
			String userId,
			Date now,
			boolean completedClanAchievements,
			UserRetrieveUtils2 userRetrieveUtil,
			AchievementForUserRetrieveUtil achievementForUserRetrieveUtil,
			MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil,
			MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil,
			InsertUtil insertUtil,
			DeleteUtil deleteUtil,
			MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils,
			MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils,
			MiniEventRetrieveUtils miniEventRetrieveUtils,
			MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils,
			MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils,
			TimeUtils timeUtil) {
		super();
		this.userId = userId;
		this.now = now;
		this.completedClanAchievements = completedClanAchievements;
		this.userRetrieveUtil = userRetrieveUtil;
		this.achievementForUserRetrieveUtil = achievementForUserRetrieveUtil;
		this.miniEventForUserRetrieveUtil = miniEventForUserRetrieveUtil;
		this.miniEventGoalForUserRetrieveUtil = miniEventGoalForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.miniEventGoalRetrieveUtils = miniEventGoalRetrieveUtils;
		this.miniEventForPlayerLvlRetrieveUtils = miniEventForPlayerLvlRetrieveUtils;
		this.miniEventRetrieveUtils = miniEventRetrieveUtils;
		this.miniEventTierRewardRetrieveUtils = miniEventTierRewardRetrieveUtils;
		this.miniEventLeaderboardRewardRetrieveUtils = miniEventLeaderboardRewardRetrieveUtils;
		this.timeUtil = timeUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveMiniEventResource {
	//
	//
	//		public RetrieveMiniEventResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveMiniEventResource execute() {
	//
	//	}

	//derived state
	private User u;
	private MiniEvent curActiveMiniEvent;
	private MiniEventForPlayerLvl lvlEntered;
	private MiniEventForUser mefu;
	private Collection<MiniEventGoalForUser> megfus;
	private boolean replaceExistingUserMiniEvent;
	private Collection<MiniEventTierReward> rewards;
	private Collection<MiniEventGoal> goals;
	private Collection<MiniEventLeaderboardReward> leaderboardRewards;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RetrieveMiniEventStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(RetrieveMiniEventStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		u = userRetrieveUtil.getUserById(userId);
		replaceExistingUserMiniEvent = false;

		if (null == u) {
			log.error("no user with id={}", userId);
			return false;
		}

		completedClanAchievements();
		if (!completedClanAchievements)
		{
			log.info("did not finish clan achievements");
			return true;
		}

		mefu = miniEventForUserRetrieveUtil
				.getSpecificUserMiniEvent(userId);
		if (null == mefu) {
			return true;
		}

		int miniEventId = mefu.getMiniEventId();
		//if the event has not even started yet then don't send it
		MiniEvent me = miniEventRetrieveUtils.getMiniEventById(miniEventId);
		if (null == me || timeUtil.isFirstEarlierThanSecond(now, me.getStartTime()))
		{
			log.warn("user's miniEvent not active. {} \t {}", mefu, me);
			mefu = null;
			return true;
		}

		int userLvl = mefu.getUserLvl();
		boolean valid = retrieveMiniEventRelatedData(miniEventId, me, userLvl);
		if (!valid) {
			//if for whatever reason there is no longer a MiniEventForPlayerLevel
			//treat as if the user does not have a MiniEvent
			log.warn("WTF...missing MiniEvent data. So, invalid mefu={}, me={}",
					mefu, me);
			mefu = null;
			cleanUp();
			return true;
		}

		megfus = miniEventGoalForUserRetrieveUtil.getUserMiniEventGoals(userId);

		//name should be "all eligible rewards collected"
		boolean allRewardsCollected = verifyRewardsCollected();

		if (allRewardsCollected) {
			replaceExistingUserMiniEvent = true;
		}

		return true;
	}

	private void completedClanAchievements()
	{
		//caller says clan achievements are completed
		if (completedClanAchievements)
		{
			return;
		}

		//assume user completed clan achievements
		completedClanAchievements = true;

		//prove that he didn't complete the clan achievements
		Integer[] clanAchievementIds = ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS;
		List<Integer> caIdList = java.util.Arrays.asList(clanAchievementIds);

		Map<Integer, AchievementForUser> achievementIdToUserAchievements = achievementForUserRetrieveUtil
				.getSpecificOrAllAchievementIdToAchievementForUserId(userId,
						caIdList);

		for (int i = 0; i < caIdList.size(); i++) {
			int achievementId = caIdList.get(i);
			if (!achievementIdToUserAchievements.containsKey(achievementId))
			{
				completedClanAchievements = false;
				break;
			}

			AchievementForUser afu = achievementIdToUserAchievements.get(achievementId);
			if (!afu.isRedeemed())
			{
				completedClanAchievements = false;
				break;
			}
		}
	}

	private boolean retrieveMiniEventRelatedData(int meId, MiniEvent me, int userLvl)
	{

		lvlEntered = miniEventForPlayerLvlRetrieveUtils
				.getMiniEventForPlayerLvl(meId, userLvl);
		if (null == lvlEntered) {
			log.warn("miniEvent doesn't have MiniEventForPlayerLvl. miniEvent={}, userLvl={}",
					me, userLvl);
			return false;
		}

		rewards = miniEventTierRewardRetrieveUtils
				.getMiniEventTierReward(lvlEntered.getId());
		if (null == rewards || rewards.isEmpty()) {
			log.error("MiniEventForPlayerLvl has no rewards. MiniEventForPlayerLvl={}",
					lvlEntered);
			return false;
		}

		goals = miniEventGoalRetrieveUtils.getGoalsForMiniEventId(meId);
		if (null == goals || goals.isEmpty()) {
			log.error("MiniEvent has no goals. MiniEvent={}, MiniEventForPlayerLvl={}",
					me, lvlEntered);
			return false;
		}

		leaderboardRewards =
				miniEventLeaderboardRewardRetrieveUtils
				.getRewardsForMiniEventId(meId);
		if (null == leaderboardRewards || leaderboardRewards.isEmpty()) {
//			log.error("MiniEvent has no leaderboardRewards. MiniEvent={}", curActiveMiniEvent);
//			return false;
			log.warn("MiniEvent has no leaderboardRewards. MiniEvent={}, MiniEventForPlayerLvl={}",
					me, lvlEntered);
		}

		return true;
	}

	private boolean verifyRewardsCollected()
	{
		int curPts = calculateCurrentPts(goals);
		int tierOne = lvlEntered.getTierOneMinPts();
		int tierTwo = lvlEntered.getTierTwoMinPts();
		int tierThree = lvlEntered.getTierThreeMinPts();

		if (curPts < tierOne)
		{
			//if user didn't get into TierOne, then user eligible to start new event
			return true;
		} else if (curPts >= tierOne && curPts < tierTwo) {
			log.warn("new event only if rewards1 are redeemed. mefu={}\t lvlEntered={}",
					mefu, lvlEntered);
			return mefu.isTierOneRedeemed();

		} else if (curPts >= tierTwo && curPts < tierThree) {
			log.warn("new event only if rewards2 are redeemed. mefu={}\t lvlEntered={}",
					mefu, lvlEntered);
			return mefu.isTierTwoRedeemed();

		} else if (curPts >= tierThree) {
			log.warn("new event only if rewards3 are redeemed. mefu={}\t lvlEntered={}",
					mefu, lvlEntered);
			return mefu.isTierThreeRedeemed();
		}

		//don't think code will ever reach here...
		log.error("user trying to get a new event when he hasn't redeemed his rewards");
		return false;
	}

	private int calculateCurrentPts(Collection<MiniEventGoal> goals) {

		Map<Integer, MiniEventGoal> goalIdToGoal = new HashMap<Integer, MiniEventGoal>();
		for (MiniEventGoal meg : goals)
		{
			goalIdToGoal.put(meg.getId(), meg);
		}

		int totalPts = 0;
		for (MiniEventGoalForUser megfu : megfus)
		{
			int goalId = megfu.getMiniEventGoalId();
			if (!goalIdToGoal.containsKey(goalId)) {
				log.error("MiniEventGoalForUser {} inconsistent with MiniEventGoals. {}",
						megfu, goals);
				continue;
			}

			MiniEventGoal meg = goalIdToGoal.get(goalId);
			int pts = meg.getPtsReward();

			int amtNeededForPts = meg.getAmt();
			int multiplier = megfu.getProgress() / amtNeededForPts;

			int curPts = pts * multiplier;
			totalPts += curPts;

			log.info("MiniEventGoal, MiniEventGoalForUser, pts. {}, {}, {}",
					new Object[] { meg, megfu, curPts } );
		}

		return totalPts;
	}

	private boolean writeChangesToDB(Builder resBuilder)
	{
		if (!completedClanAchievements)
		{
			log.info("user has not finished clan achievements, not giving mini event");
			return true;
		}

		if (null == mefu) {
			return processNonexistentUserMiniEvent();
		} else {
			log.info("process existing UserMiniEvent:{}\t lvlEntered:{}\t progress:{}",
					new Object[] { mefu, lvlEntered, megfus } );
			return processExistingUserMiniEvent();
		}

	}

	private boolean processNonexistentUserMiniEvent()
	{
		curActiveMiniEvent = miniEventRetrieveUtils.getCurrentlyActiveMiniEvent(now);
		if (null == curActiveMiniEvent) {
			log.info("no currently active MiniEvent");
			return true;
		}

		int meId = curActiveMiniEvent.getId();
		int userLvl = u.getLevel();
		log.info("this is the currently active MiniEvent: {}", curActiveMiniEvent);

		boolean success = retrieveMiniEventRelatedData(meId, curActiveMiniEvent, userLvl);

		if (!success) {
			log.warn("unable to continue processNonexistentUserMiniEvent()");
			curActiveMiniEvent = null;
			return true;
		}

		log.info("processNonexistentUserMiniEvent, newEvent:{}", curActiveMiniEvent);
		success = insertUpdateUserMiniEvent(meId, userLvl);
		if (!success) {
			log.warn("unable to create a new MiniEvent for the user.");
		}

		return success;
	}

	private boolean insertUpdateUserMiniEvent(int meId, int userLvl)
	{
		cleanUp();

		//active MiniEvent going on and user doesn't have one so create one
		MiniEventForUser oldMefu = mefu;
		mefu = generateNewMiniEvent(meId, userLvl);

		log.info("inserting/updating. oldMefu={} \t newMefu={}", oldMefu, mefu);
		return insertUtil.insertIntoUpdateMiniEventForUser(mefu);
	}

	private void cleanUp() {
		//in case the user has remnants from previous MiniEvents
		int numDeleted = deleteUtil.deleteMiniEventGoalForUser(userId);
		log.info("MiniEventGoalForUser numDeleted={}", numDeleted);
	}

	private MiniEventForUser generateNewMiniEvent(int meId, int userLvl) {
		MiniEventForUser mefu = new MiniEventForUser();
		mefu.setUserId(userId);
		mefu.setMiniEventId(meId);
		mefu.setUserLvl(userLvl);
		mefu.setTierOneRedeemed(false);
		mefu.setTierTwoRedeemed(false);
		mefu.setTierThreeRedeemed(false);

		return mefu;
	}

	private boolean processExistingUserMiniEvent()
	{
		//two cases
		if (!replaceExistingUserMiniEvent) {
			return retrieveCurrentUserMiniEvent();
		}

		return replaceCurrentUserMiniEvent();
	}

	private boolean retrieveCurrentUserMiniEvent()
	{
		int meId = mefu.getMiniEventId();

		curActiveMiniEvent = miniEventRetrieveUtils.getMiniEventById(meId);

		if (null == curActiveMiniEvent) {
			//uncommon case because someone would have to delete the MiniEvent
			log.error("MiniEvent no longer exists. {}. Giving user new one",
					curActiveMiniEvent);
			return processNonexistentUserMiniEvent();
		}

		//common case: MiniEvent exists
//		int userLvl = u.getLevel();
		int userLvl = mefu.getUserLvl();
		log.info("user's current MiniEvent: {}", curActiveMiniEvent);

		boolean success = retrieveMiniEventRelatedData(meId, curActiveMiniEvent, userLvl);
		if (!success) {
			log.info("issue retrieving MiniEvent data regarding user's MiniEvent. {}",
					curActiveMiniEvent);
		}

		return success;
	}

	private boolean replaceCurrentUserMiniEvent()
	{
		int meId = mefu.getMiniEventId();
		curActiveMiniEvent = miniEventRetrieveUtils.getCurrentlyActiveMiniEvent(now);

		if (null == curActiveMiniEvent) {
			//NOTE: reaching here means, user has collected all the rewards he can
			//since there is no active MiniEvent, act as if the user doesn't have one
			log.info("client wants new UserMiniEvent; no active MiniEvent");
			mefu = null;
			return true;
		}

		int curActiveMeId = curActiveMiniEvent.getId();
//		int userLvl = u.getLevel();
		int userLvl = mefu.getUserLvl();
		if (meId == curActiveMeId) {
			log.info("not replacing UserMiniEvent since the same MiniEvent is still ongoing");
			return retrieveMiniEventRelatedData(meId, curActiveMiniEvent, userLvl);
		}

		//mini events are different, since user getting new MiniEvent use his cur lvl
		int currUserLvl = u.getLevel();
		boolean success = retrieveMiniEventRelatedData(curActiveMeId, curActiveMiniEvent, currUserLvl);
		if (!success) {
			log.warn("invalid/insufficient MiniEventData: so no MiniEvent. existingMefu={}, curMe={}, usrLvl={}",
					new Object[] { mefu, curActiveMiniEvent, currUserLvl });
			curActiveMiniEvent = null;
			mefu = null;
			return true;
		}

		log.info("replaceCurrentUserMiniEvent. oldId:{}, \t newEvent:{}",
				meId, curActiveMiniEvent);
		success = insertUpdateUserMiniEvent(curActiveMeId, currUserLvl);
		if (!success) {
			log.warn("unable to replace MiniEvent for the user.");
		}

		return success;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public MiniEvent getCurActiveMiniEvent() {
		return curActiveMiniEvent;
	}

	public void setCurActiveMiniEvent(MiniEvent curActiveMiniEvent) {
		this.curActiveMiniEvent = curActiveMiniEvent;
	}

	public MiniEventForPlayerLvl getLvlEntered() {
		return lvlEntered;
	}

	public void setLvlEntered(MiniEventForPlayerLvl lvlEntered) {
		this.lvlEntered = lvlEntered;
	}

	public MiniEventForUser getMefu() {
		return mefu;
	}

	public void setMefu(MiniEventForUser mefu) {
		this.mefu = mefu;
	}

	public Collection<MiniEventGoalForUser> getMegfus() {
		return megfus;
	}

	public void setMegfus(Collection<MiniEventGoalForUser> megfus) {
		this.megfus = megfus;
	}

	public Collection<MiniEventTierReward> getRewards() {
		return rewards;
	}

	public void setRewards(Collection<MiniEventTierReward> rewards) {
		this.rewards = rewards;
	}

	public Collection<MiniEventGoal> getGoals() {
		return goals;
	}

	public void setGoals(Collection<MiniEventGoal> goals) {
		this.goals = goals;
	}

	public Collection<MiniEventLeaderboardReward> getLeaderboardRewards() {
		return leaderboardRewards;
	}

	public void setLeaderboardRewards(
			Collection<MiniEventLeaderboardReward> leaderboardRewards) {
		this.leaderboardRewards = leaderboardRewards;
	}

}
