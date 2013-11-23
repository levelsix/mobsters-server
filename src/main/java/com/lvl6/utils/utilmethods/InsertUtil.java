package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.lvl6.info.BlacksmithAttempt;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.ClanProto.UserClanStatus;

public interface InsertUtil {

	//	public abstract CacheManager getCache();
	//
	//	public abstract void setCache(CacheManager cache);


	public abstract boolean insertUserCityExpansionData(int userId, Timestamp expandStartTime, 
			int xPosition, int yPosition, boolean isExpanding);

//	public abstract int insertEquipEnhancement(int userId, int equipId, int equipLevel,
//			int enhancementPercentageBeforeEnhancement, Timestamp startTimeOfEnhancement);

//	public abstract int insertIntoEquipEnhancementHistory(long equipEnhancementId, int userId, int equipId, 
//			int equipLevel, int currentEnhancementPercentage, int previousEnhancementPercentage, 
//			Timestamp startTimeOfEnhancement);


	public abstract int insertUpdateUnredeemedUserQuest(int userId, int questId,
      int progress, boolean isComplete);
	
	public abstract boolean insertUserStructJustBuilt(int userId, int structId,
			Timestamp timeOfStructPurchase, Timestamp timeOfStructBuild,
			CoordinatePair structCoords);

	/*
	 * returns the id of the userstruct, -1 if none
	 */
	public abstract int insertUserStruct(int userId, int structId, CoordinatePair coordinates,
			Timestamp timeOfPurchase, Timestamp lastRetrievedTime, boolean isComplete);

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
			int currencyChange, int currencyBefore, int currencyAfter, String reasonForChange, String details);

	public abstract int insertIntoUserCurrencyHistoryMultipleRows (List<Integer> userIds,
			List<Timestamp> dates, List<String> resourceTypes, List<Integer> currenciesChange,
			List<Integer> currenciesBefore, List<Integer> currentCurrencies, List<String> reasonsForChanges,
			List<String> details);

	public abstract int insertIntoLoginHistory(String udid, int userId, Timestamp now, boolean isLogin, boolean goingThroughTutorial);

	public abstract int insertIntoFirstTimeUsers(String openUdid, String udid, String mac, String advertiserId, Timestamp now);

	public abstract int insertIntoBoosterPackPurchaseHistory(int userId, int boosterPackId, 
      Timestamp timeOfPurchase, BoosterItem bi, List<Long> userMonsterIds);

	public abstract int insertIntoPrivateChatPosts(int posterId, int recipientId, String content, Timestamp timeOfPost);

	public abstract List<Integer> insertIntoPrivateChatPosts(List<Integer> posterIds, List<Integer> recipientIds, List<String> contents,
			List<Date> timeOfPosts);

	public abstract long insertIntoUserTaskReturnId(int userId, int taskId, 
			int expGained, int silverGained, Timestamp startTime); 

	public abstract int insertIntoTaskHistory(long userTaskId, int userId,
			int taskId, int expGained, int silverGained, int numRevives,
			Timestamp startTime, Timestamp endTime, boolean userWon, boolean cancelled);
	
	public abstract int insertIntoTaskForUserCompleted(int userId, int task,
			Timestamp timeOfEntry);
	
	public abstract int insertIntoUserTaskStage(List<Long> userTaskIds, List<Integer> stageNums,
			List<Integer> taskStageMonsterIds, List<String> monsterTypes, List<Integer> expsGained,
			List<Integer> silversGained, List<Boolean> monsterPiecesDropped);
	
	public abstract int insertIntoTaskStageHistory(List<Long> userTaskStageIds,
			List<Long> userTaskIds, List<Integer> stageNums, List<Integer> monsterIds,
			List<String> monsterTypes, List<Integer> expsGained, List<Integer> silversGained,
			List<Boolean> monsterPiecesDropped);
	
	public abstract List<Long> insertIntoMonsterForUserReturnIds(int userId,
			List<MonsterForUser> userMonsters, String sourceOfPieces, Date combineStartDate);
	
	public abstract int insertIntoMonsterForUserDeleted(int userId, List<String> deleteReasons,
			List<String> deleteDetails, List<MonsterForUser> userMonsters, Date deleteDate);
	
	public abstract int insertIntoUserFbInviteForSlot(int userId, List<String> facebookIds,
			Timestamp curTime);
	
	public abstract int insertIntoUserFbInviteForSlotAccepted(List<Integer> userIds,
			List<Integer> nthExtraSlotsList,
			List<UserFacebookInviteForSlot> acceptedInvites, Timestamp curTime);
	
}