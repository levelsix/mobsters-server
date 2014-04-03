package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.StructureProto.StructOrientation;

public interface UpdateUtil {

  public abstract void updateNullifyDeviceTokens(Set<String> deviceTokens);

  /*
   * used for collecting a city expansion
   */
  public abstract boolean updateUserCityExpansionData(int userId, int xPosition, int yPosition, boolean isExpanding, Timestamp expandStartTime);
  
  public abstract boolean updateUserQuestIscomplete(int userId, int questId);

  public abstract boolean updateRedeemQuestForUser(int userId, int questId);

  /*
   * changin orientation
   */
  public abstract boolean updateUserStructOrientation(int userStructId,
      StructOrientation orientation);

  /*
   * used for updating is_complete=true and last_retrieved to purchased_time+minutestogain for a userstruct
   */
  public abstract boolean updateUserStructsBuildingIscomplete(int userId,
      List<StructureForUser> userStructs, List<Timestamp> newPurchaseTimes);

  public abstract boolean updateBeginUpgradingUserStruct(int userStructId,
  		int newStructId, Timestamp upgradeTime);
  
  public abstract boolean updateSpeedupUpgradingUserStruct(int userStructId,
  		Timestamp lastRetrievedTime);
  
  /*
   * used for updating last retrieved user struct times
   */
  public abstract boolean updateUserStructsLastretrieved(
      Map<Integer, Timestamp> userStructIdsToLastRetrievedTime,
      Map<Integer, StructureForUser> structIdsToUserStructs);

  /*
   * used for upgrading user struct's fb invite level
   */
  public abstract boolean updateUserStructLevel(int userStructId, int fbInviteLevelChange);

  /*
   * used for moving user structs
   */
  public abstract boolean updateUserStructCoord(int userStructId,
      CoordinatePair coordinates);

  //public abstract boolean updateUsersClanId(Integer clanId, List<Integer> userIds);

  public abstract boolean updateUserClanStatus(int userId, int clanId, UserClanStatus status);
  
  public abstract int updateUserClanStatuses(int clanId, List<Integer> userIdList,
			List<UserClanStatus> statuses);

  public abstract boolean incrementNumberOfLockBoxesForLockBoxEvent(int userId, int eventId,
      int increment);

  public boolean updateUsersAddDiamonds(List<Integer> userIds, int diamonds) ;
  
  public boolean updateLeaderboardEventSetRewardGivenOut(int eventId);

  public abstract boolean updateClanJoinTypeForClan(int clanId, boolean requestToJoinRequired);
  
  public abstract int incrementUserTaskNumRevives(long userTaskId, int numRevivesDelta);
  
  public abstract int updateUserMonstersHealth(Map<Long, Integer> userMonsterIdsToHealths); 
  
//  public abstract int updateUserAndEquipFail(int userId, int equipId, int failIncrement);
  
  public abstract int updateUserMonsterHealing(int userId, List<MonsterHealingForUser> monsters);
  
  public abstract int updateNullifyUserMonstersTeamSlotNum(List<Long> userMonsterIdList,
  		List<Integer> teamSlotNumList);
  
  public abstract int updateUserMonsterTeamSlotNum(long userMonsterId,
  		int teamSlotNum);
  
  public int nullifyMonstersTeamSlotNum(List<Long> userMonsterIds, int newTeamSlotNum);
  
  public abstract int updateUserMonsterEnhancing(int userId, List<MonsterEnhancingForUser> monsters);
  
  public abstract int updateUserMonsterExpAndLvl(long l, int newExp, int newLvl);

  public abstract int updateUserMonsterNumPieces(int userId,
  		Collection<MonsterForUser> monsterForUserList, String updateReason,
  		Date combineStartTime);
  
  public abstract int updateCompleteUserMonster(List<Long> userMonsterIds);
  
  public abstract int updateUserFacebookInviteForSlotAcceptTime(String recipientFacebookId,
  		List<Integer> acceptedInviteIds, Timestamp acceptTime);
  
  public abstract int updateRedeemUserFacebookInviteForSlot(Timestamp redeemTime,
  		List<UserFacebookInviteForSlot> redeemedInvites);
  
  public abstract int updateUserItems(int userId, Map<Integer, ItemForUser> itemIdsToUpdatedItems);
  
  public abstract int updateClanEventPersistentForClanStageStartTime(int clanId,
  		Timestamp curTime);
  
  public abstract int updateClanEventPersistentForClanGoToNextStage(int clanId,
  		int crsId, int crsmId);
  
  public abstract int updateClanEventPersistentForUserGoToNextStage(int crsId, int crsmId,
			Map<Integer, ClanEventPersistentForUser> userIdToCepfu);
  
  public abstract int updateClanEventPersistentForClanGoToNextMonster(int clanId,
  		int crsmId, Timestamp curTime);
  
  public abstract int updateClanEventPersistentForUsersGoToNextMonster(int crsId,
  		int crsmId, Map<Integer, ClanEventPersistentForUser> userIdToCepfu);
  
  public abstract int updateClanEventPersistentForUserCrsmDmgDone(int userId,
  		int dmgDealt, int crsId, int crsmId);
  
  public abstract int updatePvpBattleHistoryExactRevenge(int historyAttackerId,
  		int historyDefenderId, Timestamp battleEndTime);
  
  public abstract int updateObstacleForUserRemovalTime(int obstacleForUserId,
  		Timestamp clientTime);
  
  public abstract int updateClan(int clanId, boolean isChangeDescription,
  		String description, boolean isChangeJoinType, boolean requestToJoinRequired,
  		boolean isChangeIcon, int iconId);
  
  public abstract int updatePvpLeagueForUserShields(int userId, Timestamp shieldEndTime,
		  Timestamp inBattleEndTime);
  
  public abstract int updatePvpLeagueForUser(int userId, int newPvpLeagueId, 
		  int newRank, int eloChange, Timestamp shieldEndTime,
		  Timestamp inBattleEndTime, int attacksWonDelta, int defensesWonDelta,
		  int attacksLostDelta, int defensesLostDelta);
}
