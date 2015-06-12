/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureMoneyTreeConfig;

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
@Table(name = "structure_money_tree_config", schema = "mobsters")
public class StructureMoneyTreeConfigPojo implements IStructureMoneyTreeConfig {

	private static final long serialVersionUID = -1864286644;

	private Integer structId;
	private Double  productionRate;
	private Integer capacity;
	private Integer daysOfDuration;
	private Integer daysForRenewal;
	private String  iapProductId;
	private String  fakeIapProductId;

	public StructureMoneyTreeConfigPojo() {}

	public StructureMoneyTreeConfigPojo(StructureMoneyTreeConfigPojo value) {
		this.structId = value.structId;
		this.productionRate = value.productionRate;
		this.capacity = value.capacity;
		this.daysOfDuration = value.daysOfDuration;
		this.daysForRenewal = value.daysForRenewal;
		this.iapProductId = value.iapProductId;
		this.fakeIapProductId = value.fakeIapProductId;
	}

	public StructureMoneyTreeConfigPojo(
		Integer structId,
		Double  productionRate,
		Integer capacity,
		Integer daysOfDuration,
		Integer daysForRenewal,
		String  iapProductId,
		String  fakeIapProductId
	) {
		this.structId = structId;
		this.productionRate = productionRate;
		this.capacity = capacity;
		this.daysOfDuration = daysOfDuration;
		this.daysForRenewal = daysForRenewal;
		this.iapProductId = iapProductId;
		this.fakeIapProductId = fakeIapProductId;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureMoneyTreeConfigPojo setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "production_rate", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getProductionRate() {
		return this.productionRate;
	}

	@Override
	public StructureMoneyTreeConfigPojo setProductionRate(Double productionRate) {
		this.productionRate = productionRate;
		return this;
	}

	@Column(name = "capacity", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCapacity() {
		return this.capacity;
	}

	@Override
	public StructureMoneyTreeConfigPojo setCapacity(Integer capacity) {
		this.capacity = capacity;
		return this;
	}

	@Column(name = "days_of_duration", precision = 10)
	@Override
	public Integer getDaysOfDuration() {
		return this.daysOfDuration;
	}

	@Override
	public StructureMoneyTreeConfigPojo setDaysOfDuration(Integer daysOfDuration) {
		this.daysOfDuration = daysOfDuration;
		return this;
	}

	@Column(name = "days_for_renewal", precision = 10)
	@Override
	public Integer getDaysForRenewal() {
		return this.daysForRenewal;
	}

	@Override
	public StructureMoneyTreeConfigPojo setDaysForRenewal(Integer daysForRenewal) {
		this.daysForRenewal = daysForRenewal;
		return this;
	}

	@Column(name = "iap_product_id", nullable = false, length = 100)
	@NotNull
	@Size(max = 100)
	@Override
	public String getIapProductId() {
		return this.iapProductId;
	}

	@Override
	public StructureMoneyTreeConfigPojo setIapProductId(String iapProductId) {
		this.iapProductId = iapProductId;
		return this;
	}

	@Column(name = "fake_iap_product_id", length = 100)
	@Size(max = 100)
	@Override
	public String getFakeIapProductId() {
		return this.fakeIapProductId;
	}

	@Override
	public StructureMoneyTreeConfigPojo setFakeIapProductId(String fakeIapProductId) {
		this.fakeIapProductId = fakeIapProductId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureMoneyTreeConfig from) {
		setStructId(from.getStructId());
		setProductionRate(from.getProductionRate());
		setCapacity(from.getCapacity());
		setDaysOfDuration(from.getDaysOfDuration());
		setDaysForRenewal(from.getDaysForRenewal());
		setIapProductId(from.getIapProductId());
		setFakeIapProductId(from.getFakeIapProductId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureMoneyTreeConfig> E into(E into) {
		into.from(this);
		return into;
	}
}