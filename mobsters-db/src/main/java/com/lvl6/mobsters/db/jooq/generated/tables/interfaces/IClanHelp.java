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
@Table(name = "clan_help", schema = "mobsters")
public interface IClanHelp extends Serializable {

	/**
	 * Setter for <code>mobsters.clan_help.id</code>.
	 */
	public IClanHelp setId(String value);

	/**
	 * Getter for <code>mobsters.clan_help.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.clan_help.user_id</code>.
	 */
	public IClanHelp setUserId(String value);

	/**
	 * Getter for <code>mobsters.clan_help.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	public String getUserId();

	/**
	 * Setter for <code>mobsters.clan_help.user_data_id</code>.
	 */
	public IClanHelp setUserDataId(String value);

	/**
	 * Getter for <code>mobsters.clan_help.user_data_id</code>.
	 */
	@Column(name = "user_data_id", length = 36)
	@Size(max = 36)
	public String getUserDataId();

	/**
	 * Setter for <code>mobsters.clan_help.help_type</code>.
	 */
	public IClanHelp setHelpType(String value);

	/**
	 * Getter for <code>mobsters.clan_help.help_type</code>.
	 */
	@Column(name = "help_type", length = 45)
	@Size(max = 45)
	public String getHelpType();

	/**
	 * Setter for <code>mobsters.clan_help.clan_id</code>.
	 */
	public IClanHelp setClanId(String value);

	/**
	 * Getter for <code>mobsters.clan_help.clan_id</code>.
	 */
	@Column(name = "clan_id", length = 36)
	@Size(max = 36)
	public String getClanId();

	/**
	 * Setter for <code>mobsters.clan_help.time_of_entry</code>.
	 */
	public IClanHelp setTimeOfEntry(Timestamp value);

	/**
	 * Getter for <code>mobsters.clan_help.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry")
	public Timestamp getTimeOfEntry();

	/**
	 * Setter for <code>mobsters.clan_help.max_helpers</code>.
	 */
	public IClanHelp setMaxHelpers(Integer value);

	/**
	 * Getter for <code>mobsters.clan_help.max_helpers</code>.
	 */
	@Column(name = "max_helpers", precision = 10)
	public Integer getMaxHelpers();

	/**
	 * Setter for <code>mobsters.clan_help.helpers</code>.
	 */
	public IClanHelp setHelpers(String value);

	/**
	 * Getter for <code>mobsters.clan_help.helpers</code>.
	 */
	@Column(name = "helpers", length = 65535)
	@Size(max = 65535)
	public String getHelpers();

	/**
	 * Setter for <code>mobsters.clan_help.open</code>.
	 */
	public IClanHelp setOpen(Boolean value);

	/**
	 * Getter for <code>mobsters.clan_help.open</code>.
	 */
	@Column(name = "open", precision = 1)
	public Boolean getOpen();

	/**
	 * Setter for <code>mobsters.clan_help.static_data_id</code>.
	 */
	public IClanHelp setStaticDataId(Integer value);

	/**
	 * Getter for <code>mobsters.clan_help.static_data_id</code>.
	 */
	@Column(name = "static_data_id", precision = 10)
	public Integer getStaticDataId();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IClanHelp
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanHelp from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IClanHelp
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanHelp> E into(E into);
}
