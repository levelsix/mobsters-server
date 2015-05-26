/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;
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
public interface IGiftForUser extends Serializable {

	/**
	 * Setter for <code>mobsters.gift_for_user.id</code>.
	 */
	public IGiftForUser setId(String value);

	/**
	 * Getter for <code>mobsters.gift_for_user.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.gift_for_user.gifter_user_id</code>.
	 */
	public IGiftForUser setGifterUserId(String value);

	/**
	 * Getter for <code>mobsters.gift_for_user.gifter_user_id</code>.
	 */
	@Column(name = "gifter_user_id", length = 36)
	@Size(max = 36)
	public String getGifterUserId();

	/**
	 * Setter for <code>mobsters.gift_for_user.receiver_user_id</code>.
	 */
	public IGiftForUser setReceiverUserId(String value);

	/**
	 * Getter for <code>mobsters.gift_for_user.receiver_user_id</code>.
	 */
	@Column(name = "receiver_user_id", length = 36)
	@Size(max = 36)
	public String getReceiverUserId();

	/**
	 * Setter for <code>mobsters.gift_for_user.gift_type</code>. at the moment: GENERIC_GIFT, CLAN_GIFT, TANGO_GIFT
	 */
	public IGiftForUser setGiftType(String value);

	/**
	 * Getter for <code>mobsters.gift_for_user.gift_type</code>. at the moment: GENERIC_GIFT, CLAN_GIFT, TANGO_GIFT
	 */
	@Column(name = "gift_type", length = 45)
	@Size(max = 45)
	public String getGiftType();

	/**
	 * Setter for <code>mobsters.gift_for_user.static_data_id</code>. at the moment can reference clan_gift_config and tango_gift_config
	 */
	public IGiftForUser setStaticDataId(Integer value);

	/**
	 * Getter for <code>mobsters.gift_for_user.static_data_id</code>. at the moment can reference clan_gift_config and tango_gift_config
	 */
	@Column(name = "static_data_id", precision = 10)
	public Integer getStaticDataId();

	/**
	 * Setter for <code>mobsters.gift_for_user.time_of_entry</code>.
	 */
	public IGiftForUser setTimeOfEntry(Timestamp value);

	/**
	 * Getter for <code>mobsters.gift_for_user.time_of_entry</code>.
	 */
	@Column(name = "time_of_entry")
	public Timestamp getTimeOfEntry();

	/**
	 * Setter for <code>mobsters.gift_for_user.reward_id</code>.
	 */
	public IGiftForUser setRewardId(Integer value);

	/**
	 * Getter for <code>mobsters.gift_for_user.reward_id</code>.
	 */
	@Column(name = "reward_id", precision = 10)
	public Integer getRewardId();

	/**
	 * Setter for <code>mobsters.gift_for_user.collected</code>.
	 */
	public IGiftForUser setCollected(Boolean value);

	/**
	 * Getter for <code>mobsters.gift_for_user.collected</code>.
	 */
	@Column(name = "collected", precision = 1)
	public Boolean getCollected();

	/**
	 * Setter for <code>mobsters.gift_for_user.minutes_till_expiration</code>. this has the lowest expiration priority. Expiration also determined by gift_type
	 */
	public IGiftForUser setMinutesTillExpiration(Integer value);

	/**
	 * Getter for <code>mobsters.gift_for_user.minutes_till_expiration</code>. this has the lowest expiration priority. Expiration also determined by gift_type
	 */
	@Column(name = "minutes_till_expiration", precision = 10)
	public Integer getMinutesTillExpiration();

	/**
	 * Setter for <code>mobsters.gift_for_user.reason_for_gift</code>.
	 */
	public IGiftForUser setReasonForGift(String value);

	/**
	 * Getter for <code>mobsters.gift_for_user.reason_for_gift</code>.
	 */
	@Column(name = "reason_for_gift", length = 75)
	@Size(max = 75)
	public String getReasonForGift();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IGiftForUser
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftForUser from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IGiftForUser
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IGiftForUser> E into(E into);
}
