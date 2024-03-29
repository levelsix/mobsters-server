package com.lvl6.properties;

import java.util.Date;
import java.util.Random;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.joda.time.DateTime;

import com.lvl6.info.AnimatedSpriteOffset;
import com.lvl6.info.CoordinatePair;
import com.lvl6.proto.SharedEnumConfigProto.GameActionType;
import com.lvl6.proto.StructureProto.ResourceType;

public class ControllerConstants {

	//MOBSTERS CONSTANTS
	public static final Random RAND = new Random();
	//Date we launched the game...close enough
	public static final Date INCEPTION_DATE = new DateTime(2014, 12, 4, 0, 0)
			.toDate();

	//includes oil and cash, 1 gem per 1000 resource?
	public static final float GEMS_PER_RESOURCE = 0.001F;
	public static final float GEMS_PER_DOLLAR = 10f;//client doesn't need this
	public static final float MINUTES_PER_GEM = 10f;
	public static final Integer MAX_MINUTES_FOR_FREE_SPEED_UP = 5;

	//this multiplies with the cost to heal all monsters on user's battle team
	//BATTLE, DUNGEON, TASK
	public static final float BATTLE__CONTINUE_GEM_COST_MULTIPLIER = 1.2F;
	public static final float BATTLE__RUN_AWAY_BASE_PERCENT = 0.5F;
	public static final float BATTLE__RUN_AWAY_INCREMENT = 0.25F;

	//BOOSTER_PACK
	public static final int BOOSTER_PACK__INIT_PURCHASE_BOOSTER_PACK_ID = 3;
	public static final int BOOSTER_PACK__AMOUNT_NEEDED_TO_PURCHASE = 10;
	public static final int BOOSTER_PACK__AMOUNT_RECEIVED_FROM_BULK_PURCHASE = 11;

	//clan
	public static final int CLAN__MAX_NUM_MEMBERS = Globals.IS_SANDBOX() ? 20
			: 100;
	public static final Integer[] CLAN__ACHIEVEMENT_IDS_FOR_CLAN_REWARDS = { 1000,
			1001, 1002 };
	public static final int CLAN__TOP_N_CLANS = 50;
	public static final int CLAN__MINS_TO_RESOLICIT_TEAM_DONATION = 20;

	//CLAN EVENT PERSISTENT
	public static final int CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_HISTORY = 14;
	public static final int CLAN_EVENT_PERSISTENT__NUM_DAYS_FOR_RAID_STAGE_HISTORY = 7;

	//CLAN HELP
	//	public static final int   CLAN_HELP__EVOLVE_AMOUNT_REMOVED = 1;
	//	public static final float CLAN_HELP__EVOLVE_PERCENT_REMOVED = 0.01F;
	//	public static final int   CLAN_HELP__HEAL_AMOUNT_REMOVED = 5;
	//	public static final float CLAN_HELP__HEAL_PERCENT_REMOVED = 0.01F;
	//	public static final int   CLAN_HELP__MINI_JOB_AMOUNT_REMOVED = 1;
	//	public static final float CLAN_HELP__MINI_JOB_PERCENT_REMOVED = 0.01F;
	//	public static final int   CLAN_HELP__UPGRADE_STRUCT_AMOUNT_REMOVED = 1;
	//	public static final float CLAN_HELP__UPGRADE_STRUCT_PERCENT_REMOVED = 0.01F;

	public static final String[] CLAN_HELP__HELP_TYPE = {
			GameActionType.EVOLVE.name(), GameActionType.HEAL.name(),
			GameActionType.MINI_JOB.name(),
			GameActionType.UPGRADE_STRUCT.name(),
			GameActionType.ENHANCE_TIME.name(),
			GameActionType.PERFORMING_RESEARCH.name(),
			GameActionType.CREATE_BATTLE_ITEM.name()

	};
	public static final int[] CLAN_HELP__AMOUNT_REMOVED = { 1, 1, 1, 1, 1, 1, 1 };
	public static final float[] CLAN_HELP__PERCENT_REMOVED = { 0.01F, 0.01F,
			0.01F, 0.01F, 0.01F, 0.01F, 0.01F };


	//EVENT PERSISTENT STUFF
	public static final int EVENT_PERSISTENT__END_COOL_DOWN_TIMER_GEM_COST = 5;

	//FACEBOOK POP UP
	public static final boolean FACEBOOK_POP_UP__ACTIVE = false;

	public static final int ITEM_ID__HIGH_ROLLER_MODE = 50000;

	public static final int IN_APP_PURCHASE__STARTER_PACK_BOOSTER_PACK_ID = 1000;

	//dof = degrees of freedom
	public static final double ITEM_SECRET_GIFT_FOR_USER__DOF = 4D;
	public static final ChiSquaredDistribution ITEM_SECRET_GIFT_FOR_USER__RANDOM = new ChiSquaredDistribution(
			ITEM_SECRET_GIFT_FOR_USER__DOF);

	public static final int ITEM_SECRET_GIFT_FOR_USER__NUM_NEW_GIFTS = 1;
	public static final int[] ITEM_SECRET_GIFT_FOR_USER__ITEM_IDS = { 52, 2,
			21, 53, 3 };
	public static final int[] ITEM_SECRET_GIFT_FOR_USER__WAIT_TIMES_SECONDS = {
			90, 150, 179, 265, 2280 };
	public static final int ITEM_SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME = 45;
	public static final int ITEM_SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME = 2100;
	public static final int ITEM_SECRET_GIFT_FOR_USER__SECS_WAIT_TIME_DELTA = ITEM_SECRET_GIFT_FOR_USER__MAX_SECS_WAIT_TIME
			- ITEM_SECRET_GIFT_FOR_USER__MIN_SECS_WAIT_TIME;

