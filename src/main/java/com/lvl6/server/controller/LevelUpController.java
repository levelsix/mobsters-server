package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LevelUpRequestEvent;
import com.lvl6.events.response.LevelUpResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.LevelUpRequestProto;
import com.lvl6.proto.EventUserProto.LevelUpResponseProto;
import com.lvl6.proto.EventUserProto.LevelUpResponseProto.Builder;
import com.lvl6.proto.EventUserProto.LevelUpResponseProto.LevelUpStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.RetrieveUtils;

  @Component
  public class LevelUpController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  
  @Autowired
  protected Locker locker;
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
  }
  
  public LevelUpController() {
    numAllocatedThreads = 2;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new LevelUpRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_LEVEL_UP_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    LevelUpRequestProto reqProto = ((LevelUpRequestEvent)event).getLevelUpRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int newLevel = reqProto.getNextLevel();

    LevelUpResponseProto.Builder resBuilder = LevelUpResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(LevelUpStatus.FAIL_OTHER);
    
    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      boolean legitLevelUp = checkLegitLevelUp(resBuilder, user);

      boolean success = false;
      if (legitLevelUp) {
        success = writeChangesToDB(user, newLevel);
      }
      
      if (success) {
    	  resBuilder.setStatus(LevelUpStatus.SUCCESS);
      }
      
      LevelUpResponseEvent resEvent = new LevelUpResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      LevelUpResponseProto resProto = resBuilder.build();
      resEvent.setLevelUpResponseProto(resProto);
      getEventWriter().handleEvent(resEvent);
      
      if (success) {
    	  UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
    	  resEventUpdate.setTag(event.getTag());
    	  getEventWriter().handleEvent(resEventUpdate);
      }
      
    } catch (Exception e) {
      log.error("exception in LevelUpController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(LevelUpStatus.FAIL_OTHER);
    	  LevelUpResponseEvent resEvent = new LevelUpResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setLevelUpResponseProto(resBuilder.build());
    	  getEventWriter().handleEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in LevelUpController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
    }
  }

  private boolean writeChangesToDB(User user, int newLevel) {
    if (!user.updateLevel(newLevel, true)) {
      log.error("problem in changing the user's level");
      return false;
    }
    return true;
  }

  private boolean checkLegitLevelUp(Builder resBuilder, User user) {
    if (null == user) {
      log.error("user is null");
      return false;
    }
    return true;
  }
  
}
