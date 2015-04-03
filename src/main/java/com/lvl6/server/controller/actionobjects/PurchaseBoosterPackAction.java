package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto.BoosterPackType;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.Builder;
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto.PurchaseBoosterPackStatus;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class PurchaseBoosterPackAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private int boosterPackId;
	private Date now;
	private Timestamp clientTime;
	private boolean freeBoosterPack;
	private TimeUtils timeUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private BoosterPackRetrieveUtils boosterPackRetrieveUtils;
	private BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	private UpdateUtil updateUtil;

	public PurchaseBoosterPackAction(String userId, int boosterPackId,
			Date now, Timestamp clientTime, boolean freeBoosterPack,
			TimeUtils timeUtil, UserRetrieveUtils2 userRetrieveUtil,
			BoosterPackRetrieveUtils boosterPackRetrieveUtils,
			BoosterItemRetrieveUtils boosterItemRetrieveUtils,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			UpdateUtil updateUtil) {
		super();
		this.userId = userId;
		this.boosterPackId = boosterPackId;
		this.clientTime = clientTime;
		this.now = now;
		this.freeBoosterPack = freeBoosterPack;
		this.timeUtil = timeUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.boosterPackRetrieveUtils = boosterPackRetrieveUtils;
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.updateUtil = updateUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class PurchaseBoosterPackResource {
	//		
	//		
	//		public PurchaseBoosterPackResource() {
	//			
	//		}
	//	}
	//
	//	public PurchaseBoosterPackResource execute() {
	//		
	//	}

	//derived state
	private User user;
	private BoosterPack aPack;
	private boolean riggedPack;
	private int boosterPackIdPurchased;
	private int gemPrice;
	private Map<Integer, BoosterItem> boosterItemIdsToBoosterItems;
	private int userGems;
	private List<BoosterItem> itemsUserReceives;
	private List<ItemForUser> ifuList;

	private int gemReward;
	private int gemChange;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);

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
			//			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			return;
		}

		resBuilder.setStatus(PurchaseBoosterPackStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}
		boolean legitPack = verifyBoosterPack(resBuilder, boosterPackId);
		if (!legitPack) {
			return false;
		}

		boosterPackIdPurchased = 0;
		String type = aPack.getType();

		if (!user.isBoughtRiggedBoosterPack()
				&& BoosterPackType.BASIC.name().equals(type)) {
			//when user buys the lowest rated booster pack and hasn't
			//bought a rigged booster pack, rig the purchase
			log.info("rigging booster pack purchase. boosterPack={}, user={}",
					aPack, user);
			boosterPackIdPurchased = aPack.getRiggedId();
			riggedPack = true;
		} else {
			riggedPack = false;
			boosterPackIdPurchased = boosterPackId;
		}

		if (boosterPackIdPurchased > 0) {
			legitPack = verifyBoosterPack(resBuilder, boosterPackIdPurchased);
		}

		if (!legitPack) {
			return false;
		}

		boosterItemIdsToBoosterItems = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackIdPurchased);

		if (null == boosterItemIdsToBoosterItems
				|| boosterItemIdsToBoosterItems.isEmpty()) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no booster items={}", boosterItemIdsToBoosterItems);
			return false;
		}

		userGems = user.getGems();

		//check if user can afford to buy however many more user wants to buy
		if (!freeBoosterPack) {
			if (userGems < gemPrice) {
				resBuilder
						.setStatus(PurchaseBoosterPackStatus.FAIL_INSUFFICIENT_GEMS);
				return false;
			}
		} else {
			boolean legitFree = verifyFreeBoosterPack(resBuilder);
			if (!legitFree) {
				return false;
			}
		}

		return true;
	}

	private boolean verifyBoosterPack(Builder resBuilder, int bpId) {
		aPack = boosterPackRetrieveUtils.getBoosterPackForBoosterPackId(bpId);

		if (null == aPack) {
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error("no BoosterPack for id={}", bpId);
			return false;
		}

		gemPrice = aPack.getGemPrice();
		return true;
	}

	private boolean verifyFreeBoosterPack(Builder resBuilder) {
		Date lastFreeDate = user.getLastFreeBoosterPackTime();
		if (null == lastFreeDate) {
			return true;
		}
		if (!timeUtil.isFirstEarlierThanSecond(lastFreeDate, now)) {
			// lastFreeDate is not earlier than now
			resBuilder.setStatus(PurchaseBoosterPackStatus.FAIL_OTHER);
			log.error(
					"client incorrectly says time now={} is before lastFreeBoosterPackDate={}",
					now, lastFreeDate);
			return false;

		} else if (Math.abs(timeUtil.numDaysDifference(lastFreeDate, now)) == 0) {
			// lastFreeDate is earlier than now but
			// lastFreeDate is on same day as now 

			log.error(String
					.format("client already received free booster pack today. lastFreeBoosterPackDate=%s, now=%s",
							lastFreeDate, now));
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if (!freeBoosterPack) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
		}

		int numBoosterItemsUserWants = 1;
		itemsUserReceives = MiscMethods.determineBoosterItemsUserReceives(
				numBoosterItemsUserWants, boosterItemIdsToBoosterItems);

		boolean legit = MiscMethods.checkIfMonstersExist(itemsUserReceives);

		if (!legit) {
			log.error("illegal to verify booster items, {}", itemsUserReceives);
			return false;
		}

		updateUserCurrency();

		prepCurrencyHistory();

		String preface = "SPENT MONEY(?) ON BOOSTER PACK:";
		log.info(
				"{} free={}, bPackId={}, gemPrice={}, gemReward={}, itemsUserReceives={}",
				new Object[] { preface, freeBoosterPack,
						boosterPackIdPurchased, gemPrice, gemReward,
						itemsUserReceives });

		//TODO: Many places have copies of this logic: Consolidate them
		//award monsters and items
		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
		//sop = source of pieces
		String mfusop = MiscMethods.createUpdateUserMonsterArguments(userId,
				boosterPackIdPurchased, itemsUserReceives,
				monsterIdToNumPieces, completeUserMonsters, now);

		log.info("!!!!!!!!!mfusop={}", mfusop);

		//TODO: Set the monsters in the Controller
		//this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
		if (!completeUserMonsters.isEmpty()) {
			List<String> monsterForUserIds = InsertUtils.get()
					.insertIntoMonsterForUserReturnIds(userId,
							completeUserMonsters, mfusop, now);
			List<FullUserMonsterProto> newOrUpdated = MiscMethods
					.createFullUserMonsterProtos(monsterForUserIds,
							completeUserMonsters);

			preface = "YIIIIPEEEEE!. BOUGHT COMPLETE MONSTER(S)!";
			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
					new Object[] { preface, newOrUpdated,
							boosterPackIdPurchased });
			//set the builder that will be sent to the client
			resBuilder.addAllUpdatedOrNew(newOrUpdated);
		}

		//this is if the user did not buy a complete monster, UPDATE DB
		if (!monsterIdToNumPieces.isEmpty()) {
			//assume things just work while updating user monsters
			List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
					.updateUserMonsters(userId, monsterIdToNumPieces, null,
							mfusop, now);

			preface = "YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)!";
			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
					new Object[] { preface, newOrUpdated,
							boosterPackIdPurchased });
			//set the builder that will be sent to the client
			resBuilder.addAllUpdatedOrNew(newOrUpdated);
		}

		ifuList = awardBoosterItemItemRewards(userId, itemsUserReceives,
				itemForUserRetrieveUtil, updateUtil);
		return true;
	}

	private void updateUserCurrency() {
		gemReward = MiscMethods.determineGemReward(itemsUserReceives);

		gemChange = -1 * gemPrice;
		if (freeBoosterPack) {
			gemChange = 0;
		}
		gemChange += gemReward;

		//update user's flag concerning whether or not he's bought a rigged pack
		//update user's last free booster pack time
		boolean updated = user.updateBoughtBoosterPack(gemChange, now,
				freeBoosterPack, riggedPack);
		log.info("updated, user bought boosterPack? {}", updated);
	}

	public static List<ItemForUser> awardBoosterItemItemRewards(String userId,
			List<BoosterItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			UpdateUtil updateUtil) {
		List<ItemForUser> ifuList = PurchaseBoosterPackAction
				.calculateBoosterItemItemRewards(userId, itemsUserReceives,
						itemForUserRetrieveUtil);

		log.info("ifuList={}", ifuList);
		if (null != ifuList && !ifuList.isEmpty()) {
			int numUpdated = updateUtil.updateItemForUser(ifuList);
			log.info("items numUpdated={}", numUpdated);
			return ifuList;
		} else {
			return null;
		}

	}

	public static List<ItemForUser> calculateBoosterItemItemRewards(
			String userId, List<BoosterItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();

		for (BoosterItem bi : itemsUserReceives) {
			int itemId = bi.getItemId();
			int itemQuantity = bi.getItemQuantity();

			if (itemId <= 0 || itemQuantity <= 0) {
				continue;
			}

			//user could have gotten multiple of the same BoosterItem
			int newQuantity = itemQuantity;
			if (itemIdToQuantity.containsKey(itemId)) {
				newQuantity += itemIdToQuantity.get(itemId);
			}
			itemIdToQuantity.put(itemId, newQuantity);
		}

		return calculateItemRewards(userId, itemForUserRetrieveUtil,
				itemIdToQuantity);
	}

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

	private void prepCurrencyHistory() {
		String gems = MiscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb = new StringBuilder();

		if (0 != gemChange) {
			currencyDeltas.put(gems, gemChange);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
					ControllerConstants.UCHRFC__PURHCASED_BOOSTER_PACK);
			detailSb.append(" gemPrice=");
			detailSb.append(gemPrice);
			detailSb.append(" gemReward=");
			detailSb.append(gemReward);
		}

		details = new HashMap<String, String>();
		List<Integer> itemIds = new ArrayList<Integer>();
		if (null != itemsUserReceives && !itemsUserReceives.isEmpty()) {

			for (BoosterItem item : itemsUserReceives) {
				int id = item.getId();
				itemIds.add(id);
			}

			detailSb.append(" bItemIds=");
			String itemIdsCsv = StringUtils.csvList(itemIds);
			detailSb.append(itemIdsCsv);
		}
		if (gemReward > 0) {
		}
		details.put(gems, detailSb.toString());
	}

	public User getUser() {
		return user;
	}

	public List<BoosterItem> getItemsUserReceives() {
		return itemsUserReceives;
	}

	public List<ItemForUser> getIfuList() {
		return ifuList;
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
