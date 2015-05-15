/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
public interface IBoosterPackPurchaseHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.user_id</code>.
	 */
	public IBoosterPackPurchaseHistory setUserId(String value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.booster_pack_id</code>.
	 */
	public IBoosterPackPurchaseHistory setBoosterPackId(Integer value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.booster_pack_id</code>.
	 */
	@Column(name = "booster_pack_id", nullable = false, precision = 10)
	@NotNull
	public Integer getBoosterPackId();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.time_of_purchase</code>.
	 */
	public IBoosterPackPurchaseHistory setTimeOfPurchase(Timestamp value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.time_of_purchase</code>.
	 */
	@Column(name = "time_of_purchase", nullable = false)
	@NotNull
	public Timestamp getTimeOfPurchase();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.booster_item_id</code>.
	 */
	public IBoosterPackPurchaseHistory setBoosterItemId(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.booster_item_id</code>.
	 */
	@Column(name = "booster_item_id", precision = 10)
	public UInteger getBoosterItemId();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.monster_id</code>.
	 */
	public IBoosterPackPurchaseHistory setMonsterId(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 7)
	public UInteger getMonsterId();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.num_pieces</code>. How many pieces of the monster the user gets if a monster is rewarded.
	 */
	public IBoosterPackPurchaseHistory setNumPieces(UByte value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.num_pieces</code>. How many pieces of the monster the user gets if a monster is rewarded.
	 */
	@Column(name = "num_pieces", precision = 3)
	public UByte getNumPieces();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.is_complete</code>. specifies if user can skip combine wait time and use monster after purchasing
	 */
	public IBoosterPackPurchaseHistory setIsComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.is_complete</code>. specifies if user can skip combine wait time and use monster after purchasing
	 */
	@Column(name = "is_complete", precision = 1)
	public Boolean getIsComplete();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.is_special</code>. client uses this to determine if it is displayed
	 */
	public IBoosterPackPurchaseHistory setIsSpecial(Boolean value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.is_special</code>. client uses this to determine if it is displayed
	 */
	@Column(name = "is_special", precision = 1)
	public Boolean getIsSpecial();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.gem_reward</code>.
	 */
	public IBoosterPackPurchaseHistory setGemReward(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 7)
	public UInteger getGemReward();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.cash_reward</code>. At the moment, unused
	 */
	public IBoosterPackPurchaseHistory setCashReward(UInteger value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.cash_reward</code>. At the moment, unused
	 */
	@Column(name = "cash_reward", precision = 7)
	public UInteger getCashReward();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.chance_to_appear</code>.
	 */
	public IBoosterPackPurchaseHistory setChanceToAppear(Double value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	public Double getChanceToAppear();

	/**
	 * Setter for <code>mobsters.booster_pack_purchase_history.changed_monster_for_user_ids</code>.
	 */
	public IBoosterPackPurchaseHistory setChangedMonsterForUserIds(String value);

	/**
	 * Getter for <code>mobsters.booster_pack_purchase_history.changed_monster_for_user_ids</code>.
	 */
	@Column(name = "changed_monster_for_user_ids", length = 511)
	@Size(max = 511)
	public String getChangedMonsterForUserIds();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IBoosterPackPurchaseHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterPackPurchaseHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IBoosterPackPurchaseHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterPackPurchaseHistory> E into(E into);
}
