package com.lvl6.info;

import java.io.Serializable;

public class MiniEventForUser implements Serializable {

	private static final long serialVersionUID = 6757617535032512427L;

	private String userId;
	private int miniEventId;
	private int userLvl;
	private boolean tierOneRedeemed;
	private boolean tierTwoRedeemed;
	private boolean tierThreeRedeemed;

	public MiniEventForUser() {
		super();
	}

	public MiniEventForUser(MiniEventForUser mefu) {
		super();
		this.userId = mefu.getUserId();
		this.miniEventId = mefu.getMiniEventId();
		this.userLvl = mefu.getUserLvl();
		this.tierOneRedeemed = mefu.isTierOneRedeemed();
		this.tierTwoRedeemed = mefu.isTierTwoRedeemed();
		this.tierThreeRedeemed = mefu.isTierThreeRedeemed();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMiniEventId() {
		return miniEventId;
	}

	public void setMiniEventId(int miniEventId) {
		this.miniEventId = miniEventId;
	}

	public int getUserLvl() {
		return userLvl;
	}

	public void setUserLvl(int userLvl) {
		this.userLvl = userLvl;
	}

	public boolean isTierOneRedeemed() {
		return tierOneRedeemed;
	}

	public void setTierOneRedeemed(boolean tierOneRedeemed) {
		this.tierOneRedeemed = tierOneRedeemed;
	}

	public boolean isTierTwoRedeemed() {
		return tierTwoRedeemed;
	}

	public void setTierTwoRedeemed(boolean tierTwoRedeemed) {
		this.tierTwoRedeemed = tierTwoRedeemed;
	}

	public boolean isTierThreeRedeemed() {
		return tierThreeRedeemed;
	}

	public void setTierThreeRedeemed(boolean tierThreeRedeemed) {
		this.tierThreeRedeemed = tierThreeRedeemed;
	}

	@Override
	public String toString() {
		return "MiniEventForUser [userId=" + userId + ", miniEventId="
				+ miniEventId + ", userLvl=" + userLvl + ", tierOneRedeemed="
				+ tierOneRedeemed + ", tierTwoRedeemed=" + tierTwoRedeemed
				+ ", tierThreeRedeemed=" + tierThreeRedeemed + "]";
	}

}
