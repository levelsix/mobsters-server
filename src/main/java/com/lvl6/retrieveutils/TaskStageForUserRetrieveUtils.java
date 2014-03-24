package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.TaskStageForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskStageForUserRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_FOR_USER;
  
  public static TaskStageForUser getTaskStageForUserWithId(long taskStageForUserId) {
    Connection conn = DBConnection.get().getConnection();
    Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
    absoluteConditionParams.put(DBConstants.TASK_STAGE_FOR_USER__ID, taskStageForUserId);
    
    ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
    TaskStageForUser tsfu = convertRSToUserTask(rs);
    DBConnection.get().close(rs, null, conn);
    return tsfu;
  }
  
  public static List<TaskStageForUser> getTaskStagesForUserWithTaskForUserId (long taskForUserId) {
  	Connection conn = DBConnection.get().getConnection();
  	Map<String, Object> absoluteConditionParams = new HashMap<String, Object>();
  	absoluteConditionParams.put(DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID, taskForUserId);
  	
  	ResultSet rs = DBConnection.get().selectRowsAbsoluteAnd(conn, absoluteConditionParams, TABLE_NAME);
  	List<TaskStageForUser> tsfuList = convertRSToUserTaskStageList(rs);
    DBConnection.get().close(rs, null, conn);
  	return tsfuList;
  }
  
//  public static TaskStageForUser getTaskStageForUserForUserId(int userId) {
//    Connection conn = DBConnection.get().getConnection();
//    
//    ResultSet rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
//    TaskStageForUser ut = convertRSToUserTask(rs);
//    DBConnection.get().close(rs, null, conn);
//    return ut;
//  }
  
  private static TaskStageForUser convertRSToUserTask(ResultSet rs) {
    List<TaskStageForUser> utList = new ArrayList<TaskStageForUser>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          TaskStageForUser ut = convertRSRowToUserTask(rs);
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
  
  private static List<TaskStageForUser> convertRSToUserTaskStageList(ResultSet rs) {
    List<TaskStageForUser> utsList = new ArrayList<TaskStageForUser>();
    if (rs != null) {
      try {
        rs.last();
        rs.beforeFirst();
        while(rs.next()) {  //should only be one
          TaskStageForUser ut = convertRSRowToUserTask(rs);
          if (null != ut) {
          	utsList.add(ut);
          }
        }
      } catch (SQLException e) {
        log.error("problem with database call.", e);
        
      }
    }
    return utsList;
  }
  
  
  private static TaskStageForUser convertRSRowToUserTask(ResultSet rs) throws SQLException {
    int i = 1;
    long id = rs.getLong(i++);
    long taskForUserId = rs.getLong(i++);
    int stageNum = rs.getInt(i++);
    int tsmId = rs.getInt(i++);
    String monsterType = rs.getString(i++);
    int expGained = rs.getInt(i++);
    int cashGained = rs.getInt(i++);
    int oilGained = rs.getInt(i++);
    boolean monsterPieceDropped = rs.getBoolean(i++);
    int itemIdDropped = rs.getInt(i++);
    
    return new TaskStageForUser(id, taskForUserId, stageNum, tsmId, monsterType,
    		expGained, cashGained, oilGained, monsterPieceDropped, itemIdDropped);
  }
}
