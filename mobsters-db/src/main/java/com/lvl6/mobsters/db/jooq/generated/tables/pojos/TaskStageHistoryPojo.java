/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskStageHistory;

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
@Table(name = "task_stage_history", schema = "mobsters")
public class TaskStageHistoryPojo implements ITaskStageHistory {

	private static final long serialVersionUID = -1990610227;

	private String  taskStageForUserId;
	private String  taskForUserId;
	private Integer stageNum;
	private Integer taskStageMonsterId;
	private String  monsterType;
	private Integer expGained;
	private Integer cashGained;
	private Integer oilGained;
	private Boolean monsterPieceDropped;
	private Integer itemIdDropped;
	private Integer monsterIdDropped;
	private Integer monsterDroppedLvl;
	private Boolean attackedFirst;
	private Integer monsterLvl;

	public TaskStageHistoryPojo() {}

	public TaskStageHistoryPojo(TaskStageHistoryPojo value) {
		this.taskStageForUserId = value.taskStageForUserId;
		this.taskForUserId = value.taskForUserId;
		this.stageNum = value.stageNum;
		this.taskStageMonsterId = value.taskStageMonsterId;
		this.monsterType = value.monsterType;
		this.expGained = value.expGained;
		this.cashGained = value.cashGained;
		this.oilGained = value.oilGained;
		this.monsterPieceDropped = value.monsterPieceDropped;
		this.itemIdDropped = value.itemIdDropped;
		this.monsterIdDropped = value.monsterIdDropped;
		this.monsterDroppedLvl = value.monsterDroppedLvl;
		this.attackedFirst = value.attackedFirst;
		this.monsterLvl = value.monsterLvl;
	}

	public TaskStageHistoryPojo(
		String  taskStageForUserId,
		String  taskForUserId,
		Integer stageNum,
		Integer taskStageMonsterId,
		String  monsterType,
		Integer expGained,
		Integer cashGained,
		Integer oilGained,
		Boolean monsterPieceDropped,
		Integer itemIdDropped,
		Integer monsterIdDropped,
		Integer monsterDroppedLvl,
		Boolean attackedFirst,
		Integer monsterLvl
	) {
		this.taskStageForUserId = taskStageForUserId;
		this.taskForUserId = taskForUserId;
		this.stageNum = stageNum;
		this.taskStageMonsterId = taskStageMonsterId;
		this.monsterType = monsterType;
		this.expGained = expGained;
		this.cashGained = cashGained;
		this.oilGained = oilGained;
		this.monsterPieceDropped = monsterPieceDropped;
		this.itemIdDropped = itemIdDropped;
		this.monsterIdDropped = monsterIdDropped;
		this.monsterDroppedLvl = monsterDroppedLvl;
		this.attackedFirst = attackedFirst;
		this.monsterLvl = monsterLvl;
	}

	@Id
	@Column(name = "task_stage_for_user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getTaskStageForUserId() {
		return this.taskStageForUserId;
	}

	@Override
	public TaskStageHistoryPojo setTaskStageForUserId(String taskStageForUserId) {
		this.taskStageForUserId = taskStageForUserId;
		return this;
	}

	@Column(name = "task_for_user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getTaskForUserId() {
		return this.taskForUserId;
	}

	@Override
	public TaskStageHistoryPojo setTaskForUserId(String taskForUserId) {
		this.taskForUserId = taskForUserId;
		return this;
	}

	@Column(name = "stage_num", precision = 10)
	@Override
	public Integer getStageNum() {
		return this.stageNum;
	}

	@Override
	public TaskStageHistoryPojo setStageNum(Integer stageNum) {
		this.stageNum = stageNum;
		return this;
	}

	@Column(name = "task_stage_monster_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getTaskStageMonsterId() {
		return this.taskStageMonsterId;
	}

	@Override
	public TaskStageHistoryPojo setTaskStageMonsterId(Integer taskStageMonsterId) {
		this.taskStageMonsterId = taskStageMonsterId;
		return this;
	}

	@Column(name = "monster_type", length = 45)
	@Size(max = 45)
	@Override
	public String getMonsterType() {
		return this.monsterType;
	}

