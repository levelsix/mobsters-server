package com.lvl6.properties;

//SPECIFYING COLUMNS OF STATIC DATA TABLES UNNECESSARY
public class DBConstants {
	/* TABLENAMES*/
	public static final String TABLE_ACHIEVEMENT_CONFIG = "achievement_config";
	public static final String TABLE_ACHIEVEMENT_FOR_USER = "achievement_for_user";
	public static final String TABLE_ALERT_ON_STARTUP = "alert_on_startup";
	public static final String TABLE_BATTLE_ITEM_CONFIG = "battle_item_config";
	public static final String TABLE_BATTLE_ITEM_FOR_USER = "battle_item_for_user";
	public static final String TABLE_BATTLE_ITEM_QUEUE_FOR_USER = "battle_item_queue_for_user";
	public static final String TABLE_BOARD_CONFIG = "board_config";
	public static final String TABLE_BOARD_OBSTACLE_CONFIG = "board_obstacle_config";
	public static final String TABLE_BOARD_PROPERTY_CONFIG = "board_property_config";
	public static final String TABLE_BOOSTER_DISPLAY_ITEM_CONFIG = "booster_display_item_config";
	public static final String TABLE_BOOSTER_ITEM_CONFIG = "booster_item_config";
	public static final String TABLE_BOOSTER_PACK_CONFIG = "booster_pack_config";
	public static final String TABLE_BOOSTER_PACK_PURCHASE_HISTORY = "booster_pack_purchase_history";
	public static final String TABLE_CITY_CONFIG = "city_config";					//TODO: delete
	public static final String TABLE_CITY_ELEMENT_CONFIG = "city_element_config";	//TODO: delete
	public static final String TABLE_CLANS = "clan";
	public static final String TABLE_CLAN_AVENGE = "clan_avenge";
	public static final String TABLE_CLAN_AVENGE_USER = "clan_avenge_user";
	public static final String TABLE_CLAN_CHAT_POST = "clan_chat_post";
	public static final String TABLE_CEPFU_RAID_HISTORY = "cepfu_raid_history";	//TABLE CONTAINING CLAN SUMMARIZED DATA REGARDING CLAN EVENTS FOR A USER
	public static final String TABLE_CEPFU_RAID_STAGE_HISTORY = "cepfu_raid_stage_history";
	public static final String TABLE_CEPFU_RAID_STAGE_MONSTER_HISTORY = "cepfu_raid_stage_monster_history";
	public static final String TABLE_CHAT_TRANSLATIONS = "chat_translations";
	public static final String TABLE_CLAN_EVENT_PERSISTENT_CONFIG = "clan_event_persistent_config";//EVENT TABLE FOR CLANS
	public static final String TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN = "clan_event_persistent_for_clan";	//TABLE CONTAINING CLAN SPECIFIC DATA REGARDING CLAN EVENTS
	public static final String TABLE_CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY = "clan_event_persistent_for_clan_history";//HISTORY TABLE CONTAINING CLAN SPECIFIC DATA REGARDING CLAN EVENTS
	public static final String TABLE_CLAN_EVENT_PERSISTENT_FOR_USER = "clan_event_persistent_for_user";	//TABLE CONTAINING CLAN SPECIFIC DATA REGARDING CLAN EVENTS FOR A USER
	public static final String TABLE_CLAN_EVENT_PERSISTENT_USER_REWARD = "clan_event_persistent_user_reward";	//THE REWARDS A USER GETS FOR DOING A CLAN RAID
	public static final String TABLE_CLAN_FOR_USER = "clan_for_user";
	public static final String TABLE_CLAN_HELP = "clan_help";
	public static final String TABLE_CLAN_HELP_COUNT_FOR_USER = "clan_help_count_for_user";
	public static final String TABLE_CLAN_ICON_CONFIG = "clan_icon_config";
	public static final String TABLE_CLAN_INVITE = "clan_invite";
	public static final String TABLE_CLAN_MEMBER_TEAM_DONATION = "clan_member_team_donation";
	public static final String TABLE_CLAN_RAID_CONFIG = "clan_raid_config";	//TABLE SPECIFYING WHAT RAIDS THERE ARE (ALSO REFERENCED IN  TABLE_CLAN_EVENT_PERSISTENT)
	public static final String TABLE_CLAN_RAID_STAGE_CONFIG = "clan_raid_stage_config";
	public static final String TABLE_CLAN_RAID_STAGE_MONSTER_CONFIG = "clan_raid_stage_monster_config";
	public static final String TABLE_CLAN_RAID_STAGE_REWARD_CONFIG = "clan_raid_stage_reward_config";
	public static final String TABLE_CUSTOM_MENU_CONFIG = "custom_menu_config";
	public static final String TABLE_EVENT_PERSISTENT_CONFIG = "event_persistent_config";
	public static final String TABLE_EVENT_PERSISTENT_FOR_USER = "event_persistent_for_user";
	public static final String TABLE_EXPANSION_COST_CONFIG = "expansion_cost_config";				//TODO: delete
	public static final String TABLE_EXPANSION_PURCHASE_FOR_USER = "expansion_purchase_for_user";	//TODO: delete
	public static final String TABLE_FILE_DOWNLOAD_CONFIG = "file_download_config";
	public static final String TABLE_GOLD_SALE_CONFIG = "gold_sale_config";							//TODO: delete
	public static final String TABLE_IAP_HISTORY = "iap_history";
	public static final String TABLE_ITEM_CONFIG = "item_config";
	public static final String TABLE_ITEM_FOR_USER = "item_for_user";
	public static final String TABLE_ITEM_FOR_USER_USAGE = "item_for_user_usage";
	public static final String TABLE_ITEM_SECRET_GIFT_FOR_USER = "item_secret_gift_for_user";
	public static final String TABLE_LOCK_BOX_EVENT_CONFIG = "lock_box_event_config";			//TODO: delete
	public static final String TABLE_LOCK_BOX_EVENT_FOR_USER = "lock_box_event_for_user";		//TODO: delete
	public static final String TABLE_LOCK_BOX_ITEM_CONFIG = "lock_box_item_config";				//TODO: delete
	public static final String TABLE_LOGIN_HISTORY = "login_history";
	public static final String TABLE_MINI_EVENT_CONFIG = "mini_event_config";
	public static final String TABLE_MINI_EVENT_FOR_PLAYER_LVL_CONFIG = "mini_event_for_player_lvl_config";
	public static final String TABLE_MINI_EVENT_FOR_USER = "mini_event_for_user";
	public static final String TABLE_MINI_EVENT_GOAL_CONFIG = "mini_event_goal_config";
	public static final String TABLE_MINI_EVENT_GOAL_FOR_USER = "mini_event_goal_for_user";
	public static final String TABLE_MINI_EVENT_LEADERBOARD_REWARD_CONFIG = "mini_event_leaderboard_reward_config";
	public static final String TABLE_MINI_EVENT_TIER_REWARD_CONFIG = "mini_event_tier_reward_config";
	public static final String TABLE_MINI_JOB_CONFIG = "mini_job_config";
	public static final String TABLE_MINI_JOB_FOR_USER = "mini_job_for_user";
	public static final String TABLE_MINI_JOB_FOR_USER_HISTORY = "mini_job_for_user_history";
	public static final String TABLE_MINI_JOB_REFRESH_ITEM_CONFIG = "mini_job_refresh_item_config";
	public static final String TABLE_MONSTER_CONFIG = "monster_config";
	public static final String TABLE_MONSTER_BATTLE_DIALOGUE_CONFIG = "monster_battle_dialogue_config";
	public static final String TABLE_MONSTER_ENHANCING_FOR_USER = "monster_enhancing_for_user";
	public static final String TABLE_MONSTER_ENHANCING_HISTORY = "monster_enhancing_history";
	public static final String TABLE_MONSTER_EVOLVING_FOR_USER = "monster_evolving_for_user";
	public static final String TABLE_MONSTER_EVOLVING_HISTORY = "monster_evolving_history";
	public static final String TABLE_MONSTER_FOR_PVP_CONFIG = "monster_for_pvp_config";
	public static final String TABLE_MONSTER_FOR_USER = "monster_for_user";
	public static final String TABLE_MONSTER_FOR_USER_DELETED = "monster_for_user_deleted";
	public static final String TABLE_MONSTER_HEALING_FOR_USER = "monster_healing_for_user";
	public static final String TABLE_MONSTER_HEALING_HISTORY = "monster_healing_history";
	public static final String TABLE_MONSTER_LEVEL_INFO_CONFIG = "monster_level_info_config";
	public static final String TABLE_MONSTER_SNAPSHOT_FOR_USER = "monster_snapshot_for_user";
	public static final String TABLE_OBSTACLE_CONFIG = "obstacle_config";
	public static final String TABLE_OBSTACLE_FOR_USER = "obstacle_for_user";
	public static final String TABLE_PREREQUISITE_CONFIG = "prerequisite_config";
	public static final String TABLE_PROFANITY_CONFIG = "profanity_config";
	public static final String TABLE_PVP_BATTLE_FOR_USER = "pvp_battle_for_user";
	public static final String TABLE_PVP_BATTLE_HISTORY = "pvp_battle_history";
	public static final String TABLE_PVP_BOARD_OBSTACLE_FOR_USER = "pvp_board_obstacle_for_user";
	public static final String TABLE_PVP_LEAGUE_CONFIG = "pvp_league_config";
	public static final String TABLE_PVP_LEAGUE_FOR_USER = "pvp_league_for_user";
	public static final String TABLE_QUEST_CONFIG = "quest_config";
	public static final String TABLE_QUEST_FOR_USER = "quest_for_user";
	public static final String TABLE_QUEST_JOB_CONFIG = "quest_job_config";
	public static final String TABLE_QUEST_JOB_FOR_USER = "quest_job_for_user";
	public static final String TABLE_QUEST_JOB_MONSTER_ITEM_CONFIG = "quest_job_monster_item_config";
	public static final String TABLE_REFERRAL = "referral";			//TODO: delete
	public static final String TABLE_REFERRAL_CODE_AVAILABLE_CONFIG = "referral_code_available_config";	//TODO: delete
	public static final String TABLE_REFERRAL_CODE_GENERATED_CONFIG = "referral_code_generated_config";	//TODO: delete
	public static final String TABLE_RESEARCH_CONFIG = "research_config";
	public static final String TABLE_RESEARCH_PROPERTY_CONFIG = "research_property_config";
	public static final String TABLE_RESEARCH_FOR_USER = "research_for_user";
	public static final String TABLE_SALES_DISPLAY_ITEM_CONFIG = "sales_display_item_config";
	public static final String TABLE_SALES_ITEM_CONFIG = "sales_item_config";
	public static final String TABLE_SALES_PACKAGE_CONFIG = "sales_package_config";
	public static final String TABLE_REWARD_CONFIG = "reward_config";
	public static final String TABLE_SERVER_TOGGLE_CONFIG = "server_toggle_config";
	public static final String TABLE_SKILL_CONFIG = "skill_config";
	public static final String TABLE_SKILL_PROPERTY_CONFIG = "skill_property_config";
	public static final String TABLE_SKILL_SIDE_EFFECT_CONFIG = "skill_side_effect_config";
	public static final String TABLE_STATIC_LEVEL_INFO_CONFIG = "static_level_info_config";
	public static final String TABLE_STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG = "structure_battle_item_factory_config";
	public static final String TABLE_STRUCTURE_CONFIG = "structure_config";
	public static final String TABLE_STRUCTURE_CLAN_HOUSE_CONFIG = "structure_clan_house_config";
	public static final String TABLE_STRUCTURE_EVO_CHAMBER_CONFIG = "structure_evo_chamber_config";
	public static final String TABLE_STRUCTURE_FOR_USER = "structure_for_user";
	public static final String TABLE_STRUCTURE_HOSPITAL_CONFIG = "structure_hospital_config";
	public static final String TABLE_STRUCTURE_LAB_CONFIG = "structure_lab_config";
	public static final String TABLE_STRUCTURE_MINI_JOB_CONFIG = "structure_mini_job_config";
	public static final String TABLE_STRUCTURE_PVP_BOARD_CONFIG = "structure_pvp_board_config";
	public static final String TABLE_STRUCTURE_RESEARCH_HOUSE_CONFIG = "structure_research_house_config";
	public static final String TABLE_STRUCTURE_RESIDENCE_CONFIG = "structure_residence_config";
	public static final String TABLE_STRUCTURE_RESOURCE_GENERATOR_CONFIG = "structure_resource_generator_config";
	public static final String TABLE_STRUCTURE_RESOURCE_STORAGE_CONFIG = "structure_resource_storage_config";
	public static final String TABLE_STRUCTURE_TEAM_CENTER_CONFIG = "structure_team_center_config";
	public static final String TABLE_STRUCTURE_TOWN_HALL_CONFIG = "structure_town_hall_config";
	public static final String TABLE_STRUCTURE_MONEY_TREE_CONFIG = "structure_money_tree_config";
	public static final String TABLE_TASK_CONFIG = "task_config";
	public static final String TABLE_TASK_FOR_USER_CLIENT_STATE = "task_for_user_client_state";
	public static final String TABLE_TASK_FOR_USER_COMPLETED = "task_for_user_completed";
	public static final String TABLE_TASK_FOR_USER_ONGOING = "task_for_user_ongoing";
	public static final String TABLE_TASK_HISTORY = "task_history";
	public static final String TABLE_TASK_MAP_ELEMENT_CONFIG = "task_map_element_config";
	public static final String TABLE_TASK_STAGE_CONFIG = "task_stage_config";
	public static final String TABLE_TASK_STAGE_FOR_USER = "task_stage_for_user";
	public static final String TABLE_TASK_STAGE_HISTORY = "task_stage_history";
	public static final String TABLE_TASK_STAGE_MONSTER_CONFIG = "task_stage_monster_config";
	public static final String TABLE_TOURNAMENT_EVENT_CONFIG = "tournament_event_config";		//TODO: delete
	public static final String TABLE_TOURNAMENT_EVENT_FOR_USER = "tournament_event_for_user";	//TODO: delete
	public static final String TABLE_TOURNAMENT_REWARD_CONFIG = "tournament_reward_config";		//TODO: delete
	public static final String TABLE_TRANSLATION_SETTINGS_FOR_USER = "translation_settings_for_user";
	public static final String TABLE_USER = "user";
	public static final String TABLE_BANNED_USER = "user_banned";
	public static final String TABLE_USER_BEFORE_TUTORIAL_COMPLETION = "user_before_tutorial_completion";
	public static final String TABLE_USER_CURRENCY_HISTORY = "user_currency_history";
	public static final String TABLE_USER_FACEBOOK_INVITE_FOR_SLOT = "user_facebook_invite_for_slot";
	public static final String TABLE_USER_PRIVATE_CHAT_POST = "user_private_chat_post";
	public static final String TABLE_USER_SESSION = "user_session";

