package com.lvl6.info;

public class GiftForTangoUser {

	private String giftForUserId;
	private String gifterUserId;
	private String gifterTangoUserId;

	public GiftForTangoUser() {
		super();
	}

	public GiftForTangoUser(String giftForUserId, String gifterUserId,
			String gifterTangoUserId) {
		super();
		this.giftForUserId = giftForUserId;
		this.gifterUserId = gifterUserId;
		this.gifterTangoUserId = gifterTangoUserId;
	}

	public String getGiftForUserId() {
		return giftForUserId;
	}

	public void setGiftForUserId(String giftForUserId) {
		this.giftForUserId = giftForUserId;
	}

	public String getGifterUserId() {
		return gifterUserId;
	}

	public void setGifterUserId(String gifterUserId) {
		this.gifterUserId = gifterUserId;
	}

	public String getGifterTangoUserId() {
		return gifterTangoUserId;
	}

	public void setGifterTangoUserId(String gifterTangoUserId) {
		this.gifterTangoUserId = gifterTangoUserId;
	}

	@Override
	public String toString() {
		return "GiftForTangoUser [giftForUserId=" + giftForUserId
				+ ", gifterUserId=" + gifterUserId + ", gifterTangoUserId="
				+ gifterTangoUserId + "]";
	}

}
