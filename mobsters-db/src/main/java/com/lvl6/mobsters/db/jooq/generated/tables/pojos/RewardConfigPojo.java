/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IRewardConfig;

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
@Table(name = "reward_config", schema = "mobsters")
public class RewardConfigPojo implements IRewardConfig {

	private static final long serialVersionUID = 1742575039;

	private Integer id;
	private Integer staticDataId;
	private String  type;
	private Integer amt;

	public RewardConfigPojo() {}

	public RewardConfigPojo(RewardConfigPojo value) {
		this.id = value.id;
		this.staticDataId = value.staticDataId;
		this.type = value.type;
		this.amt = value.amt;
	}

	public RewardConfigPojo(
		Integer id,
		Integer staticDataId,
		String  type,
		Integer amt
	) {
		this.id = id;
		this.staticDataId = staticDataId;
		this.type = type;
		this.amt = amt;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public RewardConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "static_data_id", precision = 10)
	@Override
	public Integer getStaticDataId() {
		return this.staticDataId;
	}

	@Override
	public RewardConfigPojo setStaticDataId(Integer staticDataId) {
		this.staticDataId = staticDataId;
		return this;
	}

	@Column(name = "type", length = 55)
	@Size(max = 55)
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public RewardConfigPojo setType(String type) {
		this.type = type;
		return this;
	}

	@Column(name = "amt", precision = 10)
	@Override
	public Integer getAmt() {
		return this.amt;
	}

	@Override
	public RewardConfigPojo setAmt(Integer amt) {
		this.amt = amt;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IRewardConfig from) {
		setId(from.getId());
		setStaticDataId(from.getStaticDataId());
		setType(from.getType());
		setAmt(from.getAmt());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IRewardConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.RewardConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.RewardConfigRecord();
		poop.from(this);
		return "RewardConfigPojo[" + poop.valuesRow() + "]";
	}
}
