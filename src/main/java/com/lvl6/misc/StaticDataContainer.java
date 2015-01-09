package com.lvl6.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.Achievement;
import com.lvl6.info.Board;
import com.lvl6.info.BoardProperty;
import com.lvl6.info.BoosterDisplayItem;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.BoosterPack;
import com.lvl6.info.ClanEventPersistent;
import com.lvl6.info.ClanIcon;
import com.lvl6.info.ClanRaid;
import com.lvl6.info.EventPersistent;
import com.lvl6.info.Item;
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
import com.lvl6.info.StaticUserLevelInfo;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureClanHouse;
import com.lvl6.info.StructureEvoChamber;
import com.lvl6.info.StructureHospital;
import com.lvl6.info.StructureLab;
import com.lvl6.info.StructureMiniJob;
import com.lvl6.info.StructureResidence;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.StructureResourceStorage;
import com.lvl6.info.StructureTeamCenter;
import com.lvl6.info.StructureTownHall;
import com.lvl6.info.Task;
import com.lvl6.info.TaskMapElement;
import com.lvl6.proto.AchievementStuffProto.AchievementProto;
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
import com.lvl6.proto.SkillsProto.SkillProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto.Builder;
import com.lvl6.proto.StructureProto.ClanHouseProto;
import com.lvl6.proto.StructureProto.EvoChamberProto;
import com.lvl6.proto.StructureProto.HospitalProto;
import com.lvl6.proto.StructureProto.LabProto;
import com.lvl6.proto.StructureProto.MiniJobCenterProto;
import com.lvl6.proto.StructureProto.ObstacleProto;
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
import com.lvl6.retrieveutils.rarechange.BoardPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoardRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterDisplayItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.BoosterPackRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanEventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanIconRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ClanRaidRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.EventPersistentRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ItemRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterBattleDialogueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ObstacleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PrerequisiteRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.PvpLeagueRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SkillPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.SkillRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StaticUserLevelInfoRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureClanHouseRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureEvoChamberRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureHospitalRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureLabRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureMiniJobRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResidenceRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureResourceStorageRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureTeamCenterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StructureTownHallRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskMapElementRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.TaskRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

//goal is to not compute proto analog of static data
//and to provide a consistent view of static data.
//Data is refreshed when MiscMethods.reloadAllRareChangeStaticData()
//	is called, which refreshes all data.
//This class will hold the previous snapshot of all static data until
//	the data is refreshed, after which this class will hold the current
//	snapshot of all static data.
@Component
public class StaticDataContainer
{

	private static Logger log = LoggerFactory.getLogger(new Object() {}.getClass()
		.getEnclosingClass());

	private static StaticDataProto.Builder staticDataBuilder = null;
	
	public static StaticDataProto getStaticData() {
		if (null == staticDataBuilder) {
			setStaticData();
		}
		
		if (null == staticDataBuilder) {
			log.error("CANNOT SET UP STATIC DATA!!!!");
			return null;
		}
		return staticDataBuilder.build();
	}
	
	public static void reload() {
		setStaticData();
	}
	
	private static void setStaticData()
	{
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
		
		staticDataBuilder = sdpb;
	}
	

//	private static void setPlayerCityExpansions(Builder sdpb) {
//		//Player city expansions
//		Map<Integer, ExpansionCost> expansionCosts =
//			ExpansionCostRetrieveUtils.getAllExpansionNumsToCosts();
//		for (ExpansionCost cec : expansionCosts.values()) {
//			CityExpansionCostProto cecp = CreateInfoProtoUtils
//				.createCityExpansionCostProtoFromCityExpansionCost(cec);
//			sdpb.addExpansionCosts(cecp);
//		}
//	}
//	private static void setCities(Builder sdpb) {
//		//Cities
//		Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
//		for (Integer cityId : cities.keySet()) {
//			City city = cities.get(cityId);
//			sdpb.addAllCities(CreateInfoProtoUtils.createFullCityProtoFromCity(city));
//		}
//	}
	
