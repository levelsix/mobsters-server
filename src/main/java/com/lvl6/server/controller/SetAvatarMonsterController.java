package com.lvl6.server.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetAvatarMonsterRequestEvent;
import com.lvl6.events.response.SetAvatarMonsterResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterRequestProto;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto;
import com.lvl6.proto.EventUserProto.SetAvatarMonsterResponseProto.SetAvatarMonsterStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserRetrieveUtils2;

  @Component @DependsOn("gameServer") public class SetAvatarMonsterController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  public SetAvatarMonsterController() {
    numAllocatedThreads = 1;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new SetAvatarMonsterRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_SET_AVATAR_MONSTER_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    SetAvatarMonsterRequestProto reqProto = ((SetAvatarMonsterRequestEvent)event).getSetAvatarMonsterRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    int monsterId = reqProto.getMonsterId();

    SetAvatarMonsterResponseProto.Builder resBuilder = SetAvatarMonsterResponseProto.newBuilder();
    resBuilder.setSender(senderProto);

    UUID userUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);

      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s",
          userId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(SetAvatarMonsterStatus.FAIL_OTHER);
      SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setSetAvatarMonsterResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }
    
//    server.lockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName());
    try {
      User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());

//      boolean isDifferent = checkIfNewTokenDifferent(user.getAvatarMonster(), gameCenterId);
      
      boolean legit = monsterId > 0;
      
      boolean successful = false;
      if (legit) {
    	  successful = writeChangesToDb(user, monsterId);
      } else {
    	  log.error("can't unset avatarMonsterId");
      }

      if (successful) { 
        resBuilder.setStatus(SetAvatarMonsterStatus.SUCCESS);
      } else {
        resBuilder.setStatus(SetAvatarMonsterStatus.FAIL_OTHER);
      }

      SetAvatarMonsterResponseProto resProto = resBuilder.build();
      SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(senderProto.getUserUuid());
      resEvent.setSetAvatarMonsterResponseProto(resProto);
      server.writeEvent(resEvent);
      
      if (successful) {
    	  //game center id might have changed
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      }
      
    } catch (Exception e) {
    	log.error("exception in SetAvatarMonsterController processEvent", e);
    	//don't let the client hang
    	try {
    		resBuilder.setStatus(SetAvatarMonsterStatus.FAIL_OTHER);
    		SetAvatarMonsterResponseEvent resEvent = new SetAvatarMonsterResponseEvent(userId);
    		resEvent.setTag(event.getTag());
    		resEvent.setSetAvatarMonsterResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in SetAvatarMonsterController processEvent", e);
    	}
    } finally {
//      server.unlockPlayer(senderProto.getUserUuid(), this.getClass().getSimpleName()); 
    }
  }

  private boolean writeChangesToDb(User user, int avatarMonsterId) {
  	try {
			if (!user.updateAvatarMonsterId(avatarMonsterId)) {
			  log.error("problem with setting user's avatarMonsterId to " + avatarMonsterId);
			  return false;
			}
			return true;
		} catch (Exception e) {
			log.error("problem with updating user avatar monster id. user=" + user +
					"\t avatarMonsterId=" + avatarMonsterId);
		}
  	
  	return false;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }
  
  
}
