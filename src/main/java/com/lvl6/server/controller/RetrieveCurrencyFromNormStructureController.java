package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.RetrieveCurrencyFromNormStructureRequestEvent;
import com.lvl6.events.response.RetrieveCurrencyFromNormStructureResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.StructureForUser;
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.info.User;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureRequestProto.StructRetrieval;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.Builder;
import com.lvl6.proto.EventStructureProto.RetrieveCurrencyFromNormStructureResponseProto.RetrieveCurrencyFromNormStructureStatus;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.rarechange.StructureResourceGeneratorRetrieveUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

  @Component @DependsOn("gameServer") public class RetrieveCurrencyFromNormStructureController extends EventController{

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  public RetrieveCurrencyFromNormStructureController() {
    numAllocatedThreads = 14;
  }
  
  @Override
  public RequestEvent createRequestEvent() {
    return new RetrieveCurrencyFromNormStructureRequestEvent();
  }

  @Override
  public EventProtocolRequest getEventType() {
    return EventProtocolRequest.C_RETRIEVE_CURRENCY_FROM_NORM_STRUCTURE_EVENT;
  }

  @Override
  protected void processRequestEvent(RequestEvent event) throws Exception {
    RetrieveCurrencyFromNormStructureRequestProto reqProto = ((RetrieveCurrencyFromNormStructureRequestEvent)event).getRetrieveCurrencyFromNormStructureRequestProto();

    //get stuff client sent
    MinimumUserProto senderProto = reqProto.getSender();
    int userId = senderProto.getUserId();
    List<StructRetrieval> structRetrievals = reqProto.getStructRetrievalsList();
    Timestamp curTime = new Timestamp((new Date()).getTime());
    
    
    Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval =  new HashMap<Integer, Timestamp>();
    Map<Integer, Integer> userStructIdsToAmountCollected = new HashMap<Integer, Integer>();
    List<Integer> duplicates = new ArrayList<Integer>();
    //create map from ids to times and check for duplicates
    getIdsAndTimes(structRetrievals, duplicates,
    		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected); 
    
    RetrieveCurrencyFromNormStructureResponseProto.Builder resBuilder =
    		RetrieveCurrencyFromNormStructureResponseProto.newBuilder();
    resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
    resBuilder.setSender(senderProto);

    server.lockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());
    try {
      User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
      int previousCash = 0;
      int previousOil = 0;
      List<Integer> userStructIds = new ArrayList<Integer>(userStructIdsToTimesOfRetrieval.keySet());
      
      Map<Integer, StructureForUser> userStructIdsToUserStructs = 
      		getUserStructIdsToUserStructs(userId, userStructIds);
      Map<Integer, StructureResourceGenerator> userStructIdsToGenerators =
      		getUserStructIdsToResourceGenerators(userStructIdsToUserStructs.values());
      
      //this will contain the amount user collects
      Map<String, Integer> resourcesGained = new HashMap<String, Integer>();
      //userStructIdsToTimesOfRetrieval and userStructIdsToUserStructs will be
      //modified to contain only the valid user structs user can retrieve currency from
      boolean legitRetrieval = checkLegitRetrieval(resBuilder, user, userStructIds,
      		userStructIdsToUserStructs, userStructIdsToGenerators, duplicates,
      		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected, resourcesGained);
      
      int cashGain = 0;
      int oilGain = 0;
      boolean successful = false;
      if (legitRetrieval) {
      	cashGain = resourcesGained.get(MiscMethods.cash);
        previousCash = user.getCash();
        oilGain = resourcesGained.get(MiscMethods.oil);
        previousOil = user.getOil();
        
        successful = writeChangesToDb(user, cashGain, oilGain, userStructIdsToUserStructs,
        		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected);
      }
      if (successful) {
      	resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.SUCCESS);
      }

      RetrieveCurrencyFromNormStructureResponseEvent resEvent = new RetrieveCurrencyFromNormStructureResponseEvent(senderProto.getUserId());
      resEvent.setTag(event.getTag());
      resEvent.setRetrieveCurrencyFromNormStructureResponseProto(resBuilder.build());  
      server.writeEvent(resEvent);
      
      if (legitRetrieval) {
        UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
        resEventUpdate.setTag(event.getTag());
        server.writeEvent(resEventUpdate);
        
        writeToUserCurrencyHistory(user, previousCash, previousOil, resourcesGained,
        		curTime, userStructIdsToUserStructs, userStructIdsToGenerators,
        		userStructIdsToTimesOfRetrieval, userStructIdsToAmountCollected);
      }
    } catch (Exception e) {
      log.error("exception in RetrieveCurrencyFromNormStructureController processEvent", e);
    } finally {
      server.unlockPlayer(senderProto.getUserId(), this.getClass().getSimpleName());      
    }
  }

  //separate the duplicate ids from the unique ones
  private void getIdsAndTimes(List<StructRetrieval> srList,  List<Integer> duplicates,
  		Map<Integer, Timestamp> structIdsToTimesOfRetrieval,
  		Map<Integer, Integer> structIdsToAmountCollected) {
    if (srList.isEmpty()) {
      log.error("RetrieveCurrencyFromNormStruct request did not send any user struct ids.");
      return;
    }
    
    for(StructRetrieval sr : srList) {
      int key = sr.getUserStructId();
      Timestamp value = new Timestamp(sr.getTimeOfRetrieval());
      int amount = sr.getAmountCollected();
      
      if(structIdsToTimesOfRetrieval.containsKey(key)) {
        duplicates.add(key);
      } else {
        structIdsToTimesOfRetrieval.put(key, value);
        structIdsToAmountCollected.put(key, amount);
      }
    }
  }
  
  //retrieve these user structs from the db and put them in a map
  private Map<Integer, StructureForUser> getUserStructIdsToUserStructs(int userId,
  		List<Integer> userStructIds) {
    Map<Integer, StructureForUser> returnValue = new HashMap<Integer, StructureForUser>();
    if(null == userStructIds || userStructIds.isEmpty()) {
      log.error("no user struct ids!");
      return returnValue;
    }
    
    List<StructureForUser> userStructList = RetrieveUtils.userStructRetrieveUtils()
        .getSpecificOrAllUserStructsForUser(userId, userStructIds);
    for(StructureForUser us : userStructList) {
      if(null != us) {
        returnValue.put(us.getId(), us);
      } else {
        log.error("could not retrieve one of the user structs. userStructIds to retrieve="
            + userStructIds + ". user structs retrieved=" 
            + userStructList + ". Continuing with processing.");
      }
    }
    return returnValue;
  }
  
  //link up a user struct id with the structure object
  private Map<Integer, StructureResourceGenerator> getUserStructIdsToResourceGenerators(
  		Collection<StructureForUser> userStructs) {
    Map<Integer, StructureResourceGenerator> returnValue =
    		new HashMap<Integer, StructureResourceGenerator>();
    Map<Integer, StructureResourceGenerator> structIdsToStructs = 
    		StructureResourceGeneratorRetrieveUtils.getStructIdsToResourceGenerators();
    
    if(null == userStructs || userStructs.isEmpty()) {
      log.error("There are no user structs.");
    }
    
    for(StructureForUser us : userStructs) {
      int structId = us.getStructId();
      int userStructId = us.getId();
      
      StructureResourceGenerator s = structIdsToStructs.get(structId);
      if(null != s) {
        returnValue.put(userStructId, s);
      } else {
        log.error("structure with id " + structId + " does not exist, therefore UserStruct is invalid:" + us);
      }
    }
    
    return returnValue;
  }
  
  private boolean checkLegitRetrieval(Builder resBuilder, User user,
  		List<Integer> userStructIds, 
      Map<Integer, StructureForUser> userStructIdsToUserStructs,
      Map<Integer, StructureResourceGenerator> userStructIdsToGenerators,
      List<Integer> duplicates,
      Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval,
      Map<Integer, Integer> userStructIdsToAmountCollected,
      Map<String, Integer> resourcesGained) {

    int userId = user.getId();
    
    if (user == null || userStructIds.isEmpty() || userStructIdsToUserStructs.isEmpty()
        || userStructIdsToGenerators.isEmpty() || userStructIdsToTimesOfRetrieval.isEmpty()) { 
      log.error("user is null, or no struct ids, user structs, structures, or retrieval times . user=" +
        user + "\t userStructIds=" + userStructIds + "\t structIdsToUserStructs=" +
        userStructIdsToUserStructs + "\t userStructIdsToGenerators=" + userStructIdsToGenerators +
        "\t userStructIdsToRetrievalTimes=" + userStructIdsToTimesOfRetrieval);
      return false;
    }
    if (!duplicates.isEmpty()) {
      resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
      log.warn("duplicate struct ids in request. ids=" + duplicates);
    }

    //go through the userStructIds the user sent, checking which structs can be
    //retrieved
    int cash = 0;
    int oil = 0;
    for (Integer id : userStructIds) {
      StructureForUser userStruct = userStructIdsToUserStructs.get(id);
      StructureResourceGenerator struct = userStructIdsToGenerators.get(id);
      
      if (null == userStruct || userId != userStruct.getUserId() || !userStruct.isComplete()) {
        resBuilder.setStatus(RetrieveCurrencyFromNormStructureStatus.FAIL_OTHER);
        log.error("(will continue processing) struct owner is not user, or struct" +
        		" is not complete yet. userStruct=" + userStruct);
        //remove invalid user structure
        userStructIdsToUserStructs.remove(id);
        userStructIdsToTimesOfRetrieval.remove(id);
        userStructIdsToAmountCollected.remove(id);
        continue;
      }
      
      String type = struct.getResourceTypeGenerated();
      ResourceType rt = ResourceType.valueOf(type);
      if (ResourceType.CASH.equals(rt)) {
      	cash += userStructIdsToAmountCollected.get(id);
      } else if (ResourceType.OIL.equals(rt)) {
      	oil += userStructIdsToAmountCollected.get(id);
      } else {
      	log.error("(will continue processing) unknown resource type: " + rt);
      	//remove invalid user structure
        userStructIdsToUserStructs.remove(id);
        userStructIdsToTimesOfRetrieval.remove(id);
        userStructIdsToAmountCollected.remove(id);
      }
    }
    //return to the caller the amount of money the user can collect 
    resourcesGained.put(MiscMethods.cash, cash);
    resourcesGained.put(MiscMethods.oil, oil);
    
    return true;
  }
  
  private boolean writeChangesToDb(User user, int cashGain, int oilGain,
  		Map<Integer, StructureForUser> userStructIdsToUserStructs,
  		Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval,
  		Map<Integer, Integer> userStructIdsToAmountCollected) {
  	if (!user.updateRelativeCoinsOilRetrievedFromStructs(cashGain, oilGain)) {
      log.error("problem with updating user stats after retrieving " + cashGain + " cash");
      return false;
    }
    if (!UpdateUtils.get().updateUserStructsLastretrieved(userStructIdsToTimesOfRetrieval, userStructIdsToUserStructs)) {
      log.error("problem with updating user structs last retrieved for userStructIds " 
          + userStructIdsToTimesOfRetrieval);
      return false;
    }
    return true;
  }
  
  public void writeToUserCurrencyHistory(User aUser, int previousCash,
  		int previousOil, Map<String, Integer> resourcesGained, Timestamp curTime,
  		Map<Integer, StructureForUser> userStructIdsToUserStructs,
  		Map<Integer, StructureResourceGenerator> userStructIdsToGenerators,
  		Map<Integer, Timestamp> userStructIdsToTimesOfRetrieval,
  		Map<Integer, Integer> userStructIdsToAmountCollected) {

  	int userId = aUser.getId();
    Map<String, Integer> previousCurrencies = new HashMap<String, Integer>();
    Map<String, Integer> currentCurrencies = new HashMap<String, Integer>();
    Map<String, String> reasonsForChanges = new HashMap<String, String>();
    Map<String, String> details = new HashMap<String, String>();
    String cash = MiscMethods.cash;
    String oil = MiscMethods.oil;
    String reasonForChange = ControllerConstants.UCHRFC__RETRIEVE_CURRENCY_FROM_NORM_STRUCT;
    StringBuilder cashDetailSb = new StringBuilder();
    cashDetailSb.append("(userStructId,time,amount)=");
    StringBuilder oilDetailSb = new StringBuilder();
    oilDetailSb.append("(userStructId,time,amount)=");
    
    //being descriptive, separating cash stuff from oil stuff
    for(Integer id : userStructIdsToAmountCollected.keySet()) {
      StructureResourceGenerator struct = userStructIdsToGenerators.get(id);
      Timestamp t = userStructIdsToTimesOfRetrieval.get(id);
      int amount = userStructIdsToAmountCollected.get(id);
      
      String type = struct.getResourceTypeGenerated();
      ResourceType rt = ResourceType.valueOf(type);
      if (ResourceType.CASH.equals(rt)) {
      	cashDetailSb.append("(");
      	cashDetailSb.append(id);
      	cashDetailSb.append(",");
      	cashDetailSb.append(t);
      	cashDetailSb.append(",");
      	cashDetailSb.append(amount);
      	cashDetailSb.append(")");
      	
      } else if (ResourceType.OIL.equals(rt)) {
      	oilDetailSb.append("(");
      	oilDetailSb.append(id);
      	oilDetailSb.append(",");
      	oilDetailSb.append(t);
      	oilDetailSb.append(",");
      	oilDetailSb.append(amount);
      	oilDetailSb.append(")");
      }
    }
    
    previousCurrencies.put(cash, previousCash);
    previousCurrencies.put(oil, previousOil);
    currentCurrencies.put(cash, aUser.getCash());
    currentCurrencies.put(oil, aUser.getOil());
    reasonsForChanges.put(cash, reasonForChange);
    reasonsForChanges.put(oil, reasonForChange);
    details.put(cash, cashDetailSb.toString());
    details.put(oil, oilDetailSb.toString());
    
    MiscMethods.writeToUserCurrencyOneUser(userId, curTime, resourcesGained,
        previousCurrencies, currentCurrencies, reasonsForChanges, details);
  }
  
}
