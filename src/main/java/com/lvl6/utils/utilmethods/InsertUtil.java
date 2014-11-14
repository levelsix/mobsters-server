package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.lvl6.info.BoosterItem;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.User;

public interface InsertUtil {

	//	public abstract CacheManager getCache();
	//
	//	public abstract void setCache(CacheManager cache);


	public abstract boolean insertUserCityExpansionData(String userId, Timestamp expandStartTime, 
			int xPosition, int yPosition, boolean isExpanding);

//	public abstract int insertEquipEnhancement(int userId, int equipId, int equipLevel,
//			int enhancementPercentageBeforeEnhancement, Timestamp startTimeOfEnhancement);

//	public abstract int insertIntoEquipEnhancementHistory(long equipEnhancementId, int userId, int equipId, 
//			int equipLevel, int currentEnhancementPercentage, int previousEnhancementPercentage, 
//			Timestamp startTimeOfEnhancement);


	public abstract int insertUserQuest(String userId, int questId);
	
	public abstract int insertUserQuestJobs(String userId, int questId,
			  List<Integer> questJobIds);
	
	public abstract boolean insertUserStructJustBuilt(String userId, int structId,
			Timestamp timeOfStructPurchase, Timestamp timeOfStructBuild,
			CoordinatePair structCoords);

	/*
	 * returns the id of the userstruct, -1 if none
	 */
	public abstract String insertUserStruct(String userId, int structId, CoordinatePair coordinates,
			Timestamp timeOfPurchase, Timestamp lastRetrievedTime, boolean isComplete);
	
	public abstract int insertUserStructs(List<String> userIdList, List<Integer> structIdList,
  		List<Float> xCoordList, List<Float> yCoordList, List<Timestamp> purchaseTimeList,
  		List<Timestamp> retrievedTimeList, List<Boolean> isComplete);
	
	public abstract boolean insertIAPHistoryElem(JSONObject appleReceipt,
			int diamondChange, int coinChange, User user, double cashCost);

//	public abstract boolean insertMarketplaceItem(int posterId,
//			MarketplacePostType postType, int postedEquipId, int diamondCost,
//			int coinCost, Timestamp timeOfPost, int equipLevel,
//			int enhancementPercent);

//	public abstract boolean insertMarketplaceItemIntoHistory(
//			MarketplacePost mp, int buyerId, boolean sellerHasLicense, Timestamp timeOfPurchase);

	public abstract boolean insertReferral(int referrerId, int referredId,
			int coinsGivenToReferrer);

	// returns -1 if error
	public abstract String insertUser(String name, String udid, int level, int experience,
			int cash, int oil, int gems, boolean isFake,  String deviceToken,
			Timestamp createTime, String facebookId, int avatarMonsterId, String email,
			String fbData);
	
	public abstract int insertPvpLeagueForUser(String userId, int pvpLeagueId, int rank,
			int elo, Timestamp shieldEndTime, Timestamp inBattleShieldEndTime);

	public abstract boolean insertLastLoginLastLogoutToUserSessions(int userId, Timestamp loginTime, Timestamp logoutTime); 

//	public abstract boolean insertForgeAttemptIntoBlacksmithHistory(BlacksmithAttempt ba, boolean successfulForge);

	public abstract String insertClan(String name, Timestamp createTime, String description,
			String tag, boolean requestToJoinRequired, int clanIconId);

	public abstract boolean insertUserClan(String userId, String clanId, String status, Timestamp requestTime);

	public abstract String insertClanChatPost(String userId, String clanId, String content,
			Timestamp timeOfPost);

//	public abstract List<Long> insertUserEquips(int userId, List<Integer> equipIds,
//			List<Integer> levels, List<Integer> enhancement, Timestamp now, String reason);

	public int insertIntoUserLeaderboardEvent(int leaderboardEventId, int userId, int battlesWonChange, int battlesLostChange, int battlesFledChange);

	public abstract int insertIntoUserCurrencyHistory (int userId, Timestamp date, String resourceType, 
			int currencyChange, int currencyBefore, int currencyAfter, String reasonForChange, String details);

	public abstract int insertIntoUserCurrencyHistoryMultipleRows (List<String> userIds,
			List<Timestamp> dates, List<String> resourceTypes, List<Integer> currenciesChange,
			List<Integer> currenciesBefore, List<Integer> currentCurrencies, List<String> reasonsForChanges,
			List<String> details);

	public abstract int insertIntoLoginHistory(String udid, String userId, Timestamp now, boolean isLogin, boolean goingThroughTutorial);

	public abstract int insertIntoFirstTimeUsers(String openUdid, String udid, String mac, String advertiserId, Timestamp now);

	public abstract int insertIntoBoosterPackPurchaseHistory(String userId, int boosterPackId, 
      Timestamp timeOfPurchase, BoosterItem bi, List<String> userMonsterIds);

	public abstract String insertIntoPrivateChatPosts(String posterId, String recipientId, String content, Timestamp timeOfPost);

	public abstract List<Integer> insertIntoPrivateChatPosts(List<Integer> posterIds, List<Integer> recipientIds, List<String> contents,
			List<Date> timeOfPosts);

	public abstract String insertIntoUserTaskReturnId(String userId, int taskId, 
			int expGained, int cashGained, int oilGained, Timestamp startTime,
			int taskStageId); 

	public abstract int insertIntoTaskHistory(String userTaskId, String userId,
			int taskId, int expGained, int cashGained, int oilGained,
			int numRevives, Timestamp startTime, Timestamp endTime,
			boolean userWon, boolean cancelled, int taskStageId);
	
