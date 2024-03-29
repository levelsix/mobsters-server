/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanBoss;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanBoss;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
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
@Table(name = "clan_boss", schema = "mobsters")
public class ClanBossRecord extends UpdatableRecordImpl<ClanBossRecord> implements Record11<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>, IClanBoss {

	private static final long serialVersionUID = -1608671730;

	/**
	 * Setter for <code>mobsters.clan_boss.id</code>.
	 */
	@Override
	public ClanBossRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.name</code>.
	 */
	@Override
	public ClanBossRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.name</code>.
	 */
	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.hp</code>.
	 */
	@Override
	public ClanBossRecord setHp(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.hp</code>.
	 */
	@Column(name = "hp", precision = 10)
	@Override
	public Integer getHp() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.energy_cost</code>.
	 */
	@Override
	public ClanBossRecord setEnergyCost(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.energy_cost</code>.
	 */
	@Column(name = "energy_cost", precision = 10)
	@Override
	public Integer getEnergyCost() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.minutes_to_kill</code>.
	 */
	@Override
	public ClanBossRecord setMinutesToKill(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.minutes_to_kill</code>.
	 */
	@Column(name = "minutes_to_kill", precision = 10)
	@Override
	public Integer getMinutesToKill() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.minutes_to_respawn</code>.
	 */
	@Override
	public ClanBossRecord setMinutesToRespawn(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.minutes_to_respawn</code>.
	 */
	@Column(name = "minutes_to_respawn", precision = 10)
	@Override
	public Integer getMinutesToRespawn() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.num_runes_one</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Override
	public ClanBossRecord setNumRunesOne(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.num_runes_one</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Column(name = "num_runes_one", precision = 10)
	@Override
	public Integer getNumRunesOne() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.num_runes_two</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Override
	public ClanBossRecord setNumRunesTwo(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.num_runes_two</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Column(name = "num_runes_two", precision = 10)
	@Override
	public Integer getNumRunesTwo() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.num_runes_three</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Override
	public ClanBossRecord setNumRunesThree(Integer value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.num_runes_three</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Column(name = "num_runes_three", precision = 10)
	@Override
	public Integer getNumRunesThree() {
		return (Integer) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.num_runes_four</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Override
	public ClanBossRecord setNumRunesFour(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.num_runes_four</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Column(name = "num_runes_four", precision = 10)
	@Override
	public Integer getNumRunesFour() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.clan_boss.num_runes_five</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Override
	public ClanBossRecord setNumRunesFive(Integer value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_boss.num_runes_five</code>. Number of runes of a certain type that is used up to summon this boss.
	 */
	@Column(name = "num_runes_five", precision = 10)
	@Override
	public Integer getNumRunesFive() {
		return (Integer) getValue(10);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record11 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> fieldsRow() {
		return (Row11) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> valuesRow() {
		return (Row11) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return ClanBoss.CLAN_BOSS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanBoss.CLAN_BOSS.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return ClanBoss.CLAN_BOSS.HP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return ClanBoss.CLAN_BOSS.ENERGY_COST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return ClanBoss.CLAN_BOSS.MINUTES_TO_KILL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return ClanBoss.CLAN_BOSS.MINUTES_TO_RESPAWN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return ClanBoss.CLAN_BOSS.NUM_RUNES_ONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return ClanBoss.CLAN_BOSS.NUM_RUNES_TWO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field9() {
		return ClanBoss.CLAN_BOSS.NUM_RUNES_THREE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field10() {
		return ClanBoss.CLAN_BOSS.NUM_RUNES_FOUR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field11() {
		return ClanBoss.CLAN_BOSS.NUM_RUNES_FIVE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getHp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getEnergyCost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getMinutesToKill();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getMinutesToRespawn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getNumRunesOne();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getNumRunesTwo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value9() {
		return getNumRunesThree();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value10() {
		return getNumRunesFour();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value11() {
		return getNumRunesFive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value3(Integer value) {
		setHp(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value4(Integer value) {
		setEnergyCost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value5(Integer value) {
		setMinutesToKill(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value6(Integer value) {
		setMinutesToRespawn(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value7(Integer value) {
		setNumRunesOne(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value8(Integer value) {
		setNumRunesTwo(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value9(Integer value) {
		setNumRunesThree(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value10(Integer value) {
		setNumRunesFour(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord value11(Integer value) {
		setNumRunesFive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanBossRecord values(Integer value1, String value2, Integer value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8, Integer value9, Integer value10, Integer value11) {
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
	public void from(IClanBoss from) {
		setId(from.getId());
		setName(from.getName());
		setHp(from.getHp());
		setEnergyCost(from.getEnergyCost());
		setMinutesToKill(from.getMinutesToKill());
		setMinutesToRespawn(from.getMinutesToRespawn());
		setNumRunesOne(from.getNumRunesOne());
		setNumRunesTwo(from.getNumRunesTwo());
		setNumRunesThree(from.getNumRunesThree());
		setNumRunesFour(from.getNumRunesFour());
		setNumRunesFive(from.getNumRunesFive());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanBoss> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanBossRecord
	 */
	public ClanBossRecord() {
		super(ClanBoss.CLAN_BOSS);
	}

	/**
	 * Create a detached, initialised ClanBossRecord
	 */
	public ClanBossRecord(Integer id, String name, Integer hp, Integer energyCost, Integer minutesToKill, Integer minutesToRespawn, Integer numRunesOne, Integer numRunesTwo, Integer numRunesThree, Integer numRunesFour, Integer numRunesFive) {
		super(ClanBoss.CLAN_BOSS);

		setValue(0, id);
		setValue(1, name);
		setValue(2, hp);
		setValue(3, energyCost);
		setValue(4, minutesToKill);
		setValue(5, minutesToRespawn);
		setValue(6, numRunesOne);
		setValue(7, numRunesTwo);
		setValue(8, numRunesThree);
		setValue(9, numRunesFour);
		setValue(10, numRunesFive);
	}
}
