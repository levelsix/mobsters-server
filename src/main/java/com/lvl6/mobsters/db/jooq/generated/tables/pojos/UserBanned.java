/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserBanned;

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
@Table(name = "user_banned", schema = "mobsters")
public class UserBanned implements IUserBanned {

	private static final long serialVersionUID = -486770725;

	private String userId;

	public UserBanned() {}

	public UserBanned(UserBanned value) {
		this.userId = value.userId;
	}

	public UserBanned(
		String userId
	) {
		this.userId = userId;
	}

	@Id
	@Column(name = "user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UserBanned setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserBanned from) {
		setUserId(from.getUserId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserBanned> E into(E into) {
		into.from(this);
		return into;
	}
}