	public static final float MONSTER__CASH_PER_HEALTH_POINT = 0.5f;
	public static final float MONSTER__SECONDS_TO_HEAL_PER_HEALTH_POINT = 2f;
	public static final float MONSTER__ELEMENTAL_STRENGTH = 1.2F;
	public static final float MONSTER__ELEMENTAL_WEAKNESS = 0.8F;
	public static final float MONSTER__OIL_PER_MONSTER_LEVEL = 100f;

	public static final float MONSTER_ENHANCING__PLAYER_EXP_CONVERTER = 0.001F;
	//	public static final int MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT = 5;
	//	public static final int MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT = 2;
	//	public static final int MONSTER_INVENTORY_SLOTS__MIN_INVITES_TO_INCREASE_SLOTS = 3;

	//MFUSOP = monster_for_user_source_of_pieces
	public static final String MFUSOP__BOOSTER_PACK = "boosterPackId";
	public static final String MFUSOP__SALES_PACKAGE = "salesPackageId";
	public static final String MFUSOP__END_DUNGEON = "Task4UserId";
	public static final String MFUSOP__MINI_JOB = "miniJobId";
	public static final String MFUSOP__PVP = "pvp";
	public static final String MFUSOP__QUEST = "questId";
	public static final String MFUSOP__REDEEM_ITEM = "redeem itemId";
	public static final String MFUSOP__REDEEM_MINI_EVENT_REWARD = "redeem miniEventRewardId";
	public static final String MFUSOP__USER_CREATE = "user create";

	//MFUDR = monster_for_user_delete_reasons
	public static final String MFUDR__ENHANCING = "enhancing";
	public static final String MFUDR__QUEST = "quest";
	public static final String MFUDR__SELL = "sold for cash";

	//MONSTER FOR USER
	public static final int MONSTER_FOR_USER__MAX_TEAM_SIZE = 3;
	public static final int MONSTER_FOR_USER__INITIAL_MAX_NUM_MONSTER_LIMIT = 10;

	//MINI TUTORIAL CONSTANTS
	public static final int MINI_TUTORIAL__GUARANTEED_MONSTER_DROP_TASK_ID = 4;

	//OBSTACLE CONSTANTS
	public static final int OBSTACLE__MAX_OBSTACLES = 15;
	public static final int OBSTACLE__MINUTES_PER_OBSTACLE = 5;

	//PVP
	public static final int PVP__DEFAULT_MIN_ELO = 1000;
	//user lvl means nothing, since it doesn't indicate much besides maybe how much you played
	public static final int PVP__REQUIRED_MIN_LEVEL = 30;
	public static final int PVP__MAX_QUEUE_SIZE = 10;
	public static final int PVP__FAKE_USER_LVL_DIVISOR = 50;
	public static final long PVP__MAX_BATTLE_DURATION_MILLIS = 3600000L; //one hour
	public static final int PVP__CHARACTER_LIMIT_FOR_DEFENSIVE_MSG = 140;
	public static final int PVP__BEGIN_AVENGING_TIME_LIMIT_MINS = 10;
	public static final int PVP__REQUEST_CLAN_TO_AVENGE_TIME_LIMIT_MINS = 1440;
	public static final int PVP__MAX_ELO_TO_DISPLAY_ONLY_BOTS = 1250;

	//NOT USING ANYMORE--------------------------------------------------------------------
	//USED TO CREATE AN ELO RANGE FROM WHICH TO SELECT AN OPPONENT
	public static final int PVP__ELO_RANGE_SUBTRAHEND = 100;
	public static final int PVP__ELO_RANGE_ADDEND = 100;
	//all these pairing chances need to sum to one
	public static final float PVP__ELO_CATEGORY_ONE_PAIRING_CHANCE = 0.05F;
	public static final float PVP__ELO_CATEGORY_TWO_PAIRING_CHANCE = 0.15F;
	public static final float PVP__ELO_CATEGORY_THREE_PAIRING_CHANCE = 0.20F;
	public static final float PVP__ELO_CATEGORY_FOUR_PAIRING_CHANCE = 0.20F;
	public static final float PVP__ELO_CATEGORY_FIVE_PAIRING_CHANCE = 0.25F;
	public static final float PVP__ELO_CATEGORY_SIX_PAIRING_CHANCE = 0.15F;
	public static final int PVP__ELO_DISTANCE_ONE = 100;
	public static final int PVP__ELO_DISTANCE_TWO = 200;
	public static final int PVP__ELO_DISTANCE_THREE = 300;
	public static final int PVP__NUM_ENEMIES_LIMIT = 100;
	//-------------------------------------------------------------------------------------
	public static final float PVP__PERCENT_CASH_LOST = 0.25F;
	public static final float PVP__PERCENT_OIL_LOST = 0.25F;
	public static final int PVP__SHIELD_DURATION_DAYS = 3;
	public static final int PVP__LOST_BATTLE_SHIELD_DURATION_HOURS = 1;
	public static final int PVP__INITIAL_LEAGUE_ID = 1;
	public static final float PVP__MONSTER_DMG_MULTIPLIER = 2F;
	//(how many damages in pvp to record)
	public static final int PVP__DMGS_WINDOW_SIZE = 50;
	public static final float PVP__MIN_DMG_DELTA = 0.5F;
	public static final float PVP__MAX_DMG_DELTA = 0.3F;

	//PVP BATTLE HISTORY
	public static final int PVP_HISTORY__NUM_RECENT_BATTLES = 50;

	public static final int[] RESOURCE_CONVERSION__RESOURCE_AMOUNT = { 1, 1000,
			10000, 100000, 1000000, 3000000, 100000000 };
	public static final int[] RESOURCE_CONVERSION__NUM_GEMS = { 1, 5, 45, 400,
			3600, 10800, 360000 };

