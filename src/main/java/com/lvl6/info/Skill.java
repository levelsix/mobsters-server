package com.lvl6.info;

import java.io.Serializable;

public class Skill implements Serializable {
	
	private static final long serialVersionUID = -2406732110892655927L;
	
	private int id;
	private String name;
	private int orbCost;
	private String type;
	private String activationType;
	private int predecId;
	private int successorId;
	private String desc;
	private String iconImgName;
	
	public Skill(
		int id,
		String name,
		int orbCost,
		String type,
		String activationType,
		int predecId,
		int successorId,
		String desc,
		String iconImgName )
	{
		super();
		this.id = id;
		this.name = name;
		this.orbCost = orbCost;
		this.type = type;
		this.activationType = activationType;
		this.predecId = predecId;
		this.successorId = successorId;
		this.desc = desc;
		this.iconImgName = iconImgName;
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

	public int getOrbCost()
	{
		return orbCost;
	}

	public void setOrbCost( int orbCost )
	{
		this.orbCost = orbCost;
	}

	public String getType()
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public String getActivationType()
	{
		return activationType;
	}

	public void setActivationType( String activationType )
	{
		this.activationType = activationType;
	}

	public int getPredecId()
	{
		return predecId;
	}

	public void setPredecId( int predecId )
	{
		this.predecId = predecId;
	}

	public int getSuccessorId()
	{
		return successorId;
	}

	public void setSuccessorId( int successorId )
	{
		this.successorId = successorId;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc( String desc )
	{
		this.desc = desc;
	}

	public String getIconImgName()
	{
		return iconImgName;
	}

	public void setIconImgName( String iconImgName )
	{
		this.iconImgName = iconImgName;
	}

	@Override
	public String toString()
	{
		return "Skill [id="
			+ id
			+ ", name="
			+ name
			+ ", orbCost="
			+ orbCost
			+ ", type="
			+ type
			+ ", activationType="
			+ activationType
			+ ", predecId="
			+ predecId
			+ ", successorId="
			+ successorId
			+ ", desc="
			+ desc
			+ ", iconImgName="
			+ iconImgName
			+ "]";
	}

}
