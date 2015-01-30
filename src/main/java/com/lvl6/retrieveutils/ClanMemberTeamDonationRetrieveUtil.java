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

import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component 
public class ClanMemberTeamDonationRetrieveUtil {
	private static Logger log = LoggerFactory.getLogger(ClanMemberTeamDonationRetrieveUtil.class);
	
	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_MEMBER_TEAM_DONATION; 
	private static final ClanMemberTeamDonationForClientMapper rowMapper = new ClanMemberTeamDonationForClientMapper();
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
	public ClanMemberTeamDonation getClanMemberTeamDonationsForUserId(String userId)
	{
		ClanMemberTeamDonation cmtd = null;
		try {
			List<String> columnsToSelected = ClanMemberTeamDonationForClientMapper
				.getColumnsSelected();

			Map<String, Object> equalityConditions = new HashMap<String, Object>();
			equalityConditions.put(DBConstants.CLAN_MEMBER_TEAM_DONATION__USER_ID, userId);
			
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
			log.info("getUserIdToClanMemberTeamDonationForClanId() query={}, value={}",
				query, userId);
			List<ClanMemberTeamDonation> clanMemberTeamDonations = this.jdbcTemplate
					.query(query, rowMapper);
			
			if (null != clanMemberTeamDonations &&
				!clanMemberTeamDonations.isEmpty())
			{
				cmtd = clanMemberTeamDonations.get(0);
			}
			
		} catch (Exception e) {
			log.error(String.format(
				"could not retrieve clan invites for userId=%s", userId),
				e);
		}
		
		return cmtd;
	}
	
//	public ClanMemberTeamDonation getClanMemberTeamDonation( String userId, String inviterId )
//	{
//		ClanMemberTeamDonation invite = null;
//		try {
//			List<String> columnsToSelected = ClanMemberTeamDonationForClientMapper
//					.getColumnsSelected();
//
//			Map<String, Object> equalityConditions = new HashMap<String, Object>();
//			equalityConditions.put(DBConstants.CLAN_MEMBER_TEAM_DONATION__USER_ID, userId);
//			equalityConditions.put(DBConstants.CLAN_MEMBER_TEAM_DONATION__INVITER_ID, inviterId);
//			
//			String eqDelim = getQueryConstructionUtil().getAnd();
//
//			//query db, "values" is not used 
//			//(its purpose is to hold the values that were supposed to be put
//			// into a prepared statement)
//			List<Object> values = new ArrayList<Object>();
//			boolean preparedStatement = true;
//
//			String query = getQueryConstructionUtil()
//					.selectRowsQueryEqualityConditions(
//							columnsToSelected, TABLE_NAME, equalityConditions,
//							eqDelim, values, preparedStatement);
//			log.info(String.format(
//				"getClanMemberTeamDonation() query=%s", query));
//			List<ClanMemberTeamDonation> invites = this.jdbcTemplate
//					.query(query, values.toArray(), rowMapper);
//			
//			if (null != invites && !invites.isEmpty()) {
//				invite = invites.get(0);
//				if (invites.size() > 1) {
//					log.error(String.format(
//						"wtf, userId and inviterId s'posd 2b unique. invites=%s",
//						invites));
//				}
//			}
//			
//			
//		} catch (Exception e) {
//			log.error(String.format(
//				"could not retrieve clan invite for clanId=%s", inviterId),
//				e);
//		}
//		
//		return invite;
//	}
	
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
	private static final class ClanMemberTeamDonationForClientMapper implements RowMapper<ClanMemberTeamDonation> {

		private static List<String> columnsSelected;

		public ClanMemberTeamDonation mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanMemberTeamDonation cmtd = new ClanMemberTeamDonation();
			cmtd.setId(rs.getString(DBConstants.CLAN_MEMBER_TEAM_DONATION__ID));
			cmtd.setUserId(rs.getString(DBConstants.CLAN_MEMBER_TEAM_DONATION__USER_ID));
			cmtd.setClanId(rs.getString(DBConstants.CLAN_MEMBER_TEAM_DONATION__CLAN_ID));
			cmtd.setPowerLimit(rs.getInt(DBConstants.CLAN_MEMBER_TEAM_DONATION__POWER_LIMIT));
			cmtd.setFulfilled(rs.getBoolean(DBConstants.CLAN_MEMBER_TEAM_DONATION__IS_FULFILLED));
			cmtd.setMsg(rs.getString(DBConstants.CLAN_MEMBER_TEAM_DONATION__MSG));
			
			Timestamp ts = rs.getTimestamp(DBConstants.CLAN_MEMBER_TEAM_DONATION__TIME_OF_SOLICITATION);
			cmtd.setTimeOfSolicitation(new Date(ts.getTime()));
			
			return cmtd;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__ID);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__USER_ID);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__POWER_LIMIT);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__IS_FULFILLED);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__MSG);
				columnsSelected.add(DBConstants.CLAN_MEMBER_TEAM_DONATION__TIME_OF_SOLICITATION);
			}
			return columnsSelected;
		}
	} 	
}
