package com.lvl6.utils.utilmethods;

import java.util.List;


public interface DeleteUtil {

	public abstract boolean deleteAvailableReferralCode(String referralCode);

	/*@Caching(evict = {
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			//@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
	public abstract boolean deleteUserStruct(int userStructId);
	
  public abstract boolean deleteUserClanDataRelatedToClanId(int clanId, int numRowsToDelete);

  public abstract boolean deleteClanWithClanId(int clanId);
  
//  public abstract boolean deleteBlacksmithAttempt(int blacksmithId);

  public abstract boolean deleteUserClan(int userId, int clanId);

  public void deleteUserClansForUserExceptSpecificClan(int userId, int clanId);
  
  public abstract int deleteAllUserQuestsForUser(int userId);
  
  public abstract int deleteTaskForUserOngoingWithTaskForUserId(long taskForUserId);
  
  public abstract int deleteTaskStagesForUserWithIds(List<Long> taskStageForUserIds);
  
  public abstract int deleteMonsterHealingForUser(int userId, List<Long> userMonsterIds);
  
  public abstract int deleteMonsterEnhancingForUser(int userId, List<Long> userMonsterIds);

  public abstract int deleteMonsterForUser(long l);
  
  public abstract int deleteMonstersForUser(List<Long> userMonsterIds);
 
  public abstract int deleteUserFacebookInvitesForSlots(List<Integer> idsOfInvites);
  
  public abstract int deleteUnredeemedUserFacebookInvitesForUser(int userId);
  
  public abstract int deleteMonsterEvolvingForUser(long catalystUserMonsterId,
  		long userMonsterIdOne, long userMonsterIdTwo, int userId);
  
  public abstract int deleteEventPersistentForUser(int userId, int eventId);
  
  public abstract int deletePvpBattleForUser(int attackerId);
  
  public abstract int deleteClanEventPersistentForClan(int clanId);
  
  public abstract int deleteClanEventPersistentForUsers(List<Integer> userIdList);
  
  public abstract int deleteObstacleForUser(int userObstacleId);
  
  public abstract int deleteMiniJobForUser(long userMiniJobId);
  
}