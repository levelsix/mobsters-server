package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.lvl6.info.BlacksmithAttempt;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.UserClanStatus;

public interface InsertUtil {

	//	public abstract CacheManager getCache();
	//
	//	public abstract void setCache(CacheManager cache);


	/*@Caching(evict = {
      //@CacheEvict(value = "userEquipsForUser", key = "#userId"),
      //@CacheEvict(value = "equipsToUserEquipsForUser", key = "#userId"),
      //@CacheEvict(value = "userEquipsWithEquipId", key = "#userId+':'+#equipId") })*/
	//  public abstract int insertUserEquip(int userId, int equipId, int level,
	//	  Timestamp now);

	public abstract boolean insertUserCityExpansionData(int userId, Timestamp expandStartTime, 
			int xPosition, int yPosition, boolean isExpanding);


//	public abstract int insertUserEquip(int userId, int equipId,
//			int enhancementPercentage);
//
//	public abstract int insertEquipEnhancement(int userId, int equipId, int equipLevel,
//			int enhancementPercentageBeforeEnhancement, Timestamp startTimeOfEnhancement);

//	public abstract int insertIntoEquipEnhancementHistory(long equipEnhancementId, int userId, int equipId, 
//			int equipLevel, int currentEnhancementPercentage, int previousEnhancementPercentage, 
//			Timestamp startTimeOfEnhancement);


//	public abstract boolean insertUnredeemedUserQuest(int userId, int questId);

	public abstract int insertUpdateUnredeemedUserQuest(int userId, int questId,
      int progress, boolean isComplete);
	
	/* used for quest tasks */
	public abstract boolean insertCompletedTaskIdForUserQuest(int userId,
			int taskId, int questId);

	public abstract boolean insertUserStructJustBuilt(int userId, int structId,
			Timestamp timeOfStructPurchase, Timestamp timeOfStructBuild,
			CoordinatePair structCoords);

	/*
	 * returns the id of the userstruct, -1 if none
	 */
	public abstract int insertUserStruct(int userId, int structId,
			CoordinatePair coordinates, Timestamp timeOfPurchase);

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
	public abstract int insertUser(String udid, String name,
			String deviceToken, String newReferCode, int level,
			int experience, int coins, int diamonds, boolean isFake,
			boolean activateShield, Timestamp createTime, String rank);

	public abstract boolean insertLastLoginLastLogoutToUserSessions(int userId, Timestamp loginTime, Timestamp logoutTime); 

	public abstract boolean insertForgeAttemptIntoBlacksmithHistory(BlacksmithAttempt ba, boolean successfulForge);

	public abstract int insertClan(String name, int ownerId, Timestamp createTime, String description, String tag, boolean requestToJoinRequired);

	public abstract boolean insertUserClan(int userId, int clanId, UserClanStatus status, Timestamp requestTime);

	public abstract int insertClanChatPost(int userId, int clanId, String content,
			Timestamp timeOfPost);

//	public abstract List<Long> insertUserEquips(int userId, List<Integer> equipIds,
//			List<Integer> levels, List<Integer> enhancement, Timestamp now, String reason);

	public int insertIntoUserLeaderboardEvent(int leaderboardEventId, int userId, int battlesWonChange, int battlesLostChange, int battlesFledChange);

	public abstract int insertIntoUserCurrencyHistory (int userId, Timestamp date, int isSilver, 
			int currencyChange, int currencyBefore, int currencyAfter, String reasonForChange);

	public abstract int insertIntoUserCurrencyHistoryMultipleRows (List<Integer> userIds,
			List<Timestamp> dates, List<Integer> areSilver, List<Integer> currenciesChange,
			List<Integer> currenciesBefore, List<Integer> currentCurrencies, List<String> reasonsForChanges);

	public abstract int insertIntoLoginHistory(String udid, int userId, Timestamp now, boolean isLogin, boolean goingThroughTutorial);

	public abstract int insertIntoFirstTimeUsers(String openUdid, String udid, String mac, String advertiserId, Timestamp now);

	public abstract int insertIntoUserBoosterPackHistory(int userId, int boosterPackId, int numBought, Timestamp timeOfPurchase,
			int rarityOneQuantity, int rarityTwoQuantity, int rarityThreeQuantity, boolean excludeFromLimitCheck, List<Integer> equipIds,
			List<Long> userEquipIds);

	public abstract int insertIntoPrivateChatPosts(int posterId, int recipientId, String content, Timestamp timeOfPost);

	public abstract List<Integer> insertIntoPrivateChatPosts(List<Integer> posterIds, List<Integer> recipientIds, List<String> contents,
			List<Date> timeOfPosts);

	public abstract long insertIntoUserTaskReturnId(int userId, int taskId, 
			int expGained, int silverGained, Timestamp startTime); 

	public abstract int insertIntoTaskHistory(long userTaskId, int userId,
			int taskId, int expGained, int silverGained, int numRevives,
			Timestamp startTime, Timestamp endTime, boolean userWon);
	
	public abstract int insertIntoUserTaskStage(List<Long> userTaskId, List<Integer> stageNum,
			List<Integer> monsterId, List<Integer> expGained, List<Integer> silverGained,
			List<Boolean> monsterPieceDropped);
	
	public abstract int insertIntoTaskStageHistory(List<Long> userTaskStageId,
			List<Long> userTaskId, List<Integer> stageNum, List<Integer> monsterId,
			List<Integer> expGained, List<Integer> silverGained,
			List<Boolean> monsterPieceDropped);
}