	/*COLUMNNAMES*/
	public static final String GENERIC__USER_ID = "user_id";
	public static final String GENERIC__ID = "id";

	/*ACHIEVEMENT FOR USER*/
	public static final String ACHIEVEMENT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String ACHIEVEMENT_FOR_USER__ACHIEVEMENT_ID = "achievement_id";
	public static final String ACHIEVEMENT_FOR_USER__PROGRESS = "progress";
	public static final String ACHIEVEMENT_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String ACHIEVEMENT_FOR_USER__IS_REDEEMED = "is_redeemed";
	public static final String ACHIEVEMENT_FOR_USER__TIME_COMPLETED = "time_completed";
	public static final String ACHIEVEMENT_FOR_USER__TIME_REDEEMED = "time_redeemed";

	/*AVAILABLE REFERRAL CODES*/
	public static final String AVAILABLE_REFERRAL_CODES__ID = GENERIC__ID;
	public static final String AVAILABLE_REFERRAL_CODES__CODE = "code";

	/*BATTLE ITEM FOR USER*/
	public static final String BATTLE_ITEM_FOR_USER__ID = GENERIC__ID;
	public static final String BATTLE_ITEM_FOR_USER__USER_ID = "user_id";
	public static final String BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID = "battle_item_id";
	public static final String BATTLE_ITEM_FOR_USER__QUANTITY = "quantity";

	/*BATTLE ITEM QUEUE FOR USER*/
	public static final String BATTLE_ITEM_QUEUE_FOR_USER__PRIORITY = "priority";
	public static final String BATTLE_ITEM_QUEUE_FOR_USER__USER_ID = "user_id";
	public static final String BATTLE_ITEM_QUEUE_FOR_USER__BATTLE_ITEM_ID = "battle_item_id";
	public static final String BATTLE_ITEM_QUEUE_FOR_USER__EXPECTED_START_TIME = "expected_start_time";
	public static final String BATTLE_ITEM_QUEUE_FOR_USER__ELAPSED_TIME = "elapsed_time";

	/*BOOSTER PACK PURCHASE HISTORY*/
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_PACK_ID = "booster_pack_id";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__TIME_OF_PURCHASE = "time_of_purchase";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__BOOSTER_ITEM_ID = "booster_item_id";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__MONSTER_ID = "monster_id";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__NUM_PIECES = "num_pieces";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__IS_COMPLETE = "is_complete";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__IS_SPECIAL = "is_special";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__GEM_REWARD = "gem_reward";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CASH_REWARD = "cash_reward";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String BOOSTER_PACK_PURCHASE_HISTORY__CHANGED_MONSTER_FOR_USER_IDS = "changed_monster_for_user_ids";

	/*CLANS*/
	public static final String CLANS__ID = "id";
	//  public static final String CLANS__OWNER_ID = "owner_id";
	public static final String CLANS__NAME = "name";
	public static final String CLANS__CREATE_TIME = "create_time";
	public static final String CLANS__DESCRIPTION = "description";
	public static final String CLANS__TAG = "tag";
	public static final String CLANS__REQUEST_TO_JOIN_REQUIRED = "request_to_join_required";
	public static final String CLANS__CLAN_ICON_ID = "clan_icon_id";

	/*CLAN AVENGE*/
	public static final String CLAN_AVENGE__ID = GENERIC__ID;
	public static final String CLAN_AVENGE__CLAN_ID = "clan_id";
	public static final String CLAN_AVENGE__ATTACKER_ID = "attacker_id";
	public static final String CLAN_AVENGE__DEFENDER_ID = "defender_id";
	public static final String CLAN_AVENGE__BATTLE_END_TIME = "battle_end_time";
	public static final String CLAN_AVENGE__AVENGE_REQUEST_TIME = "avenge_request_time";

	/*CLAN AVENGE USER*/
	public static final String CLAN_AVENGE_USER__CLAN_ID = "clan_id";
	public static final String CLAN_AVENGE_USER__CLAN_AVENGE_ID = "clan_avenge_id";
	public static final String CLAN_AVENGE_USER__USER_ID = "user_id";
	public static final String CLAN_AVENGE_USER__AVENGE_TIME = "avenge_time";

	/*CLAN_CHAT_POSTS*/
	public static final String CLAN_CHAT_POST__ID = GENERIC__ID;
	public static final String CLAN_CHAT_POST__POSTER_ID = "poster_id";
	public static final String CLAN_CHAT_POST__CLAN_ID = "clan_id";
	public static final String CLAN_CHAT_POST__TIME_OF_POST = "time_of_post";
	public static final String CLAN_CHAT_POST__CONTENT = "content";

	//keeps track of clan's progress in clan raid
	/*CLAN EVENT PERSISTENT FOR CLAN*/
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_ID = "clan_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__CR_ID = "cr_id";//the primary key in clan_raid
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__CRS_ID = "crs_id";//the primary key in clan_raid_stage
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_START_TIME = "stage_start_time";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__CRSM_ID = "crsm_id"; //the primary key in clan_raid_stage_monster
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN__STAGE_MONSTER_START_TIME = "stage_monster_start_time";//Used to differentiate attacks across different stage monsters

	//history of clan's clan raid
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CLAN_ID = "clan_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CR_ID = "cr_id";
	//don't really need these data seeing as how it's data from the last stage----------
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CRS_ID = "crs_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__STAGE_START_TIME = "stage_start_time";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__CRSM_ID = "crsm_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__STAGE_MONSTER_START_TIME = "stage_monster_start_time";
	//----------------------------------------------------------------------------------
	public static final String CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY__WON = "won";

	//keeps track of user's progress in clan raid
	/*CLAN EVENT PERSISTENT FOR USER*/
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__USER_ID = "user_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CLAN_ID = "clan_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CR_ID = "cr_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CR_DMG_DONE = "cr_dmg_done";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CRS_ID = "crs_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CRS_DMG_DONE = "crs_dmg_done";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_ID = "crsm_id";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__CRSM_DMG_DONE = "crsm_dmg_done";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String CLAN_EVENT_PERSISTENT_FOR_USER__USER_MONSTER_ID_THREE = "user_monster_id_three";

	//history of user's overall contribution in clan raid
	public static final String CEPFU_RAID_HISTORY__USER_ID = "user_id";
	public static final String CEPFU_RAID_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String CEPFU_RAID_HISTORY__CLAN_ID = "clan_id";
	public static final String CEPFU_RAID_HISTORY__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CEPFU_RAID_HISTORY__CR_ID = "cr_id";
	public static final String CEPFU_RAID_HISTORY__CR_DMG_DONE = "cr_dmg_done";
	public static final String CEPFU_RAID_HISTORY__CLAN_CR_DMG = "clan_cr_dmg";
	public static final String CEPFU_RAID_HISTORY__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String CEPFU_RAID_HISTORY__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String CEPFU_RAID_HISTORY__USER_MONSTER_ID_THREE = "user_monster_id_three";

	//history of user's overall contribution in clan raid stage (only a single user will see this)
	public static final String CEPFU_RAID_STAGE_HISTORY__USER_ID = "user_id";
	public static final String CEPFU_RAID_STAGE_HISTORY__CRS_START_TIME = "crs_start_time";
	public static final String CEPFU_RAID_STAGE_HISTORY__CLAN_ID = "clan_id";
	public static final String CEPFU_RAID_STAGE_HISTORY__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CEPFU_RAID_STAGE_HISTORY__CR_ID = "cr_id";
	public static final String CEPFU_RAID_STAGE_HISTORY__CRS_ID = "crs_id";
	public static final String CEPFU_RAID_STAGE_HISTORY__CRS_DMG_DONE = "crs_dmg_done";
	public static final String CEPFU_RAID_STAGE_HISTORY__STAGE_HEALTH = "stage_health";
	public static final String CEPFU_RAID_STAGE_HISTORY__CRS_END_TIME = "crs_end_time";

	//history of user's clan raid damage for a monster
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_START_TIME = "crsm_start_time";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CLAN_ID = "clan_id";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CRS_ID = "crs_id";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_ID = "crsm_id";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_DMG_DONE = "crsm_dmg_done";
	public static final String CEPFU_RAID_STAGE_MONSTER_HISTORY__CRSM_END_TIME = "crsm_end_time";

	//translations of chats
	public static final String CHAT_TRANSLATIONS__ID = GENERIC__ID;
	public static final String CHAT_TRANSLATIONS__CHAT_TYPE = "chat_type";
	public static final String CHAT_TRANSLATIONS__CHAT_ID = "chat_id";
	public static final String CHAT_TRANSLATIONS__LANGUAGE = "language";
	public static final String CHAT_TRANSLATIONS__TEXT = "text";

	//TODO: FINISH THESE COLUMNS; THIS TABLE STORES THE REWARDS CLAN USERS GET FROM A RAID
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__ID = GENERIC__ID;
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_START_TIME = "crs_start_time";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_ID = "crs_id";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__CRS_END_TIME = "crs_end_time";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__RESOURCE_TYPE = "resource_type";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__STATIC_DATA_ID = GENERIC__ID;
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__QUANTITY = "quantity";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__CLAN_EVENT_PERSISTENT_ID = "clan_event_persistent_id";
	public static final String CLAN_EVENT_PERSISTENT_USER_REWARD__TIME_REDEEMED = "time_redeemed";

	/*CLAN FOR USER*/
	public static final String CLAN_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_FOR_USER__CLAN_ID = "clan_id";
	public static final String CLAN_FOR_USER__STATUS = "status";
	public static final String CLAN_FOR_USER__REQUEST_TIME = "request_time";

	/*CLAN HELP*/
	public static final String CLAN_HELP__ID = GENERIC__ID;
	public static final String CLAN_HELP__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_HELP__USER_DATA_ID = "user_data_id";
	public static final String CLAN_HELP__HELP_TYPE = "help_type";
	public static final String CLAN_HELP__CLAN_ID = "clan_id";
	public static final String CLAN_HELP__TIME_OF_ENTRY = "time_of_entry";
	public static final String CLAN_HELP__MAX_HELPERS = "max_helpers";
	public static final String CLAN_HELP__HELPERS = "helpers";
	public static final String CLAN_HELP__OPEN = "open";
	public static final String CLAN_HELP__STATIC_DATA_ID = "static_data_id";

	/*CLAN HELP COUNT FOR USER*/
	public static final String CLAN_HELP_COUNT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_HELP_COUNT_FOR_USER__CLAN_ID = "clan_id";
	public static final String CLAN_HELP_COUNT_FOR_USER__DATE = "date";
	public static final String CLAN_HELP_COUNT_FOR_USER__SOLICITED = "solicited";
	public static final String CLAN_HELP_COUNT_FOR_USER__GIVEN = "given";

	/*CLAN INVITE*/
	public static final String CLAN_INVITE__ID = GENERIC__ID;
	public static final String CLAN_INVITE__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_INVITE__INVITER_ID = "inviter_id";
	public static final String CLAN_INVITE__CLAN_ID = "clan_id";
	public static final String CLAN_INVITE__TIME_OF_INVITE = "time_of_invite";

	/*CLAN_MEMBER_TEAM_DONATION*/
	public static final String CLAN_MEMBER_TEAM_DONATION__ID = GENERIC__ID;
	public static final String CLAN_MEMBER_TEAM_DONATION__USER_ID = GENERIC__USER_ID;
	public static final String CLAN_MEMBER_TEAM_DONATION__CLAN_ID = "clan_id";
	public static final String CLAN_MEMBER_TEAM_DONATION__POWER_LIMIT = "power_limit";
	public static final String CLAN_MEMBER_TEAM_DONATION__FULFILLED = "fulfilled";
	public static final String CLAN_MEMBER_TEAM_DONATION__MSG = "msg";
	public static final String CLAN_MEMBER_TEAM_DONATION__TIME_OF_SOLICITATION = "time_of_solicitation";

	/*EVENT PERSISTENT FOR USER*/
	public static final String EVENT_PERSISTENT_FOR_USER__USER_ID = "user_id";
	public static final String EVENT_PERSISTENT_FOR_USER__EVENT_PERSISTENT_ID = "event_persistent_id";
	public static final String EVENT_PERSISTENT_FOR_USER__TIME_OF_ENTRY = "time_of_entry";

