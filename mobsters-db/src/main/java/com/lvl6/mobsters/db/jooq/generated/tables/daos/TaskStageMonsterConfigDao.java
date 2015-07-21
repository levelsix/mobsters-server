/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageMonsterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.TaskStageMonsterConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.TaskStageMonsterConfigRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TaskStageMonsterConfigDao extends DAOImpl<TaskStageMonsterConfigRecord, TaskStageMonsterConfigPojo, Integer> {

	/**
	 * Create a new TaskStageMonsterConfigDao without any configuration
	 */
	public TaskStageMonsterConfigDao() {
		super(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG, TaskStageMonsterConfigPojo.class);
	}

	/**
	 * Create a new TaskStageMonsterConfigDao with an attached configuration
	 */
	public TaskStageMonsterConfigDao(Configuration configuration) {
		super(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG, TaskStageMonsterConfigPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(TaskStageMonsterConfigPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchById(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public TaskStageMonsterConfigPojo fetchOneById(Integer value) {
		return fetchOne(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>task_stage_id IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByTaskStageId(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.TASK_STAGE_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMonsterId(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_type IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMonsterType(String... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MONSTER_TYPE, values);
	}

	/**
	 * Fetch records that have <code>exp_reward IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByExpReward(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.EXP_REWARD, values);
	}

	/**
	 * Fetch records that have <code>min_cash_drop IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMinCashDrop(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MIN_CASH_DROP, values);
	}

	/**
	 * Fetch records that have <code>max_cash_drop IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMaxCashDrop(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MAX_CASH_DROP, values);
	}

	/**
	 * Fetch records that have <code>min_oil_drop IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMinOilDrop(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MIN_OIL_DROP, values);
	}

	/**
	 * Fetch records that have <code>max_oil_drop IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMaxOilDrop(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MAX_OIL_DROP, values);
	}

	/**
	 * Fetch records that have <code>puzzle_piece_drop_rate IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByPuzzlePieceDropRate(Double... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.PUZZLE_PIECE_DROP_RATE, values);
	}

	/**
	 * Fetch records that have <code>level IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByLevel(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.LEVEL, values);
	}

	/**
	 * Fetch records that have <code>chance_to_appear IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByChanceToAppear(Double... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.CHANCE_TO_APPEAR, values);
	}

	/**
	 * Fetch records that have <code>dmg_multiplier IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByDmgMultiplier(Double... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.DMG_MULTIPLIER, values);
	}

	/**
	 * Fetch records that have <code>monster_id_drop IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMonsterIdDrop(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MONSTER_ID_DROP, values);
	}

	/**
	 * Fetch records that have <code>monster_drop_lvl IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByMonsterDropLvl(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.MONSTER_DROP_LVL, values);
	}

	/**
	 * Fetch records that have <code>defensive_skill_id IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByDefensiveSkillId(Integer... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.DEFENSIVE_SKILL_ID, values);
	}

	/**
	 * Fetch records that have <code>init_dialogue IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByInitDialogue(String... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.INIT_DIALOGUE, values);
	}

	/**
	 * Fetch records that have <code>default_dialogue IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByDefaultDialogue(String... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.DEFAULT_DIALOGUE, values);
	}

	/**
	 * Fetch records that have <code>user_toon_hp_scale IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByUserToonHpScale(Double... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.USER_TOON_HP_SCALE, values);
	}

	/**
	 * Fetch records that have <code>user_toon_atk_scale IN (values)</code>
	 */
	public List<TaskStageMonsterConfigPojo> fetchByUserToonAtkScale(Double... values) {
		return fetch(TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG.USER_TOON_ATK_SCALE, values);
	}
}
