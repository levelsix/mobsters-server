/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IEventPersistentForUser;

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
@Table(name = "event_persistent_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "event_persistent_id"})
})
public class EventPersistentForUserPojo implements IEventPersistentForUser {

	private static final long serialVersionUID = 144750839;

	private String    userId;
	private Integer   eventPersistentId;
	private Timestamp timeOfEntry;

	public EventPersistentForUserPojo() {}

	public EventPersistentForUserPojo(EventPersistentForUserPojo value) {
		this.userId = value.userId;
		this.eventPersistentId = value.eventPersistentId;
		this.timeOfEntry = value.timeOfEntry;
	}

	public EventPersistentForUserPojo(
		String    userId,
		Integer   eventPersistentId,
		Timestamp timeOfEntry
	) {
		this.userId = userId;
		this.eventPersistentId = eventPersistentId;
		this.timeOfEntry = timeOfEntry;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public EventPersistentForUserPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "event_persistent_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getEventPersistentId() {
		return this.eventPersistentId;
	}

	@Override
	public EventPersistentForUserPojo setEventPersistentId(Integer eventPersistentId) {
		this.eventPersistentId = eventPersistentId;
		return this;
	}

	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public EventPersistentForUserPojo setTimeOfEntry(Timestamp timeOfEntry) {
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
	public void from(IEventPersistentForUser from) {
		setUserId(from.getUserId());
		setEventPersistentId(from.getEventPersistentId());
		setTimeOfEntry(from.getTimeOfEntry());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IEventPersistentForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.EventPersistentForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.EventPersistentForUserRecord();
		poop.from(this);
		return "EventPersistentForUserPojo[" + poop.valuesRow() + "]";
	}
}
