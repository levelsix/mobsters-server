package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.lvl6.info.ClanAvengeUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ClanAvengeUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanAvengeUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_AVENGE_USER; 
	private static final ClanAvengeUserForClientMapper rowMapper = new ClanAvengeUserForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
//	public List<ClanAvengeUser> getClanAvengeUsersForIds(List<String> clanAvengeIds)
//	{
//		List<ClanAvengeUser> clanAvengeUsers = null;
//		try {
//			List<String> columnsToSelect = ClanAvengeUserForClientMapper
//					.getColumnsSelected();
//
//			Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
//			inConditions.put(DBConstants.CLAN_AVENGE_USER__ID, clanAvengeIds);
//			
//			String conditionDelimiter = getQueryConstructionUtil().getOr();
//
//			//(its purpose is to hold the values that were supposed to be put
//			// into a prepared statement)
//			List<Object> values = new ArrayList<Object>();
//			boolean preparedStatement = true;
//
//			String query = getQueryConstructionUtil()
//					.selectRowsQueryInConditions(columnsToSelect, TABLE_NAME,
//						inConditions, conditionDelimiter, values, preparedStatement);
//			log.info("getClanAvengeUsersForIds() query={} \t values={}",
//				query, values);
//			
//			clanAvengeUsers = this.jdbcTemplate
//					.query(query, values.toArray(), rowMapper);
//			
//		} catch (Exception e) {
//			log.error(String.format(
//				"could not retrieve ClanAvengeUser for clanAvengeUserId=%s", clanAvengeIds),
//				e);
//			clanAvengeUsers = new ArrayList<ClanAvengeUser>();
//		}
//		
//		return clanAvengeUsers;
//	}
	
	public List<ClanAvengeUser> getClanAvengeUser( String clanId )
	{
		List<ClanAvengeUser> clanAvengeUsers = null;
		try {
			List<String> columnsToSelected = ClanAvengeUserForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			if (null != clanId && !clanId.isEmpty()) {
				equalityConditions.put(DBConstants.CLAN_AVENGE_USER__CLAN_ID, clanId);
			}
			
			if (equalityConditions.isEmpty()) {
				return new ArrayList<ClanAvengeUser>();
			}
			
			String eqDelim = getQueryConstructionUtil().getAnd();

			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityConditions(
							columnsToSelected, TABLE_NAME, equalityConditions,
							eqDelim, values, preparedStatement);
			log.info("getClanAvengeUser() query={} \t values={}",
				query, values);
			clanAvengeUsers = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve ClanAvengeUser for clanId=%s", clanId),
				e);
			clanAvengeUsers = new ArrayList<ClanAvengeUser>();
		}
		
		return clanAvengeUsers;
	}
	
	public Map<String, List<ClanAvengeUser>> getClanAvengeUserMap( String clanId )
	{
		List<ClanAvengeUser> allClanAvengeUser = getClanAvengeUser(clanId);
		
		Map<String, List<ClanAvengeUser>> clanAvengeIdToClanAvenge =
			new HashMap<String, List<ClanAvengeUser>>();
		for (ClanAvengeUser cau : allClanAvengeUser)
		{
			String clanAvengeId = cau.getClanAvengeId();
			
			if (!clanAvengeIdToClanAvenge.containsKey(clanAvengeId)) {
				clanAvengeIdToClanAvenge.put(clanAvengeId, 
					new ArrayList<ClanAvengeUser>());
			}
			
			List<ClanAvengeUser> usersForRetaliation = 
				clanAvengeIdToClanAvenge.get(clanAvengeId);
			usersForRetaliation.add(cau);
		}
		
		return clanAvengeIdToClanAvenge;
	}
	
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
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanAvengeUserForClientMapper implements RowMapper<ClanAvengeUser> {

		private static List<String> columnsSelected;

		public ClanAvengeUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanAvengeUser ca = new ClanAvengeUser();
			
			ca.setClanId(rs.getString(DBConstants.CLAN_AVENGE_USER__CLAN_ID));
			ca.setClanAvengeId(rs.getString(DBConstants.CLAN_AVENGE_USER__CLAN_AVENGE_ID));
			ca.setUserId(rs.getString(DBConstants.CLAN_AVENGE_USER__USER_ID));

			Timestamp ts = rs.getTimestamp(DBConstants.CLAN_AVENGE_USER__AVENGE_TIME);
			if (null != ts) {
				ca.setAvengeTime(new Date(ts.getTime()));
			}
			
			return ca;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_AVENGE_USER__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE_USER__CLAN_AVENGE_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE_USER__USER_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE_USER__AVENGE_TIME);
			}
			return columnsSelected;
		}
	} 	
}
