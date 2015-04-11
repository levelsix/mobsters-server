package com.lvl6.utils;

import java.io.Serializable;
import java.util.Date;

public class PlayerInAction implements Serializable {
	private static final long serialVersionUID = 6149589858369370042L;

	protected String lockedByClass = "";

	public String getLockedByClass() {
		return lockedByClass;
	}

	public void setLockedByClass(String lockedByClass) {
		this.lockedByClass = lockedByClass;
	}

	protected String playerId;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	protected Date lockTime = new Date();

	public PlayerInAction(String playerId, String lockedByClass) {
		this.playerId = playerId;
		this.lockedByClass = lockedByClass;
	}

	@Override
	public boolean equals(Object obj) {
		PlayerInAction play = ((PlayerInAction) obj);
		return getPlayerId().equals(play.getPlayerId());
	}

}
