package com.lvl6.info;

import java.sql.Timestamp;

public class MonsterDeleteHistory {

	public MonsterDeleteHistory(MonsterForUser mfu, String deletedReason,
			String details, Timestamp deletedTime) {
		super();
		this.mfu = mfu;
		this.deletedReason = deletedReason;
		this.details = details;
		this.deletedTime = deletedTime;
	}

	private MonsterForUser mfu;
	private String deletedReason;
	private String details;
	private Timestamp deletedTime;

	public MonsterForUser getMfu() {
		return mfu;
	}

	public void setMfu(MonsterForUser mfu) {
		this.mfu = mfu;
	}

	public String getDeletedReason() {
		return deletedReason;
	}

	public void setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Timestamp getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Timestamp deletedTime) {
		this.deletedTime = deletedTime;
	}

	@Override
	public String toString() {
		return "MonsterDeleteHistory [mfu=" + mfu + ", deletedReason="
				+ deletedReason + ", details=" + details + ", deletedTime="
				+ deletedTime + "]";
	}

}
