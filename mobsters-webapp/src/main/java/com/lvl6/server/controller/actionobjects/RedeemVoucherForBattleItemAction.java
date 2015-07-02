package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.BattleItemForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserBattleItemHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.BattleItemForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserBattleItemHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.proto.EventItemProto.RedeemVoucherForBattleItemResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;

@Component
@Scope("prototype")
public class RedeemVoucherForBattleItemAction {
	private static Logger log = LoggerFactory.getLogger( RedeemVoucherForBattleItemAction.class);

	private String userId;
	private List<Integer> itemIdsUsed;
	private List<BattleItemForUserPojo> newBattleItemForUser;
	@Autowired protected UserDao userDao; 
	@Autowired protected BattleItemForUserDao bifuDao;
	@Autowired protected DeleteUtil deleteUtil; 
	private MiscMethods miscMethods;
	@Autowired protected HistoryUtils historyUtils; 
	@Autowired protected UserBattleItemHistoryDao ubihDao;
	private Date clientTime;
	
	public RedeemVoucherForBattleItemAction(String userId,
			List<Integer> itemIdsUsed, List<BattleItemForUserPojo> newBattleItemForUser,
			ItemRetrieveUtils itemRetrieveUtils,
			UserDao userDao, BattleItemForUserDao bifuDao,
			UserRetrieveUtils2 userRetrieveUtil, DeleteUtil deleteUtil,
			HistoryUtils historyUtils, UserBattleItemHistoryDao ubihDao,
			Date clientTime) {
		super();
		this.userId = userId;
		this.itemIdsUsed = itemIdsUsed;
		this.newBattleItemForUser = newBattleItemForUser;
		this.userDao = userDao;
		this.bifuDao = bifuDao;
		this.deleteUtil = deleteUtil;
		this.historyUtils = historyUtils;
		this.ubihDao = ubihDao;
		this.clientTime = clientTime;
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
	private UserPojo user;


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
	
	private boolean verifySyntax(Builder resBuilder) {
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		itemIdToQuantityUsed = new HashMap<Integer, Integer>();
		for (Integer itemId : itemIdsUsed) {
			if(itemIdToQuantityUsed.containsKey(itemId)) {
				itemIdToQuantityUsed.put(itemId, itemIdToQuantityUsed.get(itemId) + 1);
			}
			else itemIdToQuantityUsed.put(itemId, 1);
		}
		
		if(itemIdsUsed.size() != newBattleItemForUser.size()) {
			log.error("number of vouchers used doesn't equal number of battle items made");
			return false;
		}

		user = userDao.fetchOneById(userId);
		if (null == user) {
			log.error("no user with id={}", userId);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		if(newBattleItemForUser != null && !newBattleItemForUser.isEmpty()) {
			bifuDao.insert(newBattleItemForUser);
		}
		
		int numDeleted = deleteUtil.deleteUserItems(userId, itemIdsUsed);
		if(numDeleted != itemIdsUsed.size()) {
			log.error("did not delete all user items, itemIds {}", itemIdsUsed);
			return false;
		}
		saveToBattleItemHistory();
		return true;
	}
	
	public void saveToBattleItemHistory() {
		List<UserBattleItemHistoryPojo> ubihList = new ArrayList<UserBattleItemHistoryPojo>();
		for(BattleItemForUserPojo bifu : newBattleItemForUser) {
			ubihList.add(historyUtils.createBattleItemHistory(bifu.getUserId(), bifu.getBattleItemId(), 
					new Timestamp(clientTime.getTime()), true, false, "redeemed voucher", "voucher ids:" + itemIdsUsed));
		}
		ubihDao.insert(ubihList);
	}
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<Integer> getItemIdsUsed() {
		return itemIdsUsed;
	}

	public void setItemIdsUsed(List<Integer> itemIdsUsed) {
		this.itemIdsUsed = itemIdsUsed;
	}

	public List<BattleItemForUserPojo> getNewBattleItemForUser() {
		return newBattleItemForUser;
	}

	public void setNewBattleItemForUser(
			List<BattleItemForUserPojo> newBattleItemForUser) {
		this.newBattleItemForUser = newBattleItemForUser;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public BattleItemForUserDao getBifuDao() {
		return bifuDao;
	}

	public void setBifuDao(BattleItemForUserDao bifuDao) {
		this.bifuDao = bifuDao;
	}

	public DeleteUtil getDeleteUtil() {
		return deleteUtil;
	}

	public void setDeleteUtil(DeleteUtil deleteUtil) {
		this.deleteUtil = deleteUtil;
	}

	public MiscMethods getMiscMethods() {
		return miscMethods;
	}

	public void setMiscMethods(MiscMethods miscMethods) {
		this.miscMethods = miscMethods;
	}

	public HistoryUtils getHistoryUtils() {
		return historyUtils;
	}

	public void setHistoryUtils(HistoryUtils historyUtils) {
		this.historyUtils = historyUtils;
	}

	public Map<Integer, Integer> getItemIdToQuantityUsed() {
		return itemIdToQuantityUsed;
	}

	public void setItemIdToQuantityUsed(Map<Integer, Integer> itemIdToQuantityUsed) {
		this.itemIdToQuantityUsed = itemIdToQuantityUsed;
	}

	public UserPojo getUser() {
		return user;
	}

	public void setUser(UserPojo user) {
		this.user = user;
	}
	

}
