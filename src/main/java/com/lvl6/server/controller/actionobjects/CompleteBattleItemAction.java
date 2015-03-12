package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto.Builder;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto.CompleteBattleItemStatus;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

public class CompleteBattleItemAction
{ 
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<BattleItemQueueForUser> completedList; //completed queue items
	private int gemsForSpeedUp;
	private UserRetrieveUtils2 userRetrieveUtil;
	private BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	protected InsertUtil insertUtil;
	protected DeleteUtil deleteUtil;
	
	public CompleteBattleItemAction( 
		String userId,
		List<BattleItemQueueForUser> completedList,//completed queue items
		int gemsForSpeedUp,
		UserRetrieveUtils2 userRetrieveUtil,
		BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil,
		InsertUtil insertUtil,
		DeleteUtil deleteUtil )
	{
		super();
		this.userId = userId;
		this.completedList = completedList;
		this.gemsForSpeedUp = gemsForSpeedUp;
		this.userRetrieveUtil = userRetrieveUtil;
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
	}

	private User user;
	private int gemsChange;
	private List<BattleItemForUser> bifuCompletedList;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(CompleteBattleItemStatus.FAIL_OTHER);
		
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

		resBuilder.setStatus(CompleteBattleItemStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		
		if(null == completedList || completedList.isEmpty()){
			resBuilder.setStatus(CompleteBattleItemStatus.FAIL_INVALID_BATTLE_ITEMS);
			log.error("no BattleItems sent as completed");
			return false;
		}
		return true;
	}
	
	private boolean verifySemantics(Builder resBuilder) {
		
		user = userRetrieveUtil.getUserById(userId);
		
		if (null == user) {
			resBuilder.setStatus(CompleteBattleItemStatus.FAIL_OTHER);
			log.error( "no user with id={}", userId );
			return false;
		}
		
		if( !hasEnoughGems(resBuilder) ) {
			log.error("insufficient gems: {}, user={}",
					gemsForSpeedUp, user);
			return false;
		}
		
		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder) {
		if ( gemsForSpeedUp > 0 ) {
			int userGems = user.getGems();
			//check if user can afford to buy however many more user wants to buy
			if (userGems < gemsForSpeedUp) {
				resBuilder.setStatus(CompleteBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
				return false; 
			}
		}
		
		return true;
	}
	
	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();
		
		//update currency
		if(gemsForSpeedUp > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			gemsChange = -1*(gemsForSpeedUp);
		}
		
		updateUserCurrency();

		//update items in db
		int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(userId, completedList);
		if(numDeleted != completedList.size()) {
			log.error("did not delete all the battle items in queue");
			return false;
		}
		log.info("finished deleting completed items from queue");

		bifuCompletedList = new ArrayList<BattleItemForUser>();
		Map<Integer, BattleItemForUser> userBattleItemIdsToBattleItemsForUser = battleItemForUserRetrieveUtil.getBattleItemIdsToUserBattleItemForUser(userId);
		for(BattleItemQueueForUser biqfu : completedList) {
			int battleItemId = biqfu.getBattleItemId();
			if(userBattleItemIdsToBattleItemsForUser.containsKey(battleItemId)) {
				BattleItemForUser bifu = userBattleItemIdsToBattleItemsForUser.get(battleItemId);
				bifu.setQuantity(bifu.getQuantity()+1);
				bifuCompletedList.add(bifu);
			}
			else {
				BattleItemForUser bifu = new BattleItemForUser(randomUUID(), userId, battleItemId, 1);
				userBattleItemIdsToBattleItemsForUser.put(battleItemId, bifu);
				bifuCompletedList.add(bifu);
			}
		}
		
//		List<BattleItemForUser> battleItemList = new ArrayList<BattleItemForUser>(bifuCompletedList);
		int numInserted = insertUtil.insertIntoBattleItemForUser(bifuCompletedList);
		
		if(numInserted != completedList.size()) {
			log.error("did not insert or update all the battle items completed");
			return false;
		}

		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency()
	{		
		boolean updated = user.updateGemsCashAndOilFromBattleItem(gemsChange, 0, 0);
		log.info("updated, user paid for battle items {}", updated);
	}
	
	private void prepCurrencyHistory()
	{
		String gems = MiscMethods.gems;
		
		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb1 = new StringBuilder();
		details = new HashMap<String, String>();

		if (0 != gemsChange) {
			currencyDeltas.put(gems, gemsChange);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__SPED_UP_COMPLETE_BATTLE_ITEMS);
			detailSb1.append(" gems spent speedingup=");
			detailSb1.append(gemsForSpeedUp);
			details.put(gems, detailSb1.toString());
		}

	}

	private String randomUUID() {
		return UUID.randomUUID().toString();
	}

	public User getUser() {
		return user;
	}

	public List<BattleItemForUser> getBifuCompletedList() {
		return bifuCompletedList;
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

}
