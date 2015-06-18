/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated;


import com.lvl6.mobsters.db.jooq.generated.tables.AchievementConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.AchievementForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.AlertOnStartup;
import com.lvl6.mobsters.db.jooq.generated.tables.BattleItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BattleItemForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.BattleItemQueueForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.BattleReplayForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.BoardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoardObstacleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoardPropertyConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoosterDisplayItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoosterItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoosterPackConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.BoosterPackPurchaseHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.CepfuRaidHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.CepfuRaidStageHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.CepfuRaidStageMonsterHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.ChatTranslations;
import com.lvl6.mobsters.db.jooq.generated.tables.CityBoss;
import com.lvl6.mobsters.db.jooq.generated.tables.CityBossSiteForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.CityConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.CityElementConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.Clan;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanAvenge;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanAvengeUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanBoss;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanBossReward;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanChatPost;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForClan;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForClanHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanEventPersistentUserReward;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanGiftConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanGiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanGiftRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelp;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanHelpCountForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanIconConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanInvite;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanMemberTeamDonation;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanRaidConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanRaidStageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanRaidStageMonsterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ClanRaidStageRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.CustomMenuConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.CustomTranslations;
import com.lvl6.mobsters.db.jooq.generated.tables.EventPersistentConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.EventPersistentForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ExpansionCostConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ExpansionPurchaseForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.FileDownloadConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.GiftForTangoUser;
import com.lvl6.mobsters.db.jooq.generated.tables.GiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.GoldSaleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.IapHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.ItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ItemForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ItemForUserUsage;
import com.lvl6.mobsters.db.jooq.generated.tables.ItemSecretGiftForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.LoadTestingEvents;
import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.LockBoxItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.LoginHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventForPlayerLvlConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventGoalConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventGoalForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventLeaderboardRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventTierRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniEventTimetableConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobForUserHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.MiniJobRefreshItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterBattleDialogueConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEnhancingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEnhancingHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEvolvingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterEvolvingHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterForPvpConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterForUserDeleted;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterHealingForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterHealingHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterLevelInfoConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.MonsterSnapshotForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ObstacleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ObstacleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.PrerequisiteConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ProfanityConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.PvpBattleHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.PvpBoardObstacleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.PvpLeagueConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.PvpLeagueForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.QuestConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.QuestForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.QuestJobMonsterItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.Referral;
import com.lvl6.mobsters.db.jooq.generated.tables.ReferralCodeAvailableConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ReferralCodeGeneratedConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ResearchConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ResearchForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.ResearchPropertyConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.RewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SalesDisplayItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SalesItemConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SalesPackageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SalesScheduleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SecretGiftConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.ServerToggleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SkillConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SkillPropertyConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.SkillSideEffectConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StaticLevelInfoConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureBattleItemFactoryConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureClanHouseConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureEvoChamberConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureHospitalConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureLabConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureMiniJobConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureMoneyTreeConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructurePvpBoardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureResearchHouseConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureResidenceConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureResourceGeneratorConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureResourceStorageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureTeamCenterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.StructureTownHallConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TangoGiftConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TangoGiftRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskForUserClientState;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskForUserCompleted;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskForUserOngoing;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskMapElementConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.TaskStageMonsterConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.Temp1;
import com.lvl6.mobsters.db.jooq.generated.tables.TournamentEventConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TournamentEventForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.TournamentRewardConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.TranslationSettingsForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.User;
import com.lvl6.mobsters.db.jooq.generated.tables.UserBanned;
import com.lvl6.mobsters.db.jooq.generated.tables.UserBeforeTutorialCompletion;
import com.lvl6.mobsters.db.jooq.generated.tables.UserClanBossContribution;
import com.lvl6.mobsters.db.jooq.generated.tables.UserClanBossRewardHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.UserCurrencyHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.UserFacebookInviteForSlot;
import com.lvl6.mobsters.db.jooq.generated.tables.UserPrivateChatPost;
import com.lvl6.mobsters.db.jooq.generated.tables.UserRewardHistory;
import com.lvl6.mobsters.db.jooq.generated.tables.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Mobsters extends SchemaImpl {

	private static final long serialVersionUID = 568465681;

	/**
	 * The reference instance of <code>mobsters</code>
	 */
	public static final Mobsters MOBSTERS = new Mobsters();

	/**
	 * No further instances allowed
	 */
	private Mobsters() {
		super("mobsters");
	}

	@Override
	public final List<Table<?>> getTables() {
		List result = new ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final List<Table<?>> getTables0() {
		return Arrays.<Table<?>>asList(
			AchievementConfig.ACHIEVEMENT_CONFIG,
			AchievementForUser.ACHIEVEMENT_FOR_USER,
			AlertOnStartup.ALERT_ON_STARTUP,
			BattleItemConfig.BATTLE_ITEM_CONFIG,
			BattleItemForUser.BATTLE_ITEM_FOR_USER,
			BattleItemQueueForUser.BATTLE_ITEM_QUEUE_FOR_USER,
			BattleReplayForUser.BATTLE_REPLAY_FOR_USER,
			BoardConfig.BOARD_CONFIG,
			BoardObstacleConfig.BOARD_OBSTACLE_CONFIG,
			BoardPropertyConfig.BOARD_PROPERTY_CONFIG,
			BoosterDisplayItemConfig.BOOSTER_DISPLAY_ITEM_CONFIG,
			BoosterItemConfig.BOOSTER_ITEM_CONFIG,
			BoosterPackConfig.BOOSTER_PACK_CONFIG,
			BoosterPackPurchaseHistory.BOOSTER_PACK_PURCHASE_HISTORY,
			CepfuRaidHistory.CEPFU_RAID_HISTORY,
			CepfuRaidStageHistory.CEPFU_RAID_STAGE_HISTORY,
			CepfuRaidStageMonsterHistory.CEPFU_RAID_STAGE_MONSTER_HISTORY,
			ChatTranslations.CHAT_TRANSLATIONS,
			CityBoss.CITY_BOSS,
			CityBossSiteForUser.CITY_BOSS_SITE_FOR_USER,
			CityConfig.CITY_CONFIG,
			CityElementConfig.CITY_ELEMENT_CONFIG,
			Clan.CLAN,
			ClanAvenge.CLAN_AVENGE,
			ClanAvengeUser.CLAN_AVENGE_USER,
			ClanBoss.CLAN_BOSS,
			ClanBossReward.CLAN_BOSS_REWARD,
			ClanChatPost.CLAN_CHAT_POST,
			ClanEventPersistentConfig.CLAN_EVENT_PERSISTENT_CONFIG,
			ClanEventPersistentForClan.CLAN_EVENT_PERSISTENT_FOR_CLAN,
			ClanEventPersistentForClanHistory.CLAN_EVENT_PERSISTENT_FOR_CLAN_HISTORY,
			ClanEventPersistentForUser.CLAN_EVENT_PERSISTENT_FOR_USER,
			ClanEventPersistentUserReward.CLAN_EVENT_PERSISTENT_USER_REWARD,
			ClanForUser.CLAN_FOR_USER,
			ClanGiftConfig.CLAN_GIFT_CONFIG,
			ClanGiftForUser.CLAN_GIFT_FOR_USER,
			ClanGiftRewardConfig.CLAN_GIFT_REWARD_CONFIG,
			ClanHelp.CLAN_HELP,
			ClanHelpCountForUser.CLAN_HELP_COUNT_FOR_USER,
			ClanIconConfig.CLAN_ICON_CONFIG,
			ClanInvite.CLAN_INVITE,
			ClanMemberTeamDonation.CLAN_MEMBER_TEAM_DONATION,
			ClanRaidConfig.CLAN_RAID_CONFIG,
			ClanRaidStageConfig.CLAN_RAID_STAGE_CONFIG,
			ClanRaidStageMonsterConfig.CLAN_RAID_STAGE_MONSTER_CONFIG,
			ClanRaidStageRewardConfig.CLAN_RAID_STAGE_REWARD_CONFIG,
			CustomMenuConfig.CUSTOM_MENU_CONFIG,
			CustomTranslations.CUSTOM_TRANSLATIONS,
			EventPersistentConfig.EVENT_PERSISTENT_CONFIG,
			EventPersistentForUser.EVENT_PERSISTENT_FOR_USER,
			ExpansionCostConfig.EXPANSION_COST_CONFIG,
			ExpansionPurchaseForUser.EXPANSION_PURCHASE_FOR_USER,
			FileDownloadConfig.FILE_DOWNLOAD_CONFIG,
			GiftForTangoUser.GIFT_FOR_TANGO_USER,
			GiftForUser.GIFT_FOR_USER,
			GoldSaleConfig.GOLD_SALE_CONFIG,
			IapHistory.IAP_HISTORY,
			ItemConfig.ITEM_CONFIG,
			ItemForUser.ITEM_FOR_USER,
			ItemForUserUsage.ITEM_FOR_USER_USAGE,
			ItemSecretGiftForUser.ITEM_SECRET_GIFT_FOR_USER,
			LoadTestingEvents.LOAD_TESTING_EVENTS,
			LockBoxEventConfig.LOCK_BOX_EVENT_CONFIG,
			LockBoxEventForUser.LOCK_BOX_EVENT_FOR_USER,
			LockBoxItemConfig.LOCK_BOX_ITEM_CONFIG,
			LoginHistory.LOGIN_HISTORY,
			MiniEventConfig.MINI_EVENT_CONFIG,
			MiniEventForPlayerLvlConfig.MINI_EVENT_FOR_PLAYER_LVL_CONFIG,
			MiniEventForUser.MINI_EVENT_FOR_USER,
			MiniEventGoalConfig.MINI_EVENT_GOAL_CONFIG,
			MiniEventGoalForUser.MINI_EVENT_GOAL_FOR_USER,
			MiniEventLeaderboardRewardConfig.MINI_EVENT_LEADERBOARD_REWARD_CONFIG,
			MiniEventTierRewardConfig.MINI_EVENT_TIER_REWARD_CONFIG,
			MiniEventTimetableConfig.MINI_EVENT_TIMETABLE_CONFIG,
			MiniJobConfig.MINI_JOB_CONFIG,
			MiniJobForUser.MINI_JOB_FOR_USER,
			MiniJobForUserHistory.MINI_JOB_FOR_USER_HISTORY,
			MiniJobRefreshItemConfig.MINI_JOB_REFRESH_ITEM_CONFIG,
			MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG,
			MonsterConfig.MONSTER_CONFIG,
			MonsterEnhancingForUser.MONSTER_ENHANCING_FOR_USER,
			MonsterEnhancingHistory.MONSTER_ENHANCING_HISTORY,
			MonsterEvolvingForUser.MONSTER_EVOLVING_FOR_USER,
			MonsterEvolvingHistory.MONSTER_EVOLVING_HISTORY,
			MonsterForPvpConfig.MONSTER_FOR_PVP_CONFIG,
			MonsterForUser.MONSTER_FOR_USER,
			MonsterForUserDeleted.MONSTER_FOR_USER_DELETED,
			MonsterHealingForUser.MONSTER_HEALING_FOR_USER,
			MonsterHealingHistory.MONSTER_HEALING_HISTORY,
			MonsterLevelInfoConfig.MONSTER_LEVEL_INFO_CONFIG,
			MonsterSnapshotForUser.MONSTER_SNAPSHOT_FOR_USER,
			ObstacleConfig.OBSTACLE_CONFIG,
			ObstacleForUser.OBSTACLE_FOR_USER,
			PrerequisiteConfig.PREREQUISITE_CONFIG,
			ProfanityConfig.PROFANITY_CONFIG,
			PvpBattleForUser.PVP_BATTLE_FOR_USER,
			PvpBattleHistory.PVP_BATTLE_HISTORY,
			PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER,
			PvpLeagueConfig.PVP_LEAGUE_CONFIG,
			PvpLeagueForUser.PVP_LEAGUE_FOR_USER,
			QuestConfig.QUEST_CONFIG,
			QuestForUser.QUEST_FOR_USER,
			QuestJobConfig.QUEST_JOB_CONFIG,
			QuestJobForUser.QUEST_JOB_FOR_USER,
			QuestJobMonsterItemConfig.QUEST_JOB_MONSTER_ITEM_CONFIG,
			Referral.REFERRAL,
			ReferralCodeAvailableConfig.REFERRAL_CODE_AVAILABLE_CONFIG,
			ReferralCodeGeneratedConfig.REFERRAL_CODE_GENERATED_CONFIG,
			ResearchConfig.RESEARCH_CONFIG,
			ResearchForUser.RESEARCH_FOR_USER,
			ResearchPropertyConfig.RESEARCH_PROPERTY_CONFIG,
			RewardConfig.REWARD_CONFIG,
			SalesDisplayItemConfig.SALES_DISPLAY_ITEM_CONFIG,
			SalesItemConfig.SALES_ITEM_CONFIG,
			SalesPackageConfig.SALES_PACKAGE_CONFIG,
			SalesScheduleConfig.SALES_SCHEDULE_CONFIG,
			SecretGiftConfig.SECRET_GIFT_CONFIG,
			ServerToggleConfig.SERVER_TOGGLE_CONFIG,
			SkillConfig.SKILL_CONFIG,
			SkillPropertyConfig.SKILL_PROPERTY_CONFIG,
			SkillSideEffectConfig.SKILL_SIDE_EFFECT_CONFIG,
			StaticLevelInfoConfig.STATIC_LEVEL_INFO_CONFIG,
			StructureBattleItemFactoryConfig.STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG,
			StructureClanHouseConfig.STRUCTURE_CLAN_HOUSE_CONFIG,
			StructureConfig.STRUCTURE_CONFIG,
			StructureEvoChamberConfig.STRUCTURE_EVO_CHAMBER_CONFIG,
			StructureForUser.STRUCTURE_FOR_USER,
			StructureHospitalConfig.STRUCTURE_HOSPITAL_CONFIG,
			StructureLabConfig.STRUCTURE_LAB_CONFIG,
			StructureMiniJobConfig.STRUCTURE_MINI_JOB_CONFIG,
			StructureMoneyTreeConfig.STRUCTURE_MONEY_TREE_CONFIG,
			StructurePvpBoardConfig.STRUCTURE_PVP_BOARD_CONFIG,
			StructureResearchHouseConfig.STRUCTURE_RESEARCH_HOUSE_CONFIG,
			StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG,
			StructureResourceGeneratorConfig.STRUCTURE_RESOURCE_GENERATOR_CONFIG,
			StructureResourceStorageConfig.STRUCTURE_RESOURCE_STORAGE_CONFIG,
			StructureTeamCenterConfig.STRUCTURE_TEAM_CENTER_CONFIG,
			StructureTownHallConfig.STRUCTURE_TOWN_HALL_CONFIG,
			TangoGiftConfig.TANGO_GIFT_CONFIG,
			TangoGiftRewardConfig.TANGO_GIFT_REWARD_CONFIG,
			TaskConfig.TASK_CONFIG,
			TaskForUserClientState.TASK_FOR_USER_CLIENT_STATE,
			TaskForUserCompleted.TASK_FOR_USER_COMPLETED,
			TaskForUserOngoing.TASK_FOR_USER_ONGOING,
			TaskHistory.TASK_HISTORY,
			TaskMapElementConfig.TASK_MAP_ELEMENT_CONFIG,
			TaskStageConfig.TASK_STAGE_CONFIG,
			TaskStageForUser.TASK_STAGE_FOR_USER,
			TaskStageHistory.TASK_STAGE_HISTORY,
			TaskStageMonsterConfig.TASK_STAGE_MONSTER_CONFIG,
			Temp1.TEMP1,
			TournamentEventConfig.TOURNAMENT_EVENT_CONFIG,
			TournamentEventForUser.TOURNAMENT_EVENT_FOR_USER,
			TournamentRewardConfig.TOURNAMENT_REWARD_CONFIG,
			TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER,
			User.USER,
			UserBanned.USER_BANNED,
			UserBeforeTutorialCompletion.USER_BEFORE_TUTORIAL_COMPLETION,
			UserClanBossContribution.USER_CLAN_BOSS_CONTRIBUTION,
			UserClanBossRewardHistory.USER_CLAN_BOSS_REWARD_HISTORY,
			UserCurrencyHistory.USER_CURRENCY_HISTORY,
			UserFacebookInviteForSlot.USER_FACEBOOK_INVITE_FOR_SLOT,
			UserPrivateChatPost.USER_PRIVATE_CHAT_POST,
			UserRewardHistory.USER_REWARD_HISTORY,
			UserSession.USER_SESSION);
	}
}
