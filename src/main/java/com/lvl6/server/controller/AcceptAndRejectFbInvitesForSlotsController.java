package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.AcceptAndRejectFbInviteForSlotsRequestEvent;
import com.lvl6.events.response.AcceptAndRejectFbInviteForSlotsResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsRequestProto;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto.AcceptAndRejectFbInviteForSlotsStatus;
import com.lvl6.proto.EventMonsterProto.AcceptAndRejectFbInviteForSlotsResponseProto.Builder;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.proto.UserProto.MinimumUserProtoWithFacebookId;
import com.lvl6.proto.UserProto.UserFacebookInviteForSlotProto;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class AcceptAndRejectFbInvitesForSlotsController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public AcceptAndRejectFbInvitesForSlotsController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new AcceptAndRejectFbInviteForSlotsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_ACCEPT_AND_REJECT_FB_INVITE_FOR_SLOTS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    AcceptAndRejectFbInviteForSlotsRequestProto reqProto = ((AcceptAndRejectFbInviteForSlotsRequestEvent)event).getAcceptAndRejectFbInviteForSlotsRequestProto();

    log.info("reqProto=" + reqProto);
    //get values sent from the client (the request proto)
    MinimumUserProtoWithFacebookId senderProto = reqProto.getSender();
    MinimumUserProto sender = senderProto.getMinUserProto();
    int userId = sender.getUserId();
    String userFacebookId = senderProto.getFacebookId();
    
    //just accept these
    List<Integer> acceptedInviteIds = reqProto.getAcceptedInviteIdsList();
    if(null == acceptedInviteIds) {
    	acceptedInviteIds = new ArrayList<Integer>();
    } else {
    	acceptedInviteIds = new ArrayList<Integer>(acceptedInviteIds);
    }
    
    //delete these from the table
    List<Integer> rejectedInviteIds = reqProto.getRejectedInviteIdsList();
    if (null == rejectedInviteIds) {
    	rejectedInviteIds = new ArrayList<Integer>();
    } else {
    	rejectedInviteIds = new ArrayList<Integer>(rejectedInviteIds);
    }
    Timestamp acceptTime = new Timestamp((new Date()).getTime());

    
    //set some values to send to the client (the response proto)
    AcceptAndRejectFbInviteForSlotsResponseProto.Builder resBuilder =
    		AcceptAndRejectFbInviteForSlotsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.FAIL_OTHER); //default

    server.lockPlayer(userId, this.getClass().getSimpleName());
    try {
      //these will be populated. by checkLegit()
      Map<Integer, UserFacebookInviteForSlot> idsToInvitesInDb =
      		new HashMap<Integer, UserFacebookInviteForSlot>();
      
      boolean legit = checkLegit(resBuilder, userId, userFacebookId, acceptedInviteIds,
      		rejectedInviteIds, idsToInvitesInDb);

      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(userId, userFacebookId, acceptedInviteIds,
    	  		rejectedInviteIds, idsToInvitesInDb, acceptTime);
      }
      
      if (successful) {
      	//need to retrieve all the inviters from the db, set the accepted time for
      	//accepted invites
      	Collection<UserFacebookInviteForSlot> invites = idsToInvitesInDb.values(); 
      	List<Integer> userIds = getInviterIds(invites);
      	Map<Integer, User> idsToInviters = RetrieveUtils.userRetrieveUtils().getUsersByIds(userIds);
      	
      	for (UserFacebookInviteForSlot invite : invites) {
      		invite.setTimeAccepted(acceptTime);
      		
      		int inviterId = invite.getInviterUserId();
      		User inviter = idsToInviters.get(inviterId);
      		MinimumUserProtoWithFacebookId inviterProto = null;
      		
      		//create the proto for the invites
      		UserFacebookInviteForSlotProto inviteProto = CreateInfoProtoUtils
      				.createUserFacebookInviteForSlotProtoFromInvite(invite, inviter, inviterProto);
      		
      		resBuilder.addAcceptedInvites(inviteProto);
      	}
    	  resBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.SUCCESS);
      }
      
      AcceptAndRejectFbInviteForSlotsResponseEvent resEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      
      if (successful) {
      	//write to the inviters this user accepted
      	AcceptAndRejectFbInviteForSlotsResponseProto responseProto =
      			resBuilder.build();
      	for (Integer inviteId : acceptedInviteIds) {
      		UserFacebookInviteForSlot invite = idsToInvitesInDb.get(inviteId);
      		int inviterId = invite.getInviterUserId();
      		
      		 AcceptAndRejectFbInviteForSlotsResponseEvent newResEvent =
      				 new AcceptAndRejectFbInviteForSlotsResponseEvent(inviterId);
      		 newResEvent.setTag(0);
      		 newResEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(responseProto);
           server.writeEvent(newResEvent);
      		
      	}
      }

    } catch (Exception e) {
      log.error("exception in AcceptAndRejectFbInviteForSlotsController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(AcceptAndRejectFbInviteForSlotsStatus.FAIL_OTHER);
    	  AcceptAndRejectFbInviteForSlotsResponseEvent resEvent = new AcceptAndRejectFbInviteForSlotsResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setAcceptAndRejectFbInviteForSlotsResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in AcceptAndRejectFbInviteForSlotsController processEvent", e);
      }
    } finally {
      server.unlockPlayer(userId, this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value. accepetedInviteIds, 
   * rejectedInviteIds, and idsToAcceptedInvites might be modified
   */
  private boolean checkLegit(Builder resBuilder, int userId, String userFacebookId,
  		List<Integer> acceptedInviteIds, List<Integer> rejectedInviteIds,
  		Map<Integer, UserFacebookInviteForSlot> idsToInvites) {
  	
  	if (null == userFacebookId || userFacebookId.isEmpty()) {
  		log.error("facebookId is null. id=" + userFacebookId + "\t acceptedInvitesIds=" +
  				acceptedInviteIds + "\t rejectedInviteIds=" + rejectedInviteIds);
  		return false;
  	}
  	//search for these invites accepted and rejected
  	List<Integer> inviteIds = new ArrayList<Integer>(acceptedInviteIds);
  	inviteIds.addAll(rejectedInviteIds);
  	
  	//retrieve the invites for this recipient that haven't been accepted nor redeemed
  	boolean filterByAccepted = true;
  	boolean isAccepted = false;
  	boolean filterByRedeemed = true;
  	boolean isRedeemed = false;
  	Map<Integer, UserFacebookInviteForSlot> idsToInvitesInDb = UserFacebookInviteForSlotRetrieveUtils
  			.getSpecificOrAllInvitesForRecipient(userFacebookId, inviteIds, filterByAccepted,
  					isAccepted, filterByRedeemed, isRedeemed);
  	Set<Integer> validIds = idsToInvitesInDb.keySet();
  	
  	//only want the acceptedInvite ids that aren't yet accepted nor redeemed
  	log.info("acceptedInviteIds before filter: " + acceptedInviteIds);
  	retainIfInExistingInts(validIds, acceptedInviteIds);
  	log.info("acceptedInviteIds after filter: " + acceptedInviteIds);
  	
  	//only want the rejectedInvite ids that aren't yet accepted nor redeemed
  	retainIfInExistingInts(validIds, rejectedInviteIds);

  	
  	//check to make sure this user is not accepting any invites from an inviter
  	//this user has already accepted, or in other words
  	//check to make sure this user has not previously accepted any invites from 
  	//any of the inviters of the acceptedInviteIds
  	
  	//pair up inviterUserIds with the acceptedInviteIds
  	Map<Integer, Integer> acceptedInviterIdsToInviteIds = getInviterUserIds(
  			acceptedInviteIds, idsToInvitesInDb); 
  	
  	//look in the invite table for accepted invites (includes redeemed),
  	//select the inviter user ids that have recipientFacebookId = userFacebookId
  	isAccepted = true;
  	Set<Integer> redeemedInviterIds = UserFacebookInviteForSlotRetrieveUtils
  			.getUniqueInviterUserIdsForRequesterId(userFacebookId, filterByAccepted, isAccepted);
  	
  	//if any of the acceptedInviteIds contains an inviterId this user has already accepted
  	//an invite from,
  	//delete inviteId from the acceptedInviteIds list and put the
  	//inviteId into the rejectedInviteIds list,
  	//done so because the db probably has recorded that the inviter used up this user
  	//and is trying to use this user again
  	log.info("acceptedInviteIds before inviter used check: " + acceptedInviteIds);
  	retainInvitesFromUnusedInviters(redeemedInviterIds, acceptedInviterIdsToInviteIds,
  			acceptedInviteIds, rejectedInviteIds);
  	log.info("acceptedInviteIds after inviter used check: " + acceptedInviteIds);
  	
  	idsToInvites.putAll(idsToInvitesInDb);
  	
  	return true;
  }
  
  private void retainIfInExistingInts(Set<Integer> existingInts, List<Integer> someInts) {
  	int lastIndex = someInts.size() - 1;
  	for(int index = lastIndex; index >= 0; index--) {
  		int someInt = someInts.get(index);
  		
  		if (!existingInts.contains(someInt)) {
  			//since the int is not in existingInts, remove it.
  			someInts.remove(index);
  		}
  	}
  }
  
  private Map<Integer, Integer> getInviterUserIds(List<Integer> inviteIds,
  		Map<Integer, UserFacebookInviteForSlot> idsToInvites) {
  	Map<Integer, Integer> inviterUserIdsToInviteIds = new HashMap<Integer, Integer>();
  	
  	for (Integer inviteId : inviteIds) {
  		UserFacebookInviteForSlot invite = idsToInvites.get(inviteId);
  		int inviterUserId = invite.getInviterUserId();
  		//what if this guy is used more than once? meh, fuck it
  		inviterUserIdsToInviteIds.put(inviterUserId, inviteId);
  	}
  	
  	return inviterUserIdsToInviteIds;
  }
  
  //recordedInviterIds are the inviterIds in the invite table that are for redeemed invites
  private void retainInvitesFromUnusedInviters(Set<Integer> recordedInviterIds,
  		Map<Integer, Integer> acceptedInviterIdsToInviteIds, 
  		List<Integer> acceptedInviteIds, List<Integer> rejectedInviteIds) {
  	//if any of the inviter ids in acceptedInviterIdsToInviteIds are already in
  	//the invite table with this user, delete inviteId from the
  	//acceptedInviteIds list and put the inviteId into the rejectedInviteIds list
  	
  	//keep track of the inviterIds that this user has previously already accepted  
  	Map<Integer, Integer> invalidInviteIdsToUserIds = new HashMap<Integer, Integer>();
  	for (Integer potentialNewInviterId : acceptedInviterIdsToInviteIds.keySet()) {
  		if (recordedInviterIds.contains(potentialNewInviterId)) {
  			//userA trying to accept an invite from a person userA has already 
  			//accepted an invite from
  			int inviteId = acceptedInviterIdsToInviteIds.get(potentialNewInviterId);
  			
  			invalidInviteIdsToUserIds.put(inviteId, potentialNewInviterId);
  		}
  	}
  	
  	Set<Integer> invalidInviteIds = invalidInviteIdsToUserIds.keySet();
  	if (invalidInviteIds.isEmpty()) {
  		return;
  	}
  	log.warn("user tried accepting invites from users he has already accepted." +
  			"invalidInviteIdsToUserIds=" + invalidInviteIdsToUserIds);

  	log.warn("before: rejectedInviteIds=" + acceptedInviteIds);
  	log.warn("before: acceptedInviteIds=" + acceptedInviteIds);
  	//go through acceptedInviteIds and remove invalid inviteIds
  	int lastIndex = acceptedInviteIds.size() - 1;
  	for (int index = lastIndex; index >= 0; index--) {
  		int acceptedInviteId = acceptedInviteIds.get(index);

  		if (invalidInviteIds.contains(acceptedInviteId)) {
  			acceptedInviteIds.remove(index);

  			//after removing it put it into rejectedInviteIds
  			rejectedInviteIds.add(acceptedInviteId);
  		}
  	}
  	log.warn("after: acceptedInviteIds=" + acceptedInviteIds);
  	log.warn("after: rejectedInviteIds=" + rejectedInviteIds);
  }
  
  private boolean writeChangesToDb(int userId, String userFacebookId,
  		List<Integer> acceptedInviteIds, List<Integer> rejectedInviteIds,
  		Map<Integer, UserFacebookInviteForSlot> idsToInvitesInDb,
  		Timestamp acceptTime) {
  	log.info("idsToInvitesInDb=" + idsToInvitesInDb + "\t acceptedInviteIds=" +
  			acceptedInviteIds + "\t rejectedInviteIds=" + rejectedInviteIds);
  	
  	//update the acceptTimes for the acceptedInviteIds
  	//these acceptedInviteIds are for unaccepted, unredeemed invites
  	if (!acceptedInviteIds.isEmpty()) {
  		int num = UpdateUtils.get().updateUserFacebookInviteForSlotAcceptTime(
  				userFacebookId, acceptedInviteIds, acceptTime);
  		log.info("\t\t\t\t\t\t\t num acceptedInviteIds updated: " + num + "\t invites=" +
  				acceptedInviteIds);
  	}
  	
  	//DELETE THE rejectedInviteIds THAT ARE ALREADY IN DB
  	//these deleted invites are for unaccepted, unredeemed invites
  	if (!rejectedInviteIds.isEmpty()) {
  		int num = DeleteUtils.get().deleteUserFacebookInvitesForSlots(rejectedInviteIds);
  		log.info("num rejectedInviteIds deleted: " + num + "\t invites=" +
  				rejectedInviteIds);
  	}
  	
  	return true;
  }
  
  private List<Integer> getInviterIds(Collection<UserFacebookInviteForSlot> invites) {
  	List<Integer> inviterIds = new ArrayList<Integer>();
  	
  	for (UserFacebookInviteForSlot invite : invites) {
  		int inviterId = invite.getInviterUserId();
  		inviterIds.add(inviterId);
  	}
  	return inviterIds;
  }
  
}
