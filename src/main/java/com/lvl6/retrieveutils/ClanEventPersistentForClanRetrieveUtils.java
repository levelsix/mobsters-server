package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanEventPersistentForClanRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;


	public static ClanEventPersistentForClan getPersistentEventForClanId(int clanId) {
		Connection conn = null;
		ResultSet rs = null;
		
		Map<String, Object> paramsToVals = new HashMap<String, Object>();
    paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);
     
		
		ClanEventPersistentForClan clanPersistentEvent = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			clanPersistentEvent = grabClanEventPersistentForClanFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for clan retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return clanPersistentEvent;
	}

	private static ClanEventPersistentForClan grabClanEventPersistentForClanFromRS(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				while(rs.next()) {
					ClanEventPersistentForClan uc = convertRSRowToUserCityExpansionData(rs);
					return uc;
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
	private static ClanEventPersistentForClan convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
		int i = 1;
		int clanId = rs.getInt(i++);
		int clanEventPersistentId = rs.getInt(i++);

		int crId = rs.getInt(i++);
		int crsId = rs.getInt(i++);
		
		Date stageStartTime = null;
		try {
			Timestamp ts = rs.getTimestamp(i++);
			if (!rs.wasNull()) {
				stageStartTime = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("stage start time was null?. clanId=" + clanId + ", eventId=" +
					clanEventPersistentId + ", startTime=" + stageStartTime);
		}
		
		int crsmId = rs.getInt(i++);
		Date stageMonsterStartTime = null;
		try {
			Timestamp ts = rs.getTimestamp(i++);
			if (!rs.wasNull()) {
				stageMonsterStartTime = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("stage monster start time was null?. clanId=" + clanId + ", eventId=" +
					clanEventPersistentId + ", startTime=" + stageMonsterStartTime);
		}

		return new ClanEventPersistentForClan(clanId, clanEventPersistentId, crId, crsId,
				stageStartTime, crsmId, stageMonsterStartTime);
	}

}
