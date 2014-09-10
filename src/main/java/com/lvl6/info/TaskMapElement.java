package com.lvl6.info;

import java.io.Serializable;

public class TaskMapElement implements Serializable {

	private static final long serialVersionUID = 7214663032423732406L;
	
	private int id;
	private int taskId;
	private int xPos;
	private int yPos;
	private String element;
	private boolean boss;
	private String bossImgName;
	private int itemDropId;
	private String sectionName; //groups a bunch of TaskMapElement

	public TaskMapElement(
		int id,
		int taskId,
		int xPos,
		int yPos,
		String element,
		boolean boss,
		String bossImgName,
		int itemDropId,
		String sectionName )
	{
		super();
		this.id = id;
		this.taskId = taskId;
		this.xPos = xPos;
		this.yPos = yPos;
		this.element = element;
		this.boss = boss;
		this.bossImgName = bossImgName;
		this.itemDropId = itemDropId;
		this.sectionName = sectionName;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getTaskId()
	{
		return taskId;
	}

	public void setTaskId( int taskId )
	{
		this.taskId = taskId;
	}

	public int getxPos()
	{
		return xPos;
	}

	public void setxPos( int xPos )
	{
		this.xPos = xPos;
	}

	public int getyPos()
	{
		return yPos;
	}

	public void setyPos( int yPos )
	{
		this.yPos = yPos;
	}

	public String getElement()
	{
		return element;
	}

	public void setElement( String element )
	{
		this.element = element;
	}

	public boolean isBoss()
	{
		return boss;
	}

	public void setBoss( boolean boss )
	{
		this.boss = boss;
	}

	public String getBossImgName()
	{
		return bossImgName;
	}

	public void setBossImgName( String bossImgName )
	{
		this.bossImgName = bossImgName;
	}

	public int getItemDropId()
	{
		return itemDropId;
	}

	public void setItemDropId( int itemDropId )
	{
		this.itemDropId = itemDropId;
	}

	public String getSectionName()
	{
		return sectionName;
	}

	public void setSectionName( String sectionName )
	{
		this.sectionName = sectionName;
	}

	@Override
	public String toString()
	{
		return "TaskMapElement [id="
			+ id
			+ ", taskId="
			+ taskId
			+ ", xPos="
			+ xPos
			+ ", yPos="
			+ yPos
			+ ", element="
			+ element
			+ ", boss="
			+ boss
			+ ", bossImgName="
			+ bossImgName
			+ ", itemDropId="
			+ itemDropId
			+ ", sectionName="
			+ sectionName
			+ "]";
	}

}
