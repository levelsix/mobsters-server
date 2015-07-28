/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
import java.sql.Timestamp;

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
@Table(name = "pvp_battle_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"attacker_id", "defender_id", "battle_end_time"})
})
public interface IPvpBattleHistory extends Serializable {

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_id</code>.
	 */
	public IPvpBattleHistory setAttackerId(String value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_id</code>.
	 */
	@Column(name = "attacker_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getAttackerId();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_id</code>.
	 */
	public IPvpBattleHistory setDefenderId(String value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_id</code>.
	 */
	@Column(name = "defender_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getDefenderId();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.battle_end_time</code>.
	 */
	public IPvpBattleHistory setBattleEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.battle_end_time</code>.
	 */
	@Column(name = "battle_end_time", nullable = false)
	@NotNull
	public Timestamp getBattleEndTime();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.battle_start_time</code>.
	 */
	public IPvpBattleHistory setBattleStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.battle_start_time</code>.
	 */
	@Column(name = "battle_start_time")
	public Timestamp getBattleStartTime();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_change</code>.
	 */
	public IPvpBattleHistory setAttackerEloChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_change</code>.
	 */
	@Column(name = "attacker_elo_change", precision = 10)
	public Integer getAttackerEloChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_before</code>.
	 */
	public IPvpBattleHistory setAttackerEloBefore(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_before</code>.
	 */
	@Column(name = "attacker_elo_before", precision = 10)
	public Integer getAttackerEloBefore();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_after</code>.
	 */
	public IPvpBattleHistory setAttackerEloAfter(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_after</code>.
	 */
	@Column(name = "attacker_elo_after", precision = 10)
	public Integer getAttackerEloAfter();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_change</code>.
	 */
	public IPvpBattleHistory setDefenderEloChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_change</code>.
	 */
	@Column(name = "defender_elo_change", precision = 10)
	public Integer getDefenderEloChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_before</code>.
	 */
	public IPvpBattleHistory setDefenderEloBefore(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_before</code>.
	 */
	@Column(name = "defender_elo_before", precision = 10)
	public Integer getDefenderEloBefore();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_after</code>.
	 */
	public IPvpBattleHistory setDefenderEloAfter(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_after</code>.
	 */
	@Column(name = "defender_elo_after", precision = 10)
	public Integer getDefenderEloAfter();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_prev_league</code>.
	 */
	public IPvpBattleHistory setAttackerPrevLeague(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_prev_league</code>.
	 */
	@Column(name = "attacker_prev_league", precision = 10)
	public Integer getAttackerPrevLeague();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cur_league</code>.
	 */
	public IPvpBattleHistory setAttackerCurLeague(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cur_league</code>.
	 */
	@Column(name = "attacker_cur_league", precision = 10)
	public Integer getAttackerCurLeague();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_prev_league</code>.
	 */
	public IPvpBattleHistory setDefenderPrevLeague(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_prev_league</code>.
	 */
	@Column(name = "defender_prev_league", precision = 10)
	public Integer getDefenderPrevLeague();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cur_league</code>.
	 */
	public IPvpBattleHistory setDefenderCurLeague(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cur_league</code>.
	 */
	@Column(name = "defender_cur_league", precision = 10)
	public Integer getDefenderCurLeague();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_prev_rank</code>.
	 */
	public IPvpBattleHistory setAttackerPrevRank(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_prev_rank</code>.
	 */
	@Column(name = "attacker_prev_rank", precision = 10)
	public Integer getAttackerPrevRank();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cur_rank</code>.
	 */
	public IPvpBattleHistory setAttackerCurRank(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cur_rank</code>.
	 */
	@Column(name = "attacker_cur_rank", precision = 10)
	public Integer getAttackerCurRank();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_prev_rank</code>.
	 */
	public IPvpBattleHistory setDefenderPrevRank(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_prev_rank</code>.
	 */
	@Column(name = "defender_prev_rank", precision = 10)
	public Integer getDefenderPrevRank();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cur_rank</code>.
	 */
	public IPvpBattleHistory setDefenderCurRank(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cur_rank</code>.
	 */
	@Column(name = "defender_cur_rank", precision = 10)
	public Integer getDefenderCurRank();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cash_change</code>.
	 */
	public IPvpBattleHistory setAttackerCashChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cash_change</code>.
	 */
	@Column(name = "attacker_cash_change", precision = 10)
	public Integer getAttackerCashChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cash_change</code>.
	 */
	public IPvpBattleHistory setDefenderCashChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cash_change</code>.
	 */
	@Column(name = "defender_cash_change", precision = 10)
	public Integer getDefenderCashChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_oil_change</code>.
	 */
	public IPvpBattleHistory setAttackerOilChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_oil_change</code>.
	 */
	@Column(name = "attacker_oil_change", precision = 10)
	public Integer getAttackerOilChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_oil_change</code>.
	 */
	public IPvpBattleHistory setDefenderOilChange(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_oil_change</code>.
	 */
	@Column(name = "defender_oil_change", precision = 10)
	public Integer getDefenderOilChange();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cash_stolen_from_storage</code>.
	 */
	public IPvpBattleHistory setCashStolenFromStorage(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cash_stolen_from_storage</code>.
	 */
	@Column(name = "cash_stolen_from_storage", precision = 10)
	public Integer getCashStolenFromStorage();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cash_stolen_from_generators</code>.
	 */
	public IPvpBattleHistory setCashStolenFromGenerators(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cash_stolen_from_generators</code>.
	 */
	@Column(name = "cash_stolen_from_generators", precision = 10)
	public Integer getCashStolenFromGenerators();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.oil_stolen_from_storage</code>.
	 */
	public IPvpBattleHistory setOilStolenFromStorage(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.oil_stolen_from_storage</code>.
	 */
	@Column(name = "oil_stolen_from_storage", precision = 10)
	public Integer getOilStolenFromStorage();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.oil_stolen_from_generators</code>.
	 */
	public IPvpBattleHistory setOilStolenFromGenerators(Integer value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.oil_stolen_from_generators</code>.
	 */
	@Column(name = "oil_stolen_from_generators", precision = 10)
	public Integer getOilStolenFromGenerators();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_won</code>.
	 */
	public IPvpBattleHistory setAttackerWon(Boolean value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_won</code>.
	 */
	@Column(name = "attacker_won", precision = 1)
	public Boolean getAttackerWon();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cancelled</code>.
	 */
	public IPvpBattleHistory setCancelled(Boolean value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cancelled</code>.
	 */
	@Column(name = "cancelled", precision = 1)
	public Boolean getCancelled();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.exacted_revenge</code>.
	 */
	public IPvpBattleHistory setExactedRevenge(Boolean value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.exacted_revenge</code>.
	 */
	@Column(name = "exacted_revenge", precision = 1)
	public Boolean getExactedRevenge();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.display_to_defender</code>. DEPRECATED 12-12-14 ALWAYS TRUE
	 */
	public IPvpBattleHistory setDisplayToDefender(Boolean value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.display_to_defender</code>. DEPRECATED 12-12-14 ALWAYS TRUE
	 */
	@Column(name = "display_to_defender", precision = 1)
	public Boolean getDisplayToDefender();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.pvp_dmg_multiplier</code>.
	 */
	public IPvpBattleHistory setPvpDmgMultiplier(Double value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.pvp_dmg_multiplier</code>.
	 */
	@Column(name = "pvp_dmg_multiplier", precision = 12)
	public Double getPvpDmgMultiplier();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.clan_avenged</code>.
	 */
	public IPvpBattleHistory setClanAvenged(Boolean value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.clan_avenged</code>.
	 */
	@Column(name = "clan_avenged", precision = 1)
	public Boolean getClanAvenged();

	/**
	 * Setter for <code>mobsters.pvp_battle_history.replay_id</code>.
	 */
	public IPvpBattleHistory setReplayId(String value);

	/**
	 * Getter for <code>mobsters.pvp_battle_history.replay_id</code>.
	 */
	@Column(name = "replay_id", length = 36)
	@Size(max = 36)
	public String getReplayId();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IPvpBattleHistory
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IPvpBattleHistory from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IPvpBattleHistory
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IPvpBattleHistory> E into(E into);
}