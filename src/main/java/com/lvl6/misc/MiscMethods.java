package com.lvl6.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.lvl6.events.response.GeneralNotificationResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.City;
import com.lvl6.info.ClanRaid;
import com.lvl6.info.Dialogue;
import com.lvl6.info.EventPersistent;
import com.lvl6.info.ExpansionCost;
import com.lvl6.info.GoldSale;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterBattleDialogue;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.StaticUserLevelInfo;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureHospital;
import com.lvl6.info.StructureLab;
import com.lvl6.info.StructureResidence;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.StructureResourceStorage;
import com.lvl6.info.StructureTownHall;
import com.lvl6.info.Task;
import com.lvl6.info.TournamentEvent;
import com.lvl6.info.TournamentEventReward;
import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.Globals;
import com.lvl6.properties.IAPValues;
import com.lvl6.properties.MDCKeys;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.proto.CityProto.CityExpansionCostProto;
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.ClanConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.DownloadableNibConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.MonsterConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.UserMonsterConstants;
import com.lvl6.proto.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.InAppPurchaseProto.InAppPurchasePackageProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto.Builder;
import com.lvl6.proto.StructureProto.HospitalProto;
import com.lvl6.proto.StructureProto.LabProto;
import com.lvl6.proto.StructureProto.ResidenceProto;
import com.lvl6.proto.StructureProto.ResourceGeneratorProto;
import com.lvl6.proto.StructureProto.ResourceStorageProto;
import com.lvl6.proto.StructureProto.StructureInfoProto;
import com.lvl6.proto.StructureProto.TownHallProto;
import com.lvl6.proto.TaskProto.ClanRaidProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.PersistentEventProto;
import com.lvl6.proto.TournamentStuffProto.TournamentEventProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.StaticUserLevelInfoProto;
import com.lvl6.retrieveutils.rarechange.BannedUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.EventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GoldSaleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.LockBoxEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterBattleDialogueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ProfanityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StaticUserLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureHospitalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureLabRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResidenceRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceStorageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureTownHallRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TournamentEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TournamentEventRewardRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.QuestUtils;

public class MiscMethods {


  private static final Logger log = LoggerFactory.getLogger(MiscMethods.class);
  public static final String cash = "cash";
  public static final String gems = "gems";
  public static final String oil = "oil";
  public static final String boosterPackId = "boosterPackId";


  public static Dialogue createDialogue(String dialogueBlob) {
    if (dialogueBlob != null && dialogueBlob.length() > 0) { 
      StringTokenizer st = new StringTokenizer(dialogueBlob, "~");

      List<Boolean> isLeftSides = new ArrayList<Boolean>();
      List<String> speakers = new ArrayList<String>();
      List<String> speakerTexts = new ArrayList<String>();

      try {
        while (st.hasMoreTokens()) {
          String tok = st.nextToken();
          StringTokenizer st2 = new StringTokenizer(tok, ".");
          log.warn("Token: "+tok);
          if (st2.hasMoreTokens()) {
            Boolean isLeftSide = st2.nextToken().toUpperCase().equals("L");
            if (st2.hasMoreTokens()) {
              String speaker = st2.nextToken();
              String speakerText = st.nextToken();
              if (speakerText != null) {
                isLeftSides.add(isLeftSide);
                speakers.add(speaker);
                speakerTexts.add(speakerText);
              }
            }
          }
        }
      } catch (Exception e) {
        log.error("problem with creating dialogue object for this dialogueblob: {}", dialogueBlob, e);
      }
      return new Dialogue(speakers, speakerTexts, isLeftSides);
    }
    return null;
  }

  public static void explodeIntoInts(String stringToExplode, 
      String delimiter, List<Integer> returnValue) {
    StringTokenizer st = new StringTokenizer(stringToExplode, ", ");
    while (st.hasMoreTokens()) {
      returnValue.add(Integer.parseInt(st.nextToken()));
    }
  }

  public static String getIPOfPlayer(GameServer server, Integer playerId, String udid) {
    ConnectedPlayer player = null;
    if (playerId != null && playerId > 0) {
      player = server.getPlayerById(playerId); 
      if (player != null) {
        return player.getIp_connection_id();
      }
    }
    if (udid != null) {
      player = server.getPlayerByUdId(udid);
      if (player != null) {
        return player.getIp_connection_id();
      }
    }
    return null;
  }

  public static void purgeMDCProperties(){
    MDC.remove(MDCKeys.UDID);
    MDC.remove(MDCKeys.PLAYER_ID);
    MDC.remove(MDCKeys.IP);
  }

  public static void setMDCProperties(String udid, Integer playerId, String ip) {
    purgeMDCProperties();
    if (udid != null) MDC.put(MDCKeys.UDID, udid);
    if (ip != null) MDC.put(MDCKeys.IP, ip);
    if (playerId != null && playerId > 0) MDC.put(MDCKeys.PLAYER_ID.toString(), playerId.toString());
  }

  public static int calculateCoinsGivenToReferrer(User referrer) {
    return Math.min(ControllerConstants.USER_CREATE__MIN_COIN_REWARD_FOR_REFERRER, (int)(Math.ceil(
        (referrer.getCash()) * 
        ControllerConstants.USER_CREATE__PERCENTAGE_OF_COIN_WEALTH_GIVEN_TO_REFERRER)));
  }


  public static boolean checkClientTimeAroundApproximateNow(Timestamp clientTime) {
    if (clientTime.getTime() < new Date().getTime() + Globals.NUM_MINUTES_DIFFERENCE_LEEWAY_FOR_CLIENT_TIME*60000 && 
        clientTime.getTime() > new Date().getTime() - Globals.NUM_MINUTES_DIFFERENCE_LEEWAY_FOR_CLIENT_TIME*60000) {
      return true;
    }
    return false;
  }

  public static List<City> getCitiesAvailableForUserLevel(int userLevel) {
    List<City> availCities = new ArrayList<City>();
    Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
    for (Integer cityId : cities.keySet()) {
      City city = cities.get(cityId);
      availCities.add(city);
    }
    return availCities;
  }

  public static UpdateClientUserResponseEvent createUpdateClientUserResponseEventAndUpdateLeaderboard(User user) {
    try {
      if (!user.isFake()) {
        LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
        leaderboard.updateLeaderboardForUser(user);
      }
    } catch (Exception e) {
      log.error("Failed to update leaderboard.");
    }

    UpdateClientUserResponseEvent resEvent = new UpdateClientUserResponseEvent(user.getId());
    UpdateClientUserResponseProto resProto = UpdateClientUserResponseProto.newBuilder()
        .setSender(CreateInfoProtoUtils.createFullUserProtoFromUser(user))
        .setTimeOfUserUpdate(new Date().getTime()).build();
    resEvent.setUpdateClientUserResponseProto(resProto);
    return resEvent;
  }


  public static int getRowCount(ResultSet set) {
    int rowCount;
    int currentRow;
    try {
      currentRow = set.getRow();
      rowCount = set.last() ? set.getRow() : 0; 
      if (currentRow == 0)          
        set.beforeFirst(); 
      else      
        set.absolute(currentRow);
      return rowCount;
    } catch (SQLException e) {
      log.error("getRowCount error.", e);
      return -1;
    }     

  }

