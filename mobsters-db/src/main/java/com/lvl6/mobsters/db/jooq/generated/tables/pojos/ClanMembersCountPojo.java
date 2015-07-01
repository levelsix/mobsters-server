/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanMembersCount;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * VIEW
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
@Table(name = "clan_members_count", schema = "mobsters")
public class ClanMembersCountPojo implements IClanMembersCount {

	private static final long serialVersionUID = 220654327;

	private String clanId;
	private Long   members;

	public ClanMembersCountPojo() {}

	public ClanMembersCountPojo(ClanMembersCountPojo value) {
		this.clanId = value.clanId;
		this.members = value.members;
	}

	public ClanMembersCountPojo(
		String clanId,
		Long   members
	) {
		this.clanId = clanId;
		this.members = members;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanMembersCountPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "members", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getMembers() {
		return this.members;
	}

	@Override
	public ClanMembersCountPojo setMembers(Long members) {
		this.members = members;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanMembersCount from) {
		setClanId(from.getClanId());
		setMembers(from.getMembers());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanMembersCount> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanMembersCountRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanMembersCountRecord();
		poop.from(this);
		return "ClanMembersCountPojo[" + poop.valuesRow() + "]";
	}
}
