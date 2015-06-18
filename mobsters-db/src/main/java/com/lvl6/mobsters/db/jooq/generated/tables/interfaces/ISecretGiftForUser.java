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
@Table(name = "secret_gift_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id", "reward_id"})
})
public interface ISecretGiftForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.secret_gift_for_user.id</code>.
	 */
	public ISecretGiftForUser setId(String value);

	/**
	 * Getter for <code>mobsters.secret_gift_for_user.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.secret_gift_for_user.user_id</code>.
	 */
	public ISecretGiftForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.secret_gift_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.secret_gift_for_user.reward_id</code>.
	 */
	public ISecretGiftForUser setRewardId(Integer value);

	/**
	 * Getter for <code>mobsters.secret_gift_for_user.reward_id</code>.
	 */
	@Column(name = "reward_id", nullable = false, precision = 10)
	@NotNull
	public Integer getRewardId();

	/**
	 * Setter for <code>mobsters.secret_gift_for_user.secs_until_collection</code>.
	 */
	public ISecretGiftForUser setSecsUntilCollection(Integer value);

	/**
	 * Getter for <code>mobsters.secret_gift_for_user.secs_until_collection</code>.
	 */
	@Column(name = "secs_until_collection", precision = 10)
	public Integer getSecsUntilCollection();

	/**
	 * Setter for <code>mobsters.secret_gift_for_user.create_time</code>.
	 */
	public ISecretGiftForUser setCreateTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.secret_gift_for_user.create_time</code>.
	 */
	@Column(name = "create_time")
	public Timestamp getCreateTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ISecretGiftForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISecretGiftForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ISecretGiftForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISecretGiftForUser> E into(E into);
}
