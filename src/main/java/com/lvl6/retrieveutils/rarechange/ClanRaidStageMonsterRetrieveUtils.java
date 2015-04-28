package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ClanRaidStageMonsterRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Map<Integer, ClanRaidStageMonster>> clanRaidStageIdsToIdsToMonsters;
	private static Map<Integer, Map<Integer, ClanRaidStageMonster>> crsIdsToMonsterNumsToMonsters;
	private static Map<Integer, ClanRaidStageMonster> idsToMonsters;

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_RAID_STAGE_MONSTER_CONFIG;

	public static Map<Integer, ClanRaidStageMonster> getMonsterNumsToMonstersForStageId(
			int crsId) {
		log.debug("retrieving all monster num to clan raid stage monster data map");
		if (clanRaidStageIdsToIdsToMonsters == null) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}
		if (!crsIdsToMonsterNumsToMonsters.containsKey(crsId)) {
			return new HashMap<Integer, ClanRaidStageMonster>();
		}

		return crsIdsToMonsterNumsToMonsters.get(crsId);
	}

	public static Map<Integer, Map<Integer, ClanRaidStageMonster>> getClanRaidStageIdsToIdsToMonsters() {
		log.debug("retrieving all clan raid stage monster data map");
		if (clanRaidStageIdsToIdsToMonsters == null) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}
		return clanRaidStageIdsToIdsToMonsters;
	}

	public static Map<Integer, ClanRaidStageMonster> getIdsToClanRaidStageMonsters() {
		if (null == idsToMonsters) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}
		return idsToMonsters;
	}

	public static ClanRaidStageMonster getClanRaidStageMonsterForClanRaidStageMonsterId(
			int clanRaidStageMonsterId) {
		if (idsToMonsters == null) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}

		if (idsToMonsters.containsKey(clanRaidStageMonsterId)) {
			return idsToMonsters.get(clanRaidStageMonsterId);
		} else {
			log.error("no clan raid stage monster exists with id="
					+ clanRaidStageMonsterId);
			return null;
		}
	}

	public static Map<Integer, ClanRaidStageMonster> getClanRaidStageMonstersForClanRaidStageId(
			int clanRaidStageId) {
		log.debug("retrieve clan raid stage monster data for clan raid stage id "
				+ clanRaidStageId);
		if (clanRaidStageIdsToIdsToMonsters == null) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}

		if (clanRaidStageIdsToIdsToMonsters.containsKey(clanRaidStageId)) {
			return clanRaidStageIdsToIdsToMonsters.get(clanRaidStageId);
		} else {
			log.error("no clan raid stage monsters exist for clan raid stage id="
					+ clanRaidStageId);
			return new HashMap<Integer, ClanRaidStageMonster>();
		}
	}

	public static ClanRaidStageMonster getFirstMonsterForClanRaidStage(int crsId) {
		log.debug("retrieving the first clan raid stage monster for crsId="
				+ crsId);
		if (null == clanRaidStageIdsToIdsToMonsters) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}
		//check to see if stages exist for clanRaidId
		if (!clanRaidStageIdsToIdsToMonsters.containsKey(crsId)) {
			log.error("no clan raid stages monsters for crsId=" + crsId);
			return null;
		}
		Map<Integer, ClanRaidStageMonster> monsterNumsToMonsters = clanRaidStageIdsToIdsToMonsters
				.get(crsId);
		if (null == monsterNumsToMonsters || monsterNumsToMonsters.isEmpty()) {
			log.error("no clan raid stages monsters for clanRaidStageId="
					+ crsId);
			return null;
		}

		List<Integer> monsterNumsAsc = new ArrayList<Integer>(
				monsterNumsToMonsters.keySet());
		Collections.sort(monsterNumsAsc);
		int firstMonsterNum = monsterNumsAsc.get(0);

		ClanRaidStageMonster crsm = monsterNumsToMonsters.get(firstMonsterNum);
		return crsm;
	}

	public static ClanRaidStageMonster getNextMonsterForClanRaidStageMonsterId(
			int crsmId, int crsId) {
		log.debug("retrieve clan raid stage monster after clan raid stage id "
				+ crsId);
		if (clanRaidStageIdsToIdsToMonsters == null) {
			setStaticClanRaidStageIdsToIdsToMonsters();
		}

		if (!idsToMonsters.containsKey(crsmId)) {
			log.error("no clan raid stage exists for clanRaidStageId=" + crsId);
			return null;
		}
		ClanRaidStageMonster curCrsm = idsToMonsters.get(crsmId);
		int monsterNum = curCrsm.getMonsterNum();

		Map<Integer, ClanRaidStageMonster> monsterNumToCrsm = crsIdsToMonsterNumsToMonsters
				.get(crsId);

		List<Integer> monsterNumsAsc = new ArrayList<Integer>(
				monsterNumToCrsm.keySet());
		Collections.sort(monsterNumsAsc);
		int index = monsterNumsAsc.indexOf(monsterNum);

		int indexOfNextMonster = index + 1;
		if (indexOfNextMonster >= monsterNumsAsc.size()) {
			return null;
		}
		int monsterNumOfNextMonster = monsterNumsAsc.get(indexOfNextMonster);
		return monsterNumToCrsm.get(monsterNumOfNextMonster);
	}

	private static void setStaticClanRaidStageIdsToIdsToMonsters() {
		log.debug("setting static map of clanRaidStageIds to monsters");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Map<Integer, ClanRaidStageMonster>> crsIdsToCrsmIdsToCrsmTemp = new HashMap<Integer, Map<Integer, ClanRaidStageMonster>>();
						Map<Integer, Map<Integer, ClanRaidStageMonster>> crsIdsToMonsterNumsToMonstersTemp = new HashMap<Integer, Map<Integer, ClanRaidStageMonster>>();
						Map<Integer, ClanRaidStageMonster> clanRaidStageMonsterIdsToClanRaidStageMonstersTemp = new HashMap<Integer, ClanRaidStageMonster>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							ClanRaidStageMonster clanRaidStageMonster = convertRSRowToClanRaidStageMonster(rs);
							if (clanRaidStageMonster == null) {
								continue;
							}

							int clanRaidStageId = clanRaidStageMonster
									.getClanRaidStageId();
							//base case, no key with clanRaid id exists, so create map with
							//key: clanRaid id, to value: another map
							if (!crsIdsToCrsmIdsToCrsmTemp
									.containsKey(clanRaidStageId)) {
								crsIdsToCrsmIdsToCrsmTemp
										.put(clanRaidStageId,
												new HashMap<Integer, ClanRaidStageMonster>());
								crsIdsToMonsterNumsToMonstersTemp
										.put(clanRaidStageId,
												new HashMap<Integer, ClanRaidStageMonster>());
							}

							//get map of clanRaid stages related to current clanRaid id
							//stick clanRaidStageMonster into the map of ClanRaidStageMonster ids to ClanRaidStageMonster objects
							Map<Integer, ClanRaidStageMonster> clanRaidStageMonsterIdsToClanRaidStageMonstersForClanRaidStageMonster = crsIdsToCrsmIdsToCrsmTemp
									.get(clanRaidStageId);

							int clanRaidStageMonsterId = clanRaidStageMonster
									.getId();
							clanRaidStageMonsterIdsToClanRaidStageMonstersForClanRaidStageMonster
									.put(clanRaidStageMonsterId,
											clanRaidStageMonster);
							clanRaidStageMonsterIdsToClanRaidStageMonstersTemp
									.put(clanRaidStageMonsterId,
											clanRaidStageMonster);

							//enabling getting monsters in order
							Map<Integer, ClanRaidStageMonster> monsterNumsToClanRaidStageMonsters = crsIdsToMonsterNumsToMonstersTemp
									.get(clanRaidStageId);
							int monsterNum = clanRaidStageMonster
									.getMonsterNum();
							monsterNumsToClanRaidStageMonsters.put(monsterNum,
									clanRaidStageMonster);
						}

						clanRaidStageIdsToIdsToMonsters = crsIdsToCrsmIdsToCrsmTemp;
						crsIdsToMonsterNumsToMonsters = crsIdsToMonsterNumsToMonstersTemp;
						idsToMonsters = clanRaidStageMonsterIdsToClanRaidStageMonstersTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("clan raid stage monster retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticClanRaidStageIdsToIdsToMonsters();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static ClanRaidStageMonster convertRSRowToClanRaidStageMonster(
			ResultSet rs) throws SQLException {
		int i = 1;
		int id = rs.getInt(i++);
		int clanRaidStageId = rs.getInt(i++);
		int monsterId = rs.getInt(i++);
		int monsterHp = rs.getInt(i++);
		int minDmg = rs.getInt(i++);
		int maxDmg = rs.getInt(i++);
		int monsterNum = rs.getInt(i++);

		ClanRaidStageMonster clanRaidStageMonster = new ClanRaidStageMonster(
				id, clanRaidStageId, monsterId, monsterHp, minDmg, maxDmg,
				monsterNum);

		return clanRaidStageMonster;
	}
}
