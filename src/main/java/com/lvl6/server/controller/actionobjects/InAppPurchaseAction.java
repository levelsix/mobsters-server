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

import com.lvl6.info.BoosterItem;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private JSONObject receiptFromApple;
	private String iapProductId;
	private Date now;
	private String uuid;
	private IAPHistoryRetrieveUtils iapHistoryRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2;
	private BoosterItemRetrieveUtils boosterItemRetrieveUtils;
	private MonsterStuffUtils monsterStuffUtils;
	private StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private MiscMethods miscMethods;
	

	public InAppPurchaseAction(String userId, User user,
			JSONObject receiptFromApple, String iapProductId, Date now,
			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2,
			BoosterItemRetrieveUtils boosterItemRetrieveUtils, 
			MonsterStuffUtils monsterStuffUtils,
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			CreateInfoProtoUtils createInfoProtoUtils,
			MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.user = user;
		this.receiptFromApple = receiptFromApple;
		this.iapProductId = iapProductId;
		this.now = now;
		this.uuid = uuid;
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.structureForUserRetrieveUtils2 = structureForUserRetrieveUtils2;
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
		this.monsterStuffUtils = monsterStuffUtils;
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.miscMethods = miscMethods;
	}

	//	//encapsulates the return value from this Action Object
	//	static class InAppPurchaseResource {
	//		
	//		
	//		public InAppPurchaseResource() {
	//			
	//		}
	//	}
	//
	//	public InAppPurchaseResource execute() {
	//		
	//	}

	//derived state
	boolean isStarterPack;
	boolean isMoneyTree;
	boolean isSalesPackage;
	private String packageName;
	private int gemChange;
	private StructureMoneyTree smt;

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

	private boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		boolean success1 = false;
		boolean success2 = false;
		boolean success3 = false;
		success1 = verifyStarterPack(resBuilder);
		success2 = verifyMoneyTree(resBuilder);
		success3 = verifySalesPackage(resBuilder);
		
		if (!success1 && !success2 && !success3) {
			return false;
		} else
			return true;
	}

	private boolean verifyStarterPack(Builder resBuilder) {
		boolean success = true;
		try {
			String transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);

			long transactionIdLong = Long.parseLong(transactionId);
			if (iapHistoryRetrieveUtil
					.checkIfDuplicateTransaction(transactionIdLong)) {
				resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
				log.error("duplicate receipt from user {}", user);
				success = false;
			}

			isStarterPack = IAPValues.packageIsStarterPack(packageName);
			if (success && isStarterPack
					&& user.getNumBeginnerSalesPurchased() > 0) {
				log.error("user trying to buy the starter pack again! {}, {}",
						packageName, user);
				success = false;
			}
		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}
		return success;
	}

	private boolean verifyMoneyTree(Builder resBuilder) {
		boolean success = true;
		try {
			String transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);

			long transactionIdLong = Long.parseLong(transactionId);
			if (iapHistoryRetrieveUtil
					.checkIfDuplicateTransaction(transactionIdLong)) {
				resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
				log.error("duplicate receipt from user {}", user);
				success = false;
			}

			isMoneyTree = IAPValues.packageIsMoneyTree(packageName);

			getStructureMoneyTreeForIAPProductId();
			if (smt == null) {
				log.error("smt is null: {}", smt);
				success = false;
			}

			if (success && isMoneyTree && userOwnsOneMoneyTreeMax()) {
				log.error("user trying to buy the money tree again! {}, {}",
						packageName, user);
				success = false;
			}
		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple={}",
							receiptFromApple), e);
			success = false;
		}
		return success;
	}
	
	private boolean verifySalesPackage(Builder resBuilder) {
		boolean success = true;
		try {
			String transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);

			long transactionIdLong = Long.parseLong(transactionId);
			if (iapHistoryRetrieveUtil
					.checkIfDuplicateTransaction(transactionIdLong)) {
				resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
				log.error("duplicate receipt from user {}", user);
				success = false;
			}

			isSalesPackage = IAPValues.packageIsSalesPackage(packageName);

			if (success && isSalesPackage && userSalesValueMatchesSalesPackage()) {
				log.error("user should be buying more expensive sales package! {}, {}",
						packageName, user);
				success = false;
			}
		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple={}",
							receiptFromApple), e);
			success = false;
		}
		return success;
	}

	private void getStructureMoneyTreeForIAPProductId() {
		Map<Integer, StructureMoneyTree> structIdsToMoneyTrees = structureMoneyTreeRetrieveUtils
				.getStructIdsToMoneyTrees();
		log.info("structIdsToMoneyTrees: {}", structIdsToMoneyTrees);

		for (Integer i : structIdsToMoneyTrees.keySet()) {
			StructureMoneyTree smt2 = structIdsToMoneyTrees.get(i);
			if (smt2.getIapProductId().equals(iapProductId)) {
				smt = smt2;
			}
		}
	}

	private boolean userOwnsOneMoneyTreeMax() {
		Map<Integer, StructureMoneyTree> structIdsToMoneyTreesMap = structureMoneyTreeRetrieveUtils
				.getStructIdsToMoneyTrees();
		List<StructureForUser> sfuList = structureForUserRetrieveUtils2
				.getUserStructsForUser(userId);
		boolean hasMoneyTree = false;

		for (StructureForUser sfu : sfuList) {
			int structId = sfu.getStructId();
			for (Integer ids : structIdsToMoneyTreesMap.keySet()) {
				if (structId == ids) {
					hasMoneyTree = true;
				}
			}
		}

		return hasMoneyTree;
	}
	
	private boolean userSalesValueMatchesSalesPackage() {
		int salesValue = user.getSalesValue();
		int price = Integer.parseInt(packageName.replaceAll("SALE", ""));
		if(salesValue < price) {
			return true;
		}
		else {
			log.error("the sale user is trying to buy has a price of: "
					+ price + "but his salesValue is " + salesValue);
			return false;
			
		}
		
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {

			double realLifeCashCost = IAPValues
					.getCashSpentForPackageName(packageName);

			gemChange = IAPValues.getDiamondsForPackageName(packageName);

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
					user, realLifeCashCost)) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
				success = false;
			}

			if (isStarterPack) {
				processStarterPackPurchase(resBuilder);
			} else if (isMoneyTree) {
				processMoneyTreePurchase(resBuilder);
			} else if (isSalesPackage) {
				processSalesPackagePurchase(resBuilder);
			}
			else {
				processPurchase(resBuilder);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}

		return success;
	}

	private void processStarterPackPurchase(Builder resBuilder) {
		int boosterPackId = ControllerConstants.IN_APP_PURCHASE__STARTER_PACK_BOOSTER_PACK_ID;
		Map<Integer, BoosterItem> idToBoosterItem = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
		if (null == idToBoosterItem || idToBoosterItem.isEmpty()) {
			throw new RuntimeException(String.format(
					"no starter pack for boosterPackId=%s", boosterPackId));
		}

		//TODO: clean up this copy paste of PurchaseBoosterPackController logic
		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
		itemsUserReceives.addAll(idToBoosterItem.values());
		boolean legit = miscMethods.checkIfMonstersExist(itemsUserReceives);
		if (!legit) {
			throw new RuntimeException(String.format(
					"illegal monster in starter pack for boosterPackId=%s",
					boosterPackId));
		}
		gemChange = miscMethods.determineGemReward(itemsUserReceives);
		//booster packs can give out gems, so  reuse processPurchase logic
		processPurchase(resBuilder);

		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
		//sop = source of pieces
		String mfusop = miscMethods.createUpdateUserMonsterArguments(userId,
				boosterPackId, itemsUserReceives, monsterIdToNumPieces,
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
			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
					new Object[] { preface, newOrUpdated, boosterPackId });
			//set the builder that will be sent to the client
			resBuilder.addAllUpdatedOrNew(newOrUpdated);
		}

		//this is if the user did not buy a complete monster, UPDATE DB
		if (!monsterIdToNumPieces.isEmpty()) {
			//assume things just work while updating user monsters
			List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
					.updateUserMonsters(userId, monsterIdToNumPieces, null,
							mfusop, now);

			String preface = "YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)!";
			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
					new Object[] { preface, newOrUpdated, boosterPackId });
			//set the builder that will be sent to the client
			resBuilder.addAllUpdatedOrNew(newOrUpdated);
		}

		//item reward
		List<ItemForUser> ifuList = PurchaseBoosterPackAction
				.awardBoosterItemItemRewards(userId, itemsUserReceives,
						itemForUserRetrieveUtil, updateUtil);
		//		log.info("ifuList={}", ifuList);
		if (null != ifuList && !ifuList.isEmpty()) {
			List<UserItemProto> uipList = createInfoProtoUtils
					.createUserItemProtosFromUserItems(ifuList);
			resBuilder.addAllUpdatedUserItems(uipList);
		}
	}

	private void processMoneyTreePurchase(Builder resBuilder) {

		//assumed to only contain one money tree max for now
		List<StructureForUser> listOfUsersMoneyTree = structureForUserRetrieveUtils2
				.getMoneyTreeForUserList(userId, null);
		String userStructId = "";
		Timestamp purchaseTime = new Timestamp(now.getTime());
		CoordinatePair cp = new CoordinatePair(0, 0);

		if (listOfUsersMoneyTree.isEmpty()) {
			Timestamp lastRetrievedTime = purchaseTime;
			boolean isComplete = true;

			userStructId = insertUtil.insertUserStruct(userId,
					smt.getStructId(), cp, purchaseTime, lastRetrievedTime,
					isComplete);

		} else {
			userStructId = listOfUsersMoneyTree.get(0).getId();
			boolean success = updateUtil
					.updatePurchaseTimeRetrieveTimeForMoneyTree(userStructId,
							purchaseTime);
			if (!success) {
				throw new RuntimeException(
						String.format("failed to update purchase time of money tree for user"
								+ userId));
			}
		}
		if (userStructId.equals("")) {
			throw new RuntimeException(
					String.format("failed to add money tree to table for user"
							+ userId));
		}

		processPurchase(resBuilder);

		StructureForUser sfu = new StructureForUser(userStructId, userId,
				smt.getStructId(), purchaseTime, cp, purchaseTime, true,
				"POSITION_1", 0);

		List<FullUserStructureProto> fuspList = new ArrayList<FullUserStructureProto>();

		if (null != sfu) {
			FullUserStructureProto fusp = createInfoProtoUtils
					.createFullUserStructureProtoFromUserstruct(sfu);
			fuspList.add(fusp);
			resBuilder.addAllUpdatedMoneyTree(fuspList);
		}
	}

	//	private List<ItemForUser> calculateItemRewards(
	//		String userId,
	//		List<BoosterItem> itemsUserReceives )
	//	{
	//		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();
	//		
	//		for (BoosterItem bi : itemsUserReceives) {
	//			int itemId = bi.getItemId();
	//			int itemQuantity = bi.getItemQuantity();
	//			
	//			if (itemId <= 0 || itemQuantity <= 0) {
	//				continue;
	//			}
	//			
	//			//user could have gotten multiple of the same BoosterItem
	//			int newQuantity = itemQuantity;
	//			if (itemIdToQuantity.containsKey(itemId))
	//			{
	//				newQuantity += itemIdToQuantity.get(itemId);
	//			}
	//			itemIdToQuantity.put(itemId, newQuantity);
	//		}
	//		
	//		List<ItemForUser> ifuList = null;
	//	    if (!itemIdToQuantity.isEmpty()) {
	//	    	//aggregate rewarded items with user's current items
	//	    	Map<Integer, ItemForUser> itemIdToIfu = 
	//	    		itemForUserRetrieveUtil.getSpecificOrAllItemForUserMap(userId,
	//	    			itemIdToQuantity.keySet());
	//	    	
	//	    	for (Integer itemId : itemIdToQuantity.keySet()) {
	//	    		int newQuantity = itemIdToQuantity.get(itemId);
	//	    		
	//	    		ItemForUser ifu = null;
	//	    		if (itemIdToIfu.containsKey(itemId)){
	//	    			ifu = itemIdToIfu.get(itemId);
	//	    		} else {
	//	    			//user might not have the item
	//	    			ifu = new ItemForUser(userId, itemId, 0);
	//	    			itemIdToIfu.put(itemId, ifu);
	//	    		}
	//	    		
	//	    		newQuantity += ifu.getQuantity();
	//	    		ifu.setQuantity(newQuantity);
	//	    	}
	//	    	
	//	    	ifuList = new ArrayList<ItemForUser>(itemIdToIfu.values());
	//	    }
	//	    return ifuList;
	//	}
	
	private void processSalesPackagePurchase(Builder resBuilder) {
		Map<Integer, SalesPackage> idsToSalesPackage = SalesPackageRetrieveUtils.getSalesPackageIdsToSalesPackages();
		for(Integer salesPackageId : idsToSalesPackage.keySet()) {
			SalesPackage sp = idsToSalesPackage.get(salesPackageId);
			if(sp.getUuid().equals(uuid)) {
				Map<Integer, SalesItem> idToSalesItem = SalesItemRetrieveUtils
						.getSalesItemIdsToSalesItemsForSalesPackageId(salesPackageId);
				if (null == idToSalesItem || idToSalesItem.isEmpty()) {
					throw new RuntimeException(String.format(
							"no sales package for salesPackageId=%s", salesPackageId));
				}

				//TODO: clean up this copy paste of PurchaseBoosterPackController logic
				List<SalesItem> itemsUserReceives = new ArrayList<SalesItem>();
				itemsUserReceives.addAll(idToSalesItem.values());
				boolean legit = MiscMethods.checkIfMonstersExistInSalesItem(itemsUserReceives);
				if (!legit) {
					throw new RuntimeException(String.format(
							"illegal monster in sales item for salespackageId=%s",
							salesPackageId));
				}
				gemChange = MiscMethods.determineGemRewardForSale(itemsUserReceives);

				
				//booster packs can give out gems, so  reuse processPurchase logic
				processPurchase(resBuilder);

				Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
				List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
				//sop = source of pieces
				
				String mfusop = MiscMethods.createUpdateUserMonsterArgumentsForSales(userId,
						sp.getId(), itemsUserReceives, monsterIdToNumPieces,
						completeUserMonsters, now);

				log.info("!!!!!!!!!mfusop={}", mfusop);
				//this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
				if (!completeUserMonsters.isEmpty()) {
					List<String> monsterForUserIds = InsertUtils.get()
							.insertIntoMonsterForUserReturnIds(userId,
									completeUserMonsters, mfusop, now);
					List<FullUserMonsterProto> newOrUpdated = MiscMethods
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
					List<FullUserMonsterProto> newOrUpdated = MonsterStuffUtils
							.updateUserMonsters(userId, monsterIdToNumPieces, null,
									mfusop, now);

					String preface = "YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)!";
					log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
							new Object[] { preface, newOrUpdated, sp.getId() });
					//set the builder that will be sent to the client
					resBuilder.addAllUpdatedOrNew(newOrUpdated);
				}

				//item reward
				List<ItemForUser> ifuList = awardSalesItemItemRewards(userId, 
						itemsUserReceives, itemForUserRetrieveUtil, updateUtil);
				 
				if (null != ifuList && !ifuList.isEmpty()) {
					List<UserItemProto> uipList = CreateInfoProtoUtils
							.createUserItemProtosFromUserItems(ifuList);
					resBuilder.addAllUpdatedUserItems(uipList);
				}
			}
		}
		
	}
	
	private List<ItemForUser> awardSalesItemItemRewards(String userId,
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
	
	private List<ItemForUser> calculateSalesItemItemRewards(
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

	private List<ItemForUser> calculateItemRewards(String userId,
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

	private void processPurchase(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if (gemChange != 0) {
			if(isStarterPack) {
				prevCurrencies.put(MiscMethods.gems, user.getGems());
				resBuilder.setDiamondsGained(gemChange);
				user.updateRelativeDiamondsBeginnerSale(gemChange, isStarterPack);
			}
			else if(isSalesPackage) {
				prevCurrencies.put(MiscMethods.gems, user.getGems());
				resBuilder.setDiamondsGained(gemChange);
				user.updateRelativeCashAndOilAndGems(0, 0, gemChange);
			}
		}

		prepCurrencyHistory();
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		curCurrencies.put(gems, user.getGems());
		currencyDeltas.put(gems, gemChange);

		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemChange) {
			reasonsForChanges.put(gems,
					ControllerConstants.UCHRFC__IN_APP_PURCHASE);
		} else {
			reasonsForChanges.put(gems,
					ControllerConstants.UCHRFC__IN_APP_PURCHASE_MONEY_TREE);
		}
		details = new HashMap<String, String>();
		details.put(gems, packageName);
	}

	public User getUser() {
		return user;
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
