package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.QuestJobForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component
public class QuestJobForUserRetrieveUtil {
	private static Logger log = LoggerFactory
			.getLogger(QuestJobForUserRetrieveUtil.class);

	private static final String TABLE_NAME = DBConstants.TABLE_QUEST_JOB_FOR_USER;
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public QuestJobForUser getSpecificUserQuestJob(int userId, int questJobId) {
		QuestJobForUser qjfu = null;
		try {
			List<String> columnsToSelected = UserQuestJobForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.QUEST_JOB_FOR_USER__USER_ID,
					userId);
			equalityConditions.put(
					DBConstants.QUEST_JOB_FOR_USER__QUEST_JOB_ID, questJobId);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityConditions(columnsToSelected,
							TABLE_NAME, equalityConditions, conditionDelimiter,
							values, preparedStatement);

			log.info("getSpecificUserQuestJob() query=" + query);

			qjfu = this.jdbcTemplate.queryForObject(query,
					new UserQuestJobForClientMapper());
		} catch (Exception e) {
			log.error("could not retrieve user quest job for userId=" + userId
					+ " and questJobId=" + questJobId, e);
		}

		return qjfu;
	}

	public Map<Integer, QuestJobForUser> getQuestJobIdsToJobs(int userId,
			int questId) {
		Map<Integer, QuestJobForUser> questJobIdToJobs = null;
		try {
			List<String> columnsToSelected = UserQuestJobForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.QUEST_JOB_FOR_USER__USER_ID,
					userId);
			equalityConditions.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID,
					questId);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityConditions(columnsToSelected,
							TABLE_NAME, equalityConditions, conditionDelimiter,
							values, preparedStatement);

			log.info("getQuestJobIdsToJobs() query=" + query);

			List<QuestJobForUser> qjfuList = this.jdbcTemplate.query(query,
					new UserQuestJobForClientMapper());

			//map by quest job id to QuestJobForUser
			questJobIdToJobs = new HashMap<Integer, QuestJobForUser>();
			for (QuestJobForUser qjfu : qjfuList) {
				int questJobId = qjfu.getQuestJobId();
				questJobIdToJobs.put(questJobId, qjfu);
			}
		} catch (Exception e) {
			log.error("getQuestJobIdsToJobs() could not retrieve"
					+ " UserQuestJobs for userId=" + userId + "questId="
					+ questId);
			questJobIdToJobs = new HashMap<Integer, QuestJobForUser>();
		}
		return questJobIdToJobs;
	}

	public Map<Integer, Collection<QuestJobForUser>> getSpecificOrAllQuestIdToQuestJobsForUserId(
			String userId, Collection<Integer> questIds) {
		Map<Integer, Collection<QuestJobForUser>> questIdToQjfuList = null;
		try {
			List<String> columnsToSelected = UserQuestJobForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.QUEST_JOB_FOR_USER__USER_ID,
					userId);
			String eqDelim = getQueryConstructionUtil().getAnd();

			Map<String, Collection<?>> inConditions = null;
			if (null != questIds && !questIds.isEmpty()) {
				inConditions = new HashMap<String, Collection<?>>();
				inConditions.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID,
						questIds);
			}
			String inDelim = getQueryConstructionUtil().getAnd();

			String overallDelim = getQueryConstructionUtil().getAnd();
			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityAndInConditions(columnsToSelected,
							TABLE_NAME, equalityConditions, eqDelim,
							inConditions, inDelim, overallDelim, values,
							preparedStatement);

			log.info("getUserQuestJobForUserId() query=" + query);

			List<QuestJobForUser> qjfuList = this.jdbcTemplate.query(query,
					new UserQuestJobForClientMapper());

			questIdToQjfuList = new HashMap<Integer, Collection<QuestJobForUser>>();
			//map by quest id to QuestJobForUser
			for (QuestJobForUser qjfu : qjfuList) {
				int questId = qjfu.getQuestId();

				if (!questIdToQjfuList.containsKey(questId)) {
					questIdToQjfuList.put(questId,
							new ArrayList<QuestJobForUser>());
				}

				Collection<QuestJobForUser> groupedQjfuList = questIdToQjfuList
						.get(questId);
				groupedQjfuList.add(qjfu);
			}

		} catch (Exception e) {
			log.error("getQuestIdToQuestJobsForUserId() could not retrieve"
					+ " UserQuestJob for userId=" + userId, e);
			questIdToQjfuList = new HashMap<Integer, Collection<QuestJobForUser>>();
		}

		return questIdToQjfuList;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(
			QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserQuestJobForClientMapper implements
			RowMapper<QuestJobForUser> {

		private static List<String> columnsSelected;

		@Override
		public QuestJobForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			QuestJobForUser qjfu = new QuestJobForUser();
			//			qjfu.setUserId(rs.getInt(DBConstants.QUEST_JOB_FOR_USER__USER_ID));
			qjfu.setQuestId(rs.getInt(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID));
			qjfu.setQuestJobId(rs
					.getInt(DBConstants.QUEST_JOB_FOR_USER__QUEST_JOB_ID));
			qjfu.setComplete(rs
					.getBoolean(DBConstants.QUEST_JOB_FOR_USER__IS_COMPLETE));
			qjfu.setProgress(rs
					.getInt(DBConstants.QUEST_JOB_FOR_USER__PROGRESS));
			return qjfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				//				columnsSelected.add(DBConstants.QUEST_JOB_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID);
				columnsSelected
						.add(DBConstants.QUEST_JOB_FOR_USER__QUEST_JOB_ID);
				columnsSelected
						.add(DBConstants.QUEST_JOB_FOR_USER__IS_COMPLETE);
				columnsSelected.add(DBConstants.QUEST_JOB_FOR_USER__PROGRESS);
			}
			return columnsSelected;
		}
	}
}
