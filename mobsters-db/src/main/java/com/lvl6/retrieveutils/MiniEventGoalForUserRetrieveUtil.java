package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventGoalForUserPojo;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class MiniEventGoalForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(MiniEventGoalForUserRetrieveUtil.class);

	private final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_GOAL_FOR_USER;
	private static final UserMiniEventForClientMapper rowMapper = new UserMiniEventForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	public List<MiniEventGoalForUserPojo> getUserMiniEventsForUser(String userId) {
		log.debug("retrieving user MiniEvents for userId {}", userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);

		List<MiniEventGoalForUserPojo> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventGoalForUserPojo retrieve db error. userId=%s",
							userId),
					e);
			userMiniEvents = new ArrayList<MiniEventGoalForUserPojo>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userMiniEvents;
	}

	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<Integer, MiniEventGoalForUserPojo> getMiniEventIdsToUserMiniEventGoalForUserPojo(
			String userId) {
		log.debug("retrieving map of MiniEventId to MiniEventGoalForUserPojo for userId {}", userId);

		Map<Integer, MiniEventGoalForUserPojo> miniEventIdToMiniEventGoalForUserPojo = new HashMap<Integer, MiniEventGoalForUserPojo>();
		try {

			List<MiniEventGoalForUserPojo> bifuList = getUserMiniEventsForUser(userId);

			for (MiniEventGoalForUserPojo bifu : bifuList) {
				int miniEventId = bifu.getMiniEventId();
				miniEventIdToMiniEventGoalForUserPojo.put(miniEventId, bifu);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventGoalForUserPojo retrieve db error. userId=%s",
							userId),
					e);
		}

		return miniEventIdToMiniEventGoalForUserPojo;
	}*/

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public Collection<MiniEventGoalForUserPojo> getUserMiniEventGoals(String userId,
			int miniEventTimetableId)
	{
		log.debug(
				"retrieving MiniEventGoalForUserPojo with userId={}, miniEventTimeTableId={}",
				userId, miniEventTimetableId);

		Object[] values = { userId, miniEventTimetableId };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID,
				DBConstants.MINI_EVENT_GOAL_FOR_USER__MINI_EVENT_TIMETABLE_ID);

		Collection<MiniEventGoalForUserPojo> megfuList;
		try {
			megfuList = this.jdbcTemplate.query(query,
					values, rowMapper);

		} catch (Exception e) {
			megfuList = new ArrayList<MiniEventGoalForUserPojo>();
			log.error(
					String.format(
							"%s userId=%s, miniEventTimetableId=%s",
							"MiniEventGoalForUserPojo retrieve db error.",
							userId, miniEventTimetableId),
					e);
		}

		return megfuList;
	}

	/*
	public List<MiniEventGoalForUserPojo> getSpecificOrAllUserMiniEventsForUser(
			String userId, List<Integer> userMiniEventMiniEventIds) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(userId);

		//if user didn't give any userStructIds then get all the user's structs
		//else get the specific ids
		if (userMiniEventMiniEventIds != null
				&& !userMiniEventMiniEventIds.isEmpty()) {
			log.debug("retrieving userMiniEvents with miniEventIds {}",
					userMiniEventMiniEventIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.MINI_EVENT_GOAL_FOR_USER__MINI_EVENT_ID);
			querySb.append(" IN (");

			int amount = userMiniEventMiniEventIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userMiniEventMiniEventIds);
		}

		String query = querySb.toString();
		log.info("query={}, values={}", query, values);

		List<MiniEventGoalForUserPojo> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userMiniEvents = new ArrayList<MiniEventGoalForUserPojo>();

		}

		return userMiniEvents;
	}*/

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMiniEventForClientMapper implements
	RowMapper<MiniEventGoalForUserPojo> {

		private static List<String> columnsSelected;

		@Override
		public MiniEventGoalForUserPojo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MiniEventGoalForUserPojo megfu = new MiniEventGoalForUserPojo();
			megfu.setUserId(rs
					.getString(DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID));
			megfu.setMiniEventGoalId(rs
					.getInt(DBConstants.MINI_EVENT_GOAL_FOR_USER__MINI_EVENT_GOAL_ID));
			megfu.setProgress(
					rs.getInt(DBConstants.MINI_EVENT_GOAL_FOR_USER__PROGRESS));

			return megfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.MINI_EVENT_GOAL_FOR_USER__MINI_EVENT_GOAL_ID);
				columnsSelected.add(DBConstants.MINI_EVENT_GOAL_FOR_USER__PROGRESS);
			}
			return columnsSelected;
		}
	}

}