  public static StartupConstants createStartupConstantsProto() {
    StartupConstants.Builder cb = StartupConstants.newBuilder();

    for (String id : IAPValues.iapPackageNames) {
      InAppPurchasePackageProto.Builder iapb = InAppPurchasePackageProto.newBuilder();
      iapb.setImageName(IAPValues.getImageNameForPackageName(id));
      iapb.setIapPackageId(id);

      int diamondAmt = IAPValues.getDiamondsForPackageName(id);
      if (diamondAmt > 0) {
        iapb.setCurrencyAmount(diamondAmt);
      } else {
        int coinAmt = IAPValues.getCoinsForPackageName(id);
        iapb.setCurrencyAmount(coinAmt);
      }
      cb.addInAppPurchasePackages(iapb.build());
    }

    cb.setMaxLevelForUser(ControllerConstants.LEVEL_UP__MAX_LEVEL_FOR_USER);
    cb.setMaxNumOfSingleStruct(ControllerConstants.PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE);

    if (ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS != null) {
      for (int i = 0; i < ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS.length; i++) {
        AnimatedSpriteOffset aso = ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS[i];
        cb.addAnimatedSpriteOffsets(CreateInfoProtoUtils.createAnimatedSpriteOffsetProtoFromAnimatedSpriteOffset(aso));
      }
    }

    cb.setMinNameLength(ControllerConstants.USER_CREATE__MIN_NAME_LENGTH);
    cb.setMaxNameLength(ControllerConstants.USER_CREATE__MAX_NAME_LENGTH);
    cb.setMaxLengthOfChatString(ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING);

    ClanConstants.Builder clanConstantsBuilder = ClanConstants.newBuilder();
    clanConstantsBuilder.setMaxCharLengthForClanDescription(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION);
    clanConstantsBuilder.setMaxCharLengthForClanName(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME);
    clanConstantsBuilder.setCoinPriceToCreateClan(ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN);
    clanConstantsBuilder.setMaxCharLengthForClanTag(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG);
    cb.setClanConstants(clanConstantsBuilder.build());


    DownloadableNibConstants.Builder dncb = DownloadableNibConstants.newBuilder();
    dncb.setMapNibName(ControllerConstants.NIB_NAME__TRAVELING_MAP);
    dncb.setExpansionNibName(ControllerConstants.NIB_NAME__EXPANSION);
    dncb.setGoldShoppeNibName(ControllerConstants.NIB_NAME__GOLD_SHOPPE);
    cb.setDownloadableNibConstants(dncb.build());

    cb.setNumHoursBeforeReshowingGoldSale(ControllerConstants.NUM_HOURS_BEFORE_RESHOWING_GOLD_SALE);
    cb.setLevelToShowRateUsPopup(ControllerConstants.LEVEL_TO_SHOW_RATE_US_POPUP);
    //        .setHoursInAttackedByOneProtectionPeriod(ControllerConstants.BATTLE__HOURS_IN_ATTACKED_BY_ONE_PROTECTION_PERIOD)
    //        .setMaxNumTimesAttackedByOneInProtectionPeriod(ControllerConstants.BATTLE__MAX_NUM_TIMES_ATTACKED_BY_ONE_IN_PROTECTION_PERIOD)
    //        .setMinBattlesRequiredForKDRConsideration(ControllerConstants.LEADERBOARD__MIN_BATTLES_REQUIRED_FOR_KDR_CONSIDERATION)
    //        .setNumHoursBeforeReshowingLockBox(ControllerConstants.NUM_HOURS_BEFORE_RESHOWING_LOCK_BOX)

    //SET TOURNAMENT CONSTANTS HERE 

    cb.setFbConnectRewardDiamonds(ControllerConstants.EARN_FREE_DIAMONDS__FB_CONNECT_REWARD);
    cb.setFaqFileName(ControllerConstants.STARTUP__FAQ_FILE_NAME);
    User adminChatUser = StartupStuffRetrieveUtils.getAdminChatUser();
    MinimumUserProto adminChatUserProto = CreateInfoProtoUtils.createMinimumUserProtoFromUser(adminChatUser);
    cb.setAdminChatUserProto(adminChatUserProto);
    cb.setNumBeginnerSalesAllowed(ControllerConstants.NUM_BEGINNER_SALES_ALLOWED);

    UserMonsterConstants.Builder umcb = UserMonsterConstants.newBuilder();
    umcb.setMaxNumTeamSlots(ControllerConstants.MONSTER_FOR_USER__MAX_TEAM_SIZE);
    umcb.setInitialMaxNumMonsterLimit(ControllerConstants.MONSTER_FOR_USER__INITIAL_MAX_NUM_MONSTER_LIMIT);
    //    umcb.setMonsterInventoryIncrementAmount(ControllerConstants.MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT);
    //    umcb.setGemPricePerSlot(ControllerConstants.MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT);
    //    umcb.setNumFriendsToRecruitToIncreaseInventory(ControllerConstants.MONSTER_INVENTORY_SLOTS__MIN_INVITES_TO_INCREASE_SLOTS);
    cb.setUserMonsterConstants(umcb.build());

    MonsterConstants.Builder mcb = MonsterConstants.newBuilder();
    mcb.setCashPerHealthPoint(ControllerConstants.MONSTER__CASH_PER_HEALTH_POINT);
    mcb.setSecondsToHealPerHealthPoint(ControllerConstants.MONSTER__SECONDS_TO_HEAL_PER_HEALTH_POINT);
    mcb.setElementalStrength(ControllerConstants.MONSTER__ELEMENTAL_STRENGTH);
    mcb.setElementalWeakness(ControllerConstants.MONSTER__ELEMENTAL_WEAKNESS);
    cb.setMonsterConstants(mcb.build());

    cb.setMinutesPerGem(ControllerConstants.MINUTES_PER_GEM);
    cb.setPvpRequiredMinLvl(ControllerConstants.PVP__REQUIRED_MIN_LEVEL);
    cb.setGemsPerResource(ControllerConstants.GEMS_PER_RESOURCE);
    cb.setContinueBattleGemCostMultiplier(ControllerConstants.BATTLE__CONTINUE_GEM_COST_MULTIPLIER);

    //set more properties above
    //    BattleConstants battleConstants = BattleConstants.newBuilder()
    //        .setLocationBarMax(ControllerConstants.BATTLE_LOCATION_BAR_MAX)
    //        .setBattleWeightGivenToAttackStat(ControllerConstants.BATTLE_WEIGHT_GIVEN_TO_ATTACK_STAT)
    //        .setBattleWeightGivenToAttackEquipSum(ControllerConstants.BATTLE_WEIGHT_GIVEN_TO_ATTACK_EQUIP_SUM)
    //        .setBattleWeightGivenToDefenseStat(ControllerConstants.BATTLE_WEIGHT_GIVEN_TO_DEFENSE_STAT)
    //        .setBattleWeightGivenToDefenseEquipSum(ControllerConstants.BATTLE_WEIGHT_GIVEN_TO_DEFENSE_EQUIP_SUM)
    //        .setBattleWeightGivenToLevel(ControllerConstants.BATTLE_WEIGHT_GIVEN_TO_LEVEL)
    //        .setBattlePerfectPercentThreshold(ControllerConstants.BATTLE_PERFECT_PERCENT_THRESHOLD)
    //        .setBattleGreatPercentThreshold(ControllerConstants.BATTLE_GREAT_PERCENT_THRESHOLD)
    //        .setBattleGoodPercentThreshold(ControllerConstants.BATTLE_GOOD_PERCENT_THRESHOLD)
    //        .setBattlePerfectMultiplier(ControllerConstants.BATTLE_PERFECT_MULTIPLIER)
    //        .setBattleGreatMultiplier(ControllerConstants.BATTLE_GREAT_MULTIPLIER)
    //        .setBattleGoodMultiplier(ControllerConstants.BATTLE_GOOD_MULTIPLIER)
    //        .setBattleImbalancePercent(ControllerConstants.BATTLE_IMBALANCE_PERCENT)
    //        .setBattlePerfectLikelihood(ControllerConstants.BATTLE_PERFECT_LIKELIHOOD)
    //        .setBattleGreatLikelihood(ControllerConstants.BATTLE_GREAT_LIKELIHOOD)
    //        .setBattleGoodLikelihood(ControllerConstants.BATTLE_GOOD_LIKELIHOOD)
    //        .setBattleMissLikelihood(ControllerConstants.BATTLE_MISS_LIKELIHOOD)
    //        .setBattleHitAttackerPercentOfHealth(ControllerConstants.BATTLE__HIT_ATTACKER_PERCENT_OF_HEALTH)
    //        .setBattleHitDefenderPercentOfHealth(ControllerConstants.BATTLE__HIT_DEFENDER_PERCENT_OF_HEALTH)
    //        .setBattlePercentOfWeapon(ControllerConstants.BATTLE__PERCENT_OF_WEAPON)
    //        .setBattlePercentOfArmor(ControllerConstants.BATTLE__PERCENT_OF_ARMOR)
    //        .setBattlePercentOfAmulet(ControllerConstants.BATTLE__PERCENT_OF_AMULET)
    //        .setBattlePercentOfPlayerStats(ControllerConstants.BATTLE__PERCENT_OF_PLAYER_STATS)
    //        .setBattleAttackExpoMultiplier(ControllerConstants.BATTLE__ATTACK_EXPO_MULTIPLIER)
    //        .setBattlePercentOfEquipment(ControllerConstants.BATTLE__PERCENT_OF_EQUIPMENT)
    //        .setBattleIndividualEquipAttackCap(ControllerConstants.BATTLE__INDIVIDUAL_EQUIP_ATTACK_CAP)
    //        .setBattleEquipAndStatsWeight(ControllerConstants.BATTLE__EQUIP_AND_STATS_WEIGHT)
    //        .build();
    //
    //    cb = cb.setBattleConstants(battleConstants);

    //    LockBoxConstants lbc = LockBoxConstants.newBuilder()
    //        .setFreeChanceToPickLockBox(ControllerConstants.LOCK_BOXES__FREE_CHANCE_TO_PICK)
    //        .setGoldChanceToPickLockBox(ControllerConstants.LOCK_BOXES__GOLD_CHANCE_TO_PICK)
    //        .setNumMinutesToRepickLockBox(ControllerConstants.LOCK_BOXES__NUM_MINUTES_TO_REPICK)
    //        .setGoldCostToPickLockBox(ControllerConstants.LOCK_BOXES__GOLD_COST_TO_PICK)
    //        .setGoldCostToResetPickLockBox(ControllerConstants.LOCK_BOXES__GOLD_COST_TO_RESET_PICK)
    //        .setCashChanceToPickLockBox(ControllerConstants.LOCK_BOXES__SILVER_CHANCE_TO_PICK)
    //        .setCashCostToPickLockBox(ControllerConstants.LOCK_BOXES__SILVER_COST_TO_PICK)
    //        .setNumDaysToShowAfterEventEnded(ControllerConstants.LOCK_BOXES__NUM_DAYS_AFTER_END_DATE_TO_KEEP_SENDING_PROTOS)
    //        .build();
    //
    //    cb = cb.setLockBoxConstants(lbc);


    //    EnhancementConstants enc = EnhancementConstants.newBuilder()
    //        .setMaxEnhancementLevel(ControllerConstants.MAX_ENHANCEMENT_LEVEL)
    //        .setEnhanceLevelExponentBase(ControllerConstants.ENHANCEMENT__ENHANCE_LEVEL_EXPONENT_BASE)
    //        .setEnhancePercentPerLevel(ControllerConstants.ENHANCEMENT__PERCENTAGE_PER_LEVEL)
    //        .setEnhanceTimeConstantA(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_A)
    //        .setEnhanceTimeConstantB(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_B)
    //        .setEnhanceTimeConstantC(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_C)
    //        .setEnhanceTimeConstantD(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_D)
    //        .setEnhanceTimeConstantE(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_E)
    //        .setEnhanceTimeConstantF(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_F)
    //        .setEnhanceTimeConstantG(ControllerConstants.ENHANCEMENT__TIME_FORMULA_CONSTANT_G)
    //        .setEnhancePercentConstantA(ControllerConstants.ENHANCEMENT__PERCENT_FORMULA_CONSTANT_A)
    //        .setEnhancePercentConstantB(ControllerConstants.ENHANCEMENT__PERCENT_FORMULA_CONSTANT_B)
    //        .setDefaultSecondsToEnhance(ControllerConstants.ENHANCEMENT__DEFAULT_SECONDS_TO_ENHANCE)
    //        .setEnhancingCost(ControllerConstants.ENHANCEMENT__COST_CONSTANT)
    //        .build();
    //
    //    cb = cb.setEnhanceConstants(enc);


    //    LeaderboardEventConstants lec =LeaderboardEventConstants.newBuilder()
    //        .setWinsWeight(ControllerConstants.TOURNAMENT_EVENT__WINS_WEIGHT)
    //        .setLossesWeight(ControllerConstants.TOURNAMENT_EVENT__LOSSES_WEIGHT)
    //        .setFleesWeight(ControllerConstants.TOURNAMENT_EVENT__FLEES_WEIGHT)
    //        .setNumHoursToShowAfterEventEnd(ControllerConstants.TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END)
    //        .build();
    //    cb = cb.setLeaderboardConstants(lec);
    //    
    //    BoosterPackConstants bpc = BoosterPackConstants.newBuilder()
    //        .setPurchaseOptionOneNumBoosterItems(ControllerConstants.BOOSTER_PACK__PURCHASE_OPTION_ONE_NUM_BOOSTER_ITEMS)
    //        .setPurchaseOptionTwoNumBoosterItems(ControllerConstants.BOOSTER_PACK__PURCHASE_OPTION_TWO_NUM_BOOSTER_ITEMS)
    //        .setInfoImageName(ControllerConstants.BOOSTER_PACK__INFO_IMAGE_NAME)
    //        .setNumTimesToBuyStarterPack(ControllerConstants.BOOSTER_PACK__NUM_TIMES_TO_BUY_STARTER_PACK)
    //        .setNumDaysToBuyStarterPack(ControllerConstants.BOOSTER_PACK__NUM_DAYS_TO_BUY_STARTER_PACK)
    //        .build();
    //    cb = cb.setBoosterPackConstants(bpc);
    //
    //    List<Integer> questIdsGuaranteedWin = new ArrayList<Integer>();
    //    int[] questIdsForWin = ControllerConstants.STARTUP__QUEST_IDS_FOR_GUARANTEED_WIN; 
    //    for(int i = 0; i < questIdsForWin.length; i++) {
    //      questIdsGuaranteedWin.add(questIdsForWin[i]);
    //    }




    //    BossConstants.Builder bc = BossConstants.newBuilder();
    //    bc.setMaxHealthMultiplier(ControllerConstants.SOLO_BOSS__MAX_HEALTH_MULTIPLIER);
    //    cb.setBossConstants(bc.build());

    //    SpeedupConstants.Builder scb = SpeedupConstants.newBuilder();
    //    scb.setBuildLateSpeedupConstant(ControllerConstants.BUILD_LATE_SPEEDUP_CONSTANT);
    //    scb.setUpgradeLateSpeedupConstant(ControllerConstants.UPGRADE_LATE_SPEEDUP_CONSTANT);
    //    cb.setSpeedupConstants(scb.build());

    return cb.build();  
  }

