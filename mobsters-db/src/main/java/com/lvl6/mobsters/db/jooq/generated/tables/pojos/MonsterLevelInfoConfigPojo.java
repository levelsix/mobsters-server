/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterLevelInfoConfig;

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
@Table(name = "monster_level_info_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"monster_id", "level"})
})
public class MonsterLevelInfoConfigPojo implements IMonsterLevelInfoConfig {

	private static final long serialVersionUID = 2147081380;

	private Integer monsterId;
	private Integer level;
	private Integer hp;
	private Integer curLvlRequiredExp;
	private Integer feederExp;
	private Integer fireDmg;
	private Integer grassDmg;
	private Integer waterDmg;
	private Integer lightningDmg;
	private Integer darknessDmg;
	private Integer rockDmg;
	private Byte    speed;
	private Double  hpExponentBase;
	private Double  dmgExponentBase;
	private Double  expLvlDivisor;
	private Double  expLvlExponent;
	private Integer sellAmount;
	private Integer teamCost;
	private Integer costToFullyHeal;
	private String  costToFullyHealExponent;
	private Integer secsToFullyHeal;
	private String  secsToFullyHealExponent;
	private Integer enhanceCostPerFeeder;
	private Double  enhanceCostExponent;
	private Integer secondsToEnhancePerFeeder;
	private Double  secondsToEnhancePerFeederExponent;
	private Double  pvpDropRate;
	private Integer strength;
	private Double  strengthExponent;

	public MonsterLevelInfoConfigPojo() {}

	public MonsterLevelInfoConfigPojo(MonsterLevelInfoConfigPojo value) {
		this.monsterId = value.monsterId;
		this.level = value.level;
		this.hp = value.hp;
		this.curLvlRequiredExp = value.curLvlRequiredExp;
		this.feederExp = value.feederExp;
		this.fireDmg = value.fireDmg;
		this.grassDmg = value.grassDmg;
		this.waterDmg = value.waterDmg;
		this.lightningDmg = value.lightningDmg;
		this.darknessDmg = value.darknessDmg;
		this.rockDmg = value.rockDmg;
		this.speed = value.speed;
		this.hpExponentBase = value.hpExponentBase;
		this.dmgExponentBase = value.dmgExponentBase;
		this.expLvlDivisor = value.expLvlDivisor;
		this.expLvlExponent = value.expLvlExponent;
		this.sellAmount = value.sellAmount;
		this.teamCost = value.teamCost;
		this.costToFullyHeal = value.costToFullyHeal;
		this.costToFullyHealExponent = value.costToFullyHealExponent;
		this.secsToFullyHeal = value.secsToFullyHeal;
		this.secsToFullyHealExponent = value.secsToFullyHealExponent;
		this.enhanceCostPerFeeder = value.enhanceCostPerFeeder;
		this.enhanceCostExponent = value.enhanceCostExponent;
		this.secondsToEnhancePerFeeder = value.secondsToEnhancePerFeeder;
		this.secondsToEnhancePerFeederExponent = value.secondsToEnhancePerFeederExponent;
		this.pvpDropRate = value.pvpDropRate;
		this.strength = value.strength;
		this.strengthExponent = value.strengthExponent;
	}

