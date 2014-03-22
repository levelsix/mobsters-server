package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.UserFacebookInviteForSlot;
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
//	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
//	public boolean updateUserQuestsCoinsretrievedforreq(int userId, List <Integer> questIds, int coinGain) {
//		String query = "update " + DBConstants.TABLE_QUEST_FOR_USER + " set " + DBConstants.USER_QUESTS__COINS_RETRIEVED_FOR_REQ
//				+ "=" + DBConstants.USER_QUESTS__COINS_RETRIEVED_FOR_REQ + "+? where " 
//				+ DBConstants.QUEST_FOR_USER___USER_ID + "=? and (";
//		List<Object> values = new ArrayList<Object>();
//		values.add(coinGain);
//		values.add(userId);
//		List<String> condClauses = new ArrayList<String>();
//		for (Integer questId : questIds) {
//			condClauses.add(DBConstants.QUEST_FOR_USER__QUEST_ID + "=?");
//			values.add(questId);
//		}
//		query += StringUtils.getListInString(condClauses, "or") + ")";
//		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
//		if (numUpdated == questIds.size()) {
//			return true;
//		}
//		return false;
//	}

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
		conditionParams.put(DBConstants.QUEST_FOR_USER___USER_ID, userId);
		conditionParams.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.QUEST_FOR_USER__IS_COMPLETE, true);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}  



	@Override
	public boolean updateRedeemQuestForUser(int userId, int questId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.QUEST_FOR_USER___USER_ID, userId);
		conditionParams.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.QUEST_FOR_USER__IS_REDEEMED, true);
//		absoluteParams.put(DBConstants.QUEST_FOR_USER__IS_COMPLETE, true);

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
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__ORIENTATION, orientation.getNumber());

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
//	@Override
	/*@Caching(evict={//@CacheEvict(value="unredeemedAndRedeemedUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="incompleteUserQuestsForUser", key="#userId"),
      //@CacheEvict(value="unredeemedUserQuestsForUser", key="#userId")})*/
//	public boolean updateUserQuestsSetCompleted(int userId, int questId, boolean setTasksCompleteTrue, boolean setDefeatTypeJobsCompleteTrue) {
//		Map <String, Object> conditionParams = new HashMap<String, Object>();
//		conditionParams.put(DBConstants.QUEST_FOR_USER___USER_ID, userId);
//		conditionParams.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);
//
//		Map <String, Object> absoluteParams = new HashMap<String, Object>();
//		if (setTasksCompleteTrue) {
//			absoluteParams.put(DBConstants.QUEST_FOR_USER__TASKS_COMPLETE, true); 
//		}
//		if (setDefeatTypeJobsCompleteTrue) {
//			absoluteParams.put(DBConstants.QUEST_FOR_USER__DEFEAT_TYPE_JOBS_COMPLETE, true); 
//		}
//
//		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
//				conditionParams, "and");
//		if (numUpdated == 1) {
//			return true;
//		}
//		return false;
//	}



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

	public boolean updateBeginUpgradingUserStruct(int userStructId,
  		int newStructId, Timestamp upgradeTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		Map <String, Object> relativeParams = null; //new HashMap<String, Object>();
		
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, newStructId);
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME, upgradeTime);
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, false);
		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, relativeParams, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
		
	}
	
	@Override
	public boolean updateSpeedupUpgradingUserStruct(int userStructId, Timestamp lastRetrievedTime) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);
		
		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		Map <String, Object> relativeParams = null; //new HashMap<String, Object>();
		
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED, lastRetrievedTime);
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, true);
		
		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_STRUCTURE_FOR_USER, relativeParams, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateUserStructsBuildingIscomplete(int userId, 
			List<StructureForUser> userStructs, List<Timestamp> newRetrievedTimes) {
		String tableName = DBConstants.TABLE_STRUCTURE_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for (int index = 0; index < newRetrievedTimes.size(); index++) {
			StructureForUser userStruct = userStructs.get(index); 
			Timestamp lastRetrievedTime = newRetrievedTimes.get(index);
			int structId = userStruct.getStructId();
			
			//not really necessary to set them all the unchanging ones, but let's be safe
			Map<String, Object> newRow = new HashMap<String, Object>();
			newRow.put(DBConstants.STRUCTURE_FOR_USER__ID, userStruct.getId());
			newRow.put(DBConstants.STRUCTURE_FOR_USER__USER_ID, userId);
			newRow.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, structId);
			newRow.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED, lastRetrievedTime);
			CoordinatePair coord = userStruct.getCoordinates();
			float xCoordinate = coord.getX();
			float yCoordinate = coord.getY();
			newRow.put(DBConstants.STRUCTURE_FOR_USER__X_COORD, xCoordinate);
			newRow.put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, yCoordinate);
			newRow.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, true);
			newRow.put(DBConstants.STRUCTURE_FOR_USER__ORIENTATION, userStruct.getOrientation().getNumber());
