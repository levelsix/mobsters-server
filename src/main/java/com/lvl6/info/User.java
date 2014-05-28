package com.lvl6.info;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.InAppPurchaseProto.EarnFreeDiamondsType;
import com.lvl6.utils.DBConnection;

public class User implements Serializable {
	
	private static final long serialVersionUID = -3522735385815495261L;
	
	private int id;
	private String name;
	private int level;
	private int gems;
	private int cash;
	private int oil;
	private int experience;
	private int tasksCompleted;
	private String referralCode;
	private int numReferrals;
	private String udidForHistory;
	private Date lastLogin;
	private Date lastLogout;
	private String deviceToken;
	private int numBadges;
	private boolean isFake;
	private Date createTime;
	private boolean isAdmin;
	private String apsalarId;
	private int numCoinsRetrievedFromStructs;
	private int numOilRetrievedFromStructs;
	private int numConsecutiveDaysPlayed;
	private int clanId;
	private Date lastWallPostNotificationTime;
	private int kabamNaid;
	private boolean hasReceivedfbReward;
	private int numBeginnerSalesPurchased;
	private String facebookId;
	private boolean fbIdSetOnUserCreate;
	private String gameCenterId;
	private String udid;
	private Date lastObstacleSpawnedTime;
	private int numObstaclesRemoved;
	private Date lastMiniJobGeneratedTime;

	public User(int id, String name, int level, int gems, int cash, int oil,
			int experience, int tasksCompleted, String referralCode,
			int numReferrals, String udidForHistory, Date lastLogin,
			Date lastLogout, String deviceToken, int numBadges, boolean isFake,
			Date createTime, boolean isAdmin, String apsalarId,
			int numCoinsRetrievedFromStructs, int numOilRetrievedFromStructs,
			int numConsecutiveDaysPlayed, int clanId,
			Date lastWallPostNotificationTime, int kabamNaid,
			boolean hasReceivedfbReward, int numBeginnerSalesPurchased,
			String facebookId, boolean fbIdSetOnUserCreate,
			String gameCenterId, String udid, Date lastObstacleSpawnedTime,
			int numObstaclesRemoved, Date lastMiniJobGeneratedTime) {
		super();
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
		this.hasReceivedfbReward = hasReceivedfbReward;
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
		this.facebookId = facebookId;
		this.fbIdSetOnUserCreate = fbIdSetOnUserCreate;
		this.gameCenterId = gameCenterId;
		this.udid = udid;
		this.lastObstacleSpawnedTime = lastObstacleSpawnedTime;
		this.numObstaclesRemoved = numObstaclesRemoved;
		this.lastMiniJobGeneratedTime = lastMiniJobGeneratedTime;
	}

	public boolean updateSetdevicetoken(String deviceToken) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__DEVICE_TOKEN, deviceToken);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.deviceToken = deviceToken;
			return true;
		}
		return false;
	}

	public boolean updateSetFacebookId(String facebookId, boolean fbIdSetOnUserCreate) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__FACEBOOK_ID, facebookId);
		absoluteParams.put(DBConstants.USER__FB_ID_SET_ON_USER_CREATE, fbIdSetOnUserCreate);
		
		absoluteParams.put(DBConstants.USER__UDID, null);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.facebookId = facebookId;
			this.fbIdSetOnUserCreate = fbIdSetOnUserCreate;
			this.udid = null;
			return true;
		}
		return false;
	}

	public boolean updateSetKabamNaid(int kabamNaid) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__KABAM_NAID, kabamNaid);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.kabamNaid = kabamNaid;
			return true;
		}
		return false;
	}
	
	public boolean updateGameCenterId(String gameCenterId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__GAME_CENTER_ID, gameCenterId);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gameCenterId = gameCenterId;
			return true;
		}
		return false;
	}
	
