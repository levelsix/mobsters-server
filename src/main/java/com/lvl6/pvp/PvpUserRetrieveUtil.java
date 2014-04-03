package com.lvl6.pvp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component
public class PvpUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE_FOR_USER; 
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;


	//search entire pvp league for user table for users who do not have active shields,
	//also, only select certain columns
	//Disregard Above, just SELECT EVERYTHING from pvp_league_for_user table
	protected Collection<PvpUser> getPvpValidUsers() {
//		Timestamp now = new Timestamp((new Date()).getTime());

		try {
			List<String> columnsToSelect = PvpUserMapper.getColumnsSelected();
			
			Map<String, Object> inConditions = null;
			
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					columnsToSelect, TABLE_NAME, inConditions, conditionDelimiter, values,
					preparedStatement);

			log.info("query=" + query);

			List<PvpUser> puList = this.jdbcTemplate.query(
					query, new PvpUserMapper());
			
			return puList;
		} catch (Exception e) {
			log.error("could not retrieve user pvpLeague", e);
		}
		return new ArrayList<PvpUser>();
	}

	/**
	 * Get pvp users from the db. This is used if hazelcast is not used.
	 * @param minElo (exclusive) lower bound of elo range.
	 * @param maxElo (exclusive) upper bound of elo range.
	 * @param shieldEndTime Limits pvp users with actual and in battle
	 * 		shield end times before this value.
	 * @param limit Limits amount of rows returned
	 * @param excludeUserIds When fetching rows, ignores rows with these ids.
	 * @return
	 * 		Returns pvp users whose elo is in the range (minElo, maxElo)
	 * 		shieldEndTime < @param shieldEndTime and
	 * 		inBattleShieldEndTime < @param shieldEndTime and
	 * 		userId not in @param excludeUserIds
	 */
	protected Set<PvpUser> retrievePvpUsers(int minElo, int maxElo,
			Date shieldEndTime, int limit, Collection<String> excludeUserIds) {
		try {
			Timestamp endTime = new Timestamp(shieldEndTime.getTime());
			List<String> columnsToSelect = PvpUserMapper.getColumnsSelected();

			Map<String, Object> equalityConditions = null;
			String equalityCondDelim = getQueryConstructionUtil().getAnd();

			Map<String, Object> lessThanConditions = new HashMap<String, Object>();
			lessThanConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__ELO, maxElo);
			lessThanConditions.put(
					DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME, endTime);
			lessThanConditions.put(
					DBConstants.PVP_LEAGUE_FOR_USER__IN_BATTLE_SHIELD_END_TIME, endTime);
			String lessThanCondDelim = getQueryConstructionUtil().getAnd();

			Map<String, Object> greaterThanConditions = new HashMap<String, Object>();
			greaterThanConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__ELO, minElo);
			String greaterThanCondDelim = getQueryConstructionUtil().getAnd();

			String delimAcrossConditions = getQueryConstructionUtil().getAnd();
			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;
			String query = getQueryConstructionUtil().selectRowsQueryComparisonConditions(
					columnsToSelect, TABLE_NAME, equalityConditions, equalityCondDelim,
					lessThanConditions, lessThanCondDelim, greaterThanConditions,
					greaterThanCondDelim, delimAcrossConditions, values,
					preparedStatement, 0);
			
			StringBuilder sb = new StringBuilder();
			sb.append(query);
			if (null != excludeUserIds && !excludeUserIds.isEmpty()) {
				String notInConditionStr = getQueryConstructionUtil()
						.createColNotInValuesString(
								DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, excludeUserIds);   
				sb.append(" ");
				sb.append(notInConditionStr);
			}
			sb.append(" LIMIT ");
			sb.append(limit);
			query = sb.toString();
			
			
			log.info("retrievePvpUsers query=" + query);

			List<PvpUser> puList = this.jdbcTemplate.query(
					query, new PvpUserMapper());

			return new HashSet<PvpUser>(puList);
		} catch (Exception e) {
			log.error("could not retrieve user pvpLeague", e);
		}
		return new HashSet<PvpUser>();
	}
	
	//copy pasted from PvpLeagueForUserRetrieveUtil
	protected PvpUser getUserPvpLeagueForId(String userId) {
		PvpUser pu = null;
		try {
			List<String> columnsToSelect = PvpUserMapper.getColumnsSelected();
			
			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userId);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					columnsToSelect, TABLE_NAME, equalityConditions, conditionDelimiter,
					values, preparedStatement);

			log.info("getUserPvpLeagueForId query=" + query);

//			List<PvpLeagueForUser> plfuList = this.jdbcTemplate.query(query, new UserPvpLeagueForClientMapper());
//			if (null != plfuList && !plfuList.isEmpty()) {
//				plfu = plfuList.get(0); //guaranteed to only be one, primary key is user id
//			}
			// entry in this table for the user is created on start up, so should exist
			pu = this.jdbcTemplate.queryForObject(query, new PvpUserMapper());
		} catch (Exception e) {
			log.error("could not retrieve user pvpUser for userId=" + userId, e);
		}
		
		return pu;
	}

	private static final class PvpUserMapper implements RowMapper<PvpUser> {

		private static List<String> columnsSelected;

		public PvpUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			PvpUser pu = new PvpUser();
			int userId = rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
			String userIdStr = Integer.toString(userId);
			pu.setUserId(userIdStr);
			pu.setPvpLeagueId(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID));
			pu.setRank(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__RANK));
			pu.setElo(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ELO));
			
			Date shieldEndTime = null;
			try {
				Timestamp ts = rs.getTimestamp(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME);
				shieldEndTime = new Date(ts.getTime());
				pu.setShieldEndTime(shieldEndTime);
			} catch (Exception e) {
				log.error("incorrect shieldEndTime", e);
			}
			Date inBattleShieldEndTime = null;
			try {
				Timestamp ts = rs.getTimestamp(DBConstants.PVP_LEAGUE_FOR_USER__IN_BATTLE_SHIELD_END_TIME);
				inBattleShieldEndTime = new Date(ts.getTime());
				pu.setInBattleEndTime(inBattleShieldEndTime);
			} catch (Exception e) {
				log.error("incorrect shieldEndTime", e);
			}
			pu.setAttacksWon(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON));    
			pu.setDefensesWon(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON));  
			pu.setAttacksLost(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_LOST));  
			pu.setDefensesLost(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_LOST));
			
			return pu;
		}        
		
		//whatever columns are used in map row should appear here as well
		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__RANK);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__ELO);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__IN_BATTLE_SHIELD_END_TIME);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_LOST);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_LOST);
			}
			return columnsSelected;
		}
	}
	

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
}
