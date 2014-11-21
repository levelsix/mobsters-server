package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Item;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto.Builder;
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto.TradeItemForResourcesStatus;
import com.lvl6.proto.ItemsProto.ItemType;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class TradeItemForResourcesAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<Integer> itemIdsUsed;
	private List<ItemForUser> nuUserItems;
	private int maxCash;
	private int maxOil;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private UserRetrieveUtils2 userRetrieveUtil;
	private UpdateUtil updateUtil;

	public TradeItemForResourcesAction(
		String userId,
		List<Integer> itemIdsUsed,
		List<ItemForUser> nuUserItems,
		int maxCash,
		int maxOil,
		ItemForUserRetrieveUtil itemForUserRetrieveUtil,
		UserRetrieveUtils2 userRetrieveUtil,
		UpdateUtil updateUtil )
	{
		super();
		this.userId = userId;
		this.itemIdsUsed = itemIdsUsed;
		this.nuUserItems = nuUserItems;
		this.maxCash = maxCash;
		this.maxOil = maxOil;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.userRetrieveUtil = userRetrieveUtil;
		this.updateUtil = updateUtil;
	}


	//	//encapsulates the return value from this Action Object
	//	static class TradeItemForResourcesResource {
	//		
	//		
	//		public TradeItemForResourcesResource() {
	//			
	//		}
	//	}
	//
	//	public TradeItemForResourcesResource execute() {
	//		
	//	}

	//derived state
	private Map<Integer, Integer> itemIdToQuantityUsed;
	private Map<Integer,Integer> itemIdToNuQuantity;
	private int gemsGained;
	private int cashGained;
	private int oilGained;
	private User user;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(TradeItemForResourcesStatus.FAIL_OTHER);

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

		resBuilder.setStatus(TradeItemForResourcesStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (itemIdsUsed.isEmpty() || nuUserItems.isEmpty()) {
			log.error(String.format(
				"invalid itemIdsUsed=%s or nuUserItems=%s. At least one is empty.",
				itemIdsUsed, nuUserItems));
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		itemIdToQuantityUsed = new HashMap<Integer, Integer>();

		//mapify itemIdsUsed to make access easier later on
		for (Integer itemId :  itemIdsUsed) {
			if (!itemIdToQuantityUsed.containsKey(itemId)) {
				itemIdToQuantityUsed.put(itemId, 0);
			}

			int quantity = itemIdToQuantityUsed.get(itemId);
			quantity += 1;
			itemIdToQuantityUsed.put(itemId, quantity);
			
			//aggregate the amount of resources to give user
			Item item = ItemRetrieveUtils.getItemForId(itemId);
			
			String itemType = item.getItemType();
			if (ItemType.ITEM_CASH.name().equals(itemType)) {
				cashGained += item.getAmount();
			} else if (ItemType.ITEM_OIL.name().equals(itemType)) {
				oilGained += item.getAmount();
			} else {
				log.error(String.format(
					"illegal itemType (not a resource): %s",
					itemType));
				return false;
			}
		}

		//mapify nuUserItems to make access easier later on
		itemIdToNuQuantity = new HashMap<Integer, Integer>();
		for (ItemForUser ifu : nuUserItems) {
			int itemId = ifu.getItemId();
			itemIdToNuQuantity.put(itemId, ifu.getQuantity());	
		}


		//get current ItemForUser data, calculate if user is spending
		//correct amount
		Map<Integer, ItemForUser> inDb = itemForUserRetrieveUtil
			.getSpecificOrAllItemIdToItemForUserId(
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
					"client using more items than has. itemIdsUsed=%s, inDbUserItems=%s",
					itemIdsUsed, inDb));
				return false;
			}
			
			int clientExpectedQuantity = itemIdToNuQuantity.get(itemId);

			if (actualQuantity != clientExpectedQuantity) {
				log.error(String.format(
					"itemIdsUsed=%s, nuUserItems(userItem quantities)=%s, inDbUserItems=%s",
					itemIdsUsed, nuUserItems, inDb));
				return false;
			}
		}

		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error(String.format(
				"no user with id=%s", userId));
			return false;
		}
		
		gemsGained = 0;
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {

		//update items to reflect being used
		int numUpdated = updateUtil.updateItemForUser(nuUserItems);
		log.info(String.format(
			"itemForUser numUpdated: %s, nuUserItems=%s",
			numUpdated, nuUserItems));
		
		//give user the resources
		log.info(String.format(
			"user before: %s \t\t", user));
		user.updateRelativeCashAndOilAndGems(cashGained, oilGained, 0);
		log.info(String.format(
			"user after: %s", user));
		return true;
	}

}
