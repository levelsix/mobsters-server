package com.lvl6.server.controller.actionobjects;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto.CustomizePvpBoardObstacleStatus;
import com.lvl6.retrieveutils.PvpBoardObstacleForUserRetrieveUtil;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

public class CustomizePvpBoardObstacleAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private Collection<PvpBoardObstacleForUser> nuOrUpdated;
	private List<Integer> removeUserPvpBoardObstacleIds;
	protected PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil;
	protected InsertUtil insertUtil;
	protected DeleteUtil deleteUtil;

	public CustomizePvpBoardObstacleAction(
			String userId,
			Collection<PvpBoardObstacleForUser> nuOrUpdated,
			List<Integer> removeUserPvpBoardObstacleIds,
			PvpBoardObstacleForUserRetrieveUtil pvpBoardObstacleForUserRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.nuOrUpdated = nuOrUpdated;
		this.removeUserPvpBoardObstacleIds = removeUserPvpBoardObstacleIds;
		this.pvpBoardObstacleForUserRetrieveUtil = pvpBoardObstacleForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class CustomizePvpBoardObstacleResource {
	//		
	//		
	//		public CustomizePvpBoardObstacleResource() {
	//			
	//		}
	//	}
	//
	//	public CustomizePvpBoardObstacleResource execute() {
	//		
	//	}

	//derived state

	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(CustomizePvpBoardObstacleStatus.FAIL_OTHER);

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

		resBuilder.setStatus(CustomizePvpBoardObstacleStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (nuOrUpdated.isEmpty() && removeUserPvpBoardObstacleIds.isEmpty()) {
			log.error("nuOrUpdated and removeUserPvpBoardObstacleIds are empty");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		//		prevCurrencies = new HashMap<String, Integer>();
		//		
		//		if (0 != gemsGained) {
		//			prevCurrencies.put(MiscMethods.gems, user.getGems());
		//		}
		//		if (0 != cashGained) {
		//			prevCurrencies.put(MiscMethods.cash, user.getCash());
		//		}
		//		if (0 != oilGained) {
		//			prevCurrencies.put(MiscMethods.oil, user.getOil());
		//		}
		//		

		//delete obstacles first
		if (!removeUserPvpBoardObstacleIds.isEmpty()) {
			int numDeleted = deleteUtil.deletePvpBoardObstacleForUser(
					removeUserPvpBoardObstacleIds, userId);
			log.info("numDeleted={}. deletedIds={}", numDeleted,
					removeUserPvpBoardObstacleIds);
		}

		//insert/update the others
		if (!nuOrUpdated.isEmpty()) {
			int numInserted = insertUtil
					.insertIntoUpdatePvpBoardObstacleForUser(nuOrUpdated);
			log.info("numInserted={}, nuOrUpdated={}", numInserted, nuOrUpdated);
		}

		//		prepCurrencyHistory();

		return true;
	}

	//	private void prepCurrencyHistory()
	//	{
	//		String gems = MiscMethods.gems;
	//		String cash = MiscMethods.cash;
	//		String oil = MiscMethods.oil;
	//		
	//		currencyDeltas = new HashMap<String, Integer>();
	//		curCurrencies = new HashMap<String, Integer>();
	//		reasonsForChanges = new HashMap<String, String>();
	//		if (0 != gemsGained) {
	//			currencyDeltas.put(gems, gemsGained);
	//			curCurrencies.put(gems, user.getGems());
	//			reasonsForChanges.put(gems,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != cashGained) {
	//			currencyDeltas.put(cash, cashGained);
	//			curCurrencies.put(cash, user.getCash());
	//			reasonsForChanges.put(cash,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		if (0 != oilGained) {
	//			currencyDeltas.put(oil, oilGained);
	//			curCurrencies.put(oil, user.getOil());
	//			reasonsForChanges.put(oil,
	//				ControllerConstants.UCHRFC__TRADE_ITEM_FOR_RESOURCES);
	//		}
	//		
	//		details = new HashMap<String, String>();
	//		for (Integer key : itemIdToResourceToQuantities.keySet()) {
	//			String value = itemIdToResourceToQuantities.get(key).toString(); 
	//			
	//			details.put(key.toString(), value);
	//		}
	//	}

	//	public Map<String, Integer> getCurrencyDeltas() {
	//		return currencyDeltas;
	//	}
	//	
	//	public Map<String, Integer> getPreviousCurrencies() {
	//		return prevCurrencies;
	//	}
	//
	//	public Map<String, Integer> getCurrentCurrencies() {
	//		return curCurrencies;
	//	}
	//	
	//	public Map<String, String> getReasons() {
	//		return reasonsForChanges;
	//	}
	//	
	//	public Map<String, String> getDetails() {
	//		return details;
	//	}
}
