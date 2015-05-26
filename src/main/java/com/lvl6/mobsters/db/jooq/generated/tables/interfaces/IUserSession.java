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
@Table(name = "user_session", schema = "mobsters")
public interface IUserSession extends Serializable {

	/**
	 * Setter for <code>mobsters.user_session.user_id</code>.
	 */
	public IUserSession setUserId(String value);

	/**
	 * Getter for <code>mobsters.user_session.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.user_session.login_time</code>.
	 */
	public IUserSession setLoginTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.user_session.login_time</code>.
	 */
	@Column(name = "login_time")
	public Timestamp getLoginTime();

	/**
	 * Setter for <code>mobsters.user_session.logout_time</code>.
	 */
	public IUserSession setLogoutTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.user_session.logout_time</code>.
	 */
	@Column(name = "logout_time")
	public Timestamp getLogoutTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IUserSession
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserSession from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IUserSession
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserSession> E into(E into);
}
