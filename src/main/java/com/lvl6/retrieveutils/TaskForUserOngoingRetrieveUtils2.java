package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class TaskForUserOngoingRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
	private static final OngoingUserTaskForClientMapper rowMapper = new OngoingUserTaskForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TaskForUserOngoing getUserTaskForId(String userTaskId) {
		
		Object[] values = { userTaskId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.TASK_FOR_USER_ONGOING__ID);

		TaskForUserOngoing tfuo = null;
		try {
			List<TaskForUserOngoing> pbfuList = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			if (null != pbfuList && !pbfuList.isEmpty()) {
				tfuo = pbfuList.get(0);
			}
			
		} catch (Exception e) {
			log.error("TaskForUserOngoing retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		
		return tfuo;
	}

	public TaskForUserOngoing getUserTaskForUserId(String userId) {

		Object[] values = { userId };
		String query = String.format(
			"select * from %s where %s=?",
			TABLE_NAME, DBConstants.TASK_FOR_USER_ONGOING__USER_ID);

		TaskForUserOngoing tfuo = null;
		try {
			List<TaskForUserOngoing> tfuoList = this.jdbcTemplate
				.query(query, values, rowMapper);
			
			if (null != tfuoList && !tfuoList.isEmpty()) {
				tfuo = tfuoList.get(0);
			}
			
			if (tfuoList.size() > 1) {
				log.error(String.format(
					"user has more than one user_task. userTasks=%s",
					tfuoList));
			}
			
		} catch (Exception e) {
			log.error("TaskForUserOngoing retrieve db error.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		
		return tfuo;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class OngoingUserTaskForClientMapper implements RowMapper<TaskForUserOngoing> {

		private static List<String> columnsSelected;

		public TaskForUserOngoing mapRow(ResultSet rs, int rowNum) throws SQLException {
			TaskForUserOngoing tfuo = new TaskForUserOngoing();
			tfuo.setId(rs.getString(DBConstants.TASK_FOR_USER_ONGOING__ID));
			tfuo.setUserId(rs.getString(DBConstants.TASK_FOR_USER_ONGOING__USER_ID));
			tfuo.setTaskId(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__TASK_ID));
			tfuo.setExpGained(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__EXP_GAINED));
			tfuo.setCashGained(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__CASH_GAINED));
			tfuo.setOilGained(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__OIL_GAINED));
			tfuo.setNumRevives(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__NUM_REVIVES));

			try {
				Timestamp time = rs.getTimestamp(DBConstants.TASK_FOR_USER_ONGOING__START_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					tfuo.setStartDate(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe startDate is invalid, tfuo=%s", tfuo), e);
			}
			tfuo.setTaskStageId(rs.getInt(DBConstants.TASK_FOR_USER_ONGOING__TASK_STAGE_ID));

			return tfuo;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__ID);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__USER_ID);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__TASK_ID);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__EXP_GAINED);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__CASH_GAINED);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__OIL_GAINED);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__NUM_REVIVES);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__START_TIME);
				columnsSelected.add(DBConstants.TASK_FOR_USER_ONGOING__TASK_STAGE_ID);
			}
			return columnsSelected;
		}
	} 	
}
