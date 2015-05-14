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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.lvl6.info.*;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniJobRefreshItemConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBattleHistoryPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.StructureForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.UserPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.AchievementStuffProto.AchievementProto;
import com.lvl6.proto.AchievementStuffProto.AchievementProto.AchievementType;
import com.lvl6.proto.AchievementStuffProto.UserAchievementProto;
import com.lvl6.proto.BattleItemsProto.BattleItemCategory;
import com.lvl6.proto.BattleItemsProto.BattleItemProto;
import com.lvl6.proto.BattleItemsProto.BattleItemQueueForUserProto;
import com.lvl6.proto.BattleItemsProto.BattleItemType;
import com.lvl6.proto.BattleItemsProto.UserBattleItemProto;
import com.lvl6.proto.BattleProto.BattleReplayProto;
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
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.proto.ChatProto.DefaultLanguagesProto;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.ChatProto.PrivateChatDefaultLanguageProto;
import com.lvl6.proto.ChatProto.PrivateChatPostProto;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.ChatProto.TranslatedTextProto;
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
import com.lvl6.proto.CustomMenusProto.CustomMenuProto;
import com.lvl6.proto.EventPvpProto.StructStolen;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.AnimatedSpriteOffsetProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.FileDownloadConstantProto;
import com.lvl6.proto.InAppPurchaseProto.GoldSaleProto;
import com.lvl6.proto.ItemsProto.ItemGemPriceProto;
import com.lvl6.proto.ItemsProto.ItemProto;
import com.lvl6.proto.ItemsProto.ItemType;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ItemsProto.UserItemSecretGiftProto;
import com.lvl6.proto.ItemsProto.UserItemUsageProto;
import com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto;
import com.lvl6.proto.MiniEventProtos.MiniEventForPlayerLevelProto;
import com.lvl6.proto.MiniEventProtos.MiniEventGoalProto;
import com.lvl6.proto.MiniEventProtos.MiniEventGoalProto.MiniEventGoalType;
import com.lvl6.proto.MiniEventProtos.MiniEventLeaderboardRewardProto;
import com.lvl6.proto.MiniEventProtos.MiniEventProto;
import com.lvl6.proto.MiniEventProtos.MiniEventTierRewardProto;
import com.lvl6.proto.MiniEventProtos.UserMiniEventGoalProto;
import com.lvl6.proto.MiniEventProtos.UserMiniEventProto;
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
import com.lvl6.proto.ResearchsProto.UserResearchProto;
import com.lvl6.proto.RewardsProto.ClanGiftProto;
import com.lvl6.proto.RewardsProto.RewardProto;
import com.lvl6.proto.RewardsProto.RewardProto.RewardType;
import com.lvl6.proto.RewardsProto.TangoGiftProto;
import com.lvl6.proto.RewardsProto.UserClanGiftProto;
import com.lvl6.proto.RewardsProto.UserGiftProto;
import com.lvl6.proto.RewardsProto.UserRewardProto;
import com.lvl6.proto.RewardsProto.UserTangoGiftProto;
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
import com.lvl6.proto.UserProto.MinimumUserProtoWithMaxResources;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.proto.UserProto.UserPvpLeagueProto;
import com.lvl6.pvp.PvpUser;
import com.lvl6.retrieveutils.ClanHelpCountForUserRetrieveUtil.UserClanHelpCount;
import com.lvl6.retrieveutils.MonsterForUserRetrieveUtils2;
import com.lvl6.retrieveutils.TaskForUserCompletedRetrieveUtils.UserTaskCompleted;
import com.lvl6.retrieveutils.TranslationSettingsForUserRetrieveUtil;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.ChatTranslationsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanGiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;

@Component
@DependsOn("gameServer")
public class CreateInfoProtoUtils {

	@Autowired
	protected ClanGiftRetrieveUtils clanGiftRetrieveUtils;

	@Autowired
	protected ClanRaidStageRetrieveUtils clanRaidStageRetrieveUtils;

	@Autowired
	protected ClanRaidStageMonsterRetrieveUtils clanRaidStageMonsterRetrieveUtils;

	@Autowired
	protected ClanRaidStageRewardRetrieveUtils clanRaidStageRewardRetrieveUtils;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;

	@Autowired
	protected MiniJobRetrieveUtils miniJobRetrieveUtils;

	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils;

	@Autowired
	protected TaskStageRetrieveUtils taskStageRetrieveUtils;

	@Autowired
	protected QuestJobRetrieveUtils questJobRetrieveUtils;

