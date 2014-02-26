package com.lvl6.info;

import java.io.Serializable;

//this class is analogous to task_stage_monster
//multiple monsters can point to stage (all must spawn)
public class ClanRaidStageMonster implements Serializable {

	private static final long serialVersionUID = 6545206961731205658L;
	private int id;
	private int clanRaidStageId;
	private int monsterId;
	private int monsterHp; //instead of specifying monster level info, specify hp here
	private int minDmg;
	private int maxDmg;
	
	public ClanRaidStageMonster(int id, int clanRaidStageId, int monsterId,
			int monsterHp, int minDmg, int maxDmg) {
		super();
		this.id = id;
		this.clanRaidStageId = clanRaidStageId;
		this.monsterId = monsterId;
		this.monsterHp = monsterHp;
		this.minDmg = minDmg;
		this.maxDmg = maxDmg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClanRaidStageId() {
		return clanRaidStageId;
	}

	public void setClanRaidStageId(int clanRaidStageId) {
		this.clanRaidStageId = clanRaidStageId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getMonsterHp() {
		return monsterHp;
	}

	public void setMonsterHp(int monsterHp) {
		this.monsterHp = monsterHp;
	}

	public int getMinDmg() {
		return minDmg;
	}

	public void setMinDmg(int minDmg) {
		this.minDmg = minDmg;
	}

	public int getMaxDmg() {
		return maxDmg;
	}

	public void setMaxDmg(int maxDmg) {
		this.maxDmg = maxDmg;
	}

	@Override
	public String toString() {
		return "ClanRaidStageMonster [id=" + id + ", clanRaidStageId="
				+ clanRaidStageId + ", monsterId=" + monsterId + ", monsterHp="
				+ monsterHp + ", minDmg=" + minDmg + ", maxDmg=" + maxDmg + "]";
	}
	
}
