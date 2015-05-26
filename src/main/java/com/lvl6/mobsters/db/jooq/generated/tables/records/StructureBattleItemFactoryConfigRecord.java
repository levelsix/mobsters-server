/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureBattleItemFactoryConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureBattleItemFactoryConfig;

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
@Table(name = "structure_battle_item_factory_config", schema = "mobsters")
public class StructureBattleItemFactoryConfigRecord extends UpdatableRecordImpl<StructureBattleItemFactoryConfigRecord> implements Record2<Integer, Integer>, IStructureBattleItemFactoryConfig {

	private static final long serialVersionUID = -1580285894;

	/**
	 * Setter for <code>mobsters.structure_battle_item_factory_config.struct_id</code>.
	 */
	@Override
	public StructureBattleItemFactoryConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_battle_item_factory_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_battle_item_factory_config.power_limit</code>.
	 */
	@Override
	public StructureBattleItemFactoryConfigRecord setPowerLimit(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_battle_item_factory_config.power_limit</code>.
	 */
	@Column(name = "power_limit", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getPowerLimit() {
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
		return StructureBattleItemFactoryConfig.STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return StructureBattleItemFactoryConfig.STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG.POWER_LIMIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getStructId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getPowerLimit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureBattleItemFactoryConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureBattleItemFactoryConfigRecord value2(Integer value) {
		setPowerLimit(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureBattleItemFactoryConfigRecord values(Integer value1, Integer value2) {
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
	public void from(IStructureBattleItemFactoryConfig from) {
		setStructId(from.getStructId());
		setPowerLimit(from.getPowerLimit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureBattleItemFactoryConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureBattleItemFactoryConfigRecord
	 */
	public StructureBattleItemFactoryConfigRecord() {
		super(StructureBattleItemFactoryConfig.STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureBattleItemFactoryConfigRecord
	 */
	public StructureBattleItemFactoryConfigRecord(Integer structId, Integer powerLimit) {
		super(StructureBattleItemFactoryConfig.STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG);

		setValue(0, structId);
		setValue(1, powerLimit);
	}
}
