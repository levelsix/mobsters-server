package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanAvenge implements Serializable {

	private static final long serialVersionUID = -1324459407001133863L;

	private String id;
	private String clanId;
	private String attackerId;
	private String defenderId;
	private Date battleEndTime;
	private Date avengeRequestTime;

	public ClanAvenge() {
		super();
	}

	public ClanAvenge(ClanAvenge ca) {
		super();
		this.id = ca.getId();
		this.clanId = ca.getClanId();
		this.attackerId = ca.getAttackerId();
		this.defenderId = ca.getDefenderId();
		this.battleEndTime = ca.getBattleEndTime();
		this.avengeRequestTime = ca.getAvengeRequestTime();
	}

	public ClanAvenge(String id, String clanId, String attackerId,
			String defenderId, Date battleEndTime, Date avengeRequestTime) {
		super();
		this.id = id;
		this.clanId = clanId;
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.battleEndTime = battleEndTime;
		this.avengeRequestTime = avengeRequestTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
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

	public Date getBattleEndTime() {
		return battleEndTime;
	}

	public void setBattleEndTime(Date battleEndTime) {
		this.battleEndTime = battleEndTime;
	}

	public Date getAvengeRequestTime() {
		return avengeRequestTime;
	}

	public void setAvengeRequestTime(Date avengeRequestTime) {
		this.avengeRequestTime = avengeRequestTime;
	}

	@Override
	public String toString() {
		return "ClanAvenge [id=" + id + ", clanId=" + clanId + ", attackerId="
				+ attackerId + ", defenderId=" + defenderId
				+ ", battleEndTime=" + battleEndTime + ", avengeRequestTime="
				+ avengeRequestTime + "]";
	}

}
