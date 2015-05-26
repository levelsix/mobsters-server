/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureForUser;

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
@Table(name = "structure_for_user", schema = "mobsters")
public class StructureForUser implements IStructureForUser {

	private static final long serialVersionUID = 1028183980;

	private String    id;
	private String    userId;
	private Integer   structId;
	private Timestamp lastRetrieved;
	private Integer   xcoord;
	private Integer   ycoord;
	private Timestamp purchaseTime;
	private Byte      isComplete;
	private String    orientation;
	private Byte      fbInviteStructLvl;

	public StructureForUser() {}

	public StructureForUser(StructureForUser value) {
		this.id = value.id;
		this.userId = value.userId;
		this.structId = value.structId;
		this.lastRetrieved = value.lastRetrieved;
		this.xcoord = value.xcoord;
		this.ycoord = value.ycoord;
		this.purchaseTime = value.purchaseTime;
		this.isComplete = value.isComplete;
		this.orientation = value.orientation;
		this.fbInviteStructLvl = value.fbInviteStructLvl;
	}

	public StructureForUser(
		String    id,
		String    userId,
		Integer   structId,
		Timestamp lastRetrieved,
		Integer   xcoord,
		Integer   ycoord,
		Timestamp purchaseTime,
		Byte      isComplete,
		String    orientation,
		Byte      fbInviteStructLvl
	) {
		this.id = id;
		this.userId = userId;
		this.structId = structId;
		this.lastRetrieved = lastRetrieved;
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.purchaseTime = purchaseTime;
		this.isComplete = isComplete;
		this.orientation = orientation;
		this.fbInviteStructLvl = fbInviteStructLvl;
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
	public StructureForUser setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public StructureForUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "struct_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureForUser setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "last_retrieved")
	@Override
	public Timestamp getLastRetrieved() {
		return this.lastRetrieved;
	}

	@Override
	public StructureForUser setLastRetrieved(Timestamp lastRetrieved) {
		this.lastRetrieved = lastRetrieved;
		return this;
	}

	@Column(name = "xcoord", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getXcoord() {
		return this.xcoord;
	}

	@Override
	public StructureForUser setXcoord(Integer xcoord) {
		this.xcoord = xcoord;
		return this;
	}

	@Column(name = "ycoord", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getYcoord() {
		return this.ycoord;
	}

	@Override
	public StructureForUser setYcoord(Integer ycoord) {
		this.ycoord = ycoord;
		return this;
	}

	@Column(name = "purchase_time")
	@Override
	public Timestamp getPurchaseTime() {
		return this.purchaseTime;
	}

	@Override
	public StructureForUser setPurchaseTime(Timestamp purchaseTime) {
		this.purchaseTime = purchaseTime;
		return this;
	}

	@Column(name = "is_complete", precision = 3)
	@Override
	public Byte getIsComplete() {
		return this.isComplete;
	}

	@Override
	public StructureForUser setIsComplete(Byte isComplete) {
		this.isComplete = isComplete;
		return this;
	}

	@Column(name = "orientation", length = 45)
	@Size(max = 45)
	@Override
	public String getOrientation() {
		return this.orientation;
	}

	@Override
	public StructureForUser setOrientation(String orientation) {
		this.orientation = orientation;
		return this;
	}

	@Column(name = "fb_invite_struct_lvl", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getFbInviteStructLvl() {
		return this.fbInviteStructLvl;
	}

	@Override
	public StructureForUser setFbInviteStructLvl(Byte fbInviteStructLvl) {
		this.fbInviteStructLvl = fbInviteStructLvl;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureForUser from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setStructId(from.getStructId());
		setLastRetrieved(from.getLastRetrieved());
		setXcoord(from.getXcoord());
		setYcoord(from.getYcoord());
		setPurchaseTime(from.getPurchaseTime());
		setIsComplete(from.getIsComplete());
		setOrientation(from.getOrientation());
		setFbInviteStructLvl(from.getFbInviteStructLvl());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureForUser> E into(E into) {
		into.from(this);
		return into;
	}
}
