/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentForClanHistory;

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
@Table(name = "clan_event_persistent_for_clan_history", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"clan_id", "time_of_entry"})
})
public class ClanEventPersistentForClanHistory implements IClanEventPersistentForClanHistory {

	private static final long serialVersionUID = 1386566063;

	private String    clanId;
	private Timestamp timeOfEntry;
	private Integer   clanEventPersistentId;
	private Integer   crId;
	private Integer   crsId;
	private Timestamp stageStartTime;
	private Integer   crsmId;
	private Timestamp stageMonsterStartTime;
	private Boolean   won;

	public ClanEventPersistentForClanHistory() {}

	public ClanEventPersistentForClanHistory(ClanEventPersistentForClanHistory value) {
		this.clanId = value.clanId;
		this.timeOfEntry = value.timeOfEntry;
		this.clanEventPersistentId = value.clanEventPersistentId;
		this.crId = value.crId;
		this.crsId = value.crsId;
		this.stageStartTime = value.stageStartTime;
		this.crsmId = value.crsmId;
		this.stageMonsterStartTime = value.stageMonsterStartTime;
		this.won = value.won;
	}

	public ClanEventPersistentForClanHistory(
		String    clanId,
		Timestamp timeOfEntry,
		Integer   clanEventPersistentId,
		Integer   crId,
		Integer   crsId,
		Timestamp stageStartTime,
		Integer   crsmId,
		Timestamp stageMonsterStartTime,
		Boolean   won
	) {
		this.clanId = clanId;
		this.timeOfEntry = timeOfEntry;
		this.clanEventPersistentId = clanEventPersistentId;
		this.crId = crId;
		this.crsId = crsId;
		this.stageStartTime = stageStartTime;
		this.crsmId = crsmId;
		this.stageMonsterStartTime = stageMonsterStartTime;
		this.won = won;
	}

	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public ClanEventPersistentForClanHistory setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "time_of_entry", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public ClanEventPersistentForClanHistory setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
		return this;
	}

	@Column(name = "clan_event_persistent_id", precision = 10)
	@Override
	public Integer getClanEventPersistentId() {
		return this.clanEventPersistentId;
	}

	@Override
	public ClanEventPersistentForClanHistory setClanEventPersistentId(Integer clanEventPersistentId) {
		this.clanEventPersistentId = clanEventPersistentId;
		return this;
	}

	@Column(name = "cr_id", precision = 10)
	@Override
	public Integer getCrId() {
		return this.crId;
	}

	@Override
	public ClanEventPersistentForClanHistory setCrId(Integer crId) {
		this.crId = crId;
		return this;
	}

	@Column(name = "crs_id", precision = 10)
	@Override
	public Integer getCrsId() {
		return this.crsId;
	}

	@Override
	public ClanEventPersistentForClanHistory setCrsId(Integer crsId) {
		this.crsId = crsId;
		return this;
	}

	@Column(name = "stage_start_time")
	@Override
	public Timestamp getStageStartTime() {
		return this.stageStartTime;
	}

	@Override
	public ClanEventPersistentForClanHistory setStageStartTime(Timestamp stageStartTime) {
		this.stageStartTime = stageStartTime;
		return this;
	}

	@Column(name = "crsm_id", precision = 10)
	@Override
	public Integer getCrsmId() {
		return this.crsmId;
	}

	@Override
	public ClanEventPersistentForClanHistory setCrsmId(Integer crsmId) {
		this.crsmId = crsmId;
		return this;
	}

	@Column(name = "stage_monster_start_time")
	@Override
	public Timestamp getStageMonsterStartTime() {
		return this.stageMonsterStartTime;
	}

	@Override
	public ClanEventPersistentForClanHistory setStageMonsterStartTime(Timestamp stageMonsterStartTime) {
		this.stageMonsterStartTime = stageMonsterStartTime;
		return this;
	}

	@Column(name = "won", precision = 1)
	@Override
	public Boolean getWon() {
		return this.won;
	}

	@Override
	public ClanEventPersistentForClanHistory setWon(Boolean won) {
		this.won = won;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanEventPersistentForClanHistory from) {
		setClanId(from.getClanId());
		setTimeOfEntry(from.getTimeOfEntry());
		setClanEventPersistentId(from.getClanEventPersistentId());
		setCrId(from.getCrId());
		setCrsId(from.getCrsId());
		setStageStartTime(from.getStageStartTime());
		setCrsmId(from.getCrsmId());
		setStageMonsterStartTime(from.getStageMonsterStartTime());
		setWon(from.getWon());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanEventPersistentForClanHistory> E into(E into) {
		into.from(this);
		return into;
	}
}
