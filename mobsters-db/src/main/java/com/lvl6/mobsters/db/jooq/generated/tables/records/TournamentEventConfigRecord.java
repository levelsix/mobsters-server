/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.TournamentEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentEventConfig;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


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
public class TournamentEventConfigRecord extends UpdatableRecordImpl<TournamentEventConfigRecord> implements Record5<Integer, Timestamp, Timestamp, String, Integer>, ITournamentEventConfig {

	private static final long serialVersionUID = 1813885016;

	/**
	 * Setter for <code>mobsters.tournament_event_config.id</code>.
	 */
	@Override
	public TournamentEventConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.tournament_event_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.tournament_event_config.start_time</code>.
	 */
	@Override
	public TournamentEventConfigRecord setStartTime(Timestamp value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.tournament_event_config.start_time</code>.
	 */
	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return (Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.tournament_event_config.end_time</code>.
	 */
	@Override
	public TournamentEventConfigRecord setEndTime(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.tournament_event_config.end_time</code>.
	 */
	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.tournament_event_config.event_name</code>.
	 */
	@Override
	public TournamentEventConfigRecord setEventName(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.tournament_event_config.event_name</code>.
	 */
	@Column(name = "event_name", length = 65535)
	@Size(max = 65535)
	@Override
	public String getEventName() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.tournament_event_config.rewards_given_out</code>.
	 */
	@Override
	public TournamentEventConfigRecord setRewardsGivenOut(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.tournament_event_config.rewards_given_out</code>.
	 */
	@Column(name = "rewards_given_out", precision = 10)
	@Override
	public Integer getRewardsGivenOut() {
		return (Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<Integer, Timestamp, Timestamp, String, Integer> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<Integer, Timestamp, Timestamp, String, Integer> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return TournamentEventConfig.TOURNAMENT_EVENT_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field2() {
		return TournamentEventConfig.TOURNAMENT_EVENT_CONFIG.START_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return TournamentEventConfig.TOURNAMENT_EVENT_CONFIG.END_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return TournamentEventConfig.TOURNAMENT_EVENT_CONFIG.EVENT_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return TournamentEventConfig.TOURNAMENT_EVENT_CONFIG.REWARDS_GIVEN_OUT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value2() {
		return getStartTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getEndTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getEventName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getRewardsGivenOut();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord value2(Timestamp value) {
		setStartTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord value3(Timestamp value) {
		setEndTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord value4(String value) {
		setEventName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord value5(Integer value) {
		setRewardsGivenOut(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TournamentEventConfigRecord values(Integer value1, Timestamp value2, Timestamp value3, String value4, Integer value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TournamentEventConfigRecord
	 */
	public TournamentEventConfigRecord() {
		super(TournamentEventConfig.TOURNAMENT_EVENT_CONFIG);
	}

	/**
	 * Create a detached, initialised TournamentEventConfigRecord
	 */
	public TournamentEventConfigRecord(Integer id, Timestamp startTime, Timestamp endTime, String eventName, Integer rewardsGivenOut) {
		super(TournamentEventConfig.TOURNAMENT_EVENT_CONFIG);

		setValue(0, id);
		setValue(1, startTime);
		setValue(2, endTime);
		setValue(3, eventName);
		setValue(4, rewardsGivenOut);
	}
}
