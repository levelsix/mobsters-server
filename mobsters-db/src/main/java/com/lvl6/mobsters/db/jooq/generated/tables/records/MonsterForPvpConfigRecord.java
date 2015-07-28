/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterForPvpConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForPvpConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row;
import org.jooq.Row8;
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
@Table(name = "monster_for_pvp_config", schema = "mobsters")
public class MonsterForPvpConfigRecord extends UpdatableRecordImpl<MonsterForPvpConfigRecord> implements Record8<Integer, Integer, Byte, Integer, Integer, Integer, Integer, Integer>, IMonsterForPvpConfig {

	private static final long serialVersionUID = 1814789791;

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.id</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.monster_id</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMonsterId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 10)
	@Override
	public Integer getMonsterId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.monster_lvl</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMonsterLvl(Byte value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.monster_lvl</code>.
	 */
	@Column(name = "monster_lvl", precision = 3)
	@Override
	public Byte getMonsterLvl() {
		return (Byte) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.elo</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setElo(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.elo</code>.
	 */
	@Column(name = "elo", precision = 10)
	@Override
	public Integer getElo() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.min_cash_reward</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMinCashReward(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.min_cash_reward</code>.
	 */
	@Column(name = "min_cash_reward", precision = 10)
	@Override
	public Integer getMinCashReward() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.max_cash_reward</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMaxCashReward(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.max_cash_reward</code>.
	 */
	@Column(name = "max_cash_reward", precision = 10)
	@Override
	public Integer getMaxCashReward() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.min_oil_reward</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMinOilReward(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.min_oil_reward</code>.
	 */
	@Column(name = "min_oil_reward", precision = 10)
	@Override
	public Integer getMinOilReward() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.monster_for_pvp_config.max_oil_reward</code>.
	 */
	@Override
	public MonsterForPvpConfigRecord setMaxOilReward(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.monster_for_pvp_config.max_oil_reward</code>.
	 */
	@Column(name = "max_oil_reward", precision = 10)
	@Override
	public Integer getMaxOilReward() {
		return (Integer) getValue(7);
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
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, Byte, Integer, Integer, Integer, Integer, Integer> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, Byte, Integer, Integer, Integer, Integer, Integer> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MONSTER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field3() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MONSTER_LVL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.ELO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MIN_CASH_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MAX_CASH_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MIN_OIL_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field8() {
		return MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG.MAX_OIL_REWARD;
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
	public Integer value2() {
		return getMonsterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value3() {
		return getMonsterLvl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getElo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getMinCashReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getMaxCashReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getMinOilReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value8() {
		return getMaxOilReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value2(Integer value) {
		setMonsterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value3(Byte value) {
		setMonsterLvl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value4(Integer value) {
		setElo(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value5(Integer value) {
		setMinCashReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value6(Integer value) {
		setMaxCashReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value7(Integer value) {
		setMinOilReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord value8(Integer value) {
		setMaxOilReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForPvpConfigRecord values(Integer value1, Integer value2, Byte value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMonsterForPvpConfig from) {
		setId(from.getId());
		setMonsterId(from.getMonsterId());
		setMonsterLvl(from.getMonsterLvl());
		setElo(from.getElo());
		setMinCashReward(from.getMinCashReward());
		setMaxCashReward(from.getMaxCashReward());
		setMinOilReward(from.getMinOilReward());
		setMaxOilReward(from.getMaxOilReward());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMonsterForPvpConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MonsterForPvpConfigRecord
	 */
	public MonsterForPvpConfigRecord() {
		super(MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG);
	}

	/**
	 * Create a detached, initialised MonsterForPvpConfigRecord
	 */
	public MonsterForPvpConfigRecord(Integer id, Integer monsterId, Byte monsterLvl, Integer elo, Integer minCashReward, Integer maxCashReward, Integer minOilReward, Integer maxOilReward) {
		super(MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG);

		setValue(0, id);
		setValue(1, monsterId);
		setValue(2, monsterLvl);
		setValue(3, elo);
		setValue(4, minCashReward);
		setValue(5, maxCashReward);
		setValue(6, minOilReward);
		setValue(7, maxOilReward);
	}
}