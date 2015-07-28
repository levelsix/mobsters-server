package com.lvl6.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.info.Achievement;
import com.lvl6.info.BattleItem;
import com.lvl6.info.Board;
import com.lvl6.info.BoardObstacle;
import com.lvl6.info.BoardProperty;
import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.ClanRaid;
import com.lvl6.info.EventPersistent;
import com.lvl6.info.Item;
import com.lvl6.info.MiniJobRefreshItem;
import com.lvl6.info.Monster;
import com.lvl6.info.MonsterBattleDialogue;
import com.lvl6.info.MonsterLevelInfo;
import com.lvl6.info.Obstacle;
import com.lvl6.info.Prerequisite;
import com.lvl6.info.PvpLeague;
import com.lvl6.info.Research;
import com.lvl6.info.ResearchProperty;
import com.lvl6.info.Skill;
import com.lvl6.info.SkillProperty;
import com.lvl6.info.SkillSideEffect;
import com.lvl6.info.StaticUserLevelInfo;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureBattleItemFactory;
import com.lvl6.info.StructureClanHouse;
import com.lvl6.info.StructureEvoChamber;
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
import com.lvl6.info.TaskMapElement;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.MiniJobRefreshItemConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfigPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniJobRefreshItemConfigPojo;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.AchievementStuffProto.AchievementProto;
import com.lvl6.proto.BattleItemsProto.BattleItemProto;
import com.lvl6.proto.BattleProto.PvpLeagueProto;
import com.lvl6.proto.BoardProto.BoardLayoutProto;
import com.lvl6.proto.BoosterPackStuffProto.BoosterPackProto;
import com.lvl6.proto.ClanProto.ClanIconProto;
import com.lvl6.proto.ClanProto.ClanRaidProto;
import com.lvl6.proto.ClanProto.PersistentClanEventProto;
import com.lvl6.proto.ItemsProto.ItemProto;
import com.lvl6.proto.MonsterStuffProto.MonsterBattleDialogueProto;
import com.lvl6.proto.PrerequisiteProto.PrereqProto;
import com.lvl6.proto.ResearchsProto.ResearchProto;
import com.lvl6.proto.RewardsProto.GiftProto;
import com.lvl6.proto.SkillsProto.SkillProto;
import com.lvl6.proto.SkillsProto.SkillSideEffectProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto.Builder;
import com.lvl6.proto.StructureProto.BattleItemFactoryProto;
import com.lvl6.proto.StructureProto.ClanHouseProto;
import com.lvl6.proto.StructureProto.EvoChamberProto;
import com.lvl6.proto.StructureProto.HospitalProto;
import com.lvl6.proto.StructureProto.LabProto;
import com.lvl6.proto.StructureProto.MiniJobCenterProto;
import com.lvl6.proto.StructureProto.MoneyTreeProto;
import com.lvl6.proto.StructureProto.ObstacleProto;
import com.lvl6.proto.StructureProto.PvpBoardHouseProto;
import com.lvl6.proto.StructureProto.PvpBoardObstacleProto;
import com.lvl6.proto.StructureProto.ResearchHouseProto;
import com.lvl6.proto.StructureProto.ResidenceProto;
import com.lvl6.proto.StructureProto.ResourceGeneratorProto;
import com.lvl6.proto.StructureProto.ResourceStorageProto;
import com.lvl6.proto.StructureProto.StructureInfoProto;
import com.lvl6.proto.StructureProto.TeamCenterProto;
import com.lvl6.proto.StructureProto.TownHallProto;
import com.lvl6.proto.TaskProto.FullTaskProto;
import com.lvl6.proto.TaskProto.PersistentEventProto;
import com.lvl6.proto.TaskProto.TaskMapElementProto;
import com.lvl6.proto.UserProto.StaticUserLevelInfoProto;
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
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageMonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidStageRewardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.CustomMenuRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.EventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.FileDownloadRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GiftRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.GoldSaleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRefreshItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterBattleDialogueRetrieveUtils;
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
import com.lvl6.server.controller.utils.InAppPurchaseUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.DBConnection;

//goal is to not compute proto analog of static data
//and to provide a consistent view of static data.
//Data is refreshed when MiscMethods.reloadAllRareChangeStaticData()
//	is called, which refreshes all data.
//This class will hold the previous snapshot of all static data until
//	the data is refreshed, after which this class will hold the current
//	snapshot of all static data.
@Component
public class StaticDataContainer {