//			newRow.put(DBConstants.STRUCTURE_FOR_USER__UPGRADE_START_TIME, userStruct.getUpgradeStartTime());
			
			newRows.add(newRow);
		}
		//determine which columns should be replaced
		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED);
//		replaceTheseColumns.add(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME);
		replaceTheseColumns.add(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE);
		
		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);
		
		log.info("num userStructs updated: " + numUpdated  + ". Number of userStructs: " +
				userStructs.size() + "\t userStructs=" + userStructs);
		if (numUpdated == userStructs.size()*2) {
			return true;
		}
		return false;
	}

	/*
	 * used for updating last retrieved user struct time
	 */

	@Override
	public boolean updateUserStructsLastretrieved(Map<Integer, Timestamp> userStructIdsToLastRetrievedTime,
			Map<Integer, StructureForUser> structIdsToUserStructs) {
		String tableName = DBConstants.TABLE_STRUCTURE_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for(Integer userStructId : userStructIdsToLastRetrievedTime.keySet()) {
			Timestamp lastRetrievedTime = userStructIdsToLastRetrievedTime.get(userStructId);
			StructureForUser us = structIdsToUserStructs.get(userStructId);

			Map<String, Object> aRow = new HashMap<String, Object>();
			aRow.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);
			aRow.put(DBConstants.STRUCTURE_FOR_USER__USER_ID, us.getUserId());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__STRUCT_ID, us.getStructId());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED, lastRetrievedTime);
			CoordinatePair cp = us.getCoordinates();
			aRow.put(DBConstants.STRUCTURE_FOR_USER__X_COORD, cp.getX());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, cp.getY());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__PURCHASE_TIME, us.getPurchaseTime());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__IS_COMPLETE, us.isComplete());
			aRow.put(DBConstants.STRUCTURE_FOR_USER__ORIENTATION, us.getOrientation().getNumber());

			newRows.add(aRow);
		}

