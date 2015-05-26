//package com.lvl6.retrieveutils;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.EventPersistentForUser;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class EventPersistentForUserRetrieveUtils {
//
//	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//	private static final String TABLE_NAME = DBConstants.TABLE_EVENT_PERSISTENT_FOR_USER;
//
//
//	public static List<EventPersistentForUser> getUserPersistentEventForUserId(int userId) {
//		Connection conn = null;
//		ResultSet rs = null;
//		List<EventPersistentForUser> userPersistentEvent = new ArrayList<EventPersistentForUser>();
//		try {
//			conn = DBConnection.get().getConnection();
//			rs = DBConnection.get().selectRowsByUserId(conn, userId, TABLE_NAME);
//			userPersistentEvent = grabUserPersistentEventFromRS(rs);
//		} catch (Exception e) {
//    	log.error("event persistent for user retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//		return userPersistentEvent;
//	}
//
//	private static List<EventPersistentForUser> grabUserPersistentEventFromRS(ResultSet rs) {
//		if (rs != null) {
//			try {
//				rs.last();
//				rs.beforeFirst();
//				List<EventPersistentForUser> userCityExpansionDatas = new ArrayList<EventPersistentForUser>();
//				while(rs.next()) {
//					EventPersistentForUser uc = convertRSRowToUserCityExpansionData(rs);
//					userCityExpansionDatas.add(uc);
//				}
//				return userCityExpansionDatas;
//			} catch (SQLException e) {
//				log.error("problem with database call.", e);
//
//			}
//		}
//		return null;
//	}
//
//	/*
//	 * assumes the resultset is apprpriately set up. traverses the row it's on.
//	 */
//	private static EventPersistentForUser convertRSRowToUserCityExpansionData(ResultSet rs) throws SQLException {
//		int i = 1;
//		int userId = rs.getInt(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID);
//		int eventId = rs.getInt(DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID);
//
//		Date timeOfEntry = null;
//		try {
//			Timestamp ts = rs.getTimestamp(DBConstants.EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY);
//			if (!rs.wasNull()) {
//				timeOfEntry = new Date(ts.getTime());
//			}
//		} catch (Exception e) {
//			log.error("time of entry was null?. userId=" + userId + ", eventId=" + eventId +
//					", timeOfEntry=" + timeOfEntry);
//		}
//
//		return new EventPersistentForUser(userId, eventId, timeOfEntry);
//	}
//
//}