	private static Logger log = LoggerFactory.getLogger(StaticDataContainer.class);

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

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
	protected GiftRetrieveUtils giftRetrieveUtil;

	@Autowired
	protected GoldSaleRetrieveUtils goldSaleRetrieveUtils;

	@Autowired
	protected InAppPurchaseUtils inAppPurchaseUtils;

	@Autowired
	protected ItemRetrieveUtils itemRetrieveUtils;

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
	protected ResearchRetrieveUtils researchRetrieveUtils;

	@Autowired
	protected ResearchPropertyRetrieveUtils researchPropertyRetrieveUtils;

	@Autowired
	protected RewardRetrieveUtils rewardRetrieveUtils;

	@Autowired
	protected SalesDisplayItemRetrieveUtils salesDisplayItemRetrieveUtils;

	@Autowired
	protected SalesItemRetrieveUtils salesItemRetrieveUtils;

//	@Autowired
//	protected SalesPackageRetrieveUtils salesPackageRetrieveUtils;

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
	protected MiniJobRefreshItemConfigDao miniJobRefreshDao;



	private static StaticDataProto.Builder staticDataBuilder = null;

	public StaticDataProto getStaticData() {
		if (null == staticDataBuilder) {
			setStaticData();
		}

		if (null == staticDataBuilder) {
			log.error("CANNOT SET UP STATIC DATA!!!!");
			return null;
		}
		return staticDataBuilder.build();
	}

	public void reload() {
		setStaticData();
	}

	private void setStaticData() {
		StaticDataProto.Builder sdpb = StaticDataProto.newBuilder();
		//		setPlayerCityExpansions(sdpb);
		//		setCities(sdpb);
		setTasks(sdpb);
		setMonsters(sdpb);
		setUserLevelStuff(sdpb);
		setBoosterPackStuff(sdpb);
		setStructures(sdpb);
		setEvents(sdpb);
		setMonsterDialogue(sdpb);
		setClanRaidStuff(sdpb);
		setItemStuff(sdpb);
		setObstacleStuff(sdpb);
		setClanIconStuff(sdpb);
		setPvpLeagueStuff(sdpb);
		setAchievementStuff(sdpb);
		setSkillStuff(sdpb);
		setPrereqs(sdpb);
		setBoards(sdpb);
		setResearch(sdpb);
		setBattleItem(sdpb);
//		setRewards(sdpb);
//		setSales(sdpb);
		setGifts(sdpb);
		setRefreshMiniJobItemPrices(sdpb);

		staticDataBuilder = sdpb;
	}

	//	private static void setPlayerCityExpansions(Builder sdpb) {
	//		//Player city expansions
	//		Map<Integer, ExpansionCost> expansionCosts =
	//			ExpansionCostRetrieveUtils.getAllExpansionNumsToCosts();
	//		for (ExpansionCost cec : expansionCosts.values()) {
	//			CityExpansionCostProto cecp = createInfoProtoUtils
	//				.createCityExpansionCostProtoFromCityExpansionCost(cec);
	//			sdpb.addExpansionCosts(cecp);
	//		}
	//	}
	//	private static void setCities(Builder sdpb) {
	//		//Cities
	//		Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
	//		for (Integer cityId : cities.keySet()) {
	//			City city = cities.get(cityId);
	//			sdpb.addAllCities(createInfoProtoUtils.createFullCityProtoFromCity(city));
	//		}
	//	}

	private void setTasks(Builder sdpb) {
		//Tasks
		Map<Integer, Task> taskIdsToTasks = taskRetrieveUtils
				.getTaskIdsToTasks();
		for (Task aTask : taskIdsToTasks.values()) {
			FullTaskProto ftp = createInfoProtoUtils
					.createFullTaskProtoFromTask(aTask);
			sdpb.addAllTasks(ftp);
		}

		Map<Integer, TaskMapElement> idsToMapElement = taskMapElementRetrieveUtils
				.getTaskMapElement();
		for (TaskMapElement tme : idsToMapElement.values()) {
			TaskMapElementProto tmep = createInfoProtoUtils
					.createTaskMapElementProto(tme);
			sdpb.addAllTaskMapElements(tmep);
		}

	}