  /*public static List<LockBoxEventProto> currentLockBoxEvents() {
    Map<Integer, LockBoxEvent> events = LockBoxEventRetrieveUtils.getLockBoxEventIdsToLockBoxEvents();
    long curTime = new Date().getTime();
    List<LockBoxEventProto> toReturn = new ArrayList<LockBoxEventProto>();

    for (LockBoxEvent event : events.values()) {
      // Send all events that are not yet over
      long delay = 1000 * 60 * 60 * 24 *
          ControllerConstants.LOCK_BOXES__NUM_DAYS_AFTER_END_DATE_TO_KEEP_SENDING_PROTOS;

      if ((event.getEndDate().getTime() + delay) > curTime) {
        toReturn.add(CreateInfoProtoUtils.createLockBoxEventProtoFromLockBoxEvent(event));
      }
    }
    return toReturn;
  }

  public static List<BossEventProto> currentBossEvents() {
    Map<Integer, BossEvent> events = BossEventRetrieveUtils.getIdsToBossEvents();
    long curTime = new Date().getTime();
    List<BossEventProto> toReturn = new ArrayList<BossEventProto>();

    for (BossEvent event : events.values()) {
      // Send all events that are not yet over
      if (event.getEndDate().getTime() > curTime) {
        toReturn.add(CreateInfoProtoUtils.createBossEventProtoFromBossEvent(event));
      }
    }
    return toReturn;
  }*/

  public static List<TournamentEventProto> currentTournamentEventProtos() {
    Map<Integer, TournamentEvent> idsToEvents = TournamentEventRetrieveUtils.getIdsToTournamentEvents(false);
    long curTime = (new Date()).getTime();
    List<Integer> activeEventIds = new ArrayList<Integer>();

    //return value
    List<TournamentEventProto> protos = new ArrayList<TournamentEventProto>();

    //get the ids of active leader board events
    for(TournamentEvent e : idsToEvents.values()) {
      if (e.getEndDate().getTime()+ControllerConstants.TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END*3600000L > curTime) {
        activeEventIds.add(e.getId());
      }
    }

    //get all the rewards for all the current leaderboard events
    Map<Integer, List<TournamentEventReward>> eventIdsToRewards = 
        TournamentEventRewardRetrieveUtils.getLeaderboardEventRewardsForIds(activeEventIds);

    //create the protos
    for(Integer i: activeEventIds) {
      TournamentEvent e = idsToEvents.get(i);
      List<TournamentEventReward> rList = eventIdsToRewards.get(e.getId()); //rewards for the active event

      protos.add(CreateInfoProtoUtils.createTournamentEventProtoFromTournamentEvent(e, rList));
    }
    return protos;
  }

