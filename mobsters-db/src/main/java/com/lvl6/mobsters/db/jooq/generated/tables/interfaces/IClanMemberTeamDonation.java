/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
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
@Table(name = "clan_member_team_donation", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id", "clan_id"}),
	@UniqueConstraint(columnNames = {"clan_id", "user_id"})
})
public interface IClanMemberTeamDonation extends Serializable {

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.id</code>.
	 */
	public IClanMemberTeamDonation setId(String value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.user_id</code>.
	 */
	public IClanMemberTeamDonation setUserId(String value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.clan_id</code>.
	 */
	public IClanMemberTeamDonation setClanId(String value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getClanId();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.power_limit</code>.
	 */
	public IClanMemberTeamDonation setPowerLimit(Integer value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.power_limit</code>.
	 */
	@Column(name = "power_limit", precision = 10)
	public Integer getPowerLimit();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.fulfilled</code>.
	 */
	public IClanMemberTeamDonation setFulfilled(Boolean value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.fulfilled</code>.
	 */
	@Column(name = "fulfilled", precision = 1)
	public Boolean getFulfilled();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.msg</code>.
	 */
	public IClanMemberTeamDonation setMsg(String value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.msg</code>.
	 */
	@Column(name = "msg", length = 65535)
	@Size(max = 65535)
	public String getMsg();

	/**
	 * Setter for <code>mobsters.clan_member_team_donation.time_of_solicitation</code>.
	 */
	public IClanMemberTeamDonation setTimeOfSolicitation(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_member_team_donation.time_of_solicitation</code>.
	 */
	@Column(name = "time_of_solicitation")
	public Timestamp getTimeOfSolicitation();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IClanMemberTeamDonation
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanMemberTeamDonation from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IClanMemberTeamDonation
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanMemberTeamDonation> E into(E into);
}
