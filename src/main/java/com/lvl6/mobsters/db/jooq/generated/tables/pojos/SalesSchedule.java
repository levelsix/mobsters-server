/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISalesSchedule;

import java.sql.Timestamp;

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
@Table(name = "sales_schedule", schema = "mobsters")
public class SalesSchedule implements ISalesSchedule {

	private static final long serialVersionUID = 2011903838;

	private Integer   id;
	private Integer   salesPackageId;
	private Timestamp timeStart;
	private Timestamp timeEnd;

	public SalesSchedule() {}

	public SalesSchedule(SalesSchedule value) {
		this.id = value.id;
		this.salesPackageId = value.salesPackageId;
		this.timeStart = value.timeStart;
		this.timeEnd = value.timeEnd;
	}

	public SalesSchedule(
		Integer   id,
		Integer   salesPackageId,
		Timestamp timeStart,
		Timestamp timeEnd
	) {
		this.id = id;
		this.salesPackageId = salesPackageId;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public SalesSchedule setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "sales_package_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getSalesPackageId() {
		return this.salesPackageId;
	}

	@Override
	public SalesSchedule setSalesPackageId(Integer salesPackageId) {
		this.salesPackageId = salesPackageId;
		return this;
	}

	@Column(name = "time_start")
	@Override
	public Timestamp getTimeStart() {
		return this.timeStart;
	}

	@Override
	public SalesSchedule setTimeStart(Timestamp timeStart) {
		this.timeStart = timeStart;
		return this;
	}

	@Column(name = "time_end")
	@Override
	public Timestamp getTimeEnd() {
		return this.timeEnd;
	}

	@Override
	public SalesSchedule setTimeEnd(Timestamp timeEnd) {
		this.timeEnd = timeEnd;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ISalesSchedule from) {
		setId(from.getId());
		setSalesPackageId(from.getSalesPackageId());
		setTimeStart(from.getTimeStart());
		setTimeEnd(from.getTimeEnd());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ISalesSchedule> E into(E into) {
		into.from(this);
		return into;
	}
}
