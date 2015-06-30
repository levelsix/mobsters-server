/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanBossReward;

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
@Table(name = "clan_boss_reward", schema = "mobsters")
public class ClanBossRewardPojo implements IClanBossReward {

	private static final long serialVersionUID = -1535891079;

	private String  id;
	private Integer clanBossId;
	private Integer equipId;

	public ClanBossRewardPojo() {}

	public ClanBossRewardPojo(ClanBossRewardPojo value) {
		this.id = value.id;
		this.clanBossId = value.clanBossId;
		this.equipId = value.equipId;
	}

	public ClanBossRewardPojo(
		String  id,
		Integer clanBossId,
		Integer equipId
	) {
		this.id = id;
		this.clanBossId = clanBossId;
		this.equipId = equipId;
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
	public ClanBossRewardPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "clan_boss_id", precision = 10)
	@Override
	public Integer getClanBossId() {
		return this.clanBossId;
	}

	@Override
	public ClanBossRewardPojo setClanBossId(Integer clanBossId) {
		this.clanBossId = clanBossId;
		return this;
	}

	@Column(name = "equip_id", precision = 10)
	@Override
	public Integer getEquipId() {
		return this.equipId;
	}

	@Override
	public ClanBossRewardPojo setEquipId(Integer equipId) {
		this.equipId = equipId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanBossReward from) {
		setId(from.getId());
		setClanBossId(from.getClanBossId());
		setEquipId(from.getEquipId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanBossReward> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ClanBossRewardRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ClanBossRewardRecord();
		poop.from(this);
		return "ClanBossRewardPojo[" + poop.valuesRow() + "]";
	}
}
