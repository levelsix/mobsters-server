/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StaticLevelInfoConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStaticLevelInfoConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row2;
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
@Table(name = "static_level_info_config", schema = "mobsters")
public class StaticLevelInfoConfigRecord extends UpdatableRecordImpl<StaticLevelInfoConfigRecord> implements Record2<Integer, Integer>, IStaticLevelInfoConfig {

	private static final long serialVersionUID = -684079977;

	/**
	 * Setter for <code>mobsters.static_level_info_config.level_id</code>.
	 */
	@Override
	public StaticLevelInfoConfigRecord setLevelId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.static_level_info_config.level_id</code>.
	 */
	@Id
	@Column(name = "level_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getLevelId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.static_level_info_config.required_experience</code>.
	 */
	@Override
	public StaticLevelInfoConfigRecord setRequiredExperience(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.static_level_info_config.required_experience</code>.
	 */
	@Column(name = "required_experience", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getRequiredExperience() {
		return (Integer) getValue(1);
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
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<Integer, Integer> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<Integer, Integer> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return StaticLevelInfoConfig.STATIC_LEVEL_INFO_CONFIG.LEVEL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return StaticLevelInfoConfig.STATIC_LEVEL_INFO_CONFIG.REQUIRED_EXPERIENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getLevelId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getRequiredExperience();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StaticLevelInfoConfigRecord value1(Integer value) {
		setLevelId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StaticLevelInfoConfigRecord value2(Integer value) {
		setRequiredExperience(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StaticLevelInfoConfigRecord values(Integer value1, Integer value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStaticLevelInfoConfig from) {
		setLevelId(from.getLevelId());
		setRequiredExperience(from.getRequiredExperience());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStaticLevelInfoConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StaticLevelInfoConfigRecord
	 */
	public StaticLevelInfoConfigRecord() {
		super(StaticLevelInfoConfig.STATIC_LEVEL_INFO_CONFIG);
	}

	/**
	 * Create a detached, initialised StaticLevelInfoConfigRecord
	 */
	public StaticLevelInfoConfigRecord(Integer levelId, Integer requiredExperience) {
		super(StaticLevelInfoConfig.STATIC_LEVEL_INFO_CONFIG);

		setValue(0, levelId);
		setValue(1, requiredExperience);
	}
}