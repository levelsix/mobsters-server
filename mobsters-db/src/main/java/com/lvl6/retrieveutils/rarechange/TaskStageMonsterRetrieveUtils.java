package com.lvl6.retrieveutils.rarechange;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.jooq.tools.csv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.info.Dialogue;
import com.lvl6.info.Monster;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class TaskStageMonsterRetrieveUtils {


	private static final Logger log = LoggerFactory.getLogger(TaskStageMonsterRetrieveUtils.class);

	@Autowired
	MonsterRetrieveUtils monsterRetrieveUtils;


	private static Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonsters;
	private static Map<Integer, Set<Integer>> taskStageIdsToDroppableMonsterIds;
	private static Map<Integer, Set<String>> taskStageIdsToDroppableRarities;
	private static Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonsters;

	private static boolean reassignedSkills = false;

	private static final String TABLE_NAME = DBConstants.TABLE_TASK_STAGE_MONSTER_CONFIG;

	public Map<Integer, List<TaskStageMonster>> getTaskStageIdsToTaskStageMonsters() {
		log.debug("retrieving all task stage monster data map");
		if (null == taskStageIdsToTaskStageMonsters) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}

		return taskStageIdsToTaskStageMonsters;
	}

	public TaskStageMonster getTaskStageMonsterForId(int tsmId) {
		log.debug("retrieve task stage monster for id={}", tsmId);
		if (null == taskStageMonsterIdsToTaskStageMonsters) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}
		if (!taskStageMonsterIdsToTaskStageMonsters.containsKey(tsmId)) {
			log.error("no task stage monsters for tsm id={}", tsmId);
			return null;
		}

		return taskStageMonsterIdsToTaskStageMonsters.get(tsmId);
	}

	public Set<Integer> getDroppableMonsterIdsForTaskStageId(
			int taskStageId) {
		log.debug("retrieve stage monster data for stage {}", taskStageId);
		if (null == taskStageIdsToDroppableMonsterIds) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}

		if (!taskStageIdsToDroppableMonsterIds.containsKey(taskStageId)) {
			//			log.error("no monster ids for task stage id={}", taskStageId);
			return new HashSet<Integer>();
		}

		return taskStageIdsToDroppableMonsterIds.get(taskStageId);
	}

	public Set<String> getDroppableQualitiesForTaskStageId(
			int taskStageId) {
		log.debug("retrieve stage monster quality data for stage {}",
				taskStageId);
		if (null == taskStageIdsToDroppableRarities) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}

		if (!taskStageIdsToDroppableRarities.containsKey(taskStageId)) {
			//			log.error("no monster qualities for task stage id={}", taskStageId);
			return new HashSet<String>();
		}

		return taskStageIdsToDroppableRarities.get(taskStageId);
	}

	public List<TaskStageMonster> getMonstersForTaskStageId(
			int taskStageId) {
		log.debug("retrieve task stage monster data for stage {}", taskStageId);
		if (null == taskStageIdsToTaskStageMonsters) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}

		if (!taskStageIdsToTaskStageMonsters.containsKey(taskStageId)) {
			log.error("no task stage monsters for task stage id={}",
					taskStageId);
			return new ArrayList<TaskStageMonster>();
		}

		return taskStageIdsToTaskStageMonsters.get(taskStageId);
	}

	public Map<Integer, TaskStageMonster> getTaskStageMonstersForIds(
			Collection<Integer> ids) {
		if (null == taskStageMonsterIdsToTaskStageMonsters) {
			setStaticTaskStageIdsToTaskStageMonster();
		}
		if (!reassignedSkills) {
			reassignSkills();
		}

		Map<Integer, TaskStageMonster> returnMap = new HashMap<Integer, TaskStageMonster>();

		for (int id : ids) {
			TaskStageMonster tsm = taskStageMonsterIdsToTaskStageMonsters
					.get(id);
			returnMap.put(id, tsm);
		}
		return returnMap;
	}

	private void reassignSkills() {

		if (null == taskStageMonsterIdsToTaskStageMonsters) {
			return;
		}

		Map<Integer, Monster> idToMonster = monsterRetrieveUtils
				.getMonsterIdsToMonsters();
		if (null == idToMonster) {
			return;
		}

		boolean resetAllSkills = true;
		//incorporating nonskill logic
		Map<Integer, Set<Integer>> taskStageIdsToMonsterIdsTemp = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<String>> taskStageIdsToRaritiesTemp = new HashMap<Integer, Set<String>>();

		//for the TaskStageMonsters that have their defensive skill not set
		//reassign to the skill the monster has, also set the offensive skill
		for (TaskStageMonster tsm : taskStageMonsterIdsToTaskStageMonsters
				.values()) {
			Monster monzter = null;
			int monsterId = tsm.getMonsterId();
			if (idToMonster.containsKey(monsterId)) {
				monzter = idToMonster.get(monsterId);
			}

			if (null == monzter) {
				resetAllSkills = false;
				log.error("no monster for id={}, tsm={}", monsterId, tsm);
				continue;
			}

			tsm.setOffensiveSkillId(monzter.getBaseOffensiveSkillId());

			if (ControllerConstants.NOT_SET == tsm.getDefensiveSkillId()) {
				tsm.setDefensiveSkillId(monzter.getBaseDefensiveSkillId());
			}

			//incorporating nonskill logic, calculating the monsters that can drop
			if (tsm.getPuzzlePieceDropRate() <= 0F) {
				//only care about the ones that can drop
//				log.info("tsm drops no monster. {}", tsm);
				continue;
			}

			int stageId = tsm.getStageId();
			if (!taskStageIdsToMonsterIdsTemp.containsKey(stageId)) {
				//base case
				taskStageIdsToMonsterIdsTemp.put(stageId,
						new HashSet<Integer>());
			}
			if (!taskStageIdsToRaritiesTemp.containsKey(stageId)) {
				//base case
				taskStageIdsToRaritiesTemp.put(stageId, new HashSet<String>());
			}

			Set<Integer> stageMonsterIds = taskStageIdsToMonsterIdsTemp
					.get(stageId);
			stageMonsterIds.add(monsterId);
			Set<String> stageMonsterQualities = taskStageIdsToRaritiesTemp
					.get(stageId);
			stageMonsterQualities.add(monzter.getQuality());
		}

		reassignedSkills = resetAllSkills;
		taskStageIdsToDroppableMonsterIds = taskStageIdsToMonsterIdsTemp;
		taskStageIdsToDroppableRarities = taskStageIdsToRaritiesTemp;
	}

	private void setStaticTaskStageIdsToTaskStageMonster() {
		log.debug("setting static map of taskStage and taskStageMonster Ids to monsterIds");

		reassignedSkills = false;

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
						Map<Integer, List<TaskStageMonster>> taskStageIdsToTaskStageMonstersTemp = new HashMap<Integer, List<TaskStageMonster>>();
						Map<Integer, TaskStageMonster> taskStageMonsterIdsToTaskStageMonstersTemp = new HashMap<Integer, TaskStageMonster>();

						//loop through each row and convert it into a java object
						while (rs.next()) {
							TaskStageMonster taskStageMonster = convertRSRowToTaskStageMonster(
									rs, rand);

							int stageId = taskStageMonster.getStageId();
							if (!taskStageIdsToTaskStageMonstersTemp
									.containsKey(stageId)) {
								//base case
								taskStageIdsToTaskStageMonstersTemp.put(
										stageId,
										new ArrayList<TaskStageMonster>());
							}

							List<TaskStageMonster> monsters = taskStageIdsToTaskStageMonstersTemp
									.get(stageId);
							monsters.add(taskStageMonster);

							int taskStageMonsterId = taskStageMonster.getId();
							taskStageMonsterIdsToTaskStageMonstersTemp.put(
									taskStageMonsterId, taskStageMonster);
						}
						taskStageIdsToTaskStageMonsters = taskStageIdsToTaskStageMonstersTemp;
						taskStageMonsterIdsToTaskStageMonsters = taskStageMonsterIdsToTaskStageMonstersTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("task stage monster retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticTaskStageIdsToTaskStageMonster();
		reassignSkills();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private TaskStageMonster convertRSRowToTaskStageMonster(
			ResultSet rs, Random rand) throws SQLException {
		int id = rs.getInt(DBConstants.TASK_STAGE_MONSTER__ID);
		int stageId = rs.getInt(DBConstants.TASK_STAGE_MONSTER__TASK_STAGE_ID);
		int monsterId = rs.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_ID);
		String monsterType = rs
				.getString(DBConstants.TASK_STAGE_MONSTER__MONSTER_TYPE);
		int expReward = rs.getInt(DBConstants.TASK_STAGE_MONSTER__EXP_REWARD);
		int minCashDrop = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MIN_CASH_DROP);
		int maxCashDrop = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MAX_CASH_DROP);
		int minOilDrop = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MIN_OIL_DROP);
		int maxOilDrop = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MAX_OIL_DROP);
		float puzzlePieceDropRate = rs
				.getFloat(DBConstants.TASK_STAGE_MONSTER__PUZZLE_PIECE_DROP_RATE);
		int level = rs.getInt(DBConstants.TASK_STAGE_MONSTER__LEVEL);
		float chanceToAppear = rs
				.getFloat(DBConstants.TASK_STAGE_MONSTER__CHANCE_TO_APPEAR);
		float dmgMultiplier = rs
				.getFloat(DBConstants.TASK_STAGE_MONSTER__DMG_MULTIPLIER);
		int monsterIdDrop = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_ID_DROP);
		int monsterDropLvl = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__MONSTER_DROP_LVL);

		int defensiveSkillId = rs
				.getInt(DBConstants.TASK_STAGE_MONSTER__DEFENSIVE_SKILL_ID);
		//if defensiveSkillId was null
		if (rs.wasNull()) {
			//default to skill in task stage monster
			defensiveSkillId = ControllerConstants.NOT_SET;
		}
		String initDialogue = rs
				.getString(DBConstants.TASK_STAGE_MONSTER__INIT_DIALOGUE);
		String defaultDialogue = rs
				.getString(DBConstants.TASK_STAGE_MONSTER__DEFAULT_DIALOGUE);

		//continue putting new properties above here

		if (null != monsterType) {
			String newMonsterType = monsterType.trim().toUpperCase();
			if (!monsterType.equals(newMonsterType)) {
				log.error(String.format("monster type incorrect: %s, tsmId=%s",
						monsterType, id));
				monsterType = newMonsterType;
			}
		}

		if (puzzlePieceDropRate > 1F || puzzlePieceDropRate < 0F) {
			log.error(String
					.format("incorrect puzzlePieceDropRate: %s. Forcing it to be in [0,1] inclusive. id=%s",
							puzzlePieceDropRate, id));
			puzzlePieceDropRate = Math.min(1F, puzzlePieceDropRate);
			puzzlePieceDropRate = Math.max(0F, puzzlePieceDropRate);
		}

		if (chanceToAppear < 0F) {
			log.error(String
					.format("incorrect chanceToAppear: %s. Forcing it to be at/above 0. id=%s",
							chanceToAppear, id));
			chanceToAppear = Math.max(0F, chanceToAppear);
		}

		if (dmgMultiplier < 0F) {
			log.error(String
					.format("incorrect dmgMultiplier: %s. Forcing it to be at/above 0. id=%s",
							dmgMultiplier, id));
			dmgMultiplier = Math.max(0F, dmgMultiplier);
		}

		Dialogue initD = null;
		if (null != initDialogue && !initDialogue.isEmpty()) {
			initD = createDialogue(initDialogue);
		}
		Dialogue defaultD = null;
		if (null != defaultDialogue && !defaultDialogue.isEmpty()) {
			defaultD = createDialogue(defaultDialogue);
		}

		TaskStageMonster taskStageMonster = new TaskStageMonster(id, stageId,
				monsterId, monsterType, expReward, minCashDrop, maxCashDrop,
				minOilDrop, maxOilDrop, puzzlePieceDropRate, level,
				chanceToAppear, dmgMultiplier, monsterIdDrop, monsterDropLvl,
				defensiveSkillId, 0, initDialogue, defaultDialogue, initD,
				defaultD);

		if (null == monsterType) {
			log.error(String
					.format("TaskStageMonster, monster type incorrect, offending tsm=%s",
							taskStageMonster));
		}
		taskStageMonster.setRand(rand);
		return taskStageMonster;
	}

	public Dialogue createDialogue(String dialogueBlob) {
		if (dialogueBlob != null && dialogueBlob.length() > 0) {
			StringTokenizer st = new StringTokenizer(dialogueBlob, "~");

			List<Boolean> isLeftSides = new ArrayList<Boolean>();
			List<String> speakers = new ArrayList<String>();
			List<String> speakerImages = new ArrayList<String>();
			List<String> speakerTexts = new ArrayList<String>();

			CSVReader reader = null;
			try {
				while (st.hasMoreTokens()) {
					String tok = st.nextToken();
					reader = new CSVReader(new StringReader(tok), '.');
					String[] strs = reader.readNext();
					if (strs.length == 4) {
						Boolean isLeftSide = strs[0].toUpperCase().equals("L");
						String speaker = strs[1];
						String speakerImage = strs[2];
						String speakerText = strs[3];
						if (speakerText != null) {
							isLeftSides.add(isLeftSide);
							speakers.add(speaker);
							speakerImages.add(speakerImage);
							speakerTexts.add(speakerText);
						}
					}
				}
			} catch (Exception e) {
				log.error(
						"problem with creating dialogue object for this dialogueblob: {}",
						dialogueBlob, e);
			} finally {
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
						log.error("error trying to close CSVReader", e);
					}
				}
			}
			return new Dialogue(speakers, speakerImages, speakerTexts,
					isLeftSides);
		}
		return null;
	}
}
