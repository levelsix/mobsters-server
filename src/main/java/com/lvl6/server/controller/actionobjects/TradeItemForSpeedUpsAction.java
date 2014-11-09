package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.Builder;
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto.TradeItemForSpeedUpsStatus;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class TradeItemForSpeedUpsAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private int userId;
	private List<ItemForUserUsage> itemsUsed;
	private List<ItemForUser> nuUserItems;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	
	public TradeItemForSpeedUpsAction(
		int userId,
		List<ItemForUserUsage> itemsUsed,
		List<ItemForUser> nuUserItems,
		InsertUtil insertUtil,
		UpdateUtil updateUtil )
	{
		super();
		this.userId = userId;
		this.itemsUsed = itemsUsed;
		this.nuUserItems = nuUserItems;
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
	private List<Long> itemForUserUsageIds;
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
				long id = itemForUserUsageIds.get(index);
				
				ItemForUserUsage ifuuWithId = new ItemForUserUsage(id, ifuu);
				
				itemsUsedWithIds.add(ifuuWithId);
			}
		}
		
		return itemsUsedWithIds;
	}

}