//
//	public boolean updateResetNumbadgesSetdevicetoken(String deviceToken) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.USER__ID, id);
//
//		if (deviceToken != null && deviceToken.length() == 0) {
//			deviceToken = null;
//		}
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		absoluteParams.put(DBConstants.USER__NUM_BADGES, 0);
//		absoluteParams.put(DBConstants.USER__DEVICE_TOKEN, deviceToken);
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			this.numBadges = 0;
//			this.deviceToken = deviceToken;
//			return true;
//		}
//		return false;
//	}
//
//	public boolean updateRelativeBadge(int badgeChange) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.USER__ID, id);
//
//		Map <String, Object> relativeParams = new HashMap<String, Object>();
//		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			this.numBadges += badgeChange;
//			return true;
//		}
//		return false;
//	}

//	public boolean updateRelativeBadgeAbsoluteLastBattleNotificationTime(int badgeChange, Timestamp newLastBattleNotificationTime) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.USER__ID, id);
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		absoluteParams.put(DBConstants.USER__LAST_BATTLE_NOTIFICATION_TIME, newLastBattleNotificationTime);
//
//		Map <String, Object> relativeParams = new HashMap<String, Object>();
//		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			this.numBadges += badgeChange;
//			this.lastBattleNotificationTime = newLastBattleNotificationTime;
//			return true;
//		}
//		return false;
//	}
//
//
//	public boolean updateRelativeBadgeAbsoluteLastWallPostNotificationTime(int badgeChange, Timestamp newLastWallNotificationTime) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.USER__ID, id);
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		absoluteParams.put(DBConstants.USER__LAST_WALL_POST_NOTIFICATION_TIME, newLastWallNotificationTime);
//
//		Map <String, Object> relativeParams = new HashMap<String, Object>();
//		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			this.numBadges += badgeChange;
//			this.lastBattleNotificationTime = newLastWallNotificationTime;
//			return true;
//		}
//		return false;
//	}

	public boolean updateAbsoluteApsalaridLastloginBadgesNumConsecutiveDaysLoggedIn(String newApsalarId, Timestamp loginTime, 
			int newBadges, int newNumConsecutiveDaysLoggedIn) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__APSALAR_ID, newApsalarId);
		absoluteParams.put(DBConstants.USER__LAST_LOGIN, loginTime);
		absoluteParams.put(DBConstants.USER__NUM_BADGES, newBadges);
		absoluteParams.put(DBConstants.USER__NUM_CONSECUTIVE_DAYS_PLAYED, newNumConsecutiveDaysLoggedIn);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.apsalarId = newApsalarId;
			this.lastLogin = loginTime;
			this.numBadges = newBadges;
			return true;
		}
		return false;
	}

	public boolean updateLastLogout(Timestamp lastLogout) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		
		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (null != lastLogout) {
			absoluteParams.put(DBConstants.USER__LAST_LOGOUT, lastLogout);
		}
		//don't update anything if empty
		if (absoluteParams.isEmpty()) {
			return true;
		}
		
		
		Map<String, Object> relativeParams = null;
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, 
				relativeParams, absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			if (null != lastLogout) {
				this.lastLogout = lastLogout;
			}
			
			return true;
		}
		return false;
		
	}


	public boolean updateLevel(int levelChange, boolean absoluteUpdate) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map<String, Object> relativeParams = null;
		Map<String, Object> absoluteParams = null;
		if (!absoluteUpdate) {
			relativeParams = new HashMap<String, Object>();
			relativeParams.put(DBConstants.USER__LEVEL, levelChange);
		} else {
			absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.USER__LEVEL, levelChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			if (!absoluteUpdate) {
				this.level += levelChange;
			} else {
				this.level = levelChange;
			}
			return true;
		}
		return false;
	}

	/*
	 * used for purchasing and selling structures, redeeming quests
	 */
	public boolean updateRelativeGemsCashOilExperienceNaive (int gemChange,
			int cashChange, int oilChange, int experienceChange) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map<String, Object> relativeParams = new HashMap<String, Object>();

		if (0 != gemChange) {
			relativeParams.put(DBConstants.USER__GEMS, gemChange);
		}
		if (0 != cashChange) {
			relativeParams.put(DBConstants.USER__CASH, cashChange);
		}
		if (0 != oilChange) {
			relativeParams.put(DBConstants.USER__OIL, oilChange);
		}
		if (0 != experienceChange) {
			relativeParams.put(DBConstants.USER__EXPERIENCE, experienceChange);
		}
		
		if (relativeParams.isEmpty()) {
			return true;
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gems += gemChange;
			this.cash += cashChange;
			this.experience += experienceChange;
			return true;
		}
		return false;
	}

	/*
	 * used for tasks
	 *        * user- coins/exp/tasks_completed increase
	 */
	public boolean updateRelativeCashOilExpTasksCompleted (int expChange, int cashChange,
			int oilChange, int tasksCompletedChange, Timestamp clientTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		relativeParams.put(DBConstants.USER__EXPERIENCE, expChange);
		relativeParams.put(DBConstants.USER__CASH, cashChange);
		relativeParams.put(DBConstants.USER__OIL, oilChange);
		relativeParams.put(DBConstants.USER__TASKS_COMPLETED, tasksCompletedChange);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (absoluteParams.size() == 0) {
			absoluteParams = null;
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.experience += expChange;
			this.cash += cashChange;
			this.oil += oilChange;
			this.tasksCompleted += tasksCompletedChange;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsNumreferrals (int coinChange, int numReferralsChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__CASH, coinChange);
		}
		if (numReferralsChange != 0) {
			relativeParams.put(DBConstants.USER__NUM_REFERRALS, numReferralsChange); 
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.cash += coinChange;
			this.numReferrals += numReferralsChange;
			return true;
		}
		return false;
	}

	/*
	 * used for in app purchases, finishingnormstructbuild, enhancing speedup
	 */
	public boolean updateRelativeGemsNaive (int diamondChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (diamondChange != 0) {
			relativeParams.put(DBConstants.USER__GEMS, diamondChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gems += diamondChange;
			return true;
		}
		return false;
	}
	
	public int updateRelativeCashAndOilAndGems(int cashDelta, int oilDelta, int gemsDelta) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		
		Map<String, Object> relativeParams = new HashMap<String, Object>();
		if (gemsDelta != 0) {
			relativeParams.put(DBConstants.USER__GEMS, gemsDelta);
		}
		if (oilDelta != 0) {
			relativeParams.put(DBConstants.USER__OIL, oilDelta);
		}
		if (cashDelta != 0) {
			relativeParams.put(DBConstants.USER__CASH, cashDelta);
		}
		
		if (relativeParams.isEmpty()) {
			return 0;
		}
		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null,
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gems += gemsDelta;
			this.oil += oilDelta;
			this.cash += cashDelta;
		}
		return numUpdated;
	}
	
	public boolean updateRelativeDiamondsBeginnerSale (int diamondChange, boolean isBeginnerSale) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (diamondChange != 0) {
			relativeParams.put(DBConstants.USER__GEMS, diamondChange);
		}

		if (isBeginnerSale) {
			relativeParams.put(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gems += diamondChange;
			this.numBeginnerSalesPurchased += isBeginnerSale ? 1 : 0;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsAbsoluteClan (int coinChange, Integer clanId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__CASH, coinChange);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__CLAN_ID, clanId);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.cash += coinChange;
			if (clanId == null) this.clanId = ControllerConstants.NOT_SET;
			else this.clanId = clanId;
			return true;
		}
		return false;
	}

	public boolean updateGemsCashClan(int gemChange, int cashChange, int clanId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		if (0 != cashChange) {
			relativeParams.put(DBConstants.USER__CASH, cashChange);
		}
		if (0 != gemChange) {
			relativeParams.put(DBConstants.USER__GEMS, gemChange);
		}

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__CLAN_ID, clanId);
		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		
		if (1 == numUpdated) {
			if (0 != cashChange) {
				this.cash += cashChange;
			}
			if (0 != gemChange) {
				this.gems += gemChange;
			}
			this.clanId = clanId;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsOilRetrievedFromStructs (int coinChange,
			int oilChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__CASH, coinChange);
			relativeParams.put(DBConstants.USER__NUM_COINS_RETRIEVED_FROM_STRUCTS, coinChange);
		}
		if (oilChange != 0) {
			relativeParams.put(DBConstants.USER__OIL, oilChange);
			relativeParams.put(DBConstants.USER__NUM_OIL_RETRIEVED_FROM_STRUCTS, coinChange);
		}

		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.cash += coinChange;
			this.numCoinsRetrievedFromStructs += coinChange;
			this.oil += oilChange;
			this.numOilRetrievedFromStructs += oilChange;
			return true;
		}
		return false;
	}


	public boolean updateRelativeCashNaive (int coinChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__CASH, coinChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.cash += coinChange;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsBeginnerSale (int coinChange, boolean isBeginnerSale) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__CASH, coinChange);
		}

		if (isBeginnerSale) {
			relativeParams.put(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.cash += coinChange;
			this.numBeginnerSalesPurchased += isBeginnerSale ? 1 : 0;
			return true;
		}
		return false;
	}


	public boolean updateRelativeDiamondsForFree(int diamondChange, EarnFreeDiamondsType freeDiamondsType) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		Map<String, Object> absoluteParams = null;
		if (diamondChange <= 0) return false;

		relativeParams.put(DBConstants.USER__GEMS, diamondChange);
		if (EarnFreeDiamondsType.FB_CONNECT == freeDiamondsType) {
			absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.USER__HAS_RECEIVED_FB_REWARD, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.gems += diamondChange;
			if (EarnFreeDiamondsType.FB_CONNECT == freeDiamondsType) {
				this.hasReceivedfbReward = true;
			}
			return true;
		}
		return false;
	}