  public static void reloadAllRareChangeStaticData() {
    log.info("Reloading rare change static data");
    BannedUserRetrieveUtils.reload();
    BoosterDisplayItemRetrieveUtils.reload();
    BoosterItemRetrieveUtils.reload();
    BoosterPackRetrieveUtils.reload();
    //    CityBossRetrieveUtils.reload();
    CityElementsRetrieveUtils.reload(); 
    CityRetrieveUtils.reload();
    //    ClanBossRetrieveUtils.reload();
    //    ClanBossRewardRetrieveUtils.reload();
    ClanRaidRetrieveUtils.reload();
    EventPersistentRetrieveUtils.reload();
    ExpansionCostRetrieveUtils.reload();
    GoldSaleRetrieveUtils.reload();
    LockBoxEventRetrieveUtils.reload();
    //TODO: FIGURE OUT BETTER WAY TO RELOAD NON STATIC CLASS DATA
//    getMonsterForPvpRetrieveUtils().reload();
    MonsterBattleDialogueRetrieveUtils.reload();
    MonsterLevelInfoRetrieveUtils.reload();
    MonsterRetrieveUtils.reload();
    ProfanityRetrieveUtils.reload();
    QuestMonsterItemRetrieveUtils.reload();
    QuestRetrieveUtils.reload();
    StartupStuffRetrieveUtils.reload();
    StaticUserLevelInfoRetrieveUtils.reload();
    StructureHospitalRetrieveUtils.reload();
    StructureLabRetrieveUtils.reload();
    StructureResidenceRetrieveUtils.reload();
    StructureResourceGeneratorRetrieveUtils.reload();
    StructureResourceStorageRetrieveUtils.reload();
    StructureRetrieveUtils.reload();
    StructureTownHallRetrieveUtils.reload();
    TaskRetrieveUtils.reload();
    TaskStageMonsterRetrieveUtils.reload();
    TaskStageRetrieveUtils.reload();
    TournamentEventRetrieveUtils.reload();
    TournamentEventRewardRetrieveUtils.reload();
  }


  //  //returns the clan towers that changed
  //  public static void sendClanTowerWarNotEnoughMembersNotification(
  //      Map<Integer, ClanTower> clanTowerIdsToClanTowers, List<Integer> towersAttacked,
  //      List<Integer> towersOwned, Clan aClan, TaskExecutor executor, 
  //      Collection<ConnectedPlayer> onlinePlayers, GameServer server) {
  //
  //    if(null != clanTowerIdsToClanTowers && !clanTowerIdsToClanTowers.isEmpty()) {
  //
  //      List<Notification> notificationsToSend = new ArrayList<Notification>();
  //      //make notifications for the towers the clan was attacking
  //      boolean attackerWon = false;
  //      generateClanTowerNotEnoughMembersNotification(aClan, towersAttacked, clanTowerIdsToClanTowers, 
  //          notificationsToSend, attackerWon, onlinePlayers, server);
  //
  //      //make notifications for the towers the clan owned
  //      attackerWon = true;
  //      generateClanTowerNotEnoughMembersNotification(aClan, towersOwned, clanTowerIdsToClanTowers,
  //          notificationsToSend, attackerWon, onlinePlayers, server);
  //
  //      for(Notification n: notificationsToSend) {
  //        writeGlobalNotification(n, server);
  //      }
  //      return;
  //    }
  //    log.info("no towers changed");
  //    return;
  //  }

  //  private static void generateClanTowerNotEnoughMembersNotification(Clan aClan, List<Integer> towerIds, 
  //      Map<Integer, ClanTower> clanTowerIdsToClanTowers, List<Notification> notificationsToSend,
  //      boolean isTowerOwner, Collection<ConnectedPlayer> onlinePlayers, GameServer server) {
  //
  //    //for each tower make a notification for it
  //    for(Integer towerId: towerIds) {
  //      ClanTower aTower = clanTowerIdsToClanTowers.get(towerId);
  //      String towerName = aTower.getTowerName();
  //      Notification clanTowerWarNotification = new Notification ();
  //      Clan losingClan;
  //      Clan winningClan;
  //      String losingClanName;
  //      String winningClanName;
  //
  //      losingClan = aClan;
  //      winningClan = ClanRetrieveUtils.getClanWithId(aTower.getClanOwnerId());
  //
  //      losingClanName = losingClan.getName();
  //      winningClanName = winningClan != null ? winningClan.getName() : null;
  //      clanTowerWarNotification.setAsClanTowerWarClanConceded(
  //          losingClanName, winningClanName, towerName);
  //      notificationsToSend.add(clanTowerWarNotification);
  //    }
  //  }

  public static void writeGlobalNotification(Notification n, GameServer server) {
    GeneralNotificationResponseProto.Builder notificationProto = 
        n.generateNotificationBuilder();

    GeneralNotificationResponseEvent aNotification = new GeneralNotificationResponseEvent(0);
    aNotification.setGeneralNotificationResponseProto(notificationProto.build());
    server.writeGlobalEvent(aNotification);
  }

  public static void writeClanApnsNotification(Notification n, GameServer server, int clanId) {
    GeneralNotificationResponseProto.Builder notificationProto =
        n.generateNotificationBuilder();

    GeneralNotificationResponseEvent aNotification = new GeneralNotificationResponseEvent(0);
    aNotification.setGeneralNotificationResponseProto(notificationProto.build());
    server.writeApnsClanEvent(aNotification, clanId);
  }

  public static void writeNotificationToUser(Notification aNote, GameServer server, int userId) {
    GeneralNotificationResponseProto.Builder notificationProto =
        aNote.generateNotificationBuilder();
    GeneralNotificationResponseEvent aNotification =
        new GeneralNotificationResponseEvent(userId);
    aNotification.setGeneralNotificationResponseProto(notificationProto.build());

    server.writeAPNSNotificationOrEvent(aNotification);
  }

  //Simple (inefficient) word by word censor. If a word appears in 
  //a blacklist then that word is replaced by a number of asterisks 
  //equal to the word's length, e.g. fuck => ****
  //Not sure whether to use String or StringBuilder, so going with latter.
  public static String censorUserInput(String userContent) {
    StringBuilder toReturn = new StringBuilder(userContent.length());
    Set<String> blackList = ProfanityRetrieveUtils.getAllProfanity();

    String[] words = userContent.split(" ");
    String space = " "; //split by space, need to add them back in
    String w = "";

    for(int i = 0; i < words.length; i++) {
      w = words[i];

      //if at the last word, don't add a space after "censoring" it
      if ((words.length - 1) == i) {
        space = "";
      }
      //get rid of all punctuation
      String wWithNoPunctuation = w.replaceAll("\\p{Punct}", "");

      //the profanity table only holds lower case one word profanities
      if(blackList.contains(wWithNoPunctuation.toLowerCase())) {
        toReturn.append(asteriskify(w) + space);
      } else {
        toReturn.append(w + space);
      }
    }

    return toReturn.toString();
  }

  //average length of word is 4 characters. So based on this, not using
  //StringBuilder
  public static String asteriskify(String wordToAskerify) {
    int len = wordToAskerify.length();
    String s = "";

    for(int i = 0; i < len; i++) {
      s += "*";
    }
    return s;
  }

  //currencyChange should represent how much user's currency increased or decreased and
  //this should be called after the user is updated
  public static void writeToUserCurrencyOneUser(int userId, Timestamp thyme,
      Map<String,Integer> changeMap, Map<String, Integer> previousCurrencyMap,
      Map<String, Integer> currentCurrencyMap, Map<String, String> changeReasonsMap,
      Map<String, String> detailsMap) {
    try {
      int amount = changeMap.size();

      //getting rid of changes that are 0
      Set<String> keys = new HashSet<String>(changeMap.keySet());
      for (String key : keys) {
        Integer change = changeMap.get(key);
        if (0 == change) {
          changeMap.remove(key);
          previousCurrencyMap.remove(key);
          currentCurrencyMap.remove(key);
          changeReasonsMap.remove(key);
          detailsMap.remove(key);
        }
      }

      List<Integer> userIds = Collections.nCopies(amount, userId);
      List<Timestamp> timestamps = Collections.nCopies(amount, thyme); 
      List<String> resourceTypes = new ArrayList<String>(changeMap.keySet());
      List<Integer> currencyChanges = getValsInOrder(resourceTypes, changeMap);
      List<Integer> previousCurrencies = getValsInOrder(resourceTypes, previousCurrencyMap);
      List<Integer> currentCurrencies = getValsInOrder(resourceTypes, currentCurrencyMap);
      List<String> reasonsForChanges = getValsInOrder(resourceTypes, changeReasonsMap);
      List<String> details = getValsInOrder(resourceTypes, detailsMap);

      if (currencyChanges.isEmpty() || previousCurrencies.isEmpty() ||
          currentCurrencies.isEmpty() || reasonsForChanges.isEmpty()) {
        return;
      }

      InsertUtils.get().insertIntoUserCurrencyHistoryMultipleRows(userIds, timestamps,
          resourceTypes, currencyChanges, previousCurrencies, currentCurrencies,
          reasonsForChanges, details);

    } catch(Exception e) {
      log.error("error updating user_curency_history; reasonsForChanges=" + changeReasonsMap, e);
    }
  }

