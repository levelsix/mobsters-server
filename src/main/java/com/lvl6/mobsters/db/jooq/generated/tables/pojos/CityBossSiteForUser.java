/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityBossSiteForUser;

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
@Table(name = "city_boss_site_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"city_id", "user_id"})
})
public class CityBossSiteForUser implements ICityBossSiteForUser {

	private static final long serialVersionUID = -2023130489;

	private Integer   cityId;
	private String    userId;
	private Integer   taskId;
	private Integer   bossId;
	private Timestamp timeOfEntry;

	public CityBossSiteForUser() {}

	public CityBossSiteForUser(CityBossSiteForUser value) {
		this.cityId = value.cityId;
		this.userId = value.userId;
		this.taskId = value.taskId;
		this.bossId = value.bossId;
		this.timeOfEntry = value.timeOfEntry;
	}

	public CityBossSiteForUser(
		Integer   cityId,
		String    userId,
		Integer   taskId,
		Integer   bossId,
		Timestamp timeOfEntry
	) {
		this.cityId = cityId;
		this.userId = userId;
		this.taskId = taskId;
		this.bossId = bossId;
		this.timeOfEntry = timeOfEntry;
	}

	@Column(name = "city_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCityId() {
		return this.cityId;
	}

	@Override
	public CityBossSiteForUser setCityId(Integer cityId) {
		this.cityId = cityId;
		return this;
	}

	@Column(name = "user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public CityBossSiteForUser setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "task_id", unique = true, precision = 10)
	@Override
	public Integer getTaskId() {
		return this.taskId;
	}

	@Override
	public CityBossSiteForUser setTaskId(Integer taskId) {
		this.taskId = taskId;
		return this;
	}

	@Column(name = "boss_id", precision = 10)
	@Override
	public Integer getBossId() {
		return this.bossId;
	}

	@Override
	public CityBossSiteForUser setBossId(Integer bossId) {
		this.bossId = bossId;
		return this;
	}

	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public CityBossSiteForUser setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICityBossSiteForUser from) {
		setCityId(from.getCityId());
		setUserId(from.getUserId());
		setTaskId(from.getTaskId());
		setBossId(from.getBossId());
		setTimeOfEntry(from.getTimeOfEntry());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICityBossSiteForUser> E into(E into) {
		into.from(this);
		return into;
	}
}
