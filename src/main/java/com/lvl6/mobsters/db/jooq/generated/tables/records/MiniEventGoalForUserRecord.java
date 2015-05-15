/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventGoalForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventGoalForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
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
@Table(name = "mini_event_goal_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "mini_event_goal_id"})
})
public class MiniEventGoalForUserRecord extends UpdatableRecordImpl<MiniEventGoalForUserRecord> implements Record3<String, Integer, Integer>, IMiniEventGoalForUser {

	private static final long serialVersionUID = -2011368635;

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.user_id</code>.
	 */
	@Override
	public MiniEventGoalForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.mini_event_goal_id</code>.
	 */
	@Override
	public MiniEventGoalForUserRecord setMiniEventGoalId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.mini_event_goal_id</code>.
	 */
	@Column(name = "mini_event_goal_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMiniEventGoalId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.mini_event_goal_for_user.progress</code>.
	 */
	@Override
	public MiniEventGoalForUserRecord setProgress(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_goal_for_user.progress</code>.
	 */
	@Column(name = "progress", precision = 10)
	@Override
	public Integer getProgress() {
		return (Integer) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<String, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<String, Integer, Integer> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<String, Integer, Integer> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.MINI_EVENT_GOAL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER.PROGRESS;
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
	public Integer value2() {
		return getMiniEventGoalId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getProgress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventGoalForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventGoalForUserRecord value2(Integer value) {
		setMiniEventGoalId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventGoalForUserRecord value3(Integer value) {
		setProgress(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventGoalForUserRecord values(String value1, Integer value2, Integer value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventGoalForUser from) {
		setUserId(from.getUserId());
		setMiniEventGoalId(from.getMiniEventGoalId());
		setProgress(from.getProgress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventGoalForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MiniEventGoalForUserRecord
	 */
	public MiniEventGoalForUserRecord() {
		super(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER);
	}

	/**
	 * Create a detached, initialised MiniEventGoalForUserRecord
	 */
	public MiniEventGoalForUserRecord(String userId, Integer miniEventGoalId, Integer progress) {
		super(MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER);

		setValue(0, userId);
		setValue(1, miniEventGoalId);
		setValue(2, progress);
	}
}
