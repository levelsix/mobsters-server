package com.lvl6.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.lvl6.info.BoosterItem;
import com.lvl6.info.City;
import com.lvl6.info.Dialogue;
import com.lvl6.info.GoldSale;
import com.lvl6.info.TournamentEvent;
import com.lvl6.info.TournamentEventReward;
import com.lvl6.info.User;
import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.Globals;
import com.lvl6.properties.IAPValues;
import com.lvl6.properties.MDCKeys;
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.ClanConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.DownloadableNibConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.UserMonsterConstants;
import com.lvl6.proto.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.InAppPurchaseProto.InAppPurchasePackageProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.proto.TournamentStuffProto.TournamentEventProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.BannedUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ExpansionCostRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GoldSaleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.LockBoxEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ProfanityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StaticLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TournamentEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TournamentEventRewardRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

public class MiscMethods {


  private static final Logger log = LoggerFactory.getLogger(MiscMethods.class);
  public static final String cash = "cash";
  public static final String gems = "gems";
  public static final String boosterPackId = "boosterPackId";

//  public static int calculateMinutesToFinishForgeAttempt(Equipment equipment, int goalLevel) {
//    return (int)
//        (equipment.getMinutesToAttemptForgeBase()*Math.pow(ControllerConstants.FORGE_TIME_BASE_FOR_EXPONENTIAL_MULTIPLIER, goalLevel));
//  }

//  public static float calculateChanceOfSuccessForForge(Equipment equipment, int goalLevel) {
//    return  (1-equipment.getChanceOfForgeFailureBase()) - 
//        ((1-equipment.getChanceOfForgeFailureBase()) / (ControllerConstants.FORGE_MAX_EQUIP_LEVEL - 1)) * 
//        (goalLevel-2);
//  }

//  public static int calculateDiamondCostToSpeedupForgeWaittime(Equipment equipment, int goalLevel) {
//    return (int) Math.max(1, Math.ceil(ControllerConstants.FORGE_SPEEDUP_CONSTANT_A * 
//        Math.log(calculateMinutesToFinishForgeAttempt(equipment, goalLevel)) + 
//        ControllerConstants.FORGE_SPEEDUP_CONSTANT_B));
//  }

 /* public static UserEquip chooseUserEquipWithEquipIdPreferrablyNonEquippedIgnoreLevel(User user, List<UserEquip> userEquipsForEquipId) {
    if (user == null || userEquipsForEquipId == null || userEquipsForEquipId.size() <= 0) {
      return null;
    }
    if (userEquipsForEquipId.size() == 1) {
      return userEquipsForEquipId.get(0);
    }
    for (UserEquip ue : userEquipsForEquipId) {
      if (ue != null) {
        if (ue.getId() >= 1) {
          if (ue.getId() == user.getWeaponEquippedUserEquipId() || ue.getId() == user.getArmorEquippedUserEquipId()
              || ue.getId() == user.getAmuletEquippedUserEquipId()) {
            continue;
          } else {
            return ue;
          }
        }
      }
    }
    return null;
  }*/

