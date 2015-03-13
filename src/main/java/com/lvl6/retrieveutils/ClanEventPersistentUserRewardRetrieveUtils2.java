package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;

@Component
@DependsOn("gameServer")
public class ClanEventPersistentUserRewardRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_USER_REWARD;
	private static final ClanEventPersistentUserRewardsForClientMapper rowMapper = new ClanEventPersistentUserRewardsForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<Date, List<ClanEventPersistentUserReward>> getCepUserRewardForPastNDaysForUserId(
			String userId, int nDays, Date curDate, TimeUtils timeUtils) {

		curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
		Date pastDate = timeUtils.createPstDate(curDate, -1 * nDays, 0, 0);
		Timestamp pastTime = new Timestamp(pastDate.getTime());

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME);
		querySb.append(">?;");

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(pastTime);

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, values));

		Map<Date, List<ClanEventPersistentUserReward>> timeToRaidStageReward = new HashMap<Date, List<ClanEventPersistentUserReward>>();

		log.info("getting ClanEventPersistentUserReward for userId=" + userId);
		try {
			List<ClanEventPersistentUserReward> cepurList = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);

			for (ClanEventPersistentUserReward cepur : cepurList) {
				Date crsStartTime = cepur.getCrsStartTime();
				if (!timeToRaidStageReward.containsKey(crsStartTime)) {
					timeToRaidStageReward.put(crsStartTime,
							new ArrayList<ClanEventPersistentUserReward>());
				}

				List<ClanEventPersistentUserReward> rewards = timeToRaidStageReward
						.get(crsStartTime);
				rewards.add(cepur);
			}

		} catch (Exception e) {
			log.error("clan event persistent user reward retrieve db error.", e);
			//    } finally {
			//    	DBConnection.get().close(rs, null, conn);
		}
		return timeToRaidStageReward;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanEventPersistentUserRewardsForClientMapper
			implements RowMapper<ClanEventPersistentUserReward> {

		private static List<String> columnsSelected;

		@Override
		public ClanEventPersistentUserReward mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ClanEventPersistentUserReward reward = new ClanEventPersistentUserReward();
			reward.setId(rs
					.getString(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__ID));
			reward.setUserId(rs
					.getString(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID));

			Timestamp time = rs
					.getTimestamp(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME);
			if (null != time) {
				reward.setCrsStartTime(new Date(time.getTime()));
			}
			reward.setCrsId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_ID));

			Timestamp time2 = rs
					.getTimestamp(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_END_TIME);
			if (null != time2) {
				reward.setCrsEndTime(new Date(time2.getTime()));
			}

			reward.setResourceType(rs
					.getString(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__RESOURCE_TYPE));
			reward.setStaticDataId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__STATIC_DATA_ID));
			reward.setQuantity(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__QUANTITY));
			reward.setClanEventPersistentId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CLAN_EVENT_PERSISTENT_ID));

			Timestamp time3 = rs
					.getTimestamp(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__TIME_REDEEMED);
			if (null != time3) {
				reward.setTimeRedeemed(new Date(time3.getTime()));
			}

			return reward;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_END_TIME);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__RESOURCE_TYPE);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__STATIC_DATA_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__QUANTITY);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CLAN_EVENT_PERSISTENT_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__TIME_REDEEMED);
			}
			return columnsSelected;
		}
	}

}
