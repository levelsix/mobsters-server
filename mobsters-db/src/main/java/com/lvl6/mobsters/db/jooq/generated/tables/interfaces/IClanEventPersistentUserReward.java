/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
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
@Table(name = "clan_event_persistent_user_reward", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "user_id", "crs_start_time"})
})
public interface IClanEventPersistentUserReward extends Serializable {

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.id</code>.
	 */
	public IClanEventPersistentUserReward setId(String value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.id</code>.
	 */
	@Column(name = "id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.user_id</code>.
	 */
	public IClanEventPersistentUserReward setUserId(String value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.crs_start_time</code>.
	 */
	public IClanEventPersistentUserReward setCrsStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.crs_start_time</code>.
	 */
	@Column(name = "crs_start_time", nullable = false)
	@NotNull
	public Timestamp getCrsStartTime();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.crs_id</code>.
	 */
	public IClanEventPersistentUserReward setCrsId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.crs_id</code>.
	 */
	@Column(name = "crs_id", precision = 10)
	public Integer getCrsId();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.crs_end_time</code>.
	 */
	public IClanEventPersistentUserReward setCrsEndTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.crs_end_time</code>.
	 */
	@Column(name = "crs_end_time")
	public Timestamp getCrsEndTime();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.resource_type</code>. cash, oil, monsters, boat
	 */
	public IClanEventPersistentUserReward setResourceType(String value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.resource_type</code>. cash, oil, monsters, boat
	 */
	@Column(name = "resource_type", length = 45)
	@Size(max = 45)
	public String getResourceType();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.static_data_id</code>. could be a monster, or nothing, could be a boat
	 */
	public IClanEventPersistentUserReward setStaticDataId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.static_data_id</code>. could be a monster, or nothing, could be a boat
	 */
	@Column(name = "static_data_id", precision = 10)
	public Integer getStaticDataId();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.quantity</code>. if monsters then num pieces
	 */
	public IClanEventPersistentUserReward setQuantity(Integer value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.quantity</code>. if monsters then num pieces
	 */
	@Column(name = "quantity", precision = 10)
	public Integer getQuantity();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.clan_event_persistent_id</code>. just for details, history
	 */
	public IClanEventPersistentUserReward setClanEventPersistentId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.clan_event_persistent_id</code>. just for details, history
	 */
	@Column(name = "clan_event_persistent_id", precision = 10)
	public Integer getClanEventPersistentId();

	/**
	 * Setter for <code>mobsters.clan_event_persistent_user_reward.time_redeemed</code>.
	 */
	public IClanEventPersistentUserReward setTimeRedeemed(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_event_persistent_user_reward.time_redeemed</code>.
	 */
	@Column(name = "time_redeemed")
	public Timestamp getTimeRedeemed();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IClanEventPersistentUserReward
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentUserReward from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IClanEventPersistentUserReward
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanEventPersistentUserReward> E into(E into);
}