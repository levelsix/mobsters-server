package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserFacebookInviteForSlot implements Serializable {

	private static final long serialVersionUID = 1780229714903784581L;
	private int id;
	private int inviterUserId;
	private String recipientFacebookId;
	private Date timeOfInvite;
	private boolean accepted;
	
	public UserFacebookInviteForSlot(int id, int inviterUserId,
			String recipientFacebookId, Date timeOfInvite, boolean accepted) {
		super();
		this.id = id;
		this.inviterUserId = inviterUserId;
		this.recipientFacebookId = recipientFacebookId;
		this.timeOfInvite = timeOfInvite;
		this.accepted = accepted;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInviterUserId() {
		return inviterUserId;
	}

	public void setInviterUserId(int inviterUserId) {
		this.inviterUserId = inviterUserId;
	}

	public String getRecipientFacebookId() {
		return recipientFacebookId;
	}

	public void setRecipientFacebookId(String recipientFacebookId) {
		this.recipientFacebookId = recipientFacebookId;
	}

	public Date getTimeOfInvite() {
		return timeOfInvite;
	}

	public void setTimeOfInvite(Date timeOfInvite) {
		this.timeOfInvite = timeOfInvite;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return "UserFacebookInviteForSlot [id=" + id + ", inviterUserId="
				+ inviterUserId + ", recipientFacebookId=" + recipientFacebookId
				+ ", timeOfInvite=" + timeOfInvite + ", accepted=" + accepted + "]";
	}
}
