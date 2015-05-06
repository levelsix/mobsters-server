package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lvl6.info.AchievementForUser;
import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanGiftForUser;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.QuestJobForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureRetrieval;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils.UserTaskCompleted;

public interface UpdateUtil {

	public abstract void updateNullifyDeviceTokens(Set<String> deviceTokens);

	public abstract boolean updateUserCityExpansionData(String userId,
			int xPosition, int yPosition, boolean isExpanding,
			Timestamp expandStartTime);

	public abstract boolean updateUserQuestIscomplete(String userId, int questId);

	public abstract boolean updateRedeemQuestForUser(String userId, int questId);

	public abstract int updateUserQuestJobs(String userId,
			Map<Integer, QuestJobForUser> questJobIdToQuestJob);

	public abstract int updateUserAchievement(String userId,
			Timestamp completeTime,
			Map<Integer, AchievementForUser> achievementIdToAfu);

	public abstract int updateRedeemAchievementForUser(String userId,
			Collection<Integer> achievementIds, Timestamp completeTime);

	/*
	 * changin orientation
	 */
	public abstract boolean updateUserStructOrientation(String userStructId,
			StructOrientation orientation);

	/*
	 * used for updating is_complete=true and last_retrieved to purchased_time+minutestogain for a userstruct
	 */
	public abstract boolean updateUserStructsBuildingIsComplete(String userId,
			List<StructureForUser> userStructs, List<Timestamp> newPurchaseTimes);

	public abstract boolean updateBeginUpgradingUserStruct(String userStructId,
			int newStructId, Timestamp upgradeTime);

	public abstract boolean updateSpeedupUpgradingUserStruct(
			String userStructId, Timestamp lastRetrievedTime);

	public abstract boolean updatePurchaseTimeRetrieveTimeForMoneyTree(
			String userStructId, Timestamp newPurchaseTime);

	/*
	 * used for updating last retrieved user struct times
	 */
	public abstract boolean updateUserStructsLastRetrieved(
			Map<String, StructureRetrieval> userStructIdsToRetrievals,
			Map<String, StructureForUser> structIdsToUserStructs);

	/*
	 * used for upgrading user struct's fb invite level
	 */
	public abstract boolean updateUserStructFbLevel(String userStructId,
			int nuFbInviteLevel);

	/*
	 * used for moving user structs
	 */
	public abstract boolean updateUserStructCoord(String userStructId,
			CoordinatePair coordinates);

	//public abstract boolean updateUsersClanId(Integer clanId, List<Integer> userIds);

	public abstract boolean updateUserClanStatus(String userId, String clanId,
			UserClanStatus status);

	public abstract int updateUserClanStatuses(String clanId,
			List<String> userIdList, List<UserClanStatus> statuses);

	public abstract boolean incrementNumberOfLockBoxesForLockBoxEvent(
			String userId, int eventId, int increment);

	public boolean updateUsersAddDiamonds(List<String> userIds, int diamonds);

	public boolean updateLeaderboardEventSetRewardGivenOut(int eventId);

	public abstract boolean updateClanJoinTypeForClan(String clanId,
			boolean requestToJoinRequired);

	public abstract int incrementUserTaskNumRevives(String userTaskId,
			int numRevivesDelta);

	public abstract int updateUserTaskTsId(String userTaskId, int nuTaskStageId);

	public abstract int updateUserMonstersHealth(
			Map<String, Integer> userMonsterIdsToHealths);

	//  public abstract int updateUserAndEquipFail(int userId, int equipId, int failIncrement);

	public abstract int updateUserMonsterHealing(String userId,
			List<MonsterHealingForUser> monsters);

	public abstract int updateNullifyUserMonstersTeamSlotNum(
			List<String> userMonsterIdList, List<Integer> teamSlotNumList);

	public abstract int updateUserMonsterTeamSlotNum(String userMonsterId,
			int teamSlotNum);

	public int nullifyMonstersTeamSlotNum(List<String> userMonsterIds,
			int newTeamSlotNum);

	public abstract int updateUserMonsterEnhancing(String userId,
			List<MonsterEnhancingForUser> monsters);

	public abstract int updateCompleteEnhancing(String userId,
			String curEnhancingMfuId);

	public abstract int updateUserMonsterExpAndLvl(String l, int newExp,
			int newLvl, int newHp);

	public abstract int updateUserMonsterNumPieces(String userId,
			Collection<MonsterForUser> monsterForUserList, String updateReason,
			Date combineStartTime);

	public abstract int updateCompleteUserMonster(List<String> userMonsterIds);

