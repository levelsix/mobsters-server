package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

//SHOULD ONLY BE ONE ROW FOR AN ATTACKER
//A DEFENDER SHOULD NOT BE ATTACKED BY MORE THAN ONE ATTACKER,
//EXCEPT IF THE DEFENDER ID IS 0, i.e. defender is fake 
public class PvpBattleForUser implements Serializable {

	private static final long serialVersionUID = 3932008730259614440L;

	private String attackerId;
	private String defenderId;
	private int attackerWinEloChange;
	private int defenderLoseEloChange;
	private int attackerLoseEloChange;
	private int defenderWinEloChange;
	private Date battleStartTime;

	public PvpBattleForUser() {
		super();
	}

	public PvpBattleForUser(String attackerId, String defenderId,
			int attackerWinEloChange, int defenderLoseEloChange,
			int attackerLoseEloChange, int defenderWinEloChange,
			Date battleStartTime) {
		super();
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.attackerWinEloChange = attackerWinEloChange;
		this.defenderLoseEloChange = defenderLoseEloChange;
		this.attackerLoseEloChange = attackerLoseEloChange;
		this.defenderWinEloChange = defenderWinEloChange;
		this.battleStartTime = battleStartTime;
	}

	public String getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(String attackerId) {
		this.attackerId = attackerId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(String defenderId) {
		this.defenderId = defenderId;
	}

	public int getAttackerWinEloChange() {
		return attackerWinEloChange;
	}

	public void setAttackerWinEloChange(int attackerWinEloChange) {
		this.attackerWinEloChange = attackerWinEloChange;
	}

	public int getDefenderLoseEloChange() {
		return defenderLoseEloChange;
	}

	public void setDefenderLoseEloChange(int defenderLoseEloChange) {
		this.defenderLoseEloChange = defenderLoseEloChange;
	}

	public int getAttackerLoseEloChange() {
		return attackerLoseEloChange;
	}

	public void setAttackerLoseEloChange(int attackerLoseEloChange) {
		this.attackerLoseEloChange = attackerLoseEloChange;
	}

	public int getDefenderWinEloChange() {
		return defenderWinEloChange;
	}

	public void setDefenderWinEloChange(int defenderWinEloChange) {
		this.defenderWinEloChange = defenderWinEloChange;
	}

	public Date getBattleStartTime() {
		return battleStartTime;
	}

	public void setBattleStartTime(Date battleStartTime) {
		this.battleStartTime = battleStartTime;
	}

	@Override
	public String toString() {
		return "PvpBattleForUser [attackerId=" + attackerId + ", defenderId="
				+ defenderId + ", attackerWinEloChange=" + attackerWinEloChange
				+ ", defenderLoseEloChange=" + defenderLoseEloChange
				+ ", attackerLoseEloChange=" + attackerLoseEloChange
				+ ", defenderWinEloChange=" + defenderWinEloChange
				+ ", battleStartTime=" + battleStartTime + "]";
	}

}
