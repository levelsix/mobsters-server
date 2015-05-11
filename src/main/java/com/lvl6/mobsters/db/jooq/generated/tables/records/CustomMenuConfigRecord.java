/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.CustomMenuConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICustomMenuConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Row;
import org.jooq.Row6;
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
@Table(name = "custom_menu_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"custom_menu_id", "position_z"})
})
public class CustomMenuConfigRecord extends UpdatableRecordImpl<CustomMenuConfigRecord> implements Record6<Integer, Integer, Integer, Integer, String, String>, ICustomMenuConfig {

	private static final long serialVersionUID = -432858226;

	/**
	 * Setter for <code>mobsters.custom_menu_config.custom_menu_id</code>.
	 */
	@Override
	public CustomMenuConfigRecord setCustomMenuId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.custom_menu_id</code>.
	 */
	@Column(name = "custom_menu_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCustomMenuId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.custom_menu_config.position_x</code>.
	 */
	@Override
	public CustomMenuConfigRecord setPositionX(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.position_x</code>.
	 */
	@Column(name = "position_x", precision = 10)
	@Override
	public Integer getPositionX() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.custom_menu_config.position_y</code>.
	 */
	@Override
	public CustomMenuConfigRecord setPositionY(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.position_y</code>.
	 */
	@Column(name = "position_y", precision = 10)
	@Override
	public Integer getPositionY() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.custom_menu_config.position_z</code>.
	 */
	@Override
	public CustomMenuConfigRecord setPositionZ(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.position_z</code>.
	 */
	@Column(name = "position_z", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getPositionZ() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.custom_menu_config.is_jiggle</code>.
	 */
	@Override
	public CustomMenuConfigRecord setIsJiggle(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.is_jiggle</code>.
	 */
	@Column(name = "is_jiggle", length = 45)
	@Size(max = 45)
	@Override
	public String getIsJiggle() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.custom_menu_config.image_name</code>.
	 */
	@Override
	public CustomMenuConfigRecord setImageName(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_menu_config.image_name</code>.
	 */
	@Column(name = "image_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImageName() {
		return (String) getValue(5);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<Integer, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, Integer, Integer, Integer, String, String> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, Integer, Integer, Integer, String, String> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.CUSTOM_MENU_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_X;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_Y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.POSITION_Z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.IS_JIGGLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return CustomMenuConfig.CUSTOM_MENU_CONFIG.IMAGE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getCustomMenuId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getPositionX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getPositionY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getPositionZ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getIsJiggle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getImageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value1(Integer value) {
		setCustomMenuId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value2(Integer value) {
		setPositionX(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value3(Integer value) {
		setPositionY(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value4(Integer value) {
		setPositionZ(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value5(String value) {
		setIsJiggle(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord value6(String value) {
		setImageName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomMenuConfigRecord values(Integer value1, Integer value2, Integer value3, Integer value4, String value5, String value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICustomMenuConfig from) {
		setCustomMenuId(from.getCustomMenuId());
		setPositionX(from.getPositionX());
		setPositionY(from.getPositionY());
		setPositionZ(from.getPositionZ());
		setIsJiggle(from.getIsJiggle());
		setImageName(from.getImageName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICustomMenuConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CustomMenuConfigRecord
	 */
	public CustomMenuConfigRecord() {
		super(CustomMenuConfig.CUSTOM_MENU_CONFIG);
	}

	/**
	 * Create a detached, initialised CustomMenuConfigRecord
	 */
	public CustomMenuConfigRecord(Integer customMenuId, Integer positionX, Integer positionY, Integer positionZ, String isJiggle, String imageName) {
		super(CustomMenuConfig.CUSTOM_MENU_CONFIG);

		setValue(0, customMenuId);
		setValue(1, positionX);
		setValue(2, positionY);
		setValue(3, positionZ);
		setValue(4, isJiggle);
		setValue(5, imageName);
	}
}