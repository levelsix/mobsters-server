package com.lvl6.server.controller.actionobjects;

import java.util.Date;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ItemConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ItemForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.User;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.utils.DBConnection;


public class InAppPurchaseMultiSpinAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private JSONObject receiptFromApple;
	private Date now;
	
	public InAppPurchaseMultiSpinAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InAppPurchaseMultiSpinAction(String userId, JSONObject receiptFromApple,
			Date now) {
		super();
		this.userId = userId;
		this.receiptFromApple = receiptFromApple;
		this.now = now;
	}

	//derived state
	private String packageName;
	private int gachaMultiSpinItemId;
	private UserDao userDao;
	private User userPojo;
	private ItemConfigDao itemConfigDao;
	private ItemForUserDao itemForUserDao;


	public void execute(Builder resBuilder) {
		Configuration config = new DefaultConfiguration().set(DBConnection.get().getConnection()).set(SQLDialect.MYSQL);
		userDao = new UserDao(config);
		itemConfigDao = new ItemConfigDao(config);
		itemForUserDao = new ItemForUserDao(config);
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

	public boolean verifySyntax(Builder resBuilder) {
		userPojo = userDao.findById(userId);
		if(userPojo == null) {
			log.error("user is null with userId {}", userId);
			return false;
		}
		return true;
	}

	public boolean verifySemantics(Builder resBuilder) {
		gachaMultiSpinItemId = findGachaMultiSpinItemId();
		if(gachaMultiSpinItemId == 0) {
			return false;
		}
		return true;
	}
	
	public int findGachaMultiSpinItemId() {
		ItemConfig item = itemConfigDao.fetchOne(Tables.ITEM_CONFIG.ITEM_TYPE, "GACHA_MULTI_SPIN");
		if(item == null) {
			log.error("no item with item type of gacha_multi_spin");
			return 0;
		}
		return item.getId();
	}

	public boolean writeChangesToDB(Builder resBuilder) {
		boolean success = true;
		try {
			double realLifeCashCost;
			realLifeCashCost = IAPValues.getCashSpentForPackageName(packageName);

			gemChange = 0;

			if (!insertUtil.insertIAPHistoryElem(receiptFromApple, gemChange,
					user, realLifeCashCost, null)) {
				log.error(
						"problem with logging in-app purchase history for receipt:{} and user {}",
						receiptFromApple.toString(4), user);
				success = false;
			}
			processMoneyTreePurchase(resBuilder);

		} catch (Exception e) {
			log.error(
					String.format(
							"error verifying InAppPurchase request. receiptFromApple=%s",
							receiptFromApple), e);
			success = false;
		}
		
		
		
		
		ItemForUser ifuPojo = new ItemForUser
		itemForUserDao
		
		
		updateUserSalesValue();
		return success;
	}

	public void updateUserSalesValue() {
		userPojo.setSalesValue(4); //make them max sales value, this fool's a straight baller
		userDao.update(userPojo);
	}
	



}
