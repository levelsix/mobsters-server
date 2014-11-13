package com.lvl6.utils.utilmethods;

import java.util.List;


public interface DeleteUtil {

	public abstract boolean deleteAvailableReferralCode(String referralCode);

	/*@Caching(evict = {
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			//@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
	public abstract boolean deleteUserStruct(String userStructId);
	
  public abstract boolean deleteUserClanDataRelatedToClanId(String clanId, int numRowsToDelete);

  public abstract boolean deleteClanWithClanId(String clanId);
  
//  public abstract boolean deleteBlacksmithAttempt(int blacksmithId);

  public abstract boolean deleteUserClan(String userId, String clanId);

  public void deleteUserClansForUserExceptSpecificClan(String userId, String clanId);
  
  public abstract int deleteAllUserQuestsForUser(String userId);
  
  public abstract int deleteTaskForUserOngoingWithTaskForUserId(String taskForUserId);
  
  public abstract int deleteTaskStagesForUserWithIds(List<String> taskStageForUserIds);
  
  public abstract int deleteMonsterHealingForUser(String userId, List<String> userMonsterIds);
  
  public abstract int deleteMonsterEnhancingForUser(String userId, List<String> userMonsterIds);

  public abstract int deleteMonsterForUser(String l);
  
  public abstract int deleteMonstersForUser(List<String> userMonsterIds);
 
  public abstract int deleteUserFacebookInvitesForSlots(List<String> idsOfInvites);
  
  public abstract int deleteUnredeemedUserFacebookInvitesForUser(String userId);
  
  public abstract int deleteMonsterEvolvingForUser(String catalystUserMonsterId,
      String userMonsterIdOne, String userMonsterIdTwo, String userId);
  
  public abstract int deleteEventPersistentForUser(String userId, int eventId);
  
  public abstract int deletePvpBattleForUser(String attackerId);
  
  public abstract int deleteClanEventPersistentForClan(String clanId);
  
  public abstract int deleteClanEventPersistentForUsers(List<String> userIdList);
  
  public abstract int deleteObstacleForUser(String userObstacleId);
  
  public abstract int deleteMiniJobForUser(String userMiniJobId);
 
  public abstract int deleteClanHelp(String userId, List<String> clanHelpIdList);
  
  public abstract int deleteClanInvite(String userId, List<String> clanInviteIdList);
}