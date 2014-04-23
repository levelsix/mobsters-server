package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.QuestForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class QuestForUserRetrieveUtils {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private final String TABLE_NAME = DBConstants.TABLE_QUEST_FOR_USER;
  
  //only used in script
  public List<QuestForUser> getUnredeemedIncompleteUserQuests(int userId) {
    log.debug("retrieving unredeemed and incomplete user quests");
    
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
    paramsToVals.put(DBConstants.QUEST_FOR_USER__IS_REDEEMED, false);
    paramsToVals.put(DBConstants.QUEST_FOR_USER__IS_COMPLETE, false);
    
    Connection conn = null;
		ResultSet rs = null;
		List<QuestForUser> userQuests = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userQuests = convertRSToUserQuests(rs);
		} catch (Exception e) {
    	log.error("quest for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userQuests;
  }
  
  ////@Cacheable(value="unredeemedUserQuestsForUser", key="#userId")
  public List<QuestForUser> getUnredeemedUserQuestsForUser(int userId) {
    log.debug("retrieving unredeemed user quests for userId " + userId);
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
    paramsToVals.put(DBConstants.QUEST_FOR_USER__IS_REDEEMED, false);
    
    Connection conn = null;
		ResultSet rs = null;
		List<QuestForUser> userQuests = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userQuests = convertRSToUserQuests(rs);
		} catch (Exception e) {
    	log.error("quest for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userQuests;
  }
  
  public Map<Integer, QuestForUser> getQuestIdToUnredeemedUserQuests(int userId) {
  	log.debug("retrieving unredeemed user quests map for userId " + userId);
  	TreeMap<String, Object> paramsToVals = new TreeMap<String, Object>();
  	paramsToVals.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
  	paramsToVals.put(DBConstants.QUEST_FOR_USER__IS_REDEEMED, false);
    
  	Connection conn = null;
		ResultSet rs = null;
		Map<Integer, QuestForUser> questIdsToUnredeemedUserQuests = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			questIdsToUnredeemedUserQuests = convertRSToUserQuestsMap(rs);
		} catch (Exception e) {
    	log.error("quest for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return questIdsToUnredeemedUserQuests;
  }
  
  ////@Cacheable(value="userQuestsForUser", key="#userId")
  public List<QuestForUser> getUserQuestsForUser(int userId) {
    log.debug("retrieving user quests for userId " + userId);
    
    Connection conn = null;
		ResultSet rs = null;
		List<QuestForUser> userQuests = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
			userQuests = convertRSToUserQuests(rs);
		} catch (Exception e) {
    	log.error("quest for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userQuests;
  }
  
  public QuestForUser getSpecificUnredeemedUserQuest(int userId, int questId) {
    log.debug("retrieving specific unredeemed user quest for userid " + userId + " and questId " + questId);
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
    paramsToVals.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);
    paramsToVals.put(DBConstants.QUEST_FOR_USER__IS_REDEEMED, false);
    
    Connection conn = null;
		ResultSet rs = null;
		QuestForUser userQuest = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userQuest = convertRSToSingleUserQuest(rs);
		} catch (Exception e) {
    	log.error("quest for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return userQuest;
  }
  
  private QuestForUser convertRSToSingleUserQuest(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          return convertRSRowToUserQuest(rs);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }

  private List<QuestForUser> convertRSToUserQuests(ResultSet rs) {
  	List<QuestForUser> userQuests = new ArrayList<QuestForUser>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
        	QuestForUser uq = convertRSRowToUserQuest(rs);
        	if (null != uq) {
        		userQuests.add(uq);
        	}
        }
        return userQuests;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return userQuests;
  }
  
  private Map<Integer, QuestForUser> convertRSToUserQuestsMap(ResultSet rs) {
  	Map<Integer, QuestForUser> idToUserQuests = new HashMap<Integer, QuestForUser>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
        	QuestForUser uq = convertRSRowToUserQuest(rs);
        	if (null != uq) {
        		int questId = uq.getQuestId();
        		idToUserQuests.put(questId, uq);
        	}
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return idToUserQuests;
  }
  
  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private QuestForUser convertRSRowToUserQuest(ResultSet rs) throws SQLException {
    int i = 1;
    int userId = rs.getInt(i++);
    int questId = rs.getInt(i++);
    boolean isRedeemed = rs.getBoolean(i++);
    boolean isComplete = rs.getBoolean(i++);
    
    QuestForUser userQuest = new QuestForUser(userId, questId, isRedeemed,
    		isComplete);
    return userQuest;
  }
  
}
