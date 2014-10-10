package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.lvl6.info.ClanHelp;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ClanHelpRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanHelpRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_HELP; 
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
	public Map<Integer, List<ClanHelp>> getUserIdToClanHelp(
		int clanId, int userId )
	{
		Map<Integer, List<ClanHelp>> userIdToClanHelps = null;
		try {
			List<String> columnsToSelected = ClanHelpForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			if (userId > 0) {
				equalityConditions.put(DBConstants.CLAN_HELP__USER_ID, userId);
			}
			if (clanId > 0) {
				equalityConditions.put(DBConstants.CLAN_HELP__CLAN_ID, clanId);
			}
			
			if (equalityConditions.isEmpty()) {
				return new HashMap<Integer, List<ClanHelp>>();
			}
			
			String eqDelim = getQueryConstructionUtil().getOr();

			//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityConditions(
							columnsToSelected, TABLE_NAME, equalityConditions,
							eqDelim, values, preparedStatement);
			log.info(String.format(
				"getUserIdToClanHelpForClanId() query=%s", query));
			List<ClanHelp> chList = this.jdbcTemplate
					.query(query, new ClanHelpForClientMapper());
			
			
			userIdToClanHelps = new HashMap<Integer, List<ClanHelp>>();
			for (ClanHelp ch : chList) {
				int userId2 = ch.getUserId();
				
				//base case: initializing list
				if (!userIdToClanHelps.containsKey(userId2)) {
					userIdToClanHelps.put(userId2, new ArrayList<ClanHelp>());
				}
				
				List<ClanHelp> requests = userIdToClanHelps.get(userId2);
				requests.add(ch);
			}
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan help for clanId=%s", clanId),
				e);
			userIdToClanHelps =
					new HashMap<Integer, List<ClanHelp>>();
		}
		
		return userIdToClanHelps;
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
	private static final class ClanHelpForClientMapper implements RowMapper<ClanHelp> {

		private static List<String> columnsSelected;

		public ClanHelp mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanHelp ch = new ClanHelp();
			ch.setId(rs.getInt(DBConstants.CLAN_HELP__ID));
			ch.setUserId(rs.getInt(DBConstants.CLAN_HELP__USER_ID));
			ch.setUserDataId(rs.getLong(DBConstants.CLAN_HELP__USER_DATA_ID));
			ch.setHelpType(rs.getString(DBConstants.CLAN_HELP__HELP_TYPE));
			ch.setClanId(rs.getInt(DBConstants.CLAN_HELP__CLAN_ID));
			ch.setTimeOfEntry(rs.getDate(DBConstants.CLAN_HELP__TIME_OF_ENTRY));
			ch.setMaxHelpers(rs.getInt(DBConstants.CLAN_HELP__MAX_HELPERS));
			
			String helperIds = rs.getString(DBConstants.CLAN_HELP__HELPERS); 
			List<Integer> helpers = new ArrayList<Integer>();
			if (null != helperIds) {
				MiscMethods.explodeIntoInts(helperIds, ",", helpers);
			}
			ch.setHelpers(helpers);
			
			ch.setStaticDataId(rs.getInt(DBConstants.CLAN_HELP__STATIC_DATA_ID));
			
			return ch;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_HELP__ID);
				columnsSelected.add(DBConstants.CLAN_HELP__USER_ID);
				columnsSelected.add(DBConstants.CLAN_HELP__USER_DATA_ID);
				columnsSelected.add(DBConstants.CLAN_HELP__HELP_TYPE);
				columnsSelected.add(DBConstants.CLAN_HELP__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_HELP__TIME_OF_ENTRY);
				columnsSelected.add(DBConstants.CLAN_HELP__MAX_HELPERS);
				columnsSelected.add(DBConstants.CLAN_HELP__HELPERS);
				columnsSelected.add(DBConstants.CLAN_HELP__OPEN);
				columnsSelected.add(DBConstants.CLAN_HELP__STATIC_DATA_ID);
			}
			return columnsSelected;
		}
	} 	
}
