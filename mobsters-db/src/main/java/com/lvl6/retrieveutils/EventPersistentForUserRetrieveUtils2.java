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

import com.lvl6.info.EventPersistentForUser;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class EventPersistentForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(EventPersistentForUserRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_EVENT_PERSISTENT_FOR_USER;
	private static final UserEventPersistentForClientMapper rowMapper = new UserEventPersistentForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<EventPersistentForUser> getUserPersistentEventForUserId(
			String userId) {
		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID);

		List<EventPersistentForUser> userPersistentEvents = null;//new ArrayList<EventPersistentForUser>();
		try {
			userPersistentEvents = this.jdbcTemplate.query(query, values,
					rowMapper);

		} catch (Exception e) {
			log.error("event persistent for user retrieve db error.", e);
			userPersistentEvents = new ArrayList<EventPersistentForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userPersistentEvents;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserEventPersistentForClientMapper implements
			RowMapper<EventPersistentForUser> {

		private static List<String> columnsSelected;

		@Override
		public EventPersistentForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			EventPersistentForUser epfu = new EventPersistentForUser();
			epfu.setUserId(rs
					.getString(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID));
			epfu.setEventPersistentId(rs
					.getInt(DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID));
			Timestamp time = rs
					.getTimestamp(DBConstants.EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY);
			if (null != time) {
				epfu.setTimeOfEntry(new Date(time.getTime()));
			}
			return epfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID);
				columnsSelected
						.add(DBConstants.EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY);
			}
			return columnsSelected;
		}
	}

}
