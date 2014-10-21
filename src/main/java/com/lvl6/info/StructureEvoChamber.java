package com.lvl6.info;

import java.io.Serializable;

public class StructureEvoChamber implements Serializable {
	
	private static final long serialVersionUID = 4207004739133603011L;
	
	private int structId;
	private String qualityUnlocked;
	private int evoTierUnlocked;
	
	public StructureEvoChamber( int structId, String qualityUnlocked, int evoTierUnlocked )
	{
		super();
		this.structId = structId;
		this.qualityUnlocked = qualityUnlocked;
		this.evoTierUnlocked = evoTierUnlocked;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public String getQualityUnlocked()
	{
		return qualityUnlocked;
	}

	public void setQualityUnlocked( String qualityUnlocked )
	{
		this.qualityUnlocked = qualityUnlocked;
	}

	public int getEvoTierUnlocked()
	{
		return evoTierUnlocked;
	}

	public void setEvoTierUnlocked( int evoTierUnlocked )
	{
		this.evoTierUnlocked = evoTierUnlocked;
	}

	@Override
	public String toString()
	{
		return "StructureEvoChamber [structId="
			+ structId
			+ ", qualityUnlocked="
			+ qualityUnlocked
			+ ", evoTierUnlocked="
			+ evoTierUnlocked
			+ "]";
	}

}
