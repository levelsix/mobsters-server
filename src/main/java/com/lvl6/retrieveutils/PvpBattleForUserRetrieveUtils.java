package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.PvpBattleForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class PvpBattleForUserRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_PVP_BATTLE_FOR_USER;
  
  //only used in script
  public static PvpBattleForUser getPvpBattleForUserForAttacker(int userId) {
    log.debug("retrieving pvp battle for user");
    
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID, userId);
    
    Connection conn = null;
		ResultSet rs = null;
		PvpBattleForUser pvpBattleForUser = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			pvpBattleForUser = convertRSToSinglePvpBattleForUser(rs);
		} catch (Exception e) {
    	log.error("pvp battle for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
    return pvpBattleForUser;
  }
  
  private static PvpBattleForUser convertRSToSinglePvpBattleForUser(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          return convertRSRowToPvpBattleForUser(rs);
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
  private static PvpBattleForUser convertRSRowToPvpBattleForUser(ResultSet rs) throws SQLException {
    int i = 1;
    int attackerId = rs.getInt(i++);
    int defenderId = rs.getInt(i++);
    int attackerWinEloChange = rs.getInt(i++);
    int defenderLoseEloChange = rs.getInt(i++);
    int attackerLoseEloChange = rs.getInt(i++);
    int defenderWinEloChange = rs.getInt(i++);
    Date defenderOldInBattleShieldEndTime = null;
    
    Timestamp ts = null;
    try {
    	ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		defenderOldInBattleShieldEndTime = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: defenderOldInBattleShieldEndTime not set. attackerId=" + attackerId);
    }
    
    PvpBattleForUser userQuest = new PvpBattleForUser(attackerId, defenderId,
    		attackerWinEloChange, defenderLoseEloChange, attackerLoseEloChange,
    		defenderWinEloChange, defenderOldInBattleShieldEndTime);
    return userQuest;
  }
  
}
