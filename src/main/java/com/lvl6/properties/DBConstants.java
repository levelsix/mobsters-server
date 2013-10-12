package com.lvl6.properties;

//SPECIFYING COLUMNS OF STATIC DATA TABLES UNNECESSARY
public class DBConstants {
  /* TABLENAMES*/
	public static final String TABLE_AVAILABLE_REFERRAL_CODES = "available_referral_code";
	public static final String TABLE_BATTLE_HISTORY = "battle_history";
	public static final String TABLE_BOOSTER_PACK = "booster_pack";
	public static final String TABLE_USER_BOOSTER_PACK_HISTORY = "booster_pack_history_for_user";
	public static final String TABLE_BOOSTER_ITEM = "booster_item";
	public static final String TABLE_CITIES = "city";
	public static final String TABLE_NEUTRAL_CITY_ELEMENTS = "city_element";
	public static final String TABLE_CLANS = "clan";
	public static final String TABLE_CLAN_CHAT_POST = "clan_chat_post";
	public static final String TABLE_USER_CLANS = "clan_for_user";
	public static final String TABLE_CITY_EXPANSION_COST = "expansion_cost";
	public static final String TABLE_USER_CITY_EXPANSION_DATA = "expansion_purchase_for_user";
	public static final String TABLE_GOLD_SALES = "gold_sale_event";	
	public static final String TABLE_IAP_HISTORY = "iap_history_for_user";
	public static final String TABLE_JOBS_BUILD_STRUCT = "job_build_struct";
	public static final String TABLE_JOBS_UPGRADE_STRUCT = "job_upgrade_struct";
	public static final String TABLE_LEADERBOARD_EVENTS = "leaderboard_event";	
	public static final String TABLE_USER_LEADERBOARD_EVENTS = "leaderboard_event_for_user";
	public static final String TABLE_LEADERBOARD_EVENT_REWARDS = "leaderboard_event_reward";
  public static final String TABLE_LEVELS_REQUIRED_EXPERIENCE = "level_required_experience";
  public static final String TABLE_LOCK_BOX_EVENTS = "lock_box_event";
  public static final String TABLE_USER_LOCK_BOX_EVENTS = "lock_box_event_for_user";
  public static final String TABLE_LOGIN_HISTORY = "login_history_for_user";	
  public static final String TABLE_EQUIPMENT = "monster";
  public static final String TABLE_DELETED_USER_EQUIPS_FOR_ENHANCING = "monster_deleted_for_enhancing_for_user";
  public static final String TABLE_EQUIP_ENHANCEMENT= "monster_enhancing_for_user";
  public static final String TABLE_EQUIP_ENHANCEMENT_HISTORY = "monster_enhancing_history_for_user";
  public static final String TABLE_EQUIP_ENHANCEMENT_FEEDERS = "monster_enhancing_feeder_for_user";  
  public static final String TABLE_EQUIP_ENHANCEMENT_FEEDERS_HISTORY = "monster_enhancing_feeder_history_for_user";  
  public static final String TABLE_BLACKSMITH = "monster_evolution_for_user";
  public static final String TABLE_BLACKSMITH_HISTORY = "monster_evolution_history_for_user";
  public static final String TABLE_USER_AND_EQUIP_FAIL = "monster_evolution_fail_for_user";
  public static final String TABLE_USER_EQUIP = "monster_for_user";
  public static final String TABLE_MONSTER_REWARD = "monster_reward";
  public static final String TABLE_PRIVATE_CHAT_POSTS = "private_chat_post";
  public static final String TABLE_PROFANITY = "profanity";
  public static final String TABLE_QUESTS = "quest";
  public static final String TABLE_USER_QUESTS = "quest_for_user";
  public static final String TABLE_REFERRALS = "referral";
  public static final String TABLE_USER_SESSIONS = "session_for_user";
  public static final String TABLE_STRUCTURES = "structure";
  public static final String TABLE_USER_STRUCTS = "structure_for_user";
  public static final String TABLE_TASKS = "task";
  public static final String TABLE_USER_TASK = "task_for_user";
  public static final String TABLE_USER_TASK_HISTORY = "task_history_for_user";
  public static final String TABLE_TASK_STAGE = "task_stage";
  public static final String TABLE_USER_TASK_STAGE = "task_stage_for_user";
  public static final String TABLE_USER_TASK_STAGE_HISTORY = "task_stage_history_for_user";
  public static final String TABLE_TASK_STAGE_MONSTER = "task_stage_monster";
  public static final String TABLE_USER = "user";
  public static final String TABLE_FIRST_TIME_USERS = "user_pretutorial";
  public static final String TABLE_BANNED_USER = "user_banned";
  public static final String TABLE_USER_CURRENCY_HISTORY = "user_currency_history";
  
	
  public static final String TABLE_USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS = "user_quests_completed_defeat_type_jobs";
  public static final String TABLE_USER_QUESTS_COMPLETED_TASKS = "user_quests_completed_tasks";
  public static final String TABLE_USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS = "user_quests_defeat_type_job_progress";
  public static final String TABLE_USER_QUESTS_TASK_PROGRESS = "user_quests_task_progress";
  public static final String TABLE_JOBS_DEFEAT_TYPE = "jobs_defeat_type";
  /*COLUMNNAMES*/
  public static final String GENERIC__USER_ID = "user_id";
  public static final String GENERIC__ID = "id";
  