	public abstract int updateUserFacebookInviteForSlotAcceptTime(
			String recipientFacebookId, List<String> acceptedInviteIds,
			Timestamp acceptTime);

	public abstract int updateRedeemUserFacebookInviteForSlot(
			Timestamp redeemTime,
			List<UserFacebookInviteForSlot> redeemedInvites);

	//  public abstract int updateUserItems(int userId, Map<Integer, ItemForUser> itemIdsToUpdatedItems);

	public abstract int updateClanEventPersistentForClanStageStartTime(
			String clanId, Timestamp curTime);

	public abstract int updateClanEventPersistentForClanGoToNextStage(
			String clanId, int crsId, int crsmId);

	public abstract int updateClanEventPersistentForUserGoToNextStage(
			int crsId, int crsmId,
			Map<String, ClanEventPersistentForUser> userIdToCepfu);

	public abstract int updateClanEventPersistentForClanGoToNextMonster(
			String clanId, int crsmId, Timestamp curTime);

	public abstract int updateClanEventPersistentForUsersGoToNextMonster(
			int crsId, int crsmId,
			Map<String, ClanEventPersistentForUser> userIdToCepfu);

	public abstract int updateClanEventPersistentForUserCrsmDmgDone(
			String userId, int dmgDealt, int crsId, int crsmId);

	public abstract int updatePvpBattleHistoryExactRevenge(
			String historyAttackerId, String historyDefenderId,
			Timestamp battleEndTime);

	public abstract int updateObstacleForUserRemovalTime(
			String obstacleForUserId, Timestamp clientTime);

	public abstract int updateClan(String clanId, boolean isChangeDescription,
			String description, boolean isChangeJoinType,
			boolean requestToJoinRequired, boolean isChangeIcon, int iconId);

	public abstract int updatePvpLeagueForUserShields(String userId,
			Timestamp shieldEndTime, Timestamp inBattleEndTime);

	public abstract int updatePvpLeagueForUser(String userId,
			int newPvpLeagueId, int newRank, int eloChange,
			Timestamp shieldEndTime, Timestamp inBattleEndTime,
			int attacksWonDelta, int defensesWonDelta, int attacksLostDelta,
			int defensesLostDelta, float nuPvpDmgMultiplier);

	public abstract int updateMiniJobForUser(String userId,
			String userMiniJobId, String userMonsterIdStr, Timestamp now);

	public abstract int updateMiniJobForUserCompleteTime(String userId,
			String userMiniJobId, Timestamp now);

	public abstract int updateRestrictUserMonsters(String userId,
			List<String> userMonsterIdList);

	public abstract int updateUnrestrictUserMonsters(String userId,
			List<String> userMonsterIdList);

	public abstract int updateItemForUser(String userId, int itemId,
			int quantityChange);

	public abstract int updateItemForUser(List<ItemForUser> ifuList);

	public int updateTaskStageForUserNoMonsterDrop(String droplessTsfuId);

	public int updatePvpMonsterDmgMultiplier(String userId,
			float monsterDmgMultiplier);

	public int updateClanHelp(String userId, String clanId,
			List<String> clanHelpIds);

	public int closeClanHelp(String userId, String clanId);

	public abstract int updatePvpBattleHistoryClanRetaliated(
			List<String> historyAttackerId, List<String> historyDefenderId,
			List<Timestamp> battleEndTime);

	public abstract int updateRecentPvpBattleHistoryClanRetaliated(
			String historyDefenderId, Timestamp battleEndTime);

	public abstract int updateTaskForUserCompleted(UserTaskCompleted utc);

	public abstract int updateClanMemberTeamDonation(ClanMemberTeamDonation cmtd);

	public abstract boolean updateUserResearch(String userResearchUuid,
			int researchId, Timestamp timeOfPurchase);

	public abstract boolean updateUserResearchCompleteStatus(
			String userResearchUuid);

	public abstract boolean updateUserSalesValue(String userId, int newSalesValue, Date now);

	public abstract boolean updateUserSalesJumpTwoTiers(String userId, boolean jumpTwoTiers);

	public abstract boolean updateUserBattleItems(String userId,
			List<BattleItemForUser> updateList);

	public abstract boolean updateUserTranslationSetting(String recipientId,
			String senderId, String newLanguage, boolean translateOn);

	public abstract boolean updateUserTranslationSettingGlobalLanguage(String recipientId,
			String chatType, String language, boolean translateOn);

	public abstract boolean updateUserStrength(String userId, long updatedStrength);

	public abstract boolean updateUserSalesLastPurchaseTime(String userId, Timestamp ts);

	public abstract boolean updateUserClanGiftHasBeenCollected(String userId, List<ClanGiftForUser> cgfuList);


}