//	public boolean updateNameUserTypeUdid(String newName,
//			String newUdid, int relativeDiamondCost) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.USER__ID, id);
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		//if (newUserType != null) absoluteParams.put(DBConstants.USER__TYPE, newUserType.getNumber());
//		if (newName != null) absoluteParams.put(DBConstants.USER__NAME, newName);
//		if (newUdid != null) absoluteParams.put(DBConstants.USER__UDID, newUdid);
//
//		Map <String, Object> relativeParams = new HashMap<String, Object>();
//		relativeParams.put(DBConstants.USER__GEMS, relativeDiamondCost);
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
//				relativeParams, absoluteParams, conditionParams, "and");
//		if (numUpdated == 1) {
//			//if (newUserType != null) this.type = newUserType;
//			if (newName != null) this.name = newName;
//			if (newUdid != null) this.udid = newUdid;
//			this.gems += relativeDiamondCost;
//			return true;
//		}
//		return false;
//	}
	
	/*  //replaced with updateRelativeGemsAndObstacleTime
	public boolean updateLastObstacleSpawnedTime(Timestamp lastObstacleSpawnedTime) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		
		Map<String, Object> relativeParams = null;
		
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME, lastObstacleSpawnedTime);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams,
				absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			this.lastObstacleSpawnedTime = new Date(lastObstacleSpawnedTime.getTime());
			return true;
		}
		return false;
	}*/
	
	//obstaclesRemovedDelta is always positive
	public boolean updateRelativeGemsObstacleTimeNumRemoved(int gemChange,
			Timestamp lastObstacleSpawnedTime, int obstaclesRemovedDelta) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map<String, Object> relativeParams = new HashMap<String, Object>();
		if (0 != gemChange) {
			relativeParams.put(DBConstants.USER__GEMS, gemChange);
		}
		if (0 != obstaclesRemovedDelta) {
			relativeParams.put(DBConstants.USER__NUM_OBSTACLES_REMOVED,
					obstaclesRemovedDelta);
		}
		
		
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		if (null != lastObstacleSpawnedTime) {
			absoluteParams.put(DBConstants.USER__LAST_OBSTACLE_SPAWNED_TIME, lastObstacleSpawnedTime);
		}
		
		if (relativeParams.isEmpty() && absoluteParams.isEmpty()) {
			//no need to write what is essentially nothing to db
			return true;
		}
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		
		if (numUpdated == 1) {
			if (0 != gemChange) {
				this.gems += gemChange;
			}
			if (null != lastObstacleSpawnedTime) {
				this.lastObstacleSpawnedTime = new Date(
						lastObstacleSpawnedTime.getTime());
			}
			if (0 != obstaclesRemovedDelta) {
				this.numObstaclesRemoved += obstaclesRemovedDelta;
			}
			
			return true;
		}
		return false;
	}
	
	public boolean updateLastMiniJobGeneratedTime(Date now,
			Timestamp nowTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map<String, Object> relativeParams = null;
		
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		if (null != nowTime) {
			absoluteParams.put(DBConstants
					.USER__LAST_MINI_JOB_GENERATED_TIME, nowTime);
		}
		
		if (absoluteParams.isEmpty()) {
			//no need to write what is essentially nothing to db
			return true;
		}
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		
		if (numUpdated == 1) {
			if (null != now) {
				this.lastMiniJobGeneratedTime = now;
			}
			
			return true;
		}
		return false;
	}
	
	public boolean updateResetAccount() {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map<String, Object> relativeParams = null;
		
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		
		if (null != udid && !udid.isEmpty()) {
			absoluteParams.put(DBConstants.USER__UDID, udid + "_reset");
		}
		if (null != facebookId && facebookId != null) {
			absoluteParams.put(DBConstants.USER__FACEBOOK_ID, facebookId + "_reset");
		}
		
		if (absoluteParams.isEmpty()) {
			//no need to write what is essentially nothing to db
			return true;
		}
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");

		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getGems() {
		return gems;
	}

	public void setGems(int gems) {
		this.gems = gems;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getOil() {
		return oil;
	}

	public void setOil(int oil) {
		this.oil = oil;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getTasksCompleted() {
		return tasksCompleted;
	}

	public void setTasksCompleted(int tasksCompleted) {
		this.tasksCompleted = tasksCompleted;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public int getNumReferrals() {
		return numReferrals;
	}

	public void setNumReferrals(int numReferrals) {
		this.numReferrals = numReferrals;
	}

	public String getUdidForHistory() {
		return udidForHistory;
	}

	public void setUdidForHistory(String udidForHistory) {
		this.udidForHistory = udidForHistory;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout() {
		return lastLogout;
	}

	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public int getNumBadges() {
		return numBadges;
	}

	public void setNumBadges(int numBadges) {
		this.numBadges = numBadges;
	}

	public boolean isFake() {
		return isFake;
	}

	public void setFake(boolean isFake) {
		this.isFake = isFake;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getApsalarId() {
		return apsalarId;
	}

	public void setApsalarId(String apsalarId) {
		this.apsalarId = apsalarId;
	}

	public int getNumCoinsRetrievedFromStructs() {
		return numCoinsRetrievedFromStructs;
	}

	public void setNumCoinsRetrievedFromStructs(int numCoinsRetrievedFromStructs) {
		this.numCoinsRetrievedFromStructs = numCoinsRetrievedFromStructs;
	}

	public int getNumOilRetrievedFromStructs() {
		return numOilRetrievedFromStructs;
	}

	public void setNumOilRetrievedFromStructs(int numOilRetrievedFromStructs) {
		this.numOilRetrievedFromStructs = numOilRetrievedFromStructs;
	}

	public int getNumConsecutiveDaysPlayed() {
		return numConsecutiveDaysPlayed;
	}

	public void setNumConsecutiveDaysPlayed(int numConsecutiveDaysPlayed) {
		this.numConsecutiveDaysPlayed = numConsecutiveDaysPlayed;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public Date getLastWallPostNotificationTime() {
		return lastWallPostNotificationTime;
	}

	public void setLastWallPostNotificationTime(Date lastWallPostNotificationTime) {
		this.lastWallPostNotificationTime = lastWallPostNotificationTime;
	}

	public int getKabamNaid() {
		return kabamNaid;
	}

	public void setKabamNaid(int kabamNaid) {
		this.kabamNaid = kabamNaid;
	}

	public boolean isHasReceivedfbReward() {
		return hasReceivedfbReward;
	}

	public void setHasReceivedfbReward(boolean hasReceivedfbReward) {
		this.hasReceivedfbReward = hasReceivedfbReward;
	}

	public int getNumBeginnerSalesPurchased() {
		return numBeginnerSalesPurchased;
	}

	public void setNumBeginnerSalesPurchased(int numBeginnerSalesPurchased) {
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public boolean isFbIdSetOnUserCreate() {
		return fbIdSetOnUserCreate;
	}

	public void setFbIdSetOnUserCreate(boolean fbIdSetOnUserCreate) {
		this.fbIdSetOnUserCreate = fbIdSetOnUserCreate;
	}

	public String getGameCenterId() {
		return gameCenterId;
	}

	public void setGameCenterId(String gameCenterId) {
		this.gameCenterId = gameCenterId;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public Date getLastObstacleSpawnedTime() {
		return lastObstacleSpawnedTime;
	}

	public void setLastObstacleSpawnedTime(Date lastObstacleSpawnedTime) {
		this.lastObstacleSpawnedTime = lastObstacleSpawnedTime;
	}

	public int getNumObstaclesRemoved() {
		return numObstaclesRemoved;
	}

	public void setNumObstaclesRemoved(int numObstaclesRemoved) {
		this.numObstaclesRemoved = numObstaclesRemoved;
	}

	public Date getLastMiniJobGeneratedTime() {
		return lastMiniJobGeneratedTime;
	}

	public void setLastMiniJobGeneratedTime(Date lastMiniJobGeneratedTime) {
		this.lastMiniJobGeneratedTime = lastMiniJobGeneratedTime;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", level=" + level
				+ ", gems=" + gems + ", cash=" + cash + ", oil=" + oil
				+ ", experience=" + experience + ", tasksCompleted="
				+ tasksCompleted + ", referralCode=" + referralCode
				+ ", numReferrals=" + numReferrals + ", udidForHistory="
				+ udidForHistory + ", lastLogin=" + lastLogin + ", lastLogout="
				+ lastLogout + ", deviceToken=" + deviceToken + ", numBadges="
				+ numBadges + ", isFake=" + isFake + ", createTime="
				+ createTime + ", isAdmin=" + isAdmin + ", apsalarId="
				+ apsalarId + ", numCoinsRetrievedFromStructs="
				+ numCoinsRetrievedFromStructs
				+ ", numOilRetrievedFromStructs=" + numOilRetrievedFromStructs
				+ ", numConsecutiveDaysPlayed=" + numConsecutiveDaysPlayed
				+ ", clanId=" + clanId + ", lastWallPostNotificationTime="
				+ lastWallPostNotificationTime + ", kabamNaid=" + kabamNaid
				+ ", hasReceivedfbReward=" + hasReceivedfbReward
				+ ", numBeginnerSalesPurchased=" + numBeginnerSalesPurchased
				+ ", facebookId=" + facebookId + ", fbIdSetOnUserCreate="
				+ fbIdSetOnUserCreate + ", gameCenterId=" + gameCenterId
				+ ", udid=" + udid + ", lastObstacleSpawnedTime="
				+ lastObstacleSpawnedTime + ", numObstaclesRemoved="
				+ numObstaclesRemoved + ", lastMiniJobGeneratedTime="
				+ lastMiniJobGeneratedTime + "]";
	}

}
