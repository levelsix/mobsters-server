package com.lvl6.info;

import java.io.Serializable;

public class SkillSideEffect implements Serializable {
	
	private static final long serialVersionUID = -2138146268016158263L;
	
	private int id;
	private String name;
	private String  desc;
	private String type;
	private String traitType;
	private String imgName;
	private int imgPixelOffsetX;
	private int imgPixelOffsetY;
	private String iconImgName;
	private String pfxName;
	private String pfxColor;
	private String positionType;
	private int pfxPixelOffsetX;
	private int pfxPixelOffsetY;
	private String blendMode;
	
	public SkillSideEffect(
		int id,
		String name,
		String desc,
		String type,
		String traitType,
		String imgName,
		int imgPixelOffsetX,
		int imgPixelOffsetY,
		String iconImgName,
		String pfxName,
		String pfxColor,
		String positionType,
		int pfxPixelOffsetX,
		int pfxPixelOffsetY,
		String blendMode )
	{
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.traitType = traitType;
		this.imgName = imgName;
		this.imgPixelOffsetX = imgPixelOffsetX;
		this.imgPixelOffsetY = imgPixelOffsetY;
		this.iconImgName = iconImgName;
		this.pfxName = pfxName;
		this.pfxColor = pfxColor;
		this.positionType = positionType;
		this.pfxPixelOffsetX = pfxPixelOffsetX;
		this.pfxPixelOffsetY = pfxPixelOffsetY;
		this.blendMode = blendMode;
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

	public String getDesc()
	{
		return desc;
	}

	public void setDesc( String desc )
	{
		this.desc = desc;
	}

	public String getType()
	{
		return type;
	}

	public void setType( String type )
	{
		this.type = type;
	}

	public String getTraitType()
	{
		return traitType;
	}

	public void setTraitType( String traitType )
	{
		this.traitType = traitType;
	}

	public String getImgName()
	{
		return imgName;
	}

	public void setImgName( String imgName )
	{
		this.imgName = imgName;
	}

	public int getImgPixelOffsetX()
	{
		return imgPixelOffsetX;
	}

	public void setImgPixelOffsetX( int imgPixelOffsetX )
	{
		this.imgPixelOffsetX = imgPixelOffsetX;
	}

	public int getImgPixelOffsetY()
	{
		return imgPixelOffsetY;
	}

	public void setImgPixelOffsetY( int imgPixelOffsetY )
	{
		this.imgPixelOffsetY = imgPixelOffsetY;
	}

	public String getIconImgName()
	{
		return iconImgName;
	}

	public void setIconImgName( String iconImgName )
	{
		this.iconImgName = iconImgName;
	}

	public String getPfxName()
	{
		return pfxName;
	}

	public void setPfxName( String pfxName )
	{
		this.pfxName = pfxName;
	}

	public String getPfxColor()
	{
		return pfxColor;
	}

	public void setPfxColor( String pfxColor )
	{
		this.pfxColor = pfxColor;
	}

	public String getPositionType()
	{
		return positionType;
	}

	public void setPositionType( String positionType )
	{
		this.positionType = positionType;
	}

	public int getPfxPixelOffsetX()
	{
		return pfxPixelOffsetX;
	}

	public void setPfxPixelOffsetX( int pfxPixelOffsetX )
	{
		this.pfxPixelOffsetX = pfxPixelOffsetX;
	}

	public int getPfxPixelOffsetY()
	{
		return pfxPixelOffsetY;
	}

	public void setPfxPixelOffsetY( int pfxPixelOffsetY )
	{
		this.pfxPixelOffsetY = pfxPixelOffsetY;
	}

	public String getBlendMode()
	{
		return blendMode;
	}

	public void setBlendMode( String blendMode )
	{
		this.blendMode = blendMode;
	}

	@Override
	public String toString()
	{
		return "SkillSideEffect [id="
			+ id
			+ ", name="
			+ name
			+ ", desc="
			+ desc
			+ ", type="
			+ type
			+ ", traitType="
			+ traitType
			+ ", imgName="
			+ imgName
			+ ", imgPixelOffsetX="
			+ imgPixelOffsetX
			+ ", imgPixelOffsetY="
			+ imgPixelOffsetY
			+ ", iconImgName="
			+ iconImgName
			+ ", pfxName="
			+ pfxName
			+ ", pfxColor="
			+ pfxColor
			+ ", positionType="
			+ positionType
			+ ", pfxPixelOffsetX="
			+ pfxPixelOffsetX
			+ ", pfxPixelOffsetY="
			+ pfxPixelOffsetY
			+ ", blendMode="
			+ blendMode
			+ "]";
	}
	
}
