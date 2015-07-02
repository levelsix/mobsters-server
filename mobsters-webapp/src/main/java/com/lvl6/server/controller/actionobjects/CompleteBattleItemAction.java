package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserBattleItemHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserBattleItemHistoryPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.utils.utilmethods.DeleteUtil;
import com.lvl6.utils.utilmethods.InsertUtil;

@Component@Scope("prototype")public class CompleteBattleItemAction {
	private static Logger log = LoggerFactory.getLogger( CompleteBattleItemAction.class);

	private String userId;
	private List<BattleItemQueueForUser> completedList; //completed queue items
	private int gemsForSpeedUp;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtil; 
	@Autowired protected BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil; 
	@Autowired protected InsertUtil insertUtil;
	@Autowired protected DeleteUtil deleteUtil;
	private MiscMethods miscMethods;
	private HistoryUtils historyUtils;
	private Date clientTime;
	private UserBattleItemHistoryDao ubihDao;

	public CompleteBattleItemAction(
			String userId,
			List<BattleItemQueueForUser> completedList,//completed queue items
			int gemsForSpeedUp, UserRetrieveUtils2 userRetrieveUtil,
			BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil,
			InsertUtil insertUtil, DeleteUtil deleteUtil,
			MiscMethods miscMethods, HistoryUtils historyUtils,
			Date clientTime, UserBattleItemHistoryDao ubihDao) {
		super();
		this.userId = userId;
		this.completedList = completedList;
		this.gemsForSpeedUp = gemsForSpeedUp;
		this.userRetrieveUtil = userRetrieveUtil;
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
		this.insertUtil = insertUtil;
		this.deleteUtil = deleteUtil;
		this.miscMethods = miscMethods;
		this.historyUtils = historyUtils;
		this.clientTime = clientTime;
		this.ubihDao = ubihDao;
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
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

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

		if (null == completedList || completedList.isEmpty()) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_INVALID_BATTLE_ITEMS);
			log.error("no BattleItems sent as completed");
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {

		user = userRetrieveUtil.getUserById(userId);

		if (null == user) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}

		if (!hasEnoughGems(resBuilder)) {
			log.error("insufficient gems: {}, user={}", gemsForSpeedUp, user);
			return false;
		}

		return true;
	}

	private boolean hasEnoughGems(Builder resBuilder) {
		if (gemsForSpeedUp > 0) {
			int userGems = user.getGems();
			//check if user can afford to buy however many more user wants to buy
			if (userGems < gemsForSpeedUp) {
				resBuilder
						.setStatus(ResponseStatus.FAIL_INSUFFICIENT_FUNDS);
				return false;
			}
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		prevCurrencies = new HashMap<String, Integer>();

		//update currency
		if (gemsForSpeedUp > 0) {
			prevCurrencies.put(miscMethods.gems, user.getGems());
			gemsChange = -1 * (gemsForSpeedUp);
		}

		updateUserCurrency();

		//update items in db
		int numDeleted = deleteUtil.deleteFromBattleItemQueueForUser(userId,
				completedList);
		if (numDeleted != completedList.size()) {
			log.error("did not delete all the battle items in queue");
			return false;
		}
		log.info("finished deleting completed items from queue");

		bifuCompletedList = new ArrayList<BattleItemForUser>();
		Map<Integer, BattleItemForUser> userBattleItemIdsToBattleItemsForUser = battleItemForUserRetrieveUtil
				.getBattleItemIdsToUserBattleItemForUser(userId);
		for (BattleItemQueueForUser biqfu : completedList) {
			int battleItemId = biqfu.getBattleItemId();
			if (userBattleItemIdsToBattleItemsForUser.containsKey(battleItemId)) {
				BattleItemForUser bifu = userBattleItemIdsToBattleItemsForUser
						.get(battleItemId);
				bifu.setQuantity(bifu.getQuantity() + 1);
				bifuCompletedList.add(bifu);
			} else {
				BattleItemForUser bifu = new BattleItemForUser(randomUUID(),
						userId, battleItemId, 1);
				userBattleItemIdsToBattleItemsForUser.put(battleItemId, bifu);
				bifuCompletedList.add(bifu);
			}
		}

		//		List<BattleItemForUser> battleItemList = new ArrayList<BattleItemForUser>(bifuCompletedList);
		int numInserted = insertUtil
				.insertIntoBattleItemForUser(bifuCompletedList);

		//when updates row, counts as 2, insert counts as 1
		if (numInserted < bifuCompletedList.size()
				|| numInserted > bifuCompletedList.size() * 2) {
			log.error("did not insert or update all the battle items completed");
			return false;
		}

		prepCurrencyHistory();
		saveToBattleItemHistory();

		return true;
	}

	private void updateUserCurrency() {
		boolean updated = user.updateGemsCashAndOilFromBattleItem(gemsChange,
				0, 0);
		log.info("updated, user paid for battle items {}", updated);
	}

	private void prepCurrencyHistory() {
		String gems = miscMethods.gems;

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
	
	public void saveToBattleItemHistory() {
		List<UserBattleItemHistoryPojo> ubihList = new ArrayList<UserBattleItemHistoryPojo>();
		for(BattleItemForUser bifu : bifuCompletedList) {
			ubihList.add(historyUtils.createBattleItemHistory(bifu.getUserId(), bifu.getBattleItemId(), 
					new Timestamp(clientTime.getTime()), true, false, "finish making battle item", null));
		}
		ubihDao.insert(ubihList);
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
