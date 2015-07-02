package com.lvl6.utils.utilmethods;

import java.util.Collection;
import java.util.List;

import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.MonsterSnapshotForUser;

public interface DeleteUtil {

	/*@Caching(evict = {
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
			//@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
	public abstract boolean deleteUserStruct(String userStructId);

	public abstract boolean deleteUserClanDataRelatedToClanId(String clanId,
			int numRowsToDelete);

	public abstract boolean deleteClanWithClanId(String clanId);

	public abstract boolean deleteUserClan(String userId, String clanId);

	public void deleteUserClansForUserExceptSpecificClan(String userId,
			String clanId);

	public abstract int deleteAllUserQuestsForUser(String userId);

	public abstract int deleteTaskForUserOngoingWithTaskForUserId(
			String taskForUserId);

	public abstract int deleteTaskStagesForUserWithIds(
			List<String> taskStageForUserIds);

	public abstract int deleteMonsterHealingForUser(String userId,
			List<String> userMonsterIds);

	public abstract int deleteMonsterEnhancingForUser(String userId,
			List<String> userMonsterIds);

	public abstract int deleteMonsterForUser(String l);

	public abstract int deleteMonstersForUser(List<String> userMonsterIds);

	public abstract int deleteUserFacebookInvitesForSlots(
			List<String> idsOfInvites);

	public abstract int deleteUnredeemedUserFacebookInvitesForUser(String userId);

	public abstract int deleteMonsterEvolvingForUser(
			String catalystUserMonsterId, String userMonsterIdOne,
			String userMonsterIdTwo, String userId);

	public abstract int deleteEventPersistentForUser(String userId, int eventId);

	public abstract int deletePvpBattleForUser(String attackerId);

	public abstract int deleteClanEventPersistentForClan(String clanId);

	public abstract int deleteClanEventPersistentForUsers(
			List<String> userIdList);

	public abstract int deleteObstacleForUser(String userObstacleId);

	public abstract int deleteMiniJobForUser(String userMiniJobId);

	public abstract int deleteMiniJobForUser(String userId, Collection<String> userMiniJobIds);

	public abstract int deleteClanHelp(String userId,
			List<String> clanHelpIdList);

	public abstract int deleteClanInvite(String userId,
			List<String> clanInviteIdList);

	public abstract int deleteItemUsed(String userId,
			List<String> itemForUserUsageIds);

	public abstract int deleteSecretGifts(String userId, List<String> ids);

	public abstract int deleteClanAvenge(String clanId, List<String> ids);

	public abstract int deleteClanAvengeUser(String clanId, List<String> ids);

	public abstract int deleteClanMemberTeamDonationSolicitation(
			List<String> ids);

	public abstract int deleteMonsterSnapshotForUser(
			List<MonsterSnapshotForUser> snapshots);

	public int deleteMonsterSnapshotsFromUser(
			String type, List<String> idInTable);

	public abstract int deletePvpBoardObstacleForUser(Collection<Integer> ids,
			String userId);

	public abstract int deleteFromBattleItemQueueForUser(String userId,
			List<BattleItemQueueForUser> biqfuList);

	public abstract int deleteMiniEventGoalForUser(String userId);

	public abstract int deleteGiftForUser(Collection<String> gfuIds);

	public abstract int deleteGiftForTangoUser(Collection<String> gfuIds);
	
	public abstract int deleteUserItems(String userId, List<Integer> itemIdsUsed);

}
