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

import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.server.controller.utils.TimeUtils;

@Component 
public class ClanHelpCountForUserRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanHelpCountForUserRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_HELP_COUNT_FOR_USER; 
	private static final UserClanHelpCountForClientMapper rowMapper = new UserClanHelpCountForClientMapper();
	private JdbcTemplate jdbcTemplate;
	private static final int WEEK = 7;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	@Autowired
	protected TimeUtils timeUtils;
	
	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
	public Map<String, UserClanHelpCount> getUserIdToClanHelpCountForClan(
		String clanId, Date now )
	{
		Map<String, UserClanHelpCount> userIdToClanHelpCount = null;
		Date sevenDaysAgo = timeUtils.createDateAddDays(now, -WEEK);
		Date startOfDay = timeUtils.createDateAtStartOfDay(sevenDaysAgo);
		try {
			Timestamp time = new Timestamp((startOfDay).getTime());
			String query = String.format(
				"SELECT %s, %s, sum(%s) as %s, sum(%s) as %s FROM %s WHERE %s > ? and %s=? GROUP BY %s",
				DBConstants.CLAN_HELP_COUNT_FOR_USER__CLAN_ID,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__USER_ID,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__SOLICITED,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__SOLICITED,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__GIVEN,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__GIVEN,
				TABLE_NAME,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__DATE,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__CLAN_ID,
				DBConstants.CLAN_HELP_COUNT_FOR_USER__USER_ID);
			
			List<Object> values = new ArrayList<Object>();
			values.add(time);
			values.add(clanId);
			
			log.info( "getUserIdToClanHelpCountForClan() query={}, values={}",
					query, values);
			List<UserClanHelpCount> chList = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			
			
			userIdToClanHelpCount = new HashMap<String, UserClanHelpCount>();
			for (UserClanHelpCount uchc : chList) {
				String userId = uchc.getUserId();
				
				userIdToClanHelpCount.put(userId, uchc);
			}
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan help for clanId=%s", clanId),
				e);
			userIdToClanHelpCount =
					new HashMap<String, UserClanHelpCount>();
		}
		
		return userIdToClanHelpCount;
	}
	
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}

	public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	public TimeUtils getTimeUtils()
	{
		return timeUtils;
	}

	public void setTimeUtils( TimeUtils timeUtils )
	{
		this.timeUtils = timeUtils;
	}
	

	//Date twenty4ago = new DateTime().minusDays(1).toDate();
	protected String formatDateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String formatted = format.format(date);
		return formatted;
	}
	
	public static class UserClanHelpCount {
		private String userId;
		private String clanId;
		private int numSolicited;
		private int numGiven;
		
		public UserClanHelpCount()
		{
			super();
		}

		public UserClanHelpCount( String userId, String clanId,
			int numSolicited, int numGiven )
		{
			super();
			this.userId = userId;
			this.clanId = clanId;
			this.numSolicited = numSolicited;
			this.numGiven = numGiven;
		}

		public String getUserId()
		{
			return userId;
		}

		public void setUserId( String userId )
		{
			this.userId = userId;
		}

		public String getClanId()
		{
			return clanId;
		}

		public void setClanId( String clanId )
		{
			this.clanId = clanId;
		}

		public int getNumSolicited()
		{
			return numSolicited;
		}

		public void setNumSolicited( int numSolicited )
		{
			this.numSolicited = numSolicited;
		}

		public int getNumGiven()
		{
			return numGiven;
		}

		public void setNumGiven( int numGiven )
		{
			this.numGiven = numGiven;
		}

		@Override
		public String toString()
		{
			return "UserClanHelpCount [userId="
				+ userId
				+ ", clanId="
				+ clanId
				+ ", numSolicited="
				+ numSolicited
				+ ", numGiven="
				+ numGiven
				+ "]";
		}

	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserClanHelpCountForClientMapper implements RowMapper<UserClanHelpCount> {

		private static List<String> columnsSelected;

		public UserClanHelpCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserClanHelpCount uchc = new UserClanHelpCount();
			uchc.setUserId(rs.getString(DBConstants.CLAN_HELP_COUNT_FOR_USER__USER_ID));
			uchc.setClanId(rs.getString(DBConstants.CLAN_HELP_COUNT_FOR_USER__CLAN_ID));
			uchc.setNumSolicited(rs.getInt(DBConstants.CLAN_HELP_COUNT_FOR_USER__SOLICITED));
			uchc.setNumGiven(rs.getInt(DBConstants.CLAN_HELP_COUNT_FOR_USER__GIVEN));
			
			return uchc;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_HELP_COUNT_FOR_USER__USER_ID);
				columnsSelected.add(DBConstants.CLAN_HELP_COUNT_FOR_USER__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_HELP_COUNT_FOR_USER__SOLICITED);
				columnsSelected.add(DBConstants.CLAN_HELP_COUNT_FOR_USER__GIVEN);
			}
			return columnsSelected;
		}
	}

}