	// had to increase by 1 because of a bug on the client
	public static final int[] RESOURCE_CONVERSION__GACHA_CREDITS_AMOUNT = { 2, 502, 1001,
		2501, 5001, 20001, 50001, 100001, 1000001 };
	public static final int[] RESOURCE_CONVERSION__GACHA_CREDITS_NUM_GEMS = { 1, 251, 450,
		900, 1500, 5000, 12000, 24000, 240000 };


	public static final String[] RESOURCE_CONVERSION__TYPE = {
			ResourceType.CASH.name(), ResourceType.OIL.name(), ResourceType.GACHA_CREDITS.name() };

	//chats
	public static final int RETRIEVE_PLAYER_WALL_POSTS__NUM_POSTS_CAP = 150;

	//REWARDS
	public static final String REWARD_REASON__TANGO_GIFT = "tango_gift";
	public static final String REWARD_REASON__COLLECT_GIFT = "collect_gift";

	public static final int SALES_PACKAGE__HIGH_ROLLER = 1000001;

	//TOGGLES
	//	public static final String SERVER_TOGGLE__BOOSTER_PACKS_GIVE_EXP = "booster_packs_give_exp";
	public static final String SERVER_TOGGLE__ADD_ALL_FB_FRIENDS = "add_all_fb_friends";
	public static final String SERVER_TOGGLE__LOGGING_BOOSTER_ITEM_SELECTION_DETAILS = "logging_booster_item_selection_details";
	public static final String SERVER_TOGGLE__LOGGING_PVP_BATTLE_OUTCOME_DETAILS = "logging_pvp_battle_outcome_details";
	public static final String SERVER_TOGGLE__PVP_BOT_SET_ELO = "pvp_bot_set_elo";
	public static final String SERVER_TOGGLE__PVP_BOT_SHOW_ELO = "pvp_bot_show_elo";
	public static final String SERVER_TOGGLE__PVP_BOTS_ONLY_BELOW_SOME_ELO = "pvp_bots_only_below_some_elo";
	public static final String SERVER_TOGGLE__TASK_DISPLAY_RARITY = "task_display_rarity";
	public static final String SERVER_TOGGLE__TURN_OFF_TRANSLATIONS = "turn_off_translations";
	public static final String SERVER_TOGGLE__USE_BYRON_TRANSLATIONS = "use_byron_translations";
	public static final String SERVER_TOGGLE__OLD_CLAN_SEARCH = "old_clan_search";

	//SKILL
	public static final int SKILL_FIRST_TASK_ID = 12;

	//SPEED UP
	public static final int[] SPEED_UP__SECONDS = { 1, 300, 3600, 86400,
			604800, 157852800 };
	public static final int[] SPEED_UP__NUM_GEMS = { 1, 2, 60, 720, 4200, 1096200 };

	public static final int STRUCTURE__LAB_ID = 800;

	//STRUCTURE FOR USER STUFF
	public static final int STRUCTURE_FOR_USER__TOWN_HALL_ID = 120;
	public static final int STRUCTURE_FOR_USER__CASH_STORAGE_ID = 20;
	public static final int STRUCTURE_FOR_USER__OIL_STORAGE_ID = 60;
	public static final float STRUCTURE_FOR_USER__TOWN_HALL_X_COORD = 10F;
	public static final float STRUCTURE_FOR_USER__TOWN_HALL_Y_COORD = 10F;
	public static final float STRUCTURE_FOR_USER__CASH_STORAGE_X_COORD = 13F;
	public static final float STRUCTURE_FOR_USER__CASH_STORAGE_Y_COORD = 10F;
	public static final float STRUCTURE_FOR_USER__OIL_STORAGE_X_COORD = 10F;
	public static final float STRUCTURE_FOR_USER__OIL_STORAGE_Y_COORD = 13F;

	public static final int TANGO__INVITE_TANGO_FRIENDS_MAX_GEM_REWARD = 3;
	public static final int TANGO__INVITE_TANGO_FRIENDS_MIN_GEM_REWARD = 1;

	public static final String TRANSLATION_SETTINGS__DEFAULT_LANGUAGE = "ENGLISH";
	public static final boolean TRANSLATION_SETTINGS__DEFAULT_TRANSLATION_ON = false;

	//TUTORIAL CONSTANTS
	//MONSTER IDS 1 AND 3
	//the monster id that is used in tutorial
	public static final int TUTORIAL__STARTING_MONSTER_ID = 2011;
	//the monster id the user gets to use after tutorial
	public static final int TUTORIAL__USER_STARTING_MONSTER_ID = 37;
	public static final int TUTORIAL__GUIDE_MONSTER_ID = 2012;
	public static final int TUTORIAL__ENEMY_MONSTER_ID_ONE = 2010;
	public static final int TUTORIAL__ENEMY_MONSTER_ID_TWO = 1100;
	public static final int TUTORIAL__ENEMY_BOSS_MONSTER_ID = 2001;
	public static final int TUTORIAL__MARK_Z_MONSTER_ID = 2005;
	public static final int TUTORIAL__ENHANCE_GUIDE_MONSTER_ID = 1750;

	//everything at index i goes together
	public static final int[] TUTORIAL__EXISTING_BUILDING_IDS = { 200, 400,
			600, 700, 825, /*170,*/1000 };
	public static final float[] TUTORIAL__EXISTING_BUILDING_X_POS = { 24F, 12F,
			18F, 18F, 24F, /*15F,*/18F };
	public static final float[] TUTORIAL__EXISTING_BUILDING_Y_POS = { 11F, 16F,
			11F, 26F, 16F, /*-5F,*/17F };
	public static final Integer[] TUTORIAL__STRUCTURE_IDS_TO_BUILD = { 1, 100,
			300 };