	public abstract int insertIntoTaskForUserCompleted(String userId, int task,
			Timestamp timeOfEntry);
	
	public abstract int insertIntoTaskForUserCompleted(List<String> userIdList,
			List<Integer> taskIdList, List<Timestamp> timeOfEntryList);
	
	/*
	public abstract int insertIntoUserTaskStage(List<Long> userTaskIds, List<Integer> stageNums,
			List<Integer> taskStageMonsterIds, List<String> monsterTypes, List<Integer> expsGained,
			List<Integer> cashGained, List<Integer> oilGained, List<Boolean> monsterPiecesDropped,
			Map<Integer, Integer> taskStageMonsterIdToItemId);
			*/
	
	public abstract List<String> insertIntoUserTaskStage(List<TaskStageForUser> tsfuList);
	
	public abstract int insertIntoTaskStageHistory(List<String> userTaskStageIds,
			List<String> userTaskIds, List<Integer> stageNums, List<Integer> tsmIds,
			List<String> monsterTypes, List<Integer> expsGained, List<Integer> cashGained,
			List<Integer> oilGained, List<Boolean> monsterPiecesDropped,
			List<Integer> itemIdDropped, List<Integer> monsterIdDrops,
			List<Integer> monsterDropLvls);
	
	public abstract List<String> insertIntoMonsterForUserReturnIds(String userId,
			List<MonsterForUser> userMonsters, String sourceOfPieces, Date combineStartDate);
	
	public abstract int insertIntoMonsterForUserDeleted(int userId, List<String> deleteReasons,
			List<String> deleteDetails, List<MonsterForUser> userMonsters, Date deleteDate);
	
	public abstract List<Integer> insertIntoUserFbInviteForSlot(int userId, List<String> facebookIds,
			Timestamp curTime, Map<String, Integer> fbIdsToUserStructIds,
  		Map<String, Integer> fbIdsToUserStructsFbLvl);
	
	//the user monster ids will be ordered in ascending order, and this will determine
	//which one is one and which one is two
	public abstract int insertIntoMonsterEvolvingForUser(int userId, long catalystUserMonsterId,
			List<Long> userMonsterIds, Timestamp startTime);

	public abstract int insertIntoUpdateEventPersistentForUser(String userId, int eventId, Timestamp now);
	
	public abstract int insertUpdatePvpBattleForUser(String attackerId, String defenderId,
			int attackerWinEloChange, int defenderLoseEloChange, int attackerLoseEloChange,
			int defenderWinEloChange, Timestamp battleStartTime);
	
	public abstract int insertIntoClanEventPersistentForClan(String clanId,
			int clanEventPersistentId, int clanRaidId, int clanRaidStageId,
			Timestamp stageStartTime, int clanRaidStageMonsterId, Timestamp stageMonsterStartTime);
	
	public abstract int insertIntoUpdateMonstersClanEventPersistentForUser(String userId, String clanId,
			int clanRaidId, List<String> userMonsterIds);
	
	public abstract int insertIntoClanEventPersistentForClanHistory(String clanId,
			Timestamp timeOfEntry, int clanEventPersistentId, int crId, int crsId,
			Timestamp stageStartTime, int crsmId, Timestamp stageMonsterStartTime, boolean won);
	
	public abstract int insertIntoCepfuRaidHistory(Integer clanEventId, Timestamp now,
			Map<String, ClanEventPersistentForUser> clanUserInfo);	
	
	public abstract int insertIntoCepfuRaidStageHistory(Integer clanEventId,
			Timestamp crsStartTime, Timestamp crsEndTime, int stageHp,
			Map<String, ClanEventPersistentForUser> clanUserInfo);
	
	public abstract int insertIntoCepfuRaidStageMonsterHistory(Timestamp crsmEndTime,
			Map<Integer, ClanEventPersistentForUser> clanUserInfo,
			ClanEventPersistentForClan cepfc);
	
	public abstract List<String> insertIntoCepUserReward(Timestamp crsStartTime, int crsId,
			Timestamp crsEndTime, int clanEventId, List<ClanEventPersistentUserReward> userRewards);

	public abstract int insertIntoPvpBattleHistory(String attackerId, String defenderId,
			Timestamp battleEndTime, Timestamp battleStartTime, int attackerEloChange,
			int attackerEloBefore, int defenderEloChange, int defenderEloBefore,
			int attackerPrevLeague, int attackerCurLeague, int defenderPrevLeague,
			int defenderCurLeague, int attackerPrevRank, int attackerCurRank,
			int defenderPrevRank, int defenderCurRank, int attackerOilChange,
			int defenderOilChange, int attackerCashChange, int defenderCashChange,
			float nuPvpDmgMultiplier, boolean attackerWon, boolean cancelled,
			boolean gotRevenge, boolean displayToDefender);
	
	public abstract List<String> insertIntoObstaclesForUserGetIds(String userId,
			List<ObstacleForUser> ofuList);
	
	public abstract List<String> insertIntoMiniJobForUserGetIds(String userId,
			List<MiniJobForUser> mjfuList); 
	
	public abstract int insertIntoUpdateUserItem(String userId, int itemId, int delta);
	
	public abstract List<String> insertIntoClanHelpGetId(List<ClanHelp> solicitations);
	
	public abstract int insertIntoUpdateClanInvite(String userId,
		int inviterId, int clanId, Timestamp timeOfInvite);
	
	public abstract List<String> insertIntoItemForUserUsageGetId(List<ItemForUserUsage> itemsUsed);
}