//		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);
		//determine which columns should be replaced
		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.STRUCTURE_FOR_USER__LAST_RETRIEVED);
		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);

		log.info("num userStructs updated: " + numUpdated 
				+ ". Number of userStructs: " + userStructIdsToLastRetrievedTime.size());
		
		int maxNum = userStructIdsToLastRetrievedTime.size()*2;
		int minNum = userStructIdsToLastRetrievedTime.size();
		if (numUpdated >= minNum && numUpdated <= maxNum) {
			return true;
		}
		return false;
	}
	
	/*
   * used for upgrading user struct's fb invite level
   */
	@Override
  public boolean updateUserStructLevel(int userStructId, int fbInviteLevelChange) {
		String tableName = DBConstants.TABLE_STRUCTURE_FOR_USER;
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);

		Map <String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.STRUCTURE_FOR_USER__FB_INVITE_STRUCT_LVL,
				fbInviteLevelChange);

		int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
				null, conditionParams, "and");
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
		conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__X_COORD, coordinates.getX());
		absoluteParams.put(DBConstants.STRUCTURE_FOR_USER__Y_COORD, coordinates.getY());

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
//		if (ownerId > 0)
//			absoluteParams.put(DBConstants.CLANS__OWNER_ID, ownerId);
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
				+ DBConstants.USER__GEMS + "="+ DBConstants.USER__GEMS + "+?"
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

	public int incrementUserTaskNumRevives(long userTaskId, int numRevivesDelta) {
		String tableName = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TASK_FOR_USER_ONGOING__ID, userTaskId);

		Map<String, Object> relativeParams = new HashMap<String, Object>();
		relativeParams.put(DBConstants.TASK_FOR_USER_ONGOING__NUM_REVIVES, numRevivesDelta);
		
		Map<String, Object> absoluteParams = null;

		int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
				absoluteParams, conditionParams, "AND");
		
		return numUpdated;
	}
	
	//if expectedHealths contains the exact same info as userMonsterIdsToHealths
	//if map isn't set then list is used
	public int updateUserMonstersHealth(Map<Long, Integer> userMonsterIdsToHealths) {
		if (null == userMonsterIdsToHealths || userMonsterIdsToHealths.isEmpty()) {
			return 0;
		}

		List<Object> values = new ArrayList<Object>();

		//these specify the columns this transaction looks at to properly update rows
		List<String> clauses = new ArrayList<String>();
		clauses.add(DBConstants.MONSTER_FOR_USER__ID);
		//		clauses.add(DBConstants.MONSTER_FOR_USER__USER_ID); //not necessary since id is used
		String currentHealth = DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH;
		clauses.add(currentHealth);
		String columns = StringUtils.getListInString(clauses, ",");

		//this holds the (...) clauses that go after "VALUES"
		clauses.clear();
		//if map(userMonsterId -> expectedHealth) is set then use it
		//this generates the (...) clauses that go after "VALUES" 
		for (long userMonsterId : userMonsterIdsToHealths.keySet()) {
			int health = userMonsterIdsToHealths.get(userMonsterId);
			String subclause = "(?,?)";
			clauses.add(subclause);
			//so mysql can use these values in the prepared query
			values.add(userMonsterId);
			values.add(health);
		}
		
		//QUERY GENERATION
		String query =
				"INSERT INTO " 
						+ DBConstants.TABLE_MONSTER_FOR_USER + " (" + columns +
				") VALUES ";
		String valuesList = StringUtils.getListInString(clauses, ",");
		query += valuesList + 
				" ON DUPLICATE KEY UPDATE " +
				currentHealth + "=values(" + currentHealth +");";

		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
		return numUpdated;
	}

//	public int updateUserAndEquipFail(int userId, int equipId, int failIncrement) {
//		Map <String, Object> insertParams = new HashMap<String, Object>();
//
//		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__USER_ID, userId);
//		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__MONSTER_ID, equipId);
//		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__NUM_FAILS, failIncrement);
//
//		Map<String, Object> columnsToUpdate = new HashMap<String, Object>();
//		insertParams.put(DBConstants.MONSTER_EVOLVING_FAIL_FOR_USER__NUM_FAILS, failIncrement);
//
//		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdate(DBConstants.TABLE_MONSTER_EVOLVING_FAIL_FOR_USER, insertParams, 
//				columnsToUpdate, null);//DBConstants.USER_CITIES__CURRENT_RANK, increment);
//
//		return numUpdated;
//	}
	
	public int updateUserMonsterHealing(int userId, List<MonsterHealingForUser> monsters) {
		String tableName = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();

		for (MonsterHealingForUser mhfu : monsters) {
			Map <String, Object> aRow = new HashMap<String, Object>();
			
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__USER_ID, userId);
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID, mhfu.getMonsterForUserId());
			
			//monster may just be queued in the hospital but not started
			Date d = mhfu.getQueuedTime();
			Timestamp queuedTime = null;
			if (null != d) {
				queuedTime = new Timestamp(d.getTime());
			}
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__QUEUED_TIME, queuedTime);
			