	public MonsterLevelInfoConfigPojo(
		Integer monsterId,
		Integer level,
		Integer hp,
		Integer curLvlRequiredExp,
		Integer feederExp,
		Integer fireDmg,
		Integer grassDmg,
		Integer waterDmg,
		Integer lightningDmg,
		Integer darknessDmg,
		Integer rockDmg,
		Byte    speed,
		Double  hpExponentBase,
		Double  dmgExponentBase,
		Double  expLvlDivisor,
		Double  expLvlExponent,
		Integer sellAmount,
		Integer teamCost,
		Integer costToFullyHeal,
		String  costToFullyHealExponent,
		Integer secsToFullyHeal,
		String  secsToFullyHealExponent,
		Integer enhanceCostPerFeeder,
		Double  enhanceCostExponent,
		Integer secondsToEnhancePerFeeder,
		Double  secondsToEnhancePerFeederExponent,
		Double  pvpDropRate,
		Integer strength,
		Double  strengthExponent
	) {
		this.monsterId = monsterId;
		this.level = level;
		this.hp = hp;
		this.curLvlRequiredExp = curLvlRequiredExp;
		this.feederExp = feederExp;
		this.fireDmg = fireDmg;
		this.grassDmg = grassDmg;
		this.waterDmg = waterDmg;
		this.lightningDmg = lightningDmg;
		this.darknessDmg = darknessDmg;
		this.rockDmg = rockDmg;
		this.speed = speed;
		this.hpExponentBase = hpExponentBase;
		this.dmgExponentBase = dmgExponentBase;
		this.expLvlDivisor = expLvlDivisor;
		this.expLvlExponent = expLvlExponent;
		this.sellAmount = sellAmount;
		this.teamCost = teamCost;
		this.costToFullyHeal = costToFullyHeal;
		this.costToFullyHealExponent = costToFullyHealExponent;
		this.secsToFullyHeal = secsToFullyHeal;
		this.secsToFullyHealExponent = secsToFullyHealExponent;
		this.enhanceCostPerFeeder = enhanceCostPerFeeder;
		this.enhanceCostExponent = enhanceCostExponent;
		this.secondsToEnhancePerFeeder = secondsToEnhancePerFeeder;
		this.secondsToEnhancePerFeederExponent = secondsToEnhancePerFeederExponent;
		this.pvpDropRate = pvpDropRate;
		this.strength = strength;
		this.strengthExponent = strengthExponent;
	}

	@Column(name = "monster_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMonsterId() {
		return this.monsterId;
	}

