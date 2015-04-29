package com.lvl6.misc;

import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.lvl6.events.response.GeneralNotificationResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.BoosterItem;
import com.lvl6.info.Clan;
import com.lvl6.info.Dialogue;
import com.lvl6.info.FileDownload;
import com.lvl6.info.MonsterForUser;
import com.lvl6.info.PvpLeagueForUser;
import com.lvl6.info.Quest;
import com.lvl6.info.QuestForUser;
import com.lvl6.info.User;
//import com.lvl6.leaderboards.LeaderBoardUtil;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.properties.Globals;
import com.lvl6.properties.IAPValues;
import com.lvl6.properties.MDCKeys;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.ClanConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.ClanHelpConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.DownloadableNibConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.MiniTutorialConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.MonsterConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.PvpConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.ResourceConversionConstantProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.SpeedUpConstantProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.TaskMapConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.StartupConstants.UserMonsterConstants;
import com.lvl6.proto.EventStartupProto.StartupResponseProto.TutorialConstants;
import com.lvl6.proto.EventUserProto.UpdateClientUserResponseProto;
import com.lvl6.proto.InAppPurchaseProto.InAppPurchasePackageProto;
import com.lvl6.proto.InAppPurchaseProto.InAppPurchasePackageProto.InAppPurchasePackageType;
import com.lvl6.proto.MonsterStuffProto.FullUserMonsterProto;
import com.lvl6.proto.QuestProto.FullQuestProto;
import com.lvl6.proto.SharedEnumConfigProto.GameActionType;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto;
import com.lvl6.proto.StaticDataStuffProto.StaticDataProto.Builder;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.StructureProto.TutorialStructProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils2;
import com.lvl6.retrieveutils.QuestForUserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.AchievementRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.FileDownloadRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.MonsterRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ProfanityRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.QuestRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.StartupStuffRetrieveUtils;
import com.lvl6.server.GameServer;
import com.lvl6.server.controller.utils.MonsterStuffUtils;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.QuestUtils;
import com.memetix.mst.detect.Detect;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

@Component
public class MiscMethods {

	@Autowired
	protected AchievementRetrieveUtils achievementRetrieveUtils;

	@Autowired
	protected CreateInfoProtoUtils createInfoProtoUtils;

	@Autowired
	protected FileDownloadRetrieveUtils fileDownloadRetrieveUtils;

	@Autowired
	protected MonsterRetrieveUtils monsterRetrieveUtils;

	@Autowired
	protected MonsterStuffUtils monsterStuffUtils;

	@Autowired
	protected ProfanityRetrieveUtils profanityRetrieveUtils;

	@Autowired
	protected QuestRetrieveUtils questRetrieveUtils;

	@Autowired
	protected QuestUtils questUtils;

	@Autowired
	protected StartupStuffRetrieveUtils startupStuffRetrieveUtils;

	@Autowired
	protected ServerToggleRetrieveUtils serverToggleRetrieveUtils;

	@Autowired
	protected StaticDataContainer staticDataContainer;

	private static final Logger log = LoggerFactory
			.getLogger(MiscMethods.class);
	public static final String cash = "cash";
	public static final String gems = "gems";
	public static final String oil = "oil";
	public static final String boosterPackId = "boosterPackId";

	private static String pClientId = "ToonSquad";
	private static String secretId = "bZ3WX/tZHV2KoljCFOwYOWRuR9WpSaa7O/L4oZuUhHo=";

	public static final String CASH = "CASH";
	public static final String OIL = "OIL";
	public static final String MONSTER = "MONSTER";

	//METHODS FOR CAPPING USER RESOURCE
	public int capResourceGain(int currentAmt, int delta, int maxAmt) {
		currentAmt = Math.min(currentAmt, maxAmt); //in case user resource is more than max
		int maxResourceUserCanGain = maxAmt - currentAmt;
		return Math.min(delta, maxResourceUserCanGain);
	}

	//METHODS FOR PICKING A BOOSTER PACK

	//no arguments are modified
	public List<BoosterItem> determineBoosterItemsUserReceives(
			int amountUserWantsToPurchase,
			Map<Integer, BoosterItem> boosterItemIdsToBoosterItemsForPackId) {
		//return value
		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();

		Collection<BoosterItem> items = boosterItemIdsToBoosterItemsForPackId
				.values();
		List<BoosterItem> itemsList = new ArrayList<BoosterItem>(items);
		float sumOfProbabilities = sumProbabilities(boosterItemIdsToBoosterItemsForPackId
				.values());

		//selecting items at random with replacement
		for (int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
			BoosterItem bi = selectBoosterItem(itemsList, sumOfProbabilities);
			if (null == bi) {
				continue;
			}
			itemsUserReceives.add(bi);
		}

		return itemsUserReceives;
	}

	private float sumProbabilities(Collection<BoosterItem> boosterItems) {
		float sumOfProbabilities = 0.0f;
		for (BoosterItem bi : boosterItems) {
			sumOfProbabilities += bi.getChanceToAppear();
		}
		return sumOfProbabilities;
	}

	private BoosterItem selectBoosterItem(List<BoosterItem> itemsList,
			float sumOfProbabilities) {
		Random rand = new Random();
		float unnormalizedProbabilitySoFar = 0f;
		float randFloat = rand.nextFloat();

		boolean logBoosterItemDetails = serverToggleRetrieveUtils
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__LOGGING_BOOSTER_ITEM_SELECTION_DETAILS);
		if (logBoosterItemDetails) {
			log.info(
					"selecting booster item. sumOfProbabilities={} \t randFloat={}",
					sumOfProbabilities, randFloat);
		}

		int size = itemsList.size();
		//for each item, normalize before seeing if it is selected
		for (int i = 0; i < size; i++) {
			BoosterItem item = itemsList.get(i);

			//normalize probability
			unnormalizedProbabilitySoFar += item.getChanceToAppear();
			float normalizedProbabilitySoFar = unnormalizedProbabilitySoFar
					/ sumOfProbabilities;

			if (logBoosterItemDetails) {
				log.info("boosterItem={} \t normalizedProbabilitySoFar={}",
						item, normalizedProbabilitySoFar);
			}

			if (randFloat < normalizedProbabilitySoFar) {
				//we have a winner! current boosterItem is what the user gets
				return item;
			}
		}

		log.error("maybe no boosterItems exist. boosterItems={}", itemsList);
		return null;
	}

	//purpose of this method is to discover if the booster items that contain
	//monsters as rewards, if the monster ids are valid

