package com.lvl6.properties;

public class DBConstants {

  /* TABLENAMES*/
  public static final String TABLE_USER = "users";
  public static final String TABLE_USER_EQUIP = "user_equip";
  public static final String TABLE_USER_TASK = "user_task";
  public static final String TABLE_USER_CITIES = "user_cities";
  public static final String TABLE_USER_QUESTS = "user_quests";
  public static final String TABLE_USER_STRUCTS = "user_structs";
  public static final String TABLE_USER_EXPANSIONS = "user_expansions";
  public static final String TABLE_USER_QUESTS_COMPLETED_DEFEAT_TYPE_JOBS = "user_quests_completed_defeat_type_jobs";
  public static final String TABLE_USER_QUESTS_COMPLETED_TASKS = "user_quests_completed_tasks";
  public static final String TABLE_USER_QUESTS_DEFEAT_TYPE_JOB_PROGRESS = "user_quests_defeat_type_job_progress";
  public static final String TABLE_USER_QUESTS_TASK_PROGRESS = "user_quests_task_progress";
  public static final String TABLE_REFILL_STAT_HISTORY = "refill_stat_history";
  public static final String TABLE_EQUIPMENT = "equipment";
  public static final String TABLE_TASKS = "tasks";
  public static final String TABLE_TASKS_EQUIPREQS = "tasks_equipreqs";
  public static final String TABLE_CITIES = "cities";
  public static final String TABLE_IAP_HISTORY = "iap_history";
  public static final String TABLE_BLACKSMITH = "blacksmith";
  public static final String TABLE_BLACKSMITH_HISTORY = "blacksmith_history";
  public static final String TABLE_MARKETPLACE = "marketplace";
  public static final String TABLE_MARKETPLACE_TRANSACTION_HISTORY = "marketplace_transaction_history";
  public static final String TABLE_JOBS_BUILD_STRUCT = "jobs_build_struct";
  public static final String TABLE_JOBS_UPGRADE_STRUCT = "jobs_upgrade_struct";
  public static final String TABLE_JOBS_DEFEAT_TYPE = "jobs_defeat_type";
  public static final String TABLE_JOBS_MARKETPLACE = "jobs_marketplace";
  public static final String TABLE_JOBS_POSSESS_EQUIP = "jobs_possess_equip";
  public static final String TABLE_QUESTS = "quests";
  public static final String TABLE_STRUCTURES = "structures";
  public static final String TABLE_LEVELS_REQUIRED_EXPERIENCE = "levels_required_experience";
  public static final String TABLE_BATTLE_HISTORY = "battle_history";
  public static final String TABLE_REFERRALS = "referrals";
  public static final String TABLE_AVAILABLE_REFERRAL_CODES = "available_referral_codes";
  public static final String TABLE_NEUTRAL_CITY_ELEMENTS = "neutral_city_elems";
  public static final String TABLE_PLAYER_WALL_POSTS = "player_wall_posts";
  public static final String TABLE_ADCOLONY_RECENT_HISTORY = "adcolony_recent_history";
  public static final String TABLE_KIIP_REWARD_HISTORY = "kiip_reward_history";
  public static final String TABLE_C3P0_TEST = "c3p0_test_table";
  public static final String TABLE_IDDICTION_IDENTIFIERS = "iddiction_identifiers";
  public static final String TABLE_USER_SESSIONS = "user_sessions";
  public static final String TABLE_CLANS = "clans";
  public static final String TABLE_USER_CLANS = "user_clans";
  public static final String TABLE_DIAMOND_EQUIP_PURCHASE_HISTORY = "diamond_equip_purchase_history";
  public static final String TABLE_STAT_REFILL_HISTORY = "stat_refill_history";
  public static final String TABLE_CLAN_WALL_POSTS = "clan_wall_posts";
  public static final String TABLE_CLAN_BULLETIN_POSTS = "clan_bulletin_posts";
  public static final String TABLE_THREE_CARD_MONTE = "three_card_monte";
  public static final String TABLE_MONSTER = "monster";
  public static final String TABLE_MONSTER_REWARD = "monster_reward";
  public static final String TABLE_USER_BOSSES = "user_bosses";
  public static final String TABLE_USER_BOSS_HISTORY = "user_boss_history";
  public static final String TABLE_BOSS_EVENTS = "boss_events";
  //public static final String TABLE_BOSS_EQUIP_DROP_HISTORY = "boss_equip_drop_history";
  public static final String TABLE_BOSS_REWARD_DROP_HISTORY = "boss_reward_drop_history";
  public static final String TABLE_LOCK_BOX_EVENTS = "lock_box_events";
  public static final String TABLE_LOCK_BOX_ITEMS = "lock_box_items";
  public static final String TABLE_USER_LOCK_BOX_EVENTS = "user_lock_box_events";
  public static final String TABLE_USER_LOCK_BOX_ITEMS = "user_lock_box_items";
  public static final String TABLE_GOLD_SALES = "gold_sales";
  public static final String TABLE_CLAN_TOWERS = "clan_towers";
  public static final String TABLE_CLAN_TOWERS_HISTORY = "clan_towers_history";
  public static final String TABLE_CLAN_TOWER_USERS = "clan_tower_users";
  public static final String TABLE_CLAN_TIER_LEVELS = "clan_tier_levels";
  public static final String TABLE_LEADERBOARD_EVENTS = "leaderboard_events";
  public static final String TABLE_USER_LEADERBOARD_EVENTS = "user_leaderboard_events";
  public static final String TABLE_LEADERBOARD_EVENT_REWARDS = "leaderboard_event_rewards";
  public static final String TABLE_PROFANITY = "profanity";
  public static final String TABLE_USER_CURRENCY_HISTORY = "user_currency_history";
  
