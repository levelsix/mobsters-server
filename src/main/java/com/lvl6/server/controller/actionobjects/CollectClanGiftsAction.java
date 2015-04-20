package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanGiftForUser;
import com.lvl6.info.ClanGiftRewards;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto.Builder;
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto.CollectClanGiftsStatus;
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class CollectClanGiftsAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private UserRetrieveUtils2 userRetrieveUtils;
	private ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtils;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	private DeleteUtil deleteUtil;

	public CollectClanGiftsAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CollectClanGiftsAction(String userId, UserRetrieveUtils2 userRetrieveUtils,
			ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtils,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			InsertUtil insertUtil, UpdateUtil updateUtil, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.userRetrieveUtils = userRetrieveUtils;
		this.clanGiftForUserRetrieveUtils = clanGiftForUserRetrieveUtils;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
	}

	private List<ClanGiftRewards> rewardsForClanGift;
	private List<UserClan> clanMembers;
	private User user;
	private Map<Integer, Reward> rewardIdsToRewards;

	public void execute(Builder resBuilder) {

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
		resBuilder.setStatus(CollectClanGiftsStatus.FAIL_OTHER);

		boolean valid = verifySemantics();

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB();
		if (!success) {
			return;
		}
		resBuilder.setStatus(CollectClanGiftsStatus.SUCCESS);

		return;
	}

	private boolean verifySemantics() {
		user = userRetrieveUtils.getUserById(userId);

		if(user == null) {
			return false;
		}

		rewardIdsToRewards = rewardRetrieveUtils.getRewardIdsToRewards();

		if(rewardIdsToRewards == null || rewardIdsToRewards.isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB() {
		//get all the clan gifts for user
		List<ClanGiftForUser> listOfGifts = clanGiftForUserRetrieveUtils.
				getUserClanGiftsForUser(userId);
		String reasonForGift;
		List<Reward> listOfRewards = new ArrayList<Reward>();
		for(ClanGiftForUser cgfu : listOfGifts) {
			reasonForGift = cgfu.getReasonForGift();
			listOfRewards.add(rewardIdsToRewards.get(cgfu.getRewardId()));
		}

		//give the rewards
		AwardRewardAction ara = new AwardRewardAction(userId, user, 0, 0, new Date(),
				"clan gift", listOfRewards, userRetrieveUtils, itemForUserRetrieveUtil,
				insertUtil, updateUtil, monsterStuffUtils, monsterLevelInfoRetrieveUtils);

		ara.execute();

		//delete the rows in clan gifts for user
		boolean success = deleteUtil.deleteFromClanGiftForUser(listOfGifts);
		return success;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		CollectClanGiftsAction.log = log;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public List<ClanGiftRewards> getRewardsForClanGift() {
		return rewardsForClanGift;
	}

	public void setRewardsForClanGift(List<ClanGiftRewards> rewardsForClanGift) {
		this.rewardsForClanGift = rewardsForClanGift;
	}

	public List<UserClan> getClanMembers() {
		return clanMembers;
	}

	public void setClanMembers(List<UserClan> clanMembers) {
		this.clanMembers = clanMembers;
	}





}
