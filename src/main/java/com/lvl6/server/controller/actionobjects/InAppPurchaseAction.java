package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;

public class InAppPurchaseAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private String receipt;
	private UserRetrieveUtils2 userRetrieveUtil;

	public InAppPurchaseAction(
		String userId,
		String receipt,
		UserRetrieveUtils2 userRetrieveUtil )
	{
		super();
		this.userId = userId;
		this.receipt = receipt;
		this.userRetrieveUtil = userRetrieveUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class SetDefendingMsgResource {
	//		
	//		
	//		public SetDefendingMsgResource() {
	//			
	//		}
	//	}
	//
	//	public SetDefendingMsgResource execute() {
	//		
	//	}

	//derived state
	private User user;

	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;


	public void execute(Builder resBuilder) {
		resBuilder.setStatus(InAppPurchaseStatus.FAIL);

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

		resBuilder.setStatus(InAppPurchaseStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {


		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error(String.format(
				"no user with id=%s", userId));
			return false;
		}

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

		//		prepCurrencyHistory();
		//		TODO: DO STUFF BELOW HERE

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

	public User getUser() {
		return user;
	}

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
