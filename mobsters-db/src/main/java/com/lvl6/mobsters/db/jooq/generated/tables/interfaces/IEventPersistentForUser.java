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
@Table(name = "event_persistent_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "event_persistent_id"})
})
public interface IEventPersistentForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.event_persistent_for_user.user_id</code>.
	 */
	public IEventPersistentForUser setUserId(String value);

	/**
	 * Getter for <code>mobsters.event_persistent_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.event_persistent_for_user.event_persistent_id</code>.
	 */
	public IEventPersistentForUser setEventPersistentId(Integer value);

	/**
	 * Getter for <code>mobsters.event_persistent_for_user.event_persistent_id</code>.
	 */
	@Column(name = "event_persistent_id", nullable = false, precision = 10)
	@NotNull
	public Integer getEventPersistentId();

	/**
	 * Setter for <code>mobsters.event_persistent_for_user.time_of_entry</code>.
	 */
	public IEventPersistentForUser setTimeOfEntry(Timestamp value);

	/**
	 * Getter for <code>mobsters.event_persistent_for_user.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry")
	public Timestamp getTimeOfEntry();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IEventPersistentForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IEventPersistentForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IEventPersistentForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IEventPersistentForUser> E into(E into);
}
