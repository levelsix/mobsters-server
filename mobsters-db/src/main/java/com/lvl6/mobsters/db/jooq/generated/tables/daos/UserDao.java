/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.User;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.UserRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class UserDao extends DAOImpl<UserRecord, UserPojo, String> {

	/**
	 * Create a new UserDao without any configuration
	 */
	public UserDao() {
		super(User.USER, UserPojo.class);
	}

	/**
	 * Create a new UserDao with an attached configuration
	 */
	public UserDao(Configuration configuration) {
		super(User.USER, UserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(UserPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<UserPojo> fetchById(String... values) {
		return fetch(User.USER.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public UserPojo fetchOneById(String value) {
		return fetchOne(User.USER.ID, value);
	}

	/**
	 * Fetch records that have <code>name IN (values)</code>
	 */
	public List<UserPojo> fetchByName(String... values) {
		return fetch(User.USER.NAME, values);
	}

	/**
	 * Fetch records that have <code>level IN (values)</code>
	 */
	public List<UserPojo> fetchByLevel(Integer... values) {
		return fetch(User.USER.LEVEL, values);
	}

	/**
	 * Fetch records that have <code>gems IN (values)</code>
	 */
	public List<UserPojo> fetchByGems(Integer... values) {
		return fetch(User.USER.GEMS, values);
	}

	/**
	 * Fetch records that have <code>cash IN (values)</code>
	 */
	public List<UserPojo> fetchByCash(Integer... values) {
		return fetch(User.USER.CASH, values);
	}

	/**
	 * Fetch records that have <code>oil IN (values)</code>
	 */
	public List<UserPojo> fetchByOil(Integer... values) {
		return fetch(User.USER.OIL, values);
	}

	/**
	 * Fetch records that have <code>experience IN (values)</code>
	 */
	public List<UserPojo> fetchByExperience(Integer... values) {
		return fetch(User.USER.EXPERIENCE, values);
	}

	/**
	 * Fetch records that have <code>tasks_completed IN (values)</code>
	 */
	public List<UserPojo> fetchByTasksCompleted(Integer... values) {
		return fetch(User.USER.TASKS_COMPLETED, values);
	}

	/**
	 * Fetch records that have <code>referral_code IN (values)</code>
	 */
	public List<UserPojo> fetchByReferralCode(String... values) {
		return fetch(User.USER.REFERRAL_CODE, values);
	}

	/**
	 * Fetch a unique record that has <code>referral_code = value</code>
	 */
	public UserPojo fetchOneByReferralCode(String value) {
		return fetchOne(User.USER.REFERRAL_CODE, value);
	}

	/**
	 * Fetch records that have <code>num_referrals IN (values)</code>
	 */
	public List<UserPojo> fetchByNumReferrals(Integer... values) {
		return fetch(User.USER.NUM_REFERRALS, values);
	}

	/**
	 * Fetch records that have <code>udid_for_history IN (values)</code>
	 */
	public List<UserPojo> fetchByUdidForHistory(String... values) {
		return fetch(User.USER.UDID_FOR_HISTORY, values);
	}

	/**
	 * Fetch records that have <code>last_login IN (values)</code>
	 */
	public List<UserPojo> fetchByLastLogin(Timestamp... values) {
		return fetch(User.USER.LAST_LOGIN, values);
	}

	/**
	 * Fetch records that have <code>last_logout IN (values)</code>
	 */
	public List<UserPojo> fetchByLastLogout(Timestamp... values) {
		return fetch(User.USER.LAST_LOGOUT, values);
	}

	/**
	 * Fetch records that have <code>device_token IN (values)</code>
	 */
	public List<UserPojo> fetchByDeviceToken(String... values) {
		return fetch(User.USER.DEVICE_TOKEN, values);
	}

	/**
	 * Fetch records that have <code>num_badges IN (values)</code>
	 */
	public List<UserPojo> fetchByNumBadges(Integer... values) {
		return fetch(User.USER.NUM_BADGES, values);
	}

	/**
	 * Fetch records that have <code>is_fake IN (values)</code>
	 */
	public List<UserPojo> fetchByIsFake(Byte... values) {
		return fetch(User.USER.IS_FAKE, values);
	}

	/**
	 * Fetch records that have <code>create_time IN (values)</code>
	 */
	public List<UserPojo> fetchByCreateTime(Timestamp... values) {
		return fetch(User.USER.CREATE_TIME, values);
	}

	/**
	 * Fetch records that have <code>is_admin IN (values)</code>
	 */
	public List<UserPojo> fetchByIsAdmin(Byte... values) {
		return fetch(User.USER.IS_ADMIN, values);
	}

	/**
	 * Fetch records that have <code>apsalar_id IN (values)</code>
	 */
	public List<UserPojo> fetchByApsalarId(String... values) {
		return fetch(User.USER.APSALAR_ID, values);
	}

	/**
	 * Fetch records that have <code>num_coins_retrieved_from_structs IN (values)</code>
	 */
	public List<UserPojo> fetchByNumCoinsRetrievedFromStructs(Integer... values) {
		return fetch(User.USER.NUM_COINS_RETRIEVED_FROM_STRUCTS, values);
	}

	/**
	 * Fetch records that have <code>num_oil_retrieved_from_structs IN (values)</code>
	 */
	public List<UserPojo> fetchByNumOilRetrievedFromStructs(Integer... values) {
		return fetch(User.USER.NUM_OIL_RETRIEVED_FROM_STRUCTS, values);
	}

	/**
	 * Fetch records that have <code>num_consecutive_days_played IN (values)</code>
	 */
	public List<UserPojo> fetchByNumConsecutiveDaysPlayed(Integer... values) {
		return fetch(User.USER.NUM_CONSECUTIVE_DAYS_PLAYED, values);
	}

	/**
	 * Fetch records that have <code>clan_id IN (values)</code>
	 */
	public List<UserPojo> fetchByClanId(String... values) {
		return fetch(User.USER.CLAN_ID, values);
	}

	/**
	 * Fetch records that have <code>last_wall_post_notification_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastWallPostNotificationTime(Timestamp... values) {
		return fetch(User.USER.LAST_WALL_POST_NOTIFICATION_TIME, values);
	}

	/**
	 * Fetch records that have <code>kabam_naid IN (values)</code>
	 */
	public List<UserPojo> fetchByKabamNaid(Integer... values) {
		return fetch(User.USER.KABAM_NAID, values);
	}

	/**
	 * Fetch records that have <code>has_received_fb_reward IN (values)</code>
	 */
	public List<UserPojo> fetchByHasReceivedFbReward(Byte... values) {
		return fetch(User.USER.HAS_RECEIVED_FB_REWARD, values);
	}

	/**
	 * Fetch records that have <code>num_beginner_sales_purchased IN (values)</code>
	 */
	public List<UserPojo> fetchByNumBeginnerSalesPurchased(Integer... values) {
		return fetch(User.USER.NUM_BEGINNER_SALES_PURCHASED, values);
	}

	/**
	 * Fetch records that have <code>facebook_id IN (values)</code>
	 */
	public List<UserPojo> fetchByFacebookId(String... values) {
		return fetch(User.USER.FACEBOOK_ID, values);
	}

	/**
	 * Fetch records that have <code>fb_id_set_on_user_create IN (values)</code>
	 */
	public List<UserPojo> fetchByFbIdSetOnUserCreate(Boolean... values) {
		return fetch(User.USER.FB_ID_SET_ON_USER_CREATE, values);
	}

	/**
	 * Fetch records that have <code>game_center_id IN (values)</code>
	 */
	public List<UserPojo> fetchByGameCenterId(String... values) {
		return fetch(User.USER.GAME_CENTER_ID, values);
	}

	/**
	 * Fetch records that have <code>udid IN (values)</code>
	 */
	public List<UserPojo> fetchByUdid(String... values) {
		return fetch(User.USER.UDID, values);
	}

	/**
	 * Fetch records that have <code>last_obstacle_spawned_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastObstacleSpawnedTime(Timestamp... values) {
		return fetch(User.USER.LAST_OBSTACLE_SPAWNED_TIME, values);
	}

	/**
	 * Fetch records that have <code>num_obstacles_removed IN (values)</code>
	 */
	public List<UserPojo> fetchByNumObstaclesRemoved(Integer... values) {
		return fetch(User.USER.NUM_OBSTACLES_REMOVED, values);
	}

	/**
	 * Fetch records that have <code>last_mini_job_generated_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastMiniJobGeneratedTime(Timestamp... values) {
		return fetch(User.USER.LAST_MINI_JOB_GENERATED_TIME, values);
	}

	/**
	 * Fetch records that have <code>avatar_monster_id IN (values)</code>
	 */
	public List<UserPojo> fetchByAvatarMonsterId(Integer... values) {
		return fetch(User.USER.AVATAR_MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>email IN (values)</code>
	 */
	public List<UserPojo> fetchByEmail(String... values) {
		return fetch(User.USER.EMAIL, values);
	}

	/**
	 * Fetch records that have <code>fb_data IN (values)</code>
	 */
	public List<UserPojo> fetchByFbData(String... values) {
		return fetch(User.USER.FB_DATA, values);
	}

	/**
	 * Fetch records that have <code>last_free_booster_pack_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastFreeBoosterPackTime(Timestamp... values) {
		return fetch(User.USER.LAST_FREE_BOOSTER_PACK_TIME, values);
	}

	/**
	 * Fetch records that have <code>clan_helps IN (values)</code>
	 */
	public List<UserPojo> fetchByClanHelps(Integer... values) {
		return fetch(User.USER.CLAN_HELPS, values);
	}

	/**
	 * Fetch records that have <code>last_secret_gift_collect_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastSecretGiftCollectTime(Timestamp... values) {
		return fetch(User.USER.LAST_SECRET_GIFT_COLLECT_TIME, values);
	}

	/**
	 * Fetch records that have <code>pvp_defending_message IN (values)</code>
	 */
	public List<UserPojo> fetchByPvpDefendingMessage(String... values) {
		return fetch(User.USER.PVP_DEFENDING_MESSAGE, values);
	}

	/**
	 * Fetch records that have <code>last_team_donate_solicitation IN (values)</code>
	 */
	public List<UserPojo> fetchByLastTeamDonateSolicitation(Timestamp... values) {
		return fetch(User.USER.LAST_TEAM_DONATE_SOLICITATION, values);
	}

	/**
	 * Fetch records that have <code>bought_rigged_booster_pack IN (values)</code>
	 */
	public List<UserPojo> fetchByBoughtRiggedBoosterPack(Boolean... values) {
		return fetch(User.USER.BOUGHT_RIGGED_BOOSTER_PACK, values);
	}

	/**
	 * Fetch records that have <code>sales_value IN (values)</code>
	 */
	public List<UserPojo> fetchBySalesValue(Integer... values) {
		return fetch(User.USER.SALES_VALUE, values);
	}

	/**
	 * Fetch records that have <code>sales_last_purchase_time IN (values)</code>
	 */
	public List<UserPojo> fetchBySalesLastPurchaseTime(Timestamp... values) {
		return fetch(User.USER.SALES_LAST_PURCHASE_TIME, values);
	}

	/**
	 * Fetch records that have <code>sales_jump_two_tiers IN (values)</code>
	 */
	public List<UserPojo> fetchBySalesJumpTwoTiers(Boolean... values) {
		return fetch(User.USER.SALES_JUMP_TWO_TIERS, values);
	}

	/**
	 * Fetch records that have <code>total_strength IN (values)</code>
	 */
	public List<UserPojo> fetchByTotalStrength(Integer... values) {
		return fetch(User.USER.TOTAL_STRENGTH, values);
	}

	/**
	 * Fetch records that have <code>segmentation_group IN (values)</code>
	 */
	public List<UserPojo> fetchBySegmentationGroup(Integer... values) {
		return fetch(User.USER.SEGMENTATION_GROUP, values);
	}

	/**
	 * Fetch records that have <code>gacha_credits IN (values)</code>
	 */
	public List<UserPojo> fetchByGachaCredits(Integer... values) {
		return fetch(User.USER.GACHA_CREDITS, values);
	}

	/**
	 * Fetch records that have <code>last_tango_gift_sent_time IN (values)</code>
	 */
	public List<UserPojo> fetchByLastTangoGiftSentTime(Timestamp... values) {
		return fetch(User.USER.LAST_TANGO_GIFT_SENT_TIME, values);
	}

	/**
	 * Fetch records that have <code>tango_id IN (values)</code>
	 */
	public List<UserPojo> fetchByTangoId(String... values) {
		return fetch(User.USER.TANGO_ID, values);
	}

	/**
	 * Fetch records that have <code>highest_toon_atk IN (values)</code>
	 */
	public List<UserPojo> fetchByHighestToonAtk(Integer... values) {
		return fetch(User.USER.HIGHEST_TOON_ATK, values);
	}

	/**
	 * Fetch records that have <code>highest_toon_hp IN (values)</code>
	 */
	public List<UserPojo> fetchByHighestToonHp(Integer... values) {
		return fetch(User.USER.HIGHEST_TOON_HP, values);
	}
}
