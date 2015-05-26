/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IProfanityConfig;

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
public class ProfanityConfig implements IProfanityConfig {

	private static final long serialVersionUID = 1279332835;

	private String term;

	public ProfanityConfig() {}

	public ProfanityConfig(ProfanityConfig value) {
		this.term = value.term;
	}

	public ProfanityConfig(
		String term
	) {
		this.term = term;
	}

	@Id
	@Column(name = "term", unique = true, nullable = false, length = 30)
	@NotNull
	@Size(max = 30)
	@Override
	public String getTerm() {
		return this.term;
	}

	@Override
	public ProfanityConfig setTerm(String term) {
		this.term = term;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IProfanityConfig from) {
		setTerm(from.getTerm());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IProfanityConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
