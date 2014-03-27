package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
public class PvpLeagueForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(PvpLeagueForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE_FOR_USER; 
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
	private static final class UserPvpLeagueForClientMapper implements RowMapper<PvpLeagueForUser> {

		private static List<String> columnsSelected;

		public PvpLeagueForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			PvpLeagueForUser ofu = new PvpLeagueForUser();
			ofu.setUserId(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID));
			ofu.setPvpLeagueId(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID));
			ofu.setRank(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__RANK));
			ofu.setElo(rs.getInt(DBConstants.PVP_LEAGUE_FOR_USER__ELO));
			return ofu;
		}        
		
		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__RANK);
				columnsSelected.add(DBConstants.PVP_LEAGUE_FOR_USER__ELO);
			}
			return columnsSelected;
		}
	} 

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
	public PvpLeagueForUser getUserPvpLeagueForId(int userId) {
		PvpLeagueForUser plfu = null;
		try {
			List<String> columnsToSelect = UserPvpLeagueForClientMapper.getColumnsSelected();
			
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

			log.info("query=" + query);

			plfu = this.jdbcTemplate.queryForObject(query, new UserPvpLeagueForClientMapper());
		} catch (Exception e) {
			log.error("could not retrieve user pvpLeague for userId=" + userId, e);
		}
		
		return plfu;
	}
	
	public Map<Integer, PvpLeagueForUser> getUserPvpLeagueForUsers(List<Integer> userIdList) {
		Map<Integer, PvpLeagueForUser> plfuMap = new HashMap<Integer, PvpLeagueForUser>();
		try {
			List<String> columnsToSelect = UserPvpLeagueForClientMapper.getColumnsSelected();
			
			Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userIdList);
			String conditionDelimiter = getQueryConstructionUtil().getAnd();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryInConditions(
					columnsToSelect, TABLE_NAME, inConditions, conditionDelimiter, values,
					preparedStatement);

			log.info("query=" + query);

			List<PvpLeagueForUser> plfuList = this.jdbcTemplate
					.query(query, new UserPvpLeagueForClientMapper());
			
			for (PvpLeagueForUser plfu : plfuList) {
				int userId = plfu.getUserId();
				plfuMap.put(userId, plfu);
			}
		} catch (Exception e) {
			log.error("could not retrieve user pvpLeague for userIds=" + userIdList, e);
		}
		
		return plfuMap;
	}
	
}
