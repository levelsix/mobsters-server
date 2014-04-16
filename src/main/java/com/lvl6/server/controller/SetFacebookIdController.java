package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SetFacebookIdRequestEvent;
import com.lvl6.events.response.SetFacebookIdResponseEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.SetFacebookIdRequestProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto.Builder;
import com.lvl6.proto.EventUserProto.SetFacebookIdResponseProto.SetFacebookIdStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;

  @Component @DependsOn("gameServer") public class SetFacebookIdController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

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
    boolean isUserCreate = reqProto.getIsUserCreate();
    
    //basically, if fbId is empty make it null
    if (null != fbId && fbId.isEmpty()) {
    	fbId = null;
    }

    //prepping the arguments to query the db
    List<String> facebookIds = null;
    if (null != fbId) {
    	facebookIds = new ArrayList<String>();
    	facebookIds.add(fbId);
    }
    List<Integer> userIds = new ArrayList<Integer>();
    userIds.add(userId);
    
    
    Builder resBuilder = SetFacebookIdResponseProto.newBuilder();
    resBuilder.setStatus(SetFacebookIdStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    getLocker().lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
//      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
    	Map<Integer, User> userMap = RetrieveUtils.userRetrieveUtils()
    			.getUsersForFacebookIdsOrUserIds(facebookIds, userIds);
    	User user = userMap.get(userId);

      boolean legit = checkLegitRequest(resBuilder, user, fbId, userMap);

      if (legit) {
        legit = writeChangesToDb(user, fbId, isUserCreate);
      }
      
      if (legit) {
      	resBuilder.setStatus(SetFacebookIdStatus.SUCCESS);
      }
      
      SetFacebookIdResponseProto resProto = resBuilder.build();
      SetFacebookIdResponseEvent resEvent = new SetFacebookIdResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setSetFacebookIdResponseProto(resProto);
      server.writeEvent(resEvent);
      
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
      getLocker().unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName()); 
    }
  }

  private boolean checkLegitRequest(Builder resBuilder, User user, String newFbId,
  		Map<Integer, User> userMap) {
  	if (newFbId == null || newFbId.isEmpty() || user == null) { 
  		log.error("fbId not set or user is null. fbId='" + newFbId + "'\t user=" + user);
  		return false;
  	}

  	String existingFbId = user.getFacebookId();
  	boolean existingFbIdSet = existingFbId != null && !existingFbId.isEmpty();

  	if (existingFbIdSet) {
  		log.error("fbId already set for user. existingFbId='" + existingFbId + "'\t user=" +
  				user + "\t newFbId=" + newFbId);
  		resBuilder.setStatus(SetFacebookIdStatus.FAIL_USER_FB_ID_ALREADY_SET);
  		return false;
  	}
  	
  	//fbId is something and user doesn't have fbId
  	//now check if other users have the newFbId
  	
  	if (userMap.size() > 1) {
  		//queried for a userId and a facebook id
  		log.error("fbId already taken. fbId='" + newFbId + "'\t usersInDb=" + userMap);
  		resBuilder.setStatus(SetFacebookIdStatus.FAIL_FB_ID_EXISTS);

  		//client wants the user who has the facebook id
  		for (User u : userMap.values()) {
  			
  			if (!newFbId.equals(u.getFacebookId())) {
  				continue;
  			}
  			
  			MinimumUserProto existingProto = CreateInfoProtoUtils
  					.createMinimumUserProtoFromUser(u);
  			resBuilder.setExisting(existingProto);
  			break;
  		}
  		
  		return false;
  	}

  	return true;
  }
  
  private boolean writeChangesToDb(User user, String fbId, boolean isUserCreate) {
  	
  	if (!user.updateSetFacebookId(fbId, isUserCreate)) {
  		log.error("problem with setting user's facebook id to " + fbId);
  		return false;
  	}
  	
  	return true;
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

}
