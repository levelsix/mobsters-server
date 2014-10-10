package com.lvl6.info;

import java.io.Serializable;

public class StructureClanHouse implements Serializable {

	private static final long serialVersionUID = 9044869383817373202L;
	
	private int structId;
	private int maxHelpersPerSolicitation;
	
	public StructureClanHouse()
	{
		super();
	}

	public StructureClanHouse( int structId, int maxHelpersPerSolicitation )
	{
		super();
		this.structId = structId;
		this.maxHelpersPerSolicitation = maxHelpersPerSolicitation;
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

	@Override
	public String toString()
	{
		return "StructureClanHouse [structId="
			+ structId
			+ ", maxHelpersPerSolicitation="
			+ maxHelpersPerSolicitation
			+ "]";
	}

}
