package com.lvl6.info;

import java.io.Serializable;

public class StructureResidence implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = 742347051351586126L;
	//	private static final long serialVersionUID = 742347051351586126L;
	private int structId;
	//how many monster slots this residence gives the user (absolute number)
	//does not depend on previous lower level structures
	private int numMonsterSlots;
	
	//additional slots if user buys some gems or invites friends
	private int numBonusMonsterSlots;
	
	//number of gems it costs to buy one additional monster slot
	private int numGemsRequired;
	
	//number of accepted fb invites to get one additional monster slot
	private int numAcceptedFbInvites;
	
	public StructureResidence(int structId, int numMonsterSlots,
			int numBonusMonsterSlots, int numGemsRequired, int numAcceptedFbInvites) {
		super();
		this.structId = structId;
		this.numMonsterSlots = numMonsterSlots;
		this.numBonusMonsterSlots = numBonusMonsterSlots;
		this.numGemsRequired = numGemsRequired;
		this.numAcceptedFbInvites = numAcceptedFbInvites;
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

	@Override
	public String toString() {
		return "StructureResidence [structId=" + structId + ", numMonsterSlots="
				+ numMonsterSlots + ", numBonusMonsterSlots=" + numBonusMonsterSlots
				+ ", numGemsRequired=" + numGemsRequired + ", numAcceptedFbInvites="
				+ numAcceptedFbInvites + "]";
	}
	
}
