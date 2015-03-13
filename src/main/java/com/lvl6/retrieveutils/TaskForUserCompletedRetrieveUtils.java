package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class TaskForUserCompletedRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_COMPLETED;
	private static final UserTaskCompletedForClientMapper rowMapper = new UserTaskCompletedForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Integer> getTaskIds(List<UserTaskCompleted> completedTasks) {

		List<Integer> taskIds = new ArrayList<Integer>();
		for (UserTaskCompleted utc : completedTasks) {
			int taskId = utc.getTaskId();
			taskIds.add(taskId);
		}

		return taskIds;
	}

	//	public List<Integer> getAllTaskIdsForUser(String userId) {
	//
	//		StringBuilder querySb = new StringBuilder();
	//		querySb.append("SELECT DISTINCT(");
	//		querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID);
	//		querySb.append(")");
	//		querySb.append(" FROM ");
	//		querySb.append(TABLE_NAME);
	//		querySb.append(" WHERE ");
	//		querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID);
	//		querySb.append("=?");
	//
	//		List <Object> values = new ArrayList<Object>();
	//		values.add(userId);
	//
	//		String query = querySb.toString();
	//		log.info(String.format("query=%s, values=%s",
	//			query, values));
	//
	//		List<Integer> taskIds = null;
	//		try {
	//			taskIds = this.jdbcTemplate
	//				.queryForList(query, values.toArray(), Integer.class);
	//
	//		} catch (Exception e) {
	//			log.error("sql query wrong 2", e);
	//			taskIds = new ArrayList<Integer>();
	//			//  	} finally {
	//			//  		DBConnection.get().close(rs, null, conn);
	//		}
	//		return taskIds;
	//	}

	public UserTaskCompleted getCompletedTaskForUser(String userId, int taskId) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT ");
		querySb.append(UserTaskCompletedForClientMapper.getColumnsSelectedStr());
		querySb.append(" FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID);
		querySb.append("=?");

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(taskId);

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, values));

		UserTaskCompleted utc = null;
		try {
			List<UserTaskCompleted> utcList = this.jdbcTemplate.query(query,
					values.toArray(), rowMapper);

			if (null != utcList && !utcList.isEmpty()) {
				utc = utcList.get(0);
			}

		} catch (Exception e) {
			log.error("sql query wrong 2", e);
			//  	} finally {
			//  		DBConnection.get().close(rs, null, conn);
		}
		return utc;
	}

	public List<UserTaskCompleted> getAllCompletedTasksForUser(String userId) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT ");
		querySb.append(UserTaskCompletedForClientMapper.getColumnsSelectedStr());
		querySb.append(" FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID);
		querySb.append("=?");

		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, values));

		List<UserTaskCompleted> utcList = null;
		try {
			utcList = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("sql query wrong 2", e);
			utcList = new ArrayList<UserTaskCompleted>();
			//  	} finally {
			//  		DBConnection.get().close(rs, null, conn);
		}
		return utcList;
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

	public static class UserTaskCompleted {
		private String userId;
		private int taskId;
		private int unclaimedCash;
		private int unclaimedOil;

		public UserTaskCompleted() {
			super();
		}

		public UserTaskCompleted(String userId, int taskId, int unclaimedCash,
				int unclaimedOil) {
			super();
			this.userId = userId;
			this.taskId = taskId;
			this.unclaimedCash = unclaimedCash;
			this.unclaimedOil = unclaimedOil;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public int getTaskId() {
			return taskId;
		}

		public void setTaskId(int taskId) {
			this.taskId = taskId;
		}

		public int getUnclaimedCash() {
			return unclaimedCash;
		}

		public void setUnclaimedCash(int unclaimedCash) {
			this.unclaimedCash = unclaimedCash;
		}

		public int getUnclaimedOil() {
			return unclaimedOil;
		}

		public void setUnclaimedOil(int unclaimedOil) {
			this.unclaimedOil = unclaimedOil;
		}

		@Override
		public String toString() {
			return "UserTaskCompleted [userId=" + userId + ", taskId=" + taskId
					+ ", unclaimedCash=" + unclaimedCash + ", unclaimedOil="
					+ unclaimedOil + "]";
		}

	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserTaskCompletedForClientMapper implements
			RowMapper<UserTaskCompleted> {

		private static List<String> columnsSelected;
		private static String columnsSelectedStr;

		@Override
		public UserTaskCompleted mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			UserTaskCompleted utc = new UserTaskCompleted();
			utc.setUserId(rs
					.getString(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID));
			utc.setTaskId(rs
					.getInt(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID));
			utc.setUnclaimedCash(rs
					.getInt(DBConstants.TASK_FOR_USER_COMPLETED__UNCLAIMED_CASH));
			utc.setUnclaimedOil(rs
					.getInt(DBConstants.TASK_FOR_USER_COMPLETED__UNCLAIMED_OIL));

			return utc;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.TASK_FOR_USER_COMPLETED__USER_ID);
				columnsSelected
						.add(DBConstants.TASK_FOR_USER_COMPLETED__TASK_ID);
				columnsSelected
						.add(DBConstants.TASK_FOR_USER_COMPLETED__UNCLAIMED_CASH);
				columnsSelected
						.add(DBConstants.TASK_FOR_USER_COMPLETED__UNCLAIMED_OIL);
			}
			return columnsSelected;
		}

		public static String getColumnsSelectedStr() {
			if (null == columnsSelectedStr) {
				columnsSelectedStr = StringUtils.csvList(getColumnsSelected());
			}
			if (null == columnsSelectedStr || columnsSelectedStr.isEmpty()) {
				return "*";
			}
			return columnsSelectedStr;
		}
	}
}
