package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.User;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.Builder;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.TradeItemForSpeedUpsStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class TradeItemForSpeedUpsAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<ItemForUserUsage> itemsUsed;
	private List<ItemForUser> nuUserItems;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	private int gemsSpent;

	public TradeItemForSpeedUpsAction(String userId,
			List<ItemForUserUsage> itemsUsed, List<ItemForUser> nuUserItems,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil,
			int gemsSpent) {
		super();
		this.userId = userId;
		this.itemsUsed = itemsUsed;
		this.nuUserItems = nuUserItems;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
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
	private UserDao userDao;
	private User userPojo;

	private List<String> itemForUserUsageIds;
	private List<ItemForUserUsage> itemsUsedWithIds;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(TradeItemForSpeedUpsStatus.FAIL_OTHER);

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

		resBuilder.setStatus(TradeItemForSpeedUpsStatus.SUCCESS);

	}
	
	public void setUpDaos() {
		Configuration config = new DefaultConfiguration().set(DBConnection.get()
				.getConnection()).set(SQLDialect.MYSQL);
		userDao = new UserDao(config);
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
		itemIdToQuantityUsed = new HashMap<Integer, Integer>();

		for (ItemForUserUsage ifuu : itemsUsed) {
			int itemId = ifuu.getItemId();
			if (!itemIdToQuantityUsed.containsKey(itemId)) {
				itemIdToQuantityUsed.put(itemId, 0);
			}

			int quantity = itemIdToQuantityUsed.get(itemId);
			quantity += 1;
			itemIdToQuantityUsed.put(itemId, quantity);
		}

		itemIdToNuQuantity = new HashMap<Integer, Integer>();
		for (ItemForUser ifu : nuUserItems) {
			int itemId = ifu.getItemId();
			itemIdToNuQuantity.put(itemId, ifu.getQuantity());
		}

		//get current ItemForUser data, calculate if user is spending
		//correct amount
		Map<Integer, ItemForUser> inDb = itemForUserRetrieveUtil
				.getSpecificOrAllItemForUserMap(userId,
						itemIdToQuantityUsed.keySet());

		if(gemsSpent == 0) {
			if (null == inDb || inDb.size() != nuUserItems.size()) {
				log.info(String.format("inconsistent itemForUser"));
				return false;
			}
			for (Integer itemId : inDb.keySet()) {
				ItemForUser ifu = inDb.get(itemId);

				//safe to access because retrieved by itemIdToQuantityUsed.keySet()
				int quantitySpent = itemIdToQuantityUsed.get(itemId);
				int actualQuantity = ifu.getQuantity() - quantitySpent;

				if (actualQuantity < 0) {
					log.error(String
							.format("client using more items than has. itemsUsed=%s, inDbUserItems=%s",
									itemsUsed, inDb));
					return false;
				}

				int clientExpectedQuantity = itemIdToNuQuantity.get(itemId);

				if (actualQuantity != clientExpectedQuantity) {
					log.error(String
							.format("itemsUsed=%s, nuUserItems(userItem quantities)=%s, inDbUserItems=%s",
									itemsUsed, nuUserItems, inDb));
					return false;
				}
			}
		}
		
		//TODO: consider accessing db to get all the ItemForUser that changed,
		//compare db ItemForUser with the nuUserItems.
		//calculating the quantity that changed, then making sure 
		//itemsUsed.quantity() is greater than or equal to the calculated
		//difference.
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		//record that user used items
		itemForUserUsageIds = insertUtil
				.insertIntoItemForUserUsageGetId(itemsUsed);
		log.info(String.format("the new ids: %s", itemForUserUsageIds));
		//update items to reflect being used
		updateUtil.updateItemForUser(nuUserItems);
		userPojo.setGems(userPojo.getGems() - gemsSpent);
		userDao.update(userPojo);
		
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

	public User getUserPojo() {
		return userPojo;
	}

	public void setUserPojo(User userPojo) {
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
