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
import com.lvl6.events.response.RetrieveClanDataResponseEvent;
import com.lvl6.info.Clan;
import com.lvl6.info.User;
import com.lvl6.info.UserClan;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanRequestProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto;
import com.lvl6.proto.EventClanProto.RetrieveClanDataResponseProto;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.ApproveOrRejectRequestToJoinClanStatus;
import com.lvl6.proto.EventClanProto.ApproveOrRejectRequestToJoinClanResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.retrieveutils.ClanRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.server.controller.actionobjects.SetClanChatMessageAction;
import com.lvl6.server.controller.actionobjects.SetClanHelpingsAction;
import com.lvl6.server.controller.actionobjects.StartUpResource;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class ApproveOrRejectRequestToJoinClanController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
  
  @Autowired
  protected Locker locker;

  @Autowired
  protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;
  
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
    log.info(String.format("reqProto=%s", reqProto));

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserUuid();
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
    	Set<Integer> userIds = new HashSet<Integer>();
    	userIds.add(userId);
    	userIds.add(requesterId);
    	Map<Integer, User> idsToUsers = RetrieveUtils.
    		userRetrieveUtils().getUsersByIds(userIds);
    	
      User user = null;//RetrieveUtils.userRetrieveUtils().getUserById(userId);
      User requester = null;//RetrieveUtils.userRetrieveUtils().getUserById(requesterId);
      
      if (idsToUsers.containsKey(userId)) {
    	  user = idsToUsers.get(userId);
      }
      if (idsToUsers.containsKey(requesterId)) {
    	  requester = idsToUsers.get(requesterId);
      }

      List<Integer> clanSizeList = new ArrayList<Integer>();
      boolean legitDecision = checkLegitDecision(resBuilder, lockedClan, user, requester,
      		accept, clanSizeList);
      
      boolean success = false;
      if (legitDecision) {
        success = writeChangesToDB(user, requester, accept);
      }
      
      // Only need to set clan data if user accepted.
      ClanDataProto cdp = null;
      MinimumUserProto requestMup = null;
      if (success) {
      	resBuilder.setStatus(ApproveOrRejectRequestToJoinClanStatus.SUCCESS);
      	Clan clan = null;
      	
      	if (accept) {
      		clan = ClanRetrieveUtils.getClanWithId(clanId);
      		cdp = setClanData(clanId, clan, user, userId);      		
      		log.info(String.format("ClanDataProto=%s", cdp));
      	}
      	
      	setResponseBuilderStuff(resBuilder, clan, clanSizeList);
      	requestMup = CreateInfoProtoUtils
      			.createMinimumUserProtoFromUserAndClan(requester, clan);
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
        
        sendClanData(event, requestMup, accept, requesterId, cdp);
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

  //TODO: Issue one call to clan_for_user table instead of, atm, three 
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

  private ClanDataProto setClanData( int clanId,
	  Clan c, User u, int userId )
  {
	  ClanDataProto.Builder cdpb = ClanDataProto.newBuilder();
	  StartUpResource fillMe = new StartUpResource(
			RetrieveUtils.userRetrieveUtils());
	  
	  SetClanChatMessageAction sccma = new SetClanChatMessageAction(cdpb, u);
	  sccma.setUp(fillMe);
	  SetClanHelpingsAction scha = new SetClanHelpingsAction(cdpb, u, userId, clanHelpRetrieveUtil);
	  scha.setUp(fillMe);
	  
	  fillMe.fetchUsersOnly();
	  fillMe.addClan(clanId, c);
	  
	  sccma.execute(fillMe);
	  scha.execute(fillMe);
	  
	  return cdpb.build();
  }
  
  private void setResponseBuilderStuff(Builder resBuilder, Clan clan,
		  List<Integer> clanSizeList) {
  	
    resBuilder.setMinClan(CreateInfoProtoUtils.createMinimumClanProtoFromClan(clan));

    int size = clanSizeList.get(0);
    resBuilder.setFullClan(CreateInfoProtoUtils.createFullClanProtoWithClanSize(clan, size));
  }

  private void sendClanData(
	  RequestEvent event,
	  MinimumUserProto requesterMup,
	  boolean accepted,
	  int requesterId,
	  ClanDataProto cdp )
  {
	  if (!accepted || null == cdp) {
		  log.warn(String.format(
			  "accepted=%s, cdp=%s", accepted, cdp));
		  return;
	  }
	  log.info(String.format(
		  "writingClanData to clan. %s",
		  cdp));
	  RetrieveClanDataResponseEvent rcdre =
		  new RetrieveClanDataResponseEvent(requesterId);
	  rcdre.setTag(event.getTag());
	  RetrieveClanDataResponseProto.Builder rcdrpb =
		  RetrieveClanDataResponseProto.newBuilder();
	  rcdrpb.setMup(requesterMup);
	  rcdrpb.setClanData(cdp);
	  
	  rcdre.setRetrieveClanDataResponseProto(rcdrpb.build());
	  server.writeEvent(rcdre);
  }
  
  public Locker getLocker() {
	  return locker;
  }
  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public ClanHelpRetrieveUtil getClanHelpRetrieveUtil()
  {
	  return clanHelpRetrieveUtil;
  }

  public void setClanHelpRetrieveUtil( ClanHelpRetrieveUtil clanHelpRetrieveUtil )
  {
	  this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
  }

}