  public static <T> List<T> getValsInOrder(List<String> keys, Map<String, T> keysToVals) {
    List<T> valsInOrder = new ArrayList<T>();
    for (String key : keys) {
      T val = keysToVals.get(key);
      valsInOrder.add(val);
    }
    return valsInOrder;
  }

  //  public static boolean isEquipAtMaxEnhancementLevel(MonsterForUser enhancingUserEquip) {
  //    int currentEnhancementLevel = enhancingUserEquip.getEnhancementPercentage();
  //    int maxEnhancementLevel = ControllerConstants.MAX_ENHANCEMENT_LEVEL 
  //        * ControllerConstants.ENHANCEMENT__PERCENTAGE_PER_LEVEL;
  //
  //    return currentEnhancementLevel >= maxEnhancementLevel;
  //  }

  public static int pointsGainedForClanTowerUserBattle(User winner, User loser) {
    int d = winner.getLevel()-loser.getLevel();
    int pts;
    if (d > 10) {
      pts = 1;
    } else if (d < -8) {
      pts = 100;
    } else {
      pts = (int)Math.round((-0.0997*Math.pow(d, 3)+1.4051*Math.pow(d, 2)-14.252*d+90.346)/10.);
    }
    return Math.min(100, Math.max(1, pts));
  }

  public static GoldSaleProto createFakeGoldSaleForNewPlayer(User user) {
    int id = 0;
    Date startDate = user.getCreateTime();
    Date endDate = new Date(startDate.getTime()+(long)(ControllerConstants.NUM_DAYS_FOR_NEW_USER_GOLD_SALE*24*60*60*1000));

    if (endDate.getTime() < new Date().getTime()) {
      return null;
    }

    String package1SaleIdentifier = IAPValues.PACKAGE1BSALE;
    String package2SaleIdentifier = IAPValues.PACKAGE2BSALE;
    String package3SaleIdentifier = IAPValues.PACKAGE3BSALE;
    String package4SaleIdentifier = null;
    String package5SaleIdentifier = null;
    String packageS1SaleIdentifier = IAPValues.PACKAGES1BSALE;
    String packageS2SaleIdentifier = IAPValues.PACKAGES2BSALE;
    String packageS3SaleIdentifier = IAPValues.PACKAGES3BSALE;
    String packageS4SaleIdentifier = null;
    String packageS5SaleIdentifier = null;

    String gemsShoppeImageName = ControllerConstants.GOLD_SHOPPE_IMAGE_NAME_NEW_USER_GOLD_SALE;
    String gemsBarImageName = ControllerConstants.GOLD_BAR_IMAGE_NAME_NEW_USER_GOLD_SALE;

    GoldSale sale = new GoldSale(id, startDate, endDate, gemsShoppeImageName, gemsBarImageName, package1SaleIdentifier, package2SaleIdentifier, package3SaleIdentifier, package4SaleIdentifier, package5SaleIdentifier,
        packageS1SaleIdentifier, packageS2SaleIdentifier, packageS3SaleIdentifier, packageS4SaleIdentifier, packageS5SaleIdentifier, true);

    return CreateInfoProtoUtils.createGoldSaleProtoFromGoldSale(sale);
  }

  public static int dateDifferenceInDays(Date start, Date end) {
    DateMidnight previous = (new DateTime(start)).toDateMidnight(); //
    DateMidnight current = (new DateTime(end)).toDateMidnight();
    int days = Days.daysBetween(previous, current).getDays();
    return days;
  }


  //  private static List<Integer> getRaritiesCollected(
  //      List<BoosterItem> itemsUserReceives, List<Integer> equipIds) {
  //    List<Integer> raritiesCollected = new ArrayList<Integer>();
  //    
  //    Map<Integer, Equipment> equipIdsToEquips = 
  //        EquipmentRetrieveUtils.getEquipmentIdsToEquipment();
  //    int rarityOne = 0;
  //    int rarityTwo = 0;
  //    int rarityThree = 0;
  //    for (BoosterItem bi : itemsUserReceives) {
  //      int equipId = bi.getEquipId();
  //      Equipment tempEquip = null;
  //      if (equipIdsToEquips.containsKey(equipId)) {
  //        equipIds.add(equipId); //returning what equipIds this was
  //        tempEquip = equipIdsToEquips.get(equipId);
  //      } else {
  //        log.error("No equiment exists for equipId=" + equipId
  //            + ". BoosterItem has invalid equipId, boosterItem=" + bi);
  //        continue;
  //      }
  //      Rarity equipRarity = tempEquip.getRarity();
  //      if (isRarityOne(equipRarity)) {
  //        rarityOne++;
  //      } else if (isRarityTwo(equipRarity)) {
  //        rarityTwo++;
  //      } else if (isRarityThree(equipRarity)) {
  //        rarityThree++;
  //      } else {
  //        log.error("unexpected_error: booster item has unknown equip rarity. " +
  //        		"booster item=" + bi + ".  Equip rarity=" + equipRarity);
  //      }
  //    }
  //    raritiesCollected.add(rarityOne);
  //    raritiesCollected.add(rarityTwo);
  //    raritiesCollected.add(rarityThree);
  //    return raritiesCollected;
  //  }
  //  
  //  private static boolean isRarityOne(Rarity equipRarity) {
  //    if (Rarity.COMMON == equipRarity || Rarity.RARE == equipRarity) {
  //      return true;
  //    } else {
  //      return false;
  //    }
  //  }
  //  
  //  private static boolean isRarityTwo(Rarity equipRarity) {
  //    if (Rarity.UNCOMMON == equipRarity || Rarity.SUPERRARE == equipRarity) {
  //      return true;
  //    } else {
  //      return false;
  //    }
  //  }
  //  
  //  private static boolean isRarityThree(Rarity equipRarity) {
  //    if (Rarity.RARE == equipRarity || Rarity.EPIC == equipRarity) {
  //      return true;
  //    } else {
  //      return false;
  //    }
  //  }
  //  
  //csi: comma separated ints
  public static List<Integer> unCsvStringIntoIntList(String csi) {
    List<Integer> ints = new ArrayList<Integer>();
    if (null != csi) {
      StringTokenizer st = new StringTokenizer(csi, ", ");
      while (st.hasMoreTokens()) {
        ints.add(Integer.parseInt(st.nextToken()));
      }
    }
    return ints;
  }

  public static int getRandomIntFromList(List<Integer> numList) {
    int upperBound = numList.size();
    Random rand = new Random();
    int randInt = rand.nextInt(upperBound);

    int returnValue = numList.get(randInt);
    return returnValue;
  }

  public static Map<Integer, Integer> getRandomValues(List<Integer> domain, int quantity) {
    Map<Integer, Integer> domainValuesToQuantities = new HashMap<Integer, Integer>();
    int upperBound = domain.size();
    Random rand = new Random();

    for (int i = 0; i < quantity; i++) {
      int quantitySoFar = 0;

      int randIndex = rand.nextInt(upperBound);
      int domainValue = domain.get(randIndex);
      //running sum
      if (domainValuesToQuantities.containsKey(domainValue)) {
        quantitySoFar += domainValuesToQuantities.get(domainValue);
      }
      quantitySoFar++;
      domainValuesToQuantities.put(domainValue, quantitySoFar);
    }
    return domainValuesToQuantities;
  }


  /*cut out from purchase booster pack controller*/
  //populates ids, quantitiesInStock; determines the remaining booster items the user can get
  //  private static int determineBoosterItemsLeft(Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems, 
  //      Map<Integer, Integer> boosterItemIdsToNumCollected, List<Integer> boosterItemIdsUserCanGet, 
  //      List<Integer> quantitiesInStock, User aUser, int boosterPackId) {
  //    //max number randon number can go
  //    int sumQuantitiesInStock = 0;
  //
  //    //determine how many BoosterItems are left that user can get
  //    for (int boosterItemId : allBoosterItemIdsToBoosterItems.keySet()) {
  //      BoosterItem potentialEquip = allBoosterItemIdsToBoosterItems.get(boosterItemId);
  //      int quantityLimit = potentialEquip.getQuantity();
  //      int quantityPurchasedPreviously = ControllerConstants.NOT_SET;
  //
  //      if (boosterItemIdsToNumCollected.containsKey(boosterItemId)) {
  //        quantityPurchasedPreviously = boosterItemIdsToNumCollected.get(boosterItemId);
  //      }
  //
  //      if(ControllerConstants.NOT_SET == quantityPurchasedPreviously) {
  //        //user has never bought this BoosterItem before
  //        boosterItemIdsUserCanGet.add(boosterItemId);
  //        quantitiesInStock.add(quantityLimit);
  //        sumQuantitiesInStock += quantityLimit;
  //      } else if (quantityPurchasedPreviously < quantityLimit) {
  //        //user bought before, but has not reached the limit
  //        int numLeftInStock = quantityLimit - quantityPurchasedPreviously;
  //        boosterItemIdsUserCanGet.add(boosterItemId);
  //        quantitiesInStock.add(numLeftInStock);
  //        sumQuantitiesInStock += numLeftInStock;
  //      } else if (quantityPurchasedPreviously == quantityLimit) {
  //        continue;
  //      } else {//will this ever be reached?
  //        log.error("somehow user has bought more than the allowed limit for a booster item for a booster pack. "
  //            + "quantityLimit: " + quantityLimit + ", quantityPurchasedPreviously: " + quantityPurchasedPreviously
  //            + ", userId: " + aUser.getId() + ", boosterItem: " + potentialEquip + ", boosterPackId: " + boosterPackId);
  //      }
  //    }
  //
  //    return sumQuantitiesInStock;
  //  }

