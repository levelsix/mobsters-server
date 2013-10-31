package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotRequestProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.Builder;
import com.lvl6.proto.EventMonsterProto.IncreaseMonsterInventorySlotResponseProto.IncreaseMonsterInventorySlotStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.utils.RetrieveUtils;

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

    //get values sent from the client (the request proto)
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int numPurchases = reqProto.getNumPurchases();
    Timestamp date = new Timestamp((new Date()).getTime());

    //set some values to send to the client (the response proto)
    IncreaseMonsterInventorySlotResponseProto.Builder resBuilder = IncreaseMonsterInventorySlotResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.FAIL_OTHER); //default

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
    	int previousGems = 0;
      User aUser = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      int numSlots = numPurchases *
      		ControllerConstants.MONSTER_INVENTORY_SLOTS__INCREMENT_AMOUNT;
      int gemPricePerSlot = ControllerConstants.MONSTER_INVENTORY_SLOTS__GEM_PRICE_PER_SLOT;
    	int totalGemPrice = gemPricePerSlot * numSlots;
      
      boolean legit = checkLegit(resBuilder, userId, aUser, numPurchases,
      		numSlots, totalGemPrice);

      boolean successful = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if(legit) {
      	previousGems = aUser.getGems();
    	  successful = writeChangesToDb(aUser, numSlots, totalGemPrice, money);
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
      	
      	writeToUserCurrencyHistory(aUser, date, money, previousGems, numSlots, gemPricePerSlot);
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
  private boolean checkLegit(Builder resBuilder, int userId,
  		User u, int numPurchases, int numSlots, int totalGemPrice) {
  	
  	if (null == u) {
  		log.error("user is null. no user exists with id=" + userId);
  		return false;
  	}
  	
  	//check if user has enough money
  	int userGems = u.getGems();
  	if (userGems < totalGemPrice) {
  		log.error("user does not have enough gems to buy more monster inventory slots. userGems=" +
  				userGems + "\t numSlots=" + numSlots + "\t numPurchases=" + numPurchases);
  		return false;
  	}
  	
  	
  	resBuilder.setStatus(IncreaseMonsterInventorySlotStatus.SUCCESS);
  	return true;
  }
  
  private boolean writeChangesToDb(User aUser, int numSlots, int totalGemPrice,
  		Map<String, Integer> money) {
  	int cost = -1 * totalGemPrice;
  	boolean success = aUser.updateNumAdditionalMonsterSlotsAndDiamonds(
  			numSlots, cost);
  	if (!success) {
  		log.error("problem with updating user monster inventory slots and diamonds");
  	} else {
  		if (0 != cost) {
  			money.put(MiscMethods.gems, cost);
  		}
  	}
  	return success;
  }
  
  private void writeToUserCurrencyHistory(User aUser, Timestamp date,
  		Map<String, Integer> money, int previousGems, int numSlots, int pricePerSlot) {
    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    String gems = MiscMethods.gems;
    String reasonForChange = ControllerConstants.UCHRFC__INCREASE_MONSTER_INVENTORY +
    		"numSlots=" + numSlots + " pricePerSlot=" + pricePerSlot;

    previousGemsCash.put(gems, previousGems);
    reasonsForChanges.put(gems, reasonForChange);
    
    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money,
        previousGemsCash, reasonsForChanges);
    
  }
  
}
