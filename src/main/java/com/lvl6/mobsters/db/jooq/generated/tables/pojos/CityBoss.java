/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityBoss;

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
public class CityBoss implements ICityBoss {

	private static final long serialVersionUID = -1352255322;

	private Integer  id;
	private UInteger cityId;
	private Integer  monsterId;
	private UInteger expReward;
	private UInteger minCashDrop;
	private UInteger maxCashDrop;
	private Double   puzzlePieceDropRate;
	private UByte    level;

	public CityBoss() {}

	public CityBoss(CityBoss value) {
		this.id = value.id;
		this.cityId = value.cityId;
		this.monsterId = value.monsterId;
		this.expReward = value.expReward;
		this.minCashDrop = value.minCashDrop;
		this.maxCashDrop = value.maxCashDrop;
		this.puzzlePieceDropRate = value.puzzlePieceDropRate;
		this.level = value.level;
	}

	public CityBoss(
		Integer  id,
		UInteger cityId,
		Integer  monsterId,
		UInteger expReward,
		UInteger minCashDrop,
		UInteger maxCashDrop,
		Double   puzzlePieceDropRate,
		UByte    level
	) {
		this.id = id;
		this.cityId = cityId;
		this.monsterId = monsterId;
		this.expReward = expReward;
		this.minCashDrop = minCashDrop;
		this.maxCashDrop = maxCashDrop;
		this.puzzlePieceDropRate = puzzlePieceDropRate;
		this.level = level;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public CityBoss setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "city_id", precision = 7)
	@Override
	public UInteger getCityId() {
		return this.cityId;
	}

	@Override
	public CityBoss setCityId(UInteger cityId) {
		this.cityId = cityId;
		return this;
	}

	@Column(name = "monster_id", precision = 10)
	@Override
	public Integer getMonsterId() {
		return this.monsterId;
	}

	@Override
	public CityBoss setMonsterId(Integer monsterId) {
		this.monsterId = monsterId;
		return this;
	}

	@Column(name = "exp_reward", precision = 10)
	@Override
	public UInteger getExpReward() {
		return this.expReward;
	}

	@Override
	public CityBoss setExpReward(UInteger expReward) {
		this.expReward = expReward;
		return this;
	}

	@Column(name = "min_cash_drop", precision = 7)
	@Override
	public UInteger getMinCashDrop() {
		return this.minCashDrop;
	}

	@Override
	public CityBoss setMinCashDrop(UInteger minCashDrop) {
		this.minCashDrop = minCashDrop;
		return this;
	}

	@Column(name = "max_cash_drop", precision = 10)
	@Override
	public UInteger getMaxCashDrop() {
		return this.maxCashDrop;
	}

	@Override
	public CityBoss setMaxCashDrop(UInteger maxCashDrop) {
		this.maxCashDrop = maxCashDrop;
		return this;
	}

	@Column(name = "puzzle_piece_drop_rate", precision = 12)
	@Override
	public Double getPuzzlePieceDropRate() {
		return this.puzzlePieceDropRate;
	}

	@Override
	public CityBoss setPuzzlePieceDropRate(Double puzzlePieceDropRate) {
		this.puzzlePieceDropRate = puzzlePieceDropRate;
		return this;
	}

	@Column(name = "level", precision = 3)
	@Override
	public UByte getLevel() {
		return this.level;
	}

	@Override
	public CityBoss setLevel(UByte level) {
		this.level = level;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICityBoss from) {
		setId(from.getId());
		setCityId(from.getCityId());
		setMonsterId(from.getMonsterId());
		setExpReward(from.getExpReward());
		setMinCashDrop(from.getMinCashDrop());
		setMaxCashDrop(from.getMaxCashDrop());
		setPuzzlePieceDropRate(from.getPuzzlePieceDropRate());
		setLevel(from.getLevel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICityBoss> E into(E into) {
		into.from(this);
		return into;
	}
}
