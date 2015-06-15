/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

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
@Table(name = "tournament_reward_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "min_rank", "max_rank"})
})
public interface ITournamentRewardConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.tournament_reward_config.id</code>.
	 */
	public ITournamentRewardConfig setId(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.id</code>.
	 */
	@Column(name = "id", nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.min_rank</code>.
	 */
	public ITournamentRewardConfig setMinRank(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.min_rank</code>.
	 */
	@Column(name = "min_rank", nullable = false, precision = 10)
	@NotNull
	public Integer getMinRank();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.max_rank</code>.
	 */
	public ITournamentRewardConfig setMaxRank(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.max_rank</code>.
	 */
	@Column(name = "max_rank", nullable = false, precision = 10)
	@NotNull
	public Integer getMaxRank();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.gold_rewarded</code>.
	 */
	public ITournamentRewardConfig setGoldRewarded(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.gold_rewarded</code>.
	 */
	@Column(name = "gold_rewarded", precision = 10)
	public Integer getGoldRewarded();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.background_image_name</code>.
	 */
	public ITournamentRewardConfig setBackgroundImageName(String value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.background_image_name</code>.
	 */
	@Column(name = "background_image_name", length = 45)
	@Size(max = 45)
	public String getBackgroundImageName();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.prize_image_name</code>.
	 */
	public ITournamentRewardConfig setPrizeImageName(String value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.prize_image_name</code>.
	 */
	@Column(name = "prize_image_name", length = 45)
	@Size(max = 45)
	public String getPrizeImageName();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.blue</code>.
	 */
	public ITournamentRewardConfig setBlue(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.blue</code>.
	 */
	@Column(name = "blue", precision = 10)
	public Integer getBlue();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.green</code>.
	 */
	public ITournamentRewardConfig setGreen(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.green</code>.
	 */
	@Column(name = "green", precision = 10)
	public Integer getGreen();

	/**
	 * Setter for <code>mobsters.tournament_reward_config.red</code>.
	 */
	public ITournamentRewardConfig setRed(Integer value);

	/**
	 * Getter for <code>mobsters.tournament_reward_config.red</code>.
	 */
	@Column(name = "red", precision = 10)
	public Integer getRed();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ITournamentRewardConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentRewardConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ITournamentRewardConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITournamentRewardConfig> E into(E into);
}