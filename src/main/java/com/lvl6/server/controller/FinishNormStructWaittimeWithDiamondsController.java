package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsRequestProto;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.FinishNormStructWaittimeWithDiamondsResponseProto.FinishNormStructWaittimeStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class FinishNormStructWaittimeWithDiamondsController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
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

    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    int userStructId = reqProto.getUserStructId();
    Timestamp timeOfSpeedup = new Timestamp(reqProto.getTimeOfSpeedup());

    FinishNormStructWaittimeWithDiamondsResponseProto.Builder resBuilder = FinishNormStructWaittimeWithDiamondsResponseProto.newBuilder();
    resBuilder.setSender(senderProto);
    resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());

    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      int previousGems = 0;
      StructureForUser userStruct = RetrieveUtils.userStructRetrieveUtils().getSpecificUserStruct(userStructId);
      Structure struct = null;
      if (userStruct != null) {
        struct = StructureRetrieveUtils.getStructForStructId(userStruct.getStructId());
      }
      
      //don't want to recalculate gem cost, so this will contain cost when it's first computed
      List<Integer> gemCostList = new ArrayList<Integer>();
      boolean legitSpeedup = checkLegitSpeedup(resBuilder, user, userStruct,
      		timeOfSpeedup, struct, gemCostList);

      
      boolean success = false;
      Map<String, Integer> money = new HashMap<String, Integer>();
      if (legitSpeedup) {
        previousGems = user.getGems();
        int gemCost = gemCostList.get(0);
        writeChangesToDB(user, userStruct, timeOfSpeedup, struct, gemCost, money);
      }
      
      FinishNormStructWaittimeWithDiamondsResponseEvent resEvent = new FinishNormStructWaittimeWithDiamondsResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setFinishNormStructWaittimeWithDiamondsResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (success) {
      	UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
      	resEventUpdate.setTag(event.getTag());
      	server.writeEvent(resEventUpdate);
        writeToUserCurrencyHistory(user, userStruct, timeOfSpeedup, money, previousGems);
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
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  private void writeChangesToDB(User user, StructureForUser userStruct,
  		Timestamp timeOfPurchase, Structure struct, int gemCost, 
  		Map<String, Integer> money) {
  	
  	int gemChange = -1 * gemCost;
  	//update user gems
  	if (!user.updateRelativeDiamondsNaive(gemChange)) {
  		log.error("problem with using diamonds to finish norm struct build. userStruct=" +
  				userStruct + "\t struct=" + struct + "\t gemCost=" + gemChange);
  	} else {
  		//update structure for user to reflect it is complete
  		if (!UpdateUtils.get().updateUserStructLastretrievedIscomplete(userStruct.getId(), timeOfPurchase, true)) {
  			log.error("problem with completing norm struct build time. userStruct=" +
  					userStruct + "\t struct=" + struct + "\t gemCost=" + gemChange);
  		}
  		money.put(MiscMethods.gems, gemChange);
  	}
  }

  private boolean checkLegitSpeedup(Builder resBuilder, User user,
  		StructureForUser userStruct, Timestamp timeOfSpeedup, Structure struct,
  		List<Integer> gemCostList) {
    if (user == null || userStruct == null || struct == null ||
    		userStruct.getUserId() != user.getId() || userStruct.isComplete()) {
      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_OTHER);
      log.error("something passed in is null. user=" + user + ", userStruct=" + userStruct +
           ", struct=" + struct + ", struct owner's id=" + userStruct.getUserId());
      return false;
    }
    
    //TODO: ACCOUNT FOR HOW MUCH TIME HAS PASSED WHEN DETERMINING SPEEDUP GEM COST 
    int cost = struct.getInstaBuildGemCost();
    long startTimeMillis = userStruct.getPurchaseTime().getTime();
    long durationInSeconds = struct.getMinutesToBuild() * 60;
    long curTimeMillis = timeOfSpeedup.getTime();
    int gemCost = MiscMethods.speedupCostOverTime(cost, startTimeMillis, durationInSeconds, curTimeMillis);
    
    if (user.getGems() < gemCost) {
      resBuilder.setStatus(FinishNormStructWaittimeStatus.FAIL_NOT_ENOUGH_GEMS);
      log.error("user doesn't have enough diamonds. has " + user.getGems() +", needs " + gemCost);
      return false;
    }
    
    gemCostList.add(gemCost);
    resBuilder.setStatus(FinishNormStructWaittimeStatus.SUCCESS);
    return true;  
  }
  

  //TODO: FIX THIS
  public void writeToUserCurrencyHistory(User aUser, StructureForUser userStruct,
  		Timestamp timeOfPurchase, Map<String, Integer> money, int previousGems) {
    
//    int userStructId = userStruct.getId();
//    int structId = userStruct.getStructId();
//    int prevLevel = userStruct.getLevel();
//    String structDetails = "uStructId:" + userStructId + " structId:" + structId
//        + " prevLevel:" + prevLevel;
//    
//    Map<String, Integer> previousGemsCash = new HashMap<String, Integer>();
//    Map<String, String> reasonsForChanges = new HashMap<String, String>();
//    String reasonForChange = ControllerConstants.UCHRFC__FINISH_NORM_STRUCT;
//    String gems = MiscMethods.gems;
//    
//    previousGemsCash.put(gems, previousGems);
//    reasonsForChanges.put(gems, reasonForChange + "finish upgrade " + structDetails);
//      
//    MiscMethods.writeToUserCurrencyOneUserGemsAndOrCash(aUser, timeOfPurchase, money,
//        previousGemsCash, reasonsForChanges);
  }
  
  
}
