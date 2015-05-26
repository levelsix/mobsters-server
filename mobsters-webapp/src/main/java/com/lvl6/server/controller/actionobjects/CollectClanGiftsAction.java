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
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ClanGiftRewardsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class CollectClanGiftsAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private UserRetrieveUtils2 userRetrieveUtils;
	private ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;
	private ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	private DeleteUtil deleteUtil;
	private List<ClanGiftForUser> listOfClanGifts;
	private CreateInfoProtoUtils createInfoProtoUtils;

	public CollectClanGiftsAction() {
		super();
		// TODO Auto-generated constructor stub
	}


	public CollectClanGiftsAction(String userId,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			UserRetrieveUtils2 userRetrieveUtils,
			ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils,
			ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtils,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			DeleteUtil deleteUtil, List<ClanGiftForUser> listOfClanGifts,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.userId = userId;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.userRetrieveUtils = userRetrieveUtils;
		this.clanGiftForUserRetrieveUtils = clanGiftForUserRetrieveUtils;
		this.clanGiftRewardsRetrieveUtils = clanGiftRewardsRetrieveUtils;
		this.rewardRetrieveUtil = rewardRetrieveUtils;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.listOfClanGifts = listOfClanGifts;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	private List<ClanGiftRewards> rewardsForClanGift;
	private List<UserClan> clanMembers;
	private User user;
	private Map<Integer, Reward> rewardIdsToRewards;
	private UserRewardProto urp;

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

		rewardIdsToRewards = rewardRetrieveUtil.getRewardIdsToRewards();

		if(rewardIdsToRewards == null || rewardIdsToRewards.isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB() {
		//get all the clan gifts for user
		String reasonForGift = null;
		List<Reward> listOfRewards = new ArrayList<Reward>();
		for(ClanGiftForUser cgfu : listOfClanGifts) {
			reasonForGift = cgfu.getReasonForGift();
			listOfRewards.add(rewardIdsToRewards.get(cgfu.getRewardId()));
		}

		//give the rewards
		AwardRewardAction ara = new AwardRewardAction(userId, user, 0, 0, new Date(),
				reasonForGift, listOfRewards, userRetrieveUtils, itemForUserRetrieveUtil,
				insertUtil, updateUtil, monsterStuffUtils, monsterLevelInfoRetrieveUtils,
				clanGiftRewardsRetrieveUtils, rewardRetrieveUtil,
				userClanRetrieveUtils, createInfoProtoUtils);

		ara.execute();

		urp = createInfoProtoUtils.createUserRewardProto(ara.getNuOrUpdatedItems(),
				ara.getNuOrUpdatedMonsters(), ara.getGemsGained(), ara.getCashGained(),
				ara.getOilGained(), ara.getGachaCreditsGained(), null);

		//delete the rows in clan gifts for user
		boolean success = updateUtil.updateUserClanGiftHasBeenCollected(userId, listOfClanGifts);
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public ClanGiftForUserRetrieveUtils getClanGiftForUserRetrieveUtils() {
		return clanGiftForUserRetrieveUtils;
	}

	public void setClanGiftForUserRetrieveUtils(
			ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils) {
		this.clanGiftForUserRetrieveUtils = clanGiftForUserRetrieveUtils;
	}

	public RewardRetrieveUtils getRewardRetrieveUtils() {
		return rewardRetrieveUtil;
	}

	public void setRewardRetrieveUtils(RewardRetrieveUtils rewardRetrieveUtils) {
		this.rewardRetrieveUtil = rewardRetrieveUtils;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Map<Integer, Reward> getRewardIdsToRewards() {
		return rewardIdsToRewards;
	}

	public void setRewardIdsToRewards(Map<Integer, Reward> rewardIdsToRewards) {
		this.rewardIdsToRewards = rewardIdsToRewards;
	}


	public List<ClanGiftForUser> getListOfClanGifts() {
		return listOfClanGifts;
	}


	public void setListOfClanGifts(List<ClanGiftForUser> listOfClanGifts) {
		this.listOfClanGifts = listOfClanGifts;
	}


	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}


	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}


	public UserRewardProto getUrp() {
		return urp;
	}


	public void setUrp(UserRewardProto urp) {
		this.urp = urp;
	}





}
