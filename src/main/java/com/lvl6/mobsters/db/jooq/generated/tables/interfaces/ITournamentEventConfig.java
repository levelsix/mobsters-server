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
@Table(name = "tournament_event_config", schema = "mobsters")
public interface ITournamentEventConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.tournament_event_config.id</code>.
	 */
	public ITournamentEventConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_event_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.tournament_event_config.start_time</code>.
	 */
	public ITournamentEventConfig setStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.tournament_event_config.start_time</code>.
	 */
	@Column(name = "start_time")
	public Timestamp getStartTime();

	/**
	 * Setter for <code>mobsters.tournament_event_config.end_time</code>.
	 */
	public ITournamentEventConfig setEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.tournament_event_config.end_time</code>.
	 */
	@Column(name = "end_time")
	public Timestamp getEndTime();

	/**
	 * Setter for <code>mobsters.tournament_event_config.event_name</code>.
	 */
	public ITournamentEventConfig setEventName(String value);

	/**
	 * Getter for <code>mobsters.tournament_event_config.event_name</code>.
	 */
	@Column(name = "event_name", length = 65535)
	@Size(max = 65535)
	public String getEventName();

	/**
	 * Setter for <code>mobsters.tournament_event_config.rewards_given_out</code>.
	 */
	public ITournamentEventConfig setRewardsGivenOut(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_event_config.rewards_given_out</code>.
	 */
	@Column(name = "rewards_given_out", precision = 10)
	public Integer getRewardsGivenOut();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ITournamentEventConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentEventConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ITournamentEventConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentEventConfig> E into(E into);
}
