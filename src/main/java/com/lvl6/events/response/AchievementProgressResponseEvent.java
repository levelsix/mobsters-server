package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventAchievementProto.AchievementProgressResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class AchievementProgressResponseEvent extends NormalResponseEvent {

  private AchievementProgressResponseProto achievementProgressResponseProto;
  
  public AchievementProgressResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_ACHIEVEMENT_PROGRESS_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = achievementProgressResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setAchievementProgressResponseProto(AchievementProgressResponseProto achievementProgressResponseProto) {
    this.achievementProgressResponseProto = achievementProgressResponseProto;
  }

}
