/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterHealingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterHealingForUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record7;
import org.jooq.Row;
import org.jooq.Row7;
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
@Table(name = "monster_healing_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "monster_for_user_id"})
})
public class MonsterHealingForUserRecord extends UpdatableRecordImpl<MonsterHealingForUserRecord> implements Record7<String, String, Timestamp, Double, Integer, Double, String>, IMonsterHealingForUser {

	private static final long serialVersionUID = -2045844854;

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.user_id</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.monster_for_user_id</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setMonsterForUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.monster_for_user_id</code>.
	 */
	@Column(name = "monster_for_user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getMonsterForUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.queued_time</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setQueuedTime(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.queued_time</code>.
	 */
	@Column(name = "queued_time")
	@Override
	public Timestamp getQueuedTime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.health_progress</code>. How much health this monster has recouped.
	 */
	@Override
	public MonsterHealingForUserRecord setHealthProgress(Double value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.health_progress</code>. How much health this monster has recouped.
	 */
	@Column(name = "health_progress", precision = 12)
	@Override
	public Double getHealthProgress() {
		return (Double) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.priority</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setPriority(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.priority</code>.
	 */
	@Column(name = "priority", precision = 10)
	@Override
	public Integer getPriority() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.elapsed_seconds</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setElapsedSeconds(Double value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.elapsed_seconds</code>.
	 */
	@Column(name = "elapsed_seconds", precision = 12)
	@Override
	public Double getElapsedSeconds() {
		return (Double) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.monster_healing_for_user.user_struct_hospital_id</code>.
	 */
	@Override
	public MonsterHealingForUserRecord setUserStructHospitalId(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_healing_for_user.user_struct_hospital_id</code>.
	 */
	@Column(name = "user_struct_hospital_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserStructHospitalId() {
		return (String) getValue(6);
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
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, String, Timestamp, Double, Integer, Double, String> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<String, String, Timestamp, Double, Integer, Double, String> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.MONSTER_FOR_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.QUEUED_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field4() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.HEALTH_PROGRESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field6() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.ELAPSED_SECONDS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return MonsterHealingForUser.MONSTER_HEALING_FOR_USER.USER_STRUCT_HOSPITAL_ID;
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
		return getMonsterForUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getQueuedTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value4() {
		return getHealthProgress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value6() {
		return getElapsedSeconds();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getUserStructHospitalId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value2(String value) {
		setMonsterForUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value3(Timestamp value) {
		setQueuedTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value4(Double value) {
		setHealthProgress(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value5(Integer value) {
		setPriority(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value6(Double value) {
		setElapsedSeconds(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord value7(String value) {
		setUserStructHospitalId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUserRecord values(String value1, String value2, Timestamp value3, Double value4, Integer value5, Double value6, String value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterHealingForUser from) {
		setUserId(from.getUserId());
		setMonsterForUserId(from.getMonsterForUserId());
		setQueuedTime(from.getQueuedTime());
		setHealthProgress(from.getHealthProgress());
		setPriority(from.getPriority());
		setElapsedSeconds(from.getElapsedSeconds());
		setUserStructHospitalId(from.getUserStructHospitalId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterHealingForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MonsterHealingForUserRecord
	 */
	public MonsterHealingForUserRecord() {
		super(MonsterHealingForUser.MONSTER_HEALING_FOR_USER);
	}

	/**
	 * Create a detached, initialised MonsterHealingForUserRecord
	 */
	public MonsterHealingForUserRecord(String userId, String monsterForUserId, Timestamp queuedTime, Double healthProgress, Integer priority, Double elapsedSeconds, String userStructHospitalId) {
		super(MonsterHealingForUser.MONSTER_HEALING_FOR_USER);

		setValue(0, userId);
		setValue(1, monsterForUserId);
		setValue(2, queuedTime);
		setValue(3, healthProgress);
		setValue(4, priority);
		setValue(5, elapsedSeconds);
		setValue(6, userStructHospitalId);
	}
}
