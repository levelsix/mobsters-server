package com.lvl6.event.response

import scala.collection.mutable.HashMap
import com.google.protobuf.GeneratedMessage
import com.lvl6.events.ResponseEvent
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse
import com.lvl6.proto.EventAchievementProto.AchievementRedeemResponseProto
import com.lvl6.proto.EventClanProto.EndClanAvengingResponseProto
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto
import com.lvl6.proto.EventStructureProto.DestroyMoneyTreeStructureResponseProto
import com.lvl6.proto.EventClanProto.ChangeClanSettingsResponseProto
import com.lvl6.proto.EventMiniJobProto.BeginMiniJobResponseProto
import com.lvl6.proto.EventClanProto.AvengeClanMateResponseProto
import com.lvl6.proto.EventClanProto.AttackClanRaidMonsterResponseProto
import com.lvl6.proto.EventPvpProto.BeginPvpBattleResponseProto
import com.lvl6.proto.EventClanProto.AwardClanRaidStageRewardResponseProto
import com.lvl6.proto.EventPvpProto.CustomizePvpBoardObstacleResponseProto
import com.lvl6.proto.EventDevProto.DevResponseProto
import com.lvl6.proto.EventApnsProto.EnableAPNSResponseProto
import com.lvl6.proto.EventClanProto.EndClanHelpResponseProto
import com.lvl6.proto.EventMonsterProto.CombineUserMonsterPiecesResponseProto
import com.lvl6.proto.EventDungeonProto.BeginDungeonResponseProto
import com.lvl6.proto.EventDungeonProto.EndDungeonResponseProto
import com.lvl6.proto.EventBattleItemProto.CreateBattleItemResponseProto
import com.lvl6.proto.EventMiniJobProto.CompleteMiniJobResponseProto
import com.lvl6.proto.EventAchievementProto.AchievementProgressResponseProto
import com.lvl6.proto.EventClanProto.BeginClanAvengingResponseProto
import com.lvl6.proto.EventStructureProto.BeginObstacleRemovalResponseProto
import com.lvl6.proto.EventMonsterProto.AddMonsterToBattleTeamResponseProto
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto
import com.lvl6.proto.EventMonsterProto.CollectMonsterEnhancementResponseProto
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto
import com.lvl6.proto.EventClanProto.DeleteClanGiftsResponseProto
import com.lvl6.proto.EventClanProto.CreateClanResponseProto
import com.lvl6.proto.EventClanProto.BeginClanRaidResponseProto
import com.lvl6.proto.EventBattleItemProto.CompleteBattleItemResponseProto
import com.lvl6.proto.EventClanProto.BootPlayerFromClanResponseProto
import com.lvl6.proto.EventClanProto.CollectClanGiftsResponseProto
import com.lvl6.proto.EventClanProto.AcceptOrRejectClanInviteResponseProto
import com.lvl6.proto.EventUserProto.RetrieveUsersForUserIdsResponseProto
import com.lvl6.proto.EventInAppPurchaseProto.ExchangeGemsForResourcesResponseProto
import com.lvl6.proto.EventResearchProto.PerformResearchResponseProto
import com.lvl6.proto.EventStructureProto.ObstacleRemovalCompleteResponseProto
import com.lvl6.proto.EventUserProto.UpdateClientUserResponseProto
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto
import com.lvl6.proto.EventClanProto.GiveClanHelpResponseProto
import com.lvl6.proto.EventUserProto.UpdateUserCurrencyResponseProto
import com.lvl6.proto.EventMonsterProto.UpdateMonsterHealthResponseProto
import com.lvl6.proto.EventUserProto.SetGameCenterIdResponseProto
import com.lvl6.proto.EventChatProto.RetrievePrivateChatPostsResponseProto
import com.lvl6.proto.EventDungeonProto.ReviveInDungeonResponseProto
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto
import com.lvl6.proto.EventPvpProto.EndPvpBattleResponseProto
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsResponseProto
import com.lvl6.proto.EventStructureProto.NormStructWaitCompleteResponseProto
import com.lvl6.proto.EventMiniJobProto.RefreshMiniJobResponseProto
import com.lvl6.proto.EventMonsterProto.HealMonsterResponseProto
import com.lvl6.proto.EventMiniJobProto.RedeemMiniJobResponseProto
import com.lvl6.proto.EventItemProto.TradeItemForBoosterResponseProto
import com.lvl6.proto.EventClanProto.InviteToClanResponseProto
import com.lvl6.proto.EventCityProto.LoadPlayerCityResponseProto
import com.lvl6.proto.EventChatProto.GeneralNotificationResponseProto
import com.lvl6.proto.EventStructureProto.SpawnObstacleResponseProto
import com.lvl6.proto.EventClanProto.RetractRequestJoinClanResponseProto
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto
import com.lvl6.proto.EventStartupProto.ForceLogoutResponseProto
import com.lvl6.proto.EventMonsterProto.RetrieveUserMonsterTeamResponseProto
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto
import com.lvl6.proto.EventClanProto.VoidTeamDonationSolicitationResponseProto
import com.lvl6.proto.EventUserProto.LevelUpResponseProto
import com.lvl6.proto.EventMonsterProto.RestrictUserMonsterResponseProto
import com.lvl6.proto.EventItemProto.TradeItemForResourcesResponseProto
import com.lvl6.proto.EventClanProto.FulfillTeamDonationSolicitationResponseProto
import com.lvl6.proto.EventMiniJobProto.SpawnMiniJobResponseProto
import com.lvl6.proto.EventUserProto.UpdateClientTaskStateResponseProto
import com.lvl6.proto.EventMonsterProto.RemoveMonsterFromBattleTeamResponseProto
import com.lvl6.proto.EventBoosterPackProto.PurchaseBoosterPackResponseProto
import com.lvl6.proto.EventMonsterProto.EvolutionFinishedResponseProto
import com.lvl6.proto.EventClanProto.ReceivedClanGiftResponseProto
import com.lvl6.proto.EventMonsterProto.SubmitMonsterEnhancementResponseProto
import com.lvl6.proto.EventStartupProto.StartupResponseProto
import com.lvl6.proto.EventClanProto.SolicitTeamDonationResponseProto
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureResponseProto
import com.lvl6.proto.EventClanProto.TransferClanOwnershipResponseProto
import com.lvl6.proto.EventBoosterPackProto.ReceivedRareBoosterPurchaseResponseProto
import com.lvl6.proto.EventClanProto.LeaveClanResponseProto
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto
import com.lvl6.proto.EventResearchProto.FinishPerformingResearchResponseProto
import com.lvl6.proto.EventQuestProto.QuestAcceptResponseProto
import com.lvl6.proto.EventItemProto.RemoveUserItemUsedResponseProto
import com.lvl6.proto.EventClanProto.RetrieveClanInfoResponseProto
import com.lvl6.proto.EventClanProto.PromoteDemoteClanMemberResponseProto
import com.lvl6.proto.EventPvpProto.QueueUpResponseProto
import com.lvl6.proto.EventUserProto.UserCreateResponseProto
import com.lvl6.proto.EventMonsterProto.EvolveMonsterResponseProto
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto
import com.lvl6.proto.EventMiniEventProto.RetrieveMiniEventResponseProto
import com.lvl6.proto.EventQuestProto.QuestRedeemResponseProto
import com.lvl6.proto.EventMonsterProto.EnhancementWaitTimeCompleteResponseProto
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto
import com.lvl6.proto.EventInAppPurchaseProto.InAppPurchaseResponseProto
import com.lvl6.proto.EventItemProto.RedeemSecretGiftResponseProto
import com.lvl6.proto.EventChatProto.ReceivedGroupChatResponseProto
import com.lvl6.proto.EventUserProto.UpdateUserStrengthResponseProto
import com.lvl6.proto.EventMiniEventProto.RedeemMiniEventRewardResponseProto
import com.lvl6.proto.EventMiniEventProto.UpdateMiniEventResponseProto
import com.lvl6.proto.EventChatProto.SendGroupChatResponseProto
import com.lvl6.proto.EventChatProto.PrivateChatPostResponseProto
import com.lvl6.proto.EventQuestProto.QuestProgressResponseProto
import com.lvl6.proto.EventClanProto.RequestJoinClanResponseProto
import com.lvl6.proto.EventItemProto.TradeItemForSpeedUpsResponseProto
import com.lvl6.proto.EventChatProto.SendAdminMessageResponseProto
import com.lvl6.proto.EventMonsterProto.SellUserMonsterResponseProto
import com.lvl6.proto.EventStaticDataProto.PurgeClientStaticDataResponseProto
import com.lvl6.proto.EventMonsterProto.UnrestrictUserMonsterResponseProto
import com.lvl6.proto.EventPvpProto.SetDefendingMsgResponseProto
import com.lvl6.proto.EventClanProto.SolicitClanHelpResponseProto
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto
import com.lvl6.proto.EventChatProto.TranslateSelectMessagesResponseProto
import com.google.protobuf.Parser

