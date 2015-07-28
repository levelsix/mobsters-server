/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentForUser;

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
@Table(name = "clan_event_persistent_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "clan_id"})
})
public class ClanEventPersistentForUserPojo implements IClanEventPersistentForUser {

	private static final long serialVersionUID = -810028711;

	private String  userId;
	private String  clanId;
	private Integer crId;
	private Integer crDmgDone;
	private Integer crsId;
	private Integer crsDmgDone;
	private Integer crsmId;
	private Integer crsmDmgDone;
	private String  userMonsterIdOne;
	private String  userMonsterIdTwo;
	private String  userMonsterIdThree;

	public ClanEventPersistentForUserPojo() {}

	public ClanEventPersistentForUserPojo(ClanEventPersistentForUserPojo value) {
		this.userId = value.userId;
		this.clanId = value.clanId;
		this.crId = value.crId;
		this.crDmgDone = value.crDmgDone;
		this.crsId = value.crsId;
		this.crsDmgDone = value.crsDmgDone;
		this.crsmId = value.crsmId;
		this.crsmDmgDone = value.crsmDmgDone;
		this.userMonsterIdOne = value.userMonsterIdOne;
		this.userMonsterIdTwo = value.userMonsterIdTwo;
		this.userMonsterIdThree = value.userMonsterIdThree;
	}

	public ClanEventPersistentForUserPojo(
		String  userId,
		String  clanId,
		Integer crId,
		Integer crDmgDone,
		Integer crsId,
		Integer crsDmgDone,
		Integer crsmId,
		Integer crsmDmgDone,
		String  userMonsterIdOne,
		String  userMonsterIdTwo,
		String  userMonsterIdThree
	) {
		this.userId = userId;
		this.clanId = clanId;
		this.crId = crId;
		this.crDmgDone = crDmgDone;
		this.crsId = crsId;
		this.crsDmgDone = crsDmgDone;
		this.crsmId = crsmId;
		this.crsmDmgDone = crsmDmgDone;
		this.userMonsterIdOne = userMonsterIdOne;
		this.userMonsterIdTwo = userMonsterIdTwo;
		this.userMonsterIdThree = userMonsterIdThree;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public ClanEventPersistentForUserPojo setUserId(String userId) {
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
	public ClanEventPersistentForUserPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "cr_id", precision = 10)
	@Override
	public Integer getCrId() {
		return this.crId;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrId(Integer crId) {
		this.crId = crId;
		return this;
	}

	@Column(name = "cr_dmg_done", precision = 10)
	@Override
	public Integer getCrDmgDone() {
		return this.crDmgDone;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrDmgDone(Integer crDmgDone) {
		this.crDmgDone = crDmgDone;
		return this;
	}

	@Column(name = "crs_id", precision = 10)
	@Override
	public Integer getCrsId() {
		return this.crsId;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrsId(Integer crsId) {
		this.crsId = crsId;
		return this;
	}

	@Column(name = "crs_dmg_done", precision = 10)
	@Override
	public Integer getCrsDmgDone() {
		return this.crsDmgDone;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrsDmgDone(Integer crsDmgDone) {
		this.crsDmgDone = crsDmgDone;
		return this;
	}

	@Column(name = "crsm_id", precision = 10)
	@Override
	public Integer getCrsmId() {
		return this.crsmId;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrsmId(Integer crsmId) {
		this.crsmId = crsmId;
		return this;
	}

	@Column(name = "crsm_dmg_done", precision = 10)
	@Override
	public Integer getCrsmDmgDone() {
		return this.crsmDmgDone;
	}

	@Override
	public ClanEventPersistentForUserPojo setCrsmDmgDone(Integer crsmDmgDone) {
		this.crsmDmgDone = crsmDmgDone;
		return this;
	}

	@Column(name = "user_monster_id_one", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdOne() {
		return this.userMonsterIdOne;
	}

	@Override
	public ClanEventPersistentForUserPojo setUserMonsterIdOne(String userMonsterIdOne) {
		this.userMonsterIdOne = userMonsterIdOne;
		return this;
	}

	@Column(name = "user_monster_id_two", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdTwo() {
		return this.userMonsterIdTwo;
	}

	@Override
	public ClanEventPersistentForUserPojo setUserMonsterIdTwo(String userMonsterIdTwo) {
		this.userMonsterIdTwo = userMonsterIdTwo;
		return this;
	}

	@Column(name = "user_monster_id_three", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdThree() {
		return this.userMonsterIdThree;
	}

	@Override
	public ClanEventPersistentForUserPojo setUserMonsterIdThree(String userMonsterIdThree) {
		this.userMonsterIdThree = userMonsterIdThree;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanEventPersistentForUser from) {
		setUserId(from.getUserId());
		setClanId(from.getClanId());
		setCrId(from.getCrId());
		setCrDmgDone(from.getCrDmgDone());
		setCrsId(from.getCrsId());
		setCrsDmgDone(from.getCrsDmgDone());
		setCrsmId(from.getCrsmId());
		setCrsmDmgDone(from.getCrsmDmgDone());
		setUserMonsterIdOne(from.getUserMonsterIdOne());
		setUserMonsterIdTwo(from.getUserMonsterIdTwo());
		setUserMonsterIdThree(from.getUserMonsterIdThree());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanEventPersistentForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanEventPersistentForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanEventPersistentForUserRecord();
		poop.from(this);
		return "ClanEventPersistentForUserPojo[" + poop.valuesRow() + "]";
	}
}