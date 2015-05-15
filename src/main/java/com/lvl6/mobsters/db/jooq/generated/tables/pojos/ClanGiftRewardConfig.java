/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanGiftRewardConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
@Table(name = "clan_gift_reward_config", schema = "mobsters")
public class ClanGiftRewardConfig implements IClanGiftRewardConfig {

	private static final long serialVersionUID = 2014564488;

	private Integer id;
	private Integer clanGiftId;
	private Integer rewardId;
	private Double  chanceOfDrop;

	public ClanGiftRewardConfig() {}

	public ClanGiftRewardConfig(ClanGiftRewardConfig value) {
		this.id = value.id;
		this.clanGiftId = value.clanGiftId;
		this.rewardId = value.rewardId;
		this.chanceOfDrop = value.chanceOfDrop;
	}

	public ClanGiftRewardConfig(
		Integer id,
		Integer clanGiftId,
		Integer rewardId,
		Double  chanceOfDrop
	) {
		this.id = id;
		this.clanGiftId = clanGiftId;
		this.rewardId = rewardId;
		this.chanceOfDrop = chanceOfDrop;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public ClanGiftRewardConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "clan_gift_id", precision = 10)
	@Override
	public Integer getClanGiftId() {
		return this.clanGiftId;
	}

	@Override
	public ClanGiftRewardConfig setClanGiftId(Integer clanGiftId) {
		this.clanGiftId = clanGiftId;
		return this;
	}

	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return this.rewardId;
	}

	@Override
	public ClanGiftRewardConfig setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
		return this;
	}

	@Column(name = "chance_of_drop", precision = 12)
	@Override
	public Double getChanceOfDrop() {
		return this.chanceOfDrop;
	}

	@Override
	public ClanGiftRewardConfig setChanceOfDrop(Double chanceOfDrop) {
		this.chanceOfDrop = chanceOfDrop;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanGiftRewardConfig from) {
		setId(from.getId());
		setClanGiftId(from.getClanGiftId());
		setRewardId(from.getRewardId());
		setChanceOfDrop(from.getChanceOfDrop());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanGiftRewardConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
