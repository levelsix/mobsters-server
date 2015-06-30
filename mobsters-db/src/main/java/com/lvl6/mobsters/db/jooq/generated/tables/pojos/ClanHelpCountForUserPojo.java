/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanHelpCountForUser;

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
@Table(name = "clan_help_count_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "clan_id", "date"})
})
public class ClanHelpCountForUserPojo implements IClanHelpCountForUser {

	private static final long serialVersionUID = 1259944666;

	private String    userId;
	private String    clanId;
	private Timestamp date;
	private Integer   solicited;
	private Integer   given;

	public ClanHelpCountForUserPojo() {}

	public ClanHelpCountForUserPojo(ClanHelpCountForUserPojo value) {
		this.userId = value.userId;
		this.clanId = value.clanId;
		this.date = value.date;
		this.solicited = value.solicited;
		this.given = value.given;
	}

	public ClanHelpCountForUserPojo(
		String    userId,
		String    clanId,
		Timestamp date,
		Integer   solicited,
		Integer   given
	) {
		this.userId = userId;
		this.clanId = clanId;
		this.date = date;
		this.solicited = solicited;
		this.given = given;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public ClanHelpCountForUserPojo setUserId(String userId) {
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
	public ClanHelpCountForUserPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "date", nullable = false)
	@NotNull
	@Override
	public Timestamp getDate() {
		return this.date;
	}

	@Override
	public ClanHelpCountForUserPojo setDate(Timestamp date) {
		this.date = date;
		return this;
	}

	@Column(name = "solicited", precision = 10)
	@Override
	public Integer getSolicited() {
		return this.solicited;
	}

	@Override
	public ClanHelpCountForUserPojo setSolicited(Integer solicited) {
		this.solicited = solicited;
		return this;
	}

	@Column(name = "given", precision = 10)
	@Override
	public Integer getGiven() {
		return this.given;
	}

	@Override
	public ClanHelpCountForUserPojo setGiven(Integer given) {
		this.given = given;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanHelpCountForUser from) {
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setDate(from.getDate());
		setSolicited(from.getSolicited());
		setGiven(from.getGiven());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanHelpCountForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanHelpCountForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanHelpCountForUserRecord();
		poop.from(this);
		return "ClanHelpCountForUserPojo[" + poop.valuesRow() + "]";
	}
}
