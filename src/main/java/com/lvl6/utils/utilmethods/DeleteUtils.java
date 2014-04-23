package com.lvl6.utils.utilmethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mortbay.log.Log;

import com.lvl6.properties.DBConstants;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.DBConnection;

public class DeleteUtils implements DeleteUtil {


  public static DeleteUtil get(){
    return (DeleteUtil) AppContext.getApplicationContext().getBean("deleteUtils");
  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.DeleteUtil#deleteAvailableReferralCode(java.lang.String)
   */
  @Override
  public boolean deleteAvailableReferralCode(String referralCode) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.AVAILABLE_REFERRAL_CODES__CODE, referralCode);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_REFERRAL_CODE_AVAILABLE, conditionParams, "and");
    if (numDeleted != 1) {
      return false;
    }
    return true;  
  }

//  public boolean deleteBlacksmithAttempt(int blacksmithId) {
//    Map <String, Object> conditionParams = new HashMap<String, Object>();
//    conditionParams.put(DBConstants.MONSTER_EVOLVING__ID, blacksmithId);
//
//    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER, conditionParams, "and");
//    if (numDeleted == 1) {
//      return true;
//    }
//    return false;
//  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.DeleteUtil#deleteUserStruct(int)
   */
  @Override
  /*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
  public boolean deleteUserStruct(int userStructId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.STRUCTURE_FOR_USER__ID, userStructId);
    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_STRUCTURE_FOR_USER, conditionParams, "and");
    if (numDeleted == 1) {
      return true;
    }
    return false;
  }
  
  public boolean deleteUserClanDataRelatedToClanId(int clanId, int numRowsToDelete) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_CLAN_FOR_USER, conditionParams, "and");
    if (numDeleted == numRowsToDelete) {
      return true;
    }
    return false;
  }
  
  public void deleteUserClansForUserExceptSpecificClan(int userId, int clanId) {
    String query = "delete from " + DBConstants.TABLE_CLAN_FOR_USER + " where " + DBConstants.CLAN_FOR_USER__USER_ID + "=? and " +
        DBConstants.CLAN_FOR_USER__CLAN_ID + "!=?";
    
    List<Object> values = new ArrayList<Object>();
    values.add(userId);
    values.add(clanId);
    
    DBConnection.get().deleteDirectQueryNaive(query, values);
  }
  
  
  public boolean deleteUserClan(int userId, int clanId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.CLAN_FOR_USER__CLAN_ID, clanId);
    conditionParams.put(DBConstants.CLAN_FOR_USER__USER_ID, userId);
    
    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_CLAN_FOR_USER, conditionParams, "and");
    if (numDeleted == 1) {
      return true;
    }
    return false;
  }

  
  //@CacheEvict(value="clanById", key="#clanId")
  public boolean deleteClanWithClanId(int clanId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.CLANS__ID, clanId);
    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_CLANS, conditionParams, "and");
    if (numDeleted == 1) {
      return true;
    }
    return false;
  }

  public int deleteAllUserQuestsForUser(int userId) {
    String tableName = DBConstants.TABLE_QUEST_FOR_USER;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.QUEST_FOR_USER__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  
  @Override
  public int deleteTaskForUserOngoingWithTaskForUserId(long taskForUserId) {
    String tableName = DBConstants.TABLE_TASK_FOR_USER_ONGOING;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.TASK_FOR_USER_ONGOING__ID, taskForUserId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    
    return numDeleted;
  }
  
  @Override
  public int deleteTaskStagesForUserWithIds(List<Long> taskStageForUserIds) {
  	String tableName = DBConstants.TABLE_TASK_STAGE_FOR_USER;
  	int size = taskStageForUserIds.size();
    List<String> questions = Collections.nCopies(size, "?");
    
    String delimiter = ",";
    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.TASK_STAGE_FOR_USER__ID
    + " IN (" + StringUtils.getListInString(questions, delimiter) + ");";
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, taskStageForUserIds);
    return numDeleted;
  }
  
  @Override
  public int deleteMonsterHealingForUser(int userId, List<Long> userMonsterIds) {
  	String tableName = DBConstants.TABLE_MONSTER_HEALING_FOR_USER;
  	int size = userMonsterIds.size();
  	
  	List<String> questions = Collections.nCopies(size, "?");
  	
  	String delimiter = ",";
  	String query = " DELETE FROM " + tableName + " WHERE " +
  			DBConstants.MONSTER_HEALING_FOR_USER__USER_ID + "=?" +
  			" and " + DBConstants.MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID +
  			" IN (" + StringUtils.getListInString(questions, delimiter) + ");";
  	
  	List<Object> values = new ArrayList<Object>();
  	values.add(userId);
  	values.addAll(userMonsterIds);
  	
  	Log.info("userMonsterIds=" + userMonsterIds + "\t values sent to db: " + values);
  	
  	int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
    return numDeleted;
  }
  
  @Override
  public int deleteMonsterEnhancingForUser(int userId, List<Long> userMonsterIds) {
  	String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
  	int size = userMonsterIds.size();
  	
  	List<String> questions = Collections.nCopies(size, "?");
  	
  	String delimiter = ",";
  	String query = " DELETE FROM " + tableName + " WHERE " +
  			DBConstants.MONSTER_ENHANCING_FOR_USER__USER_ID + "=?" +
  			" and " + DBConstants.MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID +
  			" IN (" + StringUtils.getListInString(questions, delimiter) + ");";
  	
  	List<Object> values = new ArrayList<Object>();
  	values.add(userId);
  	values.addAll(userMonsterIds);
  	
  	Log.info("userMonsterIds=" + userMonsterIds + "\t values sent to db: " + values);
  	
  	int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
    return numDeleted;
  }

  ////@CacheEvict(value ="specificUserEquip", key="#userEquipId")
  @Override
  public int deleteMonsterForUser(long userMonsterId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userMonsterId);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_MONSTER_FOR_USER, conditionParams, "and");
    return numDeleted;
  }
  
  @Override
  public int deleteMonstersForUser(List<Long> userMonsterIds) {
    String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
    int size = userMonsterIds.size();
    List<String> questions = Collections.nCopies(size, "?");
    
    String delimiter = ",";
    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.MONSTER_FOR_USER__ID
    + " IN (" + StringUtils.getListInString(questions, delimiter) + ")";
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, userMonsterIds);
    return numDeleted;
  }

	@Override
	public int deleteUserFacebookInvitesForSlots(List<Integer> idsOfInvites) {
		String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
    int size = idsOfInvites.size();
    List<String> questions = Collections.nCopies(size, "?");
    String questionMarks = StringUtils.csvList(questions);
    
    StringBuilder querySb = new StringBuilder();
    querySb.append("DELETE FROM ");
    querySb.append(tableName);
    querySb.append(" WHERE ");
    querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
    querySb.append(" IN (");
    querySb.append(questionMarks);
    querySb.append(")");
    String query = querySb.toString();
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, idsOfInvites);
    return numDeleted;
	}

	@Override
	public int deleteUnredeemedUserFacebookInvitesForUser(int userId) {
		String tableName = DBConstants.TABLE_USER_FACEBOOK_INVITE_FOR_SLOT;
		
		StringBuilder querySb = new StringBuilder();
		querySb.append("DELETE FROM ");
		querySb.append(tableName);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__ID);
		querySb.append(" > 0 AND ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID);
		querySb.append("=?");
		querySb.append(" AND ");
		querySb.append(DBConstants.USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED);
		querySb.append(" IS NULL");
		
		String query = querySb.toString();
		List<Integer> values = new ArrayList<Integer>();
		values.add(userId);
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
		return numDeleted;
	}

	@Override
	public int deleteMonsterEvolvingForUser(long catalystUserMonsterId,
			long userMonsterIdOne, long userMonsterIdTwo, int userId) {
		String tableName = DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER;
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID,
    		catalystUserMonsterId);
		conditionParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE,
    		userMonsterIdOne);
		conditionParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO,
    		userMonsterIdTwo);
		conditionParams.put(DBConstants.MONSTER_EVOLVING_FOR_USER__USER_ID, userId);
		
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, "and");
		return numDeleted;
	}
	
	@Override
	public int deleteEventPersistentForUser(int userId, int eventId) {
		String tableName = DBConstants.TABLE_EVENT_PERSISTENT_FOR_USER;
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__USER_ID, userId);
		conditionParams.put(DBConstants.EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID, eventId);

		int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, "and");
		return numDeleted;
	}
	
	@Override
	public int deletePvpBattleForUser(int attackerId) {
		String tableName = DBConstants.TABLE_PVP_BATTLE_FOR_USER;
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID, attackerId);

		int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, "and");
		return numDeleted;
	}
	
	@Override
	public int deleteClanEventPersistentForClan(int clanId) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN;
		
		Map <String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID, clanId);

		int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, "and");
		return numDeleted;
	}
	
	@Override
	public int deleteClanEventPersistentForUsers(List<Integer> userIdList) {
		String tableName = DBConstants.TABLE_CLAN_EVENT_PERSISTENT_FOR_USER;
		int size = userIdList.size();
    List<String> questions = Collections.nCopies(size, "?");
    String questionMarks = StringUtils.csvList(questions);
    
		StringBuilder querySb = new StringBuilder();
		querySb.append("DELETE FROM ");
		querySb.append(tableName);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID);
		querySb.append(" IN (");
    querySb.append(questionMarks);
    querySb.append(")");
    String query = querySb.toString();
		
		int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, userIdList);
		return numDeleted;
	}
	
	@Override
	public int deleteObstacleForUser(int userObstacleId) {
		String tableName = DBConstants.TABLE_OBSTACLE_FOR_USER;

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put(DBConstants.OBSTACLE_FOR_USER__ID, userObstacleId);

		int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, "and");
		return numDeleted;
	}
}
