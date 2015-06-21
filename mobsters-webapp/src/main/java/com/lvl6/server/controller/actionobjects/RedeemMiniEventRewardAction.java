package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.MiniEventForPlayerLvl;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.info.MiniEventTierReward;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardRequestProto.RewardTier;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto.Builder;
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto.RedeemMiniEventRewardStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.MiniEventForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class RedeemMiniEventRewardAction {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemMiniEventRewardAction.class);

	private String userId;
	private User user;
	private int maxCash;
	private int maxOil;
	private int mefplId;
	private RewardTier rt;
	private Date clientTime;
	@Autowired protected GiftRetrieveUtils giftRetrieveUtil; 
	@Autowired protected GiftRewardRetrieveUtils giftRewardRetrieveUtils; 
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils; 
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	@Autowired protected MiniEventForUserRetrieveUtil mefuRetrieveUtil; 
	@Autowired protected ItemForUserRetrieveUtil itemForUserRetrieveUtil; 
	@Autowired protected InsertUtil insertUtil; 
	@Autowired protected UpdateUtil updateUtil; 
	@Autowired protected MonsterStuffUtils monsterStuffUtils; 
	@Autowired protected MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils; 
	@Autowired protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils; 
	@Autowired protected RewardRetrieveUtils rewardRetrieveUtils; 
	@Autowired protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils; 
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 

	public RedeemMiniEventRewardAction(String userId, User user,
			int maxCash, int maxOil, int mefplId, RewardTier rt,
			Date clientTime, GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			UserRetrieveUtils2 userRetrieveUtil,
			MiniEventForUserRetrieveUtil mefuRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			MonsterStuffUtils monsterStuffUtils,
			MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils,
			MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils)
	{
		super();
		this.userId = userId;
		this.user = user;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.mefplId = mefplId;
		this.rt = rt;
		this.clientTime = clientTime;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.userRetrieveUtil = userRetrieveUtil;
		this.mefuRetrieveUtil = mefuRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.miniEventForPlayerLvlRetrieveUtils = miniEventForPlayerLvlRetrieveUtils;
		this.miniEventTierRewardRetrieveUtils = miniEventTierRewardRetrieveUtils;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RedeemMiniEventRewardResource {
	//
	//
	//		public RedeemMiniEventRewardResource() {
	//
	//		}
	//	}
	//
	//	public RedeemMiniEventRewardResource execute() {
	//
	//	}

	//derived state
	private Collection<MiniEventTierReward> miniEventTierRewards;
	private MiniEventForUser mefu;
	private Collection<ItemForUser> nuOrUpdatedItems;
	private List<FullUserMonsterProto> nuOrUpdatedMonsters;
	private boolean awardedResources;
	private int gemsGained;
	private int cashGained;
	private int oilGained;
	private AwardRewardAction ara;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RedeemMiniEventRewardStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		boolean valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(RedeemMiniEventRewardStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}

	private boolean verifySemantics(Builder resBuilder) {
		//check to make sure the MiniEventForPlayerLvl exists
		MiniEventForPlayerLvl me = miniEventForPlayerLvlRetrieveUtils
				.getMiniEventForPlayerLvlById(mefplId);

		if (null == me) {
			log.error("no MiniEventForPlayerLvl with id={}", mefplId);
			return false;
		}

		//check to see there are rewards
		miniEventTierRewards = miniEventTierRewardRetrieveUtils
				.getMiniEventTierReward(mefplId);
		log.info("all MiniEventTierRewards: {}", miniEventTierRewards);

		if (null == miniEventTierRewards || miniEventTierRewards.isEmpty()) {
			log.error("no rewards for MiniEventForPlayerLvl: {}", me);
			return false;
		}

		//need to filter out the rewards of a certain tier lvl
		int rewardTier = 1;

		if (RewardTier.TIER_ONE.equals(rt)) {
			rewardTier = 1;
		} else if (RewardTier.TIER_TWO.equals(rt)) {
			rewardTier = 2;
		} else {
			rewardTier = 3;
		}

		Collection<MiniEventTierReward> miniEventTierRewardsTemp =
				new ArrayList<MiniEventTierReward>();
		for (MiniEventTierReward metr : miniEventTierRewards)
		{
			if (metr.getRewardTier() == rewardTier)
			{
				miniEventTierRewardsTemp.add(metr);
			}
		}
		miniEventTierRewards = miniEventTierRewardsTemp;

		mefu = mefuRetrieveUtil.getSpecificUserMiniEvent(userId);
		if (null == mefu) {
			log.error("user has no MiniEventForUser, userId={}", userId);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		if (RewardTier.TIER_ONE.equals(rt)) {
			mefu.setTierOneRedeemed(true);
		} else if (RewardTier.TIER_TWO.equals(rt)) {
			mefu.setTierTwoRedeemed(true);
		} else {
			mefu.setTierThreeRedeemed(true);
		}

		boolean success = insertUtil.insertIntoUpdateMiniEventForUser(mefu);
		log.info("successful update: {}. {}", success, mefu);

		Collection<Reward> rewards = new ArrayList<Reward>();
		StringBuilder sb = new StringBuilder();
		sb.append(ControllerConstants.MFUSOP__REDEEM_MINI_EVENT_REWARD);
		for (MiniEventTierReward metr : miniEventTierRewards) {
			int rewardId = metr.getRewardId();
			Reward r = rewardRetrieveUtils.getRewardById(rewardId);

			rewards.add(r);

			sb.append(" ");
			sb.append(metr.getId());
		}

		log.info("MiniEventTierRewards awarded: {}", miniEventTierRewards);

		//award the Rewards to the user
		//TODO: Figure out how to only set the necessary arguments
		awardedResources = false;
		ara = new AwardRewardAction(userId, user, maxCash, maxOil,
				clientTime, sb.toString(), rewards,
				userRetrieveUtil, itemForUserRetrieveUtil,
				insertUtil, updateUtil, monsterStuffUtils,
				monsterLevelInfoRetrieveUtils,
				giftRetrieveUtil,
				giftRewardRetrieveUtils, rewardRetrieveUtils,
				userClanRetrieveUtils, createInfoProtoUtils, null);

		boolean awardedRewards = ara.execute();

		if (awardedRewards) {
			nuOrUpdatedItems = ara.getNuOrUpdatedItems();

			awardedResources = ara.isAwardResources();
			gemsGained = ara.getGemsGained();
			cashGained = ara.getCashGained();
			nuOrUpdatedMonsters = ara.getNuOrUpdatedMonsters();
		}

		return awardedRewards;
	}

	public User getUser() {
		if (null != ara) {
			return ara.getU();
		}
		return null;
	}

	public Collection<ItemForUser> getNuOrUpdatedItems() {
		return nuOrUpdatedItems;
	}

	public void setNuOrUpdatedItems(Collection<ItemForUser> nuOrUpdatedItems) {
		this.nuOrUpdatedItems = nuOrUpdatedItems;
	}


	public boolean isAwardedResources() {
		return awardedResources;
	}

	public int getGemsGained() {
		return gemsGained;
	}

	public int getCashGained() {
		return cashGained;
	}

	public int getOilGained() {
		return oilGained;
	}

	//don't really feel like making private instance variables for these
	public Map<String, Integer> getCurrencyDeltas() {
		if (null != ara) {
			return ara.getCurrencyDeltas();
		}
		return null;
	}

	public List<FullUserMonsterProto> getNuOrUpdatedMonsters() {
		return nuOrUpdatedMonsters;
	}

	public Map<String, Integer> getPreviousCurrencies() {
		if (null != ara) {
			return ara.getPreviousCurrencies();
		}
		return null;
	}

	public Map<String, Integer> getCurrentCurrencies() {
		if (null != ara) {
			return ara.getCurrentCurrencies();
		}
		return null;
	}

	public Map<String, String> getReasons() {
		if (null != ara) {
			return ara.getReasons();
		}
		return null;
	}

	public Map<String, String> getDetails() {
		if (null != ara) {
			return ara.getDetails();
		}
		return null;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public void setAra(AwardRewardAction ara) {
		this.ara = ara;
	}

}
