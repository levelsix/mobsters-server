package com.lvl6.info;

import java.util.Date;

public class GiftForUser {

	private String id;
	private String gifterUserId;
	private String receiverUserId;
	private String giftType;
	private int staticDataId;
	private Date timeOfEntry;
	private int rewardId;
	private boolean collected;
	private int minutesTillExpiration;
	private String reasonForGift;

	public GiftForUser() {
		super();
	}

	public GiftForUser(String id, String gifterUserId, String receiverUserId,
			String giftType, int staticDataId, Date timeOfEntry, int rewardId,
			boolean collected, int minutesTillExpiration, String reasonForGift) {
		super();
		this.id = id;
		this.gifterUserId = gifterUserId;
		this.receiverUserId = receiverUserId;
		this.giftType = giftType;
		this.staticDataId = staticDataId;
		this.timeOfEntry = timeOfEntry;
		this.rewardId = rewardId;
		this.collected = collected;
		this.minutesTillExpiration = minutesTillExpiration;
		this.reasonForGift = reasonForGift;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGifterUserId() {
		return gifterUserId;
	}

	public void setGifterUserId(String gifterUserId) {
		this.gifterUserId = gifterUserId;
	}

	public String getReceiverUserId() {
		return receiverUserId;
	}

	public void setReceiverUserId(String receiverUserId) {
		this.receiverUserId = receiverUserId;
	}

	public String getGiftType() {
		return giftType;
	}

	public void setGiftType(String giftType) {
		this.giftType = giftType;
	}

	public int getStaticDataId() {
		return staticDataId;
	}

	public void setStaticDataId(int staticDataId) {
		this.staticDataId = staticDataId;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}

	public int getMinutesTillExpiration() {
		return minutesTillExpiration;
	}

	public void setMinutesTillExpiration(int minutesTillExpiration) {
		this.minutesTillExpiration = minutesTillExpiration;
	}

	public String getReasonForGift() {
		return reasonForGift;
	}

	public void setReasonForGift(String reasonForGift) {
		this.reasonForGift = reasonForGift;
	}

	@Override
	public String toString() {
		return "GiftForUser [id=" + id + ", gifterUserId=" + gifterUserId
				+ ", receiverUserId=" + receiverUserId + ", giftType="
				+ giftType + ", staticDataId=" + staticDataId
				+ ", timeOfEntry=" + timeOfEntry + ", rewardId=" + rewardId
				+ ", collected=" + collected + ", minutesTillExpiration="
				+ minutesTillExpiration + ", reasonForGift=" + reasonForGift
				+ "]";
	}

}
