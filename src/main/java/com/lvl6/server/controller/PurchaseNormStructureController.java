package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.PurchaseNormStructureRequestEvent;
import com.lvl6.events.response.PurchaseNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.CoordinatePair;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto.PurchaseNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

  @Component @DependsOn("gameServer") public class PurchaseNormStructureController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected InsertUtil insertUtils;

  public void setInsertUtils(InsertUtil insertUtils) {
	this.insertUtils = insertUtils;
  }

  public PurchaseNormStructureController() {
    numAllocatedThreads = 3;
  }

  @Override
  public RequestEvent createRequestEvent() {
    return new PurchaseNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_PURCHASE_NORM_STRUCTURE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    PurchaseNormStructureRequestProto reqProto = ((PurchaseNormStructureRequestEvent)event).getPurchaseNormStructureRequestProto();

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int structId = reqProto.getStructId();
    CoordinatePair cp = new CoordinatePair(reqProto.getStructCoordinates().getX(), reqProto.getStructCoordinates().getY());
    Timestamp timeOfPurchase = new Timestamp(reqProto.getTimeOfPurchase());

    //things to send to client
    PurchaseNormStructureResponseProto.Builder resBuilder = PurchaseNormStructureResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      Structure struct = StructureRetrieveUtils.getStructForStructId(structId);
      int previousCash = 0;
      int previousGems = 0;
      int uStructId = 0;

      boolean legitPurchaseNorm = checkLegitPurchaseNorm(resBuilder, struct, user, timeOfPurchase);

      boolean success = false;
      List<Integer> uStructIdList = new ArrayList<Integer>();
      Map<String, Integer> money = new HashMap<String, Integer>();
      if (legitPurchaseNorm) {
      	previousCash = user.getCash();
      	previousGems = user.getGems();
      	success = writeChangesToDB(user, struct, cp, timeOfPurchase, uStructIdList, money);
      }
      
      if (success) {
      	resBuilder.setStatus(PurchaseNormStructureStatus.SUCCESS);
      	uStructId = uStructIdList.get(0);
      	resBuilder.setUserStructId(uStructId);
      }

      PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setPurchaseNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (success) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, structId, uStructId, timeOfPurchase,
        		money, previousCash, previousGems);
      }
    } catch (Exception e) {
    	log.error("exception in PurchaseNormStructure processEvent", e);
    	//don't let the client hang
    	try {
    		resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
    		PurchaseNormStructureResponseEvent resEvent = new PurchaseNormStructureResponseEvent(userId);
    		resEvent.setTag(event.getTag());
    		resEvent.setPurchaseNormStructureResponseProto(resBuilder.build());
    		server.writeEvent(resEvent);
    	} catch (Exception e2) {
    		log.error("exception2 in PurchaseNormStructure processEvent", e);
    	}
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private boolean writeChangesToDB(User user, Structure struct, CoordinatePair cp,
  		Timestamp purchaseTime, List<Integer> uStructId, Map<String, Integer> money) {
    int gemChange = -1 * Math.max(0, struct.getGemPrice());
    int cashChange = -1 * Math.max(0, struct.getCashPrice());
    
    int userId = user.getId();
    int structId = struct.getId();
    int userStructId = insertUtils.insertUserStruct(userId, structId, cp, purchaseTime);
    if (userStructId <= 0) {
      log.error("problem with giving struct " + structId + " at " + purchaseTime +
      		" on " + cp);
      return false;
    }

    int num = user.updateRelativeCoinsAndDiamonds(cashChange, gemChange);
    if (1 != num) {
      log.error("problem with updating user currency. gemChange=" + gemChange +
      		" cashChange=" + cashChange + "\t numRowsUpdated=" + num);
      return false;
    } else {//things went ok
      if (0 != gemChange) {
        money.put(MiscMethods.gems, gemChange * -1);
      }
      if (0 != cashChange) {
        money.put(MiscMethods.cash, cashChange * -1);
      }
    }
    
    uStructId.add(userStructId);
    return true;
  }

  private boolean checkLegitPurchaseNorm(Builder resBuilder, Structure prospective,
      User user, Timestamp timeOfPurchase) {
    if (user == null || prospective == null || timeOfPurchase == null) {
      resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_OTHER);
      log.error("parameter passed in is null. user=" + user + ", struct=" + prospective 
          + ", timeOfPurchase=" + timeOfPurchase);
      return false;
    }
    //see if the user is at or above the required level to build the structure
    if (user.getLevel() < prospective.getMinLevel()) {
      resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_LEVEL_TOO_LOW);
      log.error("user is too low level to purchase struct. user level=" + user.getLevel() + 
          ", struct's min level is " + prospective.getMinLevel());
      return false;
    }
    //check if user has enough cash to buy building
    if (user.getCash() < prospective.getCashPrice()) {
      resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_CASH);
      log.error("user only has " + user.getCash() + " coins and needs " + prospective.getCashPrice());
      return false;
    }
    //check if user has enough gems to buy building
    if (user.getGems() < prospective.getGemPrice()) {
      resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_INSUFFICIENT_GEMS);
      log.error("user only has " + user.getGems() + " diamonds and needs " + prospective.getGemPrice());
      return false;
    }

    int maxNumSameStruct = ControllerConstants
    		.PURCHASE_NORM_STRUCTURE__MAX_NUM_OF_CERTAIN_STRUCTURE;
    Map<Integer, List<StructureForUser>> structIdsToUserStructs = RetrieveUtils.userStructRetrieveUtils().getStructIdsToUserStructsForUser(user.getId());
    
    //see if user can buy more of these structures and if another struct is in construction
    for (Integer structId : structIdsToUserStructs.keySet()) {
    	List<StructureForUser> duplicateUserStructs = structIdsToUserStructs.get(structId);
    	
    	if (duplicateUserStructs != null) {
    		int numCopies = duplicateUserStructs.size();
    		//check if user can buy anymore of prospectiveStruct
    		if (structId == prospective.getId() && numCopies >= maxNumSameStruct) {
    			resBuilder.setStatus(PurchaseNormStructureStatus
    					.FAIL_ALREADY_HAVE_MAX_OF_THIS_STRUCT);
    			log.error("user already has max of this struct, which is "  + maxNumSameStruct);
    			return false;
    		}
    		//check if user already building something at time of purchase
    		for (StructureForUser us : duplicateUserStructs) {
    			//when a building is complete, it's completedTime will = retrievedTime
    			if (!us.isComplete() && us.getLastRetrieved() == null) {
    				resBuilder.setStatus(PurchaseNormStructureStatus.FAIL_ANOTHER_STRUCT_STILL_BUILDING);
    				log.error("another struct still building: " + us); 
    				return false;
    			}
    		}
    	} else {
    		log.error("user has no structs? for structid " + structId);
    	}
    }
    return true;
  }
  
  //TODO: TRACK CURRENCY
  private void writeToUserCurrencyHistory(User aUser, int structId, int uStructId,
  		Timestamp date, Map<String, Integer> money, int previousSilver, int previousGold) {
//    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String gems = MiscMethods.gems;
//    String cash = MiscMethods.cash;
//    String reasonForChange = ControllerConstants.UCHRFC__PURCHASE_NORM_STRUCT +
//        " structId=" + structId + " uStructId=" + uStructId;
//
//    previousGoldSilver.put(gems, previousGold);
//    previousGoldSilver.put(cash, previousSilver);
//    reasonsForChanges.put(gems, reasonForChange);
//    reasonsForChanges.put(cash, reasonForChange);
//    
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, date, money,
//        previousGoldSilver, reasonsForChanges);
    
  }
}
