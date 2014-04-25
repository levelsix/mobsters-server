package com.lvl6.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Achievement;
import com.lvl6.info.AchievementForUser;
import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.CepfuRaidHistory;
import com.lvl6.info.CepfuRaidStageHistory;
import com.lvl6.info.City;
import com.lvl6.info.CityElement;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.ClanRaid;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.ClanRaidStageReward;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Dialogue;
import com.lvl6.info.EventPersistent;
import com.lvl6.info.EventPersistentForUser;
import com.lvl6.info.ExpansionCost;
import com.lvl6.info.ExpansionPurchaseForUser;
import com.lvl6.info.GoldSale;
import com.lvl6.info.Item;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterBattleDialogue;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.Obstacle;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.PvpBattleHistory;
import com.lvl6.info.PvpLeague;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.QuestJob;
import com.lvl6.info.QuestJobForUser;
import com.lvl6.info.Referral;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureHospital;
import com.lvl6.info.StructureLab;
import com.lvl6.info.StructureResidence;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.StructureResourceStorage;
import com.lvl6.info.StructureTownHall;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.TournamentEvent;
import com.lvl6.info.TournamentEventReward;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.AchievementStuffProto.AchievementProto;
import com.lvl6.proto.AchievementStuffProto.AchievementProto.AchievementType;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.BattleProto.PvpLeagueProto;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterDisplayItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.proto.ChatProto.ColorProto;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.CityProto.CityElementProto;
import com.lvl6.proto.CityProto.CityElementProto.CityElemType;
import com.lvl6.proto.CityProto.CityExpansionCostProto;
import com.lvl6.proto.CityProto.FullCityProto;
import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
import com.lvl6.proto.ClanProto.ClanIconProto;
import com.lvl6.proto.ClanProto.ClanRaidProto;
import com.lvl6.proto.ClanProto.ClanRaidStageMonsterProto;
import com.lvl6.proto.ClanProto.ClanRaidStageProto;
import com.lvl6.proto.ClanProto.ClanRaidStageRewardProto;
import com.lvl6.proto.ClanProto.FullClanProto;
import com.lvl6.proto.ClanProto.FullClanProtoWithClanSize;
import com.lvl6.proto.ClanProto.FullUserClanProto;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.PersistentClanEventClanInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventProto;
import com.lvl6.proto.ClanProto.PersistentClanEventRaidHistoryProto;
import com.lvl6.proto.ClanProto.PersistentClanEventRaidStageHistoryProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserInfoProto;
import com.lvl6.proto.ClanProto.PersistentClanEventUserRewardProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.ReferralNotificationProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.AnimatedSpriteOffsetProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto.DialogueType;
import com.lvl6.proto.MonsterStuffProto.MonsterLevelInfoProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.AnimationType;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterEvolutionProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.QuestProto.DialogueProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.QuestProto.FullUserQuestProto;
import com.lvl6.proto.QuestProto.ItemProto;
import com.lvl6.proto.QuestProto.QuestJobProto;
import com.lvl6.proto.QuestProto.QuestJobProto.QuestJobType;
import com.lvl6.proto.QuestProto.UserQuestJobProto;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.proto.StructureProto.HospitalProto;
import com.lvl6.proto.StructureProto.LabProto;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.ObstacleProto;
import com.lvl6.proto.StructureProto.ResidenceProto;
import com.lvl6.proto.StructureProto.ResourceGeneratorProto;
import com.lvl6.proto.StructureProto.ResourceStorageProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.proto.StructureProto.StructureInfoProto;
import com.lvl6.proto.StructureProto.StructureInfoProto.StructType;
import com.lvl6.proto.StructureProto.TownHallProto;
import com.lvl6.proto.StructureProto.TutorialStructProto;
import com.lvl6.proto.StructureProto.UserObstacleProto;
import com.lvl6.proto.TaskProto.DayOfWeek;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.MinimumUserTaskProto;
import com.lvl6.proto.TaskProto.PersistentEventProto;
import com.lvl6.proto.TaskProto.PersistentEventProto.EventType;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto.MonsterType;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.TaskProto.UserPersistentEventProto;
import com.lvl6.proto.TournamentStuffProto.MinimumUserProtoWithLevelForTournament;
import com.lvl6.proto.TournamentStuffProto.TournamentEventProto;
import com.lvl6.proto.TournamentStuffProto.TournamentEventRewardProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.proto.UserProto.UserPvpLeagueProto;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;

