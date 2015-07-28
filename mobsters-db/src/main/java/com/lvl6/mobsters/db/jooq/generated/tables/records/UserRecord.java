/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.User;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Record1;
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
@Table(name = "user", schema = "mobsters")
public class UserRecord extends UpdatableRecordImpl<UserRecord> implements IUser {

	private static final long serialVersionUID = 1050380745;

	/**
	 * Setter for <code>mobsters.user.id</code>.
	 */
	@Override
	public UserRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.id</code>.
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
	 * Setter for <code>mobsters.user.name</code>.
	 */
	@Override
	public UserRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.name</code>.
	 */
	@Column(name = "name", length = 400)
	@Size(max = 400)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.user.level</code>.
	 */
	@Override
	public UserRecord setLevel(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.level</code>.
	 */
	@Column(name = "level", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getLevel() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.user.gems</code>.
	 */
	@Override
	public UserRecord setGems(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.gems</code>.
	 */
	@Column(name = "gems", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getGems() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.user.cash</code>. soft currency
	 */
	@Override
	public UserRecord setCash(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.cash</code>. soft currency
	 */
	@Column(name = "cash", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCash() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.user.oil</code>. soft currency
	 */
	@Override
	public UserRecord setOil(Integer value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.oil</code>. soft currency
	 */
	@Column(name = "oil", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getOil() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.user.experience</code>.
	 */
	@Override
	public UserRecord setExperience(Integer value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.experience</code>.
	 */
	@Column(name = "experience", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getExperience() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.user.tasks_completed</code>.
	 */
	@Override
	public UserRecord setTasksCompleted(Integer value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.tasks_completed</code>.
	 */
	@Column(name = "tasks_completed", precision = 10)
	@Override
	public Integer getTasksCompleted() {
		return (Integer) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.user.referral_code</code>.
	 */
	@Override
	public UserRecord setReferralCode(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.referral_code</code>.
	 */
	@Column(name = "referral_code", unique = true, length = 255)
	@Size(max = 255)
	@Override
	public String getReferralCode() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.user.num_referrals</code>.
	 */
	@Override
	public UserRecord setNumReferrals(Integer value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_referrals</code>.
	 */
	@Column(name = "num_referrals", precision = 10)
	@Override
	public Integer getNumReferrals() {
		return (Integer) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.user.udid_for_history</code>.
	 */
	@Override
	public UserRecord setUdidForHistory(String value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.udid_for_history</code>.
	 */
	@Column(name = "udid_for_history", nullable = false, length = 250)
	@NotNull
	@Size(max = 250)
	@Override
	public String getUdidForHistory() {
		return (String) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.user.last_login</code>.
	 */
	@Override
	public UserRecord setLastLogin(Timestamp value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_login</code>.
	 */
	@Column(name = "last_login", nullable = false)
	@NotNull
	@Override
	public Timestamp getLastLogin() {
		return (Timestamp) getValue(11);
	}

	/**
	 * Setter for <code>mobsters.user.last_logout</code>.
	 */
	@Override
	public UserRecord setLastLogout(Timestamp value) {
		setValue(12, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_logout</code>.
	 */
	@Column(name = "last_logout")
	@Override
	public Timestamp getLastLogout() {
		return (Timestamp) getValue(12);
	}

	/**
	 * Setter for <code>mobsters.user.device_token</code>.
	 */
	@Override
	public UserRecord setDeviceToken(String value) {
		setValue(13, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.device_token</code>.
	 */
	@Column(name = "device_token", length = 90)
	@Size(max = 90)
	@Override
	public String getDeviceToken() {
		return (String) getValue(13);
	}

	/**
	 * Setter for <code>mobsters.user.num_badges</code>.
	 */
	@Override
	public UserRecord setNumBadges(Integer value) {
		setValue(14, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_badges</code>.
	 */
	@Column(name = "num_badges", precision = 10)
	@Override
	public Integer getNumBadges() {
		return (Integer) getValue(14);
	}

	/**
	 * Setter for <code>mobsters.user.is_fake</code>.
	 */
	@Override
	public UserRecord setIsFake(Byte value) {
		setValue(15, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.is_fake</code>.
	 */
	@Column(name = "is_fake", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getIsFake() {
		return (Byte) getValue(15);
	}

	/**
	 * Setter for <code>mobsters.user.create_time</code>.
	 */
	@Override
	public UserRecord setCreateTime(Timestamp value) {
		setValue(16, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.create_time</code>.
	 */
	@Column(name = "create_time")
	@Override
	public Timestamp getCreateTime() {
		return (Timestamp) getValue(16);
	}

	/**
	 * Setter for <code>mobsters.user.is_admin</code>.
	 */
	@Override
	public UserRecord setIsAdmin(Byte value) {
		setValue(17, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.is_admin</code>.
	 */
	@Column(name = "is_admin", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getIsAdmin() {
		return (Byte) getValue(17);
	}

	/**
	 * Setter for <code>mobsters.user.apsalar_id</code>.
	 */
	@Override
	public UserRecord setApsalarId(String value) {
		setValue(18, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.apsalar_id</code>.
	 */
	@Column(name = "apsalar_id", length = 250)
	@Size(max = 250)
	@Override
	public String getApsalarId() {
		return (String) getValue(18);
	}

	/**
	 * Setter for <code>mobsters.user.num_coins_retrieved_from_structs</code>.
	 */
	@Override
	public UserRecord setNumCoinsRetrievedFromStructs(Integer value) {
		setValue(19, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_coins_retrieved_from_structs</code>.
	 */
	@Column(name = "num_coins_retrieved_from_structs", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getNumCoinsRetrievedFromStructs() {
		return (Integer) getValue(19);
	}

	/**
	 * Setter for <code>mobsters.user.num_oil_retrieved_from_structs</code>.
	 */
	@Override
	public UserRecord setNumOilRetrievedFromStructs(Integer value) {
		setValue(20, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_oil_retrieved_from_structs</code>.
	 */
	@Column(name = "num_oil_retrieved_from_structs", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getNumOilRetrievedFromStructs() {
		return (Integer) getValue(20);
	}

	/**
	 * Setter for <code>mobsters.user.num_consecutive_days_played</code>.
	 */
	@Override
	public UserRecord setNumConsecutiveDaysPlayed(Integer value) {
		setValue(21, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_consecutive_days_played</code>.
	 */
	@Column(name = "num_consecutive_days_played", precision = 10)
	@Override
	public Integer getNumConsecutiveDaysPlayed() {
		return (Integer) getValue(21);
	}

	/**
	 * Setter for <code>mobsters.user.clan_id</code>.
	 */
	@Override
	public UserRecord setClanId(String value) {
		setValue(22, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.clan_id</code>.
	 */
	@Column(name = "clan_id", length = 36)
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(22);
	}

	/**
	 * Setter for <code>mobsters.user.last_wall_post_notification_time</code>.
	 */
	@Override
	public UserRecord setLastWallPostNotificationTime(Timestamp value) {
		setValue(23, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_wall_post_notification_time</code>.
	 */
	@Column(name = "last_wall_post_notification_time")
	@Override
	public Timestamp getLastWallPostNotificationTime() {
		return (Timestamp) getValue(23);
	}

	/**
	 * Setter for <code>mobsters.user.kabam_naid</code>.
	 */
	@Override
	public UserRecord setKabamNaid(Integer value) {
		setValue(24, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.kabam_naid</code>.
	 */
	@Column(name = "kabam_naid", precision = 10)
	@Override
	public Integer getKabamNaid() {
		return (Integer) getValue(24);
	}

	/**
	 * Setter for <code>mobsters.user.has_received_fb_reward</code>.
	 */
	@Override
	public UserRecord setHasReceivedFbReward(Byte value) {
		setValue(25, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.has_received_fb_reward</code>.
	 */
	@Column(name = "has_received_fb_reward", precision = 3)
	@Override
	public Byte getHasReceivedFbReward() {
		return (Byte) getValue(25);
	}

	/**
	 * Setter for <code>mobsters.user.num_beginner_sales_purchased</code>.
	 */
	@Override
	public UserRecord setNumBeginnerSalesPurchased(Integer value) {
		setValue(26, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_beginner_sales_purchased</code>.
	 */
	@Column(name = "num_beginner_sales_purchased", precision = 10)
	@Override
	public Integer getNumBeginnerSalesPurchased() {
		return (Integer) getValue(26);
	}

	/**
	 * Setter for <code>mobsters.user.facebook_id</code>.
	 */
	@Override
	public UserRecord setFacebookId(String value) {
		setValue(27, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.facebook_id</code>.
	 */
	@Column(name = "facebook_id", length = 100)
	@Size(max = 100)
	@Override
	public String getFacebookId() {
		return (String) getValue(27);
	}

	/**
	 * Setter for <code>mobsters.user.fb_id_set_on_user_create</code>.
	 */
	@Override
	public UserRecord setFbIdSetOnUserCreate(Boolean value) {
		setValue(28, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.fb_id_set_on_user_create</code>.
	 */
	@Column(name = "fb_id_set_on_user_create", precision = 1)
	@Override
	public Boolean getFbIdSetOnUserCreate() {
		return (Boolean) getValue(28);
	}

	/**
	 * Setter for <code>mobsters.user.game_center_id</code>.
	 */
	@Override
	public UserRecord setGameCenterId(String value) {
		setValue(29, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.game_center_id</code>.
	 */
	@Column(name = "game_center_id", length = 100)
	@Size(max = 100)
	@Override
	public String getGameCenterId() {
		return (String) getValue(29);
	}

	/**
	 * Setter for <code>mobsters.user.udid</code>.
	 */
	@Override
	public UserRecord setUdid(String value) {
		setValue(30, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.udid</code>.
	 */
	@Column(name = "udid", length = 250)
	@Size(max = 250)
	@Override
	public String getUdid() {
		return (String) getValue(30);
	}

	/**
	 * Setter for <code>mobsters.user.last_obstacle_spawned_time</code>.
	 */
	@Override
	public UserRecord setLastObstacleSpawnedTime(Timestamp value) {
		setValue(31, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_obstacle_spawned_time</code>.
	 */
	@Column(name = "last_obstacle_spawned_time", nullable = false)
	@NotNull
	@Override
	public Timestamp getLastObstacleSpawnedTime() {
		return (Timestamp) getValue(31);
	}

	/**
	 * Setter for <code>mobsters.user.num_obstacles_removed</code>.
	 */
	@Override
	public UserRecord setNumObstaclesRemoved(Integer value) {
		setValue(32, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.num_obstacles_removed</code>.
	 */
	@Column(name = "num_obstacles_removed", precision = 10)
	@Override
	public Integer getNumObstaclesRemoved() {
		return (Integer) getValue(32);
	}

	/**
	 * Setter for <code>mobsters.user.last_mini_job_generated_time</code>.
	 */
	@Override
	public UserRecord setLastMiniJobGeneratedTime(Timestamp value) {
		setValue(33, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_mini_job_generated_time</code>.
	 */
	@Column(name = "last_mini_job_generated_time")
	@Override
	public Timestamp getLastMiniJobGeneratedTime() {
		return (Timestamp) getValue(33);
	}

	/**
	 * Setter for <code>mobsters.user.avatar_monster_id</code>.
	 */
	@Override
	public UserRecord setAvatarMonsterId(Integer value) {
		setValue(34, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.avatar_monster_id</code>.
	 */
	@Column(name = "avatar_monster_id", precision = 10)
	@Override
	public Integer getAvatarMonsterId() {
		return (Integer) getValue(34);
	}

	/**
	 * Setter for <code>mobsters.user.email</code>.
	 */
	@Override
	public UserRecord setEmail(String value) {
		setValue(35, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.email</code>.
	 */
	@Column(name = "email", length = 255)
	@Size(max = 255)
	@Override
	public String getEmail() {
		return (String) getValue(35);
	}

	/**
	 * Setter for <code>mobsters.user.fb_data</code>.
	 */
	@Override
	public UserRecord setFbData(String value) {
		setValue(36, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.fb_data</code>.
	 */
	@Column(name = "fb_data", length = 65535)
	@Size(max = 65535)
	@Override
	public String getFbData() {
		return (String) getValue(36);
	}

	/**
	 * Setter for <code>mobsters.user.last_free_booster_pack_time</code>.
	 */
	@Override
	public UserRecord setLastFreeBoosterPackTime(Timestamp value) {
		setValue(37, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_free_booster_pack_time</code>.
	 */
	@Column(name = "last_free_booster_pack_time")
	@Override
	public Timestamp getLastFreeBoosterPackTime() {
		return (Timestamp) getValue(37);
	}

	/**
	 * Setter for <code>mobsters.user.clan_helps</code>.
	 */
	@Override
	public UserRecord setClanHelps(Integer value) {
		setValue(38, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.clan_helps</code>.
	 */
	@Column(name = "clan_helps", precision = 10)
	@Override
	public Integer getClanHelps() {
		return (Integer) getValue(38);
	}

	/**
	 * Setter for <code>mobsters.user.last_secret_gift_collect_time</code>.
	 */
	@Override
	public UserRecord setLastSecretGiftCollectTime(Timestamp value) {
		setValue(39, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_secret_gift_collect_time</code>.
	 */
	@Column(name = "last_secret_gift_collect_time")
	@Override
	public Timestamp getLastSecretGiftCollectTime() {
		return (Timestamp) getValue(39);
	}

	/**
	 * Setter for <code>mobsters.user.pvp_defending_message</code>.
	 */
	@Override
	public UserRecord setPvpDefendingMessage(String value) {
		setValue(40, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.pvp_defending_message</code>.
	 */
	@Column(name = "pvp_defending_message", length = 65535)
	@Size(max = 65535)
	@Override
	public String getPvpDefendingMessage() {
		return (String) getValue(40);
	}

	/**
	 * Setter for <code>mobsters.user.last_team_donate_solicitation</code>.
	 */
	@Override
	public UserRecord setLastTeamDonateSolicitation(Timestamp value) {
		setValue(41, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_team_donate_solicitation</code>.
	 */
	@Column(name = "last_team_donate_solicitation")
	@Override
	public Timestamp getLastTeamDonateSolicitation() {
		return (Timestamp) getValue(41);
	}

	/**
	 * Setter for <code>mobsters.user.bought_rigged_booster_pack</code>.
	 */
	@Override
	public UserRecord setBoughtRiggedBoosterPack(Boolean value) {
		setValue(42, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.bought_rigged_booster_pack</code>.
	 */
	@Column(name = "bought_rigged_booster_pack", precision = 1)
	@Override
	public Boolean getBoughtRiggedBoosterPack() {
		return (Boolean) getValue(42);
	}

	/**
	 * Setter for <code>mobsters.user.sales_value</code>. 0: never bought anything
1: has bought a 4.99
2: has bought a 9.99
3: has bought a 19.99
4: has bought a 49.99
5: has bought a 99.99
	 */
	@Override
	public UserRecord setSalesValue(Integer value) {
		setValue(43, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.sales_value</code>. 0: never bought anything
1: has bought a 4.99
2: has bought a 9.99
3: has bought a 19.99
4: has bought a 49.99
5: has bought a 99.99
	 */
	@Column(name = "sales_value", precision = 10)
	@Override
	public Integer getSalesValue() {
		return (Integer) getValue(43);
	}

	/**
	 * Setter for <code>mobsters.user.sales_last_purchase_time</code>.
	 */
	@Override
	public UserRecord setSalesLastPurchaseTime(Timestamp value) {
		setValue(44, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.sales_last_purchase_time</code>.
	 */
	@Column(name = "sales_last_purchase_time")
	@Override
	public Timestamp getSalesLastPurchaseTime() {
		return (Timestamp) getValue(44);
	}

	/**
	 * Setter for <code>mobsters.user.sales_jump_two_tiers</code>.
	 */
	@Override
	public UserRecord setSalesJumpTwoTiers(Boolean value) {
		setValue(45, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.sales_jump_two_tiers</code>.
	 */
	@Column(name = "sales_jump_two_tiers", precision = 1)
	@Override
	public Boolean getSalesJumpTwoTiers() {
		return (Boolean) getValue(45);
	}

	/**
	 * Setter for <code>mobsters.user.total_strength</code>.
	 */
	@Override
	public UserRecord setTotalStrength(Integer value) {
		setValue(46, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.total_strength</code>.
	 */
	@Column(name = "total_strength", precision = 10)
	@Override
	public Integer getTotalStrength() {
		return (Integer) getValue(46);
	}

	/**
	 * Setter for <code>mobsters.user.segmentation_group</code>. starter/builder pack: alex/arin/ashwin/byron vs cooper
	group numbers 1-50:cooper,  51-100: alex

	 */
	@Override
	public UserRecord setSegmentationGroup(Integer value) {
		setValue(47, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.segmentation_group</code>. starter/builder pack: alex/arin/ashwin/byron vs cooper
	group numbers 1-50:cooper,  51-100: alex

	 */
	@Column(name = "segmentation_group", precision = 10)
	@Override
	public Integer getSegmentationGroup() {
		return (Integer) getValue(47);
	}

	/**
	 * Setter for <code>mobsters.user.gacha_credits</code>.
	 */
	@Override
	public UserRecord setGachaCredits(Integer value) {
		setValue(48, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.gacha_credits</code>.
	 */
	@Column(name = "gacha_credits", precision = 10)
	@Override
	public Integer getGachaCredits() {
		return (Integer) getValue(48);
	}

	/**
	 * Setter for <code>mobsters.user.last_tango_gift_sent_time</code>.
	 */
	@Override
	public UserRecord setLastTangoGiftSentTime(Timestamp value) {
		setValue(49, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.last_tango_gift_sent_time</code>.
	 */
	@Column(name = "last_tango_gift_sent_time")
	@Override
	public Timestamp getLastTangoGiftSentTime() {
		return (Timestamp) getValue(49);
	}

	/**
	 * Setter for <code>mobsters.user.tango_id</code>.
	 */
	@Override
	public UserRecord setTangoId(String value) {
		setValue(50, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.tango_id</code>.
	 */
	@Column(name = "tango_id", length = 100)
	@Size(max = 100)
	@Override
	public String getTangoId() {
		return (String) getValue(50);
	}

	/**
	 * Setter for <code>mobsters.user.highest_toon_atk</code>. used to scale cake kids
	 */
	@Override
	public UserRecord setHighestToonAtk(Integer value) {
		setValue(51, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.highest_toon_atk</code>. used to scale cake kids
	 */
	@Column(name = "highest_toon_atk", precision = 10)
	@Override
	public Integer getHighestToonAtk() {
		return (Integer) getValue(51);
	}

	/**
	 * Setter for <code>mobsters.user.highest_toon_hp</code>. used to scale cake kids
	 */
	@Override
	public UserRecord setHighestToonHp(Integer value) {
		setValue(52, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user.highest_toon_hp</code>. used to scale cake kids
	 */
	@Column(name = "highest_toon_hp", precision = 10)
	@Override
	public Integer getHighestToonHp() {
		return (Integer) getValue(52);
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
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUser from) {
		setId(from.getId());
		setName(from.getName());
		setLevel(from.getLevel());
		setGems(from.getGems());
		setCash(from.getCash());
		setOil(from.getOil());
		setExperience(from.getExperience());
		setTasksCompleted(from.getTasksCompleted());
		setReferralCode(from.getReferralCode());
		setNumReferrals(from.getNumReferrals());
		setUdidForHistory(from.getUdidForHistory());
		setLastLogin(from.getLastLogin());
		setLastLogout(from.getLastLogout());
		setDeviceToken(from.getDeviceToken());
		setNumBadges(from.getNumBadges());
		setIsFake(from.getIsFake());
		setCreateTime(from.getCreateTime());
		setIsAdmin(from.getIsAdmin());
		setApsalarId(from.getApsalarId());
		setNumCoinsRetrievedFromStructs(from.getNumCoinsRetrievedFromStructs());
		setNumOilRetrievedFromStructs(from.getNumOilRetrievedFromStructs());
		setNumConsecutiveDaysPlayed(from.getNumConsecutiveDaysPlayed());
		setClanId(from.getClanId());
		setLastWallPostNotificationTime(from.getLastWallPostNotificationTime());
		setKabamNaid(from.getKabamNaid());
		setHasReceivedFbReward(from.getHasReceivedFbReward());
		setNumBeginnerSalesPurchased(from.getNumBeginnerSalesPurchased());
		setFacebookId(from.getFacebookId());
		setFbIdSetOnUserCreate(from.getFbIdSetOnUserCreate());
		setGameCenterId(from.getGameCenterId());
		setUdid(from.getUdid());
		setLastObstacleSpawnedTime(from.getLastObstacleSpawnedTime());
		setNumObstaclesRemoved(from.getNumObstaclesRemoved());
		setLastMiniJobGeneratedTime(from.getLastMiniJobGeneratedTime());
		setAvatarMonsterId(from.getAvatarMonsterId());
		setEmail(from.getEmail());
		setFbData(from.getFbData());
		setLastFreeBoosterPackTime(from.getLastFreeBoosterPackTime());
		setClanHelps(from.getClanHelps());
		setLastSecretGiftCollectTime(from.getLastSecretGiftCollectTime());
		setPvpDefendingMessage(from.getPvpDefendingMessage());
		setLastTeamDonateSolicitation(from.getLastTeamDonateSolicitation());
		setBoughtRiggedBoosterPack(from.getBoughtRiggedBoosterPack());
		setSalesValue(from.getSalesValue());
		setSalesLastPurchaseTime(from.getSalesLastPurchaseTime());
		setSalesJumpTwoTiers(from.getSalesJumpTwoTiers());
		setTotalStrength(from.getTotalStrength());
		setSegmentationGroup(from.getSegmentationGroup());
		setGachaCredits(from.getGachaCredits());
		setLastTangoGiftSentTime(from.getLastTangoGiftSentTime());
		setTangoId(from.getTangoId());
		setHighestToonAtk(from.getHighestToonAtk());
		setHighestToonHp(from.getHighestToonHp());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached UserRecord
	 */
	public UserRecord() {
		super(User.USER);
	}

	/**
	 * Create a detached, initialised UserRecord
	 */
	public UserRecord(String id, String name, Integer level, Integer gems, Integer cash, Integer oil, Integer experience, Integer tasksCompleted, String referralCode, Integer numReferrals, String udidForHistory, Timestamp lastLogin, Timestamp lastLogout, String deviceToken, Integer numBadges, Byte isFake, Timestamp createTime, Byte isAdmin, String apsalarId, Integer numCoinsRetrievedFromStructs, Integer numOilRetrievedFromStructs, Integer numConsecutiveDaysPlayed, String clanId, Timestamp lastWallPostNotificationTime, Integer kabamNaid, Byte hasReceivedFbReward, Integer numBeginnerSalesPurchased, String facebookId, Boolean fbIdSetOnUserCreate, String gameCenterId, String udid, Timestamp lastObstacleSpawnedTime, Integer numObstaclesRemoved, Timestamp lastMiniJobGeneratedTime, Integer avatarMonsterId, String email, String fbData, Timestamp lastFreeBoosterPackTime, Integer clanHelps, Timestamp lastSecretGiftCollectTime, String pvpDefendingMessage, Timestamp lastTeamDonateSolicitation, Boolean boughtRiggedBoosterPack, Integer salesValue, Timestamp salesLastPurchaseTime, Boolean salesJumpTwoTiers, Integer totalStrength, Integer segmentationGroup, Integer gachaCredits, Timestamp lastTangoGiftSentTime, String tangoId, Integer highestToonAtk, Integer highestToonHp) {
		super(User.USER);

		setValue(0, id);
		setValue(1, name);
		setValue(2, level);
		setValue(3, gems);
		setValue(4, cash);
		setValue(5, oil);
		setValue(6, experience);
		setValue(7, tasksCompleted);
		setValue(8, referralCode);
		setValue(9, numReferrals);
		setValue(10, udidForHistory);
		setValue(11, lastLogin);
		setValue(12, lastLogout);
		setValue(13, deviceToken);
		setValue(14, numBadges);
		setValue(15, isFake);
		setValue(16, createTime);
		setValue(17, isAdmin);
		setValue(18, apsalarId);
		setValue(19, numCoinsRetrievedFromStructs);
		setValue(20, numOilRetrievedFromStructs);
		setValue(21, numConsecutiveDaysPlayed);
		setValue(22, clanId);
		setValue(23, lastWallPostNotificationTime);
		setValue(24, kabamNaid);
		setValue(25, hasReceivedFbReward);
		setValue(26, numBeginnerSalesPurchased);
		setValue(27, facebookId);
		setValue(28, fbIdSetOnUserCreate);
		setValue(29, gameCenterId);
		setValue(30, udid);
		setValue(31, lastObstacleSpawnedTime);
		setValue(32, numObstaclesRemoved);
		setValue(33, lastMiniJobGeneratedTime);
		setValue(34, avatarMonsterId);
		setValue(35, email);
		setValue(36, fbData);
		setValue(37, lastFreeBoosterPackTime);
		setValue(38, clanHelps);
		setValue(39, lastSecretGiftCollectTime);
		setValue(40, pvpDefendingMessage);
		setValue(41, lastTeamDonateSolicitation);
		setValue(42, boughtRiggedBoosterPack);
		setValue(43, salesValue);
		setValue(44, salesLastPurchaseTime);
		setValue(45, salesJumpTwoTiers);
		setValue(46, totalStrength);
		setValue(47, segmentationGroup);
		setValue(48, gachaCredits);
		setValue(49, lastTangoGiftSentTime);
		setValue(50, tangoId);
		setValue(51, highestToonAtk);
		setValue(52, highestToonHp);
	}
}