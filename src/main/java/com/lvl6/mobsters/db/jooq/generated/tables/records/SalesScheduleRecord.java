/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.SalesSchedule;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ISalesSchedule;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


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
public class SalesScheduleRecord extends UpdatableRecordImpl<SalesScheduleRecord> implements Record4<Integer, Integer, Timestamp, Timestamp>, ISalesSchedule {

	private static final long serialVersionUID = -1969713481;

	/**
	 * Setter for <code>mobsters.sales_schedule.id</code>.
	 */
	@Override
	public SalesScheduleRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.sales_schedule.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.sales_schedule.sales_package_id</code>.
	 */
	@Override
	public SalesScheduleRecord setSalesPackageId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.sales_schedule.sales_package_id</code>.
	 */
	@Column(name = "sales_package_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getSalesPackageId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.sales_schedule.time_start</code>.
	 */
	@Override
	public SalesScheduleRecord setTimeStart(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.sales_schedule.time_start</code>.
	 */
	@Column(name = "time_start")
	@Override
	public Timestamp getTimeStart() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.sales_schedule.time_end</code>.
	 */
	@Override
	public SalesScheduleRecord setTimeEnd(Timestamp value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.sales_schedule.time_end</code>.
	 */
	@Column(name = "time_end")
	@Override
	public Timestamp getTimeEnd() {
		return (Timestamp) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, Integer, Timestamp, Timestamp> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, Integer, Timestamp, Timestamp> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return SalesSchedule.SALES_SCHEDULE.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return SalesSchedule.SALES_SCHEDULE.SALES_PACKAGE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return SalesSchedule.SALES_SCHEDULE.TIME_START;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return SalesSchedule.SALES_SCHEDULE.TIME_END;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getSalesPackageId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getTimeStart();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getTimeEnd();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesScheduleRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesScheduleRecord value2(Integer value) {
		setSalesPackageId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesScheduleRecord value3(Timestamp value) {
		setTimeStart(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesScheduleRecord value4(Timestamp value) {
		setTimeEnd(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SalesScheduleRecord values(Integer value1, Integer value2, Timestamp value3, Timestamp value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached SalesScheduleRecord
	 */
	public SalesScheduleRecord() {
		super(SalesSchedule.SALES_SCHEDULE);
	}

	/**
	 * Create a detached, initialised SalesScheduleRecord
	 */
	public SalesScheduleRecord(Integer id, Integer salesPackageId, Timestamp timeStart, Timestamp timeEnd) {
		super(SalesSchedule.SALES_SCHEDULE);

		setValue(0, id);
		setValue(1, salesPackageId);
		setValue(2, timeStart);
		setValue(3, timeEnd);
	}
}