//			int userStructHospitalId = mhfu.getUserStructHospitalId();
//			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__USER_STRUCT_HOSPITAL_ID, userStructHospitalId);
			float healthProgress = mhfu.getHealthProgress();
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS, healthProgress);
			int priority = mhfu.getPriority();
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__PRIORITY, priority);
			
			newRows.add(aRow);
		}
		
		log.info("newRows=" + newRows);
		
		
		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

		log.info("num monster_healing updated: " + numUpdated 
				+ ". Number of monster_healing: " + monsters.size());
		return numUpdated;
	}
	
	//only works for erasing multiple monsters team slot num
	@Override
	public int updateNullifyUserMonstersTeamSlotNum(List<Long> userMonsterIdList, List<Integer> teamSlotNumList) {
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
	public int updateUserMonsterTeamSlotNum(long userMonsterId, int teamSlotNum) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, teamSlotNum);

		int numUpdated = DBConnection.get().updateTableRows(tableName, null,
				absoluteParams, conditionParams, "AND");

		return numUpdated;
	}
	
	@Override
	public int nullifyMonstersTeamSlotNum(List<Long> userMonsterIds, int newTeamSlotNum) {
		List<Object> values = new ArrayList<Object>();
		String query = "UPDATE " + DBConstants.TABLE_MONSTER_FOR_USER + " SET "
				+ DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM + "=? "+ "where " +
				DBConstants.MONSTER_FOR_USER__ID + " in (";
		
		List<String> questions = Collections.nCopies(userMonsterIds.size(), "?");
		query += StringUtils.getListInString(questions, ",") + ");";

		values.add(newTeamSlotNum);
		values.addAll(userMonsterIds);

		log.info(query + " with values " +values);
		int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
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
			long monsterForUserId = mhfu.getMonsterForUserId();
			aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID, monsterForUserId);
			
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
			int enhancingCost = mhfu.getEnhancingCost();
			aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COST, enhancingCost);
			
			newRows.add(aRow);
		}
		
		log.info("newRows=" + newRows);
		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

		log.info("num monster_enhancing updated: " + numUpdated 
				+ ". Number of monster_enhancing: " + monsters.size());
		return numUpdated;
	}
	
