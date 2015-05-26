package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class ClanAvengeUser implements Serializable {

	private static final long serialVersionUID = 8617382026129051225L;

	private String clanId;
	private String clanAvengeId;
	private String userId;
	private Date avengeTime;

	public ClanAvengeUser() {
		super();
	}

	//
	//	public ClanAvengeUser(ClanAvengeUser ca)
	//	{
	//		super();
	//		this.id = ca.getId();
	//		this.clanId = ca.getClanId();
	//		this.attackerId = ca.getAttackerId();
	//		this.defenderId = ca.getDefenderId();
	//		this.battleEndTime = ca.getBattleEndTime();
	//		this.avengeRequestTime = ca.getAvengeRequestTime();
	//	}

	public ClanAvengeUser(String clanId, String clanAvengeId, String userId,
			Date avengeTime) {
		super();
		this.clanId = clanId;
		this.clanAvengeId = clanAvengeId;
		this.userId = userId;
		this.avengeTime = avengeTime;
	}

	public String getClanId() {
		return clanId;
	}

	public void setClanId(String clanId) {
		this.clanId = clanId;
	}

	public String getClanAvengeId() {
		return clanAvengeId;
	}

	public void setClanAvengeId(String clanAvengeId) {
		this.clanAvengeId = clanAvengeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getAvengeTime() {
		return avengeTime;
	}

	public void setAvengeTime(Date avengeTime) {
		this.avengeTime = avengeTime;
	}

	@Override
	public String toString() {
		return "ClanAvengeUser [clanId=" + clanId + ", clanAvengeId="
				+ clanAvengeId + ", userId=" + userId + ", avengeTime="
				+ avengeTime + "]";
	}

}
