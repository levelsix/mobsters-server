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
@Table(name = "city_boss_site_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"city_id", "user_id"})
})
public class CityBossSiteForUser implements ICityBossSiteForUser {

	private static final long serialVersionUID = 1404512546;

	private UInteger  cityId;
	private String    userId;
	private UInteger  taskId;
	private UInteger  bossId;
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
		UInteger  cityId,
		String    userId,
		UInteger  taskId,
		UInteger  bossId,
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
	public UInteger getCityId() {
		return this.cityId;
	}

	@Override
	public CityBossSiteForUser setCityId(UInteger cityId) {
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
	public UInteger getTaskId() {
		return this.taskId;
	}

	@Override
	public CityBossSiteForUser setTaskId(UInteger taskId) {
		this.taskId = taskId;
		return this;
	}

	@Column(name = "boss_id", precision = 10)
	@Override
	public UInteger getBossId() {
		return this.bossId;
	}

	@Override
	public CityBossSiteForUser setBossId(UInteger bossId) {
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