	/*EXPANSION PURCHASE FOR USER*/
	public static final String EXPANSION_PURCHASE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String EXPANSION_PURCHASE_FOR_USER__X_POSITION = "x_position";
	public static final String EXPANSION_PURCHASE_FOR_USER__Y_POSITION = "y_position";
	public static final String EXPANSION_PURCHASE_FOR_USER__IS_EXPANDING = "is_expanding";
	public static final String EXPANSION_PURCHASE_FOR_USER__EXPAND_START_TIME = "expand_start_time";

	/*IAP TABLE*/
	public static final String IAP_HISTORY__ID = GENERIC__ID;
	public static final String IAP_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String IAP_HISTORY__TRANSACTION_ID = "transaction_id";
	public static final String IAP_HISTORY__PURCHASE_DATE = "purchase_date";
	public static final String IAP_HISTORY__PREMIUMCUR_PURCHASED = "premiumcur_purchased";
	//	public static final String IAP_HISTORY__REGCUR_PURCHASED = "regcur_purchased";
	public static final String IAP_HISTORY__CASH_SPENT = "cash_spent";
	public static final String IAP_HISTORY__UDID = "udid";
	public static final String IAP_HISTORY__PRODUCT_ID = "product_id";
	public static final String IAP_HISTORY__QUANTITY = "quantity";
	public static final String IAP_HISTORY__BID = "bid";
	public static final String IAP_HISTORY__BVRS = "bvrs";
	public static final String IAP_HISTORY__APP_ITEM_ID = "app_item_id";
	public static final String IAP_HISTORY__FB_ID = "fb_id";
	public static final String IAP_HISTORY__SALES_UUID = "sales_uuid";

	/*ITEM FOR USER TABLE*/
	public static final String ITEM_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String ITEM_FOR_USER__ITEM_ID = "item_id";
	public static final String ITEM_FOR_USER__QUANTITY = "quantity";

	/*ITEM FOR USER TABLE*/
	public static final String ITEM_FOR_USER_USAGE__ID = GENERIC__ID;
	public static final String ITEM_FOR_USER_USAGE__USER_ID = GENERIC__USER_ID;
	public static final String ITEM_FOR_USER_USAGE__ITEM_ID = "item_id";
	public static final String ITEM_FOR_USER_USAGE__TIME_OF_ENTRY = "time_of_entry";
	public static final String ITEM_FOR_USER_USAGE__USER_DATA_ID = "user_data_id";
	public static final String ITEM_FOR_USER_USAGE__ACTION_TYPE = "action_type";

	/*ITEM FOR USER TABLE*/
	public static final String ITEM_SECRET_GIFT_FOR_USER__ID = GENERIC__ID;
	public static final String ITEM_SECRET_GIFT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String ITEM_SECRET_GIFT_FOR_USER__ITEM_ID = "item_id";
	public static final String ITEM_SECRET_GIFT_FOR_USER__SECS_UNTIL_COLLECTION = "secs_until_collection";
	public static final String ITEM_SECRET_GIFT_FOR_USER__CREATE_TIME = "create_time";

	/*LOCK BOX EVENT FOR USER*/
	public static final String LOCK_BOX_EVENT_FOR_USER__EVENT_ID = "lock_box_event_id";
	public static final String LOCK_BOX_EVENT_FOR_USER__USER_ID = "user_id";
	public static final String LOCK_BOX_EVENT_FOR_USER__NUM_BOXES = "num_boxes";
	public static final String LOCK_BOX_EVENT_FOR_USER__LAST_OPENING_TIME = "last_opening_time";
	public static final String LOCK_BOX_EVENT_FOR_USER__NUM_TIMES_COMPLETED = "num_times_completed";
	public static final String LOCK_BOX_EVENT_FOR_USER__HAS_BEEN_REDEEMED = "has_been_redeemed";

	/*LOGIN HISTORY*/
	public static final String LOGIN_HISTORY__ID = GENERIC__ID;
	public static final String LOGIN_HISTORY__UDID = "udid";
	public static final String LOGIN_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String LOGIN_HISTORY__DATE = "date";
	public static final String LOGIN_HISTORY__IS_LOGIN = "is_login";

	/*MINI EVENT FOR USER*/
	public static final String MINI_EVENT_FOR_USER__USER_ID = "user_id";
	public static final String MINI_EVENT_FOR_USER__MINI_EVENT_ID = "mini_event_id";
	public static final String MINI_EVENT_FOR_USER__USER_LVL = "user_lvl";
	public static final String MINI_EVENT_FOR_USER__TIER_ONE_REDEEMED = "tier_one_redeemed";
	public static final String MINI_EVENT_FOR_USER__TIER_TWO_REDEEMED = "tier_two_redeemed";
	public static final String MINI_EVENT_FOR_USER__TIER_THREE_REDEEMED = "tier_three_redeemed";

	/*MINI EVENT GOAL FOR USER*/
	public static final String MINI_EVENT_GOAL_FOR_USER__USER_ID = "user_id";
	public static final String MINI_EVENT_GOAL_FOR_USER__MINI_EVENT_GOAL_ID = "mini_event_goal_id";
	public static final String MINI_EVENT_GOAL_FOR_USER__PROGRESS = "progress";

	/*MINI TASK FOR USER*/
	public static final String MINI_JOB_FOR_USER__ID = GENERIC__ID;
	public static final String MINI_JOB_FOR_USER__USER_ID = "user_id";
	public static final String MINI_JOB_FOR_USER__MINI_JOB_ID = "mini_job_id";
	public static final String MINI_JOB_FOR_USER__BASE_DMG_RECEIVED = "base_dmg_received";
	//  public static final String MINI_JOB_FOR_USER__DURATION_MINUTES = "duration_minutes";
	public static final String MINI_JOB_FOR_USER__DURATION_SECONDS = "duration_seconds";
	public static final String MINI_JOB_FOR_USER__TIME_STARTED = "time_started";
	public static final String MINI_JOB_FOR_USER__USER_MONSTER_IDS = "user_monster_ids";
	public static final String MINI_JOB_FOR_USER__TIME_COMPLETED = "time_completed";

	/*MINI TASK FOR USER HISTORYS*/
	public static final String MINI_JOB_FOR_USER_HISTORY__USER_ID = "user_id";
	public static final String MINI_JOB_FOR_USER_HISTORY__MINI_JOB_ID = "mini_job_id";
	public static final String MINI_JOB_FOR_USER_HISTORY__BASE_DMG_RECEIVED = "base_dmg_received";
	public static final String MINI_JOB_FOR_USER_HISTORY__TIME_STARTED = "time_started";
	public static final String MINI_JOB_FOR_USER_HISTORY__TIME_COMPLETED = "time_completed";
	public static final String MINI_JOB_FOR_USER_HISTORY__USER_MONSTER_IDS = "user_monster_ids";

	/*MONSTER ENHANCING FOR USER*/
	public static final String MONSTER_ENHANCING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME = "expected_start_time";
	public static final String MONSTER_ENHANCING_FOR_USER__ENHANCING_COST = "enhancing_cost";
	public static final String MONSTER_ENHANCING_FOR_USER__ENHANCING_COMPLETE = "enhancing_complete";

	/*MONSTER ENHANCING HISTORY*/
	public static final String MONSTER_ENHANCING_HISTORY__ID = GENERIC__ID;
	public static final String MONSTER_ENHANCING_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_ENHANCING_HISTORY__MFU_ID_BEING_ENHANCED = "mfu_id_being_enhanced";
	public static final String MONSTER_ENHANCING_HISTORY__FEEDER_MFU_ID = "feeder_mfu_id";
	public static final String MONSTER_ENHANCING_HISTORY__CURRENT_EXPERIENCE = "current_experience";
	public static final String MONSTER_ENHANCING_HISTORY__PREVIOUS_EXPERIENCE = "previous_experience";
	public static final String MONSTER_ENHANCING_HISTORY__ENHANCING_START_TIME = "enhancing_start_time";
	public static final String MONSTER_ENHANCING_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String MONSTER_ENHANCING_HISTORY__ENHANCING_COST = "enhancing_cost";

	/*MONSTER EVOLVING FOR USER*/
	public static final String MONSTER_EVOLVING_FOR_USER__CATALYST_USER_MONSTER_ID = "catalyst_user_monster_id";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String MONSTER_EVOLVING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_EVOLVING_FOR_USER__START_TIME = "start_time";

	/*MONSTER EVOLVING HISTORY TABLE*/
	public static final String MONSTER_EVOLVING_HISTORY__ID = GENERIC__ID;
	public static final String MONSTER_EVOLVING_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_EVOLVING_HISTORY__USER_MONSTER_ID_ONE = "user_monster_id_one";
	public static final String MONSTER_EVOLVING_HISTORY__USER_MONSTER_ID_TWO = "user_monster_id_two";
	public static final String MONSTER_EVOLVING_HISTORY__CATALYST_USER_MONSTER_ID = "catalyst_user_monster_id";
	public static final String MONSTER_EVOLVING_HISTORY__START_TIME = "start_time";
	public static final String MONSTER_EVOLVING_HISTORY__TIME_OF_ENTRY = "time_of_entry";
	public static final String MONSTER_EVOLVING_HISTORY__END_TIME = "end_time";

	/*MONSTER FOR USER*/
	public static final String MONSTER_FOR_USER__ID = GENERIC__ID;
	public static final String MONSTER_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_FOR_USER__MONSTER_ID = "monster_id";
	public static final String MONSTER_FOR_USER__CURRENT_EXPERIENCE = "current_experience";
	public static final String MONSTER_FOR_USER__CURRENT_LEVEL = "current_level";
	public static final String MONSTER_FOR_USER__CURRENT_HEALTH = "current_health";
	public static final String MONSTER_FOR_USER__NUM_PIECES = "num_pieces";
	public static final String MONSTER_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String MONSTER_FOR_USER__COMBINE_START_TIME = "combine_start_time";
	public static final String MONSTER_FOR_USER__TEAM_SLOT_NUM = "team_slot_num";
	public static final String MONSTER_FOR_USER__SOURCE_OF_PIECES = "source_of_pieces";
	public static final String MONSTER_FOR_USER__HAS_ALL_PIECES = "has_all_pieces";
	public static final String MONSTER_FOR_USER__RESTRICTED = "restricted";
	public static final String MONSTER_FOR_USER__OFFENSIVE_SKILL_ID = "offensive_skill_id";
	public static final String MONSTER_FOR_USER__DEFENSIVE_SKILL_ID = "defensive_skill_id";

	/*MONSTER FOR USER DELETED*/
	public static final String MONSTER_FOR_USER_DELETED__ID = "monster_for_user_id";
	public static final String MONSTER_FOR_USER_DELETED__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_FOR_USER_DELETED__MONSTER_ID = "monster_id";
	public static final String MONSTER_FOR_USER_DELETED__CURRENT_EXPERIENCE = "current_experience";
	public static final String MONSTER_FOR_USER_DELETED__CURRENT_LEVEL = "current_level";
	public static final String MONSTER_FOR_USER_DELETED__CURRENT_HEALTH = "current_health";
	public static final String MONSTER_FOR_USER_DELETED__NUM_PIECES = "num_pieces";
	public static final String MONSTER_FOR_USER_DELETED__IS_COMPLETE = "is_complete";
	public static final String MONSTER_FOR_USER_DELETED__COMBINE_START_TIME = "combine_start_time";
	public static final String MONSTER_FOR_USER_DELETED__TEAM_SLOT_NUM = "team_slot_num";
	public static final String MONSTER_FOR_USER_DELETED__SOURCE_OF_PIECES = "source_of_pieces";
	public static final String MONSTER_FOR_USER_DELETED__DELETED_REASON = "deleted_reason";
	public static final String MONSTER_FOR_USER_DELETED__DETAILS = "details";
	public static final String MONSTER_FOR_USER_DELETED__DELETED_TIME = "deleted_time";

	/*MONSTER HEALING FOR USER*/
	public static final String MONSTER_HEALING_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_HEALING_FOR_USER__QUEUED_TIME = "queued_time";
	public static final String MONSTER_HEALING_FOR_USER__USER_STRUCT_HOSPITAL_ID = "user_struct_hospital_id";
	public static final String MONSTER_HEALING_FOR_USER__HEALTH_PROGRESS = "health_progress";
	public static final String MONSTER_HEALING_FOR_USER__PRIORITY = "priority";
	public static final String MONSTER_HEALING_FOR_USER__ELAPSED_SECONDS = "elapsed_seconds";

	/*MONSTER HEALING HISTORY*/
	public static final String MONSTER_HEALING_HISTORY__USER_ID = "user_id";
	public static final String MONSTER_HEALING_HISTORY__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_HEALING_HISTORY__QUEUED_TIME = "queued_time";
	public static final String MONSTER_HEALING_HISTORY__DEQUEUED_TIME = "dequeued_time";
	public static final String MONSTER_HEALING_HISTORY__FINISHED_HEALING = "finished_healing";

