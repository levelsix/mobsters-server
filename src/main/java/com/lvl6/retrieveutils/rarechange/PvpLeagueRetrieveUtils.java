package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.PvpLeague;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class PvpLeagueRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeagues;

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_LEAGUE;

	public static Map<Integer, PvpLeague> getPvpLeagueIdsToPvpLeagues() {
		if (null == pvpLeagueIdsToPvpLeagues) {
			setStaticPvpLeagueIdsToPvpLeagues();
		}
		return pvpLeagueIdsToPvpLeagues;
	}

	public static Map<Integer, PvpLeague> getPvpLeaguesForIds(Collection<Integer> ids) {
		if (null == pvpLeagueIdsToPvpLeagues) {
			setStaticPvpLeagueIdsToPvpLeagues();
		}
		Map<Integer, PvpLeague> returnMap = new HashMap<Integer, PvpLeague>();

		for (int id : ids) {
			PvpLeague tsm = pvpLeagueIdsToPvpLeagues.get(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	private static void setStaticPvpLeagueIdsToPvpLeagues() {
		log.debug("setting static map of pvpLeague ids to pvpLeagues");

		Random rand = new Random();
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, PvpLeague> pvpLeagueIdsToPvpLeaguesTemp =
								new HashMap<Integer, PvpLeague>();

						//loop through each row and convert it into a java object
						while(rs.next()) {  
							PvpLeague pvpLeague = convertRSRowToPvpLeague(rs, rand);

							int pvpLeagueId = pvpLeague.getId();
							pvpLeagueIdsToPvpLeaguesTemp.put(pvpLeagueId, pvpLeague);
						}

						pvpLeagueIdsToPvpLeagues = pvpLeagueIdsToPvpLeaguesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}    
			}
		} catch (Exception e) {
			log.error("pvpLeague retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticPvpLeagueIdsToPvpLeagues();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static PvpLeague convertRSRowToPvpLeague(ResultSet rs, Random rand) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		String leagueName = rs.getString(i++);
		String imgPrefix = rs.getString(i++);
		int numRanks = rs.getInt(i++);
		String description = rs.getString(i++);
		int minElo = rs.getInt(i++);
		int maxElo = rs.getInt(i++);

		PvpLeague pvpLeague = new PvpLeague(id, leagueName,
				imgPrefix, numRanks, description, minElo, maxElo);
		return pvpLeague;
	}
}
