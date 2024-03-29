/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
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
@Table(name = "load_testing_events", schema = "mobsters")
public interface ILoadTestingEvents extends Serializable {

	/**
	 * Setter for <code>mobsters.load_testing_events.id</code>.
	 */
	public ILoadTestingEvents setId(Integer value);

	/**
	 * Getter for <code>mobsters.load_testing_events.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.load_testing_events.user_id</code>.
	 */
	public ILoadTestingEvents setUserId(String value);

	/**
	 * Getter for <code>mobsters.load_testing_events.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.load_testing_events.log_time</code>.
	 */
	public ILoadTestingEvents setLogTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.load_testing_events.log_time</code>.
	 */
	@Column(name = "log_time", nullable = false)
	@NotNull
	public Timestamp getLogTime();

	/**
	 * Setter for <code>mobsters.load_testing_events.event_type</code>.
	 */
	public ILoadTestingEvents setEventType(Integer value);

	/**
	 * Getter for <code>mobsters.load_testing_events.event_type</code>.
	 */
	@Column(name = "event_type", nullable = false, precision = 10)
	@NotNull
	public Integer getEventType();

	/**
	 * Setter for <code>mobsters.load_testing_events.event_bytes</code>.
	 */
	public ILoadTestingEvents setEventBytes(byte[] value);

	/**
	 * Getter for <code>mobsters.load_testing_events.event_bytes</code>.
	 */
	@Column(name = "event_bytes", nullable = false, length = 65535)
	@NotNull
	public byte[] getEventBytes();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ILoadTestingEvents
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILoadTestingEvents from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ILoadTestingEvents
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILoadTestingEvents> E into(E into);
}
