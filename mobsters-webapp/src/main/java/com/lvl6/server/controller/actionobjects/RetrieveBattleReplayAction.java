package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.BattleReplayForUser;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayResponseProto.Builder;
import com.lvl6.proto.EventPvpProto.RetrieveBattleReplayResponseProto.RetrieveBattleReplayStatus;
import com.lvl6.retrieveutils.BattleReplayForUserRetrieveUtil;

@Component@Scope("prototype")public class RetrieveBattleReplayAction {
	private static Logger log = LoggerFactory.getLogger( RetrieveBattleReplayAction.class);

	private String userId;
	private String replayId;
	@Autowired protected BattleReplayForUserRetrieveUtil battleReplayForUserRetrieveUtil; 

	public RetrieveBattleReplayAction(
			String userId, String replayId,
			BattleReplayForUserRetrieveUtil battleReplayForUserRetrieveUtil )
	{
		super();
		this.userId = userId;
		this.replayId = replayId;
		this.battleReplayForUserRetrieveUtil = battleReplayForUserRetrieveUtil;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveBattleReplayResource {
	//
	//
	//		public RetrieveBattleReplayResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveBattleReplayResource execute() {
	//
	//	}

	//derived state
	private BattleReplayForUser brfu;
	//	private Map<String, Integer> currencyDeltas;
	//	private Map<String, Integer> prevCurrencies;
	//	private Map<String, Integer> curCurrencies;
	//	private Map<String, String> reasonsForChanges;
	//	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(RetrieveBattleReplayStatus.FAIL_OTHER);

		//check out inputs before db interaction
//		boolean valid = verifySyntax(resBuilder);
//
//		if (!valid) {
//			return;
//		}

		boolean valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

//		boolean success = writeChangesToDB(resBuilder);
//		if (!success) {
//			return;
//		}

		resBuilder.setStatus(RetrieveBattleReplayStatus.SUCCESS);

	}

//	private boolean verifySyntax(Builder resBuilder) {
//		return true;
//	}

	private boolean verifySemantics(Builder resBuilder) {
		brfu = battleReplayForUserRetrieveUtil.getUserBattleReplay(replayId);
		if (null == brfu) {
			log.error("no replay with userId=%s replayId=%s", userId, replayId);
			return false;
		}

		return true;
	}

//	private boolean writeChangesToDB(Builder resBuilder) {
//		//		prevCurrencies = new HashMap<String, Integer>();
//		//
//		//		if (0 != gemsGained) {
//		//			prevCurrencies.put(MiscMethods.gems, user.getGems());
//		//		}
//		//		if (0 != cashGained) {
//		//			prevCurrencies.put(MiscMethods.cash, user.getCash());
//		//		}
//		//		if (0 != oilGained) {
//		//			prevCurrencies.put(MiscMethods.oil, user.getOil());
//		//		}
//		//
//		//		prepCurrencyHistory();
//
//		return true;
//	}

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

//	public User getUser() {
//		return user;
//	}

	public BattleReplayForUser getBrfu() {
		return brfu;
	}

	public void setBrfu(BattleReplayForUser brfu) {
		this.brfu = brfu;
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
