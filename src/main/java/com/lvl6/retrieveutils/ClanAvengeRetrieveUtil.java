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

import com.lvl6.info.ClanAvenge;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ClanAvengeRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanAvengeRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_AVENGE; 
	private static final ClanAvengeForClientMapper rowMapper = new ClanAvengeForClientMapper();
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
	public List<ClanAvenge> getClanAvengesForIds(List<String> clanAvengeIds)
	{
		List<ClanAvenge> clanAvenges = null;
		try {
			List<String> columnsToSelect = ClanAvengeForClientMapper
					.getColumnsSelected();

			Map<String, Collection<?>> inConditions = new HashMap<String, Collection<?>>();
			inConditions.put(DBConstants.CLAN_AVENGE__ID, clanAvengeIds);
			
			String conditionDelimiter = getQueryConstructionUtil().getOr();

			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = new ArrayList<Object>();
			boolean preparedStatement = true;

			String query = getQueryConstructionUtil()
					.selectRowsQueryInConditions(columnsToSelect, TABLE_NAME,
						inConditions, conditionDelimiter, values, preparedStatement);
			log.info("getClanAvengesForIds() query={} \t values={}",
				query, values);
			
			clanAvenges = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve ClanAvenge for clanAvengeId=%s", clanAvengeIds),
				e);
			clanAvenges = new ArrayList<ClanAvenge>();
		}
		
		return clanAvenges;
	}
	
	public List<ClanAvenge> getClanAvenge( String clanId )
	{
		List<ClanAvenge> clanAvenges = null;
		try {
			List<String> columnsToSelected = ClanAvengeForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			if (null != clanId && !clanId.isEmpty()) {
				equalityConditions.put(DBConstants.CLAN_AVENGE__CLAN_ID, clanId);
			}
			
			if (equalityConditions.isEmpty()) {
				return new ArrayList<ClanAvenge>();
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
			log.info("getClanAvenge() query={} \t values={}",
				query, values);
			clanAvenges = this.jdbcTemplate
					.query(query, values.toArray(), rowMapper);
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve ClanAvenge for clanId=%s", clanId),
				e);
			clanAvenges = new ArrayList<ClanAvenge>();
		}
		
		return clanAvenges;
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
	private static final class ClanAvengeForClientMapper implements RowMapper<ClanAvenge> {

		private static List<String> columnsSelected;

		public ClanAvenge mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanAvenge ca = new ClanAvenge();
			
			ca.setId(rs.getString(DBConstants.CLAN_AVENGE__ID));
			ca.setClanId(rs.getString(DBConstants.CLAN_AVENGE__CLAN_ID));
			ca.setAttackerId(rs.getString(DBConstants.CLAN_AVENGE__ATTACKER_ID));
			ca.setDefenderId(rs.getString(DBConstants.CLAN_AVENGE__DEFENDER_ID));

			Timestamp ts = rs.getTimestamp(DBConstants.CLAN_AVENGE__BATTLE_END_TIME);
			if (null != ts) {
				ca.setBattleEndTime(new Date(ts.getTime()));
			}
			
			ts = rs.getTimestamp(DBConstants.CLAN_AVENGE__AVENGE_REQUEST_TIME);
			if (null != ts) {
				ca.setAvengeRequestTime(new Date(ts.getTime()));
			}
			
			return ca;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_AVENGE__ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE__ATTACKER_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE__DEFENDER_ID);
				columnsSelected.add(DBConstants.CLAN_AVENGE__BATTLE_END_TIME);
				columnsSelected.add(DBConstants.CLAN_AVENGE__AVENGE_REQUEST_TIME);
			}
			return columnsSelected;
		}
	} 	
}