  /*AVAILABLE REFERRAL CODES*/
  public static final String AVAILABLE_REFERRAL_CODES__ID = GENERIC__ID;
  public static final String AVAILABLE_REFERRAL_CODES__CODE = "code";

  /*BATTLE HISTORY*/
  public static final String BATTLE_HISTORY__ATTACKER_ID = "attacker_id";
  public static final String BATTLE_HISTORY__DEFENDER_ID = "defender_id";
  public static final String BATTLE_HISTORY__RESULT = "result";
  public static final String BATTLE_HISTORY__BATTLE_COMPLETE_TIME = "battle_complete_time";
  public static final String BATTLE_HISTORY__COINS_STOLEN = "coins_stolen";
  public static final String BATTLE_HISTORY__EQUIP_STOLEN = "equip_stolen";
  public static final String BATTLE_HISTORY__EXP_GAINED = "exp_gained";
  public static final String BATTLE_HISTORY__STOLEN_EQUIP_LEVEL = "stolen_equip_level";

  /*BOOSTER PACK*/
  public static final String BOOSTER_PACK__ID = GENERIC__ID;
  public static final String BOOSTER_PACK__COSTS_COINS = "costs_coins";
  public static final String BOOSTER_PACK__SALE_PRICE = "sale_price";
  public static final String BOOSTER_PACK__RETAIL_PRICE = "retail_price";
  public static final String BOOSTER_PACK__NAME = "name";
  public static final String BOOSTER_PACK__CHEST_IMAGE = "chest_image";
  public static final String BOOSTER_PACK__MIDDLE_IMAGE = "middle_image";
  public static final String BOOSTER_PACK__BACKGROUND_IMAGE = "background_image";
  public static final String BOOSTER_PACK__MIN_LEVEL = "min_level";
  public static final String BOOSTER_PACK__MAX_LEVEL = "max_level";

  /*USER BOOSTER PACK HISTORY*/
  public static final String USER_BOOSTER_PACK_HISTORY__ID = GENERIC__ID;
  public static final String USER_BOOSTER_PACK_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_BOOSTER_PACK_HISTORY__BOOSTER_PACK_ID = "booster_pack_id"; 
  public static final String USER_BOOSTER_PACK_HISTORY__NUM_BOUGHT = "num_bought";
  public static final String USER_BOOSTER_PACK_HISTORY__TIME_OF_PURCHASE = "time_of_purchase";
  public static final String USER_BOOSTER_PACK_HISTORY__RARITY_ONE_QUANTITY = "rarity_one_quantity";
  public static final String USER_BOOSTER_PACK_HISTORY__RARITY_TWO_QUANTITY = "rarity_two_quantity";
  public static final String USER_BOOSTER_PACK_HISTORY__RARITY_THREE_QUANTITY = "rarity_three_quantity";
  public static final String USER_BOOSTER_PACK_HISTORY__EXCLUDE_FROM_LIMIT_CHECK = "exclude_from_limit_check";
  public static final String USER_BOOSTER_PACK_HISTORY__EQUIP_IDS = "equip_ids";
  public static final String USER_BOOSTER_PACK_HISTORY__USER_EQUIP_IDS = "user_equip_ids";
  
  /*BOOSTER ITEM*/
  public static final String BOOSTER_ITEM__ID = GENERIC__ID;
  public static final String BOOSTER_ITEM__BOOSTER_PACK_ID = "booster_pack_id";
  public static final String BOOSTER_ITEM__EQUIP_ID = "equip_id";
  public static final String BOOSTER_ITEM__QUANTITY = "quantity";
  public static final String BOOSTER_ITEM__IS_SPECIAL = "is_special";
  
