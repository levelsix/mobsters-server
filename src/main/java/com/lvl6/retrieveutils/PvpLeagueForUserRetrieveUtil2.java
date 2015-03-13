package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component
public class PvpLeagueForUserRetrieveUtil2 {
	private static Logger log = LoggerFactory
			.getLogger(PvpLeagueForUserRetrieveUtil2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE_FOR_USER;
	private static final UserPvpLeagueForClientMapper rowMapper = new UserPvpLeagueForClientMapper();
	private static final BattlesWonMapper battlesWonMapper = new BattlesWonMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(
			QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public PvpLeagueForUser getUserPvpLeagueForId(String userId) {
		PvpLeagueForUser plfu = null;
		try {
			List<String> columnsToSelect = UserPvpLeagueForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID,
					userId);
			String conditionDelimiter = queryConstructionUtil.getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = queryConstructionUtil
					.selectRowsQueryEqualityConditions(columnsToSelect,
							TABLE_NAME, equalityConditions, conditionDelimiter,
							values, preparedStatement);

			log.info("query={}, values={}", query, values);

			//			List<PvpLeagueForUser> plfuList = this.jdbcTemplate.query(query, new UserPvpLeagueForClientMapper());
			//			if (null != plfuList && !plfuList.isEmpty()) {
			//				plfu = plfuList.get(0); //guaranteed to only be one, primary key is user id
			//			}
			// entry in this table for the user is created on start up, so should exist
			plfu = this.jdbcTemplate.queryForObject(query, values.toArray(),
					rowMapper);
		} catch (Exception e) {
			log.error(String.format(
					"could not retrieve user pvpLeague for userId=%s", userId),
					e);
		}

		return plfu;
	}

	public Map<String, PvpLeagueForUser> getUserPvpLeagueForUsers(
			List<String> userIdList) {
		Map<String, PvpLeagueForUser> plfuMap = new HashMap<String, PvpLeagueForUser>();
		try {
			List<String> columnsToSelect = UserPvpLeagueForClientMapper
					.getColumnsSelected();

			Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID,
					userIdList);
			String conditionDelimiter = queryConstructionUtil.getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = queryConstructionUtil.selectRowsQueryInConditions(
					columnsToSelect, TABLE_NAME, inConditions,
					conditionDelimiter, values, preparedStatement);

			log.info("query={}, values={}", query, values);

			List<PvpLeagueForUser> plfuList = this.jdbcTemplate.query(query,
					values.toArray(), rowMapper);

			for (PvpLeagueForUser plfu : plfuList) {
				String userId = plfu.getUserId();
				plfuMap.put(userId, plfu);
			}
		} catch (Exception e) {
			log.error(String.format(
					"could not retrieve user pvpLeague for userIds=%s",
					userIdList), e);
		}

		return plfuMap;
	}

	public int getPvpBattlesWonForUser(String userId) {
		int battlesWon = 0;
		try {
			List<String> columnsToSelect = BattlesWonMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID,
					userId);
			String conditionDelimiter = queryConstructionUtil.getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = queryConstructionUtil
					.selectRowsQueryEqualityConditions(columnsToSelect,
							TABLE_NAME, equalityConditions, conditionDelimiter,
							values, preparedStatement);

			log.info("query=" + query);

			battlesWon = this.jdbcTemplate.queryForObject(query,
					values.toArray(), battlesWonMapper);

		} catch (Exception e) {
			log.error(
					"could not retrieve user battlesWon for userId=" + userId,
					e);
		}
		return battlesWon;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserPvpLeagueForClientMapper implements
			RowMapper<PvpLeagueForUser> {

		private static List<String> columnsSelected;

		@Override
		public PvpLeagueForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PvpLeagueForUser ofu = new PvpLeagueForUser();
			ofu.setUserId(rs
					.getString(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID));
			ofu.setPvpLeagueId(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID));
			ofu.setRank(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__RANK));
			ofu.setElo(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ELO));

			Date inBattleShieldEndTime = null;
			try {
				Timestamp ts = rs
						.getTimestamp(DBConstants.PVP_LEAGUE_FOR_USER__BATTLE_END_TIME);
				inBattleShieldEndTime = new Date(ts.getTime());
				ofu.setInBattleShieldEndTime(inBattleShieldEndTime);
			} catch (Exception e) {
				log.error("incorrect inBattleShieldEndTime", e);
			}
			Date shieldEndTime = null;
			try {
				Timestamp ts = rs
						.getTimestamp(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME);
				shieldEndTime = new Date(ts.getTime());
				ofu.setShieldEndTime(shieldEndTime);
			} catch (Exception e) {
				log.error("incorrect shieldEndTime", e);
			}

			ofu.setAttacksWon(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON));
			ofu.setDefensesWon(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON));
			ofu.setAttacksLost(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_LOST));
			ofu.setDefensesLost(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_LOST));
			ofu.setMonsterDmgMultiplier(rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__MONSTER_DMG_MULTIPLIER));
			return ofu;
		}

		//whatever columns are used in map row should appear here as well
		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__RANK);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__ELO);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__BATTLE_END_TIME);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_LOST);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_LOST);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__MONSTER_DMG_MULTIPLIER);
			}
			return columnsSelected;
		}
	}

	private static final class BattlesWonMapper implements RowMapper<Integer> {

		private static List<String> columnsSelected;

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			int attacksWon = rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON);
			int defensesWon = rs
					.getInt(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON);

			return attacksWon + defensesWon;
		}

		//whatever columns are used in map row should appear here as well
		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON);
				columnsSelected
						.add(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON);
			}
			return columnsSelected;
		}
	}
}
