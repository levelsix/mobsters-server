/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.GiftConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftConfig;

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
@Table(name = "gift_config", schema = "mobsters")
public class GiftConfigRecord extends UpdatableRecordImpl<GiftConfigRecord> implements Record5<Integer, String, Integer, String, String>, IGiftConfig {

	private static final long serialVersionUID = -180098573;

	/**
	 * Setter for <code>mobsters.gift_config.id</code>.
	 */
	@Override
	public GiftConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.gift_config.name</code>.
	 */
	@Override
	public GiftConfigRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.gift_config.hours_until_expiration</code>.
	 */
	@Override
	public GiftConfigRecord setHoursUntilExpiration(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_config.hours_until_expiration</code>.
	 */
	@Column(name = "hours_until_expiration", precision = 10)
	@Override
	public Integer getHoursUntilExpiration() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.gift_config.image_name</code>.
	 */
	@Override
	public GiftConfigRecord setImageName(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_config.image_name</code>.
	 */
	@Column(name = "image_name", length = 75)
	@Size(max = 75)
	@Override
	public String getImageName() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.gift_config.gift_type</code>.
	 */
	@Override
	public GiftConfigRecord setGiftType(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_config.gift_type</code>.
	 */
	@Column(name = "gift_type", length = 75)
	@Size(max = 75)
	@Override
	public String getGiftType() {
		return (String) getValue(4);
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
	public Row5<Integer, String, Integer, String, String> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<Integer, String, Integer, String, String> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return GiftConfig.GIFT_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return GiftConfig.GIFT_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return GiftConfig.GIFT_CONFIG.HOURS_UNTIL_EXPIRATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return GiftConfig.GIFT_CONFIG.IMAGE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return GiftConfig.GIFT_CONFIG.GIFT_TYPE;
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
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getHoursUntilExpiration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getGiftType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord value3(Integer value) {
		setHoursUntilExpiration(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord value4(String value) {
		setImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord value5(String value) {
		setGiftType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftConfigRecord values(Integer value1, String value2, Integer value3, String value4, String value5) {
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
	public void from(IGiftConfig from) {
		setId(from.getId());
		setName(from.getName());
		setHoursUntilExpiration(from.getHoursUntilExpiration());
		setImageName(from.getImageName());
		setGiftType(from.getGiftType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IGiftConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached GiftConfigRecord
	 */
	public GiftConfigRecord() {
		super(GiftConfig.GIFT_CONFIG);
	}

	/**
	 * Create a detached, initialised GiftConfigRecord
	 */
	public GiftConfigRecord(Integer id, String name, Integer hoursUntilExpiration, String imageName, String giftType) {
		super(GiftConfig.GIFT_CONFIG);

		setValue(0, id);
		setValue(1, name);
		setValue(2, hoursUntilExpiration);
		setValue(3, imageName);
		setValue(4, giftType);
	}
}
