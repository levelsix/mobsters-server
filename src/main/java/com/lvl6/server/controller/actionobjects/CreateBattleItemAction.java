package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.Builder;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.CreateBattleItemStatus;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class CreateBattleItemAction
{ 
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private List<BattleItemQueueForUser> deletedList; //completed queue items
	private List<BattleItemQueueForUser> removedList; //user removed the queue items
	private List<BattleItemQueueForUser> updatedList; //only updates timestamp
	private List<BattleItemQueueForUser> newList;
	private int cashChange;
	private int oilChange;
	private int gemCostForCreating;
	private boolean isSpeedup;
	private int gemsForSpeedup;
	private BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	
	public CreateBattleItemAction( 
		String userId,
		User user,
		List<BattleItemQueueForUser> deletedList,//completed queue items
		List<BattleItemQueueForUser> removedList, //user removed the queue items
		List<BattleItemQueueForUser> updatedList, //only updates timestamp
		List<BattleItemQueueForUser> newList,
		int cashChange,
		int oilChange,
		int gemCostForCreating,
		boolean isSpeedup,
		int gemsForSpeedup,
		BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil,
		InsertUtil insertUtil,
		UpdateUtil updateUtil,
		DeleteUtil deleteUtil
	 )
	{
		super();
		this.userId = userId;
		this.user = user;
		this.deletedList = deletedList;
		this.removedList = removedList;
		this.updatedList = updatedList;
		this.newList = newList;
		this.cashChange = cashChange;
		this.oilChange = oilChange;
		this.gemCostForCreating = gemCostForCreating;
		this.isSpeedup = isSpeedup;
		this.gemsForSpeedup = gemsForSpeedup;
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
	}

	private int gemsChange;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
		
		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(CreateBattleItemStatus.SUCCESS);
	}

	private boolean verifySemantics(Builder resBuilder) {
		if (null == user) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			log.error( "no user with id={}", userId );
			return false;
		}
		
//		boolean legitMaps = verifyNewUpdatedRemovedDeletedMaps(resBuilder);
//		if (!legitMaps) {
//			return false;
//		}
		
		boolean hasEnoughGems = true;
		boolean hasEnoughCash = true;
		boolean hasEnoughOil = true;
		
		if(gemsForSpeedup + gemCostForCreating > 0) {
			hasEnoughGems = verifyEnoughGems(resBuilder);
		}
		if(cashChange > 0) {
			hasEnoughCash = verifyEnoughCash(resBuilder);
		}
		if(oilChange > 0) {
			hasEnoughOil = verifyEnoughOil(resBuilder);
		}
		
		if(hasEnoughGems && hasEnoughCash && hasEnoughOil) {
			return true;
		}
		else return false;
	}

	private boolean verifyEnoughGems(Builder resBuilder) {
		int userGems = user.getGems();

		//check if user can afford to buy however many more user wants to buy
		if (userGems < gemsForSpeedup + gemCostForCreating) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
			return false; 
		}
		else return true;
	}

	private boolean verifyEnoughCash(Builder resBuilder) {
		int userCash = user.getCash();
		if(userCash < cashChange) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}
		else return true;
	}
	
	private boolean verifyEnoughOil(Builder resBuilder) {
		int userOil = user.getOil();
		if(userOil < oilChange) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
			return false;
		}
		else return true;
	}
	

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		if(deletedList != null) {
			log.info("new list not null");
			if(!deletedList.isEmpty()) {
				log.info("new list not empty");
				//remove the elements from queue
				int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(deletedList);
				if(numDeleted != deletedList.size()) {
					log.error("did not delete all the battle items in queue");
				}
				log.info("finish deleting");

				
				//add the elements to user's battle items			
				Map<Integer, List<BattleItemForUser>> userBattleItemMap = 
						battleItemForUserRetrieveUtil.getBattleItemIdsToUserBattleItemForUser(userId);
				log.info("finish retrieving");
				int totalInserts = insertUtil.insertIntoBattleItemForUser(deletedList, userId, userBattleItemMap);
				if(totalInserts != deletedList.size()) {
					log.error("did not add all the battle items to user battle items");
				}
			}
		}
		
		if(removedList != null) {
			if(!removedList.isEmpty()) {
				int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(removedList);
				if(numDeleted != removedList.size()) {
					log.error("did not remove all the battle items in queue");
				}
			}
		}

		if(updatedList != null) {
			if(!updatedList.isEmpty()) {
				int numUpdated = updateUtil.updateBattleItemQueueForUser(updatedList);
				if(numUpdated != updatedList.size()) {
					log.error("did not update all battle items in queue");
				}
			}
		}

		if(newList != null) {
			log.info("new list not null");
			if(!newList.isEmpty()) {
				log.info("new list not empty");
				int numInserted = insertUtil.insertIntoBattleItemQueueForUser(newList);
				if(numInserted != newList.size()) {
					log.error("did not insert all battle items in queue");
				}
			}
		}

		
		if(gemCostForCreating + gemsForSpeedup > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			gemsChange = -1*(gemCostForCreating + gemsForSpeedup);
		}

		if (cashChange != 0) {
			prevCurrencies.put(MiscMethods.cash, user.getCash());
		}
		
		if(oilChange != 0) {
			prevCurrencies.put(MiscMethods.oil, user.getOil());
		}
		
		updateUserCurrency();
		
		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency()
	{		
		boolean updated = user.updateGemsCashAndOilFromBattleItem(gemsChange, -1*cashChange, -1*oilChange);
		log.info("updated, user paid for battle items {}", updated);
	}
	
	private void prepCurrencyHistory()
	{
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;
		String oil = MiscMethods.oil;
		
		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb1 = new StringBuilder();
		StringBuilder detailSb2 = new StringBuilder();
		StringBuilder detailSb3 = new StringBuilder();
		details = new HashMap<String, String>();

		if (0 != gemsChange) {
			currencyDeltas.put(gems, gemsChange);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__CREATING_BATTLE_ITEMS);
			detailSb1.append(" gems spent buying=");
			detailSb1.append(gemCostForCreating);
			if(isSpeedup) {
				detailSb1.append(" gems spent speedingup=");
				detailSb1.append(gemsForSpeedup);
			}
			details.put(gems, detailSb1.toString());
		}
		
		if (0 != cashChange) {
			currencyDeltas.put(cash, cashChange);
			curCurrencies.put(cash, user.getCash());
			reasonsForChanges.put(cash,
				ControllerConstants.UCHRFC__CREATING_BATTLE_ITEMS);
			detailSb2.append(" cash spent or refunded=");
			detailSb2.append(cashChange);
			details.put(cash, detailSb2.toString());
		}
		
		if (0 != oilChange) {
			currencyDeltas.put(oil, oilChange);
			curCurrencies.put(oil, user.getOil());
			reasonsForChanges.put(oil,
				ControllerConstants.UCHRFC__CREATING_BATTLE_ITEMS);
			detailSb3.append(" oil spent or refunded=");
			detailSb3.append(oilChange);
			details.put(oil, detailSb3.toString());
		}
		
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

}
