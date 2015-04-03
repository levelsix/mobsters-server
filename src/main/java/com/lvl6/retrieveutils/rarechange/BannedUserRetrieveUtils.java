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

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Set<Integer> userIds;

	private static final String TABLE_NAME = DBConstants.TABLE_BANNED_USER;

	public Set<Integer> getAllBannedUsers() {
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
						Set<Integer> userIdsTemp = new HashSet<Integer>();
						while (rs.next()) {
							Integer profanityTerm = convertRSRowToBannedUser(rs);
							if (null != profanityTerm)
								userIdsTemp.add(profanityTerm);
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
	private Integer convertRSRowToBannedUser(ResultSet rs)
			throws SQLException {
		Integer profanityTerm = rs.getInt(DBConstants.BANNED_USER__USER_ID);

		return profanityTerm;
	}
}
