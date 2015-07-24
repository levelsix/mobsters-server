package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class BannedUserRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(BannedUserRetrieveUtils.class);

	private static Set<String> userIds;

	private static final String TABLE_NAME = DBConstants.TABLE_BANNED_USER;

	public Set<String> getAllBannedUsers() {
		log.debug("retrieving all banned users placed in a set");
		if (userIds == null) {
			setStaticBannedUsers();
		}
		return userIds;
	}

	private void setStaticBannedUsers() {
		log.debug("setting static set of banned users");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Set<String> userIdsTemp = new HashSet<String>();
						while (rs.next()) {
							String mutedUserId = convertRSRowToBannedUser(rs);
							if (null != mutedUserId)
								userIdsTemp.add(mutedUserId);
						}
						userIds = userIdsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);
					}
				}
			}
		} catch (Exception e) {
			log.error("banned user retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticBannedUsers();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private String convertRSRowToBannedUser(ResultSet rs)
			throws SQLException {
		String userId = rs.getString(DBConstants.BANNED_USER__USER_ID);

		return userId;
	}
}