  public static final String TABLE_EQUIP_ENHANCEMENT = "equip_enhancement";
  public static final String TABLE_EQUIP_ENHANCEMENT_HISTORY = "equip_enhancement_history";
  public static final String TABLE_EQUIP_ENHANCEMENT_FEEDERS = "equip_enhancement_feeders";
  public static final String TABLE_EQUIP_ENHANCEMENT_FEEDERS_HISTORY = "equip_enhancement_feeders_history";
  public static final String TABLE_DELETED_USER_EQUIPS_FOR_ENHANCING = "deleted_user_equips_for_enhancing";
  
  public static final String TABLE_BOOSTER_PACK = "booster_pack";
  public static final String TABLE_BOOSTER_ITEM = "booster_item";
  public static final String TABLE_USER_BOOSTER_PACK_HISTORY = "user_booster_pack_history";
  public static final String TABLE_USER_BOOSTER_ITEMS = "user_booster_items";
  
  public static final String TABLE_BANNED_USER = "banned_user";
  
  public static final String TABLE_LOGIN_HISTORY = "login_history";
  public static final String TABLE_FIRST_TIME_USERS = "first_time_users";
  
  public static final String TABLE_DAILY_BONUS_REWARD = "daily_bonus_reward";
  public static final String TABLE_USER_DAILY_BONUS_REWARD_HISTORY = "user_daily_bonus_reward_history";
  
  public static final String TABLE_PRESTIGE_HISTORY = "prestige_history";
  
  public static final String TABLE_PRIVATE_CHAT_POSTS = "private_chat_posts";
  
  public static final String TABLE_MENTORSHIPS = "mentorships";
  
  public static final String TABLE_CITY_GEMS = "city_gems";
  public static final String TABLE_USER_CITY_GEMS = "user_city_gems";
  
  public static final String TABLE_TASK_STAGE = "task_stage";
  public static final String TABLE_TASK_STAGE_MONSTER = "task_stage_monster";
  public static final String TABLE_USER_TASK_HISTORY = "user_task_history";
  
  /*COLUMNNAMES*/
  public static final String GENERIC__USER_ID = "user_id";
  public static final String GENERIC__ID = "id";

  /*USER TABLE*/
  public static final String USER__ID = GENERIC__ID;
  public static final String USER__NAME = "name";
  public static final String USER__LEVEL = "level";
  public static final String USER__TYPE = "type";
  public static final String USER__ENERGY = "energy";
  public static final String USER__LAST_ENERGY_REFILL_TIME = "last_energy_refill_time";
  public static final String USER__ENERGY_MAX = "energy_max";
  public static final String USER__DIAMONDS = "diamonds";
  public static final String USER__COINS = "coins";
  public static final String USER__MARKETPLACE_DIAMONDS_EARNINGS = "marketplace_diamond_earnings";
  public static final String USER__MARKETPLACE_COINS_EARNINGS = "marketplace_coins_earnings";
  public static final String USER__VAULT_BALANCE = "vault_balance";
  public static final String USER__EXPERIENCE = "experience";
  public static final String USER__TASKS_COMPLETED = "tasks_completed";
  public static final String USER__BATTLES_WON = "battles_won";
  public static final String USER__BATTLES_LOST = "battles_lost";
  public static final String USER__FLEES = "flees";
  public static final String USER__UDID = "udid";
  public static final String USER__LATITUDE = "latitude";
  public static final String USER__LONGITUDE = "longitude";
  public static final String USER__NUM_POSTS_IN_MARKETPLACE = "num_posts_in_marketplace";
  public static final String USER__NUM_MARKETPLACE_SALES_UNREDEEMED = "num_marketplace_sales_unredeemed";
  public static final String USER__LAST_LOGIN = "last_login";
  public static final String USER__LAST_LOGOUT = "last_logout";
  public static final String USER__NUM_BADGES = "num_badges";
  public static final String USER__LAST_BATTLE_NOTIFICATION_TIME = "last_battle_notification_time";
  public static final String USER__LAST_TIME_ATTACKED = "last_time_attacked";
  public static final String USER__DEVICE_TOKEN = "device_token";
  public static final String USER__LAST_SHORT_LICENSE_PURCHASE_TIME = "last_short_license_purchase_time";
  public static final String USER__LAST_LONG_LICENSE_PURCHASE_TIME = "last_long_license_purchase_time";
  public static final String USER__REFERRAL_CODE = "referral_code";
  public static final String USER__NUM_REFERRALS = "num_referrals";
  public static final String USER__WEAPON_EQUIPPED_USER_EQUIP_ID = "weapon_equipped_user_equip_id";
  public static final String USER__ARMOR_EQUIPPED_USER_EQUIP_ID = "armor_equipped_user_equip_id";
  public static final String USER__AMULET_EQUIPPED_USER_EQUIP_ID = "amulet_equipped_user_equip_id";
  public static final String USER__IS_FAKE = "is_fake";
  public static final String USER__CREATE_TIME = "create_time";
  public static final String USER__APSALAR_ID = "apsalar_id";
  public static final String USER__NUM_COINS_RETRIEVED_FROM_STRUCTS = "num_coins_retrieved_from_structs";
  public static final String USER__NUM_ADCOLONY_VIDEOS_WATCHED = "num_adcolony_videos_watched";
  public static final String USER__NUM_TIMES_KIIP_REWARDED = "num_times_kiip_rewarded";
  public static final String USER__NUM_CONSECUTIVE_DAYS_PLAYED = "num_consecutive_days_played";
  public static final String USER__NUM_GROUP_CHATS_REMAINING = "num_group_chats_remaining";
  public static final String USER__CLAN_ID = "clan_id";
  public static final String USER__LAST_GOLDMINE_RETRIEVAL = "last_goldmine_retrieval";
  public static final String USER__LAST_MARKETPLACE_NOTIFICATION_TIME = "last_marketplace_notification_time";
  public static final String USER__LAST_WALL_POST_NOTIFICATION_TIME = "last_wall_post_notification_time";
  public static final String USER__KABAM_NAID = "kabam_naid";
  public static final String USER__HAS_RECEIVED_FB_REWARD = "has_received_fb_reward";
  public static final String USER__WEAPON_TWO_EQUIPPED_USER_EQUIP_ID = "weapon_two_equipped_user_equip_id";
  public static final String USER__ARMOR_TWO_EQUIPPED_USER_EQUIP_ID = "armor_two_equipped_user_equip_id";
  public static final String USER__AMULET_TWO_EQUIPPED_USER_EQUIP_ID = "amulet_two_equipped_user_equip_id";
  public static final String USER__PRESTIGE_LEVEL = "prestige_level";
  public static final String USER__NUM_ADDITIONAL_FORGE_SLOTS = "num_additional_forge_slots";
  public static final String USER__NUM_BEGINNER_SALES_PURCHASED = "num_beginner_sales_purchased";
  public static final String USER__IS_MENTOR = "is_mentor";
  public static final String USER__HAS_BEGINNER_SHIELD = "has_beginner_shield";
  public static final String USER__SHIELD_END_TIME = "shield_start_time";
  public static final String USER__ELO = "elo";
  public static final String USER__RANK = "rank";
  
