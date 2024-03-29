package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserCurrencyHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserCurrencyHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class TradeItemForSpeedUpsAction {
	private static Logger log = LoggerFactory.getLogger( TradeItemForSpeedUpsAction.class);

	@Autowired protected ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected UpdateUtil updateUtil;
	@Autowired protected UserDao userDao;
	@Autowired protected UserCurrencyHistoryDao userCurrencyHistoryDao;
	@Autowired private MiscMethods miscMethods;
	@Autowired protected HistoryUtils historyUtils;

	private String userId;
	private List<ItemForUserUsage> itemsUsed;
	private List<ItemForUser> nuUserItems;
	private int gemsSpent;

	/*
	public TradeItemForSpeedUpsAction(String userId,
			List<ItemForUserUsage> itemsUsed, List<ItemForUser> nuUserItems,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			int gemsSpent, MiscMethods miscMethods,
			HistoryUtils historyUtils) {

		super();
		this.userId = userId;
		this.itemsUsed = itemsUsed;
		this.nuUserItems = nuUserItems;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.gemsSpent = gemsSpent;
		this.miscMethods = miscMethods;
		this.historyUtils = historyUtils;
	}
	*/
	public void wire(
			String userId,
			List<ItemForUserUsage> itemsUsed,
			List<ItemForUser> nuUserItems,
			int gemsSpent)
	{
		this.userId = userId;
		this.itemsUsed = itemsUsed;
		this.nuUserItems = nuUserItems;
		this.gemsSpent = gemsSpent;
	}

	//	//encapsulates the return value from this Action Object
	//	static class TradeItemForSpeedUpsResource {
	//
	//
	//		public TradeItemForSpeedUpsResource() {
	//
	//		}
	//	}
	//
	//	public TradeItemForSpeedUpsResource execute() {
	//
	//	}

	//derived state
	private Map<Integer, Integer> itemIdToQuantityUsed;
	private Map<Integer, Integer> itemIdToNuQuantity;

	private UserPojo userPojo;

	private List<String> itemForUserUsageIds;
	private List<ItemForUserUsage> itemsUsedWithIds;
	private UserCurrencyHistoryPojo gemsUserCurrencyHistory;

	public void execute(Builder resBuilder) {
		setUpDaos();
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

	public void setUpDaos() {

	}

	private boolean verifySyntax(Builder resBuilder) {
		userPojo = userDao.fetchOneById(userId);
		if(userPojo.getGems() < gemsSpent) {
			log.error("user doesn't have enough gem! usergems = {}, gemsSpent={}",
					userPojo.getGems(), gemsSpent);
			return false;
		}
		if ((itemsUsed.isEmpty() || nuUserItems.isEmpty()) && gemsSpent == 0) {
			log.error("itemsUsed {} and gemsSpent {} are both empty", itemsUsed, gemsSpent);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
//		if(gemsSpent == 0) {
//
//
//		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prepCurrencyHistory();

		//record that user used items
		itemForUserUsageIds = insertUtil
				.insertIntoItemForUserUsageGetId(itemsUsed);
		log.info(String.format("the new ids: %s", itemForUserUsageIds));
		//update items to reflect being used
		updateUtil.updateItemForUser(nuUserItems);
		userPojo.setGems(userPojo.getGems() - gemsSpent);
		userDao.update(userPojo);

		insertCurrencyHistory();
		return true;
	}

	public List<ItemForUserUsage> getItemForUserUsages() {
		if (null == itemsUsedWithIds) {
			itemsUsedWithIds = new ArrayList<ItemForUserUsage>();
		}
		if (null != itemForUserUsageIds) {
			for (int index = 0; index < itemsUsed.size(); index++) {
				ItemForUserUsage ifuu = itemsUsed.get(index);
				String id = itemForUserUsageIds.get(index);
				ItemForUserUsage ifuuWithId = new ItemForUserUsage(id, ifuu);
				itemsUsedWithIds.add(ifuuWithId);
			}
		}
		return itemsUsedWithIds;
	}

	public void prepCurrencyHistory() {
		gemsUserCurrencyHistory = new UserCurrencyHistoryPojo();
		gemsUserCurrencyHistory.setResourceType(miscMethods.gems);
		gemsUserCurrencyHistory.setCurrencyBeforeChange(userPojo.getGems());
	}

	private void insertCurrencyHistory() {
		Date now = new Date();
		gemsUserCurrencyHistory.setCurrencyChange(-1 * gemsSpent);
		gemsUserCurrencyHistory.setCurrencyAfterChange(userPojo.getGems());
		List<UserCurrencyHistoryPojo> uchList = new ArrayList<UserCurrencyHistoryPojo>();
		uchList.add(gemsUserCurrencyHistory);
		String reasonForChange = ControllerConstants.UCHRFC__TRADE_ITEM_FOR_SPEEDUP;
		String details = "";
		for(ItemForUserUsage ifuu : itemsUsed) {
			details.concat(" itemId: " + ifuu.getItemId());
		}
		historyUtils.insertUserCurrencyHistory(userId, uchList, now, reasonForChange, details, userCurrencyHistoryDao);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<ItemForUserUsage> getItemsUsed() {
		return itemsUsed;
	}

	public void setItemsUsed(List<ItemForUserUsage> itemsUsed) {
		this.itemsUsed = itemsUsed;
	}

	public List<ItemForUser> getNuUserItems() {
		return nuUserItems;
	}

	public void setNuUserItems(List<ItemForUser> nuUserItems) {
		this.nuUserItems = nuUserItems;
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

	public int getGemsSpent() {
		return gemsSpent;
	}

	public void setGemsSpent(int gemsSpent) {
		this.gemsSpent = gemsSpent;
	}

	public Map<Integer, Integer> getItemIdToQuantityUsed() {
		return itemIdToQuantityUsed;
	}

	public void setItemIdToQuantityUsed(Map<Integer, Integer> itemIdToQuantityUsed) {
		this.itemIdToQuantityUsed = itemIdToQuantityUsed;
	}

	public Map<Integer, Integer> getItemIdToNuQuantity() {
		return itemIdToNuQuantity;
	}

	public void setItemIdToNuQuantity(Map<Integer, Integer> itemIdToNuQuantity) {
		this.itemIdToNuQuantity = itemIdToNuQuantity;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserPojo getUserPojo() {
		return userPojo;
	}

	public void setUserPojo(UserPojo userPojo) {
		this.userPojo = userPojo;
	}

	public List<String> getItemForUserUsageIds() {
		return itemForUserUsageIds;
	}

	public void setItemForUserUsageIds(List<String> itemForUserUsageIds) {
		this.itemForUserUsageIds = itemForUserUsageIds;
	}

	public List<ItemForUserUsage> getItemsUsedWithIds() {
		return itemsUsedWithIds;
	}

	public void setItemsUsedWithIds(List<ItemForUserUsage> itemsUsedWithIds) {
		this.itemsUsedWithIds = itemsUsedWithIds;
	}



}