	/*MONSTER SNAPSHOT FOR USER*/
	public static final String MONSTER_SNAPSHOT_FOR_USER__ID = GENERIC__ID;
	public static final String MONSTER_SNAPSHOT_FOR_USER__TIME_OF_ENTRY = "time_of_entry";
	public static final String MONSTER_SNAPSHOT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String MONSTER_SNAPSHOT_FOR_USER__TYPE = "type";
	public static final String MONSTER_SNAPSHOT_FOR_USER__ID_IN_TABLE = "id_in_table";
	public static final String MONSTER_SNAPSHOT_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
	public static final String MONSTER_SNAPSHOT_FOR_USER__MONSTER_ID = "monster_id";
	public static final String MONSTER_SNAPSHOT_FOR_USER__CURRENT_EXP = "current_exp";
	public static final String MONSTER_SNAPSHOT_FOR_USER__CURRENT_LVL = "current_lvl";
	public static final String MONSTER_SNAPSHOT_FOR_USER__CURRENT_HP = "current_hp";
	public static final String MONSTER_SNAPSHOT_FOR_USER__TEAM_SLOT_NUM = "team_slot_num";
	public static final String MONSTER_SNAPSHOT_FOR_USER__OFF_SKILL_ID = "off_skill_id";
	public static final String MONSTER_SNAPSHOT_FOR_USER__DEF_SKILL_ID = "def_skill_id";

	/*OBSTACLE FOR USER */
	public static final String OBSTACLE_FOR_USER__ID = GENERIC__ID;
	public static final String OBSTACLE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String OBSTACLE_FOR_USER__OBSTACLE_ID = "obstacle_id";
	public static final String OBSTACLE_FOR_USER__XCOORD = "xcoord";
	public static final String OBSTACLE_FOR_USER__YCOORD = "ycoord";
	public static final String OBSTACLE_FOR_USER__REMOVAL_TIME = "removal_time";
	public static final String OBSTACLE_FOR_USER__ORIENTATION = "orientation";

	/*PVP BATTLE HISTORY*/
	public static final String PVP_BATTLE_FOR_USER__ATTACKER_ID = "attacker_id";
	public static final String PVP_BATTLE_FOR_USER__DEFENDER_ID = "defender_id";
	public static final String PVP_BATTLE_FOR_USER__ATTACKER_WIN_ELO_CHANGE = "attacker_win_elo_change";
	public static final String PVP_BATTLE_FOR_USER__DEFENDER_LOSE_ELO_CHANGE = "defender_lose_elo_change";
	public static final String PVP_BATTLE_FOR_USER__ATTACKER_LOSE_ELO_CHANGE = "attacker_lose_elo_change";
	public static final String PVP_BATTLE_FOR_USER__DEFENDER_WIN_ELO_CHANGE = "defender_win_elo_change";
	public static final String PVP_BATTLE_FOR_USER__BATTLE_START_TIME = "battle_start_time";

	/*PVP BATTLE HISTORY*/
	public static final String PVP_BATTLE_HISTORY__ATTACKER_ID = "attacker_id";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_ID = "defender_id";
	public static final String PVP_BATTLE_HISTORY__BATTLE_END_TIME = "battle_end_time";
	public static final String PVP_BATTLE_HISTORY__BATTLE_START_TIME = "battle_start_time";

	public static final String PVP_BATTLE_HISTORY__ATTACKER_ELO_CHANGE = "attacker_elo_change";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_ELO_BEFORE = "attacker_elo_before";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_ELO_AFTER = "attacker_elo_after";

	public static final String PVP_BATTLE_HISTORY__DEFENDER_ELO_CHANGE = "defender_elo_change";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_ELO_BEFORE = "defender_elo_before";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_ELO_AFTER = "defender_elo_after";

	public static final String PVP_BATTLE_HISTORY__ATTACKER_PREV_LEAGUE = "attacker_prev_league";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_CUR_LEAGUE = "attacker_cur_league";

	public static final String PVP_BATTLE_HISTORY__DEFENDER_PREV_LEAGUE = "defender_prev_league";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_CUR_LEAGUE = "defender_cur_league";

	public static final String PVP_BATTLE_HISTORY__ATTACKER_PREV_RANK = "attacker_prev_rank";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_CUR_RANK = "attacker_cur_rank";

	public static final String PVP_BATTLE_HISTORY__DEFENDER_PREV_RANK = "defender_prev_rank";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_CUR_RANK = "defender_cur_rank";

	public static final String PVP_BATTLE_HISTORY__ATTACKER_CASH_CHANGE = "attacker_cash_change";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_CASH_CHANGE = "defender_cash_change";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_OIL_CHANGE = "attacker_oil_change";
	public static final String PVP_BATTLE_HISTORY__DEFENDER_OIL_CHANGE = "defender_oil_change";
	public static final String PVP_BATTLE_HISTORY__PVP_DMG_MULTIPLIER = "pvp_dmg_multiplier";
	public static final String PVP_BATTLE_HISTORY__ATTACKER_WON = "attacker_won";
	public static final String PVP_BATTLE_HISTORY__CANCELLED = "cancelled";
	public static final String PVP_BATTLE_HISTORY__EXACTED_REVENGE = "exacted_revenge";
	//public static final String PVP_BATTLE_HISTORY__DISPLAY_TO_USER = "display_to_defender";
	public static final String PVP_BATTLE_HISTORY__CLAN_AVENGED = "clan_avenged";

	/*PVP_BOARD_OBSTACLE_FOR_USER*/
	public static final String PVP_BOARD_OBSTACLE_FOR_USER__ID = GENERIC__ID;
	public static final String PVP_BOARD_OBSTACLE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String PVP_BOARD_OBSTACLE_FOR_USER__OBSTACLE_ID = "obstacle_id";
	public static final String PVP_BOARD_OBSTACLE_FOR_USER__POS_X = "pos_x";
	public static final String PVP_BOARD_OBSTACLE_FOR_USER__POS_Y = "pos_y";

	/*PVP LEAGUE FOR USER*/
	public static final String PVP_LEAGUE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String PVP_LEAGUE_FOR_USER__PVP_LEAGUE_ID = "league_id";
	public static final String PVP_LEAGUE_FOR_USER__RANK = "rank";
	public static final String PVP_LEAGUE_FOR_USER__ELO = "elo";
	public static final String PVP_LEAGUE_FOR_USER__SHIELD_END_TIME = "shield_end_time";
	//public static final String PVP_LEAGUE_FOR_USER__IN_BATTLE_SHIELD_END_TIME = "in_battle_shield_end_time";
	public static final String PVP_LEAGUE_FOR_USER__BATTLE_END_TIME = "battle_end_time";
	public static final String PVP_LEAGUE_FOR_USER__ATTACKS_WON = "attacks_won";
	public static final String PVP_LEAGUE_FOR_USER__DEFENSES_WON = "defenses_won";
	public static final String PVP_LEAGUE_FOR_USER__ATTACKS_LOST = "attacks_lost";
	public static final String PVP_LEAGUE_FOR_USER__DEFENSES_LOST = "defenses_lost";
	public static final String PVP_LEAGUE_FOR_USER__MONSTER_DMG_MULTIPLIER = "monster_dmg_multiplier";

	/*QUEST FOR USER TABLE*/
	public static final String QUEST_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String QUEST_FOR_USER__QUEST_ID = "quest_id";
	public static final String QUEST_FOR_USER__IS_REDEEMED = "is_redeemed";
	public static final String QUEST_FOR_USER__IS_COMPLETE = "is_complete";

	/*QUEST JOB FOR USER TABLE*/
	public static final String QUEST_JOB_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String QUEST_JOB_FOR_USER__QUEST_ID = "quest_id";
	public static final String QUEST_JOB_FOR_USER__QUEST_JOB_ID = "quest_job_id";
	public static final String QUEST_JOB_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String QUEST_JOB_FOR_USER__PROGRESS = "progress";

	/*REFERRALS*/
	public static final String REFERRALS__REFERRER_ID = "referrer_id";
	public static final String REFERRALS__NEWLY_REFERRED_ID = "newly_referred_id";
	public static final String REFERRALS__TIME_OF_REFERRAL = "time_of_referral";
	public static final String REFERRALS__COINS_GIVEN_TO_REFERRER = "coins_given_to_referrer";

	/*RESEARCH FOR USER TABLE*/
	public static final String RESEARCH_FOR_USER__ID = GENERIC__ID;
	public static final String RESEARCH_FOR_USER__USER_ID = "user_id";
	public static final String RESEARCH_FOR_USER__RESEARCH_ID = "research_id";
	public static final String RESEARCH_FOR_USER__TIME_PURCHASED = "time_purchased";
	public static final String RESEARCH_FOR_USER__IS_COMPLETE = "is_complete";

	/*USER STRUCTS TABLE*/
	public static final String STRUCTURE_FOR_USER__ID = GENERIC__ID;
	public static final String STRUCTURE_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String STRUCTURE_FOR_USER__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_FOR_USER__LAST_RETRIEVED = "last_retrieved";
	public static final String STRUCTURE_FOR_USER__X_COORD = "xcoord";
	public static final String STRUCTURE_FOR_USER__Y_COORD = "ycoord";
	public static final String STRUCTURE_FOR_USER__PURCHASE_TIME = "purchase_time";
	public static final String STRUCTURE_FOR_USER__IS_COMPLETE = "is_complete";
	public static final String STRUCTURE_FOR_USER__ORIENTATION = "orientation";
	public static final String STRUCTURE_FOR_USER__FB_INVITE_STRUCT_LVL = "fb_invite_struct_lvl";
	//  public static final String STRUCTURE_FOR_USER__UPGRADE_START_TIME = "upgrade_start_time";

	/*TASK FOR USER ONGOING TABLE*/
	public static final String TASK_FOR_USER_ONGOING__ID = GENERIC__ID;
	public static final String TASK_FOR_USER_ONGOING__USER_ID = GENERIC__USER_ID;
	public static final String TASK_FOR_USER_ONGOING__TASK_ID = "task_id";
	public static final String TASK_FOR_USER_ONGOING__EXP_GAINED = "exp_gained";
	public static final String TASK_FOR_USER_ONGOING__CASH_GAINED = "cash_gained";
	public static final String TASK_FOR_USER_ONGOING__OIL_GAINED = "oil_gained";
	public static final String TASK_FOR_USER_ONGOING__NUM_REVIVES = "num_revives";
	public static final String TASK_FOR_USER_ONGOING__START_TIME = "start_time";
	public static final String TASK_FOR_USER_ONGOING__TASK_STAGE_ID = "task_stage_id";

	/*TASK FOR USER CLIENT STATE TABLE*/
	public static final String TASK_FOR_USER_CLIENT_STATE__USER_ID = "user_id";
	public static final String TASK_FOR_USER_CLIENT_STATE__CLIENT_STATE = "client_state";

	/*TASK FOR USER COMPLETED TABLE*/
	public static final String TASK_FOR_USER_COMPLETED__USER_ID = GENERIC__USER_ID;
	public static final String TASK_FOR_USER_COMPLETED__TASK_ID = "task_id";
	public static final String TASK_FOR_USER_COMPLETED__TIME_OF_ENTRY = "time_of_entry";
	public static final String TASK_FOR_USER_COMPLETED__UNCLAIMED_CASH = "unclaimed_cash";
	public static final String TASK_FOR_USER_COMPLETED__UNCLAIMED_OIL = "unclaimed_oil";

	/*TASK HISTORY TABLE*/
	public static final String TASK_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String TASK_HISTORY__TASK_ID = "task_id";
	public static final String TASK_HISTORY__EXP_GAINED = "exp_gained";
	public static final String TASK_HISTORY__CASH_GAINED = "cash_gained";
	public static final String TASK_HISTORY__OIL_GAINED = "oil_gained";
	public static final String TASK_HISTORY__NUM_REVIVES = "num_revives";
	public static final String TASK_HISTORY__START_TIME = "start_time";
	public static final String TASK_HISTORY__END_TIME = "end_time";
	public static final String TASK_HISTORY__USER_WON = "user_won";
	public static final String TASK_HISTORY__CANCELLED = "cancelled";
	public static final String TASK_HISTORY__TASK_STAGE_ID = "task_stage_id";

	/*TASK STAGE FOR USER TABLE*/
	public static final String TASK_STAGE_FOR_USER__ID = GENERIC__ID;
	public static final String TASK_STAGE_FOR_USER__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_STAGE_FOR_USER__STAGE_NUM = "stage_num";
	//primary key in task stage monster table, if monster id then would be
	//task stage monster monster id
	public static final String TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
	public static final String TASK_STAGE_FOR_USER__MONSTER_TYPE = "monster_type";
	public static final String TASK_STAGE_FOR_USER__EXP_GAINED = "exp_gained";
	public static final String TASK_STAGE_FOR_USER__CASH_GAINED = "cash_gained";
	public static final String TASK_STAGE_FOR_USER__OIL_GAINED = "oil_gained";
	public static final String TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
	public static final String TASK_STAGE_FOR_USER__ITEM_ID_DROPPED = "item_id_dropped"; //0 is no item
	public static final String TASK_STAGE_FOR_USER__ATTACKED_FIRST = "attacked_first";

	/*USER TASK STAGE HISTORY TABLE*/
	public static final String TASK_STAGE_HISTORY__ID = "task_stage_for_user_id";
	public static final String TASK_STAGE_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
	public static final String TASK_STAGE_HISTORY__STAGE_NUM = "stage_num";
	public static final String TASK_STAGE_HISTORY__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
	//monster_type not needed here since task stage monster now has monster_type
	public static final String TASK_STAGE_HISTORY__MONSTER_TYPE = "monster_type";
	public static final String TASK_STAGE_HISTORY__EXP_GAINED = "exp_gained";
	public static final String TASK_STAGE_HISTORY__CASH_GAINED = "cash_gained";
	public static final String TASK_STAGE_HISTORY__OIL_GAINED = "oil_gained";
	public static final String TASK_STAGE_HISTORY__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
	public static final String TASK_STAGE_HISTORY__ITEM_ID_DROPPED = "item_id_dropped"; //0 is no item
	public static final String TASK_STAGE_HISTORY__MONSTER_ID_DROPPED = "monster_id_dropped";
	public static final String TASK_STAGE_HISTORY__MONSTER_DROPPED_LVL = "monster_dropped_lvl";
	public static final String TASK_STAGE_HISTORY__ATTACKED_FIRST = "attacked_first";

