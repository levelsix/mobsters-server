/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanAvengeUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "clan_avenge_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"clan_id", "user_id", "clan_avenge_id"})
})
public class ClanAvengeUser implements IClanAvengeUser {

	private static final long serialVersionUID = 563208547;

	private String    clanId;
	private String    clanAvengeId;
	private String    userId;
	private Timestamp avengeTime;

	public ClanAvengeUser() {}

	public ClanAvengeUser(ClanAvengeUser value) {
		this.clanId = value.clanId;
		this.clanAvengeId = value.clanAvengeId;
		this.userId = value.userId;
		this.avengeTime = value.avengeTime;
	}

	public ClanAvengeUser(
		String    clanId,
		String    clanAvengeId,
		String    userId,
		Timestamp avengeTime
	) {
		this.clanId = clanId;
		this.clanAvengeId = clanAvengeId;
		this.userId = userId;
		this.avengeTime = avengeTime;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanAvengeUser setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "clan_avenge_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanAvengeId() {
		return this.clanAvengeId;
	}

	@Override
	public ClanAvengeUser setClanAvengeId(String clanAvengeId) {
		this.clanAvengeId = clanAvengeId;
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
	public ClanAvengeUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "avenge_time")
	@Override
	public Timestamp getAvengeTime() {
		return this.avengeTime;
	}

	@Override
	public ClanAvengeUser setAvengeTime(Timestamp avengeTime) {
		this.avengeTime = avengeTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanAvengeUser from) {
		setClanId(from.getClanId());
		setClanAvengeId(from.getClanAvengeId());
		setUserId(from.getUserId());
		setAvengeTime(from.getAvengeTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanAvengeUser> E into(E into) {
		into.from(this);
		return into;
	}
}