  /*CLANS*/
  public static final String CLANS__ID = "id";
  public static final String CLANS__OWNER_ID = "owner_id";
  public static final String CLANS__NAME = "name";
  public static final String CLANS__CREATE_TIME = "create_time";
  public static final String CLANS__DESCRIPTION = "description";
  public static final String CLANS__TAG = "tag";
  public static final String CLANS__REQUEST_TO_JOIN_REQUIRED = "request_to_join_required";
  
  /*CLAN_WALL_POSTS*/
  public static final String CLAN_WALL_POSTS__ID = GENERIC__ID;
  public static final String CLAN_WALL_POSTS__POSTER_ID = "poster_id";
  public static final String CLAN_WALL_POSTS__CLAN_ID = "clan_id";
  public static final String CLAN_WALL_POSTS__TIME_OF_POST = "time_of_post";
  public static final String CLAN_WALL_POSTS__CONTENT = "content";

  /*USER CLANS*/
  public static final String USER_CLANS__USER_ID = "user_id";
  public static final String USER_CLANS__CLAN_ID = "clan_id";
  public static final String USER_CLANS__STATUS = "status";
  public static final String USER_CLANS__REQUEST_TIME = "request_time";

  /*CITY EXPANSION COST*/
  public static final String CITY_EXPANSION_COST__ID = GENERIC__ID;
  public static final String CITY_EXPANSION_COST__EXPANSION_COST = "expansion_cost";

  /*USER CITY EXPANSION DATA*/
  public static final String USER_CITY_EXPANSION_DATA__USER_ID = GENERIC__USER_ID;
  public static final String USER_CITY_EXPANSION_DATA__X_POSITION = "x_position";
  public static final String USER_CITY_EXPANSION_DATA__Y_POSITION = "y_position";
  public static final String USER_CITY_EXPANSION_DATA__IS_EXPANDING = "is_expanding";
  public static final String USER_CITY_EXPANSION_DATA__EXPAND_START_TIME = "expand_start_time";

  /*IAP TABLE*/
  public static final String IAP_HISTORY__ID = GENERIC__ID;
  public static final String IAP_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String IAP_HISTORY__TRANSACTION_ID = "transaction_id";
  public static final String IAP_HISTORY__PURCHASE_DATE = "purchase_date";
  public static final String IAP_HISTORY__PREMIUMCUR_PURCHASED = "premiumcur_purchased";
  public static final String IAP_HISTORY__REGCUR_PURCHASED = "regcur_purchased";
  public static final String IAP_HISTORY__CASH_SPENT = "cash_spent";
  public static final String IAP_HISTORY__UDID = "udid";
  public static final String IAP_HISTORY__PRODUCT_ID = "product_id";
  public static final String IAP_HISTORY__QUANTITY = "quantity";
  public static final String IAP_HISTORY__BID = "bid";
  public static final String IAP_HISTORY__BVRS = "bvrs";
  public static final String IAP_HISTORY__APP_ITEM_ID = "app_item_id";

  /*LEADERBOARD EVENTS*/
  public static final String LEADERBOARD_EVENTS__ID = GENERIC__ID;
  public static final String LEADERBOARD_EVENTS__START_TIME = "start_time";
  public static final String LEADERBOARD_EVENTS__END_TIME = "end_time";
  public static final String LEADERBOARD_EVENTS__EVENT_NAME = "event_time";
  public static final String LEADERBOARD_EVENTS__REWARDS_GIVEN_OUT = "rewards_given_out";
  
  /*USER LEADERBOARD EVENTS*/
  public static final String USER_LEADERBOARD_EVENTS__LEADERBOARD_EVENT_ID = "leaderboard_event_id";
  public static final String USER_LEADERBOARD_EVENTS__USER_ID = GENERIC__USER_ID;
  public static final String USER_LEADERBOARD_EVENTS__BATTLES_WON = "battles_won";
  public static final String USER_LEADERBOARD_EVENTS__BATTLES_LOST = "battles_lost";
  public static final String USER_LEADERBOARD_EVENTS__BATTLES_FLED = "battles_fled";
  
