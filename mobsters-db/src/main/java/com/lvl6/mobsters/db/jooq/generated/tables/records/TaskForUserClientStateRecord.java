/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.TaskForUserClientState;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskForUserClientState;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row2;
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
@Table(name = "task_for_user_client_state", schema = "mobsters")
public class TaskForUserClientStateRecord extends UpdatableRecordImpl<TaskForUserClientStateRecord> implements Record2<String, byte[]>, ITaskForUserClientState {

	private static final long serialVersionUID = 348587967;

	/**
	 * Setter for <code>mobsters.task_for_user_client_state.user_id</code>.
	 */
	@Override
	public TaskForUserClientStateRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.task_for_user_client_state.user_id</code>.
	 */
	@Id
	@Column(name = "user_id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.task_for_user_client_state.client_state</code>.
	 */
	@Override
	public TaskForUserClientStateRecord setClientState(byte[] value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.task_for_user_client_state.client_state</code>.
	 */
	@Column(name = "client_state", length = 16777215)
	@Override
	public byte[] getClientState() {
		return (byte[]) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<String> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, byte[]> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, byte[]> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return TaskForUserClientState.TASK_FOR_USER_CLIENT_STATE.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<byte[]> field2() {
		return TaskForUserClientState.TASK_FOR_USER_CLIENT_STATE.CLIENT_STATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value2() {
		return getClientState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskForUserClientStateRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskForUserClientStateRecord value2(byte[] value) {
		setClientState(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskForUserClientStateRecord values(String value1, byte[] value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskForUserClientState from) {
		setUserId(from.getUserId());
		setClientState(from.getClientState());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskForUserClientState> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TaskForUserClientStateRecord
	 */
	public TaskForUserClientStateRecord() {
		super(TaskForUserClientState.TASK_FOR_USER_CLIENT_STATE);
	}

	/**
	 * Create a detached, initialised TaskForUserClientStateRecord
	 */
	public TaskForUserClientStateRecord(String userId, byte[] clientState) {
		super(TaskForUserClientState.TASK_FOR_USER_CLIENT_STATE);

		setValue(0, userId);
		setValue(1, clientState);
	}
}