  /*USER EQUIP TABLE*/
  public static final String USER_EQUIP__ID = GENERIC__ID;
  public static final String USER_EQUIP__USER_ID = GENERIC__USER_ID;
  public static final String USER_EQUIP__EQUIP_ID = "equip_id";
  public static final String USER_EQUIP__LEVEL = "level";
  public static final String USER_EQUIP__ENHANCEMENT_PERCENT = "enhancement_percent";
  public static final String USER_EQUIP__CREATE_TIME = "create_time";
  public static final String USER_EQUIP__REASON = "reason";
  
  /*EQUIP ENHANCEMENT*/
  public static final String EQUIP_ENHANCEMENT__ID = GENERIC__ID;
  public static final String EQUIP_ENHANCEMENT__USER_ID = GENERIC__USER_ID;
  public static final String EQUIP_ENHANCEMENT__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT__ENHANCEMENT_PERCENTAGE_BEFORE_ENHANCEMENT = "enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT__START_TIME_OF_ENHANCEMENT = "start_time_of_enhancement";
  
  /*EQUIP ENHANCEMENT HISTORY*/
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id";
  public static final String EQUIP_ENHANCEMENT_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_ID = "equip_id";
  public static final String EQUIP_ENHANCEMENT_HISTORY__EQUIP_LEVEL = "equip_level";
  public static final String EQUIP_ENHANCEMENT_HISTORY__CURRENT_ENHANCEMENT_PERCENTAGE = "current_enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT_HISTORY__PREVIOUS_ENHANCEMENT_PERCENTAGE = "previous_enhancement_percentage";
  public static final String EQUIP_ENHANCEMENT_HISTORY__START_TIME_OF_ENHANCEMENT = "start_time_of_enhancement";
  public static final String EQUIP_ENHANCEMENT_HISTORY__TIME_OF_SPEED_UP = "time_of_speed_up";
  public static final String EQUIP_ENHANCEMENT_HISTORY__RESULTING_USER_EQUIP_ID = "resulting_user_equip_id";
  
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
  
  /*DELETED USER EQUIPS FOR ENHANCING*/
  public static final String DUEFE__USER_EQUIP__ID = "user_equip_id";
  public static final String DUEFE__USER_EQUIP__USER_ID = GENERIC__USER_ID;
  public static final String DUEFE__USER_EQUIP__EQUIP_ID = "equip_id";
  public static final String DUEFE__USER_EQUIP__LEVEL = "level";
  public static final String DUEFE__USER_EQUIP__ENHANCEMENT_PERCENT = "enhancement_percent";
  public static final String DUEFE__IS_FEEDER = "is_feeder";
  public static final String DUEFE__EQUIP_ENHANCEMENT_ID = "equip_enhancement_id";
  
  /*USER TASK TABLE*/
  public static final String USER_TASK__ID = GENERIC__ID;
  public static final String USER_TASK__USER_ID = GENERIC__USER_ID;
  public static final String USER_TASK__TASK_ID = "task_id";
  public static final String USER_TASK__MONSTER_REWARD_EQUIP_IDS = "monster_reward_equip_ids";
  public static final String USER_TASK__EXP_GAINED = "exp_gained";
  public static final String USER_TASK__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK__NUM_REVIVES = "num_revives";
  public static final String USER_TASK__START_TIME = "start_time";
  public static final String USER_TASK__END_TIME = "end_time";
  public static final String USER_TASK__STAGE_EXPS = "stage_exps";
  public static final String USER_TASK__STAGE_SILVERS = "stage_silvers";
  
