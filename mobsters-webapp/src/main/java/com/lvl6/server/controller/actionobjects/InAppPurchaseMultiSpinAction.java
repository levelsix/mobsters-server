package com.lvl6.server.controller.actionobjects;

import org.jooq.Configuration;
import org.jooq.impl.DefaultConfiguration;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.mobsters.db.jooq.generated.Tables;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.IapHistoryDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ItemConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.ItemForUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.UserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.properties.IAPValues;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.Builder;
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto.InAppPurchaseStatus;
import com.lvl6.server.controller.utils.HistoryUtils;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.utilmethods.InsertUtil;


public class InAppPurchaseMultiSpinAction {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private JSONObject receiptFromApple;
	protected InsertUtil insertUtil;
	private HistoryUtils historyUtils;
	
	public InAppPurchaseMultiSpinAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InAppPurchaseMultiSpinAction(String userId, JSONObject receiptFromApple,
			InsertUtil insertUtil, HistoryUtils historyUtils) {
		super();
		this.userId = userId;
		this.receiptFromApple = receiptFromApple;
		this.insertUtil = insertUtil;
		this.historyUtils = historyUtils;
	}

	//derived state
	private String packageName;
	private int gachaMultiSpinItemId;
	private UserDao userDao;
	private UserPojo userPojo;
	private ItemConfigDao itemConfigDao;
	private ItemForUserDao itemForUserDao;
	private IapHistoryDao iapHistoryDao;
	private ItemForUserPojo ifuPojo;

	public void execute(Builder resBuilder) {
		setUpDaos();
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
	
	public void setUpDaos() {
		Configuration config = AppContext.getApplicationContext().getBean(DefaultConfiguration.class);//new DefaultConfiguration().set(DBConnection.get().getConnection()).set(SQLDialect.MYSQL);
		userDao = new UserDao(config);
		itemConfigDao = new ItemConfigDao(config);
		itemForUserDao = new ItemForUserDao(config);
		iapHistoryDao = new IapHistoryDao(config);
	}

	public boolean verifySyntax(Builder resBuilder) {
		userPojo = userDao.findById(userId);
		if(userPojo == null) {
			log.error("user is null with userId {}", userId);
			return false;
		}
		try {
			packageName = receiptFromApple.getString(IAPValues.PRODUCT_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ItemConfigPojo item = itemConfigDao.fetchOne(Tables.ITEM_CONFIG.ITEM_TYPE, "GACHA_MULTI_SPIN");
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
			int gemChange = 0;
			
			historyUtils.insertIAPHistoryElem(receiptFromApple, gemChange,
					userPojo, realLifeCashCost, null, iapHistoryDao);					
			ifuPojo = new ItemForUserPojo(userId, gachaMultiSpinItemId, 1);
			itemForUserDao.insert(ifuPojo);

			updateUserSalesValue();
			return success;
		} catch (Exception e) {
			log.error("error verifying InAppPurchase request. receiptFromApple={}, exception={}",
					receiptFromApple, e);
			success = false;
		}
		return success;
	}

	public void updateUserSalesValue() {
		userPojo.setSalesValue(4); //make them max sales value, this fool's a straight baller
		userDao.update(userPojo);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JSONObject getReceiptFromApple() {
		return receiptFromApple;
	}

	public void setReceiptFromApple(JSONObject receiptFromApple) {
		this.receiptFromApple = receiptFromApple;
	}

	public InsertUtil getInsertUtil() {
		return insertUtil;
	}

	public void setInsertUtil(InsertUtil insertUtil) {
		this.insertUtil = insertUtil;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getGachaMultiSpinItemId() {
		return gachaMultiSpinItemId;
	}

	public void setGachaMultiSpinItemId(int gachaMultiSpinItemId) {
		this.gachaMultiSpinItemId = gachaMultiSpinItemId;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserPojo getUserPojo() {
		return userPojo;
	}

	public void setUserPojo(UserPojo userPojo) {
		this.userPojo = userPojo;
	}

	public ItemConfigDao getItemConfigDao() {
		return itemConfigDao;
	}

	public void setItemConfigDao(ItemConfigDao itemConfigDao) {
		this.itemConfigDao = itemConfigDao;
	}

	public ItemForUserDao getItemForUserDao() {
		return itemForUserDao;
	}

	public void setItemForUserDao(ItemForUserDao itemForUserDao) {
		this.itemForUserDao = itemForUserDao;
	}

	public IapHistoryDao getIapHistoryDao() {
		return iapHistoryDao;
	}

	public void setIapHistoryDao(IapHistoryDao iapHistoryDao) {
		this.iapHistoryDao = iapHistoryDao;
	}

	public ItemForUserPojo getIfuPojo() {
		return ifuPojo;
	}

	public void setIfuPojo(ItemForUserPojo ifuPojo) {
		this.ifuPojo = ifuPojo;
	}
	
	
}
