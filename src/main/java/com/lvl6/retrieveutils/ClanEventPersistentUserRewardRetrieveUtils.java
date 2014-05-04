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

import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanEventPersistentUserRewardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_USER_REWARD;


	public static Map<Integer, ClanEventPersistentUserReward> getIdToUserRewardForUserId(
			int userId) {
		Connection conn = null;
		ResultSet rs = null;

		Map<String, Object> paramsToVals = new HashMap<String, Object>();
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, userId);
     
		log.info("getting ClanEventPersistentUserReward for userId=" + userId);
    Map<Integer, ClanEventPersistentUserReward> userIdToClanPersistentEventUserInfo = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			userIdToClanPersistentEventUserInfo = grabClanEventPersistentForClanFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user retrieve db error.", e);
    	userIdToClanPersistentEventUserInfo = new HashMap<Integer, ClanEventPersistentUserReward>();
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return userIdToClanPersistentEventUserInfo;
	}
	
	public static ClanEventPersistentUserReward getPersistentEventUserInfoUserRewardIdClanId(
			int userId, int clanId) {
		Connection conn = null;
		ResultSet rs = null;

		Map<String, Object> paramsToVals = new HashMap<String, Object>();
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, clanId);
		paramsToVals.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		
     
		log.info("getting ClanEventPersistentUserReward for clanId=" + clanId);
    ClanEventPersistentUserReward clanPersistentEventUserInfo = null;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteAnd(conn, paramsToVals, TABLE_NAME);
			clanPersistentEventUserInfo = grabClanEventPersistentUserRewardFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent for user retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return clanPersistentEventUserInfo;
	}
	
	public static Map<Date, List<ClanEventPersistentUserReward>> getCepUserRewardForPastNDaysForUserId(
			int userId, int nDays, Date curDate, TimeUtils timeUtils) {
		
		curDate = timeUtils.createPstDate(curDate, 0, 0, 0);
		Date pastDate = timeUtils.createPstDate(curDate, -1* nDays, 0, 0);
		Timestamp pastTime = new Timestamp(pastDate.getTime());
		
		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID);
		querySb.append("=? AND ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME);
		querySb.append(">?;");
		
		List<Object> values = new ArrayList<Object>();
		values.add(userId);
		values.add(pastTime);
		
		String query = querySb.toString();
		log.info("query=" + query +"\t values=" + values);
		
		Map<Date, List<ClanEventPersistentUserReward>> timeToRaidStageReward = null;
		
		Connection conn = null;
		ResultSet rs = null;
		log.info("getting ClanEventPersistentUserReward for userId=" + userId);
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			timeToRaidStageReward = grabTimesToClanEventPersistentUserRewardFromRS(rs);
		} catch (Exception e) {
    	log.error("clan event persistent user reward retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
		return timeToRaidStageReward;
	}
	

	private static Map<Integer, ClanEventPersistentUserReward> grabClanEventPersistentForClanFromRS(
			ResultSet rs) {
		Map<Integer, ClanEventPersistentUserReward> userIdToClanPersistentEventUserInfo =
				new HashMap<Integer, ClanEventPersistentUserReward>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					ClanEventPersistentUserReward cepfu = convertRSRowToUserCityExpansionData(rs);
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

	private static Map<Date, List<ClanEventPersistentUserReward>> grabTimesToClanEventPersistentUserRewardFromRS(
			ResultSet rs) {
		Map<Date, List<ClanEventPersistentUserReward>> timesToRewards =
				new HashMap<Date, List<ClanEventPersistentUserReward>>();
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					ClanEventPersistentUserReward cepfu = convertRSRowToUserCityExpansionData(rs);
					if (null == cepfu) {
						continue;
					}
					
					Date crsStartTime = cepfu.getCrsStartTime();
					if (!timesToRewards.containsKey(crsStartTime)) {
						timesToRewards.put(crsStartTime, new ArrayList<ClanEventPersistentUserReward>());
					}
					
					List<ClanEventPersistentUserReward> rewards = timesToRewards.get(crsStartTime);
					rewards.add(cepfu);
					
				}
			} catch (SQLException e) {
				log.error("problem with database call.", e);

			}
		}
		return timesToRewards;
	}
	
	private static ClanEventPersistentUserReward grabClanEventPersistentUserRewardFromRS(
			ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				
				
				while(rs.next()) {
					ClanEventPersistentUserReward cepfu = convertRSRowToUserCityExpansionData(rs);
					return cepfu;
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
	private static ClanEventPersistentUserReward convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		int userId = rs.getInt(i++);
		
		Date crsStartTime = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		crsStartTime = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe crsStartTime is null for id=" + id + "userId=" + userId +
    			" crsId=" + rs.getInt(4), e);
    }
    
		int crsId = rs.getInt(i++);
		
		Date crsEndTime = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		crsEndTime = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe crsEndTime is null for id=" + id + "userId=" + userId +
    			" crsId=" + crsId, e);
    }
    
		String resourceType = rs.getString(i++);
		int staticDataId = rs.getInt(i++);
		int quantity = rs.getInt(i++);
		int clanEventPersistentId = rs.getInt(i++);

		Date timeRedeemed = null;
    try {
    	Timestamp ts = rs.getTimestamp(i++);
    	if (!rs.wasNull()) {
    		timeRedeemed = new Date(ts.getTime());
    	}
    } catch(Exception e) {
    	log.error("maybe raid history is null for id=" + id + "userId=" + userId +
    			" eventId=" + clanEventPersistentId + " crsId=" + crsId, e);
    }
		
		return new ClanEventPersistentUserReward(id, userId, crsStartTime, crsId, crsEndTime,
				resourceType, staticDataId, quantity, clanEventPersistentId, timeRedeemed);
	}

}