	public static final int TUTORIAL__CITY_ONE_ID = 1;
	public static final int TUTORIAL__CITY_ONE_ASSET_NUM_FOR_FIRST_DUNGEON = 5;
	public static final int TUTORIAL__CITY_ONE_ASSET_NUM_FOR_SECOND_DUNGEON = 6;
	public static final int TUTORIAL__INIT_CASH = 2500;
	public static final int TUTORIAL__INIT_OIL = 2500;
	public static final int TUTORIAL__INIT_GEMS = 100;
	public static final int TUTORIAL__GACHA_CREDITS = 500;
	public static final int TUTORIAL__INIT_RANK = 100;
	public static final int[] TUTORIAL__INIT_OBSTACLE_ID = { 2,2,4,4,4,4,4,4,5,5,5,11,12,12,13,14,15,15,16,16,21,22,31,31,32 };
	public static final int[] TUTORIAL__INIT_OBSTACLE_X = { 33,28,30,9,32,6,25,3,26,18,32,33,33,4,23,13,7,29,22,20,4,33,14,11,10 };
	public static final int[] TUTORIAL__INIT_OBSTACLE_Y = { 5,29,10,33,32,6,24,17,3,24,26,15,21,12,31,3,21,21,6,24,28,18,29,11,25 };

	//USER CURRENCY HISTORY REASON FOR CHANGE     VALUES
	public static final String UCHRFC__ACHIEVEMENT_REDEEM = "achievement redeemed";
	public static final String UCHRFC__CREATE_CLAN = "created clan";
	public static final String UCHRFC__CREATING_BATTLE_ITEMS = "creating battle items";
	public static final String UCHRFC__CURRENCY_EXCHANGE = "currency exchange";
	public static final String UCHRFC__EARN_FREE_DIAMONDS_FB_CONNECT = "connecting to facebook";
	public static final String UCHRFC__END_PERSISTENT_EVENT_COOLDOWN = "ended persistent event cooldown";
	public static final String UCHRFC__END_TASK = "end task";
	public static final String UCHRFC__ENHANCING = "enhancing user monsters";
	public static final String UCHRFC__EVOLVING = "evolving user monsters";
	public static final String UCHRFC__EXPANSION_WAIT_COMPLETE = "expansion wait complete: ";
	public static final String UCHRFC__HEAL_MONSTER_OR_SPED_UP_HEALING = "healing or sped up healing user monsters";
	public static final String UCHRFC__IN_APP_PURCHASE = "in app purchase: ";
	public static final String UCHRFC__IN_APP_PURCHASE_MONEY_TREE = "in app purchase: money tree";
	public static final String UCHRFC__IN_APP_PURCHASE_STARTER_PACK = "in app purchase: starter pack";
	public static final String UCHRFC__IN_APP_PURCHASE_SALES_PACK = "in app purchase: sales pack";
	public static final String UCHRFC__INCREASE_MONSTER_INVENTORY = "increased user monster inventory";
	public static final String UCHRFC__PERFORMING_RESEARCH = "performing research";
	public static final String UCHRFC__PURCHASE_NORM_STRUCT = "purchased norm struct";
	public static final String UCHRFC__PURHCASED_BOOSTER_PACK = "purchased booster pack";
	public static final String UCHRFC__PURCHASE_CITY_EXPANSION = "expanded city";
	public static final String UCHRFC__PVP_BATTLE = "pvp battle";
	public static final String UCHRFC__QUEST_REDEEM = "quest redeemed";
	public static final String UCHRFC__REMOVE_OBSTACLE = "remove obstacle";
	public static final String UCHRFC__RESET_MINI_JOB = "reset_mini_job";
	public static final String UCHRFC__REVIVE_IN_DUNGEON = "revive in dungeon";
	public static final String UCHRFC__SOLD_USER_MONSTERS = "sold user monsters";
	public static final String UCHRFC__SOLICIT_CLAN_MEMBER_TEAM_DONATION = "solicit clan member team donation";
	public static final String UCHRFC__SPED_UP_COMBINING_MONSTER = "sped up combining user monster";
	public static final String UCHRFC__SPED_UP_COMPLETE_BATTLE_ITEMS = "sped up complete battle items";
	public static final String UCHRFC__SPED_UP_COMPLETE_MINI_JOB = "sped up complete mini job";
	public static final String UCHRFC__SPED_UP_ENHANCING = "sped up enhancing user monster";
	public static final String UCHRFC__SPED_UP_EVOLUTION = "sped up evolving user monster";
	public static final String UCHRFC__SPED_UP_NORM_STRUCT = "sped up norm stuct";
	public static final String UCHRFC__SPED_UP_REMOVE_OBSTACLE = "sped up remove obstacle";
	public static final String UCHRFC__TANGO_GIFT = "tango gift";
	public static final String UCHRFC__TRADE_ITEM_FOR_RESOURCES = "trade item for resources";
	public static final String UCHRFC__TRADE_ITEM_FOR_SPEEDUP = "trade item for speedup";
	public static final String UCHRFC__UPGRADE_NORM_STRUCT = "upgrading norm struct";

	//old aoc constants
	public static final int NOT_SET = -1;

	//GENERATING LISTS OF ENEMIES
	public static final int NUM_MINUTES_SINCE_LAST_BATTLE_BEFORE_APPEARANCE_IN_ATTACK_LISTS = 10;

