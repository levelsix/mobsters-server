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

import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskForUserOngoingRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
  
  public static TaskForUserOngoing getUserTaskForId(long userTaskId) {
    Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.TASK_FOR_USER_ONGOING__ID, userTaskId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    TaskForUserOngoing ut = convertRSToUserTask(rs);
    return ut;
  }
  
  public static TaskForUserOngoing getUserTaskForUserId(int userId) {
    Connection conn = DBConnection.get().getConnection();
    
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    TaskForUserOngoing ut = convertRSToUserTask(rs);
    return ut;
  }
  
  private static TaskForUserOngoing convertRSToUserTask(ResultSet rs) {
    List<TaskForUserOngoing> utList = new ArrayList<TaskForUserOngoing>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          TaskForUserOngoing ut = convertRSRowToUserTask(rs);
          utList.add(ut);
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    
    //error checking. There should only be one row in user_task table for any user
    if (utList.isEmpty()) {
      return null;
    } else {
      if (utList.size() > 1) {
        log.error("unexpected error: user has more than one user_task. userTasks=" +
            utList);
      }
      return utList.get(0);
    }
  }
  
  private static TaskForUserOngoing convertRSRowToUserTask(ResultSet rs) throws SQLException {
    int i = 1;
    long id = rs.getLong(i++);
    int userId = rs.getInt(i++);
    int taskId = rs.getInt(i++);
    
    int expGained = rs.getInt(i++);
    int cashGained = rs.getInt(i++);
    int numRevives = rs.getInt(i++);
    
    Date startDate = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		startDate = new Date(ts.getTime());
    	}
    } catch (Exception e) {
    	log.error("db error: start_date is null. userId=" + userId, e);
    }
    
    return new TaskForUserOngoing(id, userId, taskId, expGained, cashGained,
    		numRevives, startDate);
  }
}
