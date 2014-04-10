package com.lvl6.server.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.ApproveOrRejectRequestToJoinClanRequestEvent;
import com.lvl6.events.response.ApproveOrRejectRequestToJoinClanResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.ApproveOrRejectRequestToJoinClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class ApproveOrRejectRequestToJoinClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

  public ApproveOrRejectRequestToJoinClanController() {
    numAllocatedThreads = 4;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new ApproveOrRejectRequestToJoinClanRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_APPROVE_OR_REJECT_REQUEST_TO_JOIN_CLAN_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    ApproveOrRejectRequestToJoinClanRequestProto reqProto = ((ApproveOrRejectRequestToJoinClanRequestEvent)event).getApproveOrRejectRequestToJoinClanRequestProto();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int requesterId = reqProto.getRequesterId();
    boolean accept = reqProto.getAccept();

    ApproveOrRejectRequestToJoinClanResponseProto.Builder resBuilder = ApproveOrRejectRequestToJoinClanResponseProto.newBuilder();
    resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);
    resBuilder.setAccept(accept);

    int clanId = 0;
    if (senderProto.hasClan() && null != senderProto.getClan()) {
    	clanId = senderProto.getClan().getClanId();
    }
    
    boolean lockedClan = false;
    if (0 != clanId) {
    	lockedClan = getLocker().lockClan(clanId);
    }
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      User requester = RetrieveUtils.userRetrieveUtils().getUserById(requesterId);

      List<Integer> clanSizeList = new ArrayList<Integer>();
      boolean legitDecision = checkLegitDecision(resBuilder, lockedClan, user, requester,
      		accept, clanSizeList);
      
      boolean success = false;
      if (legitDecision) {
        success = writeChangesToDB(user, requester, accept);
      }
      
      if (success) {
      	resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.SUCCESS);
      	setResponseBuilderStuff(resBuilder, clanId, clanSizeList);
      	MinimumUserProto requestMup = CreateInfoProtoUtils
      			.createMinimumUserProtoFromUser(requester);
      	resBuilder.setRequester(requestMup);
      }
      
      ApproveOrRejectRequestToJoinClanResponseEvent resEvent =
      		new ApproveOrRejectRequestToJoinClanResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder.build());  
      
      //if fail only to sender
      if (!success) {
      	server.writeEvent(resEvent);
      } else {
      	//if success to clan and the requester
      	server.writeClanEvent(resEvent, clanId);
        // Send message to the new guy
        ApproveOrRejectRequestToJoinClanResponseEvent resEvent2 =
        		new ApproveOrRejectRequestToJoinClanResponseEvent(requesterId);
        resEvent2.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder.build());
        //in case user is not online write an apns
        server.writeAPNSNotificationOrEvent(resEvent2);
        //server.writeEvent(resEvent2);
      }
    } catch (Exception e) {
      log.error("exception in ApproveOrRejectRequestToJoinClan processEvent", e);
    	try {
    	  resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
    	  ApproveOrRejectRequestToJoinClanResponseEvent resEvent = new ApproveOrRejectRequestToJoinClanResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setApproveOrRejectRequestToJoinClanResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in ApproveOrRejectRequestToJoinClan processEvent", e);
    	}
    } finally {
      if (0 != clanId && lockedClan) {
      	getLocker().unlockClan(clanId);
      }
    }
  }

  private boolean checkLegitDecision(Builder resBuilder, boolean lockedClan, User user,
  		User requester, boolean accept, List<Integer> clanSizeList) {
  	
  	if (!lockedClan) {
  		log.error("could not get lock for clan.");
  		return false;
  	}
  	
    if (user == null || requester == null) {
      resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_OTHER);
      log.error("user is " + user + ", requester is " + requester);
      return false;      
    }
