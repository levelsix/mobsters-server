package com.lvl6.info;

import java.io.Serializable;

public class StructureTownHall implements Serializable {

	private static final long serialVersionUID = -7228014466529143078L;
	
	private int structId;
	private int numResourceOneGenerators;
	private int numResourceOneStorages;
	private int numResourceTwoGenerators;
	private int numResourceTwoStorages;
	private int numHospitals;
	private int numResidences;
	private int numMonsterSlots;
	private int numLabs;
	private int pvpQueueCashCost;
	private int resourceCapacity;
	private int numEvoChambers;
	
	public StructureTownHall(
		int structId,
		int numResourceOneGenerators,
		int numResourceOneStorages,
		int numResourceTwoGenerators,
		int numResourceTwoStorages,
		int numHospitals,
		int numResidences,
		int numMonsterSlots,
		int numLabs,
		int pvpQueueCashCost,
		int resourceCapacity,
		int numEvoChambers )
	{
		super();
		this.structId = structId;
		this.numResourceOneGenerators = numResourceOneGenerators;
		this.numResourceOneStorages = numResourceOneStorages;
		this.numResourceTwoGenerators = numResourceTwoGenerators;
		this.numResourceTwoStorages = numResourceTwoStorages;
		this.numHospitals = numHospitals;
		this.numResidences = numResidences;
		this.numMonsterSlots = numMonsterSlots;
		this.numLabs = numLabs;
		this.pvpQueueCashCost = pvpQueueCashCost;
		this.resourceCapacity = resourceCapacity;
		this.numEvoChambers = numEvoChambers;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getNumResourceOneGenerators() {
		return numResourceOneGenerators;
	}

	public void setNumResourceOneGenerators(int numResourceOneGenerators) {
		this.numResourceOneGenerators = numResourceOneGenerators;
	}

	public int getNumResourceOneStorages() {
		return numResourceOneStorages;
	}

	public void setNumResourceOneStorages(int numResourceOneStorages) {
		this.numResourceOneStorages = numResourceOneStorages;
	}

	public int getNumResourceTwoGenerators() {
		return numResourceTwoGenerators;
	}

	public void setNumResourceTwoGenerators(int numResourceTwoGenerators) {
		this.numResourceTwoGenerators = numResourceTwoGenerators;
	}

	public int getNumResourceTwoStorages() {
		return numResourceTwoStorages;
	}

	public void setNumResourceTwoStorages(int numResourceTwoStorages) {
		this.numResourceTwoStorages = numResourceTwoStorages;
	}

	public int getNumHospitals() {
		return numHospitals;
	}

	public void setNumHospitals(int numHospitals) {
		this.numHospitals = numHospitals;
	}

	public int getNumResidences() {
		return numResidences;
	}

	public void setNumResidences(int numResidences) {
		this.numResidences = numResidences;
	}

	public int getNumMonsterSlots() {
		return numMonsterSlots;
	}

	public void setNumMonsterSlots(int numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
	}

	public int getNumLabs() {
		return numLabs;
	}

	public void setNumLabs(int numLabs) {
		this.numLabs = numLabs;
	}

	public int getPvpQueueCashCost() {
		return pvpQueueCashCost;
	}

	public void setPvpQueueCashCost(int pvpQueueCashCost) {
		this.pvpQueueCashCost = pvpQueueCashCost;
	}

	public int getResourceCapacity() {
		return resourceCapacity;
	}

	public void setResourceCapacity(int resourceCapacity) {
		this.resourceCapacity = resourceCapacity;
	}

	public int getNumEvoChambers()
	{
		return numEvoChambers;
	}

	public void setNumEvoChambers( int numEvoChambers )
	{
		this.numEvoChambers = numEvoChambers;
	}

	@Override
	public String toString()
	{
		return "StructureTownHall [structId="
			+ structId
			+ ", numResourceOneGenerators="
			+ numResourceOneGenerators
			+ ", numResourceOneStorages="
			+ numResourceOneStorages
			+ ", numResourceTwoGenerators="
			+ numResourceTwoGenerators
			+ ", numResourceTwoStorages="
			+ numResourceTwoStorages
			+ ", numHospitals="
			+ numHospitals
			+ ", numResidences="
			+ numResidences
			+ ", numMonsterSlots="
			+ numMonsterSlots
			+ ", numLabs="
			+ numLabs
			+ ", pvpQueueCashCost="
			+ pvpQueueCashCost
			+ ", resourceCapacity="
			+ resourceCapacity
			+ ", numEvoChambers="
			+ numEvoChambers
			+ "]";
	}

}
