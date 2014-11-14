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

import com.lvl6.info.AchievementForUser;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.QuestJobForUser;
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
	 * @see com.lvl6.utils.utilmethods.UpdateUtil#updateNullifyDeviceTokens(java.util.Set)
	 */
	@Override
	public void updateNullifyDeviceTokens(Set<String> deviceTokens) {
		if (deviceTokens != null && deviceTokens.size() > 0) {
//			String query = "update " + DBConstants.TABLE_USER + " set " + DBConstants.USER__DEVICE_TOKEN 
//					+ "=? where ";
			String query = String.format(
				"update %s set %s=? where ",
				DBConstants.TABLE_USER, DBConstants.USER__DEVICE_TOKEN);
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
	public boolean updateUserCityExpansionData(String userId, int xPosition, int yPosition,
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


	@Override
	public boolean updateUserQuestIscomplete(String userId, int questId) {
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.QUEST_FOR_USER__QUEST_ID, questId);

		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.QUEST_FOR_USER__IS_COMPLETE, true);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_QUEST_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}  

	@Override
	public int updateUserQuestJobs(String userId,
			Map<Integer, QuestJobForUser> questJobIdToQuestJob) {
		String tableName = DBConstants.TABLE_QUEST_JOB_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for (Integer questJobId : questJobIdToQuestJob.keySet()) {
			QuestJobForUser qjfu = questJobIdToQuestJob.get(questJobId); 
			
			Map<String, Object> newRow = new HashMap<String, Object>();
			newRow.put(DBConstants.QUEST_JOB_FOR_USER__USER_ID, userId);
			newRow.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_ID,
					qjfu.getQuestId());
			newRow.put(DBConstants.QUEST_JOB_FOR_USER__QUEST_JOB_ID,
					questJobId);
			
			newRow.put(DBConstants.QUEST_JOB_FOR_USER__IS_COMPLETE,
					qjfu.isComplete());
			
			newRow.put(DBConstants.QUEST_JOB_FOR_USER__PROGRESS,
					qjfu.getProgress());
			
			newRows.add(newRow);
		}
		//determine which columns should be replaced
		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.QUEST_JOB_FOR_USER__IS_COMPLETE);
		replaceTheseColumns.add(DBConstants.QUEST_JOB_FOR_USER__PROGRESS);

		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);
		
		return numUpdated;
	}

	@Override
	public boolean updateRedeemQuestForUser(String userId, int questId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
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

	@Override
	public int updateUserAchievement(String userId, Timestamp completeTime,
			Map<Integer, AchievementForUser> achievementIdToAfu) {
		
		String tableName = DBConstants.TABLE_ACHIEVEMENT_FOR_USER;

		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for (Integer achievementId : achievementIdToAfu.keySet()) {
			AchievementForUser afu = achievementIdToAfu.get(achievementId); 

			Map<String, Object> newRow = new HashMap<String, Object>();
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__USER_ID, userId);
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID,
					achievementId);
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__PROGRESS,
					afu.getProgress());
			
			boolean isComplete = afu.isComplete(); 
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__IS_COMPLETE,
					isComplete);
			
			Timestamp achievementCompleteTime = null;
			if (isComplete) {
				achievementCompleteTime = completeTime;
			}
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__TIME_COMPLETED,
					achievementCompleteTime);
			
			newRows.add(newRow);
		}
		//determine which columns should be replaced
		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.ACHIEVEMENT_FOR_USER__PROGRESS);
		replaceTheseColumns.add(DBConstants.ACHIEVEMENT_FOR_USER__IS_COMPLETE);
		replaceTheseColumns.add(DBConstants.ACHIEVEMENT_FOR_USER__TIME_COMPLETED);

		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);
		
		return numUpdated;
	}
	
	@Override
	public int updateRedeemAchievementForUser(String userId,
			Collection<Integer> achievementIds, Timestamp redeemTime) {
		String tableName = DBConstants.TABLE_ACHIEVEMENT_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for (Integer achievementId : achievementIds) {

			Map<String, Object> newRow = new HashMap<String, Object>();
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__USER_ID, userId);
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID,
					achievementId);
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__IS_REDEEMED, true);
			
			newRow.put(DBConstants.ACHIEVEMENT_FOR_USER__TIME_REDEEMED,
					redeemTime);
			
			newRows.add(newRow);
		}
		
		//determine which columns should be replaced
		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.ACHIEVEMENT_FOR_USER__IS_REDEEMED);
		replaceTheseColumns.add(DBConstants.ACHIEVEMENT_FOR_USER__TIME_REDEEMED);

		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);
		
		return numUpdated;
	}
	
	/*
	 * changin orientation
	 */
	@Override
	public boolean updateUserStructOrientation(String userStructId,
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


	@Override
	public boolean updateBeginUpgradingUserStruct(String userStructId,
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
	public boolean updateSpeedupUpgradingUserStruct(String userStructId, Timestamp lastRetrievedTime) {
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
	public boolean updateUserStructsBuildingIsComplete(String userId, 
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
			newRow.put(DBConstants.STRUCTURE_FOR_USER__ORIENTATION, userStruct.getOrientation());
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
	public boolean updateUserStructsLastRetrieved(Map<String, Timestamp> userStructIdsToLastRetrievedTime,
			Map<String, StructureForUser> structIdsToUserStructs) {
		String tableName = DBConstants.TABLE_STRUCTURE_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for(String userStructId : userStructIdsToLastRetrievedTime.keySet()) {
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
			aRow.put(DBConstants.STRUCTURE_FOR_USER__ORIENTATION, us.getOrientation());

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
  public boolean updateUserStructLevel(String userStructId, int fbInviteLevelChange) {
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
	@Override
	public boolean updateUserStructCoord(String userStructId, CoordinatePair coordinates) {
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
	public boolean incrementNumberOfLockBoxesForLockBoxEvent(String userId, int eventId, int increment) {
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

	/*@Override
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
	}*/

	@Override
	public boolean updateUserClanStatus(String userId, String clanId, UserClanStatus status) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.CLAN_FOR_USER__STATUS, status.name());

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_CLAN_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	@Override
	public int updateUserClanStatuses(String clanId, List<String> userIdList,
			List<UserClanStatus> statuses) {
		String tableName = DBConstants.TABLE_CLAN_FOR_USER;
		
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		for(int index = 0; index < userIdList.size(); index++) {
		  String userId = userIdList.get(index);
			String status = statuses.get(index).name();

			Map<String, Object> aRow = new HashMap<String, Object>();
			aRow.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);
			aRow.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
			aRow.put(DBConstants.CLAN_FOR_USER__STATUS, status);

			newRows.add(aRow);
		}

		Set<String> replaceTheseColumns = new HashSet<String>();
		replaceTheseColumns.add(DBConstants.CLAN_FOR_USER__STATUS);
		int numUpdated = DBConnection.get().insertOnDuplicateKeyUpdateColumnsAbsolute(
				tableName, newRows, replaceTheseColumns);
		
		return numUpdated;
		
	}
	
	@Override
	public boolean updateUsersAddDiamonds(List<String> userIds, int diamonds) {
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

	@Override
	public boolean updateLeaderboardEventSetRewardGivenOut(int eventId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TOURNAMENT_EVENT__ID, eventId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.TOURNAMENT_EVENT__REWARDS_GIVEN_OUT, 1);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_TOURNAMENT_EVENT_CONFIG, null, absoluteParams, 
				conditionParams, "or");
		if (numUpdated == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateClanJoinTypeForClan(String clanId, boolean requestToJoinRequired) {
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

	@Override
	public int incrementUserTaskNumRevives(String userTaskId, int numRevivesDelta) {
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
	
	@Override
	public int updateUserTaskTsId(String userTaskId, int nuTaskStageId) {
		String tableName = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.TASK_FOR_USER_ONGOING__ID, userTaskId);
		
		Map<String, Object> relativeParams = null;
		
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.TASK_FOR_USER_ONGOING__TASK_STAGE_ID,
				nuTaskStageId);
		
		int numUpdated = DBConnection.get().updateTableRows(tableName,
				relativeParams, absoluteParams, conditionParams, "AND");
		
		return numUpdated;
	}
	
	//if expectedHealths contains the exact same info as userMonsterIdsToHealths
	//if map isn't set then list is used
	@Override
	public int updateUserMonstersHealth(Map<String, Integer> userMonsterIdsToHealths) {
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
		for (String userMonsterId : userMonsterIdsToHealths.keySet()) {
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

	@Override
	public int updateUserMonsterHealing(String userId, List<MonsterHealingForUser> monsters) {
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
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS,
				mhfu.getHealthProgress());
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__PRIORITY,
				mhfu.getPriority());
			aRow.put(DBConstants.MONSTER_HEALING_FOR_USER__ELAPSED_SECONDS,
				mhfu.getElapsedSeconds());
			
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
	public int updateNullifyUserMonstersTeamSlotNum(List<String> userMonsterIdList, List<Integer> teamSlotNumList) {
		String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		Map<String, Object> absoluteParams = new HashMap<String, Object>();
		
		int size = userMonsterIdList.size();
		
		for (int i = 0; i < size; i++) {
		  String userMonsterId = userMonsterIdList.get(i);
			int teamSlotNum = teamSlotNumList.get(i);
			conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);
			absoluteParams.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, teamSlotNum);
		}

		int numUpdated = DBConnection.get().updateTableRows(tableName, null,
				absoluteParams, conditionParams, "AND");

		return numUpdated;
	}
	
	@Override
	public int updateUserMonsterTeamSlotNum(String userMonsterId, int teamSlotNum) {
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
	public int nullifyMonstersTeamSlotNum(List<String> userMonsterIds, int newTeamSlotNum) {
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
	public int updateUserMonsterEnhancing(String userId, List<MonsterEnhancingForUser> monsters) {
		String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
		List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
		
		log.info(String.format("monsters enhancing for user=%s", monsters));

		for (MonsterEnhancingForUser mhfu : monsters) {
			Map <String, Object> aRow = new HashMap<String, Object>();
			
			aRow.put(DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID, userId);
			String monsterForUserId = mhfu.getMonsterForUserId();
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
		
		log.info(String.format("newRows=%s", newRows));
		int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

		log.info(String.format(
			"num monster_enhancing updated: %s. Number of monster_enhancing: %s",
			numUpdated, monsters.size()));
		return numUpdated;
	}
	
	@Override
	public int updateCompleteEnhancing(String userId, String curEnhancingMfuId) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID, curEnhancingMfuId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.MONSTER_ENHANCING_FOR_USER__ENHANCING_COMPLETE, true);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		return numUpdated;
	}
	
//	update a user monster after enhancing
	@Override
	public int updateUserMonsterExpAndLvl(String userEquipId, int newExp, int newLvl, int newHp) {
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);

		Map <String, Object> absoluteParams = new HashMap<String, Object>();
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, newExp);
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, newLvl);
		absoluteParams.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, newHp);

		int numUpdated = DBConnection.get().updateTableRows(DBConstants.TABLE_MONSTER_FOR_USER, null, absoluteParams, 
				conditionParams, "and");
		return numUpdated;
	}

		@Override
		public int updateUserMonsterNumPieces(String userId,
				Collection<MonsterForUser> monsterForUserList, String updateReason,
				Date combineStartDate) {
			Timestamp combineStartTime = new Timestamp(combineStartDate.getTime()); 
			String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			
			for (MonsterForUser mfu : monsterForUserList) {
				Map <String, Object> aRow = new HashMap<String, Object>();
				String newSourceOfPieces = mfu.getSourceOfPieces();
				if (null != newSourceOfPieces) {
					newSourceOfPieces += updateReason;
				} else {
					newSourceOfPieces = updateReason;
				}
				
				aRow.put(DBConstants.MONSTER_FOR_USER__ID, mfu.getId());
				aRow.put(DBConstants.MONSTER_FOR_USER__USER_ID, mfu.getUserId());
				aRow.put(DBConstants.MONSTER_FOR_USER__MONSTER_ID, mfu.getMonsterId());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_EXPERIENCE, mfu.getCurrentExp());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_LEVEL, mfu.getCurrentLvl());
				aRow.put(DBConstants.MONSTER_FOR_USER__CURRENT_HEALTH, mfu.getCurrentHealth());
				aRow.put(DBConstants.MONSTER_FOR_USER__NUM_PIECES, mfu.getNumPieces());
				aRow.put(DBConstants.MONSTER_FOR_USER__HAS_ALL_PIECES, mfu.isHasAllPieces());
				aRow.put(DBConstants.MONSTER_FOR_USER__IS_COMPLETE, mfu.isComplete());
				aRow.put(DBConstants.MONSTER_FOR_USER__COMBINE_START_TIME, combineStartTime);
				aRow.put(DBConstants.MONSTER_FOR_USER__TEAM_SLOT_NUM, mfu.getTeamSlotNum());
				aRow.put(DBConstants.MONSTER_FOR_USER__SOURCE_OF_PIECES, newSourceOfPieces);
				newRows.add(aRow);
			}
			log.info("newRows=" + newRows);
			int numUpdated = DBConnection.get().replaceIntoTableValues(tableName, newRows);

			log.info("num monster_for_user updated: " + numUpdated 
					+ ". Number of monster_for_user: " + monsterForUserList.size());
			return numUpdated;
		}

		@Override
		public int updateCompleteUserMonster(List<String> userMonsterIds) {
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
				List<String> acceptedInviteIds, Timestamp acceptTime) {
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
			List<String> ids = new ArrayList<String>();
			
			for(UserFacebookInviteForSlot invite : redeemedInvites) {
				String id = invite.getId();
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

		/*
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
		}*/

		@Override
		public int updateClanEventPersistentForClanStageStartTime(String clanId,
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
		public int updateClanEventPersistentForClanGoToNextStage(String clanId, int crsId, int crsmId) {
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
				Map<String, ClanEventPersistentForUser> userIdToCepfu) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(String userId : userIdToCepfu.keySet()) {
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
		public int updateClanEventPersistentForClanGoToNextMonster(String clanId,
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
	  		int crsmId, Map<String, ClanEventPersistentForUser> userIdToCepfu) {
			String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(String userId : userIdToCepfu.keySet()) {
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
		public int updateClanEventPersistentForUserCrsmDmgDone(String userId, int dmgDealt,
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
		public int updatePvpBattleHistoryExactRevenge(String historyAttackerId,
		    String historyDefenderId, Timestamp battleEndTime) {
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
		public int updateObstacleForUserRemovalTime(String ofuId, Timestamp clientTime) {
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
		public int updateClan(String clanId, boolean isChangeDescription, String description,
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
		
		@Override
		public int updatePvpLeagueForUserShields(String userId, Timestamp shieldEndTime,
				Timestamp inBattleEndTime) {
			String tableName = DBConstants.TABLE_PVP_LEAGUE_FOR_USER;
			
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			if (null != shieldEndTime) {
				absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME,
						shieldEndTime);
			}
			if (null != inBattleEndTime) {
				absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__BATTLE_END_TIME,
						inBattleEndTime);
			}
			
			if (absoluteParams.isEmpty()) {
				return 0;
			}
			
			Map<String, Object> relativeParams = null;
			int numUpdated = DBConnection.get().updateTableRows(tableName,
					relativeParams, absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updatePvpLeagueForUser(String userId, int newPvpLeagueId,
				int newRank, int eloChange, Timestamp shieldEndTime,
				Timestamp inBattleEndTime, int attacksWonDelta,
				int defensesWonDelta, int attacksLostDelta,
				int defensesLostDelta, float nuPvpDmgMultiplier) {
			String tableName = DBConstants.TABLE_PVP_LEAGUE_FOR_USER;

			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userId);
			
			
			Map<String, Object> relativeParams = new HashMap<String, Object>();
			if (0 != eloChange) {
				relativeParams.put(DBConstants.PVP_LEAGUE_FOR_USER__ELO, eloChange);
			}
			if (0 != attacksWonDelta) {
				relativeParams.put(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_WON,
						attacksWonDelta);
			}
			if (0 != defensesWonDelta) {
				relativeParams.put(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_WON,
						defensesWonDelta);
			}
			if (0 != attacksLostDelta) {
				relativeParams.put(DBConstants.PVP_LEAGUE_FOR_USER__ATTACKS_LOST,
						attacksLostDelta);
			}
			if (0 != defensesLostDelta) {
				relativeParams.put(DBConstants.PVP_LEAGUE_FOR_USER__DEFENSES_LOST,
						defensesLostDelta);
			}
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID,
					newPvpLeagueId);
			absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__RANK, newRank);
			if (null != shieldEndTime) {
				absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__SHIELD_END_TIME,
						shieldEndTime);
			}
			if (null != inBattleEndTime) {
				absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__BATTLE_END_TIME,
						shieldEndTime);
			}
			if (nuPvpDmgMultiplier > 0) {
				absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__MONSTER_DMG_MULTIPLIER,
					nuPvpDmgMultiplier);
			}
			
			
			int numUpdated = DBConnection.get().updateTableRows(tableName,
					relativeParams, absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updateMiniJobForUser(String userId, String userMiniJobId,
				String userMonsterIdStr, Timestamp now) {
			String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;

			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.MINI_JOB_FOR_USER__USER_ID, userId);
			conditionParams.put(DBConstants.MINI_JOB_FOR_USER__ID,
					userMiniJobId);

			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.MINI_JOB_FOR_USER__USER_MONSTER_IDS,
					userMonsterIdStr);
			absoluteParams.put(DBConstants.MINI_JOB_FOR_USER__TIME_STARTED,
					now);

			if (absoluteParams.isEmpty()) {
				return 0;
			}

			Map<String, Object> relativeParams = null;
			int numUpdated = DBConnection.get().updateTableRows(tableName,
					relativeParams, absoluteParams, conditionParams, "and");

			return numUpdated;
		}

		@Override
		public int updateMiniJobForUserCompleteTime(String userId,
		    String userMiniJobId, Timestamp now) {
			String tableName = DBConstants.TABLE_MINI_JOB_FOR_USER;

			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.MINI_JOB_FOR_USER__USER_ID, userId);
			conditionParams.put(DBConstants.MINI_JOB_FOR_USER__ID,
					userMiniJobId);

			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.MINI_JOB_FOR_USER__TIME_COMPLETED,
					now);

			Map<String, Object> relativeParams = null;
			int numUpdated = DBConnection.get().updateTableRows(tableName,
					relativeParams, absoluteParams, conditionParams, "and");

			return numUpdated;
		}
		
		@Override
		public int updateRestrictUserMonsters(String userId, List<String> userMonsterIdList) {
			String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
			int size = userMonsterIdList.size();
			List<String> questions = Collections.nCopies(size, "?");
			List<Object> values = new ArrayList<Object>();
			
			StringBuilder querySb = new StringBuilder();
			querySb.append("UPDATE ");
			querySb.append(tableName);
			querySb.append(" SET ");
			querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
			querySb.append("=? WHERE ");
			values.add(true);
			
			querySb.append(DBConstants.MONSTER_FOR_USER__ID);
			querySb.append(" IN (");
			String questionsStr = StringUtils.csvList(questions);
			querySb.append(questionsStr);
			querySb.append(") AND ");
			values.addAll(userMonsterIdList);
			
			querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
			querySb.append("=?");
			values.add(userId);

			String query = querySb.toString();
			log.info(String.format(
				"updateRestrictUserMonsters query=%s, values=%s",
				query, values));
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			
			return numUpdated;
		}
		
		@Override
		public int updateUnrestrictUserMonsters(String userId, List<String> userMonsterIdList) {
			String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
			int size = userMonsterIdList.size();
			List<String> questions = Collections.nCopies(size, "?");
			List<Object> values = new ArrayList<Object>();
			
			StringBuilder querySb = new StringBuilder();
			querySb.append("UPDATE ");
			querySb.append(tableName);
			querySb.append(" SET ");
			querySb.append(DBConstants.MONSTER_FOR_USER__RESTRICTED);
			querySb.append("=? WHERE ");
			values.add(false);
			
			querySb.append(DBConstants.MONSTER_FOR_USER__ID);
			querySb.append(" IN (");
			String questionsStr = StringUtils.csvList(questions);
			querySb.append(questionsStr);
			querySb.append(") AND ");
			values.addAll(userMonsterIdList);
			
			querySb.append(DBConstants.MONSTER_FOR_USER__USER_ID);
			querySb.append("=?");
			values.add(userId);

			String query = querySb.toString();
			log.info(String.format(
				"updateRestrictUserMonsters query=%s, values=%s",
				query, values));
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			
			return numUpdated;
		}
		
		@Override
		public int updateItemForUser(String userId, int itemId, int quantityChange) {
			String tableName = DBConstants.TABLE_ITEM_FOR_USER;
			
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.ITEM_FOR_USER__USER_ID, userId);
			conditionParams.put(DBConstants.ITEM_FOR_USER__ITEM_ID, itemId);
			
			Map<String, Object> absoluteParams = null;
			
			Map<String, Object> relativeParams = new HashMap<String, Object>();
			relativeParams.put(DBConstants.ITEM_FOR_USER__QUANTITY, quantityChange);
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
					absoluteParams, conditionParams, "and");
			
			return numUpdated;
		}
		
		@Override
		public int updateItemForUser(List<ItemForUser> ifuList) {
			String tableName = DBConstants.TABLE_ITEM_FOR_USER;
			
			List<Map<String, Object>> newRows = new ArrayList<Map<String, Object>>();
			for(ItemForUser ifu : ifuList) {

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
		public int updateTaskStageForUserNoMonsterDrop(String droplessTsfuId) {
			String tableName = DBConstants.TABLE_TASK_STAGE_FOR_USER;
			
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.TASK_STAGE_FOR_USER__ID, droplessTsfuId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED, false);

			Map<String, Object> relativeParams = null;//new HashMap<String, Object>();
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
				absoluteParams, conditionParams, "and");

			return numUpdated;
		}
		
		@Override
		public int updatePvpMonsterDmgMultiplier(String userId, float monsterDmgMultiplier) {
			String tableName = DBConstants.TABLE_PVP_LEAGUE_FOR_USER;
			
			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.PVP_LEAGUE_FOR_USER__USER_ID, userId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.PVP_LEAGUE_FOR_USER__MONSTER_DMG_MULTIPLIER, monsterDmgMultiplier);
			
			Map<String, Object> relativeParams = null;//new HashMap<String, Object>();
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
				absoluteParams, conditionParams, "and");

			return numUpdated;
		}
		
		@Override
		public int updateClanHelp(String userId, String clanId, List<String> clanHelpIds) {
			//for every ClanHelp with clanId and clanHelpId
			//append userId to the helpers property
			String tableName = DBConstants.TABLE_CLAN_HELP;
			String helpers = DBConstants.CLAN_HELP__HELPERS;
			List<String> questions = Collections.nCopies(clanHelpIds.size(), "?");
			String questionMarks = StringUtils.implode(questions, ",");
			
			String query = String.format(
				"UPDATE %s	SET %s=CONCAT(%s, ',%s')	WHERE %s=%s AND %s IN (%s)",
				tableName, helpers, helpers, userId,
				DBConstants.CLAN_HELP__CLAN_ID, clanId,
				DBConstants.CLAN_HELP__ID, questionMarks);

			List<Object> values = new ArrayList<Object>();
			values.addAll(clanHelpIds);
			
			int numUpdated = DBConnection.get().updateDirectQueryNaive(query, values);
			return numUpdated;
		}
		
		@Override
		public int closeClanHelp(String userId, String clanId) {
			String tableName = DBConstants.TABLE_CLAN_HELP;

			Map<String, Object> conditionParams = new HashMap<String, Object>();
			conditionParams.put(DBConstants.CLAN_HELP__USER_ID, userId);
			conditionParams.put(DBConstants.CLAN_HELP__CLAN_ID, clanId);
			
			Map<String, Object> absoluteParams = new HashMap<String, Object>();
			absoluteParams.put(DBConstants.CLAN_HELP__OPEN, false);
			
			Map<String, Object> relativeParams = null;//new HashMap<String, Object>();
			int numUpdated = DBConnection.get().updateTableRows(tableName, relativeParams,
				absoluteParams, conditionParams, "and");

			return numUpdated;
		}
}
