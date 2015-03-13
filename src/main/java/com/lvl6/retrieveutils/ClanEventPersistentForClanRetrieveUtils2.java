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

import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class ClanEventPersistentForClanRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
	private static final ClanEventPersistentForClanForClientMapper rowMapper = new ClanEventPersistentForClanForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public ClanEventPersistentForClan getPersistentEventForClanId(String clanId) {

		Object[] values = { clanId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);

		ClanEventPersistentForClan clanPersistentEvent = null;
		try {
			List<ClanEventPersistentForClan> events = this.jdbcTemplate.query(
					query, values, rowMapper);

			if (null != events && !events.isEmpty()) {
				clanPersistentEvent = events.get(0);
			}

		} catch (Exception e) {
			log.error("clan event persistent for clan retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return clanPersistentEvent;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanEventPersistentForClanForClientMapper
			implements RowMapper<ClanEventPersistentForClan> {

		private static List<String> columnsSelected;

		@Override
		public ClanEventPersistentForClan mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ClanEventPersistentForClan cepfc = new ClanEventPersistentForClan();
			cepfc.setClanId(rs
					.getString(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID));
			cepfc.setClanEventPersistentId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_EVENT_PERSISTENT_ID));
			cepfc.setCrId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_EVENT_PERSISTENT_ID));
			cepfc.setCrsId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID));

			Timestamp time = rs
					.getTimestamp(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME);
			if (null != time) {
				cepfc.setStageStartTime(new Date(time.getTime()));
			}
			cepfc.setCrsmId(rs
					.getInt(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID));

			Timestamp time2 = rs
					.getTimestamp(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME);
			cepfc.setStageMonsterStartTime(new Date(time2.getTime()));
			return cepfc;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_EVENT_PERSISTENT_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CR_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID);
				columnsSelected
						.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME);
			}
			return columnsSelected;
		}
	}
}
