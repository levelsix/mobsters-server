package com.lvl6.info;

import java.io.Serializable;

public class StructureClanHouse implements Serializable {

	private static final long serialVersionUID = -5323145807596864964L;
	
	private int structId;
	private int maxHelpersPerSolicitation;
	private int teamDonationPowerLimit;
	
	public StructureClanHouse()
	{
		super();
	}

	public StructureClanHouse(
		int structId,
		int maxHelpersPerSolicitation,
		int teamDonationPowerLimit )
	{
		super();
		this.structId = structId;
		this.maxHelpersPerSolicitation = maxHelpersPerSolicitation;
		this.teamDonationPowerLimit = teamDonationPowerLimit;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getMaxHelpersPerSolicitation()
	{
		return maxHelpersPerSolicitation;
	}

	public void setMaxHelpersPerSolicitation( int maxHelpersPerSolicitation )
	{
		this.maxHelpersPerSolicitation = maxHelpersPerSolicitation;
	}

	public int getTeamDonationPowerLimit()
	{
		return teamDonationPowerLimit;
	}

	public void setTeamDonationPowerLimit( int teamDonationPowerLimit )
	{
		this.teamDonationPowerLimit = teamDonationPowerLimit;
	}

	@Override
	public String toString()
	{
		return "StructureClanHouse [structId="
			+ structId
			+ ", maxHelpersPerSolicitation="
			+ maxHelpersPerSolicitation
			+ ", teamDonationPowerLimit="
			+ teamDonationPowerLimit
			+ "]";
	}

}
