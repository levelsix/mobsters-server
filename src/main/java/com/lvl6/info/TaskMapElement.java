package com.lvl6.info;

import java.io.Serializable;

public class TaskMapElement implements Serializable {

	private static final long serialVersionUID = -2717944876986726663L;
	
	private int id;
	private int taskId;
	private int xPos;
	private int yPos;
  private String element;
  private boolean boss;
  
	public TaskMapElement( int id, int taskId, int xPos, int yPos, String element, boolean boss )
{
	super();
	this.id = id;
	this.taskId = taskId;
	this.xPos = xPos;
	this.yPos = yPos;
	this.element = element;
	this.boss = boss;
}
	public String getElement() {
    return element;
  }
  public void setElement(String element) {
    this.element = element;
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
	public boolean isBoss()
	{
		return boss;
	}
	public void setBoss( boolean boss )
	{
		this.boss = boss;
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
			+ "]";
	}
	
}
