package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RedeemMiniEventRewardAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private int maxCash;
	private int maxOil;
	private int mefplId;
	private RewardTier rt;
	private Date clientTime;
	private MiniEventForUserRetrieveUtil mefuRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;

	public RedeemMiniEventRewardAction(String userId, int maxCash,
			int maxOil, int mefplId, RewardTier rt, Date clientTime,
			MiniEventForUserRetrieveUtil mefuRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil) {
		super();
		this.userId = userId;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.mefplId = mefplId;
		this.rt = rt;
		this.clientTime = clientTime;
		this.mefuRetrieveUtil = mefuRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
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
		MiniEventForPlayerLvl me = MiniEventForPlayerLvlRetrieveUtils
				.getMiniEventForPlayerLvlById(mefplId);

		if (null == me) {
			log.error("no MiniEventForPlayerLvl with id={}", mefplId);
			return false;
		}

		//check to see there are rewards
		miniEventTierRewards = MiniEventTierRewardRetrieveUtils
				.getMiniEventTierReward(mefplId);

		if (null == miniEventTierRewards || miniEventTierRewards.isEmpty()) {
			log.error("no rewards for MiniEventForPlayerLvl: {}", me);
			return false;
		}

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
			Reward r = RewardRetrieveUtils.getRewardById(rewardId);

			rewards.add(r);

			sb.append(" ");
			sb.append(metr.getId());
		}

		//award the Rewards to the user
		//TODO: Figure out how to only set the necessary arguments
		awardedResources = false;
		ara = new AwardRewardAction(userId, maxCash, maxOil,
				clientTime, sb.toString(), rewards, itemForUserRetrieveUtil,
				insertUtil, updateUtil);

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
}