	//TODO: FIGURE OUT MORE EFFICIENT WAY TO DO THIS IF NEEDED
	//ONE WAY WOULD BE TO STORE THE MAP OF MONSTER LEVEL INFO DIRECTLY IN THE MONSTER
	private void setMonsters(Builder sdpb) {
		//Monsters
		Map<Integer, Monster> monsters = monsterRetrieveUtils
				.getMonsterIdsToMonsters();
		for (Monster monster : monsters.values()) {

			//get the level info for this monster
			int monsterId = monster.getId();
			Map<Integer, MonsterLevelInfo> monsterLvlInfo = monsterLevelInfoRetrieveUtils
					.getMonsterLevelInfoForMonsterId(monsterId);

			sdpb.addAllMonsters(createInfoProtoUtils.createMonsterProto(
					monster, monsterLvlInfo));
		}
	}

	private void setUserLevelStuff(Builder sdpb) {
		//User level stuff
		Map<Integer, StaticUserLevelInfo> levelToStaticUserLevelInfo = staticUserLevelInfoRetrieveUtils
				.getAllStaticUserLevelInfo();
		for (int lvl : levelToStaticUserLevelInfo.keySet()) {
			StaticUserLevelInfo sli = levelToStaticUserLevelInfo.get(lvl);
			int exp = sli.getRequiredExp();

			StaticUserLevelInfoProto.Builder slipb = StaticUserLevelInfoProto
					.newBuilder();
			slipb.setLevel(lvl);
			slipb.setRequiredExperience(exp);
			sdpb.addSlip(slipb.build());
		}
	}

	private void setBoosterPackStuff(Builder sdpb) {
		//Booster pack stuff
		Map<Integer, BoosterPack> idsToBoosterPacks = boosterPackRetrieveUtils
				.getBoosterPackIdsToBoosterPacks();
		Map<Integer, Map<Integer, BoosterItem>> packIdToItemIdsToItems = boosterItemRetrieveUtils
				.getBoosterItemIdsToBoosterItemsForBoosterPackIds();
		Map<Integer, Map<Integer, BoosterDisplayItem>> packIdToDisplayIdsToDisplayItems = boosterDisplayItemRetrieveUtils
				.getBoosterDisplayItemIdsToBoosterDisplayItemsForBoosterPackIds();

		int starterPackId = ControllerConstants.IN_APP_PURCHASE__STARTER_PACK_BOOSTER_PACK_ID;

		for (Integer bpackId : idsToBoosterPacks.keySet()) {
			BoosterPack bp = idsToBoosterPacks.get(bpackId);

			//get the booster items associated with this booster pack
			Map<Integer, BoosterItem> itemIdsToItems = packIdToItemIdsToItems
					.get(bpackId);
			Collection<BoosterItem> items = null;
			if (null != itemIdsToItems) {
				items = itemIdsToItems.values();
			}

			//get the booster display items for this booster pack
			Map<Integer, BoosterDisplayItem> displayIdsToDisplayItems = packIdToDisplayIdsToDisplayItems
					.get(bpackId);
			Collection<BoosterDisplayItem> displayItems = null;
			if (null != displayIdsToDisplayItems) {
				ArrayList<Integer> displayItemIds = new ArrayList<Integer>();
				displayItemIds.addAll(displayIdsToDisplayItems.keySet());
				Collections.sort(displayItemIds);

				displayItems = new ArrayList<BoosterDisplayItem>();

				for (Integer displayItemId : displayItemIds) {
					displayItems.add(displayIdsToDisplayItems
							.get(displayItemId));
				}
			}

			BoosterPackProto bpProto = createInfoProtoUtils
					.createBoosterPackProto(bp, items, displayItems, rewardRetrieveUtils);
			//do not put the starterPack into the BoosterPacks
			if (bpackId == starterPackId) {
				sdpb.setStarterPack(bpProto);

			} else if (bp.isDisplayToUser()) {
				sdpb.addBoosterPacks(bpProto);
			}

		}

	}

