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

	private static final long serialVersionUID = -2819418420486930959L;
	private int id;
	private String name;
	private int level;
	private int diamonds;
	private int coins;
	private int experience;
	private int tasksCompleted;
	private int battlesWon;
	private int battlesLost;
	private int flees;
	private String referralCode;
	private int numReferrals;
	private String udid;
	private Date lastLogin;
	private Date lastLogout;
	private String deviceToken;
	private Date lastBattleNotificationTime;
	private int numBadges;
	private boolean isFake;
	private Date createTime;
	private boolean isAdmin;
	private String apsalarId;
	private int numCoinsRetrievedFromStructs;
	private int numConsecutiveDaysPlayed;
	private int clanId;
	private Date lastWallPostNotificationTime;
	private int kabamNaid;
	private boolean hasReceivedfbReward;
	private int numAdditionalMonsterSlots;
	private int numBeginnerSalesPurchased;
	private boolean hasActiveShield;
	private Date shieldEndTime;
	private int elo;
	private String rank;
	private Date lastTimeQueued;
	private int attacksWon;
	private int defensesWon;
	private int attacksLost;
	private int defensesLost;



	public User(int id, String name, int level, int diamonds, int coins,
			int experience, int tasksCompleted, int battlesWon, int battlesLost,
			int flees, String referralCode, int numReferrals, String udid,
			Date lastLogin, Date lastLogout, String deviceToken,
			Date lastBattleNotificationTime, int numBadges, boolean isFake,
			Date createTime, boolean isAdmin, String apsalarId,
			int numCoinsRetrievedFromStructs, int numConsecutiveDaysPlayed,
			int clanId, Date lastWallPostNotificationTime, int kabamNaid,
			boolean hasReceivedfbReward, int numAdditionalMonsterSlots,
			int numBeginnerSalesPurchased, boolean hasActiveShield,
			Date shieldEndTime, int elo, String rank, Date lastTimeQueued,
			int attacksWon, int defensesWon, int attacksLost, int defensesLost) {
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.diamonds = diamonds;
		this.coins = coins;
		this.experience = experience;
		this.tasksCompleted = tasksCompleted;
		this.battlesWon = battlesWon;
		this.battlesLost = battlesLost;
		this.flees = flees;
		this.referralCode = referralCode;
		this.numReferrals = numReferrals;
		this.udid = udid;
		this.lastLogin = lastLogin;
		this.lastLogout = lastLogout;
		this.deviceToken = deviceToken;
		this.lastBattleNotificationTime = lastBattleNotificationTime;
		this.numBadges = numBadges;
		this.isFake = isFake;
		this.createTime = createTime;
		this.isAdmin = isAdmin;
		this.apsalarId = apsalarId;
		this.numCoinsRetrievedFromStructs = numCoinsRetrievedFromStructs;
		this.numConsecutiveDaysPlayed = numConsecutiveDaysPlayed;
		this.clanId = clanId;
		this.lastWallPostNotificationTime = lastWallPostNotificationTime;
		this.kabamNaid = kabamNaid;
		this.hasReceivedfbReward = hasReceivedfbReward;
		this.numAdditionalMonsterSlots = numAdditionalMonsterSlots;
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
		this.hasActiveShield = hasActiveShield;
		this.shieldEndTime = shieldEndTime;
		this.elo = elo;
		this.rank = rank;
		this.lastTimeQueued = lastTimeQueued;
		this.attacksWon = attacksWon;
		this.defensesWon = defensesWon;
		this.attacksLost = attacksLost;
		this.defensesLost = defensesLost;
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

	public boolean updateResetNumbadgesSetdevicetoken(String deviceToken) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		if (deviceToken != null && deviceToken.length() == 0) {
			deviceToken = null;
		}

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__NUM_BADGES, 0);
		absoluteParams.put(DBConstants.USER__DEVICE_TOKEN, deviceToken);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.numBadges = 0;
			this.deviceToken = deviceToken;
			return true;
		}
		return false;
	}

	public boolean updateRelativeBadge(int badgeChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.numBadges += badgeChange;
			return true;
		}
		return false;
	}

	public boolean updateRelativeBadgeAbsoluteLastBattleNotificationTime(int badgeChange, Timestamp newLastBattleNotificationTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__LAST_BATTLE_NOTIFICATION_TIME, newLastBattleNotificationTime);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.numBadges += badgeChange;
			this.lastBattleNotificationTime = newLastBattleNotificationTime;
			return true;
		}
		return false;
	}


	public boolean updateRelativeBadgeAbsoluteLastWallPostNotificationTime(int badgeChange, Timestamp newLastWallNotificationTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__LAST_WALL_POST_NOTIFICATION_TIME, newLastWallNotificationTime);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__NUM_BADGES, badgeChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.numBadges += badgeChange;
			this.lastBattleNotificationTime = newLastWallNotificationTime;
			return true;
		}
		return false;
	}

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

	public boolean updateLastlogout(Timestamp lastLogout) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (lastLogout == null) {
			return false;
		}
		absoluteParams.put(DBConstants.USER__LAST_LOGOUT, lastLogout);
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.lastLogout = lastLogout;
			return true;
		}
		return false;
	}


	public boolean updateLevel(int levelChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__LEVEL, levelChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.level += levelChange;
			return true;
		}
		return false;
	}

	/*
	 * used for reducing stamina and increasing silver and gold after attacking a boss
	 */
	/*public boolean updateUserAfterAttackingBoss(int energyChange,
			int silverChange, int goldChange, boolean simulateEnergyRefill,
			Timestamp clientTime, int expChange) {
		String tableName = DBConstants.TABLE_USER;

		//the columns that are going to change, "relative params" because 
		//column changing is relative to itself
		Map<String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__ENERGY, energyChange);
		if (silverChange != 0) relativeParams.put(DBConstants.USER__COINS, silverChange);
		if (goldChange != 0) relativeParams.put(DBConstants.USER__DIAMONDS, goldChange);
		if(expChange != 0) relativeParams.put(DBConstants.USER__EXPERIENCE, expChange);

		//WHERE clause 
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = null;
		if (simulateEnergyRefill) {
			absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.USER__LAST_ENERGY_REFILL_TIME, clientTime);
		}

		//the delimiter to separate the condition tests in the WHERE clause
		String condDelim = "";

		int numUpdated = DBConnection.get().updateTableRows(tableName, 
				relativeParams, absoluteParams, conditionParams, condDelim);
		if (1 == numUpdated) {
			//should be one since id is unique
			//need to update object since updated database
			if (simulateEnergyRefill) {
				this.lastEnergyRefillTime = clientTime;
			}
			this.energy += energyChange;
			this.coins += silverChange;
			this.diamonds += goldChange;
			this.experience += expChange;
			return true;
		}
		return false;
	}*;



	/*
	 * used for purchasing and selling structures, redeeming quests
	 */
	public boolean updateRelativeDiamondsCoinsExperienceNaive (int diamondChange, int coinChange, 
			int experienceChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		relativeParams.put(DBConstants.USER__DIAMONDS, diamondChange);
		relativeParams.put(DBConstants.USER__COINS, coinChange);
		relativeParams.put(DBConstants.USER__EXPERIENCE, experienceChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += diamondChange;
			this.coins += coinChange;
			this.experience += experienceChange;
			return true;
		}
		return false;
	}




	/*
	 * used for tasks
	 *        * user- coins/exp/tasks_completed increase
	 */
	public boolean updateRelativeCoinsExpTaskscompleted (
			int coinChange, int expChange, int tasksCompletedChange,
			Timestamp clientTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		relativeParams.put(DBConstants.USER__COINS, coinChange);
		relativeParams.put(DBConstants.USER__EXPERIENCE, expChange);
		relativeParams.put(DBConstants.USER__TASKS_COMPLETED, tasksCompletedChange);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (absoluteParams.size() == 0) {
			absoluteParams = null;
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			this.experience += expChange;
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
			relativeParams.put(DBConstants.USER__COINS, coinChange);
		}
		if (numReferralsChange != 0) {
			relativeParams.put(DBConstants.USER__NUM_REFERRALS, numReferralsChange); 
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			this.numReferrals += numReferralsChange;
			return true;
		}
		return false;
	}

	/*
	 * used for in app purchases, armory, finishingnormstructbuild
	 */
	public boolean updateRelativeDiamondsNaive (int diamondChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (diamondChange != 0) {
			relativeParams.put(DBConstants.USER__DIAMONDS, diamondChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += diamondChange;
			return true;
		}
		return false;
	}
	
	public int updateRelativeCoinsAndDiamonds(int coinsDelta, int diamondsDelta) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		
		Map<String, Object> relativeParams = new HashMap<String, Object>();
		if (diamondsDelta != 0) {
			relativeParams.put(DBConstants.USER__DIAMONDS, diamondsDelta);
		}
		if (coinsDelta != 0) {
			relativeParams.put(DBConstants.USER__COINS, coinsDelta);
		}
		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null,
				conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += diamondsDelta;
			this.coins += coinsDelta;
		}
		return numUpdated;
	}
	
	public boolean updateRelativeDiamondsBeginnerSale (int diamondChange, boolean isBeginnerSale) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (diamondChange != 0) {
			relativeParams.put(DBConstants.USER__DIAMONDS, diamondChange);
		}

		if (isBeginnerSale) {
			relativeParams.put(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += diamondChange;
			this.numBeginnerSalesPurchased += isBeginnerSale ? 1 : 0;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsAbsoluteClan (int coinChange, Integer clanId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__COINS, coinChange);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__CLAN_ID, clanId);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			if (clanId == null) this.clanId = ControllerConstants.NOT_SET;
			else this.clanId = clanId;
			return true;
		}
		return false;
	}


	public boolean updateRelativeCoinsCoinsretrievedfromstructs (int coinChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__COINS, coinChange);
			relativeParams.put(DBConstants.USER__NUM_COINS_RETRIEVED_FROM_STRUCTS, coinChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			this.numCoinsRetrievedFromStructs += coinChange;
			return true;
		}
		return false;
	}


	public boolean updateRelativeCoinsNaive (int coinChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__COINS, coinChange);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			return true;
		}
		return false;
	}

	public boolean updateRelativeCoinsBeginnerSale (int coinChange, boolean isBeginnerSale) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();

		if (coinChange != 0) {
			relativeParams.put(DBConstants.USER__COINS, coinChange);
		}

		if (isBeginnerSale) {
			relativeParams.put(DBConstants.USER__NUM_BEGINNER_SALES_PURCHASED, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, null, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.coins += coinChange;
			this.numBeginnerSalesPurchased += isBeginnerSale ? 1 : 0;
			return true;
		}
		return false;
	}

	/*
	 * used for battles
	 */
	public boolean updateRelativeExperienceCoinsBattlesWonBattlesLostFlees (
			int experience, int coins, int battlesWon, int battlesLost, int fleesChange,
			Timestamp clientTime, boolean deactivateShield,
			boolean activateShield, boolean recordWinLossFlee, int attacksWonDelta, 
			int defensesWonDelta, int attacksLostDelta, int defensesLostDelta) {
		Date shieldEndTimeTemp = this.shieldEndTime; //for changing this obj if endTime changes
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		if (experience != 0) relativeParams.put(DBConstants.USER__EXPERIENCE, experience);
		if (coins != 0) relativeParams.put(DBConstants.USER__COINS, coins);
		if (recordWinLossFlee) {
			recordWinLossFlees(relativeParams, battlesWon, battlesLost, fleesChange,
					attacksWonDelta, defensesWonDelta, attacksLostDelta, defensesLostDelta);
		}

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		
		//3 cases:deactivate a shield, activate a shield, do nothing
		shieldEndTimeTemp = recordTurnOnOffShield(absoluteParams, deactivateShield,
				clientTime, activateShield, shieldEndTimeTemp);
		
		if (absoluteParams.size() == 0) {
			absoluteParams = null;
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
//			this.energy +=energy;
			this.experience += experience;
			this.coins += coins;
			if (recordWinLossFlee) {
				updateWinsLossFlees(battlesWon, battlesLost, fleesChange, attacksWonDelta,
						defensesWonDelta, attacksLostDelta, defensesLostDelta);
			}
			turnOnOffShield(deactivateShield, activateShield, clientTime, shieldEndTimeTemp);
			
			return true;
		}
		return false;
	}
	
	private void recordWinLossFlees(Map<String, Object> relativeParams,
			int battlesWon, int battlesLost, int fleesChange, int attacksWonDelta,
			int defensesWonDelta, int attacksLostDelta, int defensesLostDelta) {
		if (battlesWon != 0) {
			relativeParams.put(DBConstants.USER__BATTLES_WON, battlesWon);
		}
		if (battlesLost != 0) {
			relativeParams.put(DBConstants.USER__BATTLES_LOST, battlesLost);
		}
		if (fleesChange != 0) {
			relativeParams.put(DBConstants.USER__FLEES, fleesChange);
		}
		if (0 != attacksWonDelta) {
			relativeParams.put(DBConstants.USER__ATTACKS_WON, attacksWonDelta);
		}
		if (0 != defensesWonDelta) {
			relativeParams.put(DBConstants.USER__DEFENSES_WON, defensesWonDelta);
		}
		if (0 != attacksLostDelta) {
			relativeParams.put(DBConstants.USER__ATTACKS_WON, attacksLostDelta);
		}
		if (0 != defensesLostDelta) {
			relativeParams.put(DBConstants.USER__DEFENSES_WON, defensesLostDelta);
		}
		//TODO: FINISH THIS
	}
	
	//returns new shield end time if shield status changes
	private Date recordTurnOnOffShield(Map<String, Object> absoluteParams, 
			boolean deactivateShield, Timestamp clientTime, boolean activateShield,
			Date shieldEndTimeTemp) {
		//3 cases:deactivate a shield, activate a shield, do nothing
		if (deactivateShield) {
			if (hasActiveShield ) {
				absoluteParams.put(DBConstants.USER__HAS_ACTIVE_SHIELD, false);
			}
			if (null != shieldEndTime && (shieldEndTime.getTime() > clientTime.getTime())) {
				absoluteParams.put(DBConstants.USER__SHIELD_END_TIME, null);
			}
		} else if (activateShield) {
			if (shieldEndTime == null || shieldEndTime.getTime() < clientTime.getTime()) {
				long time = clientTime.getTime()+43200000; //set shield end time to 12 hrs from now
				Timestamp d = new Timestamp(time);
				absoluteParams.put(DBConstants.USER__SHIELD_END_TIME, d);
				shieldEndTimeTemp = new Date(time);
			}
		}
		return shieldEndTimeTemp;
	}
	
	private void updateWinsLossFlees(int battlesWon, int battlesLost, int fleesChange,
			int attacksWonDelta, int defensesWonDelta, int attacksLostDelta,
			int defensesLostDelta) {
		this.battlesWon += battlesWon;
		this.battlesLost += battlesLost;
		this.flees += fleesChange;
		this.attacksWon += attacksWonDelta;
		this.defensesWon += defensesWonDelta;
		this.attacksLost += attacksLostDelta;
		this.defensesLost += defensesLostDelta;
	}
	
	private void turnOnOffShield(boolean deactivateShield, boolean activateShield,
			Timestamp clientTime, Date shieldEndTimeTemp) {
		if (deactivateShield) {
			if (hasActiveShield) {
				this.hasActiveShield = false;
			}
			if (null != shieldEndTime && (shieldEndTime.getTime() > clientTime.getTime())) {
				this.shieldEndTime = null;
			}
		} else if (activateShield) {
			this.shieldEndTime = shieldEndTimeTemp;
		}
	}

	public boolean updateRelativeDiamondsForFree(int diamondChange, EarnFreeDiamondsType freeDiamondsType) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		Map<String, Object> absoluteParams = null;
		if (diamondChange <= 0) return false;

		relativeParams.put(DBConstants.USER__DIAMONDS, diamondChange);
		if (EarnFreeDiamondsType.FB_CONNECT == freeDiamondsType) {
			absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.USER__HAS_RECEIVED_FB_REWARD, 1);
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, relativeParams, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += diamondChange;
			if (EarnFreeDiamondsType.FB_CONNECT == freeDiamondsType) {
				this.hasReceivedfbReward = true;
			}
			return true;
		}
		return false;
	}

	public boolean updateNameUserTypeUdid(String newName,
			String newUdid, int relativeDiamondCost) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		//if (newUserType != null) absoluteParams.put(DBConstants.USER__TYPE, newUserType.getNumber());
		if (newName != null) absoluteParams.put(DBConstants.USER__NAME, newName);
		if (newUdid != null) absoluteParams.put(DBConstants.USER__UDID, newUdid);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__DIAMONDS, relativeDiamondCost);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			//if (newUserType != null) this.type = newUserType;
			if (newName != null) this.name = newName;
			if (newUdid != null) this.udid = newUdid;
			this.diamonds += relativeDiamondCost;
			return true;
		}
		return false;
	}

	public boolean updateNumAdditionalMonsterSlotsAndDiamonds(int newAdditionalMonsterSlots, int cost) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__NUM_ADDITIONAL_FORGE_SLOTS, newAdditionalMonsterSlots);
		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER__DIAMONDS, cost);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER,
				relativeParams, absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			this.diamonds += cost;
			this.numAdditionalMonsterSlots = newAdditionalMonsterSlots;
			return true;
		}

		return false;
	}


	public boolean updateElo(int newElo) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__ELO, newElo);
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	public boolean updateLastTimeQueued(Date lastTimeQueued) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER__ID, id);
		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER__LAST_TIME_QUEUED, lastTimeQueued);
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
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


	public int getDiamonds() {
		return diamonds;
	}


	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
	}


	public int getCoins() {
		return coins;
	}


	public void setCoins(int coins) {
		this.coins = coins;
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


	public int getBattlesWon() {
		return battlesWon;
	}


	public void setBattlesWon(int battlesWon) {
		this.battlesWon = battlesWon;
	}


	public int getBattlesLost() {
		return battlesLost;
	}


	public void setBattlesLost(int battlesLost) {
		this.battlesLost = battlesLost;
	}


	public int getFlees() {
		return flees;
	}


	public void setFlees(int flees) {
		this.flees = flees;
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


	public String getUdid() {
		return udid;
	}


	public void setUdid(String udid) {
		this.udid = udid;
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


	public Date getLastBattleNotificationTime() {
		return lastBattleNotificationTime;
	}


	public void setLastBattleNotificationTime(Date lastBattleNotificationTime) {
		this.lastBattleNotificationTime = lastBattleNotificationTime;
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


	public int getNumAdditionalMonsterSlots() {
		return numAdditionalMonsterSlots;
	}


	public void setNumAdditionalMonsterSlots(int numAdditionalMonsterSlots) {
		this.numAdditionalMonsterSlots = numAdditionalMonsterSlots;
	}


	public int getNumBeginnerSalesPurchased() {
		return numBeginnerSalesPurchased;
	}


	public void setNumBeginnerSalesPurchased(int numBeginnerSalesPurchased) {
		this.numBeginnerSalesPurchased = numBeginnerSalesPurchased;
	}


	public boolean isHasActiveShield() {
		return hasActiveShield;
	}


	public void setHasActiveShield(boolean hasActiveShield) {
		this.hasActiveShield = hasActiveShield;
	}


	public Date getShieldEndTime() {
		return shieldEndTime;
	}


	public void setShieldEndTime(Date shieldEndTime) {
		this.shieldEndTime = shieldEndTime;
	}


	public int getElo() {
		return elo;
	}


	public void setElo(int elo) {
		this.elo = elo;
	}


	public String getRank() {
		return rank;
	}


	public void setRank(String rank) {
		this.rank = rank;
	}


	public Date getLastTimeQueued() {
		return lastTimeQueued;
	}


	public void setLastTimeQueued(Date lastTimeQueued) {
		this.lastTimeQueued = lastTimeQueued;
	}


	public int getAttacksWon() {
		return attacksWon;
	}

	public void setAttacksWon(int attacksWon) {
		this.attacksWon = attacksWon;
	}

	public int getDefensesWon() {
		return defensesWon;
	}

	public void setDefensesWon(int defensesWon) {
		this.defensesWon = defensesWon;
	}

	public int getAttacksLost() {
		return attacksLost;
	}

	public void setAttacksLost(int attacksLost) {
		this.attacksLost = attacksLost;
	}

	public int getDefensesLost() {
		return defensesLost;
	}

	public void setDefensesLost(int defensesLost) {
		this.defensesLost = defensesLost;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", level=" + level
				+ ", diamonds=" + diamonds + ", coins=" + coins + ", experience="
				+ experience + ", tasksCompleted=" + tasksCompleted + ", battlesWon="
				+ battlesWon + ", battlesLost=" + battlesLost + ", flees=" + flees
				+ ", referralCode=" + referralCode + ", numReferrals=" + numReferrals
				+ ", udid=" + udid + ", lastLogin=" + lastLogin + ", lastLogout="
				+ lastLogout + ", deviceToken=" + deviceToken
				+ ", lastBattleNotificationTime=" + lastBattleNotificationTime
				+ ", numBadges=" + numBadges + ", isFake=" + isFake + ", createTime="
				+ createTime + ", isAdmin=" + isAdmin + ", apsalarId=" + apsalarId
				+ ", numCoinsRetrievedFromStructs=" + numCoinsRetrievedFromStructs
				+ ", numConsecutiveDaysPlayed=" + numConsecutiveDaysPlayed
				+ ", clanId=" + clanId + ", lastWallPostNotificationTime="
				+ lastWallPostNotificationTime + ", kabamNaid=" + kabamNaid
				+ ", hasReceivedfbReward=" + hasReceivedfbReward
				+ ", numAdditionalMonsterSlots=" + numAdditionalMonsterSlots
				+ ", numBeginnerSalesPurchased=" + numBeginnerSalesPurchased
				+ ", hasActiveShield=" + hasActiveShield + ", shieldEndTime="
				+ shieldEndTime + ", elo=" + elo + ", rank=" + rank
				+ ", lastTimeQueued=" + lastTimeQueued + ", attacksWon=" + attacksWon
				+ ", defensesWon=" + defensesWon + ", attacksLost=" + attacksLost
				+ ", defensesLost=" + defensesLost + "]";
	}

}
