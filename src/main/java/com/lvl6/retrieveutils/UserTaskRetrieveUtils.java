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
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.UserTask;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class UserTaskRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_USER_TASK;
  
  public static UserTask getUserTaskForId(long userTaskId) {
    Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.USER_TASK__ID, userTaskId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    UserTask ut = convertRSToUserTask(rs);
    return ut;
  }
  
  public static UserTask getUserTaskForUserId(int userId) {
    Connection conn = DBConnection.get().getConnection();
    
    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
    UserTask ut = convertRSToUserTask(rs);
    return ut;
  }
  
  private static UserTask convertRSToUserTask(ResultSet rs) {
    List<UserTask> utList = new ArrayList<UserTask>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          UserTask ut = convertRSRowToUserTask(rs);
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
      if (utList.size() >= 1) {
        log.error("unexpected error: user has more than one user_task. userTasks=" +
            utList);
      }
      return utList.get(0);
    }
  }
  
  private static UserTask convertRSRowToUserTask(ResultSet rs) throws SQLException {
    int i = 1;
    long id = rs.getLong(i++);
    int userId = rs.getInt(i++);
    int taskId = rs.getInt(i++);
    
    String monsterRewardEquipIdsStr = rs.getString(i++);
    List<Integer> monsterRewardEquipIds = new ArrayList<Integer>();
    if (monsterRewardEquipIdsStr != null) {
      StringTokenizer st = new StringTokenizer(monsterRewardEquipIdsStr, ", ");
      while (st.hasMoreTokens()) {
        monsterRewardEquipIds.add(Integer.parseInt(st.nextToken()));
      }
    }
    
    int expGained = rs.getInt(i++);
    int silverGained = rs.getInt(i++);
    int numRevives = rs.getInt(i++);
    
    Date startDate = null;
    Timestamp ts = rs.getTimestamp(i++);
    if (!rs.wasNull()) {
    	startDate = new Date(ts.getTime());
    }
    
    String stageExps = rs.getString(i++);
    String stageSilvers = rs.getString(i++);
    
    return new UserTask(id, userId, taskId, monsterRewardEquipIds, expGained,
    		silverGained, numRevives, startDate, stageExps, stageSilvers);
  }
}
