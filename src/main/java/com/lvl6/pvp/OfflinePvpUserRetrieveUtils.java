package com.lvl6.pvp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component /*@DependsOn("gameServer")*/
public class OfflinePvpUserRetrieveUtils {

	private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


	//search entire user table for users who do not have active shields,
	//also, only select certain columns
	protected Collection<OfflinePvpUser> getPvpValidUsers() {
		Timestamp now = new Timestamp((new Date()).getTime());

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(DBConstants.USER__ID);
		sb.append(", ");
		sb.append(DBConstants.USER__ELO);
		sb.append(", ");
		sb.append(DBConstants.USER__SHIELD_END_TIME);
		sb.append(", ");
		sb.append(DBConstants.USER__IN_BATTLE_END_TIME);
		sb.append(" FROM ");
		sb.append(DBConstants.TABLE_USER);
		sb.append(" WHERE ");
		sb.append(DBConstants.USER__HAS_ACTIVE_SHIELD);
		sb.append(" =? AND ");
		sb.append(DBConstants.USER__SHIELD_END_TIME);
		sb.append(" < ?;");
		String query = sb.toString();

		List<Object> values = new ArrayList<Object>();
		values.add(false);
		values.add(now);

		log.info("query=" + query);
		log.info("values=" + values);

		Connection conn = null;
		ResultSet rs = null;
		List<OfflinePvpUser> validUsers = new ArrayList<OfflinePvpUser>();
		try {
			conn = DBConnection.get().getConnection();
			rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
			validUsers = convertRSToOfflinePvpUser(rs);
		} catch (Exception e) {
			log.error("user retrieve db error, in order to populate PvpUsers map.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
		return validUsers;
	}

	protected List<OfflinePvpUser> convertRSToOfflinePvpUser(ResultSet rs) {
		if (null != rs) {
			try {
				rs.last();
				rs.beforeFirst();
				List<OfflinePvpUser> users = new ArrayList<OfflinePvpUser>();
				while (rs.next()) {
					users.add(convertRSRowToOfflinePvpUser(rs));
				}
				return users;
			} catch (SQLException e) {
				log.error("problem with database call, in order to populate PvpUsers map.", e);
			}
		}
		return null;
	}

	protected OfflinePvpUser convertRSRowToOfflinePvpUser(ResultSet rs) throws SQLException {
		int i = 1;

		String userId = Integer.toString((rs.getInt(i++)));
		int elo = rs.getInt(i++);

		Date shieldEndTime = null;
		try {
			Timestamp ts = rs.getTimestamp(i++);
			if (!rs.wasNull()) {
				shieldEndTime = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("db error: shield_end_time not set. user_id=" + userId);
		}

		Date inBattleShieldEndTime = null;
		try {
			Timestamp ts = rs.getTimestamp(i++);
			if (!rs.wasNull()) {
				inBattleShieldEndTime = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("db error: in_battle_shield_end_time not set. user_id=" + userId);
		}

		return new OfflinePvpUser(userId, elo, shieldEndTime, inBattleShieldEndTime);
	}

}
