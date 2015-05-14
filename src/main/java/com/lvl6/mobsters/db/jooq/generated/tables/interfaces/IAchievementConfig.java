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

import org.jooq.types.UByte;


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
@Table(name = "achievement_config", schema = "mobsters")
public interface IAchievementConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.achievement_config.id</code>.
	 */
	public IAchievementConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.achievement_config.name</code>.
	 */
	public IAchievementConfig setName(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.name</code>.
	 */
	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	public String getName();

	/**
	 * Setter for <code>mobsters.achievement_config.description</code>.
	 */
	public IAchievementConfig setDescription(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.description</code>.
	 */
	@Column(name = "description", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	public String getDescription();

	/**
	 * Setter for <code>mobsters.achievement_config.gem_reward</code>.
	 */
	public IAchievementConfig setGemReward(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 10)
	public Integer getGemReward();

	/**
	 * Setter for <code>mobsters.achievement_config.lvl</code>.
	 */
	public IAchievementConfig setLvl(UByte value);

	/**
	 * Getter for <code>mobsters.achievement_config.lvl</code>.
	 */
	@Column(name = "lvl", nullable = false, precision = 3)
	@NotNull
	public UByte getLvl();

	/**
	 * Setter for <code>mobsters.achievement_config.achievement_type</code>.
	 */
	public IAchievementConfig setAchievementType(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.achievement_type</code>.
	 */
	@Column(name = "achievement_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	public String getAchievementType();

	/**
	 * Setter for <code>mobsters.achievement_config.resource_type</code>. could be null
	 */
	public IAchievementConfig setResourceType(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.resource_type</code>. could be null
	 */
	@Column(name = "resource_type", length = 45)
	@Size(max = 45)
	public String getResourceType();

	/**
	 * Setter for <code>mobsters.achievement_config.monster_element</code>. could be null
	 */
	public IAchievementConfig setMonsterElement(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.monster_element</code>. could be null
	 */
	@Column(name = "monster_element", length = 45)
	@Size(max = 45)
	public String getMonsterElement();

	/**
	 * Setter for <code>mobsters.achievement_config.monster_quality</code>. could be null
	 */
	public IAchievementConfig setMonsterQuality(String value);

	/**
	 * Getter for <code>mobsters.achievement_config.monster_quality</code>. could be null
	 */
	@Column(name = "monster_quality", length = 45)
	@Size(max = 45)
	public String getMonsterQuality();

	/**
	 * Setter for <code>mobsters.achievement_config.static_data_id</code>.
	 */
	public IAchievementConfig setStaticDataId(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.static_data_id</code>.
	 */
	@Column(name = "static_data_id", precision = 10)
	public Integer getStaticDataId();

	/**
	 * Setter for <code>mobsters.achievement_config.quantity</code>.
	 */
	public IAchievementConfig setQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.quantity</code>.
	 */
	@Column(name = "quantity", precision = 10)
	public Integer getQuantity();

	/**
	 * Setter for <code>mobsters.achievement_config.priority</code>.
	 */
	public IAchievementConfig setPriority(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.priority</code>.
	 */
	@Column(name = "priority", precision = 10)
	public Integer getPriority();

	/**
	 * Setter for <code>mobsters.achievement_config.prerequisite_id</code>.
	 */
	public IAchievementConfig setPrerequisiteId(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.prerequisite_id</code>.
	 */
	@Column(name = "prerequisite_id", precision = 10)
	public Integer getPrerequisiteId();

	/**
	 * Setter for <code>mobsters.achievement_config.successor_id</code>.
	 */
	public IAchievementConfig setSuccessorId(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.successor_id</code>.
	 */
	@Column(name = "successor_id", precision = 10)
	public Integer getSuccessorId();

	/**
	 * Setter for <code>mobsters.achievement_config.exp_reward</code>.
	 */
	public IAchievementConfig setExpReward(Integer value);

	/**
	 * Getter for <code>mobsters.achievement_config.exp_reward</code>.
	 */
	@Column(name = "exp_reward", precision = 10)
	public Integer getExpReward();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IAchievementConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IAchievementConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IAchievementConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IAchievementConfig> E into(E into);
}
