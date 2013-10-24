package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class MonsterForUserRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private final String TABLE_NAME = DBConstants.TABLE_MONSTER_FOR_USER;


  ////@Cacheable(value="userMonstersForUser", key="#userId")
  public List<MonsterForUser> getMonstersForUser(int userId) {
    log.debug("retrieving user monsters for userId " + userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    List<MonsterForUser> userMonsters = convertRSToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return userMonsters;
  }

  ////@Cacheable(value="monstersToMonstersForUser", key="#userId")
  public Map<Integer, List<MonsterForUser>> getMonsterIdsToMonstersForUser(int userId) {
    log.debug("retrieving map of monster id to usermonsters for userId " + userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    Map<Integer, List<MonsterForUser>> monsterIdsToMonsters = convertRSToMonsterIdsToMonsterList(rs);
    DBConnection.get().close(rs, null, conn);
    return monsterIdsToMonsters;
  }

  ////@Cacheable(value="specificMonster", key="#userMonsterId")
  public MonsterForUser getSpecificUserMonster(long userMonsterId) {
    log.debug("retrieving user monster for userMonsterId: " + userMonsterId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsByLongId(conn, userMonsterId, TABLE_NAME);
    MonsterForUser userMonster = convertRSSingleToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return userMonster;
  }

  public Map<Long, MonsterForUser> getSpecificUserMonsters(Collection<Long> userMonsterIds) {
    log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);

    if (userMonsterIds == null || userMonsterIds.size() <= 0 ) {
      return new HashMap<Long, MonsterForUser>();
    }

    String query = "select * from " + TABLE_NAME + " where (";
    List<String> condClauses = new ArrayList<String>();
    List <Object> values = new ArrayList<Object>();
    for (Long userMonsterId : userMonsterIds) {
      condClauses.add(DBConstants.MONSTER_FOR_USER__ID + "=?");
      values.add(userMonsterId);
    }
    query += StringUtils.getListInString(condClauses, "or") + ");"; // +
//    		DBConstants.MONSTER_FOR_USER__IS_COMPLETE + "=?;";
//    values.add(isComplete);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Long, MonsterForUser> userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return userMonsters;
  }

  ////@Cacheable(value="userMonstersWithMonsterId", key="#userId+':'+#monsterId")
  public List<MonsterForUser> getMonstersWithMonsterId(int userId, int monsterId) {
    log.debug("retrieving user monster for user: " + userId + ", monsterId: " + monsterId);

    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.MONSTER_FOR_USER__USER_ID, userId);
    paramsToVals.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, monsterId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    List<MonsterForUser> userMonsters = convertRSToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return userMonsters;
  }
  
  public Map<Integer, MonsterForUser> getIncompleteMonstersWithUserAndMonsterIds(
  		int userId, Collection<Integer> monsterIds) {
  	int size = monsterIds.size();
  	List<String> questions = Collections.nCopies(size, "?");
  	String delimiter = ",";
  	
  	String query = "select * from " + TABLE_NAME + " where ";
    List <Object> values = new ArrayList<Object>();

    //creating a "column in (value1,value2,...,value)" condition, prefer this over
    //chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
    query += DBConstants.MONSTER_FOR_USER__USER_ID + "=? and ";
    values.add(userId);
    query += DBConstants.MONSTER_FOR_USER__MONSTER_ID + " in (" +
    StringUtils.getListInString(questions, delimiter) + ") and ";
    values.addAll(monsterIds);
    
    query += DBConstants.MONSTER_FOR_USER__IS_COMPLETE + "=?;";
    values.add(false);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    Map<Integer, MonsterForUser> incompleteMonsters = convertRSToMonsterIdsToMonsters(rs);
    DBConnection.get().close(rs, null, conn);
    return incompleteMonsters;
  }

  private Map<Integer, List<MonsterForUser>> convertRSToMonsterIdsToMonsterList(
      ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        Map<Integer, List<MonsterForUser>> monsterIdsToMonsters = new HashMap<Integer, List<MonsterForUser>>();
        while(rs.next()) {
          MonsterForUser userMonster = convertRSRowToMonster(rs);
          List<MonsterForUser> userMonstersForMonsterId = monsterIdsToMonsters.get(userMonster.getMonsterId());
          if (userMonstersForMonsterId != null) {
            userMonstersForMonsterId.add(userMonster);
          } else {
            List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
            userMonsters.add(userMonster);
            monsterIdsToMonsters.put(userMonster.getMonsterId(), userMonsters);
          }
        }
        return monsterIdsToMonsters;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  private Map<Integer, MonsterForUser> convertRSToMonsterIdsToMonsters(ResultSet rs) {
  	Map<Integer, MonsterForUser> monsterIdsToMonsters = new HashMap<Integer, MonsterForUser>();
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				MonsterForUser userMonster = convertRSRowToMonster(rs);
  				if (userMonster != null) {
  					monsterIdsToMonsters.put(userMonster.getMonsterId(), userMonster);
  				}
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return monsterIdsToMonsters;
  }
  
  private Map<Long, MonsterForUser> convertRSToUserMonsterIdsToMonsters(ResultSet rs) {
  	Map<Long, MonsterForUser> monsterIdsToMonsters = new HashMap<Long, MonsterForUser>();
  	if (rs != null) {
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				MonsterForUser userMonster = convertRSRowToMonster(rs);
  				if (userMonster != null) {
  					monsterIdsToMonsters.put(userMonster.getId(), userMonster);
  				}
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	}
  	return monsterIdsToMonsters;
  }

  private List<MonsterForUser> convertRSToMonsters(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
        while(rs.next()) {  //should only be one
          userMonsters.add(convertRSRowToMonster(rs));
        }
        return userMonsters;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  private MonsterForUser convertRSSingleToMonsters(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          return convertRSRowToMonster(rs);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private MonsterForUser convertRSRowToMonster(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int userId = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int currentExp = rs.getInt(i++);
    int currentLvl = rs.getInt(i++);
    int currentHealth = rs.getInt(i++);
    int numPieces = rs.getInt(i++);
    boolean isComplete = rs.getBoolean(i++);
    int teamSlotNum = rs.getInt(i++);
    
    MonsterForUser userMonster = new MonsterForUser(id, userId, monsterId, currentExp,
    		currentLvl, currentHealth, numPieces, isComplete, teamSlotNum);
    return userMonster;
  }

}
