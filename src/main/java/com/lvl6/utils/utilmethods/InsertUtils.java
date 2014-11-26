package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.User;
import com.lvl6.properties.DBConstants;
import com.lvl6.properties.IAPValues;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.DBConnection;

public class InsertUtils implements InsertUtil{

	
	
	private static final Logger log = LoggerFactory.getLogger(InsertUtils.class);

  public static InsertUtil get() {
    return (InsertUtil) AppContext.getApplicationContext().getBean("insertUtils");
  }
  
  private String randomUUID() {
    return UUID.randomUUID().toString();
  }

  //	@Autowired
  //	protected CacheManager cache;

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#getCache()
   */
  //	@Override
  //	public CacheManager getCache() {
  //		return cache;
  //	}

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#setCache(org.springframework.cache.CacheManager)
   */
  //	@Override
  //	public void setCache(CacheManager cache) {
  //		this.cache = cache;
  //	}

	/*
	 * used for purchasing a city expansion and completing one
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserExpansionLastexpandtimeLastexpanddirectionIsexpanding(int, java.sql.Timestamp, com.lvl6.proto.InfoProto.ExpansionDirection, boolean)
	 */
	@Override
	public boolean insertUserCityExpansionData(String userId, Timestamp expandStartTime, 
			int xPosition, int yPosition, boolean isExpanding) {
		Map <String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__EXPAND_START_TIME, expandStartTime);
		insertParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__IS_EXPANDING, isExpanding);
		insertParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__X_POSITION, xPosition);
		insertParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__Y_POSITION, yPosition);

		int numUpdated = DBConnection.get().insertIntoTableBasic(DBConstants.TABLE_EXPANSION_PURCHASE_FOR_USER, insertParams);
		if (numUpdated >= 1) {
			return true;
		}
		return false;
	}
  
  public boolean insertLastLoginLastLogoutToUserSessions(String userId, Timestamp loginTime, Timestamp logoutTime) {
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.USER_SESSIONS__USER_ID, userId);
    insertParams.put(DBConstants.USER_SESSIONS__LOGIN_TIME, loginTime);
    insertParams.put(DBConstants.USER_SESSIONS__LOGOUT_TIME, logoutTime);

    int numInserted = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_USER_SESSION, insertParams);
    if (numInserted == 1) {
      return true;
    }
    return false;
  }

  @Override
  public int insertUserQuest(String userId, int questId) {
  	String tablename = DBConstants.TABLE_QUEST_FOR_USER;
  	
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
    insertParams.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);
    
    int numInserted = DBConnection.get().insertIntoTableBasic(tablename,
    		insertParams);
    return numInserted;
  }

  @Override
  public int insertUserQuestJobs(String userId, int questId,
		  List<Integer> questJobIds) {
	  String tableName = DBConstants.TABLE_QUEST_JOB_FOR_USER;
	  
	  int size = questJobIds.size();
	  List<String> userIdList = Collections.nCopies(size, userId);
	  List<Integer> questIdList = Collections.nCopies(size, questId);
	  
	  Map<String, List<?>> insertParams =
			  new HashMap<String, List<?>>();
	  
	  insertParams.put(DBConstants.QUEST_JOB_FOR_USER__USER_ID, userIdList);
	  insertParams.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID, questIdList);
	  insertParams.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_JOB_ID,
			  questJobIds);
	  int numInserted = DBConnection.get().insertIntoTableMultipleRows(
			  tableName, insertParams, size);
	  
	  return numInserted;
  }
  
  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#insertUserStructJustBuilt(int, int, java.sql.Timestamp, java.sql.Timestamp, com.lvl6.info.CoordinatePair)
   */
  @Override
  public boolean insertUserStructJustBuilt(String userId, int structId,
      Timestamp timeOfStructPurchase, Timestamp timeOfStructBuild,
      CoordinatePair structCoords) {
    String id = randomUUID();
    
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__ID, id);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__USER_ID, userId);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, structId);
    insertParams
    .put(DBConstants.STRUCTURE_FOR_USER__X_COORD, structCoords.getX());
    insertParams
    .put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, structCoords.getY());
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME,
        timeOfStructPurchase);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED,
        timeOfStructBuild);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, true);

    int numInserted = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_STRUCTURE_FOR_USER, insertParams);
    if (numInserted == 1) {
      return true;
    }
    return false;
  }

  /*
   * returns the id of the userstruct, -1 if none
   */
  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#insertUserStruct(int, int, com.lvl6.info.CoordinatePair, java.sql.Timestamp)
   */
  @Override
  public String insertUserStruct(String userId, int structId, CoordinatePair coordinates,
  		Timestamp timeOfPurchase, Timestamp lastRetrievedTime, boolean isComplete) {
    String userStructId = randomUUID();
    
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__USER_ID, userId);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, structId);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__X_COORD, coordinates.getX());
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, coordinates.getY());
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME, timeOfPurchase);
    insertParams.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, isComplete);
    if (null != lastRetrievedTime && isComplete) {
    	insertParams.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED, lastRetrievedTime);
    	
    }

    int numChanged = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_STRUCTURE_FOR_USER, insertParams);
    
    if (numChanged != 1) {
      userStructId = null;
    }
    
    return userStructId;
  }

  /*
   * assumptions: all the entries at index i across all the lists, 
   * they make up the values for one row to insert into user_currency_history
   */
  @Override
  public int insertUserStructs(List<String> userIdList, List<Integer> structIdList,
  		List<Float> xCoordList, List<Float> yCoordList, List<Timestamp> purchaseTimeList,
  		List<Timestamp> retrievedTimeList, List<Boolean> isComplete) {
  	String tablename = DBConstants.TABLE_STRUCTURE_FOR_USER;

  	//did not add generics because eclipse shows errors like: can't accept  (String, List<Integer>), needs (String, List<Object>)
  	Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
  	int numRows = userIdList.size();
    
    List<String> userStructIds = new ArrayList<String>();
    for (int i = 0; i < numRows; i++) {
      userStructIds.add(randomUUID());
    }

    insertParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructIds);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__USER_ID, userIdList);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, structIdList);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__X_COORD, xCoordList);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, yCoordList);
  	
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME, purchaseTimeList);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED, retrievedTimeList);
  	insertParams.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, isComplete);

  	int numInserted = DBConnection.get().insertIntoTableMultipleRows(tablename, 
  			insertParams, numRows);

  	return numInserted;
  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#insertIAPHistoryElem(org.json.JSONObject, int, com.lvl6.info.User, double)
   */
  @Override
  public boolean insertIAPHistoryElem(JSONObject appleReceipt,
      int diamondChange, int coinChange, User user, double cashCost) {
    Map<String, Object> insertParams = new HashMap<String, Object>();
    try {
      String id = randomUUID();
      
      insertParams.put(DBConstants.IAP_HISTORY__ID, id);
      insertParams.put(DBConstants.IAP_HISTORY__USER_ID, user.getId());
      insertParams.put(DBConstants.IAP_HISTORY__TRANSACTION_ID,
          appleReceipt.getString(IAPValues.TRANSACTION_ID));
      insertParams.put(DBConstants.IAP_HISTORY__PURCHASE_DATE,
          new Timestamp(appleReceipt.getLong(IAPValues.PURCHASE_DATE_MS)));
      insertParams.put(DBConstants.IAP_HISTORY__PREMIUMCUR_PURCHASED,
          diamondChange);
      insertParams.put(DBConstants.IAP_HISTORY__REGCUR_PURCHASED,
          coinChange);
      insertParams.put(DBConstants.IAP_HISTORY__CASH_SPENT, cashCost);
      insertParams.put(DBConstants.IAP_HISTORY__UDID, user.getUdid());
      insertParams.put(DBConstants.IAP_HISTORY__PRODUCT_ID,
          appleReceipt.getString(IAPValues.PRODUCT_ID));
      insertParams.put(DBConstants.IAP_HISTORY__QUANTITY,
          appleReceipt.getString(IAPValues.QUANTITY));
      insertParams.put(DBConstants.IAP_HISTORY__BID,
          appleReceipt.getString(IAPValues.BID));
      insertParams.put(DBConstants.IAP_HISTORY__BVRS,
          appleReceipt.getString(IAPValues.BVRS));

      if (appleReceipt.has(IAPValues.APP_ITEM_ID)) {
        insertParams.put(DBConstants.IAP_HISTORY__APP_ITEM_ID,
            appleReceipt.getString(IAPValues.APP_ITEM_ID));
      }
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }
    int numInserted = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_IAP_HISTORY, insertParams);
    if (numInserted == 1) {
      return true;
    }
    return false;
  }


  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#insertReferral(int, int, int)
   */
  @Override
  public boolean insertReferral(int referrerId, int referredId,
      int coinsGivenToReferrer) {
    Map<String, Object> insertParams = new HashMap<String, Object>();

    insertParams.put(DBConstants.REFERRALS__REFERRER_ID, referrerId);
    insertParams.put(DBConstants.REFERRALS__NEWLY_REFERRED_ID, referredId);
    insertParams.put(DBConstants.REFERRALS__TIME_OF_REFERRAL,
        new Timestamp(new Date().getTime()));
    insertParams.put(DBConstants.REFERRALS__COINS_GIVEN_TO_REFERRER,
        new Timestamp(new Date().getTime()));

    int numInserted = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_REFERRAL, insertParams);
    if (numInserted == 1) {
      return true;
    }
    return false;
  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.InsertUtil#insertUser(java.lang.String, java.lang.String, com.lvl6.proto.InfoProto.UserType, com.lvl6.info.Location, java.lang.String, java.lang.String, int, int, int, int, int, int, int, int, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, boolean)
   */
  @Override
  public String insertUser(String name, String udid, int level, int experience, int cash,
  		int oil, int gems, boolean isFake,  String deviceToken, Timestamp createTime,
  		String facebookId, int avatarMonsterId, String email, String fbData) {
    String userId = randomUUID();

    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.USER__ID, userId);
    insertParams.put(DBConstants.USER__NAME, name);
    insertParams.put(DBConstants.USER__LEVEL, level);
    insertParams.put(DBConstants.USER__GEMS, gems);
    insertParams.put(DBConstants.USER__CASH, cash);
    insertParams.put(DBConstants.USER__OIL, oil);
    insertParams.put(DBConstants.USER__EXPERIENCE, experience);
