package com.lvl6.info;

import java.io.Serializable;

public class MiniJobRefreshItem implements Serializable {

	private static final long serialVersionUID = -1488572539555619850L;

	private int structId;
	private int itemId;
	private int gemPrice;

	public MiniJobRefreshItem() {
		super();
	}

	public MiniJobRefreshItem( int structId, int itemId, int gemPrice ) {
		super();
		this.structId = structId;
		this.itemId = itemId;
		this.gemPrice = gemPrice;
	}

	public int getStructId() {
		return structId;
	}

	public void setStructId(int structId) {
		this.structId = structId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getGemPrice() {
		return gemPrice;
	}

	public void setGemPrice(int gemPrice) {
		this.gemPrice = gemPrice;
	}

	@Override
	public String toString() {
		return "MiniJobItemRefresh [structId=" + structId + ", itemId="
				+ itemId + ", gemPrice=" + gemPrice + "]";
	}

}
