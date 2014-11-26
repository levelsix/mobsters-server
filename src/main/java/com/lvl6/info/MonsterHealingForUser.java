package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterHealingForUser implements Serializable {

	private String userId;
	private String monsterForUserId;
	private Date queuedTime;
//	private int userStructHospitalId;
	private float healthProgress;
	private int priority;
	private float elapsedSeconds;

	public MonsterHealingForUser()
	{
		super();
	}

	public MonsterHealingForUser(
		String userId,
		String monsterForUserId,
		Date queuedTime,
		float healthProgress,
		int priority,
		float elapsedSeconds )
	{
		super();
		this.userId = userId;
		this.monsterForUserId = monsterForUserId;
		this.queuedTime = queuedTime;
		this.healthProgress = healthProgress;
		this.priority = priority;
		this.elapsedSeconds = elapsedSeconds;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMonsterForUserId() {
		return monsterForUserId;
	}

	public void setMonsterForUserId(String monsterForUserId) {
		this.monsterForUserId = monsterForUserId;
	}

	public Date getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(Date queuedTime) {
		this.queuedTime = queuedTime;
	}

	public float getHealthProgress() {
		return healthProgress;
	}

	public void setHealthProgress(float healthProgress) {
		this.healthProgress = healthProgress;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getElapsedSeconds()
	{
		return elapsedSeconds;
	}

	public void setElapsedSeconds( float elapsedSeconds )
	{
		this.elapsedSeconds = elapsedSeconds;
	}

	@Override
	public String toString()
	{
		return "MonsterHealingForUser [userId="
			+ userId
			+ ", monsterForUserId="
			+ monsterForUserId
			+ ", queuedTime="
			+ queuedTime
			+ ", healthProgress="
			+ healthProgress
			+ ", priority="
			+ priority
			+ ", elapsedSeconds="
			+ elapsedSeconds
			+ "]";
	}

}
