/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICepfuRaidStageHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "cepfu_raid_stage_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "crs_start_time", "clan_id"})
})
public class CepfuRaidStageHistory implements ICepfuRaidStageHistory {

	private static final long serialVersionUID = -1254805270;

	private String    userId;
	private Timestamp crsStartTime;
	private String    clanId;
	private Integer   clanEventPersistentId;
	private Integer   crId;
	private Integer   crsId;
	private Integer   crsDmgDone;
	private Integer   stageHealth;
	private Timestamp crsEndTime;

	public CepfuRaidStageHistory() {}

	public CepfuRaidStageHistory(CepfuRaidStageHistory value) {
		this.userId = value.userId;
		this.crsStartTime = value.crsStartTime;
		this.clanId = value.clanId;
		this.clanEventPersistentId = value.clanEventPersistentId;
		this.crId = value.crId;
		this.crsId = value.crsId;
		this.crsDmgDone = value.crsDmgDone;
		this.stageHealth = value.stageHealth;
		this.crsEndTime = value.crsEndTime;
	}

	public CepfuRaidStageHistory(
		String    userId,
		Timestamp crsStartTime,
		String    clanId,
		Integer   clanEventPersistentId,
		Integer   crId,
		Integer   crsId,
		Integer   crsDmgDone,
		Integer   stageHealth,
		Timestamp crsEndTime
	) {
		this.userId = userId;
		this.crsStartTime = crsStartTime;
		this.clanId = clanId;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crsId = crsId;
		this.crsDmgDone = crsDmgDone;
		this.stageHealth = stageHealth;
		this.crsEndTime = crsEndTime;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public CepfuRaidStageHistory setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "crs_start_time", nullable = false)
	@NotNull
	@Override
	public Timestamp getCrsStartTime() {
		return this.crsStartTime;
	}

	@Override
	public CepfuRaidStageHistory setCrsStartTime(Timestamp crsStartTime) {
		this.crsStartTime = crsStartTime;
		return this;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public CepfuRaidStageHistory setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "clan_event_persistent_id", precision = 10)
	@Override
	public Integer getClanEventPersistentId() {
		return this.clanEventPersistentId;
	}

	@Override
	public CepfuRaidStageHistory setClanEventPersistentId(Integer clanEventPersistentId) {
		this.clanEventPersistentId = clanEventPersistentId;
		return this;
	}

	@Column(name = "cr_id", precision = 10)
	@Override
	public Integer getCrId() {
		return this.crId;
	}

	@Override
	public CepfuRaidStageHistory setCrId(Integer crId) {
		this.crId = crId;
		return this;
	}

	@Column(name = "crs_id", precision = 10)
	@Override
	public Integer getCrsId() {
		return this.crsId;
	}

	@Override
	public CepfuRaidStageHistory setCrsId(Integer crsId) {
		this.crsId = crsId;
		return this;
	}

	@Column(name = "crs_dmg_done", precision = 10)
	@Override
	public Integer getCrsDmgDone() {
		return this.crsDmgDone;
	}

	@Override
	public CepfuRaidStageHistory setCrsDmgDone(Integer crsDmgDone) {
		this.crsDmgDone = crsDmgDone;
		return this;
	}

	@Column(name = "stage_health", precision = 10)
	@Override
	public Integer getStageHealth() {
		return this.stageHealth;
	}

	@Override
	public CepfuRaidStageHistory setStageHealth(Integer stageHealth) {
		this.stageHealth = stageHealth;
		return this;
	}

	@Column(name = "crs_end_time")
	@Override
	public Timestamp getCrsEndTime() {
		return this.crsEndTime;
	}

	@Override
	public CepfuRaidStageHistory setCrsEndTime(Timestamp crsEndTime) {
		this.crsEndTime = crsEndTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICepfuRaidStageHistory from) {
		setUserId(from.getUserId());
		setCrsStartTime(from.getCrsStartTime());
		setClanId(from.getClanId());
		setClanEventPersistentId(from.getClanEventPersistentId());
		setCrId(from.getCrId());
		setCrsId(from.getCrsId());
		setCrsDmgDone(from.getCrsDmgDone());
		setStageHealth(from.getStageHealth());
		setCrsEndTime(from.getCrsEndTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICepfuRaidStageHistory> E into(E into) {
		into.from(this);
		return into;
	}
}
