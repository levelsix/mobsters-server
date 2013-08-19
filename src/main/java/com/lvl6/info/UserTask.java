package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserTask implements Serializable {

	private static final long serialVersionUID = 523973952772559434L;
	private long id;
	private int userId;
	private int taskId;
	private List<Integer> monsterRewardEquipIds;
	public int expGained;
	public int silverGained;
	public int numRevives;
	private Date startDate;
	private String stageExps;
	private String stageSilvers;
	
	public UserTask(long id, int userId, int taskId,
			List<Integer> monsterRewardEquipIds, int expGained,
			int silverGained, int numRevives, Date startDate, String stageExps,
			String stageSilvers) {
		super();
		this.id = id;
		this.userId = userId;
		this.taskId = taskId;
		this.monsterRewardEquipIds = monsterRewardEquipIds;
		this.expGained = expGained;
		this.silverGained = silverGained;
		this.numRevives = numRevives;
		this.startDate = startDate;
		this.stageExps = stageExps;
		this.stageSilvers = stageSilvers;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public List<Integer> getMonsterRewardEquipIds() {
		return monsterRewardEquipIds;
	}

	public void setMonsterRewardEquipIds(List<Integer> monsterRewardEquipIds) {
		this.monsterRewardEquipIds = monsterRewardEquipIds;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getSilverGained() {
		return silverGained;
	}

	public void setSilverGained(int silverGained) {
		this.silverGained = silverGained;
	}

	public int getNumRevives() {
		return numRevives;
	}

	public void setNumRevives(int numRevives) {
		this.numRevives = numRevives;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStageExps() {
		return stageExps;
	}

	public void setStageExps(String stageExps) {
		this.stageExps = stageExps;
	}

	public String getStageSilvers() {
		return stageSilvers;
	}

	public void setStageSilvers(String stageSilvers) {
		this.stageSilvers = stageSilvers;
	}

	@Override
	public String toString() {
		return "UserTask [id=" + id + ", userId=" + userId + ", taskId="
				+ taskId + ", monsterRewardEquipIds=" + monsterRewardEquipIds
				+ ", expGained=" + expGained + ", silverGained=" + silverGained
				+ ", numRevives=" + numRevives + ", startDate=" + startDate
				+ ", stageExps=" + stageExps + ", stageSilvers=" + stageSilvers
				+ "]";
	}
	
}
