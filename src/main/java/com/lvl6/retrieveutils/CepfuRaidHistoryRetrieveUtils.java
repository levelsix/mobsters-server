package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.CepfuRaidHistory;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class CepfuRaidHistoryRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CEPFU_RAID_HISTORY;


	public static Map<Date, Map<Integer, CepfuRaidHistory>> getRaidHistoryForPastNDaysForClan(
			int clanId, int nDays, Date curDate, TimeUtils timeUtils) {
		
		curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
		Date pastDate = timeUtils.createPstDate(curDate, -1* nDays, 0, 0);
		Timestamp pastTime = new Timestamp(pastDate.getTime());

		StringBuffer querySb = new StringBuffer();
		querySb.append("SELECT ");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__USER_ID);
		querySb.append(",");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
		querySb.append(",");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__CR_DMG_DONE);
		querySb.append(",");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__CLAN_CR_DMG);
		querySb.append(" FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
//		querySb.append(DBConstants.CEPFU_RAID_HISTORY__USER_ID);
//		querySb.append("=? AND ");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__TIME_OF_ENTRY);
		querySb.append(">? AND ");
		querySb.append(DBConstants.CEPFU_RAID_HISTORY__CLAN_ID);
		querySb.append("=?;");
		
		List<Object> values = new ArrayList<Object>();
//		values.add(userId);
		values.add(pastTime);
		values.add(clanId);
		
		String query = querySb.toString();
		
		log.info("query=" + query +"\t values=" + values);
		
		
		Map<Date, Map<Integer, CepfuRaidHistory>> timesToRaids = null;
		log.info("getting raid history for past days. clanId=" + clanId +
				"\t pastDate=" + pastDate);
    
    Connection conn = null;
    ResultSet rs = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			timesToRaids = grabLimitedCepfuRaidHistoryMapFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user raid history retrieve db error.", e);
    	timesToRaids = new HashMap<Date, Map<Integer, CepfuRaidHistory>>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return timesToRaids;
	}
//	
//	public static ClanEventPersistentForUser getPersistentEventUserInfoForUserIdClanId(
//			int userId, int clanId) {
//		Connection conn = null;
//		ResultSet rs = null;
//
//		Map<String, Object> paramsToVals = new HashMap<String, Object>();
//		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, clanId);
//		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
//		
//     
//		log.info("getting ClanEventPersistentForUser for clanId=" + clanId);
//    ClanEventPersistentForUser clanPersistentEventUserInfo = null;
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
//			clanPersistentEventUserInfo = grabClanEventPersistentForUserRaidHistoryFromRS(rs);
//		} catch (Exception e) {
//    	log.error("clan event persistent for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//		return clanPersistentEventUserInfo;
//	}
//	
//	public static int getTotalCrsmDmgForClanId(int clanId) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("SELECT ");
//		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID);
//		sb.append(", SUM(");
//		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
//		sb.append(") FROM ");
//		sb.append(DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER);
//		sb.append(" WHERE ");
//		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID);
//		sb.append("=? AND ");
//		sb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
//		sb.append(" IS NOT NULL;");
//		
//		List<Object> values = new ArrayList<Object>();
//		values.add(clanId);
//		
//		String query = sb.toString();
//		log.info("retrieving crsm damage for clan. query=" + query + "\t values=" + values);
//		
//		Connection conn = null;
//		ResultSet rs = null;
//		Map<Integer, Integer> clanIdToCrsmDmg = new HashMap<Integer, Integer>();
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
//			clanIdToCrsmDmg = convertRSToClanIdToCrsmDmg(rs);
//		} catch (Exception e) {
//			log.error("could not retrieve crsm damage.", e);
//		} finally {
//			DBConnection.get().close(rs, null, conn);
//		}
//		
//		if (clanIdToCrsmDmg.containsKey(clanId)) {
//			return clanIdToCrsmDmg.get(clanId);
//		}
//		return 0;
//	}
//	

	private static Map<Date, Map<Integer, CepfuRaidHistory>> grabLimitedCepfuRaidHistoryMapFromRS(
			ResultSet rs) {
		Map<Date, Map<Integer, CepfuRaidHistory>> timesToUserIdsToRaidHistory =
				new HashMap<Date, Map<Integer, CepfuRaidHistory>>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				while(rs.next()) {
					CepfuRaidHistory cepfurh = convertRSRowToLimitedCepfuRaidHistory(rs);
					if (null == cepfurh) {
						continue;
					}
					Date aDate = cepfurh.getTimeOfEntry();
					
					if (!timesToUserIdsToRaidHistory.containsKey(aDate)) {
						timesToUserIdsToRaidHistory.put(aDate, new HashMap<Integer, CepfuRaidHistory>());
					}
					Map<Integer, CepfuRaidHistory> userIdToRaidHistory =
							timesToUserIdsToRaidHistory.get(aDate);
					
					int userId = cepfurh.getUserId();
					userIdToRaidHistory.put(userId, cepfurh);
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return timesToUserIdsToRaidHistory;
	}

	private static CepfuRaidHistory grabRaidHistoryFromRS(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					CepfuRaidHistory cepfurh = convertRSRowToLimitedCepfuRaidHistory(rs);
					return cepfurh;
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return null;
	}


	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static CepfuRaidHistory convertRSRowToLimitedCepfuRaidHistory(ResultSet rs) throws SQLException {
		int i = 1;
		int userId = rs.getInt(i++);

		Date timeOfEntry = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeOfEntry = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe raid history is null for userId=" + userId + "crDmgDone=" +
    			rs.getInt(3) + " clanCrDmg=" + rs.getInt(4), e);
    }
		
		int crDmgDone = rs.getInt(i++);
		int clanCrDmg = rs.getInt(i++);
		
		return new CepfuRaidHistory(userId, timeOfEntry, 0, 0, 0, crDmgDone, clanCrDmg,
				0, 0, 0);
	}

}
