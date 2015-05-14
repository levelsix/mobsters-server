/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

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
@Table(name = "battle_item_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id"})
})
public interface IBattleItemForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.battle_item_for_user.id</code>.
	 */
	public IBattleItemForUser setId(String value);

	/**
	 * Getter for <code>mobsters.battle_item_for_user.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.battle_item_for_user.user_id</code>.
	 */
	public IBattleItemForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.battle_item_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.battle_item_for_user.battle_item_id</code>.
	 */
	public IBattleItemForUser setBattleItemId(Integer value);

	/**
	 * Getter for <code>mobsters.battle_item_for_user.battle_item_id</code>.
	 */
	@Column(name = "battle_item_id", precision = 10)
	public Integer getBattleItemId();

	/**
	 * Setter for <code>mobsters.battle_item_for_user.quantity</code>.
	 */
	public IBattleItemForUser setQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.battle_item_for_user.quantity</code>.
	 */
	@Column(name = "quantity", precision = 10)
	public Integer getQuantity();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IBattleItemForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IBattleItemForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemForUser> E into(E into);
}