	private void setStructures(Builder sdpb) {
		//Structures
		Map<Integer, Structure> structs = structureRetrieveUtils
				.getStructIdsToStructs();
		Map<Integer, StructureInfoProto> structProtos = new HashMap<Integer, StructureInfoProto>();
		for (Integer structId : structs.keySet()) {
			Structure struct = structs.get(structId);
			StructureInfoProto sip = createInfoProtoUtils
					.createStructureInfoProtoFromStructure(struct);
			structProtos.put(structId, sip);
		}

		setBattleItemFactories(sdpb, structs, structProtos);
		setGenerators(sdpb, structs, structProtos);
		setStorages(sdpb, structs, structProtos);
		setHospitals(sdpb, structs, structProtos);
		setResidences(sdpb, structs, structProtos);
		setTownHalls(sdpb, structs, structProtos);
		setLabs(sdpb, structs, structProtos);
		setMiniJobCenters(sdpb, structs, structProtos);
		setEvoChambers(sdpb, structs, structProtos);
		setTeamCenters(sdpb, structs, structProtos);
		setClanHouses(sdpb, structs, structProtos);
		setMoneyTrees(sdpb, structs, structProtos);
		setPvpBoardHouses(sdpb, structs, structProtos);
		setPvpBoardObstacles(sdpb);
		setResearchHouses(sdpb, structs, structProtos);
	}

	//battle item factory
	private void setBattleItemFactories(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureBattleItemFactory> idsToFactories = structureBattleItemFactoryRetrieveUtils
				.getStructIdsToBattleItemFactorys();
		for (Integer structId : idsToFactories.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureBattleItemFactory sbif = idsToFactories.get(structId);

			BattleItemFactoryProto bifp = createInfoProtoUtils
					.createBattleItemFactoryProto(s, sip, sbif);
			sdpb.addAllBattleItemFactorys(bifp);
		}
	}

	//resource generator
	private void setGenerators(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResourceGenerator> idsToGenerators = structureResourceGeneratorRetrieveUtils
				.getStructIdsToResourceGenerators();
		for (Integer structId : idsToGenerators.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResourceGenerator srg = idsToGenerators.get(structId);

			ResourceGeneratorProto rgp = createInfoProtoUtils
					.createResourceGeneratorProto(s, sip, srg);
			sdpb.addAllGenerators(rgp);
		}
	}

	//resource storage
	private void setStorages(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResourceStorage> idsToStorages = structureResourceStorageRetrieveUtils
				.getStructIdsToResourceStorages();
		for (Integer structId : idsToStorages.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResourceStorage srg = idsToStorages.get(structId);

			ResourceStorageProto rgp = createInfoProtoUtils
					.createResourceStorageProto(s, sip, srg);
			sdpb.addAllStorages(rgp);
		}
	}

	//hospitals
	private void setHospitals(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureHospital> idsToHospitals = structureHospitalRetrieveUtils
				.getStructIdsToHospitals();
		for (Integer structId : idsToHospitals.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureHospital srg = idsToHospitals.get(structId);

			HospitalProto rgp = createInfoProtoUtils.createHospitalProto(s,
					sip, srg);
			sdpb.addAllHospitals(rgp);
		}
	}

	//residences
	private void setResidences(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureResidence> idsToResidences = structureResidenceRetrieveUtils
				.getStructIdsToResidences();
		for (Integer structId : idsToResidences.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResidence srg = idsToResidences.get(structId);

			ResidenceProto rgp = createInfoProtoUtils.createResidenceProto(s,
					sip, srg);
			sdpb.addAllResidences(rgp);
		}
	}

	//town hall
	private void setTownHalls(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureTownHall> idsToTownHalls = structureTownHallRetrieveUtils
				.getStructIdsToTownHalls();
		for (Integer structId : idsToTownHalls.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureTownHall srg = idsToTownHalls.get(structId);

			TownHallProto rgp = createInfoProtoUtils.createTownHallProto(s,
					sip, srg);
			sdpb.addAllTownHalls(rgp);
		}
	}

	//lab
	private void setLabs(Builder sdpb, Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureLab> idsToLabs = structureLabRetrieveUtils
				.getStructIdsToLabs();
		for (Integer structId : idsToLabs.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureLab srg = idsToLabs.get(structId);

			LabProto rgp = createInfoProtoUtils.createLabProto(s, sip, srg);
			sdpb.addAllLabs(rgp);
		}
	}

