/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResourceStorageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResourceStorageConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
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
@Table(name = "structure_resource_storage_config", schema = "mobsters")
public class StructureResourceStorageConfigRecord extends UpdatableRecordImpl<StructureResourceStorageConfigRecord> implements Record3<Integer, String, UInteger>, IStructureResourceStorageConfig {

	private static final long serialVersionUID = 118191110;

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.struct_id</code>.
	 */
	@Override
	public StructureResourceStorageConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.resource_type_stored</code>.
	 */
	@Override
	public StructureResourceStorageConfigRecord setResourceTypeStored(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.resource_type_stored</code>.
	 */
	@Column(name = "resource_type_stored", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getResourceTypeStored() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.capacity</code>.
	 */
	@Override
	public StructureResourceStorageConfigRecord setCapacity(UInteger value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.capacity</code>.
	 */
	@Column(name = "capacity", nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getCapacity() {
		return (UInteger) getValue(2);
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
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, String, UInteger> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, String, UInteger> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.RESOURCE_TYPE_STORED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field3() {
		return StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG.CAPACITY;
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
	public String value2() {
		return getResourceTypeStored();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value3() {
		return getCapacity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceStorageConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceStorageConfigRecord value2(String value) {
		setResourceTypeStored(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceStorageConfigRecord value3(UInteger value) {
		setCapacity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceStorageConfigRecord values(Integer value1, String value2, UInteger value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureResourceStorageConfig from) {
		setStructId(from.getStructId());
		setResourceTypeStored(from.getResourceTypeStored());
		setCapacity(from.getCapacity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureResourceStorageConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureResourceStorageConfigRecord
	 */
	public StructureResourceStorageConfigRecord() {
		super(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureResourceStorageConfigRecord
	 */
	public StructureResourceStorageConfigRecord(Integer structId, String resourceTypeStored, UInteger capacity) {
		super(StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG);

		setValue(0, structId);
		setValue(1, resourceTypeStored);
		setValue(2, capacity);
	}
}
