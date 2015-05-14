/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskMapElementConfig;

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
@Table(name = "task_map_element_config", schema = "mobsters")
public class TaskMapElementConfig implements ITaskMapElementConfig {

	private static final long serialVersionUID = -247872524;

	private Integer id;
	private Integer taskId;
	private Integer xPos;
	private Integer yPos;
	private String  element;
	private Boolean isBoss;
	private String  bossImgName;
	private Integer itemDropId;
	private String  sectionName;
	private Integer cashReward;
	private Integer oilReward;
	private String  characterImgName;
	private Integer charVertPixelOffset;
	private Integer charHorizPixelOffset;
	private Double  charScaleFactor;
	private Boolean isFake;
	private Integer strength;

	public TaskMapElementConfig() {}

	public TaskMapElementConfig(TaskMapElementConfig value) {
		this.id = value.id;
		this.taskId = value.taskId;
		this.xPos = value.xPos;
		this.yPos = value.yPos;
		this.element = value.element;
		this.isBoss = value.isBoss;
		this.bossImgName = value.bossImgName;
		this.itemDropId = value.itemDropId;
		this.sectionName = value.sectionName;
		this.cashReward = value.cashReward;
		this.oilReward = value.oilReward;
		this.characterImgName = value.characterImgName;
		this.charVertPixelOffset = value.charVertPixelOffset;
		this.charHorizPixelOffset = value.charHorizPixelOffset;
		this.charScaleFactor = value.charScaleFactor;
		this.isFake = value.isFake;
		this.strength = value.strength;
	}

	public TaskMapElementConfig(
		Integer id,
		Integer taskId,
		Integer xPos,
		Integer yPos,
		String  element,
		Boolean isBoss,
		String  bossImgName,
		Integer itemDropId,
		String  sectionName,
		Integer cashReward,
		Integer oilReward,
		String  characterImgName,
		Integer charVertPixelOffset,
		Integer charHorizPixelOffset,
		Double  charScaleFactor,
		Boolean isFake,
		Integer strength
	) {
		this.id = id;
		this.taskId = taskId;
		this.xPos = xPos;
		this.yPos = yPos;
		this.element = element;
		this.isBoss = isBoss;
		this.bossImgName = bossImgName;
		this.itemDropId = itemDropId;
		this.sectionName = sectionName;
		this.cashReward = cashReward;
		this.oilReward = oilReward;
		this.characterImgName = characterImgName;
		this.charVertPixelOffset = charVertPixelOffset;
		this.charHorizPixelOffset = charHorizPixelOffset;
		this.charScaleFactor = charScaleFactor;
		this.isFake = isFake;
		this.strength = strength;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public TaskMapElementConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "task_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getTaskId() {
		return this.taskId;
	}

	@Override
	public TaskMapElementConfig setTaskId(Integer taskId) {
		this.taskId = taskId;
		return this;
	}

