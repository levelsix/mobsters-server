/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResearchHouseConfig;

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
@Table(name = "structure_research_house_config", schema = "mobsters")
public class StructureResearchHouseConfigPojo implements IStructureResearchHouseConfig {

	private static final long serialVersionUID = -279889641;

	private Integer structId;
	private Double  researchSpeedMultiplier;

	public StructureResearchHouseConfigPojo() {}

	public StructureResearchHouseConfigPojo(StructureResearchHouseConfigPojo value) {
		this.structId = value.structId;
		this.researchSpeedMultiplier = value.researchSpeedMultiplier;
	}

	public StructureResearchHouseConfigPojo(
		Integer structId,
		Double  researchSpeedMultiplier
	) {
		this.structId = structId;
		this.researchSpeedMultiplier = researchSpeedMultiplier;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureResearchHouseConfigPojo setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "research_speed_multiplier", precision = 12)
	@Override
	public Double getResearchSpeedMultiplier() {
		return this.researchSpeedMultiplier;
	}

	@Override
	public StructureResearchHouseConfigPojo setResearchSpeedMultiplier(Double researchSpeedMultiplier) {
		this.researchSpeedMultiplier = researchSpeedMultiplier;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureResearchHouseConfig from) {
		setStructId(from.getStructId());
		setResearchSpeedMultiplier(from.getResearchSpeedMultiplier());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureResearchHouseConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResearchHouseConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResearchHouseConfigRecord();
		poop.from(this);
		return "StructureResearchHouseConfigPojo[" + poop.valuesRow() + "]";
	}
}
