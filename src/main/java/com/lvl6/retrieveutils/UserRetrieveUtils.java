package com.lvl6.retrieveutils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class UserRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private final String TABLE_NAME = DBConstants.TABLE_USER;

  private final int BATTLE_INITIAL_LEVEL_RANGE = 4;    //even number makes it more consistent. ie 6 would be +/- 3 levels from user level
  private final int BATTLE_INITIAL_RANGE_INCREASE = 2;    //even number better again
  private final int BATTLE_RANGE_INCREASE_MULTIPLE = 2;
  private final int MAX_BATTLE_DB_HITS = 5;
  private final int EXTREME_MAX_BATTLE_DB_HITS = 30;
  
  
  public int numAccountsForUDID(String udid) {
    List<Object> params = new ArrayList<Object>();
    params.add(udid);
    Connection conn = DBConnection.get().getConnection();
    String query = "select count(*) from " +
    		TABLE_NAME + " where udid like concat(\"%\", ?, \"%\");";
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, params);
    int count = 0;
    try {
      if (null != rs) {
        try {
          if (rs.first()) {
            count = rs.getInt(1);
          }
        } catch (SQLException e) {
          log.error("sql query wrong", e);
        }
      }
    } catch (Exception e) {
      log.error("sql query wrong 2", e);
    } finally {
      DBConnection.get().close(null, null, conn);
    }
    return count;
  }
  
  public Integer countUsers(Boolean isFake){
	  List<Object> params = new ArrayList<Object>();
	  params.add(isFake);
	  Connection conn = DBConnection.get().getConnection();
	  ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, "select count(*) from "+TABLE_NAME+" where is_fake = ?;", params) ;
	  try {
		  if(rs != null) {
			  Integer count;
			try {
				if(rs.first()) {
					count = rs.getInt(1);
					return count;
				}
			} catch (SQLException e) {
				
			}
		  }
	  }catch(Exception e) {
		  
	  }finally {
		DBConnection.get().close(null, null, conn);
	  }
	  log.warn("No users found when counting users for isFake="+isFake);
	  return 0;
  }
  
  
  ////@Cacheable(value="usersCache")
  public User getUserById(int userId) {
    log.debug("retrieving user with userId " + userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsById(conn, userId, TABLE_NAME);
    User user = convertRSToUser(rs);
    DBConnection.get().close(rs, null, conn);
    return user;
  }

  public Map<Integer, User> getUsersByIds(List<Integer> userIds) {
    log.debug("retrieving users with userIds " + userIds);
    
    if (userIds == null || userIds.size() <= 0 ) {
      return new HashMap<Integer, User>();
    }

    String query = "select * from " + TABLE_NAME + " where (";
    List<String> condClauses = new ArrayList<String>();
    List <Object> values = new ArrayList<Object>();
    for (Integer userId : userIds) {
      condClauses.add(DBConstants.USER__ID + "=?");
      values.add(userId);
    }
    query += StringUtils.getListInString(condClauses, "or") + ")";

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Integer, User> userIdToUserMap = convertRSToUserIdToUsersMap(rs);
    DBConnection.get().close(rs, null, conn);
    return userIdToUserMap;
  }

  public List<User> getUsersByClanId(int clanId) {
    log.debug("retrieving users with clanId " + clanId);

    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.USER__CLAN_ID, clanId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, DBConstants.TABLE_USER);
    List<User> usersList = convertRSToUsers(rs);
    DBConnection.get().close(rs, null, conn);
    return usersList;
  }

  public List<User> getUsers(int numUsers, int playerLevel, int userId, boolean guaranteeNum, 
      boolean realPlayersOnly, boolean fakePlayersOnly, boolean offlinePlayersOnly,
      boolean inactiveShield, List<Integer> forbiddenPlayerIds) {
    log.debug("retrieving list of users for user " + userId + " with " + 
        numUsers + " users " + " around player level " + playerLevel + ", guaranteeNum="+guaranteeNum);

    //when there was a map in AoC, players displayed were +- 3,
    //hence use of -1 and +1
    int levelMin = Math.max(playerLevel - BATTLE_INITIAL_LEVEL_RANGE/2 - 1, 2);
    int levelMax = playerLevel + BATTLE_INITIAL_LEVEL_RANGE/2 + 1;

    List <Object> values = new ArrayList<Object>();

    String query = "select * from " + TABLE_NAME + " where ";

    if (forbiddenPlayerIds != null && forbiddenPlayerIds.size() > 0) {
      query += "(";
      for (int i = 0; i < forbiddenPlayerIds.size(); i++) {
        values.add(forbiddenPlayerIds.get(i));
        if (i == forbiddenPlayerIds.size() - 1) {
          query += DBConstants.USER__ID + "!=?";
        } else {
          query += DBConstants.USER__ID + "!=?  and ";
        }
      }
      query += ") and ";
    }

//    if (forBattle) {
//      query += "(" + DBConstants.USER__LAST_TIME_ATTACKED + "<=? or " +  DBConstants.USER__LAST_TIME_ATTACKED + " is ?) and ";
//      values.add(new Timestamp(new Date().getTime() - ControllerConstants.NUM_MINUTES_SINCE_LAST_BATTLE_BEFORE_APPEARANCE_IN_ATTACK_LISTS*60000));
//      values.add(null);
//    }
    
    if (realPlayersOnly) {
      query += DBConstants.USER__IS_FAKE + "=? and ";
      values.add(0);
      if (offlinePlayersOnly) {
        query += DBConstants.USER__LAST_LOGOUT + " > " +
            DBConstants.USER__LAST_LOGIN + " and ";
      }
      
    } else if (fakePlayersOnly) {
      query += DBConstants.USER__IS_FAKE + "=? and ";
      values.add(1);
    } 
    
    if (inactiveShield) {
      query += DBConstants.USER__HAS_ACTIVE_SHIELD + "=? and ";
      values.add(false);
    }

    query += DBConstants.USER__LEVEL + ">=? and " + DBConstants.USER__LEVEL + "<=? ";
    values.add(levelMin);
    values.add(levelMax);

    query += "order by " + DBConstants.USER__IS_FAKE + ", rand() limit ?";
    values.add(numUsers);

    int rangeIncrease = BATTLE_INITIAL_RANGE_INCREASE;
    int numDBHits = 1;

//    log.info("\t\t\t userRetrieveUtils.getUsers() query=" + query +
//        "\t\t values=" + values);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    if (conn != null) {
      rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
      //this is in case there aren't enough users to satisfy caller's requested number of users
      //so level range is widened and db requeried
      while (rs != null && MiscMethods.getRowCount(rs) < numUsers) {
        values.remove(values.size()-1);
        values.remove(values.size()-1);
        values.remove(values.size()-1);
        levelMin = Math.max(2, levelMin - rangeIncrease/4);
        values.add(levelMin);
        levelMax = levelMax + rangeIncrease*3/4;
        values.add(levelMax);
        values.add(numUsers);
        rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
        numDBHits++;
        if (!guaranteeNum) {
          if (numDBHits == MAX_BATTLE_DB_HITS) break;
        }
        if (numDBHits == EXTREME_MAX_BATTLE_DB_HITS) break;
        rangeIncrease *= BATTLE_RANGE_INCREASE_MULTIPLE;
      }
    }
    
    List<User> users = convertRSToUsers(rs);
    if (users == null) users = new ArrayList<User>();
    
    log.debug("retrieved " + users.size() + " users in level range " + levelMin+"-"+levelMax + 
        " when " + numUsers + " around " + playerLevel + " were requested");

    
    DBConnection.get().close(rs, null, conn);
    return users;
  }

  public List<User> getUsersByReferralCodeOrName(String queryString) {
    log.debug("retrieving user with queryString " + queryString);

    Map <String, Object> paramsToVals = new HashMap<String, Object>();
    paramsToVals.put(DBConstants.USER__REFERRAL_CODE, queryString);
    paramsToVals.put(DBConstants.USER__NAME, queryString);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
    
    List<User> users = convertRSToUsers(rs);
    if (users == null) users = new ArrayList<User>();
    DBConnection.get().close(rs, null, conn);
    return users;
  }
  
  public User getUserByUDID(String UDID) {
    log.debug("retrieving user with udid " + UDID);
    Map <String, Object> paramsToVals = new HashMap<String, Object>();
    paramsToVals.put(DBConstants.USER__UDID, UDID);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
    User user = convertRSToUser(rs);
    DBConnection.get().close(rs, null, conn);
    return user;
  }

  public User getUserByReferralCode(String referralCode) {
    log.debug("retrieving user with referral code " + referralCode);
    Map <String, Object> paramsToVals = new HashMap<String, Object>();
    paramsToVals.put(DBConstants.USER__REFERRAL_CODE, referralCode);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
    User user = convertRSToUser(rs);
    DBConnection.get().close(rs, null, conn);
    return user;
  }

  private User convertRSToUser(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          return convertRSRowToUser(rs);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  private Map<Integer, User> convertRSToUserIdToUsersMap(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        Map<Integer, User> userIdsToUsers = new HashMap<Integer, User>();
        while(rs.next()) {
          User user = convertRSRowToUser(rs);
          if (user != null) {
            userIdsToUsers.put(user.getId(), user);
          }
        }
        return userIdsToUsers;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  public List<User> convertRSToUsers(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<User> users = new ArrayList<User>();
        while(rs.next()) {  //should only be one
          users.add(convertRSRowToUser(rs));
        }
        return users;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  /*
  public List<User> retrieveUsersBasedOnElo1(User attacker, int elo, List<Integer> seenUserIds, Date clientTime) {
  		
  		Connection conn = DBConnection.get().getConnection();
	    List<String> columns = null; //all columns
	    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
	    Map<String, Object> relativeGreaterThanConditionParams =
	        new HashMap<String, Object>();
	    Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
	    Map<String, Object> likeCondParams = null;
	    String tablename = TABLE_NAME;
	    String conddelim = "AND";
	    String orderByColumn = null;
	    boolean orderByAsc = false;
	    int limit = 68;
	    boolean random = true;
  
	    //trying to improve elo range calculation readability
	    int distance = ControllerConstants.BATTLE__ELO_DISTANCE_ONE;
	    int eloMin = elo - distance;
	    int eloMax = elo + distance;
	    int lastViewedTimeMillisBuffer = ControllerConstants.BATTLE__LAST_VIEWED_TIME_MILLIS_ADDEND;
	    		
	    absoluteConditionParams.put(DBConstants.USER__HAS_BEGINNER_SHIELD, false);
	    relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, eloMin);
	    relativeLessThanConditionParams.put(DBConstants.USER__ELO, eloMax);
	    Timestamp timestamp = new Timestamp(clientTime.getTime());
	    
	    //user's shield should have ended before now
	    relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
	    Timestamp timestamp2 = new Timestamp(clientTime.getTime() - lastViewedTimeMillisBuffer);
	    relativeLessThanConditionParams.put(DBConstants.USER__LAST_QUEUE_TIME, timestamp2);
	    
	    String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
	    seenUserIdsString += StringUtils.csvIntList(seenUserIds) + ") and 1";
	    absoluteConditionParams.put(seenUserIdsString, 1);

	    ResultSet rs = DBConnection.get().selectRows(conn, columns,
		        absoluteConditionParams, relativeGreaterThanConditionParams,
		        relativeLessThanConditionParams, likeCondParams,
		        tablename, conddelim, orderByColumn, orderByAsc, limit, random);
	    
	    List<User> queueList = convertRSToUsers(rs);
	    DBConnection.get().close(rs, null, conn);
	    
	    //make sure attacker himself not chosen
	    for(User u : queueList) {
	    	if(u.getId() == attacker.getId())
	    		queueList.remove(u);
	    }
	    
	    return queueList;
  }
  
  
  public List<User> retrieveUsersBasedOnElo2(User attacker, int elo, List<Integer> seenUserIds, Date clientTime) {
		
		Connection conn = DBConnection.get().getConnection();
		List<String> columns = null; //all columns
		Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
		Map<String, Object> relativeGreaterThanConditionParams =
				new HashMap<String, Object>();
		Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
		Map<String, Object> likeCondParams = null;
		String tablename = TABLE_NAME;
		String conddelim = "AND";
		String orderByColumn = null;
		boolean orderByAsc = false;
		int limit = 13;
		boolean random = true;

		absoluteConditionParams.put(DBConstants.USER__HAS_BEGINNER_SHIELD, false);
		relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, elo-200);
		relativeLessThanConditionParams.put(DBConstants.USER__ELO, elo-100);
		Timestamp timestamp = new Timestamp(clientTime.getTime());
		relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
		Timestamp timestamp2 = new Timestamp(clientTime.getTime() - 600000);
		relativeLessThanConditionParams.put(DBConstants.USER__LAST_QUEUE_TIME, timestamp2);

		String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
		seenUserIdsString += StringUtils.csvIntList(seenUserIds) + ") and 1";
		absoluteConditionParams.put(seenUserIdsString, 1);

		ResultSet rs = DBConnection.get().selectRows(conn, columns,
				absoluteConditionParams, relativeGreaterThanConditionParams,
				relativeLessThanConditionParams, likeCondParams,
				tablename, conddelim, orderByColumn, orderByAsc, limit, random);

		List<User> queueList = convertRSToUsers(rs);
		DBConnection.get().close(rs, null, conn);

		return queueList;
  }
  
  public List<User> retrieveUsersBasedOnElo3(User attacker, int elo, List<Integer> seenUserIds, Date clientTime) {
		
		Connection conn = DBConnection.get().getConnection();
	    List<String> columns = null; //all columns
	    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
	    Map<String, Object> relativeGreaterThanConditionParams =
	        new HashMap<String, Object>();
	    Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
	    Map<String, Object> likeCondParams = null;
	    String tablename = TABLE_NAME;
	    String conddelim = "AND";
	    String orderByColumn = null;
	    boolean orderByAsc = false;
	    int limit = 13;
	    boolean random = true;

	    absoluteConditionParams.put(DBConstants.USER__HAS_BEGINNER_SHIELD, false);
	    relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, elo+100);
	    relativeLessThanConditionParams.put(DBConstants.USER__ELO, elo+200);
	    Timestamp timestamp = new Timestamp(clientTime.getTime());
	    relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
	    Timestamp timestamp2 = new Timestamp(clientTime.getTime() - 600000);
	    relativeLessThanConditionParams.put(DBConstants.USER__LAST_QUEUE_TIME, timestamp2);
	    
	    String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
	    seenUserIdsString += StringUtils.csvIntList(seenUserIds) + ") and 1";
	    absoluteConditionParams.put(seenUserIdsString, 1);

	    ResultSet rs = DBConnection.get().selectRows(conn, columns,
		        absoluteConditionParams, relativeGreaterThanConditionParams,
		        relativeLessThanConditionParams, likeCondParams,
		        tablename, conddelim, orderByColumn, orderByAsc, limit, random);
	    
	    List<User> queueList = convertRSToUsers(rs);
	    DBConnection.get().close(rs, null, conn);
	    
	    return queueList;
}
  
  public List<User> retrieveUsersBasedOnElo4(User attacker, int elo, List<Integer> seenUserIds, Date clientTime) {
		
		Connection conn = DBConnection.get().getConnection();
	    List<String> columns = null; //all columns
	    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
	    Map<String, Object> relativeGreaterThanConditionParams =
	        new HashMap<String, Object>();
	    Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
	    Map<String, Object> likeCondParams = null;
	    String tablename = TABLE_NAME;
	    String conddelim = "AND";
	    String orderByColumn = null;
	    boolean orderByAsc = false;
	    int limit = 3;
	    boolean random = true;

	    absoluteConditionParams.put(DBConstants.USER__HAS_BEGINNER_SHIELD, false);
	    relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, elo-300);
	    relativeLessThanConditionParams.put(DBConstants.USER__ELO, elo-200);
	    Timestamp timestamp = new Timestamp(clientTime.getTime());
	    relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
	    Timestamp timestamp2 = new Timestamp(clientTime.getTime() - 600000);
	    relativeLessThanConditionParams.put(DBConstants.USER__LAST_QUEUE_TIME, timestamp2);
	    
	    String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
	    seenUserIdsString += StringUtils.csvIntList(seenUserIds) + ") and 1";
	    absoluteConditionParams.put(seenUserIdsString, 1);

	    ResultSet rs = DBConnection.get().selectRows(conn, columns,
		        absoluteConditionParams, relativeGreaterThanConditionParams,
		        relativeLessThanConditionParams, likeCondParams,
		        tablename, conddelim, orderByColumn, orderByAsc, limit, random);
	    
	    List<User> queueList = convertRSToUsers(rs);
	    DBConnection.get().close(rs, null, conn);
	    
	    return queueList;
}
  
  public List<User> retrieveUsersBasedOnElo5(User attacker, int elo, List<Integer> seenUserIds, Date clientTime) {
		
		Connection conn = DBConnection.get().getConnection();
	    List<String> columns = null; //all columns
	    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
	    Map<String, Object> relativeGreaterThanConditionParams =
	        new HashMap<String, Object>();
	    Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
	    Map<String, Object> likeCondParams = null;
	    String tablename = TABLE_NAME;
	    String conddelim = "AND";
	    String orderByColumn = null;
	    boolean orderByAsc = false;
	    int limit = 13;
	    boolean random = true;

	    absoluteConditionParams.put(DBConstants.USER__HAS_BEGINNER_SHIELD, false);
	    relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, elo+200);
	    relativeLessThanConditionParams.put(DBConstants.USER__ELO, elo+300);
	    Timestamp timestamp = new Timestamp(clientTime.getTime());
	    relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
	    Timestamp timestamp2 = new Timestamp(clientTime.getTime() - 600000);
	    relativeLessThanConditionParams.put(DBConstants.USER__LAST_QUEUE_TIME, timestamp2);
	    
	    String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
	    seenUserIdsString += StringUtils.csvIntList(seenUserIds) + ") and 1";
	    absoluteConditionParams.put(seenUserIdsString, 1);

	    ResultSet rs = DBConnection.get().selectRows(conn, columns,
		        absoluteConditionParams, relativeGreaterThanConditionParams,
		        relativeLessThanConditionParams, likeCondParams,
		        tablename, conddelim, orderByColumn, orderByAsc, limit, random);
	    
	    List<User> queueList = convertRSToUsers(rs);
	    DBConnection.get().close(rs, null, conn);
	    
	    return queueList;
  }*/
  
  public List<User> retrieveCompleteQueueList(User attacker, int elo,
  		List<Integer> seenUserIds, Date clientTime) {
  	List<User> retVal = new ArrayList<User>();
  	
  	//shortening the names
  	int distOne = ControllerConstants.BATTLE__ELO_DISTANCE_ONE;
  	int distTwo = ControllerConstants.BATTLE__ELO_DISTANCE_TWO;
  	int distThree = ControllerConstants.BATTLE__ELO_DISTANCE_THREE;
  	int limitOne = ControllerConstants.BATTLE__ELO_USER_LIMIT_ONE;
  	int limitTwo = ControllerConstants.BATTLE__ELO_USER_LIMIT_TWO;
  	int limitThree = ControllerConstants.BATTLE__ELO_USER_LIMIT_THREE;
  	
  	//make sure attacker himself not chosen
  	int attackerId = attacker.getId();
  	seenUserIds.add(attackerId);
  	//select users with elo above and below user's elo [elo - distOne, elo + distOne]
  	int eloMax = elo + distOne + 1; 
  	int eloMin = elo - distOne - 1;
  	List<User> qList = retrieveUsersInEloRangeN(elo, seenUserIds, clientTime,
  			eloMin, eloMax, limitOne);
  	retVal.addAll(qList);
  	//could take out attacker

  	//get users with elo lower than all those above [elo - distTwo, elo - distOne)
  	eloMax = elo - distOne; 
  	eloMin = elo - distTwo - 1;
  	qList = retrieveUsersInEloRangeN(elo, seenUserIds, clientTime, eloMin,
  			eloMax, limitTwo);
  	retVal.addAll(qList);
  	
  	//get users with elo lower than all those above [elo - distThree, elo - distTwo)
  	eloMax = elo - distTwo;
  	eloMin = elo - distThree;
  	qList = retrieveUsersInEloRangeN(elo, seenUserIds, clientTime, eloMin,
  			eloMax, limitThree);
  	retVal.addAll(qList);
  	
  	//get users with elo higher than all those above (elo + distOne, elo + distTwo]
  	eloMax = elo + distTwo + 1;
  	eloMin = elo + distOne;
  	qList = retrieveUsersInEloRangeN(elo, seenUserIds, clientTime, eloMin,
  			eloMax, limitTwo);
  	retVal.addAll(qList);
  	
  	//get users with elo higher than all those above (elo + distTwo, elo + distThree]
  	eloMax = elo + distThree;
  	eloMin = elo + distTwo;
  	qList = retrieveUsersInEloRangeN(elo, seenUserIds, clientTime, eloMin,
  			eloMax, limitThree);
  	retVal.addAll(qList);
	  return qList;
  }
  
  private List<User> retrieveUsersInEloRangeN(int elo, List<Integer> seenUserIds,
  		Date clientTime, int eloMin, int eloMax, int limit) {
  	Connection conn = DBConnection.get().getConnection();
    List<String> columns = null; //all columns
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    Map<String, Object> relativeGreaterThanConditionParams =
        new HashMap<String, Object>();
    Map<String, Object> relativeLessThanConditionParams = new HashMap<String, Object>();
    Map<String, Object> likeCondParams = null;
    String tablename = TABLE_NAME;
    String conddelim = "AND";
    String orderByColumn = null;
    boolean orderByAsc = false;
    boolean random = true;

    //trying to improve elo range calculation readability
    int lastViewedTimeMillisBuffer = ControllerConstants.BATTLE__LAST_VIEWED_TIME_MILLIS_ADDEND;
    		
    absoluteConditionParams.put(DBConstants.USER__HAS_ACTIVE_SHIELD, false);
    relativeGreaterThanConditionParams.put(DBConstants.USER__ELO, eloMin);
    relativeLessThanConditionParams.put(DBConstants.USER__ELO, eloMax);
    Timestamp timestamp = new Timestamp(clientTime.getTime());
    
    //prospective users should have their shield ended before now
    relativeLessThanConditionParams.put(DBConstants.USER__SHIELD_END_TIME, timestamp);
    Timestamp timestamp2 = new Timestamp(clientTime.getTime() - lastViewedTimeMillisBuffer);
    relativeLessThanConditionParams.put(DBConstants.USER__LAST_TIME_QUEUED, timestamp2);
    
    String seenUserIdsString = DBConstants.USER__ID + "NOT IN ("; 
    seenUserIdsString += StringUtils.csvList(seenUserIds) + ") and 1";
    absoluteConditionParams.put(seenUserIdsString, 1);

    ResultSet rs = DBConnection.get().selectRows(conn, columns,
	        absoluteConditionParams, relativeGreaterThanConditionParams,
	        relativeLessThanConditionParams, likeCondParams,
	        tablename, conddelim, orderByColumn, orderByAsc, limit, random);
    
    List<User> queueList = convertRSToUsers(rs);
    DBConnection.get().close(rs, null, conn);
    
    return queueList;
  }
  
  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  public User convertRSRowToUser(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    String name = rs.getString(i++);
    int level = rs.getInt(i++);
    int diamonds = rs.getInt(i++);
    int coins = rs.getInt(i++);
    int experience = rs.getInt(i++);
    int tasksCompleted = rs.getInt(i++);
    int battlesWon = rs.getInt(i++);
    int battlesLost = rs.getInt(i++);
    int flees = rs.getInt(i++);
    String referralCode = rs.getString(i++);
    int numReferrals = rs.getInt(i++);
    String udid = rs.getString(i++);

    Timestamp ts;
    Date lastLogin = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		lastLogin = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: last_login not set. user_id=" + id);
    }

    Date lastLogout = null;
    ts = rs.getTimestamp(i++);
    try {
    	if (!rs.wasNull()) {
    		lastLogout = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: last_logout not set. user_id=" + id);
    }

    String deviceToken = rs.getString(i++);

    Date lastBattleNotificationTime = null;
    ts = rs.getTimestamp(i++);
    try {
    	if (!rs.wasNull()) {
    		lastBattleNotificationTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: last_battle_notification_time not set. user_id=" + id);
    }

    int numBadges = rs.getInt(i++);
    boolean isFake = rs.getBoolean(i++);
    
    Date createTime = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		createTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: create_time not set. user_id=" + id);
    }

    boolean isAdmin = rs.getBoolean(i++);
    String apsalarId = rs.getString(i++);
    int numCoinsRetrievedFromStructs = rs.getInt(i++);
    int numConsecutiveDaysPlayed = rs.getInt(i++);
    
    int clanId = rs.getInt(i++);
    if (rs.wasNull()) {
      clanId = ControllerConstants.NOT_SET;
    }
    
    Date lastWallPostNotificationTime = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		lastWallPostNotificationTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: last_wall_post_notification_time not set. user_id=" + id);
    }
    
    int kabamNaid = rs.getInt(i++);

    boolean hasReceivedfbReward = rs.getBoolean(i++);
    int numAdditionalMonsterSlots = rs.getInt(i++);
    int numBeginnerSalesPurchased = rs.getInt(i++);
    boolean hasActiveShield = rs.getBoolean(i++);
    
    Date shieldEndTime = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		shieldEndTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: shield_end_time not set. user_id=" + id);
    }
    
    int elo = rs.getInt(i++);
    String rank = rs.getString(i++);
    
    Date lastTimeQueued = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		lastTimeQueued = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: last_time_queued not set. user_id=" + id);
    }
    
    int attacksWon = rs.getInt(i++);
    int defensesWon = rs.getInt(i++);
    int attacksLost = rs.getInt(i++);
    int defensesLost = rs.getInt(i++);
    String facebookId = rs.getString(i++);
    
    User user = new User(id, name, level, diamonds, coins, experience,
    		tasksCompleted, battlesWon, battlesLost, flees, referralCode,
    		numReferrals, udid, lastLogin, lastLogout, deviceToken,
    		lastBattleNotificationTime, numBadges, isFake, createTime,
    		isAdmin, apsalarId, numCoinsRetrievedFromStructs,
    		numConsecutiveDaysPlayed, clanId, lastWallPostNotificationTime,
    		kabamNaid, hasReceivedfbReward, numAdditionalMonsterSlots,
    		numBeginnerSalesPurchased, hasActiveShield, shieldEndTime, elo,
    		rank, lastTimeQueued, attacksWon, defensesWon, attacksLost,
    		defensesLost, facebookId);
    return user;
  }
}
