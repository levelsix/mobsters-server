package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SecretGiftForUserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto.Builder;
import com.lvl6.proto.EventRewardProto.RedeemSecretGiftResponseProto.RedeemSecretGiftStatus;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.ItemSecretGiftForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.SecretGiftUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class RedeemSecretGiftAction {

	private static final Logger log = LoggerFactory
			.getLogger(RedeemSecretGiftAction.class);

	private String userId;
	private List<String> rewardIdsRedeemed;
	private Timestamp clientTime;
	private ItemSecretGiftForUserRetrieveUtil secretGiftForUserRetrieveUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private SecretGiftUtils secretGiftUtils;
	private DeleteUtil deleteUtil;
	private UpdateUtil updateUtil;
	private InsertUtil insertUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private GiftRetrieveUtils giftRetrieveUtil;
	private GiftRewardRetrieveUtils giftRewardRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtils;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private UserClanRetrieveUtils2 userClanRetrieveUtils;

	public RedeemSecretGiftAction(
			String userId,
			List<String> itemIdsRedeemed,
			Timestamp clientTime,
			ItemSecretGiftForUserRetrieveUtil secretGiftForUserRetrieveUtil,
			UserRetrieveUtils2 userRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			SecretGiftUtils secretGiftUtils,
			DeleteUtil deleteUtil, UpdateUtil updateUtil, InsertUtil insertUtil) {
		super();
		this.userId = userId;
		this.rewardIdsRedeemed = itemIdsRedeemed;
		this.clientTime = clientTime;
		this.secretGiftForUserRetrieveUtil = secretGiftForUserRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.secretGiftUtils = secretGiftUtils;
		this.deleteUtil = deleteUtil;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RedeemSecretGiftResource {
	//
	//
	//		public RedeemSecretGiftResource() {
	//
	//		}
	//	}
	//
	//	public RedeemSecretGiftResource execute() {
	//
	//	}

	//derived state
	private User user;
	private Map<String, SecretGiftForUserPojo> idToSecretGift;

	private List<Reward> listOfRewards;
	private AwardRewardAction ara;
	private UserRewardProto urp;
	private List<SecretGiftForUserPojo> gifts;

	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;

	//select items at random to gift to the user


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RedeemSecretGiftStatus.FAIL_OTHER);

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

		resBuilder.setStatus(RedeemSecretGiftStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (null == rewardIdsRedeemed || rewardIdsRedeemed.isEmpty()) {
			log.error("invalid itemIdsRedeemed={}.", rewardIdsRedeemed);
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		//get the secret gifts to redeem, check to see if they exist
		idToSecretGift = secretGiftForUserRetrieveUtil
				.getSpecificOrAllSecretGiftForUserMap(userId, rewardIdsRedeemed);

		if (null == idToSecretGift
				|| idToSecretGift.size() != rewardIdsRedeemed.size()) {
			log.info("inconsistent itemSecretGiftForUserPojo in db: {} and what client asked: {}",
					idToSecretGift, rewardIdsRedeemed);
			return false;
		}

		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error("no user with id={}", userId);
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		//		prevCurrencies = new HashMap<String, Integer>();
		//
		//		if (0 != gemsGained) {
		//			prevCurrencies.put(MiscMethods.gems, user.getGems());
		//		}
		//		if (0 != cashGained) {
		//			prevCurrencies.put(MiscMethods.cash, user.getCash());
		//		}
		//		if (0 != oilGained) {
		//			prevCurrencies.put(MiscMethods.oil, user.getOil());
		//		}
		//

		//delete the SecretGifts
		deleteUtil.deleteSecretGifts(userId, rewardIdsRedeemed);

		//update the user saying he got the gifts
		user.updateLastSecretGiftCollectTime(clientTime);

		//create new SecretGifts
		gifts = secretGiftUtils.calculateGiftsForUser(userId,
				ControllerConstants.SECRET_GIFT_FOR_USER__NUM_NEW_GIFTS,
				clientTime.getTime());

		List<String> ids = null;
		if (null != gifts && !gifts.isEmpty()) {
			//save new SecretGifts
			ids = insertUtil.insertIntoSecretGiftForUserGetId(gifts);
		}

		if (null != ids && ids.size() == gifts.size()) {
			setGiftIds(ids);
		} else {
			log.error("Error calculating the new SecretGifts. nuGifts={}, ids={}",
					gifts, ids);
		}

		//award the SecretGifts to the user (aggregate by rewardId)
		aggregateGifts();
		ara = new AwardRewardAction(userId, user, user.getCash(), user.getOil(),
				clientTime,
				ControllerConstants.REWARD_REASON__SECRET_GIFT,
				listOfRewards, userRetrieveUtil, itemForUserRetrieveUtil,
				insertUtil, updateUtil,
				monsterStuffUtils, monsterLevelInfoRetrieveUtils,
				giftRetrieveUtil,
				giftRewardRetrieveUtils, rewardRetrieveUtils,
				userClanRetrieveUtils, createInfoProtoUtils, "");
		boolean awardedRewards = ara.execute();
		if (awardedRewards) {
			List<UserGiftProto> gifts = null;
			if (null != ara.getClanGift()) {
				gifts = ara.getClanGift().getUserGiftsList();
			}

			urp = createInfoProtoUtils.createUserRewardProto(ara.getNuOrUpdatedItems(),
					ara.getNuOrUpdatedMonsters(), ara.getGemsGained(), ara.getCashGained(),
					ara.getOilGained(), ara.getGachaCreditsGained(), gifts);
		} else {
			log.error("unable to award rewards! {}", listOfRewards);
			return false;
		}


		//		prepCurrencyHistory();

		return true;
	}

	private void aggregateGifts() {
		listOfRewards = new ArrayList<Reward>();

		for (SecretGiftForUserPojo gif : idToSecretGift.values()) {
			int rewardId = gif.getRewardId();
			Reward r = rewardRetrieveUtils.getRewardById(rewardId);
			listOfRewards.add(r);
		}
	}

	private void setGiftIds(List<String> ids) {
		for (int index = 0; index < ids.size(); index++) {
			String id = ids.get(index);
			SecretGiftForUserPojo isgfu = gifts.get(index);

			isgfu.setId(id);
		}
	}

	//	private void prepCurrencyHistory()
	//	{
	//		String gems = MiscMethods.gems;
	//		String cash = MiscMethods.cash;
	//		String oil = MiscMethods.oil;
	//
	//		currencyDeltas = new HashMap<String, Integer>();
	//		curCurrencies = new HashMap<String, Integer>();
	//		reasonsForChanges = new HashMap<String, String>();
	//		if (0 != gemsGained) {
	//			currencyDeltas.put(gems, gemsGained);
	//			curCurrencies.put(gems, user.getGems());
	//			reasonsForChanges.put(gems,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != cashGained) {
	//			currencyDeltas.put(cash, cashGained);
	//			curCurrencies.put(cash, user.getCash());
	//			reasonsForChanges.put(cash,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != oilGained) {
	//			currencyDeltas.put(oil, oilGained);
	//			curCurrencies.put(oil, user.getOil());
	//			reasonsForChanges.put(oil,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//
	//		details = new HashMap<String, String>();
	//		for (Integer key : itemIdToResourceToQuantities.keySet()) {
	//			String value = itemIdToResourceToQuantities.get(key).toString();
	//
	//			details.put(key.toString(), value);
	//		}
	//	}

	public User getUser() {
		return user;
	}

	public UserRewardProto getUrp() {
		return urp;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public List<SecretGiftForUserPojo> getGifts() {
		return gifts;
	}

	//	public Map<String, Integer> getCurrencyDeltas() {
	//		return currencyDeltas;
	//	}
	//
	//	public Map<String, Integer> getPreviousCurrencies() {
	//		return prevCurrencies;
	//	}
	//
	//	public Map<String, Integer> getCurrentCurrencies() {
	//		return curCurrencies;
	//	}
	//
	//	public Map<String, String> getReasons() {
	//		return reasonsForChanges;
	//	}
	//
	//	public Map<String, String> getDetails() {
	//		return details;
	//	}
}
