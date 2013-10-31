package com.lvl6.properties;

//SPECIFYING COLUMNS OF STATIC DATA TABLES UNNECESSARY
public class DBConstants {
  /* TABLENAMES*/
	public static final String TABLE_ALERT_ON_STARTUP = "alert_on_startup";
	public static final String TABLE_BATTLE_HISTORY = "battle_history";
	public static final String TABLE_BOOSTER_ITEM = "booster_item";
	public static final String TABLE_BOOSTER_PACK = "booster_pack";
	public static final String TABLE_USER_BOOSTER_PACK_HISTORY = "booster_pack_history";
	public static final String TABLE_CITY = "city";
	public static final String TABLE_CITY_ELEMENT = "city_element";
	public static final String TABLE_CLANS = "clan";
	public static final String TABLE_CLAN_CHAT_POST = "clan_chat_post";
	public static final String TABLE_CLAN_FOR_USER = "clan_for_user";
	public static final String TABLE_EXPANSION_COST = "expansion_cost";
	public static final String TABLE_EXPANSION_PURCHASE_FOR_USER = "expansion_purchase_for_user";
	public static final String TABLE_GOLD_SALE = "gold_sale";	
	public static final String TABLE_IAP_HISTORY = "iap_history";
  public static final String TABLE_LOCK_BOX_EVENT = "lock_box_event";
  public static final String TABLE_LOCK_BOX_EVENT_FOR_USER = "lock_box_event_for_user";
  public static final String TABLE_LOCK_BOX_ITEM = "lock_box_item";
  public static final String TABLE_LOGIN_HISTORY = "login_history";	
  public static final String TABLE_MONSTER = "monster";
  public static final String TABLE_MONSTER_ENHANCING_FOR_USER = "monster_enhancing_for_user";
  public static final String TABLE_MONSTER_ENHANCING_HISTORY = "monster_enhancing_history";
  public static final String TABLE_MONSTER_EVOLVING_FAIL_FOR_USER = "monster_evolving_fail_for_user";
  public static final String TABLE_MONSTER_EVOLVING_FOR_USER = "monster_evolving_for_user";
  public static final String TABLE_MONSTER_EVOLVING_HISTORY = "monster_evolving_history";
  public static final String TABLE_MONSTER_FOR_USER = "monster_for_user";
  public static final String TABLE_MONSTER_FOR_USER_DELETED = "monster_for_user_deleted";
  public static final String TABLE_MONSTER_HEALING_FOR_USER = "monster_healing_for_user";
  public static final String TABLE_MONSTER_REWARD = "monster_reward";
  public static final String TABLE_PROFANITY = "profanity";
  public static final String TABLE_QUEST = "quest";
  public static final String TABLE_QUEST_FOR_USER = "quest_for_user";
  //links up rows in task_history (thus task_stage_history where monster info resides)
  //to monster kill jobs
  public static final String TABLE_QUEST_JOB_MONSTER_KILL_HISTORY_PROOF = "quest_job_monster_kill_history_proof";
  public static final String TABLE_QUEST_TASK_HISTORY = "quest_task_history";
  public static final String TABLE_REFERRAL = "referral";
  public static final String TABLE_REFERRAL_CODE_AVAILABLE = "referral_code_available";
	public static final String TABLE_REFERRAL_CODE_GENERATED = "referral_code_generated";
  public static final String TABLE_STATIC_LEVEL_INFO = "static_level_info";
  public static final String TABLE_STRUCTURE = "structure";
  public static final String TABLE_STRUCTURE_FOR_USER = "structure_for_user";
  public static final String TABLE_TASK = "task";
  public static final String TABLE_TASK_FOR_USER = "task_for_user";
  public static final String TABLE_TASK_HISTORY = "task_history";
  public static final String TABLE_TASK_STAGE = "task_stage";
  public static final String TABLE_TASK_STAGE_FOR_USER = "task_stage_for_user";
  public static final String TABLE_TASK_STAGE_HISTORY = "task_stage_history";
  public static final String TABLE_TASK_STAGE_MONSTER = "task_stage_monster";
	public static final String TABLE_TOURNAMENT_EVENT = "tournament_event";	
	public static final String TABLE_TOURNAMENT_EVENT_FOR_USER = "tournament_event_for_user";
	public static final String TABLE_TOURNAMENT_REWARD = "tournament_reward";
	public static final String TABLE_USER = "user";
	public static final String TABLE_BANNED_USER = "user_banned";
	public static final String TABLE_USER_BEFORE_TUTORIAL_COMPLETION = "user_before_tutorial_completion";
	public static final String TABLE_USER_CURRENCY_HISTORY = "user_currency_history";
	public static final String TABLE_USER_PRIVATE_CHAT_POST = "user_private_chat_post";
  public static final String TABLE_USER_SESSION = "user_session";
  
