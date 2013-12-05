package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.IncreaseMonsterInventorySlotRequestEvent;
import com.lvl6.events.response.IncreaseMonsterInventorySlotResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.User;
import com.lvl6.info.UserFacebookInviteForSlot;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto.IncreaseSlotType;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.IncreaseMonsterInventorySlotStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.UserFacebookInviteForSlotRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component @DependsOn("gameServer") public class IncreaseMonsterInventorySlotController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());


  public IncreaseMonsterInventorySlotController() {
    numAllocatedThreads = 4;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new IncreaseMonsterInventorySlotRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_INCREASE_MONSTER_INVENTORY_SLOT_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    IncreaseMonsterInventorySlotRequestProto reqProto = ((IncreaseMonsterInventorySlotRequestEvent)event).getIncreaseMonsterInventorySlotRequestProto();

  	//EVERY TIME USER BUYS SLOTS RESET user_facebook_invite_for_slot table
    
    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    IncreaseSlotType increaseType = reqProto.getIncreaseSlotType();
    int numPurchases = reqProto.getNumPurchases();
    Timestamp curTime = new Timestamp((new Date()).getTime());

    //set some values to send to the client (the response proto)
    IncreaseMonsterInventorySlotResponseProto.Builder resBuilder = IncreaseMonsterInventorySlotResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	int previousGems = 0;
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      //this is in the case the user is buying slots
      int numSlots = numPurchases *
      		ControllerConstants.MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT;
      int gemPricePerSlot = ControllerConstants.MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT;
    	int totalGemPrice = gemPricePerSlot * numSlots;
    	
    	//will be populated if user is successfully redeeming fb invites
    	Map<Integer, UserFacebookInviteForSlot> idsToAcceptedInvites = 
    			new HashMap<Integer, UserFacebookInviteForSlot>();
      
      boolean legit = checkLegit(resBuilder, userId, aUser, increaseType,
      		numPurchases, numSlots, totalGemPrice, idsToAcceptedInvites);

      boolean successful = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if(legit) {
      	previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, increaseType, numSlots,
    	  		totalGemPrice, curTime, idsToAcceptedInvites, money);
      }
      
      if (successful) {
    	  resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.SUCCESS);
      }
      
      IncreaseMonsterInventorySlotResponseEvent resEvent = new IncreaseMonsterInventorySlotResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setIncreaseMonsterInventorySlotResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      
      if (successful) {
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(aUser);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
      	
      	writeToUserCurrencyHistory(aUser, curTime, money, previousGems, numSlots, gemPricePerSlot);
      	
      	//delete the user's facebook invites for slots
      	deleteInvitesForSlotsAfterPurchase(userId, money);
      }
    } catch (Exception e) {
      log.error("exception in IncreaseMonsterInventorySlotController processEvent", e);
      //don't let the client hang
      try {
    	  resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER);
    	  IncreaseMonsterInventorySlotResponseEvent resEvent = new IncreaseMonsterInventorySlotResponseEvent(userId);
    	  resEvent.setTag(event.getTag());
    	  resEvent.setIncreaseMonsterInventorySlotResponseProto(resBuilder.build());
    	  server.writeEvent(resEvent);
      } catch (Exception e2) {
    	  log.error("exception2 in IncreaseMonsterInventorySlotController processEvent", e);
      }
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    }
  }


  /*
   * Return true if user request is valid; false otherwise and set the
   * builder status to the appropriate value.
   */
  private boolean checkLegit(Builder resBuilder, int userId, User u,
  		IncreaseSlotType aType, int numPurchases, int numSlots, int totalGemPrice,
  		Map<Integer, UserFacebookInviteForSlot> idsToAcceptedInvites) {
  	
  	if (null == u) {
  		log.error("user is null. no user exists with id=" + userId);
  		return false;
  	}
  	
  	if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == aType) {
  		int minNumInvites = ControllerConstants
  				.MONSTER_INVENTORY_SLOTS__MIN_INVITES_TO_INCREASE_SLOTS;
  		
  		boolean acceptedInvitesOnly = true;
  		Map<Integer, UserFacebookInviteForSlot> idsToAcceptedTemp =
  				UserFacebookInviteForSlotRetrieveUtils
  				.getInviteIdsToInvitesForInviterUserId(userId, acceptedInvitesOnly);
  		
  		int acceptedAmount = idsToAcceptedTemp.size(); 
  		if(acceptedAmount <= minNumInvites) {
  			log.error("user deficient on accepted facebook invites to increase slots. " +
  					"minRequired=" + minNumInvites + "\t has:" + acceptedAmount);
  			return false;
  		}
  		//give the caller the values
  		idsToAcceptedInvites.putAll(idsToAcceptedTemp);
  		
  	} else if (IncreaseSlotType.PURCHASE == aType) {
  		//check if user has enough money
  		int userGems = u.getGems();
  		if (userGems < totalGemPrice) {
  			log.error("user does not have enough gems to buy more monster inventory slots. userGems=" +
  					userGems + "\t numSlots=" + numSlots + "\t numPurchases=" + numPurchases);
  			return false;
  		}
  		
  	} else {
  		return false;
  	}
  	
  	return true;
  }
  
  private boolean writeChangesToDb(User aUser, IncreaseSlotType increaseType,
  		int numSlots, int totalGemPrice, Timestamp curTime,
  		Map<Integer, UserFacebookInviteForSlot> idsToAcceptedInvites,
  		Map<String, Integer> money) {
  	boolean success = false;
  	/*
  	if (IncreaseSlotType.REDEEM_FACEBOOK_INVITES == increaseType) {
  		int n = ControllerConstants.MONSTER_INVENTORY_SLOTS__MIN_INVITES_TO_INCREASE_SLOTS;
  		//get the three earliest accepted invites
  		List<UserFacebookInviteForSlot> earliestAcceptedInvites =
  				nEarliestInvites(idsToAcceptedInvites, n);
  		
  		//save these to the user_facebook_invite_for_slots_accepted table
  		log.info("saving to accepted invites table. invites=" + earliestAcceptedInvites);
  		int userId = aUser.getId();
  		List<Integer> userIds = Collections.nCopies(n, userId);
  		int nthExtraSlot = aUser.getNthExtraSlotsViaFb();
  		List<Integer> nthExtraSlotsList = Collections.nCopies(n, nthExtraSlot);
  		int num = InsertUtils.get().insertIntoUserFbInviteForSlotAccepted(userIds,
  				nthExtraSlotsList, earliestAcceptedInvites, curTime);
  		log.info("num saved: " + num);
  		
  		//increase the user's extra slots
  		int slotChange = 1;
  		boolean updated = aUser.updateNthExtraSlotsViaFb(slotChange);
  		log.info("increasing user's nth extra slots via fb by 1. updated=" + updated);
  		
  		//delete all the accepted invites
  		int numCurInvites = idsToAcceptedInvites.size();
  		log.info("num current invites: " + numCurInvites + "invitesToDelete= " +
  				idsToAcceptedInvites);
  		num = DeleteUtils.get().deleteUserFacebookInvitesForUser(userId);
  		log.info("num deleted: " + num);
  	}
  	
  	if (IncreaseSlotType.PURCHASE == increaseType) {
  		int cost = -1 * totalGemPrice;
  		success = aUser.updateRelativelyNumAdditionalMonsterSlotsAndDiamonds(
  				numSlots, cost);
  		
  		if (!success) {
  			log.error("problem with updating user monster inventory slots and diamonds");
  		}
  		if (success && 0 != cost) {
  				money.put(MiscMethods.gems, cost);
  		}
  	} */
  	return success;
  }
  
  private List<UserFacebookInviteForSlot> nEarliestInvites(
  		Map<Integer, UserFacebookInviteForSlot> idsToAcceptedInvites, int n) {
  	List<UserFacebookInviteForSlot> earliestAcceptedInvites =
  			new ArrayList<UserFacebookInviteForSlot>(idsToAcceptedInvites.values());
  	orderUserFacebookAcceptedInvitesForSlots(earliestAcceptedInvites);
  	
  	if (n < earliestAcceptedInvites.size()) {
  		return earliestAcceptedInvites.subList(0, n);
  	} else {
  		return earliestAcceptedInvites;
  	}
  }
  
  private void orderUserFacebookAcceptedInvitesForSlots(
  		List<UserFacebookInviteForSlot> invites) {
  	
  	Collections.sort(invites, new Comparator<UserFacebookInviteForSlot>() {
      @Override
      public int compare(UserFacebookInviteForSlot lhs, UserFacebookInviteForSlot rhs) {
      	//sorting by accept time, which should not be null
      	Date lhsDate = lhs.getTimeAccepted();
      	Date rhsDate = rhs.getTimeAccepted();
      	
      	if (null == lhsDate && null == rhsDate) 
      		return 0;
      	else if (null == lhsDate) 
      		return -1;
      	else if (null == rhsDate) 
      		return 1;
      	else if (lhsDate.getTime() < rhsDate.getTime())
      		return -1;
      	else if (lhsDate.getTime() == rhsDate.getTime())
      		return 0;
      	else
      		return 1;
      }
  });
  }
  
  
  //TODO:FIX THIS
  private void writeToUserCurrencyHistory(User aUser, Timestamp curTime,
  		Map<String, Integer> money, int previousGems, int numSlots, int pricePerSlot) {
//    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String gems = MiscMethods.gems;
//    String reasonForChange = ControllerConstants.UCHRFC__INCREASE_MONSTER_INVENTORY +
//    		"numSlots=" + numSlots + " pricePerSlot=" + pricePerSlot;
//
//    previousGemsCash.put(gems, previousGems);
//    reasonsForChanges.put(gems, reasonForChange);
//    
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, curTime, money,
//        previousGemsCash, reasonsForChanges);
//    
  }
  
  //after user buys slots, delete all accepted and unaccepted invites for slots
  private void deleteInvitesForSlotsAfterPurchase(int userId, Map<String, Integer> money) {
  	if (money.isEmpty()) {
  		return;
  	}
  	
  	int num = DeleteUtils.get().deleteUserFacebookInvitesForUser(userId);
  	log.info("num invites deleted after buying slot. userId=" + userId + 
  			" numDeleted=" + num);
  }
  
}
