package com.lvl6.retrieveutils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class TaskForUserCompletedRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_COMPLETED;
  private JdbcTemplate jdbcTemplate;

  @Resource
  public void setDataSource(DataSource dataSource) {
	  log.info("Setting datasource and creating jdbcTemplate");
	  this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<Integer> getAllTaskIdsForUser(int userId) {
  	
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
    log.info(String.format("query=%s, values=%s",
    	query, values));
    
    List<Integer> taskIds = null;
    try {
    	taskIds = this.jdbcTemplate
			.queryForList(query, values.toArray(), Integer.class);
    	
  	} catch (Exception e) {
  		log.error("sql query wrong 2", e);
  		taskIds = new ArrayList<Integer>();
//  	} finally {
//  		DBConnection.get().close(rs, null, conn);
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
