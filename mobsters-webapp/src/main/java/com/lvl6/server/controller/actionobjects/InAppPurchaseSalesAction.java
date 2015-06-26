
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.SalesItem;
import com.lvl6.info.SalesPackage;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRewardRetrieveUtils;
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

@Component@Scope("prototype")public class InAppPurchaseSalesAction {

	private static final Logger log = LoggerFactory
			.getLogger(InAppPurchaseSalesAction.class);

	private String userId;
	private User user;
	private JSONObject receiptFromApple;
	private Date now;
	private String uuid;
	@Autowired protected IAPHistoryRetrieveUtils iapHistoryRetrieveUtil; 
	@Autowired protected GiftRetrieveUtils giftRetrieveUtil; 
	@Autowired protected GiftRewardRetrieveUtils giftRewardRetrieveUtils; 
	@Autowired protected ItemForUserRetrieveUtil itemForUserRetrieveUtil; 
	@Autowired protected MonsterStuffUtils monsterStuffUtils; 
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected UpdateUtil updateUtil;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 
	private MiscMethods miscMethods;
	@Autowired protected SalesPackageRetrieveUtils salesPackageRetrieveUtils; 
	@Autowired protected SalesItemRetrieveUtils salesItemRetrieveUtils; 
	@Autowired protected MonsterRetrieveUtils monsterRetrieveUtils; 
	@Autowired protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils; 
	private SalesPackage salesPackage;
	@Autowired protected InAppPurchaseUtils inAppPurchaseUtils; 
	@Autowired protected RewardRetrieveUtils rewardRetrieveUtils; 
	@Autowired protected UserClanRetrieveUtils2 userClanRetrieveUtils; 
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 

	public InAppPurchaseSalesAction() {
		super();
	}

	public InAppPurchaseSalesAction(String userId, User user,
			JSONObject receiptFromApple, Date now,
			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
			GiftRetrieveUtils giftRetrieveUtil,
			GiftRewardRetrieveUtils giftRewardRetrieveUtils,
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
			UserClanRetrieveUtils2 userClanRetrieveUtils,
			UserRetrieveUtils2 userRetrieveUtil) {
		super();
		this.userId = userId;
		this.user = user;
		this.receiptFromApple = receiptFromApple;
		this.now = now;
		this.uuid = uuid;
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
		this.giftRetrieveUtil = giftRetrieveUtil;
		this.giftRewardRetrieveUtils = giftRewardRetrieveUtils;
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
		this.userClanRetrieveUtils = userClanRetrieveUtils;
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
	private double realLifeCashCost;


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

	public boolean verifySyntax(Builder resBuilder) {
		try {
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
		} catch (JSONException e) {
			log.error("receiptFromApple.getString() error", e);
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
			resBuilder.setStatus(ResponseStatus.FAIL_DUPLICATE_RECEIPT);
		}

		isBuilderPack = false;
		if(IAPValues.packageIsBuilderPack(packageName) ||
				IAPValues.packageIsStarterBuilderPack(packageName)) {
			isBuilderPack = true;
			builderCheck();
		}

		if(!saleIsWithinTimeConstraints()) {
			log.error("this could be result of buying high roller pack, sales package being bought when "
					+ "outside of start/end times. userId = {} , salespackageId = {}", userId, salesPackage.getId());
		}

		if (duplicateReceipt) {
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

			realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);
			if(!salesPackageLessThanUserSalesValue()) {
				updateUserSalesValueAndLastPurchaseTime();
			}
			else {
				success = updateUtil.updateUserSalesValue(userId, 0, now);
			}

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, 0,
					user, realLifeCashCost, salesPackage.getUuid())) {
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

		String awardReasonDetail = "sales pack id: " + salesPackage.getId();
		ara = new AwardRewardAction(userId, user, 0, 0, now, "sales package",
				listOfRewards, userRetrieveUtil, itemForUserRetrieveUtil,
				insertUtil, updateUtil, monsterStuffUtils,
				monsterLevelInfoRetrieveUtils, giftRetrieveUtil,
				giftRewardRetrieveUtils,
				rewardRetrieveUtils, userClanRetrieveUtils,
				createInfoProtoUtils, awardReasonDetail);

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
			if(salesPackagePrice < 10 || isBuilderPack) {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public ItemForUserRetrieveUtil getItemForUserRetrieveUtil() {
		return itemForUserRetrieveUtil;
	}

	public void setItemForUserRetrieveUtil(
			ItemForUserRetrieveUtil itemForUserRetrieveUtil) {
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
	}

	public void setUpdateUtil(UpdateUtil updateUtil) {
		this.updateUtil = updateUtil;
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

	public int getGemChange() {
		return gemChange;
	}

	public boolean isSalesJumpTwoTiers() {
		return salesJumpTwoTiers;
	}

	public AwardRewardAction getAra() {
		return ara;
	}

	public boolean isStarterPack() {
		return isStarterPack;
	}

	public boolean isBuilderPack() {
		return isBuilderPack;
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

	public int getSalesValue() {
		return salesValue;
	}

	public void setSalesValue(int salesValue) {
		this.salesValue = salesValue;
	}

}
