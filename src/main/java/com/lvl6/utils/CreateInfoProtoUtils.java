package com.lvl6.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.lvl6.info.Achievement;
import com.lvl6.info.AchievementForUser;
import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.BattleItem;
import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.BattleItemQueueForUser;
import com.lvl6.info.Board;
import com.lvl6.info.BoardObstacle;
import com.lvl6.info.BoardProperty;
import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.CepfuRaidHistory;
import com.lvl6.info.CepfuRaidStageHistory;
import com.lvl6.info.Clan;
import com.lvl6.info.ClanAvenge;
import com.lvl6.info.ClanAvengeUser;
import com.lvl6.info.ClanChatPost;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanEventPersistentForClan;
import com.lvl6.info.ClanEventPersistentForUser;
import com.lvl6.info.ClanEventPersistentUserReward;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.ClanInvite;
import com.lvl6.info.ClanMemberTeamDonation;
import com.lvl6.info.ClanRaid;
import com.lvl6.info.ClanRaidStage;
import com.lvl6.info.ClanRaidStageMonster;
import com.lvl6.info.ClanRaidStageReward;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Dialogue;
import com.lvl6.info.EventPersistent;
import com.lvl6.info.EventPersistentForUser;
import com.lvl6.info.FileDownload;
import com.lvl6.info.GoldSale;
import com.lvl6.info.Item;
import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.info.ItemSecretGiftForUser;
import com.lvl6.info.MiniJob;
import com.lvl6.info.MiniJobForUser;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterBattleDialogue;
import com.lvl6.info.MonsterEnhancingForUser;
import com.lvl6.info.MonsterEvolvingForUser;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.MonsterHealingForUser;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.MonsterSnapshotForUser;
import com.lvl6.info.Obstacle;
import com.lvl6.info.ObstacleForUser;
import com.lvl6.info.Prerequisite;
import com.lvl6.info.PrivateChatPost;
import com.lvl6.info.PvpBattleHistory;
import com.lvl6.info.PvpBoardObstacleForUser;
import com.lvl6.info.PvpLeague;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.QuestJob;
import com.lvl6.info.QuestJobForUser;
import com.lvl6.info.Research;
import com.lvl6.info.ResearchProperty;
import com.lvl6.info.Skill;
import com.lvl6.info.SkillProperty;
import com.lvl6.info.SkillSideEffect;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureBattleItemFactory;
import com.lvl6.info.StructureClanHouse;
import com.lvl6.info.StructureEvoChamber;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureHospital;
import com.lvl6.info.StructureLab;
import com.lvl6.info.StructureMiniJob;
import com.lvl6.info.StructureMoneyTree;
import com.lvl6.info.StructurePvpBoard;
import com.lvl6.info.StructureResearchHouse;
import com.lvl6.info.StructureResidence;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.StructureResourceStorage;
import com.lvl6.info.StructureTeamCenter;
import com.lvl6.info.StructureTownHall;
import com.lvl6.info.Task;
import com.lvl6.info.TaskForUserClientState;
import com.lvl6.info.TaskForUserOngoing;
import com.lvl6.info.TaskMapElement;
import com.lvl6.info.TaskStage;
import com.lvl6.info.TaskStageForUser;
import com.lvl6.info.TaskStageMonster;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.AchievementStuffProto.AchievementProto;
import com.lvl6.proto.AchievementStuffProto.AchievementProto.AchievementType;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;
import com.lvl6.proto.BattleItemsProto.BattleItemProto;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.BattleItemsProto.UserBattleItemProto;
import com.lvl6.proto.BattleProto.PvpClanAvengeProto;
import com.lvl6.proto.BattleProto.PvpHistoryProto;
import com.lvl6.proto.BattleProto.PvpHistoryProto.Builder;
import com.lvl6.proto.BattleProto.PvpLeagueProto;
import com.lvl6.proto.BattleProto.PvpMonsterProto;
import com.lvl6.proto.BattleProto.PvpProto;
import com.lvl6.proto.BattleProto.PvpUserClanAvengeProto;
import com.lvl6.proto.BoardProto.BoardLayoutProto;
import com.lvl6.proto.BoardProto.BoardPropertyProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterDisplayItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterItemProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto.BoosterPackType;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.ClanProto.ClanIconProto;
import com.lvl6.proto.ClanProto.ClanInviteProto;
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
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.AnimatedSpriteOffsetProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.FileDownloadConstantProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.ItemsProto.ItemProto;
import com.lvl6.proto.ItemsProto.ItemType;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ItemsProto.UserItemSecretGiftProto;
import com.lvl6.proto.ItemsProto.UserItemUsageProto;
import com.lvl6.proto.MiniJobConfigProto.MiniJobProto;
import com.lvl6.proto.MiniJobConfigProto.UserMiniJobProto;
import com.lvl6.proto.MonsterStuffProto.ClanMemberTeamDonationProto;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MinimumUserMonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto.DialogueType;
import com.lvl6.proto.MonsterStuffProto.MonsterLevelInfoProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto;
import com.lvl6.proto.MonsterStuffProto.MonsterProto.AnimationType;
import com.lvl6.proto.MonsterStuffProto.UserCurrentMonsterTeamProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementItemProto;
import com.lvl6.proto.MonsterStuffProto.UserEnhancementProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterCurrentHealthProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterEvolutionProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterHealingProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto;
import com.lvl6.proto.MonsterStuffProto.UserMonsterSnapshotProto.SnapshotType;
import com.lvl6.proto.PrerequisiteProto.PrereqProto;
import com.lvl6.proto.QuestProto.DialogueProto;
import com.lvl6.proto.QuestProto.DialogueProto.SpeechSegmentProto;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.QuestProto.FullUserQuestProto;
import com.lvl6.proto.QuestProto.QuestJobProto;
import com.lvl6.proto.QuestProto.QuestJobProto.QuestJobType;
import com.lvl6.proto.QuestProto.UserQuestJobProto;
import com.lvl6.proto.ResearchsProto.ResearchDomain;
import com.lvl6.proto.ResearchsProto.ResearchPropertyProto;
import com.lvl6.proto.ResearchsProto.ResearchProto;
import com.lvl6.proto.ResearchsProto.ResearchType;
import com.lvl6.proto.SharedEnumConfigProto.DayOfWeek;
import com.lvl6.proto.SharedEnumConfigProto.Element;
import com.lvl6.proto.SharedEnumConfigProto.GameActionType;
import com.lvl6.proto.SharedEnumConfigProto.GameType;
import com.lvl6.proto.SharedEnumConfigProto.Quality;
import com.lvl6.proto.SkillsProto.SideEffectBlendMode;
import com.lvl6.proto.SkillsProto.SideEffectPositionType;
import com.lvl6.proto.SkillsProto.SideEffectTraitType;
import com.lvl6.proto.SkillsProto.SideEffectType;
import com.lvl6.proto.SkillsProto.SkillActivationType;
import com.lvl6.proto.SkillsProto.SkillPropertyProto;
import com.lvl6.proto.SkillsProto.SkillProto;
import com.lvl6.proto.SkillsProto.SkillSideEffectProto;
import com.lvl6.proto.SkillsProto.SkillType;
import com.lvl6.proto.StructureProto.BattleItemFactoryProto;
import com.lvl6.proto.StructureProto.BoardObstacleType;
import com.lvl6.proto.StructureProto.ClanHouseProto;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.EvoChamberProto;
import com.lvl6.proto.StructureProto.FullUserStructureProto;
import com.lvl6.proto.StructureProto.HospitalProto;
import com.lvl6.proto.StructureProto.LabProto;
import com.lvl6.proto.StructureProto.MiniJobCenterProto;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.MoneyTreeProto;
import com.lvl6.proto.StructureProto.ObstacleProto;
import com.lvl6.proto.StructureProto.PvpBoardHouseProto;
import com.lvl6.proto.StructureProto.PvpBoardObstacleProto;
import com.lvl6.proto.StructureProto.ResearchHouseProto;
import com.lvl6.proto.StructureProto.ResidenceProto;
import com.lvl6.proto.StructureProto.ResourceGeneratorProto;
import com.lvl6.proto.StructureProto.ResourceStorageProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.proto.StructureProto.StructureInfoProto;
import com.lvl6.proto.StructureProto.StructureInfoProto.StructType;
import com.lvl6.proto.StructureProto.TeamCenterProto;
import com.lvl6.proto.StructureProto.TownHallProto;
import com.lvl6.proto.StructureProto.TutorialStructProto;
import com.lvl6.proto.StructureProto.UserObstacleProto;
import com.lvl6.proto.StructureProto.UserPvpBoardObstacleProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.MinimumUserTaskProto;
import com.lvl6.proto.TaskProto.PersistentEventProto;
import com.lvl6.proto.TaskProto.PersistentEventProto.EventType;
import com.lvl6.proto.TaskProto.TaskMapElementProto;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto;
import com.lvl6.proto.TaskProto.TaskStageMonsterProto.MonsterType;
import com.lvl6.proto.TaskProto.TaskStageProto;
import com.lvl6.proto.TaskProto.UserPersistentEventProto;
import com.lvl6.proto.TaskProto.UserTaskCompletedProto;
import com.lvl6.proto.UserProto.FullUserProto;
import com.lvl6.proto.UserProto.MinimumClanProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.MinimumUserProtoWithLevel;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.proto.UserProto.UserPvpLeagueProto;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanHelpCountForUserRetrieveUtil.UserClanHelpCount;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils.UserTaskCompleted;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
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
				log.error(String.format(
					"invalid AchievementType. achievement=%s", a), e);
			}
		}

		str = a.getResourceType();
		if (null != str) {
			try {
				ResourceType rt = ResourceType.valueOf(str);
				ab.setResourceType(rt);
			} catch(Exception e) {
				log.error(String.format(
					"invalid ResourceType. achievement=%s", a), e);
			}
		}

		str = a.getMonsterElement();
		if (null != str) {
			try {
				Element me = Element.valueOf(str);
				ab.setElement(me);
			} catch(Exception e) {
				log.error(String.format(
					"invalid MonsterElement. achievement=%s", a), e);
			}
		}

		str = a.getMonsterQuality();
		if (null != str) {
			try {
				Quality mq = Quality.valueOf(str);
				ab.setQuality(mq);
			} catch(Exception e) {
				log.error(String.format(
					"invalid MonsterQuality. achievement=%s", a), e);
			}
		}

		ab.setStaticDataId(a.getStaticDataId());
		ab.setQuantity(a.getQuantity());
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

	public static PvpProto createPvpProto(User defender, Clan clan,
		PvpLeagueForUser plfu, PvpUser pu,
		Collection<MonsterForUser> userMonsters,
		Map<String, Integer> userMonsterIdToDropped,
		int prospectiveCashWinnings, int prospectiveOilWinnings,
		ClanMemberTeamDonation cmtd, MonsterSnapshotForUser msfu,
		int msfuMonsterIdDropped)
	{

		MinimumUserProtoWithLevel defenderProto =
			createMinimumUserProtoWithLevel(defender, clan, null);
		String userId = defender.getId();
		String msg = defender.getPvpDefendingMessage();
		return createPvpProto(userId, plfu, pu, userMonsters,
			userMonsterIdToDropped, prospectiveCashWinnings,
			prospectiveOilWinnings, defenderProto, msg, cmtd, msfu,
			msfuMonsterIdDropped);
	}

	public static PvpProto createPvpProto(
		String defenderId,
		PvpLeagueForUser plfu,
		PvpUser pu,
		Collection<MonsterForUser> userMonsters,
		Map<String, Integer> userMonsterIdToDropped,
		int prospectiveCashWinnings,
		int prospectiveOilWinnings,
		MinimumUserProtoWithLevel defender,
		String defenderMsg,
		ClanMemberTeamDonation cmtd,
		MonsterSnapshotForUser msfu,
		int msfuMonsterIdDropped)
	{
		PvpProto.Builder ppb = PvpProto.newBuilder();
		
		Collection<PvpMonsterProto> defenderMonsters = 
			createPvpMonsterProto(userMonsters, userMonsterIdToDropped);
		ppb.addAllDefenderMonsters(defenderMonsters);

		ppb.setDefender(defender);
		ppb.setProspectiveCashWinnings(prospectiveCashWinnings);
		ppb.setProspectiveOilWinnings(prospectiveOilWinnings);

		UserPvpLeagueProto uplp = 
			createUserPvpLeagueProto(defenderId, plfu, pu, true);
		ppb.setPvpLeagueStats(uplp);

		
		if (null != defenderMsg)
		{
			ppb.setDefenderMsg(defenderMsg);
		}
		
		//account for clan donated monster
		if (null != cmtd && null != msfu) {
			ClanMemberTeamDonationProto.Builder cmtdpb =
				ClanMemberTeamDonationProto.newBuilder();
			cmtdpb.setDonationUuid(cmtd.getId());
			cmtdpb.setClanUuid(cmtd.getClanId());
			
			UserMonsterSnapshotProto umsp = 
				createUserMonsterSnapshotProto(msfu, null);
			
			cmtdpb.addDonations(umsp);
			ppb.setCmtd(cmtdpb.build());
			if (msfuMonsterIdDropped > 0) {
				ppb.setMonsterIdDropped(msfuMonsterIdDropped);
			}
		}
		
		return ppb.build();
	}
	
	public static Collection<PvpMonsterProto> createPvpMonsterProto(
		Collection<MonsterForUser> userMonsters,
		Map<String, Integer> userMonsterIdToDropped)
	{
		List<PvpMonsterProto> pmpList = new ArrayList<PvpMonsterProto>();
		
		for (MonsterForUser mfu : userMonsters)
		{
			String userMonsterId = mfu.getId();
			MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfu);
			
			PvpMonsterProto pmp =
				createPvpMonsterProto(userMonsterIdToDropped, userMonsterId, mump);
			
			pmpList.add(pmp);
		}
		return pmpList;
	}

	private static PvpMonsterProto createPvpMonsterProto(
		Map<String, Integer> userMonsterIdToDropped,
		String userMonsterId,
		MinimumUserMonsterProto mump )
	{
		PvpMonsterProto.Builder pmpb = PvpMonsterProto.newBuilder();
		pmpb.setDefenderMonster(mump);
		
		if (userMonsterIdToDropped.containsKey(userMonsterId)) {
			Integer idDropped = userMonsterIdToDropped.get(userMonsterId);
			
			if (idDropped > 0) {
				pmpb.setMonsterIdDropped(idDropped);
			}
		}
		return pmpb.build();
	}
	
	private static Collection<PvpMonsterProto> createPvpMonsterProto(
		List<MonsterForPvp> mfpList,
		List<Integer> monsterIdsDropped )
	{
		Collection<PvpMonsterProto> pmpList = new ArrayList<PvpMonsterProto>();
		for (int i = 0; i < mfpList.size(); i++) {
			MonsterForPvp mfp = mfpList.get(i);
			MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfp);
			
			Integer monsterIdDropped = monsterIdsDropped.get(i);

			PvpMonsterProto pmp = createPvpMonsterProto(mump, monsterIdDropped);
			pmpList.add(pmp);
		}
		return pmpList;
	}

	private static PvpMonsterProto createPvpMonsterProto(
		MinimumUserMonsterProto mump,
		Integer monsterIdDropped )
	{
		PvpMonsterProto.Builder pmpb = PvpMonsterProto.newBuilder();
		pmpb.setDefenderMonster(mump);
		if (monsterIdDropped > 0) {
			pmpb.setMonsterIdDropped(monsterIdDropped);
		}
		PvpMonsterProto pmp = pmpb.build();
		
		return pmp;
	}
	