  /*USER TASK HISTORY TABLE*/
  public static final String USER_TASK_HISTORY__ID = GENERIC__ID;
  public static final String USER_TASK_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_TASK_HISTORY__TASK_ID = "task_id";
  public static final String USER_TASK_HISTORY__MONSTER_REWARD_EQUIP_IDS = "monster_reward_equip_ids";
  public static final String USER_TASK_HISTORY__EXP_GAINED = "exp_gained";
  public static final String USER_TASK_HISTORY__SILVER_GAINED = "silver_gained";
  public static final String USER_TASK_HISTORY__NUM_REVIVES = "num_revives";
  public static final String USER_TASK_HISTORY__STAGE_EXPS = "stage_exps";
  public static final String USER_TASK_HISTORY__STAGE_SILVERS = "stage_silvers";
  public static final String USER_TASK_HISTORY__START_TIME = "start_time";
  public static final String USER_TASK_HISTORY__END_TIME = "end_time";
  public static final String USER_TASK_HISTORY__USER_WON = "user_won";
  
  /*USER CITY TABLE*/
  public static final String USER_CITIES__USER_ID = GENERIC__USER_ID;
  public static final String USER_CITIES__CITY_ID = "city_id";
  public static final String USER_CITIES__CURRENT_RANK = "current_rank";
  public static final String USER_CITIES__NUM_TIMES_REDEEMED_GEMS = "num_times_redeemed_gems";
  
  /*REFILL STAT HISTORY*/ //missing time_of_refill column because adding to table does it automatically
  public static final String REFILL_STAT_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String REFILL_STAT_HISTORY__STAMINA_REFILL = "stamina_refill";
  public static final String REFILL_STAT_HISTORY__STAMINA_MAX = "stamina_max";
  public static final String REFILL_STAT_HISTORY__GOLD_COST = "gold_cost";
  
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
  
  /*MARKETPLACE TABLE*/
  public static final String MARKETPLACE__ID = GENERIC__ID;
  public static final String MARKETPLACE__POSTER_ID = "poster_id";
  public static final String MARKETPLACE__POST_TYPE = "post_type";
  public static final String MARKETPLACE__TIME_OF_POST = "time_of_post";
  public static final String MARKETPLACE__POSTED_EQUIP_ID = "posted_equip_id";
  public static final String MARKETPLACE__DIAMOND_COST = "diamond_cost";
  public static final String MARKETPLACE__COIN_COST = "coin_cost";
  public static final String MARKETPLACE__EQUIP_LEVEL = "equip_level";
  public static final String MARKETPLACE__EQUIP_ENHANCEMENT_PERCENT = "equip_enhancement_percent";

  /*MARKETPLACE HISTORY TABLE*/
  public static final String MARKETPLACE_TRANSACTION_HISTORY__MARKETPLACE_ID = "marketplace_id";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__POSTER_ID = "poster_id";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__BUYER_ID = "buyer_id";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__POST_TYPE = "post_type";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__TIME_OF_POST = "time_of_post";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__TIME_OF_PURCHASE = "time_of_purchase";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__POSTED_EQUIP_ID = "posted_equip_id";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__DIAMOND_COST = "diamond_cost";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__COIN_COST = "coin_cost";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__EQUIP_LEVEL = "equip_level";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__SELLER_HAS_LICENSE = "seller_has_license";
  public static final String MARKETPLACE_TRANSACTION_HISTORY__EQUIP_ENHANCEMENT_PERCENT = "equip_enhancement_percent";
  
  /*BLACKSMITH TABLE*/
  public static final String BLACKSMITH__ID = GENERIC__ID;
  public static final String BLACKSMITH__USER_ID = GENERIC__USER_ID;
  public static final String BLACKSMITH__EQUIP_ID = "equip_id";
  public static final String BLACKSMITH__GOAL_LEVEL = "goal_level";
  public static final String BLACKSMITH__GUARANTEED = "guaranteed";
  public static final String BLACKSMITH__START_TIME = "start_time";
  public static final String BLACKSMITH__DIAMOND_GUARANTEE_COST = "diamond_guarantee_cost";
  public static final String BLACKSMITH__TIME_OF_SPEEDUP = "time_of_speedup";
  public static final String BLACKSMITH__ATTEMPT_COMPLETE = "attempt_complete";
  public static final String BLACKSMITH__EQUIP_ONE_ENHANCEMENT_PERCENT = "equip_one_enhancement_percent";
  public static final String BLACKSMITH__EQUIP_TWO_ENHANCEMENT_PERCENT = "equip_two_enhancement_percent";
  public static final String BLACKSMITH__FORGE_SLOT_NUMBER = "forge_slot_number";
  
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
  
  /*USER CRITSTRUCTS TABLE*/
  public static final String USER_EXPANSIONS__USER_ID = GENERIC__USER_ID;
  public static final String USER_EXPANSIONS__X_POSITION = "x_position";
  public static final String USER_EXPANSIONS__Y_POSITION= "y_position";
  public static final String USER_EXPANSIONS__IS_EXPANDING = "is_expanding";
  public static final String USER_EXPANSIONS__LAST_EXPAND_TIME = "last_expand_time";
    
  /*USER QUESTS TABLE*/
  public static final String USER_QUESTS__USER_ID = GENERIC__USER_ID;
  public static final String USER_QUESTS__QUEST_ID = "quest_id";
  public static final String USER_QUESTS__IS_REDEEMED = "is_redeemed"; 
  public static final String USER_QUESTS__IS_COMPLETE = "is_complete";
  public static final String USER_QUESTS__TASKS_COMPLETE = "tasks_complete"; 
  public static final String USER_QUESTS__DEFEAT_TYPE_JOBS_COMPLETE = "defeat_type_jobs_complete"; 
  public static final String USER_QUESTS__COINS_RETRIEVED_FOR_REQ = "coins_retrieved_for_req";

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
  
  /*USER SESSIONS*/
  public static final String USER_SESSIONS__USER_ID = GENERIC__USER_ID;
  public static final String USER_SESSIONS__LOGIN_TIME = "login_time";
  public static final String USER_SESSIONS__LOGOUT_TIME = "logout_time";

