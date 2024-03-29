/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanStrength;

import java.math.BigDecimal;
import java.math.BigInteger;

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
@Table(name = "clan_strength", schema = "mobsters")
public class ClanStrengthPojo implements IClanStrength {

	private static final long serialVersionUID = -841911650;

	private String     clan;
	private Long       members;
	private Long       postsLastDay;
	private Long       postsLastHour;
	private BigInteger helpsGiven;
	private Long       donations;
	private BigInteger donationsFulfilled;
	private BigDecimal strength;

	public ClanStrengthPojo() {}

	public ClanStrengthPojo(ClanStrengthPojo value) {
		this.clan = value.clan;
		this.members = value.members;
		this.postsLastDay = value.postsLastDay;
		this.postsLastHour = value.postsLastHour;
		this.helpsGiven = value.helpsGiven;
		this.donations = value.donations;
		this.donationsFulfilled = value.donationsFulfilled;
		this.strength = value.strength;
	}

	public ClanStrengthPojo(
		String     clan,
		Long       members,
		Long       postsLastDay,
		Long       postsLastHour,
		BigInteger helpsGiven,
		Long       donations,
		BigInteger donationsFulfilled,
		BigDecimal strength
	) {
		this.clan = clan;
		this.members = members;
		this.postsLastDay = postsLastDay;
		this.postsLastHour = postsLastHour;
		this.helpsGiven = helpsGiven;
		this.donations = donations;
		this.donationsFulfilled = donationsFulfilled;
		this.strength = strength;
	}

	@Column(name = "clan", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClan() {
		return this.clan;
	}

	@Override
	public ClanStrengthPojo setClan(String clan) {
		this.clan = clan;
		return this;
	}

	@Column(name = "members", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getMembers() {
		return this.members;
	}

	@Override
	public ClanStrengthPojo setMembers(Long members) {
		this.members = members;
		return this;
	}

	@Column(name = "posts_last_day", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getPostsLastDay() {
		return this.postsLastDay;
	}

	@Override
	public ClanStrengthPojo setPostsLastDay(Long postsLastDay) {
		this.postsLastDay = postsLastDay;
		return this;
	}

	@Column(name = "posts_last_hour", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getPostsLastHour() {
		return this.postsLastHour;
	}

	@Override
	public ClanStrengthPojo setPostsLastHour(Long postsLastHour) {
		this.postsLastHour = postsLastHour;
		return this;
	}

	@Column(name = "helps_given", nullable = false, precision = 32)
	@NotNull
	@Override
	public BigInteger getHelpsGiven() {
		return this.helpsGiven;
	}

	@Override
	public ClanStrengthPojo setHelpsGiven(BigInteger helpsGiven) {
		this.helpsGiven = helpsGiven;
		return this;
	}

	@Column(name = "donations", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getDonations() {
		return this.donations;
	}

	@Override
	public ClanStrengthPojo setDonations(Long donations) {
		this.donations = donations;
		return this;
	}

	@Column(name = "donations_fulfilled", nullable = false, precision = 23)
	@NotNull
	@Override
	public BigInteger getDonationsFulfilled() {
		return this.donationsFulfilled;
	}

	@Override
	public ClanStrengthPojo setDonationsFulfilled(BigInteger donationsFulfilled) {
		this.donationsFulfilled = donationsFulfilled;
		return this;
	}

	@Column(name = "strength", precision = 65, scale = 16)
	@Override
	public BigDecimal getStrength() {
		return this.strength;
	}

	@Override
	public ClanStrengthPojo setStrength(BigDecimal strength) {
		this.strength = strength;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanStrength from) {
		setClan(from.getClan());
		setMembers(from.getMembers());
		setPostsLastDay(from.getPostsLastDay());
		setPostsLastHour(from.getPostsLastHour());
		setHelpsGiven(from.getHelpsGiven());
		setDonations(from.getDonations());
		setDonationsFulfilled(from.getDonationsFulfilled());
		setStrength(from.getStrength());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanStrength> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanStrengthRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanStrengthRecord();
		poop.from(this);
		return "ClanStrengthPojo[" + poop.valuesRow() + "]";
	}
}
