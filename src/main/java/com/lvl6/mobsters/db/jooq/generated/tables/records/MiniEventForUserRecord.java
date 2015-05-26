/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row;
import org.jooq.Row6;
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
@Table(name = "mini_event_for_user", schema = "mobsters")
public class MiniEventForUserRecord extends UpdatableRecordImpl<MiniEventForUserRecord> implements Record6<String, Integer, Integer, Boolean, Boolean, Boolean>, IMiniEventForUser {

	private static final long serialVersionUID = -1775392961;

	/**
	 * Setter for <code>mobsters.mini_event_for_user.user_id</code>.
	 */
	@Override
	public MiniEventForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.user_id</code>.
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
	 * Setter for <code>mobsters.mini_event_for_user.mini_event_id</code>.
	 */
	@Override
	public MiniEventForUserRecord setMiniEventId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.mini_event_id</code>.
	 */
	@Column(name = "mini_event_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getMiniEventId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.mini_event_for_user.user_lvl</code>. level of the user when he started mini_event
	 */
	@Override
	public MiniEventForUserRecord setUserLvl(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.user_lvl</code>. level of the user when he started mini_event
	 */
	@Column(name = "user_lvl", precision = 10)
	@Override
	public Integer getUserLvl() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.mini_event_for_user.tier_one_redeemed</code>.
	 */
	@Override
	public MiniEventForUserRecord setTierOneRedeemed(Boolean value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.tier_one_redeemed</code>.
	 */
	@Column(name = "tier_one_redeemed", precision = 1)
	@Override
	public Boolean getTierOneRedeemed() {
		return (Boolean) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.mini_event_for_user.tier_two_redeemed</code>.
	 */
	@Override
	public MiniEventForUserRecord setTierTwoRedeemed(Boolean value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.tier_two_redeemed</code>.
	 */
	@Column(name = "tier_two_redeemed", precision = 1)
	@Override
	public Boolean getTierTwoRedeemed() {
		return (Boolean) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.mini_event_for_user.tier_three_redeemed</code>.
	 */
	@Override
	public MiniEventForUserRecord setTierThreeRedeemed(Boolean value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.mini_event_for_user.tier_three_redeemed</code>.
	 */
	@Column(name = "tier_three_redeemed", precision = 1)
	@Override
	public Boolean getTierThreeRedeemed() {
		return (Boolean) getValue(5);
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
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<String, Integer, Integer, Boolean, Boolean, Boolean> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<String, Integer, Integer, Boolean, Boolean, Boolean> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.MINI_EVENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.USER_LVL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field4() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.TIER_ONE_REDEEMED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field5() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.TIER_TWO_REDEEMED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field6() {
		return MiniEventForUser.MINI_EVENT_FOR_USER.TIER_THREE_REDEEMED;
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
		return getMiniEventId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getUserLvl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value4() {
		return getTierOneRedeemed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value5() {
		return getTierTwoRedeemed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value6() {
		return getTierThreeRedeemed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value2(Integer value) {
		setMiniEventId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value3(Integer value) {
		setUserLvl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value4(Boolean value) {
		setTierOneRedeemed(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value5(Boolean value) {
		setTierTwoRedeemed(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord value6(Boolean value) {
		setTierThreeRedeemed(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MiniEventForUserRecord values(String value1, Integer value2, Integer value3, Boolean value4, Boolean value5, Boolean value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventForUser from) {
		setUserId(from.getUserId());
		setMiniEventId(from.getMiniEventId());
		setUserLvl(from.getUserLvl());
		setTierOneRedeemed(from.getTierOneRedeemed());
		setTierTwoRedeemed(from.getTierTwoRedeemed());
		setTierThreeRedeemed(from.getTierThreeRedeemed());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MiniEventForUserRecord
	 */
	public MiniEventForUserRecord() {
		super(MiniEventForUser.MINI_EVENT_FOR_USER);
	}

	/**
	 * Create a detached, initialised MiniEventForUserRecord
	 */
	public MiniEventForUserRecord(String userId, Integer miniEventId, Integer userLvl, Boolean tierOneRedeemed, Boolean tierTwoRedeemed, Boolean tierThreeRedeemed) {
		super(MiniEventForUser.MINI_EVENT_FOR_USER);

		setValue(0, userId);
		setValue(1, miniEventId);
		setValue(2, userLvl);
		setValue(3, tierOneRedeemed);
		setValue(4, tierTwoRedeemed);
		setValue(5, tierThreeRedeemed);
	}
}
