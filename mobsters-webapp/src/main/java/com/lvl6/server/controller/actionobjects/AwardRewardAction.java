package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfigPojo;
import com.lvl6.proto.EventRewardProto.ReceivedGiftResponseProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.RewardsProto.GiftProto.GiftType;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
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

public class AwardRewardAction {

	private static final Logger log = LoggerFactory
			.getLogger(AwardRewardAction.class);

	private String userId;
	private User u;
	private int maxCash;
	private int maxOil;
	private Date now;
	private String awardReason;
	private Collection<Reward> rewards;
	private UserRetrieveUtils2 userRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private GiftRetrieveUtils giftRetrieveUtil;
	private GiftRewardRetrieveUtils giftRewardRetrieveUtils;
	private RewardRetrieveUtils rewardRetrieveUtil; //only here because of recursive rewards
	private UserClanRetrieveUtils2 userClanRetrieveUtils;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private String awardReasonDetail;

	//TODO: Figure out a way to not have all these arguments as a requirement
	public AwardRewardAction(String userId, User u, int maxCash,
			int maxOil, Date now, String awardReason,
			Collection<Reward> rewards,
			UserRetrieveUtils2 userRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			MonsterStuffUtils monsterStuffUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
			RewardRetrieveUtils rewardRetrieveUtil,
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils,
			String awardReasonDetail) {
		super();
		this.userId = userId;
		this.u = u;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.now = now;
		this.awardReason = awardReason;
		this.rewards = rewards;
		this.userRetrieveUtil = userRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
		this.rewardRetrieveUtil = rewardRetrieveUtil;
		this.userClanRetrieveUtils = userClanRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.awardReasonDetail = awardReasonDetail;
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
	private List<ItemForUser> nuOrUpdatedItems;
	private List<FullUserMonsterProto> nuOrUpdatedMonsters;
	private boolean awardResources = false;
	private int gemsGained = 0;
	private int cashGained = 0;
	private int oilGained = 0;
	private int gachaCreditsGained = 0;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;
	private boolean existsClanGift;
	private AwardClanGiftsAction acga;

	public boolean execute() {

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

//		boolean valid = verifySemantics();

//		if (!valid) {
//			return false;
//		}

		boolean success = writeChangesToDB();
		if (!success) {
			return false;
		}

		return true;
	}

//	private boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}

//	private boolean verifySemantics() {
//
//		if (null != u && null != userRetrieveUtil) {
//			u = userRetrieveUtil.getUserById(userId);
//		}
//
//		if (null == u) {
//			log.error("no user with id={}", userId);
//			return false;
//		}
//
//		return true;
//	}

	protected int gemsGainedTemp = 0;
	protected int cashGainedTemp = 0;
	protected int oilGainedTemp = 0;
	protected int gachaCreditsGainedTemp = 0;
	private boolean writeChangesToDB() {

		//aggregate like Rewards
		//NOTE: different Reward objects can contain the same values
		Map<Integer, Integer> monsterIdToQuantity = new HashMap<Integer, Integer>();
		Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity =
				new HashMap<Integer, Map<Integer, Integer>>();
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();

		Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt =
				new HashMap<String, Map<Integer,Integer>>();
		for (Reward r : rewards) {
			int id = r.getId();
			String type = r.getType();
			int staticDataId = r.getStaticDataId();
			int amt = r.getAmt();  //amount means nothing for clan gifts

			if (RewardType.REWARD.name().equals(type)) {
				log.info("recursive reward: old={}", r);

				Reward actualReward = rewardRetrieveUtil.getRewardById(staticDataId);
				int actualRewardId = actualReward.getId();
				String actualType = actualReward.getType();
				int actualRewardStaticDataId = actualReward.getStaticDataId();
				int actualRewardAmt = actualReward.getAmt();
				for (int i = 0; i < amt; i++) {
					processReward(monsterIdToQuantity, monsterIdToLvlToQuantity,
							itemIdToQuantity, resourceTypeToRewardIdToAmt, actualReward,
							actualRewardId, actualType, actualRewardStaticDataId,
							actualRewardAmt);
				}
			} else {
				processReward(monsterIdToQuantity, monsterIdToLvlToQuantity,
						itemIdToQuantity, resourceTypeToRewardIdToAmt, r, id, type,
						staticDataId, amt);
			}
		}

		boolean success = awardItems(itemIdToQuantity);
		if (!success) {
			return false;
		}
		success = awardCurrency(gemsGainedTemp, cashGainedTemp, oilGainedTemp,
				gachaCreditsGainedTemp, resourceTypeToRewardIdToAmt);
		if (!success) {
			return false;
		}
		success = awardMonsters(monsterIdToQuantity, monsterIdToLvlToQuantity);
		if(!success) {
			log.error("error awarding monsters for userId {}", userId);
		}

		//save to reward history
		success = insertUtil.insertIntoUserRewardHistory(userId, new Timestamp(now.getTime()),
				rewards, awardReason, awardReasonDetail);
		if(!success) {
			log.error("error saving to user reward history for userId {}", userId);
		}

		return true;
	}

	private void processReward(Map<Integer, Integer> monsterIdToQuantity,
			Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity,
			Map<Integer, Integer> itemIdToQuantity,
			Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt,
			Reward r, int id, String type, int staticDataId, int amt)
	{
		if(RewardType.GIFT.name().equals(type)) {
			GiftConfigPojo gcp = giftRetrieveUtil.getGift(staticDataId);
			if (null == gcp) {
				log.error("invalid reward={}. No associated gift.", r);
				return;
			}

			if (GiftType.CLAN_GIFT.name().equals(gcp.getGiftType())) {
				//NOTE: Only awards one clan gift at the moment
				//If ever to give out multiplie clan gifts, the clan gifts should be aggregated
				acga = new AwardClanGiftsAction(userId, u, staticDataId, "clan gift",
						giftRetrieveUtil, giftRewardRetrieveUtils,
						rewardRetrieveUtil, userClanRetrieveUtils,
						insertUtil, createInfoProtoUtils);
				existsClanGift = true;
			} else {
				log.warn("no implementation for awarding gift {}", gcp);
				return;
			}

		} else if (RewardType.ITEM.name().equals(type)) {
			aggregateItems(itemIdToQuantity, staticDataId, amt);

		} else if (RewardType.GEMS.name().equals(type)) {
			gemsGainedTemp += amt;

		} else if (RewardType.CASH.name().equals(type)) {
			cashGainedTemp += amt;

		} else if (RewardType.OIL.name().equals(type)) {
			oilGainedTemp += amt;

		} else if (RewardType.GACHA_CREDITS.name().equals(type)) {
			gachaCreditsGainedTemp += amt;

		} else if (RewardType.MONSTER.name().equals(type)) {
			aggregateMonsters(r, staticDataId, amt, monsterIdToQuantity,
					monsterIdToLvlToQuantity);

		} else {
			log.warn("no implementation for awarding {}", r);
			return;
		}

		//following logic is for currency history purposes
		if (RewardType.GEMS.name().equals(type) ||
			RewardType.CASH.name().equals(type) ||
			RewardType.OIL.name().equals(type) ||
			RewardType.GACHA_CREDITS.name().equals(type))
		{
			String currencyType = null;
			if (type.equalsIgnoreCase(MiscMethods.gems)) {
				currencyType = MiscMethods.gems;
			} else if (type.equalsIgnoreCase(MiscMethods.cash)) {
				currencyType = MiscMethods.cash;
			} else if (type.equalsIgnoreCase(MiscMethods.oil)) {
				currencyType = MiscMethods.oil;
			} else if (type.equalsIgnoreCase(MiscMethods.gachaCredits)) {
				currencyType = MiscMethods.gachaCredits;
			}

			if (!resourceTypeToRewardIdToAmt.containsKey(type)) {
				resourceTypeToRewardIdToAmt.put(
						currencyType, new HashMap<Integer, Integer>());
			}

			Map<Integer, Integer> rewardIdToAmt =
					resourceTypeToRewardIdToAmt.get(currencyType);
			int existing = 0;
			if (rewardIdToAmt.containsKey(id))
			{
				existing = rewardIdToAmt.get(id);
			}
			rewardIdToAmt.put(id, existing + amt);
		}
	}

	private void aggregateItems(Map<Integer, Integer> itemIdToQuantity,
			int staticDataId, int amt) {
		int nuAmt = amt;
		if (itemIdToQuantity.containsKey(staticDataId)) {
			nuAmt += itemIdToQuantity.get(staticDataId);
		}
		itemIdToQuantity.put(staticDataId, nuAmt);
	}

	private void aggregateMonsters(Reward r, int staticDataId, int lvl,
			Map<Integer, Integer> monsterIdToQuantity,
			Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity)
	{
		//lvl = 0 means only one piece is given
		//lvl > 0 means one monster with a lvl equal to 'lvl' is given

		int nuAmt = 1;
		if (lvl < 0) {
			log.error("Reward has negative amt! Ignoring it {}", r);
			return;

		} else if (0 == lvl) {
			//give one piece
			if (monsterIdToQuantity.containsKey(staticDataId)) {
				nuAmt += monsterIdToQuantity.get(staticDataId);
			}
			monsterIdToQuantity.put(staticDataId, nuAmt);

		} else {
			//give one monster with a lvl equal to 'amt'

			if (!monsterIdToLvlToQuantity.containsKey(staticDataId)) {
				//first time seeing this monsterId
				monsterIdToLvlToQuantity.put(
						staticDataId, new HashMap<Integer, Integer>());
			}

			Map<Integer, Integer> lvlToQuantity =
					monsterIdToLvlToQuantity.get(staticDataId);
			if (lvlToQuantity.containsKey(lvl)) {
				nuAmt += lvlToQuantity.get(lvl);
			}

			lvlToQuantity.put(lvl, nuAmt);
		}
	}

	private boolean awardItems(Map<Integer, Integer> itemIdToQuantity) {
		if (!itemIdToQuantity.isEmpty()) {
			nuOrUpdatedItems = calculateItemRewards(userId,
					itemForUserRetrieveUtil, itemIdToQuantity);
		}
		if (null != nuOrUpdatedItems && !nuOrUpdatedItems.isEmpty()) {
			int numUpdated = updateUtil.updateItemForUser(nuOrUpdatedItems);
			log.info("items numUpdated={}", numUpdated);
		}

		return true;
	}

	//TODO: Get rid of copy in PurchaseBoosterPackAction
	public static List<ItemForUser> calculateItemRewards(String userId,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			Map<Integer, Integer> itemIdToQuantity) {
		List<ItemForUser> ifuList = null;
		if (!itemIdToQuantity.isEmpty()) {
			//aggregate rewarded items with user's current items
			Map<Integer, ItemForUser> itemIdToIfu = itemForUserRetrieveUtil
					.getSpecificOrAllItemForUserMap(userId,
							itemIdToQuantity.keySet());

			for (Integer itemId : itemIdToQuantity.keySet()) {
				int newQuantity = itemIdToQuantity.get(itemId);

				ItemForUser ifu = null;
				if (itemIdToIfu.containsKey(itemId)) {
					ifu = itemIdToIfu.get(itemId);
				} else {
					//user might not have the item
					ifu = new ItemForUser(userId, itemId, 0);
					itemIdToIfu.put(itemId, ifu);
				}

				newQuantity += ifu.getQuantity();
				ifu.setQuantity(newQuantity);
			}

			ifuList = new ArrayList<ItemForUser>(itemIdToIfu.values());
		}
		return ifuList;
	}

	private boolean awardCurrency(int gemsGainedTemp, int cashGainedTemp,
			int oilGainedTemp, int gachaCreditsGainedTemp,
			Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt)
	{
		boolean awardGems = gemsGainedTemp > 0;
		boolean awardCash = cashGainedTemp > 0;
		boolean awardOil = oilGainedTemp > 0;
		boolean awardGachaCredits = gachaCreditsGainedTemp > 0;
		awardResources = awardGems || awardCash || awardOil || awardGachaCredits;

		if ( awardResources ) {
			prevCurrencies = new HashMap<String, Integer>();
			if (null == u) {
				u = userRetrieveUtil.getUserById(userId);
			}
		}

		if ( awardGems ) {
			prevCurrencies.put(MiscMethods.gems, u.getGems());
			gemsGained = gemsGainedTemp;
		}
		if( awardGachaCredits) {
			prevCurrencies.put(MiscMethods.gachaCredits, u.getGachaCredits());
			gachaCreditsGained = gachaCreditsGainedTemp;
		}
		if ( awardCash ) {
			int curCash = u.getCash();
			prevCurrencies.put(MiscMethods.cash, curCash);
			curCash = Math.min(curCash, maxCash); //in case user's cash is more than maxCash
			int maxCashUserCanGain = maxCash - curCash; //this is the max cash the user can gain
			cashGained = Math.min(maxCashUserCanGain, cashGainedTemp);
		}
		if ( awardOil ) {
			int curOil = u.getOil();
			prevCurrencies.put(MiscMethods.oil, curOil);
			curOil = Math.min(curOil, maxOil);
			int maxOilUserCanGain = maxOil - curOil;
			oilGained = Math.min(maxOilUserCanGain, oilGainedTemp);
		}

		if (!awardResources) {
			return true;
		}

		if (u.updateRelativeCashAndOilAndGems(cashGained, oilGained, gemsGained, gachaCreditsGained) <= 0)
		{
			log.error("won't continue processing. unable to award cash={}, oil={}, gems={}",
					new Object[] { cashGained, oilGained, gemsGained } );

			return false;
		}

		prepCurrencyHistory(resourceTypeToRewardIdToAmt);

		return true;

	}

	private void prepCurrencyHistory(
			Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt)
	{
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;
		String oil = MiscMethods.oil;
		String gachaCredits = MiscMethods.gachaCredits;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemsGained) {
			currencyDeltas.put(gems, gemsGained);
			curCurrencies.put(gems, u.getGems());
			reasonsForChanges
					.put(gems, awardReason);
		}
		if (0 != gachaCreditsGained) {
			currencyDeltas.put(gachaCredits, gachaCreditsGained);
			curCurrencies.put(gachaCredits, u.getGachaCredits());
			reasonsForChanges
					.put(gachaCredits, awardReason);
		}
		if (0 != cashGained) {
			currencyDeltas.put(cash, cashGained);
			curCurrencies.put(cash, u.getCash());
			reasonsForChanges
					.put(cash, awardReason);
		}
		if (0 != oilGained) {
			currencyDeltas.put(oil, oilGained);
			curCurrencies.put(oil, u.getOil());
			reasonsForChanges
					.put(oil, awardReason);
		}

		String reason = "(rewardId,%s.amount)=";
		details = new HashMap<String, String>();

		//being descriptive, separating cash stuff from oil stuff
		for (String type : resourceTypeToRewardIdToAmt.keySet())
		{
			StringBuilder detailSb = new StringBuilder();
			detailSb.append(String.format(reason, type));

			Map<Integer, Integer> rewardIdToAmt = resourceTypeToRewardIdToAmt
					.get(type);
			for (int rId : rewardIdToAmt.keySet()) {
				int amt = rewardIdToAmt.get(rId);

				detailSb.append(String.format(
						"(%s,%s)",
						rId, amt));
			}

			details.put(type, detailSb.toString());
		}

	}

