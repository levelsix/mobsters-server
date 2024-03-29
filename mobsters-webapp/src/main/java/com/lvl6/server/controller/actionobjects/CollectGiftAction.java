package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventRewardProto.CollectGiftResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.retrieveutils.GiftForUserRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class CollectGiftAction {
	private static Logger log = LoggerFactory.getLogger( CollectGiftAction.class);

	private String userId;
	private int maxCash;
	private int maxOil;
	private List<String> ugIds;
	private Date clientTime;
	@Autowired protected GiftForUserRetrieveUtils giftForUserRetrieveUtil;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil;
	@Autowired protected RewardRetrieveUtils rewardRetrieveUtil;
	@Autowired protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected UpdateUtil updateUtil;
	@Autowired protected MonsterStuffUtils monsterStuffUtil;
	@Autowired protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	@Autowired protected GiftRetrieveUtils giftRetrieveUtil;
	@Autowired protected GiftRewardRetrieveUtils giftRewardRetrieveUtils;
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils;

	public CollectGiftAction(String userId, int maxCash, int maxOil,
			List<String> ugIds, Date clientTime,
			GiftForUserRetrieveUtils giftForUserRetrieveUtil,
			UserRetrieveUtils2 userRetrieveUtil,
			RewardRetrieveUtils rewardRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			MonsterStuffUtils monsterStuffUtil,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.userId = userId;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.ugIds = ugIds;
		this.clientTime = clientTime;
		this.giftForUserRetrieveUtil = giftForUserRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.rewardRetrieveUtil = rewardRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.monsterStuffUtil = monsterStuffUtil;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class CollectGiftResource {
	//
	//
	//		public CollectGiftResource() {
	//
	//		}
	//	}
	//
	//	public CollectGiftResource execute() {
	//
	//	}

	//derived state
	private User user;
	private Map<String, GiftForUserPojo> gfuIdToGiftForUserPojo;
	private List<Reward> rewards;
	private AwardRewardAction ara;
	private UserRewardProto urp;

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
		if (null == ugIds || ugIds.isEmpty()) {
			log.error("client did not send ");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error("no user with id={}", userId);
			return false;
		}

		//get the secret gifts to redeem, check to see if they exist
		gfuIdToGiftForUserPojo = giftForUserRetrieveUtil
				.getUncollectedUserGiftsForUserMap(ugIds);
		if (ugIds.isEmpty()) {
			log.error("no GiftForUserPojo in db with ids={}", ugIds);
			return false;
		}

		if ( ugIds.size() != gfuIdToGiftForUserPojo.size()) {
			log.warn("inconsistent itemSecretGiftForUserPojo in db: %s & what client sent: %s",
					ugIds, gfuIdToGiftForUserPojo);
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		//		prepCurrencyHistory();
		calculateRewards();
		//maybe figure out a way to tie each reward with its own reason.
		String awardReason = ControllerConstants.REWARD_REASON__COLLECT_GIFT;
		ara = new AwardRewardAction(userId, user, maxCash, maxOil, clientTime,
				awardReason, rewards, userRetrieveUtil, itemForUserRetrieveUtil,
				insertUtil, updateUtil, monsterStuffUtil, monsterLevelInfoRetrieveUtils,
				giftRetrieveUtil,
				giftRewardRetrieveUtils, rewardRetrieveUtil, userClanRetrieveUtils,
				createInfoProtoUtils, null);

		boolean awardedRewards = ara.execute();

		if (awardedRewards) {
			urp = createInfoProtoUtils.createUserRewardProto(ara.getNuOrUpdatedItems(),
					ara.getNuOrUpdatedMonsters(), ara.getGemsGained(), ara.getCashGained(),
					ara.getOilGained(), ara.getGachaCreditsGained(), null);
		} else {
			log.error("unable to award rewards! {}", gfuIdToGiftForUserPojo);
			return false;
		}

		boolean success = updateUtil.updateUserGiftHasBeenCollected(
				userId, gfuIdToGiftForUserPojo.values());
		return success;
	}

	private void calculateRewards() {
		rewards = new ArrayList<Reward>();

		for (GiftForUserPojo gfu : gfuIdToGiftForUserPojo.values()) {
			int rewardId = gfu.getRewardId();
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			rewards.add(r);
		}
	}

	public User getUser() {
		return user;
	}

	public UserRewardProto getUrp() {
		return urp;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

}
