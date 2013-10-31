package com.lvl6.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.City;
import com.lvl6.info.CityElement;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Dialogue;
import com.lvl6.info.ExpansionCost;
import com.lvl6.info.GoldSale;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.Referral;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.Task;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.TournamentEvent;
import com.lvl6.info.TournamentEventReward;
import com.lvl6.info.User;
import com.lvl6.info.UserCityExpansionData;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.BattleProto.MinimumUserProtoWithBattleHistory;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.proto.BoosterPackStuffProto.RareBoosterPurchaseProto;
import com.lvl6.proto.ChatProto.ColorProto;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.CityProto.CityElementProto;
import com.lvl6.proto.CityProto.CityExpansionCostProto;
import com.lvl6.proto.CityProto.FullCityProto;
import com.lvl6.proto.CityProto.UserCityExpansionDataProto;
import com.lvl6.proto.ClanProto.FullClanProto;
import com.lvl6.proto.ClanProto.FullClanProtoWithClanSize;
import com.lvl6.proto.ClanProto.FullUserClanProto;
import com.lvl6.proto.ClanProto.MinimumUserProtoForClans;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.ReferralNotificationProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.AnimatedSpriteOffsetProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.QuestProto.DialogueProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.QuestProto.FullQuestProto.QuestType;
import com.lvl6.proto.QuestProto.FullUserQuestProto;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.FullStructureProto;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.MinimumUserTaskProto;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.TournamentStuffProto.MinimumUserProtoWithLevelForTournament;
import com.lvl6.proto.TournamentStuffProto.TournamentEventProto;
import com.lvl6.proto.TournamentStuffProto.TournamentEventRewardProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;

public class CreateInfoProtoUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public static FullClanProtoWithClanSize createFullClanProtoWithClanSize(Clan c) {
    FullClanProto clan = createFullClanProtoFromClan(c);
    List<UserClan> userClanMembersInClan = RetrieveUtils.userClanRetrieveUtils().getUserClanMembersInClan(c.getId());
    int size = (userClanMembersInClan != null) ? userClanMembersInClan.size() : 0;
    return FullClanProtoWithClanSize.newBuilder().setClan(clan).setClanSize(size).build();
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