//	public static int determineGemRewardForSale(List<SalesItem> saleItems) {
//		int gemReward = 0;
//		for (SalesItem si : saleItems) {
//			gemReward += si.getGemReward();
//		}
//
//		return gemReward;
//	}

	//TODO: move to createInfoProtoUtils
	public List<FullUserMonsterProto> createFullUserMonsterProtos(
			List<String> userMonsterIds, List<MonsterForUser> mfuList) {
		List<FullUserMonsterProto> protos = new ArrayList<FullUserMonsterProto>();

		for (int i = 0; i < userMonsterIds.size(); i++) {
			String mfuId = userMonsterIds.get(i);
			MonsterForUser mfu = mfuList.get(i);
			mfu.setId(mfuId);
			FullUserMonsterProto fump = createInfoProtoUtils
					.createFullUserMonsterProtoFromUserMonster(mfu);
			protos.add(fump);
		}

		return protos;
	}

	public Dialogue createDialogue(String dialogueBlob) {
		if (dialogueBlob != null && dialogueBlob.length() > 0) {
			StringTokenizer st = new StringTokenizer(dialogueBlob, "~");

			List<Boolean> isLeftSides = new ArrayList<Boolean>();
			List<String> speakers = new ArrayList<String>();
			List<String> speakerImages = new ArrayList<String>();
			List<String> speakerTexts = new ArrayList<String>();

			CSVReader reader = null;
			try {
				while (st.hasMoreTokens()) {
					String tok = st.nextToken();
					reader = new CSVReader(new StringReader(tok), '.');
					String[] strs = reader.readNext();
					if (strs.length == 4) {
						Boolean isLeftSide = strs[0].toUpperCase().equals("L");
						String speaker = strs[1];
						String speakerImage = strs[2];
						String speakerText = strs[3];
						if (speakerText != null) {
							isLeftSides.add(isLeftSide);
							speakers.add(speaker);
							speakerImages.add(speakerImage);
							speakerTexts.add(speakerText);
						}
					}
				}
			} catch (Exception e) {
				log.error(
						"problem with creating dialogue object for this dialogueblob: {}",
						dialogueBlob, e);
			} finally {
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
						log.error("error trying to close CSVReader", e);
					}
				}
			}
			return new Dialogue(speakers, speakerImages, speakerTexts,
					isLeftSides);
		}
		return null;
	}

	public void explodeIntoInts(String stringToExplode,
			String delimiter, List<Integer> returnValue) {
		StringTokenizer st = new StringTokenizer(stringToExplode, delimiter);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken().trim();
			if (tok.isEmpty()) {
				continue;
			}
			returnValue.add(Integer.parseInt(tok));
		}
	}

	public String getIPOfPlayer(GameServer server, String playerId,
			String udid) {
		ConnectedPlayer player = null;
		if (playerId != null && !playerId.isEmpty()) {
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

	public void purgeMDCProperties() {
		MDC.remove(MDCKeys.UDID);
		MDC.remove(MDCKeys.PLAYER_ID);
		MDC.remove(MDCKeys.IP);
	}

	public void setMDCProperties(String udid, String playerId, String ip) {
		purgeMDCProperties();
		if (udid != null)
			MDC.put(MDCKeys.UDID, udid);
		if (ip != null)
			MDC.put(MDCKeys.IP, ip);
		if (null != playerId && !playerId.isEmpty())
			MDC.put(MDCKeys.PLAYER_ID, playerId);
	}

	public int calculateCoinsGivenToReferrer(User referrer) {
		return Math
				.min(ControllerConstants.USER_CREATE__MIN_COIN_REWARD_FOR_REFERRER,
						(int) (Math.ceil((referrer.getCash())
								* ControllerConstants.USER_CREATE__PERCENTAGE_OF_COIN_WEALTH_GIVEN_TO_REFERRER)));
	}

	public boolean checkClientTimeAroundApproximateNow(
			Timestamp clientTime) {
		if (clientTime.getTime() < new Date().getTime()
				+ Globals.NUM_MINUTES_DIFFERENCE_LEEWAY_FOR_CLIENT_TIME * 60000
				&& clientTime.getTime() > new Date().getTime()
						- Globals.NUM_MINUTES_DIFFERENCE_LEEWAY_FOR_CLIENT_TIME
						* 60000) {
			return true;
		}
		return false;
	}

	//	public List<City> getCitiesAvailableForUserLevel(int userLevel) {
	//		List<City> availCities = new ArrayList<City>();
	//		Map<Integer, City> cities = CityRetrieveUtils.getCityIdsToCities();
	//		for (Integer cityId : cities.keySet()) {
	//			City city = cities.get(cityId);
	//			availCities.add(city);
	//		}
	//		return availCities;
	//	}

	public UpdateClientUserResponseEvent createUpdateClientUserResponseEventAndUpdateLeaderboard(
			User user, PvpLeagueForUser plfu, Clan clan) {
		try {
			if (!user.isFake()) {
				/*LeaderBoardUtil leaderboard = AppContext.getApplicationContext().getBean(LeaderBoardUtil.class);
				leaderboard.updateLeaderboardForUser(user, plfu);*/
			}
		} catch (Exception e) {
			log.error("Failed to update leaderboard.");
		}

		// Retrieve clan if its not set
		if (clan == null && user.getClanId() != null
				&& !user.getClanId().isEmpty()) {
			ClanRetrieveUtils2 clanRetrieveUtils = AppContext
					.getApplicationContext().getBean(ClanRetrieveUtils2.class);
			clan = clanRetrieveUtils.getClanWithId(user.getClanId());
		} else if (clan != null && !clan.getId().equals(user.getClanId())) {
			log.error("Trying to set clan for user with different clan id.");
			clan = null;
		}

		UpdateClientUserResponseEvent resEvent = new UpdateClientUserResponseEvent(
				user.getId());
		UpdateClientUserResponseProto resProto = UpdateClientUserResponseProto
				.newBuilder()
				.setSender(
						createInfoProtoUtils.createFullUserProtoFromUser(user,
								plfu, clan))
				.setTimeOfUserUpdate(new Date().getTime()).build();
		resEvent.setUpdateClientUserResponseProto(resProto);
		return resEvent;
	}

	public int getRowCount(ResultSet set) {
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

	public TutorialConstants createTutorialConstantsProto() {
		TutorialConstants.Builder tcb = TutorialConstants.newBuilder();

		tcb.setStartingMonsterId(ControllerConstants.TUTORIAL__STARTING_MONSTER_ID);
		tcb.setGuideMonsterId(ControllerConstants.TUTORIAL__GUIDE_MONSTER_ID);
		tcb.setEnemyMonsterId(ControllerConstants.TUTORIAL__ENEMY_MONSTER_ID_ONE);
		tcb.setEnemyMonsterIdTwo(ControllerConstants.TUTORIAL__ENEMY_MONSTER_ID_TWO);
		tcb.setEnemyBossMonsterId(ControllerConstants.TUTORIAL__ENEMY_BOSS_MONSTER_ID);
		tcb.setMarkZMonsterId(ControllerConstants.TUTORIAL__MARK_Z_MONSTER_ID);

		for (int i = 0; i < ControllerConstants.TUTORIAL__EXISTING_BUILDING_IDS.length; i++) {

			int structId = ControllerConstants.TUTORIAL__EXISTING_BUILDING_IDS[i];
			float posX = ControllerConstants.TUTORIAL__EXISTING_BUILDING_X_POS[i];
			float posY = ControllerConstants.TUTORIAL__EXISTING_BUILDING_Y_POS[i];

			TutorialStructProto tsp = createInfoProtoUtils
					.createTutorialStructProto(structId, posX, posY);
			tcb.addTutorialStructures(tsp);
		}

		List<Integer> structureIdsToBeBuilt = Arrays
				.asList(ControllerConstants.TUTORIAL__STRUCTURE_IDS_TO_BUILD);
		tcb.addAllStructureIdsToBeBuillt(structureIdsToBeBuilt);

		//		int cityId = ControllerConstants.TUTORIAL__CITY_ONE_ID;
		//		tcb.setCityId(cityId);
		//		List<CityElement> cityElements = CityElementsRetrieveUtils.getCityElementsForCity(cityId);
		//		for (CityElement ce : cityElements) {
		//			CityElementProto cep = createInfoProtoUtils
		//				.createCityElementProtoFromCityElement(ce);
		//			tcb.addCityOneElements(cep);
		//		}
		//
		//		tcb.setCityElementIdForFirstDungeon(
		//			ControllerConstants.TUTORIAL__CITY_ONE_ASSET_NUM_FOR_FIRST_DUNGEON);
		//		tcb.setCityElementIdForSecondDungeon(
		//			ControllerConstants.TUTORIAL__CITY_ONE_ASSET_NUM_FOR_SECOND_DUNGEON);

		tcb.setCashInit(ControllerConstants.TUTORIAL__INIT_CASH);
		tcb.setOilInit(ControllerConstants.TUTORIAL__INIT_OIL);
		tcb.setGemsInit(ControllerConstants.TUTORIAL__INIT_GEMS);

		//    log.info("setting the tutorial minimum obstacle proto list!!!!!!!!!!");

		int orientation = 1;
		for (int i = 0; i < ControllerConstants.TUTORIAL__INIT_OBSTACLE_ID.length; i++) {
			int obstacleId = ControllerConstants.TUTORIAL__INIT_OBSTACLE_ID[i];
			float posX = ControllerConstants.TUTORIAL__INIT_OBSTACLE_X[i];
			float posY = ControllerConstants.TUTORIAL__INIT_OBSTACLE_Y[i];

			MinimumObstacleProto mopb = createInfoProtoUtils
					.createMinimumObstacleProto(obstacleId, posX, posY,
							orientation);
			tcb.addTutorialObstacles(mopb);
			//    	log.info("mopb=" + mopb);
		}

		return tcb.build();
	}

	public StartupConstants createStartupConstantsProto(Globals globals) {
		StartupConstants.Builder cb = StartupConstants.newBuilder();

		for (String id : IAPValues.iapPackageNames) {
			InAppPurchasePackageProto.Builder iapb = InAppPurchasePackageProto
					.newBuilder();

			String imgName = IAPValues.getImageNameForPackageName(id);
			if (null != imgName && !imgName.isEmpty()) {
				iapb.setImageName(imgName);
			}
			iapb.setIapPackageId(id);

			int diamondAmt = IAPValues.getDiamondsForPackageName(id);
			if (diamondAmt > 0) {
				iapb.setCurrencyAmount(diamondAmt);
			}

			try {
				InAppPurchasePackageType type = IAPValues.getPackageType(id);
				iapb.setIapPackageType(type);
			} catch (Exception e) {
				log.error(String.format("invalid IapPackageType. id=%s", id), e);
			}
			cb.addInAppPurchasePackages(iapb.build());

		}

		cb.setMaxLevelForUser(ControllerConstants.LEVEL_UP__MAX_LEVEL_FOR_USER);
		cb.setMaxNumOfSingleStruct(ControllerConstants.PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE);

		if (ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS != null) {
			for (int i = 0; i < ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS.length; i++) {
				AnimatedSpriteOffset aso = ControllerConstants.STARTUP__ANIMATED_SPRITE_OFFSETS[i];
				cb.addAnimatedSpriteOffsets(createInfoProtoUtils
						.createAnimatedSpriteOffsetProtoFromAnimatedSpriteOffset(aso));
			}
		}

		cb.setMinNameLength(ControllerConstants.USER_CREATE__MIN_NAME_LENGTH);
		cb.setMaxNameLength(ControllerConstants.USER_CREATE__MAX_NAME_LENGTH);
		cb.setMaxLengthOfChatString(ControllerConstants.SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING);

		ClanConstants.Builder clanConstantsBuilder = ClanConstants.newBuilder();
		clanConstantsBuilder
				.setMaxCharLengthForClanDescription(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION);
		clanConstantsBuilder
				.setMaxCharLengthForClanName(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME);
		clanConstantsBuilder
				.setCoinPriceToCreateClan(ControllerConstants.CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN);
		clanConstantsBuilder
				.setMaxCharLengthForClanTag(ControllerConstants.CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG);
		clanConstantsBuilder
				.setMaxClanSize(ControllerConstants.CLAN__MAX_NUM_MEMBERS);

		for (int i = 0; i < ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS.length; i++) {
			clanConstantsBuilder
					.addAchievementIdsForClanRewards(ControllerConstants.CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS[i]);
		}

		cb.setClanConstants(clanConstantsBuilder.build());

		DownloadableNibConstants.Builder dncb = DownloadableNibConstants
				.newBuilder();
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

		User adminChatUser = startupStuffRetrieveUtils.getAdminChatUser();
		if (null != adminChatUser) {
			MinimumUserProto adminChatUserProto = createInfoProtoUtils
					.createMinimumUserProtoFromUserAndClan(adminChatUser, null);
			cb.setAdminChatUserProto(adminChatUserProto);
		} else {
			log.error("adminChatUser is null");
		}

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
		mcb.setOilPerMonsterLevel(ControllerConstants.MONSTER__OIL_PER_MONSTER_LEVEL);
		cb.setMonsterConstants(mcb.build());

		//TODO: GET RID OF
		cb.setPvpRequiredMinLvl(ControllerConstants.PVP__REQUIRED_MIN_LEVEL);
		cb.setMonsterDmgMultiplier(ControllerConstants.PVP__MONSTER_DMG_MULTIPLIER);
		//*****************************************************************
		cb.setMinutesPerGem(ControllerConstants.MINUTES_PER_GEM);
		cb.setGemsPerResource(ControllerConstants.GEMS_PER_RESOURCE);

		createSpeedUpConstantsProto(cb);
		createResourceConversionConstantsProto(cb);

		cb.setContinueBattleGemCostMultiplier(ControllerConstants.BATTLE__CONTINUE_GEM_COST_MULTIPLIER);
		cb.setBattleRunAwayBasePercent(ControllerConstants.BATTLE__RUN_AWAY_BASE_PERCENT);
		cb.setBattleRunAwayIncrement(ControllerConstants.BATTLE__RUN_AWAY_INCREMENT);

		cb.setAddAllFbFriends(globals.isAddAllFbFriends());
		cb.setFacebookPopUp(ControllerConstants.FACEBOOK_POP_UP__ACTIVE);
		MiniTutorialConstants miniTuts = createMiniTutorialConstantsProto();
		cb.setMiniTuts(miniTuts);

		cb.setMaxObstacles(ControllerConstants.OBSTACLE__MAX_OBSTACLES);
		cb.setMinutesPerObstacle(ControllerConstants.OBSTACLE__MINUTES_PER_OBSTACLE);

		TaskMapConstants.Builder mapConstants = TaskMapConstants.newBuilder();
		mapConstants
				.setMapSectionImagePrefix(ControllerConstants.TASK_MAP__SECTION_IMAGE_PREFIX);
		mapConstants
				.setMapNumberOfSections(ControllerConstants.TASK_MAP__NUMBER_OF_SECTIONS);
		mapConstants
				.setMapSectionHeight(ControllerConstants.TASK_MAP__SECTION_HEIGHT);
		mapConstants
				.setMapTotalHeight(ControllerConstants.TASK_MAP__TOTAL_HEIGHT);
		mapConstants
				.setMapTotalWidth(ControllerConstants.TASK_MAP__TOTAL_WIDTH);
		cb.setTaskMapConstants(mapConstants.build());

		cb.setMaxMinutesForFreeSpeedUp(ControllerConstants.MAX_MINUTES_FOR_FREE_SPEED_UP);

		for (int index = 0; index < ControllerConstants.CLAN_HELP__HELP_TYPE.length; index++) {
			ClanHelpConstants.Builder chcb = ClanHelpConstants.newBuilder();
			String helpType = ControllerConstants.CLAN_HELP__HELP_TYPE[index];
			try {
				chcb.setHelpType(GameActionType.valueOf(helpType));
			} catch (Exception e) {
				log.error(String.format(
						"invalid GameActionType: %s, not using it", helpType),
						e);
				continue;
			}
			int amount = ControllerConstants.CLAN_HELP__AMOUNT_REMOVED[index];
			chcb.setAmountRemovedPerHelp(amount);
			float percent = ControllerConstants.CLAN_HELP__PERCENT_REMOVED[index];
			chcb.setPercentRemovedPerHelp(percent);

			cb.addClanHelpConstants(chcb.build());
		}

		PvpConstants.Builder pcb = PvpConstants.newBuilder();
		pcb.setPvpDmgsWindowSize(ControllerConstants.PVP__DMGS_WINDOW_SIZE);
		pcb.setMinPvpDmgDelta(ControllerConstants.PVP__MIN_DMG_DELTA);
		pcb.setMaxPvpDmgDelta(ControllerConstants.PVP__MAX_DMG_DELTA);
		pcb.setPvpRequiredMinLvl(ControllerConstants.PVP__REQUIRED_MIN_LEVEL);
		pcb.setDefendingMsgCharLimit(ControllerConstants.PVP__CHARACTER_LIMIT_FOR_DEFENSIVE_MSG);
		pcb.setBeginAvengingTimeLimitMins(ControllerConstants.PVP__BEGIN_AVENGING_TIME_LIMIT_MINS);
		pcb.setRequestClanToAvengeTimeLimitMins(ControllerConstants.PVP__REQUEST_CLAN_TO_AVENGE_TIME_LIMIT_MINS);
		cb.setPvpConstant(pcb.build());

		boolean displayQuality = serverToggleRetrieveUtils
				.getToggleValueForName(ControllerConstants.SERVER_TOGGLE__TASK_DISPLAY_RARITY);
		cb.setDisplayRarity(displayQuality);

		cb.setTaskIdOfFirstSkill(ControllerConstants.SKILL_FIRST_TASK_ID);
		cb.setMinsToResolicitTeamDonation(ControllerConstants.CLAN__MINS_TO_RESOLICIT_TEAM_DONATION);

		Map<Integer, FileDownload> fileDownloadMap = fileDownloadRetrieveUtils
				.getIdsToFileDownloads();

		if (fileDownloadMap == null) {
			log.error("no filedownloads");
		} else {
			List<FileDownload> fileDownloadList = new ArrayList<FileDownload>(
					fileDownloadMap.values());

			Collections.sort(fileDownloadList, new Comparator<FileDownload>() {
				@Override
				public int compare(FileDownload fd1, FileDownload fd2) {
					return fd1.getPriority() - fd2.getPriority();
				}
			});

			for (FileDownload fd : fileDownloadList) {
				cb.addFileDownloadProto(createInfoProtoUtils
						.createFileDownloadProtoFromFileDownload(fd));
			}

		}

		cb.setTaskIdForUpgradeTutorial(ControllerConstants.STARTUP__TASK_ID_FOR_UPGRADE_TUTORIAL);

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
		//        .setInfoImageName(ControllerConstants.BOOSTER_PACK__INFO_IMAGE_NAME)
		//        .build();
		//    cb = cb.setBoosterPackConstants(bpc);
		//

		return cb.build();
	}

	private void createSpeedUpConstantsProto(StartupConstants.Builder cb) {
		int len = ControllerConstants.SPEED_UP__SECONDS.length;
		for (int index = 0; index < len; index++) {
			int sec = ControllerConstants.SPEED_UP__SECONDS[index];
			int numGems = ControllerConstants.SPEED_UP__NUM_GEMS[index];

			SpeedUpConstantProto.Builder sucpb = SpeedUpConstantProto
					.newBuilder();
			sucpb.setSeconds(sec);
			sucpb.setNumGems(numGems);

			cb.addSucp(sucpb.build());
		}
	}

	private void createResourceConversionConstantsProto(
			StartupConstants.Builder cb) {
		//create list of protos for each resource type: oil, cash
		for (String type : ControllerConstants.RESOURCE_CONVERSION__TYPE) {
			int i = ControllerConstants.RESOURCE_CONVERSION__NUM_GEMS.length;

			ResourceType resourceType = null;
			try {
				resourceType = ResourceType.valueOf(type);
			} catch (Exception e) {
				log.error(String.format("incorrect ResourceType:%s", type), e);
			}

			//list of protos
			for (int index = 0; index < i; index++) {
				int numGems = ControllerConstants.RESOURCE_CONVERSION__NUM_GEMS[index];
				int resourceAmt = ControllerConstants.RESOURCE_CONVERSION__RESOURCE_AMOUNT[index];

				ResourceConversionConstantProto.Builder rccpb = ResourceConversionConstantProto
						.newBuilder();
				rccpb.setResourceType(resourceType);
				rccpb.setNumGems(numGems);
				rccpb.setResourceAmt(resourceAmt);

				cb.addRccp(rccpb.build());
			}
		}
	}

	public MiniTutorialConstants createMiniTutorialConstantsProto() {
		MiniTutorialConstants.Builder mtcb = MiniTutorialConstants.newBuilder();
		mtcb.setMiniTutorialTaskId(ControllerConstants.MINI_TUTORIAL__GUARANTEED_MONSTER_DROP_TASK_ID);
		mtcb.setGuideMonsterId(ControllerConstants.TUTORIAL__GUIDE_MONSTER_ID);
		mtcb.setEnhanceGuideMonsterId(ControllerConstants.TUTORIAL__ENHANCE_GUIDE_MONSTER_ID);

		return mtcb.build();
	}

	//	public List<TournamentEventProto> currentTournamentEventProtos() {
	//		Map<Integer, TournamentEvent> idsToEvents = TournamentEventRetrieveUtils.getIdsToTournamentEvents(false);
	//		long curTime = (new Date()).getTime();
	//		List<Integer> activeEventIds = new ArrayList<Integer>();
	//
	//		//return value
	//		List<TournamentEventProto> protos = new ArrayList<TournamentEventProto>();
	//
	//		//get the ids of active leader board events
	//		for(TournamentEvent e : idsToEvents.values()) {
	//			if (e.getEndDate().getTime()+ControllerConstants.TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END*3600000L > curTime) {
	//				activeEventIds.add(e.getId());
	//			}
	//		}
	//
	//		//get all the rewards for all the current leaderboard events
	//		Map<Integer, List<TournamentEventReward>> eventIdsToRewards =
	//			TournamentEventRewardRetrieveUtils.getLeaderboardEventRewardsForIds(activeEventIds);
	//
	//		//create the protos
	//		for(Integer i: activeEventIds) {
	//			TournamentEvent e = idsToEvents.get(i);
	//			List<TournamentEventReward> rList = eventIdsToRewards.get(e.getId()); //rewards for the active event
	//
	//			protos.add(createInfoProtoUtils.createTournamentEventProtoFromTournamentEvent(e, rList));
	//		}
	//		return protos;
	//	}



	//  //returns the clan towers that changed
	//  public void sendClanTowerWarNotEnoughMembersNotification(
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

	//  private void generateClanTowerNotEnoughMembersNotification(Clan aClan, List<Integer> towerIds,
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

	public GeneralNotificationResponseEvent getGlobalNotification(Notification n) {
		GeneralNotificationResponseProto.Builder notificationProto = n
				.generateNotificationBuilder();

		GeneralNotificationResponseEvent aNotification = new GeneralNotificationResponseEvent(
				"");
		aNotification.setGeneralNotificationResponseProto(notificationProto
				.build());
		return aNotification;
	}

	public void writeClanApnsNotification(Notification n,
			GameServer server, String clanId) {
		GeneralNotificationResponseProto.Builder notificationProto = n
				.generateNotificationBuilder();

		GeneralNotificationResponseEvent aNotification = new GeneralNotificationResponseEvent(
				"");
		aNotification.setGeneralNotificationResponseProto(notificationProto
				.build());
		server.writeApnsClanEvent(aNotification, clanId);
	}

	public GeneralNotificationResponseEvent writeNotificationToUser(Notification aNote,	String userId) {
		GeneralNotificationResponseProto.Builder notificationProto = aNote
				.generateNotificationBuilder();
		GeneralNotificationResponseEvent aNotification = new GeneralNotificationResponseEvent(
				userId);
		aNotification.setGeneralNotificationResponseProto(notificationProto
				.build());

		return aNotification;
	}

	//Simple (inefficient) word by word censor. If a word appears in
	//a blacklist then that word is replaced by a number of asterisks
	//equal to the word's length, e.g. fuck => ****
	//Not sure whether to use String or StringBuilder, so going with latter.
	public String censorUserInput(String userContent) {
		StringBuilder toReturn = new StringBuilder(userContent.length());
		Set<String> blackList = profanityRetrieveUtils.getAllProfanity();

		String[] words = userContent.split(" ");
		String space = " "; //split by space, need to add them back in
		String w = "";

		for (int i = 0; i < words.length; i++) {
			w = words[i];

			//if at the last word, don't add a space after "censoring" it
			if ((words.length - 1) == i) {
				space = "";
			}
			//get rid of all punctuation
			String wWithNoPunctuation = w.replaceAll("\\p{Punct}", "");

			//the profanity table only holds lower case one word profanities
			if (blackList.contains(wWithNoPunctuation.toLowerCase())) {
				toReturn.append(asteriskify(w) + space);
			} else {
				toReturn.append(w + space);
			}
		}

		return toReturn.toString();
	}

	//average length of word is 4 characters. So based on this, not using
	//StringBuilder
	public String asteriskify(String wordToAskerify) {
		int len = wordToAskerify.length();
		String s = "";

		for (int i = 0; i < len; i++) {
			s += "*";
		}
		return s;
	}

	public void writeToUserCurrencyUsers(List<String> userIds,
			Timestamp thyme, Map<String, Map<String, Integer>> changeMap,
			Map<String, Map<String, Integer>> previousCurrencyMap,
			Map<String, Map<String, Integer>> currentCurrencyMap,
			Map<String, Map<String, String>> changeReasonsMap,
			Map<String, Map<String, String>> detailsMap) {
		try {
			if (changeMap.isEmpty()) {
				String preface = "changeMap is empty";
				log.warn(String
						.format("%s userIds=%s, changeMap=%s, changeReasonsMap=%s, detailsMap=%s",
								preface, userIds, changeMap, changeReasonsMap,
								detailsMap));
				return;
			}
			List<String> allUserIds = new ArrayList<String>();
			List<Timestamp> allTimestamps = new ArrayList<Timestamp>();
			List<String> allResourceTypes = new ArrayList<String>();
			List<Integer> allCurrencyChanges = new ArrayList<Integer>();
			List<Integer> allPreviousCurrencies = new ArrayList<Integer>();
			List<Integer> allCurrentCurrencies = new ArrayList<Integer>();
			List<String> allReasonsForChanges = new ArrayList<String>();
			List<String> allDetails = new ArrayList<String>();

			//for each user, accrue up the values to store to the db
			for (String userId : userIds) {
				Map<String, Integer> oneUserChangeMap = null;
				if (changeMap.containsKey(userId)) {
					oneUserChangeMap = changeMap.get(userId);
				}
				if (null == oneUserChangeMap) {
					continue;
				}

				Map<String, Integer> oneUserPrevCurrency = previousCurrencyMap
						.get(userId);
				Map<String, Integer> oneUserCurCurrency = currentCurrencyMap
						.get(userId);
				Map<String, String> oneUserChangeReasons = changeReasonsMap
						.get(userId);
				Map<String, String> oneUserDetails = detailsMap.get(userId);

				//aggregate all the data across all the users into db friendly
				//arguments
				writeToUserCurrencyUsersHelper(userId, thyme, oneUserChangeMap,
						oneUserPrevCurrency, oneUserCurCurrency,
						oneUserChangeReasons, oneUserDetails, allUserIds,
						allTimestamps, allResourceTypes, allCurrencyChanges,
						allPreviousCurrencies, allCurrentCurrencies,
						allReasonsForChanges, allDetails);
			}

			if (allCurrencyChanges.isEmpty()) {
				String preface = "no currency changes";
				log.warn(String
						.format("%s userIds=%s, changeMap=%s, changeReasonsMap=%s, detailsMap=%s",
								preface, userIds, changeMap, changeReasonsMap,
								detailsMap));
				return;
			}

			int numInserted = InsertUtils.get()
					.insertIntoUserCurrencyHistoryMultipleRows(allUserIds,
							allTimestamps, allResourceTypes,
							allCurrencyChanges, allPreviousCurrencies,
							allCurrentCurrencies, allReasonsForChanges,
							allDetails);

			log.info(String.format("numInserted into currency history: %s",
					numInserted));

		} catch (Exception e) {
			String preface = "error updating user_curency_history";
			log.error(
					String.format(
							"%s userIds=%s, changeMap=%s, changeReasonsMap=%s, detailsMap=%s",
							preface, userIds, changeMap, changeReasonsMap,
							detailsMap), e);
		}
	}

	protected void writeToUserCurrencyUsersHelper(String userId,
			Timestamp thyme, Map<String, Integer> changeMap,
			Map<String, Integer> previousCurrencyMap,
			Map<String, Integer> currentCurrencyMap,
			Map<String, String> changeReasonsMap,
			Map<String, String> detailsMap, List<String> userIds,
			List<Timestamp> timestamps, List<String> resourceTypes,
			List<Integer> currencyChanges, List<Integer> previousCurrencies,
			List<Integer> currentCurrencies, List<String> reasonsForChanges,
			List<String> details) {

		Map<String, Integer> changeMapTemp = new HashMap<String, Integer>(
				changeMap);

		//getting rid of changes that are 0
		Set<String> keys = new HashSet<String>(changeMapTemp.keySet());
		for (String key : keys) {
			Integer change = changeMap.get(key);
			if (0 == change) {
				changeMapTemp.remove(key);
			}
		}

		//		int amount = changeMap.size();
		int amount = changeMapTemp.size();
		if (0 == amount) {
			return;
		}

		List<String> userIdsTemp = Collections.nCopies(amount, userId);
		List<Timestamp> timestampsTemp = Collections.nCopies(amount, thyme);
		List<String> resourceTypesTemp = new ArrayList<String>(
				changeMapTemp.keySet());
		List<Integer> currencyChangesTemp = getValsInOrder(resourceTypesTemp,
				changeMapTemp);
		List<Integer> previousCurrenciesTemp = getValsInOrder(
				resourceTypesTemp, previousCurrencyMap);
		List<Integer> currentCurrenciesTemp = getValsInOrder(resourceTypesTemp,
				currentCurrencyMap);
		List<String> reasonsForChangesTemp = getValsInOrder(resourceTypesTemp,
				changeReasonsMap);
		List<String> detailsTemp = getValsInOrder(resourceTypesTemp, detailsMap);

		userIds.addAll(userIdsTemp);
		timestamps.addAll(timestampsTemp);
		resourceTypes.addAll(resourceTypesTemp);
		currencyChanges.addAll(currencyChangesTemp);
		previousCurrencies.addAll(previousCurrenciesTemp);
		currentCurrencies.addAll(currentCurrenciesTemp);
		reasonsForChanges.addAll(reasonsForChangesTemp);
		details.addAll(detailsTemp);
	}

	//currencyChange should represent how much user's currency increased or decreased and
	//this should be called after the user is updated
	//arguments are modified!!!
	public void writeToUserCurrencyOneUser(String userId,
			Timestamp thyme, Map<String, Integer> changeMap,
			Map<String, Integer> previousCurrencyMap,
			Map<String, Integer> currentCurrencyMap,
			Map<String, String> changeReasonsMap, Map<String, String> detailsMap) {
		try {
		    // Safeguard against null pointer
		    if (changeMap == null || changeMap.isEmpty()) {
		        return;
		    }

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

			int amount = changeMap.size();

			List<String> userIds = Collections.nCopies(amount, userId);
			List<Timestamp> timestamps = Collections.nCopies(amount, thyme);
			List<String> resourceTypes = new ArrayList<String>(
					changeMap.keySet());
			List<Integer> currencyChanges = getValsInOrder(resourceTypes,
					changeMap);
			List<Integer> previousCurrencies = getValsInOrder(resourceTypes,
					previousCurrencyMap);
			List<Integer> currentCurrencies = getValsInOrder(resourceTypes,
					currentCurrencyMap);
			List<String> reasonsForChanges = getValsInOrder(resourceTypes,
					changeReasonsMap);
			List<String> details = getValsInOrder(resourceTypes, detailsMap);

			if (currencyChanges.isEmpty() || previousCurrencies.isEmpty()
					|| currentCurrencies.isEmpty()
					|| reasonsForChanges.isEmpty()) {
				return;
			}

			int numInserted = InsertUtils.get()
					.insertIntoUserCurrencyHistoryMultipleRows(userIds,
							timestamps, resourceTypes, currencyChanges,
							previousCurrencies, currentCurrencies,
							reasonsForChanges, details);
			log.info("(expected 1) numInserted into currency history: "
					+ numInserted);

		} catch (Exception e) {
			log.error("error updating user_curency_history; reasonsForChanges="
					+ changeReasonsMap, e);
		}
	}

	public <T> List<T> getValsInOrder(List<String> keys,
			Map<String, T> keysToVals) {
		List<T> valsInOrder = new ArrayList<T>();
		for (String key : keys) {
			T val = keysToVals.get(key);
			valsInOrder.add(val);
		}
		return valsInOrder;
	}

	//  public boolean isEquipAtMaxEnhancementLevel(MonsterForUser enhancingUserEquip) {
	//    int currentEnhancementLevel = enhancingUserEquip.getEnhancementPercentage();
	//    int maxEnhancementLevel = ControllerConstants.MAX_ENHANCEMENT_LEVEL
	//        * ControllerConstants.ENHANCEMENT__PERCENTAGE_PER_LEVEL;
	//
	//    return currentEnhancementLevel >= maxEnhancementLevel;
	//  }

	public int pointsGainedForClanTowerUserBattle(User winner, User loser) {
		int d = winner.getLevel() - loser.getLevel();
		int pts;
		if (d > 10) {
			pts = 1;
		} else if (d < -8) {
			pts = 100;
		} else {
			pts = (int) Math.round((-0.0997 * Math.pow(d, 3) + 1.4051
					* Math.pow(d, 2) - 14.252 * d + 90.346) / 10.);
		}
		return Math.min(100, Math.max(1, pts));
	}

	//  public int dateDifferenceInDays(Date start, Date end) {
	//    DateMidnight previous = (new DateTime(start)).toDateMidnight(); //
	//    DateMidnight current = (new DateTime(end)).toDateMidnight();
	//    int days = Days.daysBetween(previous, current).getDays();
	//    return days;
	//  }

	//  private List<Integer> getRaritiesCollected(
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
	//  private boolean isRarityOne(Rarity equipRarity) {
	//    if (Rarity.COMMON == equipRarity || Rarity.RARE == equipRarity) {
	//      return true;
	//    } else {
	//      return false;
	//    }
	//  }
	//
	//  private boolean isRarityTwo(Rarity equipRarity) {
	//    if (Rarity.UNCOMMON == equipRarity || Rarity.SUPERRARE == equipRarity) {
	//      return true;
	//    } else {
	//      return false;
	//    }
	//  }
	//
	//  private boolean isRarityThree(Rarity equipRarity) {
	//    if (Rarity.RARE == equipRarity || Rarity.EPIC == equipRarity) {
	//      return true;
	//    } else {
	//      return false;
	//    }
	//  }
	//
	//csi: comma separated ints
	public List<Integer> unCsvStringIntoIntList(String csi) {
		List<Integer> ints = new ArrayList<Integer>();
		if (null != csi) {
			StringTokenizer st = new StringTokenizer(csi, ", ");
			while (st.hasMoreTokens()) {
				ints.add(Integer.parseInt(st.nextToken()));
			}
		}
		return ints;
	}

	public int getRandomIntFromList(List<Integer> numList) {
		int upperBound = numList.size();
		Random rand = new Random();
		int randInt = rand.nextInt(upperBound);

		int returnValue = numList.get(randInt);
		return returnValue;
	}

	public Map<Integer, Integer> getRandomValues(List<Integer> domain,
			int quantity) {
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
	//  private int determineBoosterItemsLeft(Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems,
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
	//  private List<BoosterItem> determineStarterBoosterItemsUserReceives(List<Integer> boosterItemIdsUserCanGet,
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
	//	private List<BoosterItem> determineBoosterItemsUserReceives(List<Integer> boosterItemIdsUserCanGet,
	//		List<Integer> quantitiesInStock, int amountUserWantsToPurchase, int sumOfQuantitiesInStock,
	//		Map<Integer, BoosterItem> allBoosterItemIdsToBoosterItems) {
	//		//return value
	//		List<BoosterItem> itemsUserReceives = new ArrayList<BoosterItem>();
	//
	//		Random rand = new Random();
	//		List<Integer> newBoosterItemIdsUserCanGet = new ArrayList<Integer>(boosterItemIdsUserCanGet);
	//		List<Integer> newQuantitiesInStock = new ArrayList<Integer>(quantitiesInStock);
	//		int newSumOfQuantities = sumOfQuantitiesInStock;
	//
	//		//selects one of the ids at random without replacement
	//		for(int purchaseN = 0; purchaseN < amountUserWantsToPurchase; purchaseN++) {
	//			int sumSoFar = 0;
	//			int randomNum = rand.nextInt(newSumOfQuantities) + 1; //range [1, newSumOfQuantities]
	//
	//			for(int i = 0; i < newBoosterItemIdsUserCanGet.size(); i++) {
	//				int bItemId = newBoosterItemIdsUserCanGet.get(i);
	//				int quantity = newQuantitiesInStock.get(i);
	//
	//				sumSoFar += quantity;
	//
	//				if(randomNum <= sumSoFar) {
	//					//we have a winner! current boosterItemId is what the user gets
	//					BoosterItem selectedBoosterItem = allBoosterItemIdsToBoosterItems.get(bItemId);
	//					itemsUserReceives.add(selectedBoosterItem);
	//
	//					//preparation for next BoosterItem to be selected
	//					if (1 == quantity) {
	//						newBoosterItemIdsUserCanGet.remove(i);
	//						newQuantitiesInStock.remove(i);
	//					} else if (1 < quantity){
	//						//booster item id has more than one quantity
	//						int decrementedQuantity = newQuantitiesInStock.remove(i) - 1;
	//						newQuantitiesInStock.add(i, decrementedQuantity);
	//					} else {
	//						//ignore those with quantity of 0
	//						continue;
	//					}
	//
	//					newSumOfQuantities -= 1;
	//					break;
	//				}
	//			}
	//		}
	//
	//		return itemsUserReceives;
	//	}
	//  /*cut out from purchase booster pack controller*/
	//  public List<Long> insertNewUserEquips(int userId,
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
	//  public boolean updateUserBoosterItems(List<BoosterItem> itemsUserReceives,
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
	//	private void recordBoosterItemsThatReset(Map<Integer, Integer> changedBoosterItemIdsToNumCollected,
	//		Map<Integer, Integer> newBoosterItemIdsToNumCollected, boolean refilled) {
	//		if (refilled) {
	//			for (int boosterItemId : newBoosterItemIdsToNumCollected.keySet()) {
	//				if (!changedBoosterItemIdsToNumCollected.containsKey(boosterItemId)) {
	//					int value = newBoosterItemIdsToNumCollected.get(boosterItemId);
	//					changedBoosterItemIdsToNumCollected.put(boosterItemId, value);
	//				}
	//			}
	//		}
	//	}

	/* public Set<Long> getEquippedEquips(User aUser) {
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
	//  public int costToBuyForgeSlot(int goalNumAdditionalForgeSlots,
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

	public int sumMapValues(Map<?, Integer> idToNum) {
		int sumSoFar = 0;

		for (int value : idToNum.values()) {
			sumSoFar += value;
		}
		return sumSoFar;
	}

	public int sumListsInMap(Map<Integer, List<Integer>> aMap) {
		int sum = 0;
		for (int i : aMap.keySet()) {

			for (Integer value : aMap.get(i)) {
				sum += value;
			}
		}
		return sum;
	}

	public void calculateEloChangeAfterBattle(PvpLeagueForUser attacker,
			PvpLeagueForUser defender, boolean attackerWon) {
		double probabilityOfAttackerWin = 1 / (1 + Math.pow(10,
				(defender.getElo() - attacker.getElo()) / 400));
		double probabilityOfDefenderWin = 1 - probabilityOfAttackerWin;
		int kFactor = 0;

		if (attacker.getElo() < 1900 || defender.getElo() < 2500) {
			kFactor = 32;
		} else if (attacker.getElo() < 2400 || defender.getElo() < 3500) {
			kFactor = 24;
		} else
			kFactor = 16;

		int newAttackerElo, newDefenderElo;
		//calculate change in elo
		if (attackerWon) {
			newAttackerElo = (int) (attacker.getElo() + kFactor
					* (1 - probabilityOfAttackerWin));
			newDefenderElo = (int) (defender.getElo() + kFactor
					* (0 - probabilityOfDefenderWin));
		} else {
			newAttackerElo = (int) (attacker.getElo() + kFactor
					* (0 - probabilityOfAttackerWin));
			newDefenderElo = (int) (defender.getElo() + kFactor
					* (1 - probabilityOfDefenderWin));
		}
		attacker.setElo(newAttackerElo);
		defender.setElo(newDefenderElo);

	}

	public int speedupCostOverTime(int cost, long startTimeMillis,
			long durationInSeconds, long curTimeMillis) {

		long timePassedSeconds = (curTimeMillis = startTimeMillis) / 1000;
		long timeRemainingSeconds = durationInSeconds - timePassedSeconds;

		double percentRemaining = timeRemainingSeconds
				/ (double) (durationInSeconds);

		int newCost = (int) Math.ceil(cost * percentRemaining);
		return newCost;
	}

	public StaticDataProto getAllStaticData(String userId,
			boolean userIdSet, QuestForUserRetrieveUtils2 qfuRetrieveUtils) {

		StaticDataProto.Builder sdpb = null;

		StaticDataProto staticData = staticDataContainer.getStaticData();
		if (null == staticData) {
			log.error("NO STATIC DATA!!! Only going to try setting Quests");
			sdpb = StaticDataProto.newBuilder();
		} else {
			sdpb = staticData.toBuilder();
		}

		setInProgressAndAvailableQuests(sdpb, userId, userIdSet,
				qfuRetrieveUtils);

		//setStaticData(sdpb);

		return sdpb.build();
	}

	private void setInProgressAndAvailableQuests(Builder sdpb,
			String userId, boolean userIdSet,
			QuestForUserRetrieveUtils2 questForUserRetrieveUtils) {
		if (!userIdSet) {
			return;
		}
		List<QuestForUser> inProgressAndRedeemedUserQuests = questForUserRetrieveUtils
				.getUserQuestsForUser(userId);

		List<Integer> inProgressQuestIds = new ArrayList<Integer>();
		List<Integer> redeemedQuestIds = new ArrayList<Integer>();

		Map<Integer, Quest> questIdToQuests = questRetrieveUtils
				.getQuestIdsToQuests();
		for (QuestForUser uq : inProgressAndRedeemedUserQuests) {

			if (uq.isRedeemed()) {
				redeemedQuestIds.add(uq.getQuestId());

			} else {
				//unredeemed quest section
				Quest quest = questRetrieveUtils.getQuestForQuestId(uq
						.getQuestId());
				FullQuestProto questProto = createInfoProtoUtils
						.createFullQuestProtoFromQuest(quest);

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

		List<Integer> availableQuestIds = questUtils.getAvailableQuestsForUser(
				redeemedQuestIds, inProgressQuestIds);
		if (availableQuestIds == null) {
			return;
		}

		//from the available quest ids generate the available quest protos
		for (Integer questId : availableQuestIds) {
			FullQuestProto fqp = createInfoProtoUtils
					.createFullQuestProtoFromQuest(questIdToQuests.get(questId));
			sdpb.addAvailableQuests(fqp);
		}
	}
	//
	//	private void setStaticData(StaticDataProto.Builder sdpb) {
	//		StaticDataProto staticData = StaticDataContainer.getStaticData();
	//
	//		if (null == staticData) {
	//			log.error("NO STATIC DATA!!!");
	//			return;
	//		}
	//		setTasks(sdpb, staticData);
	//		sdpb.addAllAllMonsters(staticData.getAllMonstersList());
	//		sdpb.addAllSlip(staticData.getSlipList());
	//		sdpb.addAllBoosterPacks(sdpb.getBoosterPacksList());
	//		setStructures(sdpb, staticData);
	//		sdpb.addAllPersistentEvents(staticData.getPersistentEventsList());
	//		sdpb.addAllMbds(staticData.getMbdsList());
	//		setClanRaidStuff(sdpb, staticData);
	//		sdpb.addAllItems(staticData.getItemsList());
	//		sdpb.addAllObstacles(staticData.getObstaclesList());
	//		sdpb.addAllClanIcons(staticData.getClanIconsList());
	//		sdpb.addAllLeagues(staticData.getLeaguesList());
	//		sdpb.addAllAchievements(staticData.getAchievementsList());
	//		sdpb.addAllSkills(staticData.getSkillsList());
	//		sdpb.addAllPrereqs(staticData.getPrereqsList());
	//		sdpb.addAllBoards(staticData.getBoardsList());
	//		sdpb.addAllResearch(staticData.getResearchList());
	//	}
	//
	//	private void setTasks( StaticDataProto.Builder sdpb, StaticDataProto staticData )
	//	{
	//		sdpb.addAllAllTasks(staticData.getAllTasksList());
	//		sdpb.addAllAllTaskMapElements(staticData.getAllTaskMapElementsList());
	//	}
	//
	//	private void setStructures( StaticDataProto.Builder sdpb, StaticDataProto staticData )
	//	{
	//		sdpb.addAllAllGenerators(staticData.getAllGeneratorsList());
	//		sdpb.addAllAllStorages(staticData.getAllStoragesList());
	//		sdpb.addAllAllHospitals(staticData.getAllHospitalsList());
	//		sdpb.addAllAllResidences(staticData.getAllResidencesList());
	//		sdpb.addAllAllTownHalls(staticData.getAllTownHallsList());
	//		sdpb.addAllAllLabs(staticData.getAllLabsList());
	//		sdpb.addAllAllMiniJobCenters(staticData.getAllMiniJobCentersList());
	//		sdpb.addAllAllEvoChambers(staticData.getAllEvoChambersList());
	//		sdpb.addAllAllTeamCenters(staticData.getAllTeamCentersList());
	//		sdpb.addAllAllClanHouses(staticData.getAllClanHousesList());
	//	}
	//
	//	private void setClanRaidStuff(
	//		StaticDataProto.Builder sdpb,
	//		StaticDataProto staticData )
	//	{
	//		sdpb.addAllRaids(staticData.getRaidsList());
	//		sdpb.addAllPersistentClanEvents(staticData.getPersistentClanEventsList());
	//	}

	public Map<TranslateLanguages, String> translate(Language sourceLanguage,
			Language recipientLanguage, String text) {
		Translate.setClientId(pClientId);
		Translate.setClientSecret(secretId);

		String translatedText = "";
		Map<TranslateLanguages, String> returnMap = new HashMap<TranslateLanguages, String>();

		List<Language> listOfLanguages = new ArrayList<Language>();
		listOfLanguages.add(Language.ARABIC);
		listOfLanguages.add(Language.ENGLISH);
		listOfLanguages.add(Language.FRENCH);
		listOfLanguages.add(Language.GERMAN);
		listOfLanguages.add(Language.SPANISH);
		listOfLanguages.add(Language.RUSSIAN);

		try {
			if(recipientLanguage != null) {
				if(sourceLanguage == null) {
					translatedText = Translate.execute(text, recipientLanguage);
				}
				else translatedText = Translate.execute(text, sourceLanguage, recipientLanguage);
				TranslateLanguages tl2 = convertFromLanguageToEnum(recipientLanguage);
				returnMap.put(tl2, translatedText);
			}
			else {
				for(Language language2 : listOfLanguages) {
					if(sourceLanguage != null) {
						if(sourceLanguage.toString().equalsIgnoreCase(language2.toString())) {
							TranslateLanguages tl = convertFromLanguageToEnum(language2);
							returnMap.put(tl, text);
						}
					}
					else {
						if(sourceLanguage == null) {
							translatedText = Translate.execute(text, language2);
						}
						else translatedText = Translate.execute(text, sourceLanguage, language2);
						TranslateLanguages tl = convertFromLanguageToEnum(language2);
						returnMap.put(tl, translatedText);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMap;
	}

	public Map<TranslateLanguages, String> translateForGlobal(Language sourceLanguage, String text) {
		Translate.setClientId(pClientId);
		Translate.setClientSecret(secretId);

		String translatedText = "";
		Map<TranslateLanguages, String> returnMap = new HashMap<TranslateLanguages, String>();

		List<Language> listOfLanguages = new ArrayList<Language>();
		listOfLanguages.add(Language.ARABIC);
		listOfLanguages.add(Language.ENGLISH);
		listOfLanguages.add(Language.FRENCH);
		listOfLanguages.add(Language.GERMAN);
		listOfLanguages.add(Language.SPANISH);
		listOfLanguages.add(Language.RUSSIAN);

		try {
			for(Language language2 : listOfLanguages) {
				if(sourceLanguage.toString().equalsIgnoreCase(language2.toString())) {
					TranslateLanguages tl = convertFromLanguageToEnum(language2);
					returnMap.put(tl, text);
				}
				else {
					translatedText = Translate.execute(text, sourceLanguage, language2);
					TranslateLanguages tl = convertFromLanguageToEnum(language2);
					returnMap.put(tl, translatedText);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMap;

	}

	private TranslateLanguages convertFromLanguageToEnum(Language language) {
		TranslateLanguages tl = null;
		try {
			if(language.getName(Language.ENGLISH).equalsIgnoreCase("ARABIC")) {
				tl = TranslateLanguages.ARABIC;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("ENGLISH")) {
				tl = TranslateLanguages.ENGLISH;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("FRENCH")) {
				tl = TranslateLanguages.FRENCH;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("GERMAN")) {
				tl = TranslateLanguages.GERMAN;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("RUSSIAN")) {
				tl = TranslateLanguages.RUSSIAN;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("SPANISH")){
				tl = TranslateLanguages.SPANISH;
			}
			else {
				tl = TranslateLanguages.NO_TRANSLATION;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tl;

	}

	public Language convertFromEnumToLanguage(TranslateLanguages tl) {
		if(tl.toString().equalsIgnoreCase("ARABIC")) {
			return Language.ARABIC;
		}
		else if(tl.toString().equalsIgnoreCase("ENGLISH")) {
			return Language.ENGLISH;
		}
		else if(tl.toString().equalsIgnoreCase("FRENCH")) {
			return Language.FRENCH;
		}
		else if(tl.toString().equalsIgnoreCase("GERMAN")) {
			return Language.GERMAN;
		}
		else if(tl.toString().equalsIgnoreCase("SPANISH")) {
			return Language.SPANISH;
		}
		else if(tl.toString().equalsIgnoreCase("RUSSIAN")) {
			return Language.RUSSIAN;
		}
		else return null;
	}

	public Language detectedLanguage(String text) {
		Detect.setClientId(pClientId);
        Detect.setClientSecret(secretId);
        Language detectedLanguage = null;

        try {
			detectedLanguage = Detect.execute(text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("error detecting language of text=%s", text);
			e.printStackTrace();
		}
        return detectedLanguage;
	}



}
