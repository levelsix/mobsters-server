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

import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class MiniEventGoalForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_GOAL_FOR_USER;
	private static final UserMiniEventForClientMapper rowMapper = new UserMiniEventForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	public List<MiniEventGoalForUser> getUserMiniEventsForUser(String userId) {
		log.debug("retrieving user MiniEvents for userId {}", userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);

		List<MiniEventGoalForUser> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventGoalForUser retrieve db error. userId=%s",
							userId),
					e);
			userMiniEvents = new ArrayList<MiniEventGoalForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userMiniEvents;
	}

	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<Integer, MiniEventGoalForUser> getMiniEventIdsToUserMiniEventGoalForUser(
			String userId) {
		log.debug("retrieving map of MiniEventId to MiniEventGoalForUser for userId {}", userId);

		Map<Integer, MiniEventGoalForUser> miniEventIdToMiniEventGoalForUser = new HashMap<Integer, MiniEventGoalForUser>();
		try {

			List<MiniEventGoalForUser> bifuList = getUserMiniEventsForUser(userId);

			for (MiniEventGoalForUser bifu : bifuList) {
				int miniEventId = bifu.getMiniEventId();
				miniEventIdToMiniEventGoalForUser.put(miniEventId, bifu);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventGoalForUser retrieve db error. userId=%s",
							userId),
					e);
		}

		return miniEventIdToMiniEventGoalForUser;
	}*/

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public Collection<MiniEventGoalForUser> getUserMiniEventGoals(String userId)//,
			//int miniEventId)
	{
		log.debug(
				"retrieving MiniEventGoalForUser with userId={}",
				userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?",
				TABLE_NAME, DBConstants.MINI_EVENT_GOAL_FOR_USER__USER_ID);

		Collection<MiniEventGoalForUser> megfuList;
		try {
			megfuList = this.jdbcTemplate.query(query,
					values, rowMapper);

		} catch (Exception e) {
			megfuList = new ArrayList<MiniEventGoalForUser>();
			log.error(
					String.format(
							"MiniEventGoalForUser retrieve db error. userId=%s",
							userId), e);
		}

		return megfuList;
	}

	/*
	public List<MiniEventGoalForUser> getSpecificOrAllUserMiniEventsForUser(
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

		List<MiniEventGoalForUser> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userMiniEvents = new ArrayList<MiniEventGoalForUser>();

		}

		return userMiniEvents;
	}*/

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMiniEventForClientMapper implements
	RowMapper<MiniEventGoalForUser> {

		private static List<String> columnsSelected;

		@Override
		public MiniEventGoalForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MiniEventGoalForUser megfu = new MiniEventGoalForUser();
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
