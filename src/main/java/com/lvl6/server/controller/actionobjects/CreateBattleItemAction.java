package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.Builder;
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto.CreateBattleItemStatus;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class CreateBattleItemAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private List<BattleItemQueueForUser> deletedList; //completed queue items
	private List<BattleItemQueueForUser> updatedList; //only updates timestamp
	private List<BattleItemQueueForUser> newList;
	private int cashChange;
	private int maxCash;
	private int oilChange;
	private int maxOil;
	private int gemCostForCreating;
	private UserRetrieveUtils2 userRetrieveUtil;
	private BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	protected InsertUtil insertUtil;
	protected UpdateUtil updateUtil;
	protected DeleteUtil deleteUtil;
	private MiscMethods miscMethods;

	public CreateBattleItemAction(
			String userId,
			List<BattleItemQueueForUser> deletedList,//completed queue items
			List<BattleItemQueueForUser> updatedList, //only updates timestamp
			List<BattleItemQueueForUser> newList, int cashChange, int maxCash,
			int oilChange, int maxOil, int gemCostForCreating,
			UserRetrieveUtils2 userRetrieveUtil,
			BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil,
			InsertUtil insertUtil, UpdateUtil updateUtil, DeleteUtil deleteUtil,
			MiscMethods miscMethods) {
		super();
		this.userId = userId;
		this.deletedList = deletedList;
		this.updatedList = updatedList;
		this.newList = newList;
		this.cashChange = cashChange;
		this.maxCash = maxCash;
		this.oilChange = oilChange;
		this.maxOil = maxOil;
		this.gemCostForCreating = gemCostForCreating;
		this.userRetrieveUtil = userRetrieveUtil;
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.updateUtil = updateUtil;
		this.deleteUtil = deleteUtil;
		this.miscMethods = miscMethods;
	}

	private boolean emptyDeletedList;
	private boolean emptyUpdatedList;
	private boolean emptyNewList;
	private User user;
	private int gemsChange;
	private Map<String, Integer> currencyDeltas;
	private Map<String, Integer> prevCurrencies;
	private Map<String, Integer> curCurrencies;
	private Map<String, String> reasonsForChanges;
	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);

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

		resBuilder.setStatus(CreateBattleItemStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {

		emptyDeletedList = (null == deletedList || deletedList.isEmpty());
		emptyUpdatedList = (null == updatedList || updatedList.isEmpty());
		emptyNewList = (null == newList || newList.isEmpty());
		if (emptyDeletedList && emptyUpdatedList && emptyNewList) {
			log.error("no BattleItems sent");
			return false;
		}

		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			resBuilder.setStatus(CreateBattleItemStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}

		//		boolean legitMaps = verifyNewUpdatedRemovedDeletedMaps(resBuilder);
		//		if (!legitMaps) {
		//			return false;
		//		}

		if (!hasEnoughGems(resBuilder)) {
			log.error("insufficient gems: {}, user={}", gemCostForCreating,
					user);
			return false;
		}

		if (!hasEnoughCash(resBuilder)) {
			log.error("insufficient cash: {}, user={}", cashChange, user);
			return false;
		}
		if (!hasEnoughOil(resBuilder)) {
			log.error("insufficient oil: {}, user={}", oilChange, user);
			return false;
		}

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder) {
		if (gemCostForCreating > 0) {
			int userGems = user.getGems();
			//check if user can afford to buy however many more user wants to buy
			if (userGems < gemCostForCreating) {
				resBuilder
						.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		return true;
	}

	private boolean hasEnoughCash(Builder resBuilder) {
		//since negative cashChange means charge, then negative of that is
		//the cost. If cashChange is positive, meaning refund, user will always
		//have more than a negative amount
		int cashCost = -1 * cashChange;
		if (cashCost > 0) {
			int userCash = user.getCash();
			if (userCash < cashCost) {
				resBuilder
						.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		return true;
	}

	private boolean hasEnoughOil(Builder resBuilder) {
		//since negative oilChange means charge, then negative of that is
		//the cost. If oilChange is positive, meaning refund, user will always
		//have more than a negative amount
		int oilCost = -1 * oilChange;
		if (oilCost > 0) {
			int userOil = user.getOil();
			if (userOil < oilCost) {
				resBuilder
						.setStatus(CreateBattleItemStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		//update currency
		if (gemCostForCreating > 0) {
			prevCurrencies.put(miscMethods.gems, user.getGems());
			gemsChange = -1 * (gemCostForCreating);
		}
		if (cashChange != 0) {
			prevCurrencies.put(miscMethods.cash, user.getCash());
		}
		if (oilChange != 0) {
			prevCurrencies.put(miscMethods.oil, user.getOil());
		}

		updateUserCurrency();

		int deletedListSize = 0;
		int updatedListSize = 0;
		int newListSize = 0;

		//update items in db
		if (!emptyDeletedList) {
			log.info("new list not empty");
			//remove the elements from queue
			deletedListSize = deletedList.size();
			int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(
					userId, deletedList);
			if (numDeleted != deletedListSize) {
				log.error("did not delete all the battle items in queue");
				return false;
			}
			log.info("finished deleting");
		}

		List<BattleItemQueueForUser> nuOrUpdatedList = new ArrayList<BattleItemQueueForUser>();
		if (!emptyUpdatedList) {
			log.info("updated list not null");
			updatedListSize = updatedList.size();
			nuOrUpdatedList.addAll(updatedList);
		}
		if (!emptyNewList) {
			log.info("new list not empty");
			newListSize = newList.size();
			nuOrUpdatedList.addAll(newList);
		}
		log.info("about to insert to queue");

		int numUpdated = insertUtil
				.insertIntoBattleItemQueueForUser(nuOrUpdatedList);
		if (numUpdated < updatedListSize + newListSize
				|| numUpdated > (updatedListSize + newListSize) * 2) {
			log.error(
					"did not update all the battle items in queue, numUpdated = ",
					numUpdated);
			return false;
		}

		prepCurrencyHistory();

		return true;
	}

	private void updateUserCurrency() {
		if (cashChange != 0) {
			//for refunds
			int curCash = Math.min(user.getCash(), maxCash); //in case user's cash is more than maxCash.
			log.info("curCash={}", curCash);
			int maxCashUserCanGain = maxCash - curCash;
			log.info("maxCashUserCanGain={}", maxCashUserCanGain);
			cashChange = Math.min(cashChange, maxCashUserCanGain);
		}

		if (oilChange != 0) {
			//for refunds
			int curOil = Math.min(user.getOil(), maxOil); //in case user's oil is more than maxOil.
			log.info("curOil={}", curOil);
			int maxOilUserCanGain = maxOil - curOil;
			log.info("maxOilUserCanGain={}", maxOilUserCanGain);
			oilChange = Math.min(oilChange, maxOilUserCanGain);
		}

		boolean updated = user.updateGemsCashAndOilFromBattleItem(gemsChange,
				cashChange, oilChange);
		log.info("updated, user paid for battle items {}", updated);
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;
		String cash = miscMethods.cash;
		String oil = miscMethods.oil;

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
