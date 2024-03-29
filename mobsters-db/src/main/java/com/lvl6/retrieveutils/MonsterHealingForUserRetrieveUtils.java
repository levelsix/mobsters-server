//package com.lvl6.retrieveutils;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.MonsterHealingForUser;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class MonsterHealingForUserRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
//
//
//  ////@Cacheable(value="userMonstersForUser", key="#userId")
//  public static Map<Long, MonsterHealingForUser> getMonstersForUser(int userId) {
//    log.debug("retrieving user monsters being healined for userId " + userId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterHealingForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
//			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster healing for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonsters;
//  }
//
//  /*
//  public static Map<Long, MonsterHealingForUser> getMonstersWithUserAndMonsterIds(
//  		int userId, Collection<Long> userMonsterIds) {
//  	int size = userMonsterIds.size();
//  	List<String> questions = Collections.nCopies(size, "?");
//  	String delimiter = ",";
//  	
//  	String query = "select * from " + TABLE_NAME + " where ";
//    List <Object> values = new ArrayList<Object>();
//
//    //creating a "column in (value1,value2,...,value)" condition, prefer this over
//    //chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
//    query += DBConstants.MONSTER_HEALING_FOR_USER__USER_ID + "=? and ";
//    values.add(userId);
//    query += DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID + " in (" +
//    StringUtils.getListInString(questions, delimiter) + ") and ";
//    values.add(userMonsterIds);
//    
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterHealingForUser> incompleteMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			incompleteMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster healing for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return incompleteMonsters;
//  }*/
//  
//  private static Map<Long, MonsterHealingForUser> convertRSToUserMonsterIdsToMonsters(ResultSet rs) {
//  	Map<Long, MonsterHealingForUser> monsterIdsToMonsters = new HashMap<Long, MonsterHealingForUser>();
//  	if (rs != null) {
//  		try {
//  			rs.last();
//  			rs.beforeFirst();
//  			while(rs.next()) {
//  				MonsterHealingForUser userMonster = convertRSRowToMonster(rs);
//  				if (userMonster != null) {
//  					monsterIdsToMonsters.put(userMonster.getMonsterForUserId(), userMonster);
//  				}
//  			}
//  		} catch (SQLException e) {
//  			log.error("problem with database call.", e);
//  		}
//  	}
//  	return monsterIdsToMonsters;
//  }
//
//  /*
//   * assumes the resultset is apprpriately set up. traverses the row it's on.
//   */
//  private static MonsterHealingForUser convertRSRowToMonster(ResultSet rs) throws SQLException {
//    int userId = rs.getInt(DBConstants.MONSTER_HEALING_FOR_USER__USER_ID);
//    long monsterForUserId = rs.getLong(DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID);
//    
//    Timestamp ts = null;
//    Date queuedTime = null;
//    try {
//    	ts = rs.getTimestamp(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME);
//    	if (!rs.wasNull()) {
//    		queuedTime = new Date(ts.getTime());
//    	}
//    } catch (SQLException e) {
//    	log.error("expected start time might be null userId=" + userId, e);
//    }
//    
//    int healthProgress = rs.getInt(DBConstants.MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS);
//    int priority = rs.getInt(DBConstants.MONSTER_HEALING_FOR_USER__PRIORITY);
//    float elapsedSeconds = rs.getFloat(DBConstants.MONSTER_HEALING_FOR_USER__ELAPSED_SECONDS);
//    
//    MonsterHealingForUser userMonster = new MonsterHealingForUser(userId,
//    		monsterForUserId, queuedTime, healthProgress, priority, elapsedSeconds);
//    return userMonster;
//  }
//
//}
