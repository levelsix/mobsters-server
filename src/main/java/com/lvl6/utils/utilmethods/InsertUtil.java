package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.ClanAvenge;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.ClanHelpCountForUser;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.info.MiniEventForUser;
import com.lvl6.info.MiniEventGoalForUser;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterDeleteHistory;
import com.lvl6.info.MonsterEnhanceHistory;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.info.Research;
import com.lvl6.info.TaskForUserClientState;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils.UserTaskCompleted;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;


public interface InsertUtil {

	//	public abstract CacheManager getCache();
	//
	//	public abstract void setCache(CacheManager cache);

	public abstract boolean insertUserCityExpansionData(String userId,
			Timestamp expandStartTime, int xPosition, int yPosition,
			boolean isExpanding);

	//	public abstract int insertEquipEnhancement(int userId, int equipId, int equipLevel,
	//			int enhancementPercentageBeforeEnhancement, Timestamp startTimeOfEnhancement);

	//	public abstract int insertIntoEquipEnhancementHistory(long equipEnhancementId, int userId, int equipId,
	//			int equipLevel, int currentEnhancementPercentage, int previousEnhancementPercentage,
	//			Timestamp startTimeOfEnhancement);

	public abstract int insertUserQuest(String userId, int questId);

	public abstract int insertUserQuestJobs(String userId, int questId,
			List<Integer> questJobIds);

	public abstract boolean insertUserStructJustBuilt(String userId,
			int structId, Timestamp timeOfStructPurchase,
			Timestamp timeOfStructBuild, CoordinatePair structCoords);

	/*
	 * returns the id of the userstruct, -1 if none
	 */
	public abstract String insertUserStruct(String userId, int structId,
			CoordinatePair coordinates, Timestamp timeOfPurchase,
			Timestamp lastRetrievedTime, boolean isComplete);

	public abstract int insertUserStructs(List<String> userIdList,
			List<Integer> structIdList, List<Float> xCoordList,
			List<Float> yCoordList, List<Timestamp> purchaseTimeList,
			List<Timestamp> retrievedTimeList, List<Boolean> isComplete);

	public abstract String insertUserResearch(String userId, Research research,
			Timestamp timeOfPurchase, boolean isComplete);

	public abstract boolean insertIAPHistoryElem(JSONObject appleReceipt,
			int gemChange, User user, double cashCost);

	//	public abstract boolean insertMarketplaceItem(int posterId,
	//			MarketplacePostType postType, int postedEquipId, int diamondCost,
	//			int coinCost, Timestamp timeOfPost, int equipLevel,
	//			int enhancementPercent);

	//	public abstract boolean insertMarketplaceItemIntoHistory(
	//			MarketplacePost mp, int buyerId, boolean sellerHasLicense, Timestamp timeOfPurchase);

	public abstract boolean insertReferral(int referrerId, int referredId,
			int coinsGivenToReferrer);

	// returns -1 if error
	public abstract String insertUser(String name, String udid, int level,
			int experience, int cash, int oil, int gems, boolean isFake,
			String deviceToken, Timestamp createTime, String facebookId,
			int avatarMonsterId, String email, String fbData);

	public abstract int insertPvpLeagueForUser(String userId, int pvpLeagueId,
			int rank, int elo, Timestamp shieldEndTime,
			Timestamp inBattleShieldEndTime);

	public abstract boolean insertLastLoginLastLogoutToUserSessions(
			String userId, Timestamp loginTime, Timestamp logoutTime);

	//	public abstract boolean insertForgeAttemptIntoBlacksmithHistory(BlacksmithAttempt ba, boolean successfulForge);

	public abstract String insertClan(String name, Timestamp createTime,
			String description, String tag, boolean requestToJoinRequired,
			int clanIconId);

	public abstract boolean insertUserClan(String userId, String clanId,
			String status, Timestamp requestTime);

	public abstract String insertClanChatPost(String userId, String clanId,
			String content, Timestamp timeOfPost);

//	public abstract boolean insertTranslatedText(ChatType chatType, String chatId,
//			Map<TranslateLanguages, String> translatedTextMap);

	//	public abstract List<Long> insertUserEquips(int userId, List<Integer> equipIds,
	//			List<Integer> levels, List<Integer> enhancement, Timestamp now, String reason);

	public int insertIntoUserLeaderboardEvent(int leaderboardEventId,
			int userId, int battlesWonChange, int battlesLostChange,
			int battlesFledChange);

	public abstract int insertIntoUserCurrencyHistory(String userId,
			Timestamp date, String resourceType, int currencyChange,
			int currencyBefore, int currencyAfter, String reasonForChange,
			String details);