	@Override
	public MonsterLevelInfoConfigPojo setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
		return this;
	}

	@Column(name = "level", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getLevel() {
		return this.level;
	}

	@Override
	public MonsterLevelInfoConfigPojo setLevel(Integer level) {
		this.level = level;
		return this;
	}

	@Column(name = "hp", precision = 10)
	@Override
	public Integer getHp() {
		return this.hp;
	}

	@Override
	public MonsterLevelInfoConfigPojo setHp(Integer hp) {
		this.hp = hp;
		return this;
	}

	@Column(name = "cur_lvl_required_exp", precision = 10)
	@Override
	public Integer getCurLvlRequiredExp() {
		return this.curLvlRequiredExp;
	}

	@Override
	public MonsterLevelInfoConfigPojo setCurLvlRequiredExp(Integer curLvlRequiredExp) {
		this.curLvlRequiredExp = curLvlRequiredExp;
		return this;
	}

	@Column(name = "feeder_exp", precision = 10)
	@Override
	public Integer getFeederExp() {
		return this.feederExp;
	}

	@Override
	public MonsterLevelInfoConfigPojo setFeederExp(Integer feederExp) {
		this.feederExp = feederExp;
		return this;
	}

	@Column(name = "fire_dmg", precision = 10)
	@Override
	public Integer getFireDmg() {
		return this.fireDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setFireDmg(Integer fireDmg) {
		this.fireDmg = fireDmg;
		return this;
	}

	@Column(name = "grass_dmg", precision = 10)
	@Override
	public Integer getGrassDmg() {
		return this.grassDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setGrassDmg(Integer grassDmg) {
		this.grassDmg = grassDmg;
		return this;
	}

	@Column(name = "water_dmg", precision = 10)
	@Override
	public Integer getWaterDmg() {
		return this.waterDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setWaterDmg(Integer waterDmg) {
		this.waterDmg = waterDmg;
		return this;
	}

	@Column(name = "lightning_dmg", precision = 10)
	@Override
	public Integer getLightningDmg() {
		return this.lightningDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setLightningDmg(Integer lightningDmg) {
		this.lightningDmg = lightningDmg;
		return this;
	}

	@Column(name = "darkness_dmg", precision = 10)
	@Override
	public Integer getDarknessDmg() {
		return this.darknessDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setDarknessDmg(Integer darknessDmg) {
		this.darknessDmg = darknessDmg;
		return this;
	}

	@Column(name = "rock_dmg", precision = 10)
	@Override
	public Integer getRockDmg() {
		return this.rockDmg;
	}

	@Override
	public MonsterLevelInfoConfigPojo setRockDmg(Integer rockDmg) {
		this.rockDmg = rockDmg;
		return this;
	}

	@Column(name = "speed", precision = 3)
	@Override
	public Byte getSpeed() {
		return this.speed;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSpeed(Byte speed) {
		this.speed = speed;
		return this;
	}

	@Column(name = "hp_exponent_base", precision = 12)
	@Override
	public Double getHpExponentBase() {
		return this.hpExponentBase;
	}

	@Override
	public MonsterLevelInfoConfigPojo setHpExponentBase(Double hpExponentBase) {
		this.hpExponentBase = hpExponentBase;
		return this;
	}

	@Column(name = "dmg_exponent_base", precision = 12)
	@Override
	public Double getDmgExponentBase() {
		return this.dmgExponentBase;
	}

	@Override
	public MonsterLevelInfoConfigPojo setDmgExponentBase(Double dmgExponentBase) {
		this.dmgExponentBase = dmgExponentBase;
		return this;
	}

	@Column(name = "exp_lvl_divisor", precision = 12)
	@Override
	public Double getExpLvlDivisor() {
		return this.expLvlDivisor;
	}

	@Override
	public MonsterLevelInfoConfigPojo setExpLvlDivisor(Double expLvlDivisor) {
		this.expLvlDivisor = expLvlDivisor;
		return this;
	}

	@Column(name = "exp_lvl_exponent", precision = 12)
	@Override
	public Double getExpLvlExponent() {
		return this.expLvlExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setExpLvlExponent(Double expLvlExponent) {
		this.expLvlExponent = expLvlExponent;
		return this;
	}

	@Column(name = "sell_amount", precision = 10)
	@Override
	public Integer getSellAmount() {
		return this.sellAmount;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSellAmount(Integer sellAmount) {
		this.sellAmount = sellAmount;
		return this;
	}

	@Column(name = "team_cost", precision = 10)
	@Override
	public Integer getTeamCost() {
		return this.teamCost;
	}

	@Override
	public MonsterLevelInfoConfigPojo setTeamCost(Integer teamCost) {
		this.teamCost = teamCost;
		return this;
	}

	@Column(name = "cost_to_fully_heal", precision = 10)
	@Override
	public Integer getCostToFullyHeal() {
		return this.costToFullyHeal;
	}

	@Override
	public MonsterLevelInfoConfigPojo setCostToFullyHeal(Integer costToFullyHeal) {
		this.costToFullyHeal = costToFullyHeal;
		return this;
	}

	@Column(name = "cost_to_fully_heal_exponent", length = 45)
	@Size(max = 45)
	@Override
	public String getCostToFullyHealExponent() {
		return this.costToFullyHealExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setCostToFullyHealExponent(String costToFullyHealExponent) {
		this.costToFullyHealExponent = costToFullyHealExponent;
		return this;
	}

	@Column(name = "secs_to_fully_heal", precision = 10)
	@Override
	public Integer getSecsToFullyHeal() {
		return this.secsToFullyHeal;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSecsToFullyHeal(Integer secsToFullyHeal) {
		this.secsToFullyHeal = secsToFullyHeal;
		return this;
	}

	@Column(name = "secs_to_fully_heal_exponent", length = 45)
	@Size(max = 45)
	@Override
	public String getSecsToFullyHealExponent() {
		return this.secsToFullyHealExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSecsToFullyHealExponent(String secsToFullyHealExponent) {
		this.secsToFullyHealExponent = secsToFullyHealExponent;
		return this;
	}

	@Column(name = "enhance_cost_per_feeder", precision = 10)
	@Override
	public Integer getEnhanceCostPerFeeder() {
		return this.enhanceCostPerFeeder;
	}

	@Override
	public MonsterLevelInfoConfigPojo setEnhanceCostPerFeeder(Integer enhanceCostPerFeeder) {
		this.enhanceCostPerFeeder = enhanceCostPerFeeder;
		return this;
	}

	@Column(name = "enhance_cost_exponent", precision = 12)
	@Override
	public Double getEnhanceCostExponent() {
		return this.enhanceCostExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setEnhanceCostExponent(Double enhanceCostExponent) {
		this.enhanceCostExponent = enhanceCostExponent;
		return this;
	}

	@Column(name = "seconds_to_enhance_per_feeder", precision = 10)
	@Override
	public Integer getSecondsToEnhancePerFeeder() {
		return this.secondsToEnhancePerFeeder;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSecondsToEnhancePerFeeder(Integer secondsToEnhancePerFeeder) {
		this.secondsToEnhancePerFeeder = secondsToEnhancePerFeeder;
		return this;
	}

	@Column(name = "seconds_to_enhance_per_feeder_exponent", precision = 12)
	@Override
	public Double getSecondsToEnhancePerFeederExponent() {
		return this.secondsToEnhancePerFeederExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setSecondsToEnhancePerFeederExponent(Double secondsToEnhancePerFeederExponent) {
		this.secondsToEnhancePerFeederExponent = secondsToEnhancePerFeederExponent;
		return this;
	}

	@Column(name = "pvp_drop_rate", precision = 12)
	@Override
	public Double getPvpDropRate() {
		return this.pvpDropRate;
	}

	@Override
	public MonsterLevelInfoConfigPojo setPvpDropRate(Double pvpDropRate) {
		this.pvpDropRate = pvpDropRate;
		return this;
	}

	@Column(name = "strength", precision = 10)
	@Override
	public Integer getStrength() {
		return this.strength;
	}

	@Override
	public MonsterLevelInfoConfigPojo setStrength(Integer strength) {
		this.strength = strength;
		return this;
	}

	@Column(name = "strength_exponent", precision = 12)
	@Override
	public Double getStrengthExponent() {
		return this.strengthExponent;
	}

	@Override
	public MonsterLevelInfoConfigPojo setStrengthExponent(Double strengthExponent) {
		this.strengthExponent = strengthExponent;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterLevelInfoConfig from) {
		setMonsterId(from.getMonsterId());
		setLevel(from.getLevel());
		setHp(from.getHp());
		setCurLvlRequiredExp(from.getCurLvlRequiredExp());
		setFeederExp(from.getFeederExp());
		setFireDmg(from.getFireDmg());
		setGrassDmg(from.getGrassDmg());
		setWaterDmg(from.getWaterDmg());
		setLightningDmg(from.getLightningDmg());
		setDarknessDmg(from.getDarknessDmg());
		setRockDmg(from.getRockDmg());
		setSpeed(from.getSpeed());
		setHpExponentBase(from.getHpExponentBase());
		setDmgExponentBase(from.getDmgExponentBase());
		setExpLvlDivisor(from.getExpLvlDivisor());
		setExpLvlExponent(from.getExpLvlExponent());
		setSellAmount(from.getSellAmount());
		setTeamCost(from.getTeamCost());
		setCostToFullyHeal(from.getCostToFullyHeal());
		setCostToFullyHealExponent(from.getCostToFullyHealExponent());
		setSecsToFullyHeal(from.getSecsToFullyHeal());
		setSecsToFullyHealExponent(from.getSecsToFullyHealExponent());
		setEnhanceCostPerFeeder(from.getEnhanceCostPerFeeder());
		setEnhanceCostExponent(from.getEnhanceCostExponent());
		setSecondsToEnhancePerFeeder(from.getSecondsToEnhancePerFeeder());
		setSecondsToEnhancePerFeederExponent(from.getSecondsToEnhancePerFeederExponent());
		setPvpDropRate(from.getPvpDropRate());
		setStrength(from.getStrength());
		setStrengthExponent(from.getStrengthExponent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterLevelInfoConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterLevelInfoConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterLevelInfoConfigRecord();
		poop.from(this);
		return "MonsterLevelInfoConfigPojo[" + poop.valuesRow() + "]";
	}
}
