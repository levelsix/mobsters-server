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

import com.lvl6.info.ClanInvite;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ClanInviteRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanInviteRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_INVITE; 
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
	public List<ClanInvite> getClanInvitesForUserId(int userId)
	{
		List<ClanInvite> clanInvites = null;
		try {
			List<String> columnsToSelected = ClanInviteForClientMapper
				.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.CLAN_INVITE__USER_ID, userId);
			
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
				"getUserIdToClanInviteForClanId() query=%s", query));
			clanInvites = this.jdbcTemplate
					.query(query, new ClanInviteForClientMapper());
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan invites for userId=%s", userId),
				e);
			clanInvites =
					new ArrayList<ClanInvite>();
		}
		
		return clanInvites;
	}
	
	public ClanInvite getClanInvite( int userId, int inviterId )
	{
		ClanInvite invite = null;
		try {
			List<String> columnsToSelected = ClanInviteForClientMapper
					.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.CLAN_INVITE__USER_ID, userId);
			equalityConditions.put(DBConstants.CLAN_INVITE__INVITER_ID, inviterId);
			
			String eqDelim = getQueryConstructionUtil().getAnd();

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
				"getClanInvite() query=%s", query));
			List<ClanInvite> invites = this.jdbcTemplate
					.query(query, new ClanInviteForClientMapper());
			
			if (null != invites && !invites.isEmpty()) {
				invite = invites.get(0);
				if (invites.size() > 1) {
					log.error(String.format(
						"wtf, userId and inviterId s'posd 2b unique. invites=%s",
						invites));
				}
			}
			
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan invite for clanId=%s", inviterId),
				e);
		}
		
		return invite;
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
	private static final class ClanInviteForClientMapper implements RowMapper<ClanInvite> {

		private static List<String> columnsSelected;

		public ClanInvite mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanInvite ci = new ClanInvite();
			ci.setUserId(rs.getInt(DBConstants.CLAN_INVITE__USER_ID));
			ci.setInviterId(rs.getInt(DBConstants.CLAN_INVITE__INVITER_ID));
			ci.setClanId(rs.getInt(DBConstants.CLAN_INVITE__CLAN_ID));
			Timestamp ts = rs.getTimestamp(DBConstants.CLAN_INVITE__TIME_OF_INVITE);
			ci.setTimeOfInvite(new Date(ts.getTime()));
			
			return ci;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_INVITE__USER_ID);
				columnsSelected.add(DBConstants.CLAN_INVITE__INVITER_ID);
				columnsSelected.add(DBConstants.CLAN_INVITE__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_INVITE__TIME_OF_INVITE);
			}
			return columnsSelected;
		}
	} 	
}
