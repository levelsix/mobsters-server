package com.lvl6.info;

import java.io.Serializable;

//this class is analogous to a task_stage and task_stage_monster combined
public class ClanRaidStageMonster implements Serializable {

	private static final long serialVersionUID = -6377284601865090160L;
	private int id;
	private int clanRaidStageId;
	private int monsterId;
	
	public ClanRaidStageMonster(int id, int clanRaidStageId, int monsterId) {
		super();
		this.id = id;
		this.clanRaidStageId = clanRaidStageId;
		this.monsterId = monsterId;
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

	@Override
	public String toString() {
		return "ClanRaidStageMonster [id=" + id + ", clanRaidStageId="
				+ clanRaidStageId + ", monsterId=" + monsterId + "]";
	}
	
}
