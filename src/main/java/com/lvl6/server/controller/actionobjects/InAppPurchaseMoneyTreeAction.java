package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.CoordinatePair;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.retrieveutils.IAPHistoryRetrieveUtils;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class InAppPurchaseMoneyTreeAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private JSONObject receiptFromApple;
	private Date now;
	private String uuid;
	private IAPHistoryRetrieveUtils iapHistoryRetrieveUtil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	private CreateInfoProtoUtils createInfoProtoUtils;
	private MiscMethods miscMethods;
	private StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;
	private StructureForUserRetrieveUtils2 structureForUserRetrieveUtils;
	private InAppPurchaseUtils inAppPurchaseUtils;

	public StructureMoneyTreeRetrieveUtils getStructureMoneyTreeRetrieveUtils() {
		return structureMoneyTreeRetrieveUtils;
	}

	public void setStructureMoneyTreeRetrieveUtils(
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils) {
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
	}

	public StructureForUserRetrieveUtils2 getStructureForUserRetrieveUtils() {
		return structureForUserRetrieveUtils;
	}

	public void setStructureForUserRetrieveUtils(
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils) {
		this.structureForUserRetrieveUtils = structureForUserRetrieveUtils;
	}

	public InAppPurchaseMoneyTreeAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InAppPurchaseMoneyTreeAction(String userId, User user,
			JSONObject receiptFromApple, Date now,
			String uuid, IAPHistoryRetrieveUtils iapHistoryRetrieveUtil,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			CreateInfoProtoUtils createInfoProtoUtils,
			MiscMethods miscMethods,
			StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils,
			StructureForUserRetrieveUtils2 structureForUserRetrieveUtils,
			InAppPurchaseUtils inAppPurchaseUtils) {
		super();
		this.userId = userId;
		this.user = user;
		this.receiptFromApple = receiptFromApple;
		this.now = now;
		this.uuid = uuid;
		this.iapHistoryRetrieveUtil = iapHistoryRetrieveUtil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.miscMethods = miscMethods;
		this.structureMoneyTreeRetrieveUtils = structureMoneyTreeRetrieveUtils;
		this.structureForUserRetrieveUtils = structureForUserRetrieveUtils;
		this.inAppPurchaseUtils = inAppPurchaseUtils;
	}

	//derived state
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

	public boolean verifySyntax(Builder resBuilder) {

		return true;
	}

	public boolean verifySemantics(Builder resBuilder) {
		return verifyMoneyTree(resBuilder);
	}

	public boolean verifyMoneyTree(Builder resBuilder) {
		try {
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean duplicateReceipt = true;
		duplicateReceipt = inAppPurchaseUtils.checkIfDuplicateReceipt(receiptFromApple, iapHistoryRetrieveUtil);

		if(duplicateReceipt) {
			resBuilder.setStatus(InAppPurchaseStatus.DUPLICATE_RECEIPT);
		}

		if (duplicateReceipt || !userOwnsOneMoneyTreeMax()) {
			log.error("user trying to buy the starter pack again! {}, {}",
					packageName, user);
			return false;
		}
		return true;
	}

	public boolean userOwnsOneMoneyTreeMax() {
		Map<Integer, StructureMoneyTree> structIdsToMoneyTreesMap = structureMoneyTreeRetrieveUtils
				.getStructIdsToMoneyTrees();

		for(Integer structId : structIdsToMoneyTreesMap.keySet()) {
			smt = structIdsToMoneyTreesMap.get(structId);
		}

		List<StructureForUser> sfuList = structureForUserRetrieveUtils
				.getUserStructsForUser(userId);
		int numOfMoneyTrees = 0;

		for (StructureForUser sfu : sfuList) {
			int structId = sfu.getStructId();
			for (Integer ids : structIdsToMoneyTreesMap.keySet()) {
				if (structId == ids) {
					numOfMoneyTrees++;
				}
			}
		}

		if(numOfMoneyTrees <= 1) {
			return true;
		}
		else {
			log.info("num of trees: " + numOfMoneyTrees);
			return false;
		}
	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {
			double realLifeCashCost;
			realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);

			gemChange = 0;

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
					user, realLifeCashCost)) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
				success = false;
			}
			processMoneyTreePurchase(resBuilder);

		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}
		return success;
	}

	public void processMoneyTreePurchase(Builder resBuilder) {

		//assumed to only contain one money tree max for now
		List<StructureForUser> listOfUsersMoneyTree = structureForUserRetrieveUtils
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

		prepCurrencyHistory();

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

	public void prepCurrencyHistory() {
		String gems = miscMethods.gems;

		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		curCurrencies.put(gems, user.getGems());
		currencyDeltas.put(gems, gemChange);

		reasonsForChanges = new HashMap<String, String>();
		reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__IN_APP_PURCHASE_STARTER_PACK);

		details = new HashMap<String, String>();
		details.put(gems, packageName);

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

	public StructureMoneyTree getSmt() {
		return smt;
	}

	public void setSmt(StructureMoneyTree smt) {
		this.smt = smt;
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


}
