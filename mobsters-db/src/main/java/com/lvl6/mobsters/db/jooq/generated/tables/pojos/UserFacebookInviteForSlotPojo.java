/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserFacebookInviteForSlot;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "user_facebook_invite_for_slot", schema = "mobsters")
public class UserFacebookInviteForSlotPojo implements IUserFacebookInviteForSlot {

	private static final long serialVersionUID = 2095398863;

	private String    id;
	private String    inviterUserId;
	private String    recipientFacebookId;
	private Timestamp timeOfInvite;
	private Timestamp timeAccepted;
	private String    userStructId;
	private Byte      userStructFbLvl;
	private Timestamp timeRedeemed;

	public UserFacebookInviteForSlotPojo() {}

	public UserFacebookInviteForSlotPojo(UserFacebookInviteForSlotPojo value) {
		this.id = value.id;
		this.inviterUserId = value.inviterUserId;
		this.recipientFacebookId = value.recipientFacebookId;
		this.timeOfInvite = value.timeOfInvite;
		this.timeAccepted = value.timeAccepted;
		this.userStructId = value.userStructId;
		this.userStructFbLvl = value.userStructFbLvl;
		this.timeRedeemed = value.timeRedeemed;
	}

	public UserFacebookInviteForSlotPojo(
		String    id,
		String    inviterUserId,
		String    recipientFacebookId,
		Timestamp timeOfInvite,
		Timestamp timeAccepted,
		String    userStructId,
		Byte      userStructFbLvl,
		Timestamp timeRedeemed
	) {
		this.id = id;
		this.inviterUserId = inviterUserId;
		this.recipientFacebookId = recipientFacebookId;
		this.timeOfInvite = timeOfInvite;
		this.timeAccepted = timeAccepted;
		this.userStructId = userStructId;
		this.userStructFbLvl = userStructFbLvl;
		this.timeRedeemed = timeRedeemed;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public UserFacebookInviteForSlotPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "inviter_user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getInviterUserId() {
		return this.inviterUserId;
	}

	@Override
	public UserFacebookInviteForSlotPojo setInviterUserId(String inviterUserId) {
		this.inviterUserId = inviterUserId;
		return this;
	}

	@Column(name = "recipient_facebook_id", length = 1000)
	@Size(max = 1000)
	@Override
	public String getRecipientFacebookId() {
		return this.recipientFacebookId;
	}

	@Override
	public UserFacebookInviteForSlotPojo setRecipientFacebookId(String recipientFacebookId) {
		this.recipientFacebookId = recipientFacebookId;
		return this;
	}

	@Column(name = "time_of_invite")
	@Override
	public Timestamp getTimeOfInvite() {
		return this.timeOfInvite;
	}

	@Override
	public UserFacebookInviteForSlotPojo setTimeOfInvite(Timestamp timeOfInvite) {
		this.timeOfInvite = timeOfInvite;
		return this;
	}

	@Column(name = "time_accepted")
	@Override
	public Timestamp getTimeAccepted() {
		return this.timeAccepted;
	}

	@Override
	public UserFacebookInviteForSlotPojo setTimeAccepted(Timestamp timeAccepted) {
		this.timeAccepted = timeAccepted;
		return this;
	}

	@Column(name = "user_struct_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserStructId() {
		return this.userStructId;
	}

	@Override
	public UserFacebookInviteForSlotPojo setUserStructId(String userStructId) {
		this.userStructId = userStructId;
		return this;
	}

	@Column(name = "user_struct_fb_lvl", precision = 3)
	@Override
	public Byte getUserStructFbLvl() {
		return this.userStructFbLvl;
	}

	@Override
	public UserFacebookInviteForSlotPojo setUserStructFbLvl(Byte userStructFbLvl) {
		this.userStructFbLvl = userStructFbLvl;
		return this;
	}

	@Column(name = "time_redeemed")
	@Override
	public Timestamp getTimeRedeemed() {
		return this.timeRedeemed;
	}

	@Override
	public UserFacebookInviteForSlotPojo setTimeRedeemed(Timestamp timeRedeemed) {
		this.timeRedeemed = timeRedeemed;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserFacebookInviteForSlot from) {
		setId(from.getId());
		setInviterUserId(from.getInviterUserId());
		setRecipientFacebookId(from.getRecipientFacebookId());
		setTimeOfInvite(from.getTimeOfInvite());
		setTimeAccepted(from.getTimeAccepted());
		setUserStructId(from.getUserStructId());
		setUserStructFbLvl(from.getUserStructFbLvl());
		setTimeRedeemed(from.getTimeRedeemed());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserFacebookInviteForSlot> E into(E into) {
		into.from(this);
		return into;
	}
}