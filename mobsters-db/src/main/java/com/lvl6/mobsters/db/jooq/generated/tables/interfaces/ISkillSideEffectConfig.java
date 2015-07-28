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
@Table(name = "skill_side_effect_config", schema = "mobsters")
public interface ISkillSideEffectConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.id</code>.
	 */
	public ISkillSideEffectConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.name</code>.
	 */
	public ISkillSideEffectConfig setName(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	public String getName();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.desc</code>.
	 */
	public ISkillSideEffectConfig setDesc(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.desc</code>.
	 */
	@Column(name = "desc", length = 200)
	@Size(max = 200)
	public String getDesc();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.type</code>.
	 */
	public ISkillSideEffectConfig setType(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.type</code>.
	 */
	@Column(name = "type", length = 50)
	@Size(max = 50)
	public String getType();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.trait_type</code>. buff or ailment; good or bad
	 */
	public ISkillSideEffectConfig setTraitType(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.trait_type</code>. buff or ailment; good or bad
	 */
	@Column(name = "trait_type", length = 75)
	@Size(max = 75)
	public String getTraitType();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.img_name</code>.
	 */
	public ISkillSideEffectConfig setImgName(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.img_name</code>.
	 */
	@Column(name = "img_name", length = 75)
	@Size(max = 75)
	public String getImgName();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.img_pixel_offset_x</code>.
	 */
	public ISkillSideEffectConfig setImgPixelOffsetX(Integer value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.img_pixel_offset_x</code>.
	 */
	@Column(name = "img_pixel_offset_x", precision = 10)
	public Integer getImgPixelOffsetX();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.img_pixel_offset_y</code>.
	 */
	public ISkillSideEffectConfig setImgPixelOffsetY(Integer value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.img_pixel_offset_y</code>.
	 */
	@Column(name = "img_pixel_offset_y", precision = 10)
	public Integer getImgPixelOffsetY();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.icon_img_name</code>.
	 */
	public ISkillSideEffectConfig setIconImgName(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.icon_img_name</code>.
	 */
	@Column(name = "icon_img_name", length = 75)
	@Size(max = 75)
	public String getIconImgName();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.pfx_name</code>.
	 */
	public ISkillSideEffectConfig setPfxName(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.pfx_name</code>.
	 */
	@Column(name = "pfx_name", length = 75)
	@Size(max = 75)
	public String getPfxName();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.pfx_color</code>.
	 */
	public ISkillSideEffectConfig setPfxColor(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.pfx_color</code>.
	 */
	@Column(name = "pfx_color", length = 45)
	@Size(max = 45)
	public String getPfxColor();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.position_type</code>.
	 */
	public ISkillSideEffectConfig setPositionType(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.position_type</code>.
	 */
	@Column(name = "position_type", length = 75)
	@Size(max = 75)
	public String getPositionType();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.pfx_pixel_offset_x</code>.
	 */
	public ISkillSideEffectConfig setPfxPixelOffsetX(Integer value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.pfx_pixel_offset_x</code>.
	 */
	@Column(name = "pfx_pixel_offset_x", precision = 10)
	public Integer getPfxPixelOffsetX();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.pfx_pixel_offset_y</code>.
	 */
	public ISkillSideEffectConfig setPfxPixelOffsetY(Integer value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.pfx_pixel_offset_y</code>.
	 */
	@Column(name = "pfx_pixel_offset_y", precision = 10)
	public Integer getPfxPixelOffsetY();

	/**
	 * Setter for <code>mobsters.skill_side_effect_config.blend_mode</code>.
	 */
	public ISkillSideEffectConfig setBlendMode(String value);

	/**
	 * Getter for <code>mobsters.skill_side_effect_config.blend_mode</code>.
	 */
	@Column(name = "blend_mode", length = 75)
	@Size(max = 75)
	public String getBlendMode();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ISkillSideEffectConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISkillSideEffectConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ISkillSideEffectConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISkillSideEffectConfig> E into(E into);
}