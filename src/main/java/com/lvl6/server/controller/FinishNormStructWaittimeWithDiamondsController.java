package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.FinishNormStructWaittimeWithDiamondsRequestEvent;
import com.lvl6.events.response.FinishNormStructWaittimeWithDiamondsResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Structure;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsRequestProto;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.FinishNormStructWaittimeStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.StructureForUserRetrieveUtils2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class FinishNormStructWaittimeWithDiamondsController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  @Autowired
  protected Locker locker;

  @Autowired
  protected UserRetrieveUtils2 userRetrieveUtils;

  @Autowired
  protected StructureForUserRetrieveUtils2 userStructRetrieveUtils;

  public FinishNormStructWaittimeWithDiamondsController() {
    numAllocatedThreads = 2;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new FinishNormStructWaittimeWithDiamondsRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_FINISH_NORM_STRUCT_WAITTIME_WITH_DIAMONDS_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {

    FinishNormStructWaittimeWithDiamondsRequestProto reqProto = ((FinishNormStructWaittimeWithDiamondsRequestEvent)event).getFinishNormStructWaittimeWithDiamondsRequestProto();
    log.info("reqProto=" + reqProto);

    MinimumUserProto senderProto = reqProto.getSender();
    String userId = senderProto.getUserUuid();
    String userStructId = reqProto.getUserStructUuid();
    //userstruct's lastRetrieved will start with this date
    Timestamp timeOfSpeedup = new Timestamp(reqProto.getTimeOfSpeedup());
    int gemCostToSpeedup = reqProto.getGemCostToSpeedup();

    FinishNormStructWaittimeWithDiamondsResponseProto.Builder resBuilder = FinishNormStructWaittimeWithDiamondsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);

    UUID userUuid = null;
    UUID userStructUuid = null;
    boolean invalidUuids = true;
    try {
      userUuid = UUID.fromString(userId);
      userStructUuid = UUID.fromString(userStructId);
      invalidUuids = false;
    } catch (Exception e) {
      log.error(String.format(
          "UUID error. incorrect userId=%s or userStructId=%s",
          userId, userStructId), e);
      invalidUuids = true;
    }

    //UUID checks
    if (invalidUuids) {
      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
      FinishNormStructWaittimeWithDiamondsResponseEvent resEvent = new FinishNormStructWaittimeWithDiamondsResponseEvent(userId);
      resEvent.setTag(event.getTag());
      resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(resBuilder.build());
      server.writeEvent(resEvent);
      return;
    }

    getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
    try {
      User user = getUserRetrieveUtils().getUserById(senderProto.getUserUuid());
      log.info("user=" + user);
      int previousGems = 0;
      StructureForUser userStruct = getUserStructRetrieveUtils().getSpecificUserStruct(userStructId);
      Structure struct = null;
      Structure formerStruct = null;
      
      if (userStruct != null) {
      	int structId = userStruct.getStructId();
        struct = StructureRetrieveUtils.getStructForStructId(structId);
        formerStruct = StructureRetrieveUtils.getPredecessorStructForStructId(structId);
      }
      
      boolean legitSpeedup = checkLegitSpeedup(resBuilder, user, userStruct,
      		timeOfSpeedup, struct, gemCostToSpeedup);

      
      boolean success = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if (legitSpeedup) {
        previousGems = user.getGems();
        success = writeChangesToDB(user, userStruct, timeOfSpeedup, struct,
        		gemCostToSpeedup, money);
      }
      if (success) {
      	resBuilder.setStatus(FinishNormStructWaittimeStatus.SUCCESS);
      }
      
      FinishNormStructWaittimeWithDiamondsResponseEvent resEvent = new FinishNormStructWaittimeWithDiamondsResponseEvent(senderProto.getUserUuid());
      resEvent.setTag(event.getTag());
      resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (success) {
    	  //null PvpLeagueFromUser means will pull from hazelcast instead
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods
      			.createUpdateClientUserResponseEventAndUpdateLeaderboard(user, null, null);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
        writeToUserCurrencyHistory(user, userStruct, formerStruct, timeOfSpeedup, money, previousGems);
      }
    } catch (Exception e) {
      log.error("exception in FinishNormStructWaittimeWithDiamondsController processEvent", e);
      //don't let the client hang
      try {
      	resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
      	FinishNormStructWaittimeWithDiamondsResponseEvent resEvent = new FinishNormStructWaittimeWithDiamondsResponseEvent(userId);
      	resEvent.setTag(event.getTag());
      	resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(resBuilder.build());
      	server.writeEvent(resEvent);
      } catch (Exception e2) {
      	log.error("exception2 in FinishNormStructWaittimeWithDiamondsController processEvent", e);
      }
    } finally {
      getLocker().unlockPlayer(userUuid, this.getClass().getSimpleName());      
    }
  }

  private boolean checkLegitSpeedup(Builder resBuilder, User user,
  		StructureForUser userStruct, Timestamp timeOfSpeedup, Structure struct,
  		int gemCostToSpeedup) {
    if (user == null || userStruct == null || struct == null ||
    		!userStruct.getUserId().equals(user.getId()) || userStruct.isComplete()) {
      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
      log.error("something passed in is null. user=" + user +
           ", struct=" + struct + ", struct owner's id=" + userStruct.getUserId() +
           "\t or user struct is complete. userStruct=" + userStruct);
      return false;
    }
    
    if (user.getGems() < gemCostToSpeedup) {
      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_NOT_ENOUGH_GEMS);
      log.error("user doesn't have enough diamonds. has " + user.getGems() +", needs " +
      		gemCostToSpeedup);
      return false;
    }
    
    return true;  
  }

  private boolean writeChangesToDB(User user, StructureForUser userStruct,
  		Timestamp timeOfSpeedup, Structure struct, int gemCost, Map<String, Integer> money) {

    int gemChange = -1 * gemCost;
    if (0 != gemChange) {
      //update user gems
      if (!user.updateRelativeGemsNaive(gemChange, 0)) {
        log.error("problem with using diamonds to finish norm struct build. userStruct=" +
            userStruct + "\t struct=" + struct + "\t gemCost=" + gemChange);
        return false;
      } else {
          money.put(MiscMethods.gems, gemChange);
      }
    }
  	
  	//the last retrieved time has a value of timeOfSpeedup
  	
  	//update structure for user to reflect it is complete
  	if (!UpdateUtils.get().updateSpeedupUpgradingUserStruct(userStruct.getId(),
  			timeOfSpeedup)) {
  		log.error("problem with completing norm struct build time. userStruct=" +
  				userStruct + "\t struct=" + struct + "\t gemCost=" + gemChange);
  		return false;
  	}
  	
  	
  	return true;
  }

  public void writeToUserCurrencyHistory(User aUser, StructureForUser userStruct,
  		Structure formerStruct, Timestamp timeOfPurchase, Map<String, Integer> money,
  		int previousGems) {
	  if (money.isEmpty()) {
		  return;
	  }
    String userStructId = userStruct.getId();
    int structId = userStruct.getStructId();
    StringBuilder structDetails = new StringBuilder(); //   + structId;
    if (null == formerStruct) {
    	//no previous guy so user speeding up building first building
    	structDetails.append("construction ");
    } else {
    	structDetails.append("upgrade ");
    }
    structDetails.append("uStructId: ");
    structDetails.append(userStructId);
    structDetails.append(" structId: ");
    structDetails.append(structId);
    

    if (null != formerStruct) {
    	int prevStructId = formerStruct.getId();
    	int prevLevel = formerStruct.getLevel();
    	structDetails.append(" prevStructId: ");
    	structDetails.append(prevStructId);
    	structDetails.append(" prevLevel: ");
    	structDetails.append(prevLevel);
    }
    
    String userId = aUser.getId();
    Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    String reasonForChange = ControllerConstants.UCHRFC__SPED_UP_NORM_STRUCT;
    String gems = MiscMethods.gems;
    
    previousCurrencies.put(gems, previousGems);
    currentCurrencies.put(gems, aUser.getGems());
    reasonsForChanges.put(gems, reasonForChange);
    String detail = structDetails.toString();
    details.put(gems, detail);
      
    MiscMethods.writeToUserCurrencyOneUser(userId, timeOfPurchase, money,
    		previousCurrencies, currentCurrencies, reasonsForChanges, details);
  }

  public Locker getLocker() {
	  return locker;
  }

  public void setLocker(Locker locker) {
	  this.locker = locker;
  }

  public UserRetrieveUtils2 getUserRetrieveUtils() {
    return userRetrieveUtils;
  }

  public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
    this.userRetrieveUtils = userRetrieveUtils;
  }

  public StructureForUserRetrieveUtils2 getUserStructRetrieveUtils() {
    return userStructRetrieveUtils;
  }

  public void setUserStructRetrieveUtils(
      StructureForUserRetrieveUtils2 userStructRetrieveUtils) {
    this.userStructRetrieveUtils = userStructRetrieveUtils;
  }
  
}