  //  /*cut out from purchase booster pack controller*/
  //  //no arguments are modified
  //  private static List<BoosterItem> determineStarterBoosterItemsUserReceives(List<Integer> boosterItemIdsUserCanGet, 
  //      List<Integer> quantitiesInStock, int amountUserWantsToPurchase, int sumOfQuantitiesInStock,
  //      Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
  //    //return value
  //    List<BoosterItem> returnValue = new ArrayList<BoosterItem>();
  //    if (0 == amountUserWantsToPurchase) {
  //      return returnValue;
  //    } else if (3 != amountUserWantsToPurchase) {
  //      log.error("unexpected error: buying " + amountUserWantsToPurchase + " more equips instead of 3.");
  //      return returnValue; 
  //    } else if (0 != (sumOfQuantitiesInStock % 3)) {
  //      log.error("unexpected error: num remaining equips, " + sumOfQuantitiesInStock
  //          + ", for this chest is not a multiple of 3");
  //      return returnValue;
  //    }
  //    
  //    Map<Integer, Equipment> allEquips = EquipmentRetrieveUtils.getEquipmentIdsToEquipment();
  //    Set<EquipType> receivedEquipTypes = new HashSet<EquipType>();
  //    
  //    //loop through equips user can get; select one weapon, one armor, one amulet
  //    for (int boosterItemId : boosterItemIdsUserCanGet) {
  //      BoosterItem bi = allBoosterItemIdsToBoosterItems.get(boosterItemId);
  //      int equipId = bi.getEquipId();
  //      Equipment equip = allEquips.get(equipId);
  //      EquipType eType = equip.getType();
  //      
  //      if (receivedEquipTypes.contains(eType)) {
  //        //user already got this equip type
  //        continue;
  //      } else {
  //        //record user got a new equip type
  //        returnValue.add(bi);
  //        receivedEquipTypes.add(eType);
  //      }
  //    }
  //    
  //    if (3 != returnValue.size()) {
  //      log.error("unexpected error: user did not receive one type of each equip."
  //          + " User would have received (but now will not receive): " + MiscMethods.shallowListToString(returnValue) 
  //          + ". Chest either intialized improperly or code assigns equips incorrectly.");
  //      return new ArrayList<BoosterItem>();
  //    }
  //    return returnValue;
  //  }

  /*cut out from purchase booster pack controller*/
  //no arguments are modified
  private static List<BoosterItem> determineBoosterItemsUserReceives(List<Integer> boosterItemIdsUserCanGet, 
      List<Integer> quantitiesInStock, int amountUserWantsToPurchase, int sumOfQuantitiesInStock,
      Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
    //return value
    List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();

    Random rand = new Random();
    List<Integer> newBoosterItemIdsUserCanGet = new ArrayList<Integer>(boosterItemIdsUserCanGet);
    List<Integer> newQuantitiesInStock = new ArrayList<Integer>(quantitiesInStock);
    int newSumOfQuantities = sumOfQuantitiesInStock;

    //selects one of the ids at random without replacement
    for(int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
      int sumSoFar = 0;
      int randomNum = rand.nextInt(newSumOfQuantities) + 1; //range [1, newSumOfQuantities]

      for(int i = 0; i < newBoosterItemIdsUserCanGet.size(); i++) {
        int bItemId = newBoosterItemIdsUserCanGet.get(i);
        int quantity = newQuantitiesInStock.get(i);

        sumSoFar += quantity;

        if(randomNum <= sumSoFar) {
          //we have a winner! current boosterItemId is what the user gets
          BoosterItem selectedBoosterItem = allBoosterItemIdsToBoosterItems.get(bItemId);
          itemsUserReceives.add(selectedBoosterItem);

          //preparation for next BoosterItem to be selected
          if (1 == quantity) {
            newBoosterItemIdsUserCanGet.remove(i);
            newQuantitiesInStock.remove(i);
          } else if (1 < quantity){
            //booster item id has more than one quantity
            int decrementedQuantity = newQuantitiesInStock.remove(i) - 1;
            newQuantitiesInStock.add(i, decrementedQuantity);
          } else {
            //ignore those with quantity of 0
            continue;
          }

          newSumOfQuantities -= 1;
          break;
        }
      }
    }

    return itemsUserReceives;
  }
  //  /*cut out from purchase booster pack controller*/
  //  public static List<Long> insertNewUserEquips(int userId,
  //      List<BoosterItem> itemsUserReceives, Timestamp now, String reason) {
  //    int amount = itemsUserReceives.size();
  //    int forgeLevel = ControllerConstants.DEFAULT_USER_EQUIP_LEVEL;
  //    int enhancementLevel = ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT;
  //    List<Integer> equipIds = new ArrayList<Integer>();
  //    List<Integer> levels = new ArrayList<Integer>(Collections.nCopies(amount, forgeLevel));
  //    List<Integer> enhancement = new ArrayList<Integer>(Collections.nCopies(amount, enhancementLevel));
  //    
  //    for(BoosterItem bi : itemsUserReceives) {
  //      int equipId = bi.getEquipId();
  //      equipIds.add(equipId);
  //    }
  //    
  //    return InsertUtils.get().insertUserEquips(userId, equipIds, levels,
  //        enhancement, now, reason);
  //  }
  /*cut out from purchase booster pack controller*/
  //  public static boolean updateUserBoosterItems(List<BoosterItem> itemsUserReceives, 
  //      List<Boolean> collectedBeforeReset, Map<Integer, Integer> boosterItemIdsToNumCollected, 
  //      Map<Integer, Integer> newBoosterItemIdsToNumCollected, int userId, boolean resetOccurred) {
  //    
  //    Map<Integer, Integer> changedBoosterItemIdsToNumCollected = new HashMap<Integer, Integer>();
  //    int numCollectedBeforeReset = 0;
  //
  //    //for each booster item received record it in the map above, and record how many
  //    //booster items user has in aggregate
  //    for (int i = 0; i < itemsUserReceives.size(); i++) {
  //      boolean beforeReset = collectedBeforeReset.get(i);
  //      if (!beforeReset) {
  //        BoosterItem received = itemsUserReceives.get(i);
  //        int boosterItemId = received.getId();
  //        
  //        //default quantity user gets if user has no quantity of specific boosterItem
  //        int newQuantity = 1; 
  //        if(newBoosterItemIdsToNumCollected.containsKey(boosterItemId)) {
  //          newQuantity = newBoosterItemIdsToNumCollected.get(boosterItemId) + 1;
  //        }
  //        changedBoosterItemIdsToNumCollected.put(boosterItemId, newQuantity);
  //        newBoosterItemIdsToNumCollected.put(boosterItemId, newQuantity);
  //      } else {
  //        numCollectedBeforeReset++;
  //      }
  //    }
  //    
  //    //loop through newBoosterItemIdsToNumCollected and make sure the quantities
  //    //collected is itemsUserReceives.size() amount more than boosterItemIdsToNumCollected
  //    int changeInCollectedQuantity = 0;
  //    for (int id : changedBoosterItemIdsToNumCollected.keySet()) {
  //      int newAmount = newBoosterItemIdsToNumCollected.get(id);
  //      int oldAmount = 0;
  //      if (boosterItemIdsToNumCollected.containsKey(id)) {
  //        oldAmount = boosterItemIdsToNumCollected.get(id);
  //      }
  //      changeInCollectedQuantity += newAmount - oldAmount;
  //    }
  //    //for when user buys out a pack and then some
  //    changeInCollectedQuantity += numCollectedBeforeReset;
  //    if (itemsUserReceives.size() != changeInCollectedQuantity) {
  //      log.error("quantities of booster items do not match how many items user receives. "
  //          + "amount user receives that is recorded (user_booster_items table): " + changeInCollectedQuantity
  //          + ", amount user receives (unrecorded): " + itemsUserReceives.size());
  //      return false;
  //    }
  //
  //    recordBoosterItemsThatReset(changedBoosterItemIdsToNumCollected, newBoosterItemIdsToNumCollected, resetOccurred);
  //    
  //    return UpdateUtils.get().updateUserBoosterItemsForOneUser(userId, changedBoosterItemIdsToNumCollected);
  //  }
  /*cut out from purchase booster pack controller*/
  //if the user has bought out the whole deck, then for the booster items
  //the user did not get, record in the db that the user has 0 of them collected
  private static void recordBoosterItemsThatReset(Map<Integer, Integer> changedBoosterItemIdsToNumCollected,
      Map<Integer, Integer> newBoosterItemIdsToNumCollected, boolean refilled) {
    if (refilled) {
      for (int boosterItemId : newBoosterItemIdsToNumCollected.keySet()) {
        if (!changedBoosterItemIdsToNumCollected.containsKey(boosterItemId)) {
          int value = newBoosterItemIdsToNumCollected.get(boosterItemId);
          changedBoosterItemIdsToNumCollected.put(boosterItemId, value);
        }
      }
    }
  }

