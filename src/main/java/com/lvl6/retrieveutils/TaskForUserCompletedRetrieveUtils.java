package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class TaskForUserCompletedRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_COMPLETED;
  
  
  public static List<Integer> getAllTaskIdsForUser(int userId) {
  	
  	StringBuilder querySb = new StringBuilder();
  	querySb.append("SELECT DISTINCT(");
  	querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID);
  	querySb.append(")");
  	querySb.append(" FROM ");
  	querySb.append(TABLE_NAME);
  	querySb.append(" WHERE ");
  	querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID);
  	querySb.append("=?");
  	
  	List <Object> values = new ArrayList<Object>();
    values.add(userId);
    
    String query = querySb.toString();
    log.info("query=" + query + "\t values=" + values);
    
    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    List<Integer> taskIds = new ArrayList<Integer>();
    try {
  		if (null == rs) {
  			return taskIds;
  		}
  		try {
  			rs.last();
  			rs.beforeFirst();
  			while(rs.next()) {
  				int taskId = rs.getInt(1);
  				taskIds.add(taskId);
  			}
  		} catch (SQLException e) {
  			log.error("problem with database call.", e);
  		}
  	} catch (Exception e) {
  		log.error("sql query wrong 2", e);
  	} finally {
  		DBConnection.get().close(rs, null, conn);
  	}
    return taskIds;
  }
  
//  private static TaskForUserCompleted convertRSToUserTaskCompleted(ResultSet rs) {
//    List<TaskForUserCompleted> utList = new ArrayList<TaskForUserCompleted>();
//    if (rs != null) {
//      try {
//        rs.last();
//        rs.beforeFirst();
//        while(rs.next()) {  //should only be one
//          TaskForUserCompleted ut = convertRSRowToUserTaskCompleted(rs);
//          utList.add(ut);
//        }
//      } catch (SQLException e) {
//        log.error("problem with database call.", e);
//        
//      }
//    }
//    
//    //error checking. There should only be one row in user_task table for any user
//    if (utList.isEmpty()) {
//      return null;
//    } else {
//      if (utList.size() > 1) {
//        log.error("unexpected error: user has more than one user_task. userTasks=" +
//            utList);
//      }
//      return utList.get(0);
//    }
//  }
  
//  private static TaskForUserCompleted convertRSRowToUserTaskCompleted(ResultSet rs) throws SQLException {
//    int i = 1;
//    int userId = rs.getInt(i++);
//    int taskId = rs.getInt(i++);
//    
//    Date timeOfEntry = null;
//    try {
//    	Timestamp ts = rs.getTimestamp(i++);
//    	if (!rs.wasNull()) {
//    		timeOfEntry = new Date(ts.getTime());
//    	}
//    } catch (Exception e) {
//    	log.error("db error: start_date is null. userId=" + userId, e);
//    }
//    
//    return new TaskForUserCompleted(userId, taskId, timeOfEntry);
//  }
}