  public static Dialogue createDialogue(String dialogueBlob) {
    if (dialogueBlob != null && dialogueBlob.length() > 0) { 
      StringTokenizer st = new StringTokenizer(dialogueBlob, "~");

      List<DialogueSpeaker> speakers = new ArrayList<DialogueSpeaker>();
      List<String> speakerTexts = new ArrayList<String>();

      try {
        while (st.hasMoreTokens()) {
          DialogueSpeaker speaker = DialogueSpeaker.valueOf(Integer.parseInt(st.nextToken()));
          String speakerText = st.nextToken();
          if (speakerText != null) {
            speakers.add(speaker);
            speakerTexts.add(speakerText);
          }
        }
      } catch (Exception e) {
        log.error("problem with creating dialogue object for this dialogueblob: {}", dialogueBlob, e);
      }
      return new Dialogue(speakers, speakerTexts);
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

  /*
   * doesn't check if the user has the equip or not
   */
  /*public static boolean checkIfEquipIsEquippableOnUser(Equipment equip, User user) {
    if (equip == null || user == null) return false;
    
    //figure out how to implement this
//    EquipClassType userClass = MiscMethods.getClassTypeFromUserType(user.getType());
//    if (user.getLevel() >= equip.getMinLevel() && 
//        (userClass == equip.getClassType() || equip.getClassType() == EquipClassType.ALL_AMULET)) {
//      return true;
//    }
    if (user.getLevel() >= equip.getMinLevel()) {
    	return true;
    }
    return false;
  }*/

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

//  public static int calculateCoinsGainedFromTutorialTask(Task firstTaskToComplete) {
//    return ((firstTaskToComplete.getMinCoinsGained() + firstTaskToComplete.getMaxCoinsGained())/2)
//        * firstTaskToComplete.getNumForCompletion();
//  }

  /*public static boolean unequipUserEquipIfEquipped(User user, UserEquip userEquip) {
    long userEquipId = userEquip.getId();
    boolean isWeaponOne = user.getWeaponEquippedUserEquipId() == userEquipId;
    boolean isArmorOne = user.getArmorEquippedUserEquipId() == userEquipId; 
    boolean isAmuletOne = user.getAmuletEquippedUserEquipId() == userEquipId;
    //for players who have prestige
    boolean isWeaponTwo = user.getWeaponTwoEquippedUserEquipId() == userEquipId; 
    boolean isArmorTwo = user.getArmorTwoEquippedUserEquipId() == userEquipId;
    boolean isAmuletTwo = user.getAmuletTwoEquippedUserEquipId() == userEquipId;
    if ( isWeaponOne || isWeaponTwo || isArmorOne || isArmorTwo || isAmuletOne || isAmuletTwo) {
      return user.updateUnequip(isWeaponOne, isArmorOne, isAmuletOne, isWeaponTwo, isArmorTwo,
          isAmuletTwo);
    } 
    return true;
  }*/

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

//  public static int calculateMinutesToBuildOrUpgradeForUserStruct(int minutesToUpgradeBase, int userStructLevel) {
//    if(userStructLevel==0) {
//    	return minutesToUpgradeBase;
//    }
//  	return Math.max(1, (int)(minutesToUpgradeBase * (userStructLevel+1) * ControllerConstants.MINUTES_TO_UPGRADE_FOR_NORM_STRUCT_MULTIPLIER));
//  }
//
//  public static int calculateIncomeGainedFromUserStruct(int structIncomeBase, int userStructLevel) {
//    return Math.max(1, (int)(userStructLevel * structIncomeBase * ControllerConstants.INCOME_FROM_NORM_STRUCT_MULTIPLIER));
//  }

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
      e.printStackTrace();
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
        iapb.setIsGold(true);
      } else {
        int coinAmt = IAPValues.getCoinsForPackageName(id);
        iapb.setCurrencyAmount(coinAmt);
        iapb.setIsGold(false);
      }
      cb.addInAppPurchasePackages(iapb.build());
    }
    
    cb.setMaxLevelForUser(ControllerConstants.LEVEL_UP__MAX_LEVEL_FOR_USER);
    cb.setMaxNumOfSingleStruct(ControllerConstants.PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE);
    
    //norm struct constants go here
    
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
    umcb.setMonsterInventoryIncrementAmount(ControllerConstants.MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT);
    umcb.setGemPricePerSlot(ControllerConstants.MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT);
    umcb.setNumFriendsToRecruitToIncreaseInventory(ControllerConstants.MONSTER_INVENTORY_SLOTS__NUM_FRIENDS_TO_RECRUIT_TO_INCREASE_INVENTORY);
    cb.setUserMonsterConstants(umcb.build());
    
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
//        .setSilverChanceToPickLockBox(ControllerConstants.LOCK_BOXES__SILVER_CHANCE_TO_PICK)
//        .setSilverCostToPickLockBox(ControllerConstants.LOCK_BOXES__SILVER_COST_TO_PICK)
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
    StartupStuffRetrieveUtils.reload();
    BannedUserRetrieveUtils.reload();
    BoosterItemRetrieveUtils.reload();
    BoosterPackRetrieveUtils.reload();
    CityElementsRetrieveUtils.reload(); 
    CityRetrieveUtils.reload();
//    ClanBossRetrieveUtils.reload();
//    ClanBossRewardRetrieveUtils.reload();
    ExpansionCostRetrieveUtils.reload();
    GoldSaleRetrieveUtils.reload();
    LockBoxEventRetrieveUtils.reload();
    MonsterRetrieveUtils.reload();
    ProfanityRetrieveUtils.reload();
    QuestRetrieveUtils.reload();
    StaticLevelInfoRetrieveUtils.reload();
    StructureRetrieveUtils.reload();
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

