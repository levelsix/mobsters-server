package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.PvpBattleHistory;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class PvpBattleHistoryRetrieveUtil2 {
	private static Logger log = LoggerFactory.getLogger(PvpBattleHistoryRetrieveUtil2.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_PVP_BATTLE_HISTORY; 
	private static final PvpBattleHistoryForClientMapper rowMapper = new PvpBattleHistoryForClientMapper();
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
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//Date twenty4ago = new DateTime().minusDays(1).toDate();
	protected String formatDateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String formatted = format.format(date);
		return formatted;
	}
	
	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class PvpBattleHistoryForClientMapper implements RowMapper<PvpBattleHistory> {
		
		private static List<String> columnsSelected;

		public PvpBattleHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			PvpBattleHistory history = new PvpBattleHistory();
			history.setAttackerId(rs.getString(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID));
			history.setDefenderId(rs.getString(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID));
			Timestamp time = rs.getTimestamp(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
			history.setBattleEndTime(time);
//			time = rs.getTimestamp(DBConstants.PVP_BATTLE_HISTORY__BATTLE_START_TIME);
//			history.setBattleStartTime(time);
//			history.setDefenderEloChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ELO_CHANGE));
			
			history.setAttackerPrevLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE));
			history.setAttackerCurLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE));
			history.setDefenderPrevLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE));
			history.setDefenderCurLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE));

			history.setAttackerPrevRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK));
			history.setAttackerCurRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK));
			history.setDefenderPrevRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK));
			history.setDefenderCurRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK));
            
            history.setDefenderCashChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE));
            history.setDefenderOilChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE));
            history.setAttackerCashChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CASH_CHANGE));
            history.setAttackerOilChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_OIL_CHANGE));
			history.setAttackerWon(rs.getBoolean(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_WON));
			history.setExactedRevenge(rs.getBoolean(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE));
//			history.setClanAvenged(rs.getBoolean(DBConstants.PVP_BATTLE_HISTORY__CLAN_AVENGED));
			return history;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
				
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE);
				
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK);
				
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_WON);
				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE);
			}
			return columnsSelected;
		}
		
	} 

	//CONTROLLER LOGIC******************************************************************
	public Set<String> getAttackerIdsFromHistory(List<PvpBattleHistory> historyList) {
		Set<String> attackerIdList = new HashSet<String>();
		
		if (null == historyList)
		{
			return attackerIdList;
		}
		
		for (PvpBattleHistory history : historyList) {
			String attackerId = history.getAttackerId();
			attackerIdList.add(attackerId);
		}
		return attackerIdList;
	}
	
	public Set<String> getUserIdsFromHistory(List<PvpBattleHistory> historyList) {
		Set<String> userIdSet = new HashSet<String>();
		
		if (null == historyList)
		{
			return userIdSet;
		}
		
		for (PvpBattleHistory history : historyList) {
			String attackerId = history.getAttackerId();
			userIdSet.add(attackerId);
			
			String defenderId = history.getDefenderId();
			//defender can be a fake player
			if (null != defenderId && !defenderId.isEmpty())
			{
				userIdSet.add(defenderId);
			}
		}
		return userIdSet;
	}
	
	//RETRIEVE QUERIES*********************************************************************
//	public List<PvpBattleHistory> getRecentNBattlesForDefenderId(String defenderId, int n) {
//		List<PvpBattleHistory> recentNBattles = null;
//		try {
//			List<String> columnsToSelect = PvpBattleHistoryForClientMapper.getColumnsSelected();
//			
//			Map<String, Object> equalityConditions = new HashMap<String, Object>();
//			equalityConditions.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID, defenderId);
//			equalityConditions.put(DBConstants.PVP_BATTLE_HISTORY__CANCELLED, false);
//			equalityConditions.put(DBConstants.PVP_BATTLE_HISTORY__DISPLAY_TO_USER, true);
//			String conditionDelimiter = getQueryConstructionUtil().getAnd();
//
//			//query db, "values" is not used 
//			//(its purpose is to hold the values that were supposed to be put
//			// into a prepared statement)
//			List<Object> values = new ArrayList<Object>();
//			boolean preparedStatement = true;
//
//			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
//					columnsToSelect, TABLE_NAME, equalityConditions, conditionDelimiter,
//					values, preparedStatement);
//
//			if (n >= 1) {
//				StringBuilder querySb = new StringBuilder();
//				querySb.append(query);
//				querySb.append(" order by ");
//				querySb.append(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
//				querySb.append(" desc limit ");
//				querySb.append(n);
//
//				query = querySb.toString(); 
//			}
//			log.info("query=" + query);
//
//			recentNBattles = this.jdbcTemplate.query(query, values.toArray(), new PvpBattleHistoryForClientMapper());
//		} catch (Exception e) {
//			log.error(String.format(
//				"error retrieving pvp_battle_history for defenderId=%s", defenderId), e);
//			recentNBattles = new ArrayList<PvpBattleHistory>();
//		}
//		return recentNBattles;
//	}
	
	public List<PvpBattleHistory> getRecentNBattlesForUserId(String userId, int n) {
		List<PvpBattleHistory> recentNBattles = null;
		
		Object[] values = { userId, userId, false};//, true };
		String query = String.format(
			"select * from %s where (%s in (?) or %s in (?)) and %s=?",
			TABLE_NAME,
			DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID,
			DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID,
			DBConstants.PVP_BATTLE_HISTORY__CANCELLED);//,
			//DBConstants.PVP_BATTLE_HISTORY__DISPLAY_TO_USER);
		
		if (n >= 1) {
			StringBuilder querySb = new StringBuilder();
			querySb.append(query);
			querySb.append(" order by ");
			querySb.append(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
			querySb.append(" desc limit ");
			querySb.append(n);

			query = querySb.toString(); 
		}
		log.info("query={}, values={}", query, values);
		try {
			recentNBattles = this.jdbcTemplate.query(query, values,
				rowMapper);
		} catch (Exception e) {
			log.error(String.format(
				"error retrieving pvp_battle_history for userId=%s", userId), e);
			recentNBattles = new ArrayList<PvpBattleHistory>();
		}
		return recentNBattles;
	}
	
	public PvpBattleHistory getPvpBattle(String attackerId,
		String defenderId, Date battleEndTime)
    {
		PvpBattleHistory battle = null;
		
		Object[] values = { attackerId, defenderId, battleEndTime, false };
		String query = String.format(
			"select * from %s where %s in (?) and %s in (?) and %s=? and %s=?",
			TABLE_NAME,
			DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID,
			DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID,
			DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME,
			DBConstants.PVP_BATTLE_HISTORY__CANCELLED);//,
			//DBConstants.PVP_BATTLE_HISTORY__DISPLAY_TO_USER);
		
		log.info("query={}, values={}", query, values);
		try {
			List<PvpBattleHistory> battles = this.jdbcTemplate.query(query, values,
				rowMapper);
			
			if (null != battles && !battles.isEmpty()) {
				battle = battles.get(0);
			}
		} catch (Exception e) {
			log.error(String.format(
				"error retrieving pvp_battle_history for attackerId=%s, defenderId=%s, battleEndTime=%s",
				new Object[] {attackerId, defenderId, battleEndTime}), e);
		}
		return battle;
	}
	
}
