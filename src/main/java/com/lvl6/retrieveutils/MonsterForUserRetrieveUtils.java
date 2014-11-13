//package com.lvl6.retrieveutils;
//
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
//import com.lvl6.info.MonsterForUser;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//import com.lvl6.utils.utilmethods.StringUtils;
//
//@Component @DependsOn("gameServer") public class MonsterForUserRetrieveUtils {
//
//  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private final String TABLE_NAME = DBConstants.TABLE_MONSTER_FOR_USER;
//
//
//  ////@Cacheable(value="userMonstersForUser", key="#userId")
//  public List<MonsterForUser> getMonstersForUser(int userId) {
//    log.debug("retrieving user monsters for userId " + userId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		List<MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
//			userMonsters = convertRSToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonsters;
//  }
//
//  /*
//  ////@Cacheable(value="monstersToMonstersForUser", key="#userId")
//  public Map<Integer, List<MonsterForUser>> getMonsterIdsToMonstersForUser(int userId) {
//    log.debug("retrieving map of monster id to usermonsters for userId " + userId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, List<MonsterForUser>> monsterIdsToMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
//			monsterIdsToMonsters = convertRSToMonsterIdsToMonsterList(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return monsterIdsToMonsters;
//  }*/
//  
//  public Map<Integer, List<MonsterForUser>> getUserIdsToMonsterTeamForUserIds(
//  		List<Integer> userIds) {
//  	
//  	StringBuilder sb = new StringBuilder();
//  	sb.append("SELECT * FROM ");
//  	sb.append(TABLE_NAME);
//  	sb.append(" WHERE ");
//  	sb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
//  	sb.append(" IN (");
//  	int amount = userIds.size();
//  	List<String> questions = Collections.nCopies(amount, "?");
//  	String questionStr = StringUtils.csvList(questions);
//  	sb.append(questionStr);
//  	sb.append(") AND ");
//  	sb.append(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM);
//  	sb.append(" > ?;");
//  	
//  	List<Object> values = new ArrayList<Object>();
//  	values.addAll(userIds);
//  	values.add(0);
//  	
//  	String query = sb.toString();
//  	
//  	log.info("RETRIEVING USERS' TEAMS. query=" + query + "\t\t values=" + values);
//  	
//  	Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, List<MonsterForUser>> userIdsToCurrentTeam = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userIdsToCurrentTeam = convertRSToUserIdsToCurrentTeam(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//  	
//  	return userIdsToCurrentTeam;
//  }
//
//  ////@Cacheable(value="specificMonster", key="#userMonsterId")
//  public MonsterForUser getSpecificUserMonster(long userMonsterId) {
//    log.debug("retrieving user monster for userMonsterId: " + userMonsterId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		MonsterForUser userMonster = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsByLongId(conn, userMonsterId, TABLE_NAME);
//			userMonster = convertRSSingleToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonster;
//  }
//  
//  public Map<Long, MonsterForUser> getSpecificUserMonsters(List<Long> userMonsterIds) {
//    log.debug("retrieving user monsters for userMonsterIds: " + userMonsterIds);
//    
//    StringBuilder querySb = new StringBuilder();
//    querySb.append("SELECT * FROM ");
//    querySb.append(TABLE_NAME); 
//    querySb.append(" WHERE ");
//    querySb.append(DBConstants.MONSTER_FOR_USER__ID);
//    querySb.append(" IN (");
//
//    int amount = userMonsterIds.size();
//    List<String> questions = Collections.nCopies(amount, "?");
//    String questionMarkStr = StringUtils.csvList(questions);
//
//    querySb.append(questionMarkStr);
//    querySb.append(");");
//    
//    List <Object> values = new ArrayList<Object>();
//    values.addAll(userMonsterIds);
//
//    String query = querySb.toString();
//    log.info("query=" + query + "\t values=" + values);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonsters;
//  }
//
//  
//  public Map<Long, MonsterForUser> getSpecificOrAllUserMonstersForUser(int userId,
//  		Collection<Long> userMonsterIds) {
//    
//    StringBuilder querySb = new StringBuilder();
//    querySb.append("SELECT * FROM ");
//    querySb.append(TABLE_NAME); 
//    querySb.append(" WHERE ");
//    querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
//    querySb.append("=?");
//    List <Object> values = new ArrayList<Object>();
//    values.add(userId);
//    
//    //if user didn't give userMonsterIds then get all the user's monsters 
//    if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
//    	log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);
//    	querySb.append(" AND ");
//    	querySb.append(DBConstants.MONSTER_FOR_USER__ID);
//    	querySb.append(" IN (");
//    	
//    	int amount = userMonsterIds.size();
//    	List<String> questions = Collections.nCopies(amount, "?");
//    	String questionMarkStr = StringUtils.csvList(questions);
//    	
//    	querySb.append(questionMarkStr);
//    	querySb.append(");");
//    	values.addAll(userMonsterIds);
//    }
//    String query = querySb.toString();
//    log.info("query=" + query + "\t values=" + values);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonsters;
//  }
//
//  public Map<Long, MonsterForUser> getSpecificOrAllUnrestrictedUserMonstersForUser(int userId,
//		Collection<Long> userMonsterIds) {
//  
//  StringBuilder querySb = new StringBuilder();
//  querySb.append("SELECT * FROM ");
//  querySb.append(TABLE_NAME); 
//  querySb.append(" WHERE ");
//  querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
//  querySb.append("=?");
//  querySb.append(" AND ");
//  querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
//  querySb.append("=?");
//  
//  List <Object> values = new ArrayList<Object>();
//  values.add(userId);
//  values.add(false);
//  
//  //if user didn't give userMonsterIds then get all the user's monsters 
//  if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
//  	log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);
//  	querySb.append(" AND ");
//  	querySb.append(DBConstants.MONSTER_FOR_USER__ID);
//  	querySb.append(" IN (");
//  	
//  	int amount = userMonsterIds.size();
//  	List<String> questions = Collections.nCopies(amount, "?");
//  	String questionMarkStr = StringUtils.csvList(questions);
//  	
//  	querySb.append(questionMarkStr);
//  	querySb.append(");");
//  	values.addAll(userMonsterIds);
//  }
//  String query = querySb.toString();
//  log.info("query=" + query + "\t values=" + values);
//
//  Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//  	log.error("monster for user retrieve db error.", e);
//  } finally {
//  	DBConnection.get().close(rs, null, conn);
//  }
//  return userMonsters;
//}
//
//
//  public Map<Long, MonsterForUser> getSpecificOrAllRestrictedUserMonstersForUser(int userId,
//		Collection<Long> userMonsterIds) {
//  
//  StringBuilder querySb = new StringBuilder();
//  querySb.append("SELECT * FROM ");
//  querySb.append(TABLE_NAME); 
//  querySb.append(" WHERE ");
//  querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
//  querySb.append("=?");
//  querySb.append(" AND ");
//  querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
//  querySb.append("=?");
//  
//  List <Object> values = new ArrayList<Object>();
//  values.add(userId);
//  values.add(true);
//  
//  //if user didn't give userMonsterIds then get all the user's monsters 
//  if (userMonsterIds != null && !userMonsterIds.isEmpty() ) {
//  	log.debug("retrieving user monster for userMonsterIds: " + userMonsterIds);
//  	querySb.append(" AND ");
//  	querySb.append(DBConstants.MONSTER_FOR_USER__ID);
//  	querySb.append(" IN (");
//  	
//  	int amount = userMonsterIds.size();
//  	List<String> questions = Collections.nCopies(amount, "?");
//  	String questionMarkStr = StringUtils.csvList(questions);
//  	
//  	querySb.append(questionMarkStr);
//  	querySb.append(");");
//  	values.addAll(userMonsterIds);
//  }
//  String query = querySb.toString();
//  log.info("query=" + query + "\t values=" + values);
//
//  Connection conn = null;
//		ResultSet rs = null;
//		Map<Long, MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			userMonsters = convertRSToUserMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//  	log.error("monster for user retrieve db error.", e);
//  } finally {
//  	DBConnection.get().close(rs, null, conn);
//  }
//  return userMonsters;
//}
//  
//  /*
//  ////@Cacheable(value="userMonstersWithMonsterId", key="#userId+':'+#monsterId")
//  public List<MonsterForUser> getMonstersWithMonsterIdAndUserId(int userId, int monsterId) {
//    log.debug("retrieving user monster for user: " + userId + ", monsterId: " + monsterId);
//
//    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
//    paramsToVals.put(DBConstants.MONSTER_FOR_USER__USER_ID, userId);
//    paramsToVals.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, monsterId);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		List<MonsterForUser> userMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
//			userMonsters = convertRSToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userMonsters;
//  }*/
//  
//  public Map<Integer, MonsterForUser> getPieceDeficientIncompleteMonstersWithUserAndMonsterIds(
//  		int userId, Collection<Integer> monsterIds) {
//  	int size = monsterIds.size();
//  	List<String> questions = Collections.nCopies(size, "?");
//  	String delimiter = ",";
//  	
//  	String query = "select * from " + TABLE_NAME + " where ";
//    List<Object> values = new ArrayList<Object>();
//
//    //creating a "column in (value1,value2,...,value)" condition, prefer this over
//    //chained "or"s  e.g. (column=value1 or column=value2 or...column=value);
//    query += DBConstants.MONSTER_FOR_USER__USER_ID + "=? and ";
//    values.add(userId);
//    query += DBConstants.MONSTER_FOR_USER__MONSTER_ID + " in (" +
//    StringUtils.getListInString(questions, delimiter) + ") and ";
//    values.addAll(monsterIds);
//    
//    query += DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES + "=? and ";
//    values.add(false);
//
//    query += DBConstants.MONSTER_FOR_USER__IS_COMPLETE + "=?;";
//    values.add(false);
//
//    Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, MonsterForUser> incompleteMonsters = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			incompleteMonsters = convertRSToMonsterIdsToMonsters(rs);
//		} catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return incompleteMonsters;
//  }
//  
//  public Map<Integer, Map<Long, MonsterForUser>> getCompleteMonstersForUser(
//  		List<Integer> userIds) {
//  	StringBuilder querySb = new StringBuilder();
//  	
//  	querySb.append("SELECT * FROM ");
//  	querySb.append(TABLE_NAME);
//  	querySb.append(" WHERE ");
//  	querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
//  	querySb.append(" IN (");
//  	
//  	int amount = userIds.size();
//  	List<String> questions = Collections.nCopies(amount, "?");
//  	String questionStr = StringUtils.csvList(questions);
//  	querySb.append(questionStr);
//  	querySb.append(") AND ");
//  	
//  	querySb.append(DBConstants.MONSTER_FOR_USER__IS_COMPLETE);
//  	querySb.append(" =?;");
//  	
//  	List<Object> values = new ArrayList<Object>();
//  	values.addAll(userIds);
//  	values.add(true);
//
//    String query = querySb.toString();
//    log.info("query=" + query + "\t values=" + values);
//
//    Connection conn = null;
//    ResultSet rs = null;
//    Map<Integer, Map<Long, MonsterForUser>> userIdsToMfuIdsToMonsters = null;
//    
//    try {
//    	conn = DBConnection.get().getConnection();
//    	rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//    	userIdsToMfuIdsToMonsters = convertRSToUserIdsToMfuIdsToMonsters(rs);
//    } catch (Exception e) {
//    	log.error("monster for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//    return userIdsToMfuIdsToMonsters;
//  	
//  }
//
//  private Map<Integer, List<MonsterForUser>> convertRSToMonsterIdsToMonsterList(
//      ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        Map<Integer, List<MonsterForUser>> monsterIdsToMonsters = new HashMap<Integer, List<MonsterForUser>>();
//        while(rs.next()) {
//          MonsterForUser userMonster = convertRSRowToMonster(rs);
//          List<MonsterForUser> userMonstersForMonsterId = monsterIdsToMonsters.get(userMonster.getMonsterId());
//          if (userMonstersForMonsterId != null) {
//            userMonstersForMonsterId.add(userMonster);
//          } else {
//            List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
//            userMonsters.add(userMonster);
//            monsterIdsToMonsters.put(userMonster.getMonsterId(), userMonsters);
//          }
//        }
//        return monsterIdsToMonsters;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//  
//  private Map<Integer, List<MonsterForUser>> convertRSToUserIdsToCurrentTeam(
//      ResultSet rs) {
//  	Map<Integer, List<MonsterForUser>> userIdsToCurrentTeam = new HashMap<Integer, List<MonsterForUser>>();
//  	
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        while(rs.next()) {
//          MonsterForUser userMonster = convertRSRowToMonster(rs);
//          if (null == userMonster) {
//          	continue;
//          }
//          
//          int userId = userMonster.getUserId();
//
//          //if just saw this user for first time, create the list for his team
//          if (!userIdsToCurrentTeam.containsKey(userId)) {
//          	List<MonsterForUser> currentTeam = new ArrayList<MonsterForUser>();
//          	userIdsToCurrentTeam.put(userId, currentTeam);
//          }
//
//          //get the user's team and add in the monster
//          List<MonsterForUser> currentTeam = userIdsToCurrentTeam.get(userId);
//          currentTeam.add(userMonster);
//        }
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//      }
//    }
//    return userIdsToCurrentTeam;
//  }
//  
//  private Map<Integer, MonsterForUser> convertRSToMonsterIdsToMonsters(ResultSet rs) {
//  	Map<Integer, MonsterForUser> monsterIdsToMonsters = new HashMap<Integer, MonsterForUser>();
//  	if (rs != null) {
//  		try {
//  			rs.last();
//  			rs.beforeFirst();
//  			while(rs.next()) {
//  				MonsterForUser userMonster = convertRSRowToMonster(rs);
//  				if (userMonster != null) {
//  					monsterIdsToMonsters.put(userMonster.getMonsterId(), userMonster);
//  				}
//  			}
//  		} catch (SQLException e) {
//  			log.error("problem with database call.", e);
//  		}
//  	}
//  	return monsterIdsToMonsters;
//  }
//  
//  private Map<Long, MonsterForUser> convertRSToUserMonsterIdsToMonsters(ResultSet rs) {
//  	Map<Long, MonsterForUser> monsterIdsToMonsters = new HashMap<Long, MonsterForUser>();
//  	if (rs != null) {
//  		try {
//  			rs.last();
//  			rs.beforeFirst();
//  			while(rs.next()) {
//  				MonsterForUser userMonster = convertRSRowToMonster(rs);
//  				if (userMonster != null) {
//  					monsterIdsToMonsters.put(userMonster.getId(), userMonster);
//  				}
//  			}
//  		} catch (SQLException e) {
//  			log.error("problem with database call.", e);
//  		}
//  	}
//  	return monsterIdsToMonsters;
//  }
//
//  private List<MonsterForUser> convertRSToMonsters(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        List<MonsterForUser> userMonsters = new ArrayList<MonsterForUser>();
//        while(rs.next()) {  //should only be one
//          userMonsters.add(convertRSRowToMonster(rs));
//        }
//        return userMonsters;
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//
//  private MonsterForUser convertRSSingleToMonsters(ResultSet rs) {
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        while(rs.next()) {  //should only be one
//          return convertRSRowToMonster(rs);
//        }
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    return null;
//  }
//  
//  private Map<Integer, Map<Long, MonsterForUser>> convertRSToUserIdsToMfuIdsToMonsters(ResultSet rs) {
//  	Map<Integer, Map<Long, MonsterForUser>> userIdsToMfuIdsToMonsters =
//  			new HashMap<Integer, Map<Long, MonsterForUser>>();
//  	if (rs != null) {
//  		try {
//  			rs.last();
//  			rs.beforeFirst();
//  			
//  			while(rs.next()) {
//  				MonsterForUser userMonster = convertRSRowToMonster(rs);
//  				if (userMonster == null) {
//  					continue;
//  				}
//  				
//  				int userId = userMonster.getUserId();
//  				//base case where have not seen user before
//  				if (!userIdsToMfuIdsToMonsters.containsKey(userId)) {
//  					Map<Long, MonsterForUser> mfuIdsToMonsters = new HashMap<Long, MonsterForUser>();
//  					userIdsToMfuIdsToMonsters.put(userId, mfuIdsToMonsters);
//  				}
//  				
//  				Map<Long, MonsterForUser> mfuIdsToMonsters = userIdsToMfuIdsToMonsters.get(userId);
//  				mfuIdsToMonsters.put(userMonster.getId(), userMonster);
//  			}
//  			
//  		} catch (SQLException e) {
//  			log.error("problem with database call.", e);
//  		}
//  	}
//  	return userIdsToMfuIdsToMonsters;
//  }
//
//  /*
//   * assumes the resultset is apprpriately set up. traverses the row it's on.
//   */
//  private MonsterForUser convertRSRowToMonster(ResultSet rs) throws SQLException {
//    int id = rs.getInt(DBConstants.MONSTER_FOR_USER__ID);
//    int userId = rs.getInt(DBConstants.MONSTER_FOR_USER__USER_ID);
//    int monsterId = rs.getInt(DBConstants.MONSTER_FOR_USER__MONSTER_ID);
//    int currentExp = rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE);
//    int currentLvl = rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL);
//    int currentHealth = rs.getInt(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH);
//    int numPieces = rs.getInt(DBConstants.MONSTER_FOR_USER__NUM_PIECES);
//    boolean hasAllPieces = rs.getBoolean(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES);
//    boolean isComplete = rs.getBoolean(DBConstants.MONSTER_FOR_USER__IS_COMPLETE);
//    
//    Date combineStartTime = null;
//    try {
//    	Timestamp ts = rs.getTimestamp(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME);
//    	if (!rs.wasNull()) {
//    		combineStartTime = new Date(ts.getTime());
//    	}
//    } catch(Exception e) {
//    	log.error("maybe combineStartTime is null for monsterForUserId=" + id +
//    			" userId=" + userId, e);
//    }
//    int teamSlotNum = rs.getInt(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM);
//    String sourceOfPieces = rs.getString(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES);
//    boolean restricted = rs.getBoolean(DBConstants.MONSTER_FOR_USER__RESTRICTED);
//    int offensiveSkillId = rs.getInt(DBConstants.MONSTER_FOR_USER__OFFENSIVE_SKILL_ID);
//    int defensiveSkillId = rs.getInt(DBConstants.MONSTER_FOR_USER__DEFENSIVE_SKILL_ID);
//    
//    MonsterForUser userMonster = new MonsterForUser(id, userId, monsterId,
//    		currentExp, currentLvl, currentHealth, numPieces, hasAllPieces,
//    		isComplete, combineStartTime, teamSlotNum, sourceOfPieces, restricted,
//    		offensiveSkillId, defensiveSkillId);
//    return userMonster;
//    
//  }
//
//}