  /*LEADERBOARD EVENT REWARDS*/
  public static final String LEADERBOARD_EVENT_REWARDS__ID = "leaderboard_event_id";
  public static final String LEADERBOARD_EVENT_REWARDS__MIN_RANK = "min_rank";
  public static final String LEADERBOARD_EVENT_REWARDS__MAX_RANK = "max_rank";
  public static final String LEADERBOARD_EVENT_REWARDS__GOLD_REWARDED = "gold_rewarded";
  public static final String LEADERBOARD_EVENT_REWARDS__BACKGROUND_IMAGE_NAME = "background_image_name";
  public static final String LEADERBOARD_EVENT_REWARDS__PRIZE_IMAGE_NAME = "prize_image_name";
  public static final String LEADERBOARD_EVENT_REWARDS__BLUE = "blue";
  public static final String LEADERBOARD_EVENT_REWARDS__GREEN = "green";
  public static final String LEADERBOARD_EVENT_REWARDS__RED = "red";

  /*USER LOCK BOX EVENTS*/
  public static final String USER_LOCK_BOX_EVENTS__EVENT_ID = "lock_box_event_id";
  public static final String USER_LOCK_BOX_EVENTS__USER_ID = "user_id";
  public static final String USER_LOCK_BOX_EVENTS__NUM_BOXES = "num_boxes";
  public static final String USER_LOCK_BOX_EVENTS__LAST_OPENING_TIME = "last_opening_time";
  public static final String USER_LOCK_BOX_EVENTS__NUM_TIMES_COMPLETED = "num_times_completed";
  public static final String USER_LOCK_BOX_EVENTS__HAS_BEEN_REDEEMED = "has_been_redeemed";

  /*LOGIN HISTORY*/
  public static final String LOGIN_HISTORY__ID = GENERIC__ID;
  public static final String LOGIN_HISTORY__UDID = "udid";
  public static final String LOGIN_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String LOGIN_HISTORY__DATE = "date";
  public static final String LOGIN_HISTORY__IS_LOGIN = "is_login";

  /*DELETED USER EQUIPS FOR ENHANCING*/
  public static final String DUEFE__USER_EQUIP__ID = "user_equip_id";
  public static final String DUEFE__USER_EQUIP__USER_ID = GENERIC__USER_ID;
  public static final String DUEFE__USER_EQUIP__EQUIP_ID = "equip_id";
  public static final String DUEFE__USER_EQUIP__LEVEL = "level";
  public static final String DUEFE__USER_EQUIP__ENHANCEMENT_PERCENT = "enhancement_percent";
  public static final String DUEFE__IS_FEEDER = "is_feeder";
  public static final String DUEFE__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id";

  /*EQUIP ENHANCEMENT*/
  public static final String EQUIP_ENHANCEMENT__ID = GENERIC__ID;
  public static final String EQUIP_ENHANCEMENT__USER_ID = GENERIC__USER_ID;
  public static final String EQUIP_ENHANCEMENT__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT__ENHANCEMENT_PERCENTAGE_BEFORE_ENHANCEMENT = "enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT__START_TIME_OF_ENHANCEMENT = "start_time_of_enhancement";

  /*EQUIP ENHANCEMENT HISTORY*/
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id"; //the user equip id
  public static final String EQUIP_ENHANCEMENT_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT_HISTORY__CURRENT_ENHANCEMENT_PERCENTAGE = "current_enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT_HISTORY__PREVIOUS_ENHANCEMENT_PERCENTAGE = "previous_enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT_HISTORY__START_TIME_OF_ENHANCEMENT = "start_time_of_enhancement";

  /*EQUIP ENHANCEMENT FEEDERS*/
  public static final String EQUIP_ENHANCEMENT_FEEDERS__ID = GENERIC__ID;
  public static final String EQUIP_ENHANCEMENT_FEEDERS__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id";
  public static final String EQUIP_ENHANCEMENT_FEEDERS__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT_FEEDERS__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT_FEEDERS__ENHANCEMENT_PERCENTAGE_BEFORE_ENHANCEMENT = "enhancement_percentage_before_enhancement";

  /*EQUIP ENHANCEMENT FEEDERS HISTORY*/
  public static final String EQUIP_ENHANCEMENT_FEEDERS_HISTORY__ID = "equip_enhancement_feeders_id";
  public static final String EQUIP_ENHANCEMENT_FEEDERS_HISTORY__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id";
  public static final String EQUIP_ENHANCEMENT_FEEDERS_HISTORY__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT_FEEDERS_HISTORY__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT_FEEDERS_HISTORY__ENHANCEMENT_PERCENTAGE = "enhancement_percentage";

