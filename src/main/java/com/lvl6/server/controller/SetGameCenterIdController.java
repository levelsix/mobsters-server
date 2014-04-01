package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetGameCenterIdRequestEvent;
import com.lvl6.events.response.SetGameCenterIdResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.proto.EventUserProto.SetGameCenterIdRequestProto;
import com.lvl6.proto.EventUserProto.SetGameCenterIdResponseProto;
import com.lvl6.proto.EventUserProto.SetGameCenterIdResponseProto.SetGameCenterIdStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class SetGameCenterIdController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public SetGameCenterIdController() {
    numAllocatedThreads = 1;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new SetGameCenterIdRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_SET_GAME_CENTER_ID_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    SetGameCenterIdRequestProto reqProto = ((SetGameCenterIdRequestEvent)event).getSetGameCenterIdRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    String gameCenterId = reqProto.getGameCenterId();
    if (gameCenterId != null && gameCenterId.isEmpty()) gameCenterId = null;

    SetGameCenterIdResponseProto.Builder resBuilder = SetGameCenterIdResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    if (null != gameCenterId) {
    	resBuilder.setGameCenterId(gameCenterId);
    }
    
//    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());

//      boolean isDifferent = checkIfNewTokenDifferent(user.getGameCenterId(), gameCenterId);
      boolean legit = writeChangesToDb(user, gameCenterId);

      if (legit) { 
        resBuilder.setStatus(SetGameCenterIdStatus.SUCCESS);
      } else {
        resBuilder.setStatus(SetGameCenterIdStatus.FAIL_OTHER);
      }

      SetGameCenterIdResponseProto resProto = resBuilder.build();
      SetGameCenterIdResponseEvent resEvent = new SetGameCenterIdResponseEvent(senderProto.getUserId());
      resEvent.setSetGameCenterIdResponseProto(resProto);
      server.writeEvent(resEvent);
      
      if (legit) {
    	  //game center id might have changed
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      }
      
    } catch (Exception e) {
    	log.error("exception in SetGameCenterIdController processEvent", e);
    	//don't let the client hang
    	try {
    		resBuilder.setStatus(SetGameCenterIdStatus.FAIL_OTHER);
    		SetGameCenterIdResponseEvent resEvent = new SetGameCenterIdResponseEvent(userId);
    		resEvent.setTag(event.getTag());
    		resEvent.setSetGameCenterIdResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in SetGameCenterIdController processEvent", e);
    	}
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
    }
  }

  private boolean writeChangesToDb(User user, String gameCenterId) {
  	try {
			if (!user.updateGameCenterId(gameCenterId)) {
			  log.error("problem with setting user's facebook id to " + gameCenterId);
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("problem with updating user game center id. user=" + user +
					"\t gameCenterId=" + gameCenterId);
		}
  	
  	return false;
  }
}
