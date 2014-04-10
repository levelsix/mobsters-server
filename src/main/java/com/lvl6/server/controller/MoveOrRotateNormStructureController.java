package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.MoveOrRotateNormStructureRequestEvent;
import com.lvl6.events.response.MoveOrRotateNormStructureResponseEvent;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.StructureForUser;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureRequestProto.MoveOrRotateNormStructType;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.MoveOrRotateNormStructureResponseProto.MoveOrRotateNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.StructOrientation;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class MoveOrRotateNormStructureController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  public MoveOrRotateNormStructureController() {
    numAllocatedThreads = 3;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new MoveOrRotateNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_MOVE_OR_ROTATE_NORM_STRUCTURE_EVENT;
  }


  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    MoveOrRotateNormStructureRequestProto reqProto = ((MoveOrRotateNormStructureRequestEvent)event).getMoveOrRotateNormStructureRequestProto();

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    int userStructId = reqProto.getUserStructId();
    MoveOrRotateNormStructType type = reqProto.getType();

    CoordinatePair newCoords = null;
    StructOrientation orientation = null;
    if (type == MoveOrRotateNormStructType.MOVE) {
      newCoords = new CoordinatePair(reqProto.getCurStructCoordinates().getX(), reqProto.getCurStructCoordinates().getY());
    }

    MoveOrRotateNormStructureResponseProto.Builder resBuilder = MoveOrRotateNormStructureResponseProto.newBuilder();
    resBuilder.setSender(senderProto);


    //only locking so you cant moveOrRotate it hella times
    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      boolean legit = true;
      resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
      
      StructureForUser userStruct = RetrieveUtils.userStructRetrieveUtils().getSpecificUserStruct(userStructId);
      if (userStruct == null) {
        legit = false;
        resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
      }
      
      if (type == MoveOrRotateNormStructType.MOVE && newCoords == null) {
        legit = false;
        resBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
        log.error("asked to move, but the coordinates supplied in are null. reqProto's newStructCoordinates=" + reqProto.getCurStructCoordinates());
      }

      if (legit) {
        if (type == MoveOrRotateNormStructType.MOVE) {
          if (!UpdateUtils.get().updateUserStructCoord(userStructId, newCoords)) {
            resBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
            log.error("problem with updating coordinates to " + newCoords + " for user struct " + userStructId);
          } else {
            resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
          }
        } else {
          if (!UpdateUtils.get().updateUserStructOrientation(userStructId, orientation)) {
            resBuilder.setStatus(MoveOrRotateNormStructureStatus.OTHER_FAIL);
            log.error("problem with updating orientation to " + orientation + " for user struct " + userStructId);
          } else {
            resBuilder.setStatus(MoveOrRotateNormStructureStatus.SUCCESS);
          }
        }
      }
      MoveOrRotateNormStructureResponseEvent resEvent = new MoveOrRotateNormStructureResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setMoveOrRotateNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in MoveOrRotateNormStructure processEvent", e);
    } finally {
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
}
