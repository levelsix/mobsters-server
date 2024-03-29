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

import com.lvl6.info.StaticUserLevelInfo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StaticUserLevelInfoRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(StaticUserLevelInfoRetrieveUtils.class);

	private static Map<Integer, StaticUserLevelInfo> levelToStaticLevelInfo;

	private static final String TABLE_NAME = DBConstants.TABLE_STATIC_LEVEL_INFO_CONFIG;

	public Map<Integer, StaticUserLevelInfo> getAllStaticUserLevelInfo() {
		log.debug("retrieving all static level info");
		if (levelToStaticLevelInfo == null) {
			setStaticLevelInfo();
		}
		return levelToStaticLevelInfo;
	}

	public StaticUserLevelInfo getStaticLevelInfoForLevel(int level) {
		log.debug("retrieving static level info for a level. level=" + level);
		if (levelToStaticLevelInfo == null) {
			setStaticLevelInfo();
		}
		return levelToStaticLevelInfo.get(level);
	}

	private void setStaticLevelInfo() {
		log.debug("setting static set of static level info");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, StaticUserLevelInfo> levelToStaticLevelInfoTemp = new HashMap<Integer, StaticUserLevelInfo>();
						while (rs.next()) {
							StaticUserLevelInfo sli = convertRSRowToStaticLevelInfo(rs);
							if (null != sli) {
								int lvl = sli.getLvl();
								levelToStaticLevelInfoTemp.put(lvl, sli);
							}
						}
						levelToStaticLevelInfo = levelToStaticLevelInfoTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);
					}
				}
			}
		} catch (Exception e) {
			log.error("static user level info retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticLevelInfo();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StaticUserLevelInfo convertRSRowToStaticLevelInfo(
			ResultSet rs) throws SQLException {
		int lvl = rs.getInt(DBConstants.STATIC_LEVEL_INFO__LEVEL_ID);
		int requiredExp = rs
				.getInt(DBConstants.STATIC_LEVEL_INFO__REQUIRED_EXPERIENCE);

		StaticUserLevelInfo sli = new StaticUserLevelInfo(lvl, requiredExp);
		return sli;
	}
}