//	update a user equip after enhancing
		@Override
	public int updateUserMonsterExpAndLvl(long userEquipId, int newExp, int newLvl) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, newExp);
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, newLvl);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		return numUpdated;
	}

		@Override
		public int updateUserMonsterNumPieces(int userId,
				Collection<MonsterForUser> monsterForUserList, String updateReason,
				Date combineStartDate) {
			Timestamp combineStartTime = new Timestamp(combineStartDate.getTime()); 
			String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			
			for (MonsterForUser mfu : monsterForUserList) {
				Map <String, Object> aRow = new HashMap<String, Object>();
				
				aRow.put(DBConstants.MONSTER_FOR_USER__ID, mfu.getId());
				aRow.put(DBConstants.MONSTER_FOR_USER__USER_ID, mfu.getUserId());
				aRow.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, mfu.getMonsterId());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, mfu.getCurrentExp());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, mfu.getCurrentLvl());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, mfu.getCurrentHealth());
				aRow.put(DBConstants.MONSTER_FOR_USER__NUM_PIECES, mfu.getNumPieces());
				aRow.put(DBConstants.MONSTER_FOR_USER__IS_COMPLETE, mfu.isComplete());
				aRow.put(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME, combineStartTime);
				aRow.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, mfu.getTeamSlotNum());
				aRow.put(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES, updateReason);
				newRows.add(aRow);
			}
			log.info("newRows=" + newRows);
			int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

			log.info("num monster_for_user updated: " + numUpdated 
					+ ". Number of monster_for_user: " + monsterForUserList.size());
			return numUpdated;
		}

		@Override
		public int updateCompleteUserMonster(List<Long> userMonsterIds) {
			String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
			int size = userMonsterIds.size();
			List<String> questions = Collections.nCopies(size, "?");
			
			String query = "UPDATE " + tableName + " SET " + DBConstants.MONSTER_FOR_USER__IS_COMPLETE 
					+ "=? WHERE " + DBConstants.MONSTER_FOR_USER__ID + " IN (";
			List<Object> values = new ArrayList<Object>();
			values.add(true);
			values.addAll(userMonsterIds);
			
			query += StringUtils.getListInString(questions, ",") + ");";
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			
			return numUpdated;
		}

		@Override
		public int updateUserFacebookInviteForSlotAcceptTime(String recipientFacebookId,
				List<Integer> acceptedInviteIds, Timestamp acceptTime) {
			String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
			int size = acceptedInviteIds.size();
			List<String> questions = Collections.nCopies(size, "?");
			List<Object> values = new ArrayList<Object>();
			
			StringBuilder querySb = new StringBuilder();
			querySb.append("UPDATE ");
			querySb.append(tableName);
			querySb.append(" SET ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED);
			querySb.append("=? WHERE ");
			values.add(acceptTime);
			
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
			querySb.append(" IN (");
			String questionsStr = StringUtils.csvList(questions);
			querySb.append(questionsStr);
			querySb.append(") AND ");
			values.addAll(acceptedInviteIds);
			
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID);
			querySb.append("=?");
			values.add(recipientFacebookId);
			
			String query = querySb.toString();
			log.info("\t\t\t\t updateUserFacebookInviteForSlotAcceptTime query=" + query +
					"\t values=" + values);
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			
			return numUpdated;
		}
		
		@Override
		public int updateRedeemUserFacebookInviteForSlot(Timestamp redeemTime,
				List<UserFacebookInviteForSlot> redeemedInvites) {
			String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
			int amount = redeemedInvites.size();
			List<Integer> ids = new ArrayList<Integer>();
			
			for(UserFacebookInviteForSlot invite : redeemedInvites) {
				int id = invite.getId();
				ids.add(id);
			}
			List<String> questions = Collections.nCopies(amount, "?");
			List<Object> values = new ArrayList<Object>();
			
			StringBuilder querySb = new StringBuilder();
			querySb.append("UPDATE ");
			querySb.append(tableName);
			querySb.append(" SET ");
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
//			querySb.append("=?, ");
//			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__IS_REDEEMED);
			querySb.append("=? WHERE ");
			values.add(redeemTime);
//			values.add(true);
			
			querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
			querySb.append(" IN (");
			String questionsStr = StringUtils.csvList(questions);
			querySb.append(questionsStr);
			querySb.append(")");
			values.addAll(ids);
			
			String query = querySb.toString();
			log.info("\t\t\t\t updateUserFacebookInviteForSlotAcceptTime query=" + query +
					"\t values=" + values);
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			
			return numUpdated;
		}

		@Override
		public int updateUserItems(int userId, Map<Integer, ItemForUser> itemIdsToUpdatedItems) {
			String tableName = DBConstants.TABLE_ITEM_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(Integer itemId : itemIdsToUpdatedItems.keySet()) {
				ItemForUser ifu = itemIdsToUpdatedItems.get(itemId);

				Map<String, Object> aRow = new HashMap<String, Object>();
				aRow.put(DBConstants.ITEM_FOR_USER__USER_ID, ifu.getUserId());
				aRow.put(DBConstants.ITEM_FOR_USER__ITEM_ID, ifu.getItemId());
				aRow.put(DBConstants.ITEM_FOR_USER__QUANTITY, ifu.getQuantity());

				newRows.add(aRow);
			}

//			int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);
			//determine which columns should be replaced
			Set<String> replaceTheseColumns = new HashSet<String>();
			replaceTheseColumns.add(DBConstants.ITEM_FOR_USER__QUANTITY);
			int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
					tableName, newRows, replaceTheseColumns);
			
			return numUpdated;
		}

		@Override
		public int updateClanEventPersistentForClanStageStartTime(int clanId,
				Timestamp curTime) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
			Map <String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);

			Map<String, Object> relativeParams = null;
			Map <String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME,
					curTime);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME,
					curTime);

			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			return numUpdated;
		}
		
		@Override
		public int updateClanEventPersistentForClanGoToNextStage(int clanId, int crsId, int crsmId) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
			Map <String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);

			Map<String, Object> relativeParams = null;
			Map <String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID, crsId);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME,
					null);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID, crsmId);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME,
					null);

			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			return numUpdated;
		}
		
		@Override
		public int updateClanEventPersistentForUserGoToNextStage(int crsId, int crsmId,
				Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(Integer userId : userIdToCepfu.keySet()) {
				ClanEventPersistentForUser cepfu = userIdToCepfu.get(userId);

				Map<String, Object> aRow = new HashMap<String, Object>();
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, cepfu.getUserId());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, cepfu.getClanId());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID, cepfu.getCrId());
				
				int newCrDmgDone = cepfu.getCrDmgDone() + cepfu.getCrsDmgDone() +
						cepfu.getCrsmDmgDone();
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE, newCrDmgDone);
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID, crsId);
				
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE, 0);
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID, crsmId);
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE, 0);

				newRows.add(aRow);
			}

			//determine which columns should be replaced
			Set<String> replaceTheseColumns = new HashSet<String>();
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
			
			int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
					tableName, newRows, replaceTheseColumns);
			
			return numUpdated;
		}
		
		@Override
		public int updateClanEventPersistentForClanGoToNextMonster(int clanId,
	  		int crsmId, Timestamp curTime) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
			Map <String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);

			Map<String, Object> relativeParams = null;
			Map <String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID, crsmId);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME,
					curTime);

			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			return numUpdated;
		}

		@Override
		public int updateClanEventPersistentForUsersGoToNextMonster(int crsId,
	  		int crsmId, Map<Integer, ClanEventPersistentForUser> userIdToCepfu) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(Integer userId : userIdToCepfu.keySet()) {
				ClanEventPersistentForUser cepfu = userIdToCepfu.get(userId);

				Map<String, Object> aRow = new HashMap<String, Object>();
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, cepfu.getUserId());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID, cepfu.getClanId());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID, cepfu.getCrId());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE, cepfu.getCrDmgDone());
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID, crsId);
				
				int newCrsDmgDone = cepfu.getCrsDmgDone() + cepfu.getCrsmDmgDone();
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE, newCrsDmgDone);
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID, crsmId);
				aRow.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE, 0);

				newRows.add(aRow);
			}

