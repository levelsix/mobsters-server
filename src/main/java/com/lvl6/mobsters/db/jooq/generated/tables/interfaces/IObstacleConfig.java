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
@Table(name = "obstacle_config", schema = "mobsters")
public interface IObstacleConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.obstacle_config.id</code>.
	 */
	public IObstacleConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.obstacle_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.obstacle_config.name</code>.
	 */
	public IObstacleConfig setName(String value);

	/**
	 * Getter for <code>mobsters.obstacle_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	public String getName();

	/**
	 * Setter for <code>mobsters.obstacle_config.removal_cost_type</code>.
	 */
	public IObstacleConfig setRemovalCostType(String value);

	/**
	 * Getter for <code>mobsters.obstacle_config.removal_cost_type</code>.
	 */
	@Column(name = "removal_cost_type", length = 45)
	@Size(max = 45)
	public String getRemovalCostType();

	/**
	 * Setter for <code>mobsters.obstacle_config.cost</code>.
	 */
	public IObstacleConfig setCost(UInteger value);

	/**
	 * Getter for <code>mobsters.obstacle_config.cost</code>.
	 */
	@Column(name = "cost", precision = 7)
	public UInteger getCost();

	/**
	 * Setter for <code>mobsters.obstacle_config.seconds_to_remove</code>.
	 */
	public IObstacleConfig setSecondsToRemove(UInteger value);

	/**
	 * Getter for <code>mobsters.obstacle_config.seconds_to_remove</code>.
	 */
	@Column(name = "seconds_to_remove", precision = 7)
	public UInteger getSecondsToRemove();

	/**
	 * Setter for <code>mobsters.obstacle_config.width</code>.
	 */
	public IObstacleConfig setWidth(UByte value);

	/**
	 * Getter for <code>mobsters.obstacle_config.width</code>.
	 */
	@Column(name = "width", precision = 3)
	public UByte getWidth();

	/**
	 * Setter for <code>mobsters.obstacle_config.height</code>.
	 */
	public IObstacleConfig setHeight(UByte value);

	/**
	 * Getter for <code>mobsters.obstacle_config.height</code>.
	 */
	@Column(name = "height", precision = 3)
	public UByte getHeight();

	/**
	 * Setter for <code>mobsters.obstacle_config.img_name</code>.
	 */
	public IObstacleConfig setImgName(String value);

	/**
	 * Getter for <code>mobsters.obstacle_config.img_name</code>.
	 */
	@Column(name = "img_name", length = 45)
	@Size(max = 45)
	public String getImgName();

	/**
	 * Setter for <code>mobsters.obstacle_config.img_vertical_pixel_offset</code>.
	 */
	public IObstacleConfig setImgVerticalPixelOffset(Double value);

	/**
	 * Getter for <code>mobsters.obstacle_config.img_vertical_pixel_offset</code>.
	 */
	@Column(name = "img_vertical_pixel_offset", precision = 12)
	public Double getImgVerticalPixelOffset();

	/**
	 * Setter for <code>mobsters.obstacle_config.description</code>.
	 */
	public IObstacleConfig setDescription(String value);

	/**
	 * Getter for <code>mobsters.obstacle_config.description</code>.
	 */
	@Column(name = "description", length = 45)
	@Size(max = 45)
	public String getDescription();

	/**
	 * Setter for <code>mobsters.obstacle_config.chance_to_appear</code>.
	 */
	public IObstacleConfig setChanceToAppear(Double value);

	/**
	 * Getter for <code>mobsters.obstacle_config.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	public Double getChanceToAppear();

	/**
	 * Setter for <code>mobsters.obstacle_config.shadow_img_name</code>.
	 */
	public IObstacleConfig setShadowImgName(String value);

	/**
	 * Getter for <code>mobsters.obstacle_config.shadow_img_name</code>.
	 */
	@Column(name = "shadow_img_name", length = 45)
	@Size(max = 45)
	public String getShadowImgName();

	/**
	 * Setter for <code>mobsters.obstacle_config.shadow_vertical_offset</code>.
	 */
	public IObstacleConfig setShadowVerticalOffset(Double value);

	/**
	 * Getter for <code>mobsters.obstacle_config.shadow_vertical_offset</code>.
	 */
	@Column(name = "shadow_vertical_offset", precision = 12)
	public Double getShadowVerticalOffset();

	/**
	 * Setter for <code>mobsters.obstacle_config.shadow_horizontal_offset</code>.
	 */
	public IObstacleConfig setShadowHorizontalOffset(Double value);

	/**
	 * Getter for <code>mobsters.obstacle_config.shadow_horizontal_offset</code>.
	 */
	@Column(name = "shadow_horizontal_offset", precision = 12)
	public Double getShadowHorizontalOffset();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IObstacleConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IObstacleConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IObstacleConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IObstacleConfig> E into(E into);
}
