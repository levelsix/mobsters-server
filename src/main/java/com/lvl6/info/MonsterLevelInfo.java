package com.lvl6.info;

import java.io.Serializable;

public class MonsterLevelInfo implements Serializable {

	private static final long serialVersionUID = 2977557480137942673L;
	
	private int monsterId;
	private int level;
	private int hp;
	private int curLvlRequiredExp;
	private int feederExp;
	private int fireDmg;
	private int grassDmg;
	private int waterDmg;
	private int lightningDmg;
	private int darknessDmg;
	private int rockDmg;
	private int speed;
	private float hpExponentBase;
	private float dmgExponentBase;
	private float expLvlDivisor;
	private float expLvlExponent;
	private int sellAmount;
	private int teamCost;
	private int costToFullyHeal;
	private int secsToFullyHeal;
	private int enhanceCostPerFeeder;
	private float enhanceCostExponent;
	private float enhanceExpPerSecond;
	private float enhanceExpPerSecondExponent;

	public MonsterLevelInfo()
	{
		super();
	}

	public MonsterLevelInfo(
		int monsterId,
		int level,
		int hp,
		int curLvlRequiredExp,
		int feederExp,
		int fireDmg,
		int grassDmg,
		int waterDmg,
		int lightningDmg,
		int darknessDmg,
		int rockDmg,
		int speed,
		float hpExponentBase,
		float dmgExponentBase,
		float expLvlDivisor,
		float expLvlExponent,
		int sellAmount,
		int teamCost,
		int costToFullyHeal,
		int secsToFullyHeal,
		int enhanceCostPerFeeder,
		float enhanceCostExponent,
		float enhanceExpPerSecond,
		float enhanceExpPerSecondExponent )
	{
		super();
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
		this.secsToFullyHeal = secsToFullyHeal;
		this.enhanceCostPerFeeder = enhanceCostPerFeeder;
		this.enhanceCostExponent = enhanceCostExponent;
		this.enhanceExpPerSecond = enhanceExpPerSecond;
		this.enhanceExpPerSecondExponent = enhanceExpPerSecondExponent;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getCurLvlRequiredExp() {
		return curLvlRequiredExp;
	}

	public void setCurLvlRequiredExp(int curLvlRequiredExp) {
		this.curLvlRequiredExp = curLvlRequiredExp;
	}

	public int getFeederExp() {
		return feederExp;
	}

	public void setFeederExp(int feederExp) {
		this.feederExp = feederExp;
	}

	public int getFireDmg() {
		return fireDmg;
	}

	public void setFireDmg(int fireDmg) {
		this.fireDmg = fireDmg;
	}

	public int getGrassDmg() {
		return grassDmg;
	}

	public void setGrassDmg(int grassDmg) {
		this.grassDmg = grassDmg;
	}

	public int getWaterDmg() {
		return waterDmg;
	}

	public void setWaterDmg(int waterDmg) {
		this.waterDmg = waterDmg;
	}

	public int getLightningDmg() {
		return lightningDmg;
	}

	public void setLightningDmg(int lightningDmg) {
		this.lightningDmg = lightningDmg;
	}

	public int getDarknessDmg() {
		return darknessDmg;
	}

	public void setDarknessDmg(int darknessDmg) {
		this.darknessDmg = darknessDmg;
	}

	public int getRockDmg() {
		return rockDmg;
	}

	public void setRockDmg(int rockDmg) {
		this.rockDmg = rockDmg;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getHpExponentBase() {
		return hpExponentBase;
	}

	public void setHpExponentBase(float hpExponentBase) {
		this.hpExponentBase = hpExponentBase;
	}

	public float getDmgExponentBase() {
		return dmgExponentBase;
	}

	public void setDmgExponentBase(float dmgExponentBase) {
		this.dmgExponentBase = dmgExponentBase;
	}

	public float getExpLvlDivisor() {
		return expLvlDivisor;
	}

	public void setExpLvlDivisor(float expLvlDivisor) {
		this.expLvlDivisor = expLvlDivisor;
	}

	public float getExpLvlExponent() {
		return expLvlExponent;
	}

	public void setExpLvlExponent(float expLvlExponent) {
		this.expLvlExponent = expLvlExponent;
	}

	public int getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(int sellAmount) {
		this.sellAmount = sellAmount;
	}

	public int getTeamCost()
	{
		return teamCost;
	}

	public void setTeamCost( int teamCost )
	{
		this.teamCost = teamCost;
	}

	public int getCostToFullyHeal()
	{
		return costToFullyHeal;
	}

	public void setCostToFullyHeal( int costToFullyHeal )
	{
		this.costToFullyHeal = costToFullyHeal;
	}

	public int getSecsToFullyHeal()
	{
		return secsToFullyHeal;
	}

	public void setSecsToFullyHeal( int secsToFullyHeal )
	{
		this.secsToFullyHeal = secsToFullyHeal;
	}

	public int getEnhanceCostPerFeeder()
	{
		return enhanceCostPerFeeder;
	}

	public void setEnhanceCostPerFeeder( int enhanceCostPerFeeder )
	{
		this.enhanceCostPerFeeder = enhanceCostPerFeeder;
	}

	public float getEnhanceCostExponent()
	{
		return enhanceCostExponent;
	}

	public void setEnhanceCostExponent( float enhanceCostExponent )
	{
		this.enhanceCostExponent = enhanceCostExponent;
	}

	public float getEnhanceExpPerSecond()
	{
		return enhanceExpPerSecond;
	}

	public void setEnhanceExpPerSecond( float enhanceExpPerSecond )
	{
		this.enhanceExpPerSecond = enhanceExpPerSecond;
	}

	public float getEnhanceExpPerSecondExponent()
	{
		return enhanceExpPerSecondExponent;
	}

	public void setEnhanceExpPerSecondExponent( float enhanceExpPerSecondExponent )
	{
		this.enhanceExpPerSecondExponent = enhanceExpPerSecondExponent;
	}

	@Override
	public String toString()
	{
		return "MonsterLevelInfo [monsterId="
			+ monsterId
			+ ", level="
			+ level
			+ ", hp="
			+ hp
			+ ", curLvlRequiredExp="
			+ curLvlRequiredExp
			+ ", feederExp="
			+ feederExp
			+ ", fireDmg="
			+ fireDmg
			+ ", grassDmg="
			+ grassDmg
			+ ", waterDmg="
			+ waterDmg
			+ ", lightningDmg="
			+ lightningDmg
			+ ", darknessDmg="
			+ darknessDmg
			+ ", rockDmg="
			+ rockDmg
			+ ", speed="
			+ speed
			+ ", hpExponentBase="
			+ hpExponentBase
			+ ", dmgExponentBase="
			+ dmgExponentBase
			+ ", expLvlDivisor="
			+ expLvlDivisor
			+ ", expLvlExponent="
			+ expLvlExponent
			+ ", sellAmount="
			+ sellAmount
			+ ", teamCost="
			+ teamCost
			+ ", costToFullyHeal="
			+ costToFullyHeal
			+ ", secsToFullyHeal="
			+ secsToFullyHeal
			+ ", enhanceCostPerFeeder="
			+ enhanceCostPerFeeder
			+ ", enhanceCostExponent="
			+ enhanceCostExponent
			+ ", enhanceExpPerSecond="
			+ enhanceExpPerSecond
			+ ", enhanceExpPerSecondExponent="
			+ enhanceExpPerSecondExponent
			+ "]";
	}
	
}