//			int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);
			//determine which columns should be replaced
			Set<String> replaceTheseColumns = new HashSet<String>();
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID);
			replaceTheseColumns.add(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE);
			
			int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
					tableName, newRows, replaceTheseColumns);
			
			return numUpdated;
		}
		
		@Override
		public int updateClanEventPersistentForUserCrsmDmgDone(int userId, int dmgDealt,
				int crsId, int crsmId) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID, userId);

			Map<String, Object> relativeParams = new HashMap<String, Object>();
			relativeParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE,
					dmgDealt);
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID, crsId);
			absoluteParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID, crsmId);
			
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updatePvpBattleHistoryExactRevenge(int historyAttackerId,
	  		int historyDefenderId, Timestamp battleEndTime) {
			String tableName = DBConstants.TABLE_PVP_BATTLE_HISTORY;
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.PVP_BATTLE_HISTORY__ATTACKER_ID, historyAttackerId);
			conditionParams.put(DBConstants.PVP_BATTLE_HISTORY__DEFENDER_ID, historyDefenderId);
			conditionParams.put(DBConstants.PVP_BATTLE_HISTORY__BATTLE_END_TIME, battleEndTime);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.PVP_BATTLE_HISTORY__EXACTED_REVENGE, true);
			
			Map<String, Object> relativeParams = null;
			
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updateObstacleForUserRemovalTime(int ofuId, Timestamp clientTime) {
			String tableName = DBConstants.TABLE_OBSTACLE_FOR_USER;
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.OBSTACLE_FOR_USER__ID, ofuId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.OBSTACLE_FOR_USER__REMOVAL_TIME, clientTime);
			
			Map<String, Object> relativeParams = null;
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updateClan(int clanId, boolean isChangeDescription, String description,
				boolean isChangeJoinType, boolean requestToJoinRequired, boolean isChangeIcon,
				int iconId) {
			
			String tableName = DBConstants.TABLE_CLANS;
			
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLANS__ID, clanId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			if (isChangeDescription) {
				absoluteParams.put(DBConstants.CLANS__DESCRIPTION, description);
				
			}
			if (isChangeJoinType) {
				absoluteParams.put(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED, requestToJoinRequired);
				
			}
			if (isChangeIcon) {
				absoluteParams.put(DBConstants.CLANS__CLAN_ICON_ID, iconId);
				
			}
			
			Map<String, Object> relativeParams = null;
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
}
