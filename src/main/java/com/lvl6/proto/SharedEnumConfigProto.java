// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SharedEnumConfig.proto

package com.lvl6.proto;

public final class SharedEnumConfigProto {
  private SharedEnumConfigProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf enum {@code com.lvl6.proto.DayOfWeek}
   */
  public enum DayOfWeek
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>SUNDAY = 1;</code>
     */
    SUNDAY(0, 1),
    /**
     * <code>MONDAY = 2;</code>
     */
    MONDAY(1, 2),
    /**
     * <code>TUESDAY = 3;</code>
     */
    TUESDAY(2, 3),
    /**
     * <code>WEDNESDAY = 4;</code>
     */
    WEDNESDAY(3, 4),
    /**
     * <code>THURSDAY = 5;</code>
     */
    THURSDAY(4, 5),
    /**
     * <code>FRIDAY = 6;</code>
     */
    FRIDAY(5, 6),
    /**
     * <code>SATURDAY = 7;</code>
     */
    SATURDAY(6, 7),
    /**
     * <code>NO_DAY_OF_WEEK = 8;</code>
     */
    NO_DAY_OF_WEEK(7, 8),
    ;

    /**
     * <code>SUNDAY = 1;</code>
     */
    public static final int SUNDAY_VALUE = 1;
    /**
     * <code>MONDAY = 2;</code>
     */
    public static final int MONDAY_VALUE = 2;
    /**
     * <code>TUESDAY = 3;</code>
     */
    public static final int TUESDAY_VALUE = 3;
    /**
     * <code>WEDNESDAY = 4;</code>
     */
    public static final int WEDNESDAY_VALUE = 4;
    /**
     * <code>THURSDAY = 5;</code>
     */
    public static final int THURSDAY_VALUE = 5;
    /**
     * <code>FRIDAY = 6;</code>
     */
    public static final int FRIDAY_VALUE = 6;
    /**
     * <code>SATURDAY = 7;</code>
     */
    public static final int SATURDAY_VALUE = 7;
    /**
     * <code>NO_DAY_OF_WEEK = 8;</code>
     */
    public static final int NO_DAY_OF_WEEK_VALUE = 8;


    public final int getNumber() { return value; }

