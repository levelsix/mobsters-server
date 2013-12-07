package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserFacebookInviteForSlot implements Serializable {

	private static final long serialVersionUID = -1731978369694889921L;
	private int id;
	private int inviterUserId;
	private String recipientFacebookId;
	private Date timeOfInvite;
	private Date timeAccepted;
	private int userStructId;
	private int userStructFbLvl;
	private Date timeRedeemed;
	
	public UserFacebookInviteForSlot(int id, int inviterUserId,
			String recipientFacebookId, Date timeOfInvite, Date timeAccepted,
			int userStructId, int userStructFbLvl, Date timeRedeemed) {
		super();
		this.id = id;
		this.inviterUserId = inviterUserId;
		this.recipientFacebookId = recipientFacebookId;
		this.timeOfInvite = timeOfInvite;
		this.timeAccepted = timeAccepted;
		this.userStructId = userStructId;
		this.userStructFbLvl = userStructFbLvl;
		this.timeRedeemed = timeRedeemed;
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

	public Date getTimeAccepted() {
		return timeAccepted;
	}

	public void setTimeAccepted(Date timeAccepted) {
		this.timeAccepted = timeAccepted;
	}

	public int getUserStructId() {
		return userStructId;
	}

	public void setUserStructId(int userStructId) {
		this.userStructId = userStructId;
	}

	public int getUserStructFbLvl() {
		return userStructFbLvl;
	}

	public void setUserStructFbLvl(int userStructFbLvl) {
		this.userStructFbLvl = userStructFbLvl;
	}

	public Date getTimeRedeemed() {
		return timeRedeemed;
	}

	public void setTimeRedeemed(Date timeRedeemed) {
		this.timeRedeemed = timeRedeemed;
	}

	@Override
	public String toString() {
		return "UserFacebookInviteForSlot [id=" + id + ", inviterUserId="
				+ inviterUserId + ", recipientFacebookId=" + recipientFacebookId
				+ ", timeOfInvite=" + timeOfInvite + ", timeAccepted=" + timeAccepted
				+ ", userStructId=" + userStructId + ", userStructFbLvl="
				+ userStructFbLvl + ", timeRedeemed=" + timeRedeemed + "]";
	}
	
}
