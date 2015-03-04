package com.lvl6.info;

import java.io.Serializable;

import com.lvl6.proto.ResearchsProto.ResearchDomain;
import com.lvl6.proto.ResearchsProto.ResearchType;

public class Research implements Serializable {
	
	private static final long serialVersionUID = -7558436754504360378L;
	
	private int id;
	//reducing resource cost or speed
	private String researchType;
	//battle, resources, restorative/healing
	private String researchDomain;
	private String iconImgName;
	private String name;
	private int predId;
	private int succId;
	private String desc;
	//minutes to complete research
	private int durationMin;
	private int costAmt;
	private String costType;
	
	public Research(
		int id,
		String researchType,
		String researchDomain,
		String iconImgName,
		String name,
		int predId,
		int succId,
		String desc,
		int durationMin,
		int costAmt,
		String costType )
	{
		super();
		this.id = id;
		this.researchType = researchType;
		this.researchDomain = researchDomain;
		this.iconImgName = iconImgName;
		this.name = name;
		this.predId = predId;
		this.succId = succId;
		this.desc = desc;
		this.durationMin = durationMin;
		this.costAmt = costAmt;
		this.costType = costType;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}


	public String getIconImgName()
	{
		return iconImgName;
	}

	public void setIconImgName( String iconImgName )
	{
		this.iconImgName = iconImgName;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getPredId()
	{
		return predId;
	}

	public void setPredId( int predId )
	{
		this.predId = predId;
	}

	public int getSuccId()
	{
		return succId;
	}

	public void setSuccId( int succId )
	{
		this.succId = succId;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc( String desc )
	{
		this.desc = desc;
	}

	public int getDurationMin()
	{
		return durationMin;
	}

	public void setDurationMin( int durationMin )
	{
		this.durationMin = durationMin;
	}

	public int getCostAmt()
	{
		return costAmt;
	}

	public void setCostAmt( int costAmt )
	{
		this.costAmt = costAmt;
	}

	public String getCostType()
	{
		return costType;
	}

	public void setCostType( String costType )
	{
		this.costType = costType;
	}

	@Override
	public String toString()
	{
		return "Research [id="
			+ id
			+ ", researchType="
			+ researchType
			+ ", researchDomain="
			+ researchDomain
			+ ", iconImgName="
			+ iconImgName
			+ ", name="
			+ name
			+ ", predId="
			+ predId
			+ ", succId="
			+ succId
			+ ", desc="
			+ desc
			+ ", durationMin="
			+ durationMin
			+ ", costAmt="
			+ costAmt
			+ ", costType="
			+ costType
			+ "]";
	}

	public String getResearchType() {
		return researchType;
	}

	public void setResearchType(String researchType) {
		this.researchType = researchType;
	}

	public String getResearchDomain() {
		return researchDomain;
	}

	public void setResearchDomain(String researchDomain) {
		this.researchDomain = researchDomain;
	}




}