  /*BLACKSMITH HISTORY TABLE*/
  public static final String BLACKSMITH_HISTORY__ID = "blacksmith_id";
  public static final String BLACKSMITH_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String BLACKSMITH_HISTORY__EQUIP_ID = "equip_id";
  public static final String BLACKSMITH_HISTORY__GOAL_LEVEL = "goal_level";
  public static final String BLACKSMITH_HISTORY__GUARANTEED = "guaranteed";
  public static final String BLACKSMITH_HISTORY__START_TIME = "start_time";
  public static final String BLACKSMITH_HISTORY__DIAMOND_GUARANTEE_COST = "diamond_guarantee_cost";
  public static final String BLACKSMITH_HISTORY__TIME_OF_SPEEDUP = "time_of_speedup";
  public static final String BLACKSMITH_HISTORY__SUCCESS = "success";
  public static final String BLACKSMITH_HISTORY__EQUIP_ONE_ENHANCEMENT_PERCENT = "equip_one_enhancement_percent";
  public static final String BLACKSMITH_HISTORY__EQUIP_TWO_ENHANCEMENT_PERCENT = "equip_two_enhancement_percent";
  public static final String BLACKSMITH_HISTORY__FORGE_SLOT_NUMBER = "forge_slot_number";

  /*USER AND EQUIP FAIL*///keeping track of user's failed monster evolution attempts
  public static final String USER_AND_EQUIP_FAIL__USER_ID = GENERIC__USER_ID;
  public static final String USER_AND_EQUIP_FAIL__EQUIP_ID = "equip_id";
  public static final String USER_AND_EQUIP_FAIL__NUM_FAILS = "num_fails";

  /*USER EQUIP TABLE*/
  public static final String USER_EQUIP__ID = GENERIC__ID;
  public static final String USER_EQUIP__USER_ID = GENERIC__USER_ID;
  public static final String USER_EQUIP__EQUIP_ID = "equip_id";
  public static final String USER_EQUIP__LEVEL = "level";
  public static final String USER_EQUIP__ENHANCEMENT_PERCENT = "enhancement_percent";
  public static final String USER_EQUIP__CREATE_TIME = "create_time";
  public static final String USER_EQUIP__REASON = "reason";
  public static final String USER_EQUIP__CURRENT_DURABILITY = "current_durability";

  /*MONSTER REWARD*/
  public static final String MONSTER_REWARD__ID = GENERIC__ID;
  public static final String MONSTER_REWARD__BOSS_ID = "monster_id";
  public static final String MONSTER_REWARD__EQUIP_ID = "equip_id";
  public static final String MONSTER_REWARD__DROP_RATE = "drop_rate";

  /*PRIVATE CHAT POSTS*/
  public static final String PRIVATE_CHAT_POSTS__ID = GENERIC__ID;
  public static final String PRIVATE_CHAT_POSTS__POSTER_ID = "poster_id";
  public static final String PRIVATE_CHAT_POSTS__RECIPIENT_ID = "recipient_id";
  public static final String PRIVATE_CHAT_POSTS__TIME_OF_POST = "time_of_post";
  public static final String PRIVATE_CHAT_POSTS__CONTENT = "content";
  
  /*USER QUESTS TABLE*/
  public static final String USER_QUESTS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS__IS_REDEEMED = "is_redeemed"; 
  public static final String USER_QUESTS__IS_COMPLETE = "is_complete";
  public static final String USER_QUESTS__TASKS_COMPLETE = "tasks_complete"; 
  public static final String USER_QUESTS__DEFEAT_TYPE_JOBS_COMPLETE = "defeat_type_jobs_complete"; 
  public static final String USER_QUESTS__COINS_RETRIEVED_FOR_REQ = "coins_retrieved_for_req";

  /*REFERRALS*/
  public static final String REFERRALS__REFERRER_ID = "referrer_id";
  public static final String REFERRALS__NEWLY_REFERRED_ID = "newly_referred_id";
  public static final String REFERRALS__TIME_OF_REFERRAL = "time_of_referral";
  public static final String REFERRALS__COINS_GIVEN_TO_REFERRER = "coins_given_to_referrer";

  /*USER SESSIONS*/
  public static final String USER_SESSIONS__USER_ID = GENERIC__USER_ID;
  public static final String USER_SESSIONS__LOGIN_TIME = "login_time";
  public static final String USER_SESSIONS__LOGOUT_TIME = "logout_time";

