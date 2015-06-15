/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IPvpBattleHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Record3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class PvpBattleHistoryRecord extends UpdatableRecordImpl<PvpBattleHistoryRecord> implements IPvpBattleHistory {

	private static final long serialVersionUID = 1121818523;

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_id</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_id</code>.
	 */
	@Column(name = "attacker_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getAttackerId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_id</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_id</code>.
	 */
	@Column(name = "defender_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getDefenderId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.battle_end_time</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setBattleEndTime(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.battle_end_time</code>.
	 */
	@Column(name = "battle_end_time", nullable = false)
	@NotNull
	@Override
	public Timestamp getBattleEndTime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.battle_start_time</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setBattleStartTime(Timestamp value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.battle_start_time</code>.
	 */
	@Column(name = "battle_start_time")
	@Override
	public Timestamp getBattleStartTime() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerEloChange(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_change</code>.
	 */
	@Column(name = "attacker_elo_change", precision = 10)
	@Override
	public Integer getAttackerEloChange() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_before</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerEloBefore(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_before</code>.
	 */
	@Column(name = "attacker_elo_before", precision = 10)
	@Override
	public Integer getAttackerEloBefore() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_elo_after</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerEloAfter(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_elo_after</code>.
	 */
	@Column(name = "attacker_elo_after", precision = 10)
	@Override
	public Integer getAttackerEloAfter() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderEloChange(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_change</code>.
	 */
	@Column(name = "defender_elo_change", precision = 10)
	@Override
	public Integer getDefenderEloChange() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_before</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderEloBefore(Integer value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_before</code>.
	 */
	@Column(name = "defender_elo_before", precision = 10)
	@Override
	public Integer getDefenderEloBefore() {
		return (Integer) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_elo_after</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderEloAfter(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_elo_after</code>.
	 */
	@Column(name = "defender_elo_after", precision = 10)
	@Override
	public Integer getDefenderEloAfter() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_prev_league</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerPrevLeague(Integer value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_prev_league</code>.
	 */
	@Column(name = "attacker_prev_league", precision = 10)
	@Override
	public Integer getAttackerPrevLeague() {
		return (Integer) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cur_league</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerCurLeague(Integer value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cur_league</code>.
	 */
	@Column(name = "attacker_cur_league", precision = 10)
	@Override
	public Integer getAttackerCurLeague() {
		return (Integer) getValue(11);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_prev_league</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderPrevLeague(Integer value) {
		setValue(12, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_prev_league</code>.
	 */
	@Column(name = "defender_prev_league", precision = 10)
	@Override
	public Integer getDefenderPrevLeague() {
		return (Integer) getValue(12);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cur_league</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderCurLeague(Integer value) {
		setValue(13, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cur_league</code>.
	 */
	@Column(name = "defender_cur_league", precision = 10)
	@Override
	public Integer getDefenderCurLeague() {
		return (Integer) getValue(13);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_prev_rank</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerPrevRank(Integer value) {
		setValue(14, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_prev_rank</code>.
	 */
	@Column(name = "attacker_prev_rank", precision = 10)
	@Override
	public Integer getAttackerPrevRank() {
		return (Integer) getValue(14);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cur_rank</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerCurRank(Integer value) {
		setValue(15, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cur_rank</code>.
	 */
	@Column(name = "attacker_cur_rank", precision = 10)
	@Override
	public Integer getAttackerCurRank() {
		return (Integer) getValue(15);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_prev_rank</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderPrevRank(Integer value) {
		setValue(16, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_prev_rank</code>.
	 */
	@Column(name = "defender_prev_rank", precision = 10)
	@Override
	public Integer getDefenderPrevRank() {
		return (Integer) getValue(16);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cur_rank</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderCurRank(Integer value) {
		setValue(17, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cur_rank</code>.
	 */
	@Column(name = "defender_cur_rank", precision = 10)
	@Override
	public Integer getDefenderCurRank() {
		return (Integer) getValue(17);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_cash_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerCashChange(Integer value) {
		setValue(18, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_cash_change</code>.
	 */
	@Column(name = "attacker_cash_change", precision = 10)
	@Override
	public Integer getAttackerCashChange() {
		return (Integer) getValue(18);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_cash_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderCashChange(Integer value) {
		setValue(19, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_cash_change</code>.
	 */
	@Column(name = "defender_cash_change", precision = 10)
	@Override
	public Integer getDefenderCashChange() {
		return (Integer) getValue(19);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_oil_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerOilChange(Integer value) {
		setValue(20, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_oil_change</code>.
	 */
	@Column(name = "attacker_oil_change", precision = 10)
	@Override
	public Integer getAttackerOilChange() {
		return (Integer) getValue(20);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.defender_oil_change</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setDefenderOilChange(Integer value) {
		setValue(21, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.defender_oil_change</code>.
	 */
	@Column(name = "defender_oil_change", precision = 10)
	@Override
	public Integer getDefenderOilChange() {
		return (Integer) getValue(21);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cash_stolen_from_storage</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setCashStolenFromStorage(Integer value) {
		setValue(22, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cash_stolen_from_storage</code>.
	 */
	@Column(name = "cash_stolen_from_storage", precision = 10)
	@Override
	public Integer getCashStolenFromStorage() {
		return (Integer) getValue(22);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cash_stolen_from_generators</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setCashStolenFromGenerators(Integer value) {
		setValue(23, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cash_stolen_from_generators</code>.
	 */
	@Column(name = "cash_stolen_from_generators", precision = 10)
	@Override
	public Integer getCashStolenFromGenerators() {
		return (Integer) getValue(23);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.oil_stolen_from_storage</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setOilStolenFromStorage(Integer value) {
		setValue(24, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.oil_stolen_from_storage</code>.
	 */
	@Column(name = "oil_stolen_from_storage", precision = 10)
	@Override
	public Integer getOilStolenFromStorage() {
		return (Integer) getValue(24);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.oil_stolen_from_generators</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setOilStolenFromGenerators(Integer value) {
		setValue(25, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.oil_stolen_from_generators</code>.
	 */
	@Column(name = "oil_stolen_from_generators", precision = 10)
	@Override
	public Integer getOilStolenFromGenerators() {
		return (Integer) getValue(25);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.attacker_won</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setAttackerWon(Boolean value) {
		setValue(26, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.attacker_won</code>.
	 */
	@Column(name = "attacker_won", precision = 1)
	@Override
	public Boolean getAttackerWon() {
		return (Boolean) getValue(26);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.cancelled</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setCancelled(Boolean value) {
		setValue(27, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.cancelled</code>.
	 */
	@Column(name = "cancelled", precision = 1)
	@Override
	public Boolean getCancelled() {
		return (Boolean) getValue(27);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.exacted_revenge</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setExactedRevenge(Boolean value) {
		setValue(28, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.exacted_revenge</code>.
	 */
	@Column(name = "exacted_revenge", precision = 1)
	@Override
	public Boolean getExactedRevenge() {
		return (Boolean) getValue(28);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.display_to_defender</code>. DEPRECATED 12-12-14 ALWAYS TRUE
	 */
	@Override
	public PvpBattleHistoryRecord setDisplayToDefender(Boolean value) {
		setValue(29, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.display_to_defender</code>. DEPRECATED 12-12-14 ALWAYS TRUE
	 */
	@Column(name = "display_to_defender", precision = 1)
	@Override
	public Boolean getDisplayToDefender() {
		return (Boolean) getValue(29);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.pvp_dmg_multiplier</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setPvpDmgMultiplier(Double value) {
		setValue(30, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.pvp_dmg_multiplier</code>.
	 */
	@Column(name = "pvp_dmg_multiplier", precision = 12)
	@Override
	public Double getPvpDmgMultiplier() {
		return (Double) getValue(30);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.clan_avenged</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setClanAvenged(Boolean value) {
		setValue(31, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.clan_avenged</code>.
	 */
	@Column(name = "clan_avenged", precision = 1)
	@Override
	public Boolean getClanAvenged() {
		return (Boolean) getValue(31);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_history.replay_id</code>.
	 */
	@Override
	public PvpBattleHistoryRecord setReplayId(String value) {
		setValue(32, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_history.replay_id</code>.
	 */
	@Column(name = "replay_id", length = 36)
	@Size(max = 36)
	@Override
	public String getReplayId() {
		return (String) getValue(32);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, String, Timestamp> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IPvpBattleHistory from) {
		setAttackerId(from.getAttackerId());
		setDefenderId(from.getDefenderId());
		setBattleEndTime(from.getBattleEndTime());
		setBattleStartTime(from.getBattleStartTime());
		setAttackerEloChange(from.getAttackerEloChange());
		setAttackerEloBefore(from.getAttackerEloBefore());
		setAttackerEloAfter(from.getAttackerEloAfter());
		setDefenderEloChange(from.getDefenderEloChange());
		setDefenderEloBefore(from.getDefenderEloBefore());
		setDefenderEloAfter(from.getDefenderEloAfter());
		setAttackerPrevLeague(from.getAttackerPrevLeague());
		setAttackerCurLeague(from.getAttackerCurLeague());
		setDefenderPrevLeague(from.getDefenderPrevLeague());
		setDefenderCurLeague(from.getDefenderCurLeague());
		setAttackerPrevRank(from.getAttackerPrevRank());
		setAttackerCurRank(from.getAttackerCurRank());
		setDefenderPrevRank(from.getDefenderPrevRank());
		setDefenderCurRank(from.getDefenderCurRank());
		setAttackerCashChange(from.getAttackerCashChange());
		setDefenderCashChange(from.getDefenderCashChange());
		setAttackerOilChange(from.getAttackerOilChange());
		setDefenderOilChange(from.getDefenderOilChange());
		setCashStolenFromStorage(from.getCashStolenFromStorage());
		setCashStolenFromGenerators(from.getCashStolenFromGenerators());
		setOilStolenFromStorage(from.getOilStolenFromStorage());
		setOilStolenFromGenerators(from.getOilStolenFromGenerators());
		setAttackerWon(from.getAttackerWon());
		setCancelled(from.getCancelled());
		setExactedRevenge(from.getExactedRevenge());
		setDisplayToDefender(from.getDisplayToDefender());
		setPvpDmgMultiplier(from.getPvpDmgMultiplier());
		setClanAvenged(from.getClanAvenged());
		setReplayId(from.getReplayId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IPvpBattleHistory> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PvpBattleHistoryRecord
	 */
	public PvpBattleHistoryRecord() {
		super(PvpBattleHistory.PVP_BATTLE_HISTORY);
	}

	/**
	 * Create a detached, initialised PvpBattleHistoryRecord
	 */
	public PvpBattleHistoryRecord(String attackerId, String defenderId, Timestamp battleEndTime, Timestamp battleStartTime, Integer attackerEloChange, Integer attackerEloBefore, Integer attackerEloAfter, Integer defenderEloChange, Integer defenderEloBefore, Integer defenderEloAfter, Integer attackerPrevLeague, Integer attackerCurLeague, Integer defenderPrevLeague, Integer defenderCurLeague, Integer attackerPrevRank, Integer attackerCurRank, Integer defenderPrevRank, Integer defenderCurRank, Integer attackerCashChange, Integer defenderCashChange, Integer attackerOilChange, Integer defenderOilChange, Integer cashStolenFromStorage, Integer cashStolenFromGenerators, Integer oilStolenFromStorage, Integer oilStolenFromGenerators, Boolean attackerWon, Boolean cancelled, Boolean exactedRevenge, Boolean displayToDefender, Double pvpDmgMultiplier, Boolean clanAvenged, String replayId) {
		super(PvpBattleHistory.PVP_BATTLE_HISTORY);

		setValue(0, attackerId);
		setValue(1, defenderId);
		setValue(2, battleEndTime);
		setValue(3, battleStartTime);
		setValue(4, attackerEloChange);
		setValue(5, attackerEloBefore);
		setValue(6, attackerEloAfter);
		setValue(7, defenderEloChange);
		setValue(8, defenderEloBefore);
		setValue(9, defenderEloAfter);
		setValue(10, attackerPrevLeague);
		setValue(11, attackerCurLeague);
		setValue(12, defenderPrevLeague);
		setValue(13, defenderCurLeague);
		setValue(14, attackerPrevRank);
		setValue(15, attackerCurRank);
		setValue(16, defenderPrevRank);
		setValue(17, defenderCurRank);
		setValue(18, attackerCashChange);
		setValue(19, defenderCashChange);
		setValue(20, attackerOilChange);
		setValue(21, defenderOilChange);
		setValue(22, cashStolenFromStorage);
		setValue(23, cashStolenFromGenerators);
		setValue(24, oilStolenFromStorage);
		setValue(25, oilStolenFromGenerators);
		setValue(26, attackerWon);
		setValue(27, cancelled);
		setValue(28, exactedRevenge);
		setValue(29, displayToDefender);
		setValue(30, pvpDmgMultiplier);
		setValue(31, clanAvenged);
		setValue(32, replayId);
	}
}