package com.lvl6.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetFacebookIdRequestEvent;
import com.lvl6.events.response.SetFacebookIdResponseEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.SetFacebookIdRequestProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto.SetFacebookIdStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class SetFacebookIdController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public SetFacebookIdController() {
    numAllocatedThreads = 1;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new SetFacebookIdRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_SET_FACEBOOK_ID_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    SetFacebookIdRequestProto reqProto = ((SetFacebookIdRequestEvent)event).getSetFacebookIdRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    String fbId = reqProto.getFbId();
    if (fbId != null && fbId.length() == 0) fbId = null;

    SetFacebookIdResponseProto.Builder resBuilder = SetFacebookIdResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());

      if (fbId != null && user != null) { 
        resBuilder.setStatus(SetFacebookIdStatus.SUCCESS);
      } else {
        resBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
      }

      SetFacebookIdResponseProto resProto = resBuilder.build();
      SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(senderProto.getUserId());
      resEvent.setSetFacebookIdResponseProto(resProto);
      server.writeEvent(resEvent);

      boolean isDifferent = checkIfNewTokenDifferent(user.getFacebookId(), fbId);

      if (isDifferent) {
        if (!user.updateSetFacebookId(fbId)) {
          log.error("problem with setting user's facebook id to " + fbId);
        }
      }
    } catch (Exception e) {
    	log.error("exception in SetFacebookIdController processEvent", e);
    	//don't let the client hang
    	try {
    		resBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
    		SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(userId);
    		resEvent.setTag(event.getTag());
    		resEvent.setSetFacebookIdResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in SetFacebookIdController processEvent", e);
    	}
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
    }
  }

  private boolean checkIfNewTokenDifferent(String oldToken, String newToken) {
    boolean oldTokenIsNothing = oldToken == null || oldToken.length() == 0;
    boolean newTokenIsNothing = newToken == null || newToken.length() == 0;
    
    if (oldTokenIsNothing && newTokenIsNothing) {
      return false;
    }
    
    if (!oldTokenIsNothing && !newTokenIsNothing) {
      return !oldToken.equals(newToken);
    }
    
    return true;
  }

}
