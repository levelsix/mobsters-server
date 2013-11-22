package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.UpgradeNormStructureRequestEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.events.response.UpgradeNormStructureResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.UpgradeNormStructureResponseProto.UpgradeNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class UpgradeNormStructureController extends EventController {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public UpgradeNormStructureController() {
    numAllocatedThreads = 4;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new UpgradeNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_UPGRADE_NORM_STRUCTURE_EVENT;
  }


  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    UpgradeNormStructureRequestProto reqProto = ((UpgradeNormStructureRequestEvent)event).getUpgradeNormStructureRequestProto();

    UpgradeNormStructureResponseProto.Builder resBuilder = UpgradeNormStructureResponseProto.newBuilder();

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int userStructId = reqProto.getUserStructId();
    Timestamp timeOfUpgrade = new Timestamp(reqProto.getTimeOfUpgrade());
    
    resBuilder.setSender(senderProto);

    Structure currentStruct = null;
    Structure upgradedStruct = null;
    StructureForUser userStruct = RetrieveUtils.userStructRetrieveUtils().getSpecificUserStruct(userStructId);

    if (userStruct != null) {
    	currentStruct = StructureRetrieveUtils.getStructForStructId(userStruct.getStructId());
      upgradedStruct = StructureRetrieveUtils.getUpgradedStructForStructId(userStruct.getStructId());
    }

    server.lockPlayer(userId, this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
      int previousCash = 0;
      int previousGems = 0;
      
      boolean legitUpgrade = checkLegitUpgrade(resBuilder, user, userStruct, currentStruct,
      		upgradedStruct, timeOfUpgrade);
      UpgradeNormStructureResponseEvent resEvent = new UpgradeNormStructureResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setUpgradeNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);

      if (legitUpgrade) {
        previousCash = user.getCash();
        previousGems = user.getGems();
        
        Map<String, Integer> money = new HashMap<String, Integer>();
        writeChangesToDB(user, userStruct, upgradedStruct, timeOfUpgrade, money);
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, userStruct, currentStruct, upgradedStruct,
        		timeOfUpgrade, money, previousCash, previousGems);
      }
    } catch (Exception e) {
      log.error("exception in UpgradeNormStructure processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private void writeChangesToDB(User user, StructureForUser userStruct, Structure upgradedStruct,
  		Timestamp timeOfUpgrade, Map<String, Integer> money) {
  	
  	int newStructId = upgradedStruct.getId();
  	//upgrade the user's struct
  	if (!UpdateUtils.get().updateBeginUpgradingUserStruct(userStruct.getId(),
  			newStructId, timeOfUpgrade)) {
  		log.error("problem with changing time of upgrade to " + timeOfUpgrade + 
  				" and marking as incomplete, the user struct " + userStruct);
  	}
  	
  	//charge the user  	
  	int buildCost = upgradedStruct.getBuildCost();
  	
  	int gemCost = 0;
  	int cashCost = 0;
  		cashCost = buildCost;
  	
    int gemChange = -1*gemCost;
    int cashChange = -1*cashCost;
    if (!user.updateRelativeDiamondsCoinsExperienceNaive(gemChange, cashChange, 0)) {
      log.error("problem with updating user stats: diamondChange=" + gemChange
          + ", coinChange=" + cashChange + ", user is " + user);
    } else {
      //everything went well
      if (0 != gemChange) {
        money.put(MiscMethods.gems, gemChange);
      }
      if (0 != cashChange) {
        money.put(MiscMethods.cash, cashChange);
      }
    }
  }

  private boolean checkLegitUpgrade(Builder resBuilder, User user, StructureForUser userStruct,
      Structure currentStruct, Structure upgradedStruct, Timestamp timeOfUpgrade) {
    if (user == null || userStruct == null || userStruct.getLastRetrieved() == null) {
      resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_OTHER);
      log.error("parameter passed in is null. user=" + user + ", user struct=" + userStruct + 
         ", userStruct's last retrieve time=" + userStruct.getLastRetrieved());
      return false;
    }
    if (!userStruct.isComplete()) {
      resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_BUILT_YET);
      log.error("user struct is not complete yet");
      return false;
    }
    if (null == upgradedStruct) {
    	resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_AT_MAX_LEVEL_ALREADY);
    	log.error("user struct at max level already. struct is " + currentStruct);
    	return false;
    }
    if (timeOfUpgrade.getTime() < userStruct.getLastRetrieved().getTime()) {
      resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_BUILT_YET);
      log.error("the upgrade time " + timeOfUpgrade + " is before the last time the building was retrieved:"
          + userStruct.getLastRetrieved());
      return false;
    }
    //see if the user can upgrade it
    if (user.getId() != userStruct.getUserId()) {
      resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_USERS_STRUCT);
      log.error("user struct belongs to someone else with id " + userStruct.getUserId());
      return false;
    }
    
//    int buildPrice = upgradedStruct.getBuildPrice();
//    if (upgradedStruct.isPremiumCurrency()) {
//    	if (user.getGems() < buildPrice) {
//    		resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_ENOUGH_GEMS);
//    		log.error("user doesn't have enough gems, has " + user.getGems() + ", needs " + buildPrice);
//    		return false;
//    	}
//    } else {
//    	if (user.getCash() < buildPrice) {
//    		resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_NOT_ENOUGH_CASH);
//    		log.error("user doesn't have enough cash, has " + user.getCash() + ", needs " + buildPrice);
//    		return false;
//    	}
//    }
    //TODO: only make one user struct retrieve call 
    List<StructureForUser> userStructs = RetrieveUtils.userStructRetrieveUtils().getUserStructsForUser(user.getId());
    if (userStructs != null) {
      for (StructureForUser us : userStructs) {
//        if (!us.isComplete() && us.getUpgradeStartTime() != null) {
//          resBuilder.setStatus(UpgradeNormStructureStatus.FAIL_ANOTHER_STRUCT_STILL_UPGRADING);
//          log.error("another struct is still upgrading: user struct=" + us);
//          return false;
//        }
      }
    }
    resBuilder.setStatus(UpgradeNormStructureStatus.SUCCESS);
    return true;
  }
  
  private void writeToUserCurrencyHistory(User aUser, StructureForUser userStruct,
  		Structure curStruct, Structure upgradedStruct, Timestamp timeOfUpgrade,
  		Map<String, Integer> money, int previousSilver, int previousGold) {
    
    int userStructId = userStruct.getId();
    int prevStructId = curStruct.getId();
    int prevLevel = curStruct.getLevel();
    String structDetails = "uStructId:" + userStructId + " preStructId:" + prevStructId
        + " prevLevel:" + prevLevel;
    
    Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
    String reasonForChange = ControllerConstants.UCHRFC__UPGRADE_NORM_STRUCT;
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    String gems = MiscMethods.gems;
    String cash = MiscMethods.cash;
    
    previousGoldSilver.put(cash, previousSilver);
    previousGoldSilver.put(gems, previousGold);
    reasonsForChanges.put(cash,  reasonForChange);
    reasonsForChanges.put(gems, reasonForChange);
    details.put(cash, structDetails);
    details.put(gems, structDetails);
    
    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, timeOfUpgrade, 
        money, previousGoldSilver, reasonsForChanges, details);
  }

}
