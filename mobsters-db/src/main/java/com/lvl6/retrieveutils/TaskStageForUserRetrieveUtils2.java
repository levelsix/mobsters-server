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

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageForUserPojo;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class TaskStageForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(TaskStageForUserRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_FOR_USER;
	private static final UserTaskStageForClientMapper rowMapper = new UserTaskStageForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//
	//	public TaskStageForUserPojo getTaskStageForUserPojoWithId(String taskStageForUserId) {
	//		Object[] values = { taskStageForUserId };
	//		String query = String.format(
	//			"select * from %s where %s=?",
	//			TABLE_NAME, DBConstants.TASK_STAGE_FOR_USER__ID);
	//
	//		TaskStageForUserPojo tsfu = null;
	//		try {
	//			List<TaskStageForUserPojo> tsfuList = this.jdbcTemplate
	//				.query(query, values, rowMapper);
	//			if (null != tsfuList && !tsfuList.isEmpty()) {
	//				tsfu = tsfuList.get(0);
	//			}
	//
	//		} catch (Exception e) {
	//			log.error("TaskStageForUserPojo retrieve db error.", e);
	////		} finally {
	////			DBConnection.get().close(rs, null, conn);
	//		}
	//		return tsfu;
	//	}

	public List<TaskStageForUserPojo> getTaskStagesForUserWithTaskForUserId(
			String taskForUserId) {
		Object[] values = { taskForUserId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID);

		List<TaskStageForUserPojo> tsfuList = null;
		try {
			tsfuList = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("TaskStageForUserPojo retrieve db error.", e);
			tsfuList = new ArrayList<TaskStageForUserPojo>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return tsfuList;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserTaskStageForClientMapper implements
			RowMapper<TaskStageForUserPojo> {

		private static List<String> columnsSelected;

		@Override
		public TaskStageForUserPojo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			TaskStageForUserPojo tsfu = new TaskStageForUserPojo();
			tsfu.setId(rs.getString(DBConstants.TASK_STAGE_FOR_USER__ID));
			tsfu.setTaskForUserId(rs
					.getString(DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID));
			tsfu.setStageNum(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__STAGE_NUM));
			tsfu.setTaskStageMonsterId(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID));

			String monsterType = rs
					.getString(DBConstants.TASK_STAGE_FOR_USER__MONSTER_TYPE);
			if (null != monsterType) {
				String newMonsterType = monsterType.trim().toUpperCase();
				if (!monsterType.equals(newMonsterType)) {
					log.error(String.format("monsterType incorrect: %s, id=%s",
							monsterType, tsfu.getId()));
					monsterType = newMonsterType;
				}
			}
			tsfu.setMonsterType(monsterType);

			tsfu.setExpGained(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__EXP_GAINED));
			tsfu.setCashGained(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__CASH_GAINED));
			tsfu.setOilGained(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__OIL_GAINED));
			tsfu.setMonsterPieceDropped(rs
					.getBoolean(DBConstants.TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED));
			tsfu.setItemIdDropped(rs
					.getInt(DBConstants.TASK_STAGE_FOR_USER__ITEM_ID_DROPPED));
			tsfu.setAttackedFirst(rs
					.getBoolean(DBConstants.TASK_STAGE_FOR_USER__ATTACKED_FIRST));

			tsfu.setMonsterLvl(rs.getInt(
					DBConstants.TASK_STAGE_FOR_USER__MONSTER_LVL));

			return tsfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.TASK_STAGE_FOR_USER__ID);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__TASK_FOR_USER_ID);
				columnsSelected.add(DBConstants.TASK_STAGE_FOR_USER__STAGE_NUM);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__MONSTER_TYPE);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__EXP_GAINED);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__CASH_GAINED);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__OIL_GAINED);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__ITEM_ID_DROPPED);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__ATTACKED_FIRST);
				columnsSelected
						.add(DBConstants.TASK_STAGE_FOR_USER__MONSTER_LVL);
			}
			return columnsSelected;
		}
	}
}
