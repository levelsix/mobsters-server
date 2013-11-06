package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class UserFacebookInviteForSlotAccepted implements Serializable {

	private static final long serialVersionUID = -2589925714343409714L;
	private int id;
	private int inviterUserId;
	private String recipientFacebookId;
	private Date timeOfInvite;
	private Date timeAccepted;
	private int nthExtraSlotsViaFb;
	private Date timeOfEntry;
	
	public UserFacebookInviteForSlotAccepted(int id, int inviterUserId,
			String recipientFacebookId, Date timeOfInvite, Date timeAccepted,
			int nthExtraSlotsViaFb, Date timeOfEntry) {
		super();
		this.id = id;
		this.inviterUserId = inviterUserId;
		this.recipientFacebookId = recipientFacebookId;
		this.timeOfInvite = timeOfInvite;
		this.timeAccepted = timeAccepted;
		this.nthExtraSlotsViaFb = nthExtraSlotsViaFb;
		this.timeOfEntry = timeOfEntry;
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

	public int getNthExtraSlotsViaFb() {
		return nthExtraSlotsViaFb;
	}

	public void setNthExtraSlotsViaFb(int nthExtraSlotsViaFb) {
		this.nthExtraSlotsViaFb = nthExtraSlotsViaFb;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	@Override
	public String toString() {
		return "UserFacebookInviteForSlotAccepted [id=" + id + ", inviterUserId="
				+ inviterUserId + ", recipientFacebookId=" + recipientFacebookId
				+ ", timeOfInvite=" + timeOfInvite + ", timeAccepted=" + timeAccepted
				+ ", nthExtraSlotsViaFb=" + nthExtraSlotsViaFb + ", timeOfEntry="
				+ timeOfEntry + "]";
	}
	
}
