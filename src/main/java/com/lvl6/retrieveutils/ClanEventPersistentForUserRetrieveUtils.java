package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ClanEventPersistentForUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;


	public static Map<Integer, ClanEventPersistentForUser> getPersistentEventUserInfoForClanId(
			int clanId) {
		Connection conn = null;
		ResultSet rs = null;

		Map<String, Object> paramsToVals = new HashMap<String, Object>();
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, clanId);
     
		log.info("getting ClanEventPersistentForUser for clanId=" + clanId);
    Map<Integer, ClanEventPersistentForUser> userIdToClanPersistentEventUserInfo = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userIdToClanPersistentEventUserInfo = grabClanEventPersistentForClanFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user retrieve db error.", e);
    	userIdToClanPersistentEventUserInfo = new HashMap<Integer, ClanEventPersistentForUser>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return userIdToClanPersistentEventUserInfo;
	}
	
	public static ClanEventPersistentForUser getPersistentEventUserInfoForUserIdClanId(
			int userId, int clanId) {
		Connection conn = null;
		ResultSet rs = null;

		Map<String, Object> paramsToVals = new HashMap<String, Object>();
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, clanId);
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		
     
		log.info("getting ClanEventPersistentForUser for clanId=" + clanId);
    ClanEventPersistentForUser clanPersistentEventUserInfo = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			clanPersistentEventUserInfo = grabClanEventPersistentForUserFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return clanPersistentEventUserInfo;
	}
	/*
	public static int getTotalCrsmDmgForClanId(int clanId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);
		sb.append(", SUM(");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(") FROM ");
		sb.append(DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER);
		sb.append(" WHERE ");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
		sb.append("=? AND ");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(" IS NOT NULL;");
		
		List<Object> values = new ArrayList<Object>();
		values.add(clanId);
		
		String query = sb.toString();
		log.info("retrieving crsm damage for clan. query=" + query + "\t values=" + values);
		
		Connection conn = null;
		ResultSet rs = null;
		Map<Integer, Integer> clanIdToCrsmDmg = new HashMap<Integer, Integer>();
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			clanIdToCrsmDmg = convertRSToClanIdToCrsmDmg(rs);
		} catch (Exception e) {
			log.error("could not retrieve crsm damage.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		
		if (clanIdToCrsmDmg.containsKey(clanId)) {
			return clanIdToCrsmDmg.get(clanId);
		}
		return 0;
	}*/
	
	public static Map<Integer, Integer> getTotalCrsmDmgForClanIds(List<Integer> clanIds) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT(");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);
		sb.append("), SUM(");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(") FROM ");
		sb.append(DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER);
		sb.append(" WHERE ");
		
		List<Object> values = new ArrayList<Object>();
		
		if (null != clanIds && !clanIds.isEmpty()) {
			sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
			sb.append(" IN (");

			int amount = clanIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionStr = StringUtils.csvList(questions);
			sb.append(questionStr);
			sb.append(") AND ");
			
			values.addAll(clanIds);
		}
		
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
		sb.append(" IS NOT null AND 1=?  GROUP BY ");
		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
		
		values.add(1);
		
		String query = sb.toString();
		
		if (null != clanIds && !clanIds.isEmpty()) {
			log.info("retrieving crsm damage for clans. query=" + query + "\t values=" + values);
		} else {
			log.info("retrieving crsm damage for all clans. query=" + query);
		}
		
		Connection conn = null;
		ResultSet rs = null;
		Map<Integer, Integer> clanIdToCrsmDmg = new HashMap<Integer, Integer>();
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			clanIdToCrsmDmg = convertRSToClanIdToCrsmDmg(rs);
		} catch (Exception e) {
			log.error("could not retrieve crsm damage.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		
		return clanIdToCrsmDmg;
	}

	private static Map<Integer, ClanEventPersistentForUser> grabClanEventPersistentForClanFromRS(
			ResultSet rs) {
		Map<Integer, ClanEventPersistentForUser> userIdToClanPersistentEventUserInfo =
				new HashMap<Integer, ClanEventPersistentForUser>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					ClanEventPersistentForUser cepfu = convertRSRowToUserCityExpansionData(rs);
					if (null == cepfu) {
						continue;
					}
					int userId = cepfu.getUserId();
					userIdToClanPersistentEventUserInfo.put(userId, cepfu);
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return userIdToClanPersistentEventUserInfo;
	}

	private static ClanEventPersistentForUser grabClanEventPersistentForUserFromRS(
			ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					ClanEventPersistentForUser cepfu = convertRSRowToUserCityExpansionData(rs);
					return cepfu;
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}
	
	private static Map<Integer, Integer> convertRSToClanIdToCrsmDmg(ResultSet rs) {
		Map<Integer, Integer> clanIdToTotalCrsmDmg = new HashMap<Integer, Integer>();
		
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					int clanId = rs.getInt(1); 
					int crsmDmg = rs.getInt(2);
					
					int sumCrsmDmg = 0;
					
					if (clanIdToTotalCrsmDmg.containsKey(clanId)) {
						sumCrsmDmg = clanIdToTotalCrsmDmg.get(clanId);
					}
					
					sumCrsmDmg += crsmDmg;
					clanIdToTotalCrsmDmg.put(clanId, sumCrsmDmg);
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return clanIdToTotalCrsmDmg;
	}


	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static ClanEventPersistentForUser convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
		int i = 1;
		int userId = rs.getInt(i++);
		int clanId = rs.getInt(i++);
		int crId = rs.getInt(i++);
		int crDmgDone = rs.getInt(i++);
		int crsId = rs.getInt(i++);
		int crsDmgDone = rs.getInt(i++);
		int crsmId = rs.getInt(i++);
		int crsmDmgDone = rs.getInt(i++);
		long userMonsterIdOne = rs.getLong(i++);
		long userMonsterIdTwo = rs.getLong(i++);
		long userMonsterIdThree = rs.getLong(i++);
		
		return new ClanEventPersistentForUser(userId, clanId, crId, crDmgDone, crsId,
				crsDmgDone, crsmId, crsmDmgDone, userMonsterIdOne, userMonsterIdTwo,
				userMonsterIdThree);
	}

}
