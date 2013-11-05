package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.InviteFbFriendsForSlotsRequestEvent;
import com.lvl6.events.response.InviteFbFriendsForSlotsResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsRequestProto;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.InviteFbFriendsForSlotsResponseProto.InviteFbFriendsForSlotsStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class InviteFbFriendsForSlotsController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public InviteFbFriendsForSlotsController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new InviteFbFriendsForSlotsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_INVITE_FB_FRIENDS_FOR_SLOTS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    InviteFbFriendsForSlotsRequestProto reqProto = ((InviteFbFriendsForSlotsRequestEvent)event).getInviteFbFriendsForSlotsRequestProto();

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    List<String> fbIdsOfFriends = reqProto.getFbFriendIdsList();
    Timestamp curTime = new Timestamp((new Date()).getTime());

    //set some values to send to the client (the response proto)
    InviteFbFriendsForSlotsResponseProto.Builder resBuilder = InviteFbFriendsForSlotsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      Map<Integer, UserFacebookInviteForSlot> idsToInvites = 
      		UserFacebookInviteForSlotRetrieveUtils.getInviteIdsToInvitesForUserId(userId);
      
      //contains the facebook ids of new users the user can invite
      List<String> newFacebookIdsToInvite = new ArrayList<String>();
      boolean legit = checkLegit(resBuilder, userId, aUser, fbIdsOfFriends,
      		idsToInvites, newFacebookIdsToInvite);

      boolean successful = false;
      if(legit) {
    	  successful = writeChangesToDb(aUser, newFacebookIdsToInvite, curTime);
      }
      
      if (successful) {
    	  resBuilder.setStatus(InviteFbFriendsForSlotsStatus.SUCCESS);
      }
      
      InviteFbFriendsForSlotsResponseEvent resEvent = new InviteFbFriendsForSlotsResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setInviteFbFriendsForSlotsResponseProto(resBuilder.build());
      server.writeEvent(resEvent);

    } catch (Exception e) {
      log.error("exception in InviteFbFriendsForSlotsController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(InviteFbFriendsForSlotsStatus.FAIL_OTHER);
    	  InviteFbFriendsForSlotsResponseEvent resEvent = new InviteFbFriendsForSlotsResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setInviteFbFriendsForSlotsResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in InviteFbFriendsForSlotsController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value. newUserIdsToInvite will be 
   * modified
   */
  private boolean checkLegit(Builder resBuilder, int userId, User u,
  		List<String> fbIdsOfFriends, Map<Integer, UserFacebookInviteForSlot> idsToInvites,
  		List<String> newFacebookIdsToInvite) {
  	
  	if (null == u) {
  		log.error("user is null. no user exists with id=" + userId);
  		return false;
  	}
  	
  	//if the user already invited some friends, don't invite again, keep
  	//only new ones
  	List<String> newFacebookIdsToInviteTemp = getNewInvites(
  			fbIdsOfFriends, idsToInvites);
  	newFacebookIdsToInvite.addAll(newFacebookIdsToInviteTemp);
  	
  	resBuilder.setStatus(InviteFbFriendsForSlotsStatus.SUCCESS);
  	return true;
  }
  
  //keeps and returns the facebook ids that have not been invited yet
  private List<String> getNewInvites(List<String> fbIdsOfFriends,
  		 Map<Integer, UserFacebookInviteForSlot> idsToInvites) {
  	//contains the duplicate invites, that need to be deleted
  	//e.g. two invites exist with same inviterId and recipientId
  	List<Integer> inviteIdsOfDuplicateInvites = new ArrayList<Integer>();
  	
  	//running collection of recipient ids already seen
  	Set<String> processedRecipientIds = new HashSet<String>();
  	
  	for (Integer inviteId : idsToInvites.keySet()) {
  		UserFacebookInviteForSlot invite = idsToInvites.get(inviteId);
  		String recipientId = invite.getRecipientFacebookId(); 
  		
  		if (processedRecipientIds.contains(recipientId)) {
  			//done to ensure a user does not invite another user more than once
  			//i.e. tuple (inviterId, recipientId) is unique
  			inviteIdsOfDuplicateInvites.add(inviteId);
  		} else {
  			//keep track of the recipientIds seen so far
  			processedRecipientIds.add(recipientId);
  		}
  	}
  	
  	//DELETE THE DUPLICATE INVITES
  	if (!inviteIdsOfDuplicateInvites.isEmpty()) {
  		int num = DeleteUtils.get().deleteUserFacebookInviteForSlots(inviteIdsOfDuplicateInvites);
  		log.warn("num duplicate invites deleted: " + num);
  	}
  	
  	
  	List<String> newUserIdsToInvite = new ArrayList<String>();
  	//don't want to generate an invite to a recipient the user has already invited
  	//going through the facebook ids client sends and select only the new ones
  	for (String prospectiveRecipientId : fbIdsOfFriends) {
  		
  		//keep only the recipient ids that have not been seen/invited
  		if(!processedRecipientIds.contains(prospectiveRecipientId)) {
  			newUserIdsToInvite.add(prospectiveRecipientId);
  		}
  	}
  	return newUserIdsToInvite;
  }
  
  private boolean writeChangesToDb(User aUser, List<String> newFacebookIdsToInvite, 
  		Timestamp curTime) {
  	if (newFacebookIdsToInvite.isEmpty()) {
  		return true;
  	}
  	
  	int userId = aUser.getId();
  	int num = InsertUtils.get().insertIntoUserFbInviteForSlot(userId,
  			newFacebookIdsToInvite, curTime);
  	
  	int expectedNum = newFacebookIdsToInvite.size();
  	if (num != expectedNum) {
  		log.error("problem with updating user monster inventory slots and diamonds." +
  				" num inserted: " + num + "\t should have been: " + expectedNum);
  	} 
  	log.info("num inserted: " + num);
  	return true;
  }
  
}
