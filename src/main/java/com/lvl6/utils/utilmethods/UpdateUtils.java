package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.CoordinatePair;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.DBConnection;

public class UpdateUtils implements UpdateUtil {


	private static final Logger log = LoggerFactory.getLogger(UpdateUtils.class);


	public static UpdateUtil get() {
		return (UpdateUtil) AppContext.getApplicationContext().getBean("updateUtils");
	}

	//  private Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());


	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserQuestsCoinsretrievedforreq(int, java.util.List, int)
	 */
	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
	public boolean updateUserQuestsCoinsretrievedforreq(int userId, List <Integer> questIds, int coinGain) {
		String query = "update " + DBConstants.TABLE_QUEST_FOR_USER + " set " + DBConstants.USER_QUESTS__COINS_RETRIEVED_FOR_REQ
				+ "=" + DBConstants.USER_QUESTS__COINS_RETRIEVED_FOR_REQ + "+? where " 
				+ DBConstants.USER_QUESTS__USER_ID + "=? and (";
		List<Object> values = new ArrayList<Object>();
		values.add(coinGain);
		values.add(userId);
		List<String> condClauses = new ArrayList<String>();
		for (Integer questId : questIds) {
			condClauses.add(DBConstants.USER_QUESTS__QUEST_ID + "=?");
			values.add(questId);
		}
		query += StringUtils.getListInString(condClauses, "or") + ")";
		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
		if (numUpdated == questIds.size()) {
			return true;
		}
		return false;
	}

//	@Override
//	public boolean updateAbsoluteBlacksmithAttemptcompleteTimeofspeedup(int blacksmithId, Date timeOfSpeedup, boolean attemptComplete) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.BLACKSMITH__ID, blacksmithId);
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		absoluteParams.put(DBConstants.BLACKSMITH__ATTEMPT_COMPLETE, attemptComplete);
//
//		if (timeOfSpeedup != null) {
//			absoluteParams.put(DBConstants.BLACKSMITH__TIME_OF_SPEEDUP, timeOfSpeedup);
//		}
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_EVOLUTION, null, absoluteParams, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			return true;
//		}
//		return false;
//	}

	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateNullifyDeviceTokens(java.util.Set)
	 */
	@Override
	public void updateNullifyDeviceTokens(Set<String> deviceTokens) {
		if (deviceTokens != null && deviceTokens.size() > 0) {
			String query = "update " + DBConstants.TABLE_USER + " set " + DBConstants.USER__DEVICE_TOKEN 
					+ "=? where ";
			List<Object> values = new ArrayList<Object>();
			values.add(null);
			List<String> condClauses = new ArrayList<String>();
			for (String deviceToken : deviceTokens) {
				condClauses.add(DBConstants.USER__DEVICE_TOKEN + "=?");
				values.add(deviceToken);
			}
			query += StringUtils.getListInString(condClauses, "or");
			DBConnection.get().updateDirectQueryNaive(query, values);
		}
	}
	
