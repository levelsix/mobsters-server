/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterPackPurchaseHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "booster_pack_purchase_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "booster_pack_id", "time_of_purchase"})
})
public class BoosterPackPurchaseHistoryPojo implements IBoosterPackPurchaseHistory {

	private static final long serialVersionUID = -381483485;

	private String    userId;
	private Integer   boosterPackId;
	private Timestamp timeOfPurchase;
	private Integer   boosterItemId;
	private Integer   monsterId;
	private Byte      numPieces;
	private Boolean   isComplete;
	private Boolean   isSpecial;
	private Integer   gemReward;
	private Integer   cashReward;
	private Double    chanceToAppear;
	private String    changedMonsterForUserIds;

	public BoosterPackPurchaseHistoryPojo() {}

	public BoosterPackPurchaseHistoryPojo(BoosterPackPurchaseHistoryPojo value) {
		this.userId = value.userId;
		this.boosterPackId = value.boosterPackId;
		this.timeOfPurchase = value.timeOfPurchase;
		this.boosterItemId = value.boosterItemId;
		this.monsterId = value.monsterId;
		this.numPieces = value.numPieces;
		this.isComplete = value.isComplete;
		this.isSpecial = value.isSpecial;
		this.gemReward = value.gemReward;
		this.cashReward = value.cashReward;
		this.chanceToAppear = value.chanceToAppear;
		this.changedMonsterForUserIds = value.changedMonsterForUserIds;
	}

	public BoosterPackPurchaseHistoryPojo(
		String    userId,
		Integer   boosterPackId,
		Timestamp timeOfPurchase,
		Integer   boosterItemId,
		Integer   monsterId,
		Byte      numPieces,
		Boolean   isComplete,
		Boolean   isSpecial,
		Integer   gemReward,
		Integer   cashReward,
		Double    chanceToAppear,
		String    changedMonsterForUserIds
	) {
		this.userId = userId;
		this.boosterPackId = boosterPackId;
		this.timeOfPurchase = timeOfPurchase;
		this.boosterItemId = boosterItemId;
		this.monsterId = monsterId;
		this.numPieces = numPieces;
		this.isComplete = isComplete;
		this.isSpecial = isSpecial;
		this.gemReward = gemReward;
		this.cashReward = cashReward;
		this.chanceToAppear = chanceToAppear;
		this.changedMonsterForUserIds = changedMonsterForUserIds;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "booster_pack_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getBoosterPackId() {
		return this.boosterPackId;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setBoosterPackId(Integer boosterPackId) {
		this.boosterPackId = boosterPackId;
		return this;
	}

	@Column(name = "time_of_purchase", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfPurchase() {
		return this.timeOfPurchase;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setTimeOfPurchase(Timestamp timeOfPurchase) {
		this.timeOfPurchase = timeOfPurchase;
		return this;
	}

	@Column(name = "booster_item_id", precision = 10)
	@Override
	public Integer getBoosterItemId() {
		return this.boosterItemId;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setBoosterItemId(Integer boosterItemId) {
		this.boosterItemId = boosterItemId;
		return this;
	}

	@Column(name = "monster_id", precision = 7)
	@Override
	public Integer getMonsterId() {
		return this.monsterId;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
		return this;
	}

	@Column(name = "num_pieces", precision = 3)
	@Override
	public Byte getNumPieces() {
		return this.numPieces;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setNumPieces(Byte numPieces) {
		this.numPieces = numPieces;
		return this;
	}

	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return this.isComplete;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
		return this;
	}

	@Column(name = "is_special", precision = 1)
	@Override
	public Boolean getIsSpecial() {
		return this.isSpecial;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
		return this;
	}

	@Column(name = "gem_reward", precision = 7)
	@Override
	public Integer getGemReward() {
		return this.gemReward;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setGemReward(Integer gemReward) {
		this.gemReward = gemReward;
		return this;
	}

	@Column(name = "cash_reward", precision = 7)
	@Override
	public Integer getCashReward() {
		return this.cashReward;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setCashReward(Integer cashReward) {
		this.cashReward = cashReward;
		return this;
	}

	@Column(name = "chance_to_appear", precision = 12)
	@Override
	public Double getChanceToAppear() {
		return this.chanceToAppear;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setChanceToAppear(Double chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
		return this;
	}

	@Column(name = "changed_monster_for_user_ids", length = 511)
	@Size(max = 511)
	@Override
	public String getChangedMonsterForUserIds() {
		return this.changedMonsterForUserIds;
	}

	@Override
	public BoosterPackPurchaseHistoryPojo setChangedMonsterForUserIds(String changedMonsterForUserIds) {
		this.changedMonsterForUserIds = changedMonsterForUserIds;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBoosterPackPurchaseHistory from) {
		setUserId(from.getUserId());
		setBoosterPackId(from.getBoosterPackId());
		setTimeOfPurchase(from.getTimeOfPurchase());
		setBoosterItemId(from.getBoosterItemId());
		setMonsterId(from.getMonsterId());
		setNumPieces(from.getNumPieces());
		setIsComplete(from.getIsComplete());
		setIsSpecial(from.getIsSpecial());
		setGemReward(from.getGemReward());
		setCashReward(from.getCashReward());
		setChanceToAppear(from.getChanceToAppear());
		setChangedMonsterForUserIds(from.getChangedMonsterForUserIds());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoosterPackPurchaseHistory> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.BoosterPackPurchaseHistoryRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.BoosterPackPurchaseHistoryRecord();
		poop.from(this);
		return "BoosterPackPurchaseHistoryPojo[" + poop.valuesRow() + "]";
	}
}
