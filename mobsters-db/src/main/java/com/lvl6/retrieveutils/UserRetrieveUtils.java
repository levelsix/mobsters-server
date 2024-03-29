//package com.lvl6.retrieveutils;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.User;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//import com.lvl6.utils.utilmethods.StringUtils;
//
//@Component @DependsOn("gameServer") public class UserRetrieveUtils {
//
//  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private final String TABLE_NAME = DBConstants.TABLE_USER;
//
//  public List<Integer> getUserIdsForFacebookIds(List<String> facebookIds) {
//  	int amount = facebookIds.size();
//  	List<String> questionMarkList = Collections.nCopies(amount, "?"); 
//  	String questionMarks = StringUtils.csvList(questionMarkList);
//
//  	List<Object> params = new ArrayList<Object>();
//  	params.addAll(facebookIds);
//
//  	StringBuilder querySb = new StringBuilder();
//  	querySb.append("SELECT ");
//  	querySb.append(DBConstants.USER__ID);
//  	querySb.append(" FROM ");
//  	querySb.append(TABLE_NAME);
//  	querySb.append(" WHERE ");
//  	querySb.append(DBConstants.USER__FACEBOOK_ID);
//  	querySb.append(" IN (");
//  	querySb.append(questionMarks);
//  	querySb.append(");");
//
//  	String query = querySb.toString();
//  	log.info("query=" + query + "\t values=" + params);
//  	Connection conn = DBConnection.get().getConnection();
//  	ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, params);
//  	List<Integer> userIdList = new ArrayList<Integer>();
//  	try {
//  		if (null == rs) {
//  			return userIdList;
//  		}
//  		try {
//  			rs.last();
//  			rs.beforeFirst();
//  			while(rs.next()) {
//  				int userId = rs.getInt(1);
//  				userIdList.add(userId);
//  			}
//  		} catch (SQLException e) {
//  			log.error("problem with database call.", e);
//  		}
//  	} catch (Exception e) {
//  		log.error("sql query wrong 2", e);
//  	} finally {
//  		DBConnection.get().close(rs, null, conn);
//  	}
//  	return userIdList;
//  }
//
//
//  public Map<Integer, User> getUsersForFacebookIdsOrUserIds(List<String> facebookIds,
//  		List<Integer> userIds) {
//  	
//  	List<Object> params = new ArrayList<Object>();
//  	
//  	StringBuilder querySb = new StringBuilder();
//  	querySb.append("SELECT * FROM ");
//  	querySb.append(TABLE_NAME);
//  	querySb.append(" WHERE ");
//  	if (null != facebookIds && !facebookIds.isEmpty()) {
//  		int amount = facebookIds.size();
//  		List<String> questionMarkList = Collections.nCopies(amount, "?"); 
//  		String questionMarks = StringUtils.csvList(questionMarkList);
//
//  		querySb.append(DBConstants.USER__FACEBOOK_ID);
//  		querySb.append(" IN (");
//  		querySb.append(questionMarks);
//  		querySb.append(")");
//  		
//  		if (null != userIds && !userIds.isEmpty()) {
//  			querySb.append(" OR ");
//  		}
//  		
//  		params.addAll(facebookIds);
//  	}
//  	
//  	if (null != userIds && !userIds.isEmpty()) {
//  		int amount = userIds.size();
//  		List<String> questionMarkList = Collections.nCopies(amount, "?");
//  		String questionMarks = StringUtils.csvList(questionMarkList);
//  		
//  		querySb.append(DBConstants.USER__ID);
//			querySb.append(" IN (");
//  		querySb.append(questionMarks);
//  		querySb.append(")");
//  		
//  		params.addAll(userIds);
//		}
//  	
//  	String query = querySb.toString();
//  	log.info(String.format(
//  		"query=%s, values=%s", query, params));
//  	Connection conn = null;
//  	ResultSet rs = null;
//  	Map<Integer, User> userMap = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, params);
//			userMap = convertRSToUserIdToUsersMap(rs);
//		} catch (Exception e) {
//    	log.error("user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMap;
//  }
//  
//  public int numAccountsForUDID(String udid) {
//    List<Object> params = new ArrayList<Object>();
//    params.add(udid);
//    Connection conn = DBConnection.get().getConnection();
//    String query = "select count(*) from " +
//    		TABLE_NAME + " where udid like concat(?, \"%\");";
//    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, params);
//    int count = 0;
//    try {
//      if (null != rs) {
//        try {
//          if (rs.first()) {
//            count = rs.getInt(1);
//          }
//        } catch (SQLException e) {
//          log.error("sql query wrong", e);
//        }
//      }
//    } catch (Exception e) {
//      log.error("sql query wrong 2", e);
//    } finally {
//      DBConnection.get().close(rs, null, conn);
//    }
//    return count;
//  }
//  
//  public Integer countUsers(Boolean isFake){
//	  List<Object> params = new ArrayList<Object>();
//	  params.add(isFake);
//	  Connection conn = DBConnection.get().getConnection();
//	  ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, "select count(*) from "+TABLE_NAME+" where is_fake = ?;", params) ;
//	  try {
//		  if(rs != null) {
//			  Integer count;
//			try {
//				if(rs.first()) {
//					count = rs.getInt(1);
//					return count;
//				}
//			} catch (SQLException e) {
//				
//			}
//		  }
//	  }catch(Exception e) {
//		  
//	  }finally {
//	  	DBConnection.get().close(rs, null, conn);
//	  }
//	  log.warn("No users found when counting users for isFake="+isFake);
//	  return 0;
//  }
//  
//  
//  ////@Cacheable(value="usersCache")
//  public User getUserById(int userId) {
//    log.debug("retrieving user with userId " + userId);
//
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = DBConnection.get().selectRowsById(conn, userId, TABLE_NAME);
//    User user = convertRSToUser(rs);
//    DBConnection.get().close(rs, null, conn);
//    return user;
//  }
//
//  public Map<Integer, User> getUsersByIds(Collection<Integer> userIds) {
//    log.debug("retrieving users with userIds " + userIds);
//    
//    if (userIds == null || userIds.size() <= 0 ) {
//      return new HashMap<Integer, User>();
//    }
//
//    String query = "select * from " + TABLE_NAME + " where (";
//    List<String> condClauses = new ArrayList<String>();
//    List <Object> values = new ArrayList<Object>();
//    for (Integer userId : userIds) {
//      condClauses.add(DBConstants.USER__ID + "=?");
//      values.add(userId);
//    }
//    query += StringUtils.getListInString(condClauses, "or") + ")";
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, User> userIdToUserMap = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userIdToUserMap = convertRSToUserIdToUsersMap(rs);
//		} catch (Exception e) {
//			log.error("user retrieve db error.", e);
//			userIdToUserMap = new HashMap<Integer, User>();
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userIdToUserMap;
//  }
//
//  public List<User> getUsersByClanId(int clanId) {
//    log.debug("retrieving users with clanId " + clanId);
//
//    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
//    absoluteConditionParams.put(DBConstants.USER__CLAN_ID, clanId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		List<User> usersList = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, DBConstants.TABLE_USER);
//			usersList = convertRSToUsers(rs);
//		} catch (Exception e) {
//			log.error("user retrieve db error.", e);
//			usersList = new ArrayList<User>();
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return usersList;
//  }
//  
//  public List<User> getUsersByReferralCodeOrName(String queryString) {
//    log.debug("retrieving user with queryString " + queryString);
//
//    Map <String, Object> paramsToVals = new HashMap<String, Object>();
//    paramsToVals.put(DBConstants.USER__REFERRAL_CODE, queryString);
//    paramsToVals.put(DBConstants.USER__NAME, queryString);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		List<User> users = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
//			
//			users = convertRSToUsers(rs);
//			if (users == null) users = new ArrayList<User>();
//		} catch (Exception e) {
//    	log.error("user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return users;
//  }
//  
//  public User getUserByUDID(String UDID) {
//    log.debug("retrieving user with udid " + UDID);
//    Map <String, Object> paramsToVals = new HashMap<String, Object>();
//    paramsToVals.put(DBConstants.USER__UDID, UDID);
//
//    User user = null;
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//		try {
//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
//			user = convertRSToUser(rs);
////			DBConnection.get().close(rs, null, conn);
//		} catch (Exception e) {
//    	log.error("user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return user;
//  }
//  
//  public List<User> getUserByUDIDorFbId(String UDID, String fbId) {
//    log.debug("retrieving user with udid=" + UDID + " fbId=" + fbId);
//    Map <String, Object> paramsToVals = new HashMap<String, Object>();
//    paramsToVals.put(DBConstants.USER__UDID, UDID);
//    paramsToVals.put(DBConstants.USER__FACEBOOK_ID, fbId);
//
//    List<User> user = null;
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//		try {
//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
//			user = convertRSToUsers(rs);
////			DBConnection.get().close(rs, null, conn);
//		} catch (Exception e) {
//    	log.error("user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return user;
//  }
//
//  public User getUserByReferralCode(String referralCode) {
//    log.debug("retrieving user with referral code " + referralCode);
//    Map <String, Object> paramsToVals = new HashMap<String, Object>();
//    paramsToVals.put(DBConstants.USER__REFERRAL_CODE, referralCode);
//
//    User user = null;
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//		try {
//			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
//			user = convertRSToUser(rs);
//			DBConnection.get().close(rs, null, conn);
//		} catch (Exception e) {
//    	log.error("user  retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return user;
//  }
//
//  private User convertRSToUser(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        while(rs.next()) {  //should only be one
//          return convertRSRowToUser(rs);
//        }
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//
//  private Map<Integer, User> convertRSToUserIdToUsersMap(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        Map<Integer, User> userIdsToUsers = new HashMap<Integer, User>();
//        while(rs.next()) {
//          User user = convertRSRowToUser(rs);
//          if (user != null) {
//            userIdsToUsers.put(user.getId(), user);
//          }
//        }
//        return userIdsToUsers;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//
//  public List<User> convertRSToUsers(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        List<User> users = new ArrayList<User>();
//        while(rs.next()) {  //should only be one
//          users.add(convertRSRowToUser(rs));
//        }
//        return users;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//  
//  /*
//   * assumes the resultset is apprpriately set up. traverses the row it's on.
//   */
//  public User convertRSRowToUser(ResultSet rs) throws SQLException {
//    int id = rs.getInt(DBConstants.USER__ID);
//    String name = rs.getString(DBConstants.USER__NAME);
//    int level = rs.getInt(DBConstants.USER__LEVEL);
//    int gems = rs.getInt(DBConstants.USER__GEMS);
//    int cash = rs.getInt(DBConstants.USER__CASH);
//    int oil = rs.getInt(DBConstants.USER__OIL);
//    int experience = rs.getInt(DBConstants.USER__EXPERIENCE);
//    int tasksCompleted = rs.getInt(DBConstants.USER__TASKS_COMPLETED);
//    String referralCode = rs.getString(DBConstants.USER__REFERRAL_CODE);
//    int numReferrals = rs.getInt(DBConstants.USER__NUM_REFERRALS);
//    String udidForHistory = rs.getString(DBConstants.USER__UDID_FOR_HISTORY);
//
//    Timestamp ts;
//    Date lastLogin = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_LOGIN);
//    	if (!rs.wasNull()) {
//    		lastLogin = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: last_login not set. user_id=" + id);
//    }
//
//    Date lastLogout = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_LOGOUT);
//    	if (!rs.wasNull()) {
//    		lastLogout = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: last_logout not set. user_id=" + id);
//    }
//
//    String deviceToken = rs.getString(DBConstants.USER__DEVICE_TOKEN);
//    int numBadges = rs.getInt(DBConstants.USER__NUM_BADGES);
//    boolean isFake = rs.getBoolean(DBConstants.USER__IS_FAKE);
//    
//    Date createTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__CREATE_TIME);
//    	if (!rs.wasNull()) {
//    		createTime = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: create_time not set. user_id=" + id);
//    }
//
//    boolean isAdmin = rs.getBoolean(DBConstants.USER__IS_ADMIN);
//    String apsalarId = rs.getString(DBConstants.USER__APSALAR_ID);
//    int numCoinsRetrievedFromStructs = rs.getInt(DBConstants.USER__NUM_COINS_RETRIEVED_FROM_STRUCTS);
//    int numOilRetrievedFromStructs = rs.getInt(DBConstants.USER__NUM_OIL_RETRIEVED_FROM_STRUCTS);
//    int numConsecutiveDaysPlayed = rs.getInt(DBConstants.USER__NUM_CONSECUTIVE_DAYS_PLAYED);
//    
//    int clanId = rs.getInt(DBConstants.USER__CLAN_ID);
//    if (rs.wasNull()) {
//      clanId = ControllerConstants.NOT_SET;
//    }
//    
//    Date lastWallPostNotificationTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_WALL_POST_NOTIFICATION_TIME);
//    	if (!rs.wasNull()) {
//    		lastWallPostNotificationTime = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: last_wall_post_notification_time not set. user_id=" + id);
//    }
//    
////    int kabamNaid = rs.getInt(DBConstants.USER__KABAM_NAID);
//
//    boolean hasReceivedfbReward = rs.getBoolean(DBConstants.USER__HAS_RECEIVED_FB_REWARD);
//    int numBeginnerSalesPurchased = rs.getInt(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED);
//    String facebookId = rs.getString(DBConstants.USER__FACEBOOK_ID);
//    boolean fbIdSetOnUserCreate = rs.getBoolean(DBConstants.USER__FB_ID_SET_ON_USER_CREATE);
//    String gameCenterId = rs.getString(DBConstants.USER__GAME_CENTER_ID);
//    String udid = rs.getString(DBConstants.USER__UDID);
//    Date lastObstacleSpawnedTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME);
//    	if (!rs.wasNull()) {
//    		lastObstacleSpawnedTime = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: last_obstacle_spawned_time" +
//    			" was null...?");
//    }
//    
//    int numObstaclesRemoved = rs.getInt(DBConstants.USER__NUM_OBSTACLES_REMOVED);
//    
//    Date lastMiniJobGeneratedTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_MINI_JOB_GENERATED_TIME);
//    	if (!rs.wasNull()) {
//    		lastMiniJobGeneratedTime = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("lastMiniJobGeneratedTime null...?", e);
//    }
//    
//    int avatarMonsterId = rs.getInt(DBConstants.USER__AVATAR_MONSTER_ID);
//    
//    Date lastFreeBoosterPackTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.USER__LAST_FREE_BOOSTER_PACK_TIME);
//    	if (!rs.wasNull()) {
//    		lastFreeBoosterPackTime = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("last_free_booster_pack_time null...?", e);
//    }
//    
//    int numClanHelps = rs.getInt(DBConstants.USER__CLAN_HELPS);
//    
//    User user = new User(id, name, level, gems, cash, oil, experience,
//    		tasksCompleted, referralCode, numReferrals, udidForHistory,
//    		lastLogin, lastLogout, deviceToken, numBadges, isFake, createTime,
//    		isAdmin, apsalarId, numCoinsRetrievedFromStructs,
//    		numOilRetrievedFromStructs, numConsecutiveDaysPlayed, clanId,
//    		lastWallPostNotificationTime, hasReceivedfbReward,
//    		numBeginnerSalesPurchased, facebookId, fbIdSetOnUserCreate,
//    		gameCenterId, udid, lastObstacleSpawnedTime, numObstaclesRemoved,
//    		lastMiniJobGeneratedTime, avatarMonsterId, lastFreeBoosterPackTime,
//    		numClanHelps);
//    return user;
//  }
// 
//}
