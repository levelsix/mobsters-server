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

import com.lvl6.info.CepfuRaidStageHistory;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.TimeUtils;

@Component
@DependsOn("gameServer")
public class CepfuRaidStageHistoryRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(CepfuRaidStageHistoryRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_CEPFU_RAID_STAGE_HISTORY;
	private static final CepfuRaidStageHistoryForClientMapper rowMapper = new CepfuRaidStageHistoryForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<Date, CepfuRaidStageHistory> getRaidStageHistoryForPastNDaysForUserId(
			String userId, int nDays, Date curDate, TimeUtils timeUtils) {

		curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
		Date pastDate = timeUtils.createPstDate(curDate, -1 * nDays, 0, 0);
		Timestamp pastTime = new Timestamp(pastDate.getTime());

		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CEPFU_RAID_STAGE_HISTORY__USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME);
		querySb.append(">?;");

		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(pastTime);

		String query = querySb.toString();
		log.info(String.format("query=%s, values=%s", query, values));

		Map<Date, CepfuRaidStageHistory> timeToRaidStageHistory = new HashMap<Date, CepfuRaidStageHistory>();
		log.info("getting CepfuRaidStageHistory for userId=" + userId);
		try {
			List<CepfuRaidStageHistory> stageHistory = this.jdbcTemplate.query(
					query, values.toArray(), rowMapper);

			for (CepfuRaidStageHistory cepfursh : stageHistory) {
				Date aDate = cepfursh.getCrsStartTime();

				timeToRaidStageHistory.put(aDate, cepfursh);
			}

		} catch (Exception e) {
			log.error(
					"clan event persistent for user raid stage history retrieve db error.",
					e);

			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return timeToRaidStageHistory;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class CepfuRaidStageHistoryForClientMapper implements
			RowMapper<CepfuRaidStageHistory> {

		private static List<String> columnsSelected;

		@Override
		public CepfuRaidStageHistory mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CepfuRaidStageHistory stageHistory = new CepfuRaidStageHistory();
			stageHistory.setUserId(rs
					.getString(DBConstants.CEPFU_RAID_STAGE_HISTORY__USER_ID));
			Timestamp time = rs
					.getTimestamp(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME);
			if (null != time) {
				stageHistory.setCrsStartTime(new Date(time.getTime()));
			}

			stageHistory.setClanId(rs
					.getString(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_ID));
			stageHistory
					.setClanEventPersistentId(rs
							.getInt(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_EVENT_PERSISTENT_ID));
			stageHistory.setCrId(rs
					.getInt(DBConstants.CEPFU_RAID_STAGE_HISTORY__CR_ID));
			stageHistory.setCrsId(rs
					.getInt(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_ID));
			stageHistory
					.setCrsDmgDone(rs
							.getInt(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_DMG_DONE));
			stageHistory
					.setStageHealth(rs
							.getInt(DBConstants.CEPFU_RAID_STAGE_HISTORY__STAGE_HEALTH));

			Timestamp time2 = rs
					.getTimestamp(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_END_TIME);
			if (null != time2) {
				stageHistory.setCrsEndTime(new Date(time2.getTime()));
			}
			return stageHistory;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__USER_ID);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_ID);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CLAN_EVENT_PERSISTENT_ID);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CR_ID);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_ID);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_DMG_DONE);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__STAGE_HEALTH);
				columnsSelected
						.add(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_END_TIME);
			}
			return columnsSelected;
		}
	}

}
