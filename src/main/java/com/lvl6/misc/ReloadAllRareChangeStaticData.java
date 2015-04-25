package com.lvl6.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BannedUserRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BattleItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoardObstacleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoardPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanGiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanGiftRewardsRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CustomMenuRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.EventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.FileDownloadRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GoldSaleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventForPlayerLvlRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventGoalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventLeaderboardRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniEventTierRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRefreshItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterBattleDialogueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterForPvpRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ObstacleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PrerequisiteRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ProfanityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobMonsterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.RewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
//import com.lvl6.retrieveutils.rarechange.SalesPackageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SkillPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SkillRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SkillSideEffectRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StaticUserLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureBattleItemFactoryRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureClanHouseRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureEvoChamberRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureHospitalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureLabRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMoneyTreeRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructurePvpBoardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResearchHouseRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResidenceRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceStorageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureTeamCenterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureTownHallRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskMapElementRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskStageRetrieveUtils;

@Component
@DependsOn("gameServer")
public class ReloadAllRareChangeStaticData {

	@Autowired
	protected AchievementRetrieveUtils achievementRetrieveUtils;

	@Autowired
	protected BannedUserRetrieveUtils bannedUserRetrieveUtils;

	@Autowired
	protected BattleItemRetrieveUtils battleItemRetrieveUtils;

	@Autowired
	protected BoardObstacleRetrieveUtils boardObstacleRetrieveUtils;

	@Autowired
	protected BoardRetrieveUtils boardRetrieveUtils;

	@Autowired
	protected BoardPropertyRetrieveUtils boardPropertyRetrieveUtils;

	@Autowired
	protected BoosterDisplayItemRetrieveUtils boosterDisplayItemRetrieveUtils;

	@Autowired
	protected BoosterItemRetrieveUtils boosterItemRetrieveUtils;

	@Autowired
	protected BoosterPackRetrieveUtils boosterPackRetrieveUtils;

	@Autowired
	protected ClanGiftRetrieveUtils clanGiftRetrieveUtils;

	@Autowired
	protected ClanGiftRewardsRetrieveUtils clanGiftRewardsRetrieveUtils;

	@Autowired
	protected ClanIconRetrieveUtils clanIconRetrieveUtils;

	@Autowired
	protected ClanEventPersistentRetrieveUtils clanEventPersistentRetrieveUtils;

	@Autowired
	protected ClanRaidRetrieveUtils clanRaidRetrieveUtils;

	@Autowired
	protected ClanRaidStageRetrieveUtils clanRaidStageRetrieveUtils;

	@Autowired
	protected ClanRaidStageMonsterRetrieveUtils clanRaidStageMonsterRetrieveUtils;

	@Autowired
	protected ClanRaidStageRewardRetrieveUtils clanRaidStageRewardRetrieveUtils;

	@Autowired
	protected CustomMenuRetrieveUtils customMenuRetrieveUtils;

	@Autowired
	protected EventPersistentRetrieveUtils eventPersistentRetrieveUtils;

	@Autowired
	protected FileDownloadRetrieveUtils fileDownloadRetrieveUtils;

	@Autowired
	protected GoldSaleRetrieveUtils goldSaleRetrieveUtils;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;
	
	@Autowired
	protected MonsterForPvpRetrieveUtils monsterForPvpRetrieveUtil;

	@Autowired
	protected MiniEventForPlayerLvlRetrieveUtils miniEventForPlayerLvlRetrieveUtils;

	@Autowired
	protected MiniEventGoalRetrieveUtils miniEventGoalRetrieveUtils;

	@Autowired
	protected MiniEventLeaderboardRewardRetrieveUtils miniEventLeaderboardRewardRetrieveUtils;

	@Autowired
	protected MiniEventRetrieveUtils miniEventRetrieveUtils;

	@Autowired
	protected MiniEventTierRewardRetrieveUtils miniEventTierRewardRetrieveUtils;

	@Autowired
	protected MiniJobRetrieveUtils miniJobRetrieveUtils;

	@Autowired
	protected MiniJobRefreshItemRetrieveUtils miniJobRefreshItemRetrieveUtils;

