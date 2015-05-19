/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForUser;

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
@Table(name = "monster_for_user", schema = "mobsters")
public class MonsterForUser implements IMonsterForUser {

	private static final long serialVersionUID = -40075999;

	private String    id;
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
	private Boolean   hasAllPieces;
	private Boolean   restricted;
	private Integer   offensiveSkillId;
	private Integer   defensiveSkillId;

	public MonsterForUser() {}

	public MonsterForUser(MonsterForUser value) {
		this.id = value.id;
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
		this.hasAllPieces = value.hasAllPieces;
		this.restricted = value.restricted;
		this.offensiveSkillId = value.offensiveSkillId;
		this.defensiveSkillId = value.defensiveSkillId;
	}

	public MonsterForUser(
		String    id,
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
		Boolean   hasAllPieces,
		Boolean   restricted,
		Integer   offensiveSkillId,
		Integer   defensiveSkillId
	) {
		this.id = id;
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
		this.hasAllPieces = hasAllPieces;
		this.restricted = restricted;
		this.offensiveSkillId = offensiveSkillId;
		this.defensiveSkillId = defensiveSkillId;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public MonsterForUser setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public MonsterForUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "monster_id", precision = 10)
	@Override
	public Integer getMonsterId() {
		return this.monsterId;
	}

	@Override
	public MonsterForUser setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
		return this;
	}

	@Column(name = "current_experience", precision = 10)
	@Override
	public Integer getCurrentExperience() {
		return this.currentExperience;
	}

	@Override
	public MonsterForUser setCurrentExperience(Integer currentExperience) {
		this.currentExperience = currentExperience;
		return this;
	}

	@Column(name = "current_level", precision = 3)
	@Override
	public Byte getCurrentLevel() {
		return this.currentLevel;
	}

	@Override
	public MonsterForUser setCurrentLevel(Byte currentLevel) {
		this.currentLevel = currentLevel;
		return this;
	}

	@Column(name = "current_health", precision = 10)
	@Override
	public Integer getCurrentHealth() {
		return this.currentHealth;
	}

	@Override
	public MonsterForUser setCurrentHealth(Integer currentHealth) {
		this.currentHealth = currentHealth;
		return this;
	}

	@Column(name = "num_pieces", precision = 3)
	@Override
	public Byte getNumPieces() {
		return this.numPieces;
	}

	@Override
	public MonsterForUser setNumPieces(Byte numPieces) {
		this.numPieces = numPieces;
		return this;
	}

	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return this.isComplete;
	}

	@Override
	public MonsterForUser setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
		return this;
	}

	@Column(name = "combine_start_time")
	@Override
	public Timestamp getCombineStartTime() {
		return this.combineStartTime;
	}

	@Override
	public MonsterForUser setCombineStartTime(Timestamp combineStartTime) {
		this.combineStartTime = combineStartTime;
		return this;
	}

	@Column(name = "team_slot_num", precision = 3)
	@Override
	public Byte getTeamSlotNum() {
		return this.teamSlotNum;
	}

	@Override
	public MonsterForUser setTeamSlotNum(Byte teamSlotNum) {
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
	public MonsterForUser setSourceOfPieces(String sourceOfPieces) {
		this.sourceOfPieces = sourceOfPieces;
		return this;
	}

	@Column(name = "has_all_pieces", precision = 1)
	@Override
	public Boolean getHasAllPieces() {
		return this.hasAllPieces;
	}

	@Override
	public MonsterForUser setHasAllPieces(Boolean hasAllPieces) {
		this.hasAllPieces = hasAllPieces;
		return this;
	}

	@Column(name = "restricted", precision = 1)
	@Override
	public Boolean getRestricted() {
		return this.restricted;
	}

	@Override
	public MonsterForUser setRestricted(Boolean restricted) {
		this.restricted = restricted;
		return this;
	}

	@Column(name = "offensive_skill_id", precision = 10)
	@Override
	public Integer getOffensiveSkillId() {
		return this.offensiveSkillId;
	}

	@Override
	public MonsterForUser setOffensiveSkillId(Integer offensiveSkillId) {
		this.offensiveSkillId = offensiveSkillId;
		return this;
	}

	@Column(name = "defensive_skill_id", precision = 10)
	@Override
	public Integer getDefensiveSkillId() {
		return this.defensiveSkillId;
	}

	@Override
	public MonsterForUser setDefensiveSkillId(Integer defensiveSkillId) {
		this.defensiveSkillId = defensiveSkillId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterForUser from) {
		setId(from.getId());
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
		setHasAllPieces(from.getHasAllPieces());
		setRestricted(from.getRestricted());
		setOffensiveSkillId(from.getOffensiveSkillId());
		setDefensiveSkillId(from.getDefensiveSkillId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterForUser> E into(E into) {
		into.from(this);
		return into;
	}
}
