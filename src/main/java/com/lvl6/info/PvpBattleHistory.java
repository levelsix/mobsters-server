package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class PvpBattleHistory implements Serializable {
	
	private static final long serialVersionUID = 7761168981313051307L;
	
	private int attackerId;
	private int defenderId;
	private Date battleEndTime;
	private Date battleStartTime;
	private int attackerEloChange;
	private int defenderEloChange;
	private int attackerCashChange;
	private int defenderCashChange;
	private int attackerOilChange;
	private int defenderOilChange;
	private boolean attackerWon;
	private boolean cancelled;
	private boolean exactedRevenge;
	
	public PvpBattleHistory() {
		super();
	}
	
	public PvpBattleHistory(int attackerId, int defenderId, Date battleEndTime,
			Date battleStartTime, int attackerEloChange, int defenderEloChange,
			int attackerCashChange, int defenderCashChange, int attackerOilChange,
			int defenderOilChange, boolean attackerWon, boolean cancelled,
			boolean exactedRevenge) {
		super();
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.battleEndTime = battleEndTime;
		this.battleStartTime = battleStartTime;
		this.attackerEloChange = attackerEloChange;
		this.defenderEloChange = defenderEloChange;
		this.attackerCashChange = attackerCashChange;
		this.defenderCashChange = defenderCashChange;
		this.attackerOilChange = attackerOilChange;
		this.defenderOilChange = defenderOilChange;
		this.attackerWon = attackerWon;
		this.cancelled = cancelled;
		this.exactedRevenge = exactedRevenge;
	}

	public int getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(int attackerId) {
		this.attackerId = attackerId;
	}

	public int getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(int defenderId) {
		this.defenderId = defenderId;
	}

	public Date getBattleEndTime() {
		return battleEndTime;
	}

	public void setBattleEndTime(Date battleEndTime) {
		this.battleEndTime = battleEndTime;
	}

	public Date getBattleStartTime() {
		return battleStartTime;
	}

	public void setBattleStartTime(Date battleStartTime) {
		this.battleStartTime = battleStartTime;
	}

	public int getAttackerEloChange() {
		return attackerEloChange;
	}

	public void setAttackerEloChange(int attackerEloChange) {
		this.attackerEloChange = attackerEloChange;
	}

	public int getDefenderEloChange() {
		return defenderEloChange;
	}

	public void setDefenderEloChange(int defenderEloChange) {
		this.defenderEloChange = defenderEloChange;
	}

	public int getAttackerCashChange() {
		return attackerCashChange;
	}

	public void setAttackerCashChange(int attackerCashChange) {
		this.attackerCashChange = attackerCashChange;
	}

	public int getDefenderCashChange() {
		return defenderCashChange;
	}

	public void setDefenderCashChange(int defenderCashChange) {
		this.defenderCashChange = defenderCashChange;
	}

	public int getAttackerOilChange() {
		return attackerOilChange;
	}

	public void setAttackerOilChange(int attackerOilChange) {
		this.attackerOilChange = attackerOilChange;
	}

	public int getDefenderOilChange() {
		return defenderOilChange;
	}

	public void setDefenderOilChange(int defenderOilChange) {
		this.defenderOilChange = defenderOilChange;
	}

	public boolean isAttackerWon() {
		return attackerWon;
	}

	public void setAttackerWon(boolean attackerWon) {
		this.attackerWon = attackerWon;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isExactedRevenge() {
		return exactedRevenge;
	}

	public void setExactedRevenge(boolean exactedRevenge) {
		this.exactedRevenge = exactedRevenge;
	}

	@Override
	public String toString() {
		return "PvpBattleHistory [attackerId=" + attackerId + ", defenderId="
				+ defenderId + ", battleEndTime=" + battleEndTime
				+ ", battleStartTime=" + battleStartTime + ", attackerEloChange="
				+ attackerEloChange + ", defenderEloChange=" + defenderEloChange
				+ ", attackerCashChange=" + attackerCashChange
				+ ", defenderCashChange=" + defenderCashChange + ", attackerOilChange="
				+ attackerOilChange + ", defenderOilChange=" + defenderOilChange
				+ ", attackerWon=" + attackerWon + ", cancelled=" + cancelled
				+ ", exactedRevenge=" + exactedRevenge + "]";
	}
	
}
