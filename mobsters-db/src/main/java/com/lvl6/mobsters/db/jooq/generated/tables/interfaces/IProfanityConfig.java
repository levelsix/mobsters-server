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
@Table(name = "profanity_config", schema = "mobsters")
public interface IProfanityConfig extends Serializable {

	/**
	 * Setter for <code>mobsters.profanity_config.term</code>.
	 */
	public IProfanityConfig setTerm(String value);

	/**
	 * Getter for <code>mobsters.profanity_config.term</code>.
	 */
	@Id
	@Column(name = "term", unique = true, nullable = false, length = 30)
	@NotNull
	@Size(max = 30)
	public String getTerm();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IProfanityConfig
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IProfanityConfig from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IProfanityConfig
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IProfanityConfig> E into(E into);
}
