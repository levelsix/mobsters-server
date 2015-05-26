/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;


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
@Table(name = "mini_job_refresh_item_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"struct_id", "item_id"})
})
public interface IMiniJobRefreshItemConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.mini_job_refresh_item_config.struct_id</code>.
	 */
	public IMiniJobRefreshItemConfig setStructId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_job_refresh_item_config.struct_id</code>.
	 */
	@Column(name = "struct_id", nullable = false, precision = 10)
	@NotNull
	public Integer getStructId();

	/**
	 * Setter for <code>mobsters.mini_job_refresh_item_config.item_id</code>.
	 */
	public IMiniJobRefreshItemConfig setItemId(Integer value);

	/**
	 * Getter for <code>mobsters.mini_job_refresh_item_config.item_id</code>.
	 */
	@Column(name = "item_id", nullable = false, precision = 10)
	@NotNull
	public Integer getItemId();

	/**
	 * Setter for <code>mobsters.mini_job_refresh_item_config.gem_price</code>.
	 */
	public IMiniJobRefreshItemConfig setGemPrice(Integer value);

	/**
	 * Getter for <code>mobsters.mini_job_refresh_item_config.gem_price</code>.
	 */
	@Column(name = "gem_price", precision = 10)
	public Integer getGemPrice();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMiniJobRefreshItemConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobRefreshItemConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMiniJobRefreshItemConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobRefreshItemConfig> E into(E into);
}
