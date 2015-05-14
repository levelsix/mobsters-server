/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniJobConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
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
@Table(name = "mini_job_config", schema = "mobsters")
public class MiniJobConfigRecord extends UpdatableRecordImpl<MiniJobConfigRecord> implements IMiniJobConfig {

	private static final long serialVersionUID = 1143838259;

	/**
	 * Setter for <code>mobsters.mini_job_config.id</code>.
	 */
	@Override
	public MiniJobConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.required_struct_id</code>.
	 */
	@Override
	public MiniJobConfigRecord setRequiredStructId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.required_struct_id</code>.
	 */
	@Column(name = "required_struct_id", precision = 10)
	@Override
	public Integer getRequiredStructId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.name</code>.
	 */
	@Override
	public MiniJobConfigRecord setName(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.cash_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setCashReward(UInteger value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.cash_reward</code>.
	 */
	@Column(name = "cash_reward", precision = 10)
	@Override
	public UInteger getCashReward() {
		return (UInteger) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.oil_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setOilReward(UInteger value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.oil_reward</code>.
	 */
	@Column(name = "oil_reward", precision = 10)
	@Override
	public UInteger getOilReward() {
		return (UInteger) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.gem_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setGemReward(UInteger value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.gem_reward</code>.
	 */
	@Column(name = "gem_reward", precision = 10)
	@Override
	public UInteger getGemReward() {
		return (UInteger) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.monster_id_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setMonsterIdReward(UInteger value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.monster_id_reward</code>.
	 */
	@Column(name = "monster_id_reward", precision = 10)
	@Override
	public UInteger getMonsterIdReward() {
		return (UInteger) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.item_id_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setItemIdReward(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.item_id_reward</code>.
	 */
	@Column(name = "item_id_reward", precision = 10)
	@Override
	public Integer getItemIdReward() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.item_reward_quantity</code>.
	 */
	@Override
	public MiniJobConfigRecord setItemRewardQuantity(Integer value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.item_reward_quantity</code>.
	 */
	@Column(name = "item_reward_quantity", precision = 10)
	@Override
	public Integer getItemRewardQuantity() {
		return (Integer) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.second_item_id_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setSecondItemIdReward(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.second_item_id_reward</code>.
	 */
	@Column(name = "second_item_id_reward", precision = 10)
	@Override
	public Integer getSecondItemIdReward() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.second_item_reward_quantity</code>.
	 */
	@Override
	public MiniJobConfigRecord setSecondItemRewardQuantity(Integer value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.second_item_reward_quantity</code>.
	 */
	@Column(name = "second_item_reward_quantity", precision = 10)
	@Override
	public Integer getSecondItemRewardQuantity() {
		return (Integer) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.quality</code>.
	 */
	@Override
	public MiniJobConfigRecord setQuality(String value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.quality</code>.
	 */
	@Column(name = "quality", length = 45)
	@Size(max = 45)
	@Override
	public String getQuality() {
		return (String) getValue(11);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.max_num_monsters_allowed</code>.
	 */
	@Override
	public MiniJobConfigRecord setMaxNumMonstersAllowed(UByte value) {
		setValue(12, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.max_num_monsters_allowed</code>.
	 */
	@Column(name = "max_num_monsters_allowed", precision = 3)
	@Override
	public UByte getMaxNumMonstersAllowed() {
		return (UByte) getValue(12);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.chance_to_appear</code>.
	 */
	@Override
	public MiniJobConfigRecord setChanceToAppear(Double value) {
		setValue(13, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.chance_to_appear</code>.
	 */
	@Column(name = "chance_to_appear", precision = 12)
	@Override
	public Double getChanceToAppear() {
		return (Double) getValue(13);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.hp_required</code>.
	 */
	@Override
	public MiniJobConfigRecord setHpRequired(Integer value) {
		setValue(14, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.hp_required</code>.
	 */
	@Column(name = "hp_required", precision = 10)
	@Override
	public Integer getHpRequired() {
		return (Integer) getValue(14);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.atk_required</code>.
	 */
	@Override
	public MiniJobConfigRecord setAtkRequired(UInteger value) {
		setValue(15, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.atk_required</code>.
	 */
	@Column(name = "atk_required", precision = 10)
	@Override
	public UInteger getAtkRequired() {
		return (UInteger) getValue(15);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.min_dmg</code>.
	 */
	@Override
	public MiniJobConfigRecord setMinDmg(UInteger value) {
		setValue(16, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.min_dmg</code>.
	 */
	@Column(name = "min_dmg", precision = 7)
	@Override
	public UInteger getMinDmg() {
		return (UInteger) getValue(16);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.max_dmg</code>.
	 */
	@Override
	public MiniJobConfigRecord setMaxDmg(UInteger value) {
		setValue(17, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.max_dmg</code>.
	 */
	@Column(name = "max_dmg", precision = 10)
	@Override
	public UInteger getMaxDmg() {
		return (UInteger) getValue(17);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.duration_min_minutes</code>.
	 */
	@Override
	public MiniJobConfigRecord setDurationMinMinutes(UInteger value) {
		setValue(18, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.duration_min_minutes</code>.
	 */
	@Column(name = "duration_min_minutes", precision = 7)
	@Override
	public UInteger getDurationMinMinutes() {
		return (UInteger) getValue(18);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.duration_max_minutes</code>.
	 */
	@Override
	public MiniJobConfigRecord setDurationMaxMinutes(UInteger value) {
		setValue(19, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.duration_max_minutes</code>.
	 */
	@Column(name = "duration_max_minutes", precision = 10)
	@Override
	public UInteger getDurationMaxMinutes() {
		return (UInteger) getValue(19);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.is_valid</code>.
	 */
	@Override
	public MiniJobConfigRecord setIsValid(Boolean value) {
		setValue(20, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.is_valid</code>.
	 */
	@Column(name = "is_valid", precision = 1)
	@Override
	public Boolean getIsValid() {
		return (Boolean) getValue(20);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.exp_reward</code>.
	 */
	@Override
	public MiniJobConfigRecord setExpReward(Integer value) {
		setValue(21, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.exp_reward</code>.
	 */
	@Column(name = "exp_reward", precision = 10)
	@Override
	public Integer getExpReward() {
		return (Integer) getValue(21);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.reward_id_one</code>.
	 */
	@Override
	public MiniJobConfigRecord setRewardIdOne(Integer value) {
		setValue(22, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.reward_id_one</code>.
	 */
	@Column(name = "reward_id_one", precision = 10)
	@Override
	public Integer getRewardIdOne() {
		return (Integer) getValue(22);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.reward_id_two</code>.
	 */
	@Override
	public MiniJobConfigRecord setRewardIdTwo(Integer value) {
		setValue(23, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.reward_id_two</code>.
	 */
	@Column(name = "reward_id_two", precision = 10)
	@Override
	public Integer getRewardIdTwo() {
		return (Integer) getValue(23);
	}

	/**
	 * Setter for <code>mobsters.mini_job_config.reward_id_three</code>.
	 */
	@Override
	public MiniJobConfigRecord setRewardIdThree(Integer value) {
		setValue(24, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_job_config.reward_id_three</code>.
	 */
	@Column(name = "reward_id_three", precision = 10)
	@Override
	public Integer getRewardIdThree() {
		return (Integer) getValue(24);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MiniJobConfigRecord
	 */
	public MiniJobConfigRecord() {
		super(MiniJobConfig.MINI_JOB_CONFIG);
	}

	/**
	 * Create a detached, initialised MiniJobConfigRecord
	 */
	public MiniJobConfigRecord(Integer id, Integer requiredStructId, String name, UInteger cashReward, UInteger oilReward, UInteger gemReward, UInteger monsterIdReward, Integer itemIdReward, Integer itemRewardQuantity, Integer secondItemIdReward, Integer secondItemRewardQuantity, String quality, UByte maxNumMonstersAllowed, Double chanceToAppear, Integer hpRequired, UInteger atkRequired, UInteger minDmg, UInteger maxDmg, UInteger durationMinMinutes, UInteger durationMaxMinutes, Boolean isValid, Integer expReward, Integer rewardIdOne, Integer rewardIdTwo, Integer rewardIdThree) {
		super(MiniJobConfig.MINI_JOB_CONFIG);

		setValue(0, id);
		setValue(1, requiredStructId);
		setValue(2, name);
		setValue(3, cashReward);
		setValue(4, oilReward);
		setValue(5, gemReward);
		setValue(6, monsterIdReward);
		setValue(7, itemIdReward);
		setValue(8, itemRewardQuantity);
		setValue(9, secondItemIdReward);
		setValue(10, secondItemRewardQuantity);
		setValue(11, quality);
		setValue(12, maxNumMonstersAllowed);
		setValue(13, chanceToAppear);
		setValue(14, hpRequired);
		setValue(15, atkRequired);
		setValue(16, minDmg);
		setValue(17, maxDmg);
		setValue(18, durationMinMinutes);
		setValue(19, durationMaxMinutes);
		setValue(20, isValid);
		setValue(21, expReward);
		setValue(22, rewardIdOne);
		setValue(23, rewardIdTwo);
		setValue(24, rewardIdThree);
	}
}