//    insertParams.put(DBConstants.USER__REFERRAL_CODE, newReferCode);
    
    insertParams.put(DBConstants.USER__UDID_FOR_HISTORY, udid);
    insertParams.put(DBConstants.USER__LAST_LOGIN, createTime);
    insertParams.put(DBConstants.USER__DEVICE_TOKEN, deviceToken);
    insertParams.put(DBConstants.USER__IS_FAKE, isFake);
    insertParams.put(DBConstants.USER__CREATE_TIME, createTime);
    
    if (null != facebookId && !facebookId.isEmpty()) {
    	insertParams.put(DBConstants.USER__FACEBOOK_ID, facebookId);
    	insertParams.put(DBConstants.USER__FB_ID_SET_ON_USER_CREATE, true);
    } else {
    	insertParams.put(DBConstants.USER__UDID, udid);
    	insertParams.put(DBConstants.USER__FB_ID_SET_ON_USER_CREATE, false);
    }
    
    insertParams.put(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME, createTime);
    insertParams.put(DBConstants.USER__AVATAR_MONSTER_ID, avatarMonsterId);
    
    if (null != email && !email.isEmpty()) {
    	insertParams.put(DBConstants.USER__EMAIL, email);
    }
    if (null != fbData && !fbData.isEmpty()) {
    	insertParams.put(DBConstants.USER__FB_DATA, fbData);
    }
    insertParams.put(DBConstants.USER__LAST_FREE_BOOSTER_PACK_TIME, createTime);
    
    int numChanged = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_USER, insertParams);
    if (numChanged != 1) {
      userId = null;
    }
    return userId;
  }
  
  @Override
  public int insertPvpLeagueForUser(String userId, int pvpLeagueId, int rank,
			int elo, Timestamp shieldEndTime, Timestamp inBattleShieldEndTime) {
	  String tableName = DBConstants.TABLE_PVP_LEAGUE_FOR_USER;
	  Map<String, Object> insertParams = new HashMap<String, Object>();
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userId);
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID, pvpLeagueId);
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__RANK, rank);
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__ELO, elo);
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME,
			  shieldEndTime);
	  insertParams.put(DBConstants.PVP_LEAGUE_FOR_USER__BATTLE_END_TIME,
			  inBattleShieldEndTime);
	  
	  int numInserted = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
	  return numInserted;
  }


  @Override
  public String insertClan(String name, Timestamp createTime, String description, String tag,
      boolean requestToJoinRequired, int clanIconId) {
    String clanId = randomUUID();
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.CLANS__ID, clanId);
    insertParams.put(DBConstants.CLANS__NAME, name);
