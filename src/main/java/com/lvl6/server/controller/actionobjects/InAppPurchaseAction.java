package com.lvl6.server.controller.actionobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private JSONObject receiptFromApple;
	private Date now;
	private String uuid;
	private IAPHistoryRetrieveUtils iapHistoryRetrieveUtil;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private MiscMethods miscMethods;


	public InAppPurchaseAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InAppPurchaseAction(String userId, User user,
			JSONObject receiptFromApple, Date now,
			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			CreateInfoProtoUtils createInfoProtoUtils,
			MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.user = user;
		this.receiptFromApple = receiptFromApple;
		this.now = now;
		this.uuid = uuid;
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
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
	private String packageName;
	private int gemChange;

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
		return verifyPurchase(resBuilder);
	}

	public boolean verifyPurchase(Builder resBuilder) {
		boolean duplicateReceipt = true;
		duplicateReceipt = InAppPurchaseUtils.checkIfDuplicateReceipt(receiptFromApple, iapHistoryRetrieveUtil);

		if(duplicateReceipt) {
			resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
		}

		if (duplicateReceipt) {
			log.error("user trying to buy the starter pack again! {}, {}",
					packageName, user);
			return false;
		}
		return true;
	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {

			double realLifeCashCost;
			realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);

			gemChange = IAPValues.getDiamondsForPackageName(packageName);

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
					user, realLifeCashCost)) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
				success = false;
			}

			processPurchase(resBuilder);

		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}

		return success;
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


	public void processPurchase(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if (gemChange != 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			resBuilder.setDiamondsGained(gemChange);
			user.updateRelativeCashAndOilAndGems(0, 0, gemChange);
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
		reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__IN_APP_PURCHASE);

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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getGemChange() {
		return gemChange;
	}

	public void setGemChange(int gemChange) {
		this.gemChange = gemChange;
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
