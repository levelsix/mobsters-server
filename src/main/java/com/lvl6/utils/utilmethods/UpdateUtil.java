package com.lvl6.utils.utilmethods;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lvl6.info.CoordinatePair;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.StructureForUser;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.StructureProto.StructOrientation;

public interface UpdateUtil {

  /*@Caching(evict = {
      //@CacheEvict(value = "unredeemedAndRedeemedUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "incompleteUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "unredeemedUserQuestsForUser", key = "#userId") })*/
//  public abstract boolean updateUserQuestsCoinsretrievedforreq(int userId,
//      List<Integer> questIds, int coinGain);

  public abstract void updateNullifyDeviceTokens(Set<String> deviceTokens);

  /*
   * used for collecting a city expansion
   */
  public abstract boolean updateUserCityExpansionData(int userId, int xPosition, int yPosition, boolean isExpanding, Timestamp expandStartTime);

  /*@Caching(evict = {
      //@CacheEvict(value = "unredeemedAndRedeemedUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "incompleteUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "unredeemedUserQuestsForUser", key = "#userId") })*/
  public abstract boolean updateUserQuestIscomplete(int userId, int questId);

  /*@Caching(evict = {
      //@CacheEvict(value = "unredeemedAndRedeemedUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "incompleteUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "unredeemedUserQuestsForUser", key = "#userId") })*/
  public abstract boolean updateRedeemQuestForUser(int userId, int questId);

  /*
   * changin orientation
   */
  /*@Caching(evict = {
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      //@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
  public abstract boolean updateUserStructOrientation(int userStructId,
      StructOrientation orientation);

  /*@Caching(evict = {
      //@CacheEvict(value = "unredeemedAndRedeemedUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "incompleteUserQuestsForUser", key = "#userId"),
      //@CacheEvict(value = "unredeemedUserQuestsForUser", key = "#userId") })*/
//  public abstract boolean updateUserQuestsSetCompleted(int userId,
//      int questId, boolean setTasksCompleteTrue,
//      boolean setDefeatTypeJobsCompleteTrue);

  /*
   * used for updating is_complete=true and last_retrieved to purchased_time+minutestogain for a userstruct
   */
  public abstract boolean updateUserStructsBuildingIscomplete(int userId,
      List<StructureForUser> userStructs, List<Timestamp> newPurchaseTimes);

  public abstract boolean updateBeginUpgradingUserStruct(int userStructId,
  		int newStructId, Timestamp upgradeTime);
  
  public abstract boolean updateSpeedupUpgradingUserStruct(int userStructId,
  		Timestamp lastRetrievedTime, Timestamp purchaseTime);
  
  /*
   * used for updating last retrieved user struct times
   */
  /*@Caching(evict = {
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      //@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
  public abstract boolean updateUserStructsLastretrieved(
      Map<Integer, Timestamp> userStructIdsToLastRetrievedTime,
      Map<Integer, StructureForUser> structIdsToUserStructs);

//  /*
//   * used for upgrading user structs level
//   */
//  /*@Caching(evict = {
//      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
//      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
//      //@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
//  public abstract boolean updateUserStructLevel(int userStructId,
//      int levelChange);

  /*
   * used for moving user structs
   */
  /*@Caching(evict = {
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      ////@CacheEvict(value = "structIdsToUserStructsForUser", allEntries = true),
      //@CacheEvict(value = "specificUserStruct", key = "#userStructId") })*/
  public abstract boolean updateUserStructCoord(int userStructId,
      CoordinatePair coordinates);

  public abstract boolean updateClanOwnerDescriptionForClan(int clanId, int ownerId, String description);

  public abstract boolean updateUserEquipOwner(long userEquipId, int newOwnerId, String reason);
  
  public abstract boolean updateUsersClanId(Integer clanId, List<Integer> userIds);

  public abstract boolean updateUserClanStatus(int userId, int clanId, UserClanStatus status);

  public abstract boolean incrementNumberOfLockBoxesForLockBoxEvent(int userId, int eventId,
      int increment);

  public boolean updateUsersAddDiamonds(List<Integer> userIds, int diamonds) ;
  
  public boolean updateLeaderboardEventSetRewardGivenOut(int eventId);

  public abstract boolean updateClanJoinTypeForClan(int clanId, boolean requestToJoinRequired);
  
  public abstract int incrementUserTaskNumRevives(long userTaskId, int numRevives);
  
  public abstract int updateUserMonstersHealth(Map<Long, Integer> userMonsterIdsToHealths); 
  
  public abstract int updateUserAndEquipFail(int userId, int equipId, int failIncrement);
  
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
}
