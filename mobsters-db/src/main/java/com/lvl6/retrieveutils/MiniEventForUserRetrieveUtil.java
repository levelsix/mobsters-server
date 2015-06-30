package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventForUserPojo;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class MiniEventForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(MiniEventForUserRetrieveUtil.class);

	private final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_FOR_USER;
	private static final UserMiniEventForClientMapper rowMapper = new UserMiniEventForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	public List<MiniEventForUser> getUserMiniEventsForUser(String userId) {
		log.debug("retrieving user MiniEvents for userId {}", userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.MINI_EVENT_FOR_USER__USER_ID);

		List<MiniEventForUser> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventForUserPojo retrieve db error. userId=%s",
							userId),
					e);
			userMiniEvents = new ArrayList<MiniEventForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userMiniEvents;
	}

	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<Integer, MiniEventForUser> getMiniEventIdsToUserMiniEventForUser(
			String userId) {
		log.debug("retrieving map of MiniEventId to MiniEventForUserPojo for userId {}", userId);

		Map<Integer, MiniEventForUser> miniEventIdToMiniEventForUserPojo = new HashMap<Integer, MiniEventForUser>();
		try {

			List<MiniEventForUser> bifuList = getUserMiniEventsForUser(userId);

			for (MiniEventForUserPojo bifu : bifuList) {
				int miniEventId = bifu.getMiniEventId();
				miniEventIdToMiniEventForUser.put(miniEventId, bifu);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"MiniEventForUserPojo retrieve db error. userId=%s",
							userId),
					e);
		}

		return miniEventIdToMiniEventForUser;
	}*/

	public MiniEventForUserPojo getMostRecentUserMiniEvent(String userId)
	{
		log.debug(
				"retrieving MiniEventForUserPojo with userId={}",
				userId);

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=? order by %s desc limit 1",
				TABLE_NAME, DBConstants.MINI_EVENT_FOR_USER__USER_ID,
				DBConstants.MINI_EVENT_FOR_USER__TIME_OF_ENTRY);

		MiniEventForUserPojo userMiniEvent = null;
		try {
			List<MiniEventForUserPojo> mefuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != mefuList && !mefuList.isEmpty()) {
				userMiniEvent = mefuList.get(0);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"%s userId=%s, miniEventTimetableId=%s",
							"most recent MiniEventForUserPojo retrieve db error.",
							userId),
					e);
		}

		return userMiniEvent;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public MiniEventForUserPojo getSpecificUserMiniEvent(String userId,
			int miniEventTimetableId)
	{
		log.debug(
				"retrieving MiniEventForUserPojo with userId={}",
				userId);

		Object[] values = { userId, miniEventTimetableId };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.MINI_EVENT_FOR_USER__USER_ID,
				DBConstants.MINI_EVENT_FOR_USER__MINI_EVENT_TIMETABLE_ID);

		MiniEventForUserPojo userMiniEvent = null;
		try {
			List<MiniEventForUserPojo> mefuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != mefuList && !mefuList.isEmpty()) {
				userMiniEvent = mefuList.get(0);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"%s userId=%s, miniEventTimetableId=%s",
							"MiniEventForUserPojo retrieve db error.",
							userId, miniEventTimetableId),
					e);
		}

		return userMiniEvent;
	}

	/*
	public List<MiniEventForUser> getSpecificOrAllUserMiniEventsForUser(
			String userId, List<Integer> userMiniEventMiniEventIds) {

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.MINI_EVENT_FOR_USER__USER_ID);
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
			querySb.append(DBConstants.MINI_EVENT_FOR_USER__MINI_EVENT_ID);
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

		List<MiniEventForUser> userMiniEvents = null;
		try {
			userMiniEvents = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			userMiniEvents = new ArrayList<MiniEventForUser>();

		}

		return userMiniEvents;
	}*/

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserMiniEventForClientMapper implements
	RowMapper<MiniEventForUserPojo> {

		private static Set<String> columnsSelected;

		@Override
		public MiniEventForUserPojo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MiniEventForUserPojo bifu = new MiniEventForUserPojo();
			bifu.setUserId(rs
					.getString(DBConstants.MINI_EVENT_FOR_USER__USER_ID));
			bifu.setMiniEventId(rs
					.getInt(DBConstants.MINI_EVENT_FOR_USER__MINI_EVENT_ID));
			bifu.setUserLvl(
					rs.getInt(DBConstants.MINI_EVENT_FOR_USER__USER_LVL));
			bifu.setTierOneRedeemed(
					rs.getBoolean(DBConstants.MINI_EVENT_FOR_USER__TIER_ONE_REDEEMED));
			bifu.setTierTwoRedeemed(
					rs.getBoolean(DBConstants.MINI_EVENT_FOR_USER__TIER_TWO_REDEEMED));
			bifu.setTierThreeRedeemed(
					rs.getBoolean(DBConstants.MINI_EVENT_FOR_USER__TIER_THREE_REDEEMED));


			return bifu;
		}

		public static Set<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new HashSet<String>();
				columnsSelected.add(DBConstants.MINI_EVENT_FOR_USER__USER_ID);
				columnsSelected
				.add(DBConstants.MINI_EVENT_FOR_USER__MINI_EVENT_ID);
				columnsSelected.add(DBConstants.MINI_EVENT_FOR_USER__USER_LVL);
				columnsSelected.add(DBConstants.MINI_EVENT_FOR_USER__TIER_ONE_REDEEMED);
				columnsSelected.add(DBConstants.MINI_EVENT_FOR_USER__TIER_TWO_REDEEMED);
				columnsSelected.add(DBConstants.MINI_EVENT_FOR_USER__TIER_THREE_REDEEMED);
			}
			return columnsSelected;
		}
	}

}
