/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserClanBossContribution;

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
@Table(name = "user_clan_boss_contribution", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "clan_id", "boss_id"})
})
public class UserClanBossContribution implements IUserClanBossContribution {

	private static final long serialVersionUID = -414292536;

	private String  userId;
	private String  clanId;
	private Integer bossId;
	private Integer totalDamageDone;
	private Integer totalEnergyUsed;
	private Integer numRunesOne;
	private Integer numRunesTwo;
	private Integer numRunesThree;
	private Integer numRunesFour;
	private Integer numRunesFive;

	public UserClanBossContribution() {}

	public UserClanBossContribution(UserClanBossContribution value) {
		this.userId = value.userId;
		this.clanId = value.clanId;
		this.bossId = value.bossId;
		this.totalDamageDone = value.totalDamageDone;
		this.totalEnergyUsed = value.totalEnergyUsed;
		this.numRunesOne = value.numRunesOne;
		this.numRunesTwo = value.numRunesTwo;
		this.numRunesThree = value.numRunesThree;
		this.numRunesFour = value.numRunesFour;
		this.numRunesFive = value.numRunesFive;
	}

	public UserClanBossContribution(
		String  userId,
		String  clanId,
		Integer bossId,
		Integer totalDamageDone,
		Integer totalEnergyUsed,
		Integer numRunesOne,
		Integer numRunesTwo,
		Integer numRunesThree,
		Integer numRunesFour,
		Integer numRunesFive
	) {
		this.userId = userId;
		this.clanId = clanId;
		this.bossId = bossId;
		this.totalDamageDone = totalDamageDone;
		this.totalEnergyUsed = totalEnergyUsed;
		this.numRunesOne = numRunesOne;
		this.numRunesTwo = numRunesTwo;
		this.numRunesThree = numRunesThree;
		this.numRunesFour = numRunesFour;
		this.numRunesFive = numRunesFive;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UserClanBossContribution setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public UserClanBossContribution setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "boss_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getBossId() {
		return this.bossId;
	}

	@Override
	public UserClanBossContribution setBossId(Integer bossId) {
		this.bossId = bossId;
		return this;
	}

	@Column(name = "total_damage_done", precision = 10)
	@Override
	public Integer getTotalDamageDone() {
		return this.totalDamageDone;
	}

	@Override
	public UserClanBossContribution setTotalDamageDone(Integer totalDamageDone) {
		this.totalDamageDone = totalDamageDone;
		return this;
	}

	@Column(name = "total_energy_used", precision = 10)
	@Override
	public Integer getTotalEnergyUsed() {
		return this.totalEnergyUsed;
	}

	@Override
	public UserClanBossContribution setTotalEnergyUsed(Integer totalEnergyUsed) {
		this.totalEnergyUsed = totalEnergyUsed;
		return this;
	}

	@Column(name = "num_runes_one", precision = 10)
	@Override
	public Integer getNumRunesOne() {
		return this.numRunesOne;
	}

	@Override
	public UserClanBossContribution setNumRunesOne(Integer numRunesOne) {
		this.numRunesOne = numRunesOne;
		return this;
	}

	@Column(name = "num_runes_two", precision = 10)
	@Override
	public Integer getNumRunesTwo() {
		return this.numRunesTwo;
	}

	@Override
	public UserClanBossContribution setNumRunesTwo(Integer numRunesTwo) {
		this.numRunesTwo = numRunesTwo;
		return this;
	}

	@Column(name = "num_runes_three", precision = 10)
	@Override
	public Integer getNumRunesThree() {
		return this.numRunesThree;
	}

	@Override
	public UserClanBossContribution setNumRunesThree(Integer numRunesThree) {
		this.numRunesThree = numRunesThree;
		return this;
	}

	@Column(name = "num_runes_four", precision = 10)
	@Override
	public Integer getNumRunesFour() {
		return this.numRunesFour;
	}

	@Override
	public UserClanBossContribution setNumRunesFour(Integer numRunesFour) {
		this.numRunesFour = numRunesFour;
		return this;
	}

	@Column(name = "num_runes_five", precision = 10)
	@Override
	public Integer getNumRunesFive() {
		return this.numRunesFive;
	}

	@Override
	public UserClanBossContribution setNumRunesFive(Integer numRunesFive) {
		this.numRunesFive = numRunesFive;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserClanBossContribution from) {
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setBossId(from.getBossId());
		setTotalDamageDone(from.getTotalDamageDone());
		setTotalEnergyUsed(from.getTotalEnergyUsed());
		setNumRunesOne(from.getNumRunesOne());
		setNumRunesTwo(from.getNumRunesTwo());
		setNumRunesThree(from.getNumRunesThree());
		setNumRunesFour(from.getNumRunesFour());
		setNumRunesFive(from.getNumRunesFive());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserClanBossContribution> E into(E into) {
		into.from(this);
		return into;
	}
}
