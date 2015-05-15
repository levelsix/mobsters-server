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
@Table(name = "quest_job_config", schema = "mobsters")
public interface IQuestJobConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.quest_job_config.id</code>.
	 */
	public IQuestJobConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.quest_job_config.quest_id</code>.
	 */
	public IQuestJobConfig setQuestId(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_config.quest_id</code>.
	 */
	@Column(name = "quest_id", precision = 10)
	public Integer getQuestId();

	/**
	 * Setter for <code>mobsters.quest_job_config.quest_job_type</code>.
	 */
	public IQuestJobConfig setQuestJobType(String value);

	/**
	 * Getter for <code>mobsters.quest_job_config.quest_job_type</code>.
	 */
	@Column(name = "quest_job_type", length = 45)
	@Size(max = 45)
	public String getQuestJobType();

	/**
	 * Setter for <code>mobsters.quest_job_config.description</code>.
	 */
	public IQuestJobConfig setDescription(String value);

	/**
	 * Getter for <code>mobsters.quest_job_config.description</code>.
	 */
	@Column(name = "description", length = 45)
	@Size(max = 45)
	public String getDescription();

	/**
	 * Setter for <code>mobsters.quest_job_config.static_data_id</code>.
	 */
	public IQuestJobConfig setStaticDataId(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_config.static_data_id</code>.
	 */
	@Column(name = "static_data_id", precision = 10)
	public Integer getStaticDataId();

	/**
	 * Setter for <code>mobsters.quest_job_config.quantity</code>.
	 */
	public IQuestJobConfig setQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_config.quantity</code>.
	 */
	@Column(name = "quantity", precision = 10)
	public Integer getQuantity();

	/**
	 * Setter for <code>mobsters.quest_job_config.priority</code>.
	 */
	public IQuestJobConfig setPriority(Integer value);

	/**
	 * Getter for <code>mobsters.quest_job_config.priority</code>.
	 */
	@Column(name = "priority", precision = 10)
	public Integer getPriority();

	/**
	 * Setter for <code>mobsters.quest_job_config.city_id</code>.
	 */
	public IQuestJobConfig setCityId(UInteger value);

	/**
	 * Getter for <code>mobsters.quest_job_config.city_id</code>.
	 */
	@Column(name = "city_id", precision = 7)
	public UInteger getCityId();

	/**
	 * Setter for <code>mobsters.quest_job_config.city_asset_num</code>. when user clicks go button, the camera will be focused on this city_asset (look in city_element)
	 */
	public IQuestJobConfig setCityAssetNum(UInteger value);

	/**
	 * Getter for <code>mobsters.quest_job_config.city_asset_num</code>. when user clicks go button, the camera will be focused on this city_asset (look in city_element)
	 */
	@Column(name = "city_asset_num", precision = 7)
	public UInteger getCityAssetNum();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IQuestJobConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IQuestJobConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobConfig> E into(E into);
}