  /*COLUMNNAMES*/
  public static final String GENERIC__USER_ID = "user_id";
  public static final String GENERIC__ID = "id";
  
  /*ALERT ON STARTUP*/
  public static final String ALERT_ON_STARTUP__ID = GENERIC__ID;
  public static final String ALERT_ON_STARTUP__MESSAGE = "message";
  public static final String ALERT_ON_STARTUP__IS_ACTIVE = "is_active";
  
  
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
  public static final String BOOSTER_PACK_HISTORY__ID = GENERIC__ID;
  public static final String BOOSTER_PACK_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String BOOSTER_PACK_HISTORY__BOOSTER_PACK_ID = "booster_pack_id"; 
  public static final String BOOSTER_PACK_HISTORY__NUM_BOUGHT = "num_bought";
  public static final String BOOSTER_PACK_HISTORY__TIME_OF_PURCHASE = "time_of_purchase";
  public static final String BOOSTER_PACK_HISTORY__RARITY_ONE_QUANTITY = "rarity_one_quantity";
  public static final String BOOSTER_PACK_HISTORY__RARITY_TWO_QUANTITY = "rarity_two_quantity";
  public static final String BOOSTER_PACK_HISTORY__RARITY_THREE_QUANTITY = "rarity_three_quantity";
  public static final String BOOSTER_PACK_HISTORY__EXCLUDE_FROM_LIMIT_CHECK = "exclude_from_limit_check";
  public static final String BOOSTER_PACK_HISTORY__MONSTER_IDS = "monster_ids";
  public static final String BOOSTER_PACK_HISTORY__MONSTER_FOR_USER_IDS = "monster_for_user_ids";
  
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
  
  /*CLAN_CHAT_POSTS*/
  public static final String CLAN_CHAT_POST__ID = GENERIC__ID;
  public static final String CLAN_CHAT_POST__POSTER_ID = "poster_id";
  public static final String CLAN_CHAT_POST__CLAN_ID = "clan_id";
  public static final String CLAN_CHAT_POST__TIME_OF_POST = "time_of_post";
  public static final String CLAN_CHAT_POST__CONTENT = "content";

  /*USER CLANS*/
  public static final String CLAN_FOR_USER__USER_ID = "user_id";
  public static final String CLAN_FOR_USER__CLAN_ID = "clan_id";
  public static final String CLAN_FOR_USER__STATUS = "status";
  public static final String CLAN_FOR_USER__REQUEST_TIME = "request_time";

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
  public static final String IAP_HISTORY__REGCUR_PURCHASED = "regcur_purchased";
  public static final String IAP_HISTORY__CASH_SPENT = "cash_spent";
  public static final String IAP_HISTORY__UDID = "udid";
  public static final String IAP_HISTORY__PRODUCT_ID = "product_id";
  public static final String IAP_HISTORY__QUANTITY = "quantity";
  public static final String IAP_HISTORY__BID = "bid";
  public static final String IAP_HISTORY__BVRS = "bvrs";
  public static final String IAP_HISTORY__APP_ITEM_ID = "app_item_id";

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

  /*MONSTER ENHANCING FOR USER*/
  public static final String MONSTER_ENHANCING_FOR_USER__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_ENHANCING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
  public static final String MONSTER_ENHANCING_FOR_USER__EXPECTED_START_TIME = "expected_start_time";

  /*MONSTER ENHANCING HISTORY*/
  public static final String MONSTER_ENHANCING_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_ENHANCING_HISTORY__MONSTER_FOR_USER_ID = "monster_for_user_id";
  public static final String MONSTER_ENHANCING_HISTORY__EXPECTED_START_TIME = "expected_start_time";


  /*MONSTER EVOLVING FOR USER TABLE*/
  public static final String MONSTER_EVOLVING__ID = "id";
  