//    insertParams.put(DBConstants.CLANS__OWNER_ID, ownerId);
    insertParams.put(DBConstants.CLANS__CREATE_TIME, createTime);
    insertParams.put(DBConstants.CLANS__DESCRIPTION, description);
    insertParams.put(DBConstants.CLANS__TAG, tag);
    insertParams.put(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED, requestToJoinRequired);
    insertParams.put(DBConstants.CLANS__CLAN_ICON_ID, clanIconId);

    int numChanged = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_CLANS, insertParams);
    if (numChanged != 1) {
      clanId = null;
    }
    return clanId;
  }

  @Override
  public boolean insertUserClan(String userId, String clanId, String status, Timestamp requestTime) {
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);
    insertParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
    insertParams.put(DBConstants.CLAN_FOR_USER__STATUS, status);
    insertParams.put(DBConstants.CLAN_FOR_USER__REQUEST_TIME, requestTime);

    int numInserted = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_CLAN_FOR_USER, insertParams);
    if (numInserted == 1) {
      return true;
    }
    return false;
  }

  @Override
  public String insertClanChatPost(String userId, String clanId, String content,
      Timestamp timeOfPost) {
    String wallPostId = randomUUID();
    
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.CLAN_CHAT_POST__ID, wallPostId);
    insertParams.put(DBConstants.CLAN_CHAT_POST__POSTER_ID, userId);
    insertParams.put(DBConstants.CLAN_CHAT_POST__CLAN_ID,
        clanId);
    insertParams.put(DBConstants.CLAN_CHAT_POST__TIME_OF_POST,
        timeOfPost);
    insertParams.put(DBConstants.CLAN_CHAT_POST__CONTENT, content);

    int numChanged = DBConnection.get().insertIntoTableBasicReturnId(
        DBConstants.TABLE_CLAN_CHAT_POST, insertParams);
    if (numChanged != 1) {
      wallPostId = null;
    }
    return wallPostId;
  }
  
  public int insertIntoUserLeaderboardEvent(int leaderboardEventId, int userId, 
      int battlesWonChange, int battlesLostChange, int battlesFledChange) {
    String tablename = DBConstants.TABLE_TOURNAMENT_EVENT_FOR_USER;
    Map<String, Object> insertParams = new HashMap<String, Object>();
    Map<String, Object> relativeUpdates = new HashMap<String, Object>();
    Map<String, Object> absoluteUpdates = null;//new HashMap<String, Object>();
    
    insertParams.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__TOURNAMENT_EVENT_ID, leaderboardEventId);
    insertParams.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__USER_ID, userId);
    //as long as there is an existing row, setting these three values doesn't matter
    //this is here just for the initial insert
    insertParams.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_WON, battlesWonChange);
    insertParams.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_LOST, battlesLostChange);
    insertParams.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_FLED, battlesFledChange);
    
    //this is for the case when there is already an existing row
    relativeUpdates.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_WON, battlesWonChange);
    relativeUpdates.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_LOST, battlesLostChange);
    relativeUpdates.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__BATTLES_FLED, battlesFledChange);
    DBConnection.get().insertOnDuplicateKeyUpdate(tablename, insertParams, relativeUpdates, absoluteUpdates);
    return 0;
  }
  
  
  //0 for isSilver means currency is gold; 1 for isSilver means currency is silver
  public int insertIntoUserCurrencyHistory (String userId, Timestamp date, String resourceType, 
      int currencyChange, int currencyBefore, int currencyAfter, String reasonForChange,
      String details) {
    String userCurrId = randomUUID();
    
    String tableName = DBConstants.TABLE_USER_CURRENCY_HISTORY;
    Map<String, Object> insertParams = new HashMap<String, Object>();

    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__ID, userCurrId);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__USER_ID, userId);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__DATE, date);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__RESOURCE_TYPE, resourceType);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_CHANGE, currencyChange);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE, currencyBefore);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE, currencyAfter);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__REASON_FOR_CHANGE, reasonForChange);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__DETAILS, details);
    
    //number of rows inserted
    int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
    log.info("number of rows inserted into user_currency_history: " + numUpdated);
    return numUpdated;
  }
  
  /*
   * assumptions: all the entries at index i across all the lists, 
   * they make up the values for one row to insert into user_currency_history
   */
  public int insertIntoUserCurrencyHistoryMultipleRows(List<String> userIds, List<Timestamp> dates, 
      List<String> resourceTypes, List<Integer> changesToCurrencies, List<Integer> previousCurrencies, 
      List<Integer> currentCurrencies, List<String> reasonsForChanges, List<String> details) {
    String tablename = DBConstants.TABLE_USER_CURRENCY_HISTORY;
    
    //did not add generics because eclipse shows errors like: can't accept  (String, List<Integer>), needs (String, List<Object>)
    Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
    int numRows = userIds.size();
    
    List<String> userCurrIds = new ArrayList<String>();
    for (int i = 0; i < numRows; i++) {
      userCurrIds.add(randomUUID());
    }

    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__ID, userCurrIds);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__USER_ID, userIds);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__DATE, dates);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__RESOURCE_TYPE, resourceTypes);
    if(null != changesToCurrencies && 0 < changesToCurrencies.size()) {
      insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_CHANGE, changesToCurrencies);
    }
    if(null != previousCurrencies && 0 < previousCurrencies.size()) {
      insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE, previousCurrencies);
    }
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE, currentCurrencies);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__REASON_FOR_CHANGE, reasonsForChanges);
    insertParams.put(DBConstants.USER_CURRENCY_HISTORY__DETAILS, details);
    
    int numInserted = DBConnection.get().insertIntoTableMultipleRows(tablename, 
        insertParams, numRows);
    
    return numInserted;
  }
  
  public int insertIntoLoginHistory(String udid, String userId, Timestamp now, boolean isLogin,
      boolean goingThroughTutorial) {
    String loginId = randomUUID();
    
    String tableName = DBConstants.TABLE_LOGIN_HISTORY;
    Map<String, Object> insertParams = new HashMap<String, Object>();

    insertParams.put(DBConstants.LOGIN_HISTORY__ID, loginId);
    insertParams.put(DBConstants.LOGIN_HISTORY__UDID, udid);
    //if going through tutorial, no id exists
    if(!goingThroughTutorial) {
      insertParams.put(DBConstants.LOGIN_HISTORY__USER_ID, userId);
    }
    insertParams.put(DBConstants.LOGIN_HISTORY__DATE, now);
    insertParams.put(DBConstants.LOGIN_HISTORY__IS_LOGIN, isLogin);
    
    int numInserted = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
    
    return numInserted;
  }
  
  public int insertIntoFirstTimeUsers(String openUdid, String udid, String mac, String advertiserId,
      Timestamp now) {
    String loginId = randomUUID();
    
    String tableName = DBConstants.TABLE_USER_BEFORE_TUTORIAL_COMPLETION;
    Map<String, Object> insertParams = new HashMap<String, Object>();

    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__ID, loginId);
    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID, openUdid);
    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__UDID, udid);
    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__MAC, mac);
    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__ADVERTISER_ID, advertiserId);
    insertParams.put(DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__CREATE_TIME, now);
    
    int numInserted = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
    
    return numInserted;
  }
  
  public int insertIntoBoosterPackPurchaseHistory(String userId, int boosterPackId, 
      Timestamp timeOfPurchase, BoosterItem bi, List<String> userMonsterIds) {
    String tableName = DBConstants.TABLE_BOOSTER_PACK_PURCHASE_HISTORY;
    
    Map<String, Object> insertParams = new HashMap<String, Object>();
    int boosterItemId = bi.getId();
    int monsterId = bi.getMonsterId();
    int numPieces = bi.getNumPieces();
    boolean isComplete = bi.isComplete();
    boolean isSpecial = bi.isSpecial();
    int gemReward = bi.getGemReward();
    float chanceToAppear = bi.getChanceToAppear();
    
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__USER_ID, userId);
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_PACK_ID, boosterPackId);
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__TIME_OF_PURCHASE, timeOfPurchase);
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_ITEM_ID, boosterItemId);
    //monster prize and gem prize are mutually exclusive
    if (monsterId > 0) {
    	insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__MONSTER_ID, monsterId);
    	insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__NUM_PIECES, numPieces);
    	insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__IS_COMPLETE, isComplete);
    	insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__IS_SPECIAL, isSpecial);
    	
    	String userMonsterIdsStr = StringUtils.csvList(userMonsterIds);
    	insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__CHANGED_MONSTER_FOR_USER_IDS,
    			userMonsterIdsStr);
    } else if (gemReward > 0) {
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__GEM_REWARD, gemReward);
    }
    
    insertParams.put(DBConstants.BOOSTER_PACK_PURCHASE_HISTORY__CHANCE_TO_APPEAR, chanceToAppear);
    
    int numInserted = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
    return numInserted;
  }
  
  
  public String insertIntoPrivateChatPosts(String posterId, String recipientId, String content, Timestamp timeOfPost) {
    String wallPostId = randomUUID();
    
    Map<String, Object> insertParams = new HashMap<String, Object>();
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__ID, wallPostId);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID, posterId);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID, recipientId);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT, content);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST, timeOfPost);

    int numChanged = DBConnection.get().insertIntoTableBasic(
        DBConstants.TABLE_USER_PRIVATE_CHAT_POST, insertParams);
    if (numChanged != 1) {
      wallPostId = null;
    }
    return wallPostId;
  }
  
  public List<String> insertIntoPrivateChatPosts(List<String> posterIds, List<String> recipientIds,
      List<String> contents, List<Date> timeOfPosts) {
    String tableName = DBConstants.TABLE_USER_PRIVATE_CHAT_POST;
    
    //did not add generics because eclipse shows errors like: can't accept  (String, List<Integer>), needs (String, List<Object>)
    Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
    int numRows = posterIds.size();

    List<String> postIds = new ArrayList<String>();
    for (int i = 0; i < numRows; i++) {
      postIds.add(randomUUID());
    }

    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__ID, postIds);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__POSTER_ID, posterIds);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID, recipientIds);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__CONTENT, contents);
    insertParams.put(DBConstants.USER_PRIVATE_CHAT_POSTS__TIME_OF_POST, timeOfPosts);
    
    int numChanged = DBConnection.get().insertIntoTableMultipleRows(tableName, insertParams, numRows);
    if (numChanged != numRows) {
      postIds = new ArrayList<String>();
    }
    return postIds;
  }
  
  //returns the id
  public String insertIntoUserTaskReturnId(String userId, int taskId, int expGained,
  		int cashGained, int oilGained, Timestamp startTime, int taskStageId) {
    String userTaskId = randomUUID();
	  
	  //for recording what-dropped in which-stage
	  Map<String, Object> row = new HashMap<String, Object>();
    row.put(DBConstants.TASK_FOR_USER_ONGOING__ID, userTaskId);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__USER_ID, userId);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__TASK_ID, taskId);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__EXP_GAINED, expGained);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__CASH_GAINED, cashGained);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__OIL_GAINED, oilGained);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__NUM_REVIVES, 0);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__START_TIME, startTime);
	  row.put(DBConstants.TASK_FOR_USER_ONGOING__TASK_STAGE_ID, taskStageId);
	  
	  int numChanged = DBConnection.get().insertIntoTableBasic(
			  DBConstants.TABLE_TASK_FOR_USER_ONGOING, row);
	  if (numChanged != 1) {
	    userTaskId = null;
	  }
	  return userTaskId;
  }
  
  @Override
  public int insertIntoTaskHistory(String userTaskId, String userId, int taskId,
		  int expGained, int cashGained, int oilGained, int numRevives, Timestamp startTime,
		  Timestamp endTime, boolean userWon, boolean cancelled, int taskStageId) {
	  Map<String, Object> insertParams = new HashMap<String, Object>();
	  
	  insertParams.put(DBConstants.TASK_HISTORY__TASK_FOR_USER_ID, userTaskId);
	  insertParams.put(DBConstants.TASK_HISTORY__USER_ID, userId);
	  insertParams.put(DBConstants.TASK_HISTORY__TASK_ID, taskId);
	  insertParams.put(DBConstants.TASK_HISTORY__EXP_GAINED, expGained);
	  insertParams.put(DBConstants.TASK_HISTORY__CASH_GAINED, cashGained);
	  insertParams.put(DBConstants.TASK_HISTORY__OIL_GAINED, oilGained);
	  insertParams.put(DBConstants.TASK_HISTORY__NUM_REVIVES, numRevives);
	  insertParams.put(DBConstants.TASK_HISTORY__START_TIME, startTime);
	  insertParams.put(DBConstants.TASK_HISTORY__END_TIME, endTime);
	  insertParams.put(DBConstants.TASK_HISTORY__USER_WON, userWon);
	  insertParams.put(DBConstants.TASK_HISTORY__CANCELLED, cancelled);
	  insertParams.put(DBConstants.TASK_HISTORY__TASK_STAGE_ID, taskStageId);
	  
	  int numInserted = DBConnection.get().insertIntoTableBasic(
			  DBConstants.TABLE_TASK_HISTORY, insertParams);
	  return numInserted; 
  }
  
  @Override
  public int insertIntoTaskForUserCompleted(String userId, int taskId, 
  		Timestamp timeOfEntry) {
  	Map<String, Object> insertParams = new HashMap<String, Object>();
  	
  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID, userId);
  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID, taskId);
  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__TIME_OF_ENTRY, timeOfEntry);
  	
  	int numInserted = DBConnection.get().insertIntoTableBasic(
			  DBConstants.TABLE_TASK_FOR_USER_COMPLETED, insertParams);
	  return numInserted;
  }
  
  @Override
  public int insertIntoTaskForUserCompleted(List<String> userIdList, List<Integer> taskIdList,
  		List<Timestamp> timeOfEntryList) {
  	String tablename = DBConstants.TABLE_TASK_FOR_USER_COMPLETED;

  	//did not add generics because eclipse shows errors like: can't accept  (String, List<Integer>), needs (String, List<Object>)
  	Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
  	int numRows = userIdList.size();

  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID,
  			userIdList);														
  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID, taskIdList);
  	insertParams.put(DBConstants.TASK_FOR_USER_COMPLETED__TIME_OF_ENTRY, timeOfEntryList);

  	int numInserted = DBConnection.get().insertIntoTableMultipleRows(tablename, 
  			insertParams, numRows);

  	return numInserted;
  }

  /*
	@Override
	public int insertIntoUserTaskStage(List<Long> userTaskIds, List<Integer> stageNums,
			List<Integer> tsmIds, List<String> monsterTypes, List<Integer> expsGained,
			List<Integer> cashGained, List<Integer> oilGained, List<Boolean> monsterPiecesDropped,
			Map<Integer, Integer> tsmIdToItemId) {
		//even if a taskStageMonsterId has multiple items, just choose the first one
		List<Integer> itemIds = new ArrayList<Integer>();
		
		for (Integer tsmId : tsmIds) {
			
			if (!tsmIdToItemId.containsKey(tsmId)) {
				//0 in db means no item dropped
				itemIds.add(0);
				continue;
			}
			
			//task stage monster has an item drop associated with it.
			int itemId = tsmIdToItemId.get(tsmId);
			if (-1 == itemId) {
				itemId = 0;
			}
			itemIds.add(itemId);
			
		}

		String tablename = DBConstants.TABLE_TASK_STAGE_FOR_USER;
    Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
		int numRows = stageNums.size();

		insertParams.put(DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, userTaskIds);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__STAGE_NUM, stageNums);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID, tsmIds);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__MONSTER_TYPE, monsterTypes);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__EXP_GAINED, expsGained);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__CASH_GAINED, cashGained);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__OIL_GAINED, oilGained);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED, monsterPiecesDropped);
    insertParams.put(DBConstants.TASK_STAGE_FOR_USER__ITEM_ID_DROPPED, itemIds);
    
    int numInserted = DBConnection.get().insertIntoTableMultipleRows(tablename, 
        insertParams, numRows);
    
    return numInserted;
	}
	*/

  	@Override
  	public List<String> insertIntoUserTaskStage(List<TaskStageForUser> tsfuList) {
  		String tablename = DBConstants.TABLE_TASK_STAGE_FOR_USER;
  		
  		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
  		List<String> tsfuIds = new ArrayList<String>();
  		for (TaskStageForUser tsfu : tsfuList) {
  		  String tsfuId = randomUUID();
  		  tsfuIds.add(tsfuId);
  		  
  			Map<String, Object> newRow = new HashMap<String, Object>();

        newRow.put(DBConstants.TASK_STAGE_FOR_USER__ID, tsfuId);
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, tsfu.getUserTaskId());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__STAGE_NUM, tsfu.getStageNum());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID, tsfu.getTaskStageMonsterId());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__MONSTER_TYPE, tsfu.getMonsterType());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__EXP_GAINED, tsfu.getExpGained());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__CASH_GAINED, tsfu.getCashGained());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__OIL_GAINED, tsfu.getOilGained());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED, tsfu.isMonsterPieceDropped());
  			newRow.put(DBConstants.TASK_STAGE_FOR_USER__ITEM_ID_DROPPED, tsfu.getItemIdDropped());
  		    
  			newRows.add(newRow);
  		}
  		
  		
  		int numChanged = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tablename, newRows);
  		if (numChanged != tsfuList.size()) {
  		  tsfuIds = new ArrayList<String>();
  		}
  		return tsfuIds;
  	}
  
	@Override
	public int insertIntoTaskStageHistory(List<String> userTaskStageIds,
			List<String> userTaskIds, List<Integer> stageNums, List<Integer> tsmIds,
			List<String> monsterTypes, List<Integer> expsGained, List<Integer> cashGained,
			List<Integer> oilGained, List<Boolean> monsterPiecesDropped,
			List<Integer> itemIdDropped, List<Integer> monsterIdDrops,
			List<Integer> monsterDropLvls) {
		String tablename = DBConstants.TABLE_TASK_STAGE_HISTORY;
		int numRows = stageNums.size();
		
    Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__ID, userTaskStageIds);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__TASK_FOR_USER_ID, userTaskIds);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__STAGE_NUM, stageNums);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__TASK_STAGE_MONSTER_ID, tsmIds);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__MONSTER_TYPE, monsterTypes);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__EXP_GAINED, expsGained);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__CASH_GAINED, cashGained);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__OIL_GAINED, oilGained);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__MONSTER_PIECE_DROPPED, monsterPiecesDropped);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__ITEM_ID_DROPPED, itemIdDropped);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__MONSTER_ID_DROPPED, monsterIdDrops);
    insertParams.put(DBConstants.TASK_STAGE_HISTORY__MONSTER_DROPPED_LVL, monsterDropLvls);
    
    int numInserted = DBConnection.get().insertIntoTableMultipleRows(tablename, 
        insertParams, numRows);
    
    return numInserted;
	}

	/*
	 * README!!!!!!!!!!!!!!!
   * assumptions: all the entries at index i across all the lists, 
   * they make up the values for one row to insert into the table
   */
	@Override
	public List<String> insertIntoMonsterForUserReturnIds(String userId,
			List<MonsterForUser> userMonsters, String sourceOfPieces, Date combineStartDate) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
		Timestamp combineStartTime = new Timestamp(combineStartDate.getTime());
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		sourceOfPieces = " " + sourceOfPieces + " ";
		
		List<String> mfuIds = new ArrayList<String>();
		for(int i = 0; i < userMonsters.size(); i++){
			MonsterForUser mfu = userMonsters.get(i);
			int monsterId = mfu.getMonsterId();
			int currentExp = mfu.getCurrentExp();
			int currentLvl = mfu.getCurrentLvl();
			int currentHp = mfu.getCurrentHealth();
			int numPieces = mfu.getNumPieces();
			boolean hasAllPieces = mfu.isHasAllPieces();
			boolean isComplete = mfu.isComplete();
			int teamSlotNum = mfu.getTeamSlotNum();
			String userMonsterId = randomUUID();
			
			mfuIds.add(userMonsterId);
			
			//Since this is a new monster just use argument for sourceOfPieces
			
			Map<String, Object> row = new HashMap<String, Object>();
      row.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);
			row.put(DBConstants.MONSTER_FOR_USER__USER_ID, userId);
			row.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, monsterId);
			row.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, currentExp);
			row.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, currentLvl);
			row.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, currentHp);
			row.put(DBConstants.MONSTER_FOR_USER__NUM_PIECES, numPieces);
			row.put(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES, hasAllPieces);
			row.put(DBConstants.MONSTER_FOR_USER__IS_COMPLETE, isComplete);
			row.put(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME, combineStartTime);
			row.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, teamSlotNum);
			row.put(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES, sourceOfPieces);
			newRows.add(row);
		}
		
		int numUpdated = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tableName, newRows);
		if (numUpdated != userMonsters.size()) {
		  mfuIds = new ArrayList<String>();
		}
		log.info("monsterForUserIds= " + mfuIds);
		return mfuIds;
	}
	
	@Override
	public int insertIntoMonsterForUserDeleted(String userId, List<String> delReasons,
			List<String> deleteDetails, List<MonsterForUser> userMonsters, Date deleteDate) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER_DELETED;
		List<Object> monsterForUserIds = new ArrayList<Object>();
		List<Object> userIds = new ArrayList<Object>();
		List<Object> monsterIds = new ArrayList<Object>(); 
		List<Object> currentExps = new ArrayList<Object>();
		List<Object> currentLvls = new ArrayList<Object>();
		List<Object> currentHps = new ArrayList<Object>();
		List<Object> numPiecesList = new ArrayList<Object>();
		List<Object> areComplete = new ArrayList<Object>();
		List<Object> teamSlotNums = new ArrayList<Object>();
		List<Object> sourcesOfPieces = new ArrayList<Object>();
		List<Object> deleteReasons = new ArrayList<Object>();
		List<Object> deleteTimes = new ArrayList<Object>();
		Timestamp deleteTime = new Timestamp(deleteDate.getTime());
		
		for(int i = 0; i < userMonsters.size(); i++){
			MonsterForUser mfu = userMonsters.get(i);
			String monsterForUserId = mfu.getId();
			int monsterId = mfu.getMonsterId();
			int currentExp = mfu.getCurrentExp();
			int currentLvl = mfu.getCurrentLvl();
			int currentHp = mfu.getCurrentHealth();
			int numPieces = mfu.getNumPieces();
			boolean isComplete = mfu.isComplete();
			int teamSlotNum = mfu.getTeamSlotNum();
			String sourceOfPieces = mfu.getSourceOfPieces();
			String deleteReason = delReasons.get(i);
			
			monsterForUserIds.add(monsterForUserId);
			userIds.add(userId);
			monsterIds.add(monsterId);
			currentExps.add(currentExp);
			currentLvls.add(currentLvl);
			currentHps.add(currentHp);
			numPiecesList.add(numPieces);
			areComplete.add(isComplete);
			teamSlotNums.add(teamSlotNum);
			sourcesOfPieces.add(sourceOfPieces);
			deleteReasons.add(deleteReason);
			deleteTimes.add(deleteTime);
		}
		
		Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__ID, monsterForUserIds);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__USER_ID, userIds);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__MONSTER_ID, monsterIds);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__CURRENT_EXPERIENCE, currentExps);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__CURRENT_LEVEL, currentLvls);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__CURRENT_HEALTH, currentHps);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__NUM_PIECES, numPiecesList);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__IS_COMPLETE, areComplete);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__TEAM_SLOT_NUM, teamSlotNums);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__SOURCE_OF_PIECES, sourcesOfPieces);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__DELETED_REASON, deleteReasons);
		insertParams.put(DBConstants.MONSTER_FOR_USER_DELETED__DELETED_TIME, deleteTimes);
		int numRows = userMonsters.size();
		
		int numInserted = DBConnection.get().insertIntoTableMultipleRows(tableName, 
        insertParams, numRows);
    
    return numInserted;
	}

	@Override
	public List<String> insertIntoUserFbInviteForSlot(String userId, List<String> facebookIds,
			Timestamp curTime, Map<String, String> fbIdsToUserStructIds,
  		Map<String, Integer> fbIdsToUserStructsFbLvl) {
		String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		List<String> inviteIds = new ArrayList<String>();
		for (String fbId : facebookIds) {
		  String userStructId = fbIdsToUserStructIds.get(fbId);
			Integer userStructFbLvl = fbIdsToUserStructsFbLvl.get(fbId);
			String inviteId = randomUUID();
			
			inviteIds.add(inviteId);
			
			Map<String, Object> newRow = new HashMap<String, Object>();
      newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID, inviteId);
			newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID, userId);
			newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID, fbId);
			newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_OF_INVITE, curTime);
			newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID, userStructId);
			newRow.put(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_FB_LVL, userStructFbLvl);
			newRows.add(newRow);
		}
		
		
		int numUpdated = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tableName, newRows);
		if (numUpdated != facebookIds.size()) {
		  inviteIds = new ArrayList<String>();
		}
		return inviteIds;
	}

	//the user monster ids will be ordered in ascending order, and this will determine
	//which one is one and which one is two
	@Override
	public int insertIntoMonsterEvolvingForUser(String userId, String catalystUserMonsterId,
			List<String> userMonsterIds, Timestamp startTime) {
		Collections.sort(userMonsterIds);
		String userMonsterIdOne = userMonsterIds.get(0);
		String userMOnsterIdTwo = userMonsterIds.get(1);
		
		String tableName = DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER;
		
		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID,
				catalystUserMonsterId);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE,
				userMonsterIdOne);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO,
				userMOnsterIdTwo);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__START_TIME, startTime);

		int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
		
		return numUpdated;
	}
	
	@Override
	public int insertIntoUpdateEventPersistentForUser(String userId, int eventId, Timestamp now) {
		String tableName = DBConstants.TABLE_EVENT_PERSISTENT_FOR_USER;
		
		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID, eventId);
		insertParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY, now);

    Map<String, Object> relativeUpdates = null;//new HashMap<String, Object>();
    Map<String, Object> absoluteUpdates = new HashMap<String, Object>();
    absoluteUpdates.put(DBConstants.EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY, now);
    

    int numInserted = DBConnection.get().insertOnDuplicateKeyUpdate(tableName,
    		insertParams, relativeUpdates, absoluteUpdates);
    return numInserted;
		
	}
	
	public int insertUpdatePvpBattleForUser(String attackerId, String defenderId,
			int attackerWinEloChange, int defenderLoseEloChange, int attackerLoseEloChange,
			int defenderWinEloChange, Timestamp battleStartTime) {
		String tableName = DBConstants.TABLE_PVP_BATTLE_FOR_USER;
		
		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID, attackerId);
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_ID, defenderId);
		//elo changes when attacker wins
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_WIN_ELO_CHANGE,
				attackerWinEloChange);
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_LOSE_ELO_CHANGE,
				defenderLoseEloChange);
		
		//elo changes when attacker loses
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_LOSE_ELO_CHANGE,
				attackerLoseEloChange);
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_WIN_ELO_CHANGE,
				defenderWinEloChange);
				
		insertParams.put(DBConstants.PVP_BATTLE_FOR_USER__BATTLE_START_TIME,
				battleStartTime);
		
		Map<String, Object> relativeUpdates = null;//new HashMap<String, Object>();
		
		//if row exists already (which it shouldn't) just replace all the values
		Map<String, Object> absoluteUpdates = new HashMap<String, Object>();
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_ID, defenderId);
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_WIN_ELO_CHANGE,
				attackerWinEloChange);
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_LOSE_ELO_CHANGE,
				defenderLoseEloChange);
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_LOSE_ELO_CHANGE,
				attackerLoseEloChange);
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_WIN_ELO_CHANGE,
				defenderWinEloChange);
		absoluteUpdates.put(DBConstants.PVP_BATTLE_FOR_USER__BATTLE_START_TIME,
				battleStartTime);
		
		
		int numInserted = DBConnection.get().insertOnDuplicateKeyUpdate(tableName,
    		insertParams, relativeUpdates, absoluteUpdates);
    return numInserted;
	}
	
	@Override
	public int insertIntoClanEventPersistentForClan(String clanId, int clanEventPersistentId,
			int clanRaidId, int clanRaidStageId, Timestamp stageStartTime,
			int clanRaidStageMonsterId, Timestamp stageMonsterStartTime) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
		
		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_EVENT_PERSISTENT_ID,
				clanEventPersistentId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CR_ID, clanRaidId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID, clanRaidStageId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME, 
				stageStartTime);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID, clanRaidStageMonsterId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME,
				stageMonsterStartTime);

		int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);

		return numUpdated;
	}
	
	@Override
	public int insertIntoUpdateMonstersClanEventPersistentForUser(String userId, String clanId,
			int clanRaidId, List<String> userMonsterIds) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;

		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, clanId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID, clanRaidId);

		Map<String, Object> relativeUpdates = null;//new HashMap<String, Object>();
		
		//if row exists already, just replace all the monster id values
		Map<String, Object> absoluteUpdates = new HashMap<String, Object>();
		
		if (userMonsterIds.size() >= 1) {
			String userMonsterId = userMonsterIds.get(0);
			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_ONE, userMonsterId);
			absoluteUpdates.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_ONE, userMonsterId);
		}
		if (userMonsterIds.size() >= 2) {
		  String userMonsterId = userMonsterIds.get(1);
			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_TWO, userMonsterId);
			absoluteUpdates.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_TWO, userMonsterId);
		}
		if (userMonsterIds.size() >= 3) {
		  String userMonsterId = userMonsterIds.get(2);
			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_THREE, userMonsterId);
			absoluteUpdates.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_THREE, userMonsterId);
		}
		
		int numInserted = DBConnection.get().insertOnDuplicateKeyUpdate(tableName,
    		insertParams, relativeUpdates, absoluteUpdates);
    return numInserted;
	}
	
	@Override
	public int insertIntoClanEventPersistentForClanHistory(String clanId,
			Timestamp timeOfEntry, int clanEventPersistentId, int crId, int crsId,
			Timestamp stageStartTime, int crsmId, Timestamp stageMonsterStartTime, boolean won) {
		
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY;
		
		Map<String, Object> insertParams = new HashMap<String, Object>();
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CLAN_ID, clanId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__TIME_OF_ENTRY, timeOfEntry);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CLAN_EVENT_PERSISTENT_ID,
				clanEventPersistentId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CR_ID, crId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CRS_ID, crsId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__STAGE_START_TIME, 
				stageStartTime);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CRSM_ID, crsmId);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__STAGE_MONSTER_START_TIME,
				stageMonsterStartTime);
		insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__WON, won);

		int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);

		return numUpdated;
	}

		//SAVE CLAN RAID USER HISTORY 
		@Override
		public int insertIntoCepfuRaidHistory(Integer clanEventId, Timestamp now,
				Map<String, ClanEventPersistentForUser> clanUserInfo) {
			String tableName = DBConstants.TABLE_CEPFU_RAID_HISTORY;
			
			List<Object> userIdList = new ArrayList<Object>();
			List<Object> timeOfEntryList = new ArrayList<Object>();
			List<Object> clanIdList = new ArrayList<Object>();
			List<Object> clanEventPersistentIdList = new ArrayList<Object>();
			List<Object> crIdList = new ArrayList<Object>();
			List<Object> crDmgDoneList = new ArrayList<Object>();
			int clanCrDmg = 0;
			List<Object> userMonsterIdOneList = new ArrayList<Object>();
			List<Object> userMonsterIdTwoList = new ArrayList<Object>();
			List<Object> userMonsterIdThreeList = new ArrayList<Object>();
			for(String userId  : clanUserInfo.keySet()){
				ClanEventPersistentForUser cepfu = clanUserInfo.get(userId);
				
				userIdList.add(userId);
				timeOfEntryList.add(now);
				clanIdList.add(cepfu.getClanId());
				clanEventPersistentIdList.add(clanEventId);
				crIdList.add(cepfu.getCrId());
				
				int crDmgDone = cepfu.getCrDmgDone() + cepfu.getCrsDmgDone() + cepfu.getCrsmDmgDone();
				crDmgDoneList.add(crDmgDone);
				
				clanCrDmg += crDmgDone;
				
				userMonsterIdOneList.add(cepfu.getUserMonsterIdOne());
				userMonsterIdOneList.add(cepfu.getUserMonsterIdTwo());
				userMonsterIdThreeList.add(cepfu.getUserMonsterIdThree());
			}
			
			int size = clanUserInfo.size();
			List<Integer> clanCrDmgIntList = Collections.nCopies(size, clanCrDmg);
			List<Object> clanCrDmgList = new ArrayList<Object>(clanCrDmgIntList);
			
			Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__USER_ID, userIdList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY, timeOfEntryList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__CLAN_ID, clanIdList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__CLAN_EVENT_PERSISTENT_ID,
					clanEventPersistentIdList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__CR_ID, crIdList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__CR_DMG_DONE, crDmgDoneList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__CLAN_CR_DMG, clanCrDmgList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_ONE, userMonsterIdOneList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_TWO, userMonsterIdTwoList);
			insertParams.put(DBConstants.CEPFU_RAID_HISTORY__USER_MONSTER_ID_THREE, userMonsterIdThreeList);
			int numRows = clanUserInfo.size();
			
			int numInserted = DBConnection.get().insertIntoTableMultipleRows(tableName, 
	        insertParams, numRows);
	    
	    return numInserted;
		}
		
		@Override
		public int insertIntoCepfuRaidStageHistory(Integer clanEventId,
				Timestamp crsStartTime, Timestamp crsEndTime, int stageHp,
				Map<String, ClanEventPersistentForUser> clanUserInfo) {
			String tableName = DBConstants.TABLE_CEPFU_RAID_STAGE_HISTORY;
			
			List<Object> userIdList = new ArrayList<Object>();
			List<Object> crsStartTimeList = new ArrayList<Object>();
			List<Object> clanIdList = new ArrayList<Object>();
			List<Object> clanEventPersistentIdList = new ArrayList<Object>();
			List<Object> crsIdList = new ArrayList<Object>();
			List<Object> crsDmgDoneList = new ArrayList<Object>();
			List<Object> stageHealthList = new ArrayList<Object>();
			List<Object> crsEndTimeList = new ArrayList<Object>();
			for(String userId  : clanUserInfo.keySet()){
				ClanEventPersistentForUser cepfu = clanUserInfo.get(userId);
				
				userIdList.add(userId);
				crsStartTimeList.add(crsStartTime);
				clanIdList.add(cepfu.getClanId());
				clanEventPersistentIdList.add(clanEventId);
				crsIdList.add(cepfu.getCrsId());
				
				int crsDmgDone = cepfu.getCrsDmgDone() + cepfu.getCrsmDmgDone();
				crsDmgDoneList.add(crsDmgDone);
				stageHealthList.add(stageHp);
				crsEndTimeList.add(cepfu.getUserMonsterIdTwo());
			}
			
			Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__USER_ID, userIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME,
					crsStartTimeList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_ID, clanIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_EVENT_PERSISTENT_ID,
					clanEventPersistentIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_ID, crsIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_DMG_DONE, crsDmgDoneList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__STAGE_HEALTH, stageHealthList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_END_TIME, crsEndTimeList);
			int numRows = clanUserInfo.size();
			
			int numInserted = DBConnection.get().insertIntoTableMultipleRows(tableName, 
	        insertParams, numRows);
	    
	    return numInserted;
		}
		
		@Override
		public int insertIntoCepfuRaidStageMonsterHistory(Timestamp crsmEndTime,
				Map<String, ClanEventPersistentForUser> clanUserInfo,
				ClanEventPersistentForClan cepfc) {
			String tableName = DBConstants.TABLE_CEPFU_RAID_STAGE_MONSTER_HISTORY;
			int clanEventId = cepfc.getClanEventPersistentId();
			
			List<Object> userIdList = new ArrayList<Object>();
			List<Object> crsmStartTimeList = new ArrayList<Object>();
			List<Object> clanIdList = new ArrayList<Object>();
			List<Object> clanEventPersistentIdList = new ArrayList<Object>();
			List<Object> crsIdList = new ArrayList<Object>();
			List<Object> crsmIdList = new ArrayList<Object>();
			List<Object> crsmDmgDoneList = new ArrayList<Object>();
			List<Object> crsmEndTimeList = new ArrayList<Object>();
			
			for(String userId  : clanUserInfo.keySet()){
				ClanEventPersistentForUser cepfu = clanUserInfo.get(userId);
				
				userIdList.add(userId);
				Date crsmStartDate = cepfc.getStageMonsterStartTime();
				crsmStartTimeList.add(new Timestamp(crsmStartDate.getTime()));
				clanIdList.add(cepfu.getClanId());
				clanEventPersistentIdList.add(clanEventId);
				crsIdList.add(cepfu.getCrsId());
				crsmIdList.add(cepfu.getCrsmId());
				
				int crsmDmgDone = cepfu.getCrsmDmgDone();
				crsmDmgDoneList.add(crsmDmgDone);
				crsmEndTimeList.add(crsmEndTime);
			}
			
			Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__USER_ID, userIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_START_TIME,
					crsmStartTimeList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CLAN_ID, clanIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CLAN_EVENT_PERSISTENT_ID,
					clanEventPersistentIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CRS_ID, crsIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_ID, crsmIdList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_DMG_DONE,
					crsmDmgDoneList);
			insertParams.put(DBConstants.CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_END_TIME,
					crsmEndTimeList);
			int numRows = clanUserInfo.size();
			
			int numInserted = DBConnection.get().insertIntoTableMultipleRows(tableName, 
	        insertParams, numRows);
	    
	    return numInserted;
		}

