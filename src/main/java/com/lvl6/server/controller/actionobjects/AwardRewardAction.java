package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.Reward;
import com.lvl6.info.User;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.retrieveutils.ItemForUserRetrieveUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class AwardRewardAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private Date now;
	private Collection<Reward> rewards;
	private ItemForUserRetrieveUtil itemForUserRetrieveUtil;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;

	//TODO: Figure out a way to not have all these arguments as a requirement
	public AwardRewardAction(String userId, Date now,
			Collection<Reward> rewards,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil) {
		super();
		this.userId = userId;
		this.now = now;
		this.rewards = rewards;
		this.itemForUserRetrieveUtil = itemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveMiniEventResource {
	//
	//
	//		public RetrieveMiniEventResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveMiniEventResource execute() {
	//
	//	}

	//optional settable state
	private User u;

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}


	//derived state
	private List<ItemForUser> nuOrUpdatedItems;

	public boolean execute() {

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

//		boolean valid = verifySemantics();

//		if (!valid) {
//			return false;
//		}

		boolean success = writeChangesToDB();
		if (!success) {
			return false;
		}

		return true;
	}

//	private boolean verifySyntax(Builder resBuilder) {
//
//		return true;
//	}

//	private boolean verifySemantics() {
//
//		if (null != u && null != userRetrieveUtil) {
//			u = userRetrieveUtil.getUserById(userId);
//		}
//
//		if (null == u) {
//			log.error("no user with id={}", userId);
//			return false;
//		}
//
//		return true;
//	}

	private boolean writeChangesToDB() {

		//aggregate like Rewards
		Map<Integer, Integer> itemIdToQuantity = new HashMap<Integer, Integer>();

		for (Reward r : rewards) {
			String type = r.getType();
			int staticDataId = r.getStaticDataId();

			if (RewardType.ITEM.name().equals(type)) {
				int nuAmt = r.getAmt();
				if (itemIdToQuantity.containsKey(staticDataId)) {
					nuAmt += itemIdToQuantity.get(staticDataId);
				}
				itemIdToQuantity.put(staticDataId, nuAmt);
			} else {
				log.warn("no implementation for awarding {}", r);
			}

		}

		awardItems(itemIdToQuantity);


		return true;
	}

	private void awardItems(Map<Integer, Integer> itemIdToQuantity) {
		if (!itemIdToQuantity.isEmpty()) {
			nuOrUpdatedItems = calculateItemRewards(userId,
					itemForUserRetrieveUtil, itemIdToQuantity);
		}
		if (null != nuOrUpdatedItems && !nuOrUpdatedItems.isEmpty()) {
			int numUpdated = updateUtil.updateItemForUser(nuOrUpdatedItems);
			log.info("items numUpdated={}", numUpdated);
		}
	}

	//TODO: Get rid of copy in PurchaseBoosterPackAction
	public static List<ItemForUser> calculateItemRewards(String userId,
			ItemForUserRetrieveUtil itemForUserRetrieveUtil,
			Map<Integer, Integer> itemIdToQuantity) {
		List<ItemForUser> ifuList = null;
		if (!itemIdToQuantity.isEmpty()) {
			//aggregate rewarded items with user's current items
			Map<Integer, ItemForUser> itemIdToIfu = itemForUserRetrieveUtil
					.getSpecificOrAllItemForUserMap(userId,
							itemIdToQuantity.keySet());

			for (Integer itemId : itemIdToQuantity.keySet()) {
				int newQuantity = itemIdToQuantity.get(itemId);

				ItemForUser ifu = null;
				if (itemIdToIfu.containsKey(itemId)) {
					ifu = itemIdToIfu.get(itemId);
				} else {
					//user might not have the item
					ifu = new ItemForUser(userId, itemId, 0);
					itemIdToIfu.put(itemId, ifu);
				}

				newQuantity += ifu.getQuantity();
				ifu.setQuantity(newQuantity);
			}

			ifuList = new ArrayList<ItemForUser>(itemIdToIfu.values());
		}
		return ifuList;
	}

	public List<ItemForUser> getNuOrUpdatedItems() {
		return nuOrUpdatedItems;
	}

	public void setNuOrUpdatedItems(List<ItemForUser> nuOrUpdatedItems) {
		this.nuOrUpdatedItems = nuOrUpdatedItems;
	}

}