//    Clan clan = ClanRetrieveUtils.getClanWithId(user.getClanId());
    int clanId = user.getClanId();
    List<String> statuses = new ArrayList<String>();
    statuses.add(UserClanStatus.LEADER.name());
    statuses.add(UserClanStatus.JUNIOR_LEADER.name());
    List<Integer> userIds = RetrieveUtils.userClanRetrieveUtils()
    		.getUserIdsWithStatuses(clanId, statuses);
    //should just be one id
    Set<Integer> uniqUserIds = new HashSet<Integer>(); 
    if (null != userIds && !userIds.isEmpty()) {
    	uniqUserIds.addAll(userIds);
    }
    
    int userId = user.getId();
    if (!uniqUserIds.contains(userId)) {
      resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_AUTHORIZED);
      log.error("clan member can't approve clan join request. member=" + user +
      		"\t requester=" + requester);
      return false;      
    }
    //check if requester is already in a clan
    if (0 < requester.getClanId()) {
    	resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_ALREADY_IN_A_CLAN);
    	log.error("trying to accept a user that is already in a clan");
    	//the other requests in user_clans table that have a status of 2 (requesting to join clan)
    	//are deleted later on in writeChangesToDB
    	return false;
    }
    
    UserClan uc = RetrieveUtils.userClanRetrieveUtils().getSpecificUserClan(requester.getId(), clanId);
    if (uc == null || !UserClanStatus.REQUESTING.name().equals(uc.getStatus())) {
      resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_NOT_A_REQUESTER);
      log.error("requester has not requested for clan with id " + user.getClanId());
      return false;
    }
    if (ControllerConstants.CLAN__ALLIANCE_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId ||
        ControllerConstants.CLAN__LEGION_CLAN_ID_THAT_IS_EXCEPTION_TO_LIMIT == clanId) {
      return true;
    }
    
    //check out the size of the clan
    List<Integer> clanIdList = Collections.singletonList(clanId);
    //add in captain and member to existing leader and junior leader list
  	statuses.add(UserClanStatus.CAPTAIN.name());
  	statuses.add(UserClanStatus.MEMBER.name());
  	Map<Integer, Integer> clanIdToSize = RetrieveUtils.userClanRetrieveUtils()
  			.getClanSizeForClanIdsAndStatuses(clanIdList, statuses);
  	
  	int size = clanIdToSize.get(clanId);
    int maxSize = ControllerConstants.CLAN__MAX_NUM_MEMBERS;
    if (size >= maxSize && accept) {
      resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.FAIL_CLAN_IS_FULL);
      log.warn("user error: trying to add user into already full clan with id " + user.getClanId());
      return false;      
    }
    
    clanSizeList.add(size);
    return true;
  }
  
  private boolean writeChangesToDB(User user, User requester, boolean accept) {
  	if (accept) {
  		if (!requester.updateRelativeCoinsAbsoluteClan(0, user.getClanId())) {
  			log.error("problem with change requester " + requester + " clan id to " + user.getClanId());
  			return false;
  		}
  		if (!UpdateUtils.get().updateUserClanStatus(requester.getId(), user.getClanId(), UserClanStatus.MEMBER)) {
  			log.error("problem with updating user clan status to member for requester " + requester + " and clan id "+ user.getClanId());
  			return false;
  		}
  		DeleteUtils.get().deleteUserClansForUserExceptSpecificClan(requester.getId(), user.getClanId());
  		return true;
  	} else {
  		if (!DeleteUtils.get().deleteUserClan(requester.getId(), user.getClanId())) {
  			log.error("problem with deleting user clan info for requester with id " + requester.getId() + " and clan id " + user.getClanId()); 
  			return false;
  		}
  		return true;
  	}
  }
  
  private void setResponseBuilderStuff(Builder resBuilder, int clanId,
		  List<Integer> clanSizeList) {
  	Clan clan = ClanRetrieveUtils.getClanWithId(clanId);
    resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(clan));

    int size = clanSizeList.get(0);
    resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan, size));
  }
  
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

}
