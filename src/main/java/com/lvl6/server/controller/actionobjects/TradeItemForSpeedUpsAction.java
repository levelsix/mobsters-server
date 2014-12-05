package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.Builder;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.TradeItemForSpeedUpsStatus;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class TradeItemForSpeedUpsAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<ItemForUserUsage> itemsUsed;
	private List<ItemForUser> nuUserItems;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil; 
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	
	public TradeItemForSpeedUpsAction(
		String userId,
		List<ItemForUserUsage> itemsUsed,
		List<ItemForUser> nuUserItems,
		ItemForUserRetrieveUtil itemForUserRetrieveUtil,
		InsertUtil insertUtil,
		UpdateUtil updateUtil )
	{
		super();
		this.userId = userId;
		this.itemsUsed = itemsUsed;
		this.nuUserItems = nuUserItems;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
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
	private Map<Integer,Integer> itemIdToNuQuantity;
	
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
	
	private boolean verifySyntax(Builder resBuilder) {
		
		if (itemsUsed.isEmpty() || nuUserItems.isEmpty()) {
			log.error(String.format(
				"invalid itemsUsed=%s or nuUserItems=%s. At least one is empty.",
				itemsUsed, nuUserItems));
			return false;
		}
		
		return true;
	}
	
	private boolean verifySemantics(Builder resBuilder) {
		itemIdToQuantityUsed = new HashMap<Integer, Integer>();
		
		for (ItemForUserUsage ifuu :  itemsUsed) {
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
			.getSpecificOrAllItemForUserMap(
				userId,
				itemIdToQuantityUsed.keySet());
		
		if (null == inDb || inDb.size() != nuUserItems.size()) {
			log.info(String.format(
				"inconsistent itemForUser"));
			return false;
		}
		
		for (Integer itemId : inDb.keySet()) {
			ItemForUser ifu = inDb.get(itemId);
			
			//safe to access because retrieved by itemIdToQuantityUsed.keySet()
			int quantitySpent = itemIdToQuantityUsed.get(itemId);
			int actualQuantity = ifu.getQuantity() - quantitySpent;
			
			if (actualQuantity < 0) {
				log.error(String.format(
					"client using more items than has. itemsUsed=%s, inDbUserItems=%s",
					itemsUsed, inDb));
				return false;
			}
			
			int clientExpectedQuantity = itemIdToNuQuantity.get(itemId);
			
			if (actualQuantity != clientExpectedQuantity) {
				log.error(String.format(
					"itemsUsed=%s, nuUserItems(userItem quantities)=%s, inDbUserItems=%s",
					itemsUsed, nuUserItems, inDb));
				return false;
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
		itemForUserUsageIds = insertUtil.insertIntoItemForUserUsageGetId(itemsUsed);
		log.info(String.format("the new ids: %s", itemForUserUsageIds));
		//update items to reflect being used
		updateUtil.updateItemForUser(nuUserItems);
		
		return true;
	}

	public List<ItemForUserUsage> getItemForUserUsages() {
		if (null == itemsUsedWithIds) {
			itemsUsedWithIds = new ArrayList<ItemForUserUsage>();
		}
		
		if (null != itemForUserUsageIds) {
			for (int index = 0; index < itemsUsed.size(); index++) {
				ItemForUserUsage ifuu  = itemsUsed.get(index);
				String id = itemForUserUsageIds.get(index);
				
				ItemForUserUsage ifuuWithId = new ItemForUserUsage(id, ifuu);
				
				itemsUsedWithIds.add(ifuuWithId);
			}
		}
		
		return itemsUsedWithIds;
	}

}