	/*TOURNAMENT EVENT*/
	public static final String TOURNAMENT_EVENT__ID = GENERIC__ID;
	public static final String TOURNAMENT_EVENT__START_TIME = "start_time";
	public static final String TOURNAMENT_EVENT__END_TIME = "end_time";
	public static final String TOURNAMENT_EVENT__EVENT_NAME = "event_time";
	public static final String TOURNAMENT_EVENT__REWARDS_GIVEN_OUT = "rewards_given_out";

	/*TOURNAMENT EVENT FOR USER*/
	public static final String TOURNAMENT_EVENT_FOR_USER__TOURNAMENT_EVENT_ID = "tournament_event_id";
	public static final String TOURNAMENT_EVENT_FOR_USER__USER_ID = GENERIC__USER_ID;
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_WON = "battles_won";
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_LOST = "battles_lost";
	public static final String TOURNAMENT_EVENT_FOR_USER__BATTLES_FLED = "battles_fled";

	/*TOURNAMENT REWARD*/
	public static final String TOURNAMENT_REWARD__ID = "tournament_event_id";
	public static final String TOURNAMENT_REWARD__MIN_RANK = "min_rank";
	public static final String TOURNAMENT_REWARD__MAX_RANK = "max_rank";
	public static final String TOURNAMENT_REWARD__GOLD_REWARDED = "gold_rewarded";
	public static final String TOURNAMENT_REWARD__BACKGROUND_IMAGE_NAME = "background_image_name";
	public static final String TOURNAMENT_REWARD__PRIZE_IMAGE_NAME = "prize_image_name";
	public static final String TOURNAMENT_REWARD__BLUE = "blue";
	public static final String TOURNAMENT_REWARD__GREEN = "green";
	public static final String TOURNAMENT_REWARD__RED = "red";

	/*USER TRANSLATION SETTINGS*/
	public static final String TRANSLATION_SETTINGS_FOR_USER__ID = GENERIC__ID;
	public static final String TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID = "receiver_user_id";
	public static final String TRANSLATION_SETTINGS_FOR_USER__SENDER_USER_ID = "sender_user_id";
	public static final String TRANSLATION_SETTINGS_FOR_USER__LANGUAGE = "language";
	public static final String TRANSLATION_SETTINGS_FOR_USER__CHAT_TYPE = "chat_type";
	public static final String TRANSLATION_SETTINGS_FOR_USER__TRANSLATIONS_ON = "translations_on";

	/*USER TABLE*/
	public static final String USER__ID = GENERIC__ID;
	public static final String USER__NAME = "name";
	public static final String USER__LEVEL = "level";
	public static final String USER__GEMS = "gems";
	public static final String USER__CASH = "cash";
	public static final String USER__OIL = "oil";
	public static final String USER__EXPERIENCE = "experience";
	public static final String USER__TASKS_COMPLETED = "tasks_completed";
	public static final String USER__REFERRAL_CODE = "referral_code";
	public static final String USER__NUM_REFERRALS = "num_referrals";
	public static final String USER__UDID_FOR_HISTORY = "udid_for_history";
	public static final String USER__LAST_LOGIN = "last_login";
	public static final String USER__LAST_LOGOUT = "last_logout";
	public static final String USER__DEVICE_TOKEN = "device_token";
	public static final String USER__NUM_BADGES = "num_badges";
	public static final String USER__IS_FAKE = "is_fake";
	public static final String USER__CREATE_TIME = "create_time";
	public static final String USER__IS_ADMIN = "is_admin";
	public static final String USER__APSALAR_ID = "apsalar_id";
	public static final String USER__NUM_COINS_RETRIEVED_FROM_STRUCTS = "num_coins_retrieved_from_structs";
	public static final String USER__NUM_OIL_RETRIEVED_FROM_STRUCTS = "num_oil_retrieved_from_structs";
	public static final String USER__NUM_CONSECUTIVE_DAYS_PLAYED = "num_consecutive_days_played";
	public static final String USER__CLAN_ID = "clan_id";
	public static final String USER__LAST_WALL_POST_NOTIFICATION_TIME = "last_wall_post_notification_time";
	//  public static final String USER__KABAM_NAID = "kabam_naid";
	public static final String USER__HAS_RECEIVED_FB_REWARD = "has_received_fb_reward";
	public static final String USER__NUM_BEGINNER_SALES_PURCHASED = "num_beginner_sales_purchased";
	public static final String USER__FACEBOOK_ID = "facebook_id";
	public static final String USER__FB_ID_SET_ON_USER_CREATE = "fb_id_set_on_user_create";
	public static final String USER__GAME_CENTER_ID = "game_center_id";
	public static final String USER__UDID = "udid";
	public static final String USER__LAST_OBSTACLE_SPAWNED_TIME = "last_obstacle_spawned_time";
	public static final String USER__NUM_OBSTACLES_REMOVED = "num_obstacles_removed";
	public static final String USER__LAST_MINI_JOB_GENERATED_TIME = "last_mini_job_generated_time";
	public static final String USER__AVATAR_MONSTER_ID = "avatar_monster_id";
	public static final String USER__EMAIL = "email";
	public static final String USER__FB_DATA = "fb_data";
	public static final String USER__LAST_FREE_BOOSTER_PACK_TIME = "last_free_booster_pack_time";
	public static final String USER__CLAN_HELPS = "clan_helps";
	public static final String USER__LAST_SECRET_GIFT_COLLECT_TIME = "last_secret_gift_collect_time";
	public static final String USER__PVP_DEFENDING_MESSAGE = "pvp_defending_message";
	public static final String USER__LAST_TEAM_DONATE_SOLICITATION = "last_team_donate_solicitation";
	public static final String USER__BOUGHT_RIGGED_BOOSTER_PACK = "bought_rigged_booster_pack";
	public static final String USER__SALES_VALUE = "sales_value";
	public static final String USER__SALES_LAST_PURCHASE_TIME = "sales_last_purchase_time";
	public static final String USER__SALES_JUMP_TWO_TIERS = "sales_jump_two_tiers";
	public static final String USER__TOTAL_STRENGTH = "total_strength";
	public static final String USER__SEGMENTATION_GROUP = "segmentation_group";

	/* USER BEFORE TUTORIAL COMPLETION*/
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__ID = GENERIC__ID;
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID = "open_udid";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__UDID = "udid";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__MAC = "mac";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__ADVERTISER_ID = "advertiser_id";
	public static final String USER_BEFORE_TUTORIAL_COMPLETION__CREATE_TIME = "create_time";

	/*USER CURRENCY HISTORY (FOR GOLD/DIAMONDS AND SILVER/COINS*/
	public static final String USER_CURRENCY_HISTORY__ID = GENERIC__ID;
	public static final String USER_CURRENCY_HISTORY__USER_ID = GENERIC__USER_ID;
	public static final String USER_CURRENCY_HISTORY__DATE = "date";
	public static final String USER_CURRENCY_HISTORY__RESOURCE_TYPE = "resource_type";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_CHANGE = "currency_change";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE = "currency_before_change";
	public static final String USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE = "currency_after_change";
	public static final String USER_CURRENCY_HISTORY__REASON_FOR_CHANGE = "reason_for_change";
	public static final String USER_CURRENCY_HISTORY__DETAILS = "details";

	/*USER FACEBOOK INVITE*/
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__ID = GENERIC__ID;
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__INVITER_USER_ID = "inviter_user_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__RECIPIENT_FACEBOOK_ID = "recipient_facebook_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_OF_INVITE = "time_of_invite";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_ACCEPTED = "time_accepted";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_ID = "user_struct_id";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__USER_STRUCT_FB_LVL = "user_struct_fb_lvl";
	public static final String USER_FACEBOOK_INVITE_FOR_SLOT__TIME_REDEEMED = "time_redeemed";

	/*USER PRIVATE CHAT POSTS*/
	public static final String USER_PRIVATE_CHAT_POSTS__ID = GENERIC__ID;
	public static final String USER_PRIVATE_CHAT_POSTS__POSTER_ID = "poster_id";
	public static final String USER_PRIVATE_CHAT_POSTS__RECIPIENT_ID = "recipient_id";
	public static final String USER_PRIVATE_CHAT_POSTS__TIME_OF_POST = "time_of_post";
	public static final String USER_PRIVATE_CHAT_POSTS__CONTENT = "content";
	public static final String USER_PRIVATE_CHAT_POSTS__CONTENT_LANGUAGE = "content_language";

	/*USER SESSIONS*/
	public static final String USER_SESSIONS__USER_ID = GENERIC__USER_ID;
	public static final String USER_SESSIONS__LOGIN_TIME = "login_time";
	public static final String USER_SESSIONS__LOGOUT_TIME = "logout_time";

	/************** CONFIGURATION DATA TABLES *****************************/

	public static final String ACHIEVEMENT__ID = GENERIC__ID;
	public static final String ACHIEVEMENT__NAME = "name";
	public static final String ACHIEVEMENT__DESCRIPTION = "description";
	public static final String ACHIEVEMENT__GEM_REWARD = "gem_reward";
	public static final String ACHIEVEMENT__LVL = "lvl";
	public static final String ACHIEVEMENT__ACHIEVEMENT_TYPE = "achievement_type";
	public static final String ACHIEVEMENT__RESOURCE_TYPE = "resource_type";
	public static final String ACHIEVEMENT__MONSTER_ELEMENT = "monster_element";
	public static final String ACHIEVEMENT__MONSTER_QUALITY = "monster_quality";
	public static final String ACHIEVEMENT__STATIC_DATA_ID = "static_data_id";
	public static final String ACHIEVEMENT__QUANTITY = "quantity";
	public static final String ACHIEVEMENT__PRIORITY = "priority";
	public static final String ACHIEVEMENT__PREREQUISITE_ID = "prerequisite_id";
	public static final String ACHIEVEMENT__SUCCESSOR_ID = "successor_id";
	public static final String ACHIEVEMENT__EXP_REWARD = "exp_reward";

	/*ALERT ON STARTUP*/
	public static final String ALERT_ON_STARTUP__ID = GENERIC__ID;
	public static final String ALERT_ON_STARTUP__MESSAGE = "message";
	public static final String ALERT_ON_STARTUP__IS_ACTIVE = "is_active";

	public static final String BANNED_USER__USER_ID = GENERIC__USER_ID;

	/*BATTLE ITEM*/
	public static final String BATTLE_ITEM__ID = GENERIC__ID;
	public static final String BATTLE_ITEM__TYPE = "type";
	public static final String BATTLE_ITEM__CATEGORY = "battle_item_category";
	public static final String BATTLE_ITEM__CREATE_RESOURCE_TYPE = "create_resource_type";
	public static final String BATTLE_ITEM__CREATE_COST = "create_cost";
	public static final String BATTLE_ITEM__NAME = "name";
	public static final String BATTLE_ITEM__DESCRIPTION = "description";
	public static final String BATTLE_ITEM__POWER_AMOUNT = "power_amount";
	public static final String BATTLE_ITEM__IMAGE_NAME = "image_name";
	public static final String BATTLE_ITEM__PRIORITY = "priority";
	public static final String BATTLE_ITEM__MINUTES_TO_CREATE = "minutes_to_create";
	public static final String BATTLE_ITEM__IN_BATTLE_GEM_COST = "in_battle_gem_cost";
	public static final String BATTLE_ITEM__AMOUNT = "amount";

	/*BOARD*/
	public static final String BOARD__ID = GENERIC__ID;
	public static final String BOARD__WIDTH = "width";
	public static final String BOARD__HEIGHT = "height";
	public static final String BOARD__ORB_ELEMENTS = "orb_elements";

	/*BOARD OBSTACLE*/
	public static final String BOARD_OBSTACLE__ID = "id";
	public static final String BOARD_OBSTACLE__NAME = "name";
	public static final String BOARD_OBSTACLE__TYPE = "type";
	public static final String BOARD_OBSTACLE__POWER_AMT = "power_amt";
	public static final String BOARD_OBSTACLE__INIT_AVAILABLE = "init_available";

	/*BOARD PROPERTY*/
	public static final String BOARD_PROPERTY__ID = GENERIC__ID;
	public static final String BOARD_PROPERTY__BOARD_ID = "board_id";
	public static final String BOARD_PROPERTY__NAME = "name";
	public static final String BOARD_PROPERTY__POS_X = "pos_x";
	public static final String BOARD_PROPERTY__POS_Y = "pos_y";
	public static final String BOARD_PROPERTY__ELEMENT = "element";
	public static final String BOARD_PROPERTY__VALUE = "value";
	public static final String BOARD_PROPERTY__QUANTITY = "quantity";

	public static final String BOOSTER_DISPLAY_ITEM__ID = GENERIC__ID;
	public static final String BOOSTER_DISPLAY_ITEM__BOOSTER_PACK_ID = "booster_pack_id";
	public static final String BOOSTER_DISPLAY_ITEM__IS_MONSTER = "is_monster";
	public static final String BOOSTER_DISPLAY_ITEM__IS_COMPLETE = "is_complete";
	public static final String BOOSTER_DISPLAY_ITEM__MONSTER_QUALITY = "monster_quality";
	public static final String BOOSTER_DISPLAY_ITEM__GEM_REWARD = "gem_reward";
	public static final String BOOSTER_DISPLAY_ITEM__QUANTITY = "quantity";
	public static final String BOOSTER_DISPLAY_ITEM__ITEM_ID = "item_id";
	public static final String BOOSTER_DISPLAY_ITEM__ITEM_QUANTITY = "item_quantity";
	public static final String BOOSTER_DISPLAY_ITEM__REWARD_ID = "reward_id";