  public static MinimumUserProtoWithLevel createMinimumUserProtoWithLevelFromUser(User u) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(u);
    return MinimumUserProtoWithLevel.newBuilder().setMinUserProto(mup).setLevel(u.getLevel()).build();
  }

  public static MinimumUserProtoForClans createMinimumUserProtoForClans(User u, UserClanStatus s) {
    MinimumUserProtoWithBattleHistory mup = createMinimumUserProtoWithBattleHistory(u);
    return MinimumUserProtoForClans.newBuilder().setMinUserProto(mup).setClanStatus(s).build();
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

  public static MinimumUserProtoWithBattleHistory createMinimumUserProtoWithBattleHistory(User u) {
    MinimumUserProtoWithLevel mup = createMinimumUserProtoWithLevelFromUser(u);
    return MinimumUserProtoWithBattleHistory.newBuilder().setMinUserProtoWithLevel(mup).setBattlesWon(u.getBattlesWon()).setBattlesLost(u.getBattlesLost()).setBattlesFled(u.getFlees()).build();
  }


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

  public static UserCityExpansionDataProto createUserCityExpansionDataProtoFromUserCityExpansionData(UserCityExpansionData uced) {
    UserCityExpansionDataProto.Builder builder = UserCityExpansionDataProto.newBuilder().setUserId(uced.getUserId())
        .setXPosition(uced.getxPosition()).setYPosition(uced.getyPosition()).setIsExpanding(uced.isExpanding());
    if (uced.getExpandStartTime() != null) {
      builder.setExpandStartTime(uced.getExpandStartTime().getTime());
    }
    return builder.build();
  }

  public static CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(ExpansionCost cec) {
    CityExpansionCostProto.Builder builder = CityExpansionCostProto.newBuilder();
    builder.setExpansionNum(cec.getId())
    .setExpansionCost(cec.getExpansionCost());
    return builder.build();
  }

  public static FullQuestProto createFullQuestProtoFromQuest(Quest quest) {
    String name = null;
    String description = null;
    String doneResponse = null;
    Dialogue acceptDialogue = null;


    String questGiverImageSuffix = null;
    name = quest.getGoodName();
    description = quest.getGoodDescription();
    doneResponse = quest.getGoodDoneResponse();
    acceptDialogue = quest.getGoodAcceptDialogue();
    questGiverImageSuffix = quest.getGoodQuestGiverImageSuffix();

    //SET THE BUILDER
    FullQuestProto.Builder builder = FullQuestProto.newBuilder();
    builder.setQuestId(quest.getId());
    builder.setCityId(quest.getCityId());
    builder.setName(name);
    builder.setDescription(description);
    builder.setDoneResponse(doneResponse);
    if (acceptDialogue != null) {
      builder.setAcceptDialogue(createDialogueProtoFromDialogue(acceptDialogue));
    }
    
    int qType = quest.getQuestType();
    if (qType > 0) {
      QuestType qt = QuestType.valueOf(qType);
      builder.setQuestType(qt);
    }
    builder.setJobDescription(quest.getJobDescription());
    builder.setStaticDataId(quest.getStaticDataId());
    builder.setQuantity(quest.getQuantity());
    builder.setCoinReward(quest.getCoinReward());
    builder.setDiamondReward(quest.getDiamondReward());
    builder.setExpReward(quest.getExpReward());
    builder.setMonsterIdReward(quest.getMonsterIdReward());
    builder.setIsCompleteMonster(quest.isCompleteMonster());
    builder.addAllQuestsRequiredForThis(quest.getQuestsRequiredForThis());
    builder.setQuestGiverImageSuffix(questGiverImageSuffix);
    if (quest.getPriority() > 0) {
      builder.setPriority(quest.getPriority());
    }
    builder.setCarrotId(quest.getCarrotId());
    
    return builder.build();
  }

  public static DialogueProto createDialogueProtoFromDialogue(Dialogue d) {
    if (d == null) return null;

    DialogueProto.Builder dp = DialogueProto.newBuilder();

    List<String> speakerTexts = d.getSpeakerTexts();
    int i = 0;
    for (DialogueSpeaker speaker : d.getSpeakers()) {
      dp.addSpeechSegment(SpeechSegmentProto.newBuilder().setSpeaker(speaker).
          setSpeakerText(speakerTexts.get(i)).build());
      i++;
    }
    return dp.build();
  }

  public static FullUserStructureProto createFullUserStructureProtoFromUserstruct(StructureForUser userStruct) {
    FullUserStructureProto.Builder builder = FullUserStructureProto.newBuilder();
    builder.setUserStructId(userStruct.getId());
    builder.setUserId(userStruct.getUserId());
    builder.setStructId(userStruct.getStructId());
    builder.setLevel(userStruct.getLevel());
    builder.setIsComplete(userStruct.isComplete());
    builder.setCoordinates(createCoordinateProtoFromCoordinatePair(userStruct.getCoordinates()));
    builder.setOrientation(userStruct.getOrientation());
    if (userStruct.getPurchaseTime() != null) {
      builder.setPurchaseTime(userStruct.getPurchaseTime().getTime());
    }
    if (userStruct.getLastRetrieved() != null) {
      builder.setLastRetrieved(userStruct.getLastRetrieved().getTime());
    }
    if (userStruct.getLastUpgradeTime() != null) {
      builder.setLastUpgradeTime(userStruct.getLastUpgradeTime().getTime());
    }
    return builder.build();
  }

  public static FullUserProto createFullUserProtoFromUser(User u) {
    FullUserProto.Builder builder = FullUserProto.newBuilder();
    builder.setUserId(u.getId());
    builder.setName(u.getName());
    builder.setLevel(u.getLevel());
    builder.setGems(u.getGems());
    builder.setCash(u.getCash());
    builder.setExperience(u.getExperience());
    builder.setTasksCompleted(u.getTasksCompleted());
    builder.setBattlesWon(u.getBattlesWon());
    builder.setBattlesLost(u.getBattlesLost());
    builder.setFlees(u.getFlees());
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
    if (u.getClanId() > 0) {
      Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
      builder.setClan(createMinimumClanProtoFromClan(clan));
    }
    builder.setHasReceivedfbReward(u.isHasReceivedfbReward());
    builder.setNumAdditionalMonsterSlots(u.getNumAdditionalMonsterSlots());
    builder.setNumBeginnerSalesPurchased(u.getNumBeginnerSalesPurchased());
    builder.setHasActiveShield(u.isHasActiveShield());
    if(u.getShieldEndTime() != null) {
      builder.setShieldEndTime(u.getShieldEndTime().getTime());
    }
    builder.setElo(u.getElo());
    builder.setRank(u.getRank());
    if (u.getLastTimeQueued() != null) {
      builder.setLastTimeQueued(u.getLastTimeQueued().getTime());
    }
    builder.setAttacksWon(u.getAttacksWon());
    builder.setDefensesWon(u.getAttacksWon());
    builder.setAttacksLost(u.getAttacksLost());
    builder.setDefensesLost(u.getDefensesLost());

    //ADD NEW COLUMNS ABOVE HERE, NOT BELOW THE IF, ELSE CASE FOR IS FAKE


    if (u.isFake()) {

    }
    //don't add setting new columns/properties here, add up above

    return builder.build();
  }

  //	public static FullEquipProto createFullEquipProtoFromEquip(Equipment equip) {
  //		FullEquipProto.Builder builder =  FullEquipProto.newBuilder();
  //		builder.setEquipId(equip.getId());
  //		builder.setName(equip.getName());
  //		builder.setEquipType(equip.getType());
  //		builder.setDescription(equip.getDescription());
  //		builder.setAttackBoost(equip.getAttackBoost());
  //		builder.setDefenseBoost(equip.getDefenseBoost());
  //		builder.setMinLevel(equip.getMinLevel());
  //		builder.setRarity(equip.getRarity());
  //		builder.setChanceOfForgeFailureBase(equip.getChanceOfForgeFailureBase());
  //		builder.setMinutesToAttemptForgeBase(equip.getMinutesToAttemptForgeBase());
  //		builder.setMaxDurability(equip.getMaxDurability());
  //		return builder.build();
  //	}

  public static FullClanProto createFullClanProtoFromClan(Clan c) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(RetrieveUtils.userRetrieveUtils().getUserById(c.getOwnerId()));
    return FullClanProto.newBuilder().setClanId(c.getId()).setName(c.getName())
        .setOwner(mup).setCreateTime(c.getCreateTime().getTime())
        .setDescription(c.getDescription()).setTag(c.getTag())
        .setRequestToJoinRequired(c.isRequestToJoinRequired()).build();
  }

  public static MinimumClanProto createMinimumClanProtoFromClan(Clan c) {
    MinimumClanProto.Builder mcp = MinimumClanProto.newBuilder();
    mcp.setClanId(c.getId());
    mcp.setName(c.getName());
    mcp.setOwnerId(c.getOwnerId());
    mcp.setCreateTime(c.getCreateTime().getTime());
    mcp.setDescription(c.getDescription());
    mcp.setTag(c.getTag());
    return mcp.setRequestToJoinRequired(c.isRequestToJoinRequired()).build();
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
    fumpb.setTeamSlotNum(mfu.getTeamSlotNum());
    return fumpb.build();
  }
  
  public static UserMonsterHealingProto createUserMonsterHealingProtoFromObj(
  		MonsterHealingForUser mhfu) {
  	UserMonsterHealingProto.Builder umhpb = UserMonsterHealingProto.newBuilder();
  	umhpb.setUserId(mhfu.getUserId());
  	umhpb.setUserMonsterId(mhfu.getMonsterForUserId());
  	
  	Date aDate = mhfu.getExpectedStartTime();
  	if (null != aDate) {
  		umhpb.setExpectedStartTimeMillis(aDate.getTime());
  	}
  	
//  	aDate = mhfu.getQueuedTime();
//  	if (null != aDate) {
//  		umhpb.setQueuedTimeMillis(aDate.getTime());
//  	}
  	
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
  	
  	return ueipb.build();
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

    return builder.build();
  }

  public static CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp) {
    return CoordinateProto.newBuilder().setX(cp.getX()).setY(cp.getY()).build();
  }

  public static FullStructureProto createFullStructureProtoFromStructure(Structure s) {
    FullStructureProto.Builder builder = FullStructureProto.newBuilder();
    builder.setStructId(s.getId());
    builder.setName(s.getName());
    builder.setIncome(s.getIncome());
    builder.setMinutesToGain(s.getMinutesToGain());
    builder.setMinutesToBuild(1234567890);
    if (s.getCashPrice() > 0) {
    	builder.setCashPrice(s.getCashPrice());
    }
    if (s.getGemPrice() > 0) {
    	builder.setGemPrice(s.getGemPrice());
    }
    builder.setMinLevel(s.getMinLevel());
    builder.setXLength(s.getxLength());
    builder.setYLength(s.getyLength());
    builder.setInstaBuildGemCost(s.getInstaBuildGemCost());
    builder.setImgVerticalPixelOffset(s.getImgVerticalPixelOffset());
    
    if (s.getSuccessorStructId() > 0) {
    	builder.setSuccessorStructId(s.getSuccessorStructId());
    }
    
    return builder.build();
  }

  public static FullCityProto createFullCityProtoFromCity(City c) {
    FullCityProto.Builder builder = FullCityProto.newBuilder();
    builder.setCityId(c.getId());
    builder.setName(c.getName());
    builder.setMapImgName(c.getMapImgName()).setCenter(createCoordinateProtoFromCoordinatePair(c.getCenter()));
    List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(c.getId());
    if (tasks != null) {
      for (Task t : tasks) {
        builder.addTaskIds(t.getId());
      }
    }

    return builder.build();
  }

  public static List<FullUserQuestProto> createFullUserQuestDataLarges(List<QuestForUser> userQuests, Map<Integer, Quest> questIdsToQuests) {
    List<FullUserQuestProto> fullUserQuestDataLargeProtos = new ArrayList<FullUserQuestProto>();

    for (QuestForUser userQuest : userQuests) {
      Quest quest = questIdsToQuests.get(userQuest.getQuestId());
      FullUserQuestProto.Builder builder = FullUserQuestProto.newBuilder();

      if (quest != null) {
        builder.setUserId(userQuest.getUserId());
        builder.setQuestId(quest.getId());
        builder.setIsRedeemed(userQuest.isRedeemed());
        builder.setIsComplete(userQuest.isComplete());
        builder.setProgress(userQuest.getProgress());
        fullUserQuestDataLargeProtos.add(builder.build());
        
      } else {
        log.error("no quest with id " + userQuest.getQuestId() + ", userQuest=" + userQuest);
      }
    }
    return fullUserQuestDataLargeProtos;
  }

  public static MinimumUserTaskProto createMinimumUserTaskProto(Integer userId, int taskId, Integer numTimesUserActed) {
    return MinimumUserTaskProto.newBuilder().setUserId(userId).setTaskId(taskId).setNumTimesActed(numTimesUserActed).build();
  }

  public static CityElementProto createCityElementProtoFromCityElement(CityElement nce) {
    CityElementProto.Builder builder = CityElementProto.newBuilder();
    builder.setCityId(nce.getCityId());
    builder.setAssetId(nce.getAssetId());
//    builder.setName(nce.getGoodName());
    builder.setType(nce.getType());
    builder.setCoords(createCoordinateProtoFromCoordinatePair(nce.getCoords()));

    if (nce.getxLength() > 0) {
      builder.setXLength(nce.getxLength());
    }
    if (nce.getyLength() > 0) {
      builder.setYLength(nce.getyLength());
    }
    builder.setImgId(nce.getImgGood());
    if (nce.getOrientation() != null) {
    	builder.setOrientation(nce.getOrientation());
    }
    
    builder.setSpriteCoords(createCoordinateProtoFromCoordinatePair(nce.getSpriteCoords()));
    
    return builder.build();
  }

  public static PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPost (
      PrivateChatPost p, User poster, User recipient) {
    MinimumUserProto mupPoster = createMinimumUserProtoFromUser(poster); 
    MinimumUserProto mupRecipient = createMinimumUserProtoFromUser(recipient);

    // Truncate time because db truncates it
    long time = p.getTimeOfPost().getTime();
    time = time - time % 1000;

    return PrivateChatPostProto.newBuilder().setPrivateChatPostId(p.getId())
        .setPoster(mupPoster).setRecipient(mupRecipient)
        .setTimeOfPost(time).setContent(p.getContent()).build();
  }

  public static PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPostAndProtos (
      PrivateChatPost p, MinimumUserProto mupPoster, MinimumUserProto mupRecipient) {
    return PrivateChatPostProto.newBuilder().setPrivateChatPostId(p.getId())
        .setPoster(mupPoster).setRecipient(mupRecipient)
        .setTimeOfPost(p.getTimeOfPost().getTime()).setContent(p.getContent()).build();
  }

  public static List<PrivateChatPostProto> createPrivateChatPostProtoFromPrivateChatPostsAndProtos (
      List<PrivateChatPost> pList, Map<Integer, MinimumUserProto> idsToMups) {
    List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();

    for (PrivateChatPost pcp : pList) {
      MinimumUserProto mupPoster = idsToMups.get(pcp.getPosterId());
      MinimumUserProto mupRecipient = idsToMups.get(pcp.getRecipientId());

      PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(pcp,
          mupPoster, mupRecipient);

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
    Map<Integer, MinimumUserProto> userIdToMinimumUserProto = new HashMap<Integer, MinimumUserProto>();
    //construct the minimum user protos for the users that have clans
    //and the clanless users
    createMinimumUserProtosFromClannedAndClanlessUsers(clanIdsToClans, clanIdsToUserIdSet,
        clanlessUserIds, userIdsToUsers, userIdToMinimumUserProto);

    //now actually construct the PrivateChatPostProtos
    if (null != privateChatPostIds && !privateChatPostIds.isEmpty()) {
      //only pick out a subset of postIdsToPrivateChatPosts
      for (int postId : privateChatPostIds) {
        PrivateChatPost pcp = postIdsToPrivateChatPosts.get(postId);
        int posterId = pcp.getPosterId();
        int recipientId = pcp.getRecipientId();

        MinimumUserProto mupPoster = userIdToMinimumUserProto.get(posterId);
        MinimumUserProto mupRecipient = userIdToMinimumUserProto.get(recipientId);

        PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
            pcp, mupPoster, mupRecipient);
        pcppList.add(pcpp);
      }
    } else {
      for (PrivateChatPost pcp : postIdsToPrivateChatPosts.values()) {
        int posterId = pcp.getPosterId();
        int recipientId = pcp.getRecipientId();
        MinimumUserProto mupPoster = userIdToMinimumUserProto.get(posterId);
        MinimumUserProto mupRecipient = userIdToMinimumUserProto.get(recipientId);

        PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
            pcp, mupPoster, mupRecipient);
        pcppList.add(pcpp);
      }
    }

    return pcppList;
  }

  public static void createMinimumUserProtosFromClannedAndClanlessUsers(
      Map<Integer, Clan> clanIdsToClans, Map<Integer, Set<Integer>> clanIdsToUserIdSet,
      List<Integer> clanlessUserIds, Map<Integer, User> userIdsToUsers, 
      Map<Integer, MinimumUserProto> userIdToMinimumUserProto) {
    //construct the minimum user protos for the clanless users
    for (int userId : clanlessUserIds) {
      User u = userIdsToUsers.get(userId);
      MinimumUserProto mup = createMinimumUserProtoFromUser(u);
      userIdToMinimumUserProto.put(userId, mup);
    }

    //construct the minimum user protos for the users that have clans 
    if (null != clanIdsToClans) {
      for (int clanId : clanIdsToClans.keySet()) {
        Clan c = clanIdsToClans.get(clanId);

        //create minimum user protos for users associated with clan
        for (int userId: clanIdsToUserIdSet.get(clanId)) {
          User u = userIdsToUsers.get(userId);
          MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(u, c);
          userIdToMinimumUserProto.put(userId, mup);
        }
      }
    }
  }

  //	public static UnhandledBlacksmithAttemptProto createUnhandledBlacksmithAttemptProtoFromBlacksmithAttempt(BlacksmithAttempt ba) {
  //		UnhandledBlacksmithAttemptProto.Builder builder = UnhandledBlacksmithAttemptProto.newBuilder().setBlacksmithId(ba.getId()).setUserId(ba.getUserId())
  //				.setEquipId(ba.getEquipId()).setGoalLevel(ba.getGoalLevel()).setGuaranteed(ba.isGuaranteed()).setStartTime(ba.getStartTime().getTime())
  //				.setAttemptComplete(ba.isAttemptComplete()).setEquipOneEnhancementPercent(ba.getEquipOneEnhancementPercent())
  //				.setEquipTwoEnhancementPercent(ba.getEquipTwoEnhancementPercent()).setForgeSlotNumber(ba.getForgeSlotNumber());
  //
  //		if (ba.getDiamondGuaranteeCost() > 0) {
  //			builder.setDiamondGuaranteeCost(ba.getDiamondGuaranteeCost());
  //		}
  //
  //		if (ba.getTimeOfSpeedup() != null) {
  //			builder.setTimeOfSpeedup(ba.getTimeOfSpeedup().getTime());
  //		}
  //
  //		return builder.build();
  //	}

  public static GroupChatMessageProto createGroupChatMessageProtoFromClanChatPost(
      ClanChatPost p, User user) {
    return GroupChatMessageProto.newBuilder().setSender(createMinimumUserProtoFromUser(user))
        .setTimeOfChat(p.getTimeOfPost().getTime()).setContent(p.getContent()).build();
  }

  public static GroupChatMessageProto createGroupChatMessageProto(long time, MinimumUserProto user, String content, boolean isAdmin, int chatId) {
    return GroupChatMessageProto.newBuilder().setSender(user).setTimeOfChat(time).setContent(content).setIsAdmin(isAdmin).setChatId(chatId).build();
  }

  public static FullUserClanProto createFullUserClanProtoFromUserClan(UserClan uc) {
    return FullUserClanProto.newBuilder().setClanId(uc.getClanId()).setUserId(uc.getUserId()).setStatus(uc.getStatus())
        .setRequestTime(uc.getRequestTime().getTime()).build();
  }

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


  public static BoosterItemProto createBoosterItemProto(BoosterItem bi) {
    BoosterItemProto.Builder b = BoosterItemProto.newBuilder()
        .setBoosterItemId(bi.getId()).setEquipId(bi.getEquipId()).setQuantity(bi.getQuantity())
        .setIsSpecial(bi.isSpecial());
    return b.build();
  }

  public static BoosterPackProto createBoosterPackProto(BoosterPack bp, Collection<BoosterItem> biList) {
    BoosterPackProto.Builder b = BoosterPackProto.newBuilder();
    b.setBoosterPackId(bp.getId());
    b.setCostsCoins(bp.isCostsCoins());
    b.setName(bp.getName());
    b.setPrice(1234567890);
    if (biList != null) {
      List<BoosterItemProto> biProtos = new ArrayList<BoosterItemProto>();
      for(BoosterItem bi : biList) {
        biProtos.add(createBoosterItemProto(bi));
      }
      b.addAllBoosterItems(biProtos);
    }
    return b.build();
  }


  public static RareBoosterPurchaseProto createRareBoosterPurchaseProto(BoosterPack bp, User u, Date d) {
    return RareBoosterPurchaseProto.newBuilder().setBooster(createBoosterPackProto(bp, null))
        .setUser(createMinimumUserProtoFromUser(u))
        .setTimeOfPurchase(d.getTime()).build();
  }

  //individualSilvers should always be set, since silver dropped is within a range
  public static TaskStageProto createTaskStageProto (int taskStageId, TaskStage ts,
      List<TaskStageMonster> taskStageMonsters,
      List<Boolean> puzzlePiecesDropped, List<Integer> individualSilvers) {

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
      int silverDrop = individualSilvers.get(i);

      TaskStageMonsterProto mp = createTaskStageMonsterProto(tsm, silverDrop, puzzlePieceDropped);
      mpList.add(mp);
    }

    tspb.addAllStageMonsters(mpList);

    return tspb.build();
  }
  
  public static TaskStageMonsterProto createTaskStageMonsterProto (TaskStageMonster tsm, 
      int cashReward, boolean pieceDropped) {
    TaskStageMonsterProto.Builder bldr = TaskStageMonsterProto.newBuilder();
    bldr.setMonsterId(tsm.getMonsterId());
    bldr.setMonsterType(tsm.getMonsterType());
    bldr.setCashReward(cashReward);
    bldr.setPuzzlePieceDropped(pieceDropped);
    bldr.setExpReward(tsm.getExpReward());
    bldr.setLevel(tsm.getLevel());
    
    return bldr.build();
  }

  public static MonsterProto createMonsterProto(Monster aMonster) {
    MonsterProto.Builder mpb = MonsterProto.newBuilder();

    mpb.setMonsterId(aMonster.getId());
    mpb.setName(aMonster.getName());
    String mGroup = aMonster.getMonsterGroup();
    if (null != mGroup) {
    	mpb.setMonsterGroup(mGroup);
    }
    mpb.setQuality(aMonster.getQuality());
    mpb.setEvolutionLevel(aMonster.getEvolutionLevel());
    mpb.setDisplayName(aMonster.getDisplayName());
    mpb.setElement(aMonster.getElement());
    mpb.setBaseHp(aMonster.getBaseHp());
    mpb.setImagePrefix(aMonster.getImagePrefix());
    mpb.setNumPuzzlePieces(aMonster.getNumPuzzlePieces());
    mpb.setMinutesToCombinePieces(aMonster.getMinutesToCombinePieces());
    mpb.setElementOneDmg(aMonster.getElementOneDmg());
    mpb.setElementTwoDmg(aMonster.getElementTwoDmg());
    mpb.setElementThreeDmg(aMonster.getElementThreeDmg());
    mpb.setElementFourDmg(aMonster.getElementFourDmg());
    mpb.setElementFiveDmg(aMonster.getElementFiveDmg());
    mpb.setHpLevelMultiplier(aMonster.getHpLevelMultiplier());
    mpb.setAttackLevelMultiplier(aMonster.getAttackLevelMultiplier());
    mpb.setMaxLevel(aMonster.getMaxLevel());
    
    int evolId = aMonster.getEvolutionMonsterId();
    if (evolId > 0) {
    	mpb.setEvolutionMonsterId(evolId);
    }
    String carrot = aMonster.getCarrotRecruited();
    if (null != carrot) {
    	mpb.setCarrotRecruited(carrot);
    }
    carrot = aMonster.getCarrotDefeated();
    if (null != carrot) {
    	mpb.setCarrotDefeated(carrot);
    }
    carrot = aMonster.getCarrotEvolved();
    if (null != carrot) {
    	mpb.setCarrotEvolved(carrot);
    }
    String description = aMonster.getDescription();
    if (null != description) {
    	mpb.setDescription(description);
    }
    
    return mpb.build();
  }

}
