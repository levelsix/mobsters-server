/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForUserDeleted;

import java.sql.Timestamp;

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
@Table(name = "monster_for_user_deleted", schema = "mobsters")
public class MonsterForUserDeleted implements IMonsterForUserDeleted {

	private static final long serialVersionUID = -47852824;

	private String    monsterForUserId;
	private String    userId;
	private Integer   monsterId;
	private Integer   currentExperience;
	private Byte      currentLevel;
	private Integer   currentHealth;
	private Byte      numPieces;
	private Boolean   isComplete;
	private Timestamp combineStartTime;
	private Byte      teamSlotNum;
	private String    sourceOfPieces;
	private String    deletedReason;
	private String    details;
	private Timestamp deletedTime;

	public MonsterForUserDeleted() {}

	public MonsterForUserDeleted(MonsterForUserDeleted value) {
		this.monsterForUserId = value.monsterForUserId;
		this.userId = value.userId;
		this.monsterId = value.monsterId;
		this.currentExperience = value.currentExperience;
		this.currentLevel = value.currentLevel;
		this.currentHealth = value.currentHealth;
		this.numPieces = value.numPieces;
		this.isComplete = value.isComplete;
		this.combineStartTime = value.combineStartTime;
		this.teamSlotNum = value.teamSlotNum;
		this.sourceOfPieces = value.sourceOfPieces;
		this.deletedReason = value.deletedReason;
		this.details = value.details;
		this.deletedTime = value.deletedTime;
	}

	public MonsterForUserDeleted(
		String    monsterForUserId,
		String    userId,
		Integer   monsterId,
		Integer   currentExperience,
		Byte      currentLevel,
		Integer   currentHealth,
		Byte      numPieces,
		Boolean   isComplete,
		Timestamp combineStartTime,
		Byte      teamSlotNum,
		String    sourceOfPieces,
		String    deletedReason,
		String    details,
		Timestamp deletedTime
	) {
		this.monsterForUserId = monsterForUserId;
		this.userId = userId;
		this.monsterId = monsterId;
		this.currentExperience = currentExperience;
		this.currentLevel = currentLevel;
		this.currentHealth = currentHealth;
		this.numPieces = numPieces;
		this.isComplete = isComplete;
		this.combineStartTime = combineStartTime;
		this.teamSlotNum = teamSlotNum;
		this.sourceOfPieces = sourceOfPieces;
		this.deletedReason = deletedReason;
		this.details = details;
		this.deletedTime = deletedTime;
	}

	@Id
	@Column(name = "monster_for_user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getMonsterForUserId() {
		return this.monsterForUserId;
	}

	@Override
	public MonsterForUserDeleted setMonsterForUserId(String monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
		return this;
	}

	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public MonsterForUserDeleted setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "monster_id", precision = 10)
	@Override
	public Integer getMonsterId() {
		return this.monsterId;
	}

	@Override
	public MonsterForUserDeleted setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
		return this;
	}

	@Column(name = "current_experience", precision = 10)
	@Override
	public Integer getCurrentExperience() {
		return this.currentExperience;
	}

	@Override
	public MonsterForUserDeleted setCurrentExperience(Integer currentExperience) {
		this.currentExperience = currentExperience;
		return this;
	}

	@Column(name = "current_level", precision = 3)
	@Override
	public Byte getCurrentLevel() {
		return this.currentLevel;
	}

	@Override
	public MonsterForUserDeleted setCurrentLevel(Byte currentLevel) {
		this.currentLevel = currentLevel;
		return this;
	}

	@Column(name = "current_health", precision = 10)
	@Override
	public Integer getCurrentHealth() {
		return this.currentHealth;
	}

	@Override
	public MonsterForUserDeleted setCurrentHealth(Integer currentHealth) {
		this.currentHealth = currentHealth;
		return this;
	}

	@Column(name = "num_pieces", precision = 3)
	@Override
	public Byte getNumPieces() {
		return this.numPieces;
	}

	@Override
	public MonsterForUserDeleted setNumPieces(Byte numPieces) {
		this.numPieces = numPieces;
		return this;
	}

	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return this.isComplete;
	}

	@Override
	public MonsterForUserDeleted setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
		return this;
	}

	@Column(name = "combine_start_time")
	@Override
	public Timestamp getCombineStartTime() {
		return this.combineStartTime;
	}

	@Override
	public MonsterForUserDeleted setCombineStartTime(Timestamp combineStartTime) {
		this.combineStartTime = combineStartTime;
		return this;
	}

	@Column(name = "team_slot_num", precision = 3)
	@Override
	public Byte getTeamSlotNum() {
		return this.teamSlotNum;
	}

	@Override
	public MonsterForUserDeleted setTeamSlotNum(Byte teamSlotNum) {
		this.teamSlotNum = teamSlotNum;
		return this;
	}

	@Column(name = "source_of_pieces", length = 65535)
	@Size(max = 65535)
	@Override
	public String getSourceOfPieces() {
		return this.sourceOfPieces;
	}

	@Override
	public MonsterForUserDeleted setSourceOfPieces(String sourceOfPieces) {
		this.sourceOfPieces = sourceOfPieces;
		return this;
	}

	@Column(name = "deleted_reason", length = 45)
	@Size(max = 45)
	@Override
	public String getDeletedReason() {
		return this.deletedReason;
	}

	@Override
	public MonsterForUserDeleted setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
		return this;
	}

	@Column(name = "details", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDetails() {
		return this.details;
	}

	@Override
	public MonsterForUserDeleted setDetails(String details) {
		this.details = details;
		return this;
	}

	@Column(name = "deleted_time")
	@Override
	public Timestamp getDeletedTime() {
		return this.deletedTime;
	}

	@Override
	public MonsterForUserDeleted setDeletedTime(Timestamp deletedTime) {
		this.deletedTime = deletedTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterForUserDeleted from) {
		setMonsterForUserId(from.getMonsterForUserId());
		setUserId(from.getUserId());
		setMonsterId(from.getMonsterId());
		setCurrentExperience(from.getCurrentExperience());
		setCurrentLevel(from.getCurrentLevel());
		setCurrentHealth(from.getCurrentHealth());
		setNumPieces(from.getNumPieces());
		setIsComplete(from.getIsComplete());
		setCombineStartTime(from.getCombineStartTime());
		setTeamSlotNum(from.getTeamSlotNum());
		setSourceOfPieces(from.getSourceOfPieces());
		setDeletedReason(from.getDeletedReason());
		setDetails(from.getDetails());
		setDeletedTime(from.getDeletedTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterForUserDeleted> E into(E into) {
		into.from(this);
		return into;
	}
}