	@Autowired
	protected MonsterBattleDialogueRetrieveUtils monsterBattleDialogueRetrieveUtils;

	@Autowired
	protected MonsterLevelInfoRetrieveUtils monsterLevelInfoRetrieveUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected ObstacleRetrieveUtils obstacleRetrieveUtils;

	@Autowired
	protected PrerequisiteRetrieveUtils prerequisiteRetrieveUtils;

	@Autowired
	protected ProfanityRetrieveUtils profanityRetrieveUtils;

	@Autowired
	protected PvpLeagueRetrieveUtils pvpLeagueRetrieveUtils;

	@Autowired
	protected QuestJobRetrieveUtils questJobRetrieveUtils;

	@Autowired
	protected QuestJobMonsterItemRetrieveUtils questJobMonsterItemRetrieveUtils;

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;
	
	@Autowired
	protected ResearchPropertyRetrieveUtils researchPropertyRetrieveUtils;

	@Autowired
	protected ResearchRetrieveUtils researchRetrieveUtils;
	
	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected SalesDisplayItemRetrieveUtils salesDisplayItemRetrieveUtils;

	@Autowired
	protected SalesItemRetrieveUtils salesItemRetrieveUtils;

	@Autowired
	protected SalesPackageRetrieveUtils salesPackageRetrieveUtils;

	@Autowired
	protected SkillRetrieveUtils skillRetrieveUtils;

	@Autowired
	protected SkillPropertyRetrieveUtils skillPropertyRetrieveUtils;

	@Autowired
	protected SkillSideEffectRetrieveUtils skillSideEffectRetrieveUtils;

	@Autowired
	protected StartupStuffRetrieveUtils startupStuffRetrieveUtils;

	@Autowired
	protected StaticUserLevelInfoRetrieveUtils staticUserLevelInfoRetrieveUtils;

	@Autowired
	protected StructureBattleItemFactoryRetrieveUtils structureBattleItemFactoryRetrieveUtils;

	@Autowired
	protected StructureClanHouseRetrieveUtils structureClanHouseRetrieveUtils;

	@Autowired
	protected StructureEvoChamberRetrieveUtils structureEvoChamberRetrieveUtils;

	@Autowired
	protected StructureHospitalRetrieveUtils structureHospitalRetrieveUtils;

	@Autowired
	protected StructureLabRetrieveUtils structureLabRetrieveUtils;

	@Autowired
	protected StructureMiniJobRetrieveUtils structureMiniJobRetrieveUtils;

	@Autowired
	protected StructureMoneyTreeRetrieveUtils structureMoneyTreeRetrieveUtils;

	@Autowired
	protected StructurePvpBoardRetrieveUtils structurePvpBoardRetrieveUtils;

	@Autowired
	protected StructureResearchHouseRetrieveUtils structureResearchHouseRetrieveUtils;

	@Autowired
	protected StructureResidenceRetrieveUtils structureResidenceRetrieveUtils;

	@Autowired
	protected StructureResourceGeneratorRetrieveUtils structureResourceGeneratorRetrieveUtils;

	@Autowired
	protected StructureResourceStorageRetrieveUtils structureResourceStorageRetrieveUtils;

	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	@Autowired
	protected StructureTeamCenterRetrieveUtils structureTeamCenterRetrieveUtils;

	@Autowired
	protected StructureTownHallRetrieveUtils structureTownHallRetrieveUtils;

	@Autowired
	protected TaskMapElementRetrieveUtils taskMapElementRetrieveUtils;

	@Autowired
	protected TaskRetrieveUtils taskRetrieveUtils;

	@Autowired
	protected TaskStageMonsterRetrieveUtils taskStageMonsterRetrieveUtils;

	@Autowired
	protected TaskStageRetrieveUtils taskStageRetrieveUtils;

	@Autowired
	protected StaticDataContainer staticDataContainer;

	private static final Logger log = LoggerFactory
			.getLogger(MiscMethods.class);

