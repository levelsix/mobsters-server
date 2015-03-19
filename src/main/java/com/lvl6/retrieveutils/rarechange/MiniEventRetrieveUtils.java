package com.lvl6.retrieveutils.rarechange;

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

import com.lvl6.info.MiniEvent;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MiniEventRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_CONFIG;

	private static Map<Integer, MiniEvent> idToMiniEvent;

	public static Map<Integer, MiniEvent> getAllIdsToMiniEvents() {
		if (null == idToMiniEvent) {
			setStaticIdsToMiniEvents();
		}

		return idToMiniEvent;
	}

	public static MiniEvent getMiniEventById(int id) {
		if (null == idToMiniEvent) {
			setStaticIdsToMiniEvents();
		}
		MiniEvent ep = idToMiniEvent.get(id);
		if (null == ep) {
			log.error("No MiniEvent for id={}", id);
		}
		return ep;
	}

	public static void reload() {
		setStaticIdsToMiniEvents();
	}

	private static void setStaticIdsToMiniEvents() {
		log.debug("setting static map of id to MiniEvent");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MiniEvent> idToMiniEventTemp = new HashMap<Integer, MiniEvent>();
						while (rs.next()) {  //should only be one
							MiniEvent cec = convertRSRowToMiniEvent(rs);
							if (null != cec)
								idToMiniEventTemp.put(cec.getId(), cec);
						}
						idToMiniEvent = idToMiniEventTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("MiniEvent retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	private static MiniEvent convertRSRowToMiniEvent(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MINI_EVENT__ID);
		Timestamp ts = rs.getTimestamp(DBConstants.MINI_EVENT__START_TIME);
		Date startDate = null;
		if (null != ts && !rs.wasNull()) {
			startDate = new Date(ts.getTime());
		}

		ts = rs.getTimestamp(DBConstants.MINI_EVENT__END_TIME);
		Date endDate = null;
		if (null != ts && !rs.wasNull()) {
			endDate = new Date(ts.getTime());
		}

		MiniEvent me = new MiniEvent(id, startDate, endDate);
		return me;
	}
}
