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
import com.lvl6.info.BlacksmithAttempt;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.City;
import com.lvl6.info.CityElement;
import com.lvl6.info.CityExpansionCost;
import com.lvl6.info.CityGem;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Dialogue;
import com.lvl6.info.GoldSale;
import com.lvl6.info.LeaderboardEvent;
import com.lvl6.info.LeaderboardEventReward;
import com.lvl6.info.Mentorship;
import com.lvl6.info.Monster;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.Quest;
import com.lvl6.info.Referral;
import com.lvl6.info.Structure;
import com.lvl6.info.Task;
import com.lvl6.info.TaskStage;
import com.lvl6.info.User;
import com.lvl6.info.UserCityExpansionData;
import com.lvl6.info.UserCityGem;
import com.lvl6.info.UserClan;
import com.lvl6.info.UserEquip;
import com.lvl6.info.UserQuest;
import com.lvl6.info.UserStruct;
import com.lvl6.info.jobs.BuildStructJob;
import com.lvl6.info.jobs.UpgradeStructJob;
import com.lvl6.misc.MiscMethods;
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
import com.lvl6.proto.JobProto.BuildStructJobProto;
import com.lvl6.proto.JobProto.MinimumUserBuildStructJobProto;
import com.lvl6.proto.JobProto.MinimumUserUpgradeStructJobProto;
import com.lvl6.proto.JobProto.UpgradeStructJobProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterElement;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterQuality;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.MonsterType;
import com.lvl6.proto.QuestProto.DialogueProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto.DialogueSpeaker;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.QuestProto.FullUserQuestDataLargeProto;
import com.lvl6.proto.QuestProto.MinimumUserQuestTaskProto;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.FullStructureProto;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.MinimumUserTaskProto;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.TournamentStuffProto.MinimumUserProtoWithLevelForTournament;
import com.lvl6.proto.TournamentStuffProto.TournamentProto;
import com.lvl6.proto.TournamentStuffProto.TournamentRewardProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.retrieveutils.UserQuestsTaskProgressRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BuildStructJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CityElementsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.UpgradeStructJobRetrieveUtils;

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

