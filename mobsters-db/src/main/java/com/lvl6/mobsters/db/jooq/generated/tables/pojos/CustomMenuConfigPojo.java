/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICustomMenuConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "custom_menu_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"custom_menu_id", "position_z"})
})
public class CustomMenuConfigPojo implements ICustomMenuConfig {

	private static final long serialVersionUID = -417539683;

	private Integer customMenuId;
	private Integer positionX;
	private Integer positionY;
	private Integer positionZ;
	private Boolean isJiggle;
	private String  imageName;
	private Integer ipadPositionX;
	private Integer ipadPositionY;

	public CustomMenuConfigPojo() {}

	public CustomMenuConfigPojo(CustomMenuConfigPojo value) {
		this.customMenuId = value.customMenuId;
		this.positionX = value.positionX;
		this.positionY = value.positionY;
		this.positionZ = value.positionZ;
		this.isJiggle = value.isJiggle;
		this.imageName = value.imageName;
		this.ipadPositionX = value.ipadPositionX;
		this.ipadPositionY = value.ipadPositionY;
	}

	public CustomMenuConfigPojo(
		Integer customMenuId,
		Integer positionX,
		Integer positionY,
		Integer positionZ,
		Boolean isJiggle,
		String  imageName,
		Integer ipadPositionX,
		Integer ipadPositionY
	) {
		this.customMenuId = customMenuId;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.isJiggle = isJiggle;
		this.imageName = imageName;
		this.ipadPositionX = ipadPositionX;
		this.ipadPositionY = ipadPositionY;
	}

	@Column(name = "custom_menu_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCustomMenuId() {
		return this.customMenuId;
	}

	@Override
	public CustomMenuConfigPojo setCustomMenuId(Integer customMenuId) {
		this.customMenuId = customMenuId;
		return this;
	}

	@Column(name = "position_x", precision = 10)
	@Override
	public Integer getPositionX() {
		return this.positionX;
	}

	@Override
	public CustomMenuConfigPojo setPositionX(Integer positionX) {
		this.positionX = positionX;
		return this;
	}

	@Column(name = "position_y", precision = 10)
	@Override
	public Integer getPositionY() {
		return this.positionY;
	}

	@Override
	public CustomMenuConfigPojo setPositionY(Integer positionY) {
		this.positionY = positionY;
		return this;
	}

	@Column(name = "position_z", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getPositionZ() {
		return this.positionZ;
	}

	@Override
	public CustomMenuConfigPojo setPositionZ(Integer positionZ) {
		this.positionZ = positionZ;
		return this;
	}

	@Column(name = "is_jiggle", precision = 1)
	@Override
	public Boolean getIsJiggle() {
		return this.isJiggle;
	}

	@Override
	public CustomMenuConfigPojo setIsJiggle(Boolean isJiggle) {
		this.isJiggle = isJiggle;
		return this;
	}

	@Column(name = "image_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImageName() {
		return this.imageName;
	}

	@Override
	public CustomMenuConfigPojo setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	@Column(name = "ipad_position_x", precision = 10)
	@Override
	public Integer getIpadPositionX() {
		return this.ipadPositionX;
	}

	@Override
	public CustomMenuConfigPojo setIpadPositionX(Integer ipadPositionX) {
		this.ipadPositionX = ipadPositionX;
		return this;
	}

	@Column(name = "ipad_position_y", precision = 10)
	@Override
	public Integer getIpadPositionY() {
		return this.ipadPositionY;
	}

	@Override
	public CustomMenuConfigPojo setIpadPositionY(Integer ipadPositionY) {
		this.ipadPositionY = ipadPositionY;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICustomMenuConfig from) {
		setCustomMenuId(from.getCustomMenuId());
		setPositionX(from.getPositionX());
		setPositionY(from.getPositionY());
		setPositionZ(from.getPositionZ());
		setIsJiggle(from.getIsJiggle());
		setImageName(from.getImageName());
		setIpadPositionX(from.getIpadPositionX());
		setIpadPositionY(from.getIpadPositionY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICustomMenuConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.CustomMenuConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.CustomMenuConfigRecord();
		poop.from(this);
		return "CustomMenuConfigPojo[" + poop.valuesRow() + "]";
	}
}