  /* public static Set<Long> getEquippedEquips(User aUser) {
    Set<Long> equippedUserEquipIds = new HashSet<Long>();
    equippedUserEquipIds.add(aUser.getAmuletEquippedUserEquipId());
    equippedUserEquipIds.add(aUser.getAmuletTwoEquippedUserEquipId());
    equippedUserEquipIds.add(aUser.getArmorEquippedUserEquipId());
    equippedUserEquipIds.add(aUser.getArmorTwoEquippedUserEquipId());
    equippedUserEquipIds.add(aUser.getWeaponEquippedUserEquipId());
    equippedUserEquipIds.add(aUser.getWeaponTwoEquippedUserEquipId());

    equippedUserEquipIds.remove(ControllerConstants.NOT_SET);

    return equippedUserEquipIds;
  }*/

  //arguments don't take into account the 1 forge slot the user has by default
  //  public static int costToBuyForgeSlot(int goalNumAdditionalForgeSlots,
  //      int currentNumAdditionalForgeSlots) {
  //    int goalNumForgeSlots = goalNumAdditionalForgeSlots + ControllerConstants.FORGE_DEFAULT_NUMBER_OF_FORGE_SLOTS;
  //    log.info("goalNumForgeSlots=" + goalNumForgeSlots);
  //    if (2 == goalNumForgeSlots) {
  //      return ControllerConstants.FORGE_COST_OF_PURCHASING_SLOT_TWO;
  //    } else if (3 == goalNumForgeSlots) {
  //      return ControllerConstants.FORGE_COST_OF_PURCHASING_SLOT_THREE;
  //    } else {
  //      log.error("unexpected error: goalNumForgeSlots=" + goalNumForgeSlots);
  //      return 500;
  //    }
  //  }

  public static int sumMapValues(Map<?, Integer> idToNum) {
    int sumSoFar = 0;

    for (int value : idToNum.values()) {
      sumSoFar += value;
    }
    return sumSoFar;
  }

  public static int sumListsInMap(Map<Integer, List<Integer>> aMap) {
    int sum = 0;
    for (int i : aMap.keySet()) {

      for (Integer value : aMap.get(i)) {
        sum += value;
      }
    }
    return sum;
  }

  public static void calculateEloChangeAfterBattle(User attacker, User defender, boolean attackerWon) {
    double probabilityOfAttackerWin = 1/(1+Math.pow(10, (defender.getElo() - attacker.getElo())/400));
    double probabilityOfDefenderWin = 1 - probabilityOfAttackerWin;
    int kFactor = 0;

    if(attacker.getElo() < 1900 || defender.getElo() < 2500) {
      kFactor = 32;
    }
    else if(attacker.getElo() < 2400 || defender.getElo() < 3500) {
      kFactor = 24;
    }
    else kFactor = 16;

    int newAttackerElo, newDefenderElo;
    //calculate change in elo
    if(attackerWon) {
      newAttackerElo = (int)(attacker.getElo() + kFactor*(1-probabilityOfAttackerWin));
      newDefenderElo = (int)(defender.getElo() + kFactor*(0-probabilityOfDefenderWin));
    }
    else {
      newAttackerElo = (int)(attacker.getElo() + kFactor*(0-probabilityOfAttackerWin));
      newDefenderElo = (int)(defender.getElo() + kFactor*(1-probabilityOfDefenderWin));
    }
    attacker.setElo(newAttackerElo);
    defender.setElo(newDefenderElo);

  }

  public static int speedupCostOverTime(int cost, long startTimeMillis, 
      long durationInSeconds, long curTimeMillis) {

    long timePassedSeconds = (curTimeMillis = startTimeMillis)/1000;
    long timeRemainingSeconds = durationInSeconds - timePassedSeconds;

    double percentRemaining = timeRemainingSeconds/(double)(durationInSeconds);

    int newCost = (int)Math.ceil(cost * percentRemaining);
    return newCost;
  }