object ResponseEvents {
  val responsesMap = new HashMap[EventProtocolResponse, Class[_ <: GeneratedMessage]]()
  
  def parseEvent(eventType:EventProtocolResponse, bytes:Array[Byte]):Option[_ <: GeneratedMessage]={
    responsesMap.get(eventType) match{
      case Some(cls)=> {
        val parser:Parser[_ <: GeneratedMessage] = cls
          .getDeclaredMethod("getParserForType", null)
          .invoke(null, null)
          .asInstanceOf[Parser[_ <: GeneratedMessage]]
        Some(parser.parseFrom(bytes))
      }
      case None => None
    }
  }
  
  
  responsesMap += (EventProtocolResponse.S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT -> classOf[AcceptAndRejectFbInviteForSlotsResponseProto])
  responsesMap += (EventProtocolResponse.S_ACCEPT_OR_REJECT_CLAN_INVITE_EVENT -> classOf[AcceptOrRejectClanInviteResponseProto])
  responsesMap += (EventProtocolResponse.S_ACHIEVEMENT_PROGRESS_EVENT -> classOf[AchievementProgressResponseProto])
  responsesMap += (EventProtocolResponse.S_ACHIEVEMENT_REDEEM_EVENT -> classOf[AchievementRedeemResponseProto])
  responsesMap += (EventProtocolResponse.S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT -> classOf[AddMonsterToBattleTeamResponseProto])
  responsesMap += (EventProtocolResponse.S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT -> classOf[ApproveOrRejectRequestToJoinClanResponseProto])
  responsesMap += (EventProtocolResponse.S_ATTACK_CLAN_RAID_MONSTER_EVENT -> classOf[AttackClanRaidMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_AVENGE_CLAN_MATE_EVENT -> classOf[AvengeClanMateResponseProto])
  responsesMap += (EventProtocolResponse.S_AWARD_CLAN_RAID_STAGE_REWARD_EVENT -> classOf[AwardClanRaidStageRewardResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_CLAN_AVENGING_EVENT -> classOf[BeginClanAvengingResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_CLAN_RAID_EVENT -> classOf[BeginClanRaidResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_DUNGEON_EVENT -> classOf[BeginDungeonResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_MINI_JOB_EVENT -> classOf[BeginMiniJobResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_OBSTACLE_REMOVAL_EVENT -> classOf[BeginObstacleRemovalResponseProto])
  responsesMap += (EventProtocolResponse.S_BEGIN_PVP_BATTLE_EVENT -> classOf[BeginPvpBattleResponseProto])
  responsesMap += (EventProtocolResponse.S_BOOT_PLAYER_FROM_CLAN_EVENT -> classOf[BootPlayerFromClanResponseProto])
  responsesMap += (EventProtocolResponse.S_CHANGE_CLAN_SETTINGS_EVENT -> classOf[ChangeClanSettingsResponseProto])
  responsesMap += (EventProtocolResponse.S_COLLECT_CLAN_GIFTS_EVENT -> classOf[CollectClanGiftsResponseProto])
  responsesMap += (EventProtocolResponse.S_COLLECT_MONSTER_ENHANCEMENT_EVENT -> classOf[CollectMonsterEnhancementResponseProto])
  responsesMap += (EventProtocolResponse.S_COMBINE_USER_MONSTER_PIECES_EVENT -> classOf[CombineUserMonsterPiecesResponseProto])
  responsesMap += (EventProtocolResponse.S_COMPLETE_BATTLE_ITEM_EVENT -> classOf[CompleteBattleItemResponseProto])
  responsesMap += (EventProtocolResponse.S_COMPLETE_MINI_JOB_EVENT -> classOf[CompleteMiniJobResponseProto])
  responsesMap += (EventProtocolResponse.S_CREATE_BATTLE_ITEM_EVENT -> classOf[CreateBattleItemResponseProto])
  responsesMap += (EventProtocolResponse.S_CREATE_CLAN_EVENT -> classOf[CreateClanResponseProto])
  responsesMap += (EventProtocolResponse.S_CUSTOMIZE_PVP_BOARD_OBSTACLE_EVENT -> classOf[CustomizePvpBoardObstacleResponseProto])
  responsesMap += (EventProtocolResponse.S_DELETE_CLAN_GIFTS_EVENT -> classOf[DeleteClanGiftsResponseProto])
  responsesMap += (EventProtocolResponse.S_DESTROY_MONEY_TREE_STRUCTURE_EVENT -> classOf[DestroyMoneyTreeStructureResponseProto])
  responsesMap += (EventProtocolResponse.S_DEV_EVENT -> classOf[DevResponseProto])
  responsesMap += (EventProtocolResponse.S_DISCARD_BATTLE_ITEM_EVENT -> classOf[DiscardBattleItemResponseProto])
  responsesMap += (EventProtocolResponse.S_ENABLE_APNS_EVENT -> classOf[EnableAPNSResponseProto])
  responsesMap += (EventProtocolResponse.S_END_CLAN_AVENGING_EVENT -> classOf[EndClanAvengingResponseProto])
  responsesMap += (EventProtocolResponse.S_END_CLAN_HELP_EVENT -> classOf[EndClanHelpResponseProto])
  responsesMap += (EventProtocolResponse.S_END_DUNGEON_EVENT -> classOf[EndDungeonResponseProto])
  responsesMap += (EventProtocolResponse.S_END_PVP_BATTLE_EVENT -> classOf[EndPvpBattleResponseProto])
  responsesMap += (EventProtocolResponse.S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT -> classOf[EnhancementWaitTimeCompleteResponseProto])
  responsesMap += (EventProtocolResponse.S_EVOLUTION_FINISHED_EVENT -> classOf[EvolutionFinishedResponseProto])
  responsesMap += (EventProtocolResponse.S_EVOLVE_MONSTER_EVENT -> classOf[EvolveMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_EXCHANGE_GEMS_FOR_RESOURCES_EVENT -> classOf[ExchangeGemsForResourcesResponseProto])
  responsesMap += (EventProtocolResponse.S_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT -> classOf[FinishNormStructWaittimeWithDiamondsResponseProto])
  responsesMap += (EventProtocolResponse.S_FINISH_PERFORMING_RESEARCH_EVENT -> classOf[FinishPerformingResearchResponseProto])
  responsesMap += (EventProtocolResponse.S_FORCE_LOGOUT_EVENT -> classOf[ForceLogoutResponseProto])
  responsesMap += (EventProtocolResponse.S_FULFILL_TEAM_DONATION_SOLICITATION_EVENT -> classOf[FulfillTeamDonationSolicitationResponseProto])
  responsesMap += (EventProtocolResponse.S_GENERAL_NOTIFICATION_EVENT -> classOf[GeneralNotificationResponseProto])
  responsesMap += (EventProtocolResponse.S_GIVE_CLAN_HELP_EVENT -> classOf[GiveClanHelpResponseProto])
  responsesMap += (EventProtocolResponse.S_HEAL_MONSTER_EVENT -> classOf[HealMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_IN_APP_PURCHASE_EVENT -> classOf[InAppPurchaseResponseProto])
  responsesMap += (EventProtocolResponse.S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT -> classOf[IncreaseMonsterInventorySlotResponseProto])
  responsesMap += (EventProtocolResponse.S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT -> classOf[InviteFbFriendsForSlotsResponseProto])
  responsesMap += (EventProtocolResponse.S_INVITE_TO_CLAN_EVENT -> classOf[InviteToClanResponseProto])
  responsesMap += (EventProtocolResponse.S_LEAVE_CLAN_EVENT -> classOf[LeaveClanResponseProto])
  responsesMap += (EventProtocolResponse.S_LEVEL_UP_EVENT -> classOf[LevelUpResponseProto])
  responsesMap += (EventProtocolResponse.S_LOAD_PLAYER_CITY_EVENT -> classOf[LoadPlayerCityResponseProto])
  responsesMap += (EventProtocolResponse.S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT -> classOf[MoveOrRotateNormStructureResponseProto])
  responsesMap += (EventProtocolResponse.S_NORM_STRUCT_WAIT_COMPLETE_EVENT -> classOf[NormStructWaitCompleteResponseProto])
  responsesMap += (EventProtocolResponse.S_OBSTACLE_REMOVAL_COMPLETE_EVENT -> classOf[ObstacleRemovalCompleteResponseProto])
  responsesMap += (EventProtocolResponse.S_PERFORM_RESEARCH_EVENT -> classOf[PerformResearchResponseProto])
  responsesMap += (EventProtocolResponse.S_PRIVATE_CHAT_POST_EVENT -> classOf[PrivateChatPostResponseProto])
  responsesMap += (EventProtocolResponse.S_PROMOTE_DEMOTE_CLAN_MEMBER_EVENT -> classOf[PromoteDemoteClanMemberResponseProto])
  responsesMap += (EventProtocolResponse.S_PURCHASE_BOOSTER_PACK_EVENT -> classOf[PurchaseBoosterPackResponseProto])
  responsesMap += (EventProtocolResponse.S_PURCHASE_NORM_STRUCTURE_EVENT -> classOf[PurchaseNormStructureResponseProto])
  responsesMap += (EventProtocolResponse.S_PURGE_STATIC_DATA_EVENT -> classOf[PurgeClientStaticDataResponseProto])
  responsesMap += (EventProtocolResponse.S_QUEST_ACCEPT_EVENT -> classOf[QuestAcceptResponseProto])
  responsesMap += (EventProtocolResponse.S_QUEST_PROGRESS_EVENT -> classOf[QuestProgressResponseProto])
  responsesMap += (EventProtocolResponse.S_QUEST_REDEEM_EVENT -> classOf[QuestRedeemResponseProto])
  responsesMap += (EventProtocolResponse.S_QUEUE_UP_EVENT -> classOf[QueueUpResponseProto])
  responsesMap += (EventProtocolResponse.S_RECEIVED_CLAN_GIFTS_EVENT -> classOf[ReceivedClanGiftResponseProto])
  responsesMap += (EventProtocolResponse.S_RECEIVED_GROUP_CHAT_EVENT -> classOf[ReceivedGroupChatResponseProto])
  responsesMap += (EventProtocolResponse.S_RECEIVED_RARE_BOOSTER_PURCHASE_EVENT -> classOf[ReceivedRareBoosterPurchaseResponseProto])
  responsesMap += (EventProtocolResponse.S_RECORD_CLAN_RAID_STATS_EVENT -> classOf[RecordClanRaidStatsResponseProto])
  responsesMap += (EventProtocolResponse.S_REDEEM_MINI_EVENT_REWARD_EVENT -> classOf[RedeemMiniEventRewardResponseProto])
  responsesMap += (EventProtocolResponse.S_REDEEM_MINI_JOB_EVENT -> classOf[RedeemMiniJobResponseProto])
  responsesMap += (EventProtocolResponse.S_REDEEM_SECRET_GIFT_EVENT -> classOf[RedeemSecretGiftResponseProto])
  responsesMap += (EventProtocolResponse.S_REFRESH_MINI_JOB_EVENT -> classOf[RefreshMiniJobResponseProto])
  responsesMap += (EventProtocolResponse.S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT -> classOf[RemoveMonsterFromBattleTeamResponseProto])
  responsesMap += (EventProtocolResponse.S_REMOVE_USER_ITEM_USED_EVENT -> classOf[RemoveUserItemUsedResponseProto])
  responsesMap += (EventProtocolResponse.S_REQUEST_JOIN_CLAN_EVENT -> classOf[RequestJoinClanResponseProto])
  responsesMap += (EventProtocolResponse.S_RESTRICT_USER_MONSTER_EVENT -> classOf[RestrictUserMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRACT_REQUEST_JOIN_CLAN_EVENT -> classOf[RetractRequestJoinClanResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_CLAN_DATA_EVENT -> classOf[RetrieveClanDataResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_CLAN_INFO_EVENT -> classOf[RetrieveClanInfoResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT -> classOf[RetrieveCurrencyFromNormStructureResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_MINI_EVENT_EVENT -> classOf[RetrieveMiniEventResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_PRIVATE_CHAT_POST_EVENT -> classOf[RetrievePrivateChatPostsResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_USER_MONSTER_TEAM_EVENT -> classOf[RetrieveUserMonsterTeamResponseProto])
  responsesMap += (EventProtocolResponse.S_RETRIEVE_USERS_FOR_USER_IDS_EVENT -> classOf[RetrieveUsersForUserIdsResponseProto])
  responsesMap += (EventProtocolResponse.S_REVIVE_IN_DUNGEON_EVENT -> classOf[ReviveInDungeonResponseProto])
  responsesMap += (EventProtocolResponse.S_SELL_USER_MONSTER_EVENT -> classOf[SellUserMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_SEND_ADMIN_MESSAGE_EVENT -> classOf[SendAdminMessageResponseProto])
  responsesMap += (EventProtocolResponse.S_SEND_GROUP_CHAT_EVENT -> classOf[SendGroupChatResponseProto])
  responsesMap += (EventProtocolResponse.S_SET_AVATAR_MONSTER_EVENT -> classOf[SetAvatarMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_SET_DEFENDING_MSG_EVENT -> classOf[SetDefendingMsgResponseProto])
  responsesMap += (EventProtocolResponse.S_SET_FACEBOOK_ID_EVENT -> classOf[SetFacebookIdResponseProto])
  responsesMap += (EventProtocolResponse.S_SET_GAME_CENTER_ID_EVENT -> classOf[SetGameCenterIdResponseProto])
  responsesMap += (EventProtocolResponse.S_SOLICIT_CLAN_HELP_EVENT -> classOf[SolicitClanHelpResponseProto])
  responsesMap += (EventProtocolResponse.S_SOLICIT_TEAM_DONATION_EVENT -> classOf[SolicitTeamDonationResponseProto])
  responsesMap += (EventProtocolResponse.S_SPAWN_MINI_JOB_EVENT -> classOf[SpawnMiniJobResponseProto])
  responsesMap += (EventProtocolResponse.S_SPAWN_OBSTACLE_EVENT -> classOf[SpawnObstacleResponseProto])
  responsesMap += (EventProtocolResponse.S_STARTUP_EVENT -> classOf[StartupResponseProto])
  responsesMap += (EventProtocolResponse.S_SUBMIT_MONSTER_ENHANCEMENT_EVENT -> classOf[SubmitMonsterEnhancementResponseProto])
  responsesMap += (EventProtocolResponse.S_TRADE_ITEM_FOR_BOOSTER_EVENT -> classOf[TradeItemForBoosterResponseProto])
  responsesMap += (EventProtocolResponse.S_TRADE_ITEM_FOR_RESOURCES_EVENT -> classOf[TradeItemForResourcesResponseProto])
  responsesMap += (EventProtocolResponse.S_TRADE_ITEM_FOR_SPEED_UPS_EVENT -> classOf[TradeItemForSpeedUpsResponseProto])
  responsesMap += (EventProtocolResponse.S_TRANSFER_CLAN_OWNERSHIP -> classOf[TransferClanOwnershipResponseProto])
  responsesMap += (EventProtocolResponse.S_TRANSLATE_SELECT_MESSAGES_EVENT -> classOf[TranslateSelectMessagesResponseProto])
  responsesMap += (EventProtocolResponse.S_UNRESTRICT_USER_MONSTER_EVENT -> classOf[UnrestrictUserMonsterResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_CLIENT_TASK_STATE_EVENT -> classOf[UpdateClientTaskStateResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_CLIENT_USER_EVENT -> classOf[UpdateClientUserResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_MINI_EVENT_EVENT -> classOf[UpdateMiniEventResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_MONSTER_HEALTH_EVENT -> classOf[UpdateMonsterHealthResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_USER_CURRENCY_EVENT -> classOf[UpdateUserCurrencyResponseProto])
  responsesMap += (EventProtocolResponse.S_UPDATE_USER_STRENGTH_EVENT -> classOf[UpdateUserStrengthResponseProto])
  responsesMap += (EventProtocolResponse.S_UPGRADE_NORM_STRUCTURE_EVENT -> classOf[UpgradeNormStructureResponseProto])
  responsesMap += (EventProtocolResponse.S_USER_CREATE_EVENT -> classOf[UserCreateResponseProto])
  responsesMap += (EventProtocolResponse.S_VOID_TEAM_DONATION_SOLICITATION_EVENT -> classOf[VoidTeamDonationSolicitationResponseProto])

}