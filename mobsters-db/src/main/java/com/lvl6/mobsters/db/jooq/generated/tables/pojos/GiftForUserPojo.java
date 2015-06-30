/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftForUser;

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
@Table(name = "gift_for_user", schema = "mobsters")
public class GiftForUserPojo implements IGiftForUser {

	private static final long serialVersionUID = -2079699480;

	private String    id;
	private String    gifterUserId;
	private String    receiverUserId;
	private Integer   giftId;
	private Timestamp timeOfEntry;
	private Integer   rewardId;
	private Boolean   collected;
	private Integer   minutesTillExpiration;
	private String    reasonForGift;

	public GiftForUserPojo() {}

	public GiftForUserPojo(GiftForUserPojo value) {
		this.id = value.id;
		this.gifterUserId = value.gifterUserId;
		this.receiverUserId = value.receiverUserId;
		this.giftId = value.giftId;
		this.timeOfEntry = value.timeOfEntry;
		this.rewardId = value.rewardId;
		this.collected = value.collected;
		this.minutesTillExpiration = value.minutesTillExpiration;
		this.reasonForGift = value.reasonForGift;
	}

	public GiftForUserPojo(
		String    id,
		String    gifterUserId,
		String    receiverUserId,
		Integer   giftId,
		Timestamp timeOfEntry,
		Integer   rewardId,
		Boolean   collected,
		Integer   minutesTillExpiration,
		String    reasonForGift
	) {
		this.id = id;
		this.gifterUserId = gifterUserId;
		this.receiverUserId = receiverUserId;
		this.giftId = giftId;
		this.timeOfEntry = timeOfEntry;
		this.rewardId = rewardId;
		this.collected = collected;
		this.minutesTillExpiration = minutesTillExpiration;
		this.reasonForGift = reasonForGift;
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
	public GiftForUserPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "gifter_user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getGifterUserId() {
		return this.gifterUserId;
	}

	@Override
	public GiftForUserPojo setGifterUserId(String gifterUserId) {
		this.gifterUserId = gifterUserId;
		return this;
	}

	@Column(name = "receiver_user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getReceiverUserId() {
		return this.receiverUserId;
	}

	@Override
	public GiftForUserPojo setReceiverUserId(String receiverUserId) {
		this.receiverUserId = receiverUserId;
		return this;
	}

	@Column(name = "gift_id", precision = 10)
	@Override
	public Integer getGiftId() {
		return this.giftId;
	}

	@Override
	public GiftForUserPojo setGiftId(Integer giftId) {
		this.giftId = giftId;
		return this;
	}

	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return this.timeOfEntry;
	}

	@Override
	public GiftForUserPojo setTimeOfEntry(Timestamp timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
		return this;
	}

	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return this.rewardId;
	}

	@Override
	public GiftForUserPojo setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
		return this;
	}

	@Column(name = "collected", precision = 1)
	@Override
	public Boolean getCollected() {
		return this.collected;
	}

	@Override
	public GiftForUserPojo setCollected(Boolean collected) {
		this.collected = collected;
		return this;
	}

	@Column(name = "minutes_till_expiration", precision = 10)
	@Override
	public Integer getMinutesTillExpiration() {
		return this.minutesTillExpiration;
	}

	@Override
	public GiftForUserPojo setMinutesTillExpiration(Integer minutesTillExpiration) {
		this.minutesTillExpiration = minutesTillExpiration;
		return this;
	}

	@Column(name = "reason_for_gift", length = 75)
	@Size(max = 75)
	@Override
	public String getReasonForGift() {
		return this.reasonForGift;
	}

	@Override
	public GiftForUserPojo setReasonForGift(String reasonForGift) {
		this.reasonForGift = reasonForGift;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IGiftForUser from) {
		setId(from.getId());
		setGifterUserId(from.getGifterUserId());
		setReceiverUserId(from.getReceiverUserId());
		setGiftId(from.getGiftId());
		setTimeOfEntry(from.getTimeOfEntry());
		setRewardId(from.getRewardId());
		setCollected(from.getCollected());
		setMinutesTillExpiration(from.getMinutesTillExpiration());
		setReasonForGift(from.getReasonForGift());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IGiftForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.GiftForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.GiftForUserRecord();
		poop.from(this);
		return "GiftForUserPojo[" + poop.valuesRow() + "]";
	}
}
