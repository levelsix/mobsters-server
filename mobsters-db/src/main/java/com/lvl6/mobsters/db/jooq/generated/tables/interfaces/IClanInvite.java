/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
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
public interface IClanInvite extends Serializable {

	/**
	 * Setter for <code>mobsters.clan_invite.id</code>.
	 */
	public IClanInvite setId(String value);

	/**
	 * Getter for <code>mobsters.clan_invite.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.clan_invite.user_id</code>. This and inviter_id form a unique composite key
	 */
	public IClanInvite setUserId(String value);

	/**
	 * Getter for <code>mobsters.clan_invite.user_id</code>. This and inviter_id form a unique composite key
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.clan_invite.inviter_id</code>. This and user_id form a unique key composite key
	 */
	public IClanInvite setInviterId(String value);

	/**
	 * Getter for <code>mobsters.clan_invite.inviter_id</code>. This and user_id form a unique key composite key
	 */
	@Column(name = "inviter_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getInviterId();

	/**
	 * Setter for <code>mobsters.clan_invite.clan_id</code>.
	 */
	public IClanInvite setClanId(String value);

	/**
	 * Getter for <code>mobsters.clan_invite.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getClanId();

	/**
	 * Setter for <code>mobsters.clan_invite.time_of_invite</code>.
	 */
	public IClanInvite setTimeOfInvite(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_invite.time_of_invite</code>.
	 */
	@Column(name = "time_of_invite", nullable = false)
	@NotNull
	public Timestamp getTimeOfInvite();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IClanInvite
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanInvite from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IClanInvite
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanInvite> E into(E into);
}