	private static void setTasks(Builder sdpb) {
		//Tasks
		Map<Integer, Task> taskIdsToTasks = TaskRetrieveUtils.getTaskIdsToTasks();
		for (Task aTask : taskIdsToTasks.values()) {
			FullTaskProto ftp = CreateInfoProtoUtils.createFullTaskProtoFromTask(aTask);
			sdpb.addAllTasks(ftp);
		}

		Map<Integer, TaskMapElement> idsToMapElement = TaskMapElementRetrieveUtils.getTaskMapElement();
		for (TaskMapElement tme : idsToMapElement.values()) {
			TaskMapElementProto tmep = CreateInfoProtoUtils.createTaskMapElementProto(tme);
			sdpb.addAllTaskMapElements(tmep);
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
	private static void setUserLevelStuff(Builder sdpb) {
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
		setMiniJobCenters(sdpb, structs, structProtos);
		setEvoChambers(sdpb, structs, structProtos);
		setTeamCenters(sdpb, structs, structProtos);
		setClanHouses(sdpb, structs, structProtos);
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
	//mini job center
	private static void setMiniJobCenters(Builder sdpb,
		Map<Integer, Structure> structs,
		Map<Integer, StructureInfoProto> structProtos) {
		Map<Integer, StructureMiniJob> idsToMiniJobs =
			StructureMiniJobRetrieveUtils.getStructIdsToMiniJobs();
		for (Integer structId : idsToMiniJobs.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureMiniJob smj = idsToMiniJobs.get(structId);

			MiniJobCenterProto mjcp = CreateInfoProtoUtils
				.createMiniJobCenterProto(s, sip, smj);
			sdpb.addAllMiniJobCenters(mjcp);
		}
	}

	private static void setEvoChambers(Builder sdpb,
		Map<Integer, Structure> structs,
		Map<Integer, StructureInfoProto> structProtos)
	{
		Map<Integer, StructureEvoChamber> idsToEvoChambers =
			StructureEvoChamberRetrieveUtils.getStructIdsToEvoChambers();

		for (Integer structId : idsToEvoChambers.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureEvoChamber sec = idsToEvoChambers.get(structId);

			EvoChamberProto ecp = CreateInfoProtoUtils
				.createEvoChamberProto(s, sip, sec);
			sdpb.addAllEvoChambers(ecp);
		}
	}

	private static void setTeamCenters(Builder sdpb,
		Map<Integer, Structure> structs,
		Map<Integer, StructureInfoProto> structProtos)
	{
		Map<Integer, StructureTeamCenter> idsToTeamCenters =
			StructureTeamCenterRetrieveUtils.getStructIdsToTeamCenters();

		for (Integer structId : idsToTeamCenters.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureTeamCenter stc = idsToTeamCenters.get(structId);

			TeamCenterProto tcp = CreateInfoProtoUtils
				.createTeamCenterProto(s, sip, stc);
			sdpb.addAllTeamCenters(tcp);
		}
	}

	private static void setClanHouses(Builder sdpb,
		Map<Integer, Structure> structs,
		Map<Integer, StructureInfoProto> structProtos)
	{
		Map<Integer, StructureClanHouse> idsToClanHouses =
			StructureClanHouseRetrieveUtils.getStructIdsToClanHouses();

		for (Integer structId : idsToClanHouses.keySet()) {
			Structure s = structs.get(structId);
			StructureInfoProto sip = structProtos.get(structId);
			StructureClanHouse stc = idsToClanHouses.get(structId);

			ClanHouseProto tcp = CreateInfoProtoUtils
				.createClanHouseProto(s, sip, stc);
			sdpb.addAllClanHouses(tcp);
		}
	}

	private static void setEvents(Builder sdpb) {
		Map<Integer, EventPersistent> idsToEvents = EventPersistentRetrieveUtils
			.getAllEventIdsToEvents();
		for (Integer eventId: idsToEvents.keySet()) {
			EventPersistent event  = idsToEvents.get(eventId);
			PersistentEventProto eventProto = CreateInfoProtoUtils
				.createPersistentEventProtoFromEvent(event);
			sdpb.addPersistentEvents(eventProto);
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

	private static void setClanRaidStuff(Builder sdpb) {
		Map<Integer, ClanRaid> idsToClanRaid = ClanRaidRetrieveUtils.getClanRaidIdsToClanRaids();
		List<ClanRaidProto> raidList = new ArrayList<ClanRaidProto>();
		for (ClanRaid cr : idsToClanRaid.values()) {
			ClanRaidProto crProto = CreateInfoProtoUtils.createClanRaidProto(cr);
			raidList.add(crProto);
		}
		sdpb.addAllRaids(raidList);


		//protofy clan events
		List<PersistentClanEventProto> clanEventProtos = new ArrayList<PersistentClanEventProto>();
		Map<Integer, ClanEventPersistent> idsToClanEventPersistent =
			ClanEventPersistentRetrieveUtils .getAllEventIdsToEvents();
		for (ClanEventPersistent cep : idsToClanEventPersistent.values()) {
			PersistentClanEventProto pcep = CreateInfoProtoUtils.createPersistentClanEventProto(cep);
			clanEventProtos.add(pcep);
		}
		sdpb.addAllPersistentClanEvents(clanEventProtos);
	}

	private static void setItemStuff(Builder sdpb) {
		Map<Integer, Item> itemIdsToItems = ItemRetrieveUtils.getItemIdsToItems();

		if (null == itemIdsToItems || itemIdsToItems.isEmpty()) {
			log.warn("no items");
			return;
		}

		for (Item i : itemIdsToItems.values()) {
			ItemProto itemProto = CreateInfoProtoUtils.createItemProtoFromItem(i);
			sdpb.addItems(itemProto);
		}

	}

	private static void setObstacleStuff(Builder sdpb) {
		Map<Integer, Obstacle> idsToObstacles = ObstacleRetrieveUtils
			.getObstacleIdsToObstacles();
		if (null == idsToObstacles || idsToObstacles.isEmpty()) {
			log.warn("no obstacles");
			return;
		}

		for (Obstacle o : idsToObstacles.values()) {
			ObstacleProto op = CreateInfoProtoUtils.createObstacleProtoFromObstacle(o);
			sdpb.addObstacles(op);
		}
	}

	private static void setClanIconStuff(Builder sdpb) {
		Map<Integer, ClanIcon> clanIconIdsToClanIcons = ClanIconRetrieveUtils.getClanIconIdsToClanIcons();

		if (null == clanIconIdsToClanIcons || clanIconIdsToClanIcons.isEmpty()) {
			log.warn("no clan_icons");
			return;
		}

		for (ClanIcon ci : clanIconIdsToClanIcons.values()) {
			ClanIconProto cip = CreateInfoProtoUtils.createClanIconProtoFromClanIcon(ci);
			sdpb.addClanIcons(cip);
		}
	}

	private static void setPvpLeagueStuff(Builder sdpb) {
		Map<Integer, PvpLeague> idToPvpLeague = PvpLeagueRetrieveUtils
			.getPvpLeagueIdsToPvpLeagues();

		if (null == idToPvpLeague || idToPvpLeague.isEmpty()) {
			log.warn("no pvp_leagues");
			return;
		}

		for (PvpLeague pl : idToPvpLeague.values()) {
			PvpLeagueProto plp = CreateInfoProtoUtils.createPvpLeagueProto(pl);
			sdpb.addLeagues(plp);
		}
	}

	private static void setAchievementStuff(Builder sdpb) {
		Map<Integer, Achievement> achievementIdsToAchievements =
			AchievementRetrieveUtils.getAchievementIdsToAchievements();
		if (null == achievementIdsToAchievements ||
			achievementIdsToAchievements.isEmpty()) {
			log.warn("setAchievementStuff() no achievements");
			return;
		}
		for (Achievement a : achievementIdsToAchievements.values()) {
			AchievementProto ap = CreateInfoProtoUtils.createAchievementProto(a);
			sdpb.addAchievements(ap);
		}
	}

	private static void setSkillStuff(Builder sdpb) {
		Map<Integer, Skill> skillz =
			SkillRetrieveUtils.getIdsToSkills();
		Map<Integer, Map<Integer, SkillProperty>> skillPropertyz =
			SkillPropertyRetrieveUtils.getSkillIdsToIdsToSkillPropertys();

		if (null == skillz || skillz.isEmpty()) {
			log.warn("setSkillStuff() no skillz");
			return;
		}

		//get id and then manually get Skill
		//could also get Skill, but then manually get id
		for (Integer skillId : skillz.keySet())
		{
			Skill skil = skillz.get(skillId);

			//skill can have no properties
			Map<Integer, SkillProperty> propertyz = null;
			if (skillPropertyz.containsKey(skillId)) {
				propertyz = skillPropertyz.get(skillId);
			}

			SkillProto sp = CreateInfoProtoUtils.createSkillProtoFromSkill(skil, propertyz);
			sdpb.addSkills(sp);
		}

	}

	private static void setPrereqs(Builder sdpb) {
		Map<Integer, Prerequisite> idsToPrereqs = 
			PrerequisiteRetrieveUtils.getPrerequisiteIdsToPrerequisites();

		if (null == idsToPrereqs || idsToPrereqs.isEmpty()) {
			log.warn("setPrereqs() no prerequisites");
			return;
		}

		for (Integer prereqId : idsToPrereqs.keySet()) {
			Prerequisite prereq = idsToPrereqs.get(prereqId);

			PrereqProto pp = CreateInfoProtoUtils.createPrerequisiteProto(prereq);
			sdpb.addPrereqs(pp);
		}
	}
	
	private static void setBoards(Builder sdpb) {
		Map<Integer, Board> idsToBoards =
			BoardRetrieveUtils.getIdsToBoards();
		
		if (null == idsToBoards || idsToBoards.isEmpty()) {
			log.warn("setBoards() no boards");
		}
		
		Map<Integer, Collection<BoardProperty>> boardIdsToProperties =
			BoardPropertyRetrieveUtils.getBoardIdsToProperties();
		
		for (Integer boardId : idsToBoards.keySet())
		{
			Board b = idsToBoards.get(boardId);
			
			//board can have no properties
			Collection<BoardProperty> propertyz = null;
			if (boardIdsToProperties.containsKey(boardId)) {
				propertyz = boardIdsToProperties.get(boardId);
			}

			BoardLayoutProto blp = CreateInfoProtoUtils
				.createBoardLayoutProto(b, propertyz);
			sdpb.addBoards(blp);
		}
	}
	
	private static void setResearch(Builder sdpb) {
		Map<Integer, Research> idsToResearch =
			ResearchRetrieveUtils.getIdsToResearch();
		
		if (null == idsToResearch || idsToResearch.isEmpty()) {
			log.warn("setResearch() no research");
		}
		
		for (Integer researchId : idsToResearch.keySet())
		{
			Research r = idsToResearch.get(researchId);
			
			//research can have no properties
			Map<Integer, ResearchProperty> properties =
				ResearchPropertyRetrieveUtils.
				getResearchPropertiesForResearchId(researchId);
			
			Collection<ResearchProperty> propertyz = null;
			
			if (null != properties) {
				propertyz = properties.values();
			}

			ResearchProto rlp = CreateInfoProtoUtils
				.createResearchProto(r, propertyz);
			sdpb.addResearch(rlp);
		}
	}

}