	//mini job center
	private void setMiniJobCenters(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureMiniJob> idsToMiniJobs = structureMiniJobRetrieveUtils
				.getStructIdsToMiniJobs();

		Map<Integer, Map<Integer, MiniJobRefreshItem>> structIdToIdToMjri =
				miniJobRefreshItemRetrieveUtils.getMiniJobRefreshItem();

		for (Integer structId : idsToMiniJobs.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureMiniJob smj = idsToMiniJobs.get(structId);

			Map<Integer, MiniJobRefreshItem> idToMjriMap = null;
			if (structIdToIdToMjri.containsKey(structId))
			{
				idToMjriMap = structIdToIdToMjri.get(structId);
			}

			MiniJobCenterProto mjcp = createInfoProtoUtils
					.createMiniJobCenterProto(s, sip, smj, idToMjriMap);
			sdpb.addAllMiniJobCenters(mjcp);
		}
	}

	private void setEvoChambers(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureEvoChamber> idsToEvoChambers = structureEvoChamberRetrieveUtils
				.getStructIdsToEvoChambers();

		for (Integer structId : idsToEvoChambers.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureEvoChamber sec = idsToEvoChambers.get(structId);

			EvoChamberProto ecp = createInfoProtoUtils.createEvoChamberProto(s,
					sip, sec);
			sdpb.addAllEvoChambers(ecp);
		}
	}

	private void setTeamCenters(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureTeamCenter> idsToTeamCenters = structureTeamCenterRetrieveUtils
				.getStructIdsToTeamCenters();

		for (Integer structId : idsToTeamCenters.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureTeamCenter stc = idsToTeamCenters.get(structId);

			TeamCenterProto tcp = createInfoProtoUtils.createTeamCenterProto(s,
					sip, stc);
			sdpb.addAllTeamCenters(tcp);
		}
	}

	private void setClanHouses(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureClanHouse> idsToClanHouses = structureClanHouseRetrieveUtils
				.getStructIdsToClanHouses();

		for (Integer structId : idsToClanHouses.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureClanHouse stc = idsToClanHouses.get(structId);

			ClanHouseProto tcp = createInfoProtoUtils.createClanHouseProto(s,
					sip, stc);
			sdpb.addAllClanHouses(tcp);
		}
	}