//	private static PvpMonsterProto createPvpMonsterProto(
//		MonsterSnapshotForUser msfu, int monsterIdDropped)
//	{
//		PvpMonsterProto.Builder pmpb = PvpMonsterProto.newBuilder();
//		MinimumUserMonsterProto mump = createMinimumUserMonsterProto(msfu);
//		pmpb.setDefenderMonster(mump);
//		if (monsterIdDropped > 0) {
//			pmpb.setMonsterIdDropped(monsterIdDropped);
//		}
//		PvpMonsterProto pmp = pmpb.build();
//		
//		return pmp;
//
//	}
	
	//this is used to create fake users for PvpProtos
	public static PvpProto createFakePvpProto(String userId, String name,
		int lvl, int elo, int prospectiveCashWinnings,
		int prospectiveOilWinnings, List<MonsterForPvp> mfpList,
		List<Integer> monsterIdsDropped, boolean setElo)
	{

		//create the fake user
		MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
		//mupb.setUserUuid(userId); // fake user will never have id
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
		Collection<PvpMonsterProto> pmpList =
			createPvpMonsterProto(mfpList, monsterIdsDropped);
		ppb.addAllDefenderMonsters(pmpList);

		ppb.setProspectiveCashWinnings(prospectiveCashWinnings);
		ppb.setProspectiveOilWinnings(prospectiveOilWinnings);

		UserPvpLeagueProto uplp = createFakeUserPvpLeagueProto(
			userId, elo, setElo);
		ppb.setPvpLeagueStats(uplp);

		return ppb.build();
	}

	public static List<PvpProto> createPvpProtos(List<User> queuedOpponents,
		Map<String, Clan> userIdToClan,
		Map<String, PvpLeagueForUser> userIdToLeagueInfo,
		Map<String, PvpUser> userIdToPvpUser,
		Map<String, List<MonsterForUser>> userIdToUserMonsters,
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDropped,
		Map<String, Integer> userIdToCashReward,
		Map<String, Integer> userIdToOilReward,
		Map<String, ClanMemberTeamDonation> userIdToCmtd,
		Map<String, MonsterSnapshotForUser> userIdToMsfu,
		Map<String, Integer> userIdToMsfuMonsterIdDropped)
	{
		List<PvpProto> pvpProtoList = new ArrayList<PvpProto>();

		for (User u : queuedOpponents) {
			String userId = u.getId();
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

			Clan clan = null;
			if (userIdToClan.containsKey(userId)) {
				clan = userIdToClan.get(userId);
			}

			Map<String, Integer> userMonsterIdToDropped =
				userIdToUserMonsterIdToDropped.get(userId);
			
			
			ClanMemberTeamDonation cmtd = null;
			if (null != userIdToCmtd && userIdToCmtd.containsKey(userId)) {
				cmtd = userIdToCmtd.get(userId);
			}
			MonsterSnapshotForUser msfu = null;
			if (null != userIdToMsfu && userIdToMsfu.containsKey(userId)) {
				msfu = userIdToMsfu.get(userId);
			}
			int msfuMonsterIdDropped = 0;
			if (null != userIdToMsfuMonsterIdDropped &&
				userIdToMsfuMonsterIdDropped.containsKey(userId))
			{
				msfuMonsterIdDropped = userIdToMsfuMonsterIdDropped.get(userId);
			}
			
			PvpProto pp = createPvpProto(u, clan, plfu, pu, userMonsters,
				userMonsterIdToDropped, prospectiveCashWinnings,
				prospectiveOilWinnings, cmtd, msfu,
				msfuMonsterIdDropped);
			pvpProtoList.add(pp);
		}
		return pvpProtoList;
		}

	public static PvpHistoryProto createGotAttackedPvpHistoryProto(User attacker, Clan c,
		PvpBattleHistory info, Collection<MonsterForUser> userMonsters,
		Map<String, Integer> userMonsterIdToDropped,
		int prospectiveCashWinnings, int prospectiveOilWinnings)
	{
		PvpHistoryProto.Builder phpb = PvpHistoryProto.newBuilder();
		FullUserProto fup = createFullUserProtoFromUser(attacker, null,
			c);
		phpb.setAttacker(fup);

		if (null != userMonsters && !userMonsters.isEmpty()) {
			Collection<PvpMonsterProto> attackerMonsters = 
				createPvpMonsterProto(userMonsters, userMonsterIdToDropped);
			phpb.addAllAttackersMonsters(attackerMonsters);
		}
		phpb.setProspectiveCashWinnings(prospectiveCashWinnings);
		phpb.setProspectiveOilWinnings(prospectiveOilWinnings);

		modifyPvpHistoryProto(phpb, info);
		return phpb.build();
	}

	public static List<PvpHistoryProto> createGotAttackedPvpHistoryProto(
		List<PvpBattleHistory> historyList, Map<String, User> attackerIdsToAttackers,
		Map<String, Clan> attackerIdsToClans,
		Map<String, List<MonsterForUser>> attackerIdsToUserMonsters,
		Map<String, Map<String, Integer>> userIdToUserMonsterIdToDropped,
		Map<String, Integer> attackerIdsToProspectiveCashWinnings,
		Map<String, Integer> attackerIdsToProspectiveOilWinnings) {

		List<PvpHistoryProto> phpList = new ArrayList<PvpHistoryProto>();

		for (PvpBattleHistory history: historyList) {
			String attackerId = history.getAttackerId();

			User attacker = attackerIdsToAttackers.get(attackerId);
			List<MonsterForUser> attackerMonsters = attackerIdsToUserMonsters.get(attackerId);
			int prospectiveCashWinnings = attackerIdsToProspectiveCashWinnings.get(attackerId);
			int prospectiveOilWinnings = attackerIdsToProspectiveOilWinnings.get(attackerId);

			String clanId = attacker.getClanId();
			Clan clan = null;
			if (attackerIdsToClans.containsKey(clanId)) {
				clan = attackerIdsToClans.get(clanId);
			}
			
			Map<String, Integer> userMonsterIdToDropped =
				userIdToUserMonsterIdToDropped.get(attackerId);
			PvpHistoryProto php = createGotAttackedPvpHistoryProto(attacker,
				clan, history, attackerMonsters, userMonsterIdToDropped,
				prospectiveCashWinnings, prospectiveOilWinnings);
			phpList.add(php);
		}
		return phpList;
	}
	
	public static List<PvpHistoryProto> createAttackedOthersPvpHistoryProto(
		String attackerId, Map<String, User> idsToUsers,
		List<PvpBattleHistory> historyList)
	{
		List<PvpHistoryProto> phpList = new ArrayList<PvpHistoryProto>();
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		fupb.setUserUuid(attackerId);
		FullUserProto fup = fupb.build();
		
		for (PvpBattleHistory pbh : historyList)
		{
			//no fake users are displayed, but check in case
			String defenderId = pbh.getDefenderId();
			if (null == defenderId || defenderId.isEmpty()) {
				continue;
			}
			
			User defender = idsToUsers.get(defenderId);
			FullUserProto defenderFup = createFullUserProtoFromUser(defender, null, null);
			PvpHistoryProto php = createAttackedOthersPvpHistoryProto(
				fup, defenderFup, pbh);
			
			phpList.add(php);
		}
		
		return phpList;
	}
	
	public static PvpHistoryProto createAttackedOthersPvpHistoryProto(
		FullUserProto fup, FullUserProto defenderFup,
		PvpBattleHistory info)
	{
		PvpHistoryProto.Builder phpb = PvpHistoryProto.newBuilder();
		phpb.setAttacker(fup);
		phpb.setDefender(defenderFup);
		
		modifyPvpHistoryProto(phpb, info);
		return phpb.build();
	}

	private static void modifyPvpHistoryProto(Builder phpb, PvpBattleHistory info) {
		phpb.setAttackerWon(info.isAttackerWon());

		int defenderCashChange = info.getDefenderCashChange();
		phpb.setDefenderCashChange(defenderCashChange);
		int defenderOilChange = info.getDefenderOilChange();
		phpb.setDefenderOilChange(defenderOilChange);

		phpb.setExactedRevenge(info.isExactedRevenge());

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

		int attackerCashChange = info.getAttackerCashChange();
		phpb.setAttackerCashChange(attackerCashChange);
		int attackerOilChange = info.getAttackerOilChange();
		phpb.setAttackerOilChange(attackerOilChange);
		
		phpb.setClanAvenged(info.isClanAvenged());
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

		aStr = pl.getDescription();
		if (null != aStr) {
			plpb.setDescription(aStr);
		}

		return plpb.build();
	}

	public static UserPvpLeagueProto createUserPvpLeagueProto(String userId,
		PvpLeagueForUser plfu, PvpUser pu, boolean setElo) {
		UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
		uplpb.setUserUuid(userId);

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

			uplpb.setMonsterDmgMultiplier(plfu.getMonsterDmgMultiplier());

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

			uplpb.setMonsterDmgMultiplier(pu.getMonsterDmgMultiplier());

		}


		return uplpb.build();
	}

	public static UserPvpLeagueProto createUserPvpLeagueProto(String userId, int pvpLeagueId,
		int rank, int elo, boolean setElo) {
		UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
		uplpb.setUserUuid(userId);
		uplpb.setLeagueId(pvpLeagueId);
		uplpb.setRank(rank);

		if (setElo) {
			uplpb.setElo(elo);
		}

		return uplpb.build();
	}

	public static UserPvpLeagueProto createFakeUserPvpLeagueProto(
		String userId, int elo, boolean setElo)
	{
		UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
		//uplpb.setUserUuid(userId);

		int leagueId = PvpLeagueRetrieveUtils.getLeagueIdForElo(elo, 0);
		uplpb.setLeagueId(leagueId);
		int rank = PvpLeagueRetrieveUtils.getRankForElo(elo, leagueId);
		uplpb.setRank(rank);

		uplpb.setMonsterDmgMultiplier(ControllerConstants.PVP__MONSTER_DMG_MULTIPLIER);

		if (setElo) {
			uplpb.setElo(elo);
		}
		
		return uplpb.build();
	}
	
	public static List<PvpClanAvengeProto> createPvpClanAvengeProto(
		List<ClanAvenge> retaliations,
		Map<String, List<ClanAvengeUser>> clanAvengeIdToClanAvengeUser,
		Map<String, User> userIdsToUsers,
		Map<String, Clan> userIdsToClans)
	{
		List<PvpClanAvengeProto> pcapList = new ArrayList<PvpClanAvengeProto>();
		
		Map<String, MinimumUserProtoWithLevel> userIdToMupwl =
			createMinimumUserProtoWithLevel(userIdsToUsers, userIdsToClans);
		
		for (ClanAvenge ca : retaliations)
		{
			String clanAvengeId = ca.getId();
			List<ClanAvengeUser> cauList = null;
			
			if (clanAvengeIdToClanAvengeUser.containsKey(clanAvengeId))
			{
				cauList = clanAvengeIdToClanAvengeUser
					.get(clanAvengeId);
			}
			
			PvpClanAvengeProto pcap = createPvpClanAvengeProto(ca, cauList, userIdToMupwl);
			pcapList.add(pcap);
		}
		return pcapList;
	}
	
	public static PvpClanAvengeProto createPvpClanAvengeProto(
		ClanAvenge ca, List<ClanAvengeUser> cauList,
		Map<String, MinimumUserProtoWithLevel> userIdToMupwl)
	{
		String attackerId = ca.getAttackerId();
		String defenderId = ca.getDefenderId();
		String defenderClanUuid = ca.getClanId();
		
		MinimumUserProtoWithLevel attacker = userIdToMupwl
			.get(attackerId);
		MinimumUserProtoWithLevel defender = userIdToMupwl
			.get(defenderId);
		
		PvpClanAvengeProto.Builder pcapb = PvpClanAvengeProto.newBuilder();
		pcapb.setClanAvengeUuid(ca.getId());
		pcapb.setAttacker(attacker);
		pcapb.setDefender(defender.getMinUserProto());
		
		Date time = ca.getBattleEndTime();
		pcapb.setBattleEndTime(time.getTime());
		
		time = ca.getAvengeRequestTime();
		pcapb.setAvengeRequestTime(time.getTime());
		
		pcapb.setDefenderClanUuid(defenderClanUuid);
	
		//could be that no clan mate started retaliating
		//against person who attacked clan member
		if (null != cauList && !cauList.isEmpty())
		{
			List<PvpUserClanAvengeProto> pucapList =
				createPvpUserClanAvengeProto(cauList);
			pcapb.addAllUsersAvenging(pucapList);
		}
		
		return pcapb.build();
	}
	
	public static List<PvpUserClanAvengeProto> createPvpUserClanAvengeProto(
		List<ClanAvengeUser> cauList )
	{
		List<PvpUserClanAvengeProto> pucapList = new
			ArrayList<PvpUserClanAvengeProto>();
		
		for (ClanAvengeUser cau : cauList)
		{
			PvpUserClanAvengeProto pucap =
				createPvpUserClanAvengeProto(cau);
			pucapList.add(pucap);
		}
		
		return pucapList;
	}
	
	public static PvpUserClanAvengeProto createPvpUserClanAvengeProto(
		ClanAvengeUser cau)
	{
		PvpUserClanAvengeProto.Builder pucapb =
			PvpUserClanAvengeProto.newBuilder();
		
		pucapb.setUserUuid(cau.getUserId());
		pucapb.setClanAvengeUuid(cau.getClanAvengeId());
		pucapb.setClanUuid(cau.getClanId());
		
		Date date = cau.getAvengeTime();
		pucapb.setAvengeTime(date.getTime());
		
		return pucapb.build();
	}
	
	public static List<PvpClanAvengeProto> createPvpClanAvengeProto(
		List<ClanAvenge> retaliations, MinimumUserProto defenderMup,
		String clanUuid,
		Map<String, MinimumUserProtoWithLevel> attackerIdsToMupwls)
	{
		List<PvpClanAvengeProto> pcapList =
			new ArrayList<PvpClanAvengeProto>();
		
		for (ClanAvenge ca : retaliations)
		{
			String attackerId = ca.getAttackerId();
			MinimumUserProtoWithLevel attackerMupwl = attackerIdsToMupwls
				.get(attackerId);
			
			PvpClanAvengeProto pcap = createPvpClanAvengeProto(
				attackerMupwl, defenderMup, clanUuid, ca);
			
			pcapList.add(pcap);
		}
		return pcapList;
	}
	
	public static PvpClanAvengeProto createPvpClanAvengeProto(
		MinimumUserProtoWithLevel attacker, MinimumUserProto defender,
		String defenderClanUuid, ClanAvenge ca)
	{
		PvpClanAvengeProto.Builder pcapb = PvpClanAvengeProto.newBuilder();
		pcapb.setClanAvengeUuid(ca.getId());
		pcapb.setAttacker(attacker);
		pcapb.setDefender(defender);
		
		Date time = ca.getBattleEndTime();
		pcapb.setBattleEndTime(time.getTime());
		
		time = ca.getAvengeRequestTime();
		pcapb.setAvengeRequestTime(time.getTime());
		
		pcapb.setDefenderClanUuid(defenderClanUuid);
	
		return pcapb.build();
	}
	
	//battle item queue proto
	public static BattleItemQueueForUserProto createBattleItemQueueForUserProto(BattleItemQueueForUser biqfu) {
		BattleItemQueueForUserProto.Builder biqfupb = BattleItemQueueForUserProto.newBuilder();
		biqfupb.setUserUuid(biqfu.getUserId());
		biqfupb.setBattleItemId(biqfu.getBattleItemId());
		biqfupb.setExpectedStartTime(biqfu.getExpectedStartTime().getTime());
		biqfupb.setPriority(biqfu.getPriority());
		
		return biqfupb.build();
	}
	

	/**Board.proto****************************************/
	public static BoardLayoutProto createBoardLayoutProto(Board b,
		Collection<BoardProperty> boardProperties)
	{
		BoardLayoutProto.Builder blpb = BoardLayoutProto.newBuilder();
		
		blpb.setBoardId(b.getId());
		blpb.setHeight(b.getHeight());
		blpb.setWidth(b.getWidth());
		
//		String str = b.getOrbElements();
//		if (null != str && !str.isEmpty())
//		{
//			try {
//				Element elem = Element.valueOf(str);
//				blpb.setOrbElements(elem);
//			} catch (Exception e) {
//				log.error(String.format(
//					"invalid element. Board=%s", b),
//					e);
//			}
//		}
		int elements = b.getOrbElements();
		if (elements > 0) {
			blpb.setOrbElements(elements);
		}
		
		if (null != boardProperties) {
			List<BoardPropertyProto> bpList = createBoardPropertyProto(boardProperties);
			blpb.addAllProperties(bpList);
		}
		
		return blpb.build();
	}
	
	public static List<BoardPropertyProto> createBoardPropertyProto(
		Collection<BoardProperty> bpCollection)
	{
		List<BoardPropertyProto> retVal = new ArrayList<BoardPropertyProto>();
		for (BoardProperty bp : bpCollection)
		{
			BoardPropertyProto bpp = createBoardPropertyProto(bp);
			
			retVal.add(bpp);
		}
		
		return retVal;
	}
	
	public static BoardPropertyProto createBoardPropertyProto(BoardProperty bp)
	{
		BoardPropertyProto.Builder blpb = BoardPropertyProto.newBuilder();
		
		blpb.setBoardPropertyId(bp.getId());
		blpb.setBoardId(bp.getId());
		
		String str = bp.getName();
		if (null != str && !str.isEmpty()){
			blpb.setName(str);
		}
		
		blpb.setPosX(bp.getPosX());
		blpb.setPosY(bp.getPosY());
		
		str = bp.getElement();
		if (null != str && !str.isEmpty())
		{
			try {
				Element elem = Element.valueOf(str);
				blpb.setElem(elem);
			} catch (Exception e) {
				log.error(String.format(
					"invalid element. BoardProperty=%s", bp),
					e);
			}
		}
		blpb.setValue(bp.getValue());
		blpb.setQuantity(bp.getQuanity());
		
		return blpb.build();
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

		str = bp.getType();
		if (null != str) {
			try {
				BoosterPackType bpt= BoosterPackType.valueOf(str);
				b.setType(bpt);
			} catch (Exception e){
				log.error(String.format(
					"invalid BoosterPackType. BoosterPack=%s", bp),
					e);
			}
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
		b.setItemId(bi.getItemId());
		b.setItemQuantity(bi.getItemQuantity());
		
		return b.build();
	}

	public static BoosterDisplayItemProto createBoosterDisplayItemProto(
		BoosterDisplayItem bdi)
	{
		BoosterDisplayItemProto.Builder b = BoosterDisplayItemProto.newBuilder();

		b.setBoosterPackId(bdi.getBoosterPackId());
		b.setIsMonster(bdi.isMonster());
		b.setIsComplete(bdi.isComplete());

		String monsterQuality = bdi.getMonsterQuality();
		if (null != monsterQuality) {
			try {
				Quality mq = Quality.valueOf(monsterQuality);
				b.setQuality(mq);
			} catch (Exception e){
				log.error(String.format(
					"invalid monster quality. boosterDisplayItem=%s", bdi),
					e);
			}
		}

		b.setGemReward(bdi.getGemReward());
		b.setQuantity(bdi.getQuantity());
		b.setItemId(bdi.getItemId());
		b.setItemQuantity(bdi.getItemQuantity());
		
		return b.build();
	}

	/**Chat.proto*****************************************************/
	public static PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPost (
		PrivateChatPost p, User poster, Clan posterClan,
		User recipient, Clan recipientClan)
	{
		MinimumUserProtoWithLevel mupwlPoster = createMinimumUserProtoWithLevel(poster, posterClan, null); 
		MinimumUserProtoWithLevel mupwlRecipient = createMinimumUserProtoWithLevel(recipient, recipientClan, null);

		// Truncate time because db truncates it (?)
		long time = p.getTimeOfPost().getTime();
		time = time - time % 1000;

		PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();
		pcppb.setPrivateChatPostUuid(p.getId());
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

		pcppb.setPrivateChatPostUuid(p.getId());
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
	public static List<PrivateChatPostProto> createPrivateChatPostProtoList (Map<String, Clan> clanIdsToClans,
		Map<String, Set<String>> clanIdsToUserIdSet, Map<String, User> userIdsToUsers,
		List<String> clanlessUserIds, List<String> privateChatPostIds,
		Map<String, PrivateChatPost> postIdsToPrivateChatPosts) {

		List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();
		Map<String, MinimumUserProtoWithLevel> userIdToMinimumUserProtoWithLevel =
			new HashMap<String, MinimumUserProtoWithLevel>();
		//construct the minimum user protos for the users that have clans
		//and the clanless users
		createMinimumUserProtosFromClannedAndClanlessUsers(clanIdsToClans, clanIdsToUserIdSet,
			clanlessUserIds, userIdsToUsers, userIdToMinimumUserProtoWithLevel);

		//now actually construct the PrivateChatPostProtos
		if (null != privateChatPostIds && !privateChatPostIds.isEmpty()) {
			//only pick out a subset of postIdsToPrivateChatPosts
			for (String postId : privateChatPostIds) {
				PrivateChatPost pcp = postIdsToPrivateChatPosts.get(postId);
				String posterId = pcp.getPosterId();
				String recipientId = pcp.getRecipientId();

				MinimumUserProtoWithLevel mupwlPoster = userIdToMinimumUserProtoWithLevel.get(posterId);
				MinimumUserProtoWithLevel mupwlRecipient = userIdToMinimumUserProtoWithLevel.get(recipientId);

				PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
					pcp, mupwlPoster, mupwlRecipient);
				pcppList.add(pcpp);
			}
		} else {
			for (PrivateChatPost pcp : postIdsToPrivateChatPosts.values()) {
				String posterId = pcp.getPosterId();
				String recipientId = pcp.getRecipientId();
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
		ClanChatPost p, User user, Clan clan) {
		GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto.newBuilder();
		gcmpb.setSender(createMinimumUserProtoWithLevel(user, clan, null));
		gcmpb.setTimeOfChat(p.getTimeOfPost().getTime());
		gcmpb.setContent(p.getContent());
		return gcmpb.build();
	}

	public static GroupChatMessageProto createGroupChatMessageProto(long time, MinimumUserProtoWithLevel user, String content, boolean isAdmin, String chatId) {
		GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto.newBuilder();

		gcmpb.setSender(user);
		gcmpb.setTimeOfChat(time);
		gcmpb.setContent(content);
		gcmpb.setIsAdmin(isAdmin);
		
		if (chatId != null) {
	    gcmpb.setChatUuid(chatId);
		}
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

//	public static UserCityExpansionDataProto createUserCityExpansionDataProtoFromUserCityExpansionData(ExpansionPurchaseForUser uced) {
//		UserCityExpansionDataProto.Builder builder = UserCityExpansionDataProto.newBuilder().setUserId(uced.getUserId())
//			.setXPosition(uced.getxPosition()).setYPosition(uced.getyPosition()).setIsExpanding(uced.isExpanding());
//		if (uced.getExpandStartTime() != null) {
//			builder.setExpandStartTime(uced.getExpandStartTime().getTime());
//		}
//		return builder.build();
//	}

//	public static CityExpansionCostProto createCityExpansionCostProtoFromCityExpansionCost(ExpansionCost ec) {
//		CityExpansionCostProto.Builder builder = CityExpansionCostProto.newBuilder();
//		builder.setExpansionNum(ec.getId());
//		builder.setExpansionCostCash(ec.getExpansionCostCash());
//		builder.setNumMinutesToExpand(ec.getNumMinutesToExpand());
//		return builder.build();
//	}

//	public static CityElementProto createCityElementProtoFromCityElement(CityElement ce) {
//		CityElementProto.Builder builder = CityElementProto.newBuilder();
//		builder.setCityId(ce.getCityId());
//		builder.setAssetId(ce.getAssetId());
//		//    builder.setName(nce.getGoodName());
//
//		try {
//			CityElemType cet = CityElemType.valueOf(ce.getType());
//			builder.setType(cet);
//		} catch (Exception e) {
//			log.error(String.format(
//				"incorrect element type. cityElement=%s", ce), e);
//		}
//		builder.setCoords(createCoordinateProtoFromCoordinatePair(ce.getCoords()));
//
//		if (ce.getxLength() > 0) {
//			builder.setXLength(ce.getxLength());
//		}
//		if (ce.getyLength() > 0) {
//			builder.setYLength(ce.getyLength());
//		}
//		builder.setImgId(ce.getImgGood());
//
//		try {
//			StructOrientation so = StructOrientation.valueOf(ce.getOrientation()); 
//			builder.setOrientation(so);
//		} catch (Exception e) {
//			log.error(String.format(
//				"incorrect orientation. cityElement=%s", ce), e);
//		}
//
//		builder.setSpriteCoords(createCoordinateProtoFromCoordinatePair(ce.getSpriteCoords()));
//
//		return builder.build();
//	}

//	public static FullCityProto createFullCityProtoFromCity(City c) {
//		FullCityProto.Builder builder = FullCityProto.newBuilder();
//		builder.setCityId(c.getId());
//		builder.setName(c.getName());
//		builder.setMapImgName(c.getMapImgName());
//		builder.setCenter(createCoordinateProtoFromCoordinatePair(c.getCenter()));
//		List<Task> tasks = TaskRetrieveUtils.getAllTasksForCityId(c.getId());
//		if (tasks != null) {
//			for (Task t : tasks) {
//				builder.addTaskIds(t.getId());
//			}
//		}
//
//		String roadImgName = c.getRoadImgName();
//		if (null != roadImgName) {
//			builder.setRoadImgName(roadImgName);
//		}
//
//		String mapTmxName = c.getMapTmxName();
//		if (null != mapTmxName) {
//			builder.setMapTmxName(mapTmxName);
//		}
//
//		builder.setRoadImgCoords(createCoordinateProtoFromCoordinatePair(c.getRoadImgCoords()));
//		String atkMapLabelImgName = c.getAttackMapLabelImgName();
//		if (null != atkMapLabelImgName) {
//			builder.setAttackMapLabelImgName(c.getAttackMapLabelImgName());
//		}
//
//		return builder.build();
//	}

	/**Clan.proto*****************************************************/
	public static FullClanProto createFullClanProtoFromClan(Clan c) {
		//    MinimumUserProto mup = createMinimumUserProtoFromUser(RetrieveUtils.userRetrieveUtils().getUserById(c.getOwnerId()));
		FullClanProto.Builder fcpb= FullClanProto.newBuilder();
		fcpb.setClanUuid(c.getId());
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
		fucpb.setClanUuid(uc.getClanId());
		fucpb.setUserUuid(uc.getUserId());
		String userClanStatus = uc.getStatus();

		try {
			UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
			fucpb.setStatus(ucs);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect user clan status. userClan=%s", uc), e);
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
		Clan clan, String userClanStatus, float clanRaidContribution, int battlesWon,
		UserClanHelpCount uchc)
	{
		MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevel(u, clan, null);

		MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans.newBuilder();
		mupfcb.setMinUserProtoWithLevel(mupwl);

		try {
			UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
			mupfcb.setClanStatus(ucs);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect userClanStatus: %s, user=%s",
				userClanStatus, u), e);
		}
		mupfcb.setRaidContribution(clanRaidContribution);
		mupfcb.setBattlesWon(battlesWon);
		
		if (null != uchc) {
			mupfcb.setNumClanHelpsSolicited(uchc.getNumSolicited());
			mupfcb.setNumClanHelpsGiven(uchc.getNumGiven());
		}
		
		MinimumUserProtoForClans mupfc = mupfcb.build();
		return mupfc;
	}

	public static MinimumUserProtoForClans createMinimumUserProtoForClans(User u,
		Clan clan, UserClanStatus userClanStatus, float clanRaidContribution,
		int battlesWon, UserClanHelpCount uchc )
	{
		MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevel(u, clan, null);

		MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans.newBuilder();
		mupfcb.setMinUserProtoWithLevel(mupwl);

		mupfcb.setClanStatus(userClanStatus);
		mupfcb.setRaidContribution(clanRaidContribution);
		mupfcb.setBattlesWon(battlesWon);
		if (null != uchc) {
			mupfcb.setNumClanHelpsSolicited(uchc.getNumSolicited());
			mupfcb.setNumClanHelpsGiven(uchc.getNumGiven());
		}
		
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
			log.error(String.format(
				"incorrect DayOfWeek: %s, clanEvent=%s",
				dayOfWeekStr, cep), e);
		}


		pcepb.setStartHour(cep.getStartHour());
		pcepb.setEventDurationMinutes(cep.getEventDurationMinutes());
		pcepb.setClanRaidId(cep.getClanRaidId());

		return pcepb.build();
	}

	public static PersistentClanEventClanInfoProto createPersistentClanEventClanInfoProto(
		ClanEventPersistentForClan cepfc) {
		PersistentClanEventClanInfoProto.Builder pcecipb = PersistentClanEventClanInfoProto.newBuilder();
		pcecipb.setClanUuid(cepfc.getClanId());
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
		ClanEventPersistentForUser cepfu, Map<String, MonsterForUser> idsToUserMonsters,
		List<FullUserMonsterProto> fumpList){
		PersistentClanEventUserInfoProto.Builder pceuipb = PersistentClanEventUserInfoProto.newBuilder();
		String userId = cepfu.getUserId();
		pceuipb.setUserUuid(userId);
		pceuipb.setClanUuid(cepfu.getClanId());

		pceuipb.setCrId(cepfu.getCrId());
		pceuipb.setCrDmgDone(cepfu.getCrDmgDone());

		//  	pceuipb.setCrsId(cepfu.getCrsId());
		pceuipb.setCrsDmgDone(cepfu.getCrsDmgDone());

		//  	pceuipb.setCrsmId(cepfu.getCrsmId());
		pceuipb.setCrsmDmgDone(cepfu.getCrsmDmgDone());

		UserCurrentMonsterTeamProto.Builder ucmtpb = UserCurrentMonsterTeamProto.newBuilder();
		ucmtpb.setUserUuid(userId);

		if (null == fumpList || fumpList.isEmpty()) {
			List<String> userMonsterIds = cepfu.getUserMonsterIds();

			for (String userMonsterId : userMonsterIds) {

				if (!idsToUserMonsters.containsKey(userMonsterId)) {
					//user no longer has this monster, probably sold
					//create fake user monster proto
					FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
					fumpb.setUserMonsterUuid(userMonsterId);
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

		pceurpb.setRewardUuid(reward.getId());
		pceurpb.setUserUuid(reward.getUserId());

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
				log.info(String.format(
					"incorrect resource type. ClanEventPersistentUserReward=%s",
					reward), e);
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
		pcerhpb.setUserUuid(cepfurh.getUserId());
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

	public static ClanHelpProto createClanHelpProtoFromClanHelp(ClanHelp ch,
		User u, Clan c, MinimumUserProto mup)
	{
		ClanHelpProto.Builder chpb = ClanHelpProto.newBuilder();
		chpb.setClanHelpUuid(ch.getId());
		chpb.setClanUuid(ch.getClanId());

		if (null == mup) {
			mup = createMinimumUserProtoFromUserAndClan(u, c);
		}
		chpb.setMup(mup);
		chpb.setUserDataUuid(ch.getUserDataId());

		String helpType = ch.getHelpType();

		if ( null != helpType ) {
			try {
				GameActionType cht = GameActionType.valueOf(helpType);
				chpb.setHelpType(cht);

			} catch(Exception e) {
				log.info( String.format(
					"incorrect GameActionType. ClanHelp=%s", ch ));
			}
		}
		chpb.setTimeRequested(ch.getTimeOfEntry().getTime());
		//	  log.info(String.format(
		//			"ClanHelp=%s TimeOfEntry=%s", ch, ch.getTimeOfEntry()));

		chpb.setMaxHelpers(ch.getMaxHelpers());

		if (null != ch.getHelpers()) {
			chpb.addAllHelperUuids(ch.getHelpers());
		}
		chpb.setOpen(ch.isOpen());
		chpb.setStaticDataId(ch.getStaticDataId());

		return chpb.build();
	}
	
	public static ClanInviteProto createClanInviteProto(ClanInvite invite) {
		ClanInviteProto.Builder cipb = ClanInviteProto.newBuilder();
		cipb.setInviteUuid(
			invite.getId());
		cipb.setUserUuid(
			invite.getUserId());
		cipb.setInviterUuid(
			invite.getInviterId());
		cipb.setClanUuid(
			invite.getClanId());
		cipb.setTimeOfInvite(
			invite.getTimeOfInvite().getTime());
		
		return cipb.build();
	}
	
	public static ClanMemberTeamDonationProto createClanMemberTeamDonationProto(
		ClanMemberTeamDonation cmtd, MonsterSnapshotForUser msfu,
		MinimumUserProto solicitor, MinimumUserProto donatorProto)
	{
		ClanMemberTeamDonationProto.Builder cmtdpb =
			ClanMemberTeamDonationProto.newBuilder();
		
		cmtdpb.setDonationUuid(cmtd.getId());
		
		cmtdpb.setSolicitor(solicitor);
		cmtdpb.setClanUuid(cmtd.getClanId());
		cmtdpb.setPowerAvailability(cmtd.getPowerLimit());
		cmtdpb.setIsFulfilled(cmtd.isFulfilled());
		
		String msg = cmtd.getMsg();
		if (null != msg && !msg.isEmpty()) {
			cmtdpb.setMsg(msg);
		}
		
		cmtdpb.setTimeOfSolicitation(cmtd.getTimeOfSolicitation().getTime());
		
		if (null != msfu) {
			UserMonsterSnapshotProto usmp = createUserMonsterSnapshotProto(
				msfu, donatorProto);
			cmtdpb.addDonations(usmp);
		}
			
		return cmtdpb.build();
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

	/**Item.proto***************************************************/
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

		str = item.getItemType();
		if (null != str) {
			try {
				ItemType it = ItemType.valueOf(str);
				ipb.setItemType(it);
			} catch (Exception e) {
				log.error(String.format(
					"can't create enum type. itemType=%s. item=%s",
					str, item), e);
			}
		}

		ipb.setStaticDataId(item.getStaticDataId());
		ipb.setAmount(item.getAmount());
		ipb.setSecretGiftChance(item.getSecretGiftChance());
		ipb.setAlwaysDisplayToUser(item.isAlwaysDisplayToUser());

		return ipb.build();
	}


	public static List<UserItemProto> createUserItemProtosFromUserItems(
		List<ItemForUser> ifuCollection) {

		List<UserItemProto> userItems = new ArrayList<UserItemProto>();

		for (ItemForUser ifu : ifuCollection) {
			userItems.add(
				createUserItemProtoFromUserItem(ifu));
		}
		return userItems;
	}

	public static UserItemProto createUserItemProtoFromUserItem(ItemForUser ifu) {
		UserItemProto.Builder uipb = UserItemProto.newBuilder();

		uipb.setItemId(ifu.getItemId());
		uipb.setUserUuid(ifu.getUserId());
		uipb.setQuantity(ifu.getQuantity());

		return uipb.build();
	}

	public static UserItemProto createUserItemProto(String userId, int itemId, int quantity) {
		UserItemProto.Builder uipb = UserItemProto.newBuilder();

		uipb.setItemId(itemId);
		uipb.setUserUuid(userId);
		uipb.setQuantity(quantity);

		return uipb.build();
	}

	public static List<UserItemUsageProto> createUserItemUsageProto(
		List<ItemForUserUsage> ifuuList)
	{
		List<UserItemUsageProto> protos = new ArrayList<UserItemUsageProto>();
		
		for (ItemForUserUsage ifuu : ifuuList) {
			UserItemUsageProto uiup = createUserItemUsageProto(ifuu);
			protos.add(uiup);
		}
		
		return protos;
	}
	
	public static UserItemUsageProto createUserItemUsageProto(ItemForUserUsage ifuu) {
		UserItemUsageProto.Builder uiupb = UserItemUsageProto.newBuilder();
		uiupb.setUsageUuid(ifuu.getId());
		uiupb.setUserUuid(ifuu.getUserId());
		uiupb.setItemId(ifuu.getItemId());
		Timestamp toe = new Timestamp(ifuu.getTimeOfEntry().getTime());
		uiupb.setTimeOfEntry(toe.getTime());
		uiupb.setUserDataUuid(ifuu.getUserDataId());
		
		String str = ifuu.getActionType();
		if (null != str) {
			try {
				GameActionType gat = GameActionType.valueOf(str);
				uiupb.setActionType(gat);
			} catch (Exception e) {
				log.error(String.format(
					"can't create enum type. actionType=%s. itemForUserUsage=%s",
					str, ifuu), e);
			}
		}
		
		return uiupb.build();
	}
	
	public static Collection<UserItemSecretGiftProto> createUserItemSecretGiftProto(
		Collection<ItemSecretGiftForUser> secretGifts)
	{
		Collection<UserItemSecretGiftProto> gifs = new ArrayList<UserItemSecretGiftProto>();
		if (null == secretGifts || secretGifts.isEmpty()) 
		{
			return gifs;
		}
			
		for (ItemSecretGiftForUser isgfu : secretGifts)
		{
			gifs.add(createUserItemSecretGiftProto(isgfu));
		}
		return gifs;
	}
	
	public static UserItemSecretGiftProto createUserItemSecretGiftProto(
		ItemSecretGiftForUser secretGift)
	{
		UserItemSecretGiftProto.Builder uisgpb = UserItemSecretGiftProto.newBuilder();
		uisgpb.setUisgUuid(secretGift.getId());
		uisgpb.setUserUuid(secretGift.getUserId());
		uisgpb.setSecsTillCollection(secretGift.getSecsTillCollection());
		uisgpb.setItemId(secretGift.getItemId());
		
		Date createTime = secretGift.getCreateTime();
		uisgpb.setCreateTime(createTime.getTime());
		
		return uisgpb.build();
	}
	
	/**MiniJobConfig.proto********************************************/
	public static MiniJobProto createMiniJobProto(MiniJob mj) {
		MiniJobProto.Builder mjpb = MiniJobProto.newBuilder();

		mjpb.setMiniJobId(mj.getId());
		mjpb.setRequiredStructId(mj.getRequiredStructId());

		String str = mj.getName();
		if (null != str) {
			mjpb.setName(str);
		}

		mjpb.setCashReward(mj.getCashReward());
		mjpb.setOilReward(mj.getOilReward());
		mjpb.setGemReward(mj.getGemReward());
        mjpb.setMonsterIdReward(mj.getMonsterIdReward());
        mjpb.setItemIdReward(mj.getItemIdReward());
        mjpb.setItemRewardQuantity(mj.getItemRewardQuantity());
        mjpb.setSecondItemIdReward(mj.getSecondItemIdReward());
        mjpb.setSecondItemRewardQuantity(mj.getSecondItemRewardQuantity());

		str = mj.getQuality();
		if (null != str) {
			try {
				Quality q = Quality.valueOf(str);
				mjpb.setQuality(q);
			} catch(Exception e) {
				log.error(String.format(
					"invalid quality. MiniJob=%s", mj), e);
			}
		}

		mjpb.setMaxNumMonstersAllowed(mj.getMaxNumMonstersAllowed());
		mjpb.setChanceToAppear(mj.getChanceToAppear());
		mjpb.setHpRequired(mj.getHpRequired());
		mjpb.setAtkRequired(mj.getAtkRequired());
		mjpb.setMinDmgDealt(mj.getMinDmgDealt());
		mjpb.setMaxDmgDealt(mj.getMaxDmgDealt());
		mjpb.setDurationMaxMinutes(mj.getDurationMaxMinutes());
		mjpb.setDurationMinMinutes(mj.getDurationMinMinutes());

		return mjpb.build();
	}

	public static UserMiniJobProto createUserMiniJobProto(
		MiniJobForUser mjfu, MiniJob mj) {
		UserMiniJobProto.Builder umjpb = UserMiniJobProto.newBuilder();

		umjpb.setUserMiniJobUuid(mjfu.getId());
		umjpb.setBaseDmgReceived(mjfu.getBaseDmgReceived());
		umjpb.setDurationMinutes(mjfu.getDurationMinutes());
		umjpb.setDurationSeconds(mjfu.getDurationSeconds());

		Date time = mjfu.getTimeStarted();
		if (null != time) {
			umjpb.setTimeStarted(time.getTime());
		}

		List<String> userMonsterIds = mjfu.getUserMonsterIds();
		if (null != userMonsterIds) {
			umjpb.addAllUserMonsterUuids(userMonsterIds);
		}

		time = mjfu.getTimeCompleted();
		if (null != time) {
			umjpb.setTimeCompleted(time.getTime());
		}

		MiniJobProto mjp = createMiniJobProto(mj);
		umjpb.setMiniJob(mjp);

		return umjpb.build();
	}

	public static List<UserMiniJobProto> createUserMiniJobProtos(
		List<MiniJobForUser> mjfuList, Map<Integer,
		MiniJob> miniJobIdToMiniJob) {
		List<UserMiniJobProto> umjpList = new ArrayList<UserMiniJobProto>();

		for (MiniJobForUser mjfu : mjfuList) {
			int miniJobId = mjfu.getMiniJobId();

			MiniJob mj = null;
			if (null == miniJobIdToMiniJob ||
				!miniJobIdToMiniJob.containsKey(miniJobId)) {
				mj = MiniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
			} else {
				mj = miniJobIdToMiniJob.get(miniJobId);
			}

			UserMiniJobProto umjp = createUserMiniJobProto(mjfu, mj);
			umjpList.add(umjp);
		}

		return umjpList;
	}


	/**MonsterStuff.proto********************************************/
	public static MonsterProto createMonsterProto(Monster aMonster,
		Map<Integer, MonsterLevelInfo> levelToInfo) {
		MonsterProto.Builder mpb = MonsterProto.newBuilder();

		mpb.setMonsterId(aMonster.getId());
		String aStr = aMonster.getEvolutionGroup(); 
		if (null != aStr) {
			mpb.setEvolutionGroup(aStr);
		} else {
			log.error(String.format(
				"monster has no evolutionGroup, aMonster=%s", aMonster));
		}
		aStr = aMonster.getMonsterGroup();
		if (null != aStr) {
			mpb.setMonsterGroup(aStr);
		}
		String monsterQuality = aMonster.getQuality(); 
		try {
			Quality mq = Quality.valueOf(monsterQuality);
			mpb.setQuality(mq);
		} catch (Exception e) {
			log.error(String.format(
				"invalid monster quality. monster=%s",
				aMonster), e);
		}
		mpb.setEvolutionLevel(aMonster.getEvolutionLevel());
		aStr = aMonster.getDisplayName(); 
		if (null != aStr) {
			mpb.setDisplayName(aStr);
		}

		String monsterElement = aMonster.getElement();
		try {
			Element me = Element.valueOf(monsterElement);
			mpb.setMonsterElement(me);
		} catch (Exception e){
			log.error(String.format(
				"invalid monster element. monster=%s",
				aMonster), e);
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

		mpb.setShadowScaleFactor(aMonster.getShadowScaleFactor());
		mpb.setBaseOffensiveSkillId(aMonster.getBaseOffensiveSkillId());
		mpb.setBaseDefensiveSkillId(aMonster.getBaseDefensiveSkillId());
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
			mlipb.setSellAmount(info.getSellAmount());
			mlipb.setTeamCost(info.getTeamCost());
            mlipb.setCostToFullyHeal(info.getCostToFullyHeal());
            mlipb.setCostToFullyHealExponent(info.getCostToFullyHealExponent());
            mlipb.setSecsToFullyHeal(info.getSecsToFullyHeal());
            mlipb.setSecsToFullyHealExponent(info.getSecsToFullyHealExponent());
			
			mlipb.setEnhanceCostPerFeeder(info.getEnhanceCostPerFeeder());
			mlipb.setEnhanceCostExponent(info.getEnhanceCostExponent());
			mlipb.setEnhanceExpPerSecond(info.getEnhanceExpPerSecond());
			mlipb.setEnhanceExpPerSecondExponent(info.getEnhanceExpPerSecondExponent());

			lvlInfoProtos.add(mlipb.build());
		}

		return lvlInfoProtos;
	}

	public static FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(MonsterForUser mfu) {
		FullUserMonsterProto.Builder fumpb = FullUserMonsterProto.newBuilder();
		fumpb.setUserMonsterUuid(mfu.getId());
		fumpb.setUserUuid(mfu.getUserId());
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
		fumpb.setIsRestrictd(mfu.isRestricted());

		int curOffensiveSkillId = mfu.getOffensiveSkillId();
		int curDefensiveSkillId = mfu.getDefensiveSkillId();
		if (curOffensiveSkillId > 0) {
			fumpb.setOffensiveSkillId(curOffensiveSkillId);
		}
		if (curDefensiveSkillId > 0) {
			fumpb.setDefensiveSkillId(curDefensiveSkillId);
		}

		//set userMonster skill (if absent) to monster skill
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(mfu.getMonsterId());
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (curOffensiveSkillId <= 0 && defaultOffensiveSkillId > 0) {
			fumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (curDefensiveSkillId <= 0 && defaultDefSkillId > 0) {
			fumpb.setDefensiveSkillId(defaultDefSkillId);	
		}

		return fumpb.build();
	}

	public static List<FullUserMonsterProto> createFullUserMonsterProtoList(
		List<MonsterForUser> userMonsters) {
		List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();

		if (userMonsters != null) {
			for (MonsterForUser mfu : userMonsters) {
				FullUserMonsterProto ump = createFullUserMonsterProtoFromUserMonster(mfu);
				protos.add(ump);
			}
		}

		return protos;
	}

	public static MinimumUserMonsterProto createMinimumUserMonsterProto(
		MonsterForUser mfu)
	{
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto.newBuilder();

		mumpb.setMonsterId(mfu.getMonsterId());
		mumpb.setMonsterLvl(mfu.getCurrentLvl());
		
		int curOffensiveSkillId = mfu.getOffensiveSkillId();
		int curDefensiveSkillId = mfu.getDefensiveSkillId();
		if (curOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(curOffensiveSkillId);
		}
		if (curDefensiveSkillId > 0) {
			mumpb.setDefensiveSkillId(curDefensiveSkillId);
		}

		//set userMonster skill (if absent) to monster skill
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(mfu.getMonsterId());
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (curOffensiveSkillId <= 0 && defaultOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (curDefensiveSkillId <= 0 && defaultDefSkillId > 0) {
			mumpb.setDefensiveSkillId(defaultDefSkillId);	
		}
		
		return mumpb.build();
	}

	public static MinimumUserMonsterProto createMinimumUserMonsterProto(
		MonsterForPvp mfp)
	{
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto.newBuilder();

		//int id = mfp.getId();
		int id = mfp.getMonsterId();
		int lvl = mfp.getMonsterLvl();

		mumpb.setMonsterId(id);
		mumpb.setMonsterLvl(lvl);

		//set userMonster skill (if absent) to monster skill
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(id);
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (defaultOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (defaultDefSkillId > 0) {
			mumpb.setDefensiveSkillId(defaultDefSkillId);	
		}
		
		return mumpb.build();
	}
	
	public static MinimumUserMonsterProto createMinimumUserMonsterProto(
		MonsterSnapshotForUser msfu)
	{
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto.newBuilder();

		mumpb.setMonsterId(msfu.getMonsterId());
		mumpb.setMonsterLvl(msfu.getCurrentLvl());
		
		
		//set userMonster skill (if absent) to monster skill
		int curOffensiveSkillId = msfu.getOffSkillId();
		int curDefensiveSkillId = msfu.getDefSkillId();
		if (curOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(curOffensiveSkillId);
		}
		if (curDefensiveSkillId > 0) {
			mumpb.setDefensiveSkillId(curDefensiveSkillId);
		}

		//set userMonster skill (if absent) to monster skill
		Monster monzter = MonsterRetrieveUtils.getMonsterForMonsterId(msfu.getMonsterId());
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (curOffensiveSkillId <= 0 && defaultOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (curDefensiveSkillId <= 0 && defaultDefSkillId > 0) {
			mumpb.setDefensiveSkillId(defaultDefSkillId);	
		}
		
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
		umhpb.setUserUuid(mhfu.getUserId());
		umhpb.setUserMonsterUuid(mhfu.getMonsterForUserId());

		Date aDate = mhfu.getQueuedTime();
		if (null != aDate) {
			umhpb.setQueuedTimeMillis(aDate.getTime());
		}

		umhpb.setUserHospitalStructUuid(mhfu.getUserStructHospitalId());
		umhpb.setHealthProgress(mhfu.getHealthProgress());
		umhpb.setPriority(mhfu.getPriority());
		umhpb.setElapsedSeconds(mhfu.getElapsedSeconds());

		return umhpb.build();
	}

	public static UserEnhancementProto createUserEnhancementProtoFromObj(
		String userId, UserEnhancementItemProto baseMonster, List<UserEnhancementItemProto> feeders) {

		UserEnhancementProto.Builder uepb = UserEnhancementProto.newBuilder();

		uepb.setUserUuid(userId);
		uepb.setBaseMonster(baseMonster);
		uepb.addAllFeeders(feeders);

		return uepb.build();
	}

	public static UserEnhancementItemProto createUserEnhancementItemProtoFromObj(
		MonsterEnhancingForUser mefu) {

		UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto.newBuilder();
		ueipb.setUserMonsterUuid(mefu.getMonsterForUserId());

		Date startTime = mefu.getExpectedStartTime();
		if (null != startTime) {
			ueipb.setExpectedStartTimeMillis(startTime.getTime());
		}

		ueipb.setEnhancingCost(mefu.getEnhancingCost());

		return ueipb.build();
	}

	public static UserCurrentMonsterTeamProto createUserCurrentMonsterTeamProto(String userId,
		List<MonsterForUser> curTeam) {
		UserCurrentMonsterTeamProto.Builder ucmtpb = UserCurrentMonsterTeamProto.newBuilder();
		ucmtpb.setUserUuid(userId);

		List<FullUserMonsterProto> currentTeam = createFullUserMonsterProtoList(curTeam);
		ucmtpb.addAllCurrentTeam(currentTeam);

		return ucmtpb.build();
	}

	public static UserMonsterEvolutionProto createUserEvolutionProtoFromEvolution(
		MonsterEvolvingForUser mefu) {
		UserMonsterEvolutionProto.Builder uepb = UserMonsterEvolutionProto.newBuilder();

		String catalystUserMonsterId = mefu.getCatalystMonsterForUserId();
		String one = mefu.getMonsterForUserIdOne();
		String two = mefu.getMonsterForUserIdTwo();
		Date startTime = mefu.getStartTime();

		uepb.setCatalystUserMonsterUuid(catalystUserMonsterId);

		long startTimeMillis = startTime.getTime();
		uepb.setStartTime(startTimeMillis);

		List<String> userMonsterIds = new ArrayList<String>();
		userMonsterIds.add(one);
		userMonsterIds.add(two);
		uepb.addAllUserMonsterUuids(userMonsterIds);

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
			log.error(String.format(
				"incorrect DialogueType enum. MonsterBattleDialogue=%s", mbd), e);
		}

		mbdpb.setDialogue(mbd.getDialogue());
		mbdpb.setProbabilityUttered(mbd.getProbabilityUttered());

		return mbdpb.build();
	}

	public static UserMonsterCurrentHealthProto createUserMonsterCurrentHealthProto(
		MonsterForUser mfu) {
		UserMonsterCurrentHealthProto.Builder umchpb =
			UserMonsterCurrentHealthProto.newBuilder(); 

		umchpb.setUserMonsterUuid(mfu.getId());
		umchpb.setCurrentHealth(mfu.getCurrentHealth());

		return umchpb.build();
	}
	
	public static UserMonsterSnapshotProto createUserMonsterSnapshotProto(
		MonsterSnapshotForUser msfu,
		MinimumUserProto ownerProto)
	{
		UserMonsterSnapshotProto.Builder usmpb = UserMonsterSnapshotProto.newBuilder();
		usmpb.setSnapshotUuid(msfu.getId());
		usmpb.setTimeOfCreation(msfu.getTimeOfEntry().getTime());
		
		String str = msfu.getType();
		try {
			SnapshotType type = SnapshotType.valueOf(str);
			usmpb.setType(type);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect SnapshotType enum. MonsterSnapshotForUser=%s", msfu), e);
		}
		
		usmpb.setRelevantTableUuid(msfu.getIdInTable());
		usmpb.setMonsterForUserUuid(msfu.getMonsterForUserId());
		
		if (null != ownerProto) {
			usmpb.setUser(ownerProto);
		}
		
		usmpb.setMonsterId(msfu.getMonsterId());
		usmpb.setCurrentExp(msfu.getCurrentExp());
		usmpb.setCurrentLvl(msfu.getCurrentLvl());
		usmpb.setCurrentHp(msfu.getCurrentHp());
		usmpb.setTeamSlotNum(msfu.getTeamSlotNum());
		usmpb.setOffensiveSkillId(msfu.getOffSkillId());
		usmpb.setDefensiveSkillId(msfu.getDefSkillId());
		
		return usmpb.build();
	}

	/**Prerequisite.proto****************************************************/
	public static PrereqProto createPrerequisiteProto (Prerequisite prereq) {
		PrereqProto.Builder ppb = PrereqProto.newBuilder();
		ppb.setPrereqId(prereq.getId());

		String str = prereq.getGameType();
		if (null != str) {
			try {
				GameType type = GameType.valueOf(str);
				ppb.setGameType(type);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect GameType enum. Prerequisite=%s", prereq), e);
			}

		}

		ppb.setGameEntityId(prereq.getGameEntityId());

		str = prereq.getPrereqGameType();
		if (null != str) {
			try {
				GameType type = GameType.valueOf(str);
				ppb.setPrereqGameType(type);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect prereq GameType enum. Prerequisite=%s", prereq), e);
			}

		}

		ppb.setPrereqGameEntityId(prereq.getPrereqGameEntityId());
		ppb.setQuantity(prereq.getQuantity());

		return ppb.build();
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
				Element me = Element.valueOf(str);
				builder.setMonsterElement(me);
			} catch (Exception e) {
				log.error(String.format(
					"invalid monsterElement. quest=%s", quest), e);
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
				log.error(String.format(
					"incorrect QuestJobType. QuestJob=%s",
					qj), e);
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

		DialogueProto.Builder dp = DialogueProto.newBuilder();
		if (d == null) {
			return dp.build();
		}

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
				log.error(String.format(
					"no quest with id=%s, userQuest=%s",
					userQuest.getQuestId(), userQuest));
			}

			if (null != quest) {
				builder.setUserUuid(userQuest.getUserId());
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

		if (!questIdToUserQuestJobs.containsKey(questId)) {
			//should never go in here!
			log.error(String.format(
				"user has Quest but no QuestJobs. questId=%s. User's quest jobs:%s",
				questId, questIdToUserQuestJobs));

			return userQuestJobProtoList;
		}

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
	
	/**Research.proto****************************************/
	public static ResearchProto createResearchProto(Research r,
		Collection<ResearchProperty> researchProperties)
	{
		ResearchProto.Builder rpb = ResearchProto.newBuilder();
		
		rpb.setResearchId(r.getId());
		String rt = r.getResearchType();
		if (null != rt) {
			try {
				ResearchType researchType = ResearchType.valueOf(rt);

				rpb.setResearchType(researchType);
			} catch (Exception e) {
				log.error(String.format(
					"invalid research type. Researchtype=%s", rt),
					e);
			}
			
		}
		
		String rd = r.getResearchDomain();

		if (null != rd) {
			try {
				ResearchDomain researchDomain = ResearchDomain.valueOf(rd);
				rpb.setResearchDomain(researchDomain);
			} catch (Exception e) {
				log.error(String.format(
				"invalid research domain. Researchdomain=%s", rd),
					e);
			}

		}
		
		String str;
		str = r.getIconImgName();
		if (null != str && !str.isEmpty()) {
			rpb.setIconImgName(str);
		}
		
		str = r.getName();
		if (null != str && !str.isEmpty()) {
			rpb.setName(str);
		}
		
		int predId = r.getPredId();
		if (predId > 0) {
			rpb.setPredId(predId);
		}
		int succId = r.getSuccId();
		if (predId > 0) {
			rpb.setSuccId(succId);
		}
		
		str = r.getDesc();
		if (null != str) {
			rpb.setDesc(str);
		}
		
		rpb.setDurationMin(r.getDurationMin());
		rpb.setCostAmt(r.getCostAmt());
		rpb.setLevel(r.getLevel());
		
		str = r.getCostType();
		if (null != str && !str.isEmpty()) {
			try {
				ResourceType resType = ResourceType.valueOf(str);
				rpb.setCostType(resType);
			} catch (Exception e) {
				log.error(String.format(
					"invalid ResourceType. Research=%s", r),
					e);
			}
		}
		
		if (null != researchProperties) {
			List<ResearchPropertyProto> rppList = createResearchPropertyProto(researchProperties);
			rpb.addAllProperties(rppList);
		}
		
		return rpb.build();
	}
	
	public static List<ResearchPropertyProto> createResearchPropertyProto(
		Collection<ResearchProperty> rpCollection)
	{
		List<ResearchPropertyProto> retVal = new ArrayList<ResearchPropertyProto>();
		for (ResearchProperty bp : rpCollection)
		{
			ResearchPropertyProto bpp = createResearchPropertyProto(bp);
			
			retVal.add(bpp);
		}
		
		return retVal;
	}
	
	public static ResearchPropertyProto createResearchPropertyProto(ResearchProperty rp)
	{
		ResearchPropertyProto.Builder rppb = ResearchPropertyProto.newBuilder();
		
		rppb.setResearchPropertyId(rp.getId());
		
		String str = rp.getName();
		if (null != str && !str.isEmpty()){
			rppb.setName(str);
		}
		
		rppb.setResearchValue(rp.getValue());
		rppb.setResearchId(rp.getId());
		
		return rppb.build();
	}
	
	/**Skill.proto***************************************************/
	public static SkillProto createSkillProtoFromSkill(Skill s,
		Map<Integer, SkillProperty> skillPropertyIdToProperty)
	{
		SkillProto.Builder spb = SkillProto.newBuilder();
		spb.setSkillId(s.getId());

		String str = s.getName();
		if (null != str) {
			spb.setName(str);
		}

		int orbCost = s.getOrbCost();
		if (orbCost > 0) {
			spb.setOrbCost(orbCost);
		}

		str = s.getType();
		if (null != str) {
			try {
				SkillType st = SkillType.valueOf(str);
				spb.setType(st);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SkillType. Skill=%s.",
					s), e);
			}
		}

		str = s.getActivationType();
		if (null != str) {
			try {
				SkillActivationType st = SkillActivationType.valueOf(str);
				spb.setActivationType(st);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SkillActivationType. Skill=%s.",
					s), e);
			}
		}

		int predecId = s.getPredecId();
		if (predecId > 0) {
			spb.setPredecId(predecId);
		}

		int succId = s.getSuccessorId();
		if (succId > 0) {
			spb.setSucId(succId);
		}

		//skills can have no properties
		if (null != skillPropertyIdToProperty) { 
			for (SkillProperty sp : skillPropertyIdToProperty.values()) {
				spb.addProperties(
					createSkillPropertyProtoFromSkillProperty(sp));
			}
		}

		str = s.getDefensiveDesc();
		if (null != str) {
			spb.setDefDesc(str);
		}
		
		str = s.getOffensiveDesc();
		if (null != str) {
			spb.setOffDesc(str);
		}
		
		str = s.getImgNamePrefix();
		if (null != str) {
			spb.setImgNamePrefix(str);
		}
		
		int skillEffectDuration = s.getSkillEffectDuration();
		if (skillEffectDuration > 0) {
			spb.setSkillEffectDuration(skillEffectDuration);
		}
		
		str = s.getShortDefDesc();
		if (null != str) {
			spb.setShortDefDesc(str);
		}
		
		str = s.getShortOffDesc();
		if (null != str) {
			spb.setShortOffDesc(str);
		}

		return spb.build();
	}

	public static SkillPropertyProto createSkillPropertyProtoFromSkillProperty(
		SkillProperty property)
	{
		SkillPropertyProto.Builder sppb = SkillPropertyProto.newBuilder();
		sppb.setSkillPropertyId(property.getId());

		//TODO: Consider making this an enum
		String str = property.getName();
		if (null != str) { 
			sppb.setName(str);
		}
		//TODO: Account for non number values
		sppb.setSkillValue(property.getValue());

		return sppb.build();
	}
	
	public static SkillSideEffectProto createSkillSideEffectProto(
		SkillSideEffect sse)
	{
		SkillSideEffectProto.Builder ssepb = SkillSideEffectProto.newBuilder();
		ssepb.setSkillSideEffectId(sse.getId());
		
		String str = sse.getName();
		if (null != str) {
			ssepb.setName(str);
		}
		
		str = sse.getDesc();
		if (null != str) {
			ssepb.setDesc(str);
		}
		
		str = sse.getType();
		if (null != str) {
			try {
				SideEffectType set = SideEffectType.valueOf(str);
				ssepb.setType(set);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SideEffectType. SkillSideEffect=%s", sse),
					e);
			}
		}
		
		str = sse.getTraitType();
		if (null != str) {
			try {
				SideEffectTraitType sett = SideEffectTraitType.valueOf(str);
				ssepb.setTraitType(sett);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SideEffectTraitType. SkillSideEffect=%s", sse),
					e);
			}
		}
		
		str = sse.getImgName();
		if (null != str) {
			ssepb.setImgName(str);
		}
		
		ssepb.setImgPixelOffsetX(sse.getImgPixelOffsetX());
		ssepb.setImgPixelOffsetY(sse.getImgPixelOffsetY());

		str = sse.getIconImgName();
		if (null != str) {
			ssepb.setIconImgName(str);
		}
		
		str = sse.getPfxName();
		if (null != str) {
			ssepb.setPfxName(str);
		}
		
		str = sse.getPfxColor();
		if (null != str) {
			ssepb.setPfxColor(str);
		}
		
		str = sse.getPositionType();
		if (null != str) {
			try {
				SideEffectPositionType sept = SideEffectPositionType.valueOf(str);
				ssepb.setPositionType(sept);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SideEffectPositionType. SkillSideEffect=%s", sse),
					e);
			}
		}
		
		ssepb.setImgPixelOffsetX(sse.getPfxPixelOffsetX());
		ssepb.setImgPixelOffsetY(sse.getPfxPixelOffsetY());

		str = sse.getBlendMode();
		if (null != str) {
			try {
				SideEffectBlendMode sebm = SideEffectBlendMode.valueOf(str);
				ssepb.setBlendMode(sebm);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect enum SideEffectBlendMode. SkillSideEffect=%s", sse),
					e);
			}
		}
		
		return ssepb.build();
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
			log.error(String.format(
				"incorrect enum structType. Structure=%s", s), e);
		}

		aStr = s.getBuildResourceType();
		try {
			ResourceType rt = ResourceType.valueOf(aStr);
			builder.setBuildResourceType(rt);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect enum ResourceType. Structure=%s", s), e);
		}

		builder.setBuildCost(s.getBuildCost());
		builder.setMinutesToBuild(s.getMinutesToBuild());
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
			log.error(String.format(
				"incorrect enum ResourceType. ResourceGenerator=%s", srg), e);
		}

		rgpb.setProductionRate(srg.getProductionRate());
		rgpb.setCapacity(srg.getCapacity());

		return rgpb.build();
	}
	
	public static MoneyTreeProto createMoneyTreeProtoFromStructureMoneyTree(Structure s,
	        StructureInfoProto sip, StructureMoneyTree smt) {
        if (null == sip) {
            sip = createStructureInfoProtoFromStructure(s);
        }
		
		MoneyTreeProto.Builder mtpb = MoneyTreeProto.newBuilder();

		mtpb.setProductionRate(smt.getProductionRate());
		mtpb.setCapacity(smt.getCapacity());
		mtpb.setDaysOfDuration(smt.getDaysOfDuration());
		mtpb.setDaysForRenewal(smt.getDaysForRenewal());
		mtpb.setStructInfo(sip);
		mtpb.setIapProductId(smt.getIapProductId());
		mtpb.setFakeIAPProductId(smt.getFakeIAPProductId());
			
		return mtpb.build();
	}
	
	public static ResearchHouseProto createResearchHouseProtoFromStructureResearchHouse(Structure s,
	        StructureInfoProto sip, StructureResearchHouse srh) {
        if (null == sip) {
            sip = createStructureInfoProtoFromStructure(s);
        }
		
		ResearchHouseProto.Builder rhpb = ResearchHouseProto.newBuilder();

		rhpb.setStructId(srh.getStructId());
		return rhpb.build();
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
			log.error(String.format(
				"incorrect enum ResourceType. resourceStorage=%s",
				srs), e);
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
		hpb.setSecsToFullyHealMultiplier(sh.getSecsToFullyHealMultiplier());

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
		lpb.setPointsMultiplier(sl.getPointsMultiplier());
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
		str = sr.getImgSuffix();
		if (null != str) {
			rpb.setImgSuffix(str);
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
		thpb.setNumEvoChambers(sth.getNumEvoChambers());

		return thpb.build();
	}

	public static MiniJobCenterProto createMiniJobCenterProto(Structure s,
		StructureInfoProto sip, StructureMiniJob miniJobCenter) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		MiniJobCenterProto.Builder smjcpb = MiniJobCenterProto.newBuilder();
		smjcpb.setStructInfo(sip);
		smjcpb.setGeneratedJobLimit(miniJobCenter.getGeneratedJobLimit());
		smjcpb.setHoursBetweenJobGeneration(
			miniJobCenter.getHoursBetweenJobGeneration());

		return smjcpb.build();
	}

	public static FullUserStructureProto createFullUserStructureProtoFromUserstruct(StructureForUser userStruct) {
		FullUserStructureProto.Builder builder = FullUserStructureProto.newBuilder();
		builder.setUserStructUuid(userStruct.getId());
		builder.setUserUuid(userStruct.getUserId());
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
			log.error(String.format(
				"incorrect orientation. structureForUser=%s", userStruct), e);
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
			log.info(String.format(
				"incorrect ResourceType (RemovalCostType). Obstacle=%s", ob), e);
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

		try {
			StructOrientation structOrientation = StructOrientation.valueOf(orientation);
			mopb.setOrientation(structOrientation);
		} catch (Exception e) {
			log.info(String.format(
				"incorrect StructOrientation. ObstacleId=%s, posX=%s, posY=%s, orientation=%s",
				obstacleId, posX, posY, orientation), e);
		}

		return mopb.build();
	}

	public static UserObstacleProto createUserObstacleProto(ObstacleForUser ofu) {
		UserObstacleProto.Builder uopb = UserObstacleProto.newBuilder();
		uopb.setUserObstacleUuid(ofu.getId());
		uopb.setUserUuid(ofu.getUserId());
		uopb.setObstacleId(ofu.getObstacleId());

		int x = ofu.getXcoord();
		int y = ofu.getYcoord();
		CoordinatePair cp = new CoordinatePair(x, y);
		CoordinateProto cproto = createCoordinateProtoFromCoordinatePair(cp);
		uopb.setCoordinates(cproto);

		String orientation = ofu.getOrientation();
		if (null != orientation) {
			try {
				StructOrientation so = StructOrientation.valueOf(orientation);
				uopb.setOrientation(so);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect struct orientation=%s, ofu=%s",
					orientation, ofu));
			}
		}

		Date removalStartTime = ofu.getRemovalTime();
		if (null != removalStartTime) {
			uopb.setRemovalStartTime(removalStartTime.getTime());
		}

		return uopb.build();
	}

	public static EvoChamberProto  createEvoChamberProto (Structure s,
		StructureInfoProto sip, StructureEvoChamber sec)
	{
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		EvoChamberProto.Builder ecpb = EvoChamberProto.newBuilder();
		ecpb.setStructInfo(sip);

		String str = sec.getQualityUnlocked();
		if (null != str) {
			try {
				Quality quality = Quality.valueOf(str);
				ecpb.setQualityUnlocked(quality);
			} catch (Exception e) {
				log.error(String.format(
					"incorrect QualityUnlocked. EvoChamber=%s",
					sec), e);
			}
		}
		ecpb.setEvoTierUnlocked(sec.getEvoTierUnlocked());

		return ecpb.build();
	}

	public static TeamCenterProto createTeamCenterProto(Structure s,
		StructureInfoProto sip, StructureTeamCenter sec)
	{
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		TeamCenterProto.Builder tcpb = TeamCenterProto.newBuilder();
		tcpb.setStructInfo(sip);
		tcpb.setTeamCostLimit(sec.getTeamCostLimit());

		return tcpb.build();
	}

	public static ClanHouseProto  createClanHouseProto (Structure s,
		StructureInfoProto sip, StructureClanHouse sch)
	{
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ClanHouseProto.Builder chpb = ClanHouseProto.newBuilder();
		chpb.setStructInfo(sip);
		chpb.setMaxHelpersPerSolicitation(sch.getMaxHelpersPerSolicitation());
		chpb.setTeamDonationPowerLimit(sch.getTeamDonationPowerLimit());

		return chpb.build();
	}

	public static PvpBoardHouseProto  createPvpBoardHouseProto (Structure s,
		StructureInfoProto sip, StructurePvpBoard spb)
	{
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		PvpBoardHouseProto.Builder chpb = PvpBoardHouseProto.newBuilder();
		chpb.setStructInfo(sip);
		chpb.setPvpBoardPowerLimit(spb.getPowerLimit());

		return chpb.build();
	}
	
	public static PvpBoardObstacleProto createPvpBoardObstacleProto(BoardObstacle bo) {
		PvpBoardObstacleProto.Builder pbopb = PvpBoardObstacleProto.newBuilder();
		pbopb.setPvpBoardId(bo.getId());
		
		String str = bo.getName();
		if (null != str) {
			pbopb.setName(str);
		}
		
		str = bo.getType();
		if (null != str) {
			try {
				BoardObstacleType bot = BoardObstacleType.valueOf(str);
				pbopb.setObstacleType(bot);
			} catch (Exception e) {
				log.error(
					String.format(
						"illegal BoardObstacleType type. BoardObstacle={}",
						bo),
					e);
			}
		}
		return pbopb.build();
	}

	public static UserPvpBoardObstacleProto createUserPvpBoardObstacleProto(
			PvpBoardObstacleForUser pbofu)
	{
		UserPvpBoardObstacleProto.Builder upopb = UserPvpBoardObstacleProto.newBuilder();
		upopb.setUserPvpBoardObstacleUuid(pbofu.getId());
		upopb.setUserUuid(pbofu.getUserId());
		upopb.setObstacleId(pbofu.getObstacleId());
		upopb.setPosX(pbofu.getPosX());
		upopb.setPosY(pbofu.getPosY());
		return upopb.build();
	}
	public static BattleItemFactoryProto createBattleItemFactoryProto(Structure s, 
			StructureInfoProto sip, StructureBattleItemFactory sbif) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}
		
		BattleItemFactoryProto.Builder bifpb = BattleItemFactoryProto.newBuilder();
		bifpb.setPowerLimit(sbif.getPowerLimit());
		bifpb.setStructInfo(sip);
		
		return bifpb.build();
	}
	
	
	/**research.proto*******************************************/
	
	/**Task.proto*****************************************************/
	/*
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
	 */

	//going by stage number instead of id, maybe because it's human friendly
	//when looking at the db
	public static TaskStageProto createTaskStageProto (int taskId, int stageNum,
		List<TaskStageForUser> monsters) {
		TaskStageProto.Builder tspb = TaskStageProto.newBuilder();

		TaskStage ts = TaskStageRetrieveUtils.getTaskStageForTaskStageId(taskId,
			stageNum);
		int taskStageId = ts.getId();
		tspb.setStageId(taskStageId);
		tspb.setAttackerAlwaysHitsFirst(ts.isAttackerAlwaysHitsFirst());

		for (TaskStageForUser tsfu : monsters) {
			TaskStageMonsterProto tsmp = createTaskStageMonsterProto(tsfu); 
			tspb.addStageMonsters(tsmp);
		}
		
		return tspb.build();
	}

	public static FullTaskProto createFullTaskProtoFromTask(Task task)
	{
		String name = task.getGoodName();
		String description = task.getDescription();
		int taskId = task.getId();

		FullTaskProto.Builder builder = FullTaskProto.newBuilder();
		builder.setTaskId(taskId);
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

		builder.setBoardHeight(task.getBoardHeight());
		builder.setBoardWidth(task.getBoardWidth());

		String groundImgPrefix = task.getGroundImgPrefix();
		if (null != groundImgPrefix) {
			builder.setGroundImgPrefix(groundImgPrefix);
		}

		Dialogue initDefeatedD = task.getInitDefeatedD();
		if (null != initDefeatedD) {
			builder.setInitialDefeatedDialogue(createDialogueProtoFromDialogue(initDefeatedD));
		}
		
		int boardId = task.getBoardId();
		if (boardId > 0) {
			builder.setBoardId(boardId);
		}
		
		setDetails(taskId, builder);
		
		return builder.build();
	}

	private static void setDetails(
		int taskId,
		com.lvl6.proto.TaskProto.FullTaskProto.Builder builder )
	{
		Set<Integer> stageIds = TaskStageRetrieveUtils
			.getTaskStageIdsForTaskId(taskId);
		//aggregating all the monsterIds and rarities for a task
		
		Set<Integer> monsterIdsForTask = new HashSet<Integer>();
		Set<String> qualitiesStrForTask = new HashSet<String>();
		for (int stageId : stageIds) {
			
			Set<Integer> stageMonsterIds = TaskStageMonsterRetrieveUtils.
				getMonsterIdsForTaskStageId(stageId);
			monsterIdsForTask.addAll(stageMonsterIds);
			
			Set<String> stageQualities = TaskStageMonsterRetrieveUtils.
				getQualitiesForTaskStageId(stageId);
			qualitiesStrForTask.addAll(stageQualities);
		}
		
		Set<Quality> qualitiesForTask = new HashSet<Quality>();
		for (String quality : qualitiesStrForTask) {
			try {
				Quality q = Quality.valueOf(quality);
				qualitiesForTask.add(q);
			} catch (Exception e) {
				log.error("illegal Quality type {}. taskId={}",
					quality, taskId);
			}
		}
		
		builder.addAllMonsterIds(monsterIdsForTask);
		builder.addAllRarities(qualitiesForTask);
	}

	public static MinimumUserTaskProto createMinimumUserTaskProto(String userId,
		TaskForUserOngoing aTaskForUser, TaskForUserClientState tfucs) {
		MinimumUserTaskProto.Builder mutpb = MinimumUserTaskProto.newBuilder();
		mutpb.setUserUuid(userId);

		int taskId = aTaskForUser.getTaskId();
		mutpb.setTaskId(taskId);
		int taskStageId = aTaskForUser.getTaskStageId();
		mutpb.setCurTaskStageId(taskStageId);
		String userTaskId = aTaskForUser.getId();
		mutpb.setUserTaskUuid(userTaskId);

		try {
			byte[] bites = null;
			if (null != tfucs) {
				bites = tfucs.getClientState();
			}
			
			if (null != bites) {
				ByteString bs = ByteString.copyFrom(bites);
				mutpb.setClientState(bs);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(String.format(
				"unable to convert byte[] to google.ByteString, userId=%s",
				userId),
				e);
		}

		
		return mutpb.build();
	}
	
	public static List<UserTaskCompletedProto> createUserTaskCompletedProto(
		List<UserTaskCompleted> utcList)
	{
		List<UserTaskCompletedProto> retVal = new ArrayList<UserTaskCompletedProto>();
		for (UserTaskCompleted utc : utcList) {
			UserTaskCompletedProto utcp = createUserTaskCompletedProto(utc);
			retVal.add(utcp);
		}
		return retVal;
	}
	
	public static UserTaskCompletedProto createUserTaskCompletedProto(
		UserTaskCompleted utc)
	{
		UserTaskCompletedProto.Builder utcpb = UserTaskCompletedProto.newBuilder();
		utcpb.setTaskId(utc.getTaskId());
		utcpb.setUnclaimedCash(utc.getUnclaimedCash());
		utcpb.setUnclaimedOil(utc.getUnclaimedOil());
		utcpb.setUserId(utc.getUserId());
		return utcpb.build();
	}

	/*
  public static TaskStageMonsterProto createTaskStageMonsterProto (TaskStageMonster tsm, 
      int cashReward, int oilReward, boolean pieceDropped,
      Map<Integer, Integer> tsmIdToItemId) {
    int tsmMonsterId = tsm.getMonsterId();

    TaskStageMonsterProto.Builder bldr = TaskStageMonsterProto.newBuilder();
    bldr.setTsmId(tsm.getId());
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
    bldr.setExpReward(tsm.getExpReward());
    bldr.setLevel(tsm.getLevel());
    bldr.setDmgMultiplier(tsm.getDmgMultiplier());

    bldr.setPuzzlePieceDropped(pieceDropped);
    if ( tsm.getMonsterIdDrop() > 0 ) {
    	bldr.setPuzzlePieceMonsterId(tsm.getMonsterIdDrop());
    	bldr.setPuzzlePieceMonsterDropLvl(tsm.getMonsterDropLvl());
    }

    int defensiveSkillId = tsm.getDefensiveSkillId(); 
    if ( defensiveSkillId > 0 ) {
    	bldr.setDefensiveSkillId(defensiveSkillId);
    }

    int offensiveSkillId = tsm.getOffensiveSkillId();
    if (offensiveSkillId > 0) {
    	bldr.setOffensiveSkillId(offensiveSkillId);
    }

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
	 */

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
		bldr.setTsfuUuid(tsfu.getId());
		bldr.setTsmId(tsmId);
		bldr.setMonsterId(tsmMonsterId);
		String tsmMonsterType = tsfu.getMonsterType(); 
		try {
			MonsterType mt = MonsterType.valueOf(tsmMonsterType);
			bldr.setMonsterType(mt);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect monsterType, tsm=%s", tsm), e);
		}
		bldr.setCashReward(tsfu.getCashGained());
		bldr.setOilReward(tsfu.getOilGained());
		bldr.setPuzzlePieceDropped(didPieceDrop);
		bldr.setExpReward(tsfu.getExpGained());

		bldr.setLevel(tsm.getLevel());
		bldr.setDmgMultiplier(tsm.getDmgMultiplier());

		int itemId = tsfu.getItemIdDropped();
		if (itemId > 0) {
			//check if item exists
			Item item = ItemRetrieveUtils.getItemForId(itemId);
			if (null == item) {
				throw new RuntimeException(
					String.format("nonexistent itemId for userTask=%s", tsfu));
			}
			bldr.setItemId(itemId);
		}

		if (tsm.getMonsterIdDrop() > 0) {
			bldr.setPuzzlePieceMonsterId(tsm.getMonsterIdDrop());
			bldr.setPuzzlePieceMonsterDropLvl(tsm.getMonsterDropLvl());
		}

		int defensiveSkillId = tsm.getDefensiveSkillId(); 
		if ( defensiveSkillId > 0 ) {
			bldr.setDefensiveSkillId(defensiveSkillId);
		}

		int offensiveSkillId = tsm.getOffensiveSkillId();
		if (offensiveSkillId > 0) {
			bldr.setOffensiveSkillId(offensiveSkillId);
		}

		Dialogue initD = tsm.getInitD();
		if (null != initD) {
			bldr.setInitialD(createDialogueProtoFromDialogue(initD));
		}
		Dialogue defaultD = tsm.getDefaultD();
		if (null != defaultD) {
			bldr.setDefaultD(createDialogueProtoFromDialogue(defaultD));
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
			log.error(String.format(
				"incorrect enum DayOfWeek. EventPersistent=%s",
				 event), e);
		}

		pepb.setStartHour(startHour);
		pepb.setEventDurationMinutes(eventDurationMinutes);
		pepb.setTaskId(taskId);
		pepb.setCooldownMinutes(cooldownMinutes);

		try {
			EventType typ = EventType.valueOf(eventTypeStr);
			pepb.setType(typ);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect enum EventType. EventPersistent=%s",
				event), e);
		}
		try {
			Element elem = Element.valueOf(monsterElem);
			pepb.setMonsterElement(elem);
		} catch (Exception e) {
			log.error(String.format(
				"incorrect enum MonsterElement. %s",
				event), e);
		}

		return pepb.build();
	}

	public static UserPersistentEventProto createUserPersistentEventProto(
		EventPersistentForUser epfu) {
		UserPersistentEventProto.Builder upepb = UserPersistentEventProto.newBuilder();
		Date timeOfEntry = epfu.getTimeOfEntry();

		upepb.setUserUuid(
			epfu.getUserId());
		upepb.setEventId(
			epfu.getEventPersistentId());

		if (null != timeOfEntry) {
			upepb.setCoolDownStartTime(
				timeOfEntry.getTime());
		}

		return upepb.build();
	}

	public static TaskMapElementProto createTaskMapElementProto(TaskMapElement tme) {
		TaskMapElementProto.Builder tmepb = TaskMapElementProto.newBuilder();
		tmepb.setMapElementId(tme.getId());
		tmepb.setTaskId(tme.getTaskId());
		tmepb.setXPos(tme.getxPos());
		tmepb.setYPos(tme.getyPos());

		String str = tme.getElement();
		try {
			Element me = Element.valueOf(str);
			tmepb.setElement(me);
		} catch (Exception e){
			log.error(String.format(
				"invalid element. TaskMapElement=%s", tme), e);
		}

		tmepb.setBoss(tme.isBoss());

		str = tme.getBossImgName();
		if (null != str) {
			tmepb.setBossImgName(str);
		}

		tmepb.setItemDropId(tme.getItemDropId());

		str = tme.getSectionName();
		if (null != str) {
			tmepb.setSectionName(tme.getSectionName());
		}
		tmepb.setCashReward(tme.getCashReward());
		tmepb.setOilReward(tme.getOilReward());

		str = tme.getCharacterImgName();
		if (null != str) {
			tmepb.setCharacterImgName(str);
		}

		tmepb.setCharImgVertPixelOffset(tme.getCharImgVertPixelOffset());
		tmepb.setCharImgHorizPixelOffset(tme.getCharImgHorizPixelOffset());
		tmepb.setCharImgScaleFactor(tme.getCharImgScaleFactor());
		tmepb.setIsFake(tme.isFake());


		return tmepb.build();
	}

	/**FileDownloadProto*********************************************/
	
	public static FileDownloadConstantProto createFileDownloadProtoFromFileDownload(FileDownload fd) {
		FileDownloadConstantProto.Builder fdpb = FileDownloadConstantProto.newBuilder();
		fdpb.setFileDownloadId(fd.getId());
		fdpb.setFileName(fd.getFileName());
		fdpb.setPriority(fd.getPriority());
		fdpb.setDownloadOnlyOverWifi(fd.isDownloadOnlyOverWifi());
		
		return fdpb.build();
	}
	
	
	/**BattleItemProto*********************************************/
	
	public static BattleItemProto createBattleItemProtoFromBattleItem(BattleItem bi) {
		BattleItemProto.Builder bipb = BattleItemProto.newBuilder();
		bipb.setBattleItemId(bi.getId());
		bipb.setName(bi.getName());
		bipb.setImgName(bi.getImageName());
		bipb.setBattleItemType(bi.getType());
		bipb.setBattleItemCategory(bi.getBattleItemCategory());
		bipb.setCreateResourceType(bi.getCreateResourceType());
		bipb.setCreateCost(bi.getCreateCost());
		bipb.setDescription(bi.getDescription());
		bipb.setPowerAmount(bi.getPowerAmount());
		
		return bipb.build();
	}
	
	public static UserBattleItemProto createUserBattleItemProtoFromBattleItemForUser(BattleItemForUser bifu) {
		UserBattleItemProto.Builder ubipb = UserBattleItemProto.newBuilder();
		ubipb.setUserUuid(bifu.getUserId());
		ubipb.setBattleItemId(bifu.getBattleItemId());
		ubipb.setQuantity(bifu.getQuantity());
		ubipb.setId(bifu.getId());
		
		return ubipb.build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**TournamentStuff.proto******************************************/
//	public static TournamentEventProto createTournamentEventProtoFromTournamentEvent(
//		TournamentEvent e, List<TournamentEventReward> rList) {
//
//		TournamentEventProto.Builder b = TournamentEventProto.newBuilder().setEventId(e.getId()).setStartDate(e.getStartDate().getTime())
//			.setEndDate(e.getEndDate().getTime()).setEventName(e.getEventName())
//			.setLastShowDate(e.getEndDate().getTime()+ControllerConstants.TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END*3600000L);
//
//		List<TournamentEventRewardProto> rProtosList = new ArrayList<TournamentEventRewardProto>();
//		for(TournamentEventReward r : rList) {
//			TournamentEventRewardProto rProto = createTournamentEventRewardProtoFromTournamentEventReward(r);
//			rProtosList.add(rProto);
//		}
//
//		b.addAllRewards(rProtosList);
//
//		return b.build();
//	}

//	public static TournamentEventRewardProto createTournamentEventRewardProtoFromTournamentEventReward(TournamentEventReward r) {
//
//		TournamentEventRewardProto.Builder b = TournamentEventRewardProto.newBuilder()
//			.setTournamentEventId(r.getTournamentEventId()).setMinRank(r.getMinRank()).setMaxRank(r.getMaxRank())
//			.setGoldRewarded(r.getGoldRewarded()).setBackgroundImageName(r.getBackgroundImageName())
//			.setPrizeImageName(r.getPrizeImageName());
//
//		ColorProto.Builder clrB = ColorProto.newBuilder().setBlue(r.getBlue())
//			.setGreen(r.getGreen()).setRed(r.getRed()); 
//
//		b.setTitleColor(clrB.build());
//		return b.build();
//	}

	/*
  public static MinimumUserProtoWithLevelForTournament createMinimumUserProtoWithLevelForTournament(User u, int rank, double score) {
    MinimumUserProto mup = createMinimumUserProtoFromUser(u);
    MinimumUserProtoWithLevelForTournament.Builder mupwlftb = MinimumUserProtoWithLevelForTournament.newBuilder();
    mupwlftb.setMinUserProto(mup);
    mupwlftb.setLevel(u.getLevel());
    mupwlftb.setTournamentRank(rank);
    mupwlftb.setTournamentScore(score);
    return mupwlftb.build();
  }*/

	/**User.proto*****************************************************/
	public static MinimumClanProto createMinimumClanProtoFromClan(Clan c) {
		MinimumClanProto.Builder mcpb = MinimumClanProto.newBuilder();
		mcpb.setClanUuid(c.getId());
		mcpb.setName(c.getName());
		//    mcp.setOwnerId(c.getOwnerId());
		mcpb.setCreateTime(c.getCreateTime().getTime());
		mcpb.setDescription(c.getDescription());
		mcpb.setTag(c.getTag());
		mcpb.setClanIconId(c.getClanIconId());
		return mcpb.setRequestToJoinRequired(c.isRequestToJoinRequired()).build();
	}

	//  public static MinimumUserProto createMinimumUserProtoFromUser(User u) {
	//    MinimumUserProto.Builder builder = MinimumUserProto.newBuilder().setName(u.getName()).setUserId(u.getId());
	//    if (u.getClanId() > 0) {
	//      Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
	//      builder.setClan(createMinimumClanProtoFromClan(clan));
	//    }
	//    builder.setAvatarMonsterId(u.getAvatarMonsterId());
	//    return builder.build();
	//  }

	public static MinimumUserProto createMinimumUserProtoFromUserAndClan(User u, Clan c) {
		MinimumUserProto.Builder builder = MinimumUserProto.newBuilder();
		
		
		String name = u.getName();
		builder.setName(name);
		builder.setUserUuid(u.getId());

		if (null != c) {
			builder.setClan(createMinimumClanProtoFromClan(c));
		}
		builder.setAvatarMonsterId(u.getAvatarMonsterId());

		return builder.build();
	}

	public static MinimumUserProtoWithLevel createMinimumUserProtoWithLevel(
		User u, Clan c, MinimumUserProto mup) {

		if (null == mup) {
			mup = createMinimumUserProtoFromUserAndClan(u, c);
		}

		MinimumUserProtoWithLevel.Builder mupWithLevel = MinimumUserProtoWithLevel.newBuilder();
		mupWithLevel.setMinUserProto(mup);

		mupWithLevel.setLevel(u.getLevel());
		return mupWithLevel.build();
	}

	public static MinimumUserProtoWithFacebookId createMinimumUserProtoWithFacebookId(
		User u, Clan c) {
		MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(u, c);
		MinimumUserProtoWithFacebookId.Builder b = MinimumUserProtoWithFacebookId.newBuilder();
		b.setMinUserProto(mup);
		String facebookId = u.getFacebookId();
		if (null != facebookId) {
			b.setFacebookId(facebookId);
		}

		return b.build();
	}

	public static UserFacebookInviteForSlotProto createUserFacebookInviteForSlotProtoFromInvite(
		UserFacebookInviteForSlot invite, User inviter, Clan inviterClan,
		MinimumUserProtoWithFacebookId inviterProto)
	{
		UserFacebookInviteForSlotProto.Builder inviteProtoBuilder =
			UserFacebookInviteForSlotProto.newBuilder();
		inviteProtoBuilder.setInviteUuid(invite.getId());

		if (null == inviterProto) {
			inviterProto = createMinimumUserProtoWithFacebookId(inviter, inviterClan);

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

		String userStructId = invite.getUserStructId();
		inviteProtoBuilder.setUserStructUuid(userStructId);

		int userStructFbLvl = invite.getUserStructFbLvl();
		inviteProtoBuilder.setStructFbLvl(userStructFbLvl);

		d = invite.getTimeRedeemed();
		if (null != d) {
			inviteProtoBuilder.setRedeemedTime(d.getTime());
		}

		return inviteProtoBuilder.build();
	}

	public static FullUserProto createFullUserProtoFromUser(User u,
		PvpLeagueForUser plfu, Clan c)
	{
		FullUserProto.Builder builder = FullUserProto.newBuilder();
		String userId = u.getId();
		builder.setUserUuid(userId);
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
//		if (u.getClanId() > 0) {
		if (null != c) {
//			Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
			builder.setClan(createMinimumClanProtoFromClan(c));
		}
		builder.setHasReceivedfbReward(u.isHasReceivedfbReward());
		builder.setNumBeginnerSalesPurchased(u.getNumBeginnerSalesPurchased());
		builder.setAvatarMonsterId(u.getAvatarMonsterId());

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

		Date lastMiniJobSpawnedTime = u.getLastMiniJobGeneratedTime();
		if (null != lastMiniJobSpawnedTime) {
			builder.setLastMiniJobSpawnedTime(lastMiniJobSpawnedTime.getTime());
		}

        Date lastFreeBoosterPackTime = u.getLastFreeBoosterPackTime();
        if (null != lastFreeBoosterPackTime) {
            builder.setLastFreeBoosterPackTime(lastFreeBoosterPackTime.getTime());
        }

        Date lastSecretGiftCollectTime = u.getLastSecretGiftCollectTime();
        if (null != lastSecretGiftCollectTime) {
            builder.setLastSecretGiftCollectTime(lastSecretGiftCollectTime.getTime());
        }
        
        String pvpDefendingMessage = u.getPvpDefendingMessage();
        if (null != pvpDefendingMessage) {
            builder.setPvpDefendingMessage(pvpDefendingMessage);
        }

		//add new columns above here, not below the if. if case for is fake

		int numClanHelps = u.getClanHelps();
		builder.setNumClanHelps(numClanHelps);
		
		Date lastTeamDonationSolicitation = u.getLastTeamDonateSolicitation();
		if (null != lastTeamDonationSolicitation)
		{
			builder.setLastTeamDonationSolicitation(
				lastTeamDonationSolicitation.getTime());
		}
			

		if (u.isFake()) {

		}
		
		//don't add setting new columns/properties here, add up above

		return builder.build();
	}

	public static MinimumUserProtoWithLevel createMinimumUserProto(FullUserProto fup)
	{
		MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
		String str = fup.getUserUuid();
		if (null != str)
		{
			mupb.setUserUuid(str);
		}
		
		str = fup.getName();
		if (null != str)
		{
			mupb.setName(str);
		}
		
		MinimumClanProto mcp = fup.getClan();
		if (null != mcp)
		{
			mupb.setClan(mcp);
		}
		
		int avatarMonsterId = fup.getAvatarMonsterId();
		if (avatarMonsterId > 0)
		{
			mupb.setAvatarMonsterId(avatarMonsterId);
		}
		
		MinimumUserProtoWithLevel.Builder mupwlb = MinimumUserProtoWithLevel.newBuilder();
		mupwlb.setLevel(fup.getLevel());
		mupwlb.setMinUserProto(mupb.build());
		
		return mupwlb.build();
	}

	public static Map<String, MinimumUserProtoWithLevel> createMinimumUserProtoWithLevel(
		Map<String, User> userIdsToUsers, Map<String, Clan> userIdsToClans)
	{
		Map<String, MinimumUserProtoWithLevel> userIdToMupwl =
			new HashMap<String, MinimumUserProtoWithLevel>();
		for (User u : userIdsToUsers.values())
		{
			String userId = u.getId();
			Clan c = userIdsToClans.get(userId);
			
			MinimumUserProtoWithLevel mupwl =
				createMinimumUserProtoWithLevel(u, c, null);
			
			userIdToMupwl.put(userId, mupwl);
		}
		return userIdToMupwl;
	}










	//  public static ReferralNotificationProto createReferralNotificationProtoFromReferral(
	//      Referral r, User newlyReferred) {
	//    return ReferralNotificationProto.newBuilder().setReferred(createMinimumUserProtoFromUser(newlyReferred))
	//        .setRecruitTime(r.getTimeOfReferral().getTime()).setCoinsGivenToReferrer(r.getCoinsGivenToReferrer()).build();
	//  }


	public static AnimatedSpriteOffsetProto createAnimatedSpriteOffsetProtoFromAnimatedSpriteOffset(AnimatedSpriteOffset aso) {
		return AnimatedSpriteOffsetProto.newBuilder().setImageName(aso.getImgName())
			.setOffSet(createCoordinateProtoFromCoordinatePair(aso.getOffSet())).build();
	}

	public static void createMinimumUserProtosFromClannedAndClanlessUsers(
		Map<String, Clan> clanIdsToClans, Map<String, Set<String>> clanIdsToUserIdSet,
		List<String> clanlessUserIds, Map<String, User> userIdsToUsers, 
		Map<String, MinimumUserProtoWithLevel> userIdToMinimumUserProtoWithLevel) {
		//construct the minimum user protos for the clanless users
		for (String userId : clanlessUserIds) {
			User u = userIdsToUsers.get(userId);
			MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevel(u, null, null);
			userIdToMinimumUserProtoWithLevel.put(userId, mupwl);
		}

		//construct the minimum user protos for the users that have clans 
		if (null == clanIdsToClans) {
			return;
		}
//		for (int clanId : clanIdsToClans.keySet()) {
//			Clan c = clanIdsToClans.get(clanId);
//
//			//create minimum user protos for users associated with clan
//			for (int userId: clanIdsToUserIdSet.get(clanId)) {
//				User u = userIdsToUsers.get(userId);
//				MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevel(u, c, null);
//				userIdToMinimumUserProtoWithLevel.put(userId, mupwl);
//			}
//		}
		for (String clanId : clanIdsToUserIdSet.keySet()) {                                          
			
			//precautionary measure
			if (!clanIdsToClans.containsKey(clanId)) {
				String preface = String.format("%s, %s",
					"createMinimumUserProtosFromClannedAndClanlessUsers()",
					"no clan. curClanId=");
				log.error(String.format(
					"%s %s. clanIdsToUserIdSet=%s, clanIdsToClans=%s",
					preface, clanId, clanIdsToUserIdSet, clanIdsToClans));
			}
			Clan c = clanIdsToClans.get(clanId);                                              
		                                                                                      
			//create minimum user protos for users associated with clan                       
			for (String userId: clanIdsToUserIdSet.get(clanId)) {                                
				User u = userIdsToUsers.get(userId);                                          
				MinimumUserProtoWithLevel mupwl = createMinimumUserProtoWithLevel(u, c, null);
				userIdToMinimumUserProtoWithLevel.put(userId, mupwl);                         
			}                                                                                 
		}                                                                                     
	}

}
