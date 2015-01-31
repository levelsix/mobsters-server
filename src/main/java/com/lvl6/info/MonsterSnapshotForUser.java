package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MonsterSnapshotForUser implements Serializable {

	private static final long serialVersionUID = 7554055797416214128L;
	
	private String id;
	private Date timeOfEntry;
	private String userId;
	private String type;
	private String idInTable;
	private String monsterForUserId;
	private int monsterId;
	private int currentExp;
	private int currentLvl;
	private int currentHp;
	private int teamSlotNum;
	private int offSkillId;
	private int defSkillId;
	
	public MonsterSnapshotForUser()
	{
		super();
	}

	public MonsterSnapshotForUser(MonsterSnapshotForUser msfu) {
		super();
		msfu.id = id;
		msfu.timeOfEntry = timeOfEntry;
		msfu.userId = userId;
		msfu.type = type;
		msfu.idInTable = idInTable;
		msfu.monsterForUserId = monsterForUserId;
		msfu.monsterId = monsterId;
		msfu.currentExp = currentExp;
		msfu.currentLvl = currentLvl;
		msfu.currentHp = currentHp;
		msfu.teamSlotNum = teamSlotNum;
		msfu.offSkillId = offSkillId;
		msfu.defSkillId = defSkillId;
	}
	
	public MonsterSnapshotForUser(
		String id,
		Date timeOfEntry,
		String userId,
		String type,
		String idInTable,
		String monsterForUserId,
		int monsterId,
		int currentExp,
		int currentLvl,
		int currentHp,
		int teamSlotNum,
		int offSkillId,
		int defSkillId )
	{
		super();
		this.id = id;
		this.timeOfEntry = timeOfEntry;
		this.userId = userId;
		this.type = type;
		this.idInTable = idInTable;
		this.monsterForUserId = monsterForUserId;
		this.monsterId = monsterId;
		this.currentExp = currentExp;
		this.currentLvl = currentLvl;
		this.currentHp = currentHp;
		this.teamSlotNum = teamSlotNum;
		this.offSkillId = offSkillId;
		this.defSkillId = defSkillId;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public Date getTimeOfEntry()
	{
		return timeOfEntry;
	}

	public void setTimeOfEntry( Date timeOfEntry )
	{
		this.timeOfEntry = timeOfEntry;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( String userId )
	{
		this.userId = userId;
	}

	public String getType()
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public String getIdInTable()
	{
		return idInTable;
	}

	public void setIdInTable( String idInTable )
	{
		this.idInTable = idInTable;
	}

	public String getMonsterForUserId()
	{
		return monsterForUserId;
	}

	public void setMonsterForUserId( String monsterForUserId )
	{
		this.monsterForUserId = monsterForUserId;
	}

	public int getMonsterId()
	{
		return monsterId;
	}

	public void setMonsterId( int monsterId )
	{
		this.monsterId = monsterId;
	}

	public int getCurrentExp()
	{
		return currentExp;
	}

	public void setCurrentExp( int currentExp )
	{
		this.currentExp = currentExp;
	}

	public int getCurrentLvl()
	{
		return currentLvl;
	}

	public void setCurrentLvl( int currentLvl )
	{
		this.currentLvl = currentLvl;
	}

	public int getCurrentHp()
	{
		return currentHp;
	}

	public void setCurrentHp( int currentHp )
	{
		this.currentHp = currentHp;
	}

	public int getTeamSlotNum()
	{
		return teamSlotNum;
	}

	public void setTeamSlotNum( int teamSlotNum )
	{
		this.teamSlotNum = teamSlotNum;
	}

	public int getOffSkillId()
	{
		return offSkillId;
	}

	public void setOffSkillId( int offSkillId )
	{
		this.offSkillId = offSkillId;
	}

	public int getDefSkillId()
	{
		return defSkillId;
	}

	public void setDefSkillId( int defSkillId )
	{
		this.defSkillId = defSkillId;
	}

	@Override
	public String toString()
	{
		return "MonsterSnapshotForUser [id="
			+ id
			+ ", timeOfEntry="
			+ timeOfEntry
			+ ", userId="
			+ userId
			+ ", type="
			+ type
			+ ", idInTable="
			+ idInTable
			+ ", monsterForUserId="
			+ monsterForUserId
			+ ", monsterId="
			+ monsterId
			+ ", currentExp="
			+ currentExp
			+ ", currentLvl="
			+ currentLvl
			+ ", currentHp="
			+ currentHp
			+ ", teamSlotNum="
			+ teamSlotNum
			+ ", offSkillId="
			+ offSkillId
			+ ", defSkillId="
			+ defSkillId
			+ "]";
	}

}