	public static final String BOOSTER_ITEM__ID = GENERIC__ID;
	public static final String BOOSTER_ITEM__BOOSTER_PACK_ID = "booster_pack_id";
	public static final String BOOSTER_ITEM__MONSTER_ID = "monster_id";
	public static final String BOOSTER_ITEM__NUM_PIECES = "num_pieces";
	public static final String BOOSTER_ITEM__IS_COMPLETE = "is_complete";
	public static final String BOOSTER_ITEM__IS_SPECIAL = "is_special";
	public static final String BOOSTER_ITEM__GEM_REWARD = "gem_reward";
	public static final String BOOSTER_ITEM__CASH_REWARD = "cash_reward";
	public static final String BOOSTER_ITEM__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String BOOSTER_ITEM__ITEM_ID = "item_id";
	public static final String BOOSTER_ITEM__ITEM_QUANTITY = "item_quantity";
	public static final String BOOSTER_ITEM__REWARD_ID = "reward_id";

	public static final String BOOSTER_PACK__ID = GENERIC__ID;
	public static final String BOOSTER_PACK__NAME = "name";
	public static final String BOOSTER_PACK__GEM_PRICE = "gem_price";
	public static final String BOOSTER_PACK__LIST_BACKGROUND_IMG_NAME = "list_background_img_name";
	public static final String BOOSTER_PACK__LIST_DESCRIPTION = "list_description";
	public static final String BOOSTER_PACK__NAV_BAR_IMG_NAME = "nav_bar_img_name";
	public static final String BOOSTER_PACK__NAV_TITLE_IMG_NAME = "nav_title_img_name";
	public static final String BOOSTER_PACK__MACHINE_IMG_NAME = "machine_img_name";
	public static final String BOOSTER_PACK__EXP_PER_ITEM = "exp_per_item";
	public static final String BOOSTER_PACK__DISPLAY_TO_USER = "display_to_user";
	public static final String BOOSTER_PACK__RIGGED_ID = "rigged_id";
	public static final String BOOSTER_PACK__TYPE = "type";

	public static final String CLAN_EVENT_PERSISTENT__ID = GENERIC__ID;
	public static final String CLAN_EVENT_PERSISTENT__DAY_OF_WEEK = "day_of_week";
	public static final String CLAN_EVENT_PERSISTENT__START_HOUR = "start_hour";
	public static final String CLAN_EVENT_PERSISTENT__EVENT_DURATION_MINUTES = "event_duration_minutes";
	public static final String CLAN_EVENT_PERSISTENT__CLAN_RAID_ID = "clan_raid_id";

	public static final String CLAN_ICON__ID = GENERIC__ID;
	public static final String CLAN_ICON__IMG_NAME = "img_name";
	public static final String CLAN_ICON__IS_AVAILABLE = "is_available";

	public static final String CUSTOM_MENU__ID = "custom_menu_id";
	public static final String CUSTOM_MENU__POSITION_X = "position_x";
	public static final String CUSTOM_MENU__POSITION_Y = "position_y";
	public static final String CUSTOM_MENU__POSITION_Z = "position_z";
    public static final String CUSTOM_MENU__IS_JIGGLE = "is_jiggle";
    public static final String CUSTOM_MENU__IMAGE_NAME = "image_name";

	public static final String EVENT_PERSISTENT__ID = GENERIC__ID;
	public static final String EVENT_PERSISTENT__DAY_OF_WEEK = "day_of_week";
	public static final String EVENT_PERSISTENT__START_HOUR = "start_hour";
	public static final String EVENT_PERSISTENT__EVENT_DURATION_MINUTES = "event_duration_minutes";
	public static final String EVENT_PERSISTENT__TASK_ID = "task_id";
	public static final String EVENT_PERSISTENT__COOLDOWN_MINUTES = "cooldown_minutes";
	public static final String EVENT_PERSISTENT__EVENT_TYPE = "event_type";
	public static final String EVENT_PERSISTENT__MONSTER_ELEMENT = "monster_element";

	public static final String FILE_DOWNLOAD__ID = "id";
	public static final String FILE_DOWNLOAD__FILENAME = "filename";
	public static final String FILE_DOWNLOAD__PRIORITY = "priority";
	public static final String FILE_DOWNLOAD__DOWNLOAD_ONLY_OVER_WIFI = "download_only_over_wifi";
	public static final String FILE_DOWNLOAD__USE_IPHONE6_PREFIX = "use_iphone6_prefix";

	public static final String ITEM__ID = GENERIC__ID;
	public static final String ITEM__NAME = "name";
	public static final String ITEM__IMG_NAME = "img_name";
	public static final String ITEM__ITEM_TYPE = "item_type";
	public static final String ITEM__STATIC_DATA_ID = "static_data_id";
	public static final String ITEM__AMOUNT = "amount";
	public static final String ITEM__SECRET_GIFT_CHANCE = "secret_gift_chance";
	public static final String ITEM__ALWAYS_DISPLAY_TO_USER = "always_display_to_user";
	public static final String ITEM__SHORT_NAME = "short_name";
	public static final String ITEM__ACTION_GAME_TYPE = "action_game_type";
	public static final String ITEM__QUALITY = "quality";

	public static final String MINI_EVENT__ID = GENERIC__ID;
	public static final String MINI_EVENT__START_TIME = "start_time";
	public static final String MINI_EVENT__END_TIME = "end_time";
	public static final String MINI_EVENT__NAME = "name";
	public static final String MINI_EVENT__DESCRIPTION = "description";
	public static final String MINI_EVENT__IMG = "img";
	public static final String MINI_EVENT__ICON = "icon";

	public static final String MINI_EVENT_FOR_PLAYER_LVL__ID = GENERIC__ID;
	public static final String MINI_EVENT_FOR_PLAYER_LVL__MINI_EVENT_ID = "mini_event_id";
	public static final String MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MIN = "player_lvl_min";
	public static final String MINI_EVENT_FOR_PLAYER_LVL__PLAYER_LVL_MAX = "player_lvl_max";
	public static final String MINI_EVENT_FOR_PLAYER_LVL__TIER_ONE_MIN_PTS = "tier_one_min_pts";
	public static final String MINI_EVENT_FOR_PLAYER_LVL__TIER_TWO_MIN_PTS = "tier_two_min_pts";
	public static final String MINI_EVENT_FOR_PLAYER_LVL__TIER_THREE_MIN_PTS = "tier_three_min_pts";

	public static final String MINI_EVENT_GOAL__ID = GENERIC__ID;
	public static final String MINI_EVENT_GOAL__MINI_EVENT_ID = "mini_event_id";
	public static final String MINI_EVENT_GOAL__TYPE = "type";
	public static final String MINI_EVENT_GOAL__AMT = "amt";
	public static final String MINI_EVENT_GOAL__DESCRIPTION = "description";
	public static final String MINI_EVENT_GOAL__PTS_REWARD = "pts_reward";
	public static final String MINI_EVENT_GOAL__ACTION_DESCRIPTION = "action_description";

	public static final String MINI_EVENT_LEADERBOARD_REWARD__ID = GENERIC__ID;
	public static final String MINI_EVENT_LEADERBOARD_REWARD__MINI_EVENT_ID = "mini_event_id";
	public static final String MINI_EVENT_LEADERBOARD_REWARD__REWARD_ID = "reward_id";
	public static final String MINI_EVENT_LEADERBOARD_REWARD__LEADERBOARD_POS = "leaderboard_pos";

	public static final String MINI_EVENT_TIER_REWARD__ID = GENERIC__ID;
	public static final String MINI_EVENT_TIER_REWARD__MINI_EVENT_FOR_PLAYER_LVL_ID = "mini_event_for_player_lvl_id";
	public static final String MINI_EVENT_TIER_REWARD__REWARD_ID = "reward_id";
	public static final String MINI_EVENT_TIER_REWARD__REWARD_TIER = "reward_tier";

	public static final String MINI_JOB__ID = GENERIC__ID;
	public static final String MINI_JOB__REQUIRED_STRUCT_ID = "required_struct_id";
	public static final String MINI_JOB__NAME = "name";
	public static final String MINI_JOB__CASH_REWARD = "cash_reward";
	public static final String MINI_JOB__OIL_REWARD = "oil_reward";
	public static final String MINI_JOB__GEM_REWARD = "gem_reward";
	public static final String MINI_JOB__MONSTER_ID_REWARD = "monster_id_reward";
	public static final String MINI_JOB__ITEM_ID_REWARD = "item_id_reward";
	public static final String MINI_JOB__ITEM_REWARD_QUANTITY = "item_reward_quantity";
	public static final String MINI_JOB__SECOND_ITEM_ID_REWARD = "second_item_id_reward";
	public static final String MINI_JOB__SECOND_ITEM_REWARD_QUANTITY = "second_item_reward_quantity";
	public static final String MINI_JOB__QUALITY = "quality";
	public static final String MINI_JOB__MAX_NUM_MONSTERS_ALLOWED = "max_num_monsters_allowed";
	public static final String MINI_JOB__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String MINI_JOB__HP_REQUIRED = "hp_required";
	public static final String MINI_JOB__ATK_REQUIRED = "atk_required";
	public static final String MINI_JOB__MIN_DMG = "min_dmg";
	public static final String MINI_JOB__MAX_DMG = "max_dmg";
	public static final String MINI_JOB__DURATION_MIN_MINUTES = "duration_min_minutes";
	public static final String MINI_JOB__DURATION_MAX_MINUTES = "duration_max_minutes";
	public static final String MINI_JOB__EXP_REWARD = "exp_reward";

	public static final String MINI_JOB_REFRESH_ITEM__STRUCT_ID = "struct_id";
	public static final String MINI_JOB_REFRESH_ITEM__ITEM_ID = "item_id";
	public static final String MINI_JOB_REFRESH_ITEM__GEM_PRICE = "gem_price";

	public static final String MONSTER_BATTLE_DIALOGUE__ID = GENERIC__ID;
	public static final String MONSTER_BATTLE_DIALOGUE__MONSTER_ID = "monster_id";
	public static final String MONSTER_BATTLE_DIALOGUE__DIALOGUE_TYPE = "dialogue_type";
	public static final String MONSTER_BATTLE_DIALOGUE__DIALOGUE = "dialogue";
	public static final String MONSTER_BATTLE_DIALOGUE__PROBABILITY_UTTERED = "probability_uttered";

	public static final String MONSTER__ID = GENERIC__ID;
	public static final String MONSTER__EVOLUTION_GROUP = "evolution_group";
	public static final String MONSTER__MONSTER_GROUP = "monster_group";
	public static final String MONSTER__QUALITY = "quality";
	public static final String MONSTER__EVOLUTION_LEVEL = "evolution_level";
	public static final String MONSTER__DISPLAY_NAME = "display_name";
	public static final String MONSTER__ELEMENT = "element";
	public static final String MONSTER__IMAGE_PREFIX = "image_prefix";
	public static final String MONSTER__NUM_PUZZLE_PIECES = "num_puzzle_pieces";
	public static final String MONSTER__MINUTES_TO_COMBINE_PIECES = "minutes_to_combine_pieces";
	public static final String MONSTER__MAX_LEVEL = "max_level";
	public static final String MONSTER__EVOLUTION_MONSTER_ID = "evolution_monster_id";
	public static final String MONSTER__EVOLUTION_CATALYST_MONSTER_ID = "evolution_catalyst_monster_id";
	public static final String MONSTER__MINUTES_TO_EVOLVE = "minutes_to_evolve";
	public static final String MONSTER__NUM_EVOLUTION_CATALYSTS = "num_evolution_catalysts";
	public static final String MONSTER__CARROT_RECRUITED = "carrot_recruited";
	public static final String MONSTER__CARROT_DEFEATED = "carrot_defeated";
	public static final String MONSTER__CARROT_EVOLVED = "carrot_evolved";
	public static final String MONSTER__DESCRIPTION = "description";
	public static final String MONSTER__EVOLUTION_COST = "evolution_cost";
	public static final String MONSTER__ANIMATION_TYPE = "animation_type";
	public static final String MONSTER__VERTICAL_PIXEL_OFFSET = "vertical_pixel_offset";
	public static final String MONSTER__ATK_SOUND_FILE = "atk_sound_file";
	public static final String MONSTER__ATK_SOUND_ANIMATION_FRAME = "atk_sound_animation_frame";
	public static final String MONSTER__ATK_ANIMATION_REPEATED_FRAMES_START = "atk_animation_repeated_frames_start";
	public static final String MONSTER__ATK_ANIMATION_REPEATED_FRAMES_END = "atk_animation_repeated_frames_end";
	public static final String MONSTER__SHORT_NAME = "short_name";
	public static final String MONSTER__SHADOW_SCALE_FACTOR = "shadow_scale_factor";
	public static final String MONSTER__BASE_OFFENSIVE_SKILL_ID = "base_offensive_skill_id";
	public static final String MONSTER__BASE_DEFENSIVE_SKILL_ID = "base_defensive_skill_id";
	public static final String MONSTER__PVP_MONSTER_DROP_ID = "pvp_monster_drop_id";