	public abstract int insertIntoUserCurrencyHistoryMultipleRows(
			List<String> userIds, List<Timestamp> dates,
			List<String> resourceTypes, List<Integer> currenciesChange,
			List<Integer> currenciesBefore, List<Integer> currentCurrencies,
			List<String> reasonsForChanges, List<String> details);

	public abstract int insertIntoLoginHistory(String udid, String userId,
			Timestamp now, boolean isLogin, boolean goingThroughTutorial);

	public abstract int insertIntoFirstTimeUsers(String openUdid, String udid,
			String mac, String advertiserId, Timestamp now);

	public abstract int insertIntoBoosterPackPurchaseHistory(String userId,
			int boosterPackId, Timestamp timeOfPurchase, BoosterItem bi,
			List<String> userMonsterIds);

	public abstract String insertIntoPrivateChatPosts(String posterId,
			String recipientId, String content, Timestamp timeOfPost, String contentLanguage);

	public abstract List<String> insertIntoPrivateChatPosts(
			List<String> posterIds, List<String> recipientIds,
			List<String> contents, List<Date> timeOfPosts);

	public abstract String insertIntoChatTranslations(ChatScope chatType, String chatId,
			TranslateLanguages language, String message, ChatTranslationsRetrieveUtils
			chatTranslationsRetrieveUtils);

	public abstract boolean insertTranslateSettings(String receiverId, String senderId,
			String language, String chatType, boolean translateOn);

	public abstract boolean insertMultipleDefaultTranslateSettings(Map<String, String> pairsOfChats);

	public abstract String insertIntoUserTaskReturnId(String userId,
			int taskId, int expGained, int cashGained, int oilGained,
			Timestamp startTime, int taskStageId);

	public abstract int insertIntoTaskHistory(String userTaskId, String userId,
			int taskId, int expGained, int cashGained, int oilGained,
			int numRevives, Timestamp startTime, Timestamp endTime,
			boolean userWon, boolean cancelled, int taskStageId);

	public abstract int insertIntoTaskForUserCompleted(UserTaskCompleted utc,
			Timestamp timeOfEntry);

	public abstract int insertIntoTaskForUserCompleted(List<String> userIdList,
			List<Integer> taskIdList, List<Timestamp> timeOfEntryList);

	/*
	public abstract int insertIntoUserTaskStage(List<Long> userTaskIds, List<Integer> stageNums,
			List<Integer> taskStageMonsterIds, List<String> monsterTypes, List<Integer> expsGained,
			List<Integer> cashGained, List<Integer> oilGained, List<Boolean> monsterPiecesDropped,
			Map<Integer, Integer> taskStageMonsterIdToItemId);
			*/

	public abstract List<String> insertIntoUserTaskStage(
			List<TaskStageForUser> tsfuList);

	public abstract int insertIntoTaskStageHistory(
			List<String> userTaskStageIds, List<String> userTaskIds,
			List<Integer> stageNums, List<Integer> tsmIds,
			List<String> monsterTypes, List<Integer> expsGained,
			List<Integer> cashGained, List<Integer> oilGained,
			List<Boolean> monsterPiecesDropped, List<Integer> itemIdDropped,
			List<Integer> monsterIdDrops, List<Integer> monsterDropLvls,
			List<Boolean> attackedFirstList);

	public abstract List<String> insertIntoMonsterForUserReturnIds(
			String userId, List<MonsterForUser> userMonsters,
			String sourceOfPieces, Date combineStartDate);

	public abstract int insertIntoMonsterForUserDeleted(String userId,
			List<String> deleteReasons, List<String> deleteDetails,
			List<MonsterForUser> userMonsters, Date deleteDate);

	public abstract List<String> insertIntoUserFbInviteForSlot(String userId,
			List<String> facebookIds, Timestamp curTime,
			Map<String, String> fbIdsToUserStructIds,
			Map<String, Integer> fbIdsToUserStructsFbLvl);

	//the user monster ids will be ordered in ascending order, and this will determine
	//which one is one and which one is two
	public abstract int insertIntoMonsterEvolvingForUser(String userId,
			String catalystUserMonsterId, List<String> userMonsterIds,
			Timestamp startTime);

	public abstract int insertIntoUpdateEventPersistentForUser(String userId,
			int eventId, Timestamp now);

	//	public abstract int insertUpdatePvpBattleForUser(String attackerId, String defenderId,
	//			int attackerWinEloChange, int defenderLoseEloChange, int attackerLoseEloChange,
	//			int defenderWinEloChange, Timestamp battleStartTime);

	public abstract int insertUpdatePvpBattleForUser(PvpBattleForUser pbfu);

	public abstract int insertIntoClanEventPersistentForClan(String clanId,
			int clanEventPersistentId, int clanRaidId, int clanRaidStageId,
			Timestamp stageStartTime, int clanRaidStageMonsterId,
			Timestamp stageMonsterStartTime);