//		@Override
//		public int insertIntoCepUserReward(Timestamp crsStartTime, int crsId,
//				Timestamp crsEndTime, int clanEventId,
//				List<ClanEventPersistentUserReward> userRewards) {
//			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_USER_REWARD;
//			
//			List<Integer> userIdList = new ArrayList<Integer>();
//			List<Timestamp> crsStartTimeList = new ArrayList<Timestamp>();
//			List<Integer> crsIdList = new ArrayList<Integer>();
//			List<Timestamp> crsEndTimeList = new ArrayList<Timestamp>();
//			List<String> resourceTypeList = new ArrayList<String>();
//			List<Integer> staticDataIdList = new ArrayList<Integer>();
//			List<Integer> quantityList = new ArrayList<Integer>();
//			List<Integer> clanEventPersistentIdList = new ArrayList<Integer>();
//			
//			for (ClanEventPersistentUserReward reward : userRewards) {
//				int userId = reward.getUserId();
//				String resourceType = reward.getResourceType();
//				int staticDataId = reward.getStaticDataId();
//				int quantity = reward.getQuantity();
//				
//				userIdList.add(userId);
//				crsStartTimeList.add(crsStartTime);
//				crsIdList.add(crsId);
//				crsEndTimeList.add(crsEndTime);
//				resourceTypeList.add(resourceType);
//				staticDataIdList.add(staticDataId);
//				quantityList.add(quantity);
//				clanEventPersistentIdList.add(clanEventId);
//			}
//			
//			Map<String, List<?>> insertParams = new HashMap<String, List<?>>();
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID, userIdList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME,
//					crsStartTimeList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_ID, crsIdList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_END_TIME,
//					crsEndTimeList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__RESOURCE_TYPE, 
//					resourceTypeList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__STATIC_DATA_ID,
//					staticDataIdList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__QUANTITY,
//					quantityList);
//			insertParams.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CLAN_EVENT_PERSISTENT_ID,
//					clanEventPersistentIdList);
//			
//			int numRows = userRewards.size();
//			
//			int numInserted = DBConnection.get().insertIntoTableMultipleRows(tableName, 
//	        insertParams, numRows);
//	    
//	    return numInserted;
//		}
		
		@Override                                                                                  
		public List<String> insertIntoCepUserReward(Timestamp crsStartTime, int crsId,                      
				Timestamp crsEndTime, int clanEventId,                                                 
				List<ClanEventPersistentUserReward> userRewards) {                                     
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_USER_REWARD;                  

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			
			List<String> rewardIds = new ArrayList<String>();
			for (ClanEventPersistentUserReward reward : userRewards) {                               
				Map<String, Object> newRow = new HashMap<String, Object>();
				
				String userId = reward.getUserId();                                                       
				String resourceType = reward.getResourceType();                                        
				int staticDataId = reward.getStaticDataId();                                           
				int quantity = reward.getQuantity();   
				String rewardId = randomUUID();
				
				rewardIds.add(rewardId);

        newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__ID, rewardId);  
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID, userId);    
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME,          
						crsStartTime);                                                                   
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_ID, crsId);      
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_END_TIME, crsEndTime);                                                                     
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__RESOURCE_TYPE,           
						resourceType);                                                                   
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__STATIC_DATA_ID,          
						staticDataId);                                                                   
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__QUANTITY, quantity);                                                                       
				newRow.put(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CLAN_EVENT_PERSISTENT_ID,
						clanEventId);                                                          
				
				newRows.add(newRow);
			}                                                                                        
			int numUpdated = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tableName, newRows);                        
			if (numUpdated != userRewards.size()) {
			  rewardIds = new ArrayList<String>();
			}
			
			return rewardIds;                                                                      
		}                                                                                          
		
		@Override
		public int insertIntoPvpBattleHistory(String attackerId, String defenderId,
				Timestamp battleEndTime, Timestamp battleStartTime, int attackerEloChange,
				int attackerEloBefore, int defenderEloChange, int defenderEloBefore,
				int attackerPrevLeague, int attackerCurLeague, int defenderPrevLeague,
				int defenderCurLeague, int attackerPrevRank, int attackerCurRank,
				int defenderPrevRank, int defenderCurRank, int attackerOilChange,
				int defenderOilChange, int attackerCashChange, int defenderCashChange,
				float nuPvpDmgMultiplier,  boolean attackerWon, boolean cancelled,
				boolean gotRevenge, boolean displayToDefender) {
			
			String tableName = DBConstants.TABLE_PVP_BATTLE_HISTORY;
			
			Map <String, Object> insertParams = new HashMap<String, Object>();
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID, attackerId);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID, defenderId);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME, battleEndTime);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__BATTLE_START_TIME, battleStartTime);
			
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ELO_CHANGE,
					attackerEloChange);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ELO_BEFORE,
					attackerEloBefore);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ELO_AFTER,
					attackerEloBefore + attackerEloChange);
			
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ELO_CHANGE,
					defenderEloChange);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ELO_BEFORE,
					defenderEloBefore);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ELO_AFTER,
					defenderEloBefore + defenderEloChange);

			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE,
					attackerPrevLeague);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE,
					attackerCurLeague);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE,
					defenderPrevLeague);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE,
					defenderCurLeague);
			
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK,
					attackerPrevRank);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK,
					attackerCurRank);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK,
					defenderPrevRank);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK,
					defenderCurRank);
			
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CASH_CHANGE, attackerCashChange);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE, defenderCashChange);
			
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_OIL_CHANGE, attackerOilChange);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE, defenderOilChange);

			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__PVP_DMG_MULTIPLIER, nuPvpDmgMultiplier);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_WON, attackerWon);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__CANCELLED, cancelled);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE, gotRevenge);
			insertParams.put(DBConstants.PVP_BATTLE_HISTORY__DISPLAY_TO_USER, displayToDefender);

			int numUpdated = DBConnection.get().insertIntoTableBasic(tableName, insertParams);
			return numUpdated;
		}
		
		@Override
		public List<String> insertIntoObstaclesForUserGetIds(String userId,
				List<ObstacleForUser> ofuList) {
			String tableName = DBConstants.TABLE_OBSTACLE_FOR_USER;                  

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			
			List<String> ofuIds = new ArrayList<String>();
			for (ObstacleForUser ofu : ofuList) {                               
				Map<String, Object> newRow = new HashMap<String, Object>();
				
				int obstacleId = ofu.getObstacleId();                                           
				int xcoord = ofu.getXcoord();
				int ycoord = ofu.getYcoord();
				String orientation = ofu.getOrientation();
				String ofuId = randomUUID();
				
				ofuIds.add(ofuId);

        newRow.put(DBConstants.OBSTACLE_FOR_USER__ID, ofuId);   
				newRow.put(DBConstants.OBSTACLE_FOR_USER__USER_ID, userId);    
				newRow.put(DBConstants.OBSTACLE_FOR_USER__OBSTACLE_ID, obstacleId);                                                                   
				newRow.put(DBConstants.OBSTACLE_FOR_USER__XCOORD, xcoord);      
				newRow.put(DBConstants.OBSTACLE_FOR_USER__YCOORD, ycoord);                                                                     
				newRow.put(DBConstants.OBSTACLE_FOR_USER__ORIENTATION, orientation);                                                                   
				
				newRows.add(newRow);
			}                                                                                        
			int numUpdated = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tableName,
					newRows);
			if (numUpdated != ofuList.size()) {
			  ofuIds = new ArrayList<String>();
			}
			
			return ofuIds;            
		}
		
		@Override
		public List<String> insertIntoMiniJobForUserGetIds(String userId,
				List<MiniJobForUser> mjfuList) {
			String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

			List<String> mjIds = new ArrayList<String>();
			for (MiniJobForUser mjfu : mjfuList) {
				Map<String, Object> newRow = new HashMap<String, Object>();

				String mjId = randomUUID();
				mjIds.add(mjId);

        newRow.put(DBConstants.MINI_JOB_FOR_USER__ID, mjId);
				newRow.put(DBConstants.MINI_JOB_FOR_USER__USER_ID, userId);    
				newRow.put(DBConstants.MINI_JOB_FOR_USER__MINI_JOB_ID,
						mjfu.getMiniJobId());                                                                   
				newRow.put(DBConstants.MINI_JOB_FOR_USER__BASE_DMG_RECEIVED,
						mjfu.getBaseDmgReceived());      
				newRow.put(DBConstants.MINI_JOB_FOR_USER__DURATION_SECONDS,
						mjfu.getDurationSeconds());

				newRows.add(newRow);
			}                                                                                        
			int numUpdated = DBConnection.get().insertIntoTableBasicReturnNumUpdated(tableName, newRows);
			if (numUpdated != mjfuList.size()) {
			  mjIds = new ArrayList<String>();
			}
			
			return mjIds;            

		}
		
		@Override
		public int insertIntoUpdateUserItem(String userId, int itemId, int delta) {
			String tableName = DBConstants.TABLE_ITEM_FOR_USER;

			Map<String, Object> insertParams = new HashMap<String, Object>();
			insertParams.put(DBConstants.ITEM_FOR_USER__USER_ID, userId);
			insertParams.put(DBConstants.ITEM_FOR_USER__ITEM_ID, itemId);
			insertParams.put(DBConstants.ITEM_FOR_USER__QUANTITY, delta);

			Map<String, Object> relativeUpdates = new HashMap<String, Object>();
			relativeUpdates.put(DBConstants.ITEM_FOR_USER__QUANTITY, delta);
			
			Map<String, Object> absoluteUpdates = null;


			int numInserted = DBConnection.get().insertOnDuplicateKeyUpdate(tableName,
				insertParams, relativeUpdates, absoluteUpdates);
			return numInserted;
		}
		
		@Override
		public List<String> insertIntoClanHelpGetId( List<ClanHelp> solicitations )
		{
			String tableName = DBConstants.TABLE_CLAN_HELP;

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

			List<String> chIds = new ArrayList<String>();
			for (ClanHelp ch : solicitations) {
			  String chId = randomUUID();
			  chIds.add(chId);

				Map<String, Object> newRow = new HashMap<String, Object>();
        newRow.put(DBConstants.CLAN_HELP__ID, chId);
				newRow.put(DBConstants.CLAN_HELP__CLAN_ID, ch.getClanId());    
				newRow.put(DBConstants.CLAN_HELP__USER_ID, ch.getUserId());                                                                   
				newRow.put(DBConstants.CLAN_HELP__USER_DATA_ID, ch.getUserDataId());      
				newRow.put(DBConstants.CLAN_HELP__HELP_TYPE, ch.getHelpType());
				newRow.put(DBConstants.CLAN_HELP__TIME_OF_ENTRY, 
					new Timestamp(
						ch.getTimeOfEntry()
						.getTime()));
				newRow.put(DBConstants.CLAN_HELP__MAX_HELPERS, ch.getMaxHelpers());
				newRow.put(DBConstants.CLAN_HELP__OPEN, ch.isOpen());
                newRow.put(DBConstants.CLAN_HELP__STATIC_DATA_ID, ch.getStaticDataId());
                newRow.put(DBConstants.CLAN_HELP__HELPERS, "");

				newRows.add(newRow);
			}
			
			int numUpdated = DBConnection.get()
				.insertIntoTableBasicReturnNumUpdated(tableName, newRows);
			if (numUpdated != solicitations.size()) {
			  chIds = new ArrayList<String>();
			}
			return chIds;
		}
		
		@Override
		public int insertIntoUpdateClanInvite(String userId,
		    String inviterId, String clanId, Timestamp timeOfInvite)
		{
			String tableName = DBConstants.TABLE_CLAN_INVITE;

			Map<String, Object> newRow = new HashMap<String, Object>();
			newRow.put(DBConstants.CLAN_INVITE__USER_ID, userId);
			newRow.put(DBConstants.CLAN_INVITE__INVITER_ID,
				inviterId);
			newRow.put(DBConstants.CLAN_INVITE__CLAN_ID,
				clanId);
			newRow.put(DBConstants.CLAN_INVITE__TIME_OF_INVITE,
				timeOfInvite);

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			newRows.add(newRow);
			
			//determine which columns should be replaced
			Set<String> replaceTheseColumns = new HashSet<String>();
			replaceTheseColumns.add(DBConstants.CLAN_INVITE__TIME_OF_INVITE);
			
			//just in case there are remnants of old invites
			replaceTheseColumns.add(DBConstants.CLAN_INVITE__CLAN_ID);

			int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
					tableName, newRows, replaceTheseColumns);
			
			return numUpdated;
		}

		@Override
		public List<String> insertIntoItemForUserUsageGetId(List<ItemForUserUsage> itemsUsed)
		{
			String tableName = DBConstants.TABLE_ITEM_FOR_USER_USAGE;

			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

			List<String> ids = new ArrayList<String>();
			for (ItemForUserUsage ifuu : itemsUsed) {
			  String id = randomUUID();
			  ids.add(id);
			  
				Map<String, Object> newRow = new HashMap<String, Object>();
        newRow.put(DBConstants.ITEM_FOR_USER_USAGE__ID, id); 
				newRow.put(DBConstants.ITEM_FOR_USER_USAGE__USER_ID, ifuu.getUserId());                                                                   
				newRow.put(DBConstants.ITEM_FOR_USER_USAGE__ITEM_ID, ifuu.getItemId());    
				newRow.put(DBConstants.ITEM_FOR_USER_USAGE__TIME_OF_ENTRY, 
					new Timestamp(
						ifuu.getTimeOfEntry()
						.getTime()));
				newRow.put(DBConstants.ITEM_FOR_USER_USAGE__USER_DATA_ID, ifuu.getUserDataId());      
				newRow.put(DBConstants.ITEM_FOR_USER_USAGE__ACTION_TYPE, ifuu.getActionType());

				newRows.add(newRow);
			}
			
			int numUpdated = DBConnection.get()
				.insertIntoTableBasicReturnNumUpdated(tableName, newRows);
			if (numUpdated != itemsUsed.size()) {
			  ids = new ArrayList<String>();
			}
			return ids;
		}
		
}
