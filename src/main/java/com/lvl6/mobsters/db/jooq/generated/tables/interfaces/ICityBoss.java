/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
@Table(name = "city_boss", schema = "mobsters")
public interface ICityBoss extends Serializable {

	/**
	 * Setter for <code>mobsters.city_boss.id</code>.
	 */
	public ICityBoss setId(Integer value);

	/**
	 * Getter for <code>mobsters.city_boss.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.city_boss.city_id</code>.
	 */
	public ICityBoss setCityId(UInteger value);

	/**
	 * Getter for <code>mobsters.city_boss.city_id</code>.
	 */
	@Column(name = "city_id", precision = 7)
	public UInteger getCityId();

	/**
	 * Setter for <code>mobsters.city_boss.monster_id</code>.
	 */
	public ICityBoss setMonsterId(Integer value);

	/**
	 * Getter for <code>mobsters.city_boss.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 10)
	public Integer getMonsterId();

	/**
	 * Setter for <code>mobsters.city_boss.exp_reward</code>. Not really needed, but
	 */
	public ICityBoss setExpReward(UInteger value);

	/**
	 * Getter for <code>mobsters.city_boss.exp_reward</code>. Not really needed, but
	 */
	@Column(name = "exp_reward", precision = 10)
	public UInteger getExpReward();

	/**
	 * Setter for <code>mobsters.city_boss.min_cash_drop</code>.
	 */
	public ICityBoss setMinCashDrop(UInteger value);

	/**
	 * Getter for <code>mobsters.city_boss.min_cash_drop</code>.
	 */
	@Column(name = "min_cash_drop", precision = 7)
	public UInteger getMinCashDrop();

	/**
	 * Setter for <code>mobsters.city_boss.max_cash_drop</code>.
	 */
	public ICityBoss setMaxCashDrop(UInteger value);

	/**
	 * Getter for <code>mobsters.city_boss.max_cash_drop</code>.
	 */
	@Column(name = "max_cash_drop", precision = 10)
	public UInteger getMaxCashDrop();

	/**
	 * Setter for <code>mobsters.city_boss.puzzle_piece_drop_rate</code>.
	 */
	public ICityBoss setPuzzlePieceDropRate(Double value);

	/**
	 * Getter for <code>mobsters.city_boss.puzzle_piece_drop_rate</code>.
	 */
	@Column(name = "puzzle_piece_drop_rate", precision = 12)
	public Double getPuzzlePieceDropRate();

	/**
	 * Setter for <code>mobsters.city_boss.level</code>.
	 */
	public ICityBoss setLevel(UByte value);

	/**
	 * Getter for <code>mobsters.city_boss.level</code>.
	 */
	@Column(name = "level", precision = 3)
	public UByte getLevel();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface ICityBoss
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityBoss from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface ICityBoss
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityBoss> E into(E into);
}
