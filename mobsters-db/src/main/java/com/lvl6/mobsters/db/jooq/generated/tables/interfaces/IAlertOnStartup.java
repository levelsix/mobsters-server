/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

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
@Table(name = "alert_on_startup", schema = "mobsters")
public interface IAlertOnStartup extends Serializable {

	/**
	 * Setter for <code>mobsters.alert_on_startup.id</code>.
	 */
	public IAlertOnStartup setId(Integer value);

	/**
	 * Getter for <code>mobsters.alert_on_startup.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	public Integer getId();

	/**
	 * Setter for <code>mobsters.alert_on_startup.message</code>.
	 */
	public IAlertOnStartup setMessage(String value);

	/**
	 * Getter for <code>mobsters.alert_on_startup.message</code>.
	 */
	@Column(name = "message", length = 90)
	@Size(max = 90)
	public String getMessage();

	/**
	 * Setter for <code>mobsters.alert_on_startup.is_active</code>.
	 */
	public IAlertOnStartup setIsActive(Boolean value);

	/**
	 * Getter for <code>mobsters.alert_on_startup.is_active</code>.
	 */
	@Column(name = "is_active", precision = 1)
	public Boolean getIsActive();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IAlertOnStartup
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IAlertOnStartup from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IAlertOnStartup
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IAlertOnStartup> E into(E into);
}
