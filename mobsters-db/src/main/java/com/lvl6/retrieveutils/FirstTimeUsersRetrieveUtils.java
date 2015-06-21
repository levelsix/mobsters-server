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

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

/*NO UserTask needed because you can just return a map- only two non-user fields*/
@Component
@DependsOn("gameServer")
public class FirstTimeUsersRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(FirstTimeUsersRetrieveUtils.class);

	private static final String TABLE_NAME = DBConstants.TABLE_USER_BEFORE_TUTORIAL_COMPLETION;

	public static boolean userExistsWithUDID(String UDID) {
		log.debug("Checking open udid in first time users for user with udid "
				+ UDID);
		Map<String, Object> paramsToVals = new HashMap<String, Object>();
		paramsToVals.put(
				DBConstants.USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID, UDID);

		Connection conn = null;
		ResultSet rs = null;
		boolean loggedIn = false;
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectRowsAbsoluteOr(conn, paramsToVals,
					TABLE_NAME);
			loggedIn = convertRSToBoolean(rs);
		} catch (Exception e) {
			log.error("first time users retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return loggedIn;
	}

	private static boolean convertRSToBoolean(ResultSet rs) {
		if (rs != null) {
			try {
				rs.last();
				rs.beforeFirst();
				if (rs.next()) {  //user logged in
					return true;
				}
				return false;
			} catch (SQLException e) {
				log.error("problem with database call.", e);
			}
		}
		return false;
	}

}
