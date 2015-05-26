package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ServerToggle;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ServerToggleRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<String, ServerToggle> namesToServerToggles;

	private static final String TABLE_NAME = DBConstants.TABLE_SERVER_TOGGLE_CONFIG;

	public boolean getToggleValueForName(String name) {
		log.debug("retrieve toggle for toggleName: {}", name);
		if (namesToServerToggles == null) {
			setStaticNamesToServerToggles();
		}

		if (!namesToServerToggles.containsKey(name)) {
			log.error("no toggle for toggleName={}", name);
			return false;
		}
		ServerToggle toggle = namesToServerToggles.get(name);

		return toggle.isOn();
	}

	private void setStaticNamesToServerToggles() {
		log.debug("setting static map of names to ServerToggles");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<String, ServerToggle> namesToTogglesTemp = new HashMap<String, ServerToggle>();
						while (rs.next()) {
							ServerToggle toggle = convertRSRowToTeamCenter(rs);
							if (toggle != null)
								namesToTogglesTemp
										.put(toggle.getName(), toggle);
						}
						namesToServerToggles = namesToTogglesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("ServerToggle retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticNamesToServerToggles();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ServerToggle convertRSRowToTeamCenter(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SERVER_TOGGLE__ID);
		String name = rs.getString(DBConstants.SERVER_TOGGLE__NAME);
		boolean on = rs.getBoolean(DBConstants.SERVER_TOGGLE__ON);

		return new ServerToggle(id, name, on);
	}
}
