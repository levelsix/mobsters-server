package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
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
	private InAppPurchaseUtils inAppPurchaseUtils;
	private RewardRetrieveUtils rewardRetrieveUtils;
	private UserRetrieveUtils2 userRetrieveUtil;

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
			SalesPackage salesPackage, InAppPurchaseUtils inAppPurchaseUtils,
			RewardRetrieveUtils rewardRetrieveUtils,
			UserRetrieveUtils2 userRetrieveUtil) {
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
		this.inAppPurchaseUtils = inAppPurchaseUtils;
		this.rewardRetrieveUtils = rewardRetrieveUtils;
		this.userRetrieveUtil = userRetrieveUtil;
	}

	//derived state
	private double salesPackagePrice;
	private int gemChange;
	private boolean salesJumpTwoTiers;
	private List<Reward> listOfRewards;
	private AwardRewardAction ara;
	private boolean isStarterPack;
	private boolean isBuilderPack;
	private String packageName;
	private int salesValue;


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
		try {
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<SalesItem> salesItemList = salesItemRetrieveUtils.getSalesItemsForSalesPackageId(salesPackage.getId());
		if(salesItemList == null || salesItemList.isEmpty()) {
			log.error("no sales items for sales package {}", salesPackage);
			return false;
		}

		Map<Integer, Reward> idsToRewards = rewardRetrieveUtils.getRewardIdsToRewards();
		if(idsToRewards == null || idsToRewards.isEmpty()) {
			log.error("there are no rewards...at all");
			return false;
		}

		listOfRewards = new ArrayList<Reward>();
		for(SalesItem si : salesItemList) {
			Reward r = idsToRewards.get(si.getRewardId());
			listOfRewards.add(r);
		}
		if(listOfRewards.size() != salesItemList.size()) {
			log.error("did not properly convert all sales item to rewaards");
			return false;
		}

		return true;
	}

	public boolean verifySemantics(Builder resBuilder) {
		return verifySalesPackage(resBuilder);
	}

	public boolean verifySalesPackage(Builder resBuilder) {
		boolean duplicateReceipt = true;
		duplicateReceipt = inAppPurchaseUtils.checkIfDuplicateReceipt(receiptFromApple, iapHistoryRetrieveUtil);

		if(duplicateReceipt) {
			resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
		}

		isBuilderPack = false;
		if(IAPValues.packageIsBuilderPack(packageName) ||
				IAPValues.packageIsStarterBuilderPack(packageName)) {
			isBuilderPack = true;
			builderCheck();
		}

		if (duplicateReceipt || !saleIsWithinTimeConstraints()) {
			log.error("user should be buying more expensive sales package! {}",
					 user);
			return false;
		}

		return true;
	}

	public void builderCheck() {
		Reward builderReward = null;
		List<Integer> itemIdForBuilder = new ArrayList<Integer>();
		itemIdForBuilder.add(10000);
		List<ItemForUser> listOfUserItems = itemForUserRetrieveUtil.getSpecificOrAllItemForUser(userId, itemIdForBuilder);
		//something fucked up, bc if he has a builder item it means he alrdy bought builder pack!
		if(!listOfUserItems.isEmpty()) {
			for(Reward r : listOfRewards) {
				if(r.getStaticDataId() == 10000 && r.getType().equalsIgnoreCase("ITEM")) {
					builderReward = r;
				}
			}
			listOfRewards.remove(builderReward);
			log.error("removing builder from list of rewards bc user alrdy has a builder item");

		}
	}

	//when start and end time both null, it's for starter/builder packs, they dont expire
	public boolean saleIsWithinTimeConstraints() {
		Date saleStartTime = salesPackage.getTimeStart();
		Date saleEndTime = salesPackage.getTimeEnd();

		if(saleStartTime == null && saleEndTime == null) {
			return true;
		}

		if((now.getTime() - saleStartTime.getTime() > 0) && (saleEndTime.getTime() - now.getTime() > 0)) {
			return true;
		}
		log.error("sale didn't begin or is over, sale start time is {}, end time is {}",
				new Timestamp(saleStartTime.getTime()), new Timestamp(saleEndTime.getTime()) );
		return false;
	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {
			processSalesPackagePurchase(resBuilder);
			updateIfBeginnerPack();

			if(!salesPackageLessThanUserSalesValue()) {
				updateUserSalesValueAndLastPurchaseTime();
			}
			else {
				success = updateUtil.updateUserSalesValue(userId, 0, now);
			}
			
			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, 0,
					user, salesPackagePrice, salesPackage.getUuid())) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
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

	public void processSalesPackagePurchase(Builder resBuilder) {

		ara = new AwardRewardAction(userId, user, 0, 0, now, "sales package", listOfRewards,
				userRetrieveUtil, itemForUserRetrieveUtil, insertUtil, updateUtil, monsterStuffUtils,
				monsterLevelInfoRetrieveUtils);

		ara.execute();

	}

	public void updateIfBeginnerPack() {
		if(IAPValues.packageIsStarterPack(packageName) ||
				IAPValues.packageIsStarterBuilderPack(packageName)) {
			user.updateRelativeDiamondsBeginnerSale(0, true);
			isStarterPack = true;
		}
		else isStarterPack = false;
	}

	public boolean updateUserSalesValueAndLastPurchaseTime() {

		if(salesValue < 4)
			salesValue++;

		return updateUtil.updateUserSalesValue(userId, salesValue, now);

	}

	public boolean salesPackageLessThanUserSalesValue() {
		salesValue = user.getSalesValue();
//		salesJumpTwoTiers = user.isSalesJumpTwoTiers();
		salesPackagePrice = salesPackage.getPrice();
		if(salesValue == 0) {
			if(salesPackagePrice < 5)
				return true;
		}
		else if(salesValue == 1) {
			if(salesPackagePrice < 10) {
				return true;
			}
		}
		else if(salesValue == 2) {
			if(salesPackagePrice < 20) {
				return true;
			}
		}
		else if(salesValue == 3) {
			if(salesPackagePrice < 50) {
				return true;
			}
		}
		else if(salesValue >= 4) {
			if(salesPackagePrice < 100) {
				return true;
			}
		}
		else {
			return false;

		}
		log.info("the sale user is trying to buy has a price of: "
				+ salesPackagePrice + "and his salesValue is " + salesValue);
		return false;

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

	public AwardRewardAction getAra() {
		return ara;
	}

	public void setAra(AwardRewardAction ara) {
		this.ara = ara;
	}

	public boolean isStarterPack() {
		return isStarterPack;
	}

	public void setStarterPack(boolean isStarterPack) {
		this.isStarterPack = isStarterPack;
	}

	public boolean isBuilderPack() {
		return isBuilderPack;
	}

	public void setBuilderPack(boolean isBuilderPack) {
		this.isBuilderPack = isBuilderPack;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		InAppPurchaseSalesAction.log = log;
	}

	public InAppPurchaseUtils getInAppPurchaseUtils() {
		return inAppPurchaseUtils;
	}

	public void setInAppPurchaseUtils(InAppPurchaseUtils inAppPurchaseUtils) {
		this.inAppPurchaseUtils = inAppPurchaseUtils;
	}

	public RewardRetrieveUtils getRewardRetrieveUtils() {
		return rewardRetrieveUtils;
	}

	public void setRewardRetrieveUtils(RewardRetrieveUtils rewardRetrieveUtils) {
		this.rewardRetrieveUtils = rewardRetrieveUtils;
	}

	public UserRetrieveUtils2 getUserRetrieveUtil() {
		return userRetrieveUtil;
	}

	public void setUserRetrieveUtil(UserRetrieveUtils2 userRetrieveUtil) {
		this.userRetrieveUtil = userRetrieveUtil;
	}

	public List<Reward> getListOfRewards() {
		return listOfRewards;
	}

	public void setListOfRewards(List<Reward> listOfRewards) {
		this.listOfRewards = listOfRewards;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getSalesValue() {
		return salesValue;
	}

	public void setSalesValue(int salesValue) {
		this.salesValue = salesValue;
	}



}
