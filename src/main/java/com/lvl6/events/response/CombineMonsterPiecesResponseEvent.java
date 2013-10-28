package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventMonsterProto.CombineMonsterPiecesResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class CombineMonsterPiecesResponseEvent extends NormalResponseEvent {

  private CombineMonsterPiecesResponseProto combineMonsterPiecesResponseProto;
  
  public CombineMonsterPiecesResponseEvent(int playerId){
    super(playerId);
    eventType = EventProtocolResponse.S_COMBINE_MONSTER_PIECES_EVENT;
  }
  
  @Override
  public int write(ByteBuffer bb) {
    ByteString b = combineMonsterPiecesResponseProto.toByteString();
    b.copyTo(bb);
    return b.size();
  }

  public void setCombineMonsterPiecesResponseProto(CombineMonsterPiecesResponseProto combineMonsterPiecesResponseProto) {
    this.combineMonsterPiecesResponseProto = combineMonsterPiecesResponseProto;
  }

  public CombineMonsterPiecesResponseProto getCombineMonsterPiecesResponseProto() {   //because APNS required
    return combineMonsterPiecesResponseProto;
  }
  
}