  /*USER STRUCTS TABLE*/
  public static final String USER_STRUCTS__ID = GENERIC__ID;
  public static final String USER_STRUCTS__USER_ID = GENERIC__USER_ID;
  public static final String USER_STRUCTS__STRUCT_ID = "struct_id";
  public static final String USER_STRUCTS__LAST_RETRIEVED = "last_retrieved";
  public static final String USER_STRUCTS__X_COORD = "xcoord";
  public static final String USER_STRUCTS__Y_COORD = "ycoord";
  public static final String USER_STRUCTS__IS_COMPLETE = "is_complete";
  public static final String USER_STRUCTS__LEVEL = "level";
  public static final String USER_STRUCTS__PURCHASE_TIME = "purchase_time";
  public static final String USER_STRUCTS__LAST_UPGRADE_TIME = "last_upgrade_time";
  public static final String USER_STRUCTS__ORIENTATION = "orientation";

  /*USER TASK TABLE*/
  public static final String USER_TASK__ID = GENERIC__ID;
  public static final String USER_TASK__USER_ID = GENERIC__USER_ID;
  public static final String USER_TASK__TASK_ID = "task_id";
  public static final String USER_TASK__EXP_GAINED = "exp_gained";
  public static final String USER_TASK__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK__NUM_REVIVES = "num_revives";
  public static final String USER_TASK__START_TIME = "start_time";

  /*USER TASK HISTORY TABLE*/
  public static final String USER_TASK_HISTORY__USER_TASK_ID = "user_task_id";
  public static final String USER_TASK_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_TASK_HISTORY__TASK_ID = "task_id";
  public static final String USER_TASK_HISTORY__EXP_GAINED = "exp_gained";
  public static final String USER_TASK_HISTORY__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK_HISTORY__NUM_REVIVES = "num_revives";
  public static final String USER_TASK_HISTORY__START_TIME = "start_time";
  public static final String USER_TASK_HISTORY__END_TIME = "end_time";
  public static final String USER_TASK_HISTORY__USER_WON = "user_won";

  /*TASK STAGE*/
  public static final String TASK_STAGE__ID = GENERIC__ID;
  public static final String TASK_STAGE__TASK_ID = "task_id";
  public static final String TASK_STAGE__STAGE_NUM = "stage_num";
  public static final String TASK_STAGE__EQUIP_DROP_RATE = "equip_drop_rate";
  
  /*USER TASK STAGE TABLE*/
  public static final String USER_TASK_STAGE__ID = GENERIC__ID;
  public static final String USER_TASK_STAGE__USER_TASK_ID = "user_task_id";
  public static final String USER_TASK_STAGE__STAGE_NUM = "stage_num";
  public static final String USER_TASK_STAGE__MONSTER_ID = "monster_id";
  public static final String USER_TASK_STAGE__EXP_GAINED = "exp_gained";
  public static final String USER_TASK_STAGE__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK_STAGE__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
  
  /*USER TASK STAGE HISTORY TABLE*/
  public static final String USER_TASK_STAGE_HISTORY__ID = GENERIC__ID;
  public static final String USER_TASK_STAGE_HISTORY__USER_TASK_ID = "user_task_id";
  public static final String USER_TASK_STAGE_HISTORY__STAGE_NUM = "stage_num";
  public static final String USER_TASK_STAGE_HISTORY__MONSTER_ID = "monster_id";
  public static final String USER_TASK_STAGE_HISTORY__EXP_GAINED = "exp_gained";
  public static final String USER_TASK_STAGE_HISTORY__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK_STAGE_HISTORY__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
  
  /*TASK STAGE*/
  public static final String TASK_STAGE_MONSTER__TASK_STAGE_ID = "task_stage_id";
  public static final String TASK_STAGE_MONSTER__MONSTER_ID = "monster_id";

