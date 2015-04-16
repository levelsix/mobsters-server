package com.lvl6.info;

import java.util.Date;

public class ClanGiftForUser {

	private String id;
	private String receiverUserId;
	private String gifterUserId;
	private int clanGiftId;
	private int rewardId;
	private Date timeReceived;
	private String reasonForGift;

	public ClanGiftForUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClanGiftForUser(String id, String receiverUserId,
			String gifterUserId, int clanGiftId, int rewardId,
			Date timeReceived, String reasonForGift) {
		super();
		this.id = id;
		this.receiverUserId = receiverUserId;
		this.gifterUserId = gifterUserId;
		this.clanGiftId = clanGiftId;
		this.rewardId = rewardId;
		this.timeReceived = timeReceived;
		this.reasonForGift = reasonForGift;
	}



	public int getRewardId() {
		return rewardId;
	}



	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}



	public String getReasonForGift() {
		return reasonForGift;
	}



	public void setReasonForGift(String reasonForGift) {
		this.reasonForGift = reasonForGift;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReceiverUserId() {
		return receiverUserId;
	}
	public void setReceiverUserId(String receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
	public String getGifterUserId() {
		return gifterUserId;
	}
	public void setGifterUserId(String gifterUserId) {
		this.gifterUserId = gifterUserId;
	}

	public int getClanGiftId() {
		return clanGiftId;
	}

	public void setClanGiftId(int clanGiftId) {
		this.clanGiftId = clanGiftId;
	}

	public Date getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(Date timeReceived) {
		this.timeReceived = timeReceived;
	}

	@Override
	public String toString() {
		return "ClanGiftForUser [id=" + id + ", receiverUserId="
				+ receiverUserId + ", gifterUserId=" + gifterUserId
				+ ", clanGiftId=" + clanGiftId + ", rewardId=" + rewardId
				+ ", timeReceived=" + timeReceived + ", reasonForGift="
				+ reasonForGift + "]";
	}


}
