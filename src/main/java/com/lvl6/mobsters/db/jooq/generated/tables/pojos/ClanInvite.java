/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanInvite;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "clan_invite", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "inviter_id"})
})
public class ClanInvite implements IClanInvite {

	private static final long serialVersionUID = 1821959403;

	private String    id;
	private String    userId;
	private String    inviterId;
	private String    clanId;
	private Timestamp timeOfInvite;

	public ClanInvite() {}

	public ClanInvite(ClanInvite value) {
		this.id = value.id;
		this.userId = value.userId;
		this.inviterId = value.inviterId;
		this.clanId = value.clanId;
		this.timeOfInvite = value.timeOfInvite;
	}

	public ClanInvite(
		String    id,
		String    userId,
		String    inviterId,
		String    clanId,
		Timestamp timeOfInvite
	) {
		this.id = id;
		this.userId = userId;
		this.inviterId = inviterId;
		this.clanId = clanId;
		this.timeOfInvite = timeOfInvite;
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
	public ClanInvite setId(String id) {
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
	public ClanInvite setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "inviter_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getInviterId() {
		return this.inviterId;
	}

	@Override
	public ClanInvite setInviterId(String inviterId) {
		this.inviterId = inviterId;
		return this;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanInvite setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "time_of_invite", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfInvite() {
		return this.timeOfInvite;
	}

	@Override
	public ClanInvite setTimeOfInvite(Timestamp timeOfInvite) {
		this.timeOfInvite = timeOfInvite;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanInvite from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setInviterId(from.getInviterId());
		setClanId(from.getClanId());
		setTimeOfInvite(from.getTimeOfInvite());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanInvite> E into(E into) {
		into.from(this);
		return into;
	}
}
