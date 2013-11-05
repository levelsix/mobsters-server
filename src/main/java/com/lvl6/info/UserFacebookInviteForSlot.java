package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserFacebookInviteForSlot implements Serializable {

	private static final long serialVersionUID = -490530127649788790L;
	private int id;
	private int inviterId;
	private int recipientId;
	private Date timeOfInvite;
	private boolean accepted;
	
	public UserFacebookInviteForSlot(int id, int inviterId, int recipientId,
			Date timeOfInvite, boolean accepted) {
		super();
		this.id = id;
		this.inviterId = inviterId;
		this.recipientId = recipientId;
		this.timeOfInvite = timeOfInvite;
		this.accepted = accepted;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInviterId() {
		return inviterId;
	}

	public void setInviterId(int inviterId) {
		this.inviterId = inviterId;
	}

	public int getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
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
		return "UserFacebookInviteForSlot [id=" + id + ", inviterId=" + inviterId
				+ ", recipientId=" + recipientId + ", timeOfInvite=" + timeOfInvite
				+ ", accepted=" + accepted + "]";
	}
	
}
