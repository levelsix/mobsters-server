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

import com.lvl6.info.ClanRaid;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class ClanRaidRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, ClanRaid> clanRaidIdToClanRaid;

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID;

	public static ClanRaid getClanRaidForClanRaidId(int clanRaidId) {
		log.debug("retrieving data for clanRaid with clanRaid id " + clanRaidId);
		if (clanRaidIdToClanRaid == null) {
			setStaticClanRaidIdsToClanRaid();
		}
		if (clanRaidIdToClanRaid.containsKey(clanRaidId)) {
			return clanRaidIdToClanRaid.get(clanRaidId);
		} else {
			log.error("no ClanRaid with id=" + clanRaidId);
		}
		return null;
	}

	public static Map<Integer, ClanRaid> getClanRaidIdsToClanRaids() {
		log.debug("retrieving all clan raid data");
		if (clanRaidIdToClanRaid == null) {
			setStaticClanRaidIdsToClanRaid();
		}
		return clanRaidIdToClanRaid;
	}

	private static void setStaticClanRaidIdsToClanRaid() {
		log.debug("setting static map of clanRaidIds to clanRaid");
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
				try {
					rs.last();
					rs.beforeFirst();
					Map <Integer, ClanRaid> clanRaidIdToClanRaidTemp = new HashMap<Integer, ClanRaid>();
					while(rs.next()) {  //should only be one
						ClanRaid clanRaid = convertRSRowToClanRaid(rs);
						if (clanRaid != null)
							clanRaidIdToClanRaidTemp.put(clanRaid.getId(), clanRaid);
					}
					clanRaidIdToClanRaid = clanRaidIdToClanRaidTemp;
				} catch (SQLException e) {
					log.error("problem with database call.", e);

				}
			}
		} catch (Exception e) {
    	log.error("clanRaid retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
	}   

	public static void reload() {
		setStaticClanRaidIdsToClanRaid();
	}


	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static ClanRaid convertRSRowToClanRaid(ResultSet rs) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		String clanRaidName = rs.getString(i++);
		String activeTitleImgName = rs.getString(i++);
		String activeBackgroundImgName = rs.getString(i++);
		String activeDescription = rs.getString(i++);
		String inactiveMonsterImgName = rs.getString(i++);
		String inactiveDescription = rs.getString(i++);
		String dialogueText = rs.getString(i++);
		String spotlightMonsterImgName = rs.getString(i++);
		
		ClanRaid clanRaid = new ClanRaid(id, clanRaidName, activeTitleImgName,
				activeBackgroundImgName, activeDescription, inactiveMonsterImgName,
				inactiveDescription, dialogueText, spotlightMonsterImgName); 
		return clanRaid;
	}

}
