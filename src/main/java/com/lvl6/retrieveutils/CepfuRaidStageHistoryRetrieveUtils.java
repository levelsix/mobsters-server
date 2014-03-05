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

import com.lvl6.info.CepfuRaidStageHistory;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class CepfuRaidStageHistoryRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CEPFU_RAID_STAGE_HISTORY;


	public static Map<Date, CepfuRaidStageHistory> getRaidStageHistoryForPastNDaysForUserId(
			int userId, int nDays, Date curDate, TimeUtils timeUtils) {
		
		curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
		Date pastDate = timeUtils.createPstDate(curDate, -1* nDays, 0, 0);
		Timestamp pastTime = new Timestamp(pastDate.getTime());
		
		StringBuffer querySb = new StringBuffer();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CEPFU_RAID_STAGE_HISTORY__USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME);
		querySb.append(">?;");
		
		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(pastTime);
		
		String query = querySb.toString();
		log.info("query=" + query +"\t values=" + values);
		
		Map<Date, CepfuRaidStageHistory> timeToRaidStageHistory = null;
		log.info("getting CepfuRaidStageHistory for userId=" + userId);
     
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			timeToRaidStageHistory = grabCepfuRaidStageHistoryMapFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user raid stage history retrieve db error.", e);
    	timeToRaidStageHistory = new HashMap<Date, CepfuRaidStageHistory>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return timeToRaidStageHistory;
	}
	
	private static Map<Date, CepfuRaidStageHistory> grabCepfuRaidStageHistoryMapFromRS(
			ResultSet rs) {
		Map<Date, CepfuRaidStageHistory> timesToRaidStageHistory =
				new HashMap<Date, CepfuRaidStageHistory>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				while(rs.next()) {
					CepfuRaidStageHistory cepfursh = convertRSRowToUserCityExpansionData(rs);
					if (null == cepfursh) {
						continue;
					}
					Date aDate = cepfursh.getCrsStartTime();
					
					timesToRaidStageHistory.put(aDate, cepfursh);
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return timesToRaidStageHistory;
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static CepfuRaidStageHistory convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
		int i = 1;
		int userId = rs.getInt(i++);

		Date crsStartTime = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		crsStartTime = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe raid stage history start time is null for userId=" + userId +
    			"clanId=" + rs.getInt(3) + " eventId=" + rs.getInt(4) + " crId=" + rs.getInt(5), e);
    }
		
		int clanId = rs.getInt(i++);
		int clanEventPersistentId = rs.getInt(i++);
		int crId = rs.getInt(i++);
		int crsId = rs.getInt(i++);
		int crsDmgDone = rs.getInt(i++);
		int stageHealth = rs.getInt(i++);
		
		Date crsEndTime = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		crsEndTime = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe raid stage history end time is null for userId=" + userId +
    			"clanId=" + clanId + " eventId=" + clanEventPersistentId + " crId=" + crId, e);
    }
		
		return new CepfuRaidStageHistory(userId, crsStartTime, clanId, clanEventPersistentId, crId, crsId, crsDmgDone, stageHealth, crsEndTime);
	}

}