	public static final int DEFAULT_USER_EQUIP_LEVEL = 1;
	public static final int DEFAULT_USER_EQUIP_ENHANCEMENT_PERCENT = 0;
	public static final int DEFAULT_USER_EQUIP_DURABILITY = 100;
	public static final String UER__BOSS_ACTION = "boss action";
	public static final String UER__BATTLE = "battle";
	public static final String UER__COLLECT_EQUIP_ENHANCEMENT = "collect equip enhancement";
	public static final String UER__SUCCESSFUL_FORGE = "successful forge";
	public static final String UER__UNSUCCESSFUL_FORGE = "unsuccessful forge";
	public static final String UER__PICK_LOCK_BOX = "pick lock box";
	public static final String UER__THREE_CARD_MONTE = "three card monte";

	public static final String UER__PURCHASE_FROM_MARKETPLACE = "purchase from marketplace";
	public static final String UER__QUEST_REDEEM = "quest redeem";
	public static final String UER__REDEEM_USER_CITY_GEMS = "redeem user city gems";
	public static final String UER__REDEEM_USER_LOCK_BOX_ITEMS = "redeem user lock box items";
	public static final String UER__DAILY_BONUS_REWARD = "daily bonus reward";
	public static final String UER__RETRACT_MARKETPLACE_POST = "retract_marketplace_post";
	public static final String UER__TASK_ACTION = "task action";
	public static final String UER__USER_CREATED = "user created";
	//--------------------------------------------------------------------------------------------------------------------------

	//FORMULA CONSTANTS (ALSO) SENT TO CLIENT
	public static final double BATTLE_WEIGHT_GIVEN_TO_ATTACK_STAT = 1;
	public static final double BATTLE_WEIGHT_GIVEN_TO_ATTACK_EQUIP_SUM = 1;
	public static final double BATTLE_WEIGHT_GIVEN_TO_DEFENSE_STAT = 1;
	public static final double BATTLE_WEIGHT_GIVEN_TO_DEFENSE_EQUIP_SUM = 1;
	public static final double BATTLE_WEIGHT_GIVEN_TO_LEVEL = 1;
	public static final float BATTLE_LOCATION_BAR_MAX = 83.33f;
	public static final float BATTLE_PERFECT_PERCENT_THRESHOLD = 3.0f;
	public static final float BATTLE_GREAT_PERCENT_THRESHOLD = 17.0f;
	public static final float BATTLE_GOOD_PERCENT_THRESHOLD = 38.0f;
	public static final float BATTLE_PERFECT_MULTIPLIER = 2.0f;
	public static final float BATTLE_GREAT_MULTIPLIER = 1.5f;
	public static final float BATTLE_GOOD_MULTIPLIER = 1.0f;
	public static final float BATTLE_IMBALANCE_PERCENT = .67f;
	public static final float BATTLE_PERFECT_LIKELIHOOD = .25f;
	public static final float BATTLE_GREAT_LIKELIHOOD = .55f;
	public static final float BATTLE_GOOD_LIKELIHOOD = .15f;
	public static final float BATTLE_MISS_LIKELIHOOD = .05f;

	public static final double BATTLE__HIT_ATTACKER_PERCENT_OF_HEALTH = 0.2;
	public static final double BATTLE__HIT_DEFENDER_PERCENT_OF_HEALTH = 0.25;
	public static final double BATTLE__PERCENT_OF_WEAPON = 1.0 / 9.0;
	public static final double BATTLE__PERCENT_OF_ARMOR = 1.0 / 9.0;
	public static final double BATTLE__PERCENT_OF_AMULET = 1.0 / 9.0;
	public static final double BATTLE__PERCENT_OF_PLAYER_STATS = 3.0 / 9.0;
	public static final double BATTLE__ATTACK_EXPO_MULTIPLIER = 0.8;
	public static final double BATTLE__PERCENT_OF_EQUIPMENT = 3.0 / 9.0;
	public static final double BATTLE__INDIVIDUAL_EQUIP_ATTACK_CAP = 5.0;
	public static final double BATTLE__FAKE_PLAYER_COIN_GAIN_MULTIPLIER = 3;
	public static final double BATTLE__CHANCE_OF_ZERO_GAIN_FOR_SILVER = .2;
	public static final int BATTLE__ELO_DISTANCE_ONE = 100;
	public static final int BATTLE__ELO_DISTANCE_TWO = 200;
	public static final int BATTLE__ELO_DISTANCE_THREE = 300;
	public static final int BATTLE__ELO_USER_LIMIT_ONE = 68;
	public static final int BATTLE__ELO_USER_LIMIT_TWO = 13;
	public static final int BATTLE__ELO_USER_LIMIT_THREE = 3;
	public static final int BATTLE__LAST_VIEWED_TIME_MILLIS_ADDEND = 600000; //10 MINUTES

	//--------------------------------------------------------------------------------------------------------------------------

