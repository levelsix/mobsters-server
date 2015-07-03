/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.UserRewardHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserRewardHistory;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row;
import org.jooq.Row8;
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
@Table(name = "user_reward_history", schema = "mobsters")
public class UserRewardHistoryRecord extends UpdatableRecordImpl<UserRewardHistoryRecord> implements Record8<String, String, Timestamp, Integer, Integer, String, String, String>, IUserRewardHistory {

	private static final long serialVersionUID = 1013734108;

	/**
	 * Setter for <code>mobsters.user_reward_history.id</code>.
	 */
	@Override
	public UserRewardHistoryRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.user_id</code>.
	 */
	@Override
	public UserRewardHistoryRecord setUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.user_id</code>.
	 */
	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.date</code>.
	 */
	@Override
	public UserRewardHistoryRecord setDate(Timestamp value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.date</code>.
	 */
	@Column(name = "date")
	@Override
	public Timestamp getDate() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.reward_id</code>.
	 */
	@Override
	public UserRewardHistoryRecord setRewardId(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.static_data_id</code>.
	 */
	@Override
	public UserRewardHistoryRecord setStaticDataId(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.static_data_id</code>.
	 */
	@Column(name = "static_data_id", precision = 10)
	@Override
	public Integer getStaticDataId() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.reward_type</code>.
	 */
	@Override
	public UserRewardHistoryRecord setRewardType(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.reward_type</code>.
	 */
	@Column(name = "reward_type", length = 45)
	@Size(max = 45)
	@Override
	public String getRewardType() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.reason_for_reward</code>.
	 */
	@Override
	public UserRewardHistoryRecord setReasonForReward(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.reason_for_reward</code>.
	 */
	@Column(name = "reason_for_reward", length = 100)
	@Size(max = 100)
	@Override
	public String getReasonForReward() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.user_reward_history.details</code>.
	 */
	@Override
	public UserRewardHistoryRecord setDetails(String value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_reward_history.details</code>.
	 */
	@Column(name = "details", length = 255)
	@Size(max = 255)
	@Override
	public String getDetails() {
		return (String) getValue(7);
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
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<String, String, Timestamp, Integer, Integer, String, String, String> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<String, String, Timestamp, Integer, Integer, String, String, String> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return UserRewardHistory.USER_REWARD_HISTORY.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return UserRewardHistory.USER_REWARD_HISTORY.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return UserRewardHistory.USER_REWARD_HISTORY.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return UserRewardHistory.USER_REWARD_HISTORY.REWARD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return UserRewardHistory.USER_REWARD_HISTORY.STATIC_DATA_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return UserRewardHistory.USER_REWARD_HISTORY.REWARD_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return UserRewardHistory.USER_REWARD_HISTORY.REASON_FOR_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return UserRewardHistory.USER_REWARD_HISTORY.DETAILS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getRewardId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getStaticDataId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getRewardType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getReasonForReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getDetails();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value2(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value3(Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value4(Integer value) {
		setRewardId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value5(Integer value) {
		setStaticDataId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value6(String value) {
		setRewardType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value7(String value) {
		setReasonForReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord value8(String value) {
		setDetails(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserRewardHistoryRecord values(String value1, String value2, Timestamp value3, Integer value4, Integer value5, String value6, String value7, String value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserRewardHistory from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setDate(from.getDate());
		setRewardId(from.getRewardId());
		setStaticDataId(from.getStaticDataId());
		setRewardType(from.getRewardType());
		setReasonForReward(from.getReasonForReward());
		setDetails(from.getDetails());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserRewardHistory> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached UserRewardHistoryRecord
	 */
	public UserRewardHistoryRecord() {
		super(UserRewardHistory.USER_REWARD_HISTORY);
	}

	/**
	 * Create a detached, initialised UserRewardHistoryRecord
	 */
	public UserRewardHistoryRecord(String id, String userId, Timestamp date, Integer rewardId, Integer staticDataId, String rewardType, String reasonForReward, String details) {
		super(UserRewardHistory.USER_REWARD_HISTORY);

		setValue(0, id);
		setValue(1, userId);
		setValue(2, date);
		setValue(3, rewardId);
		setValue(4, staticDataId);
		setValue(5, rewardType);
		setValue(6, reasonForReward);
		setValue(7, details);
	}
}
