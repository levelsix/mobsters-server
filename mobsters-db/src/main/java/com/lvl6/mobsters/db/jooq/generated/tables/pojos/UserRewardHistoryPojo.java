/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserRewardHistory;

import java.sql.Timestamp;

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
@Table(name = "user_reward_history", schema = "mobsters")
public class UserRewardHistoryPojo implements IUserRewardHistory {

	private static final long serialVersionUID = 1459740091;

	private String    id;
	private String    userId;
	private Timestamp date;
	private Integer   rewardId;
	private Integer   staticDataId;
	private String    rewardType;
	private String    reasonForReward;
	private String    details;

	public UserRewardHistoryPojo() {}

	public UserRewardHistoryPojo(UserRewardHistoryPojo value) {
		this.id = value.id;
		this.userId = value.userId;
		this.date = value.date;
		this.rewardId = value.rewardId;
		this.staticDataId = value.staticDataId;
		this.rewardType = value.rewardType;
		this.reasonForReward = value.reasonForReward;
		this.details = value.details;
	}

	public UserRewardHistoryPojo(
		String    id,
		String    userId,
		Timestamp date,
		Integer   rewardId,
		Integer   staticDataId,
		String    rewardType,
		String    reasonForReward,
		String    details
	) {
		this.id = id;
		this.userId = userId;
		this.date = date;
		this.rewardId = rewardId;
		this.staticDataId = staticDataId;
		this.rewardType = rewardType;
		this.reasonForReward = reasonForReward;
		this.details = details;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public UserRewardHistoryPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UserRewardHistoryPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "date")
	@Override
	public Timestamp getDate() {
		return this.date;
	}

	@Override
	public UserRewardHistoryPojo setDate(Timestamp date) {
		this.date = date;
		return this;
	}

	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return this.rewardId;
	}

	@Override
	public UserRewardHistoryPojo setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
		return this;
	}

	@Column(name = "static_data_id", precision = 10)
	@Override
	public Integer getStaticDataId() {
		return this.staticDataId;
	}

	@Override
	public UserRewardHistoryPojo setStaticDataId(Integer staticDataId) {
		this.staticDataId = staticDataId;
		return this;
	}

	@Column(name = "reward_type", length = 45)
	@Size(max = 45)
	@Override
	public String getRewardType() {
		return this.rewardType;
	}

	@Override
	public UserRewardHistoryPojo setRewardType(String rewardType) {
		this.rewardType = rewardType;
		return this;
	}

	@Column(name = "reason_for_reward", length = 100)
	@Size(max = 100)
	@Override
	public String getReasonForReward() {
		return this.reasonForReward;
	}

	@Override
	public UserRewardHistoryPojo setReasonForReward(String reasonForReward) {
		this.reasonForReward = reasonForReward;
		return this;
	}

	@Column(name = "details", length = 255)
	@Size(max = 255)
	@Override
	public String getDetails() {
		return this.details;
	}

	@Override
	public UserRewardHistoryPojo setDetails(String details) {
		this.details = details;
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


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.UserRewardHistoryRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.UserRewardHistoryRecord();
		poop.from(this);
		return "UserRewardHistoryPojo[" + poop.valuesRow() + "]";
	}
}