	//TUTORIAL CONSTANTS
	public static final double CHARACTERS_ATTACK_DEFENSE_VARIABILITY = 0.67;
	public static final int TUTORIAL__INIT_COINS = 50;
	public static final int TUTORIAL__DIAMOND_COST_TO_INSTABUILD_FIRST_STRUCT = 2; //Because it does not warn the user
	public static final String TUTORIAL__FAKE_QUEST_GOOD_NAME = "Preserve the Peace";
	public static final String TUTORIAL__FAKE_QUEST_BAD_NAME = "Witness Protection";
	public static final String TUTORIAL__FAKE_QUEST_GOOD_ACCEPT_DIALOGUE = "10~good~";
	public static final String TUTORIAL__FAKE_QUEST_BAD_ACCEPT_DIALOGUE = "10~bad~";
	public static final String TUTORIAL__FAKE_QUEST_GOOD_DESCRIPTION = "Soldier, we are in dire times and we need your help.";
	public static final String TUTORIAL__FAKE_QUEST_BAD_DESCRIPTION = "Soldier, we are in dire times and we need your help.";
	public static final String TUTORIAL__FAKE_QUEST_GOOD_DONE_RESPONSE = "Simply amazing! Your battle prowess makes our village seem safer already. ";
	public static final String TUTORIAL__FAKE_QUEST_BAD_DONE_RESPONSE = "Excellent work soldier. Good to know I have a competent ally watching my back.";
	public static final int TUTORIAL__FIRST_TASK_ID = 1;
	//in development select any task, doesn't matter for now
	public static final int TUTORIAL__FAKE_QUEST_TASK_ID = Globals.IS_SANDBOX() ? 1
			: 168;
	public static final int TUTORIAL__FAKE_QUEST_ASSET_NUM_WITHIN_CITY = 0;
	public static final int TUTORIAL__FAKE_QUEST_COINS_GAINED = 8;
	public static final int TUTORIAL__FAKE_QUEST_EXP_GAINED = 4;
	public static final int TUTORIAL__FAKE_QUEST_AMULET_LOOT_EQUIP_ID = 5;
	public static final int TUTORIAL__FIRST_BATTLE_COIN_GAIN = 5;
	public static final int TUTORIAL__FIRST_BATTLE_EXP_GAIN = 1;
	public static final int TUTORIAL__FIRST_STRUCT_TO_BUILD = 1;
	public static final int TUTORIAL__FIRST_NEUTRAL_CITY_ID = 1;
	public static final int TUTORIAL__COST_TO_SPEED_UP_FORGE = 2;

	//STARTUP
	public static final int STARTUP__MAX_NUM_OF_STARTUP_NOTIFICATION_TYPE_TO_SEND = 20;
	//  public static final int STARTUP__HOURS_OF_BATTLE_NOTIFICATIONS_TO_SEND = 24*2;
	public static final int STARTUP__DAILY_BONUS_MAX_CONSECUTIVE_DAYS = 5;
	//public static final int STARTUP__DAILY_BONUS_TIME_REQ_BETWEEN_CONSEC_DAYS = 1; //in days
	//public static final int STARTUP__DAILY_BONUS_SMALL_BONUS_COIN_QUANTITY = 2;
	//public static final int STARTUP__DAILY_BONUS_MIN_CONSEC_DAYS_SMALL_BONUS = 1;
	//public static final int STARTUP__DAILY_BONUS_MIN_CONSEC_DAYS_BIG_BONUS = 5;
	//public static final int STARTUP__DAILY_BONUS_MAX_CONSEC_DAYS_BIG_BONUS = 5;
	//public static final double STARTUP__DAILY_BONUS_PERCENTAGE_CHANCE_COMMON_EQUIP = 0.1;    //total should add up to 1
	//public static final double STARTUP__DAILY_BONUS_PERCENTAGE_CHANCE_UNCOMMON_EQUIP = 0.85;
	//public static final double STARTUP__DAILY_BONUS_PERCENTAGE_CHANCE_RARE_EQUIP = 0;
	//public static final double STARTUP__DAILY_BONUS_PERCENTAGE_CHANCE_EPIC_EQUIP = 0.05;
	//public static final double STARTUP__DAILY_BONUS_PERCENTAGE_CHANCE_LEGENDARY_EQUIP = 0;
	//public static final int STARTUP__DAILY_BONUS_RECEIVE_EQUIP_LEVEL_RANGE = 5;
	//public static final int STARTUP__DAILY_BONUS_MYSTERY_BOX_EQUIP_FORGE_LEVEL_MAX = 2;
	public static final int STARTUP__LEADERBOARD_MIN_LEVEL = 1;
	public static final int STARTUP__ENHANCING_MIN_LEVEL_TO_UNLOCK = 20;
	//if development then use user with id = 1
	//TODO: GET THE REAL IDS
	public static final String STARTUP__ADMIN_CHAT_USER_ID = Globals
			.IS_SANDBOX() ? "96db181e-4b63-40c4-8746-d62114f587a2"
			: "4df84fdd-705f-4089-ac80-70c1a0143f24";//Globals.IS_SANDBOX() ? 98437 : 131287;
	public static final int STARTUP__MAX_PRIVATE_CHAT_POSTS_SENT = 150;
	public static final int STARTUP__MAX_PRIVATE_CHAT_POSTS_RECEIVED = 150;
	public static final int STARTUP__TASK_ID_FOR_UPGRADE_TUTORIAL = 6;

	//BATTLE
	public static final int BATTLE__MAX_ITEMS_USED = 4;   //unused right now
	public static final int BATTLE__MAX_LEVEL_DIFFERENCE = 3;
	public static final double BATTLE__A = .2;		//must be <= 1
	public static final double BATTLE__B = 80;
	public static final int BATTLE__MIN_COINS_FROM_WIN = 5;
	public static final double BATTLE__EXP_BASE_MULTIPLIER = 0.8;
	public static final int BATTLE__EXP_MIN = 1;
	public static final double BATTLE__EXP_LEVEL_DIFF_WEIGHT = 0.2;
	public static final double BATTLE__CHANCE_OF_EQUIP_LOOT_INITIAL_WALL = Globals
			.IS_SANDBOX() ? 0.5 : 0.15;
	public static final double BATTLE__EQUIP_AND_STATS_WEIGHT = 1.08;
	public static final double BATTLE__MIN_LEVEL_TO_NOT_DISPLAY_BOTS_IN_ATTACK_LIST = 30;

	//TASK ACTION
	//  public static final int TASK_ACTION__MAX_CITY_RANK = 5;

	//PURCHASE NORM STRUCTURE
	public static final int PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE = 3;

	//UPGRADE NORM STRUCTURE
	public static final int UPGRADE_NORM_STRUCTURE__MAX_STRUCT_LEVEL = 5;

	//SELL NORM STRUCTURE
	public static final double SELL_NORM_STRUCTURE__PERCENT_RETURNED_TO_USER = .2;

