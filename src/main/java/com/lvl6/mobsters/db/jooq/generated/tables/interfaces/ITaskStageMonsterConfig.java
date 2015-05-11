/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
@Entity
@Table(name = "task_stage_monster_config", schema = "mobsters")
public interface ITaskStageMonsterConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.id</code>.
	 */
	public ITaskStageMonsterConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.task_stage_id</code>.
	 */
	public ITaskStageMonsterConfig setTaskStageId(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.task_stage_id</code>.
	 */
	@Column(name = "task_stage_id", nullable = false, precision = 10)
	@NotNull
	public Integer getTaskStageId();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.monster_id</code>.
	 */
	public ITaskStageMonsterConfig setMonsterId(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.monster_id</code>.
	 */
	@Column(name = "monster_id", nullable = false, precision = 10)
	@NotNull
	public Integer getMonsterId();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.monster_type</code>.
	 */
	public ITaskStageMonsterConfig setMonsterType(String value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.monster_type</code>.
	 */
	@Column(name = "monster_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	public String getMonsterType();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.exp_reward</code>.
	 */
	public ITaskStageMonsterConfig setExpReward(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.exp_reward</code>.
	 */
	@Column(name = "exp_reward", precision = 10)
	public Integer getExpReward();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.min_cash_drop</code>.
	 */
	public ITaskStageMonsterConfig setMinCashDrop(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.min_cash_drop</code>.
	 */
	@Column(name = "min_cash_drop", precision = 10)
	public Integer getMinCashDrop();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.max_cash_drop</code>.
	 */
	public ITaskStageMonsterConfig setMaxCashDrop(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.max_cash_drop</code>.
	 */
	@Column(name = "max_cash_drop", precision = 10)
	public Integer getMaxCashDrop();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.min_oil_drop</code>.
	 */
	public ITaskStageMonsterConfig setMinOilDrop(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.min_oil_drop</code>.
	 */
	@Column(name = "min_oil_drop", precision = 10)
	public Integer getMinOilDrop();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.max_oil_drop</code>.
	 */
	public ITaskStageMonsterConfig setMaxOilDrop(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.max_oil_drop</code>.
	 */
	@Column(name = "max_oil_drop", precision = 10)
	public Integer getMaxOilDrop();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.puzzle_piece_drop_rate</code>. Can only drop a piece of a level-1 monster, or a complete level-something monster.
	 */
	public ITaskStageMonsterConfig setPuzzlePieceDropRate(Double value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.puzzle_piece_drop_rate</code>. Can only drop a piece of a level-1 monster, or a complete level-something monster.
	 */
	@Column(name = "puzzle_piece_drop_rate", precision = 12)
	public Double getPuzzlePieceDropRate();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.level</code>.
	 */
	public ITaskStageMonsterConfig setLevel(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.level</code>.
	 */
	@Column(name = "level", precision = 10)
	public Integer getLevel();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.chance_to_appear</code>.
	 */
	public ITaskStageMonsterConfig setChanceToAppear(Double value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	public Double getChanceToAppear();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.dmg_multiplier</code>.
	 */
	public ITaskStageMonsterConfig setDmgMultiplier(Double value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.dmg_multiplier</code>.
	 */
	@Column(name = "dmg_multiplier", precision = 12)
	public Double getDmgMultiplier();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.monster_id_drop</code>.
	 */
	public ITaskStageMonsterConfig setMonsterIdDrop(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.monster_id_drop</code>.
	 */
	@Column(name = "monster_id_drop", precision = 10)
	public Integer getMonsterIdDrop();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.monster_drop_lvl</code>. The level of the monster that is dropped, if a monster does drop. Level 0 means a piece is dropped, and anything higher than one is complete monster.
	 */
	public ITaskStageMonsterConfig setMonsterDropLvl(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.monster_drop_lvl</code>. The level of the monster that is dropped, if a monster does drop. Level 0 means a piece is dropped, and anything higher than one is complete monster.
	 */
	@Column(name = "monster_drop_lvl", precision = 10)
	public Integer getMonsterDropLvl();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.defensive_skill_id</code>.
	 */
	public ITaskStageMonsterConfig setDefensiveSkillId(Integer value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.defensive_skill_id</code>.
	 */
	@Column(name = "defensive_skill_id", precision = 10)
	public Integer getDefensiveSkillId();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.init_dialogue</code>.
	 */
	public ITaskStageMonsterConfig setInitDialogue(String value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.init_dialogue</code>.
	 */
	@Column(name = "init_dialogue", length = 65535)
	@Size(max = 65535)
	public String getInitDialogue();

	/**
	 * Setter for <code>mobsters.task_stage_monster_config.default_dialogue</code>.
	 */
	public ITaskStageMonsterConfig setDefaultDialogue(String value);

	/**
	 * Getter for <code>mobsters.task_stage_monster_config.default_dialogue</code>.
	 */
	@Column(name = "default_dialogue", length = 65535)
	@Size(max = 65535)
	public String getDefaultDialogue();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ITaskStageMonsterConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskStageMonsterConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ITaskStageMonsterConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskStageMonsterConfig> E into(E into);
}