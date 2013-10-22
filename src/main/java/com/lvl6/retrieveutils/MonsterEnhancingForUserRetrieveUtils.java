package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class MonsterEnhancingForUserRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;


  ////@Cacheable(value="userMonstersForUser", key="#userId")
  public static Map<Long, MonsterEnhancingForUser> getMonstersForUser(int userId) {
    log.debug("retrieving user monsters being healined for userId " + userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    Map<Long, MonsterEnhancingForUser> userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return userMonsters;
  }

  public static Map<Long, MonsterEnhancingForUser> getMonstersWithUserAndMonsterIds(
  		int userId, Collection<Long> userMonsterIds) {
  	int size = userMonsterIds.size();
  	List<String> questions = Collections.nCopies(size, "?");
  	String delimiter = ",";
  	
  	String query = "select * from " + TABLE_NAME + " where ";
    List <Object> values = new ArrayList<Object>();

    //creating a "column in (value1,value2,...,value)" condition, prefer this over
    //chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
    query += DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID + "=? and ";
    values.add(userId);
    query += DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID + " in (" +
    StringUtils.getListInString(questions, delimiter) + ") and ";
    values.add(userMonsterIds);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Long, MonsterEnhancingForUser> incompleteMonsters = convertRSToUserMonsterIdsToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return incompleteMonsters;
  }
  
  private static Map<Long, MonsterEnhancingForUser> convertRSToUserMonsterIdsToMonsters(ResultSet rs) {
  	Map<Long, MonsterEnhancingForUser> monsterIdsToMonsters = new HashMap<Long, MonsterEnhancingForUser>();
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				MonsterEnhancingForUser userMonster = convertRSRowToMonster(rs);
  				if (userMonster != null) {
  					monsterIdsToMonsters.put(userMonster.getMonsterForUserId(), userMonster);
  				}
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return monsterIdsToMonsters;
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static MonsterEnhancingForUser convertRSRowToMonster(ResultSet rs) throws SQLException {
    int i = 1;
    int userId = rs.getInt(i++);
    long monsterForUserId = rs.getLong(i++);
    
    Timestamp ts = null;
    Date expectedStartTime = null;
    try {
    	ts = rs.getTimestamp(i++);
    	expectedStartTime = new Date(ts.getTime());
    } catch (SQLException e) {
    	//if it is null, then it most likely means this is the item being enhanced
    	log.warn("expected start time might be null userId=" + userId +
    			"\t monsterForUserId=" + monsterForUserId + "\t expectedStartTime", e);
    }
    
//    Date queuedTime = null;
//    try {
//    	ts = rs.getTimestamp(i++);
//    	queuedTime = new Date(ts.getTime());
//    } catch (SQLException e) {
//    	log.error("expected start time might be null userId=" + userId, e);
//    }
    
    MonsterEnhancingForUser userMonster = new MonsterEnhancingForUser(userId,
    		monsterForUserId, expectedStartTime);//, queuedTime);
    return userMonster;
  }

}
