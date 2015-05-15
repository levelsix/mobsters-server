/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserClanBossRewardHistory;

import java.sql.Timestamp;

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
@Table(name = "user_clan_boss_reward_history", schema = "mobsters")
public class UserClanBossRewardHistory implements IUserClanBossRewardHistory {

	private static final long serialVersionUID = -1158495930;

	private String    id;
	private String    userId;
	private Integer   clanBossRewardId;
	private Timestamp timeOfEntry;

	public UserClanBossRewardHistory() {}

	public UserClanBossRewardHistory(UserClanBossRewardHistory value) {
		this.id = value.id;
		this.userId = value.userId;
		this.clanBossRewardId = value.clanBossRewardId;
		this.timeOfEntry = value.timeOfEntry;
	}

	public UserClanBossRewardHistory(
		String    id,
		String    userId,
		Integer   clanBossRewardId,
		Timestamp timeOfEntry
	) {
		this.id = id;
		this.userId = userId;
		this.clanBossRewardId = clanBossRewardId;
		this.timeOfEntry = timeOfEntry;
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
	public UserClanBossRewardHistory setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UserClanBossRewardHistory setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "clan_boss_reward_id", precision = 10)
	@Override
	public Integer getClanBossRewardId() {
		return this.clanBossRewardId;
	}

	@Override
	public UserClanBossRewardHistory setClanBossRewardId(Integer clanBossRewardId) {
		this.clanBossRewardId = clanBossRewardId;
		return this;
	}

	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public UserClanBossRewardHistory setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserClanBossRewardHistory from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setClanBossRewardId(from.getClanBossRewardId());
		setTimeOfEntry(from.getTimeOfEntry());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserClanBossRewardHistory> E into(E into) {
		into.from(this);
		return into;
	}
}
