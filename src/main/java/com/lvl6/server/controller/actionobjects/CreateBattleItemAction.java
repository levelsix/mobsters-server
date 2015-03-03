package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.Research;
import com.lvl6.info.ResearchForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.Builder;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.CreateBattleItemStatus;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.retrieveutils.ResearchForUserRetrieveUtils;
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
	private Date now;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	
	public CreateBattleItemAction( 
		String userId,
		User user,
		Research research,
		String userResearchUuid,
		int gemsSpent,
		int resourceChange,
		ResourceType resourceType,
		Date now,
		InsertUtil insertUtil,
		UpdateUtil updateUtil,
		ResearchForUserRetrieveUtils researchForUserRetrieveUtil
	 )
	{
		super();
		this.userId = userId;
		this.user = user;
		this.research = research;
		this.userResearchUuid = userResearchUuid;
		this.gemsSpent = gemsSpent;
		this.resourceChange = resourceChange;
		this.resourceType = resourceType;
		this.now = now;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.researchForUserRetrieveUtil = researchForUserRetrieveUtil;
	}

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
		
		boolean legitMaps = verifyNewUpdatedRemovedDeletedMaps(resBuilder);
		if (!legitMaps) {
			return false;
		}
		
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
	
	private boolean verifyNewUpdatedRemovedDeletedMaps(Builder resBuilder) {
		if(newList == null || updatedList == null || removedList == null || deletedList == null) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			return false;
		}
		return true;
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
		boolean successfulInsertOrUpdate = false;
		
		if(!removedList.isEmpty()) {
			int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(removedList);
			if(numDeleted != removedList.size()) {
				log.error("did not remove all the battle items in queue");
			}
		}
		
		if(!updatedList.isEmpty()) {
			int numUpdated = updateUtil.updateBattleItemQueueForUser(updatedList);
			if(numUpdated != updatedList.size()) {
				log.error("did not update all battle items in queue");
			}
		}
		
		if(!newList.isEmpty()) {
			int numInserted = insertUtil.insertIntoBattleItemQueueForUser(newList);
			if(numInserted != newList.size()) {
				log.error("did not insert all battle items in queue");
			}
		}
		
		if(!deletedList.isEmpty()) {
			//remove the elements from queue
			int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(deletedList);
			if(numDeleted != deletedList.size()) {
				log.error("did not delete all the battle items in queue");
			}
			
			//add the elements to user's battle items
			
			
			
		}
		
		
		
		if(userResearchUuid != null) {
			successfulInsertOrUpdate = updateUtil.updateUserResearch(userResearchUuid, research.getId());
		}
		else {
			Timestamp timeOfPurchase = new Timestamp(now.getTime());
			boolean isComplete = false;
			userResearchId = insertUtil.insertUserResearch(userId, research, timeOfPurchase, isComplete);
			if(!userResearchId.equals("")) {
				successfulInsertOrUpdate = true;
				userResearchUuid = userResearchId;
			}
		}
		
		if(!successfulInsertOrUpdate) {
			return false;
		}
		
		if(!(gemsSpent > 0) && (resourceType != ResourceType.CASH) && (resourceType != ResourceType.OIL)) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			log.error("not being purchased with gems, cash, or oil, what is this voodoo shit");
			return false;
		}

		int gemsChange = 0;
		int cashChange = 0;
		int oilChange = 0;
		int expChange = 0;
		
		if(gemsSpent > 0) {
			prevCurrencies.put(MiscMethods.gems, user.getGems());
			gemsChange = -1*gemsSpent;
		}

		if (resourceType == ResourceType.CASH) {
			prevCurrencies.put(MiscMethods.cash, user.getCash());
			cashChange = -1*resourceChange;
		}
		else if (resourceType == ResourceType.OIL){
			prevCurrencies.put(MiscMethods.oil, user.getOil());
			oilChange = -1*resourceChange;
		}
		
//		user.updateRelativeGemsCashOilExperienceNaive(gemsChange, cashChange, oilChange, expChange);
		
		updateUserCurrency();
		
		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency()
	{
		int gemsDelta = -1 * gemsSpent;
		int resourceDelta = -1* resourceChange;
		
		boolean updated = user.updateGemsandResourcesFromPerformingResearch(gemsDelta, resourceDelta, resourceType);
		log.info("updated, user paid for research {}", updated);
	}
	
	private void prepCurrencyHistory()
	{
		String gems = MiscMethods.gems;
		String cash = MiscMethods.cash;
		String oil = MiscMethods.oil;
		
		currencyDeltas = new HashMap<String, Integer>();
		curCurrencies = new HashMap<String, Integer>();
		reasonsForChanges = new HashMap<String, String>();
		StringBuilder detailSb = new StringBuilder();
		StringBuilder detailSb2 = new StringBuilder();
		details = new HashMap<String, String>();

		if (0 != gemsSpent) {
			currencyDeltas.put(gems, gemsSpent);
			curCurrencies.put(gems, user.getGems());
			reasonsForChanges.put(gems,
				ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
			detailSb.append(" gemsSpent=");
			detailSb.append(gemsSpent);
			details.put(gems, detailSb.toString());
		}
		
		if(resourceChange>0) {
			if(resourceType == ResourceType.CASH) {
				currencyDeltas.put(cash, resourceChange);
				curCurrencies.put(cash, user.getCash());
				reasonsForChanges.put(cash, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" cash spent= ");
				detailSb2.append(resourceChange);
				details.put(cash, detailSb2.toString());

			}
			else if(resourceType == ResourceType.OIL) {
				currencyDeltas.put(oil, resourceChange);
				curCurrencies.put(oil, user.getOil());
				reasonsForChanges.put(oil, ControllerConstants.UCHRFC__PERFORMING_RESEARCH);
				detailSb2.append(" oil spent= ");
				detailSb2.append(resourceChange);
				details.put(oil, detailSb2.toString());
			}
		}
	}

	public User getUser() {
		return user;
	}
	
	public Research getResearch() {
		return research;
	}
	
	public String getUserResearchUuid() {
		return userResearchUuid;
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
