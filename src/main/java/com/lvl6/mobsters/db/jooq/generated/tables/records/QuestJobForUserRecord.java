/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Row;
import org.jooq.Row5;
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
@Table(name = "quest_job_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "quest_id", "quest_job_id"})
})
public class QuestJobForUserRecord extends UpdatableRecordImpl<QuestJobForUserRecord> implements Record5<String, Integer, Integer, Boolean, Integer>, IQuestJobForUser {

	private static final long serialVersionUID = -1548559268;

	/**
	 * Setter for <code>mobsters.quest_job_for_user.user_id</code>.
	 */
	@Override
	public QuestJobForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.quest_job_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.quest_job_for_user.quest_id</code>.
	 */
	@Override
	public QuestJobForUserRecord setQuestId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.quest_job_for_user.quest_id</code>.
	 */
	@Column(name = "quest_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getQuestId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.quest_job_for_user.quest_job_id</code>.
	 */
	@Override
	public QuestJobForUserRecord setQuestJobId(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.quest_job_for_user.quest_job_id</code>.
	 */
	@Column(name = "quest_job_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getQuestJobId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.quest_job_for_user.is_complete</code>.
	 */
	@Override
	public QuestJobForUserRecord setIsComplete(Boolean value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.quest_job_for_user.is_complete</code>.
	 */
	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return (Boolean) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.quest_job_for_user.progress</code>.
	 */
	@Override
	public QuestJobForUserRecord setProgress(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.quest_job_for_user.progress</code>.
	 */
	@Column(name = "progress", precision = 7)
	@Override
	public Integer getProgress() {
		return (Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<String, Integer, Integer> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Boolean, Integer> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Boolean, Integer> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return QuestJobForUser.QUEST_JOB_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return QuestJobForUser.QUEST_JOB_FOR_USER.QUEST_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return QuestJobForUser.QUEST_JOB_FOR_USER.QUEST_JOB_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field4() {
		return QuestJobForUser.QUEST_JOB_FOR_USER.IS_COMPLETE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return QuestJobForUser.QUEST_JOB_FOR_USER.PROGRESS;
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
		return getQuestId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getQuestJobId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value4() {
		return getIsComplete();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getProgress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord value2(Integer value) {
		setQuestId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord value3(Integer value) {
		setQuestJobId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord value4(Boolean value) {
		setIsComplete(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord value5(Integer value) {
		setProgress(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuestJobForUserRecord values(String value1, Integer value2, Integer value3, Boolean value4, Integer value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IQuestJobForUser from) {
		setUserId(from.getUserId());
		setQuestId(from.getQuestId());
		setQuestJobId(from.getQuestJobId());
		setIsComplete(from.getIsComplete());
		setProgress(from.getProgress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IQuestJobForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached QuestJobForUserRecord
	 */
	public QuestJobForUserRecord() {
		super(QuestJobForUser.QUEST_JOB_FOR_USER);
	}

	/**
	 * Create a detached, initialised QuestJobForUserRecord
	 */
	public QuestJobForUserRecord(String userId, Integer questId, Integer questJobId, Boolean isComplete, Integer progress) {
		super(QuestJobForUser.QUEST_JOB_FOR_USER);

		setValue(0, userId);
		setValue(1, questId);
		setValue(2, questJobId);
		setValue(3, isComplete);
		setValue(4, progress);
	}
}
