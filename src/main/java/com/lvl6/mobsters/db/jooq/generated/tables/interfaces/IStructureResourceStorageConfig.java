/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public interface IStructureResourceStorageConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.struct_id</code>.
	 */
	public IStructureResourceStorageConfig setStructId(Integer value);

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getStructId();

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.resource_type_stored</code>.
	 */
	public IStructureResourceStorageConfig setResourceTypeStored(String value);

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.resource_type_stored</code>.
	 */
	@Column(name = "resource_type_stored", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	public String getResourceTypeStored();

	/**
	 * Setter for <code>mobsters.structure_resource_storage_config.capacity</code>.
	 */
	public IStructureResourceStorageConfig setCapacity(UInteger value);

	/**
	 * Getter for <code>mobsters.structure_resource_storage_config.capacity</code>.
	 */
	@Column(name = "capacity", nullable = false, precision = 10)
	@NotNull
	public UInteger getCapacity();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IStructureResourceStorageConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResourceStorageConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IStructureResourceStorageConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResourceStorageConfig> E into(E into);
}
