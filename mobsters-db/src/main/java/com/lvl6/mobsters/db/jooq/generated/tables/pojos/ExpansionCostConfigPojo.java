/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IExpansionCostConfig;

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
@Table(name = "expansion_cost_config", schema = "mobsters")
public class ExpansionCostConfigPojo implements IExpansionCostConfig {

	private static final long serialVersionUID = -385324031;

	private Integer id;
	private Integer expansionCostCash;
	private Integer numMinutesToExpand;
	private Integer speedupExpansionGemCost;

	public ExpansionCostConfigPojo() {}

	public ExpansionCostConfigPojo(ExpansionCostConfigPojo value) {
		this.id = value.id;
		this.expansionCostCash = value.expansionCostCash;
		this.numMinutesToExpand = value.numMinutesToExpand;
		this.speedupExpansionGemCost = value.speedupExpansionGemCost;
	}

	public ExpansionCostConfigPojo(
		Integer id,
		Integer expansionCostCash,
		Integer numMinutesToExpand,
		Integer speedupExpansionGemCost
	) {
		this.id = id;
		this.expansionCostCash = expansionCostCash;
		this.numMinutesToExpand = numMinutesToExpand;
		this.speedupExpansionGemCost = speedupExpansionGemCost;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public ExpansionCostConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "expansion_cost_cash", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getExpansionCostCash() {
		return this.expansionCostCash;
	}

	@Override
	public ExpansionCostConfigPojo setExpansionCostCash(Integer expansionCostCash) {
		this.expansionCostCash = expansionCostCash;
		return this;
	}

	@Column(name = "num_minutes_to_expand", precision = 10)
	@Override
	public Integer getNumMinutesToExpand() {
		return this.numMinutesToExpand;
	}

	@Override
	public ExpansionCostConfigPojo setNumMinutesToExpand(Integer numMinutesToExpand) {
		this.numMinutesToExpand = numMinutesToExpand;
		return this;
	}

	@Column(name = "speedup_expansion_gem_cost", precision = 10)
	@Override
	public Integer getSpeedupExpansionGemCost() {
		return this.speedupExpansionGemCost;
	}

	@Override
	public ExpansionCostConfigPojo setSpeedupExpansionGemCost(Integer speedupExpansionGemCost) {
		this.speedupExpansionGemCost = speedupExpansionGemCost;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IExpansionCostConfig from) {
		setId(from.getId());
		setExpansionCostCash(from.getExpansionCostCash());
		setNumMinutesToExpand(from.getNumMinutesToExpand());
		setSpeedupExpansionGemCost(from.getSpeedupExpansionGemCost());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IExpansionCostConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ExpansionCostConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ExpansionCostConfigRecord();
		poop.from(this);
		return "ExpansionCostConfigPojo[" + poop.valuesRow() + "]";
	}
}