	private void setMoneyTrees(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {

		Map<Integer, StructureMoneyTree> idsToMoneyTrees = structureMoneyTreeRetrieveUtils
				.getStructIdsToMoneyTrees();

		for (Integer structId : idsToMoneyTrees.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureMoneyTree smt = idsToMoneyTrees.get(structId);

			MoneyTreeProto mtp = createInfoProtoUtils
					.createMoneyTreeProtoFromStructureMoneyTree(s, sip, smt);
			sdpb.addAllMoneyTrees(mtp);
		}
	}

	private void setPvpBoardHouses(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {

		Map<Integer, StructurePvpBoard> idsToPvpBoards = structurePvpBoardRetrieveUtils
				.getStructIdsToPvpBoards();

		for (Integer structId : idsToPvpBoards.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructurePvpBoard smt = idsToPvpBoards.get(structId);

			PvpBoardHouseProto pbhp = createInfoProtoUtils
					.createPvpBoardHouseProto(s, sip, smt);
			sdpb.addAllPvpBoardHouses(pbhp);
		}
	}

	private void setPvpBoardObstacles(Builder sdpb) {
		Map<Integer, BoardObstacle> idToBoardObstacle = boardObstacleRetrieveUtils
				.getIdsToBoardObstacles();

		for (BoardObstacle bo : idToBoardObstacle.values()) {
			PvpBoardObstacleProto pbop = createInfoProtoUtils
					.createPvpBoardObstacleProto(bo);
			sdpb.addPvpBoardObstacleProtos(pbop);
		}
	}

	private void setResearchHouses(Builder sdpb,
			Map<Integer, Structure> structs,
			Map<Integer, StructureInfoProto> structProtos) {

		Map<Integer, StructureResearchHouse> idsToResearchHouse = structureResearchHouseRetrieveUtils
				.getStructIdsToResearchHouses();

		for (Integer structId : idsToResearchHouse.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureResearchHouse srh = idsToResearchHouse.get(structId);

			ResearchHouseProto rhp = createInfoProtoUtils
					.createResearchHouseProtoFromStructureResearchHouse(s, sip,
							srh);
			sdpb.addAllResearchHouses(rhp);
		}
	}

	private void setEvents(Builder sdpb) {
		Map<Integer, EventPersistent> idsToEvents = eventPersistentRetrieveUtils
				.getAllEventIdsToEvents();
		for (Integer eventId : idsToEvents.keySet()) {
			EventPersistent event = idsToEvents.get(eventId);
			PersistentEventProto eventProto = createInfoProtoUtils
					.createPersistentEventProtoFromEvent(event);
			sdpb.addPersistentEvents(eventProto);
		}
	}

	private void setMonsterDialogue(Builder sdpb) {
		Map<Integer, List<MonsterBattleDialogue>> monsterIdToDialogue = monsterBattleDialogueRetrieveUtils
				.getMonsterIdToBattleDialogue();

		List<MonsterBattleDialogueProto> dialogueList = new ArrayList<MonsterBattleDialogueProto>();
		for (List<MonsterBattleDialogue> dialogue : monsterIdToDialogue
				.values()) {

			for (MonsterBattleDialogue mbd : dialogue) {
				MonsterBattleDialogueProto dialogueProto = createInfoProtoUtils
						.createMonsterBattleDialogueProto(mbd);
				dialogueList.add(dialogueProto);
			}
		}

		sdpb.addAllMbds(dialogueList);
	}

	private void setClanRaidStuff(Builder sdpb) {
		Map<Integer, ClanRaid> idsToClanRaid = clanRaidRetrieveUtils
				.getClanRaidIdsToClanRaids();
		List<ClanRaidProto> raidList = new ArrayList<ClanRaidProto>();
		for (ClanRaid cr : idsToClanRaid.values()) {
			ClanRaidProto crProto = createInfoProtoUtils
					.createClanRaidProto(cr);
			raidList.add(crProto);
		}
		sdpb.addAllRaids(raidList);

		//protofy clan events
		List<PersistentClanEventProto> clanEventProtos = new ArrayList<PersistentClanEventProto>();
		Map<Integer, ClanEventPersistent> idsToClanEventPersistent = clanEventPersistentRetrieveUtils
				.getAllEventIdsToEvents();
		for (ClanEventPersistent cep : idsToClanEventPersistent.values()) {
			PersistentClanEventProto pcep = createInfoProtoUtils
					.createPersistentClanEventProto(cep);
			clanEventProtos.add(pcep);
		}
		sdpb.addAllPersistentClanEvents(clanEventProtos);
	}

	private void setItemStuff(Builder sdpb) {
		Map<Integer, Item> itemIdsToItems = itemRetrieveUtils
				.getItemIdsToItems();

//		log.info("itemIdsToItems={}", itemIdsToItems);
		if (null == itemIdsToItems || itemIdsToItems.isEmpty()) {
			log.warn("no items");
			return;
		}

		for (Item i : itemIdsToItems.values()) {
			ItemProto itemProto = createInfoProtoUtils
					.createItemProtoFromItem(i);
			sdpb.addItems(itemProto);
		}
//		log.info("itemProtos={}", sdpb.getItemsList());

	}

	private void setObstacleStuff(Builder sdpb) {
		Map<Integer, Obstacle> idsToObstacles = obstacleRetrieveUtils
				.getObstacleIdsToObstacles();
		if (null == idsToObstacles || idsToObstacles.isEmpty()) {
			log.warn("no obstacles");
			return;
		}

		for (Obstacle o : idsToObstacles.values()) {
			ObstacleProto op = createInfoProtoUtils
					.createObstacleProtoFromObstacle(o);
			sdpb.addObstacles(op);
		}
	}

	private void setClanIconStuff(Builder sdpb) {
		Map<Integer, ClanIcon> clanIconIdsToClanIcons = clanIconRetrieveUtils
				.getClanIconIdsToClanIcons();

		if (null == clanIconIdsToClanIcons || clanIconIdsToClanIcons.isEmpty()) {
			log.warn("no clan_icons");
			return;
		}

		for (ClanIcon ci : clanIconIdsToClanIcons.values()) {
			ClanIconProto cip = createInfoProtoUtils
					.createClanIconProtoFromClanIcon(ci);
			sdpb.addClanIcons(cip);
		}
	}

	private void setPvpLeagueStuff(Builder sdpb) {
		Map<Integer, PvpLeague> idToPvpLeague = pvpLeagueRetrieveUtils
				.getPvpLeagueIdsToPvpLeagues();

		if (null == idToPvpLeague || idToPvpLeague.isEmpty()) {
			log.warn("no pvp_leagues");
			return;
		}

		for (PvpLeague pl : idToPvpLeague.values()) {
			PvpLeagueProto plp = createInfoProtoUtils.createPvpLeagueProto(pl);
			sdpb.addLeagues(plp);
		}
	}

	private void setAchievementStuff(Builder sdpb) {
		Map<Integer, Achievement> achievementIdsToAchievements = achievementRetrieveUtils
				.getAchievementIdsToAchievements();
		if (null == achievementIdsToAchievements
				|| achievementIdsToAchievements.isEmpty()) {
			log.warn("setAchievementStuff() no achievements");
			return;
		}
		for (Achievement a : achievementIdsToAchievements.values()) {
			AchievementProto ap = createInfoProtoUtils
					.createAchievementProto(a);
			sdpb.addAchievements(ap);
		}
	}

	private void setSkillStuff(Builder sdpb) {
		Map<Integer, Skill> skillz = skillRetrieveUtils.getIdsToSkills();
		Map<Integer, Map<Integer, SkillProperty>> skillPropertyz = skillPropertyRetrieveUtils
				.getSkillIdsToIdsToSkillPropertys();

		if (null == skillz || skillz.isEmpty()) {
			log.warn("setSkillStuff() no skillz");
		} else {
			//get id and then manually get Skill
			//could also get Skill, but then manually get id
			for (Integer skillId : skillz.keySet()) {
				Skill skil = skillz.get(skillId);

				//skill can have no properties
				Map<Integer, SkillProperty> propertyz = null;
				if (skillPropertyz.containsKey(skillId)) {
					propertyz = skillPropertyz.get(skillId);
				}

				SkillProto sp = createInfoProtoUtils.createSkillProtoFromSkill(
						skil, propertyz);
				sdpb.addSkills(sp);
			}
		}

		Map<Integer, SkillSideEffect> skillSideEffects = skillSideEffectRetrieveUtils
				.getIdsToSkillSideEffects();

		if (null == skillSideEffects || skillSideEffects.isEmpty()) {
			log.warn("setSkillStuff() no skillSideEffects");
		} else {
			for (Integer id : skillSideEffects.keySet()) {
				SkillSideEffect sse = skillSideEffects.get(id);
				SkillSideEffectProto ssep = createInfoProtoUtils
						.createSkillSideEffectProto(sse);
				sdpb.addSideEffects(ssep);
			}
		}

	}

	private void setPrereqs(Builder sdpb) {
		Map<Integer, Prerequisite> idsToPrereqs = prerequisiteRetrieveUtils
				.getPrerequisiteIdsToPrerequisites();

		if (null == idsToPrereqs || idsToPrereqs.isEmpty()) {
			log.warn("setPrereqs() no prerequisites");
			return;
		}

		for (Integer prereqId : idsToPrereqs.keySet()) {
			Prerequisite prereq = idsToPrereqs.get(prereqId);

			PrereqProto pp = createInfoProtoUtils
					.createPrerequisiteProto(prereq);
			sdpb.addPrereqs(pp);
		}
	}

	private void setBoards(Builder sdpb) {
		Map<Integer, Board> idsToBoards = boardRetrieveUtils.getIdsToBoards();

		if (null == idsToBoards || idsToBoards.isEmpty()) {
			log.warn("setBoards() no boards");
		}

		Map<Integer, Collection<BoardProperty>> boardIdsToProperties = boardPropertyRetrieveUtils
				.getBoardIdsToProperties();

		for (Integer boardId : idsToBoards.keySet()) {
			Board b = idsToBoards.get(boardId);

			//board can have no properties
			Collection<BoardProperty> propertyz = null;
			if (boardIdsToProperties.containsKey(boardId)) {
				propertyz = boardIdsToProperties.get(boardId);
			}

			BoardLayoutProto blp = createInfoProtoUtils.createBoardLayoutProto(
					b, propertyz);
			sdpb.addBoards(blp);
		}
	}

	private void setResearch(Builder sdpb) {
		Map<Integer, Research> idsToResearch = researchRetrieveUtils
				.getIdsToResearch();

		if (null == idsToResearch || idsToResearch.isEmpty()) {
			log.warn("setResearch() no research");
		}

		for (Integer researchId : idsToResearch.keySet()) {
			Research r = idsToResearch.get(researchId);

			//research can have no properties
			Map<Integer, ResearchProperty> properties = researchPropertyRetrieveUtils
					.getResearchPropertiesForResearchId(researchId);

			Collection<ResearchProperty> propertyz = null;

			if (null != properties) {
				propertyz = properties.values();
			}

			ResearchProto rlp = createInfoProtoUtils.createResearchProto(r,
					propertyz);
			sdpb.addResearch(rlp);
		}
	}

	private void setBattleItem(Builder sdpb) {
		Map<Integer, BattleItem> idsToBattleItem = battleItemRetrieveUtils
				.getBattleItemIdsToBattleItems();

		if (null == idsToBattleItem || idsToBattleItem.isEmpty()) {
			log.warn("setBattleItem() no battle item");
			return;
		}

		for (Integer battleItemId : idsToBattleItem.keySet()) {
			BattleItem bi = idsToBattleItem.get(battleItemId);

			BattleItemProto bip = createInfoProtoUtils
					.createBattleItemProtoFromBattleItem(bi);

			sdpb.addBattleItem(bip);
		}
	}

//	private void setSales(Builder sdpb) {
//		Map<Integer, SalesPackage> idsToSalesPackages = salesPackageRetrieveUtils
//				.getSalesPackageIdsToSalesPackages();
//		Map<Integer, List<SalesItem>> salesPackageIdToItemIdsToSalesItems = salesItemRetrieveUtils
//				.getSalesItemIdsToSalesItemsForSalesPackIds();
//		Map<Integer, Map<Integer, SalesDisplayItem>> salesPackageIdToDisplayIdsToDisplayItems =
//				salesDisplayItemRetrieveUtils.
//				getSalesDisplayItemIdsToSalesDisplayItemsForSalesPackIds();
//
//		for (Integer salesPackageId : idsToSalesPackages.keySet()) {
//			SalesPackage sp = idsToSalesPackages.get(salesPackageId);
//
//			//get the sales items associated with this booster pack
//			List<SalesItem> salesItemList = salesPackageIdToItemIdsToSalesItems
//					.get(salesPackageId);
//
//			//get the booster display items for this booster pack
//			Map<Integer, SalesDisplayItem> displayIdsToDisplayItems =
//					salesPackageIdToDisplayIdsToDisplayItems.get(salesPackageId);
//			Collection<SalesDisplayItem> displayItems = null;
//			if (null != displayIdsToDisplayItems) {
//				ArrayList<Integer> displayItemIds = new ArrayList<Integer>();
//				displayItemIds.addAll(displayIdsToDisplayItems.keySet());
//				Collections.sort(displayItemIds);
//
//				displayItems = new ArrayList<SalesDisplayItem>();
//
//				for (Integer displayItemId : displayItemIds) {
//					displayItems.add(displayIdsToDisplayItems
//							.get(displayItemId));
//				}
//			}
//
//			SalesPackageProto spProto = createInfoProtoUtils
//					.createSalesPackageProto(sp, salesItemList, displayItems);
//		}
//	}

/*
	private void setRewards(Builder sdpb) {
		Map<Integer, Reward> idsToReward = rewardRetrieveUtils
				.getRewardIdsToRewards();

		if (null == idsToReward || idsToReward.isEmpty()) {
			log.warn("setReward() no Reward");
			return;
		}

		for (Integer battleItemId : idsToReward.keySet()) {
			Reward r = idsToReward.get(battleItemId);

			RewardProto rp = createInfoProtoUtils
					.createRewardProto(r);

			sdpb.addReward(rp);
		}
	}
*/

	private void setGifts(Builder sdpb) {
		Map<Integer, GiftConfigPojo> idsToGift = giftRetrieveUtil
				.getIdsToGifts();

		if (null == idsToGift || idsToGift.isEmpty()) {
			log.warn("setGift() no Gifts");
			return;
		}

		for (Integer id : idsToGift.keySet()) {
			GiftConfigPojo r = idsToGift.get(id);

			GiftProto rp = createInfoProtoUtils
					.createGiftProto(r);

			sdpb.addGifts(rp);
		}
	}

	private void setRefreshMiniJobItemPrices(Builder sdpb) {
		List<MiniJobRefreshItemConfigPojo> mjricList = miniJobRefreshDao.findAll();
		sdpb.addAllStructureItemPrices(createInfoProtoUtils.
				createItemGemPriceProtoFromMiniJobs(mjricList));
	}

}