	@Override 
	public boolean updateUserCityExpansionData(int userId, int xPosition, int yPosition,
			boolean isExpanding, Timestamp expandStartTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__X_POSITION, xPosition);
		conditionParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__Y_POSITION, yPosition);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__IS_EXPANDING, isExpanding);
		absoluteParams.put(DBConstants.EXPANSION_PURCHASE_FOR_USER__EXPAND_START_TIME, expandStartTime);
		
		int numUpdated = DBConnection.get().updateTableRows(
				DBConstants.TABLE_EXPANSION_PURCHASE_FOR_USER, null, absoluteParams, conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserQuestIscomplete(int, int)
	 */
	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
	public boolean updateUserQuestIscomplete(int userId, int questId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_QUESTS__USER_ID, userId);
		conditionParams.put(DBConstants.USER_QUESTS__QUEST_ID, questId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER_QUESTS__TASKS_COMPLETE, true);
		absoluteParams.put(DBConstants.USER_QUESTS__IS_COMPLETE, true);
		absoluteParams.put(DBConstants.USER_QUESTS__DEFEAT_TYPE_JOBS_COMPLETE, true);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}  



	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateRedeemUserQuest(int, int)
	 */
	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
	public boolean updateRedeemUserQuest(int userId, int questId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_QUESTS__USER_ID, userId);
		conditionParams.put(DBConstants.USER_QUESTS__QUEST_ID, questId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER_QUESTS__IS_REDEEMED, true);
		absoluteParams.put(DBConstants.USER_QUESTS__TASKS_COMPLETE, true);
		absoluteParams.put(DBConstants.USER_QUESTS__IS_COMPLETE, true);
		absoluteParams.put(DBConstants.USER_QUESTS__DEFEAT_TYPE_JOBS_COMPLETE, true);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	/*
	 * changin orientation
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructOrientation(int, com.lvl6.proto.InfoProto.StructOrientation)
	 */
	@Override
	/*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean updateUserStructOrientation(int userStructId,
			StructOrientation orientation) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER_STRUCTS__ORIENTATION, orientation.getNumber());

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	/*
	 * used for setting a questitemtype as completed for a user quest
	 */

	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserQuestsSetCompleted(int, int, boolean, boolean)
	 */
	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
	public boolean updateUserQuestsSetCompleted(int userId, int questId, boolean setTasksCompleteTrue, boolean setDefeatTypeJobsCompleteTrue) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_QUESTS__USER_ID, userId);
		conditionParams.put(DBConstants.USER_QUESTS__QUEST_ID, questId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (setTasksCompleteTrue) {
			absoluteParams.put(DBConstants.USER_QUESTS__TASKS_COMPLETE, true); 
		}
		if (setDefeatTypeJobsCompleteTrue) {
			absoluteParams.put(DBConstants.USER_QUESTS__DEFEAT_TYPE_JOBS_COMPLETE, true); 
		}

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}



	/*@Caching(evict= {
      //@CacheEvict(value ="specificUserEquip", key="#userEquipId"),
      //@CacheEvict(value="userEquipsForUser", key="#newOwnerId"),
      //@CacheEvict(value="equipsToUserEquipsForUser", key="#newOwnerId"),
      //@CacheEvict(value="userEquipsWithEquipId", key="#newOwnerId+':'+#equipId")  
  })*/
	public boolean updateUserEquipOwner(long userEquipId, int newOwnerId, String reason) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__USER_ID, newOwnerId); 

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}


	//update a user equip after enhancing
	public boolean updateUserEquipEnhancementPercentage(long userEquipId, int newEnhancementPercentage) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__ENHANCEMENT_PERCENT, newEnhancementPercentage); 

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}



	/*
	 * used for updating is_complete=true and last_retrieved to upgrade_time+minutestogain for a userstruct
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructsLastretrievedpostupgradeIscompleteLevelchange(java.util.List, int)
	 */
	@Override
	public boolean updateUserStructsLastretrievedpostupgradeIscompleteLevelchange(List<StructureForUser> userStructs, int levelChange) {
//		Map<Integer, Structure> structures = StructureRetrieveUtils.getStructIdsToStructs();

//		for (UserStruct userStruct : userStructs) {
//			Structure structure = structures.get(userStruct.getStructId());
//			if (structure == null) {
//				return false;
//			}
//			Timestamp lastRetrievedTime = new Timestamp(userStruct.getLastUpgradeTime().getTime() + 60000*MiscMethods.calculateMinutesToBuildOrUpgradeForUserStruct(structure.getMinutesToUpgradeBase(), userStruct.getLevel()));
//			if (!updateUserStructLastretrievedIscompleteLevelchange(userStruct.getId(), lastRetrievedTime, true, levelChange)) {
//				return false;
//			}
//		}
//		return true;
		return false;
	}

	/*
	 * used for updating last retrieved and/or last upgrade user struct time and is_complete
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructLastretrievedIscompleteLevelchange(int, java.sql.Timestamp, boolean, int)
	 */
	@Override
	/*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean updateUserStructLastretrievedIscompleteLevelchange(int userStructId, Timestamp lastRetrievedTime, boolean isComplete, int levelChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (lastRetrievedTime != null)
			absoluteParams.put(DBConstants.USER_STRUCTS__LAST_RETRIEVED, lastRetrievedTime);

		absoluteParams.put(DBConstants.USER_STRUCTS__IS_COMPLETE, isComplete);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER_STRUCTS__LEVEL, levelChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, relativeParams, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	/*
	 * used for updating is_complete=true and last_retrieved to purchased_time+minutestogain for a userstruct
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructsLastretrievedpostbuildIscomplete(java.util.List)
	 */
	@Override
	public boolean updateUserStructsLastretrievedpostbuildIscomplete(List<StructureForUser> userStructs) {
//		Map<Integer, Structure> structures = StructureRetrieveUtils.getStructIdsToStructs();

//		for (UserStruct userStruct : userStructs) {
//			Structure structure = structures.get(userStruct.getStructId());
//			if (structure == null) {
//				return false;
//			}
//			Timestamp lastRetrievedTime = new Timestamp(userStruct.getPurchaseTime().getTime() + 60000*MiscMethods.calculateMinutesToBuildOrUpgradeForUserStruct(structure.getMinutesToUpgradeBase(), 0));
//			if (!updateUserStructLastretrievedLastupgradeIscomplete(userStruct.getId(), lastRetrievedTime, null, true)) {
//				return false;
//			}
//		}
//		return true;
		return false;
	}

	/*
	 * used for updating last retrieved and/or last upgrade user struct time and is_complete
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructLastretrievedLastupgradeIscomplete(int, java.sql.Timestamp, java.sql.Timestamp, boolean)
	 */
	@Override
	/*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean updateUserStructLastretrievedLastupgradeIscomplete(int userStructId, Timestamp lastRetrievedTime, Timestamp lastUpgradeTime, boolean isComplete) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (lastRetrievedTime != null)
			absoluteParams.put(DBConstants.USER_STRUCTS__LAST_RETRIEVED, lastRetrievedTime);

		if (lastUpgradeTime != null)
			absoluteParams.put(DBConstants.USER_STRUCTS__LAST_UPGRADE_TIME, lastUpgradeTime);

		absoluteParams.put(DBConstants.USER_STRUCTS__IS_COMPLETE, isComplete);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	/*
	 * used for updating last retrieved user struct time
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructLastretrieved(int, java.sql.Timestamp)
	 */
	/*@Override
  @Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")}) */
	public boolean updateUserStructsLastretrieved(Map<Integer, Timestamp> userStructIdsToLastRetrievedTime,
			Map<Integer, StructureForUser> structIdsToUserStructs) {
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

		for(Integer userStructId : userStructIdsToLastRetrievedTime.keySet()) {
			Map <String, Object> aRow = new HashMap<String, Object>();
			Timestamp lastRetrievedTime = userStructIdsToLastRetrievedTime.get(userStructId);
			StructureForUser us = structIdsToUserStructs.get(userStructId);

			aRow.put(DBConstants.USER_STRUCTS__ID, userStructId);
			aRow.put(DBConstants.USER_STRUCTS__USER_ID, us.getUserId());
			aRow.put(DBConstants.USER_STRUCTS__STRUCT_ID, us.getStructId());
			aRow.put(DBConstants.USER_STRUCTS__LAST_RETRIEVED, lastRetrievedTime);
			CoordinatePair cp = us.getCoordinates();
			aRow.put(DBConstants.USER_STRUCTS__X_COORD, cp.getX());
			aRow.put(DBConstants.USER_STRUCTS__Y_COORD, cp.getY());
			aRow.put(DBConstants.USER_STRUCTS__LEVEL, us.getLevel());
			aRow.put(DBConstants.USER_STRUCTS__PURCHASE_TIME, us.getPurchaseTime());
			aRow.put(DBConstants.USER_STRUCTS__LAST_UPGRADE_TIME, us.getLastUpgradeTime());
			aRow.put(DBConstants.USER_STRUCTS__IS_COMPLETE, us.isComplete());
			aRow.put(DBConstants.USER_STRUCTS__ORIENTATION, us.getOrientation().getNumber());

			newRows.add(aRow);
		}

		int numUpdated = DBConnection.get().replaceIntoTableValues(DBConstants.TABLE_STRUCTURE_FOR_USER, newRows);

		log.info("num userStructs updated: " + numUpdated 
				+ ". Number of userStructs: " + userStructIdsToLastRetrievedTime.size());
		if (numUpdated == userStructIdsToLastRetrievedTime.size()*2) {
			return true;
		}
		return false;
	}

	/*
	 * used for upgrading user structs level
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructLevel(int, int)
	 */
	@Override
	/*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean updateUserStructLevel(int userStructId, int levelChange) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.USER_STRUCTS__LEVEL, levelChange);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, relativeParams, null, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	/*
	 * used for moving user structs
	 */
	/* (non-Javadoc)
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateUserStructCoord(int, com.lvl6.info.CoordinatePair)
	 */
	@Override
	/*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
	public boolean updateUserStructCoord(int userStructId, CoordinatePair coordinates) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.USER_STRUCTS__X_COORD, coordinates.getX());
		absoluteParams.put(DBConstants.USER_STRUCTS__Y_COORD, coordinates.getY());

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean incrementNumberOfLockBoxesForLockBoxEvent(int userId, int eventId, int increment) {
		Map <String, Object> insertParams = new HashMap<String, Object>();

		insertParams.put(DBConstants.LOCK_BOX_EVENT_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.LOCK_BOX_EVENT_FOR_USER__EVENT_ID, eventId);
		insertParams.put(DBConstants.LOCK_BOX_EVENT_FOR_USER__NUM_BOXES, increment);
		insertParams.put(DBConstants.LOCK_BOX_EVENT_FOR_USER__NUM_TIMES_COMPLETED, 0);

		Map<String, Object> columnsToUpdate = new HashMap<String, Object>();
		columnsToUpdate.put(DBConstants.LOCK_BOX_EVENT_FOR_USER__NUM_BOXES, increment);

		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdate(DBConstants.TABLE_LOCK_BOX_EVENT_FOR_USER, insertParams, 
				columnsToUpdate, null);//DBConstants.USER_LOCK_BOX_EVENTS__NUM_BOXES, increment);

		if (numUpdated >= 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUsersClanId(Integer clanId, List<Integer> userIds) {
		String query = "update " + DBConstants.TABLE_USER + " set " + DBConstants.USER__CLAN_ID 
				+ "=? where (" ;
		List<Object> values = new ArrayList<Object>();
		values.add(clanId);
		List<String> condClauses = new ArrayList<String>();
		for (Integer userId : userIds) {
			condClauses.add(DBConstants.USER__ID + "=?");
			values.add(userId);
		}
		query += StringUtils.getListInString(condClauses, "or") + ")";
		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
		if (numUpdated == userIds.size()) {
			return true;
		}
		return false;
	}

	@Override
	//@CacheEvict(value="clanById", key="#clanId")
	public boolean updateClanOwnerDescriptionForClan(int clanId, int ownerId, String description) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLANS__ID, clanId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		if (ownerId > 0)
			absoluteParams.put(DBConstants.CLANS__OWNER_ID, ownerId);
		if (description != null)
			absoluteParams.put(DBConstants.CLANS__DESCRIPTION, description);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_CLANS, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUserClanStatus(int userId, int clanId, UserClanStatus status) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.CLAN_FOR_USER__STATUS, status.getNumber());

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_CLAN_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	public boolean updateUsersAddDiamonds(List<Integer> userIds, int diamonds) {
		if (userIds == null || userIds.size() <= 0) return true;

		List<Object> values = new ArrayList<Object>();
		String query = "update " + DBConstants.TABLE_USER + " set "
				+ DBConstants.USER__DIAMONDS + "="+ DBConstants.USER__DIAMONDS + "+?"
				+ " where id in (?";
		values.add(diamonds);
		values.add(userIds.get(0));

		for (int i = 1; i < userIds.size(); i++) {
			query += ", ?";
			values.add(userIds.get(i));
		}
		query += ")";

		log.info(query + " with values " +values);
		int numUserIds = userIds.size();
		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
		if (numUserIds != numUpdated) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean updateLeaderboardEventSetRewardGivenOut(int eventId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TOURNAMENT_EVENT__ID, eventId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.TOURNAMENT_EVENT__REWARDS_GIVEN_OUT, 1);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_TOURNAMENT_EVENT, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

//	//this method replaces existing rows with the same (single/composite) primary key
//	public boolean updateUserBoosterItemsForOneUser(int userId, 
//			Map<Integer, Integer> userBoosterItemIdsToQuantities) {
//		String tableName = DBConstants.TABLE_USER_BOOSTER_ITEMS;
//		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
//		for (Integer biId : userBoosterItemIdsToQuantities.keySet()) {
//			int newQuantity = userBoosterItemIdsToQuantities.get(biId);
//			Map<String, Object> row = new HashMap<String, Object>();
//			row.put(DBConstants.USER_BOOSTER_ITEMS__BOOSTER_ITEM_ID, biId);
//			row.put(DBConstants.USER_BOOSTER_ITEMS__USER_ID, userId);
//			row.put(DBConstants.USER_BOOSTER_ITEMS__NUM_COLLECTED, newQuantity);
//			newRows.add(row);
//		}
//
//		int numInserted = DBConnection.get().replaceIntoTableValues(tableName, newRows);
//		log.info("num inserted: "+numInserted);
//		if(userBoosterItemIdsToQuantities.size()*2 >= numInserted) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	public boolean updateClanJoinTypeForClan(int clanId, boolean requestToJoinRequired) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLANS__ID, clanId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED, requestToJoinRequired);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_CLANS, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	public int incrementUserTaskNumRevives(long userTaskId, int numRevives) {
		String tableName = DBConstants.TABLE_TASK_FOR_USER;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TASK_FOR_USER__ID, userTaskId);

		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.TASK_FOR_USER__NUM_REVIVES, numRevives);

		int numUpdated = DBConnection.get().updateTableRows(tableName, null,
				absoluteParams, conditionParams, "AND");

		return numUpdated;
	}
	
	public int updateUserMonstersHealth(List<Long> userMonsterIds,
			List<Integer> currentHealths,
			Map<Long, Integer> userMonsterIdsToHealths) {
		Map<String, Object> relativeParams = null;
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		
		//if map(userMonsterId -> expectedHealth) is set then use it
		if (null != userMonsterIdsToHealths && !userMonsterIdsToHealths.isEmpty()) {
			for (long userMonsterId : userMonsterIdsToHealths.keySet()) {
				int health = userMonsterIdsToHealths.get(userMonsterId);
				
				conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);
				absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, health);
			}
		} else {
			//since map is not set, go through the list
			for(int i = 0; i < userMonsterIds.size(); i++) {
				long userEquipId = userMonsterIds.get(i);
				int health = currentHealths.get(i);

				conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);
				absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, health);
			}
		}

		//to prevent db update query that updates nothing
		int numUpdated = 0;
		if (!absoluteParams.isEmpty()) {
			numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_FOR_USER,
					relativeParams, absoluteParams, conditionParams, "AND");
		}
//		log.info("num userEquips updated: " + numUpdated 
//				+ ". userMonsterIds: " + userMonsterIds);
//		if (numUpdated == userMonsterIds.size()*2) {
//			return true;
//		}
//		return false;
		return numUpdated;
	}

	public int updateUserAndEquipFail(int userId, int equipId, int failIncrement) {
		Map <String, Object> insertParams = new HashMap<String, Object>();

		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__USER_ID, userId);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__MONSTER_ID, equipId);
		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__NUM_FAILS, failIncrement);

		Map<String, Object> columnsToUpdate = new HashMap<String, Object>();
		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__NUM_FAILS, failIncrement);

		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdate(DBConstants.TABLE_MONSTER_EVOLVING_FAIL_FOR_USER, insertParams, 
				columnsToUpdate, null);//DBConstants.USER_CITIES__CURRENT_RANK, increment);

		return numUpdated;
	}
	
	public int updateUserMonsterHealing(int userId, List<MonsterHealingForUser> monsters) {
		String tableName = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

		for (MonsterHealingForUser mhfu : monsters) {
			Map <String, Object> aRow = new HashMap<String, Object>();
			
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__USER_ID, userId);
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID, mhfu.getMonsterForUserId());
			
			Date d = mhfu.getExpectedStartTime();
			Timestamp startTime = new Timestamp(d.getTime());
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__EXPECTED_START_TIME, startTime);
			
//			d = mhfu.getQueuedTime();
//			Timestamp queuedTime = new Timestamp(d.getTime());
//			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME, queuedTime);
			newRows.add(aRow);
		}
		
		log.info("newRows=" + newRows);
		
		
		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

		log.info("num monster_healing updated: " + numUpdated 
				+ ". Number of monster_healing: " + monsters.size());
		return numUpdated;
	}
	
	@Override
	public int updateUserMonsterTeamSlotNum(List<Long> userMonsterIdList, List<Integer> teamSlotNumList) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		
		int size = userMonsterIdList.size();
		
		for (int i = 0; i < size; i++) {
			long userMonsterId = userMonsterIdList.get(i);
			int teamSlotNum = teamSlotNumList.get(i);
			conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);
			absoluteParams.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, teamSlotNum);
		}

		int numUpdated = DBConnection.get().updateTableRows(tableName, null,
				absoluteParams, conditionParams, "AND");

		return numUpdated;
	}
	
	@Override
	public int updateUserMonsterEnhancing(int userId, List<MonsterEnhancingForUser> monsters) {
		String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		
		log.info("monsters enhancing for user=" + monsters);

		for (MonsterEnhancingForUser mhfu : monsters) {
			Map <String, Object> aRow = new HashMap<String, Object>();
			
			aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID, userId);
			aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID, mhfu.getMonsterForUserId());
			
			Date d = mhfu.getExpectedStartTime();
			if (null != d) {
				Timestamp startTime = new Timestamp(d.getTime());
				aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME, startTime);
			} else {
				aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME, null);
			}
//			d = mhfu.getQueuedTime();
//			Timestamp queuedTime = new Timestamp(d.getTime());
//			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME, queuedTime);
			newRows.add(aRow);
		}
		
		log.info("newRows=" + newRows);
		
		
		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

		log.info("num monster_enhancing updated: " + numUpdated 
				+ ". Number of monster_enhancing: " + monsters.size());
		return numUpdated;
	}
  
}
