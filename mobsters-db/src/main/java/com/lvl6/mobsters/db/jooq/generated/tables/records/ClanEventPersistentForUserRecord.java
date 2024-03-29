/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record11;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ClanEventPersistentForUserRecord extends UpdatableRecordImpl<ClanEventPersistentForUserRecord> implements Record11<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String, String, String>, IClanEventPersistentForUser {

	private static final long serialVersionUID = -1983695732;

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.user_id</code>.
	 */
	@Override
	public ClanEventPersistentForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.clan_id</code>.
	 */
	@Override
	public ClanEventPersistentForUserRecord setClanId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.cr_id</code>. clan raid id
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrId(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.cr_id</code>. clan raid id
	 */
	@Column(name = "cr_id", precision = 10)
	@Override
	public Integer getCrId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.cr_dmg_done</code>. aggregate damage done in clan raid
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrDmgDone(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.cr_dmg_done</code>. aggregate damage done in clan raid
	 */
	@Column(name = "cr_dmg_done", precision = 10)
	@Override
	public Integer getCrDmgDone() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.crs_id</code>. clan raid stage id
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrsId(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.crs_id</code>. clan raid stage id
	 */
	@Column(name = "crs_id", precision = 10)
	@Override
	public Integer getCrsId() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.crs_dmg_done</code>. aggregate damage done in clan raid stage
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrsDmgDone(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.crs_dmg_done</code>. aggregate damage done in clan raid stage
	 */
	@Column(name = "crs_dmg_done", precision = 10)
	@Override
	public Integer getCrsDmgDone() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.crsm_id</code>. primary key in clan_raid_stage_monster
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrsmId(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.crsm_id</code>. primary key in clan_raid_stage_monster
	 */
	@Column(name = "crsm_id", precision = 10)
	@Override
	public Integer getCrsmId() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.crsm_dmg_done</code>. aggregate damage done against clan raid stage monster
	 */
	@Override
	public ClanEventPersistentForUserRecord setCrsmDmgDone(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.crsm_dmg_done</code>. aggregate damage done against clan raid stage monster
	 */
	@Column(name = "crsm_dmg_done", precision = 10)
	@Override
	public Integer getCrsmDmgDone() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_one</code>.
	 */
	@Override
	public ClanEventPersistentForUserRecord setUserMonsterIdOne(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_one</code>.
	 */
	@Column(name = "user_monster_id_one", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdOne() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_two</code>.
	 */
	@Override
	public ClanEventPersistentForUserRecord setUserMonsterIdTwo(String value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_two</code>.
	 */
	@Column(name = "user_monster_id_two", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdTwo() {
		return (String) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_three</code>.
	 */
	@Override
	public ClanEventPersistentForUserRecord setUserMonsterIdThree(String value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_event_persistent_for_user.user_monster_id_three</code>.
	 */
	@Column(name = "user_monster_id_three", length = 36)
	@Size(max = 36)
	@Override
	public String getUserMonsterIdThree() {
		return (String) getValue(10);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<String, String> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record11 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String, String, String> fieldsRow() {
		return (Row11) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<String, String, Integer, Integer, Integer, Integer, Integer, Integer, String, String, String> valuesRow() {
		return (Row11) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CR_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CR_DMG_DONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRS_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRS_DMG_DONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRSM_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.CRSM_DMG_DONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_ONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_TWO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field11() {
		return ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER.USER_MONSTER_ID_THREE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getClanId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getCrId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getCrDmgDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getCrsId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getCrsDmgDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getCrsmId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getCrsmDmgDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getUserMonsterIdOne();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getUserMonsterIdTwo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value11() {
		return getUserMonsterIdThree();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value2(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value3(Integer value) {
		setCrId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value4(Integer value) {
		setCrDmgDone(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value5(Integer value) {
		setCrsId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value6(Integer value) {
		setCrsDmgDone(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value7(Integer value) {
		setCrsmId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value8(Integer value) {
		setCrsmDmgDone(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value9(String value) {
		setUserMonsterIdOne(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value10(String value) {
		setUserMonsterIdTwo(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord value11(String value) {
		setUserMonsterIdThree(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanEventPersistentForUserRecord values(String value1, String value2, Integer value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8, String value9, String value10, String value11) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		value11(value11);
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanEventPersistentForUserRecord
	 */
	public ClanEventPersistentForUserRecord() {
		super(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER);
	}

	/**
	 * Create a detached, initialised ClanEventPersistentForUserRecord
	 */
	public ClanEventPersistentForUserRecord(String userId, String clanId, Integer crId, Integer crDmgDone, Integer crsId, Integer crsDmgDone, Integer crsmId, Integer crsmDmgDone, String userMonsterIdOne, String userMonsterIdTwo, String userMonsterIdThree) {
		super(ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER);

		setValue(0, userId);
		setValue(1, clanId);
		setValue(2, crId);
		setValue(3, crDmgDone);
		setValue(4, crsId);
		setValue(5, crsDmgDone);
		setValue(6, crsmId);
		setValue(7, crsmDmgDone);
		setValue(8, userMonsterIdOne);
		setValue(9, userMonsterIdTwo);
		setValue(10, userMonsterIdThree);
	}
}
