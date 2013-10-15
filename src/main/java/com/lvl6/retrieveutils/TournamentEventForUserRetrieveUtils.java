package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.TournamentEvent;
import com.lvl6.info.UserLeaderboardEvent;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.rarechange.TournamentEventRetrieveUtils;
import com.lvl6.utils.DBConnection;

public class TournamentEventForUserRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_TOURNAMENT_EVENT_FOR_USER;

  public static UserLeaderboardEvent getSpecificUserLeaderboardEvent(
      int leaderboardEventId, int userId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__TOURNAMENT_EVENT_ID, leaderboardEventId);
    paramsToVals.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__USER_ID, userId);

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
    UserLeaderboardEvent ulbe = grabUserLeaderboardEventFromRS(rs);
    DBConnection.get().close(rs, null, conn);
    return ulbe;
  }
  
  public static List<UserLeaderboardEvent> getUserLeaderboardEventsForUserId(int userId) {
    TreeMap <String, Object> paramsToVals = new TreeMap<String, Object>();
    paramsToVals.put(DBConstants.TOURNAMENT_EVENT_FOR_USER__USER_ID, userId);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals, TABLE_NAME);
    List<UserLeaderboardEvent> userLeaderboardEvents = grabUserLeaderboardEventsFromRS(rs);
    DBConnection.get().close(rs, null, conn);
    return userLeaderboardEvents;
  }

  public static List<UserLeaderboardEvent> getActiveUserLeaderboardEventsForUserId(int userId){
    List<UserLeaderboardEvent> events = getUserLeaderboardEventsForUserId(userId);
    List<UserLeaderboardEvent> toReturn = new ArrayList<UserLeaderboardEvent>();
    
    long curTime = new Date().getTime();
    for (UserLeaderboardEvent e : events) {
      TournamentEvent l = TournamentEventRetrieveUtils.getTournamentEventForId(e.getLeaderboardEventId());
      if(l.getEndDate().getTime() > curTime) {
        toReturn.add(e);
      }
    }
    
    return toReturn.size() > 0 ? toReturn : null;
  }

  private static UserLeaderboardEvent grabUserLeaderboardEventFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {
          UserLeaderboardEvent ulbe = convertRSRowToUserLeaderboardEvent(rs);
          return ulbe;
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  private static List<UserLeaderboardEvent> grabUserLeaderboardEventsFromRS(ResultSet rs) {
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        List<UserLeaderboardEvent> userLeaderboardEvents = new ArrayList<UserLeaderboardEvent>();
        while(rs.next()) {
          UserLeaderboardEvent ulbe = convertRSRowToUserLeaderboardEvent(rs);
          userLeaderboardEvents.add(ulbe);
        }
        return userLeaderboardEvents;
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return null;
  }
  
  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private static UserLeaderboardEvent convertRSRowToUserLeaderboardEvent(ResultSet rs) throws SQLException {
    int i = 1;
    int leaderboardEventId = rs.getInt(i++);
    int userId = rs.getInt(i++);
    int battlesWon = rs.getInt(i++);
    int battlesLost = rs.getInt(i++);
    int battlesFled = rs.getInt(i++);
    
    return new UserLeaderboardEvent(leaderboardEventId, userId, battlesWon, battlesLost, battlesFled);
  }
  
}
