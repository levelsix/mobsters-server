package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.AchievementForUser;
import com.lvl6.info.MiniEventForPlayerLvl;
import com.lvl6.info.MiniEventGoal;
import com.lvl6.info.MiniEventLeaderboardReward;
import com.lvl6.info.MiniEventTierReward;
import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventGoalForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfigPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.AchievementForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventGoalForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTimetableRetrieveUtils;
import com.lvl6.utils.TimeUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component
@Scope("prototype")
public class RetrieveMiniEventAction {

	private static final Logger log = LoggerFactory.getLogger(RetrieveMiniEventAction.class);

	private String userId;
	private Date now;
	private boolean completedClanAchievements;
	protected UserRetrieveUtils2 userRetrieveUtil;
	@Autowired protected AchievementForUserRetrieveUtil achievementForUserRetrieveUtil;
	@Autowired protected MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;
	@Autowired protected MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil;
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected DeleteUtil deleteUtil;
	@Autowired protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;
	@Autowired protected MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;
	@Autowired protected MiniEventRetrieveUtils miniEventRetrieveUtils;
	@Autowired protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;
	@Autowired protected MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils;
	@Autowired protected MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtil;
	@Autowired protected TimeUtils timeUtil;

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
			MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtil,
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
		this.miniEventTimetableRetrieveUtil = miniEventTimetableRetrieveUtil;
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
	private MiniEventConfigPojo curActiveMiniEvent;
	private MiniEventTimetableConfigPojo curActiveMiniEventTimetable;
	private MiniEventForPlayerLvl lvlEntered;
	private MiniEventForUserPojo mefu;
	private Collection<MiniEventGoalForUserPojo> megfus;
	//if false then just retrieve current MiniEventForUser
	boolean allEligibleRewardsCollected;
//	private boolean addNewUserMiniEvent;
	private Collection<MiniEventTierReward> rewards;
	private Collection<MiniEventGoal> goals;
	private Collection<MiniEventLeaderboardReward> leaderboardRewards;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

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

