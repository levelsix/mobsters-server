package com.lvl6.server.controller.actionobjects;

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
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class AwardRewardAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

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

	//TODO: Figure out a way to not have all these arguments as a requirement
	public AwardRewardAction(String userId, User u, int maxCash,
			int maxOil, Date now, String awardReason,
			Collection<Reward> rewards,
			UserRetrieveUtils2 userRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			MonsterStuffUtils monsterStuffUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
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
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

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

	private boolean writeChangesToDB() {

		//aggregate like Rewards
		//NOTE: different Reward objects can contain the same values
		Map<Integer, Integer> monsterIdToQuantity = new HashMap<Integer, Integer>();
		Map<Integer, Map<Integer, Integer>> monsterIdToLvlToQuantity =
				new HashMap<Integer, Map<Integer, Integer>>();
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
		int gemsGainedTemp = 0;
		int cashGainedTemp = 0;
		int oilGainedTemp = 0;


		Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt =
				new HashMap<String, Map<Integer,Integer>>();
		for (Reward r : rewards) {
			int id = r.getId();
			String type = r.getType();
			int staticDataId = r.getStaticDataId();
			int amt = r.getAmt();

			if (RewardType.ITEM.name().equals(type)) {
				aggregateItems(itemIdToQuantity, staticDataId, amt);

			} else if (RewardType.GEMS.name().equals(type)) {

				gemsGainedTemp += amt;

			} else if (RewardType.CASH.name().equals(type)) {
				cashGainedTemp += amt;

			} else if (RewardType.OIL.name().equals(type)) {
				oilGainedTemp += amt;

			} else if (RewardType.MONSTER.name().equals(type)) {

				aggregateMonsters(r, staticDataId, amt, monsterIdToQuantity,
						monsterIdToLvlToQuantity);

			} else {
				log.warn("no implementation for awarding {}", r);
			}

			//following logic is for currency history purposes
			if (RewardType.GEMS.name().equals(type) ||
				RewardType.CASH.name().equals(type) ||
				RewardType.OIL.name().equals(type))
			{
				String currencyType = null;
				if (type.equalsIgnoreCase(MiscMethods.gems)) {
					currencyType = MiscMethods.gems;
				} else if (type.equalsIgnoreCase(MiscMethods.cash)) {
					currencyType = MiscMethods.cash;
				} else if (type.equalsIgnoreCase(MiscMethods.oil)) {
					currencyType = MiscMethods.oil;
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

		boolean success = awardItems(itemIdToQuantity);
		if (!success) {
			return false;
		}
		success = awardCurrency(gemsGainedTemp, cashGainedTemp, oilGainedTemp,
				resourceTypeToRewardIdToAmt);
		if (!success) {
			return false;
		}
		success = awardMonsters(monsterIdToQuantity, monsterIdToLvlToQuantity);

		return true;
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
			int oilGainedTemp,
			Map<String, Map<Integer, Integer>> resourceTypeToRewardIdToAmt)
	{
		boolean awardGems = gemsGainedTemp > 0;
		boolean awardCash = cashGainedTemp > 0;
		boolean awardOil = oilGainedTemp > 0;
		awardResources = awardGems || awardCash || awardOil;

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

		if (u.updateRelativeCashAndOilAndGems(cashGained, oilGained, gemsGained) <= 0)
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

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemsGained) {
			currencyDeltas.put(gems, gemsGained);
			curCurrencies.put(gems, u.getGems());
			reasonsForChanges
					.put(gems, awardReason);
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

	public void setNuOrUpdatedMonsters(
			List<FullUserMonsterProto> nuOrUpdatedMonsters) {
		this.nuOrUpdatedMonsters = nuOrUpdatedMonsters;
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

	public void setNuOrUpdatedItems(List<ItemForUser> nuOrUpdatedItems) {
		this.nuOrUpdatedItems = nuOrUpdatedItems;
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
}
