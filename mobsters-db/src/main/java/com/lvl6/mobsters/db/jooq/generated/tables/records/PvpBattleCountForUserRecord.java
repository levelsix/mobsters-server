/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IPvpBattleCountForUser;

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
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
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
@Table(name = "pvp_battle_count_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"attacker_id", "defender_id", "date"})
})
public class PvpBattleCountForUserRecord extends UpdatableRecordImpl<PvpBattleCountForUserRecord> implements Record4<String, String, Timestamp, Integer>, IPvpBattleCountForUser {

	private static final long serialVersionUID = -1116462770;

	/**
	 * Setter for <code>mobsters.pvp_battle_count_for_user.attacker_id</code>.
	 */
	@Override
	public PvpBattleCountForUserRecord setAttackerId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_count_for_user.attacker_id</code>.
	 */
	@Column(name = "attacker_id", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getAttackerId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_count_for_user.defender_id</code>.
	 */
	@Override
	public PvpBattleCountForUserRecord setDefenderId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_count_for_user.defender_id</code>.
	 */
	@Column(name = "defender_id", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getDefenderId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_count_for_user.date</code>.
	 */
	@Override
	public PvpBattleCountForUserRecord setDate(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_count_for_user.date</code>.
	 */
	@Column(name = "date", nullable = false)
	@NotNull
	@Override
	public Timestamp getDate() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.pvp_battle_count_for_user.count</code>.
	 */
	@Override
	public PvpBattleCountForUserRecord setCount(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_battle_count_for_user.count</code>.
	 */
	@Column(name = "count", precision = 10)
	@Override
	public Integer getCount() {
		return (Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, String, Timestamp> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<String, String, Timestamp, Integer> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<String, String, Timestamp, Integer> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.ATTACKER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.DEFENDER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER.COUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getAttackerId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getDefenderId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBattleCountForUserRecord value1(String value) {
		setAttackerId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBattleCountForUserRecord value2(String value) {
		setDefenderId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBattleCountForUserRecord value3(Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBattleCountForUserRecord value4(Integer value) {
		setCount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBattleCountForUserRecord values(String value1, String value2, Timestamp value3, Integer value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IPvpBattleCountForUser from) {
		setAttackerId(from.getAttackerId());
		setDefenderId(from.getDefenderId());
		setDate(from.getDate());
		setCount(from.getCount());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IPvpBattleCountForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PvpBattleCountForUserRecord
	 */
	public PvpBattleCountForUserRecord() {
		super(PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER);
	}

	/**
	 * Create a detached, initialised PvpBattleCountForUserRecord
	 */
	public PvpBattleCountForUserRecord(String attackerId, String defenderId, Timestamp date, Integer count) {
		super(PvpBattleCountForUser.PVP_BATTLE_COUNT_FOR_USER);

		setValue(0, attackerId);
		setValue(1, defenderId);
		setValue(2, date);
		setValue(3, count);
	}
}
