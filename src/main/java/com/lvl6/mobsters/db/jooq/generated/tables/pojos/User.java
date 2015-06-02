/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUser;

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
@Table(name = "user", schema = "mobsters")
public class User implements IUser {

	private static final long serialVersionUID = 2217423;

	private String    id;
	private String    name;
	private Integer   level;
	private Integer   gems;
	private Integer   cash;
	private Integer   oil;
	private Integer   experience;
	private Integer   tasksCompleted;
	private String    referralCode;
	private Integer   numReferrals;
	private String    udidForHistory;
	private Timestamp lastLogin;
	private Timestamp lastLogout;
	private String    deviceToken;
	private Integer   numBadges;
	private Byte      isFake;
	private Timestamp createTime;
	private Byte      isAdmin;
	private String    apsalarId;
	private Integer   numCoinsRetrievedFromStructs;
	private Integer   numOilRetrievedFromStructs;
	private Integer   numConsecutiveDaysPlayed;
	private String    clanId;
	private Timestamp lastWallPostNotificationTime;
	private Integer   kabamNaid;
	private Byte      hasReceivedFbReward;
	private Integer   numBeginnerSalesPurchased;
	private String    facebookId;
	private Boolean   fbIdSetOnUserCreate;
	private String    gameCenterId;
	private String    udid;
	private Timestamp lastObstacleSpawnedTime;
	private Integer   numObstaclesRemoved;
	private Timestamp lastMiniJobGeneratedTime;
	private Integer   avatarMonsterId;
	private String    email;
	private String    fbData;
	private Timestamp lastFreeBoosterPackTime;
	private Integer   clanHelps;
	private Timestamp lastSecretGiftCollectTime;
	private String    pvpDefendingMessage;
	private Timestamp lastTeamDonateSolicitation;
	private Boolean   boughtRiggedBoosterPack;
	private Integer   salesValue;
	private Timestamp salesLastPurchaseTime;
	private Boolean   salesJumpTwoTiers;
	private Integer   totalStrength;
	private Integer   segmentationGroup;
	private Integer   gachaCredits;
	private Timestamp lastTangoGiftSentTime;
	private String    tangoId;

	public User() {}

	public User(User value) {
		this.id = value.id;
		this.name = value.name;
		this.level = value.level;
		this.gems = value.gems;
		this.cash = value.cash;
		this.oil = value.oil;
		this.experience = value.experience;
		this.tasksCompleted = value.tasksCompleted;
		this.referralCode = value.referralCode;
		this.numReferrals = value.numReferrals;
		this.udidForHistory = value.udidForHistory;
		this.lastLogin = value.lastLogin;
		this.lastLogout = value.lastLogout;
		this.deviceToken = value.deviceToken;
		this.numBadges = value.numBadges;
		this.isFake = value.isFake;
		this.createTime = value.createTime;
		this.isAdmin = value.isAdmin;
		this.apsalarId = value.apsalarId;
		this.numCoinsRetrievedFromStructs = value.numCoinsRetrievedFromStructs;
		this.numOilRetrievedFromStructs = value.numOilRetrievedFromStructs;
		this.numConsecutiveDaysPlayed = value.numConsecutiveDaysPlayed;
		this.clanId = value.clanId;
		this.lastWallPostNotificationTime = value.lastWallPostNotificationTime;
		this.kabamNaid = value.kabamNaid;
		this.hasReceivedFbReward = value.hasReceivedFbReward;
		this.numBeginnerSalesPurchased = value.numBeginnerSalesPurchased;
		this.facebookId = value.facebookId;
		this.fbIdSetOnUserCreate = value.fbIdSetOnUserCreate;
		this.gameCenterId = value.gameCenterId;
		this.udid = value.udid;
		this.lastObstacleSpawnedTime = value.lastObstacleSpawnedTime;
		this.numObstaclesRemoved = value.numObstaclesRemoved;
		this.lastMiniJobGeneratedTime = value.lastMiniJobGeneratedTime;
		this.avatarMonsterId = value.avatarMonsterId;
		this.email = value.email;
		this.fbData = value.fbData;
		this.lastFreeBoosterPackTime = value.lastFreeBoosterPackTime;
		this.clanHelps = value.clanHelps;
		this.lastSecretGiftCollectTime = value.lastSecretGiftCollectTime;
		this.pvpDefendingMessage = value.pvpDefendingMessage;
		this.lastTeamDonateSolicitation = value.lastTeamDonateSolicitation;
		this.boughtRiggedBoosterPack = value.boughtRiggedBoosterPack;
		this.salesValue = value.salesValue;
		this.salesLastPurchaseTime = value.salesLastPurchaseTime;
		this.salesJumpTwoTiers = value.salesJumpTwoTiers;
		this.totalStrength = value.totalStrength;
		this.segmentationGroup = value.segmentationGroup;
		this.gachaCredits = value.gachaCredits;
		this.lastTangoGiftSentTime = value.lastTangoGiftSentTime;
		this.tangoId = value.tangoId;
	}

