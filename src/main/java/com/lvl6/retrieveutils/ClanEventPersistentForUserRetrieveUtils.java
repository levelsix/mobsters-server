package com.lvl6.retrieveutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

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
			userIdToClanPersistentEventUserInfo = grabClanEventPersistentForUserFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user retrieve db error.", e);
    	userIdToClanPersistentEventUserInfo = new HashMap<Integer, ClanEventPersistentForUser>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return userIdToClanPersistentEventUserInfo;
	}

	private static Map<Integer, ClanEventPersistentForUser> grabClanEventPersistentForUserFromRS(
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
