/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILockBoxEventConfig;

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
import org.jooq.Record9;
import org.jooq.Row;
import org.jooq.Row9;
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
@Table(name = "lock_box_event_config", schema = "mobsters")
public class LockBoxEventConfigRecord extends UpdatableRecordImpl<LockBoxEventConfigRecord> implements Record9<Integer, Timestamp, Timestamp, String, String, Integer, String, String, String>, ILockBoxEventConfig {

	private static final long serialVersionUID = 2041353715;

	/**
	 * Setter for <code>mobsters.lock_box_event_config.id</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.start_time</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setStartTime(Timestamp value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.start_time</code>.
	 */
	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return (Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.end_time</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setEndTime(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.end_time</code>.
	 */
	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.lock_box_image_name</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setLockBoxImageName(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.lock_box_image_name</code>.
	 */
	@Column(name = "lock_box_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getLockBoxImageName() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.event_name</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setEventName(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.event_name</code>.
	 */
	@Column(name = "event_name", length = 100)
	@Size(max = 100)
	@Override
	public String getEventName() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.prize_equip_id</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setPrizeEquipId(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.prize_equip_id</code>.
	 */
	@Column(name = "prize_equip_id", precision = 10)
	@Override
	public Integer getPrizeEquipId() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.description_string</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setDescriptionString(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.description_string</code>.
	 */
	@Column(name = "description_string", length = 100)
	@Size(max = 100)
	@Override
	public String getDescriptionString() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.description_image_name</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setDescriptionImageName(String value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.description_image_name</code>.
	 */
	@Column(name = "description_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getDescriptionImageName() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.lock_box_event_config.tag_image_name</code>.
	 */
	@Override
	public LockBoxEventConfigRecord setTagImageName(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.lock_box_event_config.tag_image_name</code>.
	 */
	@Column(name = "tag_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getTagImageName() {
		return (String) getValue(8);
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
	// Record9 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row9<Integer, Timestamp, Timestamp, String, String, Integer, String, String, String> fieldsRow() {
		return (Row9) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row9<Integer, Timestamp, Timestamp, String, String, Integer, String, String, String> valuesRow() {
		return (Row9) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field2() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.START_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.END_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.LOCK_BOX_IMAGE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.EVENT_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.PRIZE_EQUIP_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.DESCRIPTION_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.DESCRIPTION_IMAGE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG.TAG_IMAGE_NAME;
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
		return getLockBoxImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getEventName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getPrizeEquipId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getDescriptionString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getDescriptionImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getTagImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value2(Timestamp value) {
		setStartTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value3(Timestamp value) {
		setEndTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value4(String value) {
		setLockBoxImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value5(String value) {
		setEventName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value6(Integer value) {
		setPrizeEquipId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value7(String value) {
		setDescriptionString(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value8(String value) {
		setDescriptionImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord value9(String value) {
		setTagImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LockBoxEventConfigRecord values(Integer value1, Timestamp value2, Timestamp value3, String value4, String value5, Integer value6, String value7, String value8, String value9) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ILockBoxEventConfig from) {
		setId(from.getId());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setLockBoxImageName(from.getLockBoxImageName());
		setEventName(from.getEventName());
		setPrizeEquipId(from.getPrizeEquipId());
		setDescriptionString(from.getDescriptionString());
		setDescriptionImageName(from.getDescriptionImageName());
		setTagImageName(from.getTagImageName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ILockBoxEventConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached LockBoxEventConfigRecord
	 */
	public LockBoxEventConfigRecord() {
		super(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG);
	}

	/**
	 * Create a detached, initialised LockBoxEventConfigRecord
	 */
	public LockBoxEventConfigRecord(Integer id, Timestamp startTime, Timestamp endTime, String lockBoxImageName, String eventName, Integer prizeEquipId, String descriptionString, String descriptionImageName, String tagImageName) {
		super(LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG);

		setValue(0, id);
		setValue(1, startTime);
		setValue(2, endTime);
		setValue(3, lockBoxImageName);
		setValue(4, eventName);
		setValue(5, prizeEquipId);
		setValue(6, descriptionString);
		setValue(7, descriptionImageName);
		setValue(8, tagImageName);
	}
}