public class CreateInfoProtoUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  /**Achievement.proto***************************************************/
  public static AchievementProto createAchievementProto(Achievement a) {
	  AchievementProto.Builder ab = AchievementProto.newBuilder();
	  
	  ab.setAchievementId(a.getId());
	  
	  String str = a.getAchievementName();
	  if (null != str) {
		  ab.setName(str);
	  }
	  
	  str = a.getDescription();
	  if (null != str) {
		  ab.setDescription(str);
	  }
	  
	  ab.setGemReward(a.getGemReward());
	  ab.setLvl(a.getLvl());
	  
	  str = a.getAchievementType();
	  if (null != str) {
		  try {
			  AchievementType at = AchievementType.valueOf(str);
			  ab.setAchievementType(at);
		  } catch(Exception e) {
			  log.error("invalid AchievementType. achievement=" + a);
		  }
	  }
	  
	  str = a.getResourceType();
	  if (null != str) {
		  try {
			  ResourceType rt = ResourceType.valueOf(str);
			  ab.setResourceType(rt);
		  } catch(Exception e) {
			  log.error("invalid ResourceType. achievement=" + a);
		  }
	  }
	  
	  str = a.getMonsterElement();
	  if (null != str) {
		  try {
			  MonsterElement me = MonsterElement.valueOf(str);
			  ab.setElement(me);
		  } catch(Exception e) {
			  log.error("invalid MonsterElement. achievement=" + a);
		  }
	  }
	  
	  str = a.getMonsterQuality();
	  if (null != str) {
		  try {
			  MonsterQuality mq = MonsterQuality.valueOf(str);
			  ab.setQuality(mq);
		  } catch(Exception e) {
			  log.error("invalid MonsterQuality. achievement=" + a);
		  }
	  }
	  
	  ab.setStaticDataId(a.getStaticDataId());
	  ab.setPriority(a.getPriority());
	  ab.setPrerequisiteId(a.getPrerequisiteId());
	  ab.setSuccessorId(a.getSuccessorId());
	  
	  return ab.build();
  }
  
  public static UserAchievementProto createUserAchievementProto(
		  AchievementForUser afu) {
	  UserAchievementProto.Builder uapb = UserAchievementProto.newBuilder();
	  
	  uapb.setAchievementId(afu.getAchievementId());
	  uapb.setProgress(afu.getProgress());
	  uapb.setIsComplete(afu.isComplete());
	  uapb.setIsRedeemed(afu.isRedeemed());
	  
	  return uapb.build();
  }
  
  /**Battle.proto***************************************************/
  /*public static MinimumUserProtoWithBattleHistory createMinimumUserProtoWithBattleHistory(
		  User u, PvpLeagueForUser plfu) {
    MinimumUserProtoWithLevel mup = createMinimumUserProtoWithLevelFromUser(u);
    MinimumUserProtoWithBattleHistory.Builder mupwbhb = MinimumUserProtoWithBattleHistory.newBuilder(); 
    mupwbhb.setMinUserProtoWithLevel(mup);
    
    int battlesWon = plfu.getAttacksWon() + plfu.getDefensesWon();
    mupwbhb.setBattlesWon(battlesWon);
    int battlesLost = plfu.getAttacksLost() + plfu.getDefensesLost();
    mupwbhb.setBattlesLost(battlesLost);
    
    return mupwbhb.build();
  }*/

  public static PvpProto createPvpProto(User u, PvpLeagueForUser plfu,
		  PvpUser pu, Collection<MonsterForUser> userMonsters,
		  int prospectiveCashWinnings, int prospectiveOilWinnings) {
	  
    PvpProto.Builder ppb = PvpProto.newBuilder();
    MinimumUserProtoWithLevel defender = createMinimumUserProtoWithLevelFromUser(u);
    Collection<MinimumUserMonsterProto> defenderMonsters = 
        createMinimumUserMonsterProtoList(userMonsters);

    ppb.setDefender(defender);
    ppb.addAllDefenderMonsters(defenderMonsters);
    ppb.setProspectiveCashWinnings(prospectiveCashWinnings);
    ppb.setProspectiveOilWinnings(prospectiveOilWinnings);
    
    int userId = u.getId();
    UserPvpLeagueProto uplp = createUserPvpLeagueProto(userId, plfu, pu, false);
    ppb.setPvpLeagueStats(uplp);

    return ppb.build();
  }
  
  //this is used to create fake users for PvpProtos
  public static PvpProto createFakePvpProto(int userId, String name, int lvl, int elo,
      int prospectiveCashWinnings, int prospectiveOilWinnings, List<MonsterForPvp> mfpList) {

    //create the fake user
    MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
    mupb.setUserId(userId);
    mupb.setName(name);
    MinimumUserProto mup = mupb.build();

    MinimumUserProtoWithLevel.Builder mupwlb = MinimumUserProtoWithLevel.newBuilder();
    mupwlb.setMinUserProto(mup);
    mupwlb.setLevel(lvl);
    MinimumUserProtoWithLevel mupwl = mupwlb.build();

    //THE ACTUAL PROTO
    PvpProto.Builder ppb = PvpProto.newBuilder();
    ppb.setDefender(mupwl);
//    ppb.setCurElo(elo);
    
    //set the defenderMonsters
    List<MinimumUserMonsterProto> mumpList = createMinimumUserMonsterProtos(mfpList);
    ppb.addAllDefenderMonsters(mumpList);

    ppb.setProspectiveCashWinnings(prospectiveCashWinnings);
    ppb.setProspectiveOilWinnings(prospectiveOilWinnings);
    
    UserPvpLeagueProto uplp = createFakeUserPvpLeagueProto(userId, elo, false);
    ppb.setPvpLeagueStats(uplp);

    return ppb.build();
  }

  public static List<PvpProto> createPvpProtos(List<User> queuedOpponents,
		  Map<Integer, PvpLeagueForUser> userIdToLeagueInfo,
		  Map<Integer, PvpUser> userIdToPvpUser,
		  Map<Integer, List<MonsterForUser>> userIdToUserMonsters,
		  Map<Integer, Integer> userIdToCashReward, Map<Integer, Integer> userIdToOilReward) {
    List<PvpProto> pvpProtoList = new ArrayList<PvpProto>();

    for (User u : queuedOpponents) {
      Integer userId = u.getId();
      PvpLeagueForUser plfu = null;
      if (null != userIdToLeagueInfo && userIdToLeagueInfo.containsKey(userId)) {
    	  plfu = userIdToLeagueInfo.get(userId);
      }
      
      PvpUser pu = null;
      if (null != userIdToPvpUser && userIdToPvpUser.containsKey(userId)) {
    	  pu = userIdToPvpUser.get(userId);
      }
      List<MonsterForUser> userMonsters = userIdToUserMonsters.get(userId);
      int prospectiveCashWinnings = userIdToCashReward.get(userId);
      int prospectiveOilWinnings = userIdToOilReward.get(userId);

      PvpProto pp = createPvpProto(u, plfu, pu, userMonsters,
    		  prospectiveCashWinnings, prospectiveOilWinnings);
      pvpProtoList.add(pp);
    }
    return pvpProtoList;
  }
  
  public static PvpHistoryProto createPvpHistoryProto(User attacker, PvpBattleHistory info,
  		Collection<MonsterForUser> userMonsters, int prospectiveCashWinnings,
  		int prospectiveOilWinnings) {
  	PvpHistoryProto.Builder phpb = PvpHistoryProto.newBuilder();
  	//there is db call for clan...
  	FullUserProto fup = createFullUserProtoFromUser(attacker, null);
  	phpb.setAttacker(fup);
  	
  	if (null != userMonsters && !userMonsters.isEmpty()) {
  		Collection<MinimumUserMonsterProto> attackerMonsters = 
  				createMinimumUserMonsterProtoList(userMonsters);
  		phpb.addAllAttackersMonsters(attackerMonsters);
  	}
  	
  	phpb.setAttackerWon(info.isAttackerWon());
  	
  	int defenderCashChange = info.getDefenderCashChange();
  	phpb.setDefenderCashChange(defenderCashChange);
  	int defenderOilChange = info.getDefenderOilChange();
  	phpb.setDefenderOilChange(defenderOilChange);
  	
  	phpb.setExactedRevenge(info.isExactedRevenge());
  	
  	phpb.setProspectiveCashWinnings(prospectiveCashWinnings);
  	phpb.setProspectiveOilWinnings(prospectiveOilWinnings);
  	
  	Date endDate = info.getBattleEndTime();
  	//endDate should not be null, it's the primary key
  	phpb.setBattleEndTime(endDate.getTime());
  	
  	
  	UserPvpLeagueProto attackerBefore = createUserPvpLeagueProto(info.getAttackerId(),
  			info.getAttackerPrevLeague(), info.getAttackerPrevRank(),
  			info.getAttackerEloBefore(), false);
  	phpb.setAttackerBefore(attackerBefore);
  	UserPvpLeagueProto attackerAfter = createUserPvpLeagueProto(info.getAttackerId(),
  			info.getAttackerCurLeague(), info.getAttackerCurRank(),
  			info.getAttackerEloAfter(), false);
  	phpb.setAttackerAfter(attackerAfter);
  	
  	UserPvpLeagueProto defenderBefore = createUserPvpLeagueProto(info.getDefenderId(),
  			info.getDefenderPrevLeague(), info.getDefenderPrevRank(),
  			info.getDefenderEloBefore(), false);
  	phpb.setDefenderBefore(defenderBefore);
  	UserPvpLeagueProto defenderAfter = createUserPvpLeagueProto(info.getDefenderId(),
  			info.getDefenderCurLeague(), info.getDefenderCurRank(),
  			info.getDefenderEloAfter(), false);
  	phpb.setDefenderAfter(defenderAfter);
  	
  	return phpb.build();
  }
  
  public static List<PvpHistoryProto> createPvpHistoryProto(
  		List<PvpBattleHistory> historyList, Map<Integer, User> attackerIdsToAttackers,
  		Map<Integer, List<MonsterForUser>> attackerIdsToUserMonsters,
  		Map<Integer, Integer> attackerIdsToProspectiveCashWinnings,
  		Map<Integer, Integer> attackerIdsToProspectiveOilWinnings) {
  	
  	List<PvpHistoryProto> phpList = new ArrayList<PvpHistoryProto>();
  	
  	for (PvpBattleHistory history: historyList) {
  		int attackerId = history.getAttackerId();
  		
  		User attacker = attackerIdsToAttackers.get(attackerId);
  		List<MonsterForUser> attackerMonsters = attackerIdsToUserMonsters.get(attackerId);
  		int prospectiveCashWinnings = attackerIdsToProspectiveCashWinnings.get(attackerId);
  		int prospectiveOilWinnings = attackerIdsToProspectiveOilWinnings.get(attackerId);
  		
  		PvpHistoryProto php = createPvpHistoryProto(attacker, history, attackerMonsters,
  				prospectiveCashWinnings, prospectiveOilWinnings);
  		phpList.add(php);
  	}
  	return phpList;
  }
  
  public static PvpLeagueProto createPvpLeagueProto(PvpLeague pl) {
	  PvpLeagueProto.Builder plpb = PvpLeagueProto.newBuilder();
	  
	  plpb.setLeagueId(pl.getId());
	  
	  String aStr = pl.getLeagueName();
	  if (null != aStr) {
		  plpb.setLeagueName(aStr);
	  }
	  
	  aStr = pl.getImgPrefix();
	  if (null != aStr) {
		  plpb.setImgPrefix(aStr);
	  }
	  
	  plpb.setNumRanks(pl.getNumRanks());
	  
	  aStr = pl.getDescription();
	  if (null != aStr) {
		  plpb.setDescription(aStr);
	  }
	  
	  plpb.setMinElo(pl.getMinElo());
	  plpb.setMaxElo(pl.getMaxElo());
	  
	  return plpb.build();
  }
  
  public static UserPvpLeagueProto createUserPvpLeagueProto(int userId,
		  PvpLeagueForUser plfu, PvpUser pu, boolean setElo) {
	  UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
	  uplpb.setUserId(userId);
	  
	  if (null != plfu) {
		  uplpb.setLeagueId(plfu.getPvpLeagueId());
		  uplpb.setRank(plfu.getRank());

		  if (setElo) {
			  uplpb.setElo(plfu.getElo());
		  }
		  Date shieldEndTime = plfu.getShieldEndTime();
		  if (null != shieldEndTime) {
			  long time = shieldEndTime.getTime();
			  uplpb.setShieldEndTime(time);
		  }
		  
	  } else if (null != pu) {
		  uplpb.setLeagueId(pu.getPvpLeagueId());
		  uplpb.setRank(pu.getRank());
		  
		  if (setElo) {
			  uplpb.setElo(pu.getElo());
		  }
		  Date shieldEndTime = pu.getShieldEndTime();
		  if (null != shieldEndTime) {
			  long time = shieldEndTime.getTime();
			  uplpb.setShieldEndTime(time);
		  }
	  }
	  
	  
	  return uplpb.build();
  }
  
  public static UserPvpLeagueProto createUserPvpLeagueProto(int userId, int pvpLeagueId,
		  int rank, int elo, boolean setElo) {
	  UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
	  uplpb.setUserId(userId);
	  uplpb.setLeagueId(pvpLeagueId);
	  uplpb.setRank(rank);
	  
	  if (setElo) {
		  uplpb.setElo(elo);
	  }
	  
	  return uplpb.build();
  }
  
  public static UserPvpLeagueProto createFakeUserPvpLeagueProto(int userId, int elo,
		  boolean setElo) {
	  UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
	  uplpb.setUserId(userId);
	  
	  int leagueId = PvpLeagueRetrieveUtils.getLeagueIdForElo(elo, true, 0);
	  uplpb.setLeagueId(leagueId);
	  int rank = PvpLeagueRetrieveUtils.getRankForElo(elo, leagueId);
	  uplpb.setRank(rank);
	  
	  return uplpb.build();
  }
  
  /**BoosterPackStuff.proto****************************************/
  //  public static RareBoosterPurchaseProto createRareBoosterPurchaseProto(BoosterPack bp, User u, Date d) {
  //    return RareBoosterPurchaseProto.newBuilder().setBooster(createBoosterPackProto(bp, null))
  //        .setUser(createMinimumUserProtoFromUser(u))
  //        .setTimeOfPurchase(d.getTime()).build();
  //  }

  public static BoosterPackProto createBoosterPackProto(BoosterPack bp,
      Collection<BoosterItem> biList, Collection<BoosterDisplayItem> bdiList) {
    BoosterPackProto.Builder b = BoosterPackProto.newBuilder();
    b.setBoosterPackId(bp.getId());

    String str = bp.getName();
    if (null != str && !str.isEmpty()) {
      b.setBoosterPackName(str);
    }

    b.setGemPrice(bp.getGemPrice());

    str = bp.getListBackgroundImgName();
    if (null != str && !str.isEmpty()) {
      b.setListBackgroundImgName(str);
    }

    str = bp.getListDescription();
    if (null != str && !str.isEmpty()) {
      b.setListDescription(str);
    }

    str = bp.getNavBarImgName();
    if (null != str && !str.isEmpty()) {
      b.setNavBarImgName(str);
    }

    str = bp.getNavTitleImgName();
    if (null != str && !str.isEmpty()) {
      b.setNavTitleImgName(str);
    }

    str = bp.getMachineImgName();
    if (null != str && !str.isEmpty()) {
      b.setMachineImgName(str);
    }


    if (biList != null) {
      for(BoosterItem bi : biList) {
        //only want special booster items
        if (bi.isSpecial()) {
          BoosterItemProto bip = createBoosterItemProto(bi); 
          b.addSpecialItems(bip);
        }
      }
    }

    if (null != bdiList) {
      for (BoosterDisplayItem bdi : bdiList) {
        BoosterDisplayItemProto bdip = createBoosterDisplayItemProto(bdi);
        b.addDisplayItems(bdip);
      }
    }

    return b.build();
  }

  public static BoosterItemProto createBoosterItemProto(BoosterItem bi) {
    BoosterItemProto.Builder b = BoosterItemProto.newBuilder();
    b.setBoosterItemId(bi.getId());
    b.setBoosterPackId(bi.getBoosterPackId());
    b.setMonsterId(bi.getMonsterId());
    b.setNumPieces(bi.getNumPieces());
    b.setIsComplete(bi.isComplete());
    b.setIsSpecial(bi.isSpecial());
    b.setGemReward(bi.getGemReward());
    b.setCashReward(bi.getCashReward());
    b.setChanceToAppear(bi.getChanceToAppear());
    return b.build();
  }

  public static BoosterDisplayItemProto createBoosterDisplayItemProto(
      BoosterDisplayItem bdi) {
    BoosterDisplayItemProto.Builder b = BoosterDisplayItemProto.newBuilder();

    b.setBoosterPackId(bdi.getBoosterPackId());
    b.setIsMonster(bdi.isMonster());
    b.setIsComplete(bdi.isComplete());

    String monsterQuality = bdi.getMonsterQuality();
    if (null != monsterQuality) {
    	try {
    		MonsterQuality mq = MonsterQuality.valueOf(monsterQuality);
    		b.setQuality(mq);
    	} catch (Exception e){
    		log.error("invalid monster quality. boosterDisplayItem=" + bdi, e);
    	}
    }

    b.setGemReward(bdi.getGemReward());
    b.setQuantity(bdi.getQuantity());

    return b.build();
  }

  /**Chat.proto*****************************************************/
  public static PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPost (
      PrivateChatPost p, User poster, User recipient) {
    MinimumUserProtoWithLevel mupwlPoster = createMinimumUserProtoWithLevelFromUser(poster); 
    MinimumUserProtoWithLevel mupwlRecipient = createMinimumUserProtoWithLevelFromUser(recipient);

    // Truncate time because db truncates it (?)
    long time = p.getTimeOfPost().getTime();
    time = time - time % 1000;

    PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();
    pcppb.setPrivateChatPostId(p.getId());
    pcppb.setPoster(mupwlPoster);
    pcppb.setRecipient(mupwlRecipient);
    pcppb.setTimeOfPost(time);
    pcppb.setContent(p.getContent());

    return pcppb.build();
  }

  public static PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPostAndProtos (
      PrivateChatPost p, MinimumUserProtoWithLevel mupwlPoster,
      MinimumUserProtoWithLevel mupwlRecipient) {
    PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();

    pcppb.setPrivateChatPostId(p.getId());
    pcppb.setPoster(mupwlPoster);
    pcppb.setRecipient(mupwlRecipient);
    pcppb.setTimeOfPost(p.getTimeOfPost().getTime());
    pcppb.setContent(p.getContent());
    return pcppb.build();
  }

  public static List<PrivateChatPostProto> createPrivateChatPostProtoFromPrivateChatPostsAndProtos (
      List<PrivateChatPost> pList, Map<Integer, MinimumUserProtoWithLevel> idsToMupwls) {
    List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();

    for (PrivateChatPost pcp : pList) {
      MinimumUserProtoWithLevel mupwlPoster = idsToMupwls.get(pcp.getPosterId());
      MinimumUserProtoWithLevel mupwlRecipient = idsToMupwls.get(pcp.getRecipientId());

      PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(pcp,
          mupwlPoster, mupwlRecipient);

      pcppList.add(pcpp);
    }

    return pcppList;
  }

  //createMinimumProtoFromUser calls ClanRetrieveUtils, so a db read, if user has a clan
  //to prevent this, all clans that will be used should be passed in, hence clanIdsToClans,
  //clanIdsToUserIdSet. Not all users will have a clan, hence clanlessUserIds
  //privateChatPostIds is used by StartupController to pick out a subset of 
  //postIdsToPrivateChatPosts; does not need to be set.
  public static List<PrivateChatPostProto> createPrivateChatPostProtoList (Map<Integer, Clan> clanIdsToClans,
      Map<Integer, Set<Integer>> clanIdsToUserIdSet, Map<Integer, User> userIdsToUsers,
      List<Integer> clanlessUserIds, List<Integer> privateChatPostIds,
      Map<Integer, PrivateChatPost> postIdsToPrivateChatPosts) {

    List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();
    Map<Integer, MinimumUserProtoWithLevel> userIdToMinimumUserProtoWithLevel =
        new HashMap<Integer, MinimumUserProtoWithLevel>();
    //construct the minimum user protos for the users that have clans
    //and the clanless users
    createMinimumUserProtosFromClannedAndClanlessUsers(clanIdsToClans, clanIdsToUserIdSet,
        clanlessUserIds, userIdsToUsers, userIdToMinimumUserProtoWithLevel);

    //now actually construct the PrivateChatPostProtos
    if (null != privateChatPostIds && !privateChatPostIds.isEmpty()) {
      //only pick out a subset of postIdsToPrivateChatPosts
      for (int postId : privateChatPostIds) {
        PrivateChatPost pcp = postIdsToPrivateChatPosts.get(postId);
        int posterId = pcp.getPosterId();
        int recipientId = pcp.getRecipientId();

        MinimumUserProtoWithLevel mupwlPoster = userIdToMinimumUserProtoWithLevel.get(posterId);
        MinimumUserProtoWithLevel mupwlRecipient = userIdToMinimumUserProtoWithLevel.get(recipientId);

        PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
            pcp, mupwlPoster, mupwlRecipient);
        pcppList.add(pcpp);
      }
    } else {
      for (PrivateChatPost pcp : postIdsToPrivateChatPosts.values()) {
        int posterId = pcp.getPosterId();
        int recipientId = pcp.getRecipientId();
        MinimumUserProtoWithLevel mupwlPoster = userIdToMinimumUserProtoWithLevel.get(posterId);
        MinimumUserProtoWithLevel mupwlRecipient = userIdToMinimumUserProtoWithLevel.get(recipientId);

        PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
            pcp, mupwlPoster, mupwlRecipient);
        pcppList.add(pcpp);
      }
    }

    return pcppList;
  }
  
  public static GroupChatMessageProto createGroupChatMessageProtoFromClanChatPost(
      ClanChatPost p, User user) {
    GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto.newBuilder();
    gcmpb.setSender(createMinimumUserProtoWithLevelFromUser(user));
    gcmpb.setTimeOfChat(p.getTimeOfPost().getTime());
    gcmpb.setContent(p.getContent());
    return gcmpb.build();
  }

  public static GroupChatMessageProto createGroupChatMessageProto(long time, MinimumUserProtoWithLevel user, String content, boolean isAdmin, int chatId) {
    GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto.newBuilder();

    gcmpb.setSender(user);
    gcmpb.setTimeOfChat(time);
    gcmpb.setContent(content);
    gcmpb.setIsAdmin(isAdmin);
    gcmpb.setChatId(chatId).build();
    return gcmpb.build();
  }
  
  
  /**City.proto*****************************************************/
  //	public static FullUserCityExpansionDataProto createFullUserCityExpansionDataProtoFromUserCityExpansionData(UserCityExpansionData uced) {
  //		FullUserCityExpansionDataProto.Builder builder = FullUserCityExpansionDataProto.newBuilder().setUserId(uced.getUserId())
  //				.setFarLeftExpansions(uced.getFarLeftExpansions()).setFarRightExpansions(uced.getFarRightExpansions())
  //				.setNearLeftExpansions(uced.getNearLeftExpansions()).setNearRightExpansions(uced.getNearRightExpansions())
  //				.setIsExpanding(uced.isExpanding());
  //		if (uced.getLastExpandTime() != null) {
  //			builder.setLastExpandTime(uced.getLastExpandTime().getTime());
  //		}
  //		if (uced.getLastExpandDirection() != null) {
  //			builder.setLastExpandDirection(uced.getLastExpandDirection());
  //		}
  //		return builder.build();
  //	}

  public static UserCityExpansionDataProto createUserCityExpansionDataProtoFromUserCityExpansionData(ExpansionPurchaseForUser uced) {
    UserCityExpansionDataProto.Builder builder = UserCityExpansionDataProto.newBuilder().setUserId(uced.getUserId())
        .setXPosition(uced.getxPosition()).setYPosition(uced.getyPosition()).setIsExpanding(uced.isExpanding());
    if (uced.getExpandStartTime() != null) {
      builder.setExpandStartTime(uced.getExpandStartTime().getTime());
    }
    return builder.build();
  }

  public static CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(ExpansionCost ec) {
    CityExpansionCostProto.Builder builder = CityExpansionCostProto.newBuilder();
    builder.setExpansionNum(ec.getId());
    builder.setExpansionCostCash(ec.getExpansionCostCash());
    builder.setNumMinutesToExpand(ec.getNumMinutesToExpand());
    return builder.build();
  }

  public static CityElementProto createCityElementProtoFromCityElement(CityElement ce) {
    CityElementProto.Builder builder = CityElementProto.newBuilder();
    builder.setCityId(ce.getCityId());
    builder.setAssetId(ce.getAssetId());
    //    builder.setName(nce.getGoodName());
    
    try {
    	CityElemType cet = CityElemType.valueOf(ce.getType());
    	builder.setType(cet);
    } catch (Exception e) {
    	log.error("incorrect element type. cityElement=" + ce);
    }
    builder.setCoords(createCoordinateProtoFromCoordinatePair(ce.getCoords()));

    if (ce.getxLength() > 0) {
      builder.setXLength(ce.getxLength());
    }
    if (ce.getyLength() > 0) {
      builder.setYLength(ce.getyLength());
    }
    builder.setImgId(ce.getImgGood());
    
    try {
    	StructOrientation so = StructOrientation.valueOf(ce.getOrientation()); 
    	builder.setOrientation(so);
    } catch (Exception e) {
    	log.error("incorrect orientation. cityElement=" + ce);
    }

    builder.setSpriteCoords(createCoordinateProtoFromCoordinatePair(ce.getSpriteCoords()));

    return builder.build();
  }

  public static FullCityProto createFullCityProtoFromCity(City c) {
    FullCityProto.Builder builder = FullCityProto.newBuilder();
    builder.setCityId(c.getId());
    builder.setName(c.getName());
    builder.setMapImgName(c.getMapImgName());
    builder.setCenter(createCoordinateProtoFromCoordinatePair(c.getCenter()));
    List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(c.getId());
    if (tasks != null) {
      for (Task t : tasks) {
        builder.addTaskIds(t.getId());
      }
    }

    String roadImgName = c.getRoadImgName();
    if (null != roadImgName) {
      builder.setRoadImgName(roadImgName);
    }

    String mapTmxName = c.getMapTmxName();
    if (null != mapTmxName) {
      builder.setMapTmxName(mapTmxName);
    }

    builder.setRoadImgCoords(createCoordinateProtoFromCoordinatePair(c.getRoadImgCoords()));
    String atkMapLabelImgName = c.getAttackMapLabelImgName();
    if (null != atkMapLabelImgName) {
      builder.setAttackMapLabelImgName(c.getAttackMapLabelImgName());
    }

    return builder.build();
  }
  
  /**Clan.proto*****************************************************/
  public static FullClanProto createFullClanProtoFromClan(Clan c) {
    //    MinimumUserProto mup = createMinimumUserProtoFromUser(RetrieveUtils.userRetrieveUtils().getUserById(c.getOwnerId()));
    FullClanProto.Builder fcpb= FullClanProto.newBuilder();
    fcpb.setClanId(c.getId());
    fcpb.setName(c.getName());
    fcpb.setCreateTime(c.getCreateTime().getTime());
    fcpb.setDescription(c.getDescription());
    fcpb.setTag(c.getTag());
    fcpb.setRequestToJoinRequired(c.isRequestToJoinRequired());
    fcpb.setClanIconId(c.getClanIconId());
    
    return fcpb.build();
  }

  public static FullUserClanProto createFullUserClanProtoFromUserClan(UserClan uc) {
  	FullUserClanProto.Builder fucpb = FullUserClanProto.newBuilder();
    fucpb.setClanId(uc.getClanId());
    fucpb.setUserId(uc.getUserId());
    String userClanStatus = uc.getStatus();
    
    try {
    	UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
    	fucpb.setStatus(ucs);
    } catch (Exception e) {
    	log.error("incorrect user clan status. userClan=" + uc);
    }
    
    Date aTime = uc.getRequestTime();
    if (null != aTime) {
    	fucpb.setRequestTime(aTime.getTime());
    }
    
    return fucpb.build();
  }
  
  public static FullClanProtoWithClanSize createFullClanProtoWithClanSize(Clan c,
  		int size) {
  	FullClanProto clan = createFullClanProtoFromClan(c);
  	
  	return FullClanProtoWithClanSize.newBuilder().setClan(clan).setClanSize(size).build();
  }

  public static MinimumUserProtoForClans createMinimumUserProtoForClans(User u,
      String userClanStatus, float clanRaidContribution, int battlesWon) {
	  MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevelFromUser(u);

    MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans.newBuilder();
    mupfcb.setMinUserProtoWithLevel(mupwl);
    
    try {
    	UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
    	mupfcb.setClanStatus(ucs);
    } catch (Exception e) {
    	log.error("incorrect userClanStatus. userClanStatus=" + userClanStatus +
    			"\t user=" + u);
    }
    mupfcb.setRaidContribution(clanRaidContribution);
    mupfcb.setBattlesWon(battlesWon);
    MinimumUserProtoForClans mupfc = mupfcb.build();

    return mupfc;
  }
  
  public static MinimumUserProtoForClans createMinimumUserProtoForClans(User u,
		  UserClanStatus userClanStatus, float clanRaidContribution,
		  int battlesWon) {
	  	MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevelFromUser(u);

	    MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans.newBuilder();
	    mupfcb.setMinUserProtoWithLevel(mupwl);
	    
	    mupfcb.setClanStatus(userClanStatus);
	    mupfcb.setRaidContribution(clanRaidContribution);
	    mupfcb.setBattlesWon(battlesWon);
	    MinimumUserProtoForClans mupfc = mupfcb.build();

	    return mupfc;
	  }

  public static ClanRaidProto createClanRaidProto(ClanRaid clanRaid) {
    ClanRaidProto.Builder crpb = ClanRaidProto.newBuilder();
    int clanRaidId = clanRaid.getId();
    crpb.setClanRaidId(clanRaidId);

    String aStr = clanRaid.getClanRaidName();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setClanRaidName(aStr);
    }

    aStr = clanRaid.getActiveTitleImgName();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setActiveTitleImgName(aStr);
    }

    aStr = clanRaid.getActiveBackgroundImgName();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setActiveBackgroundImgName(aStr);
    }

    aStr = clanRaid.getActiveDescription();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setActiveDescription(aStr);
    }

    aStr = clanRaid.getInactiveMonsterImgName();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setInactiveMonsterImgName(aStr);
    }

    aStr = clanRaid.getInactiveDescription();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setInactiveDescription(aStr);
    }

    aStr = clanRaid.getDialogueText();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setDialogueText(aStr);
    }

    aStr = clanRaid.getSpotlightMonsterImgName();
    if (null != aStr && !aStr.isEmpty()) {
      crpb.setSpotlightMonsterImgName(aStr);
    }

    //create the clan raid stage protos
    Map<Integer, ClanRaidStage> stages = ClanRaidStageRetrieveUtils
        .getClanRaidStagesForClanRaidId(clanRaidId);
    for (ClanRaidStage crs : stages.values()) {
      ClanRaidStageProto crsp = createClanRaidStageProto(crs); 
      crpb.addRaidStages(crsp);
    }

    return crpb.build();
  }

  public static ClanRaidStageProto createClanRaidStageProto(ClanRaidStage crs) {
    ClanRaidStageProto.Builder crspb = ClanRaidStageProto.newBuilder();
    int clanRaidStageId = crs.getId();
    crspb.setClanRaidStageId(clanRaidStageId);
    crspb.setClanRaidId(crs.getClanRaidId());
    crspb.setDurationMinutes(crs.getDurationMinutes());
    crspb.setStageNum(crs.getStageNum());

    String name = crs.getName();
    if (null != name) {
      crspb.setName(name);
    }

    //create the monster protos in order
    Map<Integer, ClanRaidStageMonster> monsterNumToCrsm = ClanRaidStageMonsterRetrieveUtils
        .getMonsterNumsToMonstersForStageId(clanRaidStageId);

    if (!monsterNumToCrsm.isEmpty()) {

      List<Integer> monsterNumsAsc = new ArrayList<Integer>(monsterNumToCrsm.keySet());
      Collections.sort(monsterNumsAsc);

      for (Integer monsterNum : monsterNumsAsc) {
        ClanRaidStageMonster crsm = monsterNumToCrsm.get(monsterNum);

        ClanRaidStageMonsterProto crsmp = createClanRaidStageMonsterProto(crsm);
        crspb.addMonsters(crsmp);
      }
    }

    //create the reward protos
    Map<Integer, ClanRaidStageReward> possibleRewards = ClanRaidStageRewardRetrieveUtils
        .getClanRaidStageRewardsForClanRaidStageId(clanRaidStageId);
    for (ClanRaidStageReward crsr : possibleRewards.values()) {
      ClanRaidStageRewardProto crsrp = createClanRaidStageRewardProto(crsr);
      crspb.addPossibleRewards(crsrp);
    }

    return crspb.build();
  }

  public static ClanRaidStageMonsterProto createClanRaidStageMonsterProto(ClanRaidStageMonster crsm) {
    ClanRaidStageMonsterProto.Builder crsmpb = ClanRaidStageMonsterProto.newBuilder();
    crsmpb.setCrsmId(crsm.getId());
    crsmpb.setMonsterId(crsm.getMonsterId());
    crsmpb.setMonsterHp(crsm.getMonsterHp());
    crsmpb.setMinDmg(crsm.getMinDmg());
    crsmpb.setMaxDmg(crsm.getMaxDmg());

    return crsmpb.build();
  }

  public static ClanRaidStageRewardProto createClanRaidStageRewardProto(ClanRaidStageReward crsr) {
    ClanRaidStageRewardProto.Builder crsrpb = ClanRaidStageRewardProto.newBuilder();
    crsrpb.setCrsrId(crsr.getId());
    crsrpb.setMinOilReward(crsr.getMinOilReward());
    crsrpb.setMaxOilReward(crsr.getMaxOilReward());
    crsrpb.setMinCashReward(crsr.getMinCashReward());
    crsrpb.setMaxCashReward(crsr.getMaxCashReward());
    crsrpb.setMonsterId(crsr.getMonsterId());

    return crsrpb.build();
  }

  public static PersistentClanEventProto createPersistentClanEventProto(ClanEventPersistent cep) {
    PersistentClanEventProto.Builder pcepb = PersistentClanEventProto.newBuilder();
    pcepb.setClanEventId(cep.getId());

    String dayOfWeekStr = cep.getDayOfWeek();
    try {
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
      pcepb.setDayOfWeek(dayOfWeek);
    } catch (Exception e) {
      log.error("can't create enum type. dayOfWeek=" + dayOfWeekStr + ".\t clanEvent=" + cep);
    }


    pcepb.setStartHour(cep.getStartHour());
    pcepb.setEventDurationMinutes(cep.getEventDurationMinutes());
    pcepb.setClanRaidId(cep.getClanRaidId());

    return pcepb.build();
  }

  public static PersistentClanEventClanInfoProto createPersistentClanEventClanInfoProto(
      ClanEventPersistentForClan cepfc) {
    PersistentClanEventClanInfoProto.Builder pcecipb = PersistentClanEventClanInfoProto.newBuilder();
    pcecipb.setClanId(cepfc.getClanId());
    pcecipb.setClanEventId(cepfc.getClanEventPersistentId());
    pcecipb.setClanRaidId(cepfc.getCrId());

    pcecipb.setClanRaidStageId(cepfc.getCrsId());
    Date stageStartTime = cepfc.getStageStartTime();
    if (null != stageStartTime) {
      pcecipb.setStageStartTime(stageStartTime.getTime());
    }

    pcecipb.setCrsmId(cepfc.getCrsmId());
    Date stageMonsterStartTime = cepfc.getStageMonsterStartTime();
    if (null != stageMonsterStartTime) {
      pcecipb.setStageMonsterStartTime(stageMonsterStartTime.getTime());
    }

    return pcecipb.build();
  }

  public static PersistentClanEventUserInfoProto createPersistentClanEventUserInfoProto(
      ClanEventPersistentForUser cepfu, Map<Long, MonsterForUser> idsToUserMonsters,
      List<FullUserMonsterProto> fumpList){
    PersistentClanEventUserInfoProto.Builder pceuipb = PersistentClanEventUserInfoProto.newBuilder();
    int userId = cepfu.getUserId();
    pceuipb.setUserId(userId);
    pceuipb.setClanId(cepfu.getClanId());

    pceuipb.setCrId(cepfu.getCrId());
    pceuipb.setCrDmgDone(cepfu.getCrDmgDone());

    //  	pceuipb.setCrsId(cepfu.getCrsId());
    pceuipb.setCrsDmgDone(cepfu.getCrsDmgDone());

    //  	pceuipb.setCrsmId(cepfu.getCrsmId());
    pceuipb.setCrsmDmgDone(cepfu.getCrsmDmgDone());

    UserCurrentMonsterTeamProto.Builder ucmtpb = UserCurrentMonsterTeamProto.newBuilder();
    ucmtpb.setUserId(userId);

    if (null == fumpList || fumpList.isEmpty()) {
      List<Long> userMonsterIds = cepfu.getUserMonsterIds();

      for (Long userMonsterId : userMonsterIds) {

        if (!idsToUserMonsters.containsKey(userMonsterId)) {
          //user no longer has this monster, probably sold
          //create fake user monster proto
          FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
          fumpb.setUserMonsterId(userMonsterId);
          FullUserMonsterProto fump = fumpb.build();
          ucmtpb.addCurrentTeam(fump);
          continue;
        }
        MonsterForUser mfu = idsToUserMonsters.get(userMonsterId);
        FullUserMonsterProto fump = createFullUserMonsterProtoFromUserMonster(mfu);
        ucmtpb.addCurrentTeam(fump);
      }
    } else {
      ucmtpb.addAllCurrentTeam(fumpList);
    }
    pceuipb.setUserMonsters(ucmtpb.build());

    return pceuipb.build();
  }

  public static PersistentClanEventUserRewardProto createPersistentClanEventUserRewardProto(
      ClanEventPersistentUserReward reward) {
    PersistentClanEventUserRewardProto.Builder pceurpb = PersistentClanEventUserRewardProto.newBuilder();

    pceurpb.setRewardId(reward.getId());
    pceurpb.setUserId(reward.getUserId());

    Date crsEndTime = reward.getCrsEndTime();
    if (null != crsEndTime) {
      pceurpb.setCrsEndTime(crsEndTime.getTime());
    }

    String aStr = reward.getResourceType();

    if (null != aStr) {
      try {
        ResourceType rt = ResourceType.valueOf(aStr);
        pceurpb.setResourceType(rt);
      } catch(Exception e) {
        log.info("maybe resource type null. ClanEventPersistentUserReward=" + reward);
      }
    }

    int staticDataId = reward.getStaticDataId();
    if (0 != staticDataId) {
      pceurpb.setStaticDataId(reward.getStaticDataId());
    }
    pceurpb.setQuantity(reward.getQuantity());

    Date timeRedeemed = reward.getTimeRedeemed();
    if (null != timeRedeemed) {
      pceurpb.setTimeRedeemed(timeRedeemed.getTime());
    }

    return pceurpb.build();
  }
  
  public static PersistentClanEventRaidStageHistoryProto createPersistentClanEventRaidStageHistoryProto(
      CepfuRaidStageHistory cepfursh, Collection<ClanEventPersistentUserReward> rewards) {
    PersistentClanEventRaidStageHistoryProto.Builder pcershpb =
        PersistentClanEventRaidStageHistoryProto.newBuilder();

    if (null != rewards && !rewards.isEmpty()) {
      for (ClanEventPersistentUserReward reward : rewards) {
        PersistentClanEventUserRewardProto rewardPRoto =
            createPersistentClanEventUserRewardProto(reward);
        pcershpb.addRewards(rewardPRoto);
      }
    }

    pcershpb.setEventId(cepfursh.getClanEventPersistentId());
    pcershpb.setClanRaidId(cepfursh.getCrId());
    pcershpb.setClanRaidId(cepfursh.getCrsId());

    Date endTime = cepfursh.getCrsEndTime();
    if (null != endTime) {
      pcershpb.setCrsEndTime(endTime.getTime());
    }
    pcershpb.setCrsDmgDone(cepfursh.getCrsDmgDone());
    pcershpb.setStageHp(cepfursh.getStageHealth());

    return pcershpb.build();
  }

  public static PersistentClanEventRaidHistoryProto createPersistentClanEventRaidHistoryProto(
      CepfuRaidHistory cepfurh) {
    PersistentClanEventRaidHistoryProto.Builder pcerhpb = PersistentClanEventRaidHistoryProto.newBuilder();
    pcerhpb.setUserId(cepfurh.getUserId());
    pcerhpb.setCrDmg(cepfurh.getCrDmgDone());
    pcerhpb.setClanCrDmg(cepfurh.getClanCrDmg());

    return pcerhpb.build();
  }
  
  public static ClanIconProto createClanIconProtoFromClanIcon(ClanIcon ci) {
  	ClanIconProto.Builder cipb = ClanIconProto.newBuilder();

  	int id = ci.getId();
		String imgName = ci.getImgName();
		boolean isAvailable = ci.isAvailable();
		
		cipb.setClanIconId(id);
		if (null != imgName) {
			cipb.setImgName(imgName);
		}
		cipb.setIsAvailable(isAvailable);
		
  	return cipb.build();
  }
  
  /**InAppPurchase.proto********************************************/
  public static GoldSaleProto createGoldSaleProtoFromGoldSale(GoldSale sale) {
    GoldSaleProto.Builder b = GoldSaleProto.newBuilder().setSaleId(sale.getId()).setStartDate(sale.getStartDate().getTime()).setEndDate(sale.getEndDate().getTime());

    if (sale.getPackage1SaleIdentifier() != null) b.setPackage1SaleIdentifier(sale.getPackage1SaleIdentifier());
    if (sale.getPackage2SaleIdentifier() != null) b.setPackage2SaleIdentifier(sale.getPackage2SaleIdentifier());
    if (sale.getPackage3SaleIdentifier() != null) b.setPackage3SaleIdentifier(sale.getPackage3SaleIdentifier());
    if (sale.getPackage4SaleIdentifier() != null) b.setPackage4SaleIdentifier(sale.getPackage4SaleIdentifier());
    if (sale.getPackage5SaleIdentifier() != null) b.setPackage5SaleIdentifier(sale.getPackage5SaleIdentifier());
    if (sale.getPackageS1SaleIdentifier() != null) b.setPackageS1SaleIdentifier(sale.getPackageS1SaleIdentifier());
    if (sale.getPackageS2SaleIdentifier() != null) b.setPackageS2SaleIdentifier(sale.getPackageS2SaleIdentifier());
    if (sale.getPackageS3SaleIdentifier() != null) b.setPackageS3SaleIdentifier(sale.getPackageS3SaleIdentifier());
    if (sale.getPackageS4SaleIdentifier() != null) b.setPackageS4SaleIdentifier(sale.getPackageS4SaleIdentifier());
    if (sale.getPackageS5SaleIdentifier() != null) b.setPackageS5SaleIdentifier(sale.getPackageS5SaleIdentifier());
    b.setGoldShoppeImageName(sale.getGoldShoppeImageName()).setGoldBarImageName(sale.getGoldBarImageName());
    b.setIsBeginnerSale(sale.isBeginnerSale());

    return b.build();
  }
  
  /**MonsterStuff.proto********************************************/
  public static MonsterProto createMonsterProto(Monster aMonster,
      Map<Integer, MonsterLevelInfo> levelToInfo) {
    MonsterProto.Builder mpb = MonsterProto.newBuilder();

    mpb.setMonsterId(aMonster.getId());
    String aStr = aMonster.getName(); 
    if (null != aStr) {
    	mpb.setName(aStr);
    } else {
    	log.error("monster has no name, aMonster=" + aMonster);
    }
    aStr = aMonster.getMonsterGroup();
    if (null != aStr) {
      mpb.setMonsterGroup(aStr);
    }
    String monsterQuality = aMonster.getQuality(); 
    try {
    	MonsterQuality mq = MonsterQuality.valueOf(monsterQuality);
    	mpb.setQuality(mq);
    } catch (Exception e) {
    	log.error("invalid monster quality. monster=" + aMonster);
    }
    mpb.setEvolutionLevel(aMonster.getEvolutionLevel());
    aStr = aMonster.getDisplayName(); 
    if (null != aStr) {
      mpb.setDisplayName(aStr);
    }

    String monsterElement = aMonster.getElement();
    try {
    	MonsterElement me = MonsterElement.valueOf(monsterElement);
    	mpb.setMonsterElement(me);
    } catch (Exception e){
      log.error("invalid monster element. monster=" + aMonster);
    }
    aStr = aMonster.getImagePrefix(); 
    if (null != aStr) {
      mpb.setImagePrefix(aStr);
    }
    mpb.setNumPuzzlePieces(aMonster.getNumPuzzlePieces());
    mpb.setMinutesToCombinePieces(aMonster.getMinutesToCombinePieces());
    mpb.setMaxLevel(aMonster.getMaxLevel());

    int evolId = aMonster.getEvolutionMonsterId();
    if (evolId > 0) {
      mpb.setEvolutionMonsterId(evolId);
    }
    aStr = aMonster.getCarrotRecruited();
    if (null != aStr) {
      mpb.setCarrotRecruited(aStr);
    }
    aStr = aMonster.getCarrotDefeated();
    if (null != aStr) {
      mpb.setCarrotDefeated(aStr);
    }
    aStr = aMonster.getCarrotEvolved();
    if (null != aStr) {
      mpb.setCarrotEvolved(aStr);
    }
    aStr = aMonster.getDescription();
    if (null != aStr) {
      mpb.setDescription(aStr);
    }

    int evolutionCatalystMonsterId = aMonster.getEvolutionCatalystMonsterId();
    mpb.setEvolutionCatalystMonsterId(evolutionCatalystMonsterId);
    int minutesToEvolve = aMonster.getMinutesToEvolve();
    mpb.setMinutesToEvolve(minutesToEvolve);
    int num = aMonster.getNumCatalystsRequired();
    mpb.setNumCatalystMonstersRequired(num);

    List<MonsterLevelInfoProto> lvlInfoProtos = createMonsterLevelInfoFromInfo(levelToInfo);
    mpb.addAllLvlInfo(lvlInfoProtos);

    int cost = aMonster.getEvolutionCost();
    mpb.setEvolutionCost(cost);

    aStr = aMonster.getAnimationType();
    try {
      AnimationType typ = AnimationType.valueOf(aStr);
      mpb.setAttackAnimationType(typ);
    } catch (Exception e) {
      log.error("invalid animation type for monster. type=" + aStr + "\t monster=" + aMonster, e);
    }
    
    int verticalPixelOffset = aMonster.getVerticalPixelOffset();
    mpb.setVerticalPixelOffset(verticalPixelOffset);
    
    String atkSoundFile = aMonster.getAtkSoundFile();
    if (null != atkSoundFile) {
    	mpb.setAtkSoundFile(atkSoundFile);
    }
    
    int atkSoundAnimationFrame = aMonster.getAtkSoundAnimationFrame();
    mpb.setAtkSoundAnimationFrame(atkSoundAnimationFrame);
    
    int atkAnimationRepeatedFramesStart = aMonster.getAtkAnimationRepeatedFramesStart();
    mpb.setAtkAnimationRepeatedFramesStart(atkAnimationRepeatedFramesStart);
    
    int atkAnimationRepeatedFramesEnd = aMonster.getAtkAnimationRepeatedFramesEnd();
    mpb.setAtkAnimationRepeatedFramesEnd(atkAnimationRepeatedFramesEnd);
    
    String shorterName = aMonster.getShorterName();
    if (null != shorterName) {
    	mpb.setShorterName(shorterName);
    }
    return mpb.build();
  }

  public static List<MonsterLevelInfoProto> createMonsterLevelInfoFromInfo(
      Map<Integer, MonsterLevelInfo> lvlToInfo) {

    if (null == lvlToInfo || lvlToInfo.isEmpty()) {
      return new ArrayList<MonsterLevelInfoProto>();
    }

    //order the MonsterLevelInfoProto by ascending lvl
    Set<Integer> lvls = lvlToInfo.keySet();
    List<Integer> ascendingLvls = new ArrayList<Integer>(lvls);
    Collections.sort(ascendingLvls);

    List<MonsterLevelInfoProto> lvlInfoProtos = new ArrayList<MonsterLevelInfoProto>();
    for (Integer lvl : ascendingLvls) {
      MonsterLevelInfo info = lvlToInfo.get(lvl);

      //create the proto
      MonsterLevelInfoProto.Builder mlipb = MonsterLevelInfoProto.newBuilder();
      mlipb.setLvl(lvl);
      mlipb.setHp(info.getHp());
      mlipb.setCurLvlRequiredExp(info.getCurLvlRequiredExp());
      mlipb.setFeederExp(info.getFeederExp());
      mlipb.setFireDmg(info.getFireDmg());
      mlipb.setGrassDmg(info.getGrassDmg());
      mlipb.setWaterDmg(info.getWaterDmg());
      mlipb.setLightningDmg(info.getLightningDmg());
      mlipb.setDarknessDmg(info.getDarknessDmg());
      mlipb.setRockDmg(info.getRockDmg());
      mlipb.setSpeed(info.getSpeed());
      mlipb.setHpExponentBase(info.getHpExponentBase());
      mlipb.setDmgExponentBase(info.getDmgExponentBase());
      mlipb.setExpLvlDivisor(info.getExpLvlDivisor());
      mlipb.setExpLvlExponent(info.getExpLvlExponent());
      
      lvlInfoProtos.add(mlipb.build());
    }

    return lvlInfoProtos;
  }

  public static FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(MonsterForUser mfu) {
    FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
    fumpb.setUserMonsterId(mfu.getId());
    fumpb.setUserId(mfu.getUserId());
    fumpb.setMonsterId(mfu.getMonsterId());
    fumpb.setCurrentExp(mfu.getCurrentExp());
    fumpb.setCurrentLvl(mfu.getCurrentLvl());
    fumpb.setCurrentHealth(mfu.getCurrentHealth());
    fumpb.setNumPieces(mfu.getNumPieces());
    fumpb.setIsComplete(mfu.isComplete());

    Date combineStartTime = mfu.getCombineStartTime();
    if (null != combineStartTime) {
      fumpb.setCombineStartTime(combineStartTime.getTime());
    }

    fumpb.setTeamSlotNum(mfu.getTeamSlotNum());
    return fumpb.build();
  }

  public static List<FullUserMonsterProto> createFullUserMonsterProtoList(
      List<MonsterForUser> userMonsters) {
    List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();

    for (MonsterForUser mfu : userMonsters) {
      FullUserMonsterProto ump = createFullUserMonsterProtoFromUserMonster(mfu);
      protos.add(ump);
    }

    return protos;
  }

  public static MinimumUserMonsterProto createMinimumUserMonsterProto(MonsterForUser mfu) {
    MinimumUserMonsterProto.Builder mump = MinimumUserMonsterProto.newBuilder();

    mump.setMonsterId(mfu.getMonsterId());
    mump.setMonsterLvl(mfu.getCurrentLvl());
    return mump.build();
  }

  public static MinimumUserMonsterProto createMinimumUserMonsterProto(MonsterForPvp mfp) {
    MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto.newBuilder();

    int id = mfp.getId();
    int lvl = mfp.getMonsterLvl();

    mumpb.setMonsterId(id);
    mumpb.setMonsterLvl(lvl);

    return mumpb.build();
  }

  public static Collection<MinimumUserMonsterProto> createMinimumUserMonsterProtoList(
      Collection<MonsterForUser> userMonsters) {
    List<MinimumUserMonsterProto> returnList = new ArrayList<MinimumUserMonsterProto>();

    for (MonsterForUser mfu : userMonsters) {
      MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfu);
      returnList.add(mump);
    }

    return returnList;
  }

  public static List<MinimumUserMonsterProto> createMinimumUserMonsterProtos(
      List<MonsterForPvp> mfpList) {
    List<MinimumUserMonsterProto> mumpList = new ArrayList<MinimumUserMonsterProto>();

    for (MonsterForPvp mfp : mfpList) {
      MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfp);
      mumpList.add(mump);
    }

    return mumpList;
  }

  public static UserMonsterHealingProto createUserMonsterHealingProtoFromObj(
      MonsterHealingForUser mhfu) {
    UserMonsterHealingProto.Builder umhpb = UserMonsterHealingProto.newBuilder();
    umhpb.setUserId(mhfu.getUserId());
    umhpb.setUserMonsterId(mhfu.getMonsterForUserId());

    Date aDate = mhfu.getQueuedTime();
    if (null != aDate) {
      umhpb.setQueuedTimeMillis(aDate.getTime());
    }

    //  	umhpb.setUserHospitalStructId(mhfu.getUserStructHospitalId());
    umhpb.setHealthProgress(mhfu.getHealthProgress());
    umhpb.setPriority(mhfu.getPriority());

    return umhpb.build();
  }

  public static UserEnhancementProto createUserEnhancementProtoFromObj(
      int userId, UserEnhancementItemProto baseMonster, List<UserEnhancementItemProto> feeders) {

    UserEnhancementProto.Builder uepb = UserEnhancementProto.newBuilder();

    uepb.setUserId(userId);
    uepb.setBaseMonster(baseMonster);
    uepb.addAllFeeders(feeders);

    return uepb.build();
  }

  public static UserEnhancementItemProto createUserEnhancementItemProtoFromObj(
      MonsterEnhancingForUser mefu) {

    UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto.newBuilder();
    ueipb.setUserMonsterId(mefu.getMonsterForUserId());

    Date startTime = mefu.getExpectedStartTime();
    if (null != startTime) {
      ueipb.setExpectedStartTimeMillis(startTime.getTime());
    }

    ueipb.setEnhancingCost(mefu.getEnhancingCost());

    return ueipb.build();
  }
  
  public static UserCurrentMonsterTeamProto createUserCurrentMonsterTeamProto(int userId,
  		List<MonsterForUser> curTeam) {
  	UserCurrentMonsterTeamProto.Builder ucmtp = UserCurrentMonsterTeamProto.newBuilder();
  	ucmtp.setUserId(userId);
  	
  	List<FullUserMonsterProto> currentTeam = createFullUserMonsterProtoList(curTeam);
  	ucmtp.addAllCurrentTeam(currentTeam);
  	
  	return ucmtp.build();
  }

  public static UserMonsterEvolutionProto createUserEvolutionProtoFromEvolution(
      MonsterEvolvingForUser mefu) {
    UserMonsterEvolutionProto.Builder uepb = UserMonsterEvolutionProto.newBuilder();

    long catalystUserMonsterId = mefu.getCatalystMonsterForUserId();
    long one = mefu.getMonsterForUserIdOne();
    long two = mefu.getMonsterForUserIdTwo();
    Date startTime = mefu.getStartTime();

    uepb.setCatalystUserMonsterId(catalystUserMonsterId);

    long startTimeMillis = startTime.getTime();
    uepb.setStartTime(startTimeMillis);

    List<Long> userMonsterIds = new ArrayList<Long>();
    userMonsterIds.add(one);
    userMonsterIds.add(two);
    uepb.addAllUserMonsterIds(userMonsterIds);

    return uepb.build();
  }

  public static MonsterBattleDialogueProto createMonsterBattleDialogueProto(MonsterBattleDialogue mbd) {
    MonsterBattleDialogueProto.Builder mbdpb = MonsterBattleDialogueProto.newBuilder();
    mbdpb.setMonsterId(mbd.getMonsterId());

    String aStr = mbd.getDialogueType();
    try {
      DialogueType type = DialogueType.valueOf(aStr);
      mbdpb.setDialogueType(type);
    } catch (Exception e) {
      log.error("could not create DialogueType enum", e);
    }

    mbdpb.setDialogue(mbd.getDialogue());
    mbdpb.setProbabilityUttered(mbd.getProbabilityUttered());

    return mbdpb.build();
  }

  /**Quest.proto****************************************************/
  public static FullQuestProto createFullQuestProtoFromQuest(Quest quest) {
    //SET THE BUILDER
    FullQuestProto.Builder builder = FullQuestProto.newBuilder();
    builder.setQuestId(quest.getId());
//    builder.setCityId(quest.getCityId());
    
    String str = quest.getQuestName();
    if (null != str) {
    	builder.setName(str);
    }

    str = quest.getDescription();
    if (null != str) {
    	builder.setDescription(str);
    }

    str = quest.getDoneResponse();
    if (null != str) {
    	builder.setDoneResponse(str);
    }
    
    Dialogue acceptDialogue = quest.getAcceptDialogue();
    if (acceptDialogue != null) {
      builder.setAcceptDialogue(createDialogueProtoFromDialogue(acceptDialogue));
    }

//    String qType = quest.getQuestType();
//    try {
//      QuestType qt = QuestType.valueOf(qType);
//      builder.setQuestType(qt);
//    } catch (Exception e) {
//      log.error("can't create enum type. questType=" + qType + ".\t quest=" + quest);
//    }
    
//    str = quest.getJobDescription();
//    if (null != str && !str.isEmpty()) {
//    	builder.setJobDescription(str);
//    }
    
//    builder.setStaticDataId(quest.getStaticDataId());
//    builder.setQuantity(quest.getQuantity());
    builder.setCashReward(quest.getCashReward());
    builder.setOilReward(quest.getOilReward());
    builder.setGemReward(quest.getGemReward());
    builder.setExpReward(quest.getExpReward());
    builder.setMonsterIdReward(quest.getMonsterIdReward());
    builder.setIsCompleteMonster(quest.isCompleteMonster());
    builder.addAllQuestsRequiredForThis(quest.getQuestsRequiredForThis());
    
    str = quest.getQuestGiverName();
    if (null != str) {
    	builder.setQuestGiverName(str);
    }
    
    str = quest.getQuestGiverImagePrefix();
    if (null != str) {
    	builder.setQuestGiverImagePrefix(str);
    }
    
    if (quest.getPriority() > 0) {
      builder.setPriority(quest.getPriority());
    }
    
    str = quest.getCarrotId();
    if (null != str && !str.isEmpty()) {
    	builder.setCarrotId(str);
    }
//    builder.setIsAchievement(quest.isAchievement());
    
    str = quest.getMonsterElement();
    if (null != str) {
    	try {
    		MonsterElement me = MonsterElement.valueOf(str);
    		builder.setMonsterElement(me);
    	} catch (Exception e) {
    		log.error("invalid monsterElement. quest=" + quest);
    	}
    }
    
    List<QuestJobProto> jobsList = createQuestJobProto(quest.getId());
    builder.addAllJobs(jobsList);

    return builder.build();
  }
  
  public static List<QuestJobProto> createQuestJobProto(int questId) {
	  Map<Integer, QuestJob> questJobIdsForQuestId = 
			  QuestJobRetrieveUtils.getQuestJobsForQuestId(questId);
	  if (null == questJobIdsForQuestId) {
		  return new ArrayList<QuestJobProto>();
	  }

	  List<QuestJobProto> qjpList = new ArrayList<QuestJobProto>();
	  
	  for (Integer qjId : questJobIdsForQuestId.keySet()) {
		  QuestJob qj = questJobIdsForQuestId.get(qjId);
		  
		  QuestJobProto qjp = createQuestJobProto(qj);
		  qjpList.add(qjp);
	  }
	  
	  return qjpList;
  }
  
  public static QuestJobProto createQuestJobProto(QuestJob qj) {
	  QuestJobProto.Builder qjpb = QuestJobProto.newBuilder();
	  
	  qjpb.setQuestJobId(qj.getId());
	  qjpb.setQuestId(qj.getQuestId());
	  
	  String aStr = qj.getQuestJobType();
	  if (null != aStr) {
		  try {
			  QuestJobType qjt = QuestJobType.valueOf(aStr);
			  qjpb.setQuestJobType(qjt);
		  } catch (Exception e) {
			  log.error("incorrect QuestJobType: " + aStr +
					  ". QuestJob=" + qj);
		  }
	  }
	  
	  aStr = qj.getDescription();
	  if (null != aStr) {
		  qjpb.setDescription(aStr);
	  }
	  
	  qjpb.setStaticDataId(qj.getStaticDataId());
	  qjpb.setQuantity(qj.getQuantity());
	  qjpb.setPriority(qj.getPriority());
	  qjpb.setCityId(qj.getCityId());
	  qjpb.setCityAssetNum(qj.getCityAssetNum());
	  
	  return qjpb.build();	  
  }

  public static DialogueProto createDialogueProtoFromDialogue(Dialogue d) {
    if (d == null) return null;

    DialogueProto.Builder dp = DialogueProto.newBuilder();

    List<String> speakerTexts = d.getSpeakerTexts();
    List<String> speakers = d.getSpeakers();
    List<String> speakerImages = d.getSpeakerImages();
    List<Boolean> isLeftSides = d.getIsLeftSides();
    for (int i = 0; i < speakerTexts.size(); i++) {
      dp.addSpeechSegment(SpeechSegmentProto.newBuilder().setSpeaker(speakers.get(i)).
          setSpeakerText(speakerTexts.get(i)).setIsLeftSide(isLeftSides.get(i)).
          setSpeakerImage(speakerImages.get(i)).build());
    }
    return dp.build();
  }

  public static List<FullUserQuestProto> createFullUserQuestDataLarges(
		  List<QuestForUser> userQuests, Map<Integer, Quest> questIdsToQuests,
		  Map<Integer, Collection<QuestJobForUser>> questIdToUserQuestJobs) {
	  List<FullUserQuestProto> fullUserQuestDataLargeProtos =
			  new ArrayList<FullUserQuestProto>();

	  for (QuestForUser userQuest : userQuests) {
		  int questId = userQuest.getQuestId();
		  Quest quest = questIdsToQuests.get(questId);
		  FullUserQuestProto.Builder builder = FullUserQuestProto.newBuilder();

		  if (null == quest) {
			  log.error("no quest with id " + userQuest.getQuestId() + ", userQuest=" + userQuest);
		  }
		  
		  if (null != quest) {
			  builder.setUserId(userQuest.getUserId());
			  builder.setQuestId(quest.getId());
			  builder.setIsRedeemed(userQuest.isRedeemed());
			  builder.setIsComplete(userQuest.isComplete());

			  //protofy the userQuestJobs
			  List<UserQuestJobProto> userQuestJobProtoList =
					  createUserQuestJobProto(questId, questIdToUserQuestJobs);
			  

			  builder.addAllUserQuestJobs(userQuestJobProtoList);
			  fullUserQuestDataLargeProtos.add(builder.build());

		  }
	  }
	  return fullUserQuestDataLargeProtos;
  }
  
  public static List<UserQuestJobProto> createUserQuestJobProto(int questId,
		  Map<Integer, Collection<QuestJobForUser>> questIdToUserQuestJobs) {
	  List<UserQuestJobProto> userQuestJobProtoList =
			  new ArrayList<UserQuestJobProto>();
	  
	  for (QuestJobForUser qjfu : questIdToUserQuestJobs.get(questId)) {
		  UserQuestJobProto uqjp = createUserJobProto(qjfu);
		  userQuestJobProtoList.add(uqjp);
	  }
	  return userQuestJobProtoList;
  }
  
  public static UserQuestJobProto createUserJobProto(QuestJobForUser qjfu) {
	  UserQuestJobProto.Builder uqjpb = UserQuestJobProto.newBuilder();
	  
	  uqjpb.setQuestId(qjfu.getQuestId());
	  uqjpb.setQuestJobId(qjfu.getQuestJobId());
	  uqjpb.setIsComplete(qjfu.isComplete());
	  uqjpb.setProgress(qjfu.getProgress());
	  
	  return uqjpb.build();
  }

  public static ItemProto createItemProtoFromItem(Item item) {
  	ItemProto.Builder ipb = ItemProto.newBuilder();
  	
  	ipb.setItemId(item.getId());
  	
  	String str = item.getName();
  	if (null != str) {
  		ipb.setName(str);
  	}
  	
  	str = item.getImgName();
  	if (null != str) {
  		ipb.setImgName(str);
  	}
  	
  	str = item.getBorderImgName();
  	if (null != str) {
  		ipb.setBorderImgName(str);
  	}
  	
  	ColorProto.Builder clrB = ColorProto.newBuilder();
  	clrB.setBlue(item.getBlue());
  	clrB.setGreen(item.getGreen());
  	clrB.setRed(item.getRed());
  	ipb.setColor(clrB.build());
  	
  	return ipb.build();
  }

  /**Structure.proto************************************************/
  public static StructureInfoProto createStructureInfoProtoFromStructure(Structure s) {
    StructureInfoProto.Builder builder = StructureInfoProto.newBuilder();
    builder.setStructId(s.getId());

    String aStr = s.getName();
    if (null != aStr) {
      builder.setName(s.getName());
    }

    builder.setLevel(s.getLevel());
    aStr = s.getStructType();
    try {
      StructType st = StructType.valueOf(aStr);
      builder.setStructType(st);
    } catch (Exception e) {
      log.error("can't create enum type. structType=" + aStr + ".\t structure=" + s);
    }

    aStr = s.getBuildResourceType();
    try {
      ResourceType rt = ResourceType.valueOf(aStr);
      builder.setBuildResourceType(rt);
    } catch (Exception e) {
      log.error("can't create enum type. resourceType=" + aStr + ". structure=" + s);
    }

    builder.setBuildCost(s.getBuildCost());
    builder.setMinutesToBuild(s.getMinutesToBuild());
    builder.setPrerequisiteTownHallLvl(s.getRequiredTownHallLvl());
    builder.setWidth(s.getWidth());
    builder.setHeight(s.getHeight());

    if (s.getPredecessorStructId() > 0) {
      builder.setPredecessorStructId(s.getPredecessorStructId());
    }
    if (s.getSuccessorStructId() > 0) {
      builder.setSuccessorStructId(s.getSuccessorStructId());
    }

    aStr = s.getImgName();
    if (null != aStr) {
      builder.setImgName(aStr);
    }

    builder.setImgVerticalPixelOffset(s.getImgVerticalPixelOffset());
    builder.setImgHorizontalPixelOffset(s.getImgHorizontalPixelOffset());

    aStr = s.getDescription();
    if (null != aStr) {
      builder.setDescription(aStr);
    }

    aStr = s.getShortDescription();
    if (null != aStr) {
      builder.setShortDescription(aStr);
    }
    
    aStr = s.getShadowImgName();
    if (null != aStr) {
    	builder.setShadowImgName(aStr);
    }
    
    builder.setShadowVerticalOffset(s.getShadowVerticalOffset());
    builder.setShadowHorizontalOfffset(s.getShadowHorizontalOffset());
    builder.setShadowScale(s.getShadowScale());

    return builder.build();
  }

  public static ResourceGeneratorProto createResourceGeneratorProto(Structure s,
      StructureInfoProto sip, StructureResourceGenerator srg) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    ResourceGeneratorProto.Builder rgpb = ResourceGeneratorProto.newBuilder();
    rgpb.setStructInfo(sip);

    String aStr = srg.getResourceTypeGenerated();
    try {
      ResourceType rt = ResourceType.valueOf(aStr);
      rgpb.setResourceType(rt);
    } catch (Exception e) {
      log.error("can't create enum type. resourceType=" + aStr +
          ". resourceGenerator=" + srg);
    }

    rgpb.setProductionRate(srg.getProductionRate());
    rgpb.setCapacity(srg.getCapacity());

    return rgpb.build();
  }

  public static ResourceStorageProto createResourceStorageProto(Structure s,
      StructureInfoProto sip,  StructureResourceStorage srs) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    ResourceStorageProto.Builder rspb = ResourceStorageProto.newBuilder();
    rspb.setStructInfo(sip);

    String aStr = srs.getResourceTypeStored();
    try {
      ResourceType rt = ResourceType.valueOf(aStr);
      rspb.setResourceType(rt);
    } catch (Exception e) {
      log.error("can't create enum type. resourceType=" + aStr +
          ". resourceStorage=" + srs);
    }

    rspb.setCapacity(srs.getCapacity());

    return rspb.build();
  }

  public static HospitalProto createHospitalProto(Structure s, StructureInfoProto sip,
      StructureHospital sh) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    HospitalProto.Builder hpb = HospitalProto.newBuilder();
    hpb.setStructInfo(sip);
    hpb.setQueueSize(sh.getQueueSize());
    hpb.setHealthPerSecond(sh.getHealthPerSecond());

    return hpb.build();
  }

  public static LabProto createLabProto(Structure s, StructureInfoProto sip,
      StructureLab sl) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    LabProto.Builder lpb = LabProto.newBuilder();
    lpb.setStructInfo(sip);
    lpb.setQueueSize(sl.getQueueSize());
    lpb.setPointsPerSecond(sl.getPointsPerSecond());

    return lpb.build();
  }

  public static ResidenceProto createResidenceProto(Structure s, StructureInfoProto sip,
      StructureResidence sr) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    ResidenceProto.Builder rpb = ResidenceProto.newBuilder();
    rpb.setStructInfo(sip);
    rpb.setNumMonsterSlots(sr.getNumMonsterSlots());
    rpb.setNumBonusMonsterSlots(sr.getNumBonusMonsterSlots());
    rpb.setNumGemsRequired(sr.getNumGemsRequired());
    rpb.setNumAcceptedFbInvites(sr.getNumAcceptedFbInvites());
    String str = sr.getOccupationName();
    if (null != str) {
      rpb.setOccupationName(str);
    }
    return rpb.build();
  }

  public static TownHallProto createTownHallProto(Structure s, StructureInfoProto sip,
      StructureTownHall sth) {
    if (null == sip) {
      sip = createStructureInfoProtoFromStructure(s);
    }

    TownHallProto.Builder thpb = TownHallProto.newBuilder();
    thpb.setStructInfo(sip);
    thpb.setNumResourceOneGenerators(sth.getNumResourceOneGenerators());
    thpb.setNumResourceOneStorages(sth.getNumResourceOneStorages());
    thpb.setNumResourceTwoGenerators(sth.getNumResourceTwoGenerators());
    thpb.setNumResourceTwoStorages(sth.getNumResourceTwoStorages());
    thpb.setNumHospitals(sth.getNumHospitals());
    thpb.setNumResidences(sth.getNumResidences());
    thpb.setNumMonsterSlots(sth.getNumMonsterSlots());
    thpb.setNumLabs(sth.getNumLabs());
    thpb.setPvpQueueCashCost(sth.getPvpQueueCashCost());
    thpb.setResourceCapacity(sth.getResourceCapacity());

    return thpb.build();
  }

  public static FullUserStructureProto createFullUserStructureProtoFromUserstruct(StructureForUser userStruct) {
    FullUserStructureProto.Builder builder = FullUserStructureProto.newBuilder();
    builder.setUserStructId(userStruct.getId());
    builder.setUserId(userStruct.getUserId());
    builder.setStructId(userStruct.getStructId());
    //    builder.setLevel(userStruct.getLevel());
    builder.setFbInviteStructLvl(userStruct.getFbInviteStructLvl());
    builder.setIsComplete(userStruct.isComplete());
    builder.setCoordinates(createCoordinateProtoFromCoordinatePair(userStruct.getCoordinates()));
    String orientation = userStruct.getOrientation();
    try {
    	StructOrientation so = StructOrientation.valueOf(orientation);
    	builder.setOrientation(so);
    } catch (Exception e) {
    	log.error("invalid StructureForUser orientation. structureForUser=" + userStruct);
    }
    
    if (userStruct.getPurchaseTime() != null) {
      builder.setPurchaseTime(userStruct.getPurchaseTime().getTime());
    }
    if (userStruct.getLastRetrieved() != null) {
      builder.setLastRetrieved(userStruct.getLastRetrieved().getTime());
    }
    //    if (userStruct.getLastUpgradeTime() != null) {
    //      builder.setLastUpgradeTime(userStruct.getLastUpgradeTime().getTime());
    //    }
    return builder.build();
  }

  public static CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp) {
  	CoordinateProto.Builder cpb = CoordinateProto.newBuilder();
  	cpb.setX(cp.getX());
  	cpb.setY(cp.getY());
  	
  	return cpb.build();
  }
  
  public static TutorialStructProto createTutorialStructProto(int structId, float posX,
      float posY) {
    TutorialStructProto.Builder tspb = TutorialStructProto.newBuilder();

    tspb.setStructId(structId);
    CoordinatePair cp = new CoordinatePair(posX, posY);
    CoordinateProto cpp = CreateInfoProtoUtils.createCoordinateProtoFromCoordinatePair(cp);
    tspb.setCoordinate(cpp);
    return tspb.build();
  }
  
  public static ObstacleProto createObstacleProtoFromObstacle(Obstacle o) {
  	ObstacleProto.Builder ob = ObstacleProto.newBuilder();
  	
  	ob.setObstacleId(o.getId());
  	String aStr = o.getName();
  	if (null != aStr) {
  		ob.setName(aStr);
  	}
  	
  	aStr = o.getRemovalCostType();
  	try {
  		ResourceType rt = ResourceType.valueOf(aStr);
  		ob.setRemovalCostType(rt);
  	} catch (Exception e) {
  		log.info("incorrect resource type name in db. name=" + aStr, e);
  	}
  	
  	ob.setCost(o.getCost());
  	ob.setSecondsToRemove(o.getSecondsToRemove());
  	ob.setWidth(o.getWidth());
  	ob.setHeight(o.getHeight());
  	
  	aStr = o.getImgName();
  	if (null != aStr) {
  		ob.setImgName(aStr);
  	}
  	
  	ob.setImgVerticalPixelOffset(o.getImgVerticalPixelOffset());
  	
  	aStr = o.getDescription();
  	if (null != aStr) {
  		ob.setDescription(aStr);
  	}
  	ob.setChanceToAppear(o.getChanceToAppear());
  	
  	aStr = o.getShadowImgName();
  	if (null != aStr) {
  		ob.setShadowImgName(o.getShadowImgName());
  	}
  	
  	ob.setShadowVerticalOffset(o.getShadowVerticalOffset());
  	ob.setShadowHorizontalOfffset(o.getShadowHorizontalOffset());
  	
  	return ob.build();
  }
  
  public static MinimumObstacleProto createMinimumObstacleProto(int obstacleId,
  		float posX, float posY, int orientation) {
  	
  	MinimumObstacleProto.Builder mopb = MinimumObstacleProto.newBuilder();
  	mopb.setObstacleId(obstacleId);
  	
  	CoordinatePair cp = new CoordinatePair(posX, posY);
  	CoordinateProto cProto = createCoordinateProtoFromCoordinatePair(cp); 
  	mopb.setCoordinate(cProto);
  	
  	StructOrientation structOrientation = StructOrientation.valueOf(orientation);
  	mopb.setOrientation(structOrientation);
  	
  	return mopb.build();
  }
  
  public static UserObstacleProto createUserObstacleProto(ObstacleForUser ofu) {
  	UserObstacleProto.Builder uopb = UserObstacleProto.newBuilder();
  	uopb.setUserObstacleId(ofu.getId());
  	uopb.setUserId(ofu.getUserId());
  	uopb.setObstacleId(ofu.getObstacleId());
  	
  	int x = ofu.getXcoord();
  	int y = ofu.getYcoord();
  	CoordinatePair cp = new CoordinatePair(x, y);
  	CoordinateProto cproto = createCoordinateProtoFromCoordinatePair(cp);
  	uopb.setCoordinates(cproto);
  	
  	String orientation = ofu.getOrientation();
  	try {
  		StructOrientation so = StructOrientation.valueOf(orientation);
  		uopb.setOrientation(so);
  	} catch (Exception e) {
  		log.error("incorrect struct orientation=" + orientation + "\t ofu=" + ofu);
  	}
  	
  	Date removalStartTime = ofu.getRemovalTime();
  	
  	if (null != removalStartTime) {
  		uopb.setRemovalStartTime(removalStartTime.getTime());
  	}
  	
  	return uopb.build();
  }
  
  /**Task.proto*****************************************************/
  //individualCash should always be set, could be 0 or more
  public static TaskStageProto createTaskStageProto (int taskStageId, TaskStage ts,
      List<TaskStageMonster> taskStageMonsters, List<Boolean> puzzlePiecesDropped,
      List<Integer> individualCash, List<Integer> individualOil,
      Map<Integer, Integer> tsmIdToItemId) {

    TaskStageProto.Builder tspb = TaskStageProto.newBuilder();
    if (null == ts) {
      ts = TaskStageRetrieveUtils.getTaskStageForTaskStageId(taskStageId);
    }

    tspb.setStageId(taskStageId);

    //holds all the monsterProtos
    List<TaskStageMonsterProto> mpList = new ArrayList<TaskStageMonsterProto>();

    for (int i = 0; i < taskStageMonsters.size(); i++) {
      TaskStageMonster tsm = taskStageMonsters.get(i);

      boolean puzzlePieceDropped = puzzlePiecesDropped.get(i);
      int cashDrop = individualCash.get(i);
      int oilDrop = individualOil.get(i);

      TaskStageMonsterProto mp = createTaskStageMonsterProto(tsm, cashDrop, oilDrop,
          puzzlePieceDropped, tsmIdToItemId);
      mpList.add(mp);
    }

    tspb.addAllStageMonsters(mpList);

    return tspb.build();
  }
  
  //going by stage number instead of id, maybe because it's human friendly
  //when looking at the db
  public static TaskStageProto createTaskStageProto (int taskId, int stageNum,
		  List<TaskStageForUser> monsters) {
	  TaskStageProto.Builder tspb = TaskStageProto.newBuilder();
	  
	  TaskStage ts = TaskStageRetrieveUtils.getTaskStageForTaskStageId(taskId,
			  stageNum);
	  int taskStageId = ts.getId();
	  tspb.setStageId(taskStageId);
	  
	  for (TaskStageForUser tsfu : monsters) {
		  TaskStageMonsterProto tsmp = createTaskStageMonsterProto(tsfu); 
		  tspb.addStageMonsters(tsmp);
	  }
	  
	  return tspb.build();
  }

  public static FullTaskProto createFullTaskProtoFromTask(Task task) {
    String name = task.getGoodName();
    String description = task.getDescription();

    FullTaskProto.Builder builder = FullTaskProto.newBuilder();
    builder.setTaskId(task.getId());
    if (null != name) {
      builder.setName(name);
    }

    if (null != description) {
      builder.setDescription(description);
    }

    builder.setCityId(task.getCityId());
    builder.setAssetNumWithinCity(task.getAssetNumberWithinCity());

    int prerequisiteTaskId = task.getPrerequisiteTaskId();
    if (prerequisiteTaskId > 0) {
      builder.setPrerequisiteTaskId(prerequisiteTaskId);
    }

    int prerequisiteQuestId = task.getPrerequisiteQuestId();
    if (prerequisiteQuestId > 0) {
      builder.setPrerequisiteQuestId(prerequisiteQuestId);
    }

    return builder.build();
  }

  public static MinimumUserTaskProto createMinimumUserTaskProto(int userId,
		  TaskForUserOngoing aTaskForUser) {
  	MinimumUserTaskProto.Builder mutpb = MinimumUserTaskProto.newBuilder();
  	mutpb.setUserId(userId);
  	
  	int taskId = aTaskForUser.getTaskId();
  	mutpb.setTaskId(taskId);
  	int taskStageId = aTaskForUser.getTaskStageId();
  	mutpb.setCurTaskStageId(taskStageId);
  	long userTaskId = aTaskForUser.getId();
  	mutpb.setUserTaskId(userTaskId);
  	
  	return mutpb.build();
  }

  public static TaskStageMonsterProto createTaskStageMonsterProto (TaskStageMonster tsm, 
      int cashReward, int oilReward, boolean pieceDropped,
      Map<Integer, Integer> tsmIdToItemId) {
    int tsmMonsterId = tsm.getMonsterId();

    TaskStageMonsterProto.Builder bldr = TaskStageMonsterProto.newBuilder();
    bldr.setMonsterId(tsmMonsterId);
    String tsmMonsterType = tsm.getMonsterType(); 
    try {
    	MonsterType mt = MonsterType.valueOf(tsmMonsterType);
    	bldr.setMonsterType(mt);
    } catch (Exception e) {
    	log.error("monster type incorrect, tsm=" + tsm);
    }
    bldr.setCashReward(cashReward);
    bldr.setOilReward(oilReward);
    bldr.setPuzzlePieceDropped(pieceDropped);
    bldr.setExpReward(tsm.getExpReward());
    bldr.setLevel(tsm.getLevel());

    int tsmId = tsm.getId();
    if (tsmIdToItemId.containsKey(tsmId)) {
      //if multiple identical monsters spawned, each one should have a 
      //corresponding item id that it drops, could be -1. (-1 means no item drop)
      int itemId = tsmIdToItemId.get(tsmId);

      if (itemId > 0) {
        bldr.setItemId(itemId);
      }
      log.info("creating tsm. taskStageMonsterIdToItemId=" + tsmIdToItemId);
      log.info("itemId=" + itemId);
      log.info("tsm=" + bldr.build());
    }

    return bldr.build();
  }
  
  public static TaskStageMonsterProto createTaskStageMonsterProto (
		  TaskStageForUser tsfu) {
	  int tsmId = tsfu.getTaskStageMonsterId();
	  TaskStageMonster tsm = TaskStageMonsterRetrieveUtils
			  .getTaskStageMonsterForId(tsmId);

	  int tsmMonsterId = tsm.getMonsterId();
	  boolean didPieceDrop = tsfu.isMonsterPieceDropped();
	  //check if monster id exists
	  if (didPieceDrop) {
		  Monster mon = MonsterRetrieveUtils.getMonsterForMonsterId(tsmMonsterId);
		  if (null == mon)  {
			  throw new RuntimeException("Non existent monsterId for userTask=" +
					  tsfu);
		  }
	  }

	  TaskStageMonsterProto.Builder bldr = TaskStageMonsterProto.newBuilder();
	  bldr.setMonsterId(tsmMonsterId);
	  String tsmMonsterType = tsfu.getMonsterType(); 
	  try {
		  MonsterType mt = MonsterType.valueOf(tsmMonsterType);
		  bldr.setMonsterType(mt);
	  } catch (Exception e) {
		  log.error("monster type incorrect, tsm=" + tsm);
	  }
	  bldr.setCashReward(tsfu.getCashGained());
	  bldr.setOilReward(tsfu.getOilGained());
	  bldr.setPuzzlePieceDropped(didPieceDrop);
	  bldr.setExpReward(tsfu.getExpGained());
	  
	  bldr.setLevel(tsm.getLevel());

	  int itemId = tsfu.getItemIdDropped();
	  if (itemId > 0) {
		  //check if item exists
		  Item item = ItemRetrieveUtils.getItemForId(itemId);
		  if (null == item) {
			  throw new RuntimeException("nonexistent itemId for userTask=" +
					  tsfu);
		  }
		  bldr.setItemId(itemId);
	  }

	  return bldr.build();
  }

  public static PersistentEventProto createPersistentEventProtoFromEvent(
      EventPersistent event) {
    PersistentEventProto.Builder pepb = PersistentEventProto.newBuilder();

    int eventId = event.getId();
    String dayOfWeekStr = event.getDayOfWeek();
    int startHour = event.getStartHour();
    int eventDurationMinutes = event.getEventDurationMinutes();
    int taskId = event.getTaskId();
    int cooldownMinutes = event.getCooldownMinutes();
    String eventTypeStr = event.getEventType();
    String monsterElem = event.getMonsterElement();

    pepb.setEventId(eventId);
    try {
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
      pepb.setDayOfWeek(dayOfWeek);
    } catch (Exception e) {
      log.error("can't create enum type. dayOfWeek=" + dayOfWeekStr + ".\t event=" + event);
    }

    pepb.setStartHour(startHour);
    pepb.setEventDurationMinutes(eventDurationMinutes);
    pepb.setTaskId(taskId);
    pepb.setCooldownMinutes(cooldownMinutes);

    try {
      EventType typ = EventType.valueOf(eventTypeStr);
      pepb.setType(typ);
    } catch (Exception e) {
      log.error("can't create enum type. eventType=" + eventTypeStr + ".\t event=" + event);
    }
    try {
      MonsterElement elem = MonsterElement.valueOf(monsterElem);
      pepb.setMonsterElement(elem);
    } catch (Exception e) {
      log.error("can't create enum type. monster elem=" + monsterElem + 
          ".\t event=" + event);
    }

    return pepb.build();
  }

  public static UserPersistentEventProto createUserPersistentEventProto(
      EventPersistentForUser epfu) {
    UserPersistentEventProto.Builder upepb = UserPersistentEventProto.newBuilder();

    int userId = epfu.getUserId();
    int eventId = epfu.getEventPersistentId();
    Date timeOfEntry = epfu.getTimeOfEntry();

    upepb.setUserId(userId);
    upepb.setEventId(eventId);

    if (null != timeOfEntry) {
      upepb.setCoolDownStartTime(timeOfEntry.getTime());
    }

    return upepb.build();
  }

  /**TournamentStuff.proto******************************************/
  public static TournamentEventProto createTournamentEventProtoFromTournamentEvent(
      TournamentEvent e, List<TournamentEventReward> rList) {

    TournamentEventProto.Builder b = TournamentEventProto.newBuilder().setEventId(e.getId()).setStartDate(e.getStartDate().getTime())
        .setEndDate(e.getEndDate().getTime()).setEventName(e.getEventName())
        .setLastShowDate(e.getEndDate().getTime()+ControllerConstants.TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END*3600000L);

    List<TournamentEventRewardProto> rProtosList = new ArrayList<TournamentEventRewardProto>();
    for(TournamentEventReward r : rList) {
      TournamentEventRewardProto rProto = createTournamentEventRewardProtoFromTournamentEventReward(r);
      rProtosList.add(rProto);
    }

    b.addAllRewards(rProtosList);

    return b.build();
  }
  
  public static TournamentEventRewardProto createTournamentEventRewardProtoFromTournamentEventReward(TournamentEventReward r) {

    TournamentEventRewardProto.Builder b = TournamentEventRewardProto.newBuilder()
        .setTournamentEventId(r.getTournamentEventId()).setMinRank(r.getMinRank()).setMaxRank(r.getMaxRank())
        .setGoldRewarded(r.getGoldRewarded()).setBackgroundImageName(r.getBackgroundImageName())
        .setPrizeImageName(r.getPrizeImageName());

    ColorProto.Builder clrB = ColorProto.newBuilder().setBlue(r.getBlue())
        .setGreen(r.getGreen()).setRed(r.getRed()); 

    b.setTitleColor(clrB.build());
    return b.build();
  }

  public static MinimumUserProtoWithLevelForTournament createMinimumUserProtoWithLevelForTournament(User u, int rank, double score) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(u);
    MinimumUserProtoWithLevelForTournament.Builder mupwlftb = MinimumUserProtoWithLevelForTournament.newBuilder();
    mupwlftb.setMinUserProto(mup);
    mupwlftb.setLevel(u.getLevel());
    mupwlftb.setTournamentRank(rank);
    mupwlftb.setTournamentScore(score);
    return mupwlftb.build();
  }

  /**User.proto*****************************************************/
  public static MinimumClanProto createMinimumClanProtoFromClan(Clan c) {
    MinimumClanProto.Builder mcp = MinimumClanProto.newBuilder();
    mcp.setClanId(c.getId());
    mcp.setName(c.getName());
    //    mcp.setOwnerId(c.getOwnerId());
    mcp.setCreateTime(c.getCreateTime().getTime());
    mcp.setDescription(c.getDescription());
    mcp.setTag(c.getTag());
    mcp.setClanIconId(c.getClanIconId());
    return mcp.setRequestToJoinRequired(c.isRequestToJoinRequired()).build();
  }

  public static MinimumUserProto createMinimumUserProtoFromUser(User u) {
    MinimumUserProto.Builder builder = MinimumUserProto.newBuilder().setName(u.getName()).setUserId(u.getId());
    if (u.getClanId() > 0) {
      Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
      builder.setClan(createMinimumClanProtoFromClan(clan));
    }
    return builder.build();
  }

  public static MinimumUserProto createMinimumUserProtoFromUserAndClan(User u, Clan c) {
    MinimumUserProto.Builder builder = MinimumUserProto.newBuilder().setName(u.getName())
        .setUserId(u.getId()).setClan(createMinimumClanProtoFromClan(c));
    return builder.build();
  }

  public static MinimumUserProtoWithLevel createMinimumUserProtoWithLevelFromUserAndClan(User u, Clan c) {
    MinimumUserProto.Builder builder = MinimumUserProto.newBuilder();
    builder.setName(u.getName());
    builder.setUserId(u.getId());
    builder.setClan(createMinimumClanProtoFromClan(c));

    MinimumUserProtoWithLevel.Builder builderWithLevel = MinimumUserProtoWithLevel.newBuilder();
    builderWithLevel.setMinUserProto(builder.build());
    builderWithLevel.setLevel(u.getLevel());
    return builderWithLevel.build();
  }

  public static MinimumUserProtoWithLevel createMinimumUserProtoWithLevelFromUser(User u) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(u);
    MinimumUserProtoWithLevel.Builder mupWithLevel = MinimumUserProtoWithLevel.newBuilder();
    mupWithLevel.setMinUserProto(mup);
    mupWithLevel.setLevel(u.getLevel());
    return mupWithLevel.build();
  }

  public static MinimumUserProtoWithFacebookId createMinimumUserProtoWithFacebookIdFromUser(User u) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(u);
    MinimumUserProtoWithFacebookId.Builder b = MinimumUserProtoWithFacebookId.newBuilder();
    b.setMinUserProto(mup);
    String facebookId = u.getFacebookId();
    if (null != facebookId) {
      b.setFacebookId(facebookId);
    }

    return b.build();
  }

  public static UserFacebookInviteForSlotProto createUserFacebookInviteForSlotProtoFromInvite(
      UserFacebookInviteForSlot invite, User inviter, MinimumUserProtoWithFacebookId inviterProto) {
    UserFacebookInviteForSlotProto.Builder inviteProtoBuilder =
        UserFacebookInviteForSlotProto.newBuilder();
    inviteProtoBuilder.setInviteId(invite.getId());

    if (null == inviterProto) {
      inviterProto = createMinimumUserProtoWithFacebookIdFromUser(inviter);

    }

    inviteProtoBuilder.setInviter(inviterProto);
    inviteProtoBuilder.setRecipientFacebookId(invite.getRecipientFacebookId());

    Date d = invite.getTimeOfInvite();
    if (null != d) {
      inviteProtoBuilder.setTimeOfInvite(d.getTime());
    }

    d = invite.getTimeAccepted();
    if (null != d) {
      inviteProtoBuilder.setTimeAccepted(d.getTime());
    }

    int userStructId = invite.getUserStructId();
    inviteProtoBuilder.setUserStructId(userStructId);

    int userStructFbLvl = invite.getUserStructFbLvl();
    inviteProtoBuilder.setStructFbLvl(userStructFbLvl);

    d = invite.getTimeRedeemed();
    if (null != d) {
      inviteProtoBuilder.setRedeemedTime(d.getTime());
    }

    return inviteProtoBuilder.build();
  }

  public static FullUserProto createFullUserProtoFromUser(User u,
		  PvpLeagueForUser plfu) {
    FullUserProto.Builder builder = FullUserProto.newBuilder();
    int userId = u.getId();
    builder.setUserId(userId);
    builder.setName(u.getName());
    builder.setLevel(u.getLevel());
    builder.setGems(u.getGems());
    builder.setCash(u.getCash());
    builder.setOil(u.getOil());
    builder.setExperience(u.getExperience());
    builder.setTasksCompleted(u.getTasksCompleted());
    if (u.getReferralCode() != null) {
      builder.setReferralCode(u.getReferralCode());
    }
    builder.setNumReferrals(u.getNumReferrals());
    if (u.getLastLogin() != null) {
      builder.setLastLoginTime(u.getLastLogin().getTime());
    }	
    if (u.getLastLogout() != null) {
      builder.setLastLogoutTime(u.getLastLogout().getTime());
    }
    builder.setIsFake(u.isFake());
    builder.setCreateTime(u.getCreateTime().getTime());
    builder.setIsAdmin(u.isAdmin());
    builder.setNumCoinsRetrievedFromStructs(u.getNumCoinsRetrievedFromStructs());
    builder.setNumOilRetrievedFromStructs(u.getNumOilRetrievedFromStructs());
    if (u.getClanId() > 0) {
      Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
      builder.setClan(createMinimumClanProtoFromClan(clan));
    }
    builder.setHasReceivedfbReward(u.isHasReceivedfbReward());
    builder.setNumBeginnerSalesPurchased(u.getNumBeginnerSalesPurchased());

    String facebookId = u.getFacebookId();
    if (null != facebookId) {
      builder.setFacebookId(facebookId);
    }

    String gameCenterId = u.getGameCenterId();
    if (null != gameCenterId) {
      builder.setGameCenterId(gameCenterId);
    }
    
    Date lastObstacleSpawnedTime = u.getLastObstacleSpawnedTime();
    if (null != lastObstacleSpawnedTime) {
    	builder.setLastObstacleSpawnedTime(lastObstacleSpawnedTime.getTime());
    }
    
    if (null != plfu) {
    	//every user should have one, since pvp info created when user is created
    	//but could be null if not important to have it
    	UserPvpLeagueProto pvpLeagueInfo = createUserPvpLeagueProto(userId,
    			plfu, null, false);
    	builder.setPvpLeagueInfo(pvpLeagueInfo);
    }
    
    int numObstaclesRemoved = u.getNumObstaclesRemoved();
    builder.setNumObstaclesRemoved(numObstaclesRemoved);
    
    //add new columns above here, not below the if. if case for is fake

    if (u.isFake()) {

    }
    //don't add setting new columns/properties here, add up above

    return builder.build();
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  

  public static ReferralNotificationProto createReferralNotificationProtoFromReferral(
      Referral r, User newlyReferred) {
    return ReferralNotificationProto.newBuilder().setReferred(createMinimumUserProtoFromUser(newlyReferred))
        .setRecruitTime(r.getTimeOfReferral().getTime()).setCoinsGivenToReferrer(r.getCoinsGivenToReferrer()).build();
  }


  public static AnimatedSpriteOffsetProto createAnimatedSpriteOffsetProtoFromAnimatedSpriteOffset(AnimatedSpriteOffset aso) {
    return AnimatedSpriteOffsetProto.newBuilder().setImageName(aso.getImgName())
        .setOffSet(createCoordinateProtoFromCoordinatePair(aso.getOffSet())).build();
  }

  public static void createMinimumUserProtosFromClannedAndClanlessUsers(
      Map<Integer, Clan> clanIdsToClans, Map<Integer, Set<Integer>> clanIdsToUserIdSet,
      List<Integer> clanlessUserIds, Map<Integer, User> userIdsToUsers, 
      Map<Integer, MinimumUserProtoWithLevel> userIdToMinimumUserProtoWithLevel) {
    //construct the minimum user protos for the clanless users
    for (int userId : clanlessUserIds) {
      User u = userIdsToUsers.get(userId);
      MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevelFromUser(u);
      userIdToMinimumUserProtoWithLevel.put(userId, mupwl);
    }

    //construct the minimum user protos for the users that have clans 
    if (null == clanIdsToClans) {
    	return;
    }
    for (int clanId : clanIdsToClans.keySet()) {
    	Clan c = clanIdsToClans.get(clanId);
    	
    	//create minimum user protos for users associated with clan
    	for (int userId: clanIdsToUserIdSet.get(clanId)) {
    		User u = userIdsToUsers.get(userId);
    		MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevelFromUserAndClan(u, c);
    		userIdToMinimumUserProtoWithLevel.put(userId, mupwl);
    	}
    }
  }

 }
