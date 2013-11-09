// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Protocols.proto

package com.lvl6.proto;

public final class ProtocolsProto {
  private ProtocolsProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum EventProtocolRequest
      implements com.google.protobuf.ProtocolMessageEnum {
    C_STARTUP_EVENT(0, 1),
    C_IN_APP_PURCHASE_EVENT(1, 2),
    C_PURCHASE_NORM_STRUCTURE_EVENT(2, 3),
    C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT(3, 4),
    C_SELL_NORM_STRUCTURE_EVENT(4, 5),
    C_UPGRADE_NORM_STRUCTURE_EVENT(5, 6),
    C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT(6, 7),
    C_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT(7, 8),
    C_NORM_STRUCT_WAIT_COMPLETE_EVENT(8, 9),
    C_LOAD_PLAYER_CITY_EVENT(9, 10),
    C_QUEST_ACCEPT_EVENT(10, 12),
    C_QUEST_PROGRESS_EVENT(11, 13),
    C_QUEST_REDEEM_EVENT(12, 14),
    C_PURCHASE_CITY_EXPANSION_EVENT(13, 15),
    C_EXPANSION_WAIT_COMPLETE_EVENT(14, 16),
    C_LEVEL_UP_EVENT(15, 17),
    C_ENABLE_APNS_EVENT(16, 18),
    C_USER_CREATE_EVENT(17, 19),
    C_LOAD_CITY_EVENT(18, 20),
    C_RETRIEVE_USERS_FOR_USER_IDS_EVENT(19, 21),
    C_EARN_FREE_DIAMONDS_EVENT(20, 22),
    C_SEND_GROUP_CHAT_EVENT(21, 23),
    C_CREATE_CLAN_EVENT(22, 24),
    C_LEAVE_CLAN_EVENT(23, 25),
    C_REQUEST_JOIN_CLAN_EVENT(24, 26),
    C_RETRACT_REQUEST_JOIN_CLAN_EVENT(25, 27),
    C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT(26, 28),
    C_TRANSFER_CLAN_OWNERSHIP(27, 29),
    C_RETRIEVE_CLAN_INFO_EVENT(28, 30),
    C_CHANGE_CLAN_DESCRIPTION_EVENT(29, 31),
    C_BOOT_PLAYER_FROM_CLAN_EVENT(30, 32),
    C_PICK_LOCK_BOX_EVENT(31, 33),
    C_RETRIEVE_TOURNAMENT_RANKINGS_EVENT(32, 34),
    C_SUBMIT_MONSTER_ENHANCEMENT_EVENT(33, 35),
    C_RETRIEVE_BOOSTER_PACK_EVENT(34, 36),
    C_PURCHASE_BOOSTER_PACK_EVENT(35, 37),
    C_RESET_BOOSTER_PACK_EVENT(36, 38),
    C_CHANGE_CLAN_JOIN_TYPE_EVENT(37, 39),
    C_PRIVATE_CHAT_POST_EVENT(38, 40),
    C_RETRIEVE_PRIVATE_CHAT_POST_EVENT(39, 41),
    C_REDEEM_USER_LOCK_BOX_ITEMS_EVENT(40, 42),
    C_BEGIN_DUNGEON_EVENT(41, 43),
    C_END_DUNGEON_EVENT(42, 44),
    C_REVIVE_IN_DUNGEON_EVENT(43, 45),
    C_QUEUE_UP_EVENT(44, 46),
    C_UPDATE_MONSTER_HEALTH_EVENT(45, 47),
    C_HEAL_MONSTER_EVENT(46, 48),
    C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT(47, 49),
    C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT(48, 50),
    C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT(49, 51),
    C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT(50, 52),
    C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT(51, 53),
    C_COMBINE_USER_MONSTER_PIECES_EVENT(52, 54),
    C_SELL_USER_MONSTER_EVENT(53, 55),
    C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT(54, 56),
    C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT(55, 57),
    C_LOGOUT_EVENT(56, 101),
    ;
    
    public static final int C_STARTUP_EVENT_VALUE = 1;
    public static final int C_IN_APP_PURCHASE_EVENT_VALUE = 2;
    public static final int C_PURCHASE_NORM_STRUCTURE_EVENT_VALUE = 3;
    public static final int C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT_VALUE = 4;
    public static final int C_SELL_NORM_STRUCTURE_EVENT_VALUE = 5;
    public static final int C_UPGRADE_NORM_STRUCTURE_EVENT_VALUE = 6;
    public static final int C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT_VALUE = 7;
    public static final int C_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT_VALUE = 8;
    public static final int C_NORM_STRUCT_WAIT_COMPLETE_EVENT_VALUE = 9;
    public static final int C_LOAD_PLAYER_CITY_EVENT_VALUE = 10;
    public static final int C_QUEST_ACCEPT_EVENT_VALUE = 12;
    public static final int C_QUEST_PROGRESS_EVENT_VALUE = 13;
    public static final int C_QUEST_REDEEM_EVENT_VALUE = 14;
    public static final int C_PURCHASE_CITY_EXPANSION_EVENT_VALUE = 15;
    public static final int C_EXPANSION_WAIT_COMPLETE_EVENT_VALUE = 16;
    public static final int C_LEVEL_UP_EVENT_VALUE = 17;
    public static final int C_ENABLE_APNS_EVENT_VALUE = 18;
    public static final int C_USER_CREATE_EVENT_VALUE = 19;
    public static final int C_LOAD_CITY_EVENT_VALUE = 20;
    public static final int C_RETRIEVE_USERS_FOR_USER_IDS_EVENT_VALUE = 21;
    public static final int C_EARN_FREE_DIAMONDS_EVENT_VALUE = 22;
    public static final int C_SEND_GROUP_CHAT_EVENT_VALUE = 23;
    public static final int C_CREATE_CLAN_EVENT_VALUE = 24;
    public static final int C_LEAVE_CLAN_EVENT_VALUE = 25;
    public static final int C_REQUEST_JOIN_CLAN_EVENT_VALUE = 26;
    public static final int C_RETRACT_REQUEST_JOIN_CLAN_EVENT_VALUE = 27;
    public static final int C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT_VALUE = 28;
    public static final int C_TRANSFER_CLAN_OWNERSHIP_VALUE = 29;
    public static final int C_RETRIEVE_CLAN_INFO_EVENT_VALUE = 30;
    public static final int C_CHANGE_CLAN_DESCRIPTION_EVENT_VALUE = 31;
    public static final int C_BOOT_PLAYER_FROM_CLAN_EVENT_VALUE = 32;
    public static final int C_PICK_LOCK_BOX_EVENT_VALUE = 33;
    public static final int C_RETRIEVE_TOURNAMENT_RANKINGS_EVENT_VALUE = 34;
    public static final int C_SUBMIT_MONSTER_ENHANCEMENT_EVENT_VALUE = 35;
    public static final int C_RETRIEVE_BOOSTER_PACK_EVENT_VALUE = 36;
    public static final int C_PURCHASE_BOOSTER_PACK_EVENT_VALUE = 37;
    public static final int C_RESET_BOOSTER_PACK_EVENT_VALUE = 38;
    public static final int C_CHANGE_CLAN_JOIN_TYPE_EVENT_VALUE = 39;
    public static final int C_PRIVATE_CHAT_POST_EVENT_VALUE = 40;
    public static final int C_RETRIEVE_PRIVATE_CHAT_POST_EVENT_VALUE = 41;
    public static final int C_REDEEM_USER_LOCK_BOX_ITEMS_EVENT_VALUE = 42;
    public static final int C_BEGIN_DUNGEON_EVENT_VALUE = 43;
    public static final int C_END_DUNGEON_EVENT_VALUE = 44;
    public static final int C_REVIVE_IN_DUNGEON_EVENT_VALUE = 45;
    public static final int C_QUEUE_UP_EVENT_VALUE = 46;
    public static final int C_UPDATE_MONSTER_HEALTH_EVENT_VALUE = 47;
    public static final int C_HEAL_MONSTER_EVENT_VALUE = 48;
    public static final int C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT_VALUE = 49;
    public static final int C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT_VALUE = 50;
    public static final int C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT_VALUE = 51;
    public static final int C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT_VALUE = 52;
    public static final int C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT_VALUE = 53;
    public static final int C_COMBINE_USER_MONSTER_PIECES_EVENT_VALUE = 54;
    public static final int C_SELL_USER_MONSTER_EVENT_VALUE = 55;
    public static final int C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT_VALUE = 56;
    public static final int C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT_VALUE = 57;
    public static final int C_LOGOUT_EVENT_VALUE = 101;
    
    
    public final int getNumber() { return value; }
    
    public static EventProtocolRequest valueOf(int value) {
      switch (value) {
        case 1: return C_STARTUP_EVENT;
        case 2: return C_IN_APP_PURCHASE_EVENT;
        case 3: return C_PURCHASE_NORM_STRUCTURE_EVENT;
        case 4: return C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT;
        case 5: return C_SELL_NORM_STRUCTURE_EVENT;
        case 6: return C_UPGRADE_NORM_STRUCTURE_EVENT;
        case 7: return C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT;
        case 8: return C_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT;
        case 9: return C_NORM_STRUCT_WAIT_COMPLETE_EVENT;
        case 10: return C_LOAD_PLAYER_CITY_EVENT;
        case 12: return C_QUEST_ACCEPT_EVENT;
        case 13: return C_QUEST_PROGRESS_EVENT;
        case 14: return C_QUEST_REDEEM_EVENT;
        case 15: return C_PURCHASE_CITY_EXPANSION_EVENT;
        case 16: return C_EXPANSION_WAIT_COMPLETE_EVENT;
        case 17: return C_LEVEL_UP_EVENT;
        case 18: return C_ENABLE_APNS_EVENT;
        case 19: return C_USER_CREATE_EVENT;
        case 20: return C_LOAD_CITY_EVENT;
        case 21: return C_RETRIEVE_USERS_FOR_USER_IDS_EVENT;
        case 22: return C_EARN_FREE_DIAMONDS_EVENT;
        case 23: return C_SEND_GROUP_CHAT_EVENT;
        case 24: return C_CREATE_CLAN_EVENT;
        case 25: return C_LEAVE_CLAN_EVENT;
        case 26: return C_REQUEST_JOIN_CLAN_EVENT;
        case 27: return C_RETRACT_REQUEST_JOIN_CLAN_EVENT;
        case 28: return C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT;
        case 29: return C_TRANSFER_CLAN_OWNERSHIP;
        case 30: return C_RETRIEVE_CLAN_INFO_EVENT;
        case 31: return C_CHANGE_CLAN_DESCRIPTION_EVENT;
        case 32: return C_BOOT_PLAYER_FROM_CLAN_EVENT;
        case 33: return C_PICK_LOCK_BOX_EVENT;
        case 34: return C_RETRIEVE_TOURNAMENT_RANKINGS_EVENT;
        case 35: return C_SUBMIT_MONSTER_ENHANCEMENT_EVENT;
        case 36: return C_RETRIEVE_BOOSTER_PACK_EVENT;
        case 37: return C_PURCHASE_BOOSTER_PACK_EVENT;
        case 38: return C_RESET_BOOSTER_PACK_EVENT;
        case 39: return C_CHANGE_CLAN_JOIN_TYPE_EVENT;
        case 40: return C_PRIVATE_CHAT_POST_EVENT;
        case 41: return C_RETRIEVE_PRIVATE_CHAT_POST_EVENT;
        case 42: return C_REDEEM_USER_LOCK_BOX_ITEMS_EVENT;
        case 43: return C_BEGIN_DUNGEON_EVENT;
        case 44: return C_END_DUNGEON_EVENT;
        case 45: return C_REVIVE_IN_DUNGEON_EVENT;
        case 46: return C_QUEUE_UP_EVENT;
        case 47: return C_UPDATE_MONSTER_HEALTH_EVENT;
        case 48: return C_HEAL_MONSTER_EVENT;
        case 49: return C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT;
        case 50: return C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT;
        case 51: return C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT;
        case 52: return C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
        case 53: return C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT;
        case 54: return C_COMBINE_USER_MONSTER_PIECES_EVENT;
        case 55: return C_SELL_USER_MONSTER_EVENT;
        case 56: return C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT;
        case 57: return C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
        case 101: return C_LOGOUT_EVENT;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<EventProtocolRequest>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<EventProtocolRequest>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<EventProtocolRequest>() {
            public EventProtocolRequest findValueByNumber(int number) {
              return EventProtocolRequest.valueOf(number);
            }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.lvl6.proto.ProtocolsProto.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final EventProtocolRequest[] VALUES = {
      C_STARTUP_EVENT, C_IN_APP_PURCHASE_EVENT, C_PURCHASE_NORM_STRUCTURE_EVENT, C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT, C_SELL_NORM_STRUCTURE_EVENT, C_UPGRADE_NORM_STRUCTURE_EVENT, C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT, C_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT, C_NORM_STRUCT_WAIT_COMPLETE_EVENT, C_LOAD_PLAYER_CITY_EVENT, C_QUEST_ACCEPT_EVENT, C_QUEST_PROGRESS_EVENT, C_QUEST_REDEEM_EVENT, C_PURCHASE_CITY_EXPANSION_EVENT, C_EXPANSION_WAIT_COMPLETE_EVENT, C_LEVEL_UP_EVENT, C_ENABLE_APNS_EVENT, C_USER_CREATE_EVENT, C_LOAD_CITY_EVENT, C_RETRIEVE_USERS_FOR_USER_IDS_EVENT, C_EARN_FREE_DIAMONDS_EVENT, C_SEND_GROUP_CHAT_EVENT, C_CREATE_CLAN_EVENT, C_LEAVE_CLAN_EVENT, C_REQUEST_JOIN_CLAN_EVENT, C_RETRACT_REQUEST_JOIN_CLAN_EVENT, C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT, C_TRANSFER_CLAN_OWNERSHIP, C_RETRIEVE_CLAN_INFO_EVENT, C_CHANGE_CLAN_DESCRIPTION_EVENT, C_BOOT_PLAYER_FROM_CLAN_EVENT, C_PICK_LOCK_BOX_EVENT, C_RETRIEVE_TOURNAMENT_RANKINGS_EVENT, C_SUBMIT_MONSTER_ENHANCEMENT_EVENT, C_RETRIEVE_BOOSTER_PACK_EVENT, C_PURCHASE_BOOSTER_PACK_EVENT, C_RESET_BOOSTER_PACK_EVENT, C_CHANGE_CLAN_JOIN_TYPE_EVENT, C_PRIVATE_CHAT_POST_EVENT, C_RETRIEVE_PRIVATE_CHAT_POST_EVENT, C_REDEEM_USER_LOCK_BOX_ITEMS_EVENT, C_BEGIN_DUNGEON_EVENT, C_END_DUNGEON_EVENT, C_REVIVE_IN_DUNGEON_EVENT, C_QUEUE_UP_EVENT, C_UPDATE_MONSTER_HEALTH_EVENT, C_HEAL_MONSTER_EVENT, C_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT, C_ADD_MONSTER_TO_BATTLE_TEAM_EVENT, C_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT, C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT, C_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT, C_COMBINE_USER_MONSTER_PIECES_EVENT, C_SELL_USER_MONSTER_EVENT, C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT, C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT, C_LOGOUT_EVENT, 
    };
    
    public static EventProtocolRequest valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    
    private final int index;
    private final int value;
    
    private EventProtocolRequest(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.EventProtocolRequest)
  }
  
  public enum EventProtocolResponse
      implements com.google.protobuf.ProtocolMessageEnum {
    S_STARTUP_EVENT(0, 1),
    S_IN_APP_PURCHASE_EVENT(1, 2),
    S_PURCHASE_NORM_STRUCTURE_EVENT(2, 3),
    S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT(3, 4),
    S_SELL_NORM_STRUCTURE_EVENT(4, 5),
    S_UPGRADE_NORM_STRUCTURE_EVENT(5, 6),
    S_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT(6, 7),
    S_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT(7, 8),
    S_NORM_STRUCT_WAIT_COMPLETE_EVENT(8, 9),
    S_LOAD_PLAYER_CITY_EVENT(9, 10),
    S_QUEST_ACCEPT_EVENT(10, 12),
    S_QUEST_PROGRESS_EVENT(11, 13),
    S_QUEST_REDEEM_EVENT(12, 14),
    S_PURCHASE_CITY_EXPANSION_EVENT(13, 15),
    S_EXPANSION_WAIT_COMPLETE_EVENT(14, 16),
    S_LEVEL_UP_EVENT(15, 17),
    S_ENABLE_APNS_EVENT(16, 18),
    S_USER_CREATE_EVENT(17, 19),
    S_LOAD_CITY_EVENT(18, 20),
    S_RETRIEVE_USERS_FOR_USER_IDS_EVENT(19, 21),
    S_EARN_FREE_DIAMONDS_EVENT(20, 22),
    S_SEND_GROUP_CHAT_EVENT(21, 23),
    S_CREATE_CLAN_EVENT(22, 24),
    S_LEAVE_CLAN_EVENT(23, 25),
    S_REQUEST_JOIN_CLAN_EVENT(24, 26),
    S_RETRACT_REQUEST_JOIN_CLAN_EVENT(25, 27),
    S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT(26, 28),
    S_TRANSFER_CLAN_OWNERSHIP(27, 29),
    S_RETRIEVE_CLAN_INFO_EVENT(28, 30),
    S_CHANGE_CLAN_DESCRIPTION_EVENT(29, 31),
    S_BOOT_PLAYER_FROM_CLAN_EVENT(30, 32),
    S_PICK_LOCK_BOX_EVENT(31, 33),
    S_RETRIEVE_TOURNAMENT_RANKINGS_EVENT(32, 34),
    S_SUBMIT_MONSTER_ENHANCEMENT_EVENT(33, 35),
    S_RETRIEVE_BOOSTER_PACK_EVENT(34, 36),
    S_PURCHASE_BOOSTER_PACK_EVENT(35, 37),
    S_RESET_BOOSTER_PACK_EVENT(36, 38),
    S_CHANGE_CLAN_JOIN_TYPE_EVENT(37, 39),
    S_PRIVATE_CHAT_POST_EVENT(38, 40),
    S_RETRIEVE_PRIVATE_CHAT_POST_EVENT(39, 41),
    S_REDEEM_USER_LOCK_BOX_ITEMS_EVENT(40, 42),
    S_BEGIN_DUNGEON_EVENT(41, 43),
    S_END_DUNGEON_EVENT(42, 44),
    S_REVIVE_IN_DUNGEON_EVENT(43, 45),
    S_QUEUE_UP_EVENT(44, 46),
    S_UPDATE_MONSTER_HEALTH_EVENT(45, 47),
    S_HEAL_MONSTER_EVENT(46, 48),
    S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT(47, 49),
    S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT(48, 50),
    S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT(49, 51),
    S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT(50, 52),
    S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT(51, 53),
    S_COMBINE_USER_MONSTER_PIECES_EVENT(52, 54),
    S_SELL_USER_MONSTER_EVENT(53, 55),
    S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT(54, 56),
    S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT(55, 57),
    S_UPDATE_CLIENT_USER_EVENT(56, 101),
    S_REFERRAL_CODE_USED_EVENT(57, 102),
    S_PURGE_STATIC_DATA_EVENT(58, 103),
    S_RECEIVED_GROUP_CHAT_EVENT(59, 104),
    S_SEND_ADMIN_MESSAGE_EVENT(60, 105),
    S_GENERAL_NOTIFICATION_EVENT(61, 106),
    S_RECEIVED_RARE_BOOSTER_PURCHASE_EVENT(62, 107),
    ;
    
    public static final int S_STARTUP_EVENT_VALUE = 1;
    public static final int S_IN_APP_PURCHASE_EVENT_VALUE = 2;
    public static final int S_PURCHASE_NORM_STRUCTURE_EVENT_VALUE = 3;
    public static final int S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT_VALUE = 4;
    public static final int S_SELL_NORM_STRUCTURE_EVENT_VALUE = 5;
    public static final int S_UPGRADE_NORM_STRUCTURE_EVENT_VALUE = 6;
    public static final int S_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT_VALUE = 7;
    public static final int S_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT_VALUE = 8;
    public static final int S_NORM_STRUCT_WAIT_COMPLETE_EVENT_VALUE = 9;
    public static final int S_LOAD_PLAYER_CITY_EVENT_VALUE = 10;
    public static final int S_QUEST_ACCEPT_EVENT_VALUE = 12;
    public static final int S_QUEST_PROGRESS_EVENT_VALUE = 13;
    public static final int S_QUEST_REDEEM_EVENT_VALUE = 14;
    public static final int S_PURCHASE_CITY_EXPANSION_EVENT_VALUE = 15;
    public static final int S_EXPANSION_WAIT_COMPLETE_EVENT_VALUE = 16;
    public static final int S_LEVEL_UP_EVENT_VALUE = 17;
    public static final int S_ENABLE_APNS_EVENT_VALUE = 18;
    public static final int S_USER_CREATE_EVENT_VALUE = 19;
    public static final int S_LOAD_CITY_EVENT_VALUE = 20;
    public static final int S_RETRIEVE_USERS_FOR_USER_IDS_EVENT_VALUE = 21;
    public static final int S_EARN_FREE_DIAMONDS_EVENT_VALUE = 22;
    public static final int S_SEND_GROUP_CHAT_EVENT_VALUE = 23;
    public static final int S_CREATE_CLAN_EVENT_VALUE = 24;
    public static final int S_LEAVE_CLAN_EVENT_VALUE = 25;
    public static final int S_REQUEST_JOIN_CLAN_EVENT_VALUE = 26;
    public static final int S_RETRACT_REQUEST_JOIN_CLAN_EVENT_VALUE = 27;
    public static final int S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT_VALUE = 28;
    public static final int S_TRANSFER_CLAN_OWNERSHIP_VALUE = 29;
    public static final int S_RETRIEVE_CLAN_INFO_EVENT_VALUE = 30;
    public static final int S_CHANGE_CLAN_DESCRIPTION_EVENT_VALUE = 31;
    public static final int S_BOOT_PLAYER_FROM_CLAN_EVENT_VALUE = 32;
    public static final int S_PICK_LOCK_BOX_EVENT_VALUE = 33;
    public static final int S_RETRIEVE_TOURNAMENT_RANKINGS_EVENT_VALUE = 34;
    public static final int S_SUBMIT_MONSTER_ENHANCEMENT_EVENT_VALUE = 35;
    public static final int S_RETRIEVE_BOOSTER_PACK_EVENT_VALUE = 36;
    public static final int S_PURCHASE_BOOSTER_PACK_EVENT_VALUE = 37;
    public static final int S_RESET_BOOSTER_PACK_EVENT_VALUE = 38;
    public static final int S_CHANGE_CLAN_JOIN_TYPE_EVENT_VALUE = 39;
    public static final int S_PRIVATE_CHAT_POST_EVENT_VALUE = 40;
    public static final int S_RETRIEVE_PRIVATE_CHAT_POST_EVENT_VALUE = 41;
    public static final int S_REDEEM_USER_LOCK_BOX_ITEMS_EVENT_VALUE = 42;
    public static final int S_BEGIN_DUNGEON_EVENT_VALUE = 43;
    public static final int S_END_DUNGEON_EVENT_VALUE = 44;
    public static final int S_REVIVE_IN_DUNGEON_EVENT_VALUE = 45;
    public static final int S_QUEUE_UP_EVENT_VALUE = 46;
    public static final int S_UPDATE_MONSTER_HEALTH_EVENT_VALUE = 47;
    public static final int S_HEAL_MONSTER_EVENT_VALUE = 48;
    public static final int S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT_VALUE = 49;
    public static final int S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT_VALUE = 50;
    public static final int S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT_VALUE = 51;
    public static final int S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT_VALUE = 52;
    public static final int S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT_VALUE = 53;
    public static final int S_COMBINE_USER_MONSTER_PIECES_EVENT_VALUE = 54;
    public static final int S_SELL_USER_MONSTER_EVENT_VALUE = 55;
    public static final int S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT_VALUE = 56;
    public static final int S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT_VALUE = 57;
    public static final int S_UPDATE_CLIENT_USER_EVENT_VALUE = 101;
    public static final int S_REFERRAL_CODE_USED_EVENT_VALUE = 102;
    public static final int S_PURGE_STATIC_DATA_EVENT_VALUE = 103;
    public static final int S_RECEIVED_GROUP_CHAT_EVENT_VALUE = 104;
    public static final int S_SEND_ADMIN_MESSAGE_EVENT_VALUE = 105;
    public static final int S_GENERAL_NOTIFICATION_EVENT_VALUE = 106;
    public static final int S_RECEIVED_RARE_BOOSTER_PURCHASE_EVENT_VALUE = 107;
    
    
    public final int getNumber() { return value; }
    
    public static EventProtocolResponse valueOf(int value) {
      switch (value) {
        case 1: return S_STARTUP_EVENT;
        case 2: return S_IN_APP_PURCHASE_EVENT;
        case 3: return S_PURCHASE_NORM_STRUCTURE_EVENT;
        case 4: return S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT;
        case 5: return S_SELL_NORM_STRUCTURE_EVENT;
        case 6: return S_UPGRADE_NORM_STRUCTURE_EVENT;
        case 7: return S_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT;
        case 8: return S_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT;
        case 9: return S_NORM_STRUCT_WAIT_COMPLETE_EVENT;
        case 10: return S_LOAD_PLAYER_CITY_EVENT;
        case 12: return S_QUEST_ACCEPT_EVENT;
        case 13: return S_QUEST_PROGRESS_EVENT;
        case 14: return S_QUEST_REDEEM_EVENT;
        case 15: return S_PURCHASE_CITY_EXPANSION_EVENT;
        case 16: return S_EXPANSION_WAIT_COMPLETE_EVENT;
        case 17: return S_LEVEL_UP_EVENT;
        case 18: return S_ENABLE_APNS_EVENT;
        case 19: return S_USER_CREATE_EVENT;
        case 20: return S_LOAD_CITY_EVENT;
        case 21: return S_RETRIEVE_USERS_FOR_USER_IDS_EVENT;
        case 22: return S_EARN_FREE_DIAMONDS_EVENT;
        case 23: return S_SEND_GROUP_CHAT_EVENT;
        case 24: return S_CREATE_CLAN_EVENT;
        case 25: return S_LEAVE_CLAN_EVENT;
        case 26: return S_REQUEST_JOIN_CLAN_EVENT;
        case 27: return S_RETRACT_REQUEST_JOIN_CLAN_EVENT;
        case 28: return S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT;
        case 29: return S_TRANSFER_CLAN_OWNERSHIP;
        case 30: return S_RETRIEVE_CLAN_INFO_EVENT;
        case 31: return S_CHANGE_CLAN_DESCRIPTION_EVENT;
        case 32: return S_BOOT_PLAYER_FROM_CLAN_EVENT;
        case 33: return S_PICK_LOCK_BOX_EVENT;
        case 34: return S_RETRIEVE_TOURNAMENT_RANKINGS_EVENT;
        case 35: return S_SUBMIT_MONSTER_ENHANCEMENT_EVENT;
        case 36: return S_RETRIEVE_BOOSTER_PACK_EVENT;
        case 37: return S_PURCHASE_BOOSTER_PACK_EVENT;
        case 38: return S_RESET_BOOSTER_PACK_EVENT;
        case 39: return S_CHANGE_CLAN_JOIN_TYPE_EVENT;
        case 40: return S_PRIVATE_CHAT_POST_EVENT;
        case 41: return S_RETRIEVE_PRIVATE_CHAT_POST_EVENT;
        case 42: return S_REDEEM_USER_LOCK_BOX_ITEMS_EVENT;
        case 43: return S_BEGIN_DUNGEON_EVENT;
        case 44: return S_END_DUNGEON_EVENT;
        case 45: return S_REVIVE_IN_DUNGEON_EVENT;
        case 46: return S_QUEUE_UP_EVENT;
        case 47: return S_UPDATE_MONSTER_HEALTH_EVENT;
        case 48: return S_HEAL_MONSTER_EVENT;
        case 49: return S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT;
        case 50: return S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT;
        case 51: return S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT;
        case 52: return S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
        case 53: return S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT;
        case 54: return S_COMBINE_USER_MONSTER_PIECES_EVENT;
        case 55: return S_SELL_USER_MONSTER_EVENT;
        case 56: return S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT;
        case 57: return S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
        case 101: return S_UPDATE_CLIENT_USER_EVENT;
        case 102: return S_REFERRAL_CODE_USED_EVENT;
        case 103: return S_PURGE_STATIC_DATA_EVENT;
        case 104: return S_RECEIVED_GROUP_CHAT_EVENT;
        case 105: return S_SEND_ADMIN_MESSAGE_EVENT;
        case 106: return S_GENERAL_NOTIFICATION_EVENT;
        case 107: return S_RECEIVED_RARE_BOOSTER_PURCHASE_EVENT;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<EventProtocolResponse>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<EventProtocolResponse>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<EventProtocolResponse>() {
            public EventProtocolResponse findValueByNumber(int number) {
              return EventProtocolResponse.valueOf(number);
            }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.lvl6.proto.ProtocolsProto.getDescriptor().getEnumTypes().get(1);
    }
    
    private static final EventProtocolResponse[] VALUES = {
      S_STARTUP_EVENT, S_IN_APP_PURCHASE_EVENT, S_PURCHASE_NORM_STRUCTURE_EVENT, S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT, S_SELL_NORM_STRUCTURE_EVENT, S_UPGRADE_NORM_STRUCTURE_EVENT, S_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT, S_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT, S_NORM_STRUCT_WAIT_COMPLETE_EVENT, S_LOAD_PLAYER_CITY_EVENT, S_QUEST_ACCEPT_EVENT, S_QUEST_PROGRESS_EVENT, S_QUEST_REDEEM_EVENT, S_PURCHASE_CITY_EXPANSION_EVENT, S_EXPANSION_WAIT_COMPLETE_EVENT, S_LEVEL_UP_EVENT, S_ENABLE_APNS_EVENT, S_USER_CREATE_EVENT, S_LOAD_CITY_EVENT, S_RETRIEVE_USERS_FOR_USER_IDS_EVENT, S_EARN_FREE_DIAMONDS_EVENT, S_SEND_GROUP_CHAT_EVENT, S_CREATE_CLAN_EVENT, S_LEAVE_CLAN_EVENT, S_REQUEST_JOIN_CLAN_EVENT, S_RETRACT_REQUEST_JOIN_CLAN_EVENT, S_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT, S_TRANSFER_CLAN_OWNERSHIP, S_RETRIEVE_CLAN_INFO_EVENT, S_CHANGE_CLAN_DESCRIPTION_EVENT, S_BOOT_PLAYER_FROM_CLAN_EVENT, S_PICK_LOCK_BOX_EVENT, S_RETRIEVE_TOURNAMENT_RANKINGS_EVENT, S_SUBMIT_MONSTER_ENHANCEMENT_EVENT, S_RETRIEVE_BOOSTER_PACK_EVENT, S_PURCHASE_BOOSTER_PACK_EVENT, S_RESET_BOOSTER_PACK_EVENT, S_CHANGE_CLAN_JOIN_TYPE_EVENT, S_PRIVATE_CHAT_POST_EVENT, S_RETRIEVE_PRIVATE_CHAT_POST_EVENT, S_REDEEM_USER_LOCK_BOX_ITEMS_EVENT, S_BEGIN_DUNGEON_EVENT, S_END_DUNGEON_EVENT, S_REVIVE_IN_DUNGEON_EVENT, S_QUEUE_UP_EVENT, S_UPDATE_MONSTER_HEALTH_EVENT, S_HEAL_MONSTER_EVENT, S_HEAL_MONSTER_WAIT_TIME_COMPLETE_EVENT, S_ADD_MONSTER_TO_BATTLE_TEAM_EVENT, S_REMOVE_MONSTER_FROM_BATTLE_TEAM_EVENT, S_INCREASE_MONSTER_INVENTORY_SLOT_EVENT, S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVENT, S_COMBINE_USER_MONSTER_PIECES_EVENT, S_SELL_USER_MONSTER_EVENT, S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT, S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT, S_UPDATE_CLIENT_USER_EVENT, S_REFERRAL_CODE_USED_EVENT, S_PURGE_STATIC_DATA_EVENT, S_RECEIVED_GROUP_CHAT_EVENT, S_SEND_ADMIN_MESSAGE_EVENT, S_GENERAL_NOTIFICATION_EVENT, S_RECEIVED_RARE_BOOSTER_PURCHASE_EVENT, 
    };
    
    public static EventProtocolResponse valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    
    private final int index;
    private final int value;
    
    private EventProtocolResponse(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.EventProtocolResponse)
  }
  
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017Protocols.proto\022\016com.lvl6.proto\032\014Battl" +
      "e.proto\032\026BoosterPackStuff.proto\032\nChat.pr" +
      "oto\032\nCity.proto\032\nClan.proto\032\017EventApns.p" +
      "roto\032\026EventBoosterPack.proto\032\017EventChat." +
      "proto\032\017EventCity.proto\032\017EventClan.proto\032" +
      "\022EventDungeon.proto\032\030EventInAppPurchase." +
      "proto\032\022EventMonster.proto\032\016EventPvp.prot" +
      "o\032\020EventQuest.proto\032\023EventReferral.proto" +
      "\032\022EventStartup.proto\032\025EventStaticData.pr" +
      "oto\032\024EventStructure.proto\032\025EventTourname",
      "nt.proto\032\017EventUser.proto\032\023InAppPurchase" +
      ".proto\032\022MonsterStuff.proto\032\013Quest.proto\032" +
      "\017Structure.proto\032\nTask.proto\032\025Tournament" +
      "Stuff.proto\032\nUser.proto*\273\017\n\024EventProtoco" +
      "lRequest\022\023\n\017C_STARTUP_EVENT\020\001\022\033\n\027C_IN_AP" +
      "P_PURCHASE_EVENT\020\002\022#\n\037C_PURCHASE_NORM_ST" +
      "RUCTURE_EVENT\020\003\022)\n%C_MOVE_OR_ROTATE_NORM" +
      "_STRUCTURE_EVENT\020\004\022\037\n\033C_SELL_NORM_STRUCT" +
      "URE_EVENT\020\005\022\"\n\036C_UPGRADE_NORM_STRUCTURE_" +
      "EVENT\020\006\0221\n-C_RETRIEVE_CURRENCY_FROM_NORM",
      "_STRUCTURE_EVENT\020\007\0225\n1C_FINISH_NORM_STRU" +
      "CT_WAITTIME_WITH_DIAMONDS_EVENT\020\010\022%\n!C_N" +
      "ORM_STRUCT_WAIT_COMPLETE_EVENT\020\t\022\034\n\030C_LO" +
      "AD_PLAYER_CITY_EVENT\020\n\022\030\n\024C_QUEST_ACCEPT" +
      "_EVENT\020\014\022\032\n\026C_QUEST_PROGRESS_EVENT\020\r\022\030\n\024" +
      "C_QUEST_REDEEM_EVENT\020\016\022#\n\037C_PURCHASE_CIT" +
      "Y_EXPANSION_EVENT\020\017\022#\n\037C_EXPANSION_WAIT_" +
      "COMPLETE_EVENT\020\020\022\024\n\020C_LEVEL_UP_EVENT\020\021\022\027" +
      "\n\023C_ENABLE_APNS_EVENT\020\022\022\027\n\023C_USER_CREATE" +
      "_EVENT\020\023\022\025\n\021C_LOAD_CITY_EVENT\020\024\022\'\n#C_RET",
      "RIEVE_USERS_FOR_USER_IDS_EVENT\020\025\022\036\n\032C_EA" +
      "RN_FREE_DIAMONDS_EVENT\020\026\022\033\n\027C_SEND_GROUP" +
      "_CHAT_EVENT\020\027\022\027\n\023C_CREATE_CLAN_EVENT\020\030\022\026" +
      "\n\022C_LEAVE_CLAN_EVENT\020\031\022\035\n\031C_REQUEST_JOIN" +
      "_CLAN_EVENT\020\032\022%\n!C_RETRACT_REQUEST_JOIN_" +
      "CLAN_EVENT\020\033\0222\n.C_APPROVE_OR_REJECT_REQU" +
      "EST_TO_JOIN_CLAN_EVENT\020\034\022\035\n\031C_TRANSFER_C" +
      "LAN_OWNERSHIP\020\035\022\036\n\032C_RETRIEVE_CLAN_INFO_" +
      "EVENT\020\036\022#\n\037C_CHANGE_CLAN_DESCRIPTION_EVE" +
      "NT\020\037\022!\n\035C_BOOT_PLAYER_FROM_CLAN_EVENT\020 \022",
      "\031\n\025C_PICK_LOCK_BOX_EVENT\020!\022(\n$C_RETRIEVE" +
      "_TOURNAMENT_RANKINGS_EVENT\020\"\022&\n\"C_SUBMIT" +
      "_MONSTER_ENHANCEMENT_EVENT\020#\022!\n\035C_RETRIE" +
      "VE_BOOSTER_PACK_EVENT\020$\022!\n\035C_PURCHASE_BO" +
      "OSTER_PACK_EVENT\020%\022\036\n\032C_RESET_BOOSTER_PA" +
      "CK_EVENT\020&\022!\n\035C_CHANGE_CLAN_JOIN_TYPE_EV" +
      "ENT\020\'\022\035\n\031C_PRIVATE_CHAT_POST_EVENT\020(\022&\n\"" +
      "C_RETRIEVE_PRIVATE_CHAT_POST_EVENT\020)\022&\n\"" +
      "C_REDEEM_USER_LOCK_BOX_ITEMS_EVENT\020*\022\031\n\025" +
      "C_BEGIN_DUNGEON_EVENT\020+\022\027\n\023C_END_DUNGEON",
      "_EVENT\020,\022\035\n\031C_REVIVE_IN_DUNGEON_EVENT\020-\022" +
      "\024\n\020C_QUEUE_UP_EVENT\020.\022!\n\035C_UPDATE_MONSTE" +
      "R_HEALTH_EVENT\020/\022\030\n\024C_HEAL_MONSTER_EVENT" +
      "\0200\022+\n\'C_HEAL_MONSTER_WAIT_TIME_COMPLETE_" +
      "EVENT\0201\022&\n\"C_ADD_MONSTER_TO_BATTLE_TEAM_" +
      "EVENT\0202\022+\n\'C_REMOVE_MONSTER_FROM_BATTLE_" +
      "TEAM_EVENT\0203\022+\n\'C_INCREASE_MONSTER_INVEN" +
      "TORY_SLOT_EVENT\0204\022*\n&C_ENHANCEMENT_WAIT_" +
      "TIME_COMPLETE_EVENT\0205\022\'\n#C_COMBINE_USER_" +
      "MONSTER_PIECES_EVENT\0206\022\035\n\031C_SELL_USER_MO",
      "NSTER_EVENT\0207\022\'\n#C_INVITE_FB_FRIENDS_FOR" +
      "_SLOTS_EVENT\0208\0221\n-C_ACCEPT_AND_REJECT_FB" +
      "_INVITE_FOR_SLOTS_EVENT\0209\022\022\n\016C_LOGOUT_EV" +
      "ENT\020e*\226\021\n\025EventProtocolResponse\022\023\n\017S_STA" +
      "RTUP_EVENT\020\001\022\033\n\027S_IN_APP_PURCHASE_EVENT\020" +
      "\002\022#\n\037S_PURCHASE_NORM_STRUCTURE_EVENT\020\003\022)" +
      "\n%S_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT\020" +
      "\004\022\037\n\033S_SELL_NORM_STRUCTURE_EVENT\020\005\022\"\n\036S_" +
      "UPGRADE_NORM_STRUCTURE_EVENT\020\006\0221\n-S_RETR" +
      "IEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT\020",
      "\007\0225\n1S_FINISH_NORM_STRUCT_WAITTIME_WITH_" +
      "DIAMONDS_EVENT\020\010\022%\n!S_NORM_STRUCT_WAIT_C" +
      "OMPLETE_EVENT\020\t\022\034\n\030S_LOAD_PLAYER_CITY_EV" +
      "ENT\020\n\022\030\n\024S_QUEST_ACCEPT_EVENT\020\014\022\032\n\026S_QUE" +
      "ST_PROGRESS_EVENT\020\r\022\030\n\024S_QUEST_REDEEM_EV" +
      "ENT\020\016\022#\n\037S_PURCHASE_CITY_EXPANSION_EVENT" +
      "\020\017\022#\n\037S_EXPANSION_WAIT_COMPLETE_EVENT\020\020\022" +
      "\024\n\020S_LEVEL_UP_EVENT\020\021\022\027\n\023S_ENABLE_APNS_E" +
      "VENT\020\022\022\027\n\023S_USER_CREATE_EVENT\020\023\022\025\n\021S_LOA" +
      "D_CITY_EVENT\020\024\022\'\n#S_RETRIEVE_USERS_FOR_U",
      "SER_IDS_EVENT\020\025\022\036\n\032S_EARN_FREE_DIAMONDS_" +
      "EVENT\020\026\022\033\n\027S_SEND_GROUP_CHAT_EVENT\020\027\022\027\n\023" +
      "S_CREATE_CLAN_EVENT\020\030\022\026\n\022S_LEAVE_CLAN_EV" +
      "ENT\020\031\022\035\n\031S_REQUEST_JOIN_CLAN_EVENT\020\032\022%\n!" +
      "S_RETRACT_REQUEST_JOIN_CLAN_EVENT\020\033\0222\n.S" +
      "_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_" +
      "EVENT\020\034\022\035\n\031S_TRANSFER_CLAN_OWNERSHIP\020\035\022\036" +
      "\n\032S_RETRIEVE_CLAN_INFO_EVENT\020\036\022#\n\037S_CHAN" +
      "GE_CLAN_DESCRIPTION_EVENT\020\037\022!\n\035S_BOOT_PL" +
      "AYER_FROM_CLAN_EVENT\020 \022\031\n\025S_PICK_LOCK_BO",
      "X_EVENT\020!\022(\n$S_RETRIEVE_TOURNAMENT_RANKI" +
      "NGS_EVENT\020\"\022&\n\"S_SUBMIT_MONSTER_ENHANCEM" +
      "ENT_EVENT\020#\022!\n\035S_RETRIEVE_BOOSTER_PACK_E" +
      "VENT\020$\022!\n\035S_PURCHASE_BOOSTER_PACK_EVENT\020" +
      "%\022\036\n\032S_RESET_BOOSTER_PACK_EVENT\020&\022!\n\035S_C" +
      "HANGE_CLAN_JOIN_TYPE_EVENT\020\'\022\035\n\031S_PRIVAT" +
      "E_CHAT_POST_EVENT\020(\022&\n\"S_RETRIEVE_PRIVAT" +
      "E_CHAT_POST_EVENT\020)\022&\n\"S_REDEEM_USER_LOC" +
      "K_BOX_ITEMS_EVENT\020*\022\031\n\025S_BEGIN_DUNGEON_E" +
      "VENT\020+\022\027\n\023S_END_DUNGEON_EVENT\020,\022\035\n\031S_REV",
      "IVE_IN_DUNGEON_EVENT\020-\022\024\n\020S_QUEUE_UP_EVE" +
      "NT\020.\022!\n\035S_UPDATE_MONSTER_HEALTH_EVENT\020/\022" +
      "\030\n\024S_HEAL_MONSTER_EVENT\0200\022+\n\'S_HEAL_MONS" +
      "TER_WAIT_TIME_COMPLETE_EVENT\0201\022&\n\"S_ADD_" +
      "MONSTER_TO_BATTLE_TEAM_EVENT\0202\022+\n\'S_REMO" +
      "VE_MONSTER_FROM_BATTLE_TEAM_EVENT\0203\022+\n\'S" +
      "_INCREASE_MONSTER_INVENTORY_SLOT_EVENT\0204" +
      "\022*\n&S_ENHANCEMENT_WAIT_TIME_COMPLETE_EVE" +
      "NT\0205\022\'\n#S_COMBINE_USER_MONSTER_PIECES_EV" +
      "ENT\0206\022\035\n\031S_SELL_USER_MONSTER_EVENT\0207\022\'\n#",
      "S_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT\0208\0221\n" +
      "-S_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS" +
      "_EVENT\0209\022\036\n\032S_UPDATE_CLIENT_USER_EVENT\020e" +
      "\022\036\n\032S_REFERRAL_CODE_USED_EVENT\020f\022\035\n\031S_PU" +
      "RGE_STATIC_DATA_EVENT\020g\022\037\n\033S_RECEIVED_GR" +
      "OUP_CHAT_EVENT\020h\022\036\n\032S_SEND_ADMIN_MESSAGE" +
      "_EVENT\020i\022 \n\034S_GENERAL_NOTIFICATION_EVENT" +
      "\020j\022*\n&S_RECEIVED_RARE_BOOSTER_PURCHASE_E" +
      "VENT\020kB\020B\016ProtocolsProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.lvl6.proto.BattleProto.getDescriptor(),
          com.lvl6.proto.BoosterPackStuffProto.getDescriptor(),
          com.lvl6.proto.ChatProto.getDescriptor(),
          com.lvl6.proto.CityProto.getDescriptor(),
          com.lvl6.proto.ClanProto.getDescriptor(),
          com.lvl6.proto.EventApnsProto.getDescriptor(),
          com.lvl6.proto.EventBoosterPackProto.getDescriptor(),
          com.lvl6.proto.EventChatProto.getDescriptor(),
          com.lvl6.proto.EventCityProto.getDescriptor(),
          com.lvl6.proto.EventClanProto.getDescriptor(),
          com.lvl6.proto.EventDungeonProto.getDescriptor(),
          com.lvl6.proto.EventInAppPurchaseProto.getDescriptor(),
          com.lvl6.proto.EventMonsterProto.getDescriptor(),
          com.lvl6.proto.EventPvpProto.getDescriptor(),
          com.lvl6.proto.EventQuestProto.getDescriptor(),
          com.lvl6.proto.EventReferralProto.getDescriptor(),
          com.lvl6.proto.EventStartupProto.getDescriptor(),
          com.lvl6.proto.EventStaticDataProto.getDescriptor(),
          com.lvl6.proto.EventStructureProto.getDescriptor(),
          com.lvl6.proto.EventTournamentProto.getDescriptor(),
          com.lvl6.proto.EventUserProto.getDescriptor(),
          com.lvl6.proto.InAppPurchaseProto.getDescriptor(),
          com.lvl6.proto.MonsterStuffProto.getDescriptor(),
          com.lvl6.proto.QuestProto.getDescriptor(),
          com.lvl6.proto.StructureProto.getDescriptor(),
          com.lvl6.proto.TaskProto.getDescriptor(),
          com.lvl6.proto.TournamentStuffProto.getDescriptor(),
          com.lvl6.proto.UserProto.getDescriptor(),
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
