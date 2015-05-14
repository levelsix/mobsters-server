/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.EventPersistentConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IEventPersistentConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
@Table(name = "event_persistent_config", schema = "mobsters")
public class EventPersistentConfigRecord extends UpdatableRecordImpl<EventPersistentConfigRecord> implements Record8<Integer, String, UByte, UInteger, UInteger, UInteger, String, String>, IEventPersistentConfig {

	private static final long serialVersionUID = -1839504470;

	/**
	 * Setter for <code>mobsters.event_persistent_config.id</code>.
	 */
	@Override
	public EventPersistentConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.day_of_week</code>.
	 */
	@Override
	public EventPersistentConfigRecord setDayOfWeek(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.day_of_week</code>.
	 */
	@Column(name = "day_of_week", length = 45)
	@Size(max = 45)
	@Override
	public String getDayOfWeek() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.start_hour</code>.
	 */
	@Override
	public EventPersistentConfigRecord setStartHour(UByte value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.start_hour</code>.
	 */
	@Column(name = "start_hour", precision = 3)
	@Override
	public UByte getStartHour() {
		return (UByte) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.event_duration_minutes</code>.
	 */
	@Override
	public EventPersistentConfigRecord setEventDurationMinutes(UInteger value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.event_duration_minutes</code>.
	 */
	@Column(name = "event_duration_minutes", precision = 7)
	@Override
	public UInteger getEventDurationMinutes() {
		return (UInteger) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.task_id</code>. Refers to a task in task table
	 */
	@Override
	public EventPersistentConfigRecord setTaskId(UInteger value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.task_id</code>. Refers to a task in task table
	 */
	@Column(name = "task_id", precision = 10)
	@Override
	public UInteger getTaskId() {
		return (UInteger) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.cooldown_minutes</code>.
	 */
	@Override
	public EventPersistentConfigRecord setCooldownMinutes(UInteger value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.cooldown_minutes</code>.
	 */
	@Column(name = "cooldown_minutes", precision = 7)
	@Override
	public UInteger getCooldownMinutes() {
		return (UInteger) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.event_type</code>. what kind of event this is.
	 */
	@Override
	public EventPersistentConfigRecord setEventType(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.event_type</code>. what kind of event this is.
	 */
	@Column(name = "event_type", length = 100)
	@Size(max = 100)
	@Override
	public String getEventType() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.event_persistent_config.monster_element</code>.
	 */
	@Override
	public EventPersistentConfigRecord setMonsterElement(String value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.event_persistent_config.monster_element</code>.
	 */
	@Column(name = "monster_element", length = 75)
	@Size(max = 75)
	@Override
	public String getMonsterElement() {
		return (String) getValue(7);
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
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, String, UByte, UInteger, UInteger, UInteger, String, String> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, String, UByte, UInteger, UInteger, UInteger, String, String> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.DAY_OF_WEEK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UByte> field3() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.START_HOUR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field4() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.EVENT_DURATION_MINUTES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field5() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.TASK_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field6() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.COOLDOWN_MINUTES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.EVENT_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return EventPersistentConfig.EVENT_PERSISTENT_CONFIG.MONSTER_ELEMENT;
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
	public String value2() {
		return getDayOfWeek();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UByte value3() {
		return getStartHour();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value4() {
		return getEventDurationMinutes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value5() {
		return getTaskId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value6() {
		return getCooldownMinutes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getEventType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getMonsterElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value2(String value) {
		setDayOfWeek(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value3(UByte value) {
		setStartHour(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value4(UInteger value) {
		setEventDurationMinutes(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value5(UInteger value) {
		setTaskId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value6(UInteger value) {
		setCooldownMinutes(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value7(String value) {
		setEventType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord value8(String value) {
		setMonsterElement(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPersistentConfigRecord values(Integer value1, String value2, UByte value3, UInteger value4, UInteger value5, UInteger value6, String value7, String value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IEventPersistentConfig from) {
		setId(from.getId());
		setDayOfWeek(from.getDayOfWeek());
		setStartHour(from.getStartHour());
		setEventDurationMinutes(from.getEventDurationMinutes());
		setTaskId(from.getTaskId());
		setCooldownMinutes(from.getCooldownMinutes());
		setEventType(from.getEventType());
		setMonsterElement(from.getMonsterElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IEventPersistentConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached EventPersistentConfigRecord
	 */
	public EventPersistentConfigRecord() {
		super(EventPersistentConfig.EVENT_PERSISTENT_CONFIG);
	}

	/**
	 * Create a detached, initialised EventPersistentConfigRecord
	 */
	public EventPersistentConfigRecord(Integer id, String dayOfWeek, UByte startHour, UInteger eventDurationMinutes, UInteger taskId, UInteger cooldownMinutes, String eventType, String monsterElement) {
		super(EventPersistentConfig.EVENT_PERSISTENT_CONFIG);

		setValue(0, id);
		setValue(1, dayOfWeek);
		setValue(2, startHour);
		setValue(3, eventDurationMinutes);
		setValue(4, taskId);
		setValue(5, cooldownMinutes);
		setValue(6, eventType);
		setValue(7, monsterElement);
	}
}
