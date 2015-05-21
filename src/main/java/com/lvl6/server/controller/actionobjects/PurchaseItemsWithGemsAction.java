package com.lvl6.server.controller.actionobjects;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Item;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ItemForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.User;
import com.lvl6.proto.EventItemProto.PurchaseItemsWithGemsResponseProto.Builder;
import com.lvl6.proto.EventItemProto.PurchaseItemsWithGemsResponseProto.PurchaseItemsWithGemsStatus;


public class PurchaseItemsWithGemsAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private int gemsSpent;
	private Item itemGained;
	
	public PurchaseItemsWithGemsAction(String userId, int gemsSpent,
			Item itemGained) {
		super();
		this.userId = userId;
		this.gemsSpent = gemsSpent;
		this.itemGained = itemGained;
	}

	//derived state
	private User user;
	private int userGems;
	private UserDao userDao;
	private ItemForUserDao itemForUserDao;

//	private Map<String, Integer> currencyDeltas;
//	private Map<String, Integer> prevCurrencies;
//	private Map<String, Integer> curCurrencies;
//	private Map<String, String> reasonsForChanges;
//	private Map<String, String> details;

	public void execute(Builder resBuilder) {
		userDao = new UserDao();
		resBuilder.setStatus(PurchaseItemsWithGemsStatus.FAIL_OTHER);

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
			//			resBuilder.setStatus(PurchaseItemsWithGemsStatus.FAIL_OTHER);
			return;
		}
		resBuilder.setStatus(PurchaseItemsWithGemsStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {
		user = userDao.findById(userId);
		if(user == null) {
			log.error("user null with userId {}", userId);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		userGems = user.getGems();
		if(userGems < gemsSpent) {
			log.error("user doesn't have enough gems, userId {}", userId);
			return false;
		}
		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
//		prevCurrencies = new HashMap<String, Integer>();
		
		

	}

	private void updateUserCurrency() {
		user.setGems(userGems - gemsSpent);
		userDao.update(user);
	}

//	private void prepCurrencyHistory() {
//		String gems = miscMethods.gems;
//
//		currencyDeltas = new HashMap<String, Integer>();
//		curCurrencies = new HashMap<String, Integer>();
//		reasonsForChanges = new HashMap<String, String>();
//		StringBuilder detailSb = new StringBuilder();
//
//		if (0 != gemChange) {
//			currencyDeltas.put(gems, gemChange);
//			curCurrencies.put(gems, user.getGems());
//			reasonsForChanges.put(gems,
//					ControllerConstants.UCHRFC__PURHCASED_BOOSTER_PACK);
//			detailSb.append(" gemPrice=");
//			detailSb.append(gemPrice);
//			detailSb.append(" gemReward=");
//			detailSb.append(gemReward);
//		}
//
//		details = new HashMap<String, String>();
//		List<Integer> itemIds = new ArrayList<Integer>();
//		if (null != itemsUserReceives && !itemsUserReceives.isEmpty()) {
//
//			for (BoosterItem item : itemsUserReceives) {
//				int id = item.getId();
//				itemIds.add(id);
//			}
//
//			detailSb.append(" bItemIds=");
//			String itemIdsCsv = StringUtils.csvList(itemIds);
//			detailSb.append(itemIdsCsv);
//		}
//		if (gemReward > 0) {
//		}
//		details.put(gems, detailSb.toString());
//	}


}
