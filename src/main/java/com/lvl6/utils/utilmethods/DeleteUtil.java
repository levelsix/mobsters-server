package com.lvl6.utils.utilmethods;

import java.util.Collection;
import java.util.List;


public interface DeleteUtil {

	public abstract boolean deleteAvailableReferralCode(String referralCode);

	public abstract boolean deleteQuestTaskCompletedForUser(int userId, int questId, int numTasks);

	/*@Caching(evict = {
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			//@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
	public abstract boolean deleteUserStruct(int userStructId);
	
  public abstract boolean deleteUserEquip(long l);
  
  public abstract boolean deleteUserEquips(List<Long> userEquipIds);
  
  public abstract boolean deleteUserClanDataRelatedToClanId(int clanId, int numRowsToDelete);

  public abstract boolean deleteClanWithClanId(int clanId);
  
  public abstract boolean deleteBlacksmithAttempt(int blacksmithId);

  public abstract boolean deleteUserClan(int userId, int clanId);

  public void deleteUserClansForUserExceptSpecificClan(int userId, int clanId);
  
  public abstract boolean deleteEquipEnhancements(List<Integer> equipEnhancementIds);
  
  public abstract boolean deleteEquipEnhancementFeeders(List<Integer> equipEnhancementFeederIds);
  
  public abstract int deleteAllUserQuestsForUser(int userId);
  
  public abstract int deleteAllUserQuestsCompletedTasksForUser(int userId);
  
  public abstract int deleteTaskForUserWithTaskForUserId(long taskForUserId);
  
  public abstract int deleteTaskStagesForIds(List<Long> taskStageForUserIds);
  
  public abstract int deleteMonsterHealingForUser(int userId, List<Long> userMonsterIds);
}