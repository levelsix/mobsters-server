// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: LeaderBoard.proto

package com.lvl6.proto;

public final class LeaderBoardProto {
  private LeaderBoardProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface StrengthLeaderBoardProtoOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.lvl6.proto.StrengthLeaderBoardProto)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional int32 rank = 1;</code>
     */
    boolean hasRank();
    /**
     * <code>optional int32 rank = 1;</code>
     */
    int getRank();

    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    boolean hasMup();
    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    com.lvl6.proto.UserProto.MinimumUserProto getMup();
    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder getMupOrBuilder();

    /**
     * <code>optional int64 strength = 3;</code>
     */
    boolean hasStrength();
    /**
     * <code>optional int64 strength = 3;</code>
     */
    long getStrength();

    /**
     * <code>optional int32 monsterId = 4;</code>
     */
    boolean hasMonsterId();
    /**
     * <code>optional int32 monsterId = 4;</code>
     */
    int getMonsterId();
  }
  /**
   * Protobuf type {@code com.lvl6.proto.StrengthLeaderBoardProto}
   */
  public static final class StrengthLeaderBoardProto extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.lvl6.proto.StrengthLeaderBoardProto)
      StrengthLeaderBoardProtoOrBuilder {
    // Use StrengthLeaderBoardProto.newBuilder() to construct.
    private StrengthLeaderBoardProto(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private StrengthLeaderBoardProto(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final StrengthLeaderBoardProto defaultInstance;
    public static StrengthLeaderBoardProto getDefaultInstance() {
      return defaultInstance;
    }

    public StrengthLeaderBoardProto getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private StrengthLeaderBoardProto(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              rank_ = input.readInt32();
              break;
            }
            case 18: {
              com.lvl6.proto.UserProto.MinimumUserProto.Builder subBuilder = null;
              if (((bitField0_ & 0x00000002) == 0x00000002)) {
                subBuilder = mup_.toBuilder();
              }
              mup_ = input.readMessage(com.lvl6.proto.UserProto.MinimumUserProto.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(mup_);
                mup_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000002;
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              strength_ = input.readInt64();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              monsterId_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.lvl6.proto.LeaderBoardProto.internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.lvl6.proto.LeaderBoardProto.internal_static_com_lvl6_proto_StrengthLeaderBoardProto_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.class, com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.Builder.class);
    }

    public static com.google.protobuf.Parser<StrengthLeaderBoardProto> PARSER =
        new com.google.protobuf.AbstractParser<StrengthLeaderBoardProto>() {
      public StrengthLeaderBoardProto parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new StrengthLeaderBoardProto(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<StrengthLeaderBoardProto> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int RANK_FIELD_NUMBER = 1;
    private int rank_;
    /**
     * <code>optional int32 rank = 1;</code>
     */
    public boolean hasRank() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int32 rank = 1;</code>
     */
    public int getRank() {
      return rank_;
    }

    public static final int MUP_FIELD_NUMBER = 2;
    private com.lvl6.proto.UserProto.MinimumUserProto mup_;
    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    public boolean hasMup() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    public com.lvl6.proto.UserProto.MinimumUserProto getMup() {
      return mup_;
    }
    /**
     * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
     */
    public com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder getMupOrBuilder() {
      return mup_;
    }

    public static final int STRENGTH_FIELD_NUMBER = 3;
    private long strength_;
    /**
     * <code>optional int64 strength = 3;</code>
     */
    public boolean hasStrength() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional int64 strength = 3;</code>
     */
    public long getStrength() {
      return strength_;
    }

    public static final int MONSTERID_FIELD_NUMBER = 4;
    private int monsterId_;
    /**
     * <code>optional int32 monsterId = 4;</code>
     */
    public boolean hasMonsterId() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional int32 monsterId = 4;</code>
     */
    public int getMonsterId() {
      return monsterId_;
    }

    private void initFields() {
      rank_ = 0;
      mup_ = com.lvl6.proto.UserProto.MinimumUserProto.getDefaultInstance();
      strength_ = 0L;
      monsterId_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, rank_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, mup_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt64(3, strength_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, monsterId_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, rank_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, mup_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, strength_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, monsterId_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.lvl6.proto.StrengthLeaderBoardProto}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.lvl6.proto.StrengthLeaderBoardProto)
        com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProtoOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.lvl6.proto.LeaderBoardProto.internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.lvl6.proto.LeaderBoardProto.internal_static_com_lvl6_proto_StrengthLeaderBoardProto_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.class, com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.Builder.class);
      }

      // Construct using com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getMupFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        rank_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (mupBuilder_ == null) {
          mup_ = com.lvl6.proto.UserProto.MinimumUserProto.getDefaultInstance();
        } else {
          mupBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        strength_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000004);
        monsterId_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.lvl6.proto.LeaderBoardProto.internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor;
      }

      public com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto getDefaultInstanceForType() {
        return com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.getDefaultInstance();
      }

      public com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto build() {
        com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto buildPartial() {
        com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto result = new com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.rank_ = rank_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (mupBuilder_ == null) {
          result.mup_ = mup_;
        } else {
          result.mup_ = mupBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.strength_ = strength_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.monsterId_ = monsterId_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto) {
          return mergeFrom((com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto other) {
        if (other == com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto.getDefaultInstance()) return this;
        if (other.hasRank()) {
          setRank(other.getRank());
        }
        if (other.hasMup()) {
          mergeMup(other.getMup());
        }
        if (other.hasStrength()) {
          setStrength(other.getStrength());
        }
        if (other.hasMonsterId()) {
          setMonsterId(other.getMonsterId());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int rank_ ;
      /**
       * <code>optional int32 rank = 1;</code>
       */
      public boolean hasRank() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int32 rank = 1;</code>
       */
      public int getRank() {
        return rank_;
      }
      /**
       * <code>optional int32 rank = 1;</code>
       */
      public Builder setRank(int value) {
        bitField0_ |= 0x00000001;
        rank_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 rank = 1;</code>
       */
      public Builder clearRank() {
        bitField0_ = (bitField0_ & ~0x00000001);
        rank_ = 0;
        onChanged();
        return this;
      }

      private com.lvl6.proto.UserProto.MinimumUserProto mup_ = com.lvl6.proto.UserProto.MinimumUserProto.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.proto.UserProto.MinimumUserProto, com.lvl6.proto.UserProto.MinimumUserProto.Builder, com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder> mupBuilder_;
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public boolean hasMup() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public com.lvl6.proto.UserProto.MinimumUserProto getMup() {
        if (mupBuilder_ == null) {
          return mup_;
        } else {
          return mupBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public Builder setMup(com.lvl6.proto.UserProto.MinimumUserProto value) {
        if (mupBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          mup_ = value;
          onChanged();
        } else {
          mupBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public Builder setMup(
          com.lvl6.proto.UserProto.MinimumUserProto.Builder builderForValue) {
        if (mupBuilder_ == null) {
          mup_ = builderForValue.build();
          onChanged();
        } else {
          mupBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public Builder mergeMup(com.lvl6.proto.UserProto.MinimumUserProto value) {
        if (mupBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) &&
              mup_ != com.lvl6.proto.UserProto.MinimumUserProto.getDefaultInstance()) {
            mup_ =
              com.lvl6.proto.UserProto.MinimumUserProto.newBuilder(mup_).mergeFrom(value).buildPartial();
          } else {
            mup_ = value;
          }
          onChanged();
        } else {
          mupBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public Builder clearMup() {
        if (mupBuilder_ == null) {
          mup_ = com.lvl6.proto.UserProto.MinimumUserProto.getDefaultInstance();
          onChanged();
        } else {
          mupBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public com.lvl6.proto.UserProto.MinimumUserProto.Builder getMupBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getMupFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      public com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder getMupOrBuilder() {
        if (mupBuilder_ != null) {
          return mupBuilder_.getMessageOrBuilder();
        } else {
          return mup_;
        }
      }
      /**
       * <code>optional .com.lvl6.proto.MinimumUserProto mup = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<
          com.lvl6.proto.UserProto.MinimumUserProto, com.lvl6.proto.UserProto.MinimumUserProto.Builder, com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder> 
          getMupFieldBuilder() {
        if (mupBuilder_ == null) {
          mupBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              com.lvl6.proto.UserProto.MinimumUserProto, com.lvl6.proto.UserProto.MinimumUserProto.Builder, com.lvl6.proto.UserProto.MinimumUserProtoOrBuilder>(
                  getMup(),
                  getParentForChildren(),
                  isClean());
          mup_ = null;
        }
        return mupBuilder_;
      }

      private long strength_ ;
      /**
       * <code>optional int64 strength = 3;</code>
       */
      public boolean hasStrength() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional int64 strength = 3;</code>
       */
      public long getStrength() {
        return strength_;
      }
      /**
       * <code>optional int64 strength = 3;</code>
       */
      public Builder setStrength(long value) {
        bitField0_ |= 0x00000004;
        strength_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 strength = 3;</code>
       */
      public Builder clearStrength() {
        bitField0_ = (bitField0_ & ~0x00000004);
        strength_ = 0L;
        onChanged();
        return this;
      }

      private int monsterId_ ;
      /**
       * <code>optional int32 monsterId = 4;</code>
       */
      public boolean hasMonsterId() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional int32 monsterId = 4;</code>
       */
      public int getMonsterId() {
        return monsterId_;
      }
      /**
       * <code>optional int32 monsterId = 4;</code>
       */
      public Builder setMonsterId(int value) {
        bitField0_ |= 0x00000008;
        monsterId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 monsterId = 4;</code>
       */
      public Builder clearMonsterId() {
        bitField0_ = (bitField0_ & ~0x00000008);
        monsterId_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.lvl6.proto.StrengthLeaderBoardProto)
    }

    static {
      defaultInstance = new StrengthLeaderBoardProto(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.lvl6.proto.StrengthLeaderBoardProto)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_lvl6_proto_StrengthLeaderBoardProto_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021LeaderBoard.proto\022\016com.lvl6.proto\032\nUse" +
      "r.proto\"|\n\030StrengthLeaderBoardProto\022\014\n\004r" +
      "ank\030\001 \001(\005\022-\n\003mup\030\002 \001(\0132 .com.lvl6.proto." +
      "MinimumUserProto\022\020\n\010strength\030\003 \001(\003\022\021\n\tmo" +
      "nsterId\030\004 \001(\005B\022B\020LeaderBoardProto"
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
          com.lvl6.proto.UserProto.getDescriptor(),
        }, assigner);
    internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_lvl6_proto_StrengthLeaderBoardProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_lvl6_proto_StrengthLeaderBoardProto_descriptor,
        new java.lang.String[] { "Rank", "Mup", "Strength", "MonsterId", });
    com.lvl6.proto.UserProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
