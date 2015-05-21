package com.lvl6.info;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class BattleReplayForUser implements Serializable {

	private static final long serialVersionUID = 7412022896712826986L;

	private String id;
	private String creatorId;
	private byte[] replay;
	private Date timeCreated;

	public BattleReplayForUser() {
		super();
	}

	public BattleReplayForUser(String id, String creatorId, byte[] replay,
			Date timeCreated) {
		super();
		this.id = id;
		this.creatorId = creatorId;
		this.replay = replay;
		this.timeCreated = timeCreated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public byte[] getReplay() {
		return replay;
	}

	public void setReplay(byte[] replay) {
		this.replay = replay;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	@Override
	public String toString() {
		return "BattleReplayForUser [id=" + id + ", creatorId=" + creatorId
				+ ", replay=" + Arrays.toString(replay) + ", timeCreated="
				+ timeCreated + "]";
	}

}