		resBuilder.setStatus(ResponseStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		u = userRetrieveUtil.getUserById(userId);
//		addNewUserMiniEvent = false;
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
				.getMostRecentUserMiniEvent(userId);
//		log.info("mefu={}", mefu);
		if (null == mefu) {
//			addNewUserMiniEvent = true;
			return true;
		}

		int miniEventTimetableId = mefu.getMiniEventTimetableId();
		//if user's recorded event has not even started yet then don't send it
		MiniEventTimetableConfigPojo meTimetable = miniEventTimetableRetrieveUtil
				.getTimetableForId(miniEventTimetableId);
		if (null == meTimetable ||
				timeUtil.isFirstEarlierThanSecond(now, meTimetable.getStartTime()))
		{
			log.warn("user's miniEvent not active/nonexistent. {}.\t timeActive={}",
					mefu, meTimetable);
			mefu = null;
//			addNewUserMiniEvent = true;
			return true;
		}

		int miniEventId = meTimetable.getMiniEventId();
		//sanity check
		if (mefu.getMiniEventId() != miniEventId) {
			log.error("miniEventId mismatch. mefu={}, MiniEventTimetable={}",
					mefu, meTimetable);
//			addNewUserMiniEvent = true;
		}

		MiniEventConfigPojo me = miniEventRetrieveUtils.getMiniEventById(miniEventId);
		int userLvl = mefu.getUserLvl();
		boolean valid = retrieveMiniEventRelatedData(miniEventId, me, userLvl);
		if (!valid) {
			//if for whatever reason there is no longer a MiniEventForPlayerLevel
			//treat as if the user does not have a MiniEvent
			log.warn("WTF...missing MiniEvent data. So, invalid mefu={}, me={}",
					mefu, me);
			mefu = null;
			//cleanUp();
			return true;
		}

		megfus = miniEventGoalForUserRetrieveUtil.getUserMiniEventGoals(
				userId, mefu.getMiniEventTimetableId());
		allEligibleRewardsCollected = verifyRewardsCollected();

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

	private boolean retrieveMiniEventRelatedData(int meId, MiniEventConfigPojo me, int userLvl)
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
			log.warn("MiniEvent has no leaderboardRewards. {}, {}",
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
			return mefu.getTierOneRedeemed();

		} else if (curPts >= tierTwo && curPts < tierThree) {
			log.warn("new event only if rewards2 are redeemed. mefu={}\t lvlEntered={}",
					mefu, lvlEntered);
			return mefu.getTierTwoRedeemed();

		} else if (curPts >= tierThree) {
			log.warn("new event only if rewards3 are redeemed. mefu={}\t lvlEntered={}",
					mefu, lvlEntered);
			return mefu.getTierThreeRedeemed();
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
		for (MiniEventGoalForUserPojo megfu : megfus)
		{
			int goalId = megfu.getMiniEventGoalId();
			if (!goalIdToGoal.containsKey(goalId)) {
				log.error("MiniEventGoalForUserPojo {} inconsistent with MiniEventGoals. {}",
						megfu, goals);
				continue;
			}

			MiniEventGoal meg = goalIdToGoal.get(goalId);
			int pts = meg.getPtsReward();

			int amtNeededForPts = meg.getAmt();
			int multiplier = megfu.getProgress() / amtNeededForPts;

			int curPts = pts * multiplier;
			totalPts += curPts;

//			log.info("MiniEventGoal, MiniEventGoalForUserPojo, pts. {}, {}, {}",
//					new Object[] { meg, megfu, curPts } );
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
			return addUserMiniEvent();
		} else {
			log.info("process existing {}\t lvlEntered:{}\t progress:{}. allEligibleRewardsCollected={}",
					new Object[] { mefu, lvlEntered, megfus, allEligibleRewardsCollected } );
			return processExistingUserMiniEvent();
//			return retrieveCurrentUserMiniEvent();
		}

	}

	private boolean addUserMiniEvent()
	{
		curActiveMiniEventTimetable = miniEventTimetableRetrieveUtil.getCurrentlyActiveMiniEvent(now);
		if (null == curActiveMiniEventTimetable) {
			log.info("no currently active MiniEvent");
			curActiveMiniEvent = null;
			return true;
		}
		curActiveMiniEvent = miniEventRetrieveUtils.getMiniEventById(
				curActiveMiniEventTimetable.getMiniEventId());

		int meId = curActiveMiniEvent.getId();
		int userLvl = u.getLevel();
		log.info("this is the currently active MiniEvent={}, timetable={}",
				curActiveMiniEvent, curActiveMiniEventTimetable);

		boolean success = retrieveMiniEventRelatedData(meId, curActiveMiniEvent, userLvl);

		if (!success) {
			log.warn("unable to continue addUserMiniEvent()");
			curActiveMiniEvent = null;
			curActiveMiniEventTimetable = null;
			return true;
		}

//		log.info("addUserMiniEvent, newEvent:{}", curActiveMiniEvent);
		int metId = curActiveMiniEventTimetable.getId();
		success = insertUpdateUserMiniEvent(meId, metId, userLvl);
		if (!success) {
			log.warn("unable to create a new MiniEvent for the user.");
		}

		return success;
	}

	private boolean insertUpdateUserMiniEvent(int meId, int metId, int userLvl)
	{
		//cleanUp();

		//active MiniEvent going on and user doesn't have one so create one
		MiniEventForUserPojo oldMefu = mefu;
		mefu = generateNewMiniEvent(meId, metId, userLvl);

		log.info("inserting/updating. oldMefu={} \t newMefu={}", oldMefu, mefu);
		return insertUtil.insertIntoUpdateMiniEventForUser(mefu);
	}

	/*private void cleanUp() {
		//in case the user has remnants from previous MiniEvents
		int numDeleted = deleteUtil.deleteMiniEventGoalForUserPojo(userId);
		log.info("MiniEventGoalForUserPojo numDeleted={}", numDeleted);
	}*/

	private MiniEventForUserPojo generateNewMiniEvent(
			int meId, int metId, int userLvl)
	{
		MiniEventForUserPojo mefu = new MiniEventForUserPojo();
		mefu.setUserId(userId);
		mefu.setMiniEventTimetableId(metId);
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
		if (allEligibleRewardsCollected) {
			return createOrRetrieveCurrentUserMiniEvent();
		}

		log.info("just retrieving UserMiniEvent");
		return retrieveCurrentUserMiniEvent();
	}

	private boolean retrieveCurrentUserMiniEvent()
	{
		int metId = mefu.getMiniEventTimetableId();
		curActiveMiniEventTimetable = miniEventTimetableRetrieveUtil
				.getTimetableForId(metId);
		if (null == curActiveMiniEventTimetable) {
			//uncommon case because someone would have to delete the MiniEventTimetable
			log.error("MiniEventTimetable no longer exists. {}. Giving user new one",
					curActiveMiniEvent);
			return addUserMiniEvent();
		}

		int meId = curActiveMiniEventTimetable.getMiniEventId();
		curActiveMiniEvent = miniEventRetrieveUtils.getMiniEventById(meId);
		if (null == curActiveMiniEvent) {
			//uncommon case because someone would have to delete the MiniEvent
			log.error("MiniEvent no longer exists. {}. Giving user new one",
					curActiveMiniEvent);
			return addUserMiniEvent();
		}

		//common case: MiniEvent exists
//		int userLvl = u.getLevel();
		int userLvl = mefu.getUserLvl();
//		log.debug("user's current MiniEvent: {}", curActiveMiniEvent);

		boolean success = retrieveMiniEventRelatedData(meId, curActiveMiniEvent, userLvl);
		if (!success) {
			log.warn("issue retrieving MiniEvent data regarding user's MiniEvent. {}",
					curActiveMiniEvent);
		}

		return success;
	}


	private boolean createOrRetrieveCurrentUserMiniEvent()
	{
		curActiveMiniEventTimetable = miniEventTimetableRetrieveUtil.getCurrentlyActiveMiniEvent(now);

		if (null == curActiveMiniEventTimetable) {
			//NOTE: reaching here means, user has collected all the rewards he can
			//since there is no active MiniEvent, act as if the user doesn't have one
			log.info("client wants new UserMiniEvent; no active MiniEvent");
			mefu = null;
			curActiveMiniEvent = null;
			curActiveMiniEventTimetable = null;
			return true;
		}
		int metId = mefu.getMiniEventTimetableId();
		int curActiveMetId = curActiveMiniEventTimetable.getId();
		int userLvl = mefu.getUserLvl();

		int curActiveMeId = curActiveMiniEventTimetable.getMiniEventId();
		curActiveMiniEvent = miniEventRetrieveUtils.getMiniEventById(curActiveMeId);
		if (mefu.getMiniEventId() != curActiveMeId) {
			log.error("existing MiniEventForUser/MiniEvent inconsistent. {} {} {}",
					new Object[] {mefu, curActiveMiniEvent, curActiveMiniEventTimetable} );
			mefu = null;
			curActiveMiniEvent = null;
			curActiveMiniEventTimetable = null;
			return true;
		}

		if (metId == curActiveMetId) {
			log.info("not replacing UserMiniEvent since the same MiniEventTimetable is still ongoing");
			return retrieveMiniEventRelatedData(curActiveMeId, curActiveMiniEvent, userLvl);
		}

		//mini events are different, since user getting new MiniEvent use his cur lvl
		int currUserLvl = u.getLevel();
		boolean success = retrieveMiniEventRelatedData(curActiveMeId, curActiveMiniEvent, currUserLvl);
		if (!success) {
			log.warn("invalid/insufficient MiniEventData: so no MiniEvent. existingMefu={}, curMe={}, usrLvl={}",
					new Object[] { mefu, curActiveMiniEvent, currUserLvl });
			mefu = null;
			curActiveMiniEvent = null;
			curActiveMiniEventTimetable = null;
			return true;
		}

		success = insertUpdateUserMiniEvent(curActiveMeId, curActiveMetId, currUserLvl);
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

	public MiniEventConfigPojo getCurActiveMiniEvent() {
		return curActiveMiniEvent;
	}

	public MiniEventTimetableConfigPojo getCurActiveMiniEventTimetable() {
		return curActiveMiniEventTimetable;
	}

	public MiniEventForPlayerLvl getLvlEntered() {
		return lvlEntered;
	}

	public MiniEventForUserPojo getMefu() {
		return mefu;
	}

	public Collection<MiniEventGoalForUserPojo> getMegfus() {
		return megfus;
	}

	public Collection<MiniEventTierReward> getRewards() {
		return rewards;
	}

	public Collection<MiniEventGoal> getGoals() {
		return goals;
	}

	public Collection<MiniEventLeaderboardReward> getLeaderboardRewards() {
		return leaderboardRewards;
	}

}