  /*MONSTER EVOLVING HISTORY TABLE*/
  public static final String MONSTER_EVOLVING_HISTORY__ID = "monster_evolving_id";
  public static final String MONSTER_EVOLVING_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_EVOLVING_HISTORY__MONSTER_ID = "monster_id";
  public static final String MONSTER_EVOLVING_HISTORY__GOAL_LEVEL = "goal_level";
  public static final String MONSTER_EVOLVING_HISTORY__GUARANTEED = "guaranteed";
  public static final String MONSTER_EVOLVING_HISTORY__START_TIME = "start_time";
  public static final String MONSTER_EVOLVING_HISTORY__DIAMOND_GUARANTEE_COST = "diamond_guarantee_cost";
  public static final String MONSTER_EVOLVING_HISTORY__TIME_OF_SPEEDUP = "time_of_speedup";
  public static final String MONSTER_EVOLVING_HISTORY__SUCCESS = "success";
  public static final String MONSTER_EVOLVING_HISTORY__EQUIP_ONE_ENHANCEMENT_PERCENT = "equip_one_enhancement_percent";
  public static final String MONSTER_EVOLVING_HISTORY__EQUIP_TWO_ENHANCEMENT_PERCENT = "equip_two_enhancement_percent";
  public static final String MONSTER_EVOLVING_HISTORY__FORGE_SLOT_NUMBER = "forge_slot_number";

  /*MONSTER EVOLVING FAIL FOR USER*///keeping track of user's failed monster evolution attempts
  public static final String MONSTER_EVOLVING_FAIL_FOR_USER__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_EVOLVING_FAIL_FOR_USER__MONSTER_ID = "monster_id";
  public static final String MONSTER_EVOLVING_FAIL_FOR_USER__NUM_FAILS = "num_fails";

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
  
  /*MONSTER FOR USER DELETED*/
  public static final String MONSTER_FOR_USER_DELETED__ID = "monster_for_user_id";
  public static final String MONSTER_FOR_USER_DELETED__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_FOR_USER_DELETED__MONSTER_ID = "monster_id";
  public static final String MONSTER_FOR_USER_DELETED__CURRENT_EXPERIENCE = "current_experience";
  public static final String MONSTER_FOR_USER_DELETED__CURRENT_LEVEL = "current_level";
  public static final String MONSTER_FOR_USER_DELETED__CURRENT_HEALTH = "current_health";
  public static final String MONSTER_FOR_USER_DELETED__NUM_PIECES = "num_pieces";
  public static final String MONSTER_FOR_USER_DELETED__IS_COMPLETE = "is_complete";
  public static final String MONSTER_FOR_USER_DELETED__TEAM_SLOT_NUM = "team_slot_num";
  public static final String MONSTER_FOR_USER_DELETED__SOURCE_OF_PIECES = "source_of_pieces";
  public static final String MONSTER_FOR_USER_DELETED__DELETED_REASON = "deleted_reason";
  public static final String MONSTER_FOR_USER_DELETED__DELETED_TIME = "deleted_time";
  
  /*MONSTER HEALING FOR USER*/
  public static final String MONSTER_HEALING_FOR_USER__USER_ID = GENERIC__USER_ID;
  public static final String MONSTER_HEALING_FOR_USER__MONSTER_FOR_USER_ID = "monster_for_user_id";
  public static final String MONSTER_HEALING_FOR_USER__EXPECTED_START_TIME = "expected_start_time";
//  public static final String MONSTER_HEALING_FOR_USER__QUEUED_TIME = "queued_time";

  /*PRIVATE CHAT POSTS*/
  public static final String PRIVATE_CHAT_POSTS__ID = GENERIC__ID;
  public static final String PRIVATE_CHAT_POSTS__POSTER_ID = "poster_id";
  public static final String PRIVATE_CHAT_POSTS__RECIPIENT_ID = "recipient_id";
  public static final String PRIVATE_CHAT_POSTS__TIME_OF_POST = "time_of_post";
  public static final String PRIVATE_CHAT_POSTS__CONTENT = "content";
  
  /*USER QUESTS TABLE*/
  public static final String QUEST_FOR_USER___USER_ID = GENERIC__USER_ID;
  public static final String QUEST_FOR_USER__QUEST_ID = "quest_id";
  public static final String QUEST_FOR_USER__IS_REDEEMED = "is_redeemed"; 
  public static final String QUEST_FOR_USER__IS_COMPLETE = "is_complete";
  public static final String QUEST_FOR_USER__PROGRESS = "progress"; 

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
  public static final String STRUCTURE_FOR_USER__ID = GENERIC__ID;
  public static final String STRUCTURE_FOR_USER__USER_ID = GENERIC__USER_ID;
  public static final String STRUCTURE_FOR_USER__STRUCT_ID = "struct_id";
  public static final String STRUCTURE_FOR_USER__LAST_RETRIEVED = "last_retrieved";
  public static final String STRUCTURE_FOR_USER__X_COORD = "xcoord";
  public static final String STRUCTURE_FOR_USER__Y_COORD = "ycoord";
  public static final String STRUCTURE_FOR_USER__LEVEL = "level";
  public static final String STRUCTURE_FOR_USER__PURCHASE_TIME = "purchase_time";
  public static final String STRUCTURE_FOR_USER__LAST_UPGRADE_TIME = "last_upgrade_time";
  public static final String STRUCTURE_FOR_USER__IS_COMPLETE = "is_complete";
  public static final String STRUCTURE_FOR_USER__ORIENTATION = "orientation";

