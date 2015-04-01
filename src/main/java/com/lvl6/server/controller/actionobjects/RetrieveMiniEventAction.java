package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.MiniEvent;
import com.lvl6.info.MiniEventForPlayerLvl;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.info.MiniEventGoal;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.info.MiniEventLeaderboardReward;
import com.lvl6.info.MiniEventTierReward;
import com.lvl6.info.User;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto.RetrieveMiniEventStatus;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventGoalForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

public class RetrieveMiniEventAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private Date now;
	protected UserRetrieveUtils2 userRetrieveUtil;
	private MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil;
	private MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private DeleteUtil deleteUtil;

	public RetrieveMiniEventAction(String userId, Date now,
			UserRetrieveUtils2 userRetrieveUtil,
			MiniEventForUserRetrieveUtil miniEventForUserRetrieveUtil,
			MiniEventGoalForUserRetrieveUtil miniEventGoalForUserRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.now = now;
		this.userRetrieveUtil = userRetrieveUtil;
		this.miniEventForUserRetrieveUtil = miniEventForUserRetrieveUtil;
		this.miniEventGoalForUserRetrieveUtil = miniEventGoalForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
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
	private boolean allRewardsCollected;
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

		if (null == u) {
			log.error("no user with id={}", userId);
			return false;
		}


		mefu = miniEventForUserRetrieveUtil
				.getSpecificUserMiniEvent(userId);
		//make sure that the user has redeemed eligible rewards
		if (null == mefu) {
			return true;
		}

		int miniEventId = mefu.getMiniEventId();
		Collection<MiniEventGoal> goals = MiniEventGoalRetrieveUtils
				.getGoalsForMiniEventId(miniEventId);
		if (null == goals || goals.isEmpty()) {
			log.error("MiniEvent with id has no goals. UserMiniEvent={}", mefu);
			return false;
		}

		megfus = miniEventGoalForUserRetrieveUtil.getUserMiniEventGoals(userId);

		allRewardsCollected = verifyRewardsCollected(goals);
		return allRewardsCollected;
	}

	private boolean verifyRewardsCollected(Collection<MiniEventGoal> goals) {
		int miniEventId = mefu.getMiniEventId();
		int curLvl = u.getLevel();

		MiniEventForPlayerLvl mefpl = MiniEventForPlayerLvlRetrieveUtils
				.getMiniEventForPlayerLvl(miniEventId, curLvl);

		int curPts = calculateCurrentPts(goals);
		int tierOne = mefpl.getTierOneMinPts();
		int tierTwo = mefpl.getTierTwoMinPts();
		int tierThree = mefpl.getTierThreeMinPts();

		if (curPts < tierOne)
		{
			//if user didn't get into TierOne, then user eligible to start new event
			return true;
		} else if (curPts >= tierOne && curPts < tierTwo) {
			return mefu.isTierOneRedeemed();

		} else if (curPts >= tierTwo && curPts < tierThree) {
			return mefu.isTierTwoRedeemed();

		} else if (curPts >= tierThree) {
			return mefu.isTierThreeRedeemed();
		}

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
				log.error("MiniEventGoalForUser with nonexisting MiniEventGoal. {}", megfu);
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
		//NOTE: user has redeemed all his rewards for this MiniEvent

//		curActiveMiniEvent = null;
//		if (null == mefu) {
		//don't care if the user has an existing MiniEvent
		curActiveMiniEvent = MiniEventRetrieveUtils.getCurrentlyActiveMiniEvent(now);
//		} else {
//			curActiveMiniEvent = MiniEventRetrieveUtils.getMiniEventById(mefu.getMiniEventId());
//		}

		log.info("this is the currently active mini event: {}", curActiveMiniEvent);

		if (null == curActiveMiniEvent) {
			log.info("no currently active MiniEvent");
			return true;
		}

		int meId = curActiveMiniEvent.getId();
		int userLvl = u.getLevel();
		lvlEntered = MiniEventForPlayerLvlRetrieveUtils
				.getMiniEventForPlayerLvl(meId, userLvl);

		if (null == lvlEntered) {
			log.error("miniEvent doesn't have MiniEventForPlayerLvl. miniEvent={}",
					 curActiveMiniEvent);
			return false;
		}

		rewards = MiniEventTierRewardRetrieveUtils
				.getMiniEventTierReward(lvlEntered.getId());
		if (null == rewards || rewards.isEmpty()) {
			log.error("MiniEventForPlayerLvl has no rewards. MiniEventForPlayerLvl={}",
					lvlEntered);
			return false;
		}

		goals = MiniEventGoalRetrieveUtils.getGoalsForMiniEventId(meId);
		if (null == goals || goals.isEmpty()) {
			log.error("MiniEvent has no goals. MiniEvent={}", curActiveMiniEvent);
			return false;
		}

		leaderboardRewards =
				MiniEventLeaderboardRewardRetrieveUtils
				.getRewardsForMiniEventId(meId);
		if (null == leaderboardRewards || leaderboardRewards.isEmpty()) {
//			log.error("MiniEvent has no leaderboardRewards. MiniEvent={}", curActiveMiniEvent);
//			return false;
			log.warn("MiniEvent has no leaderboardRewards. MiniEvent={}", curActiveMiniEvent);
		}

		boolean success = null != mefu;

		boolean createOrUpdate = null == mefu || meId != mefu.getMiniEventId();
		if ( createOrUpdate ) {
			mefu = new MiniEventForUser();
			mefu.setUserId(userId);
			mefu.setMiniEventId(meId);
			mefu.setUserLvl(userLvl);
			mefu.setTierOneRedeemed(false);
			mefu.setTierTwoRedeemed(false);
			mefu.setTierThreeRedeemed(false);
			success = insertUtil.insertIntoUpdateMiniEventForUser(mefu);
		}

		//only execute if user is updating, not if inserting
		if (success && null != mefu && meId != mefu.getMiniEventId()) {
			//user redeemed all his rewards, and there is a different MiniEventId
			//from the one the user has. delete his progress for the other goals
			int numDeleted = deleteUtil.deleteMiniEventGoalForUser(userId);
			log.info("MiniEventGoalForUser numDeleted={}", numDeleted);
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
