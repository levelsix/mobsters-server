/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobConfig;

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
@Table(name = "mini_job_config", schema = "mobsters")
public class MiniJobConfigPojo implements IMiniJobConfig {

	private static final long serialVersionUID = -999446445;

	private Integer id;
	private Integer requiredStructId;
	private String  name;
	private Integer cashReward;
	private Integer oilReward;
	private Integer gemReward;
	private Integer monsterIdReward;
	private Integer itemIdReward;
	private Integer itemRewardQuantity;
	private Integer secondItemIdReward;
	private Integer secondItemRewardQuantity;
	private String  quality;
	private Byte    maxNumMonstersAllowed;
	private Double  chanceToAppear;
	private Integer hpRequired;
	private Integer atkRequired;
	private Integer minDmg;
	private Integer maxDmg;
	private Integer durationMinMinutes;
	private Integer durationMaxMinutes;
	private Boolean isValid;
	private Integer expReward;
	private Integer rewardIdOne;
	private Integer rewardIdTwo;
	private Integer rewardIdThree;

	public MiniJobConfigPojo() {}

	public MiniJobConfigPojo(MiniJobConfigPojo value) {
		this.id = value.id;
		this.requiredStructId = value.requiredStructId;
		this.name = value.name;
		this.cashReward = value.cashReward;
		this.oilReward = value.oilReward;
		this.gemReward = value.gemReward;
		this.monsterIdReward = value.monsterIdReward;
		this.itemIdReward = value.itemIdReward;
		this.itemRewardQuantity = value.itemRewardQuantity;
		this.secondItemIdReward = value.secondItemIdReward;
		this.secondItemRewardQuantity = value.secondItemRewardQuantity;
		this.quality = value.quality;
		this.maxNumMonstersAllowed = value.maxNumMonstersAllowed;
		this.chanceToAppear = value.chanceToAppear;
		this.hpRequired = value.hpRequired;
		this.atkRequired = value.atkRequired;
		this.minDmg = value.minDmg;
		this.maxDmg = value.maxDmg;
		this.durationMinMinutes = value.durationMinMinutes;
		this.durationMaxMinutes = value.durationMaxMinutes;
		this.isValid = value.isValid;
		this.expReward = value.expReward;
		this.rewardIdOne = value.rewardIdOne;
		this.rewardIdTwo = value.rewardIdTwo;
		this.rewardIdThree = value.rewardIdThree;
	}

	public MiniJobConfigPojo(
		Integer id,
		Integer requiredStructId,
		String  name,
		Integer cashReward,
		Integer oilReward,
		Integer gemReward,
		Integer monsterIdReward,
		Integer itemIdReward,
		Integer itemRewardQuantity,
		Integer secondItemIdReward,
		Integer secondItemRewardQuantity,
		String  quality,
		Byte    maxNumMonstersAllowed,
		Double  chanceToAppear,
		Integer hpRequired,
		Integer atkRequired,
		Integer minDmg,
		Integer maxDmg,
		Integer durationMinMinutes,
		Integer durationMaxMinutes,
		Boolean isValid,
		Integer expReward,
		Integer rewardIdOne,
		Integer rewardIdTwo,
		Integer rewardIdThree
	) {
		this.id = id;
		this.requiredStructId = requiredStructId;
		this.name = name;
		this.cashReward = cashReward;
		this.oilReward = oilReward;
		this.gemReward = gemReward;
		this.monsterIdReward = monsterIdReward;
		this.itemIdReward = itemIdReward;
		this.itemRewardQuantity = itemRewardQuantity;
		this.secondItemIdReward = secondItemIdReward;
		this.secondItemRewardQuantity = secondItemRewardQuantity;
		this.quality = quality;
		this.maxNumMonstersAllowed = maxNumMonstersAllowed;
		this.chanceToAppear = chanceToAppear;
		this.hpRequired = hpRequired;
		this.atkRequired = atkRequired;
		this.minDmg = minDmg;
		this.maxDmg = maxDmg;
		this.durationMinMinutes = durationMinMinutes;
		this.durationMaxMinutes = durationMaxMinutes;
		this.isValid = isValid;
		this.expReward = expReward;
		this.rewardIdOne = rewardIdOne;
		this.rewardIdTwo = rewardIdTwo;
		this.rewardIdThree = rewardIdThree;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public MiniJobConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "required_struct_id", precision = 10)
	@Override
	public Integer getRequiredStructId() {
		return this.requiredStructId;
	}

