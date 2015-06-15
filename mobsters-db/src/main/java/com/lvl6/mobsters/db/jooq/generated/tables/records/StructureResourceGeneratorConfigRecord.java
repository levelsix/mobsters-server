/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResourceGeneratorConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResourceGeneratorConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
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
@Table(name = "structure_resource_generator_config", schema = "mobsters")
public class StructureResourceGeneratorConfigRecord extends UpdatableRecordImpl<StructureResourceGeneratorConfigRecord> implements Record4<Integer, String, Double, Integer>, IStructureResourceGeneratorConfig {

	private static final long serialVersionUID = -2048757291;

	/**
	 * Setter for <code>mobsters.structure_resource_generator_config.struct_id</code>. Id in structure table
	 */
	@Override
	public StructureResourceGeneratorConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_generator_config.struct_id</code>. Id in structure table
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_resource_generator_config.resource_type_generated</code>.
	 */
	@Override
	public StructureResourceGeneratorConfigRecord setResourceTypeGenerated(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_generator_config.resource_type_generated</code>.
	 */
	@Column(name = "resource_type_generated", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getResourceTypeGenerated() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.structure_resource_generator_config.production_rate</code>. at the moment, some amount per hour
	 */
	@Override
	public StructureResourceGeneratorConfigRecord setProductionRate(Double value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_generator_config.production_rate</code>. at the moment, some amount per hour
	 */
	@Column(name = "production_rate", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getProductionRate() {
		return (Double) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.structure_resource_generator_config.capacity</code>.
	 */
	@Override
	public StructureResourceGeneratorConfigRecord setCapacity(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_resource_generator_config.capacity</code>.
	 */
	@Column(name = "capacity", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCapacity() {
		return (Integer) getValue(3);
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
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, Double, Integer> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, String, Double, Integer> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG.RESOURCE_TYPE_GENERATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field3() {
		return StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG.PRODUCTION_RATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG.CAPACITY;
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
		return getResourceTypeGenerated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value3() {
		return getProductionRate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getCapacity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceGeneratorConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceGeneratorConfigRecord value2(String value) {
		setResourceTypeGenerated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceGeneratorConfigRecord value3(Double value) {
		setProductionRate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceGeneratorConfigRecord value4(Integer value) {
		setCapacity(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResourceGeneratorConfigRecord values(Integer value1, String value2, Double value3, Integer value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureResourceGeneratorConfig from) {
		setStructId(from.getStructId());
		setResourceTypeGenerated(from.getResourceTypeGenerated());
		setProductionRate(from.getProductionRate());
		setCapacity(from.getCapacity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureResourceGeneratorConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureResourceGeneratorConfigRecord
	 */
	public StructureResourceGeneratorConfigRecord() {
		super(StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureResourceGeneratorConfigRecord
	 */
	public StructureResourceGeneratorConfigRecord(Integer structId, String resourceTypeGenerated, Double productionRate, Integer capacity) {
		super(StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG);

		setValue(0, structId);
		setValue(1, resourceTypeGenerated);
		setValue(2, productionRate);
		setValue(3, capacity);
	}
}