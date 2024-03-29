/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEvolvingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterEvolvingForUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Row;
import org.jooq.Row5;
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
@Table(name = "monster_evolving_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"catalyst_user_monster_id", "user_monster_id_one", "user_monster_id_two"})
})
public class MonsterEvolvingForUserRecord extends UpdatableRecordImpl<MonsterEvolvingForUserRecord> implements Record5<String, String, String, String, Timestamp>, IMonsterEvolvingForUser {

	private static final long serialVersionUID = 1615749018;

	/**
	 * Setter for <code>mobsters.monster_evolving_for_user.catalyst_user_monster_id</code>.
	 */
	@Override
	public MonsterEvolvingForUserRecord setCatalystUserMonsterId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_evolving_for_user.catalyst_user_monster_id</code>.
	 */
	@Column(name = "catalyst_user_monster_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getCatalystUserMonsterId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.monster_evolving_for_user.user_monster_id_one</code>.
	 */
	@Override
	public MonsterEvolvingForUserRecord setUserMonsterIdOne(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_evolving_for_user.user_monster_id_one</code>.
	 */
	@Column(name = "user_monster_id_one", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserMonsterIdOne() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.monster_evolving_for_user.user_monster_id_two</code>.
	 */
	@Override
	public MonsterEvolvingForUserRecord setUserMonsterIdTwo(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_evolving_for_user.user_monster_id_two</code>.
	 */
	@Column(name = "user_monster_id_two", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserMonsterIdTwo() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.monster_evolving_for_user.user_id</code>.
	 */
	@Override
	public MonsterEvolvingForUserRecord setUserId(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_evolving_for_user.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.monster_evolving_for_user.start_time</code>.
	 */
	@Override
	public MonsterEvolvingForUserRecord setStartTime(Timestamp value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_evolving_for_user.start_time</code>.
	 */
	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return (Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, String, String> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, String, String, Timestamp> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, String, String, String, Timestamp> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.CATALYST_USER_MONSTER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_MONSTER_ID_ONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_MONSTER_ID_TWO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field5() {
		return MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER.START_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getCatalystUserMonsterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getUserMonsterIdOne();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getUserMonsterIdTwo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value5() {
		return getStartTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord value1(String value) {
		setCatalystUserMonsterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord value2(String value) {
		setUserMonsterIdOne(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord value3(String value) {
		setUserMonsterIdTwo(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord value4(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord value5(Timestamp value) {
		setStartTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterEvolvingForUserRecord values(String value1, String value2, String value3, String value4, Timestamp value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterEvolvingForUser from) {
		setCatalystUserMonsterId(from.getCatalystUserMonsterId());
		setUserMonsterIdOne(from.getUserMonsterIdOne());
		setUserMonsterIdTwo(from.getUserMonsterIdTwo());
		setUserId(from.getUserId());
		setStartTime(from.getStartTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterEvolvingForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MonsterEvolvingForUserRecord
	 */
	public MonsterEvolvingForUserRecord() {
		super(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER);
	}

	/**
	 * Create a detached, initialised MonsterEvolvingForUserRecord
	 */
	public MonsterEvolvingForUserRecord(String catalystUserMonsterId, String userMonsterIdOne, String userMonsterIdTwo, String userId, Timestamp startTime) {
		super(MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER);

		setValue(0, catalystUserMonsterId);
		setValue(1, userMonsterIdOne);
		setValue(2, userMonsterIdTwo);
		setValue(3, userId);
		setValue(4, startTime);
	}
}
