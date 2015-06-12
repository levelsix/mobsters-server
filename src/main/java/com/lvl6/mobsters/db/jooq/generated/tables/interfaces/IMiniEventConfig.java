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
@Table(name = "mini_event_config", schema = "mobsters")
public interface IMiniEventConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.mini_event_config.id</code>.
	 */
	public IMiniEventConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_event_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.mini_event_config.start_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	public IMiniEventConfig setStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_event_config.start_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Column(name = "start_time")
	public Timestamp getStartTime();

	/**
	 * Setter for <code>mobsters.mini_event_config.end_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	public IMiniEventConfig setEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.mini_event_config.end_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Column(name = "end_time")
	public Timestamp getEndTime();

	/**
	 * Setter for <code>mobsters.mini_event_config.name</code>.
	 */
	public IMiniEventConfig setName(String value);

	/**
	 * Getter for <code>mobsters.mini_event_config.name</code>.
	 */
	@Column(name = "name", length = 75)
	@Size(max = 75)
	public String getName();

	/**
	 * Setter for <code>mobsters.mini_event_config.description</code>.
	 */
	public IMiniEventConfig setDescription(String value);

	/**
	 * Getter for <code>mobsters.mini_event_config.description</code>.
	 */
	@Column(name = "description", length = 200)
	@Size(max = 200)
	public String getDescription();

	/**
	 * Setter for <code>mobsters.mini_event_config.img</code>.
	 */
	public IMiniEventConfig setImg(String value);

	/**
	 * Getter for <code>mobsters.mini_event_config.img</code>.
	 */
	@Column(name = "img", length = 75)
	@Size(max = 75)
	public String getImg();

	/**
	 * Setter for <code>mobsters.mini_event_config.icon</code>.
	 */
	public IMiniEventConfig setIcon(String value);

	/**
	 * Getter for <code>mobsters.mini_event_config.icon</code>.
	 */
	@Column(name = "icon", length = 75)
	@Size(max = 75)
	public String getIcon();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMiniEventConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMiniEventConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventConfig> E into(E into);
}
