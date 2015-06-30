/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanForUser;

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
@Table(name = "clan_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "clan_id"})
})
public class ClanForUserPojo implements IClanForUser {

	private static final long serialVersionUID = -1767158022;

	private String    userId;
	private String    clanId;
	private String    status;
	private Timestamp requestTime;

	public ClanForUserPojo() {}

	public ClanForUserPojo(ClanForUserPojo value) {
		this.userId = value.userId;
		this.clanId = value.clanId;
		this.status = value.status;
		this.requestTime = value.requestTime;
	}

	public ClanForUserPojo(
		String    userId,
		String    clanId,
		String    status,
		Timestamp requestTime
	) {
		this.userId = userId;
		this.clanId = clanId;
		this.status = status;
		this.requestTime = requestTime;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public ClanForUserPojo setUserId(String userId) {
		this.userId = userId;
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
	public ClanForUserPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "status", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	public ClanForUserPojo setStatus(String status) {
		this.status = status;
		return this;
	}

	@Column(name = "request_time", nullable = false)
	@NotNull
	@Override
	public Timestamp getRequestTime() {
		return this.requestTime;
	}

	@Override
	public ClanForUserPojo setRequestTime(Timestamp requestTime) {
		this.requestTime = requestTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanForUser from) {
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setStatus(from.getStatus());
		setRequestTime(from.getRequestTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanForUserRecord();
		poop.from(this);
		return "ClanForUserPojo[" + poop.valuesRow() + "]";
	}
}