    public static DayOfWeek valueOf(int value) {
      switch (value) {
        case 1: return SUNDAY;
        case 2: return MONDAY;
        case 3: return TUESDAY;
        case 4: return WEDNESDAY;
        case 5: return THURSDAY;
        case 6: return FRIDAY;
        case 7: return SATURDAY;
        case 8: return NO_DAY_OF_WEEK;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<DayOfWeek>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<DayOfWeek>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<DayOfWeek>() {
            public DayOfWeek findValueByNumber(int number) {
              return DayOfWeek.valueOf(number);
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
      return com.lvl6.proto.SharedEnumConfigProto.getDescriptor().getEnumTypes().get(0);
    }

    private static final DayOfWeek[] VALUES = values();

    public static DayOfWeek valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private DayOfWeek(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.DayOfWeek)
  }

  /**
   * Protobuf enum {@code com.lvl6.proto.Element}
   */
  public enum Element
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>FIRE = 1;</code>
     *
     * <pre>
     *beats earth
     * </pre>
     */
    FIRE(0, 1),
    /**
     * <code>EARTH = 2;</code>
     *
     * <pre>
     *beats water
     * </pre>
     */
    EARTH(1, 2),
    /**
     * <code>WATER = 3;</code>
     *
     * <pre>
     *beats fire
     * </pre>
     */
    WATER(2, 3),
    /**
     * <code>LIGHT = 4;</code>
     *
     * <pre>
     *beats dark
     * </pre>
     */
    LIGHT(3, 4),
    /**
     * <code>DARK = 5;</code>
     *
     * <pre>
     *beats light
     * </pre>
     */
    DARK(4, 5),
    /**
     * <code>ROCK = 6;</code>
     *
     * <pre>
     *damages everything
     * </pre>
     */
    ROCK(5, 6),
    /**
     * <code>NO_ELEMENT = 7;</code>
     */
    NO_ELEMENT(6, 7),
    ;

    /**
     * <code>FIRE = 1;</code>
     *
     * <pre>
     *beats earth
     * </pre>
     */
    public static final int FIRE_VALUE = 1;
    /**
     * <code>EARTH = 2;</code>
     *
     * <pre>
     *beats water
     * </pre>
     */
    public static final int EARTH_VALUE = 2;
    /**
     * <code>WATER = 3;</code>
     *
     * <pre>
     *beats fire
     * </pre>
     */
    public static final int WATER_VALUE = 3;
    /**
     * <code>LIGHT = 4;</code>
     *
     * <pre>
     *beats dark
     * </pre>
     */
    public static final int LIGHT_VALUE = 4;
    /**
     * <code>DARK = 5;</code>
     *
     * <pre>
     *beats light
     * </pre>
     */
    public static final int DARK_VALUE = 5;
    /**
     * <code>ROCK = 6;</code>
     *
     * <pre>
     *damages everything
     * </pre>
     */
    public static final int ROCK_VALUE = 6;
    /**
     * <code>NO_ELEMENT = 7;</code>
     */
    public static final int NO_ELEMENT_VALUE = 7;


    public final int getNumber() { return value; }

    public static Element valueOf(int value) {
      switch (value) {
        case 1: return FIRE;
        case 2: return EARTH;
        case 3: return WATER;
        case 4: return LIGHT;
        case 5: return DARK;
        case 6: return ROCK;
        case 7: return NO_ELEMENT;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Element>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<Element>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Element>() {
            public Element findValueByNumber(int number) {
              return Element.valueOf(number);
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
      return com.lvl6.proto.SharedEnumConfigProto.getDescriptor().getEnumTypes().get(1);
    }

    private static final Element[] VALUES = values();

    public static Element valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private Element(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.Element)
  }

  /**
   * Protobuf enum {@code com.lvl6.proto.Quality}
   */
  public enum Quality
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>NO_QUALITY = 1;</code>
     */
    NO_QUALITY(0, 1),
    /**
     * <code>COMMON = 2;</code>
     */
    COMMON(1, 2),
    /**
     * <code>RARE = 3;</code>
     */
    RARE(2, 3),
    /**
     * <code>SUPER = 4;</code>
     */
    SUPER(3, 4),
    /**
     * <code>ULTRA = 5;</code>
     */
    ULTRA(4, 5),
    /**
     * <code>EPIC = 6;</code>
     */
    EPIC(5, 6),
    /**
     * <code>LEGENDARY = 7;</code>
     */
    LEGENDARY(6, 7),
    /**
     * <code>EVO = 8;</code>
     *
     * <pre>
     *MONSTERS USED FOR EVOLVING
     * </pre>
     */
    EVO(7, 8),
    ;

    /**
     * <code>NO_QUALITY = 1;</code>
     */
    public static final int NO_QUALITY_VALUE = 1;
    /**
     * <code>COMMON = 2;</code>
     */
    public static final int COMMON_VALUE = 2;
    /**
     * <code>RARE = 3;</code>
     */
    public static final int RARE_VALUE = 3;
    /**
     * <code>SUPER = 4;</code>
     */
    public static final int SUPER_VALUE = 4;
    /**
     * <code>ULTRA = 5;</code>
     */
    public static final int ULTRA_VALUE = 5;
    /**
     * <code>EPIC = 6;</code>
     */
    public static final int EPIC_VALUE = 6;
    /**
     * <code>LEGENDARY = 7;</code>
     */
    public static final int LEGENDARY_VALUE = 7;
    /**
     * <code>EVO = 8;</code>
     *
     * <pre>
     *MONSTERS USED FOR EVOLVING
     * </pre>
     */
    public static final int EVO_VALUE = 8;


    public final int getNumber() { return value; }

    public static Quality valueOf(int value) {
      switch (value) {
        case 1: return NO_QUALITY;
        case 2: return COMMON;
        case 3: return RARE;
        case 4: return SUPER;
        case 5: return ULTRA;
        case 6: return EPIC;
        case 7: return LEGENDARY;
        case 8: return EVO;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Quality>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<Quality>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Quality>() {
            public Quality findValueByNumber(int number) {
              return Quality.valueOf(number);
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
      return com.lvl6.proto.SharedEnumConfigProto.getDescriptor().getEnumTypes().get(2);
    }

    private static final Quality[] VALUES = values();

    public static Quality valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private Quality(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.Quality)
  }

  /**
   * Protobuf enum {@code com.lvl6.proto.GameActionType}
   *
   * <pre>
   *TODO: Consider redesigning this enum type. At the moment, enum
   *is used in ClanHelp (deals only with speeding up actions) and
   *items ("superset" of ClanHelp, so not just speeding up actions).
   * </pre>
   */
  public enum GameActionType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>NO_HELP = 1;</code>
     */
    NO_HELP(0, 1),
    /**
     * <code>UPGRADE_STRUCT = 2;</code>
     */
    UPGRADE_STRUCT(1, 2),
    /**
     * <code>HEAL = 3;</code>
     */
    HEAL(2, 3),
    /**
     * <code>EVOLVE = 4;</code>
     */
    EVOLVE(3, 4),
    /**
     * <code>MINI_JOB = 5;</code>
     */
    MINI_JOB(4, 5),
    /**
     * <code>ENHANCE_TIME = 6;</code>
     */
    ENHANCE_TIME(5, 6),
    /**
     * <code>REMOVE_OBSTACLE = 7;</code>
     */
    REMOVE_OBSTACLE(6, 7),
    /**
     * <code>COMBINE_MONSTER = 8;</code>
     */
    COMBINE_MONSTER(7, 8),
    /**
     * <code>ENTER_PERSISTENT_EVENT = 9;</code>
     */
    ENTER_PERSISTENT_EVENT(8, 9),
    /**
     * <code>PERFORMING_RESEARCH = 10;</code>
     *
     * <pre>
     *all enums not in "message" share namespace 
     * </pre>
     */
    PERFORMING_RESEARCH(9, 10),
    /**
     * <code>CREATE_BATTLE_ITEM = 11;</code>
     */
    CREATE_BATTLE_ITEM(10, 11),
    ;

    /**
     * <code>NO_HELP = 1;</code>
     */
    public static final int NO_HELP_VALUE = 1;
    /**
     * <code>UPGRADE_STRUCT = 2;</code>
     */
    public static final int UPGRADE_STRUCT_VALUE = 2;
    /**
     * <code>HEAL = 3;</code>
     */
    public static final int HEAL_VALUE = 3;
    /**
     * <code>EVOLVE = 4;</code>
     */
    public static final int EVOLVE_VALUE = 4;
    /**
     * <code>MINI_JOB = 5;</code>
     */
    public static final int MINI_JOB_VALUE = 5;
    /**
     * <code>ENHANCE_TIME = 6;</code>
     */
    public static final int ENHANCE_TIME_VALUE = 6;
    /**
     * <code>REMOVE_OBSTACLE = 7;</code>
     */
    public static final int REMOVE_OBSTACLE_VALUE = 7;
    /**
     * <code>COMBINE_MONSTER = 8;</code>
     */
    public static final int COMBINE_MONSTER_VALUE = 8;
    /**
     * <code>ENTER_PERSISTENT_EVENT = 9;</code>
     */
    public static final int ENTER_PERSISTENT_EVENT_VALUE = 9;
    /**
     * <code>PERFORMING_RESEARCH = 10;</code>
     *
     * <pre>
     *all enums not in "message" share namespace 
     * </pre>
     */
    public static final int PERFORMING_RESEARCH_VALUE = 10;
    /**
     * <code>CREATE_BATTLE_ITEM = 11;</code>
     */
    public static final int CREATE_BATTLE_ITEM_VALUE = 11;


    public final int getNumber() { return value; }

    public static GameActionType valueOf(int value) {
      switch (value) {
        case 1: return NO_HELP;
        case 2: return UPGRADE_STRUCT;
        case 3: return HEAL;
        case 4: return EVOLVE;
        case 5: return MINI_JOB;
        case 6: return ENHANCE_TIME;
        case 7: return REMOVE_OBSTACLE;
        case 8: return COMBINE_MONSTER;
        case 9: return ENTER_PERSISTENT_EVENT;
        case 10: return PERFORMING_RESEARCH;
        case 11: return CREATE_BATTLE_ITEM;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<GameActionType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<GameActionType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<GameActionType>() {
            public GameActionType findValueByNumber(int number) {
              return GameActionType.valueOf(number);
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
      return com.lvl6.proto.SharedEnumConfigProto.getDescriptor().getEnumTypes().get(3);
    }

    private static final GameActionType[] VALUES = values();

    public static GameActionType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private GameActionType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.GameActionType)
  }

  /**
   * Protobuf enum {@code com.lvl6.proto.GameType}
   */
  public enum GameType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>NO_TYPE = 1;</code>
     */
    NO_TYPE(0, 1),
    /**
     * <code>STRUCTURE = 2;</code>
     */
    STRUCTURE(1, 2),
    /**
     * <code>RESEARCH = 3;</code>
     */
    RESEARCH(2, 3),
    /**
     * <code>SKILL = 4;</code>
     */
    SKILL(3, 4),
    /**
     * <code>TASK = 5;</code>
     */
    TASK(4, 5),
    ;

    /**
     * <code>NO_TYPE = 1;</code>
     */
    public static final int NO_TYPE_VALUE = 1;
    /**
     * <code>STRUCTURE = 2;</code>
     */
    public static final int STRUCTURE_VALUE = 2;
    /**
     * <code>RESEARCH = 3;</code>
     */
    public static final int RESEARCH_VALUE = 3;
    /**
     * <code>SKILL = 4;</code>
     */
    public static final int SKILL_VALUE = 4;
    /**
     * <code>TASK = 5;</code>
     */
    public static final int TASK_VALUE = 5;


    public final int getNumber() { return value; }

    public static GameType valueOf(int value) {
      switch (value) {
        case 1: return NO_TYPE;
        case 2: return STRUCTURE;
        case 3: return RESEARCH;
        case 4: return SKILL;
        case 5: return TASK;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<GameType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<GameType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<GameType>() {
            public GameType findValueByNumber(int number) {
              return GameType.valueOf(number);
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
      return com.lvl6.proto.SharedEnumConfigProto.getDescriptor().getEnumTypes().get(4);
    }

    private static final GameType[] VALUES = values();

    public static GameType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private GameType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.lvl6.proto.GameType)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026SharedEnumConfig.proto\022\016com.lvl6.proto" +
      "*{\n\tDayOfWeek\022\n\n\006SUNDAY\020\001\022\n\n\006MONDAY\020\002\022\013\n" +
      "\007TUESDAY\020\003\022\r\n\tWEDNESDAY\020\004\022\014\n\010THURSDAY\020\005\022" +
      "\n\n\006FRIDAY\020\006\022\014\n\010SATURDAY\020\007\022\022\n\016NO_DAY_OF_W" +
      "EEK\020\010*X\n\007Element\022\010\n\004FIRE\020\001\022\t\n\005EARTH\020\002\022\t\n" +
      "\005WATER\020\003\022\t\n\005LIGHT\020\004\022\010\n\004DARK\020\005\022\010\n\004ROCK\020\006\022" +
      "\016\n\nNO_ELEMENT\020\007*g\n\007Quality\022\016\n\nNO_QUALITY" +
      "\020\001\022\n\n\006COMMON\020\002\022\010\n\004RARE\020\003\022\t\n\005SUPER\020\004\022\t\n\005U" +
      "LTRA\020\005\022\010\n\004EPIC\020\006\022\r\n\tLEGENDARY\020\007\022\007\n\003EVO\020\010" +
      "*\336\001\n\016GameActionType\022\013\n\007NO_HELP\020\001\022\022\n\016UPGR",
      "ADE_STRUCT\020\002\022\010\n\004HEAL\020\003\022\n\n\006EVOLVE\020\004\022\014\n\010MI" +
      "NI_JOB\020\005\022\020\n\014ENHANCE_TIME\020\006\022\023\n\017REMOVE_OBS" +
      "TACLE\020\007\022\023\n\017COMBINE_MONSTER\020\010\022\032\n\026ENTER_PE" +
      "RSISTENT_EVENT\020\t\022\027\n\023PERFORMING_RESEARCH\020" +
      "\n\022\026\n\022CREATE_BATTLE_ITEM\020\013*I\n\010GameType\022\013\n" +
      "\007NO_TYPE\020\001\022\r\n\tSTRUCTURE\020\002\022\014\n\010RESEARCH\020\003\022" +
      "\t\n\005SKILL\020\004\022\010\n\004TASK\020\005B\027B\025SharedEnumConfig" +
      "Proto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
