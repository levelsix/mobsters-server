/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IResearchConfig;

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
@Table(name = "research_config", schema = "mobsters")
public class ResearchConfigPojo implements IResearchConfig {

	private static final long serialVersionUID = -683102155;

	private Integer id;
	private String  researchType;
	private String  researchDomain;
	private String  iconImgName;
	private String  name;
	private Integer predId;
	private Integer succId;
	private String  desc;
	private Integer durationMin;
	private Integer costAmt;
	private String  costType;
	private Integer level;
	private Double  priority;
	private Integer tier;
	private Integer strength;

	public ResearchConfigPojo() {}

	public ResearchConfigPojo(ResearchConfigPojo value) {
		this.id = value.id;
		this.researchType = value.researchType;
		this.researchDomain = value.researchDomain;
		this.iconImgName = value.iconImgName;
		this.name = value.name;
		this.predId = value.predId;
		this.succId = value.succId;
		this.desc = value.desc;
		this.durationMin = value.durationMin;
		this.costAmt = value.costAmt;
		this.costType = value.costType;
		this.level = value.level;
		this.priority = value.priority;
		this.tier = value.tier;
		this.strength = value.strength;
	}

	public ResearchConfigPojo(
		Integer id,
		String  researchType,
		String  researchDomain,
		String  iconImgName,
		String  name,
		Integer predId,
		Integer succId,
		String  desc,
		Integer durationMin,
		Integer costAmt,
		String  costType,
		Integer level,
		Double  priority,
		Integer tier,
		Integer strength
	) {
		this.id = id;
		this.researchType = researchType;
		this.researchDomain = researchDomain;
		this.iconImgName = iconImgName;
		this.name = name;
		this.predId = predId;
		this.succId = succId;
		this.desc = desc;
		this.durationMin = durationMin;
		this.costAmt = costAmt;
		this.costType = costType;
		this.level = level;
		this.priority = priority;
		this.tier = tier;
		this.strength = strength;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public ResearchConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "research_type", length = 75)
	@Size(max = 75)
	@Override
	public String getResearchType() {
		return this.researchType;
	}

	@Override
	public ResearchConfigPojo setResearchType(String researchType) {
		this.researchType = researchType;
		return this;
	}

	@Column(name = "research_domain", length = 75)
	@Size(max = 75)
	@Override
	public String getResearchDomain() {
		return this.researchDomain;
	}

	@Override
	public ResearchConfigPojo setResearchDomain(String researchDomain) {
		this.researchDomain = researchDomain;
		return this;
	}

	@Column(name = "icon_img_name", length = 50)
	@Size(max = 50)
	@Override
	public String getIconImgName() {
		return this.iconImgName;
	}

	@Override
	public ResearchConfigPojo setIconImgName(String iconImgName) {
		this.iconImgName = iconImgName;
		return this;
	}

	@Column(name = "name", length = 50)
	@Size(max = 50)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ResearchConfigPojo setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "pred_id", precision = 10)
	@Override
	public Integer getPredId() {
		return this.predId;
	}

	@Override
	public ResearchConfigPojo setPredId(Integer predId) {
		this.predId = predId;
		return this;
	}

	@Column(name = "succ_id", precision = 10)
	@Override
	public Integer getSuccId() {
		return this.succId;
	}

	@Override
	public ResearchConfigPojo setSuccId(Integer succId) {
		this.succId = succId;
		return this;
	}

	@Column(name = "desc", length = 130)
	@Size(max = 130)
	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public ResearchConfigPojo setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	@Column(name = "duration_min", precision = 10)
	@Override
	public Integer getDurationMin() {
		return this.durationMin;
	}

	@Override
	public ResearchConfigPojo setDurationMin(Integer durationMin) {
		this.durationMin = durationMin;
		return this;
	}

	@Column(name = "cost_amt", precision = 10)
	@Override
	public Integer getCostAmt() {
		return this.costAmt;
	}

	@Override
	public ResearchConfigPojo setCostAmt(Integer costAmt) {
		this.costAmt = costAmt;
		return this;
	}

	@Column(name = "cost_type", length = 45)
	@Size(max = 45)
	@Override
	public String getCostType() {
		return this.costType;
	}

	@Override
	public ResearchConfigPojo setCostType(String costType) {
		this.costType = costType;
		return this;
	}

	@Column(name = "level", precision = 10)
	@Override
	public Integer getLevel() {
		return this.level;
	}

	@Override
	public ResearchConfigPojo setLevel(Integer level) {
		this.level = level;
		return this;
	}

	@Column(name = "priority", precision = 12)
	@Override
	public Double getPriority() {
		return this.priority;
	}

	@Override
	public ResearchConfigPojo setPriority(Double priority) {
		this.priority = priority;
		return this;
	}

	@Column(name = "tier", precision = 10)
	@Override
	public Integer getTier() {
		return this.tier;
	}

	@Override
	public ResearchConfigPojo setTier(Integer tier) {
		this.tier = tier;
		return this;
	}

	@Column(name = "strength", precision = 10)
	@Override
	public Integer getStrength() {
		return this.strength;
	}

	@Override
	public ResearchConfigPojo setStrength(Integer strength) {
		this.strength = strength;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IResearchConfig from) {
		setId(from.getId());
		setResearchType(from.getResearchType());
		setResearchDomain(from.getResearchDomain());
		setIconImgName(from.getIconImgName());
		setName(from.getName());
		setPredId(from.getPredId());
		setSuccId(from.getSuccId());
		setDesc(from.getDesc());
		setDurationMin(from.getDurationMin());
		setCostAmt(from.getCostAmt());
		setCostType(from.getCostType());
		setLevel(from.getLevel());
		setPriority(from.getPriority());
		setTier(from.getTier());
		setStrength(from.getStrength());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IResearchConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ResearchConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ResearchConfigRecord();
		poop.from(this);
		return "ResearchConfigPojo[" + poop.valuesRow() + "]";
	}
}