  /*USER TABLE*/
  public static final String USER__ID = GENERIC__ID;
  public static final String USER__NAME = "name";
  public static final String USER__LEVEL = "level";
  public static final String USER__DIAMONDS = "diamonds";
  public static final String USER__COINS = "coins";
  public static final String USER__EXPERIENCE = "experience";
  public static final String USER__TASKS_COMPLETED = "tasks_completed";
  public static final String USER__BATTLES_WON = "battles_won";
  public static final String USER__BATTLES_LOST = "battles_lost";
  public static final String USER__FLEES = "flees";
  public static final String USER__REFERRAL_CODE = "referral_code";
  public static final String USER__NUM_REFERRALS = "num_referrals";
  public static final String USER__UDID = "udid";
  public static final String USER__LAST_LOGIN = "last_login";
  public static final String USER__LAST_LOGOUT = "last_logout";
  public static final String USER__DEVICE_TOKEN = "device_token";
  public static final String USER__LAST_BATTLE_NOTIFICATION_TIME = "last_battle_notification_time";
  public static final String USER__NUM_BADGES = "num_badges";
  public static final String USER__IS_FAKE = "is_fake";
  public static final String USER__CREATE_TIME = "create_time";
  public static final String USER__IS_ADMIN = "is_admin";
  public static final String USER__APSALAR_ID = "apsalar_id";
  public static final String USER__NUM_COINS_RETRIEVED_FROM_STRUCTS = "num_coins_retrieved_from_structs";
  public static final String USER__NUM_CONSECUTIVE_DAYS_PLAYED = "num_consecutive_days_played";
  public static final String USER__CLAN_ID = "clan_id";
  public static final String USER__LAST_WALL_POST_NOTIFICATION_TIME = "last_wall_post_notification_time";
  public static final String USER__KABAM_NAID = "kabam_naid";
  public static final String USER__HAS_RECEIVED_FB_REWARD = "has_received_fb_reward";
  public static final String USER__NUM_ADDITIONAL_FORGE_SLOTS = "num_additional_forge_slots";
  public static final String USER__NUM_BEGINNER_SALES_PURCHASED = "num_beginner_sales_purchased";
  public static final String USER__HAS_ACTIVE_SHIELD = "has_active_shield";
  public static final String USER__SHIELD_END_TIME = "shield_end_time";
  public static final String USER__ELO = "elo";
  public static final String USER__RANK = "rank";
  public static final String USER__LAST_TIME_QUEUED = "last_time_queued";
  public static final String USER__ATTACKS_WON = "attacks_won";
  public static final String USER__DEFENSES_WON = "defenses_won";
  public static final String USER__ATTACKS_LOST = "attacks_lost";
  public static final String USER__DEFENSES_LOST = "defenses_lost";

  /*FIRST TIME USERS*/
  public static final String FIRST_TIME_USERS__ID = GENERIC__ID;
  public static final String FIRST_TIME_USERS__OPEN_UDID = "open_udid";
  public static final String FIRST_TIME_USERS__UDID = "udid";
  public static final String FIRST_TIME_USERS__MAC = "mac";
  public static final String FIRST_TIME_USERS__ADVERTISER_ID = "advertiser_id";
  public static final String FIRST_TIME_USERS__CREATE_TIME = "create_time";

  /*USER CURRENCY HISTORY (FOR GOLD/DIAMONDS AND SILVER/COINS*/
  public static final String USER_CURRENCY_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_CURRENCY_HISTORY__DATE = "date";
  public static final String USER_CURRENCY_HISTORY__IS_SILVER = "is_silver";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_CHANGE = "currency_change";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE = "currency_before_change";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE = "currency_after_change";
  public static final String USER_CURRENCY_HISTORY__REASON_FOR_CHANGE = "reason_for_change";
 
  
  
  
  /*USER QUESTS COMPLETED TASKS TABLE*/
  public static final String USER_QUESTS_COMPLETED_TASKS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS_COMPLETED_TASKS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS_COMPLETED_TASKS__COMPLETED_TASK_ID = "completed_task_id";

  /*USER QUESTS COMPLETED DEFEAT TYPE JOBS TABLE*/
  public static final String USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS__COMPLETED_DEFEAT_TYPE_JOB_ID = "completed_defeat_type_job_id";

  /*USER QUESTS DEFEAT TYPE JOB PROGRESS*/
  public static final String USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__DEFEAT_TYPE_JOB_ID = "defeat_type_job_id";
  public static final String USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS__NUM_DEFEATED = "num_defeated";
  
  /*USER QUESTS DEFEAT TYPE JOB PROGRESS*/
  public static final String USER_QUESTS_TASK_PROGRESS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS_TASK_PROGRESS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS_TASK_PROGRESS__TASK_ID = "task_id";
  public static final String USER_QUESTS_TASK_PROGRESS__NUM_TIMES_ACTED = "num_times_acted";
  
}


