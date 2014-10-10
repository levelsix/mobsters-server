package com.lvl6.info;

import java.io.Serializable;

public class StructureClanHouse implements Serializable {

	private static final long serialVersionUID = 8658559364312847443L;
	
	private int structId;
	private int solicitationLimit;
	
	public StructureClanHouse()
	{
		super();
	}

	public StructureClanHouse( int structId, int solicitationLimit )
	{
		super();
		this.structId = structId;
		this.solicitationLimit = solicitationLimit;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getSolicitationLimit()
	{
		return solicitationLimit;
	}

	public void setSolicitationLimit( int solicitationLimit )
	{
		this.solicitationLimit = solicitationLimit;
	}

	@Override
	public String toString()
	{
		return "StructureClanHouse [structId="
			+ structId
			+ ", solicitationLimit="
			+ solicitationLimit
			+ "]";
	}

}