	public abstract int insertIntoUpdateMonstersClanEventPersistentForUser(
			String userId, String clanId, int clanRaidId,
			List<String> userMonsterIds);

	public abstract int insertIntoClanEventPersistentForClanHistory(
			String clanId, Timestamp timeOfEntry, int clanEventPersistentId,
			int crId, int crsId, Timestamp stageStartTime, int crsmId,
			Timestamp stageMonsterStartTime, boolean won);

	public abstract int insertIntoCepfuRaidHistory(Integer clanEventId,
			Timestamp now, Map<String, ClanEventPersistentForUser> clanUserInfo);

	public abstract int insertIntoCepfuRaidStageHistory(Integer clanEventId,
			Timestamp crsStartTime, Timestamp crsEndTime, int stageHp,
			Map<String, ClanEventPersistentForUser> clanUserInfo);

	public abstract int insertIntoCepfuRaidStageMonsterHistory(
			Timestamp crsmEndTime,
			Map<String, ClanEventPersistentForUser> clanUserInfo,
			ClanEventPersistentForClan cepfc);

	public abstract List<String> insertIntoCepUserReward(
			Timestamp crsStartTime, int crsId, Timestamp crsEndTime,
			int clanEventId, List<ClanEventPersistentUserReward> userRewards);

	public abstract int insertIntoPvpBattleHistory(String attackerId,
			String defenderId, Timestamp battleEndTime,
			Timestamp battleStartTime, int attackerEloChange,
			int attackerEloBefore, int defenderEloChange,
			int defenderEloBefore, int attackerPrevLeague,
			int attackerCurLeague, int defenderPrevLeague,
			int defenderCurLeague, int attackerPrevRank, int attackerCurRank,
			int defenderPrevRank, int defenderCurRank, int attackerOilChange,
			int defenderOilChange, int attackerCashChange,
			int defenderCashChange, float nuPvpDmgMultiplier,
			boolean attackerWon, boolean cancelled, boolean gotRevenge,
			boolean displayToDefender);

	public abstract List<String> insertIntoObstaclesForUserGetIds(
			String userId, List<ObstacleForUser> ofuList);

	public abstract List<String> insertIntoMiniJobForUserGetIds(String userId,
			List<MiniJobForUser> mjfuList);

	public abstract int insertIntoUpdateUserItem(String userId, int itemId,
			int delta);

	public abstract List<String> insertIntoClanHelpGetId(
			List<ClanHelp> solicitations);

	public abstract int insertIntoUpdateClanInvite(String userId,
			String inviterId, String clanId, Timestamp timeOfInvite);

	public abstract List<String> insertIntoItemForUserUsageGetId(
			List<ItemForUserUsage> itemsUsed);

	public abstract List<String> insertIntoItemSecretGiftForUserGetId(
			List<ItemSecretGiftForUser> gifts);

	public abstract List<String> insertIntoClanAvengeGetId(
			List<ClanAvenge> caList, String clanId);

	public abstract int insertIntoClanAvengeUser(List<ClanAvengeUser> cauList);

	public abstract int insertIntoUpdateClientTaskState(
			List<TaskForUserClientState> tfucsList);

	public abstract int insertIntoUpdateClanHelpCount(ClanHelpCountForUser chcfu);

	public abstract String insertIntoClanMemberTeamDonateGetId(
			ClanMemberTeamDonation cmtd);

	public abstract String insertIntoMonsterSnapshotForUser(
			MonsterSnapshotForUser msfu);

	public abstract int insertIntoBattleItemQueueForUser(
			List<BattleItemQueueForUser> biqfuList);

	public abstract int insertIntoBattleItemForUser(
			List<BattleItemForUser> biqfuList);

	public abstract int insertIntoUpdatePvpBoardObstacleForUser(
			Collection<PvpBoardObstacleForUser> pbofus);

	public abstract boolean insertMonsterEvolveHistory(String userId,
			String userMonsterId1, String userMonsterId2,
			String catalystMonsterId, Timestamp startTime, Timestamp timeOfEntry);

	public abstract boolean insertMonsterDeleteHistory(
			List<MonsterDeleteHistory> monsterDeleteHistoryList);

	public abstract boolean insertMonsterEnhanceHistory(
			MonsterEnhanceHistory meh);

	public abstract boolean insertIntoUpdateMiniEventForUser(MiniEventForUser mefu);

	public abstract boolean insertIntoUpdateMiniEventGoalForUser(
			Collection<MiniEventGoalForUser> megfus);

	public abstract boolean insertMultipleTranslationsForPrivateChat(
			List<PrivateChatPost> listOfPrivateChatPosts,
			ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils);
}
