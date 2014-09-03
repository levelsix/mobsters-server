package com.lvl6.info;

import java.io.Serializable;

public class SkillProperty implements Serializable {
	
	
	private static final long serialVersionUID = 7357820566608901324L;
	
	private int id;
	private String name;
	private float value;
	private int skillId;
	private String shortName;
	
	public SkillProperty( int id, String name, float value, int skillId,
		String shortName)
	{
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.skillId = skillId;
		this.shortName = shortName;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public float getValue()
	{
		return value;
	}

	public void setValue( float value )
	{
		this.value = value;
	}

	public int getSkillId()
	{
		return skillId;
	}

	public void setSkillId( int skillId )
	{
		this.skillId = skillId;
	}
	
	public String getShortName()
	{
		return shortName;
	}

	public void setShortName( String shortName )
	{
		this.shortName = shortName;
	}

	@Override
	public String toString()
	{
		return "SkillProperty [id="
			+ id
			+ ", name="
			+ name
			+ ", value="
			+ value
			+ ", skillId="
			+ skillId
			+ ", shortName="
			+ shortName
			+ "]";
	}

}
