/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentForClan;

import java.sql.Timestamp;

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
@Table(name = "clan_event_persistent_for_clan", schema = "mobsters")
public class ClanEventPersistentForClanPojo implements IClanEventPersistentForClan {

	private static final long serialVersionUID = 1190337846;

	private String    clanId;
	private Integer   clanEventPersistentId;
	private Integer   crId;
	private Integer   crsId;
	private Timestamp stageStartTime;
	private Integer   crsmId;
	private Timestamp stageMonsterStartTime;

	public ClanEventPersistentForClanPojo() {}

	public ClanEventPersistentForClanPojo(ClanEventPersistentForClanPojo value) {
		this.clanId = value.clanId;
		this.clanEventPersistentId = value.clanEventPersistentId;
		this.crId = value.crId;
		this.crsId = value.crsId;
		this.stageStartTime = value.stageStartTime;
		this.crsmId = value.crsmId;
		this.stageMonsterStartTime = value.stageMonsterStartTime;
	}

	public ClanEventPersistentForClanPojo(
		String    clanId,
		Integer   clanEventPersistentId,
		Integer   crId,
		Integer   crsId,
		Timestamp stageStartTime,
		Integer   crsmId,
		Timestamp stageMonsterStartTime
	) {
		this.clanId = clanId;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crsId = crsId;
		this.stageStartTime = stageStartTime;
		this.crsmId = crsmId;
		this.stageMonsterStartTime = stageMonsterStartTime;
	}

	@Id
	@Column(name = "clan_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanEventPersistentForClanPojo setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "clan_event_persistent_id", precision = 10)
	@Override
	public Integer getClanEventPersistentId() {
		return this.clanEventPersistentId;
	}

	@Override
	public ClanEventPersistentForClanPojo setClanEventPersistentId(Integer clanEventPersistentId) {
		this.clanEventPersistentId = clanEventPersistentId;
		return this;
	}

	@Column(name = "cr_id", precision = 10)
	@Override
	public Integer getCrId() {
		return this.crId;
	}

	@Override
	public ClanEventPersistentForClanPojo setCrId(Integer crId) {
		this.crId = crId;
		return this;
	}

	@Column(name = "crs_id", precision = 10)
	@Override
	public Integer getCrsId() {
		return this.crsId;
	}

	@Override
	public ClanEventPersistentForClanPojo setCrsId(Integer crsId) {
		this.crsId = crsId;
		return this;
	}

	@Column(name = "stage_start_time")
	@Override
	public Timestamp getStageStartTime() {
		return this.stageStartTime;
	}

	@Override
	public ClanEventPersistentForClanPojo setStageStartTime(Timestamp stageStartTime) {
		this.stageStartTime = stageStartTime;
		return this;
	}

	@Column(name = "crsm_id", precision = 10)
	@Override
	public Integer getCrsmId() {
		return this.crsmId;
	}

	@Override
	public ClanEventPersistentForClanPojo setCrsmId(Integer crsmId) {
		this.crsmId = crsmId;
		return this;
	}

	@Column(name = "stage_monster_start_time")
	@Override
	public Timestamp getStageMonsterStartTime() {
		return this.stageMonsterStartTime;
	}

	@Override
	public ClanEventPersistentForClanPojo setStageMonsterStartTime(Timestamp stageMonsterStartTime) {
		this.stageMonsterStartTime = stageMonsterStartTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanEventPersistentForClan from) {
		setClanId(from.getClanId());
		setClanEventPersistentId(from.getClanEventPersistentId());
		setCrId(from.getCrId());
		setCrsId(from.getCrsId());
		setStageStartTime(from.getStageStartTime());
		setCrsmId(from.getCrsmId());
		setStageMonsterStartTime(from.getStageMonsterStartTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanEventPersistentForClan> E into(E into) {
		into.from(this);
		return into;
	}
}