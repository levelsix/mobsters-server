package com.lvl6.info;

import java.io.Serializable;

public class StructureResidence implements Serializable {

	private static final long serialVersionUID = 1538477813718671920L;
	
	private int structId;
	//how many monster slots this residence gives the user (absolute number)
	//does not depend on previous lower level structures
	private int numMonsterSlots;
	
	//additional slots if user buys some gems or invites friends
	private int numBonusMonsterSlots;
	
	//number of gems it costs to buy all numBonusMonsterSlots
	private int numGemsRequired;
	
	//number of accepted fb invites to get all numBonusMonsterSlots
	private int numAcceptedFbInvites;
	
	private String occupationName;
	
	private String imgSuffix;
	
	public StructureResidence(int structId, int numMonsterSlots,
			int numBonusMonsterSlots, int numGemsRequired, int numAcceptedFbInvites,
			String occupationName, String imgSuffix) {
		super();
		this.structId = structId;
		this.numMonsterSlots = numMonsterSlots;
		this.numBonusMonsterSlots = numBonusMonsterSlots;
		this.numGemsRequired = numGemsRequired;
		this.numAcceptedFbInvites = numAcceptedFbInvites;
		this.occupationName = occupationName;
		this.imgSuffix = imgSuffix;
	}

	public int getStructId() {
		return structId;
	}
	
	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getNumMonsterSlots() {
		return numMonsterSlots;
	}

	public void setNumMonsterSlots(int numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
	}

	public int getNumBonusMonsterSlots() {
		return numBonusMonsterSlots;
	}

	public void setNumBonusMonsterSlots(int numBonusMonsterSlots) {
		this.numBonusMonsterSlots = numBonusMonsterSlots;
	}

	public int getNumGemsRequired() {
		return numGemsRequired;
	}

	public void setNumGemsRequired(int numGemsRequired) {
		this.numGemsRequired = numGemsRequired;
	}

	public int getNumAcceptedFbInvites() {
		return numAcceptedFbInvites;
	}

	public void setNumAcceptedFbInvites(int numAcceptedFbInvites) {
		this.numAcceptedFbInvites = numAcceptedFbInvites;
	}

	public String getOccupationName() {
		return occupationName;
	}

	public void setOccupationName(String occupationName) {
		this.occupationName = occupationName;
	}

	public String getImgSuffix()
	{
		return imgSuffix;
	}

	public void setImgSuffix( String imgSuffix )
	{
		this.imgSuffix = imgSuffix;
	}

	@Override
	public String toString()
	{
		return "StructureResidence [structId="
			+ structId
			+ ", numMonsterSlots="
			+ numMonsterSlots
			+ ", numBonusMonsterSlots="
			+ numBonusMonsterSlots
			+ ", numGemsRequired="
			+ numGemsRequired
			+ ", numAcceptedFbInvites="
			+ numAcceptedFbInvites
			+ ", occupationName="
			+ occupationName
			+ ", imgSuffix="
			+ imgSuffix
			+ "]";
	}

}