//	public static MarketplacePostPurchasedNotificationProto createMarketplacePostPurchasedNotificationProtoFromMarketplaceTransaction(MarketplaceTransaction mt, User buyer, User seller) {
//		FullMarketplacePostProto fmpp = createFullMarketplacePostProtoFromMarketplacePost(mt.getPost(), seller);
//		return MarketplacePostPurchasedNotificationProto.newBuilder().setMarketplacePost(fmpp)
//				.setBuyer(createMinimumUserProtoFromUser(buyer)).setTimeOfPurchase(mt.getTimeOfPurchase().getTime())
//				.setSellerHadLicense(mt.getSellerHadLicense()).build();
//	}

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
	
	public static CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(CityExpansionCost cec) {
		CityExpansionCostProto.Builder builder = CityExpansionCostProto.newBuilder();
		builder.setExpansionNum(cec.getId())
				.setExpansionCost(cec.getExpansionCost());
		return builder.build();
	}

	public static FullQuestProto createFullQuestProtoFromQuest(Quest quest) {
		String name = null;
		String description = null;
		String doneResponse = null;
		List<Integer> defeatTypeReqs = null;
		Dialogue acceptDialogue = null;

		String questGiverName = null;
		CityElement nce = CityElementsRetrieveUtils.getCityElement(quest.getCityId(), quest.getAssetNumWithinCity());

		String questGiverImageSuffix = null;
		name = quest.getGoodName();
		description = quest.getGoodDescription();
		doneResponse = quest.getGoodDoneResponse();
		acceptDialogue = quest.getGoodAcceptDialogue();
		if (nce != null) {
			questGiverName = nce.getGoodName();
		}
		questGiverImageSuffix = quest.getGoodQuestGiverImageSuffix();

		FullQuestProto.Builder builder = FullQuestProto.newBuilder();
		builder.setQuestId(quest.getId());
		builder.setCityId(quest.getCityId());
		builder.setName(name);
		builder.setDescription(description);
		builder.setDoneResponse(doneResponse);
		builder.setAssetNumWithinCity(quest.getAssetNumWithinCity());
		builder.setCoinsGained(quest.getCoinsGained());
		builder.setDiamondsGained(quest.getDiamondsGained());
		builder.setExpGained(quest.getExpGained());
		builder.setMonsterIdGained(quest.getEquipIdGained());
		builder.addAllQuestsRequiredForThis(quest.getQuestsRequiredForThis());
		builder.addAllTaskReqs(quest.getTasksRequired());
		builder.addAllUpgradeStructJobsReqs(quest.getUpgradeStructJobsRequired());
		builder.addAllBuildStructJobsReqs(quest.getBuildStructJobsRequired());
		builder.setNumComponentsForGood(quest.getNumComponents());
		builder.setCoinRetrievalReq(quest.getCoinRetrievalAmountRequired());
		builder.setQuestGiverImageSuffix(questGiverImageSuffix);
		if (acceptDialogue != null) {
			builder.setAcceptDialogue(createDialogueProtoFromDialogue(acceptDialogue));
		}
		if (questGiverName != null) {
			builder.setQuestGiverName(questGiverName);
		}
		if (quest.getSpecialQuestActionRequired() != null) {
			builder.setSpecialQuestActionReq(quest.getSpecialQuestActionRequired());
		}
		if (quest.getPriority() > 0) {
			builder.setPriority(quest.getPriority());
		}
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

	public static FullUserStructureProto createFullUserStructureProtoFromUserstruct(UserStruct userStruct) {
		FullUserStructureProto.Builder builder = FullUserStructureProto.newBuilder().setUserStructId(userStruct.getId())
				.setUserId(userStruct.getUserId()).setStructId(userStruct.getStructId()).setLevel(userStruct.getLevel())
				.setIsComplete(userStruct.isComplete())
				.setCoordinates(createCoordinateProtoFromCoordinatePair(userStruct.getCoordinates()))
				.setOrientation(userStruct.getOrientation());
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
		builder.setDiamonds(u.getDiamonds());
		builder.setCoins(u.getCoins());
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
		builder.setNumAdditionalForgeSlots(u.getNumAdditionalForgeSlots());
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
			int equipmentLevel = u.getLevel();

			UserEquip weaponUserEquip = null;
			UserEquip armorUserEquip = null;
			UserEquip amuletUserEquip = null;
			
			int forgeLevel = ControllerConstants.DEFAULT_USER_EQUIP_LEVEL; 
			int enhancement = ControllerConstants.DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT;
			int durability = ControllerConstants.DEFAULT_USER_EQUIP_LEVEL;

			weaponUserEquip = new UserEquip(ControllerConstants.NOT_SET, u.getId(),
					ControllerConstants.ALL_CHARACTERS_WEAPON_ID_PER_LEVEL[equipmentLevel-1],
					forgeLevel, enhancement, durability);
			armorUserEquip = new UserEquip(ControllerConstants.NOT_SET, u.getId(),
					ControllerConstants.ALL_CHARACTERS_ARMOR_ID_PER_LEVEL[equipmentLevel-1],
					forgeLevel, enhancement, durability);
			amuletUserEquip = new UserEquip(ControllerConstants.NOT_SET, u.getId(),
					ControllerConstants.ALL_CHARACTERS_EQUIP_LEVEL[equipmentLevel-1],
					forgeLevel, enhancement, durability);
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
		return MinimumClanProto.newBuilder()
				.setClanId(c.getId()).setName(c.getName()).setOwnerId(c.getOwnerId())
				.setCreateTime(c.getCreateTime().getTime()).setDescription(c.getDescription())
				.setTag(c.getTag()).setCurrentTierLevel(c.getCurrentTierLevel())
				.setRequestToJoinRequired(c.isRequestToJoinRequired()).build();
	}

	public static FullUserEquipProto createFullUserEquipProtoFromUserEquip(UserEquip ue) {
		FullUserEquipProto.Builder fuepb = FullUserEquipProto.newBuilder();
		fuepb.setUserEquipId(ue.getId());
		fuepb.setUserId(ue.getUserId());
		fuepb.setEquipId(ue.getEquipId());
		fuepb.setLevel(ue.getLevel());
		fuepb.setEnhancementPercentage(ue.getEnhancementPercentage());
		fuepb.setCurrentDurability(ue.getCurrentDurability());
		return fuepb.build();
	}

	public static FullUserEquipProto createFullUserEquipProto(long userEquipId,
			int uId, int equipId, int equipLevel, int enhancement) {
		FullUserEquipProto.Builder fuepb = FullUserEquipProto.newBuilder();
		fuepb.setUserEquipId(userEquipId);
		fuepb.setUserId(uId);
		fuepb.setEquipId(equipId);
		fuepb.setLevel(equipLevel);
		fuepb.setEnhancementPercentage(enhancement);

		return fuepb.build();
	}


	public static FullTaskProto createFullTaskProtoFromTask(Task task) {

		String name = null;
//		String processingText = null;

			name = task.getGoodName();
//			processingText = task.getGoodProcessingText();

		FullTaskProto.Builder builder = FullTaskProto.newBuilder();
		builder.setTaskId(task.getId());
		builder.setName(name).setCityId(task.getCityId());
		builder.setAssetNumWithinCity(task.getAssetNumberWithinCity());
		
		Map<Integer, Integer> equipIdsToQuantity = TaskEquipReqRetrieveUtils.getEquipmentIdsToQuantityForTaskId(task.getId());
		if (equipIdsToQuantity != null && equipIdsToQuantity.size() > 0) {
			for (Integer equipId : equipIdsToQuantity.keySet()) {
				FullTaskEquipReqProto fterp = FullTaskEquipReqProto.newBuilder().setTaskId(task.getId()).setEquipId(equipId).setQuantity(equipIdsToQuantity.get(equipId)).build();
				builder.addEquipReqs(fterp);
			}
		}
		return builder.build();
	}

	public static CoordinateProto createCoordinateProtoFromCoordinatePair(CoordinatePair cp) {
		return CoordinateProto.newBuilder().setX(cp.getX()).setY(cp.getY()).build();
	}

	public static FullStructureProto createFullStructureProtoFromStructure(Structure s) {
		return FullStructureProto.newBuilder().setStructId(s.getId()).setName(s.getName()).setIncome(s.getIncome())
				.setMinutesToGain(s.getMinutesToGain()).setMinutesToBuild(MiscMethods.calculateMinutesToBuildOrUpgradeForUserStruct(s.getMinutesToUpgradeBase(), 0))
				.setMinutesToUpgradeBase(s.getMinutesToUpgradeBase()).setCoinPrice(s.getCoinPrice())
				.setDiamondPrice(s.getDiamondPrice()).setMinLevel(s.getMinLevel())
				.setXLength(s.getxLength()).setYLength(s.getyLength())
				.setInstaBuildDiamondCost(s.getInstaBuildDiamondCost())
				.setInstaRetrieveDiamondCostBase(s.getInstaRetrieveDiamondCostBase())
				.setInstaUpgradeDiamondCostBase(s.getInstaUpgradeDiamondCostBase())
				.setImgVerticalPixelOffset(s.getImgVerticalPixelOffset()).build();
	}

	public static FullCityProto createFullCityProtoFromCity(City c) {
		FullCityProto.Builder builder = FullCityProto.newBuilder().setCityId(c.getId()).setName(c.getName()).setMinLevel(c.getMinLevel())
				.setExpGainedBaseOnRankup(c.getExpGainedBaseOnRankup()).setCoinsGainedBaseOnRankup(c.getCoinsGainedBaseOnRankup())
				.setMapImgName(c.getMapImgName()).setCenter(createCoordinateProtoFromCoordinatePair(c.getCenter()));
		List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(c.getId());
		if (tasks != null) {
			for (Task t : tasks) {
				builder.addTaskIds(t.getId());
			}
		}

		return builder.build();
	}

	public static BuildStructJobProto createFullBuildStructJobProtoFromBuildStructJob(
			BuildStructJob j) {
		return BuildStructJobProto.newBuilder().setBuildStructJobId(j.getId()).setStructId(j.getStructId()).setQuantityRequired(j.getQuantity()).build();
	}

	public static UpgradeStructJobProto createFullUpgradeStructJobProtoFromUpgradeStructJob(
			UpgradeStructJob j) {
		return UpgradeStructJobProto.newBuilder().setUpgradeStructJobId(j.getId()).setStructId(j.getStructId()).setLevelReq(j.getLevelReq()).build();
	}


	public static List<FullUserQuestDataLargeProto> createFullUserQuestDataLarges(List<UserQuest> userQuests, Map<Integer, Quest> questIdsToQuests) {
		List<FullUserQuestDataLargeProto> fullUserQuestDataLargeProtos = new ArrayList<FullUserQuestDataLargeProto>();

		Map<Integer, List<Integer>> questIdToUserTasksCompletedForQuestForUser = null;
		Map<Integer, Map<Integer, Integer>> questIdToTaskIdsToNumTimesActedInQuest = null;

		Map<Integer, List<Integer>> questIdToUserDefeatTypeJobsCompletedForQuestForUser = null;
		Map<Integer, Map<Integer, Integer>> questIdToDefeatTypeJobIdsToNumDefeated = null;

		Map<Integer, List<UserStruct>> structIdsToUserStructs = null;

		Map<Integer, List<UserEquip>> equipIdsToUserEquips = null;

		for (UserQuest userQuest : userQuests) {
			Quest quest = questIdsToQuests.get(userQuest.getQuestId());
			FullUserQuestDataLargeProto.Builder builder = FullUserQuestDataLargeProto.newBuilder();

			if (quest != null) {
				int numComponentsComplete = 0;
				builder.setUserId(userQuest.getUserId());
				builder.setQuestId(quest.getId());
				builder.setIsRedeemed(userQuest.isRedeemed());
				builder.setIsComplete(userQuest.isComplete());
				builder.setCoinsRetrievedForReq(userQuest.getCoinsRetrievedForReq());

				if (!userQuest.isRedeemed() && !userQuest.isComplete()) {
					List<Integer> tasksRequired = quest.getTasksRequired(); 
					if (tasksRequired != null && tasksRequired.size() > 0) {

						if (questIdToUserTasksCompletedForQuestForUser == null) {
							questIdToUserTasksCompletedForQuestForUser = RetrieveUtils.userQuestsCompletedTasksRetrieveUtils().getQuestIdToUserTasksCompletedForQuestForUser(userQuest.getUserId());
						}
						List<Integer> userTasksCompletedForQuest = questIdToUserTasksCompletedForQuestForUser.get(userQuest.getQuestId());

						for (Integer requiredTaskId : tasksRequired) {
							boolean taskCompletedForQuest = false;
							Integer numTimesActed = null;
							if (userQuest.isTasksComplete() || (userTasksCompletedForQuest != null && userTasksCompletedForQuest.contains(requiredTaskId))) {
								taskCompletedForQuest = true;
								numComponentsComplete++;
							} else {
								if (questIdToTaskIdsToNumTimesActedInQuest == null) {
									questIdToTaskIdsToNumTimesActedInQuest = UserQuestsTaskProgressRetrieveUtils.getQuestIdToTaskIdsToNumTimesActedInQuest(userQuest.getUserId());
								}
								Map<Integer, Integer> taskIdsToNumTimesActedForUserQuest = questIdToTaskIdsToNumTimesActedInQuest.get(userQuest.getQuestId());
								if (taskIdsToNumTimesActedForUserQuest != null) {
									numTimesActed = taskIdsToNumTimesActedForUserQuest.get(requiredTaskId);
									if (numTimesActed == null) {
										numTimesActed = 0;
									}
								} else {
									numTimesActed = 0;
								}
							}
							builder.addRequiredTasksProgress(createMinimumUserQuestTaskProto(userQuest, requiredTaskId, taskCompletedForQuest, numTimesActed));
						}
					}

					List<Integer> defeatTypeJobsRequired; 
//					if (defeatTypeJobsRequired != null && defeatTypeJobsRequired.size() > 0) {
//						if (questIdToUserDefeatTypeJobsCompletedForQuestForUser == null) {
//							questIdToUserDefeatTypeJobsCompletedForQuestForUser = RetrieveUtils.userQuestsCompletedDefeatTypeJobsRetrieveUtils().getQuestIdToUserDefeatTypeJobsCompletedForQuestForUser(userQuest.getUserId());
//						}
//						List<Integer> userDefeatTypeJobsCompletedForQuest = questIdToUserDefeatTypeJobsCompletedForQuestForUser.get(userQuest.getQuestId());
//						for (Integer requiredDefeatTypeJobId : defeatTypeJobsRequired) {
//							boolean defeatJobCompletedForQuest = false;
//							Integer numTimesUserDidJob = null;
//							if (userQuest.isDefeatTypeJobsComplete() || (userDefeatTypeJobsCompletedForQuest != null && userDefeatTypeJobsCompletedForQuest.contains(requiredDefeatTypeJobId))) {
//								defeatJobCompletedForQuest = true;
//								numComponentsComplete++;
//							} else {
//								if (questIdToDefeatTypeJobIdsToNumDefeated == null) {
//									questIdToDefeatTypeJobIdsToNumDefeated = UserQuestsDefeatTypeJobProgressRetrieveUtils.getQuestIdToDefeatTypeJobIdsToNumDefeated(userQuest.getUserId());
//								}
//								Map<Integer, Integer> defeatTypeJobIdsToNumDefeatedForUserQuest = questIdToDefeatTypeJobIdsToNumDefeated.get(userQuest.getQuestId());
//								if (defeatTypeJobIdsToNumDefeatedForUserQuest != null) {
//									numTimesUserDidJob = defeatTypeJobIdsToNumDefeatedForUserQuest.get(requiredDefeatTypeJobId);
//									if (numTimesUserDidJob == null) {
//										numTimesUserDidJob = 0;
//									}
//								} else {
//									numTimesUserDidJob = 0;
//								}
//							}
//							builder.addRequiredDefeatTypeJobProgress(createMinimumUserDefeatTypeJobProto(userQuest, requiredDefeatTypeJobId, defeatJobCompletedForQuest, numTimesUserDidJob));
//						}
//					}
					if (quest.getBuildStructJobsRequired() != null && quest.getBuildStructJobsRequired().size() > 0) {
						if (structIdsToUserStructs == null) {
							structIdsToUserStructs = RetrieveUtils.userStructRetrieveUtils().getStructIdsToUserStructsForUser(userQuest.getUserId());              
						}
						for (Integer buildStructJobId : quest.getBuildStructJobsRequired()) {
							BuildStructJob buildStructJob = BuildStructJobRetrieveUtils.getBuildStructJobForBuildStructJobId(buildStructJobId);
							List<UserStruct> userStructs = structIdsToUserStructs.get(buildStructJob.getStructId());
							int quantityBuilt = 0;
							if (userStructs != null) {
								for (UserStruct us : userStructs) {
									if (us.getLastRetrieved() != null) {
										quantityBuilt++;
									}
								}
							}
							if (quantityBuilt >= buildStructJob.getQuantity()) {
								numComponentsComplete++;
							}
							builder.addRequiredBuildStructJobProgress(createMinimumUserBuildStructJobProto(userQuest, buildStructJob, quantityBuilt));
						}
					}
					if (quest.getUpgradeStructJobsRequired() != null && quest.getUpgradeStructJobsRequired().size() > 0) {
						if (structIdsToUserStructs == null) {
							structIdsToUserStructs = RetrieveUtils.userStructRetrieveUtils().getStructIdsToUserStructsForUser(userQuest.getUserId());              
						}
						for (Integer upgradeStructJobId : quest.getUpgradeStructJobsRequired()) {
							UpgradeStructJob upgradeStructJob = UpgradeStructJobRetrieveUtils.getUpgradeStructJobForUpgradeStructJobId(upgradeStructJobId);
							List<UserStruct> userStructs = structIdsToUserStructs.get(upgradeStructJob.getStructId());
							int currentLevel = 0;
							if (userStructs != null) {
								for (UserStruct us : userStructs) {
									if (us.getLevel() > currentLevel) {
										currentLevel = us.getLevel();
									}
								}
							}
							if (currentLevel >= upgradeStructJob.getLevelReq()) {
								numComponentsComplete++;
							}
							builder.addRequiredUpgradeStructJobProgress(createMinimumUserUpgradeStructJobProto(userQuest, upgradeStructJob, currentLevel));
						}
					}
//					if (quest.getPossessEquipJobsRequired() != null && quest.getPossessEquipJobsRequired().size() > 0) {
//						if (equipIdsToUserEquips == null) {
//							equipIdsToUserEquips = RetrieveUtils.userEquipRetrieveUtils().getEquipIdsToUserEquipsForUser(userQuest.getUserId());
//						}
//						for (Integer possessEquipJobId : quest.getPossessEquipJobsRequired()) {
//							PossessEquipJob possessEquipJob = PossessEquipJobRetrieveUtils.getPossessEquipJobForPossessEquipJobId(possessEquipJobId);
//							List<UserEquip> userEquips = equipIdsToUserEquips.get(possessEquipJob.getEquipId());
//							int quantityOwned = (userEquips != null) ? userEquips.size() : 0;
//							if (quantityOwned >= possessEquipJob.getQuantity()) {
//								numComponentsComplete++;
//							}
//							builder.addRequiredPossessEquipJobProgress(createMinimumUserPossessEquipJobProto(userQuest, possessEquipJob, quantityOwned));
//						}
//					}
					if (quest.getCoinRetrievalAmountRequired() > 0) {
						if (userQuest.getCoinsRetrievedForReq() >= quest.getCoinRetrievalAmountRequired()) {
							numComponentsComplete++;
						}
					}
				} else {
					numComponentsComplete = quest.getNumComponents();
				}
				fullUserQuestDataLargeProtos.add(builder.setNumComponentsComplete(numComponentsComplete).build());
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
		CityElementProto.Builder builder = CityElementProto.newBuilder().setCityId(nce.getCityId()).setAssetId(nce.getAssetId())
				.setType(nce.getType())
				.setCoords(createCoordinateProtoFromCoordinatePair(nce.getCoords()));
		builder.setName(nce.getGoodName());
		builder.setImgId(nce.getImgGood());

		if (nce.getOrientation() != null) {
			builder.setOrientation(nce.getOrientation());
		}
		if (nce.getxLength() > 0) {
			builder.setXLength(nce.getxLength());
		}
		if (nce.getyLength() > 0) {
			builder.setYLength(nce.getyLength());
		}
		return builder.build();
	}

	public static FullUserCityProto createFullUserCityProto(int userId, int cityId, int currentRank, int numTasksCurrentlyCompleteInRank) {
		return FullUserCityProto.newBuilder().setUserId(userId).setCityId(cityId).setCurrentRank(currentRank).setNumTasksCurrentlyCompleteInRank(numTasksCurrentlyCompleteInRank)
				.build();
	}

	private static MinimumUserUpgradeStructJobProto createMinimumUserUpgradeStructJobProto(UserQuest userQuest, UpgradeStructJob upgradeStructJob, int currentLevel) {
		return MinimumUserUpgradeStructJobProto.newBuilder().setUserId(userQuest.getUserId()).setQuestId(userQuest.getQuestId()).setUpgradeStructJobId(upgradeStructJob.getId()).setCurrentLevel(currentLevel).build();
	}

	private static MinimumUserBuildStructJobProto createMinimumUserBuildStructJobProto(UserQuest userQuest, BuildStructJob buildStructJob, int quantityOwned) {
		return MinimumUserBuildStructJobProto.newBuilder().setUserId(userQuest.getUserId()).setQuestId(userQuest.getQuestId()).setBuildStructJobId(buildStructJob.getId()).setNumOfStructUserHas(quantityOwned).build();
	}


	private static MinimumUserQuestTaskProto createMinimumUserQuestTaskProto(UserQuest userQuest, Integer requiredTaskId, boolean taskCompletedForQuest, Integer numTimesUserActed) {
		//TODO:
		//Task task = TaskRetrieveUtils.getTaskForTaskId(requiredTaskId);
		int numTimesCompleted = 1;//(taskCompletedForQuest) ? task.getNumForCompletion() : numTimesUserActed;
		return MinimumUserQuestTaskProto.newBuilder().setUserId(userQuest.getUserId()).setTaskId(requiredTaskId).setNumTimesActed(numTimesCompleted).setQuestId(userQuest.getQuestId()).build();
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

	public static UnhandledBlacksmithAttemptProto createUnhandledBlacksmithAttemptProtoFromBlacksmithAttempt(BlacksmithAttempt ba) {
		UnhandledBlacksmithAttemptProto.Builder builder = UnhandledBlacksmithAttemptProto.newBuilder().setBlacksmithId(ba.getId()).setUserId(ba.getUserId())
				.setEquipId(ba.getEquipId()).setGoalLevel(ba.getGoalLevel()).setGuaranteed(ba.isGuaranteed()).setStartTime(ba.getStartTime().getTime())
				.setAttemptComplete(ba.isAttemptComplete()).setEquipOneEnhancementPercent(ba.getEquipOneEnhancementPercent())
				.setEquipTwoEnhancementPercent(ba.getEquipTwoEnhancementPercent()).setForgeSlotNumber(ba.getForgeSlotNumber());

		if (ba.getDiamondGuaranteeCost() > 0) {
			builder.setDiamondGuaranteeCost(ba.getDiamondGuaranteeCost());
		}

		if (ba.getTimeOfSpeedup() != null) {
			builder.setTimeOfSpeedup(ba.getTimeOfSpeedup().getTime());
		}

		return builder.build();
	}

	public static GroupChatMessageProto createGroupChatMessageProtoFromClanChatPost(
			ClanChatPost p, User user) {
		return GroupChatMessageProto.newBuilder().setSender(createMinimumUserProtoFromUser(user))
				.setTimeOfChat(p.getTimeOfPost().getTime()).setContent(p.getContent()).build();
	}

	public static GroupChatMessageProto createGroupChatMessageProto(long time, MinimumUserProto user, String content, boolean isAdmin, int chatId) {
		return GroupChatMessageProto.newBuilder().setSender(user).setTimeOfChat(time).setContent(content).setIsAdmin(isAdmin).setChatId(chatId).build();
	}


	public static ClanBulletinPostProto createClanBulletinPostProtoFromClanBulletinPost(
			ClanBulletinPost p, User user) {
		return ClanBulletinPostProto.newBuilder().setClanBulletinPostId(p.getId()).setPoster(createMinimumUserProtoFromUser(user)).setClanId(user.getClanId())
				.setTimeOfPost(p.getTimeOfPost().getTime()).setContent(p.getContent()).build();
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
	
	public static TournamentProto createTournamentProtoFromTournament(
			LeaderboardEvent e, List<LeaderboardEventReward> rList) {

		TournamentProto.Builder b = TournamentProto.newBuilder().setEventId(e.getId()).setStartDate(e.getStartDate().getTime())
				.setEndDate(e.getEndDate().getTime()).setEventName(e.getEventName())
				.setLastShowDate(e.getEndDate().getTime()+ControllerConstants.LEADERBOARD_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END*3600000L);

		List<TournamentRewardProto> rProtosList = new ArrayList<TournamentRewardProto>();
		for(TournamentReward r : rList) {
			TournamentRewardProto rProto = createLeaderboardEventRewardProtoFromLeaderboardEventReward(r);
			rProtosList.add(rProto);
		}

		b.addAllRewards(rProtosList);

		return b.build();
	}

	public static TournamentRewardProto createTournamentRewardProtoFromTournamentReward(TournamentReward r) {

		TournamentRewardProto.Builder b = TournamentRewardProto.newBuilder()
				.setLeaderboardEventId(r.getLeaderboardEventId()).setMinRank(r.getMinRank()).setMaxRank(r.getMaxRank())
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
		b.setPrice(value);
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
			List<Integer> monsterIds, Map<Integer, Monster> monsterIdsToMonsters,
			List<Boolean> puzzlePiecesDropped, List<Integer> individualSilvers) {

		TaskStageProto.Builder tspb = TaskStageProto.newBuilder();
		if (null == ts) {
			ts = TaskStageRetrieveUtils.getTaskStageForTaskStageId(taskStageId);
		}

		tspb.setStageId(taskStageId);

		//holds all the monsterProtos
		List<MonsterProto> mpList = new ArrayList<MonsterProto>();

		for (int i = 0; i < monsterIds.size(); i++) {
			int monsterId = monsterIds.get(i);
			Monster m;
			//retrieve monster if not given
			if (!monsterIdsToMonsters.containsKey(monsterId)) {
				m = MonsterRetrieveUtils.getMonsterForMonsterId(monsterId);
			} else {
				m = monsterIdsToMonsters.get(monsterId);
			}
			
			boolean puzzlePieceDropped = puzzlePiecesDropped.get(i);
			int silverDrop = individualSilvers.get(i);

			MonsterProto mp = createMonsterProto(monsterId, m, puzzlePieceDropped, silverDrop);
			mpList.add(mp);
		}

		tspb.addAllMp(mpList);

		return tspb.build();
	}

	// if caller wanted the silverDrop, then silverDrop should be set
	public static MonsterProto createMonsterProto(int monsterId, Monster aMonster,
			boolean puzzlePieceDropped, int silverDrop) {
		if (null == aMonster) {
			aMonster = MonsterRetrieveUtils.getMonsterForMonsterId(monsterId);
		}
		MonsterProto.Builder mpb = MonsterProto.newBuilder();
		
		
		mpb.setMonsterId(monsterId);
		mpb.setName(aMonster.getName());
		
		int val = aMonster.getQuality();
		if (val > 0) {
			MonsterQuality mq = MonsterQuality.valueOf(val);
			mpb.setQuality(mq);
		}
		mpb.setEvolutionLevel(aMonster.getEvolutionLevel());
		mpb.setDisplayName(aMonster.getDisplayName());
		
		val = aMonster.getElement();
		if (val > 0) {
			MonsterElement me = MonsterElement.valueOf(val);
			mpb.setElement(me);
		}
		mpb.setMaxHp(aMonster.getMaxHp());
		mpb.setImageName(aMonster.getImageName());
		
		val = aMonster.getMonsterType();
		if (val > 0) {
			MonsterType mt = MonsterType.valueOf(val);
			mpb.setMonsterType(mt);
		}
		mpb.setExpReward(aMonster.getExpReward());
		mpb.setSilverReward(silverDrop);
		
		mpb.setPuzzlePieceDropped(puzzlePieceDropped);
		return mpb.build();
	}
}