	@Override
	public TaskStageHistoryPojo setMonsterType(String monsterType) {
		this.monsterType = monsterType;
		return this;
	}

	@Column(name = "exp_gained", precision = 10)
	@Override
	public Integer getExpGained() {
		return this.expGained;
	}

	@Override
	public TaskStageHistoryPojo setExpGained(Integer expGained) {
		this.expGained = expGained;
		return this;
	}

	@Column(name = "cash_gained", precision = 10)
	@Override
	public Integer getCashGained() {
		return this.cashGained;
	}

	@Override
	public TaskStageHistoryPojo setCashGained(Integer cashGained) {
		this.cashGained = cashGained;
		return this;
	}

	@Column(name = "oil_gained", precision = 10)
	@Override
	public Integer getOilGained() {
		return this.oilGained;
	}

	@Override
	public TaskStageHistoryPojo setOilGained(Integer oilGained) {
		this.oilGained = oilGained;
		return this;
	}

	@Column(name = "monster_piece_dropped", precision = 1)
	@Override
	public Boolean getMonsterPieceDropped() {
		return this.monsterPieceDropped;
	}

	@Override
	public TaskStageHistoryPojo setMonsterPieceDropped(Boolean monsterPieceDropped) {
		this.monsterPieceDropped = monsterPieceDropped;
		return this;
	}

	@Column(name = "item_id_dropped", precision = 10)
	@Override
	public Integer getItemIdDropped() {
		return this.itemIdDropped;
	}

	@Override
	public TaskStageHistoryPojo setItemIdDropped(Integer itemIdDropped) {
		this.itemIdDropped = itemIdDropped;
		return this;
	}

	@Column(name = "monster_id_dropped", precision = 10)
	@Override
	public Integer getMonsterIdDropped() {
		return this.monsterIdDropped;
	}

	@Override
	public TaskStageHistoryPojo setMonsterIdDropped(Integer monsterIdDropped) {
		this.monsterIdDropped = monsterIdDropped;
		return this;
	}

	@Column(name = "monster_dropped_lvl", precision = 10)
	@Override
	public Integer getMonsterDroppedLvl() {
		return this.monsterDroppedLvl;
	}

	@Override
	public TaskStageHistoryPojo setMonsterDroppedLvl(Integer monsterDroppedLvl) {
		this.monsterDroppedLvl = monsterDroppedLvl;
		return this;
	}

	@Column(name = "attacked_first", precision = 1)
	@Override
	public Boolean getAttackedFirst() {
		return this.attackedFirst;
	}

	@Override
	public TaskStageHistoryPojo setAttackedFirst(Boolean attackedFirst) {
		this.attackedFirst = attackedFirst;
		return this;
	}

	@Column(name = "monster_lvl", precision = 10)
	@Override
	public Integer getMonsterLvl() {
		return this.monsterLvl;
	}

	@Override
	public TaskStageHistoryPojo setMonsterLvl(Integer monsterLvl) {
		this.monsterLvl = monsterLvl;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskStageHistory from) {
		setTaskStageForUserId(from.getTaskStageForUserId());
		setTaskForUserId(from.getTaskForUserId());
		setStageNum(from.getStageNum());
		setTaskStageMonsterId(from.getTaskStageMonsterId());
		setMonsterType(from.getMonsterType());
		setExpGained(from.getExpGained());
		setCashGained(from.getCashGained());
		setOilGained(from.getOilGained());
		setMonsterPieceDropped(from.getMonsterPieceDropped());
		setItemIdDropped(from.getItemIdDropped());
		setMonsterIdDropped(from.getMonsterIdDropped());
		setMonsterDroppedLvl(from.getMonsterDroppedLvl());
		setAttackedFirst(from.getAttackedFirst());
		setMonsterLvl(from.getMonsterLvl());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskStageHistory> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.TaskStageHistoryRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.TaskStageHistoryRecord();
		poop.from(this);
		return "TaskStageHistoryPojo[" + poop.valuesRow() + "]";
	}
}
