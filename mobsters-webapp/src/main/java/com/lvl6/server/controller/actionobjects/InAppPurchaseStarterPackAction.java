//package com.lvl6.server.controller.actionobjects;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.lvl6.info.BoosterItem;
//import com.lvl6.info.ItemForUser;
//import com.lvl6.info.MonsterForUser;
//import com.lvl6.info.User;
//import com.lvl6.misc.MiscMethods;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.IAPValues;
//import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
//import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
//import com.lvl6.proto.ItemsProto.UserItemProto;
//import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
//import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
//import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
//import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
//import com.lvl6.server.controller.utils.BoosterItemUtils;
//import com.lvl6.server.controller.utils.InAppPurchaseUtils;
//import com.lvl6.server.controller.utils.MonsterStuffUtils;
//import com.lvl6.utils.CreateInfoProtoUtils;
//import com.lvl6.utils.utilmethods.InsertUtil;
//import com.lvl6.utils.utilmethods.UpdateUtil;
//
//@Component//@Scope("prototype")//public class InAppPurchaseStarterPackAction {
//
//	private static Logger log = LoggerFactory.getLogger(//	}.getClass().getEnclosingClass());
//
//	private String userId;
//	private User user;
//	private JSONObject receiptFromApple;
//	private Date now;
//	private String uuid;
//	@Autowired protected IAPHistoryRetrieveUtils iapHistoryRetrieveUtil; 
//	@Autowired protected ItemForUserRetrieveUtil itemForUserRetrieveUtil; 
//	@Autowired protected MonsterStuffUtils monsterStuffUtils; 
//	@Autowired protected InsertUtil insertUtil;
//	@Autowired protected UpdateUtil updateUtil;
//	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 
//	private MiscMethods miscMethods;
//	@Autowired protected BoosterItemRetrieveUtils boosterItemRetrieveUtils; 
//	@Autowired protected MonsterRetrieveUtils monsterRetrieveUtils; 
//	@Autowired protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils; 
//	@Autowired protected InAppPurchaseUtils inAppPurchaseUtils; 
//	@Autowired protected BoosterItemUtils boosterItemUtils; 
//	@Autowired protected RewardRetrieveUtils rewardRetrieveUtils; 
//
//	public InAppPurchaseStarterPackAction() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	public InAppPurchaseStarterPackAction(String userId, User user,
//			JSONObject receiptFromApple, Date now,
//			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
//			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
//			MonsterStuffUtils monsterStuffUtils,
//			InsertUtil insertUtil, UpdateUtil updateUtil,
//			CreateInfoProtoUtils createInfoProtoUtils,
//			MiscMethods miscMethods,
//			BoosterItemRetrieveUtils boosterItemRetrieveUtils,
//			MonsterRetrieveUtils monsterRetrieveUtils,
//			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils,
//			InAppPurchaseUtils inAppPurchaseUtils,
//			BoosterItemUtils boosterItemUtils) {
//		super();
//		this.userId = userId;
//		this.user = user;
//		this.receiptFromApple = receiptFromApple;
//		this.now = now;
//		this.uuid = uuid;
//		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
//		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
//		this.monsterStuffUtils = monsterStuffUtils;
//		this.insertUtil = insertUtil;
//		this.updateUtil = updateUtil;
//		this.createInfoProtoUtils = createInfoProtoUtils;
//		this.miscMethods = miscMethods;
//		this.boosterItemRetrieveUtils = boosterItemRetrieveUtils;
//		this.monsterRetrieveUtils = monsterRetrieveUtils;
//		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
//		this.inAppPurchaseUtils = inAppPurchaseUtils;
//		this.boosterItemUtils = boosterItemUtils;
//	}
//
//	//derived state
//	private int boosterPackId;
//	private String packageName;
//	private int gemChange;
//
//	private Map<String, Integer> currencyDeltas;
//	private Map<String, Integer> prevCurrencies;
//	private Map<String, Integer> curCurrencies;
//	private Map<String, String> reasonsForChanges;
//	private Map<String, String> details;
//
//	public void execute(Builder resBuilder) {
//		resBuilder.setStatus(InAppPurchaseStatus.FAIL);
//
//		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
//		valid = verifySemantics(resBuilder);
//
//		if (!valid) {
//			return;
//		}
//
//		boolean success = writeChangesToDB(resBuilder);
//		if (!success) {
//			return;
//		}
//
//		resBuilder.setStatus(InAppPurchaseStatus.SUCCESS);
//
//	}
//
//	public boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}
//
//	public boolean verifySemantics(Builder resBuilder) {
//		return verifyStarterPack(resBuilder);
//	}
//
//	public boolean verifyStarterPack(Builder resBuilder) {
//		boolean duplicateReceipt = true;
//		duplicateReceipt = inAppPurchaseUtils.checkIfDuplicateReceipt(receiptFromApple, iapHistoryRetrieveUtil);
//
//		if(duplicateReceipt) {
//			resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
//		}
//
//		if (duplicateReceipt || user.getNumBeginnerSalesPurchased() > 0) {
//			log.error("user trying to buy the starter pack again! {}, {}",
//					packageName, user);
//			return false;
//		}
//		return true;
//	}
//
//	public boolean writeChangesToDB(Builder resBuilder) {
//		boolean success = true;
//		try {
//			double realLifeCashCost;
//			realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);
//
//			List<BoosterItem> itemsUserReceives = getItemsUserReceives();
//			gemChange = boosterItemUtils.determineGemReward(itemsUserReceives, rewardRetrieveUtils);
//
////			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
////					user, realLifeCashCost)) {
////				log.error(
////						"problem with logging in-app purchase history for receipt:{} and user {}",
////						receiptFromApple.toString(4), user);
////				success = false;
////			}
//			processStarterPackPurchase(resBuilder, itemsUserReceives);
//
//		} catch (Exception e) {
//			log.error(
//					String.format(
//							"error verifying InAppPurchase request. receiptFromApple=%s",
//							receiptFromApple), e);
//			success = false;
//		}
//		return success;
//	}
//
//	public List<BoosterItem> getItemsUserReceives() {
//		boosterPackId = ControllerConstants.IN_APP_PURCHASE__STARTER_PACK_BOOSTER_PACK_ID;
//		Map<Integer, BoosterItem> idToBoosterItem = boosterItemRetrieveUtils
//				.getBoosterItemIdsToBoosterItemsForBoosterPackId(boosterPackId);
//		if (null == idToBoosterItem || idToBoosterItem.isEmpty()) {
//			throw new RuntimeException(String.format(
//					"no starter pack for boosterPackId=%s", boosterPackId));
//		}
//
//		//TODO: clean up this copy paste of PurchaseBoosterPackController logic
//		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
//		itemsUserReceives.addAll(idToBoosterItem.values());
//		return itemsUserReceives;
//	}
//
//	public void processStarterPackPurchase(Builder resBuilder, List<BoosterItem> itemsUserReceives) {
//
//		boolean legit = boosterItemUtils.checkIfMonstersExist(itemsUserReceives, monsterRetrieveUtils);
//		if (!legit) {
//			throw new RuntimeException(String.format(
//					"illegal monster in starter pack for boosterPackId=%s",
//					boosterPackId));
//		}
//
//		//booster packs can give out gems, so  reuse processPurchase logic
//		processPurchase(resBuilder);
//
//		Map<Integer, Integer> monsterIdToNumPieces = new HashMap<Integer, Integer>();
//		List<MonsterForUser> completeUserMonsters = new ArrayList<MonsterForUser>();
//		//sop = source of pieces
//		String mfusop = BoosterItemUtils.createUpdateUserMonsterArguments(userId,
//				boosterPackId, itemsUserReceives, monsterIdToNumPieces,
//				completeUserMonsters, now, monsterLevelInfoRetrieveUtils,
//				monsterRetrieveUtils, monsterStuffUtils);
//
//		log.info("!!!!!!!!!mfusop={}", mfusop);
//		//this is if the user bought a complete monster, STORE TO DB THE NEW MONSTERS
//		if (!completeUserMonsters.isEmpty()) {
//			List<String> monsterForUserIds = insertUtil
//					.insertIntoMonsterForUserReturnIds(userId,
//							completeUserMonsters, mfusop, now);
//			List<FullUserMonsterProto> newOrUpdated = miscMethods
//					.createFullUserMonsterProtos(monsterForUserIds,
//							completeUserMonsters);
//
//			String preface = "YIIIIPEEEEE!. BOUGHT COMPLETE MONSTER(S)!";
//			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
//					new Object[] { preface, newOrUpdated, boosterPackId });
//			//set the builder that will be sent to the client
//			resBuilder.addAllUpdatedOrNew(newOrUpdated);
//		}
//
//		//this is if the user did not buy a complete monster, UPDATE DB
//		if (!monsterIdToNumPieces.isEmpty()) {
//			//assume things just work while updating user monsters
//			List<FullUserMonsterProto> newOrUpdated = monsterStuffUtils
//					.updateUserMonsters(userId, monsterIdToNumPieces, null,
//							mfusop, now, monsterLevelInfoRetrieveUtils);
//
//			String preface = "YIIIIPEEEEE!. BOUGHT INCOMPLETE MONSTER(S)!";
//			log.info("{} monster(s) newOrUpdated: {} \t bpackId={}",
//					new Object[] { preface, newOrUpdated, boosterPackId });
//			//set the builder that will be sent to the client
//			resBuilder.addAllUpdatedOrNew(newOrUpdated);
//		}
//
//		//item reward
//		List<ItemForUser> ifuList = BoosterItemUtils
//				.awardBoosterItemItemRewards(userId, itemsUserReceives,
//						itemForUserRetrieveUtil, updateUtil);
//		//		log.info("ifuList={}", ifuList);
//		if (null != ifuList && !ifuList.isEmpty()) {
//			List<UserItemProto> uipList = createInfoProtoUtils
//					.createUserItemProtosFromUserItems(ifuList);
//			resBuilder.addAllUpdatedUserItems(uipList);
//		}
//	}
//
//	public void processPurchase(Builder resBuilder) {
//		prevCurrencies = new HashMap<String, Integer>();
//
//		if (gemChange != 0) {
//			prevCurrencies.put(MiscMethods.gems, user.getGems());
//			resBuilder.setDiamondsGained(gemChange);
//			user.updateRelativeDiamondsBeginnerSale(gemChange, true);
//		}
//
//		prepCurrencyHistory();
//	}
//
//	public void prepCurrencyHistory() {
//		String gems = miscMethods.gems;
//
//		currencyDeltas = new HashMap<String, Integer>();
//		curCurrencies = new HashMap<String, Integer>();
//		curCurrencies.put(gems, user.getGems());
//		currencyDeltas.put(gems, gemChange);
//
//		reasonsForChanges = new HashMap<String, String>();
//		if (0 != gemChange) {
//			reasonsForChanges.put(gems,
//					ControllerConstants.UCHRFC__IN_APP_PURCHASE_STARTER_PACK);
//
//			details = new HashMap<String, String>();
//			details.put(gems, packageName);
//		}
//	}
//
//
//	public String getUserId() {
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public JSONObject getReceiptFromApple() {
//		return receiptFromApple;
//	}
//
//	public void setReceiptFromApple(JSONObject receiptFromApple) {
//		this.receiptFromApple = receiptFromApple;
//	}
//
//	public Date getNow() {
//		return now;
//	}
//
//	public void setNow(Date now) {
//		this.now = now;
//	}
//
//	public String getUuid() {
//		return uuid;
//	}
//
//	public void setUuid(String uuid) {
//		this.uuid = uuid;
//	}
//
//	public IAPHistoryRetrieveUtils getIapHistoryRetrieveUtil() {
//		return iapHistoryRetrieveUtil;
//	}
//
//	public void setIapHistoryRetrieveUtil(
//			IAPHistoryRetrieveUtils iapHistoryRetrieveUtil) {
//		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
//	}
//
//	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
//		return itemForUserRetrieveUtil;
//	}
//
//	public void setItemForUserRetrieveUtil(
//			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
//		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
//	}
//
//	public MonsterStuffUtils getMonsterStuffUtils() {
//		return monsterStuffUtils;
//	}
//
//	public void setMonsterStuffUtils(MonsterStuffUtils monsterStuffUtils) {
//		this.monsterStuffUtils = monsterStuffUtils;
//	}
//
//	public InsertUtil getInsertUtil() {
//		return insertUtil;
//	}
//
//	public void setInsertUtil(InsertUtil insertUtil) {
//		this.insertUtil = insertUtil;
//	}
//
//	public UpdateUtil getUpdateUtil() {
//		return updateUtil;
//	}
//
//	public void setUpdateUtil(UpdateUtil updateUtil) {
//		this.updateUtil = updateUtil;
//	}
//
//	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
//		return createInfoProtoUtils;
//	}
//
//	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
//		this.createInfoProtoUtils = createInfoProtoUtils;
//	}
//
//	public MiscMethods getMiscMethods() {
//		return miscMethods;
//	}
//
//	public void setMiscMethods(MiscMethods miscMethods) {
//		this.miscMethods = miscMethods;
//	}
//
//	public MonsterRetrieveUtils getMonsterRetrieveUtils() {
//		return monsterRetrieveUtils;
//	}
//
//	public void setMonsterRetrieveUtils(MonsterRetrieveUtils monsterRetrieveUtils) {
//		this.monsterRetrieveUtils = monsterRetrieveUtils;
//	}
//
//	public String getPackageName() {
//		return packageName;
//	}
//
//	public void setPackageName(String packageName) {
//		this.packageName = packageName;
//	}
//
//	public int getGemChange() {
//		return gemChange;
//	}
//
//	public void setGemChange(int gemChange) {
//		this.gemChange = gemChange;
//	}
//
//	public Map<String, Integer> getCurrencyDeltas() {
//		return currencyDeltas;
//	}
//
//	public void setCurrencyDeltas(Map<String, Integer> currencyDeltas) {
//		this.currencyDeltas = currencyDeltas;
//	}
//
//	public Map<String, Integer> getPrevCurrencies() {
//		return prevCurrencies;
//	}
//
//	public void setPrevCurrencies(Map<String, Integer> prevCurrencies) {
//		this.prevCurrencies = prevCurrencies;
//	}
//
//	public Map<String, Integer> getCurCurrencies() {
//		return curCurrencies;
//	}
//
//	public void setCurCurrencies(Map<String, Integer> curCurrencies) {
//		this.curCurrencies = curCurrencies;
//	}
//
//	public Map<String, String> getReasonsForChanges() {
//		return reasonsForChanges;
//	}
//
//	public void setReasonsForChanges(Map<String, String> reasonsForChanges) {
//		this.reasonsForChanges = reasonsForChanges;
//	}
//
//	public Map<String, String> getDetails() {
//		return details;
//	}
//
//	public void setDetails(Map<String, String> details) {
//		this.details = details;
//	}
//
//	public MonsterLevelInfoRetrieveUtils getMonsterLevelInfoRetrieveUtils() {
//		return monsterLevelInfoRetrieveUtils;
//	}
//
//	public void setMonsterLevelInfoRetrieveUtils(
//			MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils) {
//		this.monsterLevelInfoRetrieveUtils = monsterLevelInfoRetrieveUtils;
//	}
//
//
//
//}