  public static void writeToUserCurrencyOneUserGoldAndSilver(
      User aUser, Timestamp date, Map<String,Integer> goldSilverChange, 
      Map<String, Integer> previousGoldSilver, Map<String, String> reasons) {
    //try, catch is here just in case this blows up, not really necessary;
    try {
      List<Integer> userIds = new ArrayList<Integer>();
      List<Timestamp> dates = new ArrayList<Timestamp>();
      List<Integer> areSilver = new ArrayList<Integer>();
      List<Integer> changesToCurrencies = new ArrayList<Integer>();
      List<Integer> previousCurrencies = new ArrayList<Integer>();
      List<Integer> currentCurrencies = new ArrayList<Integer>();
      List<String> reasonsForChanges = new ArrayList<String>();

      int userId = aUser.getId();
      int goldChange = goldSilverChange.get(gems);
      int silverChange = goldSilverChange.get(cash);
      int previousGold = 0;
      int previousSilver = 0;
      int currentGold = aUser.getGems();
      //recording total silver user has
      int currentSilver = aUser.getCash();

      //record gold change first
      if (0 != goldChange) {
        userIds.add(userId);
        dates.add(date);
        areSilver.add(0); //gold
        changesToCurrencies.add(goldChange);
        if(null == previousGoldSilver || previousGoldSilver.isEmpty()) {
          //difference instead of sum because of example:
          //(previous gold) u.gold = 10; 
          //change = -5 
          //current gold = 10 - 5 = 5
          //previous gold = currenty gold - change
          //previous_gold = 5 - -5 = 10
          previousGold = currentGold - goldChange;
        } else {
          previousGold = previousGoldSilver.get(gems);
        }

        previousCurrencies.add(previousGold);
        currentCurrencies.add(currentGold);
        reasonsForChanges.add(reasons.get(gems));
      }

      //record silver change next
      if (0 != silverChange) {
        userIds.add(userId);
        dates.add(date);
        areSilver.add(1); //silver
        changesToCurrencies.add(silverChange);
        if(null == previousGoldSilver || previousGoldSilver.isEmpty()) {
          previousSilver = currentSilver - silverChange;
        } else {
          previousSilver = previousGoldSilver.get(cash);
        }

        previousCurrencies.add(previousSilver);
        currentCurrencies.add(currentSilver);
        reasonsForChanges.add(reasons.get(cash));
      }

      //using multiple rows because could be 2 entries: one for silver, other for gold
      InsertUtils.get().insertIntoUserCurrencyHistoryMultipleRows(userIds, dates, areSilver,
          changesToCurrencies, previousCurrencies, currentCurrencies, reasonsForChanges);
    } catch(Exception e) {
      log.error("Maybe table's not there or duplicate keys? ", e);
    }
  }

  public static void writeToUserCurrencyOneUserGoldOrSilver(
      User aUser, Timestamp date, Map<String,Integer> goldSilverChange, 
      Map<String, Integer> previousGoldSilver, Map<String, String> reasons) {
    try {
      //determine what changed, gold or silver
      Set<String> keySet = goldSilverChange.keySet();
      Object[] keyArray = keySet.toArray();
      String key = (String) keyArray[0];

      //arguments to insertIntoUserCurrency
      int userId = aUser.getId();
      int isSilver = 0;
      int currencyChange = goldSilverChange.get(key);
      int previousCurrency = 0;
      int currentCurrency = 0;
      String reasonForChange = reasons.get(key);

      if (0 == currencyChange) {
        return;//don't write a non change to history table to avoid bloat
      }

      if (key.equals(gems)) {
        currentCurrency = aUser.getGems();
      } else if(key.equals(cash)) {
        //record total silver
        currentCurrency = aUser.getCash();
        isSilver = 1;
      } else {
        log.error("invalid key for map representing currency change. key=" + key);
        return;
      }

      if(null == previousGoldSilver || previousGoldSilver.isEmpty()) {
        previousCurrency = currentCurrency - currencyChange;
      } else {
        previousCurrency = previousGoldSilver.get(key);
      }

      InsertUtils.get().insertIntoUserCurrencyHistory(
          userId, date, isSilver, currencyChange, previousCurrency, currentCurrency, reasonForChange);
    } catch(Exception e) {
      log.error("null pointer exception?", e);
    }
  }

  //goldSilverChange should represent how much user's silver and, or gold increased or decreased and
  //this should be called after the user is updated
  //only previousGoldSilver can be null.
  public static void writeToUserCurrencyOneUserGoldAndOrSilver(
      User aUser, Timestamp date, Map<String,Integer> goldSilverChange, 
      Map<String, Integer> previousGoldSilver, Map<String, String> reasonsForChanges) {
    try {
      int amount = goldSilverChange.size();
      if(2 == amount) {
        writeToUserCurrencyOneUserGoldAndSilver(aUser, date, goldSilverChange, 
            previousGoldSilver, reasonsForChanges);
      } else if(1 == amount) {
        writeToUserCurrencyOneUserGoldOrSilver(aUser, date, goldSilverChange,
            previousGoldSilver, reasonsForChanges);
      }
    } catch(Exception e) {
      log.error("error updating user_curency_history; reasonsForChanges=" + shallowMapToString(reasonsForChanges), e);
    }
  }

