/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentEventConfig;

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
public class TournamentEventConfigPojo implements ITournamentEventConfig {

	private static final long serialVersionUID = -1047376025;

	private Integer   id;
	private Timestamp startTime;
	private Timestamp endTime;
	private String    eventName;
	private Integer   rewardsGivenOut;

	public TournamentEventConfigPojo() {}

	public TournamentEventConfigPojo(TournamentEventConfigPojo value) {
		this.id = value.id;
		this.startTime = value.startTime;
		this.endTime = value.endTime;
		this.eventName = value.eventName;
		this.rewardsGivenOut = value.rewardsGivenOut;
	}

	public TournamentEventConfigPojo(
		Integer   id,
		Timestamp startTime,
		Timestamp endTime,
		String    eventName,
		Integer   rewardsGivenOut
	) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventName = eventName;
		this.rewardsGivenOut = rewardsGivenOut;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public TournamentEventConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return this.startTime;
	}

	@Override
	public TournamentEventConfigPojo setStartTime(Timestamp startTime) {
		this.startTime = startTime;
		return this;
	}

	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return this.endTime;
	}

	@Override
	public TournamentEventConfigPojo setEndTime(Timestamp endTime) {
		this.endTime = endTime;
		return this;
	}

	@Column(name = "event_name", length = 65535)
	@Size(max = 65535)
	@Override
	public String getEventName() {
		return this.eventName;
	}

	@Override
	public TournamentEventConfigPojo setEventName(String eventName) {
		this.eventName = eventName;
		return this;
	}

	@Column(name = "rewards_given_out", precision = 10)
	@Override
	public Integer getRewardsGivenOut() {
		return this.rewardsGivenOut;
	}

	@Override
	public TournamentEventConfigPojo setRewardsGivenOut(Integer rewardsGivenOut) {
		this.rewardsGivenOut = rewardsGivenOut;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITournamentEventConfig from) {
		setId(from.getId());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setEventName(from.getEventName());
		setRewardsGivenOut(from.getRewardsGivenOut());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITournamentEventConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.TournamentEventConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.TournamentEventConfigRecord();
		poop.from(this);
		return "TournamentEventConfigPojo[" + poop.valuesRow() + "]";
	}
}