  public static StaticDataProto getAllStaticData(int userId) {
  	StaticDataProto.Builder sdpb = StaticDataProto.newBuilder();
  	
  	setPlayerCityExpansions(sdpb);
  	setCities(sdpb);
  	setTasks(sdpb);
  	setMonsters(sdpb);
  	setUserLevelStuff(sdpb, userId);
  	setInProgressAndAvailableQuests(sdpb, userId);
  	setBoosterPackStuff(sdpb);
  	setStructures(sdpb);
  	setEvents(sdpb);
  	setMonsterDialogue(sdpb);
  	setClanRaid(sdpb);
  	
  	return sdpb.build();
  }
  private static void setPlayerCityExpansions(Builder sdpb) {
    //Player city expansions
    Map<Integer, ExpansionCost> expansionCosts =
        ExpansionCostRetrieveUtils.getAllExpansionNumsToCosts();
    for (ExpansionCost cec : expansionCosts.values()) {
      CityExpansionCostProto cecp = CreateInfoProtoUtils
          .createCityExpansionCostProtoFromCityExpansionCost(cec);
      sdpb.addExpansionCosts(cecp);
    }
  }
  private static void setCities(Builder sdpb) {
    //Cities
    Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
    for (Integer cityId : cities.keySet()) {
      City city = cities.get(cityId);
      sdpb.addAllCities(CreateInfoProtoUtils.createFullCityProtoFromCity(city));
    }
  }
  private static void setTasks(Builder sdpb) {
    //Tasks
    Map<Integer, Task> taskIdsToTasks = TaskRetrieveUtils.getTaskIdsToTasks();
    for (Task aTask : taskIdsToTasks.values()) {
      FullTaskProto ftp = CreateInfoProtoUtils.createFullTaskProtoFromTask(aTask);
      sdpb.addAllTasks(ftp);
    }
  }
  //TODO: FIGURE OUT MORE EFFICIENT WAY TO DO THIS IF NEEDED
  //ONE WAY WOULD BE TO STORE THE MAP OF MONSTER LEVEL INFO DIRECTLY IN THE MONSTER
  private static void setMonsters(Builder sdpb) {
    //Monsters
    Map<Integer, Monster> monsters = MonsterRetrieveUtils.getMonsterIdsToMonsters();
    for (Monster monster : monsters.values()) {

      //get the level info for this monster
      int monsterId = monster.getId();
      Map<Integer, MonsterLevelInfo> monsterLvlInfo = MonsterLevelInfoRetrieveUtils
          .getMonsterLevelInfoForMonsterId(monsterId);

      sdpb.addAllMonsters(CreateInfoProtoUtils.createMonsterProto(monster, monsterLvlInfo));
    }
  }
  private static void setUserLevelStuff(Builder sdpb, int userId) {
    //User level stuff
    Map<Integer, StaticUserLevelInfo> levelToStaticUserLevelInfo = 
        StaticUserLevelInfoRetrieveUtils.getAllStaticUserLevelInfo();
    for (int lvl : levelToStaticUserLevelInfo.keySet())  {
      StaticUserLevelInfo sli = levelToStaticUserLevelInfo.get(lvl);
      int exp = sli.getRequiredExp();

      StaticUserLevelInfoProto.Builder slipb = StaticUserLevelInfoProto.newBuilder();
      slipb.setLevel(lvl);
      slipb.setRequiredExperience(exp);
      sdpb.addSlip(slipb.build());
    }
  }
  private static void setInProgressAndAvailableQuests(Builder sdpb, int userId) {
    List<QuestForUser> inProgressAndRedeemedUserQuests = RetrieveUtils.questForUserRetrieveUtils()
        .getUserQuestsForUser(userId);


    List<Integer> inProgressQuestIds = new ArrayList<Integer>();
    List<Integer> redeemedQuestIds = new ArrayList<Integer>();

    Map<Integer, Quest> questIdToQuests = QuestRetrieveUtils.getQuestIdsToQuests();
    for (QuestForUser uq : inProgressAndRedeemedUserQuests) {

      if (uq.isRedeemed()) {
        redeemedQuestIds.add(uq.getQuestId());

      } else {
        //unredeemed quest section
        Quest quest = QuestRetrieveUtils.getQuestForQuestId(uq.getQuestId());
        FullQuestProto questProto = CreateInfoProtoUtils.createFullQuestProtoFromQuest(quest);

        inProgressQuestIds.add(uq.getQuestId());
        if (uq.isComplete()) { 
          //complete and unredeemed userQuest, so quest goes in unredeemedQuest
          sdpb.addUnredeemedQuests(questProto);
        } else {
          //incomplete and unredeemed userQuest, so quest goes in inProgressQuest
          sdpb.addInProgressQuests(questProto);
        }
      }
    }

    List<Integer> availableQuestIds = QuestUtils.getAvailableQuestsForUser(redeemedQuestIds,
        inProgressQuestIds);
    if (availableQuestIds == null) {
      return;
    }

    //from the available quest ids generate the available quest protos
    for (Integer questId : availableQuestIds) {
      FullQuestProto fqp = CreateInfoProtoUtils.createFullQuestProtoFromQuest(
          questIdToQuests.get(questId));
      sdpb.addAvailableQuests(fqp);
    }
  }
  private static void setBoosterPackStuff(Builder sdpb) {
    //Booster pack stuff
    Map<Integer, BoosterPack> idsToBoosterPacks = BoosterPackRetrieveUtils
        .getBoosterPackIdsToBoosterPacks();
    Map<Integer, Map<Integer, BoosterItem>> packIdToItemIdsToItems =
        BoosterItemRetrieveUtils.getBoosterItemIdsToBoosterItemsForBoosterPackIds();
    Map<Integer, Map<Integer, BoosterDisplayItem>> packIdToDisplayIdsToDisplayItems =
        BoosterDisplayItemRetrieveUtils.getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();

    for (Integer bpackId : idsToBoosterPacks.keySet()) {
      BoosterPack bp = idsToBoosterPacks.get(bpackId);

      //get the booster items associated with this booster pack
      Map<Integer, BoosterItem> itemIdsToItems = packIdToItemIdsToItems.get(bpackId);
      Collection<BoosterItem> items = null;
      if (null != itemIdsToItems) {
        items = itemIdsToItems.values();
      }

      //get the booster display items for this booster pack
      Map<Integer, BoosterDisplayItem> displayIdsToDisplayItems = 
          packIdToDisplayIdsToDisplayItems.get(bpackId);
      Collection<BoosterDisplayItem> displayItems = null;
      if (null != displayIdsToDisplayItems) {
        displayItems = displayIdsToDisplayItems.values();
      }

      BoosterPackProto bpProto = CreateInfoProtoUtils.createBoosterPackProto(
          bp, items, displayItems);
      sdpb.addBoosterPacks(bpProto);
    }
  }
  private static void setStructures(Builder sdpb) {
    //Structures
    Map<Integer, Structure> structs = StructureRetrieveUtils.getStructIdsToStructs();
    Map<Integer, StructureInfoProto> structProtos = new HashMap<Integer, StructureInfoProto>();
    for (Integer structId : structs.keySet()) {
      Structure struct = structs.get(structId);
      StructureInfoProto sip = CreateInfoProtoUtils.createStructureInfoProtoFromStructure(struct);
      structProtos.put(structId, sip);
    }

    setGenerators(sdpb, structs, structProtos);
    setStorages(sdpb, structs, structProtos);
    setHospitals(sdpb, structs, structProtos);
    setResidences(sdpb, structs, structProtos);
    setTownHalls(sdpb, structs, structProtos);
    setLabs(sdpb, structs, structProtos);
  }
  //resource generator
  private static void setGenerators(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureResourceGenerator> idsToGenerators = 
        StructureResourceGeneratorRetrieveUtils.getStructIdsToResourceGenerators();
    for (Integer structId : idsToGenerators.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureResourceGenerator srg = idsToGenerators.get(structId);

      ResourceGeneratorProto rgp = CreateInfoProtoUtils.createResourceGeneratorProto(s, sip, srg);
      sdpb.addAllGenerators(rgp);
    }
  }
  //resource storage
  private static void setStorages(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureResourceStorage> idsToStorages = 
        StructureResourceStorageRetrieveUtils.getStructIdsToResourceStorages();
    for (Integer structId : idsToStorages.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureResourceStorage srg = idsToStorages.get(structId);

      ResourceStorageProto rgp = CreateInfoProtoUtils.createResourceStorageProto(s, sip, srg);
      sdpb.addAllStorages(rgp);
    }
  }
  //hospitals
  private static void setHospitals(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureHospital> idsToHospitals = 
        StructureHospitalRetrieveUtils.getStructIdsToHospitals();
    for (Integer structId : idsToHospitals.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureHospital srg = idsToHospitals.get(structId);

      HospitalProto rgp = CreateInfoProtoUtils.createHospitalProto(s, sip, srg);
      sdpb.addAllHospitals(rgp);
    }
  }
  //residences
  private static void setResidences(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureResidence> idsToResidences = 
        StructureResidenceRetrieveUtils.getStructIdsToResidences();
    for (Integer structId : idsToResidences.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureResidence srg = idsToResidences.get(structId);

      ResidenceProto rgp = CreateInfoProtoUtils.createResidenceProto(s, sip, srg);
      sdpb.addAllResidences(rgp);
    }
  }
  //town hall
  private static void setTownHalls(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureTownHall> idsToTownHalls = 
        StructureTownHallRetrieveUtils.getStructIdsToTownHalls();
    for (Integer structId : idsToTownHalls.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureTownHall srg = idsToTownHalls.get(structId);

      TownHallProto rgp = CreateInfoProtoUtils.createTownHallProto(s, sip, srg);
      sdpb.addAllTownHalls(rgp);
    }
  }
  //lab
  private static void setLabs(Builder sdpb, Map<Integer, Structure> structs,
      Map<Integer, StructureInfoProto> structProtos) {
    Map<Integer, StructureLab> idsToLabs = StructureLabRetrieveUtils
        .getStructIdsToLabs();
    for (Integer structId : idsToLabs.keySet()) {
      Structure s = structs.get(structId);
      StructureInfoProto sip = structProtos.get(structId);
      StructureLab srg = idsToLabs.get(structId);

      LabProto rgp = CreateInfoProtoUtils.createLabProto(s, sip, srg);
      sdpb.addAllLabs(rgp);
    }		
  }

  private static void setEvents(Builder sdpb) {
    Map<Integer, EventPersistent> idsToEvents = EventPersistentRetrieveUtils
        .getAllEventIdsToEvents();
    for (Integer eventId: idsToEvents.keySet()) {
      EventPersistent event  = idsToEvents.get(eventId);
      PersistentEventProto eventProto = CreateInfoProtoUtils
          .createPersistentEventProtoFromEvent(event);
      sdpb.addEvents(eventProto);
    }
  }
  
  private static void setMonsterDialogue(Builder sdpb) {
  	Map<Integer, List<MonsterBattleDialogue>> monsterIdToDialogue =
  			MonsterBattleDialogueRetrieveUtils.getMonsterIdToBattleDialogue();
  	
  	List<MonsterBattleDialogueProto> dialogueList = new ArrayList<MonsterBattleDialogueProto>();
  	for (List<MonsterBattleDialogue> dialogue : monsterIdToDialogue.values()) {
  		
  		for (MonsterBattleDialogue mbd : dialogue) {
  			MonsterBattleDialogueProto dialogueProto = CreateInfoProtoUtils
  					.createMonsterBattleDialogueProto(mbd);
  			dialogueList.add(dialogueProto);
  		}
  	}
  	
  	sdpb.addAllMbds(dialogueList);
  }

  private static void setClanRaid(Builder sdpb) {
  	Map<Integer, ClanRaid> idsToClanRaid = new HashMap<Integer, ClanRaid>();
  	
  	List<ClanRaidProto> raidList = new ArrayList<ClanRaidProto>();
  	for(ClanRaid cr : idsToClanRaid.values()) {
  		ClanRaidProto crProto = CreateInfoProtoUtils.createClanRaidProto(cr);
  		raidList.add(crProto);
  	}
  	
  	sdpb.addAllRaids(raidList);
  }
}