  /*BATTLE HISTORY*/
  public static final String BATTLE_HISTORY__ATTACKER_ID = "attacker_id";
  public static final String BATTLE_HISTORY__DEFENDER_ID = "defender_id";
  public static final String BATTLE_HISTORY__RESULT = "result";
  public static final String BATTLE_HISTORY__BATTLE_COMPLETE_TIME = "battle_complete_time";
  public static final String BATTLE_HISTORY__COINS_STOLEN = "coins_stolen";
  public static final String BATTLE_HISTORY__EQUIP_STOLEN = "equip_stolen";
  public static final String BATTLE_HISTORY__EXP_GAINED = "exp_gained";
  public static final String BATTLE_HISTORY__STOLEN_EQUIP_LEVEL = "stolen_equip_level";
  
  /*REFERRALS*/
  public static final String REFERRALS__REFERRER_ID = "referrer_id";
  public static final String REFERRALS__NEWLY_REFERRED_ID = "newly_referred_id";
  public static final String REFERRALS__TIME_OF_REFERRAL = "time_of_referral";
  public static final String REFERRALS__COINS_GIVEN_TO_REFERRER = "coins_given_to_referrer";
  
  /*AVAILABLE REFERRAL CODES*/
  public static final String AVAILABLE_REFERRAL_CODES__ID = GENERIC__ID;
  public static final String AVAILABLE_REFERRAL_CODES__CODE = "code";

  /*PLAYER_WALL_POSTS*/
  public static final String PLAYER_WALL_POSTS__ID = GENERIC__ID;
  public static final String PLAYER_WALL_POSTS__POSTER_ID = "poster_id";
  public static final String PLAYER_WALL_POSTS__WALL_OWNER_ID = "wall_owner_id";
  public static final String PLAYER_WALL_POSTS__TIME_OF_POST = "time_of_post";
  public static final String PLAYER_WALL_POSTS__CONTENT = "content";

  /*CLAN_WALL_POSTS*/
  public static final String CLAN_BULLETIN_POSTS__ID = GENERIC__ID;
  public static final String CLAN_BULLETIN_POSTS__POSTER_ID = "poster_id";
  public static final String CLAN_BULLETIN_POSTS__CLAN_ID = "clan_id";
  public static final String CLAN_BULLETIN_POSTS__TIME_OF_POST = "time_of_post";
  public static final String CLAN_BULLETIN_POSTS__CONTENT = "content";

  /*CLAN_WALL_POSTS*/
  public static final String CLAN_WALL_POSTS__ID = GENERIC__ID;
  public static final String CLAN_WALL_POSTS__POSTER_ID = "poster_id";
  public static final String CLAN_WALL_POSTS__CLAN_ID = "clan_id";
  public static final String CLAN_WALL_POSTS__TIME_OF_POST = "time_of_post";
  public static final String CLAN_WALL_POSTS__CONTENT = "content";
  
  /*ADCOLONY_RECENT_HISTORY*/
  public static final String ADCOLONY_RECENT_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String ADCOLONY_RECENT_HISTORY__TIME_OF_REWARD = "time_of_reward";
  public static final String ADCOLONY_RECENT_HISTORY__DIAMONDS_EARNED = "diamonds_earned";
  public static final String ADCOLONY_RECENT_HISTORY__COINS_EARNED = "coins_earned";
  public static final String ADCOLONY_RECENT_HISTORY__DIGEST = "digest";
  
  /*KIIP REWARD HISTORY*/
  public static final String KIIP_REWARD_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String KIIP_REWARD_HISTORY__TRANSACTION_ID = "transaction_id";
  public static final String KIIP_REWARD_HISTORY__CONTENT = "content";
  public static final String KIIP_REWARD_HISTORY__QUANTITY = "quantity";
  public static final String KIIP_REWARD_HISTORY__SIGNATURE = "signature";
  public static final String KIIP_REWARD_HISTORY__TIME_OF_REWARD = "time_of_reward";
  
  /*IDDICTION*/
  public static final String IDDICTION_IDENTIFIERS__IDENTIFIER = "identifier";
  public static final String IDDICTION_IDENTIFIERS__CLICK_TIME = "click_time";
  
  /*CLANS*/
  public static final String CLANS__ID = "id";
  public static final String CLANS__OWNER_ID = "owner_id";
  public static final String CLANS__NAME = "name";
  public static final String CLANS__CREATE_TIME = "create_time";
  public static final String CLANS__DESCRIPTION = "description";
  public static final String CLANS__TAG = "tag";
  public static final String CLANS__IS_GOOD = "is_good";
  public static final String CLANS__CURRENT_TIER_LEVEL = "current_tier_level";
  public static final String CLANS__REQUEST_TO_JOIN_REQUIRED = "request_to_join_required";
  
  /*CLAN TIER LEVELS*/
  public static final String CLAN_TIER_LEVELS__TIER_LEVEL = "tier_level";
  public static final String CLAN_TIER_LEVELS__MAX_CLAN_SIZE = "max_clan_size";
  public static final String CLAN_TIER_LEVELS__GOLD_COST_TO_UPGRADE_TO_NEXT_TIER_LEVEL = "gold_cost_to_upgrade_to_next_tier_level";
  
  /*USER CLANS*/
  public static final String USER_CLANS__USER_ID = "user_id";
  public static final String USER_CLANS__CLAN_ID = "clan_id";
  public static final String USER_CLANS__STATUS = "status";
  public static final String USER_CLANS__REQUEST_TIME = "request_time";
  