	@Override
	public MiniJobConfigPojo setRequiredStructId(Integer requiredStructId) {
		this.requiredStructId = requiredStructId;
		return this;
	}

	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public MiniJobConfigPojo setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "cash_reward", precision = 10)
	@Override
	public Integer getCashReward() {
		return this.cashReward;
	}

	@Override
	public MiniJobConfigPojo setCashReward(Integer cashReward) {
		this.cashReward = cashReward;
		return this;
	}

	@Column(name = "oil_reward", precision = 10)
	@Override
	public Integer getOilReward() {
		return this.oilReward;
	}

	@Override
	public MiniJobConfigPojo setOilReward(Integer oilReward) {
		this.oilReward = oilReward;
		return this;
	}

	@Column(name = "gem_reward", precision = 10)
	@Override
	public Integer getGemReward() {
		return this.gemReward;
	}

	@Override
	public MiniJobConfigPojo setGemReward(Integer gemReward) {
		this.gemReward = gemReward;
		return this;
	}

	@Column(name = "monster_id_reward", precision = 10)
	@Override
	public Integer getMonsterIdReward() {
		return this.monsterIdReward;
	}

	@Override
	public MiniJobConfigPojo setMonsterIdReward(Integer monsterIdReward) {
		this.monsterIdReward = monsterIdReward;
		return this;
	}

	@Column(name = "item_id_reward", precision = 10)
	@Override
	public Integer getItemIdReward() {
		return this.itemIdReward;
	}

	@Override
	public MiniJobConfigPojo setItemIdReward(Integer itemIdReward) {
		this.itemIdReward = itemIdReward;
		return this;
	}

	@Column(name = "item_reward_quantity", precision = 10)
	@Override
	public Integer getItemRewardQuantity() {
		return this.itemRewardQuantity;
	}

	@Override
	public MiniJobConfigPojo setItemRewardQuantity(Integer itemRewardQuantity) {
		this.itemRewardQuantity = itemRewardQuantity;
		return this;
	}

	@Column(name = "second_item_id_reward", precision = 10)
	@Override
	public Integer getSecondItemIdReward() {
		return this.secondItemIdReward;
	}

	@Override
	public MiniJobConfigPojo setSecondItemIdReward(Integer secondItemIdReward) {
		this.secondItemIdReward = secondItemIdReward;
		return this;
	}

	@Column(name = "second_item_reward_quantity", precision = 10)
	@Override
	public Integer getSecondItemRewardQuantity() {
		return this.secondItemRewardQuantity;
	}

	@Override
	public MiniJobConfigPojo setSecondItemRewardQuantity(Integer secondItemRewardQuantity) {
		this.secondItemRewardQuantity = secondItemRewardQuantity;
		return this;
	}

	@Column(name = "quality", length = 45)
	@Size(max = 45)
	@Override
	public String getQuality() {
		return this.quality;
	}

	@Override
	public MiniJobConfigPojo setQuality(String quality) {
		this.quality = quality;
		return this;
	}

	@Column(name = "max_num_monsters_allowed", precision = 3)
	@Override
	public Byte getMaxNumMonstersAllowed() {
		return this.maxNumMonstersAllowed;
	}

	@Override
	public MiniJobConfigPojo setMaxNumMonstersAllowed(Byte maxNumMonstersAllowed) {
		this.maxNumMonstersAllowed = maxNumMonstersAllowed;
		return this;
	}

	@Column(name = "chance_to_appear", precision = 12)
	@Override
	public Double getChanceToAppear() {
		return this.chanceToAppear;
	}

	@Override
	public MiniJobConfigPojo setChanceToAppear(Double chanceToAppear) {
		this.chanceToAppear = chanceToAppear;
		return this;
	}

	@Column(name = "hp_required", precision = 10)
	@Override
	public Integer getHpRequired() {
		return this.hpRequired;
	}

	@Override
	public MiniJobConfigPojo setHpRequired(Integer hpRequired) {
		this.hpRequired = hpRequired;
		return this;
	}

	@Column(name = "atk_required", precision = 10)
	@Override
	public Integer getAtkRequired() {
		return this.atkRequired;
	}

	@Override
	public MiniJobConfigPojo setAtkRequired(Integer atkRequired) {
		this.atkRequired = atkRequired;
		return this;
	}

	@Column(name = "min_dmg", precision = 7)
	@Override
	public Integer getMinDmg() {
		return this.minDmg;
	}

	@Override
	public MiniJobConfigPojo setMinDmg(Integer minDmg) {
		this.minDmg = minDmg;
		return this;
	}

	@Column(name = "max_dmg", precision = 10)
	@Override
	public Integer getMaxDmg() {
		return this.maxDmg;
	}

	@Override
	public MiniJobConfigPojo setMaxDmg(Integer maxDmg) {
		this.maxDmg = maxDmg;
		return this;
	}

	@Column(name = "duration_min_minutes", precision = 7)
	@Override
	public Integer getDurationMinMinutes() {
		return this.durationMinMinutes;
	}

	@Override
	public MiniJobConfigPojo setDurationMinMinutes(Integer durationMinMinutes) {
		this.durationMinMinutes = durationMinMinutes;
		return this;
	}

	@Column(name = "duration_max_minutes", precision = 10)
	@Override
	public Integer getDurationMaxMinutes() {
		return this.durationMaxMinutes;
	}

	@Override
	public MiniJobConfigPojo setDurationMaxMinutes(Integer durationMaxMinutes) {
		this.durationMaxMinutes = durationMaxMinutes;
		return this;
	}

	@Column(name = "is_valid", precision = 1)
	@Override
	public Boolean getIsValid() {
		return this.isValid;
	}

	@Override
	public MiniJobConfigPojo setIsValid(Boolean isValid) {
		this.isValid = isValid;
		return this;
	}

	@Column(name = "exp_reward", precision = 10)
	@Override
	public Integer getExpReward() {
		return this.expReward;
	}

	@Override
	public MiniJobConfigPojo setExpReward(Integer expReward) {
		this.expReward = expReward;
		return this;
	}

	@Column(name = "reward_id_one", precision = 10)
	@Override
	public Integer getRewardIdOne() {
		return this.rewardIdOne;
	}

	@Override
	public MiniJobConfigPojo setRewardIdOne(Integer rewardIdOne) {
		this.rewardIdOne = rewardIdOne;
		return this;
	}

	@Column(name = "reward_id_two", precision = 10)
	@Override
	public Integer getRewardIdTwo() {
		return this.rewardIdTwo;
	}

	@Override
	public MiniJobConfigPojo setRewardIdTwo(Integer rewardIdTwo) {
		this.rewardIdTwo = rewardIdTwo;
		return this;
	}

	@Column(name = "reward_id_three", precision = 10)
	@Override
	public Integer getRewardIdThree() {
		return this.rewardIdThree;
	}

	@Override
	public MiniJobConfigPojo setRewardIdThree(Integer rewardIdThree) {
		this.rewardIdThree = rewardIdThree;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniJobConfig from) {
		setId(from.getId());
		setRequiredStructId(from.getRequiredStructId());
		setName(from.getName());
		setCashReward(from.getCashReward());
		setOilReward(from.getOilReward());
		setGemReward(from.getGemReward());
		setMonsterIdReward(from.getMonsterIdReward());
		setItemIdReward(from.getItemIdReward());
		setItemRewardQuantity(from.getItemRewardQuantity());
		setSecondItemIdReward(from.getSecondItemIdReward());
		setSecondItemRewardQuantity(from.getSecondItemRewardQuantity());
		setQuality(from.getQuality());
		setMaxNumMonstersAllowed(from.getMaxNumMonstersAllowed());
		setChanceToAppear(from.getChanceToAppear());
		setHpRequired(from.getHpRequired());
		setAtkRequired(from.getAtkRequired());
		setMinDmg(from.getMinDmg());
		setMaxDmg(from.getMaxDmg());
		setDurationMinMinutes(from.getDurationMinMinutes());
		setDurationMaxMinutes(from.getDurationMaxMinutes());
		setIsValid(from.getIsValid());
		setExpReward(from.getExpReward());
		setRewardIdOne(from.getRewardIdOne());
		setRewardIdTwo(from.getRewardIdTwo());
		setRewardIdThree(from.getRewardIdThree());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniJobConfig> E into(E into) {
		into.from(this);
		return into;
	}
}