  public static String shallowListToString(List<?> aList) {
    StringBuilder returnValue = new StringBuilder();
    for(Object o : aList) {
      returnValue.append(" ");
      returnValue.append(o.toString()); 
    }
    return returnValue.toString();
  }

  public static String shallowMapToString(Map<?, ?> aMap) {
    StringBuilder returnValue = new StringBuilder();
    if (null != aMap && !aMap.isEmpty()) {
      returnValue.append("[");
      for(Object key : aMap.keySet()) {
        returnValue.append(" ");
        returnValue.append(key);
        returnValue.append("=");
        returnValue.append(aMap.get(key).toString());
      }
      returnValue.append("]");
    }
    return returnValue.toString();
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

    String goldShoppeImageName = ControllerConstants.GOLD_SHOPPE_IMAGE_NAME_NEW_USER_GOLD_SALE;
    String goldBarImageName = ControllerConstants.GOLD_BAR_IMAGE_NAME_NEW_USER_GOLD_SALE;

    GoldSale sale = new GoldSale(id, startDate, endDate, goldShoppeImageName, goldBarImageName, package1SaleIdentifier, package2SaleIdentifier, package3SaleIdentifier, package4SaleIdentifier, package5SaleIdentifier,
        packageS1SaleIdentifier, packageS2SaleIdentifier, packageS3SaleIdentifier, packageS4SaleIdentifier, packageS5SaleIdentifier, true);

    return CreateInfoProtoUtils.createGoldSaleProtoFromGoldSale(sale);
  }
  
  public static int dateDifferenceInDays(Date start, Date end) {
    DateMidnight previous = (new DateTime(start)).toDateMidnight(); //
    DateMidnight current = (new DateTime(end)).toDateMidnight();
    int days = Days.daysBetween(previous, current).getDays();
    return days;
  }
  
//  public static void writeToUserBoosterPackHistoryOneUser(int userId, int packId,
//      int numBought, Timestamp nowTimestamp, List<BoosterItem> itemsUserReceives,
//      boolean excludeFromLimitCheck, List<Long> userEquipIds) {
//    List<Integer> equipIds = new ArrayList<Integer>();
//    List<Integer> raritiesCollected = getRaritiesCollected(itemsUserReceives, equipIds);
//    int rarityOne = raritiesCollected.get(0);
//    int rarityTwo = raritiesCollected.get(1);
//    int rarityThree = raritiesCollected.get(2);
//    
//    InsertUtils.get().insertIntoUserBoosterPackHistory(userId,
//        packId, numBought, nowTimestamp, rarityOne, rarityTwo,
//        rarityThree, excludeFromLimitCheck, equipIds, userEquipIds);
//  }
  
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
  
//  /*cut out from purchase booster pack controller*/
//  //Returns all the booster items the user purchased and whether or not the use reset the chesst.
//  //If the user buys out deck start over from a fresh deck 
//  //(boosterItemIdsToNumCollected is changed to reflect none have been collected).
//  //Also, keep track of which items were purchased before and/or after the reset (via collectedBeforeReset)
//  public static boolean getAllBoosterItemsForUser(Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems, 
//      Map<Integer, Integer> boosterItemIdsToNumCollected, int numBoosterItemsUserWants, User aUser, 
//      BoosterPack aPack, List<BoosterItem> returnValue, List<Boolean> collectedBeforeReset) {
//    boolean resetOccurred = false;
//    int boosterPackId = aPack.getId();
//    
//    //the possible items user can get
//    List<Integer> boosterItemIdsUserCanGet = new ArrayList<Integer>();
//    List<Integer> quantitiesInStock = new ArrayList<Integer>();
//    
//    //populate boosterItemIdsUserCanGet, and quantitiesInStock
//    int sumQuantitiesInStock = determineBoosterItemsLeft(allBoosterItemIdsToBoosterItems, 
//        boosterItemIdsToNumCollected, boosterItemIdsUserCanGet, quantitiesInStock, aUser, boosterPackId);
//    
//    //just in case user is allowed to purchase a lot more than what is available in a chest
//    //should take care of the case where user buys out the exact amount remaining in the chest
//    while (numBoosterItemsUserWants >= sumQuantitiesInStock) {
//      resetOccurred = true;
//      //give all the remaining booster items to the user, 
//      for (int i = 0; i < boosterItemIdsUserCanGet.size(); i++) {
//        int bItemId = boosterItemIdsUserCanGet.get(i);
//        BoosterItem bi = allBoosterItemIdsToBoosterItems.get(bItemId);
//        int quantityInStock = quantitiesInStock.get(i);
//        for (int quant = 0; quant < quantityInStock; quant++) {
//          returnValue.add(bi);
//          collectedBeforeReset.add(true);
//        }
//      }
//      //decrement number user still needs to receive, and then reset deck
//      numBoosterItemsUserWants -= sumQuantitiesInStock;
//      
//      //start from a clean slate as if it is the first time user is purchasing
//      boosterItemIdsUserCanGet.clear();
//      boosterItemIdsToNumCollected.clear();
//      quantitiesInStock.clear();
//      sumQuantitiesInStock = 0;
//      for (int boosterItemId : allBoosterItemIdsToBoosterItems.keySet()) {
//        BoosterItem boosterItemUserCanGet = allBoosterItemIdsToBoosterItems.get(boosterItemId);
//        boosterItemIdsUserCanGet.add(boosterItemId);
//        boosterItemIdsToNumCollected.put(boosterItemId, 0);
//        int quantityInStock = boosterItemUserCanGet.getQuantity();
//        quantitiesInStock.add(quantityInStock);
//        sumQuantitiesInStock += quantityInStock;
//      }
//    }
//
//    //set the booster item(s) the user will receieve  
//    List<BoosterItem> itemUserReceives = new ArrayList<BoosterItem>();
//    if (aPack.isStarterPack()) {
//      itemUserReceives = determineStarterBoosterItemsUserReceives(boosterItemIdsUserCanGet,
//          quantitiesInStock, numBoosterItemsUserWants, sumQuantitiesInStock, allBoosterItemIdsToBoosterItems);
//    } else {
//      itemUserReceives = determineBoosterItemsUserReceives(boosterItemIdsUserCanGet, 
//          quantitiesInStock, numBoosterItemsUserWants, sumQuantitiesInStock, allBoosterItemIdsToBoosterItems);
//    }
//    returnValue.addAll(itemUserReceives);
//    collectedBeforeReset.addAll(Collections.nCopies(itemUserReceives.size(), false));
//    return resetOccurred;
//  }

