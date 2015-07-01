/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISkillSideEffectConfig;

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
public class SkillSideEffectConfigPojo implements ISkillSideEffectConfig {

	private static final long serialVersionUID = -407321331;

	private Integer id;
	private String  name;
	private String  desc;
	private String  type;
	private String  traitType;
	private String  imgName;
	private Integer imgPixelOffsetX;
	private Integer imgPixelOffsetY;
	private String  iconImgName;
	private String  pfxName;
	private String  pfxColor;
	private String  positionType;
	private Integer pfxPixelOffsetX;
	private Integer pfxPixelOffsetY;
	private String  blendMode;

	public SkillSideEffectConfigPojo() {}

	public SkillSideEffectConfigPojo(SkillSideEffectConfigPojo value) {
		this.id = value.id;
		this.name = value.name;
		this.desc = value.desc;
		this.type = value.type;
		this.traitType = value.traitType;
		this.imgName = value.imgName;
		this.imgPixelOffsetX = value.imgPixelOffsetX;
		this.imgPixelOffsetY = value.imgPixelOffsetY;
		this.iconImgName = value.iconImgName;
		this.pfxName = value.pfxName;
		this.pfxColor = value.pfxColor;
		this.positionType = value.positionType;
		this.pfxPixelOffsetX = value.pfxPixelOffsetX;
		this.pfxPixelOffsetY = value.pfxPixelOffsetY;
		this.blendMode = value.blendMode;
	}

	public SkillSideEffectConfigPojo(
		Integer id,
		String  name,
		String  desc,
		String  type,
		String  traitType,
		String  imgName,
		Integer imgPixelOffsetX,
		Integer imgPixelOffsetY,
		String  iconImgName,
		String  pfxName,
		String  pfxColor,
		String  positionType,
		Integer pfxPixelOffsetX,
		Integer pfxPixelOffsetY,
		String  blendMode
	) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.traitType = traitType;
		this.imgName = imgName;
		this.imgPixelOffsetX = imgPixelOffsetX;
		this.imgPixelOffsetY = imgPixelOffsetY;
		this.iconImgName = iconImgName;
		this.pfxName = pfxName;
		this.pfxColor = pfxColor;
		this.positionType = positionType;
		this.pfxPixelOffsetX = pfxPixelOffsetX;
		this.pfxPixelOffsetY = pfxPixelOffsetY;
		this.blendMode = blendMode;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public SkillSideEffectConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public SkillSideEffectConfigPojo setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "desc", length = 200)
	@Size(max = 200)
	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public SkillSideEffectConfigPojo setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	@Column(name = "type", length = 50)
	@Size(max = 50)
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public SkillSideEffectConfigPojo setType(String type) {
		this.type = type;
		return this;
	}

	@Column(name = "trait_type", length = 75)
	@Size(max = 75)
	@Override
	public String getTraitType() {
		return this.traitType;
	}

	@Override
	public SkillSideEffectConfigPojo setTraitType(String traitType) {
		this.traitType = traitType;
		return this;
	}

	@Column(name = "img_name", length = 75)
	@Size(max = 75)
	@Override
	public String getImgName() {
		return this.imgName;
	}

	@Override
	public SkillSideEffectConfigPojo setImgName(String imgName) {
		this.imgName = imgName;
		return this;
	}

	@Column(name = "img_pixel_offset_x", precision = 10)
	@Override
	public Integer getImgPixelOffsetX() {
		return this.imgPixelOffsetX;
	}

	@Override
	public SkillSideEffectConfigPojo setImgPixelOffsetX(Integer imgPixelOffsetX) {
		this.imgPixelOffsetX = imgPixelOffsetX;
		return this;
	}

	@Column(name = "img_pixel_offset_y", precision = 10)
	@Override
	public Integer getImgPixelOffsetY() {
		return this.imgPixelOffsetY;
	}

	@Override
	public SkillSideEffectConfigPojo setImgPixelOffsetY(Integer imgPixelOffsetY) {
		this.imgPixelOffsetY = imgPixelOffsetY;
		return this;
	}

	@Column(name = "icon_img_name", length = 75)
	@Size(max = 75)
	@Override
	public String getIconImgName() {
		return this.iconImgName;
	}

	@Override
	public SkillSideEffectConfigPojo setIconImgName(String iconImgName) {
		this.iconImgName = iconImgName;
		return this;
	}

	@Column(name = "pfx_name", length = 75)
	@Size(max = 75)
	@Override
	public String getPfxName() {
		return this.pfxName;
	}

	@Override
	public SkillSideEffectConfigPojo setPfxName(String pfxName) {
		this.pfxName = pfxName;
		return this;
	}

	@Column(name = "pfx_color", length = 45)
	@Size(max = 45)
	@Override
	public String getPfxColor() {
		return this.pfxColor;
	}

	@Override
	public SkillSideEffectConfigPojo setPfxColor(String pfxColor) {
		this.pfxColor = pfxColor;
		return this;
	}

	@Column(name = "position_type", length = 75)
	@Size(max = 75)
	@Override
	public String getPositionType() {
		return this.positionType;
	}

	@Override
	public SkillSideEffectConfigPojo setPositionType(String positionType) {
		this.positionType = positionType;
		return this;
	}

	@Column(name = "pfx_pixel_offset_x", precision = 10)
	@Override
	public Integer getPfxPixelOffsetX() {
		return this.pfxPixelOffsetX;
	}

	@Override
	public SkillSideEffectConfigPojo setPfxPixelOffsetX(Integer pfxPixelOffsetX) {
		this.pfxPixelOffsetX = pfxPixelOffsetX;
		return this;
	}

	@Column(name = "pfx_pixel_offset_y", precision = 10)
	@Override
	public Integer getPfxPixelOffsetY() {
		return this.pfxPixelOffsetY;
	}

	@Override
	public SkillSideEffectConfigPojo setPfxPixelOffsetY(Integer pfxPixelOffsetY) {
		this.pfxPixelOffsetY = pfxPixelOffsetY;
		return this;
	}

	@Column(name = "blend_mode", length = 75)
	@Size(max = 75)
	@Override
	public String getBlendMode() {
		return this.blendMode;
	}

	@Override
	public SkillSideEffectConfigPojo setBlendMode(String blendMode) {
		this.blendMode = blendMode;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ISkillSideEffectConfig from) {
		setId(from.getId());
		setName(from.getName());
		setDesc(from.getDesc());
		setType(from.getType());
		setTraitType(from.getTraitType());
		setImgName(from.getImgName());
		setImgPixelOffsetX(from.getImgPixelOffsetX());
		setImgPixelOffsetY(from.getImgPixelOffsetY());
		setIconImgName(from.getIconImgName());
		setPfxName(from.getPfxName());
		setPfxColor(from.getPfxColor());
		setPositionType(from.getPositionType());
		setPfxPixelOffsetX(from.getPfxPixelOffsetX());
		setPfxPixelOffsetY(from.getPfxPixelOffsetY());
		setBlendMode(from.getBlendMode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ISkillSideEffectConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.SkillSideEffectConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.SkillSideEffectConfigRecord();
		poop.from(this);
		return "SkillSideEffectConfigPojo[" + poop.valuesRow() + "]";
	}
}
