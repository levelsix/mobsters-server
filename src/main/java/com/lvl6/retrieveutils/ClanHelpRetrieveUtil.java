package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.lvl6.info.ClanHelp;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.utils.utilmethods.StringUtils;

@Component 
public class ClanHelpRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanHelpRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_HELP; 
	private static final ClanHelpForClientMapper rowMapper = new ClanHelpForClientMapper();
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
	public List<ClanHelp> getClanHelpsForIds(List<String> clanHelpIds)
	{
		List<ClanHelp> clanHelps = null;
		try {
			List<String> columnsToSelect = ClanHelpForClientMapper
					.getColumnsSelected();

			Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(DBConstants.CLAN_HELP__ID, clanHelpIds);
			
			String conditionDelimiter = getQueryConstructionUtil().getOr();

			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryInConditions(columnsToSelect, TABLE_NAME,
						inConditions, conditionDelimiter, values, preparedStatement);
			log.info(String.format(
				"getUserIdToClanHelpForClanId() query=%s", query));
			
			clanHelps = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan help for clanHelpId=%s", clanHelpIds),
				e);
			clanHelps =
					new ArrayList<ClanHelp>();
		}
		
		return clanHelps;
	}
	
	public Map<String, List<ClanHelp>> getUserIdToClanHelp(
		String clanId, String userId )
	{
		Map<String, List<ClanHelp>> userIdToClanHelps = null;
		try {
			List<String> columnsToSelected = ClanHelpForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			if (null != userId && !userId.isEmpty()) {
				equalityConditions.put(DBConstants.CLAN_HELP__USER_ID, userId);
			}
			if (null != clanId && !clanId.isEmpty()) {
				equalityConditions.put(DBConstants.CLAN_HELP__CLAN_ID, clanId);
			}
			
			if (equalityConditions.isEmpty()) {
				return new HashMap<String, List<ClanHelp>>();
			}
			
			String eqDelim = getQueryConstructionUtil().getOr();

			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryEqualityConditions(
							columnsToSelected, TABLE_NAME, equalityConditions,
							eqDelim, values, preparedStatement);
			log.info(String.format(
				"getUserIdToClanHelpForClanId() query=%s", query));
			List<ClanHelp> chList = this.jdbcTemplate
					.query(query, values.toArray(), new ClanHelpForClientMapper());
			
			
			userIdToClanHelps = new HashMap<String, List<ClanHelp>>();
			for (ClanHelp ch : chList) {
				String userId2 = ch.getUserId();
				
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
					new HashMap<String, List<ClanHelp>>();
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
			ch.setId(rs.getString(DBConstants.CLAN_HELP__ID));
			ch.setUserId(rs.getString(DBConstants.CLAN_HELP__USER_ID));
			ch.setUserDataId(rs.getString(DBConstants.CLAN_HELP__USER_DATA_ID));

			String helpType = rs.getString(DBConstants.CLAN_HELP__HELP_TYPE);
			if (null != helpType) {
		    	String newHelpType = helpType.trim().toUpperCase();
		    	if (!helpType.equals(newHelpType)) {
		    		log.error(String.format(
		    			"helpType incorrect: %s, ClanHelp=%s",
		    			helpType, ch));
		    		helpType = newHelpType;
		    	}
		    }
			ch.setHelpType(helpType);
			
			ch.setClanId(rs.getString(DBConstants.CLAN_HELP__CLAN_ID));
			Timestamp ts = rs.getTimestamp(DBConstants.CLAN_HELP__TIME_OF_ENTRY);
			ch.setTimeOfEntry(new Date(ts.getTime()));
			ch.setMaxHelpers(rs.getInt(DBConstants.CLAN_HELP__MAX_HELPERS));
			
			String helperIds = rs.getString(DBConstants.CLAN_HELP__HELPERS); 
			List<String> helpers = null;
			if (null != helperIds) {
				helpers = StringUtils.explodeIntoStrings(helperIds, ",");
			}
			ch.setHelpers(helpers);
			
			ch.setOpen(rs.getBoolean(DBConstants.CLAN_HELP__OPEN));
			ch.setStaticDataId(rs.getInt(DBConstants.CLAN_HELP__STATIC_DATA_ID));
			
//			log.info(String.format(
//				"ClanHelp=%s TimeOfEntry=%s", ch, ts));
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