  /*DIAMOND EQUIP PURCHASE HISTORY*/
  public static final String DIAMOND_EQUIP_PURCHASE_HISTORY__BUYER_ID = "buyer_id";
  public static final String DIAMOND_EQUIP_PURCHASE_HISTORY__EQUIP_ID = "equip_id";
  public static final String DIAMOND_EQUIP_PURCHASE_HISTORY__DIAMONDS_SPENT = "diamonds_spent";
  public static final String DIAMOND_EQUIP_PURCHASE_HISTORY__PURCHASE_TIME = "purchase_time";
  
  /*STAT REFILL HISTORY*/
  public static final String STAT_REFILL_HISTORY__USER_ID = "user_id";
  public static final String STAT_REFILL_HISTORY__REFILL_TYPE = "refill_type";
  public static final String STAT_REFILL_HISTORY__DIAMONDS_SPENT = "diamonds_spent";
  public static final String STAT_REFILL_HISTORY__REFILL_TIME = "refill_time";
  
  /*MONSTER*/
  public static final String MONSTER__ID = GENERIC__USER_ID;
  public static final String MONSTER__NAME = "name";
  public static final String MONSTER__MAX_HP = "max_hp";
  public static final String MONSTER__IMAGE_NAME = "image_name";
  public static final String MONSTER__IS_BOSS = "is_boss";
  public static final String MONSTER__WEAPON_ID = "weapon_id";
  public static final String MONSTER__WEAPON_LVL = "weapon_lvl";
  public static final String MONSTER__ARMOR_ID = "armor_id";
  public static final String MONSTER__ARMOR_LVL = "armor_lvl";
  public static final String MONSTER__AMULET_ID = "amulet_id";
  public static final String MONSTER__AMULET_LVL = "amulet_lvl";
  public static final String MONSTER__EXP_DROP = "exp_drop";
  public static final String MONSTER__MIN_SILVER_DROP = "min_silver_drop";
  public static final String MONSTER__MAX_SILVER_DROP = "max_silver_drop";
  
  /*USER BOSSES and USER BOSS HISTORY*/
  public static final String USER_BOSS_HISTORY__ID = GENERIC__ID;
  public static final String USER_BOSSES__USER_ID = "user_id";
  public static final String USER_BOSSES__BOSS_ID = "boss_id";
  public static final String USER_BOSSES__START_TIME = "start_time";
  public static final String USER_BOSSES__CUR_HEALTH = "cur_health";
  public static final String USER_BOSSES__CURRENT_LEVEL = "current_level";
//  public static final String USER_BOSSES__LAST_TIME_KILLED = "last_time_killed";
  public static final String USER_BOSSES__GEMLESS_STREAK = "gemless_streak";

  /*BOSS EVENTS*/
  public static final String BOSS_EVENTS__ID = GENERIC__USER_ID;
  public static final String BOSS_EVENTS__BOSS_ID = "boss_id";
  public static final String BOSS_EVENTS__START_TIME = "start_time";
  public static final String BOSS_EVENTS__END_TIME = "end_time";
  public static final String BOSS_EVENTS__BOSS_IMAGE_NAME = "boss_image_name";
  public static final String BOSS_EVENTS__EVENT_NAME = "event_name";
  public static final String BOSS_EVENTS__DESCRIPTION_STRING = "description_string";
  public static final String BOSS_EVENTS__DESCRIPTION_IMAGE_NAME = "description_image_name";
  public static final String BOSS_EVENTS__TAG_IMAGE_NAME = "tag_image_name";
  public static final String BOSS_EVENTS__CHANCE_OF_EQUIP_LOOT = "chance_of_equip_loot";
  public static final String BOSS_EVENTS__POTENTIAL_LOOT_EQUIP_IDS = "potential_loot_equip_ids";
  
  /*MONSTER REWARD*/
  public static final String MONSTER_REWARD__ID = GENERIC__ID;
  public static final String MONSTER_REWARD__BOSS_ID = "monster_id";
  public static final String MONSTER_REWARD__EQUIP_ID = "equip_id";
  public static final String MONSTER_REWARD__DROP_RATE = "drop_rate";
  
  /*BOSS EQUIP DROP HISTORY*/
//  public static final String BOSS_EQUIP_DROP_HISTORY__BOSS_REWARD_DROP_HISTORY_ID = "boss_reward_drop_history_id";
//  public static final String BOSS_EQUIP_DROP_HISTORY__EQUIP_ID = "equip_id";
//  public static final String BOSS_EQUIP_DROP_HISTORY__QUANTITY = "quantity";
  
  /*BOSS REWARD DROP HISTORY*/
  public static final String BOSS_REWARD_DROP_HISTORY__ID = GENERIC__USER_ID;
  public static final String BOSS_REWARD_DROP_HISTORY__BOSS_ID = "boss_id";
  public static final String BOSS_REWARD_DROP_HISTORY__USER_ID = "user_id";
  public static final String BOSS_REWARD_DROP_HISTORY__SILVER = "silver";
  public static final String BOSS_REWARD_DROP_HISTORY__GOLD = "gold";
  public static final String BOSS_REWARD_DROP_HISTORY__TIME_OF_DROP = "time_of_drop";
		  
  /*USER LOCK BOX EVENTS*/
  public static final String USER_LOCK_BOX_EVENTS__EVENT_ID = "lock_box_event_id";
  public static final String USER_LOCK_BOX_EVENTS__USER_ID = "user_id";
  public static final String USER_LOCK_BOX_EVENTS__NUM_BOXES = "num_boxes";
  public static final String USER_LOCK_BOX_EVENTS__LAST_OPENING_TIME = "last_opening_time";
  public static final String USER_LOCK_BOX_EVENTS__NUM_TIMES_COMPLETED = "num_times_completed";
  public static final String USER_LOCK_BOX_EVENTS__HAS_BEEN_REDEEMED = "has_been_redeemed";