	public boolean awardMonsters( Map<Integer, Integer> monsterIdToQuantity,
			Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity )
	{
		boolean success = false;
		try {
			//TODO: DON'T PROTO THE MONSTERS, leave it to the caller of this class
			nuOrUpdatedMonsters = monsterStuffUtils.updateUserMonsters(
					userId, monsterIdToQuantity, monsterIdToLvlToQuantity,
					awardReason, now, monsterLevelInfoRetrieveUtils);
			success = true;
		} catch (Exception e) {
			log.error(
				String.format(
					"unable to award monsters. rewards=%s. awardReason",
					rewards, awardReason),
				e);
		}

		return success;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public List<ItemForUser> getNuOrUpdatedItems() {
		return nuOrUpdatedItems;
	}

	public List<FullUserMonsterProto> getNuOrUpdatedMonsters() {
		return nuOrUpdatedMonsters;
	}

	public boolean isAwardResources() {
		return awardResources;
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

	public Map<String, Integer> getCurrencyDeltas() {
		return currencyDeltas;
	}

	public Map<String, Integer> getPreviousCurrencies() {
		return prevCurrencies;
	}

	public Map<String, Integer> getCurrentCurrencies() {
		return curCurrencies;
	}

	public Map<String, String> getReasons() {
		return reasonsForChanges;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public boolean existsClanGift() {
		return existsClanGift;
	}

	public ReceivedGiftResponseProto getClanGift() {
		if (existsClanGift) {
			return acga.getChatProto();
		}
		return null;
	}

	public int getGachaCreditsGained() {
		return gachaCreditsGained;
	}

}
