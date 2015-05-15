/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventTierRewardConfig;

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
@Table(name = "mini_event_tier_reward_config", schema = "mobsters")
public class MiniEventTierRewardConfig implements IMiniEventTierRewardConfig {

	private static final long serialVersionUID = -331209685;

	private Integer id;
	private Integer miniEventForPlayerLvlId;
	private Integer rewardId;
	private Integer rewardTier;

	public MiniEventTierRewardConfig() {}

	public MiniEventTierRewardConfig(MiniEventTierRewardConfig value) {
		this.id = value.id;
		this.miniEventForPlayerLvlId = value.miniEventForPlayerLvlId;
		this.rewardId = value.rewardId;
		this.rewardTier = value.rewardTier;
	}

	public MiniEventTierRewardConfig(
		Integer id,
		Integer miniEventForPlayerLvlId,
		Integer rewardId,
		Integer rewardTier
	) {
		this.id = id;
		this.miniEventForPlayerLvlId = miniEventForPlayerLvlId;
		this.rewardId = rewardId;
		this.rewardTier = rewardTier;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public MiniEventTierRewardConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "mini_event_for_player_lvl_id", precision = 10)
	@Override
	public Integer getMiniEventForPlayerLvlId() {
		return this.miniEventForPlayerLvlId;
	}

	@Override
	public MiniEventTierRewardConfig setMiniEventForPlayerLvlId(Integer miniEventForPlayerLvlId) {
		this.miniEventForPlayerLvlId = miniEventForPlayerLvlId;
		return this;
	}

	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return this.rewardId;
	}

	@Override
	public MiniEventTierRewardConfig setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
		return this;
	}

	@Column(name = "reward_tier", precision = 10)
	@Override
	public Integer getRewardTier() {
		return this.rewardTier;
	}

	@Override
	public MiniEventTierRewardConfig setRewardTier(Integer rewardTier) {
		this.rewardTier = rewardTier;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventTierRewardConfig from) {
		setId(from.getId());
		setMiniEventForPlayerLvlId(from.getMiniEventForPlayerLvlId());
		setRewardId(from.getRewardId());
		setRewardTier(from.getRewardTier());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventTierRewardConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