	@Column(name = "x_pos", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getXPos() {
		return this.xPos;
	}

	@Override
	public TaskMapElementConfig setXPos(Integer xPos) {
		this.xPos = xPos;
		return this;
	}

	@Column(name = "y_pos", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getYPos() {
		return this.yPos;
	}

	@Override
	public TaskMapElementConfig setYPos(Integer yPos) {
		this.yPos = yPos;
		return this;
	}

	@Column(name = "element", length = 15)
	@Size(max = 15)
	@Override
	public String getElement() {
		return this.element;
	}

	@Override
	public TaskMapElementConfig setElement(String element) {
		this.element = element;
		return this;
	}

	@Column(name = "is_boss", precision = 1)
	@Override
	public Boolean getIsBoss() {
		return this.isBoss;
	}

	@Override
	public TaskMapElementConfig setIsBoss(Boolean isBoss) {
		this.isBoss = isBoss;
		return this;
	}

	@Column(name = "boss_img_name", length = 75)
	@Size(max = 75)
	@Override
	public String getBossImgName() {
		return this.bossImgName;
	}

	@Override
	public TaskMapElementConfig setBossImgName(String bossImgName) {
		this.bossImgName = bossImgName;
		return this;
	}

	@Column(name = "item_drop_id", precision = 10)
	@Override
	public Integer getItemDropId() {
		return this.itemDropId;
	}

	@Override
	public TaskMapElementConfig setItemDropId(Integer itemDropId) {
		this.itemDropId = itemDropId;
		return this;
	}

	@Column(name = "section_name", length = 45)
	@Size(max = 45)
	@Override
	public String getSectionName() {
		return this.sectionName;
	}

	@Override
	public TaskMapElementConfig setSectionName(String sectionName) {
		this.sectionName = sectionName;
		return this;
	}

	@Column(name = "cash_reward", precision = 10)
	@Override
	public Integer getCashReward() {
		return this.cashReward;
	}

	@Override
	public TaskMapElementConfig setCashReward(Integer cashReward) {
		this.cashReward = cashReward;
		return this;
	}

	@Column(name = "oil_reward", precision = 10)
	@Override
	public Integer getOilReward() {
		return this.oilReward;
	}

	@Override
	public TaskMapElementConfig setOilReward(Integer oilReward) {
		this.oilReward = oilReward;
		return this;
	}

	@Column(name = "character_img_name", length = 75)
	@Size(max = 75)
	@Override
	public String getCharacterImgName() {
		return this.characterImgName;
	}

	@Override
	public TaskMapElementConfig setCharacterImgName(String characterImgName) {
		this.characterImgName = characterImgName;
		return this;
	}

	@Column(name = "char_vert_pixel_offset", precision = 10)
	@Override
	public Integer getCharVertPixelOffset() {
		return this.charVertPixelOffset;
	}

	@Override
	public TaskMapElementConfig setCharVertPixelOffset(Integer charVertPixelOffset) {
		this.charVertPixelOffset = charVertPixelOffset;
		return this;
	}

	@Column(name = "char_horiz_pixel_offset", precision = 10)
	@Override
	public Integer getCharHorizPixelOffset() {
		return this.charHorizPixelOffset;
	}

	@Override
	public TaskMapElementConfig setCharHorizPixelOffset(Integer charHorizPixelOffset) {
		this.charHorizPixelOffset = charHorizPixelOffset;
		return this;
	}

	@Column(name = "char_scale_factor", precision = 12)
	@Override
	public Double getCharScaleFactor() {
		return this.charScaleFactor;
	}

	@Override
	public TaskMapElementConfig setCharScaleFactor(Double charScaleFactor) {
		this.charScaleFactor = charScaleFactor;
		return this;
	}

	@Column(name = "is_fake", precision = 1)
	@Override
	public Boolean getIsFake() {
		return this.isFake;
	}

	@Override
	public TaskMapElementConfig setIsFake(Boolean isFake) {
		this.isFake = isFake;
		return this;
	}

	@Column(name = "strength", precision = 10)
	@Override
	public Integer getStrength() {
		return this.strength;
	}

	@Override
	public TaskMapElementConfig setStrength(Integer strength) {
		this.strength = strength;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskMapElementConfig from) {
		setId(from.getId());
		setTaskId(from.getTaskId());
		setXPos(from.getXPos());
		setYPos(from.getYPos());
		setElement(from.getElement());
		setIsBoss(from.getIsBoss());
		setBossImgName(from.getBossImgName());
		setItemDropId(from.getItemDropId());
		setSectionName(from.getSectionName());
		setCashReward(from.getCashReward());
		setOilReward(from.getOilReward());
		setCharacterImgName(from.getCharacterImgName());
		setCharVertPixelOffset(from.getCharVertPixelOffset());
		setCharHorizPixelOffset(from.getCharHorizPixelOffset());
		setCharScaleFactor(from.getCharScaleFactor());
		setIsFake(from.getIsFake());
		setStrength(from.getStrength());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskMapElementConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
