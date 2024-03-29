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
@Table(name = "item_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "item_id"})
})
public interface IItemForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.item_for_user.user_id</code>.
	 */
	public IItemForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.item_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.item_for_user.item_id</code>.
	 */
	public IItemForUser setItemId(Integer value);

	/**
	 * Getter for <code>mobsters.item_for_user.item_id</code>.
	 */
	@Column(name = "item_id", nullable = false, precision = 10)
	@NotNull
	public Integer getItemId();

	/**
	 * Setter for <code>mobsters.item_for_user.quantity</code>.
	 */
	public IItemForUser setQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.item_for_user.quantity</code>.
	 */
	@Column(name = "quantity", precision = 10)
	public Integer getQuantity();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IItemForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IItemForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IItemForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IItemForUser> E into(E into);
}
