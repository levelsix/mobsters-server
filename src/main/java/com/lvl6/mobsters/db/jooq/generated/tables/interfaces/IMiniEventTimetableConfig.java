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
@Table(name = "mini_event_timetable_config", schema = "mobsters")
public interface IMiniEventTimetableConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.mini_event_timetable_config.id</code>.
	 */
	public IMiniEventTimetableConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_timetable_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.mini_event_timetable_config.mini_event_id</code>.
	 */
	public IMiniEventTimetableConfig setMiniEventId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_timetable_config.mini_event_id</code>.
	 */
	@Column(name = "mini_event_id", precision = 10)
	public Integer getMiniEventId();

	/**
	 * Setter for <code>mobsters.mini_event_timetable_config.start_time</code>.
	 */
	public IMiniEventTimetableConfig setStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_event_timetable_config.start_time</code>.
	 */
	@Column(name = "start_time")
	public Timestamp getStartTime();

	/**
	 * Setter for <code>mobsters.mini_event_timetable_config.end_time</code>.
	 */
	public IMiniEventTimetableConfig setEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_event_timetable_config.end_time</code>.
	 */
	@Column(name = "end_time")
	public Timestamp getEndTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMiniEventTimetableConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventTimetableConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMiniEventTimetableConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventTimetableConfig> E into(E into);
}