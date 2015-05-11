/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Table(name = "monster_for_user_deleted", schema = "mobsters")
public interface IMonsterForUserDeleted extends Serializable {

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.monster_for_user_id</code>.
	 */
	public IMonsterForUserDeleted setMonsterForUserId(String value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.monster_for_user_id</code>.
	 */
	@Id
	@Column(name = "monster_for_user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getMonsterForUserId();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.user_id</code>.
	 */
	public IMonsterForUserDeleted setUserId(String value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.monster_id</code>.
	 */
	public IMonsterForUserDeleted setMonsterId(UInteger value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.monster_id</code>.
	 */
	@Column(name = "monster_id", precision = 10)
	public UInteger getMonsterId();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.current_experience</code>.
	 */
	public IMonsterForUserDeleted setCurrentExperience(UInteger value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.current_experience</code>.
	 */
	@Column(name = "current_experience", precision = 10)
	public UInteger getCurrentExperience();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.current_level</code>.
	 */
	public IMonsterForUserDeleted setCurrentLevel(UByte value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.current_level</code>.
	 */
	@Column(name = "current_level", precision = 3)
	public UByte getCurrentLevel();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.current_health</code>.
	 */
	public IMonsterForUserDeleted setCurrentHealth(UInteger value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.current_health</code>.
	 */
	@Column(name = "current_health", precision = 10)
	public UInteger getCurrentHealth();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.num_pieces</code>.
	 */
	public IMonsterForUserDeleted setNumPieces(UByte value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.num_pieces</code>.
	 */
	@Column(name = "num_pieces", precision = 3)
	public UByte getNumPieces();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.is_complete</code>.
	 */
	public IMonsterForUserDeleted setIsComplete(Boolean value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	public Boolean getIsComplete();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.combine_start_time</code>.
	 */
	public IMonsterForUserDeleted setCombineStartTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.combine_start_time</code>.
	 */
	@Column(name = "combine_start_time")
	public Timestamp getCombineStartTime();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.team_slot_num</code>.
	 */
	public IMonsterForUserDeleted setTeamSlotNum(UByte value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.team_slot_num</code>.
	 */
	@Column(name = "team_slot_num", precision = 3)
	public UByte getTeamSlotNum();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.source_of_pieces</code>.
	 */
	public IMonsterForUserDeleted setSourceOfPieces(String value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.source_of_pieces</code>.
	 */
	@Column(name = "source_of_pieces", length = 65535)
	@Size(max = 65535)
	public String getSourceOfPieces();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.deleted_reason</code>.
	 */
	public IMonsterForUserDeleted setDeletedReason(String value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.deleted_reason</code>.
	 */
	@Column(name = "deleted_reason", length = 45)
	@Size(max = 45)
	public String getDeletedReason();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.details</code>.
	 */
	public IMonsterForUserDeleted setDetails(String value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.details</code>.
	 */
	@Column(name = "details", length = 65535)
	@Size(max = 65535)
	public String getDetails();

	/**
	 * Setter for <code>mobsters.monster_for_user_deleted.deleted_time</code>.
	 */
	public IMonsterForUserDeleted setDeletedTime(Timestamp value);

	/**
	 * Getter for <code>mobsters.monster_for_user_deleted.deleted_time</code>.
	 */
	@Column(name = "deleted_time")
	public Timestamp getDeletedTime();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IMonsterForUserDeleted
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForUserDeleted from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IMonsterForUserDeleted
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMonsterForUserDeleted> E into(E into);
}