  /*USER LOCK BOX ITEMS*/
  public static final String USER_LOCK_BOX_ITEMS__ITEM_ID = "lock_box_item_id";
  public static final String USER_LOCK_BOX_ITEMS__USER_ID = "user_id";
  public static final String USER_LOCK_BOX_ITEMS__QUANTITY = "quantity";
  
  /*EQUIPMENT TABLE*/
  public static final String EQUIPMENT__EQUIP_ID = GENERIC__ID;
  public static final String EQUIPMENT__ATK_BOOST = "atk_boost";
  public static final String EQUIPMENT__DEF_BOOST = "def_boost";
  public static final String EQUIPMENT__MIN_LEVEL = "min_level";
  public static final String EQUIPMENT__NAME = "name";
  public static final String EQUIPMENT__RARITY = "rarity";
  public static final String EQUIPMENT__TYPE = "type";
  
  /*CLAN TOWERS*/
  public static final String CLAN_TOWERS__TOWER_ID = GENERIC__ID;
  public static final String CLAN_TOWERS__CLAN_OWNER_ID = "clan_owner_id";
  public static final String CLAN_TOWERS__OWNED_START_TIME = "owned_start_time";
  public static final String CLAN_TOWERS__CLAN_ATTACKER_ID = "clan_attacker_id";
  public static final String CLAN_TOWERS__ATTACK_START_TIME = "attack_start_time";
  public static final String CLAN_TOWERS__OWNER_BATTLE_WINS = "owner_battle_wins";
  public static final String CLAN_TOWERS__ATTACKER_BATTLE_WINS = "attacker_battle_wins";
  public static final String CLAN_TOWERS__LAST_REWARD_GIVEN = "last_reward_given";
  public static final String CLAN_TOWERS__CURRENT_BATTLE_ID = "current_battle_id";
  
  /*CLAN TOWERS HISTORY*/
  public static final String CLAN_TOWERS_HISTORY__OWNER_CLAN_ID = "owner_clan_id";
  public static final String CLAN_TOWERS_HISTORY__ATTACKER_CLAN_ID = "attacker_clan_id";
  public static final String CLAN_TOWERS_HISTORY__TOWER_ID = "tower_id";
  public static final String CLAN_TOWERS_HISTORY__WINNER_ID = "winner_id";
  public static final String CLAN_TOWERS_HISTORY__OWNER_BATTLE_WINS = "owner_battle_wins";
  public static final String CLAN_TOWERS_HISTORY__ATTACKER_BATTLE_WINS = "attacker_battle_wins";
  public static final String CLAN_TOWERS_HISTORY__ATTACK_START_TIME = "attack_start_time";
  public static final String CLAN_TOWERS_HISTORY__NUM_HOURS_FOR_BATTLE = "num_hours_for_battle";
  public static final String CLAN_TOWERS_HISTORY__LAST_REWARD_GIVEN = "last_reward_given";
  public static final String CLAN_TOWERS_HISTORY__TIME_OF_ENTRY = "time_of_entry";
  public static final String CLAN_TOWERS_HISTORY__REASON_FOR_ENTRY = "reason_for_entry";
  public static final String CLAN_TOWERS_HISTORY__CURRENT_BATTLE_ID = "current_battle_id";

  /*CLAN TOWER USERS*/
  public static final String CLAN_TOWER_USERS__BATTLE_ID = "battle_id";
  public static final String CLAN_TOWER_USERS__USER_ID = "user_id";
  public static final String CLAN_TOWER_USERS__IS_IN_OWNER_CLAN = "is_in_owner_clan";
  public static final String CLAN_TOWER_USERS__POINTS_GAINED = "points_gained";
  public static final String CLAN_TOWER_USERS__POINTS_LOST = "points_lost";
  
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

  /*USER CURRENCY HISTORY (FOR GOLD/DIAMONDS AND SILVER/COINS*/
  public static final String USER_CURRENCY_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_CURRENCY_HISTORY__DATE = "date";
  public static final String USER_CURRENCY_HISTORY__IS_SILVER = "is_silver";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_CHANGE = "currency_change";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_BEFORE_CHANGE = "currency_before_change";
  public static final String USER_CURRENCY_HISTORY__CURRENCY_AFTER_CHANGE = "currency_after_change";
  public static final String USER_CURRENCY_HISTORY__REASON_FOR_CHANGE = "reason_for_change";
 
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
  
  /*USER BOOSTER ITEMS*/
  public static final String USER_BOOSTER_ITEMS__BOOSTER_ITEM_ID = "booster_item_id";
  public static final String USER_BOOSTER_ITEMS__USER_ID = "user_id";
  public static final String USER_BOOSTER_ITEMS__NUM_COLLECTED = "num_collected";
  
  /*LOGIN HISTORY*/
  public static final String LOGIN_HISTORY__ID = GENERIC__ID;
  public static final String LOGIN_HISTORY__UDID = "udid";
  public static final String LOGIN_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String LOGIN_HISTORY__DATE = "date";
  public static final String LOGIN_HISTORY__IS_LOGIN = "is_login";
  
  /*FIRST TIME USERS*/
  public static final String FIRST_TIME_USERS__ID = GENERIC__ID;
  public static final String FIRST_TIME_USERS__OPEN_UDID = "open_udid";
  public static final String FIRST_TIME_USERS__UDID = "udid";
  public static final String FIRST_TIME_USERS__MAC = "mac";
  public static final String FIRST_TIME_USERS__ADVERTISER_ID = "advertiser_id";
  public static final String FIRST_TIME_USERS__CREATE_TIME = "create_time";