	public User(
		String    id,
		String    name,
		Integer   level,
		Integer   gems,
		Integer   cash,
		Integer   oil,
		Integer   experience,
		Integer   tasksCompleted,
		String    referralCode,
		Integer   numReferrals,
		String    udidForHistory,
		Timestamp lastLogin,
		Timestamp lastLogout,
		String    deviceToken,
		Integer   numBadges,
		Byte      isFake,
		Timestamp createTime,
		Byte      isAdmin,
		String    apsalarId,
		Integer   numCoinsRetrievedFromStructs,
		Integer   numOilRetrievedFromStructs,
		Integer   numConsecutiveDaysPlayed,
		String    clanId,
		Timestamp lastWallPostNotificationTime,
		Integer   kabamNaid,
		Byte      hasReceivedFbReward,
		Integer   numBeginnerSalesPurchased,
		String    facebookId,
		Boolean   fbIdSetOnUserCreate,
		String    gameCenterId,
		String    udid,
		Timestamp lastObstacleSpawnedTime,
		Integer   numObstaclesRemoved,
		Timestamp lastMiniJobGeneratedTime,
		Integer   avatarMonsterId,
		String    email,
		String    fbData,
		Timestamp lastFreeBoosterPackTime,
		Integer   clanHelps,
		Timestamp lastSecretGiftCollectTime,
		String    pvpDefendingMessage,
		Timestamp lastTeamDonateSolicitation,
		Boolean   boughtRiggedBoosterPack,
		Integer   salesValue,
		Timestamp salesLastPurchaseTime,
		Boolean   salesJumpTwoTiers,
		Integer   totalStrength,
		Integer   segmentationGroup,
		Integer   gachaCredits,
		Timestamp lastTangoGiftSentTime,
		String    tangoId
	) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.gems = gems;
		this.cash = cash;
		this.oil = oil;
		this.experience = experience;
		this.tasksCompleted = tasksCompleted;
		this.referralCode = referralCode;
		this.numReferrals = numReferrals;
		this.udidForHistory = udidForHistory;
		this.lastLogin = lastLogin;
		this.lastLogout = lastLogout;
		this.deviceToken = deviceToken;
		this.numBadges = numBadges;
		this.isFake = isFake;
		this.createTime = createTime;
		this.isAdmin = isAdmin;
		this.apsalarId = apsalarId;
		this.numCoinsRetrievedFromStructs = numCoinsRetrievedFromStructs;
		this.numOilRetrievedFromStructs = numOilRetrievedFromStructs;
		this.numConsecutiveDaysPlayed = numConsecutiveDaysPlayed;
		this.clanId = clanId;
		this.lastWallPostNotificationTime = lastWallPostNotificationTime;
		this.kabamNaid = kabamNaid;
		this.hasReceivedFbReward = hasReceivedFbReward;
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
		this.facebookId = facebookId;
		this.fbIdSetOnUserCreate = fbIdSetOnUserCreate;
		this.gameCenterId = gameCenterId;
		this.udid = udid;
		this.lastObstacleSpawnedTime = lastObstacleSpawnedTime;
		this.numObstaclesRemoved = numObstaclesRemoved;
		this.lastMiniJobGeneratedTime = lastMiniJobGeneratedTime;
		this.avatarMonsterId = avatarMonsterId;
		this.email = email;
		this.fbData = fbData;
		this.lastFreeBoosterPackTime = lastFreeBoosterPackTime;
		this.clanHelps = clanHelps;
		this.lastSecretGiftCollectTime = lastSecretGiftCollectTime;
		this.pvpDefendingMessage = pvpDefendingMessage;
		this.lastTeamDonateSolicitation = lastTeamDonateSolicitation;
		this.boughtRiggedBoosterPack = boughtRiggedBoosterPack;
		this.salesValue = salesValue;
		this.salesLastPurchaseTime = salesLastPurchaseTime;
		this.salesJumpTwoTiers = salesJumpTwoTiers;
		this.totalStrength = totalStrength;
		this.segmentationGroup = segmentationGroup;
		this.gachaCredits = gachaCredits;
		this.lastTangoGiftSentTime = lastTangoGiftSentTime;
		this.tangoId = tangoId;
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
	public User setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "name", length = 400)
	@Size(max = 400)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public User setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "level", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getLevel() {
		return this.level;
	}

	@Override
	public User setLevel(Integer level) {
		this.level = level;
		return this;
	}

	@Column(name = "gems", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getGems() {
		return this.gems;
	}

	@Override
	public User setGems(Integer gems) {
		this.gems = gems;
		return this;
	}

	@Column(name = "cash", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCash() {
		return this.cash;
	}

	@Override
	public User setCash(Integer cash) {
		this.cash = cash;
		return this;
	}

	@Column(name = "oil", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getOil() {
		return this.oil;
	}

	@Override
	public User setOil(Integer oil) {
		this.oil = oil;
		return this;
	}

	@Column(name = "experience", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getExperience() {
		return this.experience;
	}

	@Override
	public User setExperience(Integer experience) {
		this.experience = experience;
		return this;
	}

	@Column(name = "tasks_completed", precision = 10)
	@Override
	public Integer getTasksCompleted() {
		return this.tasksCompleted;
	}

	@Override
	public User setTasksCompleted(Integer tasksCompleted) {
		this.tasksCompleted = tasksCompleted;
		return this;
	}

	@Column(name = "referral_code", unique = true, length = 255)
	@Size(max = 255)
	@Override
	public String getReferralCode() {
		return this.referralCode;
	}

	@Override
	public User setReferralCode(String referralCode) {
		this.referralCode = referralCode;
		return this;
	}

	@Column(name = "num_referrals", precision = 10)
	@Override
	public Integer getNumReferrals() {
		return this.numReferrals;
	}

	@Override
	public User setNumReferrals(Integer numReferrals) {
		this.numReferrals = numReferrals;
		return this;
	}

	@Column(name = "udid_for_history", nullable = false, length = 250)
	@NotNull
	@Size(max = 250)
	@Override
	public String getUdidForHistory() {
		return this.udidForHistory;
	}

	@Override
	public User setUdidForHistory(String udidForHistory) {
		this.udidForHistory = udidForHistory;
		return this;
	}

	@Column(name = "last_login", nullable = false)
	@NotNull
	@Override
	public Timestamp getLastLogin() {
		return this.lastLogin;
	}

	@Override
	public User setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
		return this;
	}

	@Column(name = "last_logout")
	@Override
	public Timestamp getLastLogout() {
		return this.lastLogout;
	}

	@Override
	public User setLastLogout(Timestamp lastLogout) {
		this.lastLogout = lastLogout;
		return this;
	}

	@Column(name = "device_token", length = 90)
	@Size(max = 90)
	@Override
	public String getDeviceToken() {
		return this.deviceToken;
	}

	@Override
	public User setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
		return this;
	}

	@Column(name = "num_badges", precision = 10)
	@Override
	public Integer getNumBadges() {
		return this.numBadges;
	}

	@Override
	public User setNumBadges(Integer numBadges) {
		this.numBadges = numBadges;
		return this;
	}

	@Column(name = "is_fake", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getIsFake() {
		return this.isFake;
	}

	@Override
	public User setIsFake(Byte isFake) {
		this.isFake = isFake;
		return this;
	}

	@Column(name = "create_time")
	@Override
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	@Override
	public User setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		return this;
	}

	@Column(name = "is_admin", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getIsAdmin() {
		return this.isAdmin;
	}

	@Override
	public User setIsAdmin(Byte isAdmin) {
		this.isAdmin = isAdmin;
		return this;
	}

	@Column(name = "apsalar_id", length = 250)
	@Size(max = 250)
	@Override
	public String getApsalarId() {
		return this.apsalarId;
	}

	@Override
	public User setApsalarId(String apsalarId) {
		this.apsalarId = apsalarId;
		return this;
	}

	@Column(name = "num_coins_retrieved_from_structs", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getNumCoinsRetrievedFromStructs() {
		return this.numCoinsRetrievedFromStructs;
	}

	@Override
	public User setNumCoinsRetrievedFromStructs(Integer numCoinsRetrievedFromStructs) {
		this.numCoinsRetrievedFromStructs = numCoinsRetrievedFromStructs;
		return this;
	}

	@Column(name = "num_oil_retrieved_from_structs", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getNumOilRetrievedFromStructs() {
		return this.numOilRetrievedFromStructs;
	}

	@Override
	public User setNumOilRetrievedFromStructs(Integer numOilRetrievedFromStructs) {
		this.numOilRetrievedFromStructs = numOilRetrievedFromStructs;
		return this;
	}

	@Column(name = "num_consecutive_days_played", precision = 10)
	@Override
	public Integer getNumConsecutiveDaysPlayed() {
		return this.numConsecutiveDaysPlayed;
	}

	@Override
	public User setNumConsecutiveDaysPlayed(Integer numConsecutiveDaysPlayed) {
		this.numConsecutiveDaysPlayed = numConsecutiveDaysPlayed;
		return this;
	}

	@Column(name = "clan_id", length = 36)
	@Size(max = 36)
	@Override
	public String getClanId() {
		return this.clanId;
	}

	@Override
	public User setClanId(String clanId) {
		this.clanId = clanId;
		return this;
	}

	@Column(name = "last_wall_post_notification_time")
	@Override
	public Timestamp getLastWallPostNotificationTime() {
		return this.lastWallPostNotificationTime;
	}

	@Override
	public User setLastWallPostNotificationTime(Timestamp lastWallPostNotificationTime) {
		this.lastWallPostNotificationTime = lastWallPostNotificationTime;
		return this;
	}

	@Column(name = "kabam_naid", precision = 10)
	@Override
	public Integer getKabamNaid() {
		return this.kabamNaid;
	}

	@Override
	public User setKabamNaid(Integer kabamNaid) {
		this.kabamNaid = kabamNaid;
		return this;
	}

	@Column(name = "has_received_fb_reward", precision = 3)
	@Override
	public Byte getHasReceivedFbReward() {
		return this.hasReceivedFbReward;
	}

	@Override
	public User setHasReceivedFbReward(Byte hasReceivedFbReward) {
		this.hasReceivedFbReward = hasReceivedFbReward;
		return this;
	}

	@Column(name = "num_beginner_sales_purchased", precision = 10)
	@Override
	public Integer getNumBeginnerSalesPurchased() {
		return this.numBeginnerSalesPurchased;
	}

	@Override
	public User setNumBeginnerSalesPurchased(Integer numBeginnerSalesPurchased) {
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
		return this;
	}

	@Column(name = "facebook_id", length = 100)
	@Size(max = 100)
	@Override
	public String getFacebookId() {
		return this.facebookId;
	}

	@Override
	public User setFacebookId(String facebookId) {
		this.facebookId = facebookId;
		return this;
	}

	@Column(name = "fb_id_set_on_user_create", precision = 1)
	@Override
	public Boolean getFbIdSetOnUserCreate() {
		return this.fbIdSetOnUserCreate;
	}

	@Override
	public User setFbIdSetOnUserCreate(Boolean fbIdSetOnUserCreate) {
		this.fbIdSetOnUserCreate = fbIdSetOnUserCreate;
		return this;
	}

	@Column(name = "game_center_id", length = 100)
	@Size(max = 100)
	@Override
	public String getGameCenterId() {
		return this.gameCenterId;
	}

	@Override
	public User setGameCenterId(String gameCenterId) {
		this.gameCenterId = gameCenterId;
		return this;
	}

	@Column(name = "udid", length = 250)
	@Size(max = 250)
	@Override
	public String getUdid() {
		return this.udid;
	}

	@Override
	public User setUdid(String udid) {
		this.udid = udid;
		return this;
	}

	@Column(name = "last_obstacle_spawned_time", nullable = false)
	@NotNull
	@Override
	public Timestamp getLastObstacleSpawnedTime() {
		return this.lastObstacleSpawnedTime;
	}

	@Override
	public User setLastObstacleSpawnedTime(Timestamp lastObstacleSpawnedTime) {
		this.lastObstacleSpawnedTime = lastObstacleSpawnedTime;
		return this;
	}

	@Column(name = "num_obstacles_removed", precision = 10)
	@Override
	public Integer getNumObstaclesRemoved() {
		return this.numObstaclesRemoved;
	}

	@Override
	public User setNumObstaclesRemoved(Integer numObstaclesRemoved) {
		this.numObstaclesRemoved = numObstaclesRemoved;
		return this;
	}

	@Column(name = "last_mini_job_generated_time")
	@Override
	public Timestamp getLastMiniJobGeneratedTime() {
		return this.lastMiniJobGeneratedTime;
	}

	@Override
	public User setLastMiniJobGeneratedTime(Timestamp lastMiniJobGeneratedTime) {
		this.lastMiniJobGeneratedTime = lastMiniJobGeneratedTime;
		return this;
	}

	@Column(name = "avatar_monster_id", precision = 10)
	@Override
	public Integer getAvatarMonsterId() {
		return this.avatarMonsterId;
	}

	@Override
	public User setAvatarMonsterId(Integer avatarMonsterId) {
		this.avatarMonsterId = avatarMonsterId;
		return this;
	}

	@Column(name = "email", length = 255)
	@Size(max = 255)
	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	@Column(name = "fb_data", length = 65535)
	@Size(max = 65535)
	@Override
	public String getFbData() {
		return this.fbData;
	}

	@Override
	public User setFbData(String fbData) {
		this.fbData = fbData;
		return this;
	}

	@Column(name = "last_free_booster_pack_time")
	@Override
	public Timestamp getLastFreeBoosterPackTime() {
		return this.lastFreeBoosterPackTime;
	}

	@Override
	public User setLastFreeBoosterPackTime(Timestamp lastFreeBoosterPackTime) {
		this.lastFreeBoosterPackTime = lastFreeBoosterPackTime;
		return this;
	}

	@Column(name = "clan_helps", precision = 10)
	@Override
	public Integer getClanHelps() {
		return this.clanHelps;
	}

	@Override
	public User setClanHelps(Integer clanHelps) {
		this.clanHelps = clanHelps;
		return this;
	}

	@Column(name = "last_secret_gift_collect_time")
	@Override
	public Timestamp getLastSecretGiftCollectTime() {
		return this.lastSecretGiftCollectTime;
	}

	@Override
	public User setLastSecretGiftCollectTime(Timestamp lastSecretGiftCollectTime) {
		this.lastSecretGiftCollectTime = lastSecretGiftCollectTime;
		return this;
	}

	@Column(name = "pvp_defending_message", length = 65535)
	@Size(max = 65535)
	@Override
	public String getPvpDefendingMessage() {
		return this.pvpDefendingMessage;
	}

	@Override
	public User setPvpDefendingMessage(String pvpDefendingMessage) {
		this.pvpDefendingMessage = pvpDefendingMessage;
		return this;
	}

	@Column(name = "last_team_donate_solicitation")
	@Override
	public Timestamp getLastTeamDonateSolicitation() {
		return this.lastTeamDonateSolicitation;
	}

	@Override
	public User setLastTeamDonateSolicitation(Timestamp lastTeamDonateSolicitation) {
		this.lastTeamDonateSolicitation = lastTeamDonateSolicitation;
		return this;
	}

	@Column(name = "bought_rigged_booster_pack", precision = 1)
	@Override
	public Boolean getBoughtRiggedBoosterPack() {
		return this.boughtRiggedBoosterPack;
	}

	@Override
	public User setBoughtRiggedBoosterPack(Boolean boughtRiggedBoosterPack) {
		this.boughtRiggedBoosterPack = boughtRiggedBoosterPack;
		return this;
	}

	@Column(name = "sales_value", precision = 10)
	@Override
	public Integer getSalesValue() {
		return this.salesValue;
	}

	@Override
	public User setSalesValue(Integer salesValue) {
		this.salesValue = salesValue;
		return this;
	}

	@Column(name = "sales_last_purchase_time")
	@Override
	public Timestamp getSalesLastPurchaseTime() {
		return this.salesLastPurchaseTime;
	}

	@Override
	public User setSalesLastPurchaseTime(Timestamp salesLastPurchaseTime) {
		this.salesLastPurchaseTime = salesLastPurchaseTime;
		return this;
	}

	@Column(name = "sales_jump_two_tiers", precision = 1)
	@Override
	public Boolean getSalesJumpTwoTiers() {
		return this.salesJumpTwoTiers;
	}

	@Override
	public User setSalesJumpTwoTiers(Boolean salesJumpTwoTiers) {
		this.salesJumpTwoTiers = salesJumpTwoTiers;
		return this;
	}

	@Column(name = "total_strength", precision = 10)
	@Override
	public Integer getTotalStrength() {
		return this.totalStrength;
	}

	@Override
	public User setTotalStrength(Integer totalStrength) {
		this.totalStrength = totalStrength;
		return this;
	}

	@Column(name = "segmentation_group", precision = 10)
	@Override
	public Integer getSegmentationGroup() {
		return this.segmentationGroup;
	}

	@Override
	public User setSegmentationGroup(Integer segmentationGroup) {
		this.segmentationGroup = segmentationGroup;
		return this;
	}

	@Column(name = "gacha_credits", precision = 10)
	@Override
	public Integer getGachaCredits() {
		return this.gachaCredits;
	}

	@Override
	public User setGachaCredits(Integer gachaCredits) {
		this.gachaCredits = gachaCredits;
		return this;
	}

	@Column(name = "last_tango_gift_sent_time")
	@Override
	public Timestamp getLastTangoGiftSentTime() {
		return this.lastTangoGiftSentTime;
	}

	@Override
	public User setLastTangoGiftSentTime(Timestamp lastTangoGiftSentTime) {
		this.lastTangoGiftSentTime = lastTangoGiftSentTime;
		return this;
	}

	@Column(name = "tango_id", length = 100)
	@Size(max = 100)
	@Override
	public String getTangoId() {
		return this.tangoId;
	}

	@Override
	public User setTangoId(String tangoId) {
		this.tangoId = tangoId;
		return this;
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUser> E into(E into) {
		into.from(this);
		return into;
	}
}
