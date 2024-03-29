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
@Table(name = "structure_money_tree_config", schema = "mobsters")
public interface IStructureMoneyTreeConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.struct_id</code>.
	 */
	public IStructureMoneyTreeConfig setStructId(Integer value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getStructId();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.production_rate</code>.
	 */
	public IStructureMoneyTreeConfig setProductionRate(Double value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.production_rate</code>.
	 */
	@Column(name = "production_rate", nullable = false, precision = 12)
	@NotNull
	public Double getProductionRate();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.capacity</code>.
	 */
	public IStructureMoneyTreeConfig setCapacity(Integer value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.capacity</code>.
	 */
	@Column(name = "capacity", nullable = false, precision = 10)
	@NotNull
	public Integer getCapacity();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.days_of_duration</code>.
	 */
	public IStructureMoneyTreeConfig setDaysOfDuration(Integer value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.days_of_duration</code>.
	 */
	@Column(name = "days_of_duration", precision = 10)
	public Integer getDaysOfDuration();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.days_for_renewal</code>.
	 */
	public IStructureMoneyTreeConfig setDaysForRenewal(Integer value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.days_for_renewal</code>.
	 */
	@Column(name = "days_for_renewal", precision = 10)
	public Integer getDaysForRenewal();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.iap_product_id</code>.
	 */
	public IStructureMoneyTreeConfig setIapProductId(String value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.iap_product_id</code>.
	 */
	@Column(name = "iap_product_id", nullable = false, length = 100)
	@NotNull
	@Size(max = 100)
	public String getIapProductId();

	/**
	 * Setter for <code>mobsters.structure_money_tree_config.fake_iap_product_id</code>.
	 */
	public IStructureMoneyTreeConfig setFakeIapProductId(String value);

	/**
	 * Getter for <code>mobsters.structure_money_tree_config.fake_iap_product_id</code>.
	 */
	@Column(name = "fake_iap_product_id", length = 100)
	@Size(max = 100)
	public String getFakeIapProductId();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IStructureMoneyTreeConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureMoneyTreeConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IStructureMoneyTreeConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureMoneyTreeConfig> E into(E into);
}