	public static final String MONSTER_FOR_PVP__ID = GENERIC__ID;
	public static final String MONSTER_FOR_PVP__MONSTER_ID = "monster_id";
	public static final String MONSTER_FOR_PVP__MONSTER_LVL = "monster_lvl";
	public static final String MONSTER_FOR_PVP__ELO = "elo";
	public static final String MONSTER_FOR_PVP__MIN_CASH_REWARD = "min_cash_reward";
	public static final String MONSTER_FOR_PVP__MAX_CASH_REWARD = "max_cash_reward";
	public static final String MONSTER_FOR_PVP__MIN_OIL_REWARD = "min_oil_reward";
	public static final String MONSTER_FOR_PVP__MAX_OIL_REWARD = "max_oil_reward";

	public static final String MONSTER_LEVEL_INFO__MONSTER_ID = "monster_id";
	public static final String MONSTER_LEVEL_INFO__LEVEL = "level";
	public static final String MONSTER_LEVEL_INFO__HP = "hp";
	public static final String MONSTER_LEVEL_INFO__CUR_LVL_REQUIRED_EXP = "cur_lvl_required_exp";
	public static final String MONSTER_LEVEL_INFO__FEEDER_EXP = "feeder_exp";
	public static final String MONSTER_LEVEL_INFO__FIRE_DMG = "fire_dmg";
	public static final String MONSTER_LEVEL_INFO__GRASS_DMG = "grass_dmg";
	public static final String MONSTER_LEVEL_INFO__WATER_DMG = "water_dmg";
	public static final String MONSTER_LEVEL_INFO__LIGHTNING_DMG = "lightning_dmg";
	public static final String MONSTER_LEVEL_INFO__DARKNESS_DMG = "darkness_dmg";
	public static final String MONSTER_LEVEL_INFO__ROCK_DMG = "rock_dmg";
	public static final String MONSTER_LEVEL_INFO__SPEED = "speed";
	public static final String MONSTER_LEVEL_INFO__HP_EXPONENT_BASE = "hp_exponent_base";
	public static final String MONSTER_LEVEL_INFO__DMG_EXPONENT_BASE = "dmg_exponent_base";
	public static final String MONSTER_LEVEL_INFO__EXP_LVL_DIVISOR = "exp_lvl_divisor";
	public static final String MONSTER_LEVEL_INFO__EXP_LVL_EXPONENT = "exp_lvl_exponent";
	public static final String MONSTER_LEVEL_INFO__SELL_AMOUNT = "sell_amount";
	public static final String MONSTER_LEVEL_INFO__TEAM_COST = "team_cost";
	public static final String MONSTER_LEVEL_INFO__COST_TO_FULLY_HEAL = "cost_to_fully_heal";
	public static final String MONSTER_LEVEL_INFO__COST_TO_FULLY_HEAL_EXPONENT = "cost_to_fully_heal_exponent";
	public static final String MONSTER_LEVEL_INFO__SECS_TO_FULLY_HEAL = "secs_to_fully_heal";
	public static final String MONSTER_LEVEL_INFO__SECS_TO_FULLY_HEAL_EXPONENT = "secs_to_fully_heal_exponent";
	public static final String MONSTER_LEVEL_INFO__ENHANCE_COST_PER_FEEDER = "enhance_cost_per_feeder";
	public static final String MONSTER_LEVEL_INFO__ENHANCE_COST_EXPONENT = "enhance_cost_exponent";
	public static final String MONSTER_LEVEL_INFO__SECONDS_TO_ENHANCE_PER_FEEDER = "seconds_to_enhance_per_feeder";
	public static final String MONSTER_LEVEL_INFO__SECONDS_TO_ENHANCE_PER_FEEDER_EXPONENT = "seconds_to_enhance_per_feeder_exponent";
	public static final String MONSTER_LEVEL_INFO__PVP_DROP_RATE = "pvp_drop_rate";
	public static final String MONSTER_LEVEL_INFO__STRENGTH = "strength";
	public static final String MONSTER_LEVEL_INFO__STRENGTH_EXPONENT = "strength_exponent";

	public static final String OBSTACLE__ID = GENERIC__ID;
	public static final String OBSTACLE__NAME = "name";
	public static final String OBSTACLE__REMOVAL_COST_TYPE = "removal_cost_type";
	public static final String OBSTACLE__COST = "cost";
	public static final String OBSTACLE__SECONDS_TO_REMOVE = "seconds_to_remove";
	public static final String OBSTACLE__WIDTH = "width";
	public static final String OBSTACLE__HEIGHT = "height";
	public static final String OBSTACLE__IMG_NAME = "img_name";
	public static final String OBSTACLE__IMG_VERTICAL_PIXEL_OFFSET = "img_vertical_pixel_offset";
	public static final String OBSTACLE__DESCRIPTION = "description";
	public static final String OBSTACLE__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String OBSTACLE__SHADOW_IMG_NAME = "shadow_img_name";
	public static final String OBSTACLE__SHADOW_VERTICAL_OFFSET = "shadow_vertical_offset";
	public static final String OBSTACLE__SHADOW_HORIZONTAL_OFFSET = "shadow_horizontal_offset";

	public static final String PREREQUISITE__ID = GENERIC__ID;
	public static final String PREREQUISITE__GAME_TYPE = "game_type";
	public static final String PREREQUISITE__GAME_ENTITY_ID = "game_entity_id";
	public static final String PREREQUISITE__PREREQ_GAME_TYPE = "prereq_game_type";
	public static final String PREREQUISITE__PREREQ_GAME_ENTITY_ID = "prereq_game_entity_id";
	public static final String PREREQUISITE__QUANTITY = "quantity";

	public static final String PROFANITY__TERM = "term";

	public static final String PVP_LEAGUE__ID = GENERIC__ID;
	public static final String PVP_LEAGUE__LEAGUE_NAME = "league_name";
	public static final String PVP_LEAGUE__IMG_PREFIX = "img_prefix";
	public static final String PVP_LEAGUE__DESCRIPTION = "description";
	public static final String PVP_LEAGUE__MIN_ELO = "min_elo";
	public static final String PVP_LEAGUE__MAX_ELO = "max_elo";
	public static final String PVP_LEAGUE__MIN_RANK = "min_rank";
	public static final String PVP_LEAGUE__MAX_RANK = "max_rank";
	public static final String PVP_LEAGUE__PREDECESSOR_ID = "predecessor_id";
	public static final String PVP_LEAGUE__SUCCESSOR_ID = "successor_id";

	public static final String QUEST__ID = GENERIC__ID;
	public static final String QUEST__QUEST_NAME = "quest_name";
	public static final String QUEST__DESCRIPTION = "description";
	public static final String QUEST__DONE_RESPONSE = "done_response";
	public static final String QUEST__ACCEPT_DIALOGUE = "accept_dialogue";
	public static final String QUEST__CASH_REWARD = "cash_reward";
	public static final String QUEST__OIL_REWARD = "oil_reward";
	public static final String QUEST__GEM_REWARD = "gem_reward";
	public static final String QUEST__EXP_REWARD = "exp_reward";
	public static final String QUEST__MONSTER_ID_REWARD = "monster_id_reward";
	public static final String QUEST__IS_COMPLETE_MONSTER = "is_complete_monster";
	public static final String QUEST__QUESTS_REQUIRED_FOR_THIS = "quests_required_for_this";
	public static final String QUEST__QUEST_GIVER_NAME = "quest_giver_name";
	public static final String QUEST__QUEST_GIVER_IMAGE_PREFIX = "quest_giver_image_prefix";
	public static final String QUEST__PRIORITY = "priority";
	public static final String QUEST__CARROT_ID = "carrot_id";
	public static final String QUEST__MONSTER_ELEMENT = "monster_element";

	public static final String QUEST_JOB__ID = GENERIC__ID;
	public static final String QUEST_JOB__QUEST_ID = "quest_id";
	public static final String QUEST_JOB__QUEST_JOB_TYPE = "quest_job_type";
	public static final String QUEST_JOB__DESCRIPTION = "description";
	public static final String QUEST_JOB__STATIC_DATA_ID = "static_data_id";
	public static final String QUEST_JOB__QUANTITY = "quantity";
	public static final String QUEST_JOB__PRIORITY = "priority";
	public static final String QUEST_JOB__CITY_ID = "city_id";
	public static final String QUEST_JOB__CITY_ASSET_NUM = "city_asset_num";

	public static final String QUEST_JOB_MONSTER_ITEM__QUEST_JOB_ID = "quest_job_id";
	public static final String QUEST_JOB_MONSTER_ITEM__MONSTER_ID = "monster_id";
	public static final String QUEST_JOB_MONSTER_ITEM__ITEM_ID = "item_id";
	public static final String QUEST_JOB_MONSTER_ITEM__ITEM_DROP_RATE = "item_drop_rate";

	public static final String RESEARCH__ID = GENERIC__ID;
	public static final String RESEARCH__RESEARCH_TYPE = "research_type";
	public static final String RESEARCH__RESEARCH_DOMAIN = "research_domain";
	public static final String RESEARCH__ICON_IMG_NAME = "icon_img_name";
	public static final String RESEARCH__NAME = "name";
	public static final String RESEARCH__PRED_ID = "pred_id";
	public static final String RESEARCH__SUCC_ID = "succ_id";
	public static final String RESEARCH__DESC = "desc";
	public static final String RESEARCH__DURATION_MIN = "duration_min";
	public static final String RESEARCH__COST_AMT = "cost_amt";
	public static final String RESEARCH__COST_TYPE = "cost_type";
	public static final String RESEARCH__LEVEL = "level";
	public static final String RESEARCH__PRIORITY = "priority";
	public static final String RESEARCH__TIER = "tier";
	public static final String RESEARCH__STRENGTH = "strength";

	public static final String RESEARCH_PROPERTY__ID = GENERIC__ID;
	public static final String RESEARCH_PROPERTY__RESEARCH_ID = "research_id";
	public static final String RESEARCH_PROPERTY__NAME = "name";
	public static final String RESEARCH_PROPERTY__VALUE = "value";

	public static final String SALES_DISPLAY_ITEM__ID = GENERIC__ID;
	public static final String SALES_DISPLAY_ITEM__SALES_PACKAGE_ID = "sales_package_id";
	public static final String SALES_DISPLAY_ITEM__REWARD_ID = "reward_id";

	public static final String SALES_ITEM__ID = GENERIC__ID;
	public static final String SALES_ITEM__SALES_PACKAGE_ID = "sales_package_id";
	public static final String SALES_ITEM__REWARD_ID = "reward_id";

	public static final String SALES_PACKAGE__ID = GENERIC__ID;
	public static final String SALES_PACKAGE__PRODUCT_ID = "product_id";
	public static final String SALES_PACKAGE__NAME = "name";
	public static final String SALES_PACKAGE__PRICE = "price";
	public static final String SALES_PACKAGE__UUID = "uuid";
	public static final String SALES_PACKAGE__START_TIME = "time_start";
	public static final String SALES_PACKAGE__END_TIME = "time_end";
    public static final String SALES_PACKAGE__SUCC_ID = "succ_id";
    public static final String SALES_PACKAGE__CUSTOM_MENU_ID = "custom_menu_id";
    public static final String SALES_PACKAGE__ANIMATING_ICON = "animating_icon";
    public static final String SALES_PACKAGE__SLAM_ICON = "slam_icon";
    public static final String SALES_PACKAGE__TITLE_COLOR = "title_color";

	public static final String REWARD__ID = GENERIC__ID;
	public static final String REWARD__STATIC_DATA_ID = "static_data_id";
	public static final String REWARD__TYPE ="type";
	public static final String REWARD__AMT = "amt";

	public static final String SERVER_TOGGLE__ID = GENERIC__ID;
	public static final String SERVER_TOGGLE__NAME = "name";
	public static final String SERVER_TOGGLE__ON = "on";

	public static final String SKILL__ID = GENERIC__ID;
	public static final String SKILL__NAME = "name";
	public static final String SKILL__ORB_COST = "orb_cost";
	public static final String SKILL__TYPE = "type";
	public static final String SKILL__ACTIVATION_TYPE = "activation_type";
	public static final String SKILL__PREDEC_ID = "predec_id";
	public static final String SKILL__SUCC_ID = "succ_id";
	public static final String SKILL__DEFENSIVE_DESC = "defensive_desc";
	public static final String SKILL__OFFENSIVE_DESC = "offensive_desc";
	public static final String SKILL__IMG_NAME_PREFIX = "img_name_prefix";
	public static final String SKILL__SKILL_EFFECT_DURATION = "skill_effect_duration";
	public static final String SKILL__SHORT_DEF_DESC = "short_def_desc";
	public static final String SKILL__SHORT_OFF_DESC = "short_off_desc";
	//	public static final String SKILL__ICON_IMG_NAME = "icon_img_name";
	//Animation shown when skill activates.
	//	public static final String SKILL__LOGO_IMG_NAME = "logo_img_name";

	public static final String SKILL_PROPERTY__ID = GENERIC__ID;
	public static final String SKILL_PROPERTY__NAME = "name";
	public static final String SKILL_PROPERTY__VALUE = "value";
	public static final String SKILL_PROPERTY__SKILL_ID = "skill_id";