  /*DAILY BONUS REWARD*/
  public static final String DAILY_BONUS_REWARD__ID = GENERIC__ID;
  public static final String DAILY_BONUS_REWARD__MIN_LEVEL = "min_level";
  public static final String DAILY_BONUS_REWARD__MAX_LEVEL = "max_level";
  public static final String DAILY_BONUS_REWARD__DAY_ONE_COINS = "day_one_coins";
  public static final String DAILY_BONUS_REWARD__DAY_TWO_COINS = "day_two_coins"; 
  public static final String DAILY_BONUS_REWARD__DAY_THREE_DIAMONDS = "day_three_diamonds";
  public static final String DAILY_BONUS_REWARD__DAY_FOUR_COINS = "day_four_coins";
  public static final String DAILY_BONUS_REWARD__DAY_FIVE_BOOSTER_PACK_IDS = "day_five_booster_pack_ids";
  
  /*USER DAILY BONUS REWARD HISTORY*/
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__ID = GENERIC__ID;
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__CURRENCY_REWARDED = "currency_rewarded";
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__IS_COINS = "is_coins";
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__BOOSTER_PACK_ID_REWARDED = "booster_pack_id_rewarded";
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__EQUIP_ID_REWARDED = "equip_id_rewarded";
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__NTH_CONSECUTIVE_DAY = "nth_consecutive_day";
  public static final String USER_DAILY_BONUS_REWARD_HISTORY__DATE_AWARDED = "date_awarded";
  
  /*PRESTIGE HISTORY TABLE*/
  public static final String PRESTIGE_HISTORY__ID = GENERIC__ID;
  public static final String PRESTIGE_HISTORY__USER_ID = GENERIC__USER_ID;
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_LEVEL = "preprestige_level";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_PRESTIGE_LEVEL = "preprestige_prestige_level";
  public static final String PRESTIGE_HISTORY__NEW_PRESTIGE_LEVEL = "new_prestige_level";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_ATTACK_STAT = "preprestige_attack_stat";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_DEFENSE_STAT = "preprestige_defense_stat";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_STAMINA_STAT = "preprestige_stamina_stat";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_ENERGY_STAT = "preprestige_energy_stat";
  public static final String PRESTIGE_HISTORY__PRESTIGE_DATE = "prestige_date";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_EXPERIENCE = "preprestige_experience";
  public static final String PRESTIGE_HISTORY__PREPRESTIGE_SKILL_POINTS = "preprestige_skill_points";
  
  /*PRIVATE CHAT POSTS*/
  public static final String PRIVATE_CHAT_POSTS__ID = GENERIC__ID;
  public static final String PRIVATE_CHAT_POSTS__POSTER_ID = "poster_id";
  public static final String PRIVATE_CHAT_POSTS__RECIPIENT_ID = "recipient_id";
  public static final String PRIVATE_CHAT_POSTS__TIME_OF_POST = "time_of_post";
  public static final String PRIVATE_CHAT_POSTS__CONTENT = "content";
  
  /*MENTORSHIPS*/
  public static final String MENTORSHIPS__ID = GENERIC__ID;
  public static final String MENTORSHIPS__MENTOR_ID = "mentor_id"; 
  public static final String MENTORSHIPS__MENTEE_ID = "mentee_id";
  public static final String MENTORSHIPS__START_TIME = "start_time";
  public static final String MENTORSHIPS__QUEST_ONE_COMPLETE_TIME = "quest_one_complete_time";
  public static final String MENTORSHIPS__QUEST_TWO_COMPLETE_TIME = "quest_two_complete_time";
  public static final String MENTORSHIPS__QUEST_THREE_COMPLETE_TIME = "quest_three_complete_time"; 
  public static final String MENTORSHIPS__QUEST_FOUR_COMPLETE_TIME = "quest_four_complete_time";
  public static final String MENTORSHIPS__QUEST_FIVE_COMPLETE_TIME = "quest_five_complete_time";
  public static final String MENTORSHIPS__IS_DROPPED = "is_dropped";
  
  /*CITY GEMS*/
  public static final String CITY_GEMS__ID = GENERIC__ID;
  public static final String CITY_GEMS__DROP_RATE = "drop_rate";
  public static final String CITY_GEMS__IS_ACTIVE = "is_active";
  public static final String CITY_GEMS__GEM_IMAGE_NAME = "gem_image_name";
  public static final String CITY_GEMS__DROPPED_ONLY_FROM_BOSSES = "dropped_only_from_bosses";
  
  /*USER CITY GEMS*/
  public static final String USER_CITY_GEMS__USER_ID = GENERIC__USER_ID;
  public static final String USER_CITY_GEMS__CITY_ID = "city_id";
  public static final String USER_CITY_GEMS__GEM_ID = "gem_id";
  public static final String USER_CITY_GEMS__QUANTITY = "quantity";
  
  /*TASK STAGE*/
  public static final String TASK_STAGE__ID = GENERIC__ID;
  public static final String TASK_STAGE__TASK_ID = "task_id";
  public static final String TASK_STAGE__STAGE_NUM = "stage_num";
  public static final String TASK_STAGE__EQUIP_DROP_RATE = "equip_drop_rate";
  
  /*TASK STAGE*/
  public static final String TASK_STAGE_MONSTER__TASK_STAGE_ID = "task_stage_id";
  public static final String TASK_STAGE_MONSTER__MONSTER_ID = "monster_id";
}
