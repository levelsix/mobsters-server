package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.StringUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseSalesAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private JSONObject receiptFromApple;
	private Date now;
	private String uuid;
	private IAPHistoryRetrieveUtils iapHistoryRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private MonsterStuffUtils monsterStuffUtils;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private MiscMethods miscMethods;
	private SalesPackageRetrieveUtils salesPackageRetrieveUtils;
	private SalesItemRetrieveUtils salesItemRetrieveUtils;
	private MonsterRetrieveUtils monsterRetrieveUtils;
	private MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;
	private SalesPackage salesPackage;

	public InAppPurchaseSalesAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InAppPurchaseSalesAction(String userId, User user,
			JSONObject receiptFromApple, Date now,
			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			MonsterStuffUtils monsterStuffUtils,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			CreateInfoProtoUtils createInfoProtoUtils,
			MiscMethods miscMethods,
			SalesPackageRetrieveUtils salesPackageRetrieveUtils,
			SalesItemRetrieveUtils salesItemRetrieveUtils,
			MonsterRetrieveUtils monsterRetrieveUtils,
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
			SalesPackage salesPackage) {
		super();
		this.userId = userId;
		this.user = user;
		this.receiptFromApple = receiptFromApple;
		this.now = now;
		this.uuid = uuid;
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.monsterStuffUtils = monsterStuffUtils;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.miscMethods = miscMethods;
		this.salesPackageRetrieveUtils = salesPackageRetrieveUtils;
		this.salesItemRetrieveUtils = salesItemRetrieveUtils;
		this.monsterRetrieveUtils = monsterRetrieveUtils;
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
		this.salesPackage = salesPackage;
	}

	//derived state
	private double salesPackagePrice;
	private int gemChange;
	private boolean salesJumpTwoTiers;

	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(InAppPurchaseStatus.FAIL);

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

		resBuilder.setStatus(InAppPurchaseStatus.SUCCESS);

	}

	public boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	public boolean verifySemantics(Builder resBuilder) {
		return verifySalesPackage(resBuilder);
	}

	public boolean verifySalesPackage(Builder resBuilder) {
		boolean duplicateReceipt = true;
		duplicateReceipt = InAppPurchaseUtils.checkIfDuplicateReceipt(receiptFromApple, iapHistoryRetrieveUtil);

		if(duplicateReceipt) {
			resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
		}

		if (duplicateReceipt || !userSalesValueMatchesSalesPackage() || !saleIsWithinTimeConstraints()) {
			log.error("user should be buying more expensive sales package! {}",
					 user);
			return false;
		}

		return true;
	}

	public boolean saleIsWithinTimeConstraints() {
		Date saleStartTime = salesPackage.getTimeStart();
		Date saleEndTime = salesPackage.getTimeEnd();

		if((now.getTime() - saleStartTime.getTime() > 0) && (saleEndTime.getTime() - now.getTime() > 0)) {
			return true;
		}
		log.error("sale didn't begin or is over, sale start time is {}, end time is {}",
				new Timestamp(saleStartTime.getTime()), new Timestamp(saleEndTime.getTime()) );
		return false;
	}

	public boolean userSalesValueMatchesSalesPackage() {
		int salesValue = user.getSalesValue();
		salesJumpTwoTiers = user.isSalesJumpTwoTiers();
		salesPackagePrice = salesPackage.getPrice();
		if(salesValue == 0) {
			if(salesPackagePrice == 5)
				return true;
		}
		else if(salesValue == 1) {
			if(!salesJumpTwoTiers && salesPackagePrice == 10)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 20)
				return true;
		}
		else if(salesValue == 2) {
			if(!salesJumpTwoTiers && salesPackagePrice == 20)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 50)
				return true;
		}
		else if(salesValue == 3) {
			if(!salesJumpTwoTiers && salesPackagePrice == 50)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 100)
				return true;
		}
		else if(salesValue >= 4) {
			if(salesPackagePrice == 100)
				return true;
		}
		else {
			log.error("the sale user is trying to buy has a price of: "
					+ salesPackagePrice + "but his salesValue is " + salesValue);
			return false;

		}
		log.error("the sale user is trying to buy has a price of: "
				+ salesPackagePrice + "but his salesValue is " + salesValue);
		return false;

	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {
			Map<Integer, List<SalesItem>> salesItemIdsToSalesItemsForSalesPackIds =
					salesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackIds();
			gemChange = getDiamondsForSalesPackage(salesPackage.getId(), salesItemIdsToSalesItemsForSalesPackIds);

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
					user, salesPackagePrice)) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
				success = false;
			}

			processSalesPackagePurchase(resBuilder);
			updateUserSalesValueAndLastPurchaseTime();

		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}

		return success;
	}

	public int getDiamondsForSalesPackage(int salesPackageId, Map<Integer, List<SalesItem>>
	salesItemIdsToSalesItemsForSalesPackIds) {
		int totalGems = 0;

		for(Integer salesPackId : salesItemIdsToSalesItemsForSalesPackIds.keySet()) {
			if(salesPackId == salesPackageId) {
				List<SalesItem> innerList = salesItemIdsToSalesItemsForSalesPackIds.
						get(salesPackId);
				for(SalesItem si : innerList) {
					totalGems += si.getGemReward();
				}
			}
		}
		return totalGems;
	}

	public void processSalesPackagePurchase(Builder resBuilder) {
		Map<Integer, SalesPackage> idsToSalesPackage = salesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();
		for(Integer salesPackageId : idsToSalesPackage.keySet()) {
			SalesPackage sp = idsToSalesPackage.get(salesPackageId);
			if(sp.getUuid().equals(uuid)) {
				List<SalesItem> salesItemList = salesItemRetrieveUtils
						.getSalesItemsForSalesPackageId(salesPackageId);
				if (null == salesItemList || salesItemList.isEmpty()) {
					throw new RuntimeException(String.format(
							"no sales package for salesPackageId=%s", salesPackageId));
				}

				boolean legit = checkIfMonstersExistInSalesItem(salesItemList);
				if (!legit) {
					throw new RuntimeException(String.format(
							"illegal monster in sales item for salespackageId=%s",
							salesPackageId));
				}

				//booster packs can give out gems, so  reuse processPurchase logic
				processPurchase(resBuilder);

				Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
				List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
				//sop = source of pieces

				String mfusop = createUpdateUserMonsterArgumentsForSales(userId,
						sp.getId(), salesItemList, monsterIdToNumPieces,
						completeUserMonsters, now);

				log.info("!!!!!!!!!mfusop={}", mfusop);
				//this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
				if (!completeUserMonsters.isEmpty()) {
					List<String> monsterForUserIds = insertUtil
							.insertIntoMonsterForUserReturnIds(userId,
									completeUserMonsters, mfusop, now);
					List<FullUserMonsterProto> newOrUpdated = miscMethods
							.createFullUserMonsterProtos(monsterForUserIds,
									completeUserMonsters);

					String preface = "YIIIIPEEEEE!. BOUGHT COMPLETE MONSTER(S)!";
					log.info("{} monster(s) newOrUpdated: {} \t sPackId={}",
							new Object[] { preface, newOrUpdated, sp.getId() });
					//set the builder that will be sent to the client
					resBuilder.addAllUpdatedOrNew(newOrUpdated);
				}

				//this is if the user did not buy a complete monster, UPDATE DB
				if (!monsterIdToNumPieces.isEmpty()) {
					//assume things just work while updating user monsters
					List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
							.updateUserMonsters(userId, monsterIdToNumPieces, null,
									mfusop, now, monsterLevelInfoRetrieveUtils);

					String preface = "YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)!";
					log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
							new Object[] { preface, newOrUpdated, sp.getId() });
					//set the builder that will be sent to the client
					resBuilder.addAllUpdatedOrNew(newOrUpdated);
				}

				//item reward
				List<ItemForUser> ifuList = awardSalesItemItemRewards(userId,
						salesItemList, itemForUserRetrieveUtil, updateUtil);

				if (null != ifuList && !ifuList.isEmpty()) {
					List<UserItemProto> uipList = createInfoProtoUtils
							.createUserItemProtosFromUserItems(ifuList);
					resBuilder.addAllUpdatedUserItems(uipList);
				}
			}
		}

	}

	public boolean checkIfMonstersExistInSalesItem(
			List<SalesItem> itemsUserReceives) {
		boolean monstersExist = true;

		Map<Integer, Monster> monsterIdsToMonsters = monsterRetrieveUtils
				.getMonsterIdsToMonsters();
		for (SalesItem si : itemsUserReceives) {
			int monsterId = si.getMonsterId();

			if (0 == monsterId) {
				//this sales item does not contain a monster reward
				continue;
			} else if (!monsterIdsToMonsters.containsKey(monsterId)) {
				log.error("This sales item contains nonexistent monsterId. item="
						+ si);
				monstersExist = false;
			}
		}
		return monstersExist;
	}

	public String createUpdateUserMonsterArgumentsForSales(String userId,
			int salesPackageId, List<SalesItem> salesItems,
			Map<Integer, Integer> monsterIdsToNumPieces,
			List<MonsterForUser> completeUserMonsters, Date now) {
		StringBuilder sb = new StringBuilder();
		sb.append(ControllerConstants.MFUSOP__SALES_PACKAGE);
		sb.append(" ");
		sb.append(salesPackageId);
		sb.append(" salesItemMonsterIds ");

		List<Integer> salesItemIds = new ArrayList<Integer>();
		for (SalesItem item : salesItems) {
			Integer id = item.getId();
			Integer monsterId = item.getMonsterId();

			//only keep track of the sales item ids that are a monster reward
			if (monsterId <= 0) {
				continue;
			}

			if (item.getMonsterLevel() > 0) {
				//create a "complete" user monster
				int monsterQuantity = item.getMonsterQuantity();
				Monster monzter = monsterRetrieveUtils
						.getMonsterForMonsterId(monsterId);
				List<MonsterForUser> monstersCreated = monsterStuffUtils
						.createLeveledMonsterForUserFromQuantity(userId, monzter,
								monsterQuantity, now, item.getMonsterLevel(),
								monsterLevelInfoRetrieveUtils);
				log.info("monster for users just created" + monstersCreated);

				//return this monster in the argument list completeUserMonsters, so caller
				//can use it
				completeUserMonsters.addAll(monstersCreated);

			} else {
				monsterIdsToNumPieces.put(item.getMonsterId(), item.getMonsterQuantity());
			}
			salesItemIds.add(id);
		}
		if (!salesItemIds.isEmpty()) {
			String salesItemIdsStr = StringUtils.csvList(salesItemIds);
			sb.append(salesItemIdsStr);
		}

		return sb.toString();
	}

	public void processPurchase(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		prevCurrencies.put(MiscMethods.gems, user.getGems());

		if (gemChange != 0) {
			resBuilder.setDiamondsGained(gemChange);
			user.updateRelativeCashAndOilAndGems(0, 0, gemChange);
		}

		prepCurrencyHistory();
	}

	public static List<ItemForUser> awardSalesItemItemRewards(String userId,
			List<SalesItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			UpdateUtil updateUtil) {
		List<ItemForUser> ifuList = calculateSalesItemItemRewards(userId,
				itemsUserReceives, itemForUserRetrieveUtil);

		log.info("ifuList={}", ifuList);
		if (null != ifuList && !ifuList.isEmpty()) {
			int numUpdated = updateUtil.updateItemForUser(ifuList);
			log.info("items numUpdated={}", numUpdated);
			return ifuList;
		} else {
			return null;
		}

	}

	public static List<ItemForUser> calculateSalesItemItemRewards(
			String userId, List<SalesItem> itemsUserReceives,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();

		for (SalesItem si : itemsUserReceives) {
			int itemId = si.getItemId();
			int itemQuantity = si.getItemQuantity();

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


	public boolean updateUserSalesValueAndLastPurchaseTime() {
		int salesValue = user.getSalesValue();
		boolean salesJumpTwoTiers = user.isSalesJumpTwoTiers();

		if(salesValue == 0) {
			salesValue = 1;
		}
		else if(salesValue > 3) {
			salesValue = 5;
		}
		else if(salesJumpTwoTiers) {
			salesValue += 2;
		}
		else {
			salesValue += 1;
		}
		if(salesValue < 1 || salesValue > 5) {
			log.info("invalid sales value {}", salesValue);
			return false;
		}
		return updateUtil.updateUserSalesValue(userId, salesValue, now);
	}

	public void prepCurrencyHistory() {
		String gems = miscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		curCurrencies.put(gems, user.getGems());
		currencyDeltas.put(gems, gemChange);

		reasonsForChanges = new HashMap<String, String>();
		reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__IN_APP_PURCHASE_SALES_PACK);
		details = new HashMap<String, String>();
		details.put(gems, "buying sales pack with uuid " + uuid);
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JSONObject getReceiptFromApple() {
		return receiptFromApple;
	}

	public void setReceiptFromApple(JSONObject receiptFromApple) {
		this.receiptFromApple = receiptFromApple;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public IAPHistoryRetrieveUtils getIapHistoryRetrieveUtil() {
		return iapHistoryRetrieveUtil;
	}

	public void setIapHistoryRetrieveUtil(
			IAPHistoryRetrieveUtils iapHistoryRetrieveUtil) {
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
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

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public UpdateUtil getUpdateUtil() {
		return updateUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public SalesPackageRetrieveUtils getSalesPackageRetrieveUtils() {
		return salesPackageRetrieveUtils;
	}

	public void setSalesPackageRetrieveUtils(
			SalesPackageRetrieveUtils salesPackageRetrieveUtils) {
		this.salesPackageRetrieveUtils = salesPackageRetrieveUtils;
	}

	public SalesItemRetrieveUtils getSalesItemRetrieveUtils() {
		return salesItemRetrieveUtils;
	}

	public void setSalesItemRetrieveUtils(
			SalesItemRetrieveUtils salesItemRetrieveUtils) {
		this.salesItemRetrieveUtils = salesItemRetrieveUtils;
	}

	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
		return monsterRetrieveUtils;
	}

	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
		this.monsterRetrieveUtils = monsterRetrieveUtils;
	}

	public SalesPackage getSalesPackage() {
		return salesPackage;
	}

	public void setSalesPackage(SalesPackage salesPackage) {
		this.salesPackage = salesPackage;
	}

	public double getSalesPackagePrice() {
		return salesPackagePrice;
	}

	public void setSalesPackagePrice(double salesPackagePrice) {
		this.salesPackagePrice = salesPackagePrice;
	}

	public int getGemChange() {
		return gemChange;
	}

	public void setGemChange(int gemChange) {
		this.gemChange = gemChange;
	}

	public Map<String, Integer> getCurrencyDeltas() {
		return currencyDeltas;
	}

	public void setCurrencyDeltas(Map<String, Integer> currencyDeltas) {
		this.currencyDeltas = currencyDeltas;
	}

	public Map<String, Integer> getPrevCurrencies() {
		return prevCurrencies;
	}

	public void setPrevCurrencies(Map<String, Integer> prevCurrencies) {
		this.prevCurrencies = prevCurrencies;
	}

	public Map<String, Integer> getCurCurrencies() {
		return curCurrencies;
	}

	public void setCurCurrencies(Map<String, Integer> curCurrencies) {
		this.curCurrencies = curCurrencies;
	}

	public Map<String, String> getReasonsForChanges() {
		return reasonsForChanges;
	}

	public void setReasonsForChanges(Map<String, String> reasonsForChanges) {
		this.reasonsForChanges = reasonsForChanges;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
		return monsterLevelInfoRetrieveUtils;
	}

	public void setMonsterLevelInfoRetrieveUtils(
			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
	}

	public boolean isSalesJumpTwoTiers() {
		return salesJumpTwoTiers;
	}

	public void setSalesJumpTwoTiers(boolean salesJumpTwoTiers) {
		this.salesJumpTwoTiers = salesJumpTwoTiers;
	}



}
