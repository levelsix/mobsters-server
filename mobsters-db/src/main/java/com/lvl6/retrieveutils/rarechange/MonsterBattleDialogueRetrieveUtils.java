package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.MonsterBattleDialogue;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class MonsterBattleDialogueRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(MonsterBattleDialogueRetrieveUtils.class);

	private static Map<Integer, List<MonsterBattleDialogue>> monsterIdToBattleDialogue;

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_BATTLE_DIALOGUE_CONFIG;

	public List<MonsterBattleDialogue> getMonsterBattleDialogueForMonsterId(
			int monsterId) {
		log.debug("retrieving data for monsterBattleDialogue with monster id "
				+ monsterId);
		if (monsterIdToBattleDialogue == null) {
			setStaticMonsterIdToMonsterBattleDialogue();
		}
		return monsterIdToBattleDialogue.get(monsterId);
	}

	public Map<Integer, List<MonsterBattleDialogue>> getMonsterIdToBattleDialogue() {
		log.debug("retrieving all cities data");
		if (monsterIdToBattleDialogue == null) {
			setStaticMonsterIdToMonsterBattleDialogue();
		}
		return monsterIdToBattleDialogue;
	}

	private void setStaticMonsterIdToMonsterBattleDialogue() {
		log.debug("setting static map of monsterIds to monsterBattleDialogue");
		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
				try {
					rs.last();
					rs.beforeFirst();
					Map<Integer, List<MonsterBattleDialogue>> monsterIdToMonsterBattleDialogueTemp = new HashMap<Integer, List<MonsterBattleDialogue>>();
					while (rs.next()) {  //should only be one
						MonsterBattleDialogue monsterBattleDialogue = convertRSRowToMonsterBattleDialogue(rs);
						if (null == monsterBattleDialogue) {
							continue;
						}

						int monsterId = monsterBattleDialogue.getMonsterId();
						if (!monsterIdToMonsterBattleDialogueTemp
								.containsKey(monsterId)) {
							List<MonsterBattleDialogue> dialogue = new ArrayList<MonsterBattleDialogue>();
							monsterIdToMonsterBattleDialogueTemp.put(monsterId,
									dialogue);
						}
						List<MonsterBattleDialogue> dialogue = monsterIdToMonsterBattleDialogueTemp
								.get(monsterId);
						dialogue.add(monsterBattleDialogue);
					}
					monsterIdToBattleDialogue = monsterIdToMonsterBattleDialogueTemp;
				} catch (SQLException e) {
					log.error("problem with database call.", e);

				}
			}
		} catch (Exception e) {
			log.error("monsterBattleDialogue retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticMonsterIdToMonsterBattleDialogue();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private MonsterBattleDialogue convertRSRowToMonsterBattleDialogue(
			ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.MONSTER_BATTLE_DIALOGUE__ID);
		int monsterId = rs
				.getInt(DBConstants.MONSTER_BATTLE_DIALOGUE__MONSTER_ID);
		String dialogueType = rs
				.getString(DBConstants.MONSTER_BATTLE_DIALOGUE__DIALOGUE_TYPE);
		String dialogue = rs
				.getString(DBConstants.MONSTER_BATTLE_DIALOGUE__DIALOGUE);
		float probabilityUttered = rs
				.getFloat(DBConstants.MONSTER_BATTLE_DIALOGUE__PROBABILITY_UTTERED);

		if (null != dialogueType) {
			String newDialogueType = dialogueType.trim().toUpperCase();
			if (!dialogueType.equals(newDialogueType)) {
				log.error(String.format(
						"incorrect MonsterBattleDialogue type. %s, id=%s",
						dialogueType, id));
				dialogueType = newDialogueType;
			}
		}

		MonsterBattleDialogue monsterBattleDialogue = new MonsterBattleDialogue(
				id, monsterId, dialogueType, dialogue, probabilityUttered);

		return monsterBattleDialogue;
	}

}