	public void reloadAllRareChangeStaticData() {
		log.info("Reloading rare change static data");
		achievementRetrieveUtils.reload();
		bannedUserRetrieveUtils.reload();
		battleItemRetrieveUtils.reload();
		boardRetrieveUtils.reload();
		boardObstacleRetrieveUtils.reload();
		boardPropertyRetrieveUtils.reload();
		boosterDisplayItemRetrieveUtils.reload();
		boosterItemRetrieveUtils.reload();
		boosterPackRetrieveUtils.reload();
		//    CityBossRetrieveUtils.reload();
		//		CityElementsRetrieveUtils.reload();
		//		CityRetrieveUtils.reload();
		//    ClanBossRetrieveUtils.reload();
		//    ClanBossRewardRetrieveUtils.reload();
		clanGiftRetrieveUtils.reload();
		clanGiftRewardsRetrieveUtils.reload();
		clanIconRetrieveUtils.reload();
		clanEventPersistentRetrieveUtils.reload();
		clanRaidRetrieveUtils.reload();
		clanRaidStageRetrieveUtils.reload();
		clanRaidStageMonsterRetrieveUtils.reload();
		clanRaidStageRewardRetrieveUtils.reload();
		customMenuRetrieveUtils.reload();
		eventPersistentRetrieveUtils.reload();
		fileDownloadRetrieveUtils.reload();
		//		ExpansionCostRetrieveUtils.reload();
		goldSaleRetrieveUtils.reload();
		itemRetrieveUtils.reload();
		//		LockBoxEventRetrieveUtils.reload();
		monsterForPvpRetrieveUtil.reload();
		miniEventForPlayerLvlRetrieveUtils.reload();
		miniEventGoalRetrieveUtils.reload();
		miniEventLeaderboardRewardRetrieveUtils.reload();
		miniEventRetrieveUtils.reload();
		miniEventTierRewardRetrieveUtils.reload();
		miniJobRetrieveUtils.reload();
		miniJobRefreshItemRetrieveUtils.reload();
		monsterBattleDialogueRetrieveUtils.reload();
		monsterLevelInfoRetrieveUtils.reload();
		monsterRetrieveUtils.reload();
		obstacleRetrieveUtils.reload();
		prerequisiteRetrieveUtils.reload();
		profanityRetrieveUtils.reload();
		pvpLeagueRetrieveUtils.reload();
		questJobRetrieveUtils.reload();
		questJobMonsterItemRetrieveUtils.reload();
		questRetrieveUtils.reload();
		researchRetrieveUtils.reload();
		researchPropertyRetrieveUtils.reload();
		rewardRetrieveUtils.reload();
		skillRetrieveUtils.reload();
		skillPropertyRetrieveUtils.reload();
		skillSideEffectRetrieveUtils.reload();
		salesDisplayItemRetrieveUtils.reload();
		salesItemRetrieveUtils.reload();
//		salesPackageRetrieveUtils.reload();
		startupStuffRetrieveUtils.reload();
		staticUserLevelInfoRetrieveUtils.reload();
		structureBattleItemFactoryRetrieveUtils.reload();
		structureClanHouseRetrieveUtils.reload();
		structureEvoChamberRetrieveUtils.reload();
		structureHospitalRetrieveUtils.reload();
		structureLabRetrieveUtils.reload();
		structureMiniJobRetrieveUtils.reload();
		structureMoneyTreeRetrieveUtils.reload();
		structurePvpBoardRetrieveUtils.reload();
		structureResearchHouseRetrieveUtils.reload();
		structureResidenceRetrieveUtils.reload();
		structureResourceGeneratorRetrieveUtils.reload();
		structureResourceStorageRetrieveUtils.reload();
		structureRetrieveUtils.reload();
		structureTeamCenterRetrieveUtils.reload();
		structureTownHallRetrieveUtils.reload();
		taskMapElementRetrieveUtils.reload();
		taskRetrieveUtils.reload();
		taskStageMonsterRetrieveUtils.reload();
		taskStageRetrieveUtils.reload();
		//		TournamentEventRetrieveUtils.reload();
		//		TournamentEventRewardRetrieveUtils.reload();

		staticDataContainer.reload();
	}

}