  /*cut out from purchase booster pack controller*/
  //populates ids, quantitiesInStock; determines the remaining booster items the user can get
  private static int determineBoosterItemsLeft(Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems, 
      Map<Integer, Integer> boosterItemIdsToNumCollected, List<Integer> boosterItemIdsUserCanGet, 
      List<Integer> quantitiesInStock, User aUser, int boosterPackId) {
    //max number randon number can go
    int sumQuantitiesInStock = 0;

    //determine how many BoosterItems are left that user can get
    for (int boosterItemId : allBoosterItemIdsToBoosterItems.keySet()) {
      BoosterItem potentialEquip = allBoosterItemIdsToBoosterItems.get(boosterItemId);
      int quantityLimit = potentialEquip.getQuantity();
      int quantityPurchasedPreviously = ControllerConstants.NOT_SET;

      if (boosterItemIdsToNumCollected.containsKey(boosterItemId)) {
        quantityPurchasedPreviously = boosterItemIdsToNumCollected.get(boosterItemId);
      }

      if(ControllerConstants.NOT_SET == quantityPurchasedPreviously) {
        //user has never bought this BoosterItem before
        boosterItemIdsUserCanGet.add(boosterItemId);
        quantitiesInStock.add(quantityLimit);
        sumQuantitiesInStock += quantityLimit;
      } else if (quantityPurchasedPreviously < quantityLimit) {
        //user bought before, but has not reached the limit
        int numLeftInStock = quantityLimit - quantityPurchasedPreviously;
        boosterItemIdsUserCanGet.add(boosterItemId);
        quantitiesInStock.add(numLeftInStock);
        sumQuantitiesInStock += numLeftInStock;
      } else if (quantityPurchasedPreviously == quantityLimit) {
        continue;
      } else {//will this ever be reached?
        log.error("somehow user has bought more than the allowed limit for a booster item for a booster pack. "
            + "quantityLimit: " + quantityLimit + ", quantityPurchasedPreviously: " + quantityPurchasedPreviously
            + ", userId: " + aUser.getId() + ", boosterItem: " + potentialEquip + ", boosterPackId: " + boosterPackId);
      }
    }

    return sumQuantitiesInStock;
  }
  
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
  
}
