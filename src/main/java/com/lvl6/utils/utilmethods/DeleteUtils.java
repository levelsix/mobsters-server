package com.lvl6.utils.utilmethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  
  ////@CacheEvict(value ="specificUserEquip", key="#userEquipId")
  public boolean deleteUserEquip(long userEquipId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.MONSTER_FOR_USER__ID, userEquipId);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_MONSTER_FOR_USER, conditionParams, "and");
    if (numDeleted == 1) {
      return true;
    }
    return false;
  }
  
  public boolean deleteUserEquips(List<Long> userEquipIds) {
    String tableName = DBConstants.TABLE_MONSTER_FOR_USER;
    List<String> questions = new ArrayList<String>();
    for(long userEquipId : userEquipIds) {
      questions.add("?");
    }
    
    String delimiter = ",";
    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.MONSTER_FOR_USER__ID
    + " IN (" + StringUtils.getListInString(questions, delimiter) + ")";
    
    List values = userEquipIds; //adding generics will throw (type mismatch?) errors
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
    if(userEquipIds.size() == numDeleted) {
      return true;
    } else {
      return false;
    }
  }
  
  public boolean deleteBlacksmithAttempt(int blacksmithId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.MONSTER_EVOLVING__ID, blacksmithId);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_MONSTER_EVOLVING_FOR_USER, conditionParams, "and");
    if (numDeleted == 1) {
      return true;
    }
    return false;
  }

  @Override
  public boolean deleteQuestTaskCompletedForUser(int userId, int questId, int numTasks) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_TASKS__USER_ID, userId);
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_TASKS__QUEST_ID, questId);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_QUEST_TASK_HISTORY, conditionParams, "and");
    if (numDeleted != numTasks) {
      return false;
    }
    
    return true;  
  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.DeleteUtil#deleteUserQuestInfoInDefeatTypeJobProgressAndCompletedDefeatTypeJobs(int, int, int)
   */
  @Override
  ////@CacheEvict(value="questIdToUserDefeatTypeJobsCompletedForQuestForUserCache", key="#userId")
  public boolean deleteUserQuestInfoInDefeatTypeJobProgressAndCompletedDefeatTypeJobs(int userId, int questId, int numDefeatJobs) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__USER_ID, userId);
    conditionParams.put(DBConstants.USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__QUEST_ID, questId);

    //trust?
    DBConnection.get().deleteRows(DBConstants.TABLE_USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS, conditionParams, "and");

    conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__USER_ID, userId);
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__QUEST_ID, questId);

    int numDeleted = DBConnection.get().deleteRows(DBConstants.TABLE_USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS, conditionParams, "and");
    if (numDeleted != numDefeatJobs) {
      return false;
    }
    return true;  
  }

  /* (non-Javadoc)
   * @see com.lvl6.utils.utilmethods.DeleteUtil#deleteUserStruct(int)
   */
  @Override
  /*@Caching(evict= {
      //@CacheEvict(value="structIdsToUserStructsForUser", allEntries=true),
      //@CacheEvict(value="specificUserStruct", key="#userStructId")})*/
  public boolean deleteUserStruct(int userStructId) {
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_STRUCTS__ID, userStructId);
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
  
  public boolean deleteEquipEnhancements(List<Integer> equipEnhancementIds) {
    String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FOR_USER;
    List<String> questions = new ArrayList<String>();
    for(int id : equipEnhancementIds) {
      questions.add("?");
    }
    
    String delimiter = ",";
    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.MONSTER_ENHANCING__ID 
    + " IN (" + StringUtils.getListInString(questions, delimiter) + ")";
    
    List values = equipEnhancementIds; //adding generics will throw (type mismatch?) errors
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
    if(equipEnhancementIds.size() == numDeleted) {
      return true;
    } else {
      return false;
    }
  }
  
  //since many EquipEnhancementFeeders point to one EquipEnhancement, could just delete by EnhancementId
  public boolean deleteEquipEnhancementFeeders(List<Integer> equipEnhancementFeederIds) {
    String tableName = DBConstants.TABLE_MONSTER_ENHANCING_FEEDER;
    List<String> questions = new ArrayList<String>();
    for(int id : equipEnhancementFeederIds) {
      questions.add("?");
    }
    
    String delimiter = ",";
    String query = " DELETE FROM " + tableName + " WHERE " + DBConstants.MONSTER_ENHANCING__ID 
    + " IN (" + StringUtils.getListInString(questions, delimiter) + ")";
    
    List values = equipEnhancementFeederIds; //adding generics will throw (type mismatch?) errors
    
    int numDeleted = DBConnection.get().deleteDirectQueryNaive(query, values);
    if(equipEnhancementFeederIds.size() == numDeleted) {
      return true;
    } else {
      return false;
    }
  }

  public int deleteAllUserQuestsForUser(int userId) {
    String tableName = DBConstants.TABLE_QUEST_FOR_USER;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  public int deleteAllUserQuestsCompletedDefeatTypeJobsForUser(int userId) {
    String tableName = DBConstants.TABLE_USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  public int deleteAllUserQuestsCompletedTasksForUser(int userId) {
    String tableName = DBConstants.TABLE_QUEST_TASK_HISTORY;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_COMPLETED_TASKS__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  public int deleteAllUserQuestsDefeatTypeJobProgressForUser(int userId) {
    String tableName = DBConstants.TABLE_USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  public int deleteAllUserTasksForUser(int userId) {
    String tableName = DBConstants.TABLE_TASK_FOR_USER;
    String condDelim = "and";
    Map <String, Object> conditionParams = new HashMap<String, Object>();
    conditionParams.put(DBConstants.USER_TASK__USER_ID, userId);
    int numDeleted = DBConnection.get().deleteRows(tableName, conditionParams, condDelim);
    
    return numDeleted;
  }
  
}
