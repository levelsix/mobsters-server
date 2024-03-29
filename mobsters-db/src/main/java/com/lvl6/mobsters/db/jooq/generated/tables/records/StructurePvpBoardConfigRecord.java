/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructurePvpBoardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructurePvpBoardConfig;

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
@Table(name = "structure_pvp_board_config", schema = "mobsters")
public class StructurePvpBoardConfigRecord extends UpdatableRecordImpl<StructurePvpBoardConfigRecord> implements Record2<Integer, Integer>, IStructurePvpBoardConfig {

	private static final long serialVersionUID = -1895435384;

	/**
	 * Setter for <code>mobsters.structure_pvp_board_config.struct_id</code>.
	 */
	@Override
	public StructurePvpBoardConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_pvp_board_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_pvp_board_config.power_limit</code>.
	 */
	@Override
	public StructurePvpBoardConfigRecord setPowerLimit(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_pvp_board_config.power_limit</code>.
	 */
	@Column(name = "power_limit", precision = 10)
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
		return StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG.POWER_LIMIT;
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
	public StructurePvpBoardConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructurePvpBoardConfigRecord value2(Integer value) {
		setPowerLimit(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructurePvpBoardConfigRecord values(Integer value1, Integer value2) {
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
	public void from(IStructurePvpBoardConfig from) {
		setStructId(from.getStructId());
		setPowerLimit(from.getPowerLimit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructurePvpBoardConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructurePvpBoardConfigRecord
	 */
	public StructurePvpBoardConfigRecord() {
		super(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG);
	}

	/**
	 * Create a detached, initialised StructurePvpBoardConfigRecord
	 */
	public StructurePvpBoardConfigRecord(Integer structId, Integer powerLimit) {
		super(StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG);

		setValue(0, structId);
		setValue(1, powerLimit);
	}
}
