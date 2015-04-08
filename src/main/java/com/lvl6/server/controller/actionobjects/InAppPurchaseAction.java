package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
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
	private SalesPackageRetrieveUtils salesPackageRetrieveUtils;
	private SalesItemRetrieveUtils salesItemRetrieveUtils;
	
	public InAppPurchaseAction() {
		super();
		// TODO Auto-generated constructor stub
	}

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
			MiscMethods miscMethods,
			SalesPackageRetrieveUtils salesPackageRetrieveUtils,
			SalesItemRetrieveUtils salesItemRetrieveUtils) {
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
		this.salesPackageRetrieveUtils = salesPackageRetrieveUtils;
		this.salesItemRetrieveUtils = salesItemRetrieveUtils;
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
	private SalesPackage salesPackage;
	private double salesPackagePrice;
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

	public boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	public boolean verifySemantics(Builder resBuilder) {
		boolean success1 = false;
		boolean success2 = false;
		boolean success3 = false;
		success1 = verifyStarterPack(resBuilder);
//		success2 = verifyMoneyTree(resBuilder);
		success3 = verifySalesPackage(resBuilder);
		
		if (!success1 && !success2 && !success3) {
			return false;
		} else
			return true;
	}

	public boolean verifyStarterPack(Builder resBuilder) {
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

	public boolean verifyMoneyTree(Builder resBuilder) {
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
	
	public boolean verifySalesPackage(Builder resBuilder) {
		boolean duplicateReceipt = true;
			duplicateReceipt = checkIfDuplicateReceipt();
			
			if(duplicateReceipt) {
				resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
			}

			Map<String, SalesPackage> salesPackageNamesToSalesPackages = 
					salesPackageRetrieveUtils.getSalesPackageNamesToSalesPackages();
			isSalesPackage = packageIsSalesPackage(packageName, salesPackageNamesToSalesPackages);

			if (!(duplicateReceipt && isSalesPackage && userSalesValueMatchesSalesPackage())) {
				log.error("user should be buying more expensive sales package! {}, {}",
						packageName, user);
				return false;
			}
		
		log.info("sales package verified");
		salesPackage = salesPackageNamesToSalesPackages.get(packageName);
		
		return true;
	}
	
	public boolean checkIfDuplicateReceipt() { //returns true if duplicate
		String transactionId = null;
		try {
			transactionId = receiptFromApple
					.getString(IAPValues.TRANSACTION_ID);
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
		} catch (JSONException e) {
			log.error(String.format("error verifying InAppPurchase request. "
					+ "receiptFromApple={}", receiptFromApple), e);
			e.printStackTrace();
		}

		long transactionIdLong = Long.parseLong(transactionId);
		if (iapHistoryRetrieveUtil
				.checkIfDuplicateTransaction(transactionIdLong)) {
			log.error("duplicate receipt from user {}", user);
			return true;
		}
		else return false;
	}

	public void getStructureMoneyTreeForIAPProductId() {
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

	public boolean userOwnsOneMoneyTreeMax() {
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
	
	public boolean packageIsSalesPackage(String packageName, 
			Map<String, SalesPackage> salesPackageNamesToSalesPackages) {
		
		for(String name : salesPackageNamesToSalesPackages.keySet()) {
			if(name.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		log.error("packagename {} does not exist in table of sales packages",
				packageName);
		return false;
	}
	
	public boolean userSalesValueMatchesSalesPackage() {
		int salesValue = user.getSalesValue();
		boolean salesJumpTwoTiers = user.isSalesJumpTwoTiers();
		salesPackagePrice = salesPackage.getPrice();
		if(salesValue == 0) {
			if(salesPackagePrice == 4.99)
				return true;
		}
		else if(salesValue == 1) {
			if(!salesJumpTwoTiers && salesPackagePrice == 9.99)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 19.99)
				return true;
		}
		else if(salesValue == 2) {
			if(!salesJumpTwoTiers && salesPackagePrice == 19.99)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 49.99)
				return true;
		}
		else if(salesValue == 3) {
			if(!salesJumpTwoTiers && salesPackagePrice == 49.99)
				return true;
			else if(salesJumpTwoTiers && salesPackagePrice == 99.99)
				return true;
		}
		else if(salesValue >= 4) {
			if(salesPackagePrice == 99.99)
				return true;
		}
		else {
			log.error("the sale user is trying to buy has a price of: "
					+ salesPackagePrice + "but his salesValue is " + salesValue);
			return false;
			
		}
		return false;
		
	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {
			
			double realLifeCashCost;
			if(isSalesPackage) {
				realLifeCashCost = salesPackagePrice;
				Map<Integer, List<SalesItem>> salesItemIdsToSalesItemsForSalesPackIds = 
						salesItemRetrieveUtils.getSalesItemIdsToSalesItemsForSalesPackIds();
				gemChange = getDiamondsForSalesPackage(salesPackage.getId(), salesItemIdsToSalesItemsForSalesPackIds);
			}
			else {
				realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);
				gemChange = IAPValues.getDiamondsForPackageName(packageName);
			}

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
				updateUserSalesValueAndLastPurchaseTime();
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

	public void processStarterPackPurchase(Builder resBuilder) {
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

	public void processMoneyTreePurchase(Builder resBuilder) {

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

				boolean legit = miscMethods.checkIfMonstersExistInSalesItem(salesItemList);
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
				
				String mfusop = miscMethods.createUpdateUserMonsterArgumentsForSales(userId,
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
									mfusop, now);

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
	
	public List<ItemForUser> awardSalesItemItemRewards(String userId,
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
	
	public List<ItemForUser> calculateSalesItemItemRewards(
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

	public List<ItemForUser> calculateItemRewards(String userId,
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

	public void processPurchase(Builder resBuilder) {
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

	public void prepCurrencyHistory() {
		String gems = miscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		curCurrencies.put(gems, user.getGems());
		currencyDeltas.put(gems, gemChange);

		reasonsForChanges = new HashMap<String, String>();
		if (0 != gemChange) {
			if(isStarterPack) {
				reasonsForChanges.put(gems,
						ControllerConstants.UCHRFC__IN_APP_PURCHASE_STARTER_PACK);
			}
			else if(isSalesPackage) {
				reasonsForChanges.put(gems,
						ControllerConstants.UCHRFC__IN_APP_PURCHASE_SALES_PACK);
			}
			else reasonsForChanges.put(gems,
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JSONObject getReceiptFromApple() {
		return receiptFromApple;
	}

	public void setReceiptFromApple(JSONObject receiptFromApple) {
		this.receiptFromApple = receiptFromApple;
	}

	public String getIapProductId() {
		return iapProductId;
	}

	public void setIapProductId(String iapProductId) {
		this.iapProductId = iapProductId;
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

	public StructureForUserRetrieveUtils2 getStructureForUserRetrieveUtils2() {
		return structureForUserRetrieveUtils2;
	}

	public void setStructureForUserRetrieveUtils2(
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils2) {
		this.structureForUserRetrieveUtils2 = structureForUserRetrieveUtils2;
	}

	public BoosterItemRetrieveUtils getBoosterItemRetrieveUtils() {
		return boosterItemRetrieveUtils;
	}

	public void setBoosterItemRetrieveUtils(
			BoosterItemRetrieveUtils boosterItemRetrieveUtils) {
		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
	}

	public MonsterStuffUtils getMonsterStuffUtils() {
		return monsterStuffUtils;
	}

	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
		this.monsterStuffUtils = monsterStuffUtils;
	}

	public StructureMoneyTreeRetrieveUtils getStructureMoneyTreeRetrieveUtils() {
		return structureMoneyTreeRetrieveUtils;
	}

	public void setStructureMoneyTreeRetrieveUtils(
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils) {
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
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

	public boolean isStarterPack() {
		return isStarterPack;
	}

	public void setStarterPack(boolean isStarterPack) {
		this.isStarterPack = isStarterPack;
	}

	public boolean isMoneyTree() {
		return isMoneyTree;
	}

	public void setMoneyTree(boolean isMoneyTree) {
		this.isMoneyTree = isMoneyTree;
	}

	public boolean isSalesPackage() {
		return isSalesPackage;
	}

	public void setSalesPackage(boolean isSalesPackage) {
		this.isSalesPackage = isSalesPackage;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

	public StructureMoneyTree getSmt() {
		return smt;
	}

	public void setSmt(StructureMoneyTree smt) {
		this.smt = smt;
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

	public void setUser(User user) {
		this.user = user;
	}

	public void setCurrencyDeltas(Map<String, Integer> currencyDeltas) {
		this.currencyDeltas = currencyDeltas;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}
	
	

}