	//EARN FREE DIAMONDS
	public static final int EARN_FREE_DIAMONDS__FB_CONNECT_REWARD = 10;

	//USER CREATE
	public static final int USER_CREATE__START_LEVEL = 1;
	public static final int USER_CREATE__MIN_NAME_LENGTH = 1;
	public static final int USER_CREATE__MAX_NAME_LENGTH = 15;
	public static final int USER_CREATE__MIN_COIN_REWARD_FOR_REFERRER = 100;
	public static final int USER_CREATE__COIN_REWARD_FOR_BEING_REFERRED = 50;
	public static final double USER_CREATE__PERCENTAGE_OF_COIN_WEALTH_GIVEN_TO_REFERRER = .2;
	public static final String USER_CREATE__ID_OF_POSTER_OF_FIRST_WALL = Globals
			.IS_SANDBOX() ? "1" : "1";
	public static final String USER_CREATE__FIRST_WALL_POST_TEXT = "Hi! My name's "
			+ (Globals.KABAM_ENABLED() ? "Stevie" : "Andrew")
			+ ", one of the creators of this game. Feel free to message me if you need any help.";
	public static final int USER_CREATE__INITIAL_GLOBAL_CHATS = 10;

	//LEVEL UP
	public static final int LEVEL_UP__MAX_LEVEL_FOR_USER = 100; //add level up equipment for fake players if increasing
	//  public static final double LEVEL_UP_HEALTH_GAINED = 5.0;
	public static final double LEVEL_UP_ATTACK_GAINED = 2.0;
	public static final double LEVEL_UP_DEFENSE_GAINED = 2.0;

	//LEVEL UP EQUIPMENT FOR FAKE PLAYERS (levels 1-30 must add more if going above level 30)

	//CHARACTER MOD
	public static final int CHARACTER_MOD__DIAMOND_COST_OF_CHANGE_NAME = 50;

	//LEADERBOARD
	public static final int LEADERBOARD__MIN_BATTLES_REQUIRED_FOR_KDR_CONSIDERATION = 100;
	public static final int LEADERBOARD__MAX_PLAYERS_SENT_AT_ONCE = 15;
	public static final int TOURNAMENT_EVENT__MAX_PLAYERS_SENT_AT_ONCE = 200;

	//SEND GROUP CHAT
	public static final int SEND_GROUP_CHAT__MAX_LENGTH_OF_CHAT_STRING = 200;

	//CREATE CLAN
	public static final int CREATE_CLAN__COIN_PRICE_TO_CREATE_CLAN = 10000;
	public static final int CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_NAME = 15;
	public static final int CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_DESCRIPTION = 350;
	public static final int CREATE_CLAN__MAX_CHAR_LENGTH_FOR_CLAN_TAG = 3;
	public static final int RETRIEVE_CLANS__NUM_CLANS_CAP = 50;
	public static final String CLAN__CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT = Globals
			.IS_SANDBOX() ? "967" : "0";
	public static final int CLAN__ALLIANCE_LEGION_LIMIT_TO_RETRIEVE_FROM_DB = 50;

	//TIME BEFORE RESHOWING MENUS
	public static final int NUM_HOURS_BEFORE_RESHOWING_GOLD_SALE = 24;
	public static final int NUM_HOURS_BEFORE_RESHOWING_LOCK_BOX = 24;
	public static final int LEVEL_TO_SHOW_RATE_US_POPUP = 8;

	// GOLD SALE NEW USERS
	public static final int NUM_DAYS_FOR_NEW_USER_GOLD_SALE = 3;
	public static final String GOLD_SHOPPE_IMAGE_NAME_NEW_USER_GOLD_SALE = "BeginnerSaleSign.png";
	public static final String GOLD_BAR_IMAGE_NAME_NEW_USER_GOLD_SALE = "BeginnerSale.png";
	public static final int NUM_BEGINNER_SALES_ALLOWED = 2;

	//LEADERBOARD EVENT
	public static final int TOURNAMENT_EVENT__WINS_WEIGHT = 2;
	public static final int TOURNAMENT_EVENT__LOSSES_WEIGHT = -1;
	public static final int TOURNAMENT_EVENT__FLEES_WEIGHT = -3;
	public static final int TOURNAMENT_EVENT__NUM_HOURS_TO_SHOW_AFTER_EVENT_END = 24;

