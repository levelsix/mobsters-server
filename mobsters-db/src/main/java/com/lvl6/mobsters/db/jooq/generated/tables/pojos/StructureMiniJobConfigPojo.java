/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureMiniJobConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
@Table(name = "structure_mini_job_config", schema = "mobsters")
public class StructureMiniJobConfigPojo implements IStructureMiniJobConfig {

	private static final long serialVersionUID = -1621205059;

	private Integer structId;
	private Integer generatedJobLimit;
	private Integer hoursBetweenJobGeneration;

	public StructureMiniJobConfigPojo() {}

	public StructureMiniJobConfigPojo(StructureMiniJobConfigPojo value) {
		this.structId = value.structId;
		this.generatedJobLimit = value.generatedJobLimit;
		this.hoursBetweenJobGeneration = value.hoursBetweenJobGeneration;
	}

	public StructureMiniJobConfigPojo(
		Integer structId,
		Integer generatedJobLimit,
		Integer hoursBetweenJobGeneration
	) {
		this.structId = structId;
		this.generatedJobLimit = generatedJobLimit;
		this.hoursBetweenJobGeneration = hoursBetweenJobGeneration;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureMiniJobConfigPojo setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "generated_job_limit", precision = 7)
	@Override
	public Integer getGeneratedJobLimit() {
		return this.generatedJobLimit;
	}

	@Override
	public StructureMiniJobConfigPojo setGeneratedJobLimit(Integer generatedJobLimit) {
		this.generatedJobLimit = generatedJobLimit;
		return this;
	}

	@Column(name = "hours_between_job_generation", precision = 7)
	@Override
	public Integer getHoursBetweenJobGeneration() {
		return this.hoursBetweenJobGeneration;
	}

	@Override
	public StructureMiniJobConfigPojo setHoursBetweenJobGeneration(Integer hoursBetweenJobGeneration) {
		this.hoursBetweenJobGeneration = hoursBetweenJobGeneration;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureMiniJobConfig from) {
		setStructId(from.getStructId());
		setGeneratedJobLimit(from.getGeneratedJobLimit());
		setHoursBetweenJobGeneration(from.getHoursBetweenJobGeneration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureMiniJobConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.StructureMiniJobConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.StructureMiniJobConfigRecord();
		poop.from(this);
		return "StructureMiniJobConfigPojo[" + poop.valuesRow() + "]";
	}
}
