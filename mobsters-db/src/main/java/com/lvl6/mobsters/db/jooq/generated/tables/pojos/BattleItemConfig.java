/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBattleItemConfig;

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
@Table(name = "battle_item_config", schema = "mobsters")
public class BattleItemConfig implements IBattleItemConfig {

	private static final long serialVersionUID = -1674483310;

	private Integer id;
	private String  type;
	private String  battleItemCategory;
	private String  createResourceType;
	private Integer createCost;
	private String  name;
	private String  description;
	private Integer powerAmount;
	private String  imageName;
	private Integer priority;
	private Integer minutesToCreate;
	private Integer inBattleGemCost;
	private Integer amount;

	public BattleItemConfig() {}

	public BattleItemConfig(BattleItemConfig value) {
		this.id = value.id;
		this.type = value.type;
		this.battleItemCategory = value.battleItemCategory;
		this.createResourceType = value.createResourceType;
		this.createCost = value.createCost;
		this.name = value.name;
		this.description = value.description;
		this.powerAmount = value.powerAmount;
		this.imageName = value.imageName;
		this.priority = value.priority;
		this.minutesToCreate = value.minutesToCreate;
		this.inBattleGemCost = value.inBattleGemCost;
		this.amount = value.amount;
	}

	public BattleItemConfig(
		Integer id,
		String  type,
		String  battleItemCategory,
		String  createResourceType,
		Integer createCost,
		String  name,
		String  description,
		Integer powerAmount,
		String  imageName,
		Integer priority,
		Integer minutesToCreate,
		Integer inBattleGemCost,
		Integer amount
	) {
		this.id = id;
		this.type = type;
		this.battleItemCategory = battleItemCategory;
		this.createResourceType = createResourceType;
		this.createCost = createCost;
		this.name = name;
		this.description = description;
		this.powerAmount = powerAmount;
		this.imageName = imageName;
		this.priority = priority;
		this.minutesToCreate = minutesToCreate;
		this.inBattleGemCost = inBattleGemCost;
		this.amount = amount;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public BattleItemConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public BattleItemConfig setType(String type) {
		this.type = type;
		return this;
	}

	@Column(name = "battle_item_category", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getBattleItemCategory() {
		return this.battleItemCategory;
	}

	@Override
	public BattleItemConfig setBattleItemCategory(String battleItemCategory) {
		this.battleItemCategory = battleItemCategory;
		return this;
	}

	@Column(name = "create_resource_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getCreateResourceType() {
		return this.createResourceType;
	}

	@Override
	public BattleItemConfig setCreateResourceType(String createResourceType) {
		this.createResourceType = createResourceType;
		return this;
	}

	@Column(name = "create_cost", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCreateCost() {
		return this.createCost;
	}

	@Override
	public BattleItemConfig setCreateCost(Integer createCost) {
		this.createCost = createCost;
		return this;
	}

	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public BattleItemConfig setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "description", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public BattleItemConfig setDescription(String description) {
		this.description = description;
		return this;
	}

	@Column(name = "power_amount", precision = 10)
	@Override
	public Integer getPowerAmount() {
		return this.powerAmount;
	}

	@Override
	public BattleItemConfig setPowerAmount(Integer powerAmount) {
		this.powerAmount = powerAmount;
		return this;
	}

	@Column(name = "image_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImageName() {
		return this.imageName;
	}

	@Override
	public BattleItemConfig setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	@Column(name = "priority", precision = 10)
	@Override
	public Integer getPriority() {
		return this.priority;
	}

	@Override
	public BattleItemConfig setPriority(Integer priority) {
		this.priority = priority;
		return this;
	}

	@Column(name = "minutes_to_create", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMinutesToCreate() {
		return this.minutesToCreate;
	}

	@Override
	public BattleItemConfig setMinutesToCreate(Integer minutesToCreate) {
		this.minutesToCreate = minutesToCreate;
		return this;
	}

	@Column(name = "in_battle_gem_cost", precision = 10)
	@Override
	public Integer getInBattleGemCost() {
		return this.inBattleGemCost;
	}

	@Override
	public BattleItemConfig setInBattleGemCost(Integer inBattleGemCost) {
		this.inBattleGemCost = inBattleGemCost;
		return this;
	}

	@Column(name = "amount", precision = 10)
	@Override
	public Integer getAmount() {
		return this.amount;
	}

	@Override
	public BattleItemConfig setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBattleItemConfig from) {
		setId(from.getId());
		setType(from.getType());
		setBattleItemCategory(from.getBattleItemCategory());
		setCreateResourceType(from.getCreateResourceType());
		setCreateCost(from.getCreateCost());
		setName(from.getName());
		setDescription(from.getDescription());
		setPowerAmount(from.getPowerAmount());
		setImageName(from.getImageName());
		setPriority(from.getPriority());
		setMinutesToCreate(from.getMinutesToCreate());
		setInBattleGemCost(from.getInBattleGemCost());
		setAmount(from.getAmount());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBattleItemConfig> E into(E into) {
		into.from(this);
		return into;
	}
}