	public static final String UCHRFC__USER_CREATED = "user created";
	public static final String UCHRFC__LEADERBOARD = "leaderboard event";
	public static final String UCHRFC__CLAN_TOWER_WAR_ENDED = "clan tower war ended";
	public static final String UCHRFC__SHORT_MARKET_PLACE_LICENSE = "purchased short market place license";
	public static final String UCHRFC__LONG_MARKET_PLACE_LICENSE = "purchased long market place license";
	public static final String UCHRFC__GROUP_CHAT = "purchased group chat"; //is controller for this even used?
	public static final String UCHRFC__BOSS_ACTION = "boss action";
	public static final String UCHRFC__REFILL_STAT = "refilled stat: ";
	public static final String UCHRFC__SELL_NORM_STRUCT = "sell norm struct";
	public static final String UCHRFC__REDEEM_MARKETPLACE_EARNINGS = "redeemed marketplace earnings";
	public static final String UCHRFC__PICK_LOCKBOX = "picked lockbox";
	public static final String UCHRFC__RETRACT_MARKETPLACE_POST = "retract marketplace post";
	public static final String UCHRFC__PLAY_THREE_CARD_MONTE = "played three card monte";
	//public static final String UCHRFC__SOLD_ITEM_ON_MARKETPLACE = "sold item on marketplace"; //user's currency change is 0
	public static final String UCHRFC__PURCHASED_FROM_MARKETPLACE = "purchased from marketplace";
	public static final String UCHRFC__SUBMIT_EQUIPS_TO_BLACKSMITH = "submit equips to blacksmith";
	public static final String UCHRFC__FINISH_FORGE_ATTEMPT_WAIT_TIME = "finish forge attempt wait time";
	public static final String UCHRFC__ARMORY_TRANSACTION = "armory transaction";
	public static final String UCHRFC__UPGRADE_CLAN_TIER_LEVEL = "upgraded clan tier level";
	public static final String UCHRFC__EARN_FREE_DIAMONDS_KIIP = "kiip";
	public static final String UCHRFC__EARN_FREE_DIAMONDS_ADCOLONY = "adcolony";
	public static final String UCHRFC__CHARACTER_MOD_TYPE = "character type, class";
	public static final String UCHRFC__CHARACTER_MOD_NAME = "character name";
	public static final String UCHRFC__CHARACTER_MOD_RESET = "character reset";
	public static final String UCHRFC__CHARACTER_MOD_SKILL_POINTS = "character skill points";
	public static final String UCHRFC__GOLDMINE = "goldmine reset";
	public static final String UCHRFC__COLLECT_GOLDMINE = "collect from goldmine";
	public static final String UCHRFC__PURCHASED_ADDITIONAL_FORGE_SLOTS = "purchased additional forge slots";
	//silver only reasons
	public static final String UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT = "retrieve currency from normal structures";
	public static final String UCHRFC__RETRIEVE_CURRENCY_FROM_MONEY_TREE = "retrieve currency from money tree";
	public static final String UCHRFC__TASK_ACTION = "performed task with id ";
	public static final String UCHRFC__STARTUP_DAILY_BONUS = "startup daily bonus";
	public static final String UCHRFC__USER_CREATE_REFERRED_A_USER = "referred a user";
	public static final String UCHRFC__BATTLE_WON = "won battle";
	public static final String UCHRFC__BATTLE_LOST = "lost battle";

	//ENHANCING
	public static final int MAX_ENHANCEMENT_LEVEL = 5;
	public static final int ENHANCEMENT__PERCENTAGE_PER_LEVEL = 10000;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_A = 0.f;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_B = 0.f;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_C = 1;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_D = 0.1f;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_E = 1.5f;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_F = 2.1f;
	public static final float ENHANCEMENT__TIME_FORMULA_CONSTANT_G = 1.365f;
	public static final float ENHANCEMENT__PERCENT_FORMULA_CONSTANT_A = 0.75f;
	public static final float ENHANCEMENT__PERCENT_FORMULA_CONSTANT_B = 2.f;
	public static final float ENHANCEMENT__ENHANCE_LEVEL_EXPONENT_BASE = 1.2f;
	public static final int ENHANCEMENT__DEFAULT_SECONDS_TO_ENHANCE = 5;
	public static final double ENHANCEMENT__COST_CONSTANT = 12.5;

	//BOOSTER PACKS
	//amount of booster packs user can buy at one time
	public static final String BOOSTER_PACK__INFO_IMAGE_NAME = "howchestswork.png";

	// MAP
	public static final String TASK_MAP__SECTION_IMAGE_PREFIX = "mapsection";
	public static final int TASK_MAP__NUMBER_OF_SECTIONS = 8;
	public static final float TASK_MAP__SECTION_HEIGHT = 328;
	public static final float TASK_MAP__TOTAL_WIDTH = 328;
	public static final float TASK_MAP__TOTAL_HEIGHT = 2571;

	public static final AnimatedSpriteOffset[] STARTUP__ANIMATED_SPRITE_OFFSETS = {
			new AnimatedSpriteOffset("TutorialGuide", new CoordinatePair(0, -5)),
			new AnimatedSpriteOffset("TutorialGuideBad", new CoordinatePair(0,
					-7)),
			new AnimatedSpriteOffset("AllianceArcher",
					new CoordinatePair(0, -5)),
			new AnimatedSpriteOffset("AllianceWarrior", new CoordinatePair(0,
					-7)),
			new AnimatedSpriteOffset("AllianceMage", new CoordinatePair(0, -6)),
			new AnimatedSpriteOffset("LegionArcher", new CoordinatePair(0, -7)),
			new AnimatedSpriteOffset("LegionWarrior",
					new CoordinatePair(0, -11)),
			new AnimatedSpriteOffset("LegionMage", new CoordinatePair(0, -8)),
			new AnimatedSpriteOffset("Bandit", new CoordinatePair(0, -15)),
			new AnimatedSpriteOffset("FarmerMitch", new CoordinatePair(0, -8)),
			new AnimatedSpriteOffset("Carpenter", new CoordinatePair(0, -6)),
			new AnimatedSpriteOffset("Bandit", new CoordinatePair(0, -6)), };

	public static final String[] STARTUP__NOTICES_TO_PLAYERS = {
	//    "FREE limited edition gold equip for joining today!"
	//    "Forging Contest! 50 GOLD reward! Details at forum.lvl6.com"
	//      "We have just added 40+ equips, a new city, and increased the level cap!"
	"Happy birthday AoC! Buildings will make silver twice as fast all week long!" };

	public static final int STARTUP__QUEST_ID_FOR_FIRST_LOSS_TUTORIAL = 326;
	public static final String STARTUP__CREDITS_FILE_NAME = "FAQ.3.txt";
	public static final String STARTUP__FAQ_FILE_NAME = "FAQ.3.txt";

	public static final String NIB_NAME__LOCK_BOX = "LockBox.4";
	public static final String NIB_NAME__TRAVELING_MAP = "TravelingMap.4";
	public static final String NIB_NAME__EXPANSION = "Expansion.2";
	public static final String NIB_NAME__GOLD_SHOPPE = "GoldShoppe.4";

}
