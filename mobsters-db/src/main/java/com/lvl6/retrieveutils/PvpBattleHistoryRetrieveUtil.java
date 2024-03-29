//package com.lvl6.retrieveutils;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.PvpBattleHistory;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.retrieveutils.util.QueryConstructionUtil;
//
//@Component 
//public class PvpBattleHistoryRetrieveUtil {
//	private static Logger log = LoggerFactory.getLogger(PvpBattleHistoryRetrieveUtil.class);
//	
//	private static final String TABLE_NAME = DBConstants.TABLE_PVP_BATTLE_HISTORY; 
//	private JdbcTemplate jdbcTemplate;
//
//	@Resource
//	public void setDataSource(DataSource dataSource) {
//		log.info("Setting datasource and creating jdbcTemplate");
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//	
//	@Autowired
//	protected QueryConstructionUtil queryConstructionUtil;
//
//	public QueryConstructionUtil getQueryConstructionUtil() {
//		return queryConstructionUtil;
//	}
//	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
//		this.queryConstructionUtil = queryConstructionUtil;
//	}
//
//	//Date twenty4ago = new DateTime().minusDays(1).toDate();
//	protected String formatDateToString(Date date) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//		String formatted = format.format(date);
//		return formatted;
//	}
//	
//	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
//	//mimics PvpHistoryProto in Battle.proto
//	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
//	//says so (search for "private static final")
//	private static final class PvpBattleHistoryForClientMapper implements RowMapper<PvpBattleHistory> {
//		
//		private static List<String> columnsSelected;
//
//		public PvpBattleHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
//			PvpBattleHistory history = new PvpBattleHistory();
//			history.setAttackerId(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID));
//			history.setDefenderId(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID));
//			Timestamp time = rs.getTimestamp(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
//			history.setBattleEndTime(time);
////			time = rs.getTimestamp(DBConstants.PVP_BATTLE_HISTORY__BATTLE_START_TIME);
////			history.setBattleStartTime(time);
////			history.setDefenderEloChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ELO_CHANGE));
//			
//			history.setAttackerPrevLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE));
//			history.setAttackerCurLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE));
//			history.setDefenderPrevLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE));
//			history.setDefenderCurLeague(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE));
//
//			history.setAttackerPrevRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK));
//			history.setAttackerCurRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK));
//			history.setDefenderPrevRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK));
//			history.setDefenderCurRank(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK));
//			
//			history.setDefenderCashChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE));
//			history.setDefenderOilChange(rs.getInt(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE));
//			history.setAttackerWon(rs.getBoolean(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_WON));
//			history.setExactedRevenge(rs.getBoolean(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE));
//			return history;
//		}
//
//		public static List<String> getColumnsSelected() {
//			if (null == columnsSelected) {
//				columnsSelected = new ArrayList<String>();
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME);
//				
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE);
//				
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK);
//				
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_WON);
//				columnsSelected.add(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE);
//			}
//			return columnsSelected;
//		}
//		
//	} 
//
//	//CONTROLLER LOGIC******************************************************************
//	public Set<Integer> getAttackerIdsFromHistory(List<PvpBattleHistory> historyList) {
//		Set<Integer> attackerIdList = new HashSet<Integer>();
//		
//		for (PvpBattleHistory history : historyList) {
//			int attackerId = history.getAttackerId();
//			attackerIdList.add(attackerId);
//		}
//		return attackerIdList;
//	}
//	
//	//RETRIEVE QUERIES*********************************************************************
//	public List<PvpBattleHistory> getRecentNBattlesForDefenderId(int defenderId, int n) {
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
//			List<Object> values = null;
//			boolean preparedStatement = false;
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
//			recentNBattles = this.jdbcTemplate.query(query, new PvpBattleHistoryForClientMapper());
//		} catch (Exception e) {
//			log.error("error retrieving pvp_battle_history for defenderId=" + defenderId, e);
//			recentNBattles = new ArrayList<PvpBattleHistory>();
//		}
//		return recentNBattles;
//	}
//	
//}
