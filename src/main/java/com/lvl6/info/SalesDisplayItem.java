package com.lvl6.info;

public class SalesDisplayItem {

	public SalesDisplayItem(int id, int salesPackageId, int monsterId,
			int monsterQuantity, int itemId, int itemQuantity) {
		super();
		this.id = id;
		this.salesPackageId = salesPackageId;
		this.monsterId = monsterId;
		this.monsterQuantity = monsterQuantity;
		this.itemId = itemId;
		this.itemQuantity = itemQuantity;
	}
	private static final long serialVersionUID = 1549953377153488834L;

	private int id;
	private int salesPackageId;
	private int monsterId;
	private int monsterQuantity;
	private int itemId;
	private int itemQuantity;

<<<<<<< HEAD


	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getSalesPackageId() {
		return salesPackageId;
	}



	public void setSalesPackageId(int salesPackageId) {
		this.salesPackageId = salesPackageId;
	}



	public int getMonsterId() {
		return monsterId;
	}



	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}



	public int getMonsterQuantity() {
		return monsterQuantity;
	}



	public void setMonsterQuantity(int monsterQuantity) {
		this.monsterQuantity = monsterQuantity;
	}



	public int getItemId() {
		return itemId;
	}



	public void setItemId(int itemId) {
		this.itemId = itemId;
	}



	public int getItemQuantity() {
		return itemQuantity;
	}



	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}



=======
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSalesPackageId() {
		return salesPackageId;
	}
	public void setSalesPackageId(int salesPackageId) {
		this.salesPackageId = salesPackageId;
	}
	public int getMonsterId() {
		return monsterId;
	}
	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}
	public int getMonsterQuantity() {
		return monsterQuantity;
	}
	public void setMonsterQuantity(int monsterQuantity) {
		this.monsterQuantity = monsterQuantity;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
>>>>>>> d735b6a62f536b42e8bf5dd47b958cf6a43e1bc9
	@Override
	public String toString() {
		return "SalesDisplayItem [id=" + id + ", salesPackageId="
				+ salesPackageId + ", monsterId=" + monsterId
				+ ", monsterQuantity=" + monsterQuantity + ", itemId=" + itemId
				+ ", itemQuantity=" + itemQuantity + "]";
	}


<<<<<<< HEAD
=======


>>>>>>> d735b6a62f536b42e8bf5dd47b958cf6a43e1bc9
}
