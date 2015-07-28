/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventConfig;

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
import org.jooq.Record7;
import org.jooq.Row;
import org.jooq.Row7;
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
@Table(name = "mini_event_config", schema = "mobsters")
public class MiniEventConfigRecord extends UpdatableRecordImpl<MiniEventConfigRecord> implements Record7<Integer, Timestamp, Timestamp, String, String, String, String>, IMiniEventConfig {

	private static final long serialVersionUID = -963701651;

	/**
	 * Setter for <code>mobsters.mini_event_config.id</code>.
	 */
	@Override
	public MiniEventConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.start_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Override
	public MiniEventConfigRecord setStartTime(Timestamp value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.start_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return (Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.end_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Override
	public MiniEventConfigRecord setEndTime(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.end_time</code>. DEPRECATED. Use mini_event_timetable_config
	 */
	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.name</code>.
	 */
	@Override
	public MiniEventConfigRecord setName(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.name</code>.
	 */
	@Column(name = "name", length = 75)
	@Size(max = 75)
	@Override
	public String getName() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.description</code>.
	 */
	@Override
	public MiniEventConfigRecord setDescription(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.description</code>.
	 */
	@Column(name = "description", length = 200)
	@Size(max = 200)
	@Override
	public String getDescription() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.img</code>.
	 */
	@Override
	public MiniEventConfigRecord setImg(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.img</code>.
	 */
	@Column(name = "img", length = 75)
	@Size(max = 75)
	@Override
	public String getImg() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.mini_event_config.icon</code>.
	 */
	@Override
	public MiniEventConfigRecord setIcon(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_config.icon</code>.
	 */
	@Column(name = "icon", length = 75)
	@Size(max = 75)
	@Override
	public String getIcon() {
		return (String) getValue(6);
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
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Integer, Timestamp, Timestamp, String, String, String, String> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Integer, Timestamp, Timestamp, String, String, String, String> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return MiniEventConfig.MINI_EVENT_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field2() {
		return MiniEventConfig.MINI_EVENT_CONFIG.START_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return MiniEventConfig.MINI_EVENT_CONFIG.END_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return MiniEventConfig.MINI_EVENT_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return MiniEventConfig.MINI_EVENT_CONFIG.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return MiniEventConfig.MINI_EVENT_CONFIG.IMG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return MiniEventConfig.MINI_EVENT_CONFIG.ICON;
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
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getImg();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getIcon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value2(Timestamp value) {
		setStartTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value3(Timestamp value) {
		setEndTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value4(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value5(String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value6(String value) {
		setImg(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord value7(String value) {
		setIcon(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventConfigRecord values(Integer value1, Timestamp value2, Timestamp value3, String value4, String value5, String value6, String value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventConfig from) {
		setId(from.getId());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setName(from.getName());
		setDescription(from.getDescription());
		setImg(from.getImg());
		setIcon(from.getIcon());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MiniEventConfigRecord
	 */
	public MiniEventConfigRecord() {
		super(MiniEventConfig.MINI_EVENT_CONFIG);
	}

	/**
	 * Create a detached, initialised MiniEventConfigRecord
	 */
	public MiniEventConfigRecord(Integer id, Timestamp startTime, Timestamp endTime, String name, String description, String img, String icon) {
		super(MiniEventConfig.MINI_EVENT_CONFIG);

		setValue(0, id);
		setValue(1, startTime);
		setValue(2, endTime);
		setValue(3, name);
		setValue(4, description);
		setValue(5, img);
		setValue(6, icon);
	}
}