  /*TASK FOR USER TABLE*/
  public static final String TASK_FOR_USER__ID = GENERIC__ID;
  public static final String TASK_FOR_USER__USER_ID = GENERIC__USER_ID;
  public static final String TASK_FOR_USER__TASK_ID = "task_id";
  public static final String TASK_FOR_USER__EXP_GAINED = "exp_gained";
  public static final String TASK_FOR_USER__SILVER_GAINED = "silver_gained";
  public static final String TASK_FOR_USER__NUM_REVIVES = "num_revives";
  public static final String TASK_FOR_USER__START_TIME = "start_time";

  /*TASK HISTORY TABLE*/
  public static final String TASK_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
  public static final String TASK_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String TASK_HISTORY__TASK_ID = "task_id";
  public static final String TASK_HISTORY__EXP_GAINED = "exp_gained";
  public static final String TASK_HISTORY__SILVER_GAINED = "silver_gained";
  public static final String TASK_HISTORY__NUM_REVIVES = "num_revives";
  public static final String TASK_HISTORY__START_TIME = "start_time";
  public static final String TASK_HISTORY__END_TIME = "end_time";
  public static final String TASK_HISTORY__USER_WON = "user_won";

  /*TASK STAGE*/
  public static final String TASK_STAGE__ID = GENERIC__ID;
  public static final String TASK_STAGE__TASK_ID = "task_id";
  public static final String TASK_STAGE__STAGE_NUM = "stage_num";
  public static final String TASK_STAGE__EQUIP_DROP_RATE = "equip_drop_rate";
  
  /*TASK STAGE FOR USER TABLE*/
  public static final String TASK_STAGE_FOR_USER__ID = GENERIC__ID;
  public static final String TASK_STAGE_FOR_USER__TASK_FOR_USER_ID = "task_for_user_id";
  public static final String TASK_STAGE_FOR_USER__STAGE_NUM = "stage_num";
  public static final String TASK_STAGE_FOR_USER__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
  public static final String TASK_STAGE_FOR_USER__EXP_GAINED = "exp_gained";
  public static final String TASK_STAGE_FOR_USER__SILVER_GAINED = "silver_gained";
  public static final String TASK_STAGE_FOR_USER__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
  
  /*USER TASK STAGE HISTORY TABLE*/
  public static final String TASK_STAGE_HISTORY__ID = "task_stage_for_user_id";
  public static final String TASK_STAGE_HISTORY__TASK_FOR_USER_ID = "task_for_user_id";
  public static final String TASK_STAGE_HISTORY__STAGE_NUM = "stage_num";
  public static final String TASK_STAGE_HISTORY__TASK_STAGE_MONSTER_ID = "task_stage_monster_id";
  public static final String TASK_STAGE_HISTORY__EXP_GAINED = "exp_gained";
  public static final String TASK_STAGE_HISTORY__SILVER_GAINED = "silver_gained";
  public static final String TASK_STAGE_HISTORY__MONSTER_PIECE_DROPPED = "monster_piece_dropped";
  
  /*TASK STAGE*/
  public static final String TASK_STAGE_MONSTER__TASK_STAGE_ID = "task_stage_id";
  public static final String TASK_STAGE_MONSTER__MONSTER_ID = "monster_id";

  /*USER TABLE*/
  public static final String USER__ID = GENERIC__ID;
  public static final String USER__NAME = "name";
  public static final String USER__LEVEL = "level";
  public static final String USER__GEMS = "gems";
  public static final String USER__CASH = "cash";
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

  /* USER BEFORE TUTORIAL COMPLETION*/
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__ID = GENERIC__ID;
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__OPEN_UDID = "open_udid";
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__UDID = "udid";
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__MAC = "mac";
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__ADVERTISER_ID = "advertiser_id";
  public static final String USER_BEFORE_TUTORIAL_COMPLETION__CREATE_TIME = "create_time";

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

}