	public static final String SKILL_SIDE_EFFECT__ID = GENERIC__ID;
	public static final String SKILL_SIDE_EFFECT__NAME = "name";
	public static final String SKILL_SIDE_EFFECT__DESC = "desc";
	public static final String SKILL_SIDE_EFFECT__TYPE = "type";
	public static final String SKILL_SIDE_EFFECT__TRAIT_TYPE = "trait_type";
	public static final String SKILL_SIDE_EFFECT__IMG_NAME = "img_name";
	public static final String SKILL_SIDE_EFFECT__IMG_PIXEL_OFFSET_X = "img_pixel_offset_x";
	public static final String SKILL_SIDE_EFFECT__IMG_PIXEL_OFFSET_Y = "img_pixel_offset_y";
	public static final String SKILL_SIDE_EFFECT__ICON_IMG_NAME = "icon_img_name";
	public static final String SKILL_SIDE_EFFECT__PFX_NAME = "pfx_name";
	public static final String SKILL_SIDE_EFFECT__PFX_COLOR = "pfx_color";
	public static final String SKILL_SIDE_EFFECT__POSITION_TYPE = "position_type";
	public static final String SKILL_SIDE_EFFECT__PFX_PIXEL_OFFSET_X = "pfx_pixel_offset_x";
	public static final String SKILL_SIDE_EFFECT__PFX_PIXEL_OFFSET_Y = "pfx_pixel_offset_y";
	public static final String SKILL_SIDE_EFFECT__BLEND_MODE = "blend_mode";

	public static final String STATIC_LEVEL_INFO__LEVEL_ID = "level_id";
	public static final String STATIC_LEVEL_INFO__REQUIRED_EXPERIENCE = "required_experience";

	public static final String STRUCTURE__ID = "id";
	public static final String STRUCTURE__NAME = "name";
	public static final String STRUCTURE__LEVEL = "level";
	public static final String STRUCTURE__STRUCT_TYPE = "struct_type";
	public static final String STRUCTURE__BUILD_RESOURCE_TYPE = "build_resource_type";
	public static final String STRUCTURE__BUILD_COST = "build_cost";
	public static final String STRUCTURE__MINUTES_TO_BUILD = "minutes_to_build";
	public static final String STRUCTURE__REQUIRED_TOWN_HALL_LVL = "required_town_hall_lvl";
	public static final String STRUCTURE__WIDTH = "width";
	public static final String STRUCTURE__HEIGHT = "height";
	public static final String STRUCTURE__PREDECESSOR_STRUCT_ID = "predecessor_struct_id";
	public static final String STRUCTURE__SUCCESSOR_STRUCT_ID = "successor_struct_id";
	public static final String STRUCTURE__IMG_NAME = "img_name";
	public static final String STRUCTURE__IMG_VERTICAL_PIXEL_OFFSET = "img_vertical_pixel_offset";
	public static final String STRUCTURE__IMG_HORIZONTAL_PIXEL_OFFSET = "img_horizontal_pixel_offset";
	public static final String STRUCTURE__DESCRIPTION = "description";
	public static final String STRUCTURE__SHORT_DESCRIPTION = "short_description";
	public static final String STRUCTURE__SHADOW_IMG_NAME = "shadow_img_name";
	public static final String STRUCTURE__SHADOW_VERTICAL_OFFSET = "shadow_vertical_offset";
	public static final String STRUCTURE__SHADOW_HORIZONTAL_OFFSET = "shadow_horizontal_offset";
	public static final String STRUCTURE__SHADOW_SCALE = "shadow_scale";
	public static final String STRUCTURE__EXP_REWARD = "exp_reward";
	public static final String STRUCTURE__STRENGTH = "strength";

	public static final String STRUCTURE_BATTLE_ITEM_FACTORY__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_BATTLE_ITEM_FACTORY__POWER_LIMIT = "power_limit";

	public static final String STRUCTURE_CLAN_HOUSE__STRUCT_ID = "struct_id";
	//clan helps
	public static final String STRUCTURE_CLAN_HOUSE__MAX_HELPERS_PER_SOLICITATION = "max_helpers_per_solicitation";
	public static final String STRUCTURE_CLAN_HOUSE__TEAM_DONATION_POWER_LIMIT = "team_donation_power_limit";

	public static final String STRUCTURE_EVO__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_EVO__QUALITY_UNLOCKED = "quality_unlocked";
	public static final String STRUCTURE_EVO__EVO_TIER_UNLOCKED = "evo_tier_unlocked";

	public static final String STRUCTURE_HOSPITAL__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_HOSPITAL__QUEUE_SIZE = "queue_size";
	public static final String STRUCTURE_HOSPITAL__HEALTH_PER_SECOND = "health_per_second";
	public static final String STRUCTURE_HOSPITAL__SECS_TO_FULLY_HEAL_MULTIPLIER = "secs_to_fully_heal_multiplier";

	public static final String STRUCTURE_LAB__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_LAB__QUEUE_SIZE = "queue_size";
	public static final String STRUCTURE_LAB__POINTS_MULTIPLIER = "points_multiplier";

	public static final String STRUCTURE_MINI_JOB__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_MINI_JOB__GENERATED_JOB_LIMIT = "generated_job_limit";
	public static final String STRUCTURE_MINI_JOB__HOURS_BETWEEN_JOB_GENERATION = "hours_between_job_generation";

	public static final String STRUCTURE_MONEY_TREE__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_MONEY_TREE__PRODUCTION_RATE = "production_rate";
	public static final String STRUCTURE_MONEY_TREE__CAPACITY = "capacity";
	public static final String STRUCTURE_MONEY_TREE__DAYS_OF_DURATION = "days_of_duration";
	public static final String STRUCTURE_MONEY_TREE__DAYS_FOR_RENEWAL = "days_for_renewal";
	public static final String STRUCTURE_MONEY_TREE__IAP_PRODUCT_ID = "iap_product_id";
	public static final String STRUCTURE_MONEY_TREE__FAKE_IAP_PRODUCT_ID = "fake_iap_product_id";

	public static final String STRUCTURE_PVP_BOARD__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_PVP_BOARD__POWER_LIMIT = "power_limit";

	public static final String STRUCTURE_RESEARCH_HOUSE__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_RESEARCH_HOUSE__RESEARCH_SPEED_MULTIPLIER = "research_speed_multiplier";

	public static final String STRUCTURE_RESIDENCE__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_RESIDENCE__NUM_MONSTER_SLOTS = "num_monster_slots";
	public static final String STRUCTURE_RESIDENCE__NUM_BONUS_MONSTER_SLOTS = "num_bonus_monster_slots";
	public static final String STRUCTURE_RESIDENCE__NUM_GEMS_REQUIRED = "num_gems_required";
	public static final String STRUCTURE_RESIDENCE__NUM_ACCEPETED_FB_INVITES = "num_accepeted_fb_invites";
	public static final String STRUCTURE_RESIDENCE__OCCUPATION_NAME = "occupation_name";
	public static final String STRUCTURE_RESIDENCE__IMG_SUFFIX = "img_suffix";

	public static final String STRUCTURE_RESOURCE_GENERATOR__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_RESOURCE_GENERATOR__RESOURCE_TYPE_GENERATED = "resource_type_generated";
	public static final String STRUCTURE_RESOURCE_GENERATOR__PRODUCTION_RATE = "production_rate";
	public static final String STRUCTURE_RESOURCE_GENERATOR__CAPACITY = "capacity";

	public static final String STRUCTURE_RESOURCE_STORAGE__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_RESOURCE_STORAGE__RESOURCE_TYPE_STORED = "resource_type_stored";
	public static final String STRUCTURE_RESOURCE_STORAGE__CAPACITY = "capacity";

	public static final String STRUCTURE_TEAM_CENTER__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_TEAM_CENTER__TEAM_COST_LIMIT = "team_cost_limit";

	public static final String STRUCTURE_TOWN_HALL__STRUCT_ID = "struct_id";
	public static final String STRUCTURE_TOWN_HALL__NUM_RESOURCE_ONE_GENERATORS = "num_resource_one_generators";
	public static final String STRUCTURE_TOWN_HALL__NUM_RESOURCE_ONE_STORAGES = "num_resource_one_storages";
	public static final String STRUCTURE_TOWN_HALL__NUM_RESOURCE_TWO_GENERATORS = "num_resource_two_generators";
	public static final String STRUCTURE_TOWN_HALL__NUM_RESOURCE_TWO_STORAGES = "num_resource_two_storages";
	public static final String STRUCTURE_TOWN_HALL__NUM_HOSPITALS = "num_hospitals";
	public static final String STRUCTURE_TOWN_HALL__NUM_RESIDENCES = "num_residences";
	public static final String STRUCTURE_TOWN_HALL__NUM_MONSTER_SLOTS = "num_monster_slots";
	public static final String STRUCTURE_TOWN_HALL__NUM_LABS = "num_labs";
	public static final String STRUCTURE_TOWN_HALL__PVP_QUEUE_CASH_COST = "pvp_queue_cash_cost";
	public static final String STRUCTURE_TOWN_HALL__RESOURCE_CAPACITY = "resource_capacity";
	public static final String STRUCTURE_TOWN_HALL__NUM_EVO_CHAMBERS = "num_evo_chambers";

	public static final String TASK__ID = "id";
	public static final String TASK__GOOD_NAME = "good_name";
	public static final String TASK__DESCRIPTION = "description";
	public static final String TASK__CITY_ID = "city_id";
	public static final String TASK__ASSET_NUM_WITHIN_CITY = "asset_num_within_city";
	public static final String TASK__PREREQUISITE_TASK_ID = "prerequisite_task_id";
	public static final String TASK__PREREQUISITE_QUEST_ID = "prerequisite_quest_id";
	public static final String TASK__BOARD_WIDTH = "board_width";
	public static final String TASK__BOARD_HEIGHT = "board_height";
	public static final String TASK__GROUND_IMG_PREFIX = "ground_img_prefix";
	public static final String TASK__INIT_DEFEATED_DIALOGUE = "init_defeated_dialogue";
	public static final String TASK__EXP_REWARD = "exp_reward";
	public static final String TASK__BOARD_ID = "board_id";

	public static final String TASK_MAP_ELEMENT__ID = GENERIC__ID;
	public static final String TASK_MAP_ELEMENT__TASK_ID = "task_id";
	public static final String TASK_MAP_ELEMENT__X_POS = "x_pos";
	public static final String TASK_MAP_ELEMENT__Y_POS = "y_pos";
	public static final String TASK_MAP_ELEMENT__ELEMENT = "element";
	public static final String TASK_MAP_ELEMENT__IS_BOSS = "is_boss";
	public static final String TASK_MAP_ELEMENT__BOSS_IMG_NAME = "boss_img_name";
	public static final String TASK_MAP_ELEMENT__ITEM_DROP_ID = "item_drop_id";
	public static final String TASK_MAP_ELEMENT__SECTION_NAME = "section_name";
	public static final String TASK_MAP_ELEMENT__CASH_REWARD = "cash_reward";
	public static final String TASK_MAP_ELEMENT__OIL_REWARD = "oil_reward";
	public static final String TASK_MAP_ELEMENT__CHARACTER_IMG_NAME = "character_img_name";
	public static final String TASK_MAP_ELEMENT__CHAR_VERT_PIXEL_OFFSET = "char_vert_pixel_offset";
	public static final String TASK_MAP_ELEMENT__CHAR_HORIZ_PIXEL_OFFSET = "char_horiz_pixel_offset";
	public static final String TASK_MAP_ELEMENT__CHAR_SCALE_FACTOR = "char_scale_factor";
	public static final String TASK_MAP_ELEMENT__IS_FAKE = "is_fake";
	public static final String TASK_MAP_ELEMENT__STRENGTH = "strength";

	public static final String TASK_STAGE_MONSTER__ID = GENERIC__ID;
	public static final String TASK_STAGE_MONSTER__TASK_STAGE_ID = "task_stage_id";
	public static final String TASK_STAGE_MONSTER__MONSTER_ID = "monster_id";
	public static final String TASK_STAGE_MONSTER__MONSTER_TYPE = "monster_type";
	public static final String TASK_STAGE_MONSTER__EXP_REWARD = "exp_reward";
	public static final String TASK_STAGE_MONSTER__MIN_CASH_DROP = "min_cash_drop";
	public static final String TASK_STAGE_MONSTER__MAX_CASH_DROP = "max_cash_drop";
	public static final String TASK_STAGE_MONSTER__MIN_OIL_DROP = "min_oil_drop";
	public static final String TASK_STAGE_MONSTER__MAX_OIL_DROP = "max_oil_drop";
	public static final String TASK_STAGE_MONSTER__PUZZLE_PIECE_DROP_RATE = "puzzle_piece_drop_rate";
	public static final String TASK_STAGE_MONSTER__LEVEL = "level";
	public static final String TASK_STAGE_MONSTER__CHANCE_TO_APPEAR = "chance_to_appear";
	public static final String TASK_STAGE_MONSTER__DMG_MULTIPLIER = "dmg_multiplier";
	public static final String TASK_STAGE_MONSTER__MONSTER_ID_DROP = "monster_id_drop";
	public static final String TASK_STAGE_MONSTER__MONSTER_DROP_LVL = "monster_drop_lvl";
	public static final String TASK_STAGE_MONSTER__DEFENSIVE_SKILL_ID = "defensive_skill_id";
	public static final String TASK_STAGE_MONSTER__INIT_DIALOGUE = "init_dialogue";
	public static final String TASK_STAGE_MONSTER__DEFAULT_DIALOGUE = "default_dialogue";

	public static final String TASK_STAGE__ID = GENERIC__ID;
	public static final String TASK_STAGE__TASK_ID = "task_id";
	public static final String TASK_STAGE__STAGE_NUM = "stage_num";
	public static final String TASK_STAGE__ATTACKER_ALWAYS_HITS_FIRST = "attacker_always_hits_first";

}