	@Autowired
	protected ChatTranslationsRetrieveUtils chatTranslationsRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());


	/** Achievement.proto ***************************************************/
	public AchievementProto createAchievementProto(Achievement a) {
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
			} catch (Exception e) {
				log.error(String.format(
						"invalid AchievementType. achievement=%s", a), e);
			}
		}

		str = a.getResourceType();
		if (null != str) {
			try {
				ResourceType rt = ResourceType.valueOf(str);
				ab.setResourceType(rt);
			} catch (Exception e) {
				log.error(String.format("invalid ResourceType. achievement=%s",
						a), e);
			}
		}

		str = a.getMonsterElement();
		if (null != str) {
			try {
				Element me = Element.valueOf(str);
				ab.setElement(me);
			} catch (Exception e) {
				log.error(String.format(
						"invalid MonsterElement. achievement=%s", a), e);
			}
		}

		str = a.getMonsterQuality();
		if (null != str) {
			try {
				Quality mq = Quality.valueOf(str);
				ab.setQuality(mq);
			} catch (Exception e) {
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

	public UserAchievementProto createUserAchievementProto(
			AchievementForUser afu) {
		UserAchievementProto.Builder uapb = UserAchievementProto.newBuilder();

		uapb.setAchievementId(afu.getAchievementId());
		uapb.setProgress(afu.getProgress());
		uapb.setIsComplete(afu.isComplete());
		uapb.setIsRedeemed(afu.isRedeemed());

		return uapb.build();
	}

	/** Battle.proto ***************************************************/

	public PvpProto createPvpProto(User defender, Clan clan,
			PvpLeagueForUser plfu, PvpUser pu,
			Collection<MonsterForUser> userMonsters,
			Map<String, Integer> userMonsterIdToDropped,
			int prospectiveCashStolenFromStorage, int prospectiveOilStolenFromStorage,
			ClanMemberTeamDonation cmtd, MonsterSnapshotForUser msfu,
			int msfuMonsterIdDropped,
			List<PvpBoardObstacleForUser> boardObstacles,
			List<ResearchForUser> rfuList,
			List<StructureForUserPojo> sfuList,
			float percentageToStealFromGenerator) {

		FullUserProto defenderProto = createFullUserProtoFromUser(defender, plfu, clan);

		String userId = defender.getId();
		String msg = defender.getPvpDefendingMessage();

		return createPvpProto(userId, plfu, pu, userMonsters,
				userMonsterIdToDropped, prospectiveCashStolenFromStorage,
				prospectiveOilStolenFromStorage, defenderProto, msg, cmtd, msfu,
				msfuMonsterIdDropped, boardObstacles, rfuList, sfuList,
				percentageToStealFromGenerator);
	}

	public PvpProto createPvpProto(String defenderId,
			PvpLeagueForUser plfu, PvpUser pu,
			Collection<MonsterForUser> userMonsters,
			Map<String, Integer> userMonsterIdToDropped,
			int prospectiveCashStolenFromStorage, int prospectiveOilStolenFromStorage,
			FullUserProto defender, String defenderMsg,
			ClanMemberTeamDonation cmtd, MonsterSnapshotForUser msfu,
			int msfuMonsterIdDropped,
			List<PvpBoardObstacleForUser> boardObstacles,
			List<ResearchForUser> rfuList,
			List<StructureForUserPojo> sfuList,
			float percentageToStealFromGenerator) {
		PvpProto.Builder ppb = PvpProto.newBuilder();

		Collection<PvpMonsterProto> defenderMonsters = createPvpMonsterProto(
				userMonsters, userMonsterIdToDropped);
		ppb.addAllDefenderMonsters(defenderMonsters);

		ppb.setDefender(defender);
		ppb.setProspectiveCashStolenFromStorage(prospectiveCashStolenFromStorage);
		ppb.setProspectiveOilStolenFromStorage(prospectiveOilStolenFromStorage);

		UserPvpLeagueProto uplp = createUserPvpLeagueProto(defenderId, plfu,
				pu, true);
		ppb.setPvpLeagueStats(uplp);

		if (null != defenderMsg) {
			ppb.setDefenderMsg(defenderMsg);
		}

		//account for clan donated monster
		if (null != cmtd && null != msfu) {
			ClanMemberTeamDonationProto.Builder cmtdpb = ClanMemberTeamDonationProto
					.newBuilder();
			cmtdpb.setDonationUuid(cmtd.getId());
			cmtdpb.setClanUuid(cmtd.getClanId());

			UserMonsterSnapshotProto umsp = createUserMonsterSnapshotProto(
					msfu, null);

			cmtdpb.addDonations(umsp);
			ppb.setCmtd(cmtdpb.build());
			if (msfuMonsterIdDropped > 0) {
				ppb.setMonsterIdDropped(msfuMonsterIdDropped);
			}
		}

		if (null != boardObstacles && !boardObstacles.isEmpty()) {
			List<UserPvpBoardObstacleProto> upbops = createUserPvpBoardObstacleProto(boardObstacles);
			ppb.addAllUserBoardObstacles(upbops);
		}

		if (null != rfuList && !rfuList.isEmpty()) {
			Collection<UserResearchProto> urpList = createUserResearchProto(rfuList);
			ppb.addAllUserResearch(urpList);
		}

		if (null != sfuList && !sfuList.isEmpty()) {
			List<FullUserStructureProto> uspList = new ArrayList<FullUserStructureProto>();
			for(StructureForUserPojo sfu : sfuList) {
				uspList.add(createFullUserStructureProtoFromUserStructPojo(sfu));

			}
			ppb.addAllUserStructure(uspList);
		}
		
		ppb.setPercentageToStealFromGenerator(percentageToStealFromGenerator);

		return ppb.build();
	}

	public Collection<PvpMonsterProto> createPvpMonsterProto(
			Collection<MonsterForUser> userMonsters,
			Map<String, Integer> userMonsterIdToDropped) {
		List<PvpMonsterProto> pmpList = new ArrayList<PvpMonsterProto>();

		for (MonsterForUser mfu : userMonsters) {
			String userMonsterId = mfu.getId();
			MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfu);

			PvpMonsterProto pmp = createPvpMonsterProto(userMonsterIdToDropped,
					userMonsterId, mump);

			pmpList.add(pmp);
		}
		return pmpList;
	}

	private PvpMonsterProto createPvpMonsterProto(
			Map<String, Integer> userMonsterIdToDropped, String userMonsterId,
			MinimumUserMonsterProto mump) {
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

	private Collection<PvpMonsterProto> createPvpMonsterProto(
			List<MonsterForPvp> mfpList, List<Integer> monsterIdsDropped) {
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

	private PvpMonsterProto createPvpMonsterProto(
			MinimumUserMonsterProto mump, Integer monsterIdDropped) {
		PvpMonsterProto.Builder pmpb = PvpMonsterProto.newBuilder();
		pmpb.setDefenderMonster(mump);
		if (monsterIdDropped > 0) {
			pmpb.setMonsterIdDropped(monsterIdDropped);
		}
		PvpMonsterProto pmp = pmpb.build();

		return pmp;
	}

	public MinimumUserProtoWithMaxResources createMinimumUserProtoWithMaxResources(
			MinimumUserProto mup, int maxCash, int maxOil) {
		MinimumUserProtoWithMaxResources.Builder mupwmrb = MinimumUserProtoWithMaxResources
				.newBuilder();
		mupwmrb.setMinUserProto(mup);
		mupwmrb.setMaxCash(maxCash);
		mupwmrb.setMaxOil(maxOil);
		MinimumUserProtoWithMaxResources mupwmr = mupwmrb.build();

		return mupwmr;
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
	public PvpProto createFakePvpProto(String userId, String name,
			int lvl, int elo, int prospectiveCashStolenFromStorage,
			int prospectiveOilStolenFromStorage, List<MonsterForPvp> mfpList,
			List<Integer> monsterIdsDropped, boolean setElo) {

		//create the fake user
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		//mupb.setUserUuid(userId); // fake user will never have id
		fupb.setName(name);
		FullUserProto fup = fupb.build();

		//THE ACTUAL PROTO
		PvpProto.Builder ppb = PvpProto.newBuilder();
		ppb.setDefender(fup);
		//    ppb.setCurElo(elo);

		//set the defenderMonsters
		Collection<PvpMonsterProto> pmpList = createPvpMonsterProto(mfpList,
				monsterIdsDropped);
		ppb.addAllDefenderMonsters(pmpList);

		ppb.setProspectiveCashStolenFromStorage(prospectiveCashStolenFromStorage);
		ppb.setProspectiveOilStolenFromStorage(prospectiveOilStolenFromStorage);

		UserPvpLeagueProto uplp = createFakeUserPvpLeagueProto(userId, elo,
				setElo);
		ppb.setPvpLeagueStats(uplp);

		return ppb.build();
	}

	public List<PvpProto> createPvpProtos(List<User> queuedOpponents,
			Map<String, Clan> userIdToClan,
			Map<String, PvpLeagueForUser> userIdToLeagueInfo,
			Map<String, PvpUser> userIdToPvpUser,
			Map<String, List<MonsterForUser>> userIdToUserMonsters,
			Map<String, Map<String, Integer>> userIdToUserMonsterIdToDropped,
			Map<String, Integer> userIdToCashReward,
			Map<String, Integer> userIdToOilReward,
			Map<String, ClanMemberTeamDonation> userIdToCmtd,
			Map<String, MonsterSnapshotForUser> userIdToMsfu,
			Map<String, Integer> userIdToMsfuMonsterIdDropped,
			Map<String, List<PvpBoardObstacleForUser>> userIdToPvpBoardObstacles,
			Map<String, List<ResearchForUser>> userIdToRfuList,
			Map<String, List<StructureForUserPojo>> userIdToSfuList,
			Map<String, Float> userIdToPercentageStealFromGenerator) {
		List<PvpProto> pvpProtoList = new ArrayList<PvpProto>();

		for (User u : queuedOpponents) {
			String userId = u.getId();
			PvpLeagueForUser plfu = null;
			if (null != userIdToLeagueInfo
					&& userIdToLeagueInfo.containsKey(userId)) {
				plfu = userIdToLeagueInfo.get(userId);
			}

			PvpUser pu = null;
			if (null != userIdToPvpUser && userIdToPvpUser.containsKey(userId)) {
				pu = userIdToPvpUser.get(userId);
			}
			List<MonsterForUser> userMonsters = userIdToUserMonsters
					.get(userId);
			int prospectiveCashWinnings = 0;
			if(userIdToCashReward.containsKey(userId)) {
				prospectiveCashWinnings = userIdToCashReward.get(userId);;
			}
			
			int prospectiveOilWinnings = 0;
			if(userIdToOilReward.containsKey(userId)) {
				prospectiveOilWinnings = userIdToOilReward.get(userId);;
			}

			Clan clan = null;
			if (userIdToClan.containsKey(userId)) {
				clan = userIdToClan.get(userId);
			}

			Map<String, Integer> userMonsterIdToDropped = userIdToUserMonsterIdToDropped
					.get(userId);

			ClanMemberTeamDonation cmtd = null;
			if (null != userIdToCmtd && userIdToCmtd.containsKey(userId)) {
				cmtd = userIdToCmtd.get(userId);
			}
			MonsterSnapshotForUser msfu = null;
			if (null != userIdToMsfu && userIdToMsfu.containsKey(userId)) {
				msfu = userIdToMsfu.get(userId);
			}
			int msfuMonsterIdDropped = 0;
			if (null != userIdToMsfuMonsterIdDropped
					&& userIdToMsfuMonsterIdDropped.containsKey(userId)) {
				msfuMonsterIdDropped = userIdToMsfuMonsterIdDropped.get(userId);
			}

			List<PvpBoardObstacleForUser> boardObstacles = null;
			if (userIdToPvpBoardObstacles.containsKey(userId)) {
				boardObstacles = userIdToPvpBoardObstacles.get(userId);
			}

			List<ResearchForUser> rfuList = null;
			if (userIdToRfuList.containsKey(userId)) {
				rfuList = userIdToRfuList.get(userId);
			}

			List<StructureForUserPojo> sfuList = null;
			if (userIdToSfuList.containsKey(userId)) {
				sfuList = userIdToSfuList.get(userId);
			}
			
			float percentageToStealFromGenerator = 0;
			if(userIdToPercentageStealFromGenerator.containsKey(userId)) {
				percentageToStealFromGenerator = 
						userIdToPercentageStealFromGenerator.get(userId);
			}

			PvpProto pp = createPvpProto(u, clan, plfu, pu, userMonsters,
					userMonsterIdToDropped, prospectiveCashWinnings,
					prospectiveOilWinnings, cmtd, msfu, msfuMonsterIdDropped,
					boardObstacles, rfuList, sfuList, percentageToStealFromGenerator);
			pvpProtoList.add(pp);
		}
		return pvpProtoList;
	}

	public PvpHistoryProto createGotAttackedPvpHistoryProto(
			User attacker, Clan c, 
			PvpBattleHistoryPojo info,
			Collection<MonsterForUser> userMonsters,
			Map<String, Integer> userMonsterIdToDropped,
			int prospectiveCashWinnings, int prospectiveOilWinnings,
			String replayId, int cashStolenFromStorage,
			int cashStolenFromGenerators, int oilStolenFromStorage,
			int oilStolenFromGenerators)
			//BattleReplayForUser brfu)
	{
		PvpHistoryProto.Builder phpb = PvpHistoryProto.newBuilder();
		FullUserProto fup = createFullUserProtoFromUser(attacker, null, c);
		phpb.setAttacker(fup);

		if (null != userMonsters && !userMonsters.isEmpty()) {
			Collection<PvpMonsterProto> attackerMonsters = createPvpMonsterProto(
					userMonsters, userMonsterIdToDropped);
			phpb.addAllAttackersMonsters(attackerMonsters);
		}
		phpb.setProspectiveCashWinnings(prospectiveCashWinnings);
		phpb.setProspectiveOilWinnings(prospectiveOilWinnings);

		phpb.setCashStolenFromStorage(cashStolenFromStorage);
		phpb.setCashStolenFromGenerators(cashStolenFromGenerators);
		phpb.setOilStolenFromStorage(oilStolenFromStorage);
		phpb.setOilStolenFromGenerators(oilStolenFromGenerators);
		
		modifyPvpHistoryProto(phpb, info);

//		if (null != brfu) {
//			BattleReplayProto brp = createBattleReplayProto(brfu);
//			phpb.setReplay(brp);
//		}
		if (null != replayId && !replayId.isEmpty()) {
			phpb.setReplayId(replayId);
		}
		return phpb.build();
	}

	public List<PvpHistoryProto> createGotAttackedPvpHistoryProto(
			List<PvpBattleHistoryPojo> historyList,
			Map<String, User> attackerIdsToAttackers,
			Map<String, Clan> attackerIdsToClans,
			Map<String, List<MonsterForUser>> attackerIdsToUserMonsters,
			Map<String, Map<String, Integer>> userIdToUserMonsterIdToDropped,
			Map<String, Integer> attackerIdsToProspectiveCashWinnings,
			Map<String, Integer> attackerIdsToProspectiveOilWinnings,
			Map<String, Integer> cashStolenFromStorageMap, 
			Map<String, Integer> cashStolenFromGeneratorsMap,
			Map<String, Integer> oilStolenFromStorageMap, 
			Map<String, Integer> oilStolenFromGeneratorsMap)
	{

		List<PvpHistoryProto> phpList = new ArrayList<PvpHistoryProto>();

		for (PvpBattleHistoryPojo history : historyList) {
			String attackerId = history.getAttackerId();

			User attacker = attackerIdsToAttackers.get(attackerId);
			List<MonsterForUser> attackerMonsters = attackerIdsToUserMonsters
					.get(attackerId);
			int prospectiveCashWinnings = attackerIdsToProspectiveCashWinnings
					.get(attackerId);
			int prospectiveOilWinnings = attackerIdsToProspectiveOilWinnings
					.get(attackerId);

			String clanId = attacker.getClanId();
			Clan clan = null;
			if (attackerIdsToClans.containsKey(clanId)) {
				clan = attackerIdsToClans.get(clanId);
			}

			Map<String, Integer> userMonsterIdToDropped = userIdToUserMonsterIdToDropped
					.get(attackerId);

			String replayId = history.getReplayId();
//			BattleReplayForUser brfu = null;
//			if (replayIdToReplay.containsKey(replayId))
//			{
//				brfu = replayIdToReplay.get(replayId);
//			}

			int cashStolenFromStorage = cashStolenFromStorageMap.get(attackerId);
			int cashStolenFromGenerators = cashStolenFromGeneratorsMap.get(attackerId);
			int oilStolenFromStorage = oilStolenFromStorageMap.get(attackerId);
			int oilStolenFromGenerators = oilStolenFromGeneratorsMap.get(attackerId);
			
			
			PvpHistoryProto php = createGotAttackedPvpHistoryProto(attacker,
					clan, history, attackerMonsters, userMonsterIdToDropped,
					prospectiveCashWinnings, prospectiveOilWinnings,
					replayId, cashStolenFromStorage, cashStolenFromGenerators,
					oilStolenFromStorage, oilStolenFromGenerators);// brfu);
			phpList.add(php);
		}
		return phpList;
	}

	public List<PvpHistoryProto> createAttackedOthersPvpHistoryProto(
			String attackerId, Map<String, User> idsToUsers,
			List<PvpBattleHistoryPojo> historyList) {
		List<PvpHistoryProto> phpList = new ArrayList<PvpHistoryProto>();
		FullUserProto.Builder fupb = FullUserProto.newBuilder();
		fupb.setUserUuid(attackerId);
		FullUserProto fup = fupb.build();

		for (PvpBattleHistoryPojo pbh : historyList) {
			//no fake users are displayed, but check in case
			String defenderId = pbh.getDefenderId();
			if (null == defenderId || defenderId.isEmpty()) {
				continue;
			}

			User defender = idsToUsers.get(defenderId);
			FullUserProto defenderFup = createFullUserProtoFromUser(defender,
					null, null);

//			BattleReplayForUser brfu = null;
			String replayId = pbh.getReplayId();
//			if (null != replayId && replayIdToReplay.containsKey(replayId))
//			{
//				brfu = replayIdToReplay.get(replayId);
//			}
			PvpHistoryProto php = createAttackedOthersPvpHistoryProto(fup,
					defenderFup, pbh, replayId); //brfu);

			phpList.add(php);
		}

		return phpList;
	}

	public PvpHistoryProto createAttackedOthersPvpHistoryProto(
			FullUserProto fup, FullUserProto defenderFup, 
			PvpBattleHistoryPojo info,
			String replayId) //BattleReplayForUser brfu)
	{
		PvpHistoryProto.Builder phpb = PvpHistoryProto.newBuilder();
		phpb.setAttacker(fup);
		phpb.setDefender(defenderFup);

		modifyPvpHistoryProto(phpb, info);

//		if (null != brfu)
//		{
//			BattleReplayProto brp = createBattleReplayProto(brfu);
//			phpb.setReplay(brp);
//		}

		if (null != replayId) {
			phpb.setReplayId(replayId);
		}

		return phpb.build();
	}

	private void modifyPvpHistoryProto(Builder phpb,
			PvpBattleHistoryPojo info) {
		phpb.setAttackerWon(info.getAttackerWon());

		int defenderCashChange = info.getDefenderCashChange();
		phpb.setDefenderCashChange(defenderCashChange);
		int defenderOilChange = info.getDefenderOilChange();
		phpb.setDefenderOilChange(defenderOilChange);

		phpb.setExactedRevenge(info.getExactedRevenge());

		Date endDate = info.getBattleEndTime();
		//endDate should not be null, it's the primary key
		phpb.setBattleEndTime(endDate.getTime());

		UserPvpLeagueProto attackerBefore = createUserPvpLeagueProto(
				info.getAttackerId(), info.getAttackerPrevLeague(),
				info.getAttackerPrevRank(), info.getAttackerEloBefore(), false);
		phpb.setAttackerBefore(attackerBefore);
		UserPvpLeagueProto attackerAfter = createUserPvpLeagueProto(
				info.getAttackerId(), info.getAttackerCurLeague(),
				info.getAttackerCurRank(), info.getAttackerEloAfter(), false);
		phpb.setAttackerAfter(attackerAfter);

		UserPvpLeagueProto defenderBefore = createUserPvpLeagueProto(
				info.getDefenderId(), info.getDefenderPrevLeague(),
				info.getDefenderPrevRank(), info.getDefenderEloBefore(), false);
		phpb.setDefenderBefore(defenderBefore);
		UserPvpLeagueProto defenderAfter = createUserPvpLeagueProto(
				info.getDefenderId(), info.getDefenderCurLeague(),
				info.getDefenderCurRank(), info.getDefenderEloAfter(), false);
		phpb.setDefenderAfter(defenderAfter);

		int attackerCashChange = info.getAttackerCashChange();
		phpb.setAttackerCashChange(attackerCashChange);
		int attackerOilChange = info.getAttackerOilChange();
		phpb.setAttackerOilChange(attackerOilChange);

		phpb.setClanAvenged(info.getClanAvenged());
	}

	public PvpLeagueProto createPvpLeagueProto(PvpLeague pl) {
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

	public UserPvpLeagueProto createUserPvpLeagueProto(String userId,
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
			uplpb.setBattlesWon(plfu.getBattlesWon());
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
			uplpb.setBattlesWon(pu.getBattlesWon());
			uplpb.setMonsterDmgMultiplier(pu.getMonsterDmgMultiplier());

		}

		return uplpb.build();
	}

	public UserPvpLeagueProto createUserPvpLeagueProto(String userId,
			int pvpLeagueId, int rank, int elo, boolean setElo) {
		UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
		uplpb.setUserUuid(userId);
		uplpb.setLeagueId(pvpLeagueId);
		uplpb.setRank(rank);

		if (setElo) {
			uplpb.setElo(elo);
		}

		return uplpb.build();
	}

	public UserPvpLeagueProto createFakeUserPvpLeagueProto(
			String userId, int elo, boolean setElo) {
		UserPvpLeagueProto.Builder uplpb = UserPvpLeagueProto.newBuilder();
		//uplpb.setUserUuid(userId);

		int leagueId = pvpLeagueRetrieveUtils.getLeagueIdForElo(elo, 0);
		uplpb.setLeagueId(leagueId);
		int rank = pvpLeagueRetrieveUtils.getRankForElo(elo, leagueId);
		uplpb.setRank(rank);

		uplpb.setMonsterDmgMultiplier(ControllerConstants.PVP__MONSTER_DMG_MULTIPLIER);

		if (setElo) {
			uplpb.setElo(elo);
		}

		return uplpb.build();
	}

	public List<PvpClanAvengeProto> createPvpClanAvengeProto(
			List<ClanAvenge> retaliations,
			Map<String, List<ClanAvengeUser>> clanAvengeIdToClanAvengeUser,
			Map<String, User> userIdsToUsers, Map<String, Clan> userIdsToClans) {
		List<PvpClanAvengeProto> pcapList = new ArrayList<PvpClanAvengeProto>();

		Map<String, MinimumUserProto> userIdToMup = createMinimumUserProtoFromUserAndClan(
				userIdsToUsers, userIdsToClans);

		for (ClanAvenge ca : retaliations) {
			String clanAvengeId = ca.getId();
			List<ClanAvengeUser> cauList = null;

			if (clanAvengeIdToClanAvengeUser.containsKey(clanAvengeId)) {
				cauList = clanAvengeIdToClanAvengeUser.get(clanAvengeId);
			}

			PvpClanAvengeProto pcap = createPvpClanAvengeProto(ca, cauList,
					userIdToMup);
			pcapList.add(pcap);
		}
		return pcapList;
	}

	public PvpClanAvengeProto createPvpClanAvengeProto(ClanAvenge ca,
			List<ClanAvengeUser> cauList,
			Map<String, MinimumUserProto> userIdToMup) {
		String attackerId = ca.getAttackerId();
		String defenderId = ca.getDefenderId();
		String defenderClanUuid = ca.getClanId();

		MinimumUserProto attacker = userIdToMup.get(attackerId);
		MinimumUserProto defender = userIdToMup.get(defenderId);

		PvpClanAvengeProto.Builder pcapb = PvpClanAvengeProto.newBuilder();
		pcapb.setClanAvengeUuid(ca.getId());
		pcapb.setAttacker(attacker);
		pcapb.setDefender(defender);

		Date time = ca.getBattleEndTime();
		pcapb.setBattleEndTime(time.getTime());

		time = ca.getAvengeRequestTime();
		pcapb.setAvengeRequestTime(time.getTime());

		pcapb.setDefenderClanUuid(defenderClanUuid);

		//could be that no clan mate started retaliating
		//against person who attacked clan member
		if (null != cauList && !cauList.isEmpty()) {
			List<PvpUserClanAvengeProto> pucapList = createPvpUserClanAvengeProto(cauList);
			pcapb.addAllUsersAvenging(pucapList);
		}

		return pcapb.build();
	}

	public List<PvpUserClanAvengeProto> createPvpUserClanAvengeProto(
			List<ClanAvengeUser> cauList) {
		List<PvpUserClanAvengeProto> pucapList = new ArrayList<PvpUserClanAvengeProto>();

		for (ClanAvengeUser cau : cauList) {
			PvpUserClanAvengeProto pucap = createPvpUserClanAvengeProto(cau);
			pucapList.add(pucap);
		}

		return pucapList;
	}

	public PvpUserClanAvengeProto createPvpUserClanAvengeProto(
			ClanAvengeUser cau) {
		PvpUserClanAvengeProto.Builder pucapb = PvpUserClanAvengeProto
				.newBuilder();

		pucapb.setUserUuid(cau.getUserId());
		pucapb.setClanAvengeUuid(cau.getClanAvengeId());
		pucapb.setClanUuid(cau.getClanId());

		Date date = cau.getAvengeTime();
		pucapb.setAvengeTime(date.getTime());

		return pucapb.build();
	}

	public List<PvpClanAvengeProto> createPvpClanAvengeProto(
			List<ClanAvenge> retaliations, MinimumUserProto defenderMup,
			String clanUuid,
			Map<String, MinimumUserProto> attackerIdsToMups) {
		List<PvpClanAvengeProto> pcapList = new ArrayList<PvpClanAvengeProto>();

		for (ClanAvenge ca : retaliations) {
			String attackerId = ca.getAttackerId();
			MinimumUserProto attackerMup = attackerIdsToMups
					.get(attackerId);

			PvpClanAvengeProto pcap = createPvpClanAvengeProto(attackerMup,
					defenderMup, clanUuid, ca);

			pcapList.add(pcap);
		}
		return pcapList;
	}

	public PvpClanAvengeProto createPvpClanAvengeProto(
			MinimumUserProto attacker, MinimumUserProto defender,
			String defenderClanUuid, ClanAvenge ca) {
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
	public BattleItemQueueForUserProto createBattleItemQueueForUserProto(
			BattleItemQueueForUser biqfu) {
		BattleItemQueueForUserProto.Builder biqfupb = BattleItemQueueForUserProto
				.newBuilder();
		biqfupb.setUserUuid(biqfu.getUserId());
		biqfupb.setBattleItemId(biqfu.getBattleItemId());
		biqfupb.setExpectedStartTime(biqfu.getExpectedStartTime().getTime());
		biqfupb.setPriority(biqfu.getPriority());
		biqfupb.setElapsedTime(biqfu.getElapsedTime());

		return biqfupb.build();
	}

	public List<BattleItemQueueForUserProto> createBattleItemQueueForUserProtoList(
			List<BattleItemQueueForUser> biqfuList) {
		List<BattleItemQueueForUserProto> biqfupList = new ArrayList<BattleItemQueueForUserProto>();
		for (BattleItemQueueForUser biqfu : biqfuList) {
			BattleItemQueueForUserProto.Builder biqfupb = BattleItemQueueForUserProto
					.newBuilder();
			biqfupb.setUserUuid(biqfu.getUserId());
			biqfupb.setBattleItemId(biqfu.getBattleItemId());
			biqfupb.setExpectedStartTime(biqfu.getExpectedStartTime().getTime());
			biqfupb.setPriority(biqfu.getPriority());
			biqfupb.setElapsedTime(biqfu.getElapsedTime());
			biqfupList.add(biqfupb.build());
		}
		return biqfupList;
	}

	public BattleReplayProto createBattleReplayProto(BattleReplayForUser brfu) {
		BattleReplayProto.Builder brpb = BattleReplayProto.newBuilder();
		brpb.setReplayUuid(brfu.getId());
		brpb.setCreatorUuid(brfu.getCreatorId());

		try {
			byte[] bites = brfu.getReplay();
			ByteString bs = ByteString.copyFrom(bites);
			brpb.setReplay(bs);

		} catch (Exception e) {
			log.error(String.format(
					"unable to convert byte[] to google.ByteString, brfu=%s",
					brfu), e);
		}

		return brpb.build();
	}

	/** Board.proto ****************************************/
	public BoardLayoutProto createBoardLayoutProto(Board b,
			Collection<BoardProperty> boardProperties) {
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

	public List<BoardPropertyProto> createBoardPropertyProto(
			Collection<BoardProperty> bpCollection) {
		List<BoardPropertyProto> retVal = new ArrayList<BoardPropertyProto>();
		for (BoardProperty bp : bpCollection) {
			BoardPropertyProto bpp = createBoardPropertyProto(bp);

			retVal.add(bpp);
		}

		return retVal;
	}

	public BoardPropertyProto createBoardPropertyProto(BoardProperty bp) {
		BoardPropertyProto.Builder blpb = BoardPropertyProto.newBuilder();

		blpb.setBoardPropertyId(bp.getId());
		blpb.setBoardId(bp.getId());

		String str = bp.getName();
		if (null != str && !str.isEmpty()) {
			blpb.setName(str);
		}

		blpb.setPosX(bp.getPosX());
		blpb.setPosY(bp.getPosY());

		str = bp.getElement();
		if (null != str && !str.isEmpty()) {
			try {
				Element elem = Element.valueOf(str);
				blpb.setElem(elem);
			} catch (Exception e) {
				log.error(
						String.format("invalid element. BoardProperty=%s", bp),
						e);
			}
		}
		blpb.setValue(bp.getValue());
		blpb.setQuantity(bp.getQuanity());

		return blpb.build();
	}

	/** BoosterPackStuff.proto ****************************************/
	//  public static RareBoosterPurchaseProto createRareBoosterPurchaseProto(BoosterPack bp, User u, Date d) {
	//    return RareBoosterPurchaseProto.newBuilder().setBooster(createBoosterPackProto(bp, null))
	//        .setUser(createMinimumUserProtoFromUser(u))
	//        .setTimeOfPurchase(d.getTime()).build();
	//  }

	public BoosterPackProto createBoosterPackProto(BoosterPack bp,
			Collection<BoosterItem> biList,
			Collection<BoosterDisplayItem> bdiList,
			RewardRetrieveUtils rewardRetrieveUtils) {
		BoosterPackProto.Builder b = BoosterPackProto.newBuilder();
		b.setBoosterPackId(bp.getId());

		String str = bp.getName();
		if (null != str && !str.isEmpty()) {
			b.setBoosterPackName(str);
		}

		b.setGemPrice(bp.getGemPrice());
		b.setGachaCreditsPrice(bp.getGachaCreditsPrice());

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
				BoosterPackType bpt = BoosterPackType.valueOf(str);
				b.setType(bpt);
			} catch (Exception e) {
				log.error(String.format(
						"invalid BoosterPackType. BoosterPack=%s", bp), e);
			}
		}

		if (biList != null) {
			for (BoosterItem bi : biList) {
				//only want special booster items
				if (bi.isSpecial()) {
					BoosterItemProto bip = createBoosterItemProto(bi,
							rewardRetrieveUtils);
					b.addSpecialItems(bip);
				}
			}
		}

		if (null != bdiList) {
			for (BoosterDisplayItem bdi : bdiList) {
				BoosterDisplayItemProto bdip = createBoosterDisplayItemProto(bdi,
						rewardRetrieveUtils);
				b.addDisplayItems(bdip);
			}
		}

		return b.build();
	}

	public BoosterItemProto createBoosterItemProto(BoosterItem bi,
			RewardRetrieveUtils rewardRetrieveUtils) {
		BoosterItemProto.Builder b = BoosterItemProto.newBuilder();
		b.setBoosterItemId(bi.getId());
		b.setBoosterPackId(bi.getBoosterPackId());

		if (bi.getRewardId() > 0) {
			Reward r = rewardRetrieveUtils.getRewardById(bi.getRewardId());
			b.setReward(createRewardProto(r));
		}

		return b.build();
	}

	public BoosterDisplayItemProto createBoosterDisplayItemProto(
			BoosterDisplayItem bdi, RewardRetrieveUtils rewardRetrieveUtils) {
		BoosterDisplayItemProto.Builder b = BoosterDisplayItemProto
				.newBuilder();

		b.setBoosterPackId(bdi.getBoosterPackId());
		if (bdi.getRewardId() > 0) {
			Reward r = rewardRetrieveUtils.getRewardById(bdi.getRewardId());
			b.setReward(createRewardProto(r));
		}

		return b.build();
	}

	/** Chat.proto *****************************************************/
	public PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPost(
			PrivateChatPost p, User poster, Clan posterClan, User recipient,
			Clan recipientClan, Map<TranslateLanguages, String> translatedMessage,
			TranslateLanguages contentLanguage) {
		MinimumUserProto mupPoster = createMinimumUserProtoFromUserAndClan(
				poster, posterClan);
		MinimumUserProto mupRecipient = createMinimumUserProtoFromUserAndClan(
				recipient, recipientClan);

		// Truncate time because db truncates it (?)
		long time = p.getTimeOfPost().getTime();
		time = time - time % 1000;

		PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();
		pcppb.setPrivateChatPostUuid(p.getId());
		pcppb.setPoster(mupPoster);
		pcppb.setRecipient(mupRecipient);
		pcppb.setTimeOfPost(time);
		pcppb.setContent(p.getContent());

		if(contentLanguage != null) {
			pcppb.setOriginalContentLanguage(contentLanguage);
		}

		if(translatedMessage != null) {
			for(TranslateLanguages tl : translatedMessage.keySet()) {
				TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();
				ttpb.setLanguage(tl);
				ttpb.setText(translatedMessage.get(tl));
				pcppb.addTranslatedContent(ttpb.build());
			}
		}

		return pcppb.build();
	}


	public PrivateChatPostProto createPrivateChatPostProtoFromPrivateChatPostAndProtos(
			PrivateChatPost p, MinimumUserProto mupPoster,
			MinimumUserProto mupRecipient,
			TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil) {
		PrivateChatPostProto.Builder pcppb = PrivateChatPostProto.newBuilder();

		pcppb.setPrivateChatPostUuid(p.getId());
		pcppb.setPoster(mupPoster);
		pcppb.setRecipient(mupRecipient);
		pcppb.setTimeOfPost(p.getTimeOfPost().getTime());
		pcppb.setContent(p.getContent());

		if(p.getContentLanguage() != null) {
			pcppb.setOriginalContentLanguage(TranslateLanguages.valueOf(p.getContentLanguage()));
		}
		else {
			List<TranslationSettingsForUser> tsfu = translationSettingsForUserRetrieveUtil.
					getUserTranslationSettingsForUserGlobal(p.getPosterId());
			if(tsfu == null || tsfu.isEmpty()) {
				pcppb.setOriginalContentLanguage(TranslateLanguages.valueOf(ControllerConstants.TRANSLATION_SETTINGS__DEFAULT_LANGUAGE));
			}
			else pcppb.setOriginalContentLanguage(TranslateLanguages.valueOf(tsfu.get(0).getLanguage()));
		}

		List<String> chatIds = new ArrayList<String>();
		chatIds.add(p.getId());
		Map<String, List<ChatTranslations>> chatTranslationMap = chatTranslationsRetrieveUtils.
				getChatTranslationsForSpecificChatIds(chatIds);

		TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();

		for(String chatId : chatTranslationMap.keySet()) {
			List<ChatTranslations> list = chatTranslationMap.get(chatId);
			for(ChatTranslations ct : list) {
				ttpb.setLanguage(ct.getTranslateLanguage());
				ttpb.setText(ct.getText());
				pcppb.addTranslatedContent(ttpb.build());
			}
		}
		return pcppb.build();
	}

	public List<PrivateChatPostProto> createPrivateChatPostProtoFromPrivateChatPostsAndProtos(
			List<PrivateChatPost> pList,
			Map<Integer, MinimumUserProto> idsToMups,
			TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil) {
		List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();

		for (PrivateChatPost pcp : pList) {
			MinimumUserProto mupPoster = idsToMups.get(pcp
					.getPosterId());
			MinimumUserProto mupRecipient = idsToMups.get(pcp
					.getRecipientId());

			PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
					pcp, mupPoster, mupRecipient, translationSettingsForUserRetrieveUtil);

			pcppList.add(pcpp);
		}

		return pcppList;
	}

	//createMinimumProtoFromUser calls ClanRetrieveUtils, so a db read, if user has a clan
	//to prevent this, all clans that will be used should be passed in, hence clanIdsToClans,
	//clanIdsToUserIdSet. Not all users will have a clan, hence clanlessUserIds
	//privateChatPostIds is used by StartupController to pick out a subset of
	//postIdsToPrivateChatPosts; does not need to be set.
	public List<PrivateChatPostProto> createPrivateChatPostProtoList(
			Map<String, Clan> clanIdsToClans,
			Map<String, Set<String>> clanIdsToUserIdSet,
			Map<String, User> userIdsToUsers, List<String> clanlessUserIds,
			List<String> privateChatPostIds,
			Map<String, PrivateChatPost> postIdsToPrivateChatPosts,
			TranslationSettingsForUserRetrieveUtil translationSettingsForUserRetrieveUtil) {

		List<PrivateChatPostProto> pcppList = new ArrayList<PrivateChatPostProto>();
		Map<String, MinimumUserProto> userIdToMinimumUserProto = new HashMap<String, MinimumUserProto>();
		//construct the minimum user protos for the users that have clans
		//and the clanless users
		createMinimumUserProtosFromClannedAndClanlessUsers(clanIdsToClans,
				clanIdsToUserIdSet, clanlessUserIds, userIdsToUsers,
				userIdToMinimumUserProto);

		//now actually construct the PrivateChatPostProtos
		if (null != privateChatPostIds && !privateChatPostIds.isEmpty()) {
			//only pick out a subset of postIdsToPrivateChatPosts
			for (String postId : privateChatPostIds) {
				PrivateChatPost pcp = postIdsToPrivateChatPosts.get(postId);
				String posterId = pcp.getPosterId();
				String recipientId = pcp.getRecipientId();

				MinimumUserProto mupPoster = userIdToMinimumUserProto
						.get(posterId);
				MinimumUserProto mupRecipient = userIdToMinimumUserProto
						.get(recipientId);

				PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
						pcp, mupPoster, mupRecipient, translationSettingsForUserRetrieveUtil);
				pcppList.add(pcpp);
			}
		} else {
			for (PrivateChatPost pcp : postIdsToPrivateChatPosts.values()) {
				String posterId = pcp.getPosterId();
				String recipientId = pcp.getRecipientId();
				MinimumUserProto mupPoster = userIdToMinimumUserProto
						.get(posterId);
				MinimumUserProto mupRecipient = userIdToMinimumUserProto
						.get(recipientId);

				PrivateChatPostProto pcpp = createPrivateChatPostProtoFromPrivateChatPostAndProtos(
						pcp, mupPoster, mupRecipient, translationSettingsForUserRetrieveUtil);
				pcppList.add(pcpp);
			}
		}

		return pcppList;
	}

	public GroupChatMessageProto createGroupChatMessageProtoFromClanChatPost(
			ClanChatPost p, User user, Clan clan) {
		GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto
				.newBuilder();
		gcmpb.setSender(createMinimumUserProtoFromUserAndClan(user, clan));
		gcmpb.setTimeOfChat(p.getTimeOfPost().getTime());

		//		boolean turnOffTranslation = ServerToggleRetrieveUtils.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__TURN_OFF_TRANSLATIONS);


		gcmpb.setContent(p.getContent());

		//		if(!turnOffTranslation) {
		//			Map<TranslateLanguages, String> translatedMap = MiscMethods.translate(null, p.getContent());
		//			for(TranslateLanguages tl : translatedMap.keySet()) {
		//				TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();
		//				ttpb.setLanguage(tl);
		//				ttpb.setText(translatedMap.get(tl));
		//				gcmpb.addTranslatedContent(ttpb.build());
		//			}
		//		}

		//		gcmpb.setContent(p.getContent());
		return gcmpb.build();
	}

	public GroupChatMessageProto createGroupChatMessageProto(long time,
			MinimumUserProto user, String content, boolean isAdmin,
			String chatId, TranslateLanguages contentLanguage, TranslateLanguages translatedLanguage,
			String translatedContent) {
		GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto
				.newBuilder();

		gcmpb.setSender(user);
		gcmpb.setTimeOfChat(time);
		gcmpb.setContent(content);

		if(contentLanguage != null) {
			gcmpb.setContentLanguage(contentLanguage);
		}

		TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();

		if(translatedLanguage != null && translatedContent != null) {
			ttpb.setLanguage(translatedLanguage);
			ttpb.setText(translatedContent);
		}
		else {
			ttpb.setLanguage(contentLanguage);
			ttpb.setText(content);
		}

		gcmpb.addTranslatedContent(ttpb.build());


		if (chatId != null) {
			gcmpb.setChatUuid(chatId);
		}

		return gcmpb.build();
	}

	public GroupChatMessageProto createGroupChatMessageProto(long time,
			MinimumUserProto user, String content, boolean isAdmin,
			String chatId, Map<TranslateLanguages, String> translatedMap,
			TranslateLanguages contentLanguage, TranslationUtils translationUtils) {

		GroupChatMessageProto.Builder gcmpb = GroupChatMessageProto
				.newBuilder();

		gcmpb.setSender(user);
		gcmpb.setTimeOfChat(time);
		gcmpb.setContent(content);

		if(contentLanguage != null) {
			gcmpb.setContentLanguage(contentLanguage);
		}

		boolean turnOffTranslation = serverToggleRetrieveUtils.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__TURN_OFF_TRANSLATIONS);

		if(!turnOffTranslation) {
			if(translatedMap == null) {
				translatedMap = translationUtils.translate(null, null, content, serverToggleRetrieveUtils);
			}
			for(TranslateLanguages tl : translatedMap.keySet()) {
				TranslatedTextProto.Builder ttpb = TranslatedTextProto.newBuilder();
				ttpb.setLanguage(tl);
				ttpb.setText(translatedMap.get(tl));
				gcmpb.addTranslatedContent(ttpb.build());
			}
		}

		if (chatId != null) {
			gcmpb.setChatUuid(chatId);
		}

		return gcmpb.build();
	}

	public DefaultLanguagesProto createDefaultLanguagesProto(List<TranslationSettingsForUser> tsfuList) {
		DefaultLanguagesProto.Builder dlpb = DefaultLanguagesProto.newBuilder();
		List<PrivateChatDefaultLanguageProto> pcdlpList = new ArrayList<PrivateChatDefaultLanguageProto>();

		for(TranslationSettingsForUser tsfu : tsfuList) {
			if(tsfu.getChatType().equalsIgnoreCase(ChatScope.PRIVATE.toString())) {
				PrivateChatDefaultLanguageProto.Builder pcdlpb = PrivateChatDefaultLanguageProto.newBuilder();
				pcdlpb.setDefaultLanguage(TranslateLanguages.valueOf(tsfu.getLanguage()));
				pcdlpb.setRecipientUserId(tsfu.getReceiverUserId());
				pcdlpb.setSenderUserId(tsfu.getSenderUserId());
				pcdlpb.setTranslateOn(tsfu.isTranslationsOn());

				PrivateChatDefaultLanguageProto lala = pcdlpb.build();
				pcdlpList.add(lala);
			}
			else if(tsfu.getChatType().equalsIgnoreCase(ChatScope.GLOBAL.toString())) {
				dlpb.setGlobalDefaultLanguage(TranslateLanguages.valueOf(tsfu.getLanguage()));
				dlpb.setGlobalTranslateOn(tsfu.isTranslationsOn());
				log.info("global translateon: " + tsfu.isTranslationsOn());
			}
		}

		dlpb.addAllPrivateDefaultLanguage(pcdlpList);
		return dlpb.build();

	}


	/** City.proto *****************************************************/
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

	/** Clan.proto *****************************************************/
	public FullClanProto createFullClanProtoFromClan(Clan c) {
		//    MinimumUserProto mup = createMinimumUserProtoFromUser(RetrieveUtils.userRetrieveUtils().getUserById(c.getOwnerId()));
		FullClanProto.Builder fcpb = FullClanProto.newBuilder();
		fcpb.setClanUuid(c.getId());
		fcpb.setName(c.getName());
		fcpb.setCreateTime(c.getCreateTime().getTime());
		fcpb.setDescription(c.getDescription());
		fcpb.setTag(c.getTag());
		fcpb.setRequestToJoinRequired(c.isRequestToJoinRequired());
		fcpb.setClanIconId(c.getClanIconId());

		return fcpb.build();
	}

	public FullUserClanProto createFullUserClanProtoFromUserClan(
			UserClan uc) {
		FullUserClanProto.Builder fucpb = FullUserClanProto.newBuilder();
		fucpb.setClanUuid(uc.getClanId());
		fucpb.setUserUuid(uc.getUserId());
		String userClanStatus = uc.getStatus();

		try {
			UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
			fucpb.setStatus(ucs);
		} catch (Exception e) {
			log.error(String.format("incorrect user clan status. userClan=%s",
					uc), e);
		}

		Date aTime = uc.getRequestTime();
		if (null != aTime) {
			fucpb.setRequestTime(aTime.getTime());
		}

		return fucpb.build();
	}

	public FullClanProtoWithClanSize createFullClanProtoWithClanSize(
			Clan c, int size) {
		FullClanProto clan = createFullClanProtoFromClan(c);

		return FullClanProtoWithClanSize.newBuilder().setClan(clan)
				.setClanSize(size).build();
	}

	public MinimumUserProtoForClans createMinimumUserProtoForClans(
			User u, Clan clan, String userClanStatus,
			float clanRaidContribution, int battlesWon, UserClanHelpCount uchc) {
		MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(u,
				clan);

		MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans
				.newBuilder();
		mupfcb.setSender(mup);

		try {
			UserClanStatus ucs = UserClanStatus.valueOf(userClanStatus);
			mupfcb.setClanStatus(ucs);
		} catch (Exception e) {
			log.error(String.format("incorrect userClanStatus: %s, user=%s",
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

	public MinimumUserProtoForClans createMinimumUserProtoForClans(
			User u, Clan clan, UserClanStatus userClanStatus,
			float clanRaidContribution, int battlesWon, UserClanHelpCount uchc) {
		MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(u,
				clan);

		MinimumUserProtoForClans.Builder mupfcb = MinimumUserProtoForClans
				.newBuilder();
		mupfcb.setSender(mup);

		mupfcb.setClanStatus(userClanStatus);
		mupfcb.setRaidContribution(clanRaidContribution);
		if(battlesWon != -1) {
			mupfcb.setBattlesWon(battlesWon);
		}
		if (null != uchc) {
			mupfcb.setNumClanHelpsSolicited(uchc.getNumSolicited());
			mupfcb.setNumClanHelpsGiven(uchc.getNumGiven());
		}

		MinimumUserProtoForClans mupfc = mupfcb.build();
		return mupfc;
	}

	public ClanRaidProto createClanRaidProto(ClanRaid clanRaid) {
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
		Map<Integer, ClanRaidStage> stages = clanRaidStageRetrieveUtils
				.getClanRaidStagesForClanRaidId(clanRaidId);
		for (ClanRaidStage crs : stages.values()) {
			ClanRaidStageProto crsp = createClanRaidStageProto(crs);
			crpb.addRaidStages(crsp);
		}

		return crpb.build();
	}

	public ClanRaidStageProto createClanRaidStageProto(ClanRaidStage crs) {
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
		Map<Integer, ClanRaidStageMonster> monsterNumToCrsm = clanRaidStageMonsterRetrieveUtils
				.getMonsterNumsToMonstersForStageId(clanRaidStageId);

		if (!monsterNumToCrsm.isEmpty()) {

			List<Integer> monsterNumsAsc = new ArrayList<Integer>(
					monsterNumToCrsm.keySet());
			Collections.sort(monsterNumsAsc);

			for (Integer monsterNum : monsterNumsAsc) {
				ClanRaidStageMonster crsm = monsterNumToCrsm.get(monsterNum);

				ClanRaidStageMonsterProto crsmp = createClanRaidStageMonsterProto(crsm);
				crspb.addMonsters(crsmp);
			}
		}

		//create the reward protos
		Map<Integer, ClanRaidStageReward> possibleRewards = clanRaidStageRewardRetrieveUtils
				.getClanRaidStageRewardsForClanRaidStageId(clanRaidStageId);
		for (ClanRaidStageReward crsr : possibleRewards.values()) {
			ClanRaidStageRewardProto crsrp = createClanRaidStageRewardProto(crsr);
			crspb.addPossibleRewards(crsrp);
		}

		return crspb.build();
	}

	public ClanRaidStageMonsterProto createClanRaidStageMonsterProto(
			ClanRaidStageMonster crsm) {
		ClanRaidStageMonsterProto.Builder crsmpb = ClanRaidStageMonsterProto
				.newBuilder();
		crsmpb.setCrsmId(crsm.getId());
		crsmpb.setMonsterId(crsm.getMonsterId());
		crsmpb.setMonsterHp(crsm.getMonsterHp());
		crsmpb.setMinDmg(crsm.getMinDmg());
		crsmpb.setMaxDmg(crsm.getMaxDmg());

		return crsmpb.build();
	}

	public ClanRaidStageRewardProto createClanRaidStageRewardProto(
			ClanRaidStageReward crsr) {
		ClanRaidStageRewardProto.Builder crsrpb = ClanRaidStageRewardProto
				.newBuilder();
		crsrpb.setCrsrId(crsr.getId());
		crsrpb.setMinOilReward(crsr.getMinOilReward());
		crsrpb.setMaxOilReward(crsr.getMaxOilReward());
		crsrpb.setMinCashReward(crsr.getMinCashReward());
		crsrpb.setMaxCashReward(crsr.getMaxCashReward());
		crsrpb.setMonsterId(crsr.getMonsterId());

		return crsrpb.build();
	}

	public PersistentClanEventProto createPersistentClanEventProto(
			ClanEventPersistent cep) {
		PersistentClanEventProto.Builder pcepb = PersistentClanEventProto
				.newBuilder();
		pcepb.setClanEventId(cep.getId());

		String dayOfWeekStr = cep.getDayOfWeek();
		try {
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
			pcepb.setDayOfWeek(dayOfWeek);
		} catch (Exception e) {
			log.error(String.format("incorrect DayOfWeek: %s, clanEvent=%s",
					dayOfWeekStr, cep), e);
		}

		pcepb.setStartHour(cep.getStartHour());
		pcepb.setEventDurationMinutes(cep.getEventDurationMinutes());
		pcepb.setClanRaidId(cep.getClanRaidId());

		return pcepb.build();
	}

	public PersistentClanEventClanInfoProto createPersistentClanEventClanInfoProto(
			ClanEventPersistentForClan cepfc) {
		PersistentClanEventClanInfoProto.Builder pcecipb = PersistentClanEventClanInfoProto
				.newBuilder();
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

	public PersistentClanEventUserInfoProto createPersistentClanEventUserInfoProto(
			ClanEventPersistentForUser cepfu,
			Map<String, MonsterForUser> idsToUserMonsters,
			List<FullUserMonsterProto> fumpList) {
		PersistentClanEventUserInfoProto.Builder pceuipb = PersistentClanEventUserInfoProto
				.newBuilder();
		String userId = cepfu.getUserId();
		pceuipb.setUserUuid(userId);
		pceuipb.setClanUuid(cepfu.getClanId());

		pceuipb.setCrId(cepfu.getCrId());
		pceuipb.setCrDmgDone(cepfu.getCrDmgDone());

		//  	pceuipb.setCrsId(cepfu.getCrsId());
		pceuipb.setCrsDmgDone(cepfu.getCrsDmgDone());

		//  	pceuipb.setCrsmId(cepfu.getCrsmId());
		pceuipb.setCrsmDmgDone(cepfu.getCrsmDmgDone());

		UserCurrentMonsterTeamProto.Builder ucmtpb = UserCurrentMonsterTeamProto
				.newBuilder();
		ucmtpb.setUserUuid(userId);

		if (null == fumpList || fumpList.isEmpty()) {
			List<String> userMonsterIds = cepfu.getUserMonsterIds();

			for (String userMonsterId : userMonsterIds) {

				if (!idsToUserMonsters.containsKey(userMonsterId)) {
					//user no longer has this monster, probably sold
					//create fake user monster proto
					FullUserMonsterProto.Builder fumpb = FullUserMonsterProto
							.newBuilder();
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

	public PersistentClanEventUserRewardProto createPersistentClanEventUserRewardProto(
			ClanEventPersistentUserReward reward) {
		PersistentClanEventUserRewardProto.Builder pceurpb = PersistentClanEventUserRewardProto
				.newBuilder();

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
			} catch (Exception e) {
				log.info(
						String.format(
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

	public PersistentClanEventRaidStageHistoryProto createPersistentClanEventRaidStageHistoryProto(
			CepfuRaidStageHistory cepfursh,
			Collection<ClanEventPersistentUserReward> rewards) {
		PersistentClanEventRaidStageHistoryProto.Builder pcershpb = PersistentClanEventRaidStageHistoryProto
				.newBuilder();

		if (null != rewards && !rewards.isEmpty()) {
			for (ClanEventPersistentUserReward reward : rewards) {
				PersistentClanEventUserRewardProto rewardPRoto = createPersistentClanEventUserRewardProto(reward);
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

	public PersistentClanEventRaidHistoryProto createPersistentClanEventRaidHistoryProto(
			CepfuRaidHistory cepfurh) {
		PersistentClanEventRaidHistoryProto.Builder pcerhpb = PersistentClanEventRaidHistoryProto
				.newBuilder();
		pcerhpb.setUserUuid(cepfurh.getUserId());
		pcerhpb.setCrDmg(cepfurh.getCrDmgDone());
		pcerhpb.setClanCrDmg(cepfurh.getClanCrDmg());

		return pcerhpb.build();
	}

	public ClanIconProto createClanIconProtoFromClanIcon(ClanIcon ci) {
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

	public ClanHelpProto createClanHelpProtoFromClanHelp(ClanHelp ch,
			User u, Clan c, MinimumUserProto mup) {
		ClanHelpProto.Builder chpb = ClanHelpProto.newBuilder();
		chpb.setClanHelpUuid(ch.getId());
		chpb.setClanUuid(ch.getClanId());

		if (null == mup) {
			mup = createMinimumUserProtoFromUserAndClan(u, c);
		}
		chpb.setMup(mup);
		chpb.setUserDataUuid(ch.getUserDataId());

		String helpType = ch.getHelpType();

		if (null != helpType) {
			try {
				GameActionType cht = GameActionType.valueOf(helpType);
				chpb.setHelpType(cht);

			} catch (Exception e) {
				log.info(String.format("incorrect GameActionType. ClanHelp=%s",
						ch));
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

	public ClanInviteProto createClanInviteProto(ClanInvite invite) {
		ClanInviteProto.Builder cipb = ClanInviteProto.newBuilder();
		cipb.setInviteUuid(invite.getId());
		cipb.setUserUuid(invite.getUserId());
		cipb.setInviterUuid(invite.getInviterId());
		cipb.setClanUuid(invite.getClanId());
		cipb.setTimeOfInvite(invite.getTimeOfInvite().getTime());

		return cipb.build();
	}

	public ClanMemberTeamDonationProto createClanMemberTeamDonationProto(
			ClanMemberTeamDonation cmtd, MonsterSnapshotForUser msfu,
			MinimumUserProto solicitor, MinimumUserProto donatorProto) {
		ClanMemberTeamDonationProto.Builder cmtdpb = ClanMemberTeamDonationProto
				.newBuilder();

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

	/** InAppPurchase.proto ********************************************/
	public GoldSaleProto createGoldSaleProtoFromGoldSale(GoldSale sale) {
		GoldSaleProto.Builder b = GoldSaleProto.newBuilder()
				.setSaleId(sale.getId())
				.setStartDate(sale.getStartDate().getTime())
				.setEndDate(sale.getEndDate().getTime());

		if (sale.getPackage1SaleIdentifier() != null)
			b.setPackage1SaleIdentifier(sale.getPackage1SaleIdentifier());
		if (sale.getPackage2SaleIdentifier() != null)
			b.setPackage2SaleIdentifier(sale.getPackage2SaleIdentifier());
		if (sale.getPackage3SaleIdentifier() != null)
			b.setPackage3SaleIdentifier(sale.getPackage3SaleIdentifier());
		if (sale.getPackage4SaleIdentifier() != null)
			b.setPackage4SaleIdentifier(sale.getPackage4SaleIdentifier());
		if (sale.getPackage5SaleIdentifier() != null)
			b.setPackage5SaleIdentifier(sale.getPackage5SaleIdentifier());
		if (sale.getPackageS1SaleIdentifier() != null)
			b.setPackageS1SaleIdentifier(sale.getPackageS1SaleIdentifier());
		if (sale.getPackageS2SaleIdentifier() != null)
			b.setPackageS2SaleIdentifier(sale.getPackageS2SaleIdentifier());
		if (sale.getPackageS3SaleIdentifier() != null)
			b.setPackageS3SaleIdentifier(sale.getPackageS3SaleIdentifier());
		if (sale.getPackageS4SaleIdentifier() != null)
			b.setPackageS4SaleIdentifier(sale.getPackageS4SaleIdentifier());
		if (sale.getPackageS5SaleIdentifier() != null)
			b.setPackageS5SaleIdentifier(sale.getPackageS5SaleIdentifier());
		b.setGoldShoppeImageName(sale.getGoldShoppeImageName())
		.setGoldBarImageName(sale.getGoldBarImageName());
		b.setIsBeginnerSale(sale.isBeginnerSale());

		return b.build();
	}

	/** Item.proto ***************************************************/
	public ItemProto createItemProtoFromItem(Item item) {
		ItemProto.Builder ipb = ItemProto.newBuilder();

		ipb.setItemId(item.getId());

		String str = item.getName();
		if (null != str) {
			ipb.setName(str);
		}

		str = item.getShortName();
		if (null != str) {
			ipb.setShortName(str);
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
						"can't create enum type. itemType=%s. item=%s", str,
						item), e);
			}
		}

		ipb.setStaticDataId(item.getStaticDataId());
		ipb.setAmount(item.getAmount());
		ipb.setSecretGiftChance(item.getSecretGiftChance());
		ipb.setAlwaysDisplayToUser(item.isAlwaysDisplayToUser());

		str = item.getGameActionType();
		if(null != str) {
			try {
				GameActionType gat = GameActionType.valueOf(str);
				ipb.setGameActionType(gat);
			} catch (Exception e) {
				log.error(String.format(
						"can't create enum type. gameType=%s. item=%s", str,
						item), e);
			}
		}

		str = item.getQuality();
		if (null != str) {
			try {
				Quality iq = Quality.valueOf(str);
				ipb.setQuality(iq);
			} catch (Exception e) {
				log.error(String.format("invalid item quality. item=%s",
						item), e);
			}
		}

		return ipb.build();
	}

	public List<UserItemProto> createUserItemProtosFromUserItems(
			List<ItemForUser> ifuCollection) {

		List<UserItemProto> userItems = new ArrayList<UserItemProto>();

		for (ItemForUser ifu : ifuCollection) {
			userItems.add(createUserItemProtoFromUserItem(ifu));
		}
		return userItems;
	}

	public UserItemProto createUserItemProtoFromUserItem(ItemForUser ifu) {
		UserItemProto.Builder uipb = UserItemProto.newBuilder();

		uipb.setItemId(ifu.getItemId());
		uipb.setUserUuid(ifu.getUserId());
		uipb.setQuantity(ifu.getQuantity());

		return uipb.build();
	}

	public UserItemProto createUserItemProto(String userId, int itemId,
			int quantity) {
		UserItemProto.Builder uipb = UserItemProto.newBuilder();

		uipb.setItemId(itemId);
		uipb.setUserUuid(userId);
		uipb.setQuantity(quantity);

		return uipb.build();
	}

	public List<UserItemUsageProto> createUserItemUsageProto(
			List<ItemForUserUsage> ifuuList) {
		List<UserItemUsageProto> protos = new ArrayList<UserItemUsageProto>();

		for (ItemForUserUsage ifuu : ifuuList) {
			UserItemUsageProto uiup = createUserItemUsageProto(ifuu);
			protos.add(uiup);
		}

		return protos;
	}

	public UserItemUsageProto createUserItemUsageProto(
			ItemForUserUsage ifuu) {
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
				log.error(
						String.format(
								"can't create enum type. actionType=%s. itemForUserUsage=%s",
								str, ifuu), e);
			}
		}

		return uiupb.build();
	}

	public Collection<UserItemSecretGiftProto> createUserItemSecretGiftProto(
			Collection<ItemSecretGiftForUser> secretGifts) {
		Collection<UserItemSecretGiftProto> gifs = new ArrayList<UserItemSecretGiftProto>();
		if (null == secretGifts || secretGifts.isEmpty()) {
			return gifs;
		}

		for (ItemSecretGiftForUser isgfu : secretGifts) {
			gifs.add(createUserItemSecretGiftProto(isgfu));
		}
		return gifs;
	}

	public UserItemSecretGiftProto createUserItemSecretGiftProto(
			ItemSecretGiftForUser secretGift) {
		UserItemSecretGiftProto.Builder uisgpb = UserItemSecretGiftProto
				.newBuilder();
		uisgpb.setUisgUuid(secretGift.getId());
		uisgpb.setUserUuid(secretGift.getUserId());
		uisgpb.setSecsTillCollection(secretGift.getSecsTillCollection());
		uisgpb.setItemId(secretGift.getItemId());

		Date createTime = secretGift.getCreateTime();
		uisgpb.setCreateTime(createTime.getTime());

		return uisgpb.build();
	}

	public List<ItemGemPriceProto> createItemGemPriceProto(
			Map<Integer, MiniJobRefreshItem> idToMjriMap)
	{
		List<ItemGemPriceProto> igppList = new ArrayList<ItemGemPriceProto>();

		for (Integer id : idToMjriMap.keySet())
		{
			MiniJobRefreshItem mjri = idToMjriMap.get(id);
			ItemGemPriceProto igpp = createItemGemPriceProto(mjri);
			igppList.add(igpp);
		}

		return igppList;
	}

	public ItemGemPriceProto createItemGemPriceProto(MiniJobRefreshItem mjri)
	{
		ItemGemPriceProto.Builder igppb = ItemGemPriceProto.newBuilder();
		igppb.setGemPrice(mjri.getGemPrice());
		igppb.setItemId(mjri.getItemId());
		igppb.setStructId(mjri.getStructId());

		return igppb.build();
	}

	/** MiniEvent.proto ********************************************/
	public UserMiniEventProto createUserMiniEventProto(MiniEventForUser mefu,
			MiniEventConfigPojo me, MiniEventTimetableConfigPojo metc,
			Collection<MiniEventGoalForUser> megfus,
			MiniEventForPlayerLvl mefpl, Collection<MiniEventTierReward> rewards,
			Collection<MiniEventGoal> goals,
			Collection<MiniEventLeaderboardReward> leaderboardRewards,
			RewardRetrieveUtils rewardRetrieveUtil)
	{
		UserMiniEventProto.Builder umepb = createUserMiniEventProto(mefu);

		MiniEventProto mep = createMiniEventProto(me, metc, mefpl, rewards, goals,
				leaderboardRewards, rewardRetrieveUtil);
		umepb.setMiniEvent(mep);

		if (null != megfus && !megfus.isEmpty())
		{
			Collection<UserMiniEventGoalProto> umegps =
					createUserMiniEventGoalProto(megfus);
			umepb.addAllGoals(umegps);
		}


		return umepb.build();
	}

	public UserMiniEventProto.Builder createUserMiniEventProto(
			MiniEventForUser mefu)
	{
		UserMiniEventProto.Builder umepb = UserMiniEventProto.newBuilder();
		umepb.setMiniEventId(mefu.getMiniEventId());
		umepb.setUserUuid(mefu.getUserId());
		umepb.setUserLvl(mefu.getUserLvl());
		umepb.setTierOneRedeemed(mefu.isTierOneRedeemed());
		umepb.setTierTwoRedeemed(mefu.isTierTwoRedeemed());
		umepb.setTierThreeRedeemed(mefu.isTierThreeRedeemed());
		return umepb;
	}

	public MiniEventProto createMiniEventProto(MiniEventConfigPojo me,
			MiniEventTimetableConfigPojo metc,
			MiniEventForPlayerLvl mefpl, Collection<MiniEventTierReward> rewards,
			Collection<MiniEventGoal> goals,
			Collection<MiniEventLeaderboardReward> leaderboardRewards,
			RewardRetrieveUtils rewardRetrieveUtil)
	{
		MiniEventProto.Builder mepb = MiniEventProto.newBuilder();
		mepb.setMiniEventId(me.getId());

		//Date d = me.getStartTime();
		Date d = metc.getStartTime();
		if (null != d) {
			mepb.setMiniEventStartTime(d.getTime());
		}

		//d = me.getEndTime();
		d = metc.getEndTime();
		if (null != d) {
			mepb.setMiniEventEndTime(d.getTime());
		}

		MiniEventForPlayerLevelProto mefplp =
				createMiniEventForPlayerLevelProto(mefpl, rewards, rewardRetrieveUtil);
		mepb.setLvlEntered(mefplp);

		Collection<MiniEventGoalProto> goalProtos = createMiniEventGoalProto(goals);
		mepb.addAllGoals(goalProtos);

		Collection<MiniEventLeaderboardRewardProto> leaderboardRewardProtos =
				createMiniEventLeaderboardRewardProto(leaderboardRewards, rewardRetrieveUtil);
		mepb.addAllLeaderboardRewards(leaderboardRewardProtos);

		String str = me.getName();
		if (null != str) {
			mepb.setName(str);
		}

		str = me.getDescription();
		if (null != str) {
			mepb.setDesc(str);
		}

		str = me.getImg();
		if (null != str) {
			mepb.setImg(str);
		}

		str = me.getIcon();
		if (null != str) {
			mepb.setIcon(str);
		}

		return mepb.build();
	}

	public MiniEventForPlayerLevelProto createMiniEventForPlayerLevelProto(
			MiniEventForPlayerLvl mefpl, Collection<MiniEventTierReward> rewards,
			RewardRetrieveUtils rewardRetrieveUtil)
	{
		MiniEventForPlayerLevelProto.Builder mefplpb =
				createMiniEventForPlayerLevelProto(mefpl);

		if (null != rewards && !rewards.isEmpty()) {
			Collection<MiniEventTierRewardProto> rewardProtos =
					createMiniEventTierRewardProto(rewards, rewardRetrieveUtil);
			mefplpb.addAllRewards(rewardProtos);
		}


		return mefplpb.build();
	}

	private MiniEventForPlayerLevelProto.Builder createMiniEventForPlayerLevelProto(
			MiniEventForPlayerLvl mefpl) {
		MiniEventForPlayerLevelProto.Builder mefplpb =
				MiniEventForPlayerLevelProto.newBuilder();
		mefplpb.setMefplId(mefpl.getId());
		mefplpb.setMiniEventId(mefpl.getMiniEventId());
		mefplpb.setPlayerLvlMin(mefpl.getPlayerLvlMin());
		mefplpb.setPlayerLvlMax(mefpl.getPlayerLvlMax());
		mefplpb.setTierOneMinPts(mefpl.getTierOneMinPts());
		mefplpb.setTierTwoMinPts(mefpl.getTierTwoMinPts());
		mefplpb.setTierThreeMinPts(mefpl.getTierThreeMinPts());
		return mefplpb;
	}

	public Collection<MiniEventTierRewardProto> createMiniEventTierRewardProto(
			Collection<MiniEventTierReward> metrs, RewardRetrieveUtils rewardRetrieveUtil)
	{
		Collection<MiniEventTierRewardProto> rewardProtos =
				new ArrayList<MiniEventTierRewardProto>();

		for (MiniEventTierReward metr : metrs) {
			MiniEventTierRewardProto metrp = createMiniEventTierRewardProto(
					metr, rewardRetrieveUtil);
			rewardProtos.add(metrp);
		}

		return rewardProtos;
	}

	private MiniEventTierRewardProto createMiniEventTierRewardProto(
			MiniEventTierReward metr, RewardRetrieveUtils rewardRetrieveUtil)
	{
		MiniEventTierRewardProto.Builder metrpb =
				MiniEventTierRewardProto.newBuilder();

		metrpb.setMetrId(metr.getId());
		metrpb.setMefplId(metr.getMiniEventForPlayerLvlId());
		Reward r = rewardRetrieveUtil.getRewardById(metr.getRewardId());
		metrpb.setRewardProto(createRewardProto(r));
		metrpb.setTierLvl(metr.getRewardTier());

		return metrpb.build();
	}

	private Collection<MiniEventGoalProto> createMiniEventGoalProto(
			Collection<MiniEventGoal> goals)
	{
		Collection<MiniEventGoalProto> goalProtos = new ArrayList<MiniEventGoalProto>();
		for (MiniEventGoal meg : goals)
		{
			MiniEventGoalProto megp = createMiniEventGoalProto(meg);
			goalProtos.add(megp);

		}

		return goalProtos;
	}

	private MiniEventGoalProto createMiniEventGoalProto(MiniEventGoal meg) {
		MiniEventGoalProto.Builder megpb = MiniEventGoalProto.newBuilder();
		megpb.setMiniEventGoalId(meg.getId());
		megpb.setMiniEventId(meg.getMiniEventId());

		String str = meg.getType();
		if (null != str) {
			try {
				MiniEventGoalType q = MiniEventGoalType.valueOf(str);
				megpb.setGoalType(q);
			} catch (Exception e) {
				log.error(
						String.format("invalid MiniEventGoalType. MiniEventGoal=%s",
								meg),
				e);
			}
		}
		megpb.setGoalAmt(meg.getAmt());

		str = meg.getDesc();
		if (null != str) {
			megpb.setGoalDesc(str);
		}

		str = meg.getActionDescription();
		if(null != str) {
			megpb.setActionDescription(str);
		}

		megpb.setPointsGained(meg.getPtsReward());
		return megpb.build();
	}

	private Collection<MiniEventLeaderboardRewardProto> createMiniEventLeaderboardRewardProto(
			Collection<MiniEventLeaderboardReward> rewards,
			RewardRetrieveUtils rewardRetrieveUtil)
	{
		Collection<MiniEventLeaderboardRewardProto> rewardProtos =
				new ArrayList<MiniEventLeaderboardRewardProto>();

		for (MiniEventLeaderboardReward melr : rewards) {
			MiniEventLeaderboardRewardProto melrp =
					createMiniEventLeaderboardRewardProto(
							melr, rewardRetrieveUtil);

			rewardProtos.add(melrp);
		}

		return rewardProtos;
	}

	private MiniEventLeaderboardRewardProto createMiniEventLeaderboardRewardProto(
			MiniEventLeaderboardReward melr, RewardRetrieveUtils rewardRetrieveUtil)
	{
		MiniEventLeaderboardRewardProto.Builder melrpb =
				MiniEventLeaderboardRewardProto.newBuilder();
		melrpb.setMelrId(melr.getId());
		melrpb.setMiniEventId(melr.getMiniEventId());
		Reward r = rewardRetrieveUtil.getRewardById(melr.getRewardId());
		melrpb.setRewardProto(createRewardProto(r));
		melrpb.setLeaderboardMinPos(melr.getLeaderboardPos());

		return melrpb.build();
	}

	private Collection<UserMiniEventGoalProto> createUserMiniEventGoalProto(
			Collection<MiniEventGoalForUser> megfus)
	{
		Collection<UserMiniEventGoalProto> goalProtos =
				new ArrayList<UserMiniEventGoalProto>();
		if (null == megfus || megfus.isEmpty())
		{
			return goalProtos;
		}

		for (MiniEventGoalForUser megfu : megfus) {
			UserMiniEventGoalProto umegp = createUserMiniEventGoalProto(megfu);
			goalProtos.add(umegp);
		}

		return goalProtos;
	}

	private UserMiniEventGoalProto createUserMiniEventGoalProto(
			MiniEventGoalForUser megfu)
	{
		UserMiniEventGoalProto.Builder umegpb = UserMiniEventGoalProto.newBuilder();
		umegpb.setUserUuid(megfu.getUserId());
		umegpb.setMiniEventGoalId(megfu.getMiniEventGoalId());
		umegpb.setProgress(megfu.getProgress());

		return umegpb.build();
	}

	/** MiniJobConfig.proto ********************************************/
	public MiniJobProto createMiniJobProto(MiniJob mj, RewardRetrieveUtils rewardRetrieveUtil) {
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
			} catch (Exception e) {
				log.error(String.format("invalid quality. MiniJob=%s", mj), e);
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

		int rewardId = mj.getRewardIdOne();
		if (rewardId > 0) {
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			RewardProto rp = createRewardProto(r);
			mjpb.setRewardOne(rp);
		}
		rewardId = mj.getRewardIdTwo();
		if (rewardId > 0) {
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			RewardProto rp = createRewardProto(r);
			mjpb.setRewardTwo(rp);
		}
		rewardId = mj.getRewardIdThree();
		if (rewardId > 0) {
			Reward r = rewardRetrieveUtil.getRewardById(rewardId);
			RewardProto rp = createRewardProto(r);
			mjpb.setRewardThree(rp);
		}

		return mjpb.build();
	}

	public UserMiniJobProto createUserMiniJobProto(MiniJobForUser mjfu,
			MiniJob mj, RewardRetrieveUtils rewardRetrieveUtil) {
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

		MiniJobProto mjp = createMiniJobProto(mj, rewardRetrieveUtil);
		umjpb.setMiniJob(mjp);

		return umjpb.build();
	}

	public List<UserMiniJobProto> createUserMiniJobProtos(
			List<MiniJobForUser> mjfuList,
			Map<Integer, MiniJob> miniJobIdToMiniJob,
			RewardRetrieveUtils rewardRetrieveUtil) {
		List<UserMiniJobProto> umjpList = new ArrayList<UserMiniJobProto>();

		for (MiniJobForUser mjfu : mjfuList) {
			int miniJobId = mjfu.getMiniJobId();

			MiniJob mj = null;
			if (null == miniJobIdToMiniJob
					|| !miniJobIdToMiniJob.containsKey(miniJobId)) {
				mj = miniJobRetrieveUtils.getMiniJobForMiniJobId(miniJobId);
			} else {
				mj = miniJobIdToMiniJob.get(miniJobId);
			}

			UserMiniJobProto umjp = createUserMiniJobProto(mjfu, mj, rewardRetrieveUtil);
			umjpList.add(umjp);
		}

		return umjpList;
	}

	/** MonsterStuff.proto ********************************************/
	public MonsterProto createMonsterProto(Monster aMonster,
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
			log.error(String.format("invalid monster quality. monster=%s",
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
		} catch (Exception e) {
			log.error(String.format("invalid monster element. monster=%s",
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

		int evolutionCatalystMonsterId = aMonster
				.getEvolutionCatalystMonsterId();
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
			log.error("invalid animation type for monster. type=" + aStr
					+ "\t monster=" + aMonster, e);
		}

		int verticalPixelOffset = aMonster.getVerticalPixelOffset();
		mpb.setVerticalPixelOffset(verticalPixelOffset);

		String atkSoundFile = aMonster.getAtkSoundFile();
		if (null != atkSoundFile) {
			mpb.setAtkSoundFile(atkSoundFile);
		}

		int atkSoundAnimationFrame = aMonster.getAtkSoundAnimationFrame();
		mpb.setAtkSoundAnimationFrame(atkSoundAnimationFrame);

		int atkAnimationRepeatedFramesStart = aMonster
				.getAtkAnimationRepeatedFramesStart();
		mpb.setAtkAnimationRepeatedFramesStart(atkAnimationRepeatedFramesStart);

		int atkAnimationRepeatedFramesEnd = aMonster
				.getAtkAnimationRepeatedFramesEnd();
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

	public List<MonsterLevelInfoProto> createMonsterLevelInfoFromInfo(
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
			MonsterLevelInfoProto.Builder mlipb = MonsterLevelInfoProto
					.newBuilder();
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
			mlipb.setSecsToEnhancePerFeeder(info.getSecondsToEnhancePerFeeder());
			mlipb.setSecsToEnhancePerFeederExponent(info.getSecondsToEnhancePerFeederExponent());
			mlipb.setStrength(info.getStrength());
			mlipb.setStrengthExponent(info.getStrengthExponent());

			lvlInfoProtos.add(mlipb.build());
		}

		return lvlInfoProtos;
	}

	public FullUserMonsterProto createFullUserMonsterProtoFromUserMonster(
			MonsterForUser mfu) {
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
		Monster monzter = monsterRetrieveUtils.getMonsterForMonsterId(mfu
				.getMonsterId());
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

	public List<FullUserMonsterProto> createFullUserMonsterProtoList(
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

	public MinimumUserMonsterProto createMinimumUserMonsterProto(
			MonsterForUser mfu) {
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto
				.newBuilder();

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
		Monster monzter = monsterRetrieveUtils.getMonsterForMonsterId(mfu
				.getMonsterId());
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (curOffensiveSkillId <= 0 && defaultOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (curDefensiveSkillId <= 0 && defaultDefSkillId > 0) {
			mumpb.setDefensiveSkillId(defaultDefSkillId);
		}

		mumpb.setTeamSlotNum(mfu.getTeamSlotNum());

		return mumpb.build();
	}

	public MinimumUserMonsterProto createMinimumUserMonsterProto(
			MonsterForPvp mfp) {
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto
				.newBuilder();

		//int id = mfp.getId();
		int id = mfp.getMonsterId();
		int lvl = mfp.getMonsterLvl();

		mumpb.setMonsterId(id);
		mumpb.setMonsterLvl(lvl);

		//set userMonster skill (if absent) to monster skill
		Monster monzter = monsterRetrieveUtils.getMonsterForMonsterId(id);
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

	public MinimumUserMonsterProto createMinimumUserMonsterProto(
			MonsterSnapshotForUser msfu) {
		MinimumUserMonsterProto.Builder mumpb = MinimumUserMonsterProto
				.newBuilder();

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
		Monster monzter = monsterRetrieveUtils.getMonsterForMonsterId(msfu
				.getMonsterId());
		int defaultOffensiveSkillId = monzter.getBaseOffensiveSkillId();
		if (curOffensiveSkillId <= 0 && defaultOffensiveSkillId > 0) {
			mumpb.setOffensiveSkillId(defaultOffensiveSkillId);
		}

		int defaultDefSkillId = monzter.getBaseDefensiveSkillId();
		if (curDefensiveSkillId <= 0 && defaultDefSkillId > 0) {
			mumpb.setDefensiveSkillId(defaultDefSkillId);
		}

		mumpb.setTeamSlotNum(msfu.getTeamSlotNum());

		return mumpb.build();
	}

	//	public static Collection<MinimumUserMonsterProto> createMinimumUserMonsterProtoList(
	//			Collection<MonsterForUser> userMonsters) {
	//		List<MinimumUserMonsterProto> returnList = new ArrayList<MinimumUserMonsterProto>();
	//
	//		for (MonsterForUser mfu : userMonsters) {
	//			MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfu);
	//			returnList.add(mump);
	//		}
	//
	//		return returnList;
	//	}

	public List<MinimumUserMonsterProto> createMinimumUserMonsterProtos(
			List<MonsterForPvp> mfpList) {
		List<MinimumUserMonsterProto> mumpList = new ArrayList<MinimumUserMonsterProto>();

		for (MonsterForPvp mfp : mfpList) {
			MinimumUserMonsterProto mump = createMinimumUserMonsterProto(mfp);
			mumpList.add(mump);
		}

		return mumpList;
	}

	public UserMonsterHealingProto createUserMonsterHealingProtoFromObj(
			MonsterHealingForUser mhfu) {
		UserMonsterHealingProto.Builder umhpb = UserMonsterHealingProto
				.newBuilder();
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

	public UserEnhancementProto createUserEnhancementProtoFromObj(
			String userId, UserEnhancementItemProto baseMonster,
			List<UserEnhancementItemProto> feeders) {

		UserEnhancementProto.Builder uepb = UserEnhancementProto.newBuilder();

		uepb.setUserUuid(userId);
		uepb.setBaseMonster(baseMonster);
		uepb.addAllFeeders(feeders);

		return uepb.build();
	}

	public UserEnhancementItemProto createUserEnhancementItemProtoFromObj(
			MonsterEnhancingForUser mefu) {

		UserEnhancementItemProto.Builder ueipb = UserEnhancementItemProto
				.newBuilder();
		ueipb.setUserMonsterUuid(mefu.getMonsterForUserId());

		Date startTime = mefu.getExpectedStartTime();
		if (null != startTime) {
			ueipb.setExpectedStartTimeMillis(startTime.getTime());
		}

		ueipb.setEnhancingCost(mefu.getEnhancingCost());

		return ueipb.build();
	}

	public UserCurrentMonsterTeamProto createUserCurrentMonsterTeamProto(
			String userId, List<MonsterForUser> curTeam) {
		UserCurrentMonsterTeamProto.Builder ucmtpb = UserCurrentMonsterTeamProto
				.newBuilder();
		ucmtpb.setUserUuid(userId);

		List<FullUserMonsterProto> currentTeam = createFullUserMonsterProtoList(curTeam);
		ucmtpb.addAllCurrentTeam(currentTeam);

		return ucmtpb.build();
	}

	public UserMonsterEvolutionProto createUserEvolutionProtoFromEvolution(
			MonsterEvolvingForUser mefu) {
		UserMonsterEvolutionProto.Builder uepb = UserMonsterEvolutionProto
				.newBuilder();

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

	public MonsterBattleDialogueProto createMonsterBattleDialogueProto(
			MonsterBattleDialogue mbd) {
		MonsterBattleDialogueProto.Builder mbdpb = MonsterBattleDialogueProto
				.newBuilder();
		mbdpb.setMonsterId(mbd.getMonsterId());

		String aStr = mbd.getDialogueType();
		try {
			DialogueType type = DialogueType.valueOf(aStr);
			mbdpb.setDialogueType(type);
		} catch (Exception e) {
			log.error(String.format(
					"incorrect DialogueType enum. MonsterBattleDialogue=%s",
					mbd), e);
		}

		mbdpb.setDialogue(mbd.getDialogue());
		mbdpb.setProbabilityUttered(mbd.getProbabilityUttered());

		return mbdpb.build();
	}

	public UserMonsterCurrentHealthProto createUserMonsterCurrentHealthProto(
			MonsterForUser mfu) {
		UserMonsterCurrentHealthProto.Builder umchpb = UserMonsterCurrentHealthProto
				.newBuilder();

		umchpb.setUserMonsterUuid(mfu.getId());
		umchpb.setCurrentHealth(mfu.getCurrentHealth());

		return umchpb.build();
	}

	public  UserMonsterSnapshotProto createUserMonsterSnapshotProto(
			MonsterSnapshotForUser msfu, MinimumUserProto ownerProto) {
		UserMonsterSnapshotProto.Builder usmpb = UserMonsterSnapshotProto
				.newBuilder();
		usmpb.setSnapshotUuid(msfu.getId());
		usmpb.setTimeOfCreation(msfu.getTimeOfEntry().getTime());

		String str = msfu.getType();
		try {
			SnapshotType type = SnapshotType.valueOf(str);
			usmpb.setType(type);
		} catch (Exception e) {
			log.error(String.format(
					"incorrect SnapshotType enum. MonsterSnapshotForUser=%s",
					msfu), e);
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

	/** Prerequisite.proto ****************************************************/
	public PrereqProto createPrerequisiteProto(Prerequisite prereq) {
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
						"incorrect prereq GameType enum. Prerequisite=%s",
						prereq), e);
			}

		}

		ppb.setPrereqGameEntityId(prereq.getPrereqGameEntityId());
		ppb.setQuantity(prereq.getQuantity());

		return ppb.build();
	}

	/** Quest.proto ****************************************************/
	public FullQuestProto createFullQuestProtoFromQuest(Quest quest) {
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
				log.error(String.format("invalid monsterElement. quest=%s",
						quest), e);
			}
		}

		List<QuestJobProto> jobsList = createQuestJobProto(quest.getId());
		builder.addAllJobs(jobsList);

		return builder.build();
	}

	public List<QuestJobProto> createQuestJobProto(int questId) {
		Map<Integer, QuestJob> questJobIdsForQuestId = questJobRetrieveUtils
				.getQuestJobsForQuestId(questId);
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

	public QuestJobProto createQuestJobProto(QuestJob qj) {
		QuestJobProto.Builder qjpb = QuestJobProto.newBuilder();

		qjpb.setQuestJobId(qj.getId());
		qjpb.setQuestId(qj.getQuestId());

		String aStr = qj.getQuestJobType();
		if (null != aStr) {
			try {
				QuestJobType qjt = QuestJobType.valueOf(aStr);
				qjpb.setQuestJobType(qjt);
			} catch (Exception e) {
				log.error(String.format("incorrect QuestJobType. QuestJob=%s",
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

	public DialogueProto createDialogueProtoFromDialogue(Dialogue d) {

		DialogueProto.Builder dp = DialogueProto.newBuilder();
		if (d == null) {
			return dp.build();
		}

		List<String> speakerTexts = d.getSpeakerTexts();
		List<String> speakers = d.getSpeakers();
		List<String> speakerImages = d.getSpeakerImages();
		List<Boolean> isLeftSides = d.getIsLeftSides();
		for (int i = 0; i < speakerTexts.size(); i++) {
			dp.addSpeechSegment(SpeechSegmentProto.newBuilder()
					.setSpeaker(speakers.get(i))
					.setSpeakerText(speakerTexts.get(i))
					.setIsLeftSide(isLeftSides.get(i))
					.setSpeakerImage(speakerImages.get(i)).build());
		}
		return dp.build();
	}

	public List<FullUserQuestProto> createFullUserQuestDataLarges(
			List<QuestForUser> userQuests,
			Map<Integer, Quest> questIdsToQuests,
			Map<Integer, Collection<QuestJobForUser>> questIdToUserQuestJobs) {
		List<FullUserQuestProto> fullUserQuestDataLargeProtos = new ArrayList<FullUserQuestProto>();

		for (QuestForUser userQuest : userQuests) {
			int questId = userQuest.getQuestId();
			Quest quest = questIdsToQuests.get(questId);
			FullUserQuestProto.Builder builder = FullUserQuestProto
					.newBuilder();

			if (null == quest) {
				log.error(String.format("no quest with id=%s, userQuest=%s",
						userQuest.getQuestId(), userQuest));
			}

			if (null != quest) {
				builder.setUserUuid(userQuest.getUserId());
				builder.setQuestId(quest.getId());
				builder.setIsRedeemed(userQuest.isRedeemed());
				builder.setIsComplete(userQuest.isComplete());

				//protofy the userQuestJobs
				List<UserQuestJobProto> userQuestJobProtoList = createUserQuestJobProto(
						questId, questIdToUserQuestJobs);

				builder.addAllUserQuestJobs(userQuestJobProtoList);
				fullUserQuestDataLargeProtos.add(builder.build());

			}
		}
		return fullUserQuestDataLargeProtos;
	}

	public List<UserQuestJobProto> createUserQuestJobProto(int questId,
			Map<Integer, Collection<QuestJobForUser>> questIdToUserQuestJobs) {
		List<UserQuestJobProto> userQuestJobProtoList = new ArrayList<UserQuestJobProto>();

		if (!questIdToUserQuestJobs.containsKey(questId)) {
			//should never go in here!
			log.error(String
					.format("user has Quest but no QuestJobs. questId=%s. User's quest jobs:%s",
							questId, questIdToUserQuestJobs));

			return userQuestJobProtoList;
		}

		for (QuestJobForUser qjfu : questIdToUserQuestJobs.get(questId)) {
			UserQuestJobProto uqjp = createUserJobProto(qjfu);
			userQuestJobProtoList.add(uqjp);
		}
		return userQuestJobProtoList;
	}

	public UserQuestJobProto createUserJobProto(QuestJobForUser qjfu) {
		UserQuestJobProto.Builder uqjpb = UserQuestJobProto.newBuilder();

		uqjpb.setQuestId(qjfu.getQuestId());
		uqjpb.setQuestJobId(qjfu.getQuestJobId());
		uqjpb.setIsComplete(qjfu.isComplete());
		uqjpb.setProgress(qjfu.getProgress());

		return uqjpb.build();
	}

	/** Research.proto ****************************************/
	public ResearchProto createResearchProto(Research r,
			Collection<ResearchProperty> researchProperties) {
		ResearchProto.Builder rpb = ResearchProto.newBuilder();

		rpb.setResearchId(r.getId());
		String rt = r.getResearchType();
		if (null != rt) {
			try {
				ResearchType researchType = ResearchType.valueOf(rt);

				rpb.setResearchType(researchType);
			} catch (Exception e) {
				log.error(String.format(
						"invalid research type. Researchtype=%s", rt), e);
			}

		}

		String rd = r.getResearchDomain();

		if (null != rd) {
			try {
				ResearchDomain researchDomain = ResearchDomain.valueOf(rd);
				rpb.setResearchDomain(researchDomain);
			} catch (Exception e) {
				log.error(String.format(
						"invalid research domain. Researchdomain=%s", rd), e);
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
		if (succId > 0) {
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
				log.error(
						String.format("invalid ResourceType. Research=%s", r),
						e);
			}
		}

		if (null != researchProperties) {
			List<ResearchPropertyProto> rppList = createResearchPropertyProto(researchProperties);
			rpb.addAllProperties(rppList);
		}

		rpb.setPriority(r.getPriority());
		rpb.setTier(r.getTier());
		rpb.setStrength(r.getStrength());

		return rpb.build();
	}

	public List<ResearchPropertyProto> createResearchPropertyProto(
			Collection<ResearchProperty> rpCollection) {
		List<ResearchPropertyProto> retVal = new ArrayList<ResearchPropertyProto>();
		for (ResearchProperty bp : rpCollection) {
			ResearchPropertyProto bpp = createResearchPropertyProto(bp);

			retVal.add(bpp);
		}

		return retVal;
	}

	public ResearchPropertyProto createResearchPropertyProto(
			ResearchProperty rp) {
		ResearchPropertyProto.Builder rppb = ResearchPropertyProto.newBuilder();

		rppb.setResearchPropertyId(rp.getId());

		String str = rp.getName();
		if (null != str && !str.isEmpty()) {
			rppb.setName(str);
		}

		rppb.setResearchValue(rp.getValue());
		rppb.setResearchId(rp.getId());

		return rppb.build();
	}

	public Collection<UserResearchProto> createUserResearchProto(
			Collection<ResearchForUser> userResearchs) {
		List<UserResearchProto> urpList = new ArrayList<UserResearchProto>();
		for (ResearchForUser rfu : userResearchs) {
			UserResearchProto urp = createUserResearchProto(rfu);
			urpList.add(urp);
		}
		return urpList;
	}

	public UserResearchProto createUserResearchProto(ResearchForUser rfu) {
		UserResearchProto.Builder urpb = UserResearchProto.newBuilder();
		urpb.setUserResearchUuid(rfu.getId());
		urpb.setResearchId(rfu.getResearchId());
		urpb.setUserUuid(rfu.getUserId());
		urpb.setTimePurchased(rfu.getTimePurchased().getTime());
		urpb.setComplete(rfu.isComplete());

		return urpb.build();
	}

	/** Reward.proto ***************************************************/
	public RewardProto createRewardProto(Reward r) {
		return createRewardProto(r, 1);
	}

	public RewardProto createRewardProto(Reward r, int currentDepth)
	{
		RewardProto.Builder rpb = RewardProto.newBuilder();

		rpb.setRewardId(r.getId());
		rpb.setStaticDataId(r.getStaticDataId());
		String str = r.getType();

		if (null != str && !str.isEmpty()) {
			try {
				RewardType rt = RewardType.valueOf(str);
				rpb.setTyp(rt);

				if (rt == RewardType.REWARD) {
					if (currentDepth <= 3) {
						Reward rr = rewardRetrieveUtils.getRewardById(r.getStaticDataId());
						rpb.setActualReward(createRewardProto(rr, currentDepth + 1));
					} else {
						log.error("reward {} reached depth={}", r, currentDepth);
					}
				}
			} catch (Exception e) {
				log.error(String.format("invalid RewardType. Reward=%s",
						r), e);
			}
		}
		rpb.setAmt(r.getAmt());

		return rpb.build();
	}

	public UserRewardProto createUserRewardProto(
			Collection<ItemForUser> newOrUpdatedIfu,
			Collection<FullUserMonsterProto> fumpList,
			int gems, int cash, int oil, int gachaCredits, UserClanGiftProto ucgp)
	{
		UserRewardProto.Builder urp = UserRewardProto.newBuilder();

		if (null != fumpList && !fumpList.isEmpty()) {
			urp.addAllUpdatedOrNewMonsters(fumpList);
		}

		if (null != newOrUpdatedIfu && !newOrUpdatedIfu.isEmpty()) {
			Collection<UserItemProto> userItems = createUserItemProtosFromUserItems((List<ItemForUser>)newOrUpdatedIfu);
			urp.addAllUpdatedUserItems(userItems);
		}

		urp.setGems(gems);
		urp.setCash(cash);
		urp.setOil(oil);

		if(ucgp != null) {
			urp.setClanGift(ucgp);
		}
		urp.setGachaCredits(gachaCredits);

		return urp.build();
	}

	public UserGiftProto createUserGiftProto(GiftForUser gfu,
			MinimumUserProto gifterMup, Reward r, ClanGift cg,
			GiftForTangoUser gftu, TangoGift tg)
	{
		UserGiftProto.Builder ugpb = UserGiftProto.newBuilder();
		ugpb.setUgId(gfu.getId());
		ugpb.setReceiverUserId(gfu.getReceiverUserId());
		ugpb.setGifterUser(gifterMup);

		String gt = gfu.getGiftType();
		if (null != gt && !gt.isEmpty()) {
			try {
				RewardType rt = RewardType.valueOf(gt);
				ugpb.setGiftType(rt);

				switch (rt) {
				case CLAN_GIFT:
					ugpb.setClanGift(createClanGiftProto(cg));
					break;
				case TANGO_GIFT:
					ugpb.setTangoGift(createUserTangoGiftProto(gftu, tg));
				default:
					break;
				}

			} catch(Exception e) {
				log.error(
						String.format("incorrect enum RewardType. GiftForUser=%s.", gfu),
						e);
			}
		}

		Date timeOfEntry = gfu.getTimeOfEntry();
		ugpb.setTimeReceived(timeOfEntry.getTime());
		ugpb.setRp(createRewardProto(r));
		ugpb.setHasBeenCollected(gfu.isCollected());
		ugpb.setMinutesTillExpiration(gfu.getMinutesTillExpiration());

		return ugpb.build();
	}

	public ClanGiftProto createClanGiftProto(ClanGift cg) {
		ClanGiftProto.Builder b = ClanGiftProto.newBuilder();
		b.setClanGiftId(cg.getId());
		if(cg.getName() != null) {
			b.setName(cg.getName());
		}

		b.setHoursUntilExpiration(cg.getHoursUntilExpiration());
		b.setImageName(cg.getImageName());
//		b.setQuality(Quality.valueOf(cg.getQuality()));

		return b.build();
	}

	public UserTangoGiftProto createUserTangoGiftProto(GiftForTangoUser gftu,
			TangoGift tg)
	{
		UserTangoGiftProto.Builder utgpb = UserTangoGiftProto.newBuilder();
		utgpb.setUserGiftId(gftu.getGifterUserId());
		utgpb.setGifterTangoUserId(gftu.getGifterTangoUserId());

		TangoGiftProto tgp = createTangoGiftProto(tg);
		utgpb.setTangoGift(tgp);

		return utgpb.build();
	}

	public TangoGiftProto createTangoGiftProto(TangoGift tg) {
		TangoGiftProto.Builder tgpb = TangoGiftProto.newBuilder();
		tgpb.setTangoGiftId(tg.getId());
		tgpb.setName(tg.getName());
		tgpb.setHoursUntilExpiration(tg.getHoursUntilExpiration());
		tgpb.setImageName(tg.getImageName());
		return tgpb.build();
	}

	/** Skill.proto ***************************************************/
	public SkillProto createSkillProtoFromSkill(Skill s,
			Map<Integer, SkillProperty> skillPropertyIdToProperty) {
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
				log.error(
						String.format("incorrect enum SkillType. Skill=%s.", s),
						e);
			}
		}

		str = s.getActivationType();
		if (null != str) {
			try {
				SkillActivationType st = SkillActivationType.valueOf(str);
				spb.setActivationType(st);
			} catch (Exception e) {
				log.error(String.format(
						"incorrect enum SkillActivationType. Skill=%s.", s), e);
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
				spb.addProperties(createSkillPropertyProtoFromSkillProperty(sp));
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

	public SkillPropertyProto createSkillPropertyProtoFromSkillProperty(
			SkillProperty property) {
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

	public SkillSideEffectProto createSkillSideEffectProto(
			SkillSideEffect sse) {
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
						"incorrect enum SideEffectType. SkillSideEffect=%s",
						sse), e);
			}
		}

		str = sse.getTraitType();
		if (null != str) {
			try {
				SideEffectTraitType sett = SideEffectTraitType.valueOf(str);
				ssepb.setTraitType(sett);
			} catch (Exception e) {
				log.error(
						String.format(
								"incorrect enum SideEffectTraitType. SkillSideEffect=%s",
								sse), e);
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
				SideEffectPositionType sept = SideEffectPositionType
						.valueOf(str);
				ssepb.setPositionType(sept);
			} catch (Exception e) {
				log.error(
						String.format(
								"incorrect enum SideEffectPositionType. SkillSideEffect=%s",
								sse), e);
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
				log.error(
						String.format(
								"incorrect enum SideEffectBlendMode. SkillSideEffect=%s",
								sse), e);
			}
		}

		return ssepb.build();
	}

	/** Structure.proto ************************************************/
	public StructureInfoProto createStructureInfoProtoFromStructure(
			Structure s) {
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
			log.error(
					String.format("incorrect enum structType. Structure=%s", s),
					e);
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
		builder.setStrength(s.getStrength());

		return builder.build();
	}

	public ResourceGeneratorProto createResourceGeneratorProto(
			Structure s, StructureInfoProto sip, StructureResourceGenerator srg) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ResourceGeneratorProto.Builder rgpb = ResourceGeneratorProto
				.newBuilder();
		rgpb.setStructInfo(sip);

		String aStr = srg.getResourceTypeGenerated();
		try {
			ResourceType rt = ResourceType.valueOf(aStr);
			rgpb.setResourceType(rt);
		} catch (Exception e) {
			log.error(String.format(
					"incorrect enum ResourceType. ResourceGenerator=%s", srg),
					e);
		}

		rgpb.setProductionRate(srg.getProductionRate());
		rgpb.setCapacity(srg.getCapacity());

		return rgpb.build();
	}

	public MoneyTreeProto createMoneyTreeProtoFromStructureMoneyTree(
			Structure s, StructureInfoProto sip, StructureMoneyTree smt) {
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

	public ResearchHouseProto createResearchHouseProtoFromStructureResearchHouse(
			Structure s, StructureInfoProto sip, StructureResearchHouse srh) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ResearchHouseProto.Builder rhpb = ResearchHouseProto.newBuilder();

		rhpb.setStructInfo(sip);
		rhpb.setResearchSpeedMultiplier(srh.getResearchSpeedMultiplier());
		return rhpb.build();
	}

	public ResourceStorageProto createResourceStorageProto(Structure s,
			StructureInfoProto sip, StructureResourceStorage srs) {
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
					"incorrect enum ResourceType. resourceStorage=%s", srs), e);
		}

		rspb.setCapacity(srs.getCapacity());

		return rspb.build();
	}

	public HospitalProto createHospitalProto(Structure s,
			StructureInfoProto sip, StructureHospital sh) {
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

	public LabProto createLabProto(Structure s, StructureInfoProto sip,
			StructureLab sl) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		LabProto.Builder lpb = LabProto.newBuilder();
		lpb.setStructInfo(sip);
		lpb.setQueueSize(sl.getQueueSize());
		lpb.setPointsMultiplier(sl.getPointsMultiplier());

		return lpb.build();
	}

	public ResidenceProto createResidenceProto(Structure s,
			StructureInfoProto sip, StructureResidence sr) {
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

	public TownHallProto createTownHallProto(Structure s,
			StructureInfoProto sip, StructureTownHall sth) {
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

	public MiniJobCenterProto createMiniJobCenterProto(Structure s,
			StructureInfoProto sip, StructureMiniJob miniJobCenter,
			Map<Integer, MiniJobRefreshItem> idToMjriMap)
	{
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		MiniJobCenterProto.Builder smjcpb = MiniJobCenterProto.newBuilder();
		smjcpb.setStructInfo(sip);
		smjcpb.setGeneratedJobLimit(miniJobCenter.getGeneratedJobLimit());
		smjcpb.setHoursBetweenJobGeneration(miniJobCenter
				.getHoursBetweenJobGeneration());

		return smjcpb.build();
	}

	public FullUserStructureProto createFullUserStructureProtoFromUserstruct(
			StructureForUser userStruct) {
		FullUserStructureProto.Builder builder = FullUserStructureProto
				.newBuilder();
		builder.setUserStructUuid(userStruct.getId());
		builder.setUserUuid(userStruct.getUserId());
		builder.setStructId(userStruct.getStructId());
		//    builder.setLevel(userStruct.getLevel());
		builder.setFbInviteStructLvl(userStruct.getFbInviteStructLvl());
		builder.setIsComplete(userStruct.isComplete());
		builder.setCoordinates(createCoordinateProtoFromCoordinatePair(userStruct
				.getCoordinates()));
		String orientation = userStruct.getOrientation();
		try {
			StructOrientation so = StructOrientation.valueOf(orientation);
			builder.setOrientation(so);
		} catch (Exception e) {
			log.error(String.format(
					"incorrect orientation. structureForUser=%s", userStruct),
					e);
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

	public FullUserStructureProto createFullUserStructureProtoFromUserStructPojo(
			StructureForUserPojo userStructPojo) {
		FullUserStructureProto.Builder builder = FullUserStructureProto
				.newBuilder();
		builder.setUserStructUuid(userStructPojo.getId());
		builder.setUserUuid(userStructPojo.getUserId());
		builder.setStructId(userStructPojo.getStructId());
		//    builder.setLevel(userStruct.getLevel());
		builder.setFbInviteStructLvl(userStructPojo.getFbInviteStructLvl());
		
		if(userStructPojo.getIsComplete().equals((byte) 0)) {
			builder.setIsComplete(false);
		}
		else {
			builder.setIsComplete(true);
		}
		
		CoordinatePair cp = new CoordinatePair(userStructPojo.getXcoord(), userStructPojo.getYcoord());
		builder.setCoordinates(createCoordinateProtoFromCoordinatePair(cp));
		String orientation = userStructPojo.getOrientation();
		try {
			StructOrientation so = StructOrientation.valueOf(orientation);
			builder.setOrientation(so);
		} catch (Exception e) {
			log.error(String.format(
					"incorrect orientation. structureForUser=%s", userStructPojo),
					e);
		}

		if (userStructPojo.getPurchaseTime() != null) {
			builder.setPurchaseTime(userStructPojo.getPurchaseTime().getTime());
		}
		if (userStructPojo.getLastRetrieved() != null) {
			builder.setLastRetrieved(userStructPojo.getLastRetrieved().getTime());
		}
		//    if (userStruct.getLastUpgradeTime() != null) {
		//      builder.setLastUpgradeTime(userStruct.getLastUpgradeTime().getTime());
		//    }
		return builder.build();
	}

	public CoordinateProto createCoordinateProtoFromCoordinatePair(
			CoordinatePair cp) {
		CoordinateProto.Builder cpb = CoordinateProto.newBuilder();
		cpb.setX(cp.getX());
		cpb.setY(cp.getY());

		return cpb.build();
	}

	public TutorialStructProto createTutorialStructProto(int structId,
			float posX, float posY) {
		TutorialStructProto.Builder tspb = TutorialStructProto.newBuilder();

		tspb.setStructId(structId);
		CoordinatePair cp = new CoordinatePair(posX, posY);
		CoordinateProto cpp = createCoordinateProtoFromCoordinatePair(cp);
		tspb.setCoordinate(cpp);
		return tspb.build();
	}

	public ObstacleProto createObstacleProtoFromObstacle(Obstacle o) {
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
			log.info(
					String.format(
							"incorrect ResourceType (RemovalCostType). Obstacle=%s",
							ob), e);
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

	public MinimumObstacleProto createMinimumObstacleProto(
			int obstacleId, float posX, float posY, int orientation) {

		MinimumObstacleProto.Builder mopb = MinimumObstacleProto.newBuilder();
		mopb.setObstacleId(obstacleId);

		CoordinatePair cp = new CoordinatePair(posX, posY);
		CoordinateProto cProto = createCoordinateProtoFromCoordinatePair(cp);
		mopb.setCoordinate(cProto);

		try {
			StructOrientation structOrientation = StructOrientation
					.valueOf(orientation);
			mopb.setOrientation(structOrientation);
		} catch (Exception e) {
			log.info(
					String.format(
							"incorrect StructOrientation. ObstacleId=%s, posX=%s, posY=%s, orientation=%s",
							obstacleId, posX, posY, orientation), e);
		}

		return mopb.build();
	}

	public UserObstacleProto createUserObstacleProto(ObstacleForUser ofu) {
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
						"incorrect struct orientation=%s, ofu=%s", orientation,
						ofu));
			}
		}

		Date removalStartTime = ofu.getRemovalTime();
		if (null != removalStartTime) {
			uopb.setRemovalStartTime(removalStartTime.getTime());
		}

		return uopb.build();
	}

	public EvoChamberProto createEvoChamberProto(Structure s,
			StructureInfoProto sip, StructureEvoChamber sec) {
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
						"incorrect QualityUnlocked. EvoChamber=%s", sec), e);
			}
		}
		ecpb.setEvoTierUnlocked(sec.getEvoTierUnlocked());

		return ecpb.build();
	}

	public TeamCenterProto createTeamCenterProto(Structure s,
			StructureInfoProto sip, StructureTeamCenter sec) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		TeamCenterProto.Builder tcpb = TeamCenterProto.newBuilder();
		tcpb.setStructInfo(sip);
		tcpb.setTeamCostLimit(sec.getTeamCostLimit());

		return tcpb.build();
	}

	public ClanHouseProto createClanHouseProto(Structure s,
			StructureInfoProto sip, StructureClanHouse sch) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		ClanHouseProto.Builder chpb = ClanHouseProto.newBuilder();
		chpb.setStructInfo(sip);
		chpb.setMaxHelpersPerSolicitation(sch.getMaxHelpersPerSolicitation());
		chpb.setTeamDonationPowerLimit(sch.getTeamDonationPowerLimit());

		return chpb.build();
	}

	public PvpBoardHouseProto createPvpBoardHouseProto(Structure s,
			StructureInfoProto sip, StructurePvpBoard spb) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		PvpBoardHouseProto.Builder chpb = PvpBoardHouseProto.newBuilder();
		chpb.setStructInfo(sip);
		chpb.setPvpBoardPowerLimit(spb.getPowerLimit());

		return chpb.build();
	}

	public PvpBoardObstacleProto createPvpBoardObstacleProto(
			BoardObstacle bo) {
		PvpBoardObstacleProto.Builder pbopb = PvpBoardObstacleProto
				.newBuilder();
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
								bo), e);
			}
		}
		pbopb.setPowerAmt(bo.getPowerAmt());
		pbopb.setInitiallyAvailable(bo.getInitAvailable());

		return pbopb.build();
	}

	public List<UserPvpBoardObstacleProto> createUserPvpBoardObstacleProto(
			List<PvpBoardObstacleForUser> boardObstacles) {
		List<UserPvpBoardObstacleProto> upbopList = new ArrayList<UserPvpBoardObstacleProto>();
		for (PvpBoardObstacleForUser pbofu : boardObstacles) {
			UserPvpBoardObstacleProto upbop = createUserPvpBoardObstacleProto(pbofu);
			upbopList.add(upbop);
		}
		return upbopList;
	}

	public UserPvpBoardObstacleProto createUserPvpBoardObstacleProto(
			PvpBoardObstacleForUser pbofu) {
		UserPvpBoardObstacleProto.Builder upopb = UserPvpBoardObstacleProto
				.newBuilder();
		upopb.setUserPvpBoardObstacleId(pbofu.getId());
		upopb.setUserUuid(pbofu.getUserId());
		upopb.setObstacleId(pbofu.getObstacleId());
		upopb.setPosX(pbofu.getPosX());
		upopb.setPosY(pbofu.getPosY());
		return upopb.build();
	}

	public BattleItemFactoryProto createBattleItemFactoryProto(
			Structure s, StructureInfoProto sip, StructureBattleItemFactory sbif) {
		if (null == sip) {
			sip = createStructureInfoProtoFromStructure(s);
		}

		BattleItemFactoryProto.Builder bifpb = BattleItemFactoryProto
				.newBuilder();
		bifpb.setPowerLimit(sbif.getPowerLimit());
		bifpb.setStructInfo(sip);

		return bifpb.build();
	}

	/** research.proto *******************************************/

	/** Task.proto *****************************************************/
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
	public TaskStageProto createTaskStageProto(int taskId, int stageNum,
			List<TaskStageForUser> monsters) {
		TaskStageProto.Builder tspb = TaskStageProto.newBuilder();

		TaskStage ts = taskStageRetrieveUtils.getTaskStageForTaskStageId(
				taskId, stageNum);
		int taskStageId = ts.getId();
		tspb.setStageId(taskStageId);
		tspb.setAttackerAlwaysHitsFirst(ts.isAttackerAlwaysHitsFirst());

		for (TaskStageForUser tsfu : monsters) {
			TaskStageMonsterProto tsmp = createTaskStageMonsterProto(tsfu);
			tspb.addStageMonsters(tsmp);
		}

		return tspb.build();
	}

	public FullTaskProto createFullTaskProtoFromTask(Task task) {
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

	private void setDetails(int taskId,
			com.lvl6.proto.TaskProto.FullTaskProto.Builder builder) {
		Set<Integer> stageIds = taskStageRetrieveUtils
				.getTaskStageIdsForTaskId(taskId);
		//aggregating all the monsterIds and rarities for a task

		Set<Integer> monsterIdsForTask = new HashSet<Integer>();
		Set<String> qualitiesStrForTask = new HashSet<String>();
		for (int stageId : stageIds) {

			Set<Integer> stageMonsterIds = taskStageMonsterRetrieveUtils
					.getDroppableMonsterIdsForTaskStageId(stageId);
			monsterIdsForTask.addAll(stageMonsterIds);

			Set<String> stageQualities = taskStageMonsterRetrieveUtils
					.getDroppableQualitiesForTaskStageId(stageId);
			qualitiesStrForTask.addAll(stageQualities);
		}

		Set<Quality> qualitiesForTask = new HashSet<Quality>();
		for (String quality : qualitiesStrForTask) {
			try {
				Quality q = Quality.valueOf(quality);
				qualitiesForTask.add(q);
			} catch (Exception e) {
				log.error("illegal Quality type {}. taskId={}", quality, taskId);
			}
		}

		builder.addAllMonsterIds(monsterIdsForTask);
		builder.addAllRarities(qualitiesForTask);
	}

	public MinimumUserTaskProto createMinimumUserTaskProto(
			String userId, TaskForUserOngoing aTaskForUser,
			TaskForUserClientState tfucs) {
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
			log.error(String.format(
					"unable to convert byte[] to google.ByteString, userId=%s",
					userId), e);
		}

		return mutpb.build();
	}

	public List<UserTaskCompletedProto> createUserTaskCompletedProto(
			List<UserTaskCompleted> utcList) {
		List<UserTaskCompletedProto> retVal = new ArrayList<UserTaskCompletedProto>();
		for (UserTaskCompleted utc : utcList) {
			UserTaskCompletedProto utcp = createUserTaskCompletedProto(utc);
			retVal.add(utcp);
		}
		return retVal;
	}

	public UserTaskCompletedProto createUserTaskCompletedProto(
			UserTaskCompleted utc) {
		UserTaskCompletedProto.Builder utcpb = UserTaskCompletedProto
				.newBuilder();
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

	public TaskStageMonsterProto createTaskStageMonsterProto(
			TaskStageForUser tsfu) {
		int tsmId = tsfu.getTaskStageMonsterId();
		TaskStageMonster tsm = taskStageMonsterRetrieveUtils
				.getTaskStageMonsterForId(tsmId);

		int tsmMonsterId = tsm.getMonsterId();
		boolean didPieceDrop = tsfu.isMonsterPieceDropped();
		//check if monster id exists
		if (didPieceDrop) {
			Monster mon = monsterRetrieveUtils
					.getMonsterForMonsterId(tsmMonsterId);
			if (null == mon) {
				throw new RuntimeException(
						"Non existent monsterId for userTask=" + tsfu);
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
			log.error(String.format("incorrect monsterType, tsm=%s", tsm), e);
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
			Item item = itemRetrieveUtils.getItemForId(itemId);
			if (null == item) {
				throw new RuntimeException(String.format(
						"nonexistent itemId for userTask=%s", tsfu));
			}
			bldr.setItemId(itemId);
		}

		if (tsm.getMonsterIdDrop() > 0) {
			bldr.setPuzzlePieceMonsterId(tsm.getMonsterIdDrop());
			bldr.setPuzzlePieceMonsterDropLvl(tsm.getMonsterDropLvl());
		}

		int defensiveSkillId = tsm.getDefensiveSkillId();
		if (defensiveSkillId > 0) {
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

	public PersistentEventProto createPersistentEventProtoFromEvent(
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
					"incorrect enum DayOfWeek. EventPersistent=%s", event), e);
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
					"incorrect enum EventType. EventPersistent=%s", event), e);
		}
		try {
			Element elem = Element.valueOf(monsterElem);
			pepb.setMonsterElement(elem);
		} catch (Exception e) {
			log.error(
					String.format("incorrect enum MonsterElement. %s", event),
					e);
		}

		return pepb.build();
	}

	public UserPersistentEventProto createUserPersistentEventProto(
			EventPersistentForUser epfu) {
		UserPersistentEventProto.Builder upepb = UserPersistentEventProto
				.newBuilder();
		Date timeOfEntry = epfu.getTimeOfEntry();

		upepb.setUserUuid(epfu.getUserId());
		upepb.setEventId(epfu.getEventPersistentId());

		if (null != timeOfEntry) {
			upepb.setCoolDownStartTime(timeOfEntry.getTime());
		}

		return upepb.build();
	}

	public TaskMapElementProto createTaskMapElementProto(
			TaskMapElement tme) {
		TaskMapElementProto.Builder tmepb = TaskMapElementProto.newBuilder();
		tmepb.setMapElementId(tme.getId());
		tmepb.setTaskId(tme.getTaskId());
		tmepb.setXPos(tme.getxPos());
		tmepb.setYPos(tme.getyPos());

		String str = tme.getElement();
		try {
			Element me = Element.valueOf(str);
			tmepb.setElement(me);
		} catch (Exception e) {
			log.error(String.format("invalid element. TaskMapElement=%s", tme),
					e);
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
		tmepb.setStrength(tme.getStrength());

		return tmepb.build();
	}

	/** FileDownloadProto *********************************************/

	public FileDownloadConstantProto createFileDownloadProtoFromFileDownload(
			FileDownload fd) {
		FileDownloadConstantProto.Builder fdpb = FileDownloadConstantProto
				.newBuilder();
		fdpb.setFileDownloadId(fd.getId());
		fdpb.setFileName(fd.getFileName());
		fdpb.setPriority(fd.getPriority());
		fdpb.setDownloadOnlyOverWifi(fd.isDownloadOnlyOverWifi());
		fdpb.setUseIpadSuffix(fd.isUseIpadSuffix());

		return fdpb.build();
	}

	/** BattleItemProto *********************************************/

	public BattleItemProto createBattleItemProtoFromBattleItem(
			BattleItem bi) {
		BattleItemProto.Builder bipb = BattleItemProto.newBuilder();
		bipb.setBattleItemId(bi.getId());
		bipb.setName(bi.getName());
		bipb.setImgName(bi.getImageName());

		String type = bi.getType();
		if (type != null) {
			try {
				BattleItemType battleItemType = BattleItemType.valueOf(type);

				bipb.setBattleItemType(battleItemType);
			} catch (Exception e) {
				log.error(String.format(
						"invalid battleitem type. battleitemtype=%s", type), e);
			}
		}

		String category = bi.getBattleItemCategory();
		if (category != null) {
			try {
				BattleItemCategory battleItemCategory = BattleItemCategory
						.valueOf(category);

				bipb.setBattleItemCategory(battleItemCategory);

			} catch (Exception e) {
				log.error(String.format(
						"invalid battleitem category. battleitemcateogry=%s",
						category), e);
			}
		}

		String resourceType = bi.getCreateResourceType();
		if (null != resourceType) {
			try {
				ResourceType rt = ResourceType.valueOf(resourceType);
				bipb.setCreateResourceType(rt);
			} catch (Exception e) {
				log.error(
						String.format("invalid ResourceType. resource type=%s",
								resourceType), e);
			}
		}
		bipb.setCreateCost(bi.getCreateCost());
		if (bi.getDescription() != null) {
			bipb.setDescription(bi.getDescription());
		}
		bipb.setPowerAmount(bi.getPowerAmount());
		bipb.setPriority(bi.getPriority());
		bipb.setMinutesToCreate(bi.getMinutesToCreate());
		bipb.setInBattleGemCost(bi.getInBattleGemCost());
		bipb.setAmount(bi.getAmount());

		return bipb.build();
	}

	public List<UserBattleItemProto> convertBattleItemForUserListToBattleItemForUserProtoList(
			List<BattleItemForUser> bifuList) {
		List<UserBattleItemProto> returnList = new ArrayList<UserBattleItemProto>();
		for (BattleItemForUser bifu : bifuList) {
			UserBattleItemProto ubip = createUserBattleItemProtoFromBattleItemForUser(bifu);
			returnList.add(ubip);
		}
		return returnList;
	}

	public UserBattleItemProto createUserBattleItemProtoFromBattleItemForUser(
			BattleItemForUser bifu) {
		UserBattleItemProto.Builder ubipb = UserBattleItemProto.newBuilder();
		ubipb.setUserBattleItemId(bifu.getId());
		ubipb.setUserUuid(bifu.getUserId());
		ubipb.setBattleItemId(bifu.getBattleItemId());
		ubipb.setQuantity(bifu.getQuantity());

		return ubipb.build();
	}

	/** TournamentStuff.proto ******************************************/
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

	/** User.proto *****************************************************/
	public MinimumClanProto createMinimumClanProtoFromClan(Clan c) {
		MinimumClanProto.Builder mcpb = MinimumClanProto.newBuilder();
		mcpb.setClanUuid(c.getId());
		mcpb.setName(c.getName());
		//    mcp.setOwnerId(c.getOwnerId());
		mcpb.setCreateTime(c.getCreateTime().getTime());
		mcpb.setDescription(c.getDescription());
		mcpb.setTag(c.getTag());
		mcpb.setClanIconId(c.getClanIconId());
		return mcpb.setRequestToJoinRequired(c.isRequestToJoinRequired())
				.build();
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

	public MinimumUserProto createMinimumUserProtoFromUserAndClan(
			User u, Clan c) {
		MinimumUserProto.Builder builder = MinimumUserProto.newBuilder();

		String name = u.getName();
		builder.setName(name);
		builder.setUserUuid(u.getId());

		if (null != c) {
			builder.setClan(createMinimumClanProtoFromClan(c));
		}
		builder.setAvatarMonsterId(u.getAvatarMonsterId());
		builder.setStrength(u.getTotalStrength());

		return builder.build();
	}

	public MinimumUserProtoWithFacebookId createMinimumUserProtoWithFacebookId(
			User u, Clan c) {
		MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(u, c);
		MinimumUserProtoWithFacebookId.Builder b = MinimumUserProtoWithFacebookId
				.newBuilder();
		b.setMinUserProto(mup);
		String facebookId = u.getFacebookId();
		if (null != facebookId) {
			b.setFacebookId(facebookId);
		}

		return b.build();
	}

	public UserFacebookInviteForSlotProto createUserFacebookInviteForSlotProtoFromInvite(
			UserFacebookInviteForSlot invite, User inviter, Clan inviterClan,
			MinimumUserProtoWithFacebookId inviterProto) {
		UserFacebookInviteForSlotProto.Builder inviteProtoBuilder = UserFacebookInviteForSlotProto
				.newBuilder();
		inviteProtoBuilder.setInviteUuid(invite.getId());

		if (null == inviterProto) {
			inviterProto = createMinimumUserProtoWithFacebookId(inviter,
					inviterClan);

		}

		inviteProtoBuilder.setInviter(inviterProto);
		inviteProtoBuilder.setRecipientFacebookId(invite
				.getRecipientFacebookId());

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

	public FullUserProto createFullUserProtoFromUser(User u,
			PvpLeagueForUser plfu, Clan c) {
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
		builder.setNumCoinsRetrievedFromStructs(u
				.getNumCoinsRetrievedFromStructs());
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
			builder.setLastObstacleSpawnedTime(lastObstacleSpawnedTime
					.getTime());
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
			builder.setLastFreeBoosterPackTime(lastFreeBoosterPackTime
					.getTime());
		}

		Date lastSecretGiftCollectTime = u.getLastSecretGiftCollectTime();
		if (null != lastSecretGiftCollectTime) {
			builder.setLastSecretGiftCollectTime(lastSecretGiftCollectTime
					.getTime());
		}

		String pvpDefendingMessage = u.getPvpDefendingMessage();
		if (null != pvpDefendingMessage) {
			builder.setPvpDefendingMessage(pvpDefendingMessage);
		}

		//add new columns above here, not below the if. if case for is fake

		int numClanHelps = u.getClanHelps();
		builder.setNumClanHelps(numClanHelps);

		Date lastTeamDonationSolicitation = u.getLastTeamDonateSolicitation();
		if (null != lastTeamDonationSolicitation) {
			builder.setLastTeamDonationSolicitation(lastTeamDonationSolicitation
					.getTime());
		}

		if (u.isFake()) {

		}

		long totalStrength = u.getTotalStrength();
		builder.setTotalStrength(totalStrength);

		int segmentationGroup = u.getSegmentationGroup();
		builder.setSegmentationGroup(segmentationGroup);
		int gachaCredits = u.getGachaCredits();
		builder.setGachaCredits(gachaCredits);

		Date lastTangoGiftSentTime = u.getLastTangoGiftSentTime();
		if (null != lastTangoGiftSentTime) {
			builder.setLastTangoGiftSentTime(lastTangoGiftSentTime.getTime());
		}

		String tangoId = u.getTangoId();
		if (null != tangoId && !tangoId.isEmpty()) {
			builder.setTangoId(tangoId);
		}

		//don't add setting new columns/properties here, add up above

		return builder.build();
	}
	
	//using user pojo
	public FullUserProto createFullUserProtoFromUser(
			UserPojo u,
			PvpLeagueForUser plfu, Clan c) {
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
		if (u.getIsFake().compareTo((byte)0) == 0) {
			builder.setIsFake(false);
		}
		else builder.setIsFake(true);
		
		builder.setCreateTime(u.getCreateTime().getTime());
		if (u.getIsAdmin().compareTo((byte)0) == 0) {
			builder.setIsAdmin(false);
		}
		else builder.setIsAdmin(true);
		
		builder.setNumCoinsRetrievedFromStructs(u
				.getNumCoinsRetrievedFromStructs());
		builder.setNumOilRetrievedFromStructs(u.getNumOilRetrievedFromStructs());
		//		if (u.getClanId() > 0) {
		if (null != c) {
			//			Clan clan = ClanRetrieveUtils.getClanWithId(u.getClanId());
			builder.setClan(createMinimumClanProtoFromClan(c));
		}
		if (u.getHasReceivedFbReward().compareTo((byte)0) == 0) {
			builder.setHasReceivedfbReward(false);
		}
		else builder.setHasReceivedfbReward(true);

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
			builder.setLastObstacleSpawnedTime(lastObstacleSpawnedTime
					.getTime());
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
			builder.setLastFreeBoosterPackTime(lastFreeBoosterPackTime
					.getTime());
		}

		Date lastSecretGiftCollectTime = u.getLastSecretGiftCollectTime();
		if (null != lastSecretGiftCollectTime) {
			builder.setLastSecretGiftCollectTime(lastSecretGiftCollectTime
					.getTime());
		}

		String pvpDefendingMessage = u.getPvpDefendingMessage();
		if (null != pvpDefendingMessage) {
			builder.setPvpDefendingMessage(pvpDefendingMessage);
		}

		//add new columns above here, not below the if. if case for is fake

		int numClanHelps = u.getClanHelps();
		builder.setNumClanHelps(numClanHelps);

		Date lastTeamDonationSolicitation = u.getLastTeamDonateSolicitation();
		if (null != lastTeamDonationSolicitation) {
			builder.setLastTeamDonationSolicitation(lastTeamDonationSolicitation
					.getTime());
		}

		long totalStrength = u.getTotalStrength();
		builder.setTotalStrength(totalStrength);

		int segmentationGroup = u.getSegmentationGroup();
		builder.setSegmentationGroup(segmentationGroup);

		//don't add setting new columns/properties here, add up above

		return builder.build();
	}

	public MinimumUserProto createMinimumUserProto(
			FullUserProto fup) {
		MinimumUserProto.Builder mupb = MinimumUserProto.newBuilder();
		String str = fup.getUserUuid();
		if (null != str) {
			mupb.setUserUuid(str);
		}

		str = fup.getName();
		if (null != str) {
			mupb.setName(str);
		}

		MinimumClanProto mcp = fup.getClan();
		if (null != mcp) {
			mupb.setClan(mcp);
		}

		int avatarMonsterId = fup.getAvatarMonsterId();
		if (avatarMonsterId > 0) {
			mupb.setAvatarMonsterId(avatarMonsterId);
		}

		mupb.setStrength(fup.getTotalStrength());

		return mupb.build();
	}

	public Map<String, MinimumUserProto> createMinimumUserProtoFromUserAndClan(
			Map<String, User> userIdsToUsers, Map<String, Clan> userIdsToClans) {
		Map<String, MinimumUserProto> userIdToMup = new HashMap<String, MinimumUserProto>();
		for (User u : userIdsToUsers.values()) {
			String userId = u.getId();
			Clan c = userIdsToClans.get(userId);

			MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(
					u, c);

			userIdToMup.put(userId, mup);
		}
		return userIdToMup;
	}

	//  public static ReferralNotificationProto createReferralNotificationProtoFromReferral(
	//      Referral r, User newlyReferred) {
	//    return ReferralNotificationProto.newBuilder().setReferred(createMinimumUserProtoFromUser(newlyReferred))
	//        .setRecruitTime(r.getTimeOfReferral().getTime()).setCoinsGivenToReferrer(r.getCoinsGivenToReferrer()).build();
	//  }

	public AnimatedSpriteOffsetProto createAnimatedSpriteOffsetProtoFromAnimatedSpriteOffset(
			AnimatedSpriteOffset aso) {
		return AnimatedSpriteOffsetProto
				.newBuilder()
				.setImageName(aso.getImgName())
				.setOffSet(
						createCoordinateProtoFromCoordinatePair(aso.getOffSet()))
				.build();
	}

	public void createMinimumUserProtosFromClannedAndClanlessUsers(
			Map<String, Clan> clanIdsToClans,
			Map<String, Set<String>> clanIdsToUserIdSet,
			List<String> clanlessUserIds,
			Map<String, User> userIdsToUsers,
			Map<String, MinimumUserProto> userIdToMinimumUserProto) {
		//construct the minimum user protos for the clanless users
		for (String userId : clanlessUserIds) {
			User u = userIdsToUsers.get(userId);
			MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(
					u, null);
			userIdToMinimumUserProto.put(userId, mup);
		}

		//construct the minimum user protos for the users that have clans
		if (null == clanIdsToClans) {
			return;
		}
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
			for (String userId : clanIdsToUserIdSet.get(clanId)) {
				User u = userIdsToUsers.get(userId);
				MinimumUserProto mup = createMinimumUserProtoFromUserAndClan(
						u, c);
				userIdToMinimumUserProto.put(userId, mup);
			}
		}
	}

	public CustomMenuProto createCustomMenuProto(CustomMenuConfigPojo cm) {
		CustomMenuProto.Builder cmpb = CustomMenuProto.newBuilder();
		cmpb.setCustomMenuId(cm.getCustomMenuId());
		cmpb.setPositionX(cm.getPositionX());
		cmpb.setPositionY(cm.getPositionY());
		cmpb.setPositionZ(cm.getPositionZ());
		cmpb.setIsJiggle(cm.getIsJiggle());
		cmpb.setImageName(cm.getImageName());
		cmpb.setIpadPositionX(cm.getIpadPositionX());
		cmpb.setIpadPositionY(cm.getIpadPositionY());
		return cmpb.build();
	}




	///////////////////////////////CLAN GIFTS PROTOS/////////////////////////////////////////////

	public UserClanGiftProto createUserClanGiftProto(ClanGiftForUser ucg, MinimumUserProto mup) {
		UserClanGiftProto.Builder b = UserClanGiftProto.newBuilder();
		b.setUserClanGiftId(ucg.getId());
		b.setReceiverUserId(ucg.getReceiverUserId());

		if(mup != null) {
			b.setGifterUser(mup);
		}

		ClanGift cg = clanGiftRetrieveUtils.getClanGiftForClanGiftId(ucg.getClanGiftId());

		b.setClanGift(createClanGiftProto(cg));
		b.setTimeReceived(ucg.getTimeReceived().getTime());

		Reward r = rewardRetrieveUtils.getRewardById(ucg.getRewardId());
		b.setReward(createRewardProto(r));

		b.setHasBeenCollected(ucg.isHasBeenCollected());

		return b.build();
	}

	public List<StrengthLeaderBoardProto> createStrengthLeaderBoardProtosWithMonsterId(List<StrengthLeaderBoard> slbList,
			UserRetrieveUtils2 userRetrieveUtils, MonsterForUserRetrieveUtils2 monsterForUserRetrieveUtils) {
		List<StrengthLeaderBoardProto> slbpList = new ArrayList<StrengthLeaderBoardProto>();
		List<String> userIds = new ArrayList<String>();
		for(StrengthLeaderBoard slb : slbList) {
			userIds.add(slb.getUserId());
		}
		
		Map<String, User> userMap = userRetrieveUtils.getUsersByIds(userIds);
		for(StrengthLeaderBoard slb : slbList) {
			StrengthLeaderBoardProto.Builder b = StrengthLeaderBoardProto.newBuilder();
			String userId = slb.getUserId();
			b.setMup(createMinimumUserProtoFromUserAndClan(userMap.get(userId), null));
			b.setRank(slb.getRank());
			b.setStrength(slb.getStrength());
			
			slbpList.add(b.build());
		}
		return slbpList;
	}
	
	public List<StrengthLeaderBoardProto> createStrengthLeaderBoardProtos(List<StrengthLeaderBoard> slbList,
			UserRetrieveUtils2 userRetrieveUtils) {
		List<StrengthLeaderBoardProto> slbpList = new ArrayList<StrengthLeaderBoardProto>();
		List<String> userIds = new ArrayList<String>();
		for(StrengthLeaderBoard slb : slbList) {
			userIds.add(slb.getUserId());
		}
		log.info("userIds {}", userIds);
		Map<String, User> userMap = userRetrieveUtils.getUsersByIds(userIds);
		log.info("userMap: {}" + userMap);
		for(StrengthLeaderBoard slb : slbList) {
			StrengthLeaderBoardProto.Builder b = StrengthLeaderBoardProto.newBuilder();
			String userId = slb.getUserId();
			log.info("userId {}", userId);
			b.setMup(createMinimumUserProtoFromUserAndClan(userMap.get(userId), null));
			b.setRank(slb.getRank());
			b.setStrength(slb.getStrength());
			slbpList.add(b.build());
		}
		return slbpList;
	}
	
	public List<ItemGemPriceProto> createItemGemPriceProtoFromMiniJobs(List<MiniJobRefreshItemConfigPojo> mjricList) {
		List<ItemGemPriceProto> igppList = new ArrayList<ItemGemPriceProto>();
		for(MiniJobRefreshItemConfigPojo mjric : mjricList) {
			ItemGemPriceProto.Builder b = ItemGemPriceProto.newBuilder();
			b.setItemId(mjric.getItemId());
			b.setGemPrice(mjric.getGemPrice());
			b.setStructId(mjric.getStructId());
			igppList.add(b.build());
		}
		return igppList;
	}

	public List<StructStolen> createStructStolenFromGenerators(
			List<StructureForUserPojo> updateList) {
		List<StructStolen> returnList = new ArrayList<StructStolen>();
		for(StructureForUserPojo sfu : updateList) {
			StructStolen.Builder b = StructStolen.newBuilder();
			b.setUserStructUuid(sfu.getId());
			b.setTimeOfRetrieval(sfu.getLastRetrieved().getTime());
			returnList.add(b.build());
		}
		return returnList;
	}
	

}
