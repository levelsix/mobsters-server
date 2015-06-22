package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto.SetDefendingMsgStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;

@Component@Scope("prototype")public class SetDefendingMsgAction {
	private static Logger log = LoggerFactory.getLogger( SetDefendingMsgAction.class);

	private String userId;
	private String msg;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	private MiscMethods miscMethods;

	public SetDefendingMsgAction(String userId, String msg,
			UserRetrieveUtils2 userRetrieveUtil,
			MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.msg = msg;
		this.userRetrieveUtil = userRetrieveUtil;
		this.miscMethods = miscMethods;
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
		resBuilder.setStatus(SetDefendingMsgStatus.FAIL_OTHER);

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

		resBuilder.setStatus(SetDefendingMsgStatus.SUCCESS);

	}

	private boolean verifySyntax(Builder resBuilder) {

		if (null == msg) {
			log.error(String.format("invalid msg for defendingStatus=%s.", msg));
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		user = userRetrieveUtil.getUserById(userId);
		if (null == user) {
			log.error(String.format("no user with id=%s", userId));
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

		String censoredMsg = miscMethods.censorUserInput(msg);
		log.info("old msg={} \t censoredMsg={},", msg, censoredMsg);

		//update the user saying he got the gifts
		user.updateDefendingMsg(censoredMsg);

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
