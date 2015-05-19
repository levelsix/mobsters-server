/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.GiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftForUser;

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
import org.jooq.Record10;
import org.jooq.Row;
import org.jooq.Row10;
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
@Table(name = "gift_for_user", schema = "mobsters")
public class GiftForUserRecord extends UpdatableRecordImpl<GiftForUserRecord> implements Record10<String, String, String, String, Integer, Timestamp, Integer, Boolean, Integer, String>, IGiftForUser {

	private static final long serialVersionUID = -1304744353;

	/**
	 * Setter for <code>mobsters.gift_for_user.id</code>.
	 */
	@Override
	public GiftForUserRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.id</code>.
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
	 * Setter for <code>mobsters.gift_for_user.gifter_user_id</code>.
	 */
	@Override
	public GiftForUserRecord setGifterUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.gifter_user_id</code>.
	 */
	@Column(name = "gifter_user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getGifterUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.receiver_user_id</code>.
	 */
	@Override
	public GiftForUserRecord setReceiverUserId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.receiver_user_id</code>.
	 */
	@Column(name = "receiver_user_id", length = 36)
	@Size(max = 36)
	@Override
	public String getReceiverUserId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.gift_type</code>. at the moment: GENERIC_GIFT, CLAN_GIFT, TANGO_GIFT
	 */
	@Override
	public GiftForUserRecord setGiftType(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.gift_type</code>. at the moment: GENERIC_GIFT, CLAN_GIFT, TANGO_GIFT
	 */
	@Column(name = "gift_type", length = 45)
	@Size(max = 45)
	@Override
	public String getGiftType() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.static_data_id</code>. at the moment can reference clan_gift_config and tango_gift_config
	 */
	@Override
	public GiftForUserRecord setStaticDataId(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.static_data_id</code>. at the moment can reference clan_gift_config and tango_gift_config
	 */
	@Column(name = "static_data_id", precision = 10)
	@Override
	public Integer getStaticDataId() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.time_of_entry</code>.
	 */
	@Override
	public GiftForUserRecord setTimeOfEntry(Timestamp value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry")
	@Override
	public Timestamp getTimeOfEntry() {
		return (Timestamp) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.reward_id</code>.
	 */
	@Override
	public GiftForUserRecord setRewardId(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	@Override
	public Integer getRewardId() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.collected</code>.
	 */
	@Override
	public GiftForUserRecord setCollected(Boolean value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.collected</code>.
	 */
	@Column(name = "collected", precision = 1)
	@Override
	public Boolean getCollected() {
		return (Boolean) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.minutes_till_expiration</code>. this has the lowest expiration priority. Expiration also determined by gift_type
	 */
	@Override
	public GiftForUserRecord setMinutesTillExpiration(Integer value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.minutes_till_expiration</code>. this has the lowest expiration priority. Expiration also determined by gift_type
	 */
	@Column(name = "minutes_till_expiration", precision = 10)
	@Override
	public Integer getMinutesTillExpiration() {
		return (Integer) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.gift_for_user.reason_for_gift</code>.
	 */
	@Override
	public GiftForUserRecord setReasonForGift(String value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.gift_for_user.reason_for_gift</code>.
	 */
	@Column(name = "reason_for_gift", length = 75)
	@Size(max = 75)
	@Override
	public String getReasonForGift() {
		return (String) getValue(9);
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
	// Record10 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row10<String, String, String, String, Integer, Timestamp, Integer, Boolean, Integer, String> fieldsRow() {
		return (Row10) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row10<String, String, String, String, Integer, Timestamp, Integer, Boolean, Integer, String> valuesRow() {
		return (Row10) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return GiftForUser.GIFT_FOR_USER.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return GiftForUser.GIFT_FOR_USER.GIFTER_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return GiftForUser.GIFT_FOR_USER.RECEIVER_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return GiftForUser.GIFT_FOR_USER.GIFT_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return GiftForUser.GIFT_FOR_USER.STATIC_DATA_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field6() {
		return GiftForUser.GIFT_FOR_USER.TIME_OF_ENTRY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return GiftForUser.GIFT_FOR_USER.REWARD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field8() {
		return GiftForUser.GIFT_FOR_USER.COLLECTED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field9() {
		return GiftForUser.GIFT_FOR_USER.MINUTES_TILL_EXPIRATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return GiftForUser.GIFT_FOR_USER.REASON_FOR_GIFT;
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
		return getGifterUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getReceiverUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getGiftType();
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
	public Timestamp value6() {
		return getTimeOfEntry();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getRewardId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value8() {
		return getCollected();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value9() {
		return getMinutesTillExpiration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getReasonForGift();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value2(String value) {
		setGifterUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value3(String value) {
		setReceiverUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value4(String value) {
		setGiftType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value5(Integer value) {
		setStaticDataId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value6(Timestamp value) {
		setTimeOfEntry(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value7(Integer value) {
		setRewardId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value8(Boolean value) {
		setCollected(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value9(Integer value) {
		setMinutesTillExpiration(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord value10(String value) {
		setReasonForGift(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GiftForUserRecord values(String value1, String value2, String value3, String value4, Integer value5, Timestamp value6, Integer value7, Boolean value8, Integer value9, String value10) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
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
		setGiftType(from.getGiftType());
		setStaticDataId(from.getStaticDataId());
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached GiftForUserRecord
	 */
	public GiftForUserRecord() {
		super(GiftForUser.GIFT_FOR_USER);
	}

	/**
	 * Create a detached, initialised GiftForUserRecord
	 */
	public GiftForUserRecord(String id, String gifterUserId, String receiverUserId, String giftType, Integer staticDataId, Timestamp timeOfEntry, Integer rewardId, Boolean collected, Integer minutesTillExpiration, String reasonForGift) {
		super(GiftForUser.GIFT_FOR_USER);

		setValue(0, id);
		setValue(1, gifterUserId);
		setValue(2, receiverUserId);
		setValue(3, giftType);
		setValue(4, staticDataId);
		setValue(5, timeOfEntry);
		setValue(6, rewardId);
		setValue(7, collected);
		setValue(8, minutesTillExpiration);
		setValue(9, reasonForGift